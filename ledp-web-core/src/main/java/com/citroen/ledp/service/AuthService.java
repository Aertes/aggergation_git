package com.citroen.ledp.service;

import com.citroen.ledp.dao.mybaits.MybaitsGenericDao;
import com.citroen.ledp.domain.*;
import com.citroen.ledp.exception.LedpException;
import com.citroen.ledp.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author ZENGGUANG
 * @date 2017/8/15
 * @remark
 */

public interface AuthService {

    boolean exists(String username) throws LedpException;

    User getUser(String username) throws LedpException;

    User getUser(String username, String password) throws LedpException;

    User lock(String username) throws LedpException;

    User unlock(String username) throws LedpException;

    void updateErrCount(User user) throws LedpException;


    void updateUnlockDate(User user) throws LedpException;

    boolean isActive(User user) throws LedpException;

    List<Role> getRoles(User user) throws LedpException;

    List<String> getPermissions(User user) throws LedpException;

    List<Menu> getMenuChildren(Menu parent) throws LedpException;
}