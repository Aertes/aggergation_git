package com.citroen.wechat.api.service;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

import com.citroen.wechat.api.model.Fans;
import com.citroen.wechat.api.model.Final;
import com.citroen.wechat.api.util.HttpUtil;
import com.citroen.wechat.api.util.ReturnCode;

/**
 * 粉丝接口
 * 
 */
public class ApiFans {
	/**
	 * 粉丝获取接口
	 */
	public static Response<ArrayList<Fans>> list(String access_token,String next_openid)throws ApiException {
		JSONObject json = null;
		try {
			String uri = Final.GET_USER_SUM + access_token;
			if(!StringUtils.isBlank(next_openid)){
				uri+="&next_openid="+next_openid;
			}
			json = HttpUtil.doGet(uri);
		} catch (Exception e) {
			throw new ApiException(e.getCause());
		}
		if(json.containsKey("errcode")){
			throw new ApiException(json.getInt("errcode"),json.getString("errmsg"));
		}
		if(!json.containsKey("count")){
			throw new ApiException("从接口返回数据中未获取到粉丝");
		}
		if(!json.containsKey("data")){
			throw new ApiException("从接口返回数据中未获取到粉丝");
		}
		long total = json.getLong("total");
		long count = json.getLong("count");
		String next_openid2 = json.getString("next_openid");
		ArrayList<Fans> fansList = new ArrayList<Fans>();
		
		JSONObject data = json.getJSONObject("data");
		JSONArray array = data.getJSONArray("openid");
		for(int i=0;i<array.size();i++){
			String openid = array.getString(i);
			Fans fans = new Fans();
			fans.setOpenid(openid);
			fansList.add(fans);
		}
		Response<ArrayList<Fans>> response = new Response<ArrayList<Fans>>(Response.ERROR,"操作成功");
		response.put("total",String.valueOf(total));
		response.put("count",String.valueOf(count));
		response.put("next_openid",next_openid2);
		response.setObject(fansList);
		return response;
	}
	
	public static Fans getFansByOpenId(String access_token,String openid)throws ApiException {
		if(StringUtils.isBlank(openid)){
			throw new ApiException("参数openid不能为空");
		}
		JSONObject json = null;
		try {
			String uri = Final.GET_USER_INFO+access_token+"&lang=zh_CN&openid="+openid;
			json = HttpUtil.doGet(uri);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ApiException(e.getCause());
		}
		if(json.containsKey("errcode")){
			throw new ApiException(json.getInt("errcode"),json.getString("errmsg"));
		}
		return (Fans) JSONObject.toBean(json,Fans.class);
	}

	
	public static Response<?> update(String access_token,Fans fans){
		if(fans==null){
			return new Response(Response.ERROR,"粉丝信息不能为空");
		}
		if(StringUtils.isBlank(fans.getOpenid())){
			return new Response(Response.ERROR,"粉丝openid不能为空");
		}
		if(StringUtils.isBlank(fans.getRemark())){
			return new Response(Response.ERROR,"粉丝remark不能为空");
		}
		
		JSONObject json = null;
		try {
			String uri = Final.UPDATEREMARK+access_token;
			String params = "{\"openid\":\""+fans.getOpenid()+"\",\"remark\":\""+fans.getRemark()+"\"}";
			json = HttpUtil.doPost(uri,params);
		} catch (Exception e) {
			return new Response(Response.ERROR,e.getMessage(),json);
		}
		if(json==null){
			return new Response(Response.ERROR,"接口返回数据为空",json);
		}
		if(json.containsKey("errcode") && json.getInt("errcode")!=0){
			return new Response(Response.ERROR,ReturnCode.get(json.getInt("errcode")),json);
		}
		return new Response(Response.SUCCESS,"操作成功",json);
	}

	
}
