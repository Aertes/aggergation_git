package com.citroen.ledp.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.citroen.ledp.dao.mybaits.MybaitsGenericDao;
import com.citroen.ledp.domain.Log;
import com.citroen.ledp.domain.User;
import com.citroen.ledp.exception.LedpException;


public interface LogService {

	/**
	 * 
	 * queryLog:(根据条件进行分页查询列表). <br/>
	 * 
	 * @param params
	 * @param pageSize
	 * @param pageNumber
	 * @return
	 */
	List<Log> queryLog(Log log, int pageSize, int pageNumber,String name,String date1,String date2,String sortName,String sortOrder) ;
	
	
	@SuppressWarnings("unchecked")   //  有疑问
	Integer getTotal(Log log,String name,String date1,String date2);
	
	
	/**
	 * 
	 * getCondition:(查询条件). <br/>
	 * 
	 * @param params
	 * @return
	 */
	Map<String, Object> getCondition(Log log, StringBuffer sql,String name,String date1,String date2) throws LedpException ;


}
