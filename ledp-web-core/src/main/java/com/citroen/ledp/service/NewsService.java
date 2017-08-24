package com.citroen.ledp.service;

import com.citroen.ledp.domain.Media;
import com.citroen.ledp.domain.News;
import com.citroen.ledp.domain.NewsMedia;
import com.citroen.ledp.exception.LedpException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @Title: NewsService.java
 * @Package com.citroen.ledp.service
 * @Description: TODO(用一句话描述该文件做什么)
 * @author 廖启洪
 * @date 2015年3月18日 下午11:21:18
 * @version V1.0
 */
public interface NewsService {


	List<Map<String,String>> executeMapQuery(Map params) throws LedpException;

	List<News> executeQuery(Map params) throws LedpException;

	int getTotalRow(Map params) throws LedpException;

	Map<String,Object> getCondition(Map<String,Object> params);

	News get(long id) throws LedpException;

	Long save(News entity,String medias) throws LedpException;

	void update(News entity) throws LedpException;

	void update(News entity,String medias) throws LedpException;
	List<Media> getDealerMediaList(Long dealer) throws LedpException;
	List<Long> getNewsMediaList(Long news) throws LedpException;

	List<NewsMedia> getNewsMedia(Long newsId) throws LedpException;

	void delete(Long id) throws LedpException;

	void destroy(Long id) throws LedpException;

	News find(Long dealer,String name) throws LedpException;

	List<News> executeQuery1(Map params) throws LedpException;

	int getTotalRow1(Map params) throws LedpException;

	Map<String,String> uploadCover(HttpServletRequest request,String name);

	String getSaveFileName(String originalFilename,String saveFileName);
}
