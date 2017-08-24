package com.citroen.ledp.service.impl;

import com.citroen.ledp.dao.mybaits.MybaitsGenericDao;
import com.citroen.ledp.domain.Constant;
import com.citroen.ledp.exception.LedpException;

import com.citroen.ledp.service.ConstantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
/**
 * @Title: OrganizationService.java
 * @Package com.citroen.ledp.service
 * @Description: TODO(机构服务类)
 * @author 廖启洪
 * @date 2015年1月25日 下午3:13:13
 * @version V1.0
 */

@Service("constantServiceImpl")
public class ConstantServiceImpl implements ConstantService {
	
	@Autowired
	private MybaitsGenericDao<Long> mybaitsGenericDao;
	
	public Constant find(String type,String code) throws LedpException{
		return mybaitsGenericDao.find(Constant.class,"select c.* from t_constant c left join t_constant_category cc on c.category=cc.id where cc.code='"+type+"' and c.code='"+code+"'");
	}
	
	public List<Constant> findAll(String type) throws LedpException{
		return mybaitsGenericDao.executeQuery(Constant.class,"select c.* from t_constant c left join t_constant_category cc on c.category=cc.id where c.status=1 and cc.code='"+type+"' order by c.weight asc");
	}
	
	public List<Constant> findAll(String type,int status) throws LedpException{
		return mybaitsGenericDao.executeQuery(Constant.class,"select c.* from t_constant c left join t_constant_category cc on c.category=cc.id where c.status="+status+" and cc.code='"+type+"' order by c.weight asc");
	}
	public Constant get(long id) throws LedpException {
		return mybaitsGenericDao.get(Constant.class, id);
	}
	
	/**
	 * 根据常量名称和类型查询
	 * @param name
	 * @param category
	 * @return
	 * @throws LedpException 
	 */
	public Constant getByNameAndCategory(String name,Long category) throws LedpException{
		String sql = "SELECT * FROM t_constant WHERE name = '"+name+"' and category = "+category;
		return mybaitsGenericDao.find(Constant.class, sql);
	}
}
