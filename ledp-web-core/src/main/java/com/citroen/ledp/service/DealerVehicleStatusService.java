package com.citroen.ledp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.citroen.ledp.dao.mybaits.MybaitsGenericDao;
import com.citroen.ledp.domain.Dealer;
import com.citroen.ledp.exception.LedpException;
import com.citroen.wechat.domain.DealerVehicleStatus;

/**
 * 网点车型状态服务类
 * 
 * @author 何海粟
 * @date2015年7月4日
 */

public interface DealerVehicleStatusService {


	Long save(DealerVehicleStatus entity) throws LedpException;
	
	/**
	 * 新车展厅专用
	 * @param dealer
	 * @return
	 * @throws LedpException
	 */
	public List<Map> getDealerVehicleSeries(Dealer dealer,Long seriesId) throws LedpException;
	public List<Map> getDealerVehicleSeries_bk(Dealer dealer,Long seriesId) throws LedpException;
	
	/**
	 * 新车展厅专用
	 * @param dealer
	 * @return
	 * @throws LedpException
	 */
	public List<Map> getDealerDefaultVehicles(Dealer dealer,Long seriesId,String type) throws LedpException;
	public List<Map> getDealerDefaultVehicleSeries(Dealer dealer,Long seriesId) throws LedpException;
	
	public List<Map> getDealerVehicles(Dealer dealer,Long seriesId,String type) throws LedpException;
	public Long getTotals(Dealer dealer,Long seriesId) throws LedpException;
	
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
