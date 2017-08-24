package com.citroen.wechat.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.citroen.ledp.dao.mybaits.MybaitsGenericDao;
import com.citroen.ledp.exception.LedpException;
import com.citroen.ledp.service.ConstantService;
import com.citroen.ledp.util.MapUtil;
import com.citroen.wechat.domain.FansGroup;

/** 
 * @ClassName: FansGroupService 
 * @Description: TODO(的大卫杜夫) 
 * @author 杨少波
 * @date 2015年6月28日 下午2:06:55 
 * 
 */
@Service
public class FansGroupService {
	@Autowired
	private MybaitsGenericDao<Long> mybaitsGenericDao;
	@Autowired
	private ConstantService constantService;
	
	
	public List<FansGroup> executeQuery(Map params) throws LedpException {
		Map<String, Object> condition = getCondition(params);
		Map namedParams = (Map) condition.get("namedParams");
		String sql = "select * from t_fans_group where status=1 " + condition.get("namedSql");
		sql +=" order by sort asc";
		return mybaitsGenericDao.executeQuery(FansGroup.class, sql, namedParams);
		
	}
	public int getTotalRow(Map params) throws LedpException {
		Map<String, Object> condition = getCondition(params);
		Map namedParams = (Map) condition.get("namedParams");
		String sql = "select count(id) count from t_fans_group where status=1 " + condition.get("namedSql");
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
		if(!mapUtil.isBlank("publicNo")){
			Long publicNoId=Long.valueOf((String) params.get("publicNo")) ;
			namedParams.put("publicNoId", publicNoId);
			namedSql.append(" and publicno =:publicNoId");
		}
		
		
		Map<String, Object> rs = new HashMap<String, Object>();
		rs.put("namedSql", namedSql);
		rs.put("namedParams", namedParams);
		return rs;
	}
	public Long save(FansGroup fansGroup) throws LedpException {
		return mybaitsGenericDao.save(fansGroup);
	}
	public void deleteFansGroupInstance(Long fansGroupId) throws LedpException {
		 mybaitsGenericDao.delete(FansGroup.class, fansGroupId);
	}
	public FansGroup fansGroupInstance(Long fansGroupId) throws LedpException {
		return mybaitsGenericDao.get(FansGroup.class, fansGroupId);
	}
	public FansGroup getFansGroup(Long publicnoId,String name) throws LedpException {
		String sql="select * from t_fans_group where status=1 and publicno="+publicnoId+" and wechatgroupid='"+name+"'";
		return mybaitsGenericDao.find(FansGroup.class, sql);
	}
	public FansGroup get(Long id) throws LedpException {
		return mybaitsGenericDao.get(FansGroup.class, id);
	}
	public List<FansGroup> getFansGroup(Long publicno) throws LedpException{
		String sql="select * from t_fans_group where status=1 and publicno="+publicno+" order by sort desc limit 0,1";
		return mybaitsGenericDao.executeQuery(FansGroup.class,sql);
	}
	public List<FansGroup> getFansGroups(Long publicno) throws LedpException{
		String sql="select * from t_fans_group where status=1 and publicno="+publicno+" order by sort asc ";
		return mybaitsGenericDao.executeQuery(FansGroup.class,sql);
	}
	public FansGroup getGoupByFans(long fansId) throws LedpException{
		return mybaitsGenericDao.find(FansGroup.class,"select g.* from t_fans f left join t_fans_group g on f.fans_group=g.id where f.id="+fansId);
	}
	
	
}
