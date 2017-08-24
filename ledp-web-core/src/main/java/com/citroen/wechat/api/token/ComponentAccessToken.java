package com.citroen.wechat.api.token;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.citroen.ledp.util.SpringContextUtil;
import com.citroen.wechat.api.model.Final;
import com.citroen.wechat.api.model.Ticket;
import com.citroen.wechat.api.security.WXBizMsgCrypt;
import com.citroen.wechat.api.util.HttpUtil;
import com.citroen.wechat.service.ApiMessageService;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
/**
 * 公众号授权相关操作
 */
@Component("componentAccessToken")
public class ComponentAccessToken {
	private static Log logger = LogFactory.getLog(ComponentAccessToken.class);
	/**
	 * 该方法用于更新component_verify_ticket，微信服务器每隔10分钟会来调用一次。component_verify_ticket用于获取第三方平台令牌（component_access_token）
	 * @param req
	 */
	public void refreshComponentVerifyTicket(HttpServletRequest req){
		logger.info("执行setComponentVerifyTicket，刷新component_verify_ticket");
        XStream xs = new XStream(new DomDriver());
        xs.alias("xml", Ticket.class);  
        byte[] b = new byte[4096];  
        try {
        	ServletInputStream in = req.getInputStream(); 
            StringBuilder xmlMsg = new StringBuilder();  
	        for (int n; (n = in.read(b)) != -1;) {
	        	xmlMsg.append(new String(b, 0, n, "UTF-8"));
	        }  
	        in.close();
	        Ticket ticket = (Ticket) xs.fromXML(xmlMsg.toString());
	        WXBizMsgCrypt wxb = new WXBizMsgCrypt("", Final.getValue(Final.COMPONENT_ENCODING_AES_KEY),Final.getValue(Final.COMPONENT_APPID));
	        String decrypt = wxb.decrypt(ticket.getEncrypt());
	        ticket =(Ticket) xs.fromXML(decrypt);
	        
			if("unauthorized".equals(ticket.getInfoType())){
				//取消公众号授权
				ApiMessageService messageService = SpringContextUtil.getTypeBean("apiMessageService");
				messageService.unauthorized(ticket.getAuthorizerAppid());
			}else{
		        /*刷新缓存component_verify_ticket参数*/
		        TokenHolder.setComponentVerifyTicket(ticket.getComponentVerifyTicket());
				logger.info("setComponentVerifyTicket执行完成，componentVerifyTicket="+TokenHolder.getComponentVerifyTicket());
			}

        } catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * 该方法用于刷新第三方平台令牌（component_access_token），每100分钟刷新一次。component_access_token用于获取预授权码，component_access_token有效时间为120分钟。
	 */
	@Scheduled(cron = "0 0 0/1 * * ?")
	public void refreshComponentAccessToken(){
		logger.info("执行refreshComponentAccessToken，刷新componentAccessToken");
		if(TokenHolder.getComponentVerifyTicket()==null){
			logger.info("componentVerifyTicket==null,停止刷新componentAccessToken");
			return;
		}
		String uri = Final.GET_COMPONENT_ACCESS_TOKEN;
		Map<String,String> params = new HashMap<String,String>();
		params.put("component_appid",Final.getValue(Final.COMPONENT_APPID));
		params.put("component_appsecret",Final.getValue(Final.COMPONENT_APPSECRET));
		params.put("component_verify_ticket",TokenHolder.getComponentVerifyTicket());
		try {
			JSONObject json = HttpUtil.doPost(uri,params);
			if(json==null ||!json.containsKey("component_access_token")){
				logger.error("componentAccessToken刷新失败，返回JSONObject['component_access_token'] not found.");
			}else{
				/**缓存第三方平台令牌componentAccessToken*/
				String componentAccessToken =  json.getString("component_access_token");
				TokenHolder.setComponentAccessToken(componentAccessToken);
				/**清空缓存公众号令牌authorizer_access_toke*/
				TokenHolder.clearAuthorizerAccessToken();
				/**清空公众号JS临时票据jsapi_ticket*/
				TokenHolder.clearAuthorizerJsapiTicket();
				logger.info("缓存第三方平台令牌componentAccessToken="+componentAccessToken);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("componentAccessToken刷新出现异常");
		}
	}
}

