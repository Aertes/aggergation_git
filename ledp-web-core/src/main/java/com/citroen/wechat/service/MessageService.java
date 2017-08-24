package com.citroen.wechat.service;

import com.citroen.ledp.dao.mybaits.MybaitsGenericDao;
import com.citroen.ledp.exception.LedpException;
import com.citroen.ledp.service.ConstantService;
import com.citroen.ledp.util.MapUtil;
import com.citroen.wechat.domain.Message;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** 
 * @ClassName: MessageService 
 * @Description: TODO(的大卫杜夫) 
 * @author 杨少波
 * @date 2015年6月16日 下午3:31:50 
 * 
 */
@Service
public class MessageService {
    private static Logger logger = Logger.getLogger(MessageService.class);
	@Autowired
	private MybaitsGenericDao<Long> mybaitsGenericDao;
	@Autowired
	private ConstantService constantService;
	
	
	public List<Message> executeQuery(Map params) throws LedpException {
		Map<String, Object> condition = getCondition(params);
		Map namedParams = (Map) condition.get("namedParams");
		String sql = "select * from t_message where parent is null and transtype=0 " + condition.get("namedSql");
		sql +=" order by date_create desc";
		Map paginateParams = new HashMap();
		int pageNumber = (Integer) params.get("pageNumber");
		int pageSize = (Integer) params.get("pageSize");
		int offset = (pageNumber - 1) * pageSize;
		paginateParams.put("max", pageSize);
		paginateParams.put("offset", offset);
		return mybaitsGenericDao.executeQuery(Message.class, sql, namedParams, paginateParams);
		
	}
	public int getTotalRow(Map params) throws LedpException {
		Map<String, Object> condition = getCondition(params);
		Map namedParams = (Map) condition.get("namedParams");
		String sql = "select count(id) count from t_message where parent is null and transtype=0 " + condition.get("namedSql");
		List<Map> list = mybaitsGenericDao.executeQuery(sql, namedParams);
		if (list.isEmpty()) {
			return 0;
		}
		Map map = list.get(0);
		return map.isEmpty() ? 0 : Integer.parseInt(map.get("count").toString());
	}
	private Map<String, Object> getCondition(Map<String, Object> params) {
		MapUtil<String, Object> mapUtil = new MapUtil<String, Object>(params);
		Map<String, Object> namedParams = new HashMap<String, Object>();

		StringBuilder namedSql = new StringBuilder();
		if(!mapUtil.isBlank("parameter[startTime]")){
			String date1 = (String) params.get("parameter[startTime]");
			namedParams.put("date1",date1);
			namedSql.append(" and date_create >=:date1 ");
		}
		if(!mapUtil.isBlank("parameter[endTime]")){
			String date2 = (String) params.get("parameter[endTime]");
			namedParams.put("date2",date2);
			namedSql.append(" and date_create <=:date2 ");
		}
		if(!mapUtil.isBlank("parameter[fanname]")){
			String fanname = (String) params.get("parameter[fanname]");
			try {
				fanname = new String(fanname.getBytes("ISO-8859-1"),"UTF-8" );
			} catch (UnsupportedEncodingException e) {
				logger.error("异常信息：" + e.getMessage());
			}
			namedParams.put("fanname","%" + fanname + "%");
			namedSql.append(" and exists(select 1 from t_fans where id=fan and id in(select id from t_fans where nickname like :fanname )) ");
		}
		if(!mapUtil.isBlank("parameter[content]")){
			String content = (String) params.get("parameter[content]");
			try {
				content = new String(content.getBytes("ISO-8859-1"),"UTF-8" );
			} catch (UnsupportedEncodingException e) {
				logger.error(e.getMessage());
			}
			namedParams.put("content","%" + content + "%");
			namedSql.append(" and content like :content ");
		}
		long publicNo =  (Long) params.get("publicNo");
		namedParams.put("publicNo",publicNo);
		namedSql.append(" and publicno =:publicNo ");
		Map<String, Object> rs = new HashMap<String, Object>();
		rs.put("namedSql", namedSql);
		rs.put("namedParams", namedParams);
		return rs;
	}
	public Message messageInstance(Long messageId) throws LedpException {
		return mybaitsGenericDao.get(Message.class, messageId);
	}
	public Long save(Message message) throws LedpException {
		return mybaitsGenericDao.save(message);
	}

	public int getNewMessage(Map params) throws LedpException {
		Map<String, Object> condition = getCondition(params);
		Map namedParams = (Map) condition.get("namedParams");
		String sql = "select count(id) count from t_message where parent is null and transtype=0 and canstant = 0 and TO_DAYS(NOW()) - TO_DAYS(date_create) < 3"+ condition.get("namedSql");
		List<Map> list = mybaitsGenericDao.executeQuery(sql, namedParams);
		if (list.isEmpty()) {
			return 0;
		}
		Map map = list.get(0);
		return map.isEmpty() ? 0 : Integer.parseInt(map.get("count").toString());
	}
}
