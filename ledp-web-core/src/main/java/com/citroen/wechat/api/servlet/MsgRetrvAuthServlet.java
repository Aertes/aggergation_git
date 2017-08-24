package com.citroen.wechat.api.servlet;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.citroen.ledp.util.SpringContextUtil;
import com.citroen.wechat.api.token.ComponentAccessToken;
import com.citroen.wechat.service.PublicNoService;

public class MsgRetrvAuthServlet extends HttpServlet{
	private static Log logger = LogFactory.getLog(ComponentAccessToken.class);
	private static final long serialVersionUID = 1L;
	
	//公众号授权后，微信服务器回调
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException {
		//保存公众号信息、绑定公众号到机构或网点、初始化公众号信息
		PublicNoService publicNoService = SpringContextUtil.getTypeBean("publicNoService");
		String message = "公众号授权成功！";
		try {
			publicNoService.init(req);
		} catch (Exception e) {
			message = e.getMessage();
		}
		req.getSession().setAttribute("msgretrvauth_message",message);
		resp.sendRedirect(req.getContextPath()+"/wechat/publicno/index");
		
	}
	
	//微信服务器每十分钟推送一次
	protected void doPost(HttpServletRequest req, HttpServletResponse response)throws ServletException, IOException {
		//刷新component_verify_ticket参数
		new ComponentAccessToken().refreshComponentVerifyTicket(req);
		//会写结果给微信服务器
		response.getWriter().print("success");
	}
}
