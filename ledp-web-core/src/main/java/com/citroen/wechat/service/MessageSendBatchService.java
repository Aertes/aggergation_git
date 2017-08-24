/**
 * 
 */
package com.citroen.wechat.service;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.citroen.ledp.dao.mybaits.MybaitsGenericDao;
import com.citroen.ledp.exception.LedpException;
import com.citroen.ledp.util.DateUtil;
import com.citroen.ledp.util.PropertiyUtil;
import com.citroen.wechat.domain.Material;
import com.citroen.wechat.domain.MessageSendBatch;
import com.citroen.wechat.domain.PublicNo;
import com.citroen.wechat.util.Pagination;

/**
 * Created by vpc on 2015/6/19.
 */
@Service
@Transactional
public class MessageSendBatchService {
	private static Logger log = Logger.getLogger(MessageSendBatchService.class);
	@Autowired
	private MybaitsGenericDao<Long> mybaitsGenericDao;
    @Resource
    private MaterialService materialService;
	
	public int remainTodayCount(PublicNo publicno) {
		String today = DateUtil.getCurrentDate();
		int count = 0;
		try{
			Map map = mybaitsGenericDao.find("select count(id) count from t_messagesendbatch where publicno="+publicno.getId()+" and DATE_FORMAT(sendTime,'%Y-%d-%m')='"+today+"' and status=1");
			count = Integer.parseInt(map.get("count").toString());
		}catch(Exception e){}
		
		PropertiyUtil pro = new PropertiyUtil("/wechat.properties");
		if("subscription".equals(publicno.getType().getCode())){
			int massMothMax = pro.getInt("api.mass.max.subscription.daily");
			int remain = massMothMax-count;
			return remain>0?remain:0;
		}
		if("service".equals(publicno.getType().getCode())){
			int massMothMax = pro.getInt("api.mass.max.service.daily");
			int remain = massMothMax-count;
			return remain>0?remain:0;
		}
		return 0;
	}
    
	public MessageSendBatch get(Long id) {
		try {
			return mybaitsGenericDao.get(MessageSendBatch.class, id);
		} catch (LedpException e) {
			log.error(e.getMessage());
		}
		return null;
	}
	
	public Long save(MessageSendBatch entity) {
		try {
			return mybaitsGenericDao.save(entity);
		} catch (LedpException e) {
			log.error(e.getMessage());
		}
		
		return -1L;
	}
	
	public boolean delete(Long id) {
		try {
			mybaitsGenericDao.delete(MessageSendBatch.class, id);
			return true;
		} catch (LedpException e) {
			log.error(e.getMessage());
		}
		return false;
	}
	
	public List<MessageSendBatch> list(Map<String, Object> params) {
		return null;
	}

	public Map<String,Object> query(int pageNumber, int pageSize, Map<String, String[]> parameterMap,Long publicnoId) {
		Map<String,Object> json = new JSONObject();
        StringBuffer sb = new StringBuffer("select * from t_messagesendbatch where 1 = 1");
        if(null != parameterMap) {
            String[] groupId = parameterMap.get("parameter[groupId]");
            if(groupId != null && groupId[0].matches("^\\d+$")) {
                sb.append(" and group_id=").append(groupId[0]);
            }
        }
        sb.append(" and publicno="+publicnoId+" order by SendTime desc");
		String sql = sb.toString();
		Map paginateParams = new HashMap();

		int offset = (pageNumber - 1) * pageSize;
		paginateParams.put("max", pageSize);
		paginateParams.put("offset", offset);

		try {
            List<Map<String,Object>> data = new ArrayList<Map<String, Object>>();
			List<MessageSendBatch> messageSendBatchList = mybaitsGenericDao.executeQuery(MessageSendBatch.class, sql, new HashMap<String, Object>(), paginateParams);
			int count = 0;

			List<Map> mapList = mybaitsGenericDao.executeQuery("select count(1) as count from t_messagesendbatch where publicno="+publicnoId);
			if(!mapList.isEmpty()) {
				count = Integer.parseInt(mapList.get(0).get("count").toString());
			}

            Pagination pagination = new Pagination(count,pageSize, pageNumber);

           /* Map<String, Object> pd = new HashMap<String, Object>();
            pd.put("currentPage", pagination.getCurrentPage());
            pd.put("pageNumber", pagination.getPageNumber());
            pd.put("pages", pagination.getPages());*/

            json.put("paginationData", pagination);
            if(null != messageSendBatchList && messageSendBatchList.size() >= 1) {
                for(Iterator<MessageSendBatch> iterator = messageSendBatchList.iterator(); iterator.hasNext();) {
                    Map<String, Object> map = new HashMap<String, Object>();
                    MessageSendBatch messageSendBatch = iterator.next();
                    map.put("id", messageSendBatch.getId()); // 理论上是完全不用这么干的，框架层面就可以直接把list<Object>转成json输出，这边貌似对null数据会报错，有时间再调整吧
                    if(null != messageSendBatch.getContent()) {
                        map.put("content", messageSendBatch.getContent());
                    }
                    if(messageSendBatch.getIsToAll()){
                    	map.put("groupId","全部");
                    }else if(null != messageSendBatch.getGroupId()) {
                        map.put("groupId", messageSendBatch.getGroupId().getName());
                    }
                    if(null != messageSendBatch.getMaterialId()) {
                        map.put("materialId", messageSendBatch.getMaterialId());
                        Material model = materialService.get(messageSendBatch.getMaterialId());
                        if(model!=null){
                        	map.put("url",model.getUrl());
                        }
                    }
                    
                    map.put("isToAll",messageSendBatch.getIsToAll()==true);
                    map.put("sendTime",DateUtil.format(messageSendBatch.getSendTime(),"MM月dd日"));
                    map.put("status","发送完毕");
                    map.put("totalCount",messageSendBatch.getTotalCount()==null?0:messageSendBatch.getTotalCount());
                    map.put("filterCount",messageSendBatch.getFilterCount()==null?0:messageSendBatch.getFilterCount());
                    map.put("sentCount",messageSendBatch.getSentCount()==null?0:messageSendBatch.getSentCount());
                    map.put("errorCount",messageSendBatch.getErrorCount()==null?0:messageSendBatch.getErrorCount());
                    map.put("id", messageSendBatch.getId());
                    data.add(map);
                }
            }

            json.put("data",data);
        } catch (LedpException e) {
            log.error("异常信息：" + e.getMessage());
		} catch (Exception e) {
            log.error("异常信息：" + e.getMessage());
        }

        return json;
	}
	
	public void update(MessageSendBatch entity){
		try {
			mybaitsGenericDao.update(entity);
		} catch (LedpException e) {
            log.error("异常信息：" + e.getMessage());
		}
	}
}
