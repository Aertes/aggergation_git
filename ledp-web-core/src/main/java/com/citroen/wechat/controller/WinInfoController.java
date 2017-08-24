package com.citroen.wechat.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.citroen.wechat.form.WinInfoForm;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.citroen.ledp.domain.Dealer;
import com.citroen.ledp.domain.Organization;
import com.citroen.ledp.domain.User;
import com.citroen.ledp.exception.LedpException;
import com.citroen.ledp.service.OrganizationService;
import com.citroen.wechat.domain.WinInfo;
import com.citroen.wechat.service.IWinInfoService;
import com.citroen.wechat.util.JsonUtil;
import com.citroen.wechat.util.Pagination;

/**
 * 中奖信息
 * @author 何海粟
 * @date2015年11月10日
 */
@Controller("winInfoController")
@RequestMapping("/wechat/wininfo")
public class WinInfoController {

	private static Log logger = LogFactory.getLog(WinInfoController.class);
	
	private static final String DATEFORMAT = "yyyy-MM-dd HH:mm";
	
	private Map<String,Object> params;
	@Autowired
	private IWinInfoService winInfoService;
	@Autowired
	private OrganizationService organizationService;
	
	@RequestMapping("index")
	public String index(Model model,HttpServletRequest request,HttpServletResponse response) {
		//获取用户
		User loginUser = (User) request.getSession().getAttribute("loginUser");
		//获取组织机构
		Organization org = loginUser.getOrg();
		//网点
		Dealer dealer = loginUser.getDealer();
		
		if(org != null && org.getLevel() == 1){//总部
			model.addAttribute("type",1);
			try {
				model.addAttribute("orgs",organizationService.getChildren(1L));
			} catch (LedpException e) {
				logger.error(e.getMessage());
			}
		}else if(org != null && org.getLevel() == 2){//大区
			model.addAttribute("type",2);
			model.addAttribute("orgs",org);
		}else{//网点
			model.addAttribute("type",3);
			model.addAttribute("dealer",dealer);
			model.addAttribute("orgs",org);
		}
		// 返回到界面
		return "wechat/wininfo/index";
	}
	
	@RequestMapping("search")
	public void search(Model model,HttpServletResponse response,WinInfoForm form) {
		Map<String, Object> json = new HashMap<String, Object>();
		
		if(StringUtils.isNotBlank(form.getName())) {
			try {
				form.setName(new String(form.getName().getBytes("ISO-8859-1"),"UTF-8"));
			} catch (UnsupportedEncodingException e) {
				logger.error(e.getMessage());
			}
		}
		int currentPage = form.getCurrentPage();
		
		form.setCurrentPage(currentPage>0?(currentPage-1)*form.getPageSize():0);
		
		int total = winInfoService.getWinInfoCount(form);
		List<WinInfo> rows = winInfoService.getWinInfos(form);
		Pagination pagination = new Pagination(total,form.getPageSize(),currentPage);
		json.put("paginationData", pagination);
		json.put("data", rows);
		response.setContentType("text/html; charset=utf-8");
		try {
			response.getWriter().print(JsonUtil.toJSON(DATEFORMAT, json));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@RequestMapping("edit")
	@ResponseBody
	public Map<String, Object> edit(Model model,long id) {
		Map<String, Object> json = new HashMap<String, Object>();
		if(winInfoService.get(id) != null){
			winInfoService.updateStatus(id);
			json.put("status", 200);
		}else{
			json.put("status", 201);
			json.put("errMsg", "领取失败,数据不存在");
		}
		return json;
	}
	
}