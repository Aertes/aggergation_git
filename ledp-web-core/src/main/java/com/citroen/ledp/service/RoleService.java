package com.citroen.ledp.service;

import com.citroen.ledp.domain.Permission;
import com.citroen.ledp.domain.Role;
import com.citroen.ledp.exception.LedpException;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Title: RoleService.java
 * @Package com.citroen.ledp.service
 * @Description: TODO(用一句话描述该文件做什么)
 * @author 廖启洪
 * @date 2015年3月14日 下午5:29:13
 * @version V1.0
 */

public interface RoleService {

	void save(Role role,Set<String> checkeds) throws LedpException;
	void update(Role role) throws LedpException;


	void update(Role role,Set<String> checkeds) throws LedpException;

	Role get(long id) throws LedpException ;

	Role find(String name) throws LedpException ;

	List<Permission> getRolePermission(Long parent,Long roleId) throws LedpException ;

	/**
	 * @Title: hasChildren
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param id
	 * @return
	 * @return boolean
	 * @throws LedpException
	 */
	boolean hasChildren(Long parent) throws LedpException ;

	/**
	 * query children permission by parentId
	 * @param parentId
	 * @return
	 */
	List<Permission> queryChildPermission(Long parentId);

	/**
	 * @Title: executeQuery
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param params
	 * @return
	 * @return List<Organization>
	 * @throws LedpException
	 */
	List<Role> executeQuery(Map<String, Object> params) throws LedpException;

	/**
	 * @Title: getTotalRow
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param params
	 * @return
	 * @return int
	 */
	int getTotalRow(Map params) throws LedpException;

	Map<String,Object> getCondition(Map<String,Object> params);
}
