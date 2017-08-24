package com.citroen.ledp.service.impl;

import com.citroen.ledp.dao.mybaits.MybaitsGenericDao;
import com.citroen.ledp.domain.Constant;
import com.citroen.ledp.domain.Dealer;
import com.citroen.ledp.domain.Vehicle;
import com.citroen.ledp.domain.VehicleSeries;
import com.citroen.ledp.exception.LedpException;
import com.citroen.ledp.service.ConstantService;
import com.citroen.ledp.service.VehicleService;
import com.citroen.wechat.domain.DealerVehicleStatus;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class VehicleServiceImpl implements VehicleService{
    private Log logger = LogFactory.getLog(VehicleServiceImpl.class);
	
	@Autowired
	private MybaitsGenericDao<Long> mybaitsGenericDao;
	@Autowired
	private ConstantService constantService;
	/**
	 * @Title: insert
	 * @Description: TODO(查询车型的方法)
	 * @param model 参数传递容器
	 * @param id 机构ID
	 * @return String
	 * @throws LedpException 
	 */
	public List<Vehicle> insert(Map<String,String> params,int pageSize,int pageNum) throws LedpException{
		String onsale = "";
		//String price = "";
		if(params.containsKey("dealerId")){
			onsale = ",(case when (select count(id) from t_dealer_vehicle_status where status=1 and dealer="+params.get("dealerId")+" and vehicle=v.id)>0 then '上架' else '下架' end) onsale";
			//price  = ",(select price from t_dealer_vehicle_status where dealer="+params.get("dealerId")+" and vehicle=v.id limit 1) price";
		}
		
		Map<String,Object> namedParams =  getConditions(params);
		 
		Map<String,Object> paginateParams = new HashMap<String,Object>();
		 
		paginateParams.put("max", pageSize);
		
		paginateParams.put("offset",(pageNum-1)*pageSize);
		
		String sql = " select v.id,v.series,v.name,v.code,v.status "+onsale+" from t_vehicle v where 1=1 "+namedParams.get("sql").toString();
		 
		Map<String,Object> named = (Map<String,Object>)namedParams.get("nameParam");
		 
		List<Vehicle> vehicle = mybaitsGenericDao.executeQuery(Vehicle.class, sql,named, paginateParams);
			
		return vehicle;
	}
	
	/**
	 * @Title: getVehicle
	 * @Description: TODO( 查询总行数)
	 * @throws LedpException 
	 */
	public Integer getCount(Map<String,String> params) throws LedpException{
		Map<String,Object> namedParams = getConditions(params);
		Integer total = 0;
		 String query = " select count(v.id) count from t_vehicle v where 1=1 "+namedParams.get("sql").toString();
		 
		 Long count = (Long) mybaitsGenericDao.executeQuery(query,(Map<String,Object>)namedParams.get("nameParam")).get(0).get("count");
		 
		 return Integer.valueOf(count.toString());
	}
	
	/**
	 * @Title: getVehicle
	 * @Description: TODO(通过id查找相应的对象)
	 * @param mediaId
	 * @return
	 */
	public Vehicle getVehicle(Long dealerId,Long vehicleId){
		try {
			Vehicle vehicle = mybaitsGenericDao.get(Vehicle.class,vehicleId);
			if(dealerId!=null){
				String sql = "select price,status from t_dealer_vehicle_status where dealer="+dealerId+" and vehicle="+vehicleId;
				Map row = mybaitsGenericDao.find(sql);
				vehicle.setOnsale("0");
				if(row!=null){
					if(row.containsKey("price")){
						vehicle.setPrice(row.get("price").toString());
					}
					if(row.containsKey("status")){
						vehicle.setOnsale(row.get("status").toString());
					}
				}
			}
			return vehicle;
		} catch (LedpException e) {
            logger.error("异常信息：" + e.getMessage());
		}
		return null;
	}
	public Vehicle getVehicle(Long vehicleId){
		try {
			Vehicle vehicle = mybaitsGenericDao.get(Vehicle.class,vehicleId);
			return vehicle;
		} catch (LedpException e) {
            logger.error("异常信息：" + e.getMessage());
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
	public Vehicle findN(String name,Long id) throws LedpException{
		String query = "select * from t_vehicle where series ="+id+" and  name='"+name+"'";
		return mybaitsGenericDao.find(Vehicle.class,query);
	}
	
	/**
	 * @Title: find
	 * @Description: TODO(查询车型相同代码的方法)
	 * @param model 参数传递容器
	 * @param id 机构ID
	 * @return String
	 * @throws LedpException 
	 */
	public Vehicle findC(String code,Long id) throws LedpException{
		String query = "select * from t_vehicle where series ="+id+" and  code='"+code+"'";
		return mybaitsGenericDao.find(Vehicle.class,query);
	}
	
	
	public VehicleSeries get(long id) throws LedpException{
		return mybaitsGenericDao.get(VehicleSeries.class, id);
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
				sql.append(" and v.name like :name ");
				nameParam.put("name","%"+params.get("name")+"%");
			}
			if(StringUtils.isNotBlank(params.get("code"))){
				sql.append(" and v.code like :code ");
				nameParam.put("code","%"+params.get("code")+"%" );
			}
			if(StringUtils.isNotBlank(params.get("status"))){
				String query = "select * from t_constant where code='"+ params.get("status") +"'";
				Constant c = mybaitsGenericDao.find(Constant.class, query);
				if(c != null){
					sql.append(" and v.status=" + c.getId());
				}
			}
			
			if(StringUtils.isNotBlank(params.get("series"))){
				String query = "select * from t_vehicle_series where id='"+ params.get("series") +"'";
				Constant c = mybaitsGenericDao.find(Constant.class, query);
				if(c != null){
					sql.append(" and v.series =" + c.getId());
				}
			}else{
				sql.append(" and v.series = 0" );
			}
			
			Constant constant = getConstant();
			if(constant != null){
				sql.append("  and v.status !=" + constant.getId());
			}
			
			sql.append(" ORDER BY  id DESC  ");
			
			map.put("sql", sql);
			map.put("nameParam", nameParam);
			
			return map;
	  }
	  
	  /**
		 * 
		 * ONorOFF:(启用/禁用车型). <br/>
		 * 
		 */
		public void ONorOFF(Long id) {
			try {
				Vehicle vehicles = mybaitsGenericDao.get(Vehicle.class, id);
				if (vehicles != null && vehicles.getStatus() != null) {
					String sql = "select * from t_constant where code='"
							+ (vehicles.getStatus().getCode().equals("active") ? "inactive" : "active") + "'";
					Constant constant = mybaitsGenericDao.find(Constant.class, sql);
					vehicles.setStatus(constant);
					mybaitsGenericDao.update(vehicles);
				}
			} catch (LedpException e) {
                logger.error("异常信息：" + e.getMessage());
			}
		}
		
		/**
		 * 
		 * ONorOFF:(删除车型). <br/>
		 * 
		 */
		public void deleteVS(Long id) {
			try {
				Vehicle vehicles = mybaitsGenericDao.get(Vehicle.class, id);
				if (vehicles != null && vehicles.getStatus() != null) {
					
					Constant constant = getConstant();
					vehicles.setStatus(constant);
					mybaitsGenericDao.update(vehicles);
				}
			} catch (LedpException e) {
                logger.error("异常信息：" + e.getMessage());
			}
		}
		
		
	
	/**
	 * 查询指定对象
	 * @param parent
	 * @return
	 * @throws LedpException
	 */
	public List<VehicleSeries> getChildren(Long parent) throws LedpException{
		Constant  constantI =   getConstantIn();
		return mybaitsGenericDao.executeQuery(VehicleSeries.class,"select * from t_vehicle_series  where status != '"+constantI.getId()+"'   order by id desc");
	}
	/**
	 * 返回没有禁用对象
	 * @throws LedpException 
	 * 
	 */
	public VehicleSeries getVehicleSeries() throws LedpException{
		Constant  constantI =   getConstantIn();
		return mybaitsGenericDao.find(VehicleSeries.class,"select * from t_vehicle_series  where status != '"+constantI.getId()+"'   order by id desc");
	}
	
	public List<Vehicle> checkCode(String code, Long id ,Long seriesId) throws LedpException {
		if (id != null) {
			return mybaitsGenericDao.executeQuery(Vehicle.class, " select * from t_vehicle where 1=1 and code='" + code + "'"
					+ " and id != " + id +" and series="+seriesId);
		}
		return mybaitsGenericDao.executeQuery(Vehicle.class, " select * from t_vehicle where 1=1 and code='" + code + "' and series="+seriesId);
	}
	
	public List<Vehicle> checkName(String name, Long id, Long seriesId) throws LedpException {
		if (id != null) {
			return mybaitsGenericDao.executeQuery(Vehicle.class, " select * from t_vehicle where 1=1 and name='" + name + "'"
					+ " and id != " + id +" and series="+seriesId);
		}
		return mybaitsGenericDao.executeQuery(Vehicle.class, " select * from t_vehicle where 1=1 and name='" + name + "' and series="+seriesId);
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
	  
	  /**
	   * 禁用Constant对象单独提出
	   * @return
	   * @throws LedpException
	   */
	  public Constant  getConstantIn() throws LedpException{
		  String query = "select * from t_constant where code='inactive'";
			Constant constant = mybaitsGenericDao.find(Constant.class, query);
		  return constant;
	  }
	  
	  
	  
		public Vehicle getSeries() throws LedpException{
			String query = "select * from t_vehicle where series is null";
			return mybaitsGenericDao.find(Vehicle.class,query);
		}

	

	public boolean hasChildren(Long parent) throws LedpException{
		String query = "select count(id) count from t_vehicle_series";
		Map<?,?> row = mybaitsGenericDao.find(query);
		Object count = row.get("count");
		return (count==null)?false:Integer.parseInt(count.toString())>0;
	}
	
	public void onsale(Dealer dealer,long id)throws LedpException {
		DealerVehicleStatus dealerVehicleStatus = mybaitsGenericDao.find(DealerVehicleStatus.class,"select * from t_dealer_vehicle_status where dealer="+dealer.getId()+" and vehicle="+id);
		if(dealerVehicleStatus==null){
			Vehicle vehicle =mybaitsGenericDao.get(Vehicle.class, id);
			
			dealerVehicleStatus = new DealerVehicleStatus();
			dealerVehicleStatus.setDealer(dealer);
			dealerVehicleStatus.setVehicle(vehicle);
			dealerVehicleStatus.setPrice(vehicle.getPrice());
			dealerVehicleStatus.setStatus(1);
			dealerVehicleStatus.setSeries(vehicle.getSeries());
			mybaitsGenericDao.save(dealerVehicleStatus);
		}else{
			dealerVehicleStatus.setStatus(1);
			mybaitsGenericDao.update(dealerVehicleStatus);
		}
	}

	public void unsale(Dealer dealer,long id)throws LedpException {
		mybaitsGenericDao.execute("update t_dealer_vehicle_status set status=0 where dealer="+dealer.getId()+" and vehicle="+id);
	}
	
	public void updatePricer(Dealer dealer,Long vehicleId,String onsale,String price) throws LedpException {
		DealerVehicleStatus dealerVehicleStatus = mybaitsGenericDao.find(DealerVehicleStatus.class,"select * from t_dealer_vehicle_status where dealer="+dealer.getId()+" and vehicle="+vehicleId);
		if(dealerVehicleStatus==null){
			Vehicle vehicle =mybaitsGenericDao.get(Vehicle.class,vehicleId);
			dealerVehicleStatus = new DealerVehicleStatus();
			dealerVehicleStatus.setDealer(dealer);
			dealerVehicleStatus.setVehicle(vehicle);
			dealerVehicleStatus.setPrice(vehicle.getPrice());
			dealerVehicleStatus.setStatus("1".equals(onsale)?1:0);
			mybaitsGenericDao.save(dealerVehicleStatus);
		}else{
			dealerVehicleStatus.setPrice(price);
			dealerVehicleStatus.setStatus("1".equals(onsale)?1:0);
			mybaitsGenericDao.update(dealerVehicleStatus);
		}
	}
	
	
}
