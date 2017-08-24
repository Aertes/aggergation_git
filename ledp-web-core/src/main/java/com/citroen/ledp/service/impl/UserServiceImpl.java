package com.citroen.ledp.service.impl;

import com.citroen.ledp.dao.mybaits.MybaitsGenericDao;
import com.citroen.ledp.domain.Constant;
import com.citroen.ledp.domain.User;
import com.citroen.ledp.exception.LedpException;
import com.citroen.ledp.exception.LogicException;
import com.citroen.ledp.service.ConstantService;
import com.citroen.ledp.service.UserService;
import com.citroen.ledp.util.MapUtil;
import com.citroen.wechat.util.ConstantUtil;
import com.citroen.wechat.util.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * /**
 *
 * @author 廖启洪
 * @version V1.0
 * @Title: UserService.java
 * @Package com.citroen.ledp.service
 * @Description: TODO(用一句话描述该文件做什么)
 * @date 2015年1月25日 下午3:13:13
 */

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private MybaitsGenericDao<Long> mybaitsGenericDao;
    @Autowired
    private ConstantService constantService;


    public List<User> executeQuery(Map params) throws LedpException {

        Map<String, Object> condition = getCondition(params);
        Map namedParams = (Map) condition.get("namedParams");
        String sql = "select * from t_user where id<>1" + condition.get("namedSql");
        if (params.containsKey("sortName")) {
            if (params.get("sortName").toString().equals("status.name")) {
                sql += " order by status";
            } else {
                sql += " order by " + params.get("sortName");
            }
        }
        if (params.containsKey("sortOrder")) {
            sql += " " + params.get("sortOrder");
        }

        Map paginateParams = new HashMap();
        if (!params.containsKey("max")) {
            paginateParams.put("max", 10);
            paginateParams.put("offset", 0);
        }
        return mybaitsGenericDao.executeQuery(User.class, sql, namedParams, paginateParams);
    }

    public int getTotalRow(Map params) throws LedpException {
        Map<String, Object> condition = getCondition(params);
        Map namedParams = (Map) condition.get("namedParams");
        String sql = "select count(id) count from t_user where id<>1" + condition.get("namedSql");
        List<Map> list = mybaitsGenericDao.executeQuery(sql, namedParams);
        if (list.isEmpty()) {
            return 0;
        }
        Map map = list.get(0);
        return map.isEmpty() ? 0 : Integer.parseInt(map.get("count").toString());
    }


    public boolean findCode(String code) throws LedpException {
        if (code == null) {
            return false;
        }

        String sql = "select * from t_user where id<> 1 and code='" + code + "'";
        List<User> lists = mybaitsGenericDao.executeQuery(User.class, sql);

        if (lists.size() > 0) {
            return true;
        }

        return false;
    }

    public boolean findCodes(String code, Long userId) throws LedpException {
        if (code == null) {
            return false;
        }

        String sql = "select * from t_user where id<> 1 and id!=" + userId + " and code='" + code + "'";
        List<User> lists = mybaitsGenericDao.executeQuery(User.class, sql);

        if (lists.size() > 0) {
            return true;
        }

        return false;
    }


    public Map<String, Object> getCondition(Map<String, Object> params) throws LedpException {
        MapUtil<String, Object> mapUtil = new MapUtil<String, Object>(params);
        Map<String, Object> namedParams = new HashMap<String, Object>();

        StringBuilder namedSql = new StringBuilder();

        if (!mapUtil.isBlank("name")) {
            String name = mapUtil.get("name");
            namedParams.put("name", "%" + name + "%");
            namedSql.append(" and name like :name");
        }
        if (!mapUtil.isBlank("code")) {
            String code = mapUtil.get("code");
            namedParams.put("code", "%" + code + "%");
            namedSql.append(" and code like :code");
        }
        if (!mapUtil.isBlank("username")) {
            String username = mapUtil.get("username");
            namedParams.put("username", "%" + username + "%");
            namedSql.append(" and username like :username");
        }
        if (!mapUtil.isBlank("statusId")) {
            Long statusId = Long.parseLong(params.get("statusId").toString());
            namedParams.put("statusId", statusId);
            namedSql.append(" and status =:statusId");
        }

        if (!mapUtil.isBlank("roleId")) {
            Long statusId = Long.parseLong(params.get("roleId").toString());
            namedParams.put("roleId", statusId);
            namedSql.append(" and role =:roleId");
        }

        if (!mapUtil.isBlank("usernodeId") && !mapUtil.isBlank("nodeType")) {
            Long nodeId = Long.parseLong(params.get("usernodeId").toString());
            String nodeType = mapUtil.get("nodeType");
            if (nodeType.equals("headquarters")) {
                namedSql.append(" and org=:nodeId");
            }
            if (nodeType.equals("largeArea")) {
                namedSql.append(" and org=:nodeId");
            }
            if (nodeType.equals("dealer")) {
                namedSql.append(" and dealer=:nodeId");
            }
            namedParams.put("nodeId", nodeId);
        } else {
            if (!mapUtil.isBlank("orgId")) {
                Long orgId = Long.parseLong(params.get("orgId").toString());
                namedParams.put("orgId", orgId);
                namedSql.append(" and org =:orgId");
            }
        }
        Constant con = constantService.find("record_status", "delete");
        namedSql.append(" and status!=" + con.getId());

        Map<String, Object> rs = new HashMap<String, Object>();
        rs.put("namedSql", namedSql);
        rs.put("namedParams", namedParams);
        return rs;
    }

    public User get(long id) throws LedpException {
        return mybaitsGenericDao.get(User.class, id);
    }

    public Long save(User entity) throws LedpException {
        //密码加密方式为密码+用户名
        entity.setPassword(MD5Util.MD5(MD5Util.MD5(entity.getPassword() + entity.getUsername())));
        return mybaitsGenericDao.save(entity);
    }

    public void update(User entity) throws LedpException {
        User user = this.get(entity.getId());
        if (user == null) {
            throw new LedpException("用户不存在");
        }
        String newPwd = MD5Util.MD5(MD5Util.MD5(entity.getPassword() + entity.getUsername()));
        if (!ConstantUtil.FALSE_PASSWORD.equalsIgnoreCase(entity.getPassword())
                && !entity.getPassword().equalsIgnoreCase(user.getPassword())
                && !newPwd.equalsIgnoreCase(user.getPassword())) {
            entity.setPassword(newPwd);
        }else {
            entity.setPassword(user.getPassword());
        }
        mybaitsGenericDao.update(entity);
    }

    public void delete(Long id) throws LedpException {
        User entity = mybaitsGenericDao.get(User.class, id);
        if (entity == null) {
            throw new LogicException("操作对象不存在！");
        }
        entity.setStatus(constantService.find("record_status", "delete"));
        mybaitsGenericDao.update(entity);
    }

    public void active(Long id) throws LedpException {
        User entity = mybaitsGenericDao.get(User.class, id);
        if (entity == null) {
            throw new LogicException("操作对象不存在！");
        }
        entity.setStatus(constantService.find("record_status", "active"));
        mybaitsGenericDao.update(entity);
    }

    public void inactive(Long id) throws LedpException {
        User entity = mybaitsGenericDao.get(User.class, id);
        if (entity == null) {
            throw new LogicException("操作对象不存在！");
        }
        entity.setStatus(constantService.find("record_status", "inactive"));
        mybaitsGenericDao.update(entity);
    }

    public User find(Long organizaton, String name) throws LedpException {
        String query = "select * from t_user where org=" + organizaton + " and name='" + name + "'";
        return mybaitsGenericDao.find(User.class, query);
    }

    public User finds(Long organizaton, Long dealerId, String name) throws LedpException {
        if (organizaton != null) {
            String query = "select * from t_user where org=" + organizaton + " and name='" + name + "'";
            return mybaitsGenericDao.find(User.class, query);
        }
        if (dealerId != null) {
            String query = "select * from t_user where dealer=" + dealerId + " and name='" + name + "'";
            return mybaitsGenericDao.find(User.class, query);
        }
        return null;
    }

    public User findss(Long organizaton, Long dealerId, String name, Long userId) throws LedpException {
        if (organizaton != null) {
            String query = "select * from t_user where org=" + organizaton + " and name='" + name + "' and id!=" + userId;
            return mybaitsGenericDao.find(User.class, query);
        }
        if (dealerId != null) {
            String query = "select * from t_user where dealer=" + dealerId + " and name='" + name + "' and id!=" + userId;
            return mybaitsGenericDao.find(User.class, query);
        }
        return null;
    }

    public User findByUsername(String username) throws LedpException {
        String query = "select * from t_user where username='" + username + "'";
        return mybaitsGenericDao.find(User.class, query);
    }

    public User findByUsernames(String username, Long userId) throws LedpException {
        String query = "select * from t_user where username='" + username + "' and id!=" + userId;
        return mybaitsGenericDao.find(User.class, query);
    }


}
