package com.citroen.ledp.service.impl;

import com.citroen.ledp.dao.mybaits.MybaitsGenericDao;
import com.citroen.ledp.domain.Constant;
import com.citroen.ledp.domain.Permission;
import com.citroen.ledp.domain.Role;
import com.citroen.ledp.domain.RolePermission;
import com.citroen.ledp.exception.LedpException;
import com.citroen.ledp.service.ConstantService;
import com.citroen.ledp.service.RoleService;
import com.citroen.ledp.util.MapUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @Title: RoleService.java
 * @Package com.citroen.ledp.service
 * @Description: TODO(用一句话描述该文件做什么)
 * @author 廖启洪
 * @date 2015年3月14日 下午5:29:13
 * @version V1.0
 */
@Service
public class RoleServiceImpl implements RoleService{
	@Autowired
	private MybaitsGenericDao<Long> genericDao;
	@Autowired
	private ConstantService constantService;

    @Transactional
	public void save(Role role,Set<String> checkeds) throws LedpException{
		Long id = genericDao.save(role);
		role.setId(id);
		for(String checked:checkeds){
			RolePermission rp = new RolePermission();
			try{
				Permission permission = new Permission();
				permission.setId(Long.parseLong(checked.trim()));
				rp.setRole(role);
				rp.setPermission(permission);
			}catch(Exception e){}
			genericDao.save(rp);
		}
	}
	public void update(Role role) throws LedpException{
		genericDao.update(role);
	}

    @Transactional
	public void update(Role role,Set<String> checkeds) throws LedpException{
		genericDao.update(role);
		//删除旧权限
		List<RolePermission> rps = genericDao.executeQuery(RolePermission.class,"select id from t_role_permission where role= "+role.getId());
		for(RolePermission rp:rps){
			genericDao.delete(RolePermission.class,rp.getId());
		}
		//添加新权限
		for(String checked:checkeds){
			RolePermission rp = new RolePermission();
			try{
				Permission permission = new Permission();
				permission.setId(Long.parseLong(checked.trim()));
				rp.setRole(role);
				rp.setPermission(permission);
			}catch(Exception e){}
			genericDao.save(rp);
		}
	}

	public Role get(long id) throws LedpException {
		return genericDao.get(Role.class, id);
	}
	
	public Role find(String name) throws LedpException {
		return genericDao.find(Role.class,"select id,name from t_role where name='"+name+"'");
	}
	
	public List<Permission> getRolePermission(Long parent,Long roleId) throws LedpException {
		if(roleId==null){
			return new ArrayList<Permission>();
		}
		
		String query = "";
		if(parent==null){
			query = "select p.*  from t_permission p left join t_role_permission rp on p.id = rp.permission where p.status ='active' and p.parent is null and rp.role = "+roleId+" order by p.weight asc";
		}else{
			query = "select p.*  from t_permission p left join t_role_permission rp on p.id = rp.permission where p.status ='active' and p.parent = "+parent+" and rp.role = "+roleId+" order by p.weight asc";
		}
		return genericDao.executeQuery(Permission.class,query);
	}

	/**
	 * @Title: hasChildren
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param id
	 * @return
	 * @return boolean
	 * @throws LedpException 
	 */
	public boolean hasChildren(Long parent) throws LedpException {
		String query = null;
		if(parent==null){
			query = "select count(p.id) count from t_permission p where p.status ='active' and p.parent is null";
		}else{
			query = "select count(p.id) count from t_permission p where p.status ='active' and p.parent = "+parent;
		}
		Map row = genericDao.find(query);
		Object c = row.get("count");
		if(c==null){
			return false;
		}
		try{
			return Long.parseLong(row.get("count").toString())>0;
		}catch(Exception e){
		}
		return false;
	}
	
	/**
	 * query children permission by parentId
	 * @param parentId
	 * @return
	 */
	public List<Permission> queryChildPermission(Long parentId){
		String query ="select * from t_permission where parent.id="+parentId+" and status ='active'";
		try {
			return genericDao.executeQuery(Permission.class,query);
		} catch (LedpException e) {
			return null;
		}
	}

	/**
	 * @Title: executeQuery
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param params
	 * @return
	 * @return List<Organization>
	 * @throws LedpException 
	 */
	public List<Role> executeQuery(Map<String, Object> params) throws LedpException {
		Map<String,Object> condition = getCondition(params);
		Map namedParams = (Map)condition.get("namedParams");
		Constant delete = constantService.find("record_status","delete");
		String sql = "select * from t_role where id<>1 and status<>"+delete.getId() +condition.get("namedSql");
		
		Map paginateParams = new HashMap();
		int pageNumber= (Integer) params.get("pageNumber") != null?(Integer) params.get("pageNumber"):1;
		int pageSize=(Integer) params.get("pageSize") != null? (Integer) params.get("pageSize"):10;
		int offset = (pageNumber - 1) * pageSize;
		paginateParams.put("max",pageSize);
		paginateParams.put("offset",offset);
		
		return genericDao.executeQuery(Role.class,sql,namedParams,paginateParams);
	}

	/**
	 * @Title: getTotalRow
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param params
	 * @return
	 * @return int
	 */
	public int getTotalRow(Map params) throws LedpException{
		Map<String,Object> condition = getCondition(params);
		Map namedParams = (Map)condition.get("namedParams");
		Constant delete = constantService.find("record_status","delete");
		String sql = "select count(id) count from t_role where id<>1 and status<>"+delete.getId() +condition.get("namedSql");
		List<Map> list = genericDao.executeQuery(sql,namedParams);
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
			namedSql.append(" and name like :name");
		}
		if(!mapUtil.isBlank("statusId")){
			Long statusId = Long.parseLong(params.get("statusId").toString());
			namedParams.put("statusId",statusId);
			namedSql.append(" and status =:statusId");
		}

		Map<String,Object> rs = new HashMap<String,Object>();
		rs.put("namedSql",namedSql);
		rs.put("namedParams",namedParams);
		return rs;
	}
	
}
