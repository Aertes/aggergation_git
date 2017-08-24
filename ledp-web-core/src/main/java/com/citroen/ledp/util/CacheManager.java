package com.citroen.ledp.util;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.citroen.ledp.interceptor.Permission;

/**
 * 緩存
 * @author Administrator
 *
 */
public class CacheManager {
	private static Map<String,String[]> permissions =  new HashMap<String,String[]>();
	
	private static void init(Object controller){
		Class<?> clazz = controller.getClass();
		String controllerName = clazz.getSimpleName();
		Method[] controllerMethods = clazz.getMethods();
		for(Method method:controllerMethods){
			if(!method.isAnnotationPresent(Permission.class)){
				continue;
			}
			Permission permission =  method.getAnnotation(Permission.class);
			permissions.put(getKey(controllerName,method.getName()),permission.code());
			
		}
	}
	
	private static String getKey(String controllerName,String methodName){
		return controllerName+"_"+methodName;
	}
	
	public static Map<String,Object> controllers = new HashMap<String,Object>();
	
	private static CacheManager cache;
	
	public CacheManager getCache(){
		if(null==cache){
			cache = new CacheManager();
		}
		return cache;
	}
	
	public static Map<String,String[]> hasController(Object controller,String className, String methodName){
		if(controllers.containsKey(className+"_"+methodName)){ //如果controler存在
			return permissions;
		}
		init(controller);
		return permissions;
	}
	//是否有权限注解
	public static boolean hasController(String className, String methodName){
		if(permissions.containsKey(className+"_"+methodName)){ //如果controler存在
			return true;
		}else{
			return false;
		}
	}
}
