package com.citroen.wechat.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.citroen.ledp.interceptor.Permission;
import com.citroen.ledp.service.PermissionService;
import com.citroen.ledp.util.PropertiyUtil;
import com.citroen.wechat.domain.AnswerConfig;
import com.citroen.wechat.domain.Awards;
import com.citroen.wechat.domain.CouponConfig;
import com.citroen.wechat.domain.Plugin;
import com.citroen.wechat.domain.PluginBaseConfig;
import com.citroen.wechat.domain.PluginType;
import com.citroen.wechat.domain.PublicNo;
import com.citroen.wechat.domain.RotaryTableConfig;
import com.citroen.wechat.domain.ShareConfig;
import com.citroen.wechat.domain.SitePage;
import com.citroen.wechat.exception.WechatException;
import com.citroen.wechat.service.PluginService;
import com.citroen.wechat.service.SitePageService;
import com.citroen.wechat.service.SiteService;
import com.citroen.wechat.util.ConstantUtil;
import com.citroen.wechat.util.JsonUtil;
import com.citroen.wechat.util.Pagination;

/**
 * 插件管理
 * @author 何海粟
 * @date2015年6月4日
 */
@Controller("pluginController")
@RequestMapping("/wechat/plugin")
public class PluginController {
	
	private String dateFormat = "yyyy-MM-dd HH:mm:ss";
	
	private Map<String,Object> params;
	
	@Autowired
	private PermissionService permissionService;
	@Autowired
	private PluginService pluginService;
	@Autowired
	private SiteService siteService;
	@Autowired
	private SitePageService sitePageService;
	
	
	@RequestMapping("index")
	@Permission(code="wechat/plugin/index")
	public ModelAndView index(Model model,HttpServletRequest request,HttpServletResponse response) throws Exception {
		PublicNo publicNo = (PublicNo) request.getSession().getAttribute(ConstantUtil.SESSION_CURRENT_PUBLICNO);
		if(publicNo==null){
			return new ModelAndView("redirect:/wechat/publicno/message");
		}
		
		ModelAndView view = new ModelAndView();
		view.setViewName("wechat/plugin/index");
		List<PluginType> pluginTypes = pluginService.getPlugin(null);
		model.addAttribute("types", pluginTypes);
		return view;
	}
	
	/**
	 * 获取插件列表
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value={"","getPluginList"})
	@Permission(code="wechat/plugin/index")
	@ResponseBody
	public void getPluginList(Model model, HttpServletRequest request,HttpServletResponse response) throws Exception {
		response.setContentType("text/html; charset=utf-8");
		Map<String,Object> params = new HashMap<String,Object>();
		Map<String,Integer> paginateParams = new HashMap<String,Integer>();
		int pageSize   = 10;
		int currentPage = 1;
		//文件保存目录路径
		PropertiyUtil propUtil = new PropertiyUtil("/wechat.properties");
		String pluginUrl = propUtil.getString("plugin.thumb.url");
		
		String type = request.getParameter("parameter[type]");
		params.put("type", type);
		
		try {
			currentPage = Integer.parseInt(request.getParameter("currentPage"));
			pageSize = Integer.parseInt(request.getParameter("pageSize"));
		} catch (Exception e) {
		}
		
		paginateParams.put("offset", (currentPage-1)*pageSize);
		paginateParams.put("max", pageSize);
		
		List<Plugin> rows = pluginService.executeQuery(params,paginateParams);
		
		Pagination pagination = new Pagination(pluginService.getTotalRow(params),pageSize,currentPage);
		
		Map<String,Object> json = new HashMap<String,Object>();
		json.put("paginationData", pagination);
		json.put("data", rows);
		model.addAttribute("pluginUrl", pluginUrl);
		response.getWriter().print(JsonUtil.toJSON(json));
	}
	
	/**
	 * 查看插件详情
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("detail/{id}")
	@Permission(code="wechat/plugin/index")
	public String detial(@PathVariable long id ,Model model,HttpServletRequest request) throws Exception {
		//文件保存目录路径
		PropertiyUtil propUtil = new PropertiyUtil("/wechat.properties");
		String pluginUrl = propUtil.getString("plugin.thumb.url");
		Plugin plugin = pluginService.get(id);
		model.addAttribute("plugin", plugin);
		model.addAttribute("pluginUrl", pluginUrl);
		return "wechat/plugin/detail";
	}
	
	
	
	@RequestMapping(value={"","search"})
	@Permission(code="wechat/plugin/index")
	@ResponseBody
	public void search(HttpServletRequest request,HttpServletResponse response) throws Exception {
		response.setContentType("text/html; charset=utf-8");
		List<PluginType> pluginTypes = pluginService.getPlugin(null);
		response.getWriter().print(JsonUtil.toJSON(pluginTypes));
	}
	
	@RequestMapping("init")
	@Permission(code="wechat/plugin/index")
	@ResponseBody
	public void init(HttpServletRequest request,HttpServletResponse response) throws Exception {
		response.setContentType("text/html; charset=utf-8");
		String code = request.getParameter("code");
		List<Map> base = new ArrayList();
		Map params = new HashMap();
		Map json = new HashMap();
		try {
			List<Map> maps = pluginService.getBaseConfig(code);
			params.put("params", maps);
			base.add(params);
			json.put("base", base);
		} catch (Exception e) {
			e.printStackTrace();
			throw new WechatException("查询出错");
		}
		response.getWriter().print(JsonUtil.toJSON(dateFormat,json));
	}
	
	
	/**
	 * 获取配置参数
	 * @param model
	 * @param request
	 * @param response
	 * @param session
	 * @throws Exception
	 */
	@RequestMapping("getConfig")
	@ResponseBody
	public void getConfig(HttpServletRequest request,HttpServletResponse response) throws Exception{
		response.setContentType("text/html; charset=utf-8");
		Map<String, Object> params = new HashMap<String,Object>();
		Map<String,Object> json = new HashMap<String, Object>();
		String pluginIdStr = request.getParameter("pluginId");
		String pageId = request.getParameter("pageId");
		String pluginUnion = request.getParameter("pluginUnion");
		String pluginCode = request.getParameter("code");
		Plugin plugin = null;
		long pluginId = 0l;
		if(StringUtils.isNotBlank(pluginIdStr)){
			params.put("pluginId", pluginIdStr);
		}
		if(StringUtils.isNotBlank(pageId)){
			params.put("pageId", pageId);
		}
		if(StringUtils.isNotBlank(pluginUnion)){
			params.put("pluginUnion", pluginUnion);
		}
		
		List<PluginBaseConfig> baseConfigs = pluginService
				.getBaseConfig(params);
		json.put("base", baseConfigs);
		
		try {
			pluginId = Long.valueOf(pluginIdStr);
			plugin = pluginService.get(pluginId);
			if(plugin == null){
				response.getWriter().print("");
				return;
			}
		} catch (Exception e) {
			plugin = pluginService.getByCode(pluginCode);
		}
		
		try {
			switch (getNameByCode(plugin.getCode())) {
			case 0:
				break;
			case 1:break;
			case 2:
				//大转盘
				RotaryTableConfig rotaryTable = pluginService
						.getRotaryTableConfig(params);
				json.put("advance", rotaryTable);
				json.put("type", "rotatyTable");
				break;
			case 3:
				//分享有礼
				ShareConfig share = pluginService.getShareConfig(params);
				json.put("advance", share);
				json.put("type", "share");
				break;
			case 4:
				//答题闯关
				AnswerConfig answer = pluginService.getAnswerConfig(params);
				json.put("advance", answer);
				json.put("type", "answer");
				break;
			/*case 5:
				//惠团购在基本元素中处理
				break;*/
			case 6:
				//优惠券
				CouponConfig coupon = pluginService.getCouponConfig(params);
				json.put("advance", coupon);
				json.put("type", "coupon");
				break;
			}
		} catch (Exception e) {
			response.getWriter().print("");
		}
		response.getWriter().print(JsonUtil.toJSON(dateFormat,json));
		
	}
	
	
	/**
	 * 保存基本配置参数
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("saveBaseConfig")
	@ResponseBody
	public void saveBaseConfig(HttpServletRequest request,HttpServletResponse response) throws Exception{
		response.setContentType("text/html; charset=utf-8");
		PluginBaseConfig entity = new PluginBaseConfig();
		if(params.isEmpty() || params.get("data")== null){
			response.getWriter().print("no");
			return;
		}
		String str = (String)params.get("data");
		
		try {
			entity = (PluginBaseConfig) JSONObject.parseObject(str,
					PluginBaseConfig.class);
			if(entity.getPluginId() > 8){
				str = new String(str.getBytes("ISO-8859-1"),"UTF-8" );
				entity = (PluginBaseConfig) JSONObject.parseObject(str,
						PluginBaseConfig.class);
			}
			if(entity != null && StringUtils.isNotBlank(entity.getCode())){
				Plugin plugin = pluginService.getByCode(entity.getCode());
				if(plugin != null){
					entity.setPluginId(plugin.getId());
				}
			}
			pluginService.saveBaseConfig(entity);
			//如果是导航，则更新所有页面导航配置信息
			if("nav".equals(entity.getCode())){
				SitePage page = sitePageService.siteInstance(entity.getId());
				if(page != null && page.getSite() != null){
					List<SitePage> pages = sitePageService.getSitePages(page.getSite().getId(),page.getId());
					if(CollectionUtils.isNotEmpty(pages)){
						for(SitePage navPage : pages){
							entity.setPageId(navPage.getId());
							pluginService.saveBaseConfig(entity);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.getWriter().print("no");
			return;
		}
		response.getWriter().print("yes");
	}
	
	/**
	 * 删除配置参数(包括基本及高级)
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("deleteConfig")
	@ResponseBody
	public void deleteBaseConfig(HttpServletRequest request,HttpServletResponse response) throws Exception{
		response.setContentType("text/html; charset=utf-8");
		Map<String, Object> params = new HashMap<String,Object>();
		String pluginId = request.getParameter("pluginId");
		String pageId = request.getParameter("pageId");
		String pluginUnion = request.getParameter("pluginUnion");
		if(StringUtils.isNotBlank(pluginId)){
			params.put("pluginId", pluginId);
		}
		if(StringUtils.isNotBlank(pageId)){
			params.put("pageId", pageId);
		}
		if(StringUtils.isNotBlank(pluginUnion)){
			params.put("pluginUnion", pluginUnion);
		}
		//删除基本配置参数
		pluginService.deleteBaseConfig(params);
		//删除高级配置参数
		Plugin plugin = pluginService.get(Long.valueOf(pluginId));
		if(plugin == null){
			response.getWriter().print("no");
			return;
		}
		switch(getNameByCode(plugin.getCode())){
		case 0:
			response.getWriter().print("");
			return;
		case 1:
			break;
		case 2:
			//大转盘
			pluginService.deleteRotaryTable(params);
		case 3:
			//分享有礼
			pluginService.deleteShare(params);
			break;
		case 4:
			//答题闯关
			pluginService.deleteAnswer(params);
			break;
		/*case 5:
			//惠团购在基本元素中处理
			break;*/
		case 6:
			//优惠券
			pluginService.deleteCoupon(params);
			break;
		}
		response.getWriter().print("yes");
	}
	
	/**
	 * 保存高级配置参数
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("saveAdvancedConfig")
	@ResponseBody
	public void saveAdvancedConfig(HttpServletRequest request,HttpServletResponse response) throws Exception{
		response.setContentType("text/html; charset=utf-8");
		JSONObject json = null;
		if(params.isEmpty() || params.get("data")== null){
			response.getWriter().print("no");
			return;
		}
		String str = (String)params.get("data");
		if(StringUtils.isBlank(str)){
			response.getWriter().print("no");
			return;
		}
		int pluginId = 0;
		try {
			json = JSONObject.parseObject(str);
			pluginId = Integer.valueOf(json.getString("pluginId"));
		} catch (Exception e) {
			response.getWriter().print("no");
			return;
		}
		
		Plugin plugin = pluginService.get(pluginId);
		if(plugin == null){
			response.getWriter().print("no");
			return;
		}
		try {
			switch (getNameByCode(plugin.getCode())) {
			case 0:
				response.getWriter().print("");
				break;
			case 1:
				break;
			case 2:
				//大转盘
				RotaryTableConfig config = new RotaryTableConfig();
				config = (RotaryTableConfig) JSONObject.parseObject(str,
						RotaryTableConfig.class);
				pluginService.saveRotaryTable(config);
				break;
			case 3:
				//分享有礼
				ShareConfig share = new ShareConfig();
				share = (ShareConfig) JSONObject.parseObject(str,
						ShareConfig.class);
				pluginService.saveShare(share);
				break;
			case 4:
				//答题闯关
				AnswerConfig answer = new AnswerConfig();
				answer = (AnswerConfig) JSONObject.parseObject(str,
						AnswerConfig.class);
				//获取通关奖品
				String prizeName = json.getString("prize.name");
				if(StringUtils.isNotBlank(prizeName)){
					Awards prize = new Awards();
					prize.setName(prizeName);
					answer.setPrize(prize);
				}
				pluginService.saveAnswer(answer);
				break;
			/*case 5:
				//惠团购在基本元素中处理
				break;*/
			case 6:
				//优惠券
				CouponConfig coupon = new CouponConfig();
				coupon = (CouponConfig) JSONObject.parseObject(str,
						CouponConfig.class);
				pluginService.saveCoupon(coupon);
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.getWriter().print("no");
			return;
		}
		response.getWriter().print("yes");
	}
	
	
	
	private int getNameByCode(String code){
		if(StringUtils.isBlank(code)){
			return 0;
		}else if("carousel".equals(code) || "button".equals(code) 
				|| "image".equals(code) || "imgText".equals(code) 
				|| "nav".equals(code)|| "table".equals(code) 
				|| "title".equals(code) || "text".equals(code)
				|| "purchase".equals(code)){
			//基本元素插件
			return 1;
		}else if("rotatyTable".equals(code)){
			//大转盘
			return 2;
		}else if("share".equals(code)){
			//分享有礼
			return 3;
		}else if("answer".equals(code)){
			//答题闯关
			return 4;
		}else if("coupon".equals(code)){
			//优惠券
			return 6;
		}
		return 0;
	}
	
}
