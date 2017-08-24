package com.citroen.ledp.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.citroen.ledp.dao.mybaits.MybaitsGenericDao;
import com.citroen.ledp.domain.Organization;
import com.citroen.ledp.domain.Permission;
import com.citroen.ledp.exception.LedpException;

/**
 * @Title: PermissionService.java
 * @Package com.citroen.ledp.service
 * @Description: TODO(权限服务类)
 * @author 廖启洪
 * @date 2015年3月14日 上午11:03:45
 * @version V1.0
 */

public interface PermissionService {

	/**
	 * @Title: hasAuth
	 * @Description: TODO(判断参数代码是否有授权)
	 * @param code
	 * @return
	 * @return boolean
	 */
	boolean hasAuth(String code);
	Permission get(long id) throws LedpException;
}
