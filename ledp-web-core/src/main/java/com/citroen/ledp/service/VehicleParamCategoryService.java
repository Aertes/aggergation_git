package com.citroen.ledp.service;

import com.citroen.ledp.dao.mybaits.MybaitsGenericDao;
import com.citroen.ledp.domain.VehicleParam;
import com.citroen.ledp.domain.VehicleParamCategory;
import com.citroen.ledp.exception.LedpException;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Created by vpc on 2015/6/27.
 */
public interface VehicleParamCategoryService {
    /**
     * 通过车型ID获取下面的参数分类
     * @param id
     * @return
     * @throws LedpException
     */
    List<Map> get(int id) throws LedpException;
    /**
     * 通过车型id查询该车型所有配置参数
     * @param vehicleId
     * @return
     * @throws LedpException
     */
   List<VehicleParamCategory> get(long vehicleId) throws LedpException;
}
