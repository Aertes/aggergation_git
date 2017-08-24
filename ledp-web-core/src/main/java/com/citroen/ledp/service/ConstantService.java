package com.citroen.ledp.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.citroen.ledp.dao.mybaits.MybaitsGenericDao;
import com.citroen.ledp.domain.Constant;
import com.citroen.ledp.exception.LedpException;

/**
/**
 * @Title: OrganizationService.java
 * @Package com.citroen.ledp.service
 * @Description: TODO(机构服务类)
 * @author 廖启洪
 * @date 2015年1月25日 下午3:13:13
 * @version V1.0
 */


public interface ConstantService {

	Constant find(String type,String code) throws LedpException;
	
	List<Constant> findAll(String type) throws LedpException;
	
	List<Constant> findAll(String type,int status) throws LedpException;
	Constant get(long id) throws LedpException ;
	
	/**
	 * 根据常量名称和类型查询
	 * @param name
	 * @param category
	 * @return
	 * @throws LedpException 
	 */
	Constant getByNameAndCategory(String name,Long category) throws LedpException;
}
