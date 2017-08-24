package com.citroen.ledp.service;

import com.citroen.ledp.exception.LedpException;

import java.util.List;
import java.util.Map;

/**
 * Created by vpc on 2015/6/27.
 */
public interface VehicleParamService {
   List<Map> get(int id) throws LedpException ;
}
