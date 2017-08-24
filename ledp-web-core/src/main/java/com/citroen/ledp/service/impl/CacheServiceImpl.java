package com.citroen.ledp.service.impl;

import com.citroen.ledp.dao.mybaits.MybaitsGenericDao;
import com.citroen.ledp.domain.Menu;
import com.citroen.ledp.exception.LedpException;
import com.citroen.ledp.service.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @Title: CacheServiceImpl.java
 * @Package com.citroen.ledp.service
 * @Description: TODO(用一句话描述该文件做什么)
 * @author 廖启洪
 * @date 2015年2月6日 上午5:11:28
 * @version V1.0
 */
@Service
public class CacheServiceImpl implements CacheService {
	@Autowired
	private MybaitsGenericDao<Long> genericDao;
	private Map<String,Menu> menus = new HashMap<String,Menu>();
	
	public Menu getMenuByAction(String action) throws LedpException{
		if(!menus.containsKey(action)){
			Menu menu = genericDao.find(Menu.class,"select * from t_menu where action='"+action+"'");
			menus.put(action,menu);
		}
		return menus.get(action);
	}
}
