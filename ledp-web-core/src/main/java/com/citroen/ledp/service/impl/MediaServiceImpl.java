package com.citroen.ledp.service.impl;

import com.citroen.ledp.dao.mybaits.MybaitsGenericDao;
import com.citroen.ledp.domain.Constant;
import com.citroen.ledp.domain.Media;
import com.citroen.ledp.exception.LedpException;
import com.citroen.ledp.service.MediaService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MediaServiceImpl implements MediaService{

	private Log logger = LogFactory.getLog(MediaServiceImpl.class);

	@Autowired
	private MybaitsGenericDao<Long> mybaitsGenericDao;

	public List<Media> insert(Map<String, String> params, int pageSize,
							  int pageNum) throws LedpException {

		Map<String, Object> namedParams = getConditions(params);

		Map<String, Object> paginateParams = new HashMap<String, Object>();
		paginateParams.put("max", pageSize);

		paginateParams.put("offset", pageNum);

		String sql = " select * from t_media where 1=1 "
				+ namedParams.get("sql").toString();

		Map<String, Object> named = (Map<String, Object>) namedParams
				.get("nameParam");

		List<Media> vehicleList = mybaitsGenericDao.executeQuery(Media.class, sql,
				named, paginateParams);

		return vehicleList;
	}

	/**
	 * 查询总行数
	 *
	 * @throws LedpException
	 */
	public Integer getCount(Map<String, String> params) throws LedpException {
		Map<String, Object> namedParams = getConditions(params);
		String query = " select count(id) from t_media where 1=1 "
				+ namedParams.get("sql").toString();
		Long count = (Long) mybaitsGenericDao
				.executeQuery(query,
						(Map<String, Object>) namedParams.get("nameParam"))
				.get(0).get("count(id)");

		return Integer.valueOf(count.toString());
	}

	/**
	 * 通过id查找相应的对象
	 *
	 * @param mediaId
	 * @return
	 */

	public Media getMedia(Long mediaId) {
		try {
			return mybaitsGenericDao.get(Media.class, mediaId);
		} catch (LedpException e) {
			logger.error("异常信息：" + e.getMessage());
		}
		return null;
	}

	/**
	 * @Title: getConditions
	 * @Description: TODO(查询车系条件方法)
	 */
	public Map<String, Object> getConditions(Map<String, String> params)
			throws LedpException {
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> nameParam = new HashMap<String, Object>();
		StringBuilder sql = new StringBuilder();

		if (StringUtils.isNotBlank(params.get("name"))) {
			sql.append(" and name like :name ");
			nameParam.put("name", "%" + params.get("name") + "%");
		}
		if (StringUtils.isNotBlank(params.get("code"))) {
			sql.append(" and code like :code ");
			nameParam.put("code", "%" + params.get("code") + "%");
		}
		if (StringUtils.isNotBlank(params.get("status"))) {
			String query = "select * from t_constant where code='"
					+ params.get("status") + "'";
			Constant c = mybaitsGenericDao.find(Constant.class, query);
			if (c != null) {
				sql.append(" and status=" + c.getId());
			}
		}
		map.put("sql", sql);
		map.put("nameParam", nameParam);

		return map;
	}

	/**
	 * 查询所有媒体
	 * @return
	 * @throws LedpException
	 */
	public List<Media> listAll() throws LedpException{
		String sql = "select * from t_media where status=1010 order by id asc";
		List<Media> mediaList = mybaitsGenericDao .executeQuery(Media.class, sql);
		return mediaList;
	}

	/**
	 *
	 * ONorOFF:(启用/禁用车型). <br/>
	 *
	 */
	public void ONorOFF(Long id) {
		try {
			Media media = mybaitsGenericDao.get(Media.class, id);
			if (media != null && media.getStatus() != null) {
				String sql = "select * from t_constant where code='"
						+ (media.getStatus().getCode().equals("active") ? "inactive" : "active") + "'";
				Constant constant = mybaitsGenericDao.find(Constant.class, sql);
				media.setStatus(constant);
				mybaitsGenericDao.update(media);
			}
		} catch (LedpException e) {
			logger.error("异常信息：" + e.getMessage());
		}
	}

	/**
	 * 获取媒体名称
	 * @param mediaIds
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public String getMediaNamesByMediaIds(Long[] mediaIds) {
		StringBuffer names = new StringBuffer();
		String query = "select name from t_media where id in ("+StringUtils.join(mediaIds, ",")+")";
		try{
			List<Map> list = mybaitsGenericDao.executeQuery(query.toString());
			for(Map item : list){
				String name = item.get("name").toString();
				names.append(name).append(",");
			}

		}catch(LedpException ex){
			String msg = "查询对比数据发生异常：" + ex.getMessage();
			logger.error(msg,ex);
		}

		if(names.length() == 0){
			return "";
		}

		return names.substring(0,names.length()-1);
	}

}
