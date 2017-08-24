package com.citroen.ledp.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.citroen.ledp.dao.mybaits.MybaitsGenericDao;
import com.citroen.ledp.domain.Constant;
import com.citroen.ledp.domain.Organization;
import com.citroen.ledp.domain.User;
import com.citroen.ledp.exception.LedpException;
import com.citroen.ledp.exception.LogicException;
import com.citroen.ledp.util.MapUtil;

/**
 * /**
 *
 * @Title: OrganizationService.java
 * @Package com.citroen.ledp.service
 * @Description: TODO(机构服务类)
 * @author 廖启洪
 * @date 2015年1月25日 下午3:13:13
 * @version V1.0
 */

public interface OrganizationService {

	List<Organization> executeQuery(Map params) throws LedpException ;

	int getTotalRow(Map params) throws LedpException;

	Map<String, Object> getCondition(Map<String, Object> params) ;

	Organization get(long id) throws LedpException ;
	Long save(Organization entity) throws LedpException ;

	void update(Organization entity) throws LedpException ;

	void delete(Long id) throws LedpException ;

	void active(Long id) throws LedpException ;

	void inactive(Long id) throws LedpException ;

	Organization find(Long parent, String name) throws LedpException;

	Organization getRoot() throws LedpException;
	Organization getFirstOrganization() throws LedpException;
	boolean hasChildren(Long parent) throws LedpException;

	List<Organization> getChildren(Long parent) throws LedpException;
	List<Organization> getChildrenByID(Long parent) throws LedpException;
	List<Organization> getActiviteChildren(Long parent) throws LedpException;
	List<User> getUserList(Long orgId) throws LedpException ;
	int getChildrenCount(long parent) throws LedpException ;
}
