package com.citroen.wechat.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.citroen.ledp.domain.User;
import com.citroen.ledp.exception.LedpException;
import com.citroen.ledp.interceptor.Permission;
import com.citroen.ledp.service.PermissionService;
import com.citroen.wechat.api.model.Menu;
import com.citroen.wechat.api.service.ApiMenu;
import com.citroen.wechat.api.service.Response;
import com.citroen.wechat.api.token.TokenHolder;
import com.citroen.wechat.domain.PublicNo;
import com.citroen.wechat.domain.WechatMenu;
import com.citroen.wechat.exception.WechatException;
import com.citroen.wechat.service.WechatMenuService;
import com.citroen.wechat.util.ConstantUtil;
import com.citroen.wechat.util.JsonUtil;

/**
 * 菜单管理
 * @author 何海粟
 * @date2015年6月8日
 */
@Controller("wechatMenuController")
@RequestMapping("/wechat/menu")
public class WechatMenuController {
	
	private static Log logger = LogFactory.getLog(WinInfoController.class);
	
	private static final String CAMPAIGN = "活动(网点自定义)";
	
	@Autowired
	private PermissionService permissionService;
	@Autowired
	private WechatMenuService wechatMenuService;
	
	private Map<String,Object> params;
	
	@RequestMapping(value={"","index"})
	@Permission(code="wechat/menu/index")
	public ModelAndView index(Model model,HttpServletRequest request,HttpServletResponse response,HttpSession session) {
		ModelAndView view = new ModelAndView();
		view.setViewName("wechat/menu/index");
		PublicNo publicNo = (PublicNo) session.getAttribute(ConstantUtil.SESSION_CURRENT_PUBLICNO);
		if(publicNo==null){
			return new ModelAndView("redirect:/wechat/publicno/message");
		}
		if(session != null){
			User user = (User) session.getAttribute("loginUser");
			model.addAttribute("curOrg", user.getOrg()==null?0:1);
			model.addAttribute("curDealer", user.getDealer()==null?0:1);
		}
		//点击新建素材后跳转回来的
		String materialId = request.getParameter("materialId");
		String menuid = request.getParameter("menuid");
        if(null != materialId && materialId.matches("^\\d+$")) {
            model.addAttribute("materialId",materialId);
        }
        if(null != menuid && menuid.matches("^\\d+$")) {
            model.addAttribute("menuid",menuid);
        }
		model.addAttribute("publicNoId",publicNo.getId()); // 在页面校验是否存在公众号
		model.addAttribute("publicNo_Verify",publicNo.getVerify_type_info()); // 认证信息
		// 返回到界面
		return view;
	}
	
	@RequestMapping("search")
	@Permission(code="wechat/menu/index")
	public void search(Model model,HttpServletRequest request,HttpServletResponse response,HttpSession session) throws Exception {
		int pageSize   = 10;
		int currentPage = 1;
		
		String materialId = request.getParameter("materialId");
		String menuid = request.getParameter("menuid");
		if(StringUtils.isNotBlank(materialId)){
			model.addAttribute("materialId", materialId);
		}
		if(StringUtils.isNotBlank(menuid)){
			model.addAttribute("menuid", menuid);
		}
		Map<String,Integer> paginateParams = new HashMap<String,Integer>();
		PublicNo publicNo = (PublicNo) request.getSession().getAttribute(ConstantUtil.SESSION_CURRENT_PUBLICNO);
		if(publicNo == null){
			throw new WechatException("请选择公众号");
		}
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
		
		List<WechatMenu> rows = wechatMenuService.getMenus(null,publicNo.getId());
		
		Map<String,Object> json = new HashMap<String,Object>();
		json.put("data", rows);
        response.setContentType("text/html; charset=utf-8");
		response.getWriter().print(JsonUtil.toJSON(json));
	}
	
	@RequestMapping(value = "/delete/{id}")
	@Permission(code="wechat/menu/delete")
	public void delete(Model model, @PathVariable long id,HttpServletResponse response) throws Exception {
		response.setContentType("text/html; charset=utf-8");
		try {
			wechatMenuService.delete(id);
		} catch (WechatException e) {
			logger.error(e.getMessage());
			response.getWriter().print(JsonUtil.toJSON(getFailure("删除失败,原因:"+e.getMessage())));
		}
		response.getWriter().print(JsonUtil.toJSON(getSuccess("删除成功")));
	}
	
	@RequestMapping(value = "/saveOrUpdate")
	@Permission(code="wechat/menu/create")
	public void saveOrUpdate(Model model, WechatMenu menu,HttpServletRequest request,HttpServletResponse response) throws Exception {
		response.setContentType("text/html; charset=utf-8");
		PublicNo publicNo = (PublicNo) request.getSession().getAttribute(ConstantUtil.SESSION_CURRENT_PUBLICNO);
		if(publicNo == null){
			throw new WechatException("请选择公众号");
		}
		try {
			//菜单验证
			validate(menu, publicNo.getId());
		} catch (Exception e) {
			response.getWriter().print(JsonUtil.toJSON(getFailure(e.getMessage())));
			return;
		}
		
		try {
			menu.setPublicNo(publicNo);
			long id = wechatMenuService.saveOrUpdate(menu);
			Map json = getSuccess("保存成功");
			json.put("id", id);
			response.getWriter().print(JsonUtil.toJSON(json));
		} catch (WechatException e) {
			response.getWriter().print(JsonUtil.toJSON(getFailure(e.getMessage())));
		}
	}
	
	@RequestMapping(value = "/reset")
	@Permission(code="wechat/menu/index")
	public void reset(Model model, long id,HttpServletRequest request,HttpServletResponse response) throws Exception {
		response.setContentType("text/html; charset=utf-8");
		PublicNo publicNo = (PublicNo) request.getSession().getAttribute(ConstantUtil.SESSION_CURRENT_PUBLICNO);
		if(publicNo == null){
			throw new WechatException("请选择公众号");
		}
		try {
			WechatMenu menu = new WechatMenu(id);
			menu.setPublicNo(publicNo);
			wechatMenuService.reset(menu);
			response.getWriter().print(JsonUtil.toJSON(getSuccess("重置成功")));
		} catch (WechatException e) {
			response.getWriter().print(JsonUtil.toJSON(getFailure(e.getMessage())));
		}
	}
	
	
	@RequestMapping(value={"preview"})
	public ModelAndView preview(Model model,HttpServletRequest request,HttpServletResponse response,HttpSession session) throws Exception {
		ModelAndView view = new ModelAndView();
		view.setViewName("wechat/menu/preview");
		
		// 返回到界面
		return view;
	}
	
	@RequestMapping(value = "/previewData")
	public void previewData(Model model, WechatMenu menu,HttpServletRequest request,HttpServletResponse response) throws Exception {
		response.setContentType("text/html; charset=utf-8");
		PublicNo publicNo = (PublicNo) request.getSession().getAttribute(ConstantUtil.SESSION_CURRENT_PUBLICNO);
		if(publicNo == null){
			throw new WechatException("请选择公众号");
		}
		List<WechatMenu> rows = wechatMenuService.getMenus(null,publicNo.getId());
		response.getWriter().print(JsonUtil.toJSON(getPreviewList(rows)));
	}
	
	/**
	 * 获取本列最大排序
	 * @param model
	 * @param menu
	 * @param request
	 * @param response
	 * @throws WechatException 
	 * @throws LedpException 
	 */
	@RequestMapping(value = "/getMaxSort")
	public void getMaxSort(Model model, int col,HttpServletRequest request,HttpServletResponse response) throws Exception{
		response.setContentType("text/html; charset=utf-8");
		PublicNo publicNo = (PublicNo) request.getSession().getAttribute(ConstantUtil.SESSION_CURRENT_PUBLICNO);
		if(publicNo == null){
			throw new WechatException("请选择公众号");
		}
		int sort = wechatMenuService.getMaxSort(col, publicNo.getId());
		response.getWriter().print(JsonUtil.toJSON(sort));
	}
	

	/**
	 * 发布
	 * @param model
	 * @param menu
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "/publish")
	@Permission(code="wechat/menu/publish")
	public void publish(Model model, WechatMenu menu,HttpServletRequest request,HttpServletResponse response) throws Exception {
		response.setContentType("text/html; charset=utf-8");
		try {
			PublicNo publicNo = (PublicNo) request.getSession().getAttribute(ConstantUtil.SESSION_CURRENT_PUBLICNO);
			if(publicNo == null){
				response.getWriter().print(JsonUtil.toJSON(getFailure("发布失败,未选择当前操作的公众号")));
				return;
			}
			String access_token = TokenHolder.getAuthorizerAccessToken(publicNo.getAppid(), publicNo.getAuthorizer_refresh_token());
			//调用微信接口
			List<WechatMenu> wechatMenus = wechatMenuService.getPublishMenus(null,publicNo.getId());
			List<Menu> menus = getMenus(wechatMenus);
			Response rep = ApiMenu.update(access_token, menus);
			if(Response.SUCCESS == rep.getStatus()){
				response.getWriter().print(JsonUtil.toJSON(getSuccess("发布成功")));
			}else{
				response.getWriter().print(JsonUtil.toJSON(getFailure("发布失败,原因:"+rep.getMessage())));
			}
			
		} catch (Exception e) {
			response.getWriter().print(JsonUtil.toJSON(getFailure(e.getMessage())));
		}
	}
	
	
	
	
	private void validate(WechatMenu menu,long publicNoId) throws Exception {
		if(menu != null && menu.getCol() > 0){
			int countParent = wechatMenuService.countParentByCol(menu.getCol(),publicNoId);
			//新增根菜单
			if(countParent > 0 && menu.getParent() == null){
				throw new WechatException("新增失败,超出菜单个数限制");
			}
			int countChildren = wechatMenuService.countSubMenuByCol(menu.getCol(),publicNoId);
			if((countChildren >= 5 && menu.getParent() != null)){
				throw new RuntimeException("新增失败,超出菜单个数限制");
			}
		}
	}
	

	private List<Menu> getMenus(List<WechatMenu> wechatMenus) {
		List<Menu> menus = new ArrayList<Menu>();
		if(CollectionUtils.isNotEmpty(wechatMenus)){
			for(WechatMenu wechatMenu : wechatMenus){
				if(CAMPAIGN.equals(wechatMenu.getName()) && StringUtils.isBlank(wechatMenu.getEvent())){
					continue;
				}
				Menu menu = new Menu();
				menu.setKey(wechatMenu.getSn());
				if(wechatMenu.getMaterial() != null){
					menu.setMediaId(wechatMenu.getMaterial().getMediaId());	
				}
				menu.setName(wechatMenu.getName());
				menu.setType(wechatMenu.getEvent());
				menu.setUrl(wechatMenu.getUrl());
				menu.setChildren(getMenus(wechatMenu.getChildren()));
				menus.add(menu);
				
			}
		}
		return menus;
	}
	
	private List<Map> getPreviewList(List<WechatMenu> rows) {
		List<Map> list = new ArrayList<Map>();
		if(CollectionUtils.isNotEmpty(rows)){
			for(WechatMenu menu : rows){
				if(CAMPAIGN.equals(menu.getName()) && StringUtils.isBlank(menu.getEvent())){
					continue;
				}
				Map map = new HashMap();
				map.put("id", menu.getId());
				map.put("type", menu.getEvent());
				map.put("name", menu.getName());
				map.put("col", menu.getCol());
				if("view".equals(menu.getEvent())){
					map.put("urlOrContent", menu.getUrl());
				}else if("click".equals(menu.getEvent())){
					if(menu.getMaterial() != null){
						map.put("urlOrContent", menu.getMaterial().getId());
					}else{
						map.put("urlOrContent", "");
					}
				}else{
					map.put("urlOrContent", "");
				}
				map.put("children", getPreviewList(menu.getChildren()));
				list.add(map);
			}
		}
		return list;
		
	}

	private Map getSuccess(String message){
		Map json = new HashMap();
		json.put("code", "success");
		json.put("message", message);
		return json;
	}
	
	private Map getFailure(String message) {
		Map json = new HashMap();
		json.put("code", "failure");
		json.put("message", message);
		return json;
	}
}
