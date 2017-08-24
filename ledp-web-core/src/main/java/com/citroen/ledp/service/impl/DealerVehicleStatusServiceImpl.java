package com.citroen.ledp.service.impl;

import com.citroen.ledp.dao.mybaits.MybaitsGenericDao;
import com.citroen.ledp.domain.Dealer;
import com.citroen.ledp.exception.LedpException;
import com.citroen.ledp.service.DealerVehicleStatusService;
import com.citroen.wechat.domain.DealerVehicleStatus;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 网点车型状态服务类
 * 
 * @author 何海粟
 * @date2015年7月4日
 */
@Service
public class DealerVehicleStatusServiceImpl implements DealerVehicleStatusService{
	@Autowired
	private MybaitsGenericDao<Long> mybaitsGenericDao;

	public Long save(DealerVehicleStatus entity) throws LedpException{
		return mybaitsGenericDao.save(entity);
	}
	
	/**
	 * 新车展厅专用
	 * @param dealer
	 * @return
	 * @throws LedpException
	 */
	public List<Map> getDealerVehicleSeries(Dealer dealer,Long seriesId) throws LedpException{
		List<Map> result = new ArrayList<Map>();
		StringBuilder query = new StringBuilder();
		query.append("select vs.series,se.name as name,");
		query.append("(select max(vs2.price) from  t_dealer_vehicle_status vs2 where vs2.status=1 and vs2.dealer = vs.dealer and vs2.series=vs.series) as maxmoney,");
		query.append("(select min(vs2.price) from t_dealer_vehicle_status vs2 where vs2.status=1 and vs2.dealer = vs.dealer and vs2.series=vs.series ) as minmoney,");
		query.append("(select count(vs2.id) from t_dealer_vehicle_status vs2 where vs2.status=1 and vs2.series=vs.series and vs2.dealer = vs.dealer and vs2.vehicle is not null) as ct  ");
		query.append(" from t_dealer_vehicle_status vs ");
		query.append(" inner join t_vehicle_series se on se.id= vs.series and se.status=1010 ");
		query.append(" where vs.status=1 ");
		if(dealer != null){
			query.append(" and vs.dealer="+dealer.getId());
		}
		if(seriesId != null && seriesId > 0){
			query.append(" and vs.series="+seriesId);
		}
		query.append(" GROUP BY vs.series");
		List<Map> maps = mybaitsGenericDao.executeQuery(query.toString());
		//排除只有车第没有车型的数据
		if(CollectionUtils.isNotEmpty(maps)){
			for(int i=0; i<maps.size(); i++){
				Map map = maps.get(i);
				long count = 0L;
				try {
					count = Long.valueOf(map.get("ct").toString());
				} catch (Exception e) {}
				if(map != null && count > 0){
					result.add(map);
				}
			}
		}
		return result;
	}
	public List<Map> getDealerVehicleSeries_bk(Dealer dealer,Long seriesId) throws LedpException{
		List<Map> result = new ArrayList<Map>();
		StringBuilder query = new StringBuilder();
		query.append("select vs.series,vs.vehicle,se.name as name,max(vs.price) as maxmoney, ");
		query.append(" min(vs.price) as minmoney,count(vs.vehicle) as ct  ");
		query.append(" from t_dealer_vehicle_status vs ");
		query.append(" left join t_vehicle_series se on se.id= vs.series and se.status=1010 ");
		query.append(" left join t_vehicle v on v.series = se.id and v.id=vs.vehicle and v.status=1010 ");
		query.append(" where vs.status=1 ");
		if(dealer != null){
			query.append(" and vs.dealer="+dealer.getId());
		}
		if(seriesId != null && seriesId > 0){
			query.append(" and vs.series="+seriesId);
		}
		List<Map> maps = mybaitsGenericDao.executeQuery(query.toString());
		//排除只有车第没有车型的数据
		if(CollectionUtils.isNotEmpty(maps)){
			for(int i=0; i<maps.size(); i++){
				Map map = maps.get(i);
				long count = 0L;
				try {
					count = Long.valueOf(map.get("ct").toString());
				} catch (Exception e) {}
				if(map != null && count > 0){
					result.add(map);
				}
			}
		}
		return result;
	}
	
	/**
	 * 新车展厅专用
	 * @param dealer
	 * @return
	 * @throws LedpException
	 */
	public List<Map> getDealerDefaultVehicles(Dealer dealer,Long seriesId,String type) throws LedpException{
		StringBuilder query = new StringBuilder();
		query.append(" select v.id as vehicle,v.name as name,v.price as guidePrice ");
		query.append(" from t_vehicle v  ");
		query.append(" where v.status=1010 ");
		if(seriesId != null && seriesId > 0){
			query.append(" and v.series="+seriesId);
		}
		if(StringUtils.isNotBlank(type)){
			query.append(" and v.name like '%"+type+"%' ");
		}
		return mybaitsGenericDao.executeQuery(query.toString());
	}
	
	public List<Map> getDealerDefaultVehicleSeries(Dealer dealer,Long seriesId) throws LedpException{
		List<Map> result = new ArrayList<Map>();
		StringBuilder query = new StringBuilder();
		query.append("select vs.id as series,vs.name as name,");
		query.append("(select max(vs2.price) from  t_vehicle vs2 where vs2.status=1010  and vs2.series=vs.id) as maxmoney,");
		query.append("(select min(vs2.price) from t_vehicle vs2 where vs2.status=1010  and vs2.series=vs.id ) as minmoney,");
		query.append("(select count(vs2.id) from t_vehicle vs2 where vs2.status=1010  and vs2.series=vs.id ) as ct  ");
		query.append(" from t_vehicle_series vs ");
		query.append(" where vs.status=1010 ");
		if(seriesId != null && seriesId > 0){
			query.append(" and vs.id="+seriesId);
		}
		List<Map> maps = mybaitsGenericDao.executeQuery(query.toString());
		//排除只有车第没有车型的数据
		if(CollectionUtils.isNotEmpty(maps)){
			for(int i=0; i<maps.size(); i++){
				Map map = maps.get(i);
				long count = 0L;
				try {
					count = Long.valueOf(map.get("ct").toString());
				} catch (Exception e) {}
				if(map != null && count > 0){
					result.add(map);
				}
			}
		}
		return result;
	}
	
	public List<Map> getDealerVehicles(Dealer dealer,Long seriesId,String type) throws LedpException{
		StringBuilder query = new StringBuilder();
		query.append(" select vs.vehicle,v.name as name,vs.price as guidePrice ");
		query.append(" from t_dealer_vehicle_status vs ");
		query.append(" inner join t_vehicle v on v.id=vs.vehicle and v.status=1010 ");
		query.append(" where vs.status=1 ");
		if(dealer != null){
			query.append(" and vs.dealer="+dealer.getId());
		}
		if(seriesId != null && seriesId > 0){
			query.append(" and v.series="+seriesId);
		}
		if(StringUtils.isNotBlank(type)){
			query.append(" and v.name like '%"+type+"%' ");
		}
		query.append(" order by v.name ");
		return mybaitsGenericDao.executeQuery(query.toString());
	}
	
	public Long getTotals(Dealer dealer,Long seriesId) throws LedpException{
		StringBuilder query = new StringBuilder();
		query.append("select count(DISTINCT vs.series) as ct from t_dealer_vehicle_status vs where vs.status=1 ");
		if(dealer != null){
			query.append(" and vs.dealer="+dealer.getId());
		}
		if(seriesId != null && seriesId > 0){
			query.append(" and vs.series="+seriesId);
		}
		Map map = mybaitsGenericDao.find(query.toString());
		if(map.isEmpty()){
			return 0l;
		}
		return (Long) map.get("ct");
	}
	
	/*public Map getDealerVehicleSeries(Dealer dealer) throws LedpException{
		StringBuilder query = new StringBuilder();
		query.append("select vs.series,count(vs.vehicle) ct from t_dealer_vehicle_status vs where 1=1 ");
		if(dealer != null){
			query.append(" and dealer="+dealer.getId());
		}
		query.append(" and status=1 group by series ");
		return mybaitsGenericDao.find(query.toString());
	}*/
}
