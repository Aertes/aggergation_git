package com.citroen.ledp.service.impl;

import com.citroen.ledp.dao.mybaits.MybaitsGenericDao;
import com.citroen.ledp.exception.LedpException;
import com.citroen.ledp.service.VehicleSeriesImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 车系图片服务
 *
 * @author 何海粟
 * @date2015年7月5日
 */
@Service
public class VehicleSeriesImageServiceImpl implements VehicleSeriesImageService{
	@Autowired
	private MybaitsGenericDao<Long> mybaitsGenericDao;

	public Map getBySeries(long seriesId) throws LedpException{
		return mybaitsGenericDao.find("select webpath as webpath from t_vehicle_series_image si where si.status=1010 and series="+seriesId);
	}
}
