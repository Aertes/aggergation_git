package com.citroen.wechat.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.citroen.ledp.exception.LedpException;
import com.citroen.ledp.service.PermissionService;
import com.citroen.wechat.domain.AutoReply;
import com.citroen.wechat.domain.PublicNo;
import com.citroen.wechat.service.AutoReplyService;
import com.citroen.wechat.util.ConstantUtil;

/**
 * 自动回复管理
 * @author 何海粟
 * @date2015年6月4日
 */
@Controller("autoReplyController")
@RequestMapping("/wechat/autoreply")
public class AutoReplyController {
	private Map<String, Object> params;
	@Autowired
	private PermissionService permissionService; 
	
	@Autowired
	private AutoReplyService autoReplyService;
	
	@RequestMapping(value={"","index"})
	public String index(Model model,HttpServletRequest request,HttpServletResponse response,HttpSession session) throws Exception {
		ModelAndView view = new ModelAndView();
		view.setViewName("wechat/autoreply/index");
		// 公众号获取
		PublicNo publicNo = (PublicNo) request.getSession().getAttribute(ConstantUtil.SESSION_CURRENT_PUBLICNO);
		if(publicNo==null){
			return "redirect:/wechat/publicno/message";
		}
		String type = request.getParameter("type");
		type = StringUtils.isBlank(type)?"subscribe":type;
		AutoReply autoReply = autoReplyService.find(type,publicNo.getId());
		request.setAttribute("autoReply",autoReply);
		// 返回到界面
		return "wechat/autoreply/"+type;
	}

	@RequestMapping("subscribe")
	public String subscribe(@ModelAttribute("autoReply") AutoReply autoReply,Model model,HttpServletRequest request,HttpSession session) {
		// 公众号获取
		PublicNo publicNo = (PublicNo) request.getSession().getAttribute(ConstantUtil.SESSION_CURRENT_PUBLICNO);
		if(publicNo==null){
			return "redirect:/wechat/publicno/message";
		}
		model.addAttribute("autoReply",autoReply);
		if(autoReply.getStatus()==1){
			if("news".equals(autoReply.getMsgType()) && autoReply.getMaterialId()==null){
				model.addAttribute("message","订阅自动回复图文信息不能为空");	
				return "wechat/autoreply/subscribe";
			}
			if("text".equals(autoReply.getMsgType()) && StringUtils.isBlank(autoReply.getContent())){
				model.addAttribute("message","订阅自动回复文字信息不能为空");	
				return "wechat/autoreply/subscribe";
			}
			if(StringUtils.isNotBlank(autoReply.getContent()) && autoReply.getContent().length()>600){
				model.addAttribute("message","订阅自动回复文字信息不能超过600字符");	
				return "wechat/autoreply/subscribe";
			}
		}
		try {
			autoReplyService.saveOrUpdate(autoReply,publicNo.getId());
			model.addAttribute("message","订阅自动回复保存成功！");	
		} catch (LedpException e) {
			model.addAttribute("message","订阅自动回复保存失败！");	
		}
		return "wechat/autoreply/subscribe";
	}

	@RequestMapping("message")
	public String message(@ModelAttribute("autoReply") AutoReply autoReply,Model model,HttpServletRequest request,HttpSession session) {
		// 公众号获取
		PublicNo publicNo = (PublicNo) request.getSession().getAttribute(ConstantUtil.SESSION_CURRENT_PUBLICNO);
		if(publicNo==null){
			return "redirect:/wechat/publicno/message";
		}
		model.addAttribute("autoReply",autoReply);
		if(autoReply.getStatus()==1){
			if("news".equals(autoReply.getMsgType()) && autoReply.getMaterialId()==null){
				model.addAttribute("message","消息自动回复图文信息不能为空");	
				return "wechat/autoreply/message";
			}
			if("text".equals(autoReply.getMsgType()) && StringUtils.isBlank(autoReply.getContent())){
				model.addAttribute("message","消息自动回复文字信息不能为空");	
				return "wechat/autoreply/message";
			}
			if(StringUtils.isNotBlank(autoReply.getContent()) && autoReply.getContent().length()>600){
				model.addAttribute("message","消息自动回复文字信息不能超过600字符");	
				return "wechat/autoreply/message";
			}
		}
		try {
			autoReplyService.saveOrUpdate(autoReply,publicNo.getId());
			model.addAttribute("message","消息自动回复保存成功！");	
		} catch (LedpException e) {
			model.addAttribute("message","消息自动回复保存失败！");	
		}
		return "wechat/autoreply/message";
	}
	
}
