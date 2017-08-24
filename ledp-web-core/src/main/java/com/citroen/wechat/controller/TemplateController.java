package com.citroen.wechat.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.citroen.ledp.interceptor.Permission;
import com.citroen.ledp.service.PermissionService;
import com.citroen.ledp.util.PropertiyUtil;
import com.citroen.wechat.domain.PublicNo;
import com.citroen.wechat.domain.Template;
import com.citroen.wechat.service.TemplateService;
import com.citroen.wechat.util.ConstantUtil;

/**
 * 模板管理
 * @author 何海粟
 * @date2015年6月4日
 */
@Controller("templateController")
@RequestMapping("/wechat/template")
public class TemplateController {
	@Autowired
	private PermissionService permissionService;
	@Autowired
	private TemplateService templateService;
	
	private Map<String,Object> params;
	
	@RequestMapping(value={"","index"})
	@Permission(code="wechat/template/index")
	public ModelAndView index(Model model,HttpServletRequest request,HttpServletResponse response,HttpSession session) throws Exception {
		PublicNo publicNo = (PublicNo) request.getSession().getAttribute(ConstantUtil.SESSION_CURRENT_PUBLICNO);
		if(publicNo==null){
			return new ModelAndView("redirect:/wechat/publicno/message");
		}
		
		ModelAndView view = new ModelAndView();
		view.setViewName("wechat/template/index");
		
		//文件保存目录路径
		PropertiyUtil propUtil = new PropertiyUtil("/wechat.properties");
		String templateUrl = propUtil.getString("template.url");
		List<Template> templates = templateService.getTemplates();
		model.addAttribute("templates", templates);
		model.addAttribute("templateUrl", templateUrl);
		// 返回到界面
		return view;
	}
	
	/**
	 * 查询
	 * 
	 * @param model
	 * @param request
	 * @return
	 * @throws Exception
	 *//*
	@RequestMapping(value = {"search"})
	@ResponseBody
	public void search(Model model, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		int pageSize   = 10;
		int currentPage = 1;
		Map<String,Integer> paginateParams = new HashMap<String,Integer>();
		Map<String, Object> params = new HashMap<String,Object>();
		try {
			currentPage = Integer.parseInt(request.getParameter("currentPage"));
		} catch (Exception e) {
		}
		try {
			pageSize = Integer.parseInt(request.getParameter("pageSize"));
		} catch (Exception e) {
		}
		
		paginateParams.put("offset", (currentPage-1)*pageSize);
		paginateParams.put("max", pageSize);
		List<Template> templates = templateService.getTemplates();
		
		Pagination pagination = new Pagination(templates.size(),pageSize,currentPage);
		
		Map<String,Object> json = new HashMap<String,Object>();
		json.put("paginationData", pagination);
		json.put("data", templates);
        response.setContentType("text/html; charset=utf-8");
		response.getWriter().print(JsonUtil.toJSON(json));
	}*/

}
