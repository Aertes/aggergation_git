package com.citroen.wechat.api.service;

import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.citroen.wechat.api.model.Final;
import com.citroen.wechat.api.token.TokenHolder;
import com.citroen.wechat.api.util.HttpUtil;

public class ApiAccessToken {
	private static Log logger = LogFactory.getLog(ApiAccessToken.class);
	
	

	/**根据authorization_code获取公众号信息*/
	public static Response getAuthorizerInfo(String authorizer_appid){
		//获取微信矩阵信息
		//准备接口参数信息
		String uri=Final.API_AUTHORIZER_INFO+TokenHolder.getComponentAccessToken();
		Map<String,String> params = new HashMap<String,String>();
		params.put("component_appid",Final.getValue(Final.COMPONENT_APPID));
		params.put("authorizer_appid",authorizer_appid);
		//调用接口，获取公众号授权信息
		JSONObject json = null;
		try {
			json = HttpUtil.doPost(uri,params);
		} catch (Exception e) {
			return new Response(Response.ERROR,e.getMessage());
		}
		if(json==null){
			return new Response(Response.ERROR,"接口操作失败");
		}
		if(!json.containsKey("authorization_info")){
			return new Response(Response.ERROR,"从接口返回数据中未获取到公众号基本信息");
		}
		Response response = new Response(Response.SUCCESS,"操作成功");
		JSONObject info = json.getJSONObject("authorizer_info");
		if(info.containsKey("nick_name")){
			response.put("nick_name",info.getString("nick_name"));
		}
		if(info.containsKey("head_img")){
			response.put("head_img",info.getString("head_img"));
		}
		if(info.containsKey("service_type_info")){
			response.put("service_type_info",info.getJSONObject("service_type_info").getString("id"));
		}
		if(info.containsKey("verify_type_info")){
			response.put("verify_type_info",info.getJSONObject("verify_type_info").getString("id"));
		}
		if(info.containsKey("user_name")){
			response.put("user_name",info.getString("user_name"));
		}
		if(info.containsKey("alias")){
			response.put("alias",info.getString("alias"));
		}
		if(info.containsKey("qrcode_url")){
			response.put("qrcode_url",info.getString("qrcode_url"));
		}
		if(info.containsKey("authorization_info")){
			StringBuilder permissions = new StringBuilder();
			JSONArray func_info = json.getJSONObject("authorization_info").getJSONArray("func_info");
			for(int i=0;i<func_info.size();i++){
				JSONObject func = func_info.getJSONObject(i);
				String permission = func.getJSONObject("funcscope_category").getString("id");
				if(i>0){permissions.append(",");}
				permissions.append(permission);
			}
			response.put("funcscope_categorys",permissions.toString());
		}
		return response;
	}
	
	/**根据authorization_code获取公众号信息*/
	public static Response getAuthorizerToken(String authorization_code){
		//获取微信矩阵信息
		//准备接口参数信息
		String uri=Final.API_QUERY_AUTH+TokenHolder.getComponentAccessToken();
		Map<String,String> params = new HashMap<String,String>();
		params.put("component_appid",Final.getValue(Final.COMPONENT_APPID));
		params.put("authorization_code",authorization_code);
		//调用接口，获取公众号授权信息
		JSONObject json = null;
		try {
			json = HttpUtil.doPost(uri,params);
		} catch (Exception e) {
			return new Response(Response.ERROR,e.getMessage());
		}
		if(json==null){
			return new Response(Response.ERROR,"接口操作失败");
		}
		if(!json.containsKey("authorization_info")){
			return new Response(Response.ERROR,"从接口返回数据中未获取到公众号认证信息");
		}
		JSONObject info = json.getJSONObject("authorization_info");
		String authorizer_appid = info.getString("authorizer_appid");
		String authorizer_access_token = info.getString("authorizer_access_token");
		String authorizer_refresh_token = info.getString("authorizer_refresh_token");

		StringBuilder permissions = new StringBuilder();
		JSONArray func_info = info.getJSONArray("func_info");
		for(int i=0;i<func_info.size();i++){
			JSONObject func = func_info.getJSONObject(i);
			String permission = func.getJSONObject("funcscope_category").getString("id");
			if(i>0){permissions.append(",");}
			permissions.append(permission);
		}
		
		Response response = new Response(Response.SUCCESS,"操作成功");
		response.put("authorizer_appid", authorizer_appid);
		response.put("authorizer_access_token", authorizer_access_token);
		response.put("authorizer_refresh_token", authorizer_refresh_token);
		response.put("funcscope_categorys",permissions.toString());
		return response;
	}
	
	/**根据authorizer_refresh_token获取新的authorizer_access_token*/
	public static Response getAuthorizerAccessToken(String authorizer_appid,String authorizer_refresh_token){
			//获取微信矩阵信息
			String uri = Final.API_AUTHORIZER_TOKEN+TokenHolder.getComponentAccessToken();
			//准备接口参数信息
			Map<String,String> params  = new HashMap<String,String>();
			params.put("component_appid",Final.getValue(Final.COMPONENT_APPID));
			params.put("authorizer_appid",authorizer_appid);
			params.put("authorizer_refresh_token",authorizer_refresh_token);
			//调用接口，获取公众号令牌
			JSONObject json = null;
			try {
				json = HttpUtil.doPost(uri,params);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(json==null){
				return new Response(Response.ERROR,"接口操作失败");
			}
			if(!json.containsKey("authorizer_access_token")){
				return new Response(Response.ERROR,"从接口返回数据中未获取到authorizer_access_token");
			}
			return new Response(Response.SUCCESS,"操作成功",json);
	}

	/**获取公众号JS临时票据jsapi_ticket*/
	public static Response<?> getAuthorizerJsapiTicket(String authorizer_appid,String authorizer_refresh_token) {
		//获取微信矩阵信息
		//String access_token = TokenHolder.getComponentAccessToken();
		String access_token = TokenHolder.getAuthorizerAccessToken(authorizer_appid, authorizer_refresh_token);
		logger.info("*******************access_token****************==="+access_token);
		String uri = Final.API_AUTHORIZER_JSAPI_TICKET+access_token;
		//调用接口，获取公众号令牌
		JSONObject json = null;
		try {
			json = HttpUtil.doGet(uri);
		} catch (Exception e) {
			logger.error("获取公众号令牌失败：" + e.getMessage());
		}
		if(json==null){
			return new Response(Response.ERROR,"接口操作失败");
		}
		if(!json.containsKey("errcode")||!json.containsKey("ticket")||json.getInt("errcode")!=0){
			return new Response(Response.ERROR,"从接口返回数据中未获取到JS临时票据jsapi_ticket");
		}
		return new Response(Response.SUCCESS,"操作成功",json);
	}
	
	/**
	 * 获取公众号授权前往URL
	 */
	public static Response getAuthorizerAccessTokenUrl() throws ApiException{
		//获取预授权码。预授权码用于公众号授权时的第三方平台方安全验证。
		String uri=Final.API_CREATE_PREAUTHCODE+TokenHolder.getComponentAccessToken();
		Map<String,String> params = new HashMap<String,String>();
		params.put("component_appid",Final.getValue(Final.COMPONENT_APPID));
		try {
			JSONObject json = HttpUtil.doPost(uri,params);
			String pre_auth_code = json.getString("pre_auth_code");
			String url = Final.COMPONENT_LOGINPAGE +"&pre_auth_code="+pre_auth_code+"&redirect_uri="+Final.getValue(Final.COMPONENT_LOGINPAGE_REDIRECT_URI);
			Response response = new Response(Response.SUCCESS,"操作成功");
			response.put("url", url);
			return response;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("getAuthorizerAccessTokenUrl出现异常");
			return new Response(Response.ERROR,e.getMessage());
		}
	}

}
