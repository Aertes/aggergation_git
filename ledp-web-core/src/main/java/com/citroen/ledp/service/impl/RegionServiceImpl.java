package com.citroen.ledp.service.impl;

import com.citroen.ledp.dao.mybaits.MybaitsGenericDao;
import com.citroen.ledp.domain.Region;
import com.citroen.ledp.exception.LedpException;
import com.citroen.ledp.service.RegionService;
import com.citroen.ledp.util.MapUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 地区服务类
 * @author      miaoshuai
 * @email       miaoshuai@tocersfot.com
 * @company		www.tocersoft.com
 * @create-time 2015年3月13日 下午1:58:34
 * @version     1.0
 */
@Service
public class RegionServiceImpl implements RegionService{

	@Autowired
	private MybaitsGenericDao<Long> mybaitsGenericDao;

	public List<Region> executeQuery(Map params) throws LedpException{
		Map<String,Object> condition = getCondition(params);
		Map namedParams = (Map)condition.get("namedParams");

		StringBuffer sql = new StringBuffer();
		sql.append("select `id`,`version`,`code`,");
		sql.append(" `en_alias`,`en_name`,`level`,");
		sql.append(" `parent`,`weight`,`zh_alias`,");
		sql.append(" `zh_name`,`used`");
		sql.append("  from t_region");
		sql.append(" where 1=1");
		sql.append(condition.get("namedSql"));
		return mybaitsGenericDao.executeQuery(Region.class,sql.toString(),namedParams);
	}

	public Map<String,Object> getCondition(Map<String,Object> params){
		MapUtil<String,Object> mapUtil = new MapUtil<String,Object>(params);
		Map<String,Object> namedParams = new HashMap<String,Object>();

		StringBuilder namedSql = new StringBuilder();

		// 客户名称
		if(!mapUtil.isBlank("parent")){
			String parent = mapUtil.get("parent").toString();
			namedParams.put("parent",parent);
			namedSql.append(" and parent =:parent");
		}
		Map<String,Object> rs = new HashMap<String,Object>();
		rs.put("namedSql",namedSql);
		rs.put("namedParams",namedParams);
		return rs;
	}

	public Region get(long id) throws LedpException{
		return mybaitsGenericDao.get(Region.class, id);
	}

	/**
	 * 获取省份
	 * @param id
	 * @return
	 * @throws LedpException
	 */
	public List<Region> getProvinces(Long id) throws LedpException{
		String sql = "select * from t_region r where 1=1 ";
		if(id == null || id <= 0){
			sql += " and id="+id;
		}
		sql+=" and level=1 order by code";
		return mybaitsGenericDao.executeQuery(Region.class, sql);
	}

	/**
	 * 获取城市
	 * @param id
	 * @return
	 * @throws LedpException
	 */
	public List<Region> getCitys(Long parentId) throws LedpException{
		String sql = "select * from t_region r where 1=1 ";
		if(parentId > 0){
			sql += " and parent="+parentId;
		}
		sql+=" and level=2 order by code";
		return mybaitsGenericDao.executeQuery(Region.class, sql);
	}


}
