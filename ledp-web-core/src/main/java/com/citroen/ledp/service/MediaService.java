package com.citroen.ledp.service;

import com.citroen.ledp.domain.Media;
import com.citroen.ledp.exception.LedpException;

import java.util.List;
import java.util.Map;


public interface MediaService {

	List<Media> insert(Map<String, String> params, int pageSize,
					   int pageNum) throws LedpException ;

	/**
	 * 查询总行数
	 *
	 * @throws LedpException
	 */
	Integer getCount(Map<String, String> params) throws LedpException ;

	/**
	 * 通过id查找相应的对象
	 *
	 * @param mediaId
	 * @return
	 */

	Media getMedia(Long mediaId) ;

	/**
	 * @Title: getConditions
	 * @Description: TODO(查询车系条件方法)
	 */
	Map<String, Object> getConditions(Map<String, String> params)
			throws LedpException;

	/**
	 * 查询所有媒体
	 * @return
	 * @throws LedpException
	 */
	List<Media> listAll() throws LedpException;

	/**
	 *
	 * ONorOFF:(启用/禁用车型). <br/>
	 *
	 */
	void ONorOFF(Long id) ;

	/**
	 * 获取媒体名称
	 * @param mediaIds
	 * @return
	 */
	String getMediaNamesByMediaIds(Long[] mediaIds);
}
