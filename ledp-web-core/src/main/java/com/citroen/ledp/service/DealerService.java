/**
 * Copyright © 2013-2015 上海扬邑企业管理咨询有限公司.
 * Project Name:ledp-web-core
 * File Name:DealerService.java
 * Package Name:com.citroen.ledp.service
 * Date:2015-3-5下午1:39:42
 * Description: //模块目的、功能描述      
 * History: //修改记录
 */

package com.citroen.ledp.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.citroen.ledp.dao.mybaits.MybaitsGenericDao;
import com.citroen.ledp.domain.Constant;
import com.citroen.ledp.domain.ConstantCategory;
import com.citroen.ledp.domain.Dealer;
import com.citroen.ledp.domain.DealerMedia;
import com.citroen.ledp.domain.Media;
import com.citroen.ledp.domain.Organization;
import com.citroen.ledp.domain.User;
import com.citroen.ledp.exception.LedpException;
import org.springframework.transaction.annotation.Transactional;

/**
 * ClassName:DealerService <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2015-3-5 下午1:39:42 <br/>
 * 
 * @author 刘学斌
 * @version V1.0
 */


public interface DealerService {


	@SuppressWarnings("unchecked")
	Integer getTotal(Dealer dealer);
	/**
	 * 
	 * queryByOrganization:(根据所属机构获取网店列表). <br/>
	 * 
	 * @author 刘学斌 Date: 2015-3-6 上午9:56:19
	 * @param orgId
	 * @return
	 */
	List<Dealer> queryByOrganization(Long orgId);
	/**
	 * 获取网点信息
	 * 
	 * @param id
	 * @return
	 */
	List<Dealer> get(Long id);

	/**
	 * 获取网点信息
	 * 
	 * @param id
	 * @return
	 */
	Dealer gets(Long id);

	/**
	 * 
	 * queryByCondition:(根据条件获取网店列表). <br/>
	 * 
	 * @author 刘学斌 Date: 2015-3-6 上午10:37:42
	 * @param dealer
	 * @return
	 */
	List<Dealer> queryByCondition(Dealer dealer);
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
	List<Dealer> queryByConditionForPage(Dealer dealer, int pageSize, int pageNumber, String sortName, String sortOrder);
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
	List<Dealer> checkCode(String code, Long id) throws LedpException;
	/**
	 * 
	 * getUsers:(根据机构id获取此机构下的成员). <br/>
	 * 
	 * @author 刘学斌 Date: 2015-3-17 下午6:45:34
	 * @param orgId
	 * @return
	 * @throws LedpException
	 */
	List<User> getUsers(Long orgId) throws LedpException;
	List<Constant> getTypeList(String categoryCode);

	User getUser(Long userId);

	Constant getManager(String code);

	Organization getOrg(Long orgId);

	List<Media> getMediaList();

    void update(Dealer dealer) throws LedpException;

	Long save(Dealer dealer) throws LedpException;

	void delete(Long id);

    @Transactional
	Long saveDealerMedia(String medias, Long dealer);
	Dealer getDealerInstance(Long dealerId) throws LedpException ;

	List<DealerMedia> getDealerMedia(Long dealerId);

	Map<String, Object> getCondition(Dealer dealer, StringBuffer sql, String sortName, String sortOrder)
			throws LedpException;

	boolean hasChildren(Long parent) throws LedpException;

	List<Organization> getChildren(Long parent) throws LedpException;

	List<Dealer> getByOrganizationId(Long orgId) throws LedpException;
	List<Dealer> getByActivitOrganizationId(Long orgId) throws LedpException;
	Dealer getDealerBydealerId(long id) throws LedpException;
	List<Organization> getChildren(List<Organization> childs, List<Organization> all) throws LedpException;
	String getOrganizationIds(Long current) throws LedpException;

	@Autowired
	void setGenericDao(MybaitsGenericDao<Long> genericDao);
	int getChildrenCount(Long parent) throws LedpException;
	int getChildrenCount1(Long parent) throws LedpException;
	int getTotalDealerMediaCount(Long regionId) throws LedpException;

	int getTotalDealerCount(Long regionId) throws LedpException;
	int getTotalLeadesCount(Long regionId) throws LedpException;
	
	List<Map> queryListByOrganization(Long orgId);
	Organization getOrganization(long id);
}
