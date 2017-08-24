package com.citroen.ledp.service;

import com.citroen.ledp.domain.Region;
import com.citroen.ledp.exception.LedpException;

import java.util.List;
import java.util.Map;

/**
 * 地区服务类
 * @author      miaoshuai
 * @email       miaoshuai@tocersfot.com
 * @company		www.tocersoft.com
 * @create-time 2015年3月13日 下午1:58:34
 * @version     1.0
 */

public interface RegionService {

	List<Region> executeQuery(Map params) throws LedpException;

	Map<String,Object> getCondition(Map<String,Object> params);

	Region get(long id) throws LedpException;

	/**
	 * 获取省份
	 * @param id
	 * @return
	 * @throws LedpException
	 */
	List<Region> getProvinces(Long id) throws LedpException;

	/**
	 * 获取城市
	 * @param id
	 * @return
	 * @throws LedpException
	 */
	List<Region> getCitys(Long parentId) throws LedpException;


}
