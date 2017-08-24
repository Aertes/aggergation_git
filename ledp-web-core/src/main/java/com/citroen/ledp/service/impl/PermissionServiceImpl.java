package com.citroen.ledp.service.impl;

import com.citroen.ledp.dao.mybaits.MybaitsGenericDao;
import com.citroen.ledp.domain.Permission;
import com.citroen.ledp.exception.LedpException;
import com.citroen.ledp.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @Title: PermissionService.java
 * @Package com.citroen.ledp.service
 * @Description: TODO(权限服务类)
 * @author 廖启洪
 * @date 2015年3月14日 上午11:03:45
 * @version V1.0
 */
@Service
public class PermissionServiceImpl implements PermissionService{

	@Autowired
	private MybaitsGenericDao<Long> mybaitsGenericDao;
	/**
	 * @Title: hasAuth
	 * @Description: TODO(判断参数代码是否有授权)
	 * @param code
	 * @return
	 * @return boolean
	 */
	public boolean hasAuth(String code){
		HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
		HttpSession session = request.getSession();
		List<String> permissions = (List<String>)session.getAttribute("permissions");
		if(permissions==null){
			return false;
		}
		return permissions.contains(code);
	}
	public Permission get(long id) throws LedpException {
		return mybaitsGenericDao.get(Permission.class, id);
	}



}
