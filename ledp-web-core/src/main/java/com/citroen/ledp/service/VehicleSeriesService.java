package com.citroen.ledp.service;

import com.citroen.ledp.domain.Constant;
import com.citroen.ledp.domain.Dealer;
import com.citroen.ledp.domain.VehicleSeries;
import com.citroen.ledp.exception.LedpException;

import java.util.List;
import java.util.Map;


public interface VehicleSeriesService {

	List<VehicleSeries> insert(Map<String,String> params,int pageSize,int pageNum) throws LedpException;
	/**
	 * 查询总行数
	 * @throws LedpException
	 */
	Integer getCount(Map<String,String> params) throws LedpException;

	/**
	 * 通过id查找相应的对象
	 * @param mediaId
	 * @return
	 */

	VehicleSeries getVehicleSeries(Long vehicleSeriesId);

	/**
	 * @Title: find
	 * @Description: TODO(查询车型相同名称的方法)
	 * @param model 参数传递容器
	 * @param id 机构ID
	 * @return String
	 * @throws LedpException
	 */
	VehicleSeries findN(String name) throws LedpException;

	/**
	 * @Title: find
	 * @Description: TODO(查询车型相同code的方法)
	 * @param id 机构ID
	 * @return String
	 * @throws LedpException
	 */
	VehicleSeries findC(String code) throws LedpException;


	/**
	 *
	 * ONorOFF:(删除车系). <br/>
	 *
	 */
	void deleteVS(Long id);




	/**
	 * @Title: getConditions
	 * @Description: TODO(查询车系条件方法)
	 */
	Map<String,Object> getConditions(Map<String,String> params) throws LedpException;

	/**
	 * 删除Constant对象单独提出
	 * @return
	 * @throws LedpException
	 */
	Constant  getConstant() throws LedpException;



	List<VehicleSeries> checkCode(String code, Long id) throws LedpException;

	List<VehicleSeries> checkName(String name, Long id) throws LedpException;

	void onsale(Dealer dealer,long id)throws LedpException;

	public void unsale(Dealer dealer,long id)throws LedpException;

}
