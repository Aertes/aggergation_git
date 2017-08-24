package com.citroen.wechat.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.citroen.ledp.service.PermissionService;

/**
 * 群发管理
 * @author 何海粟
 * @date2015年6月4日
 */
@Controller("wechatMassController")
@RequestMapping("/wechat/mass")
public class MassController {
	
	private Map<String, Object> params;
	
	@Autowired
	private PermissionService permissionService;
	
	@RequestMapping(value={"","index"})
	public ModelAndView index(Model model,HttpServletRequest request,HttpServletResponse response,HttpSession session) throws Exception {
		ModelAndView view = new ModelAndView();
		view.setViewName("wechat/mass/index");
		/*if(permissionService.hasAuth("news/auth")){
			model.addAttribute("permission", "permission");
		}*/
		String home="";
		try{
			home=request.getParameter("home");
			if(home!=null){
				model.addAttribute("home", home);
			}
		}catch(Exception e){
			
		}
		// 返回到界面
		return view;
	}

}
