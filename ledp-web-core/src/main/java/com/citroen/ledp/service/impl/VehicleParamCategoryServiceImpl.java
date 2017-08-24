package com.citroen.ledp.service.impl;

import com.citroen.ledp.dao.mybaits.MybaitsGenericDao;
import com.citroen.ledp.domain.VehicleParam;
import com.citroen.ledp.domain.VehicleParamCategory;
import com.citroen.ledp.exception.LedpException;
import com.citroen.ledp.service.VehicleParamCategoryService;
import org.apache.commons.collections.CollectionUtils;
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
public class VehicleParamCategoryServiceImpl implements VehicleParamCategoryService{

    @Autowired
    private MybaitsGenericDao<Long> mybaitsGenericDao;

    /**
     * 通过车型ID获取下面的参数分类
     * @param id
     * @return
     * @throws LedpException
     */
    public List<Map> get(int id) throws LedpException {
        String sql = "SELECT * FROM t_vehicle_param_category WHERE VEHICLE_ID=" +id + " ORDER BY WEIGHT"; // 因为ID是int，不是字符串，这边应该不会有sql注入问题

        return mybaitsGenericDao.executeQuery(sql);
    }
    /**
     * 通过车型id查询该车型所有配置参数
     * @param vehicleId
     * @return
     * @throws LedpException
     */
    public List<VehicleParamCategory> get(long vehicleId) throws LedpException{
    	String sql = "select * from t_vehicle_param_category where vehicle_id=" +vehicleId+" order by weight";
    	List<VehicleParamCategory> categories = mybaitsGenericDao.executeQuery(VehicleParamCategory.class,sql);
    	if(CollectionUtils.isNotEmpty(categories)){
    		for(VehicleParamCategory category : categories){
    			//查分类下所有配置信息
    			String query = "select * from t_vehicle_param p where p.category_id = "+category.getId()+" order by p.weight";
    			List<VehicleParam> params = mybaitsGenericDao.executeQuery(VehicleParam.class, query);
    			category.setParams(params);
    		}
    	}
    	return categories;
    }
}
