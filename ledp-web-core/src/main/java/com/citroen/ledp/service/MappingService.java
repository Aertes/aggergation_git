package com.citroen.ledp.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.citroen.ledp.dao.mybaits.MybaitsGenericDao;
import com.citroen.ledp.domain.Constant;
import com.citroen.ledp.domain.Mapping;
import com.citroen.ledp.exception.LedpException;
import com.citroen.ledp.util.MapUtil;

/**
 * @Title: MappingService.java
 * @Package com.citroen.ledp.service
 * @Description: TODO(用一句话描述该文件做什么)
 * @author 廖启洪
 * @date 2015年3月16日 下午8:13:55
 * @version V1.0
 */

public interface MappingService {

	List<Map> executeQuery(Map params) throws LedpException;
	
	int getTotalRow(Map params) throws LedpException;
	
	Map<String,Object> getCondition(Map<String,Object> params);

	/**
	 * @Title: save
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param mapping
	 * @return void
	 * @throws LedpException 
	 */
	void save(Mapping mapping) throws LedpException ;

	/**
	 * @Title: get
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param id
	 * @return
	 * @return Mapping
	 * @throws LedpException 
	 */
	Mapping get(long id) throws LedpException ;

	/**
	 * @Title: update
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param mapping2
	 * @return void
	 * @throws LedpException 
	 */
	void update(Mapping mapping) throws LedpException ;

	/**
	 * @Title: delete
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param id
	 * @return void
	 * @throws LedpException 
	 */
	void delete(long id) throws LedpException ;

	/**
	 * @Title: find
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param type
	 * @param record
	 * @return
	 * @return Mapping
	 * @throws LedpException 
	 */
	Mapping find(Constant type, Long record) throws LedpException ;
	Mapping finds(Constant type,String codeName,String code,Long id) throws LedpException;
	Mapping finds1(Constant type,String codeName,String code) throws LedpException ;
	Mapping findss(Constant type,String codeName,String code,Long currentId) throws LedpException ;

	List<Map> getUnbindRecords(Constant type) throws LedpException;
	
	String getRecordName(String type,Long id,String name) throws LedpException;

	/**
	 * @Title: getRecord
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param mapping
	 * @return
	 * @return Object
	 * @throws LedpException 
	 */
	Map getRecord(Mapping mapping) throws LedpException ;
	
	/**
	 * @Title: getRecord
	 * @Description: TODO(根据类型和记录id获取映射关系)
	 * @param mapping
	 * @return
	 * @return Object
	 * @throws LedpException 
	 */
	Map getMapping(String typeId,String recordId) throws LedpException ;
}
