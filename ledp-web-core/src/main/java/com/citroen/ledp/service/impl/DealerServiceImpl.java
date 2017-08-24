/**
 * Copyright © 2013-2015 上海扬邑企业管理咨询有限公司.
 * Project Name:ledp-web-core
 * File Name:DealerService.java
 * Package Name:com.citroen.ledp.service
 * Date:2015-3-5下午1:39:42
 * Description: //模块目的、功能描述      
 * History: //修改记录
 */

package com.citroen.ledp.service.impl;

import com.citroen.ledp.dao.mybaits.MybaitsGenericDao;
import com.citroen.ledp.domain.*;
import com.citroen.ledp.exception.LedpException;
import com.citroen.ledp.service.ConstantService;
import com.citroen.ledp.service.DealerService;
import org.apache.commons.lang.StringUtils;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ClassName:DealerService <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2015-3-5 下午1:39:42 <br/>
 * 
 * @author 刘学斌
 * @version V1.0
 */

@Service
public class DealerServiceImpl implements DealerService{

	private static Logger logger = Logger.getLogger(DealerServiceImpl.class);

	private MybaitsGenericDao<Long> genericDao;

	@Autowired
	private ConstantService constantService;

	private static final Long ORGANIZATION_ID = 1L;

	@SuppressWarnings("unchecked")
	public Integer getTotal(Dealer dealer) {

		StringBuffer sql = new StringBuffer();
		sql.append("select count(id) from t_dealer where 1=1 ");
		Map<String, Object> rs = null;
		try {
			Map<String, Object> nameParams = getCondition(dealer, sql, null, null);
			rs = genericDao.executeQuery(sql.toString(), nameParams).get(0);

		} catch (LedpException e) {
			logger.error("获取网店总量出错" + e.getMessage());
			e.printStackTrace();
		}
		return rs.isEmpty() ? 0 : Integer.valueOf(rs.get("count(id)").toString());
	}

	/**
	 * 
	 * queryByOrganization:(根据所属机构获取网店列表). <br/>
	 * 
	 * @author 刘学斌 Date: 2015-3-6 上午9:56:19
	 * @param orgId
	 * @return
	 */
	public List<Dealer> queryByOrganization(Long orgId) {
		String sql = "select * from t_dealer where organization=" + orgId;
		try {
			return genericDao.executeQuery(Dealer.class, sql);
		} catch (LedpException e) {
			logger.error("根据所属机构获取网店列表出错" + e.getMessage());
		}
		return null;
	}

	/**
	 * 获取网点信息
	 * 
	 * @param id
	 * @return
	 */
	public List<Dealer> get(Long id) {
		String sql = "select * from t_dealer where id=" + id;
		try {
			return genericDao.executeQuery(Dealer.class, sql);
		} catch (LedpException e) {
            logger.error(e.getMessage());
			return null;
		}
	}

	/**
	 * 获取网点信息
	 * 
	 * @param id
	 * @return
	 */
	public Dealer gets(Long id) {
		try {
			return genericDao.get(Dealer.class, id);
		} catch (LedpException e) {
            logger.error("获取网点信息为空，错误信息为："+e.getMessage());
			return null;
		}
	}

	/**
	 * 
	 * queryByCondition:(根据条件获取网店列表). <br/>
	 * 
	 * @author 刘学斌 Date: 2015-3-6 上午10:37:42
	 * @param dealer
	 * @return
	 */
	public List<Dealer> queryByCondition(Dealer dealer) {

		List<Dealer> result = null;
		StringBuffer sql = new StringBuffer();
		sql.append("select * from t_dealer where organization=1");
		try {
			Map<String, Object> nameParams = getCondition(dealer, sql, null, null);
			result = genericDao.executeQuery(Dealer.class, sql.toString(), nameParams);
		} catch (LedpException e) {
			logger.error("根据条件获取网店列表出错" + e.getMessage());
		}
		return result;
	}

	/**
	 * 
	 * queryByConditionForPaginate:(根据条件进行分页查询列表). <br/>
	 * 
	 * @author 刘学斌 Date: 2015-3-6 上午10:43:27
	 * @param dealer
	 * @param pageSize
	 * @param pageNumber
	 * @return
	 */
	public List<Dealer> queryByConditionForPage(Dealer dealer, int pageSize, int pageNumber, String sortName, String sortOrder) {

		List<Dealer> result = null;
		Map<String, Object> paginate = new HashMap<String, Object>();

		StringBuffer sql = new StringBuffer();
		sql.append("select * from t_dealer where 1=1 ");
		
		int offset = (pageNumber - 1) * pageSize;
		paginate.put("offset", offset);
		paginate.put("max", pageSize);
		try {
			Map<String, Object> nameParams = getCondition(dealer, sql, sortName, sortOrder);
			result = genericDao.executeQuery(Dealer.class, sql.toString(), nameParams, paginate);
		} catch (LedpException e) {
			logger.error("根据分页查询网店列表出错，异常信息为：" + e.getMessage());
		}
		return result;
	}

	/**
	 * 
	 * checkCode:(网点编码的唯一性校验). <br/>
	 * 
	 * @author 刘学斌 Date: 2015-3-19 下午3:40:11
	 * @param code
	 * @param id
	 * @return
	 * @throws LedpException
	 */
	public List<Dealer> checkCode(String code, Long id) throws LedpException {
		if (id != null) {
			return genericDao.executeQuery(Dealer.class, " select * from t_dealer where 1=1 and code='" + code + "'"
					+ " and id != " + id);
		}
		return genericDao.executeQuery(Dealer.class, " select * from t_dealer where 1=1 and code='" + code + "'");
	}

	/**
	 * 
	 * getUsers:(根据机构id获取此机构下的成员). <br/>
	 * 
	 * @author 刘学斌 Date: 2015-3-17 下午6:45:34
	 * @param orgId
	 * @return
	 * @throws LedpException
	 */
	public List<User> getUsers(Long orgId) throws LedpException {

		Constant status = constantService.find("record_status", "active");
		if (orgId == null) {
			Organization organization = genericDao.executeQuery(Organization.class,
					" select * from t_organization where parent is null order by id asc ").get(0);
			if (organization == null) {
				return null;
			}
			return genericDao.executeQuery(User.class, "select * from t_user where org=" + organization.getId() + " and status="
					+ status.getId());
		}
		return genericDao.executeQuery(User.class, "select * from t_user where org=" + orgId + " and status=" + status.getId());
	}

	public List<Constant> getTypeList(String categoryCode) {
		String categorySql = "select * from t_constant_category where code='" + categoryCode + "'";
		ConstantCategory category = null;
		List<Constant> constantList = null;
		try {
			category = genericDao.find(ConstantCategory.class, categorySql);
			if (category != null) {
				String sql = "select * from t_constant where category=" + category.getId();
				constantList = genericDao.executeQuery(Constant.class, sql);
			}
		} catch (LedpException e1) {
            logger.error("获取分类信息出错，异常信息为：" + e1.getMessage());
		}
		return constantList;
	}

	public Organization getOrganization(long id) {
		try {
			return genericDao.get(Organization.class, id);
		} catch (LedpException e) {
            logger.error("根据组织id获取组织出错，异常信息为：" + e.getMessage());
		}
		return null;
	}

	public User getUser(Long userId) {
		try {
			return genericDao.get(User.class, userId);
		} catch (LedpException e) {
            logger.error("根据用户id获取用户出错，异常信息为：" + e.getMessage());
		}
		return null;
	}

	public Constant getManager(String code) {
		String sql = "select * from t_constant where code='" + code + "'";
		try {
			return genericDao.find(Constant.class, sql);
		} catch (LedpException e) {
            logger.error("根据常量id获取常量出错，异常信息为：" + e.getMessage());
		}
		return null;
	}

	public Organization getOrg(Long orgId) {
		try {
			return genericDao.get(Organization.class, orgId);
		} catch (LedpException e) {
            logger.error("异常信息为：" + e.getMessage());
		}
		return null;
	}

	public List<Media> getMediaList() {
		String sql = "select * from t_media where status=1010 order by id asc";
		try {
			return genericDao.executeQuery(Media.class, sql);
		} catch (LedpException e) {
            logger.error("异常信息为：" + e.getMessage());
        }
        return null;
	}

	public void update(Dealer dealer) throws LedpException {
		genericDao.update(dealer);
	}

	public Long save(Dealer dealer) throws LedpException {
		return genericDao.save(dealer);
	}

	public void delete(Long id) {
		try {
			genericDao.delete(DealerMedia.class, id);
		} catch (LedpException e) {
            logger.error("异常信息为：" + e.getMessage());
		}
	}

    @Transactional
	public Long saveDealerMedia(String medias, Long dealer) {
		if (StringUtils.isBlank(medias) || dealer == null) {
			return null;
		}
		String[] media = StringUtils.split(medias, ",");
		DealerMedia dealerMedia = null;
		try {
			for (String s : media) {
				Media m = genericDao.get(Media.class, Long.parseLong(s));
				if (m != null && dealer != null) {
					dealerMedia = new DealerMedia();
					dealerMedia.setDealer(genericDao.get(Dealer.class, dealer));
					dealerMedia.setMedia(m);
					genericDao.save(dealerMedia);
				}
			}
		} catch (Exception e) {
			logger.error("保存合作媒体出错！" + e.getMessage());
		}
		return null;
	}

	public Dealer getDealerInstance(Long dealerId) throws LedpException {
		return genericDao.get(Dealer.class, dealerId);
	}

	public List<DealerMedia> getDealerMedia(Long dealerId) {
		String sql = "select * from t_dealer_media where dealer=" + dealerId;
		try {
			return genericDao.executeQuery(DealerMedia.class, sql);
		} catch (LedpException e) {
            logger.error("异常信息为：" + e.getMessage());
		}
		return null;
	}

	public Map<String, Object> getCondition(Dealer dealer, StringBuffer sql, String sortName, String sortOrder)
			throws LedpException {
		Map<String, Object> condition = new HashMap<String, Object>();

		String orgid = ((dealer == null || dealer.getOrganization() == null) ? getOrganizationIds(ORGANIZATION_ID)
				: getOrganizationIds(dealer.getOrganization().getId()));
		sql.append(" and organization in(" + orgid + ")");

		if (dealer != null) {
			if (StringUtils.isNotBlank(dealer.getName())) {
				condition.put("name", "%" + dealer.getName() + "%");
				sql.append(" and name like :name");
			}
			if (StringUtils.isNotBlank(dealer.getCode())) {
				condition.put("code", "%" + dealer.getCode() + "%");
				sql.append(" and code like :code");
			}
			if (dealer.getStatus() != null && dealer.getStatus().getId() != null) {
				condition.put("status", dealer.getStatus().getId());
				sql.append(" and status=:status");
			}
			if (dealer.getType() != null && dealer.getType().getId() != null) {
				condition.put("type", dealer.getType().getId());
				sql.append(" and type=:type");
			}
		}
		if (StringUtils.isBlank(sortName) && StringUtils.isBlank(sortOrder)) {
			sql.append(" order by id desc");
		}
		if (StringUtils.isNotBlank(sortName) && StringUtils.isNotBlank(sortOrder)) {
			if(sortName.equals("organization.name")){
				sql.append(" order by organization " + sortOrder);
			}else if(sortName.equals("type.name")){
				sql.append(" order by type " + sortOrder);
			}else if(sortName.equals("status.name")){
				sql.append(" order by status " + sortOrder);
			}else if(sortName.equals("dateCreate")){
				sql.append(" order by date_create " + sortOrder);
			}else{
				sql.append(" order by " + sortName + " " + sortOrder);
			}
		}
		return condition;
	}

	public boolean hasChildren(Long parent) throws LedpException {
		String query = "select count(id) count from t_organization where parent=" + parent;
		if (parent == null) {
			query = "select count(id) count from t_organization where parent is null";
		}
		Map<?, ?> row = genericDao.find(query);
		Object count = row.get("count");
		return (count == null) ? false : Integer.parseInt(count.toString()) > 0;
	}

	public List<Organization> getChildren(Long parent) throws LedpException {
		if (parent == null) {
			return genericDao.executeQuery(Organization.class,
					"select * from t_organization where parent is null order by id asc");
		}
		return genericDao.executeQuery(Organization.class, "select * from t_organization where parent=" + parent
				+ " order by id asc");
	}

	public List<Dealer> getByOrganizationId(Long orgId) throws LedpException {
		if (null == orgId) {
			return genericDao.executeQuery(Dealer.class, "select * from t_dealer order by id asc");
		}
		return genericDao.executeQuery(Dealer.class, "select * from t_dealer where organization=" + orgId + " order by id asc");
	}
	public List<Dealer> getByActivitOrganizationId(Long orgId) throws LedpException {
		if (null == orgId) {
			return genericDao.executeQuery(Dealer.class, "select * from t_dealer where status=1010 order by id asc");
		}
		return genericDao.executeQuery(Dealer.class, "select * from t_dealer where organization=" + orgId + " and status=1010 order by id asc");
	}
	public Dealer getDealerBydealerId(long id) throws LedpException{
		return genericDao.find(Dealer.class, "select * from t_dealer where id=" + id + " and status=1010 order by id asc");
	}
	public List<Organization> getChildren(List<Organization> childs, List<Organization> all) throws LedpException {
		if (!childs.isEmpty()) {
			all.addAll(childs);
			for (Organization o : childs) {
				if (hasChildren(o.getId())) {
					return getChildren(getChildren(o.getId()), all);
				}
			}
		}
		return all;
	}

	public String getOrganizationIds(Long current) throws LedpException {
		List<Organization> org = new ArrayList<Organization>();
		List<Organization> list = getChildren(current);
		List<Organization> childs = getChildren(list, org);
		StringBuffer buffer = new StringBuffer();
		buffer.append(current);
		if (!childs.isEmpty()) {
			buffer.append(",");
		}
		for (int i = 0; i < childs.size(); i++) {
			if (i == childs.size() - 1) {
				buffer.append(childs.get(i).getId());
			} else {
				buffer.append(childs.get(i).getId()).append(",");
			}
		}
		return buffer.toString();
	}

	@Autowired
	public void setGenericDao(MybaitsGenericDao<Long> genericDao) {
		this.genericDao = genericDao;
	}

	public int getChildrenCount(Long parent) throws LedpException {
		//List<Map> list = genericDao.executeQuery("select count(1) as ct from t_dealer where organization=" + parent);
		//查询所有经销商有无，并改正为一下代码
		List<Map> list = genericDao.executeQuery("select count(1) as ct from t_dealer where status=1010");
		int totalCount = 0;
		for (Map map : list) {
			totalCount = Integer.parseInt(map.get("ct").toString());
		}

		return totalCount;
	}
	public int getChildrenCount1(Long parent) throws LedpException {
		List<Map> list = genericDao.executeQuery("select count(1) as ct from t_dealer where status=1010 and  organization=" + parent);
		//查询所有经销商有无，并改正为一下代码
		//List<Map> list = genericDao.executeQuery("select count(1) as ct from t_dealer where status=1010");
		int totalCount = 0;
		for (Map map : list) {
			totalCount = Integer.parseInt(map.get("ct").toString());
		}

		return totalCount;
	}
	public int getTotalDealerMediaCount(Long regionId) throws LedpException {
		StringBuffer query = new StringBuffer();
		if (null != regionId && regionId>1) {
			query.append("select count(distinct o0.id) as ct from t_dealer o0 ");
			query.append("inner join t_dealer_media o1 on o0.id = o1.dealer ");
			query.append("where o0.status=1010 and o0.organization =  " + regionId);
		} else {
			query.append("select count(distinct o0.id) as ct from t_dealer o0 ");
			query.append("inner join t_dealer_media o1 on o0.id = o1.dealer ");
			query.append("where  o0.status=1010 and  1=1 ");
		}

		List<Map> list = genericDao.executeQuery(query.toString());

		int totalCount = 0;
		for (Map map : list) {
			totalCount = Integer.parseInt(map.get("ct").toString());
		}

		return totalCount;
	}

	public int getTotalDealerCount(Long regionId) throws LedpException {
		StringBuffer query = new StringBuffer();
		if (null != regionId) {
			query.append("select count(distinct o0.id) as ct from t_dealer o0 ");
			query.append("where o0.status=1010 and  o0.organization =  " + regionId);
		} else {
			query.append("select count(1) as ct from t_dealer o0 ");
			query.append("where o0.status=1010 and  1=1 ");
		}

		List<Map> list = genericDao.executeQuery(query.toString());

		int totalCount = 0;
		for (Map map : list) {
			totalCount = Integer.parseInt(map.get("ct").toString());
		}

		return totalCount;
	}
	public int getTotalLeadesCount(Long regionId) throws LedpException {
		StringBuffer query = new StringBuffer();
		if (null != regionId) {
			query.append("select count(distinct o0.id) as ct from t_leads o0 INNER JOIN t_dealer o1 ON  o1.status=1010 and  o0.ledp_dealer = o1.id");
			query.append("where o1.organization =  " + regionId);
		} else {
			query.append("select count(1) as ct from t_leads o0 ");
			query.append("where 1=1 ");
		}

		List<Map> list = genericDao.executeQuery(query.toString());

		int totalCount = 0;
		for (Map map : list) {
			totalCount = Integer.parseInt(map.get("ct").toString());
		}

		return totalCount;
	}
	
	public List<Map> queryListByOrganization(Long orgId) {
		String sql = "select id,name title from t_dealer where status=1010 ";
		if(orgId!=null){
			sql += " and organization=" + orgId;
		}
		try {
			return genericDao.executeQuery(sql);
		} catch (LedpException e) {
			logger.error("根据所属机构获取网店列表出错" + e.getMessage());
			e.printStackTrace();
		}
		return new ArrayList<Map>();
	}

}
