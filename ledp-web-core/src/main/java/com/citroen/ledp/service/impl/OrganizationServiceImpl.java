package com.citroen.ledp.service.impl;

import com.citroen.ledp.dao.mybaits.MybaitsGenericDao;
import com.citroen.ledp.domain.Constant;
import com.citroen.ledp.domain.Organization;
import com.citroen.ledp.domain.User;
import com.citroen.ledp.exception.LedpException;
import com.citroen.ledp.exception.LogicException;
import com.citroen.ledp.service.ConstantService;
import com.citroen.ledp.service.OrganizationService;
import com.citroen.ledp.util.MapUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

@Service
public class OrganizationServiceImpl implements OrganizationService{

	@Autowired
	private MybaitsGenericDao<Long> mybaitsGenericDao;
	@Autowired
	private ConstantService constantService;

	public List<Organization> executeQuery(Map params) throws LedpException {
		Map<String, Object> condition = getCondition(params);
		Map namedParams = (Map) condition.get("namedParams");
		String sql = "select * from t_organization where 1=1" + condition.get("namedSql");
		if (params.containsKey("sortName")) {

			if(params.get("sortName").toString().equals("parent.name")){
				sql += " order by parent";
			}else if(params.get("sortName").toString().equals("status.name")){
				sql += " order by status";
			}else{
				sql += " order by " + params.get("sortName");
			}
		}
		if (params.containsKey("sortOrder")) {
			sql += " " + params.get("sortOrder");
		}

		Map paginateParams = new HashMap();
		int pageNumber = (Integer) params.get("pageNumber");
		int pageSize = (Integer) params.get("pageSize");
		int offset = (pageNumber - 1) * pageSize;
		paginateParams.put("max", pageSize);
		paginateParams.put("offset", offset);
		return mybaitsGenericDao.executeQuery(Organization.class, sql, namedParams, paginateParams);
	}

	public int getTotalRow(Map params) throws LedpException {
		Map<String, Object> condition = getCondition(params);
		Map namedParams = (Map) condition.get("namedParams");
		String sql = "select count(id) count from t_organization where 1=1" + condition.get("namedSql");
		List<Map> list = mybaitsGenericDao.executeQuery(sql, namedParams);
		if (list.isEmpty()) {
			return 0;
		}
		Map map = list.get(0);
		return map.isEmpty() ? 0 : Integer.parseInt(map.get("count").toString());
	}

	public Map<String, Object> getCondition(Map<String, Object> params) {
		MapUtil<String, Object> mapUtil = new MapUtil<String, Object>(params);
		Map<String, Object> namedParams = new HashMap<String, Object>();

		StringBuilder namedSql = new StringBuilder();
		if(params.get("parent")==null ||params.get("parent").toString().equals("1")){
			namedSql.append(" and parent=" + params.get("parent"));
		}else{
			namedSql.append(" and id=" + params.get("parent"));
		}

		if (!mapUtil.isBlank("name")) {
			String name = mapUtil.get("name");
			namedParams.put("name", "%" + name + "%");
			namedSql.append(" and name like :name");
		}
		if (!mapUtil.isBlank("statusId")) {
			Long statusId = Long.parseLong(params.get("statusId").toString());
			namedParams.put("statusId", statusId);
			namedSql.append(" and status =:statusId");
		}

		Map<String, Object> rs = new HashMap<String, Object>();
		rs.put("namedSql", namedSql);
		rs.put("namedParams", namedParams);
		return rs;
	}

	public Organization get(long id) throws LedpException {
		return mybaitsGenericDao.get(Organization.class, id);
	}

	public Long save(Organization entity) throws LedpException {
		Organization parent = get(entity.getParent().getId());
		entity.setLevel(parent.getLevel() + 1);
		return mybaitsGenericDao.save(entity);
	}

	public void update(Organization entity) throws LedpException {
		mybaitsGenericDao.update(entity);
	}

	public void delete(Long id) throws LedpException {
		Organization entity = mybaitsGenericDao.get(Organization.class, id);
		if (entity == null) {
			throw new LogicException("操作对象不存在！");
		}
		entity.setStatus(constantService.find("record_status", "delete"));
		mybaitsGenericDao.update(entity);
	}

	public void active(Long id) throws LedpException {
		Organization entity = mybaitsGenericDao.get(Organization.class, id);
		if (entity == null) {
			throw new LogicException("操作对象不存在！");
		}
		entity.setStatus(constantService.find("record_status", "active"));
		mybaitsGenericDao.update(entity);
	}

	public void inactive(Long id) throws LedpException {
		Organization entity = mybaitsGenericDao.get(Organization.class, id);
		if (entity == null) {
			throw new LogicException("操作对象不存在！");
		}
		entity.setStatus(constantService.find("record_status", "inactive"));
		mybaitsGenericDao.update(entity);
	}

	public Organization find(Long parent, String name) throws LedpException {
		String query = "select * from t_organization where parent=" + parent + " and name='" + name + "'";
		return mybaitsGenericDao.find(Organization.class, query);
	}

	public Organization getRoot() throws LedpException {
		String query = "select * from t_organization where parent is null";
		return mybaitsGenericDao.find(Organization.class, query);
	}
	public Organization getFirstOrganization() throws LedpException {
		String query = "select * from t_organization where parent=1 and status=1010 ";
		return mybaitsGenericDao.find(Organization.class, query);
	}
	public boolean hasChildren(Long parent) throws LedpException {
		String query = "select count(id) count from t_organization where parent=" + parent;
		if (parent == null) {
			query = "select count(id) count from t_organization where parent is null";
		}
		Map<?, ?> row = mybaitsGenericDao.find(query);
		Object count = row.get("count");
		return (count == null) ? false : Integer.parseInt(count.toString()) > 0;
	}

	public List<Organization> getChildren(Long parent) throws LedpException {
		if (parent == null) {
			return mybaitsGenericDao.executeQuery(Organization.class,
					"select * from t_organization where parent is null order by id asc");
		}
		return mybaitsGenericDao.executeQuery(Organization.class, "select * from t_organization where parent=" + parent
				+ " order by id asc");
	}
	public List<Organization> getChildrenByID(Long parent) throws LedpException {
		return mybaitsGenericDao.executeQuery(Organization.class, "select * from t_organization where id=" + parent
				+ " order by id asc");
	}
	public List<Organization> getActiviteChildren(Long parent) throws LedpException {
		if (parent == null) {
			return mybaitsGenericDao.executeQuery(Organization.class,
					"select * from t_organization where parent is null  and status=1010 order by id asc");
		}
		return mybaitsGenericDao.executeQuery(Organization.class, "select * from t_organization where parent=" + parent
				+ " and status=1010 order by id asc");
	}
	public List<User> getUserList(Long orgId) throws LedpException {

		Constant status = constantService.find("record_status", "active");
		if (orgId == null) {
			Organization organization = mybaitsGenericDao.executeQuery(Organization.class,
					" select * from t_organization where parent is null order by id asc ").get(0);
			if (organization == null) {
				return null;
			}
			return mybaitsGenericDao.executeQuery(User.class, "select * from t_user where org=" + organization.getId()
					+ " and status=" + status.getId());
		}
		return mybaitsGenericDao.executeQuery(User.class,
				"select * from t_user where org=" + orgId + " and status=" + status.getId());
	}

	@SuppressWarnings("rawtypes")
	public int getChildrenCount(long parent) throws LedpException {
		List<Map> list = mybaitsGenericDao.executeQuery("select count(1) as ct from t_organization where status=1010 and parent=" + parent);

		int totalCount = 0;
		for (Map map : list) {
			totalCount = Integer.parseInt(map.get("ct").toString());
		}
		return totalCount;
	}
}
