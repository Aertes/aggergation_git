package com.citroen.ledp.service;

import com.citroen.ledp.exception.LedpException;

import java.util.List;
import java.util.Map;

/**
 * Created by vpc on 2015/6/27.
 */

public interface VehicleImageService {
    List<Map> get(int id) throws LedpException ;

    List<Map> get(long id) throws LedpException;

    Map getSeriesImages(long id) throws LedpException;
}
