package com.citroen.ledp.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.citroen.ledp.mapper.AuthMapper;
import com.citroen.ledp.query.UserQuery;
import com.citroen.ledp.service.AuthService;
import com.citroen.ledp.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.citroen.ledp.dao.mybaits.MybaitsGenericDao;
import com.citroen.ledp.domain.Menu;
import com.citroen.ledp.domain.Organization;
import com.citroen.ledp.domain.Permission;
import com.citroen.ledp.domain.Role;
import com.citroen.ledp.domain.User;
import com.citroen.ledp.exception.LedpException;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

/**
 * @Title: AuthService.java
 * @Package com.citroen.ledp.service
 * @Description: TODO(用户登录服务类)
 * @author 廖启洪
 * @date 2015年2月6日 上午2:25:54
 * @version V1.0
 */
@Service
public class AuthServiceImpl implements AuthService {
	@Autowired
	private MybaitsGenericDao<Long> genericDao;

	public boolean exists(String username) throws LedpException{
		String query = "select count(id) count_ from t_user where username='"+username+"'";
		Map user = genericDao.find(query);
		if(user ==null){
			return false;
		}
		return Integer.parseInt(user.get("count_").toString())>0;
	}
	public User getUser(String username) throws LedpException{
		String query = "select * from t_user where username='"+username+"'";
		return genericDao.find(User.class,query);
	}
	public User getUser(String username,String password) throws LedpException{
		String query = "select * from t_user where username='"+username+"' and password='"+password+"'";
		return genericDao.find(User.class,query);

	}

	public User lock(String username) throws LedpException{
		String sql = "update t_user set status=1040,date_update=now() where username='"+username+"'";
		genericDao.execute(sql);
		return getUser(username);

	}
	public User unlock(String username) throws LedpException{
		String sql = "update t_user set status=1010,date_update=now(),err_count = 0 where username='"+username+"'";
		genericDao.execute(sql);
		return getUser(username);

	}


    public void updateErrCount(User user) throws LedpException{
        String sql = "update t_user set err_count = "+user.getErrCount()+" where id='"+user.getId()+"'";
        genericDao.execute(sql);
    }


    public void updateUnlockDate(User user) throws LedpException{
        String sql = "update t_user set unlock_date = '"+ DateUtil.format(user.getUnlockDate(),"yyyy-MM-dd hh:mm:ss")+"' where id='"+user.getId()+"'";
        genericDao.execute(sql);
    }
	
	public boolean isActive(User user) throws LedpException{
        if(user.getStatus() == null){
            return false;
        }
		return "active".equals(user.getStatus().getCode());
	}
	
	public List<Role> getRoles(User user) throws LedpException{
		List<Role> roles = new ArrayList<Role>();
		roles.add(user.getRole());
		return roles;
	}
	
	public List<String> getPermissions(User user) throws LedpException{
		
		List<Role> roles = getRoles(user);
		if(roles.isEmpty()){
			new ArrayList<Permission>();
		}
		
		StringBuilder sb = new StringBuilder();
		for(int i = 0;i<roles.size();i++){
			if(i>0){sb.append(",");}
			sb.append(roles.get(i).getId());
		}
		String query = "select p.code from t_permission p left join t_role_permission rp on p.id = rp.permission where rp.role in ("+sb+") ";
		List<Map> rows = genericDao.executeQuery(query);
		List<String> permissions = new ArrayList<String>();
		for(Map row:rows){
			permissions.add(row.get("code").toString());
		}
		
		/*非网点用户不需要添加权限*/
		Organization org = user.getOrg();
		if(org!=null){
			permissions.remove("news/create");
		}
		return permissions;
	}
	
	/**
	 * @Title: getChildren
	 * @Description: TODO(获取菜单树)
	 * @param parent
	 * @return
	 * @throws LedpException
	 * @return List<Menu>
	 */
	public List<Menu> getMenuChildren(Menu parent) throws LedpException{
		List<Menu> children = null;
		if(parent == null){
			children = genericDao.executeQuery(Menu.class,"select * from t_menu where status='active' and parent is null order by weight asc");
		}else{
			children = genericDao.executeQuery(Menu.class,"select * from t_menu where status='active' and parent="+parent.getId()+"  order by weight asc");
		}
		for(Menu child:children){
			child.setChildren(getMenuChildren(child));
		}
		return children;
	}
}