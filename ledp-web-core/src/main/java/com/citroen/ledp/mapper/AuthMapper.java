package com.citroen.ledp.mapper;

import com.citroen.ledp.query.UserQuery;
import com.citroen.ledp.domain.User;

import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

/**
 * Created by cyberoller on 2015/11/3.
 */
@Repository
public interface AuthMapper extends Mapper<User>{


}
