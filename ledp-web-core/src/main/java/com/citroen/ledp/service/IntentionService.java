package com.citroen.ledp.service;

import com.citroen.ledp.dao.mybaits.MybaitsGenericDao;
import com.citroen.ledp.domain.VehicleSeriesIntention;
import com.citroen.ledp.exception.LedpException;
import com.citroen.ledp.util.MapUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by maskx on 2017/5/22.
 */

public interface IntentionService {

    List<VehicleSeriesIntention> executeQuery(Map<String, Object> params) throws LedpException;
    int getTotalRow(Map<String, Object> params) throws LedpException ;

    Map<String,Object> getCondition(Map<String,Object> params);
    VehicleSeriesIntention find(String code) throws LedpException ;

    void save(VehicleSeriesIntention intention) throws LedpException;
    void delete(long id) throws LedpException ;

    int count() throws LedpException ;
}
