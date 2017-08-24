package com.citroen.wechat.api.token;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.citroen.ledp.dao.mybaits.MybaitsGenericDao;
import com.citroen.ledp.util.SpringContextUtil;
import com.citroen.wechat.api.service.ApiAccessToken;
import com.citroen.wechat.api.service.Response;

public class TokenHolder {
	private  static Log logger = LogFactory.getLog(TokenHolder.class);
	
	// 第三方component_verify_ticket参数
	private static String componentVerifyTicket;
	// 第三方平台令牌
	private static String componentAccessToken;
	// 公众号令牌
	private static Map<String,String> authorizerAccessTokens = new HashMap<String,String>();
	// 公众号JS临时票据
	private static Map<String,String> authorizerJsapiTickets = new HashMap<String,String>();
	
	/**缓存component_verify_ticket参数*/
	public static void setComponentVerifyTicket(String componentVerifyTicket) {
		TokenHolder.componentVerifyTicket = componentVerifyTicket;
		try {
			MybaitsGenericDao<Long> mybaitsGenericDao = SpringContextUtil.getTypeBean("mybaitsGenericDao");
			Map map = mybaitsGenericDao.find("select * from t_field_value where field='componentVerifyTicket'");
			if(map!=null && map.containsKey("value")){
				mybaitsGenericDao.execute("update t_field_value set value='"+componentVerifyTicket+"' where field='componentVerifyTicket'");
			}else{
				mybaitsGenericDao.execute("insert into t_field_value(field,value) values('componentVerifyTicket','"+componentVerifyTicket+"')");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**获取component_verify_ticket参数*/
	public static String getComponentVerifyTicket() {
		if(componentVerifyTicket==null){
			try {
				MybaitsGenericDao<Long> mybaitsGenericDao = SpringContextUtil.getTypeBean("mybaitsGenericDao");
				Map map = mybaitsGenericDao.find("select * from t_field_value where field='componentVerifyTicket'");
				if(map!=null && map.containsKey("value")){
					componentVerifyTicket = map.get("value").toString();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return componentVerifyTicket;
	}
	
	/**缓存第三方平台令牌componentAccessToken*/
	public static void setComponentAccessToken(String componentAccessToken){
		TokenHolder.componentAccessToken = componentAccessToken;
	}

	/**获取第三方平台令牌componentAccessToken*/
	public static String getComponentAccessToken(){
		if(componentAccessToken==null){
			ComponentAccessToken act = SpringContextUtil.getTypeBean("componentAccessToken");
			act.refreshComponentAccessToken();
		}
		return componentAccessToken;
	}
	
	/**缓存公众号令牌authorizer_access_token*/
	public static void setAuthorizerAccessToken(String authorizer_appid,String authorizer_access_token){
		authorizerAccessTokens.put(authorizer_appid, authorizer_access_token);
		logger.info("缓存公众号信息完成,authorizer_appid="+authorizer_appid+",authorizer_access_token="+authorizer_access_token);
	}

	/**获取公众号令牌authorizer_access_token*/
	public static String getAuthorizerAccessToken(String authorizer_appid,String authorizer_refresh_token){
		if(!authorizerAccessTokens.containsKey(authorizer_appid)){
			Response<?> response = ApiAccessToken.getAuthorizerAccessToken(authorizer_appid, authorizer_refresh_token);
			if(response.getStatus()==Response.SUCCESS){
				String authorizer_access_token = response.get("authorizer_access_token");
				setAuthorizerAccessToken(authorizer_appid,authorizer_access_token);
			}
		}
		return authorizerAccessTokens.get(authorizer_appid);
	}

	/**获取缓存中公众号authorizer_access_appid*/
	public static String getAuthorizerAccessAppid(String access_token){
		if(access_token==null){
			return null;
		}
		for(Map.Entry<String,String> entry :authorizerAccessTokens.entrySet()){
			String authorizer_access_token = entry.getValue();
			String authorizer_access_appid = entry.getKey();
			if(authorizer_access_token!=null && authorizer_access_token.equals(access_token)){
				return authorizer_access_appid;
			}
		}
		return null;
	}
	
	
	/**清空公众号令牌authorizer_access_token*/
	public static void clearAuthorizerAccessToken(){
		authorizerAccessTokens.clear();
	}
	
	/**缓存公众号JS临时票据jsapi_ticket*/
	public static void setAuthorizerJsapiTicket(String component_access_token,String jsapi_ticket){
		authorizerJsapiTickets.put(component_access_token, jsapi_ticket);
		logger.info("缓存公众号JS临时票据,component_access_token="+component_access_token+",jsapi_ticket="+jsapi_ticket);
	}
	/**获取公众号JS临时票据jsapi_ticket*/
	public static String getAuthorizerJsapiTicket(String appid,String authorizer_refresh_token){
		if(!authorizerJsapiTickets.containsKey(appid)){
			Response<?> response = ApiAccessToken.getAuthorizerJsapiTicket(appid,authorizer_refresh_token);
			if(response.getStatus()==Response.SUCCESS){
				String jsapi_ticket = response.get("ticket");
				setAuthorizerJsapiTicket(appid,jsapi_ticket);
			}
		}
		return authorizerJsapiTickets.get(appid);
	}
	/**清空公众号JS临时票据jsapi_ticket*/
	public static void clearAuthorizerJsapiTicket(){
		authorizerJsapiTickets.clear();
	}
	
}
