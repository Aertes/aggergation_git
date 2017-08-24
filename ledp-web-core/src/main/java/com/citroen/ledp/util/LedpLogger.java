package com.citroen.ledp.util;

import java.util.Date;

import com.citroen.ledp.dao.mybaits.MybaitsGenericDao;
import com.citroen.ledp.domain.Log;
import com.citroen.ledp.domain.User;
import com.citroen.ledp.exception.LedpException;

/**
 * @Title: LedpLogger.java
 * @Package com.citroen.ledp.util
 * @Description: TODO(日志接口类)
 * @author 廖启洪
 * @date 2015年2月6日 上午2:25:54
 * @version V1.0
 */
public class LedpLogger {
	public enum Operation{
		create("create"),update("update"),delete("delete"),login("login");
		public String value;
		private Operation(String value){this.value = value;}
	}
	
	public enum Result{
		success("success"),failure("failure");
		public String value;
		private Result(String value){this.value = value;}
	}

	public static void info(User operator,String resource,Operation operation,Result result){
		log("info",operator, resource, operation, result,"");
	}
	public static void info(User operator,String resource,Operation operation,Result result,String comment){
		log("info",operator, resource, operation, result,comment);
	}
	public static void warn(User operator,String resource,Operation operation,Result result){
		log("warn",operator, resource, operation, result,"");
	}
	public static void warn(User operator,String resource,Operation operation,Result result,String comment){
		log("warn",operator, resource, operation, result,comment);
	}
	public static void error(User operator,String resource,Operation operation,Result result){
		log("error",operator, resource, operation, result,"");
	}
	public static void error(User operator,String resource,Operation operation,Result result,String comment){
		log("error",operator, resource, operation, result,comment);
	}
	public static void error(String resource,Operation operation,Result result,String comment){
		log("error",new User(1L), resource, operation, result,comment);
	}
	
	private static void log(String type,User operator,String resource,Operation operation,Result result,String comment){
		Log log = new Log();
		log.setType(type);
		log.setOperator(operator);
		log.setResource(resource);
		log.setOperation(operation.value);
		log.setResult(result.value);
		log.setComment(comment);
		log.setDatetime(new Date());
		try {
			MybaitsGenericDao<Long> genericDao = SpringContextUtil.getTypeBean("mybaitsGenericDao");
			genericDao.save(log);
		} catch (LedpException e) {
			e.printStackTrace();
		}
	}
	
}
