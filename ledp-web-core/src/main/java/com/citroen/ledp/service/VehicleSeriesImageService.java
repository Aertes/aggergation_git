package com.citroen.ledp.service;

import com.citroen.ledp.exception.LedpException;

import java.util.Map;

/**
 * 车系图片服务
 *
 * @author 何海粟
 * @date2015年7月5日
 */

public interface VehicleSeriesImageService {
	Map getBySeries(long seriesId) throws LedpException;
}
