package com.citroen.ledp.interceptor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Title: Permission.java
 * @Package com.citroen.ledp.interceptor
 * @Description: TODO(权限注解)
 * @author 廖启洪
 * @date 2015年3月3日 上午10:20:01
 * @version V1.0
 */

@Target({ElementType.FIELD,ElementType.METHOD}) 
@Retention(RetentionPolicy.RUNTIME)
public @interface Permission {
	public String[] code();
}
