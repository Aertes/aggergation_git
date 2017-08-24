package com.citroen.ledp.service;

import com.citroen.ledp.domain.User;
import com.citroen.ledp.exception.LedpException;

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


public interface UserService {

    List<User> executeQuery(Map params) throws LedpException;

    int getTotalRow(Map params) throws LedpException;


    boolean findCode(String code) throws LedpException;

    boolean findCodes(String code, Long userId) throws LedpException;


    Map<String, Object> getCondition(Map<String, Object> params) throws LedpException;

    User get(long id) throws LedpException;

    Long save(User entity) throws LedpException;

    void update(User entity) throws LedpException;

    void delete(Long id) throws LedpException;

    void active(Long id) throws LedpException ;

    void inactive(Long id) throws LedpException;

    User find(Long organizaton, String name) throws LedpException ;

    User finds(Long organizaton, Long dealerId, String name) throws LedpException;

    User findss(Long organizaton, Long dealerId, String name, Long userId) throws LedpException;

    User findByUsername(String username) throws LedpException ;

    User findByUsernames(String username, Long userId) throws LedpException;


}
