package com.citroen.wechat.api.servlet;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.citroen.ledp.util.LedpLogger;
import com.citroen.ledp.util.SpringContextUtil;
import com.citroen.wechat.api.model.Final;
import com.citroen.wechat.api.model.Message;
import com.citroen.wechat.api.security.WXBizMsgCrypt;
import com.citroen.wechat.api.service.ApiAccessToken;
import com.citroen.wechat.api.service.ApiReply;
import com.citroen.wechat.api.service.Response;
import com.citroen.wechat.service.ApiMessageService;
import com.citroen.wechat.util.ReplyXMLUtil;

public class MsgCallbackFilter implements Filter {
	private static Log logger = LogFactory.getLog(MsgCallbackFilter.class);
	public void destroy() {}
	public void init(FilterConfig fConfig) throws ServletException {}
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest)request;
		if(req.getRequestURI().endsWith("msgcallback")){
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
			try {
				ApiMessageService messageService = SpringContextUtil.getTypeBean("apiMessageService");
				//解析微信服务器推送过来的信息
				logger.info("解析微信服务器推送过来的信息");
				Message message = ReplyXMLUtil.resolve(req);
				//获取回复微信服务器的消息XML
				logger.info("获取回复微信服务器的消息XML");
				
				/******************全网发布腾讯测试公众号******************/
				if("wx570bc396a51b8ff8".equals(message.getAppid())){
					logger.info("全网发布腾讯测试公众号");
					String content = message.getContent();
					//模拟粉丝发送文本消息给专用测试公众号
					if(StringUtils.isNotBlank(content) &&content.startsWith("QUERY_AUTH_CODE")){
						logger.info("模拟粉丝发送文本消息给专用测试公众号");
						//在5秒内返回空串表明暂时不回复
						response.getWriter().write("");
						//然后再立即使用客服消息接口发送消息回复粉丝
						String authorization_code = content.substring(content.indexOf(":")+1);
						Response<?> res = ApiAccessToken.getAuthorizerToken(authorization_code);
						String access_token = res.get("authorizer_access_token");
						Message msg = new Message();
						msg.setMsgType("text");
						msg.setToUserName(message.getFromUserName());
						msg.setContent(authorization_code+"_from_api");
						ApiReply.reply(access_token,msg);
						logger.info(authorization_code+"_from_api");
						return;
					}
					//模拟粉丝触发专用测试公众号的事件，并推送事件消息到专用测试公众号
					if("event".equals(message.getMsgType())){
						content = message.getEvent()+"from_callback";
						logger.info("模拟粉丝触发专用测试公众号的事件，并推送事件消息到专用测试公众号"+content);
					}
					//模拟粉丝发送文本消息给专用测试公众号
					if("TESTCOMPONENT_MSG_TYPE_TEXT".equals(content)){
						content = "TESTCOMPONENT_MSG_TYPE_TEXT_callback";
						logger.info("模拟粉丝发送文本消息给专用测试公众号"+content);
					}
					long CreateTime = System.currentTimeMillis()/1000;
					StringBuilder xml = new StringBuilder();
					xml.append("<xml>");
					xml.append("<ToUserName><![CDATA[").append(message.getFromUserName()).append("]]></ToUserName>");
					xml.append("<FromUserName><![CDATA[").append(message.getToUserName()).append("]]></FromUserName>");
					xml.append("<CreateTime>").append(CreateTime).append("</CreateTime>");
					xml.append("<MsgType><![CDATA[text]]></MsgType>");
					xml.append("<Content><![CDATA["+content+"]]></Content>");
					xml.append("</xml>");
					WXBizMsgCrypt cxb = new WXBizMsgCrypt(Final.getValue(Final.COMPONENT_TOKEN),Final.getValue(Final.COMPONENT_ENCODING_AES_KEY),Final.getValue(Final.COMPONENT_APPID));
					String replyXML = cxb.encryptMsg(xml.toString(), "1434647494","123");
					response.getWriter().write(replyXML);
					return;
				}
				/******************全网发布腾讯测试公众号******************/
				
				String replyXML = messageService.getReplyXML(message);
				logger.info("replyXML="+replyXML);
				//保存微信服务器推送过来的信息
				if(StringUtils.isNotBlank(replyXML)){
					logger.info("保存微信服务器推送过来的已回复信息");
					//已回复
					messageService.save(message,1);
				}else{
					logger.info("保存微信服务器推送过来的未回复信息");
					//未回复
					messageService.save(message,0);
				}
				//取消或订阅粉丝
				logger.info("取消或订阅粉丝");
				messageService.subscribe(message);
				//回复消息到微信服务器
				logger.info("回复消息到微信服务器");
				response.getWriter().write(replyXML);
			} catch (Exception e) {
				e.printStackTrace();
				LedpLogger.error("解析微信推送信息",LedpLogger.Operation.create,LedpLogger.Result.failure,e.getMessage());
				response.getWriter().write("");
			}
			return;
		}
		chain.doFilter(request, response);
	}

}
