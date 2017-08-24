package com.citroen.ledp.service;

import com.citroen.ledp.domain.Constant;
import com.citroen.ledp.domain.Dealer;
import com.citroen.ledp.domain.Vehicle;
import com.citroen.ledp.domain.VehicleSeries;
import com.citroen.ledp.exception.LedpException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface VehicleService {

	/**
	 * @Title: insert
	 * @Description: TODO(查询车型的方法)
	 * @param model 参数传递容器
	 * @param id 机构ID
	 * @return String
	 * @throws LedpException
	 */
	List<Vehicle> insert(Map<String,String> params,int pageSize,int pageNum) throws LedpException;

	/**
	 * @Title: getVehicle
	 * @Description: TODO( 查询总行数)
	 * @throws LedpException
	 */
	Integer getCount(Map<String,String> params) throws LedpException;

	/**
	 * @Title: getVehicle
	 * @Description: TODO(通过id查找相应的对象)
	 * @param mediaId
	 * @return
	 */
	Vehicle getVehicle(Long dealerId,Long vehicleId);
	Vehicle getVehicle(Long vehicleId);
	/**
	 * @Title: find
	 * @Description: TODO(查询车型相同名称的方法)
	 * @param model 参数传递容器
	 * @param id 机构ID
	 * @return String
	 * @throws LedpException
	 */
	Vehicle findN(String name,Long id) throws LedpException;

	/**
	 * @Title: find
	 * @Description: TODO(查询车型相同代码的方法)
	 * @param model 参数传递容器
	 * @param id 机构ID
	 * @return String
	 * @throws LedpException
	 */
	Vehicle findC(String code,Long id) throws LedpException;


	VehicleSeries get(long id) throws LedpException;



	/**
	 * @Title: getConditions
	 * @Description: TODO(查询车系条件方法)
	 */
	Map<String,Object> getConditions(Map<String,String> params) throws LedpException;

	/**
	 *
	 * ONorOFF:(启用/禁用车型). <br/>
	 *
	 */
	void ONorOFF(Long id);

	/**
	 *
	 * ONorOFF:(删除车型). <br/>
	 *
	 */
	void deleteVS(Long id);



	/**
	 * 查询指定对象
	 * @param parent
	 * @return
	 * @throws LedpException
	 */
	List<VehicleSeries> getChildren(Long parent) throws LedpException;
	/**
	 * 返回没有禁用对象
	 * @throws LedpException
	 *
	 */
	VehicleSeries getVehicleSeries() throws LedpException;

	List<Vehicle> checkCode(String code, Long id ,Long seriesId) throws LedpException;

	List<Vehicle> checkName(String name, Long id, Long seriesId) throws LedpException;


	/**
	 * 删除Constant对象单独提出
	 * @return
	 * @throws LedpException
	 */
	Constant  getConstant() throws LedpException;

	/**
	 * 禁用Constant对象单独提出
	 * @return
	 * @throws LedpException
	 */
	Constant  getConstantIn() throws LedpException;



	Vehicle getSeries() throws LedpException;


	boolean hasChildren(Long parent) throws LedpException;

	void onsale(Dealer dealer,long id)throws LedpException;

	void unsale(Dealer dealer,long id)throws LedpException;

	void updatePricer(Dealer dealer,Long vehicleId,String onsale,String price) throws LedpException;


}
