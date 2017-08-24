package com.citroen.ledp.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.citroen.ledp.dao.mybaits.MybaitsGenericDao;
import com.citroen.ledp.domain.Menu;
import com.citroen.ledp.exception.LedpException;

/**
 * @Title: CacheServiceImpl.java
 * @Package com.citroen.ledp.service
 * @Description: TODO(用一句话描述该文件做什么)
 * @author 廖启洪
 * @date 2015年2月6日 上午5:11:28
 * @version V1.0
 */

public interface CacheService {

	Menu getMenuByAction(String action) throws LedpException;
}
