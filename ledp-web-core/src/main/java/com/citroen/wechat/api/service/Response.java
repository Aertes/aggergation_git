package com.citroen.wechat.api.service;

import java.util.HashMap;
import java.util.Map;

public class Response<T> {
	public static int SUCCESS = 200;//200成功
	public static int ERROR = 201;//201失败
	private int status;//200成功，201失败
	private String message;
	private T object;
	public Response(int status,String message) {
		this.status = status;
		this.message = message;
	}
	public Response(int status,String message,Map<?,?> json) {
		this.status = status;
		this.message = message;
		if(json!=null){
			for(Object object:json.entrySet()){
				Map.Entry<?,?> entry = (Map.Entry<?,?>)object;
				String key   = entry.getKey()!=null?entry.getKey().toString():null;
				String value = entry.getValue()!=null?entry.getValue().toString():null;
				params.put(key, value);
			}
		}
	}
	
	private Map<String,String> params = new HashMap<String,String>();
	
	public int getStatus() {
		return status;
	}
	public String getMessage() {
		return message;
	}
	public boolean containsKey(String key){
		return params.containsKey(key);
	}
	public String get(String key){
		return params.get(key);
	}
	public void put(String key,String value){
		params.put(key,value);
	}
	public Map<String, String> getParams() {
		return params;
	}
	public void setParams(Map<String, String> params) {
		this.params = params;
	}
	public void setObject(T t) {
		this.object = t;
	}
	public T getObject() {
		return object;
	}
	public String toString() {
		return "{status="+status+"，message="+message+",params="+params+"}";
	}
}
