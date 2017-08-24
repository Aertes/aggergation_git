package com.citroen.ledp.service.impl;

import com.citroen.ledp.dao.mybaits.MybaitsGenericDao;
import com.citroen.ledp.domain.Constant;
import com.citroen.ledp.domain.Mapping;
import com.citroen.ledp.exception.LedpException;
import com.citroen.ledp.service.ConstantService;
import com.citroen.ledp.service.MappingService;
import com.citroen.ledp.util.MapUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Title: MappingService.java
 * @Package com.citroen.ledp.service
 * @Description: TODO(用一句话描述该文件做什么)
 * @author 廖启洪
 * @date 2015年3月16日 下午8:13:55
 * @version V1.0
 */
@Service
public class MappingServiceImpl implements MappingService{
	@Autowired
	private MybaitsGenericDao<Long> mybaitsGenericDao;
	@Autowired
	private ConstantService constantService;
	
	public List<Map> executeQuery(Map params) throws LedpException{
		if(params.get("type.id") == null){
			return null;
		}
		String typeId = params.get("type.id") != null ?params.get("type.id").toString() : null;
		long id = Long.parseLong(typeId);
		Constant type = constantService.get(id);
		String table = type.getComment();
		StringBuilder sb = new StringBuilder();
		sb.append("select m.id,m.record,m.code,m.code1,m.code2,m.code3,m.code4,m.code5");
		sb.append(",'"+type.getName()+"' typeName");
		sb.append(",(select name from "+table+" where id=m.record) name");
		sb.append(" from t_mapping m left join "+table+" t on t.id=m.record");
		sb.append(" where m.type='").append(params.get("type.id")).append("'");
		Map<String,Object> condition = getCondition(params);
		Map namedParams = (Map)condition.get("namedParams");
		
		String sql =sb.toString()+condition.get("namedSql");
		if(params.containsKey("sortName")){
			sql+=" order by "+params.get("sortName");
		}
		if(params.containsKey("sortOrder")){
			sql+=" "+params.get("sortOrder");
		}
		Map paginateParams = new HashMap();
		if(params.containsKey("pageSize")){
			int pageNumber= (Integer) params.get("pageNumber");
			int pageSize=(Integer) params.get("pageSize");
			int offset = (pageNumber - 1) * pageSize;
			paginateParams.put("max",pageSize);
			paginateParams.put("offset",offset);
		}
		return mybaitsGenericDao.executeQuery(sql,namedParams,paginateParams);
	}
	
	public int getTotalRow(Map params) throws LedpException{
		@SuppressWarnings("unchecked")
		Map<String,Object> condition = getCondition(params);
		Map namedParams = (Map)condition.get("namedParams");
		
		if(params.get("type.id")==null){
			return 0;
		}
		String typeId = params.get("type.id").toString();
		long id = Long.parseLong(typeId);
		Constant type = constantService.get(id);
		String sql = "select count(m.id) count from t_mapping m left join "+type.getComment()+" t on t.id=m.record where m.type="+params.get("type.id")+condition.get("namedSql");
		List<Map> list = mybaitsGenericDao.executeQuery(sql,namedParams);
		if(list.isEmpty()){
			return 0;
		}
		Map map = list.get(0);
		return map.isEmpty()?0:Integer.parseInt(map.get("count").toString());
	}
	
	public Map<String,Object> getCondition(Map<String,Object> params){
		MapUtil<String,Object> mapUtil = new MapUtil<String,Object>(params);
		Map<String,Object> namedParams = new HashMap<String,Object>();
		StringBuilder namedSql = new StringBuilder();
		
		if(!mapUtil.isBlank("name")){
			String name = mapUtil.get("name");
			namedParams.put("name","%"+name+"%");
			namedSql.append(" and t.name like '%"+name+"%'");
		}
		if(!mapUtil.isBlank("code1")){
			String name = mapUtil.get("code1");
			namedParams.put("code1","%"+name+"%");
			namedSql.append(" and m.code like '%"+name+"%'");
		}
		Map<String,Object> rs = new HashMap<String,Object>();
		rs.put("namedSql",namedSql);
		rs.put("namedParams",namedParams);
		return rs;
	}

	/**
	 * @Title: save
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param mapping
	 * @return void
	 * @throws LedpException 
	 */
	public void save(Mapping mapping) throws LedpException {
		mybaitsGenericDao.save(mapping);
	}

	/**
	 * @Title: get
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param id
	 * @return
	 * @return Mapping
	 * @throws LedpException 
	 */
	public Mapping get(long id) throws LedpException {
		return mybaitsGenericDao.get(Mapping.class, id);
	}

	/**
	 * @Title: update
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param mapping2
	 * @return void
	 * @throws LedpException 
	 */
	public void update(Mapping mapping) throws LedpException {
		mybaitsGenericDao.update(mapping);
		
	}

	/**
	 * @Title: delete
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param id
	 * @return void
	 * @throws LedpException 
	 */
	public void delete(long id) throws LedpException {
		mybaitsGenericDao.delete(Mapping.class, id);
	}

	/**
	 * @Title: find
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param type
	 * @param record
	 * @return
	 * @return Mapping
	 * @throws LedpException 
	 */
	public Mapping find(Constant type, Long record) throws LedpException {
		return mybaitsGenericDao.find(Mapping.class,"select * from t_mapping where type='"+type.getId()+"' and record="+record);
	}
	public Mapping finds(Constant type,String codeName,String code,Long id) throws LedpException {
		return mybaitsGenericDao.find(Mapping.class,"select * from t_mapping where type='"+type.getId()+"' and "+codeName+"='"+code+"' and id not in ("+id+")");
	}
	public Mapping finds1(Constant type,String codeName,String code) throws LedpException {
		return mybaitsGenericDao.find(Mapping.class,"select * from t_mapping where type='"+type.getId()+"' and "+codeName+"='"+code+"'");
	}
	public Mapping findss(Constant type,String codeName,String code,Long currentId) throws LedpException {
		return mybaitsGenericDao.find(Mapping.class,"select * from t_mapping where id<>"+currentId+" and type='"+type.getId()+"' and "+codeName+"='"+code+"'");
	}

	public List<Map> getUnbindRecords(Constant type) throws LedpException{
		
		Constant statusInstance = constantService.find("record_status", "active");
		if(type==null){
			return new ArrayList<Map>();
		}
		String caluse = "";
		if("provice".equals(type.getCode())){
			caluse = "and level=1";
		}else if("city".equals(type.getCode())){
			caluse = "and level=2";
		}else if("district".equals(type.getCode())){
			caluse = "and level=3";
		}else{
			caluse = " and status="+statusInstance.getId();
		}
		
		String sql = "select id,name from "+type.getComment()+" where 1=1 "+caluse+" order by id asc";
		List<Map> rows = mybaitsGenericDao.executeQuery(sql);
		List<Map> records = new ArrayList<Map>();
		for(Map row:rows){
			String id = row.get("id").toString();
			String name = row.get("name").toString();
			Map record = new HashMap();
			record.put("id",row.get("id"));
			record.put("name",getRecordName(type.getCode(),Long.parseLong(id),name));
			records.add(record);
		}
		return records;
	}
	
	public String getRecordName(String type,Long id,String name) throws LedpException{
		if("vehicle".equals(type)){
			Map vehicle = mybaitsGenericDao.find("select id,name,series from t_vehicle where id ="+id);
			Map series = mybaitsGenericDao.find("select id,name from t_vehicle_series where id ="+vehicle.get("series"));
			return series.get("name")+">"+name;
		}
		if("city".equals(type)){
			Map provice = mybaitsGenericDao.find("select id,name from t_region where id =(select parent from t_region where id = "+id+")");
			return provice.get("name")+">"+name;
		}
		if("district".equals(type)){
			Map district = mybaitsGenericDao.find("select id,name,parent from t_region where id ="+id);
			Map city = mybaitsGenericDao.find("select id,name,parent from t_region where id ="+district.get("parent"));
			Map provice=null ;
			if(city != null){
				provice= mybaitsGenericDao.find("select id,name from t_region where id ="+city.get("parent"));
				return provice.get("name")+">"+city.get("name")+">"+name;
			}
		}
		return name;
	}

	/**
	 * @Title: getRecord
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param mapping
	 * @return
	 * @return Object
	 * @throws LedpException 
	 */
	public Map getRecord(Mapping mapping) throws LedpException {
		Constant type = mapping.getType();
		String sql = "select * from "+type.getComment()+" where id="+mapping.getRecord();
		return mybaitsGenericDao.find(sql);
	}
	
	/**
	 * @Title: getRecord
	 * @Description: TODO(根据类型和记录id获取映射关系)
	 * @param mapping
	 * @return
	 * @return Object
	 * @throws LedpException 
	 */
	public Map getMapping(String typeId,String recordId) throws LedpException {
		String sql = "select * from t_mapping where type="+typeId+" and record="+recordId+"";
		return mybaitsGenericDao.find(sql);
	}
}
