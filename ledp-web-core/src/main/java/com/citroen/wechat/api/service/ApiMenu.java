package com.citroen.wechat.api.service;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.citroen.wechat.api.model.Final;
import com.citroen.wechat.api.model.Menu;
import com.citroen.wechat.api.util.HttpUtil;
import com.citroen.wechat.api.util.ReturnCode;

/**
 * 菜单接口
 * 
 */
public class ApiMenu {
	/**
	 * 添加菜单
	 * */
	public static Response update(String access_token,String menus) {
		String uri = Final.MENU_CREATE+access_token;
		JSONObject json = null;
		try {
			json = HttpUtil.doPost(uri,menus);
		} catch (Exception e) {
			return new Response(Response.ERROR,e.getMessage());
		}
		if(json==null){
			return new Response(Response.ERROR,"接口返回数据为空",json);
		}
		if(json.containsKey("errcode") && json.getInt("errcode")!=0){
			return new Response(Response.ERROR,ReturnCode.get(json.getInt("errcode")),json);
		}
		return new Response(Response.SUCCESS,"操作成功",json);
	}
	
	/**
	 * 添加菜单
	 * */
	public static Response update(String access_token,List<Menu> menus) {
		StringBuilder params = new StringBuilder();
		params.append("{");
		params.append("\"button\": [");
		for(int i = 0;i<menus.size();i++){
			if(i>0){params.append(",");}
			params.append(menus.get(i));
		}
		params.append("]");
		params.append("}");
		return update(access_token,params.toString());
	}
	
	/**
	 * 删除菜单
	 * */
	public static Response delete(String access_token) {
		String uri = Final.MENU_DELETE+access_token;
		JSONObject json = null;
		try {
			json = HttpUtil.doPost(uri);
		} catch (Exception e) {
			return new Response(Response.ERROR,e.getMessage());
		}
		if(json==null){
			return new Response(Response.ERROR,"接口返回数据为空",json);
		}
		if(json.containsKey("errcode") && json.getInt("errcode")!=0){
			return new Response(Response.ERROR,ReturnCode.get(json.getInt("errcode")),json);
		}
		return new Response(Response.SUCCESS,"操作成功",json);
	}
	/**
	 * 获取菜单
	 * */
	public static List<Menu> get(String access_token) throws ApiException {
		String uri = Final.GET_MENU+access_token;
		JSONObject json = null;
		try {
			json = HttpUtil.doPost(uri);
		} catch (Exception e) {
			throw new ApiException(e.getCause());
		}
		if(json == null){
			throw new ApiException("获取菜单接口调用失败,接口返回：null");
		}
		if(json.containsKey("errcode")){
			throw new ApiException(json.getInt("errcode"),json.getString("errmsg"));
		}
		if(!json.containsKey("menu")){
			throw new ApiException("获取菜单接口调用失败,接口返回："+json);
		}
		JSONObject jsonMenu = json.getJSONObject("menu");
		return toMenuList(jsonMenu.getJSONArray("button"));
	}
	
	private static List<Menu> toMenuList(JSONArray jsonArray){
		List<Menu> menus = new ArrayList<Menu>();
		for(int i=0;i<jsonArray.size();i++){
			JSONObject j = jsonArray.getJSONObject(i);
			Menu menu = new Menu();
			menu.setName(j.getString("name"));
			if(j.containsKey("type")){
				menu.setType(j.getString("type"));
			}
			if(j.containsKey("key")){
				menu.setKey(j.getString("key"));
			}
			if(j.containsKey("url")){
				menu.setUrl(j.getString("url"));
			}
			if(j.containsKey("media_id")){
				menu.setMediaId(j.getString("media_id"));
			}
			if(j.containsKey("sub_button")){
				List<Menu> children = toMenuList(j.getJSONArray("sub_button"));
				menu.setChildren(children);
			}
			menus.add(menu);
		}
		return menus;
	}
}
