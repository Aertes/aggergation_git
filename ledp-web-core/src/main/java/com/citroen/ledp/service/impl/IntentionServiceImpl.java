package com.citroen.ledp.service.impl;

import com.citroen.ledp.dao.mybaits.MybaitsGenericDao;
import com.citroen.ledp.domain.VehicleSeriesIntention;
import com.citroen.ledp.exception.LedpException;
import com.citroen.ledp.service.IntentionService;
import com.citroen.ledp.util.MapUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by maskx on 2017/5/22.
 */
@Service
public class IntentionServiceImpl implements IntentionService{

    @Autowired
    private MybaitsGenericDao<Long> genericDao;

    public List<VehicleSeriesIntention> executeQuery(Map<String, Object> params) throws LedpException {
        Map<String,Object> condition = getCondition(params);
        Map namedParams = (Map)condition.get("namedParams");
        String sql = "select * from t_vehicle_series_intention where 1 = 1 " + condition.get("namedSql");

        Map paginateParams = new HashMap();
        int pageNumber= (Integer) params.get("pageNumber") != null?(Integer) params.get("pageNumber"):1;
        int pageSize=(Integer) params.get("pageSize") != null? (Integer) params.get("pageSize"):10;
        int offset = (pageNumber - 1) * pageSize;
        paginateParams.put("max",pageSize);
        paginateParams.put("offset",offset);

        return genericDao.executeQuery(VehicleSeriesIntention.class, sql, namedParams, paginateParams);
    }

    public int getTotalRow(Map<String, Object> params) throws LedpException {
        Map<String,Object> condition = getCondition(params);
        Map namedParams = (Map)condition.get("namedParams");
        String sql = "select count(1) count from t_vehicle_series_intention where 1 = 1 " + condition.get("namedSql");

        List<Map> list = genericDao.executeQuery(sql,namedParams);
        if(list.isEmpty()){
            return 0;
        }
        Map map = list.get(0);
        return map.isEmpty()?0:Integer.parseInt(map.get("count").toString());
    }

    public Map<String,Object> getCondition(Map<String,Object> params){
        MapUtil<String,Object> mapUtil = new MapUtil<String,Object>(params);
        Map<String,Object> namedParams = new HashMap<String,Object>();

        StringBuilder namedSql = new StringBuilder();

        if(!mapUtil.isBlank("vehicleSeriesName")){
            String vehicleSeriesName = mapUtil.get("vehicleSeriesName");
            namedParams.put("vehicleSeriesName","%"+vehicleSeriesName+"%");
            namedSql.append(" and vehicle_series_name like :vehicleSeriesName");
        }
        if(!mapUtil.isBlank("vehicleSeriesCode")){
            String vehicleSeriesCode = params.get("vehicleSeriesCode").toString();
            namedParams.put("vehicleSeriesCode",vehicleSeriesCode);
            namedSql.append(" and vehicle_series_code =:vehicleSeriesCode");
        }

        Map<String,Object> rs = new HashMap<String,Object>();
        rs.put("namedSql",namedSql);
        rs.put("namedParams",namedParams);
        return rs;
    }

    public VehicleSeriesIntention find(String code) throws LedpException {
        return genericDao.find(VehicleSeriesIntention.class, "select * from t_vehicle_series_intention where vehicle_series_code = '" + code + "'");
    }

    public void save(VehicleSeriesIntention intention) throws LedpException {
        genericDao.save(intention);
    }

    public void delete(long id) throws LedpException {
        genericDao.delete(VehicleSeriesIntention.class, id);
    }

    public int count() throws LedpException {
        Map map = genericDao.find("select count(1) count from t_vehicle_series_intention");
        return Integer.parseInt(map.get("count").toString());
    }
}
