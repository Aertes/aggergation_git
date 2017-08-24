package com.citroen.ledp.service.impl;

import com.citroen.ledp.dao.mybaits.MybaitsGenericDao;
import com.citroen.ledp.exception.LedpException;
import com.citroen.ledp.service.VehicleImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Created by vpc on 2015/6/27.
 */
@Service
@Transactional
public class VehicleImageServiceImpl implements VehicleImageService{
    @Autowired
    private MybaitsGenericDao<Long> mybaitsGenericDao;

    public List<Map> get(int id) throws LedpException {
        String sql = "SELECT * FROM t_vehicle_image WHERE vehicle_id=" +id+" ORDER BY WEIGHT";
       return mybaitsGenericDao.executeQuery(sql);
    }
    
    public List<Map> get(long id) throws LedpException {
        String sql = "SELECT * FROM t_vehicle_image WHERE vehicle_id=" +id+" ORDER BY WEIGHT";
       return mybaitsGenericDao.executeQuery(sql);
    }
    
    public Map getSeriesImages(long id) throws LedpException {
        String sql = "SELECT * FROM t_vehicle_series_image WHERE series=" +id+" ORDER BY WEIGHT";
       return mybaitsGenericDao.find(sql);
    }
}
