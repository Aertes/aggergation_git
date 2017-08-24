package com.citroen.ledp.service.impl;

import com.citroen.ledp.dao.mybaits.MybaitsGenericDao;
import com.citroen.ledp.domain.Constant;
import com.citroen.ledp.domain.Dealer;
import com.citroen.ledp.domain.VehicleSeries;
import com.citroen.ledp.exception.LedpException;
import com.citroen.ledp.service.VehicleSeriesService;
import com.citroen.ledp.service.VehicleService;
import com.citroen.wechat.domain.DealerVehicleStatus;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class VehicleSeriesServiceImpl implements VehicleSeriesService{
	@Autowired
	private MybaitsGenericDao<Long> mybaitsGenericDao;

	public List<VehicleSeries> insert(Map<String,String> params,int pageSize,int pageNum) throws LedpException{
		String onsale = "";
		if(params.containsKey("dealerId")){
			onsale = ",(case when (select count(id) from t_dealer_vehicle_status where status=1 and dealer="+params.get("dealerId")+" and series=v.id)>0 then '上架' else '下架' end) onsale";
		}

		Map<String,Object> namedParams =  getConditions(params);

		Map<String,Object> paginateParams = new HashMap<String,Object>();
		paginateParams.put("max", pageSize);

		paginateParams.put("offset", pageNum);

		String sql = " select v.id,v.name,v.status,v.code "+onsale+" from t_vehicle_series v where 1=1  "+namedParams.get("sql").toString();
		Map<String,Object> named = (Map<String,Object>)namedParams.get("nameParam");

		List<VehicleSeries> vehicleList = mybaitsGenericDao.executeQuery(VehicleSeries.class, sql,named, paginateParams);
		return vehicleList;
	}
	/**
	 * 查询总行数
	 * @throws LedpException
	 */
	public Integer getCount(Map<String,String> params) throws LedpException{
		Map<String,Object> namedParams = getConditions(params);
		Integer total = 0;
		String query = " select count(id) from t_vehicle_series where 1=1 "+namedParams.get("sql").toString();

		Long count = (Long) mybaitsGenericDao.executeQuery(query,(Map<String,Object>)namedParams.get("nameParam")).get(0).get("count(id)");

		return Integer.valueOf(count.toString());
	}


	/**
	 * 通过id查找相应的对象
	 * @param mediaId
	 * @return
	 */

	public VehicleSeries getVehicleSeries(Long vehicleSeriesId){
		try {
			return mybaitsGenericDao.get(VehicleSeries.class,vehicleSeriesId);
		} catch (LedpException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @Title: find
	 * @Description: TODO(查询车型相同名称的方法)
	 * @param model 参数传递容器
	 * @param id 机构ID
	 * @return String
	 * @throws LedpException
	 */
	public VehicleSeries findN(String name) throws LedpException{
		String query = "select * from t_vehicle_series where name='"+name+"'";
		return mybaitsGenericDao.find(VehicleSeries.class,query);
	}

	/**
	 * @Title: find
	 * @Description: TODO(查询车型相同code的方法)
	 * @param id 机构ID
	 * @return String
	 * @throws LedpException
	 */
	public VehicleSeries findC(String code) throws LedpException{
		String query = "select * from t_vehicle_series where code='"+code+"'";
		return mybaitsGenericDao.find(VehicleSeries.class,query);
	}


	/**
	 *
	 * ONorOFF:(删除车系). <br/>
	 *
	 */
	public void deleteVS(Long id) {
		try {
			VehicleSeries vehicleSeries = mybaitsGenericDao.get(VehicleSeries.class, id);
			if (vehicleSeries != null && vehicleSeries.getStatus() != null) {

				Constant constant = getConstant();
				vehicleSeries.setStatus(constant);
				mybaitsGenericDao.update(vehicleSeries);
			}
		} catch (LedpException e) {
			e.printStackTrace();
		}
	}




	/**
	 * @Title: getConditions
	 * @Description: TODO(查询车系条件方法)
	 */
	public  Map<String,Object> getConditions(Map<String,String> params) throws LedpException{
		Map<String,Object>   map = new HashMap<String, Object>();
		Map<String,Object> nameParam = new HashMap<String, Object>();

		StringBuilder sql = new StringBuilder();

		if(StringUtils.isNotBlank(params.get("name"))){
			sql.append(" and name like :name ");
			nameParam.put("name","%"+params.get("name")+"%");
		}
		if(StringUtils.isNotBlank(params.get("code"))){
			sql.append(" and code like :code ");
			nameParam.put("code","%"+params.get("code")+"%" );
		}
		if(StringUtils.isNotBlank(params.get("status"))){
			String query = "select * from t_constant where code='"+ params.get("status") +"'";
			Constant c = mybaitsGenericDao.find(Constant.class, query);
			if(c != null){
				sql.append(" and status=" + c.getId());
			}
		}

		Constant constant = getConstant();
		if(constant != null){
			sql.append("  and status !=" + constant.getId());
		}

		sql.append(" ORDER BY  id DESC  ");

		map.put("sql", sql);
		map.put("nameParam", nameParam);

		return map;
	}

	/**
	 * 删除Constant对象单独提出
	 * @return
	 * @throws LedpException
	 */
	public Constant  getConstant() throws LedpException{
		String query = "select * from t_constant where code='delete'";
		Constant constant = mybaitsGenericDao.find(Constant.class, query);
		return constant;
	}



	public List<VehicleSeries> checkCode(String code, Long id) throws LedpException {
		if (id != null) {
			return mybaitsGenericDao.executeQuery(VehicleSeries.class, " select * from t_vehicle_series where 1=1 and code='" + code + "'"
					+ " and id != " + id);
		}
		return mybaitsGenericDao.executeQuery(VehicleSeries.class, " select * from t_vehicle_series where 1=1 and code='" + code + "'");
	}

	public List<VehicleSeries> checkName(String name, Long id) throws LedpException {
		if (id != null) {
			return mybaitsGenericDao.executeQuery(VehicleSeries.class, " select * from t_vehicle_series where 1=1 and name='" + name + "'"
					+ " and id != " + id);
		}
		return mybaitsGenericDao.executeQuery(VehicleSeries.class, " select * from t_vehicle_series where 1=1 and name='" + name + "'");
	}

	public void onsale(Dealer dealer,long id)throws LedpException {
		DealerVehicleStatus dealerVehicleStatus = mybaitsGenericDao.find(DealerVehicleStatus.class,"select * from t_dealer_vehicle_status where dealer="+dealer.getId()+" and series="+id);
		if(dealerVehicleStatus==null){
			dealerVehicleStatus = new DealerVehicleStatus();
			dealerVehicleStatus.setDealer(dealer);
			dealerVehicleStatus.setSeries(new VehicleSeries(id));
			dealerVehicleStatus.setStatus(1);
			mybaitsGenericDao.save(dealerVehicleStatus);
		}else{
			dealerVehicleStatus.setStatus(1);
			mybaitsGenericDao.update(dealerVehicleStatus);
		}
	}

	public void unsale(Dealer dealer,long id)throws LedpException {
		mybaitsGenericDao.execute("update t_dealer_vehicle_status set status=0 where dealer="+dealer.getId()+" and series="+id);
	}

}
