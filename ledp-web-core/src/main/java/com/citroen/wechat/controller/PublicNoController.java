package com.citroen.wechat.controller;

import com.alibaba.fastjson.JSON;
import com.citroen.ledp.domain.Constant;
import com.citroen.ledp.domain.Dealer;
import com.citroen.ledp.domain.Organization;
import com.citroen.ledp.domain.User;
import com.citroen.ledp.service.OrganizationService;
import com.citroen.ledp.service.PermissionService;
import com.citroen.ledp.util.JSONConverter;
import com.citroen.ledp.util.PropertiyUtil;
import com.citroen.ledp.util.SysConstant;
import com.citroen.wechat.api.service.ApiAccessToken;
import com.citroen.wechat.api.service.ApiMaterial;
import com.citroen.wechat.api.service.Response;
import com.citroen.wechat.api.token.TokenHolder;
import com.citroen.wechat.domain.PublicNo;
import com.citroen.wechat.service.PublicNoService;
import com.citroen.wechat.util.ConstantUtil;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 公众号管理
 * @author 何海粟
 * @date2015年6月4日
 */
@Controller("publicNoController")
@RequestMapping("/wechat/publicno")
public class PublicNoController {
	@Autowired
	private PermissionService permissionService;
	@Autowired
	private PublicNoService publicNoService;
	@Autowired
	private OrganizationService organizationService;
	
	private String dateFormat = "yyyy-MM-dd HH:mm:ss";
	
	private Map<String, Object> params;
	
	
	@RequestMapping(value="message")
	public String message(Model model,HttpServletRequest request,HttpServletResponse response,HttpSession session) throws Exception {
		session.setAttribute("choose", 121);
		model.addAttribute("message","在使用公众号相关信息前，请先设置当前公众号！");
		return this.index(model, request, response, session);
	}
	
	@RequestMapping(value="unauth/{code}")
	public String unauth(@PathVariable String code,Model model,HttpServletRequest request,HttpServletResponse response,HttpSession session) throws Exception {
		session.setAttribute("choose", 121);
		model.addAttribute("message","当前公众号未认证，无此操作权限！");
		return this.index(model, request, response, session);
	}
	
	@RequestMapping(value={"","index"})
	public String index(Model model,HttpServletRequest request,HttpServletResponse response,HttpSession session) throws Exception {
		model.addAttribute("types", publicNoService.getPublicNoType());

		Dealer loginDealer = (Dealer)request.getSession().getAttribute("loginDealer");
		Organization loginOrg = (Organization)request.getSession().getAttribute("loginOrg");
		Map<String,String> params = new HashMap<String,String>();
		if(loginDealer!=null){
			params.put("dealerId",loginDealer.getId().toString());
		}
		if(loginOrg!=null && loginOrg.getId()>1){
			params.put("orgId",loginOrg.getId().toString());
		}
		int total = publicNoService.getTotalRow(params);
		model.addAttribute("total",total);
		if(loginOrg!=null && loginOrg.getLevel()==1){
			model.addAttribute("orgs",organizationService.getChildren(loginOrg.getId()));
		}
		model.addAttribute("org",loginOrg);
		model.addAttribute("dealer",loginDealer);
		
		String message = (String)request.getSession().getAttribute("msgretrvauth_message");
		if(StringUtils.isNotBlank(message)){
			model.addAttribute("message",message);
			request.getSession().removeAttribute("msgretrvauth_message");
		}
		// 返回到界面
		return "wechat/publicno/index";
	}

	@RequestMapping(value = {"search"})
	@ResponseBody
	public JSON search(Model model, HttpServletRequest request, HttpServletResponse response)throws Exception {
		int pageSize = 10;
		int pageNumber = 1;
		try {pageSize = Integer.parseInt(request.getParameter("pageSize"));} catch (Exception e) {}
		try {pageNumber = Integer.parseInt(request.getParameter("currentPage"));} catch (Exception e) {}

		Dealer loginDealer = (Dealer)request.getSession().getAttribute("loginDealer");
		Organization loginOrg = (Organization)request.getSession().getAttribute("loginOrg");
		String alias_name = request.getParameter("parameter[alias_name]");
		String user_name = request.getParameter("parameter[user_name]");
		String type = request.getParameter("parameter[type]");
		String orgId = request.getParameter("parameter[orgId]");
		String dealerId = request.getParameter("parameter[dealerId]");
				
		Map<String,String> params = new HashMap<String,String>();
		
		if(StringUtils.isNotBlank(alias_name)){
			alias_name = new String(alias_name.getBytes("ISO-8859-1"),"UTF-8" );
			params.put("alias_name",alias_name);
		}
		if(StringUtils.isNotBlank(user_name)){
			user_name = new String(user_name.getBytes("ISO-8859-1"),"UTF-8" );
			params.put("user_name",user_name);
		}
		if(StringUtils.isNotBlank(type)){
			params.put("type",type);
		}
		if(StringUtils.isNotBlank(dealerId)){
			params.put("dealerId",dealerId);
		}else if(loginDealer!=null){
			params.put("dealerId",loginDealer.getId().toString());
		}
		if(StringUtils.isNotBlank(orgId)){
			params.put("orgId",orgId);
		}else if(loginOrg!=null){
			if(loginOrg.getLevel()>1){
				params.put("orgId",loginOrg.getId().toString());
			}
		}
		Map<String,Integer> paginateParams = new HashMap<String,Integer>();
		paginateParams.put("max", pageSize);
		paginateParams.put("offset", (pageNumber-1)*pageSize);
		
		List<PublicNo> rows = publicNoService.executeQuery(params,paginateParams);
		int total = publicNoService.getTotalRow(params);
		JSON json = JSONConverter.convert(total,pageSize,pageNumber,rows,new String[]{"id","nick_name","user_name","alias","head_img","status.name","status.code","service_type_info","verify_type_info","authorized","type.name","dateCreate","dealer.id","org.id","dealer.name","org.name"},
			new JSONConverter.Operation<PublicNo>() {
				public String operate(PublicNo t) {
					if(permissionService.hasAuth(SysConstant.PERMISSION_LEADS_DETAIL)){
						return "";
					}	
					return "";
				}
			}
		);
		return json;
	}
	
	@RequestMapping(value="authorize")
	public String authorize(Model model,HttpServletRequest request,HttpServletResponse response) throws Exception {
		return "wechat/publicno/authorize";
	}
	
	@RequestMapping(value="doAuthorize")
	public String doAuthorize(Model model,HttpServletRequest request,HttpServletResponse response) throws Exception {
		// 返回到界面
		String appid = request.getParameter("appid");
		String app_secret = request.getParameter("app_secret");
		String reAuth = request.getParameter("reAuth");
		if(StringUtils.isBlank(appid)){
			model.addAttribute("appid",appid);
			model.addAttribute("app_secret",app_secret);
			model.addAttribute("reAuth",reAuth);
			model.addAttribute("message","请输入公众号 AppID！");
			return "wechat/publicno/authorize";
		}
		
		if(StringUtils.isBlank(reAuth)||"confirm".equals(reAuth)){
			if(publicNoService.exist(appid)){
				model.addAttribute("appid",appid);
				model.addAttribute("app_secret",app_secret);
				model.addAttribute("reAuth","confirm");
				model.addAttribute("message","公众号 AppID已被授权，是否需要重新授权？");
				return "wechat/publicno/authorize";
			}
		}
		
		if(StringUtils.isBlank(app_secret)){
			model.addAttribute("appid",appid);
			model.addAttribute("app_secret",app_secret);
			model.addAttribute("reAuth",reAuth);
			model.addAttribute("message","请输入公众号 AppSecret！");
			return "wechat/publicno/authorize";
		}
		Response api = ApiAccessToken.getAuthorizerAccessTokenUrl();
		String url = api.get("url");
		if(StringUtils.isBlank(url)){
			model.addAttribute("appid",appid);
			model.addAttribute("app_secret",app_secret);
			model.addAttribute("message","获取授权地址发送错误！");
			return "wechat/publicno/authorize";
		}
		
		Object object = request.getSession().getAttribute("loginDealer");
		if(object==null){
			object = request.getSession().getAttribute("loginOrg");
		}
		request.getSession().getServletContext().setAttribute(appid+"app_secret",app_secret);
		request.getSession().getServletContext().setAttribute(appid,object);
		return "redirect:"+url;
	}
	@RequestMapping(value="setCurrent")
	@ResponseBody
	public JSONObject setSessionPublicNo(Model model,HttpServletRequest request,HttpServletResponse response) throws Exception {
		String id = request.getParameter("id");
		Map<String,String> result = new HashMap<String,String>();
		if(StringUtils.isNotBlank(id)){
			Long longId = 0L;
			try{longId = Long.parseLong(id);}catch(Exception e){}
			PublicNo publicNo = publicNoService.get(longId);
			if(publicNo!=null){
				//设置当前公众号
				User user = (User)request.getSession().getAttribute("loginUser");
				publicNoService.setActivePublicno(user,publicNo);
				request.getSession().setAttribute(ConstantUtil.SESSION_CURRENT_PUBLICNO,publicNo);
				result.put("code","200");
				result.put("message","当前公众号设置成功！");
			}
			return JSONObject.fromObject(result);
		}
		result.put("code","201");
		result.put("message","当前公众号设置失败!");
		return JSONObject.fromObject(result);
	}
	
	@RequestMapping(value="active")
	@ResponseBody
	public JSONObject active(Model model,HttpServletRequest request,HttpServletResponse response) throws Exception {
		String id = request.getParameter("id");
		Map<String,String> result = new HashMap<String,String>();
		if(StringUtils.isNotBlank(id)){
			Long longId = 0L;
			try{longId = Long.parseLong(id);}catch(Exception e){}
			PublicNo publicNo = publicNoService.get(longId);
			publicNo.setStatus(new Constant(1010L));
			publicNoService.update(publicNo);
			
			result.put("code","200");
			result.put("message","公众号激活成功！");
			return JSONObject.fromObject(result);
		}
		result.put("code","201");
		result.put("message","公众号激活失败!");
		return JSONObject.fromObject(result);
	}
	
	@RequestMapping(value="inactive")
	@ResponseBody
	public JSONObject inactive(Model model,HttpServletRequest request,HttpServletResponse response) throws Exception {
		String id = request.getParameter("id");
		Map<String,String> result = new HashMap<String,String>();
		if(StringUtils.isNotBlank(id)){
			Long longId = 0L;
			try{longId = Long.parseLong(id);}catch(Exception e){}
			PublicNo publicNo = publicNoService.get(longId);
			publicNo.setStatus(new Constant(1020L));
			publicNoService.update(publicNo);
			result.put("code","200");
			result.put("message","公众号禁用成功！");
			return JSONObject.fromObject(result);
		}
		result.put("code","201");
		result.put("message","公众号禁用失败!");
		return JSONObject.fromObject(result);
	}
	
	/**
	 * 同步粉丝组
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="syncFansGroup")
	@ResponseBody
	public JSONObject syncFansGroup(Model model,HttpServletRequest request,HttpServletResponse response) throws Exception {
		PublicNo publicNo = (PublicNo) request.getSession().getAttribute(ConstantUtil.SESSION_CURRENT_PUBLICNO);
		Map<String,Object> result = new HashMap<String,Object>();
		try {
			publicNoService.downloadFansGroup(publicNo);
		} catch (Exception e) {
			result.put("code", "201");
			result.put("msg", e.getMessage());
			return JSONObject.fromObject(result);
		}
		result.put("code", "200");
		result.put("msg", "执行成功！");
		return JSONObject.fromObject(result);
	}
	
	/**
	 * 同步粉丝
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="syncFans")
	@ResponseBody
	public JSONObject syncFans(Model model,HttpServletRequest request,HttpServletResponse response) throws Exception {
		PublicNo publicNo = (PublicNo) request.getSession().getAttribute(ConstantUtil.SESSION_CURRENT_PUBLICNO);
		Map<String,Object> result = new HashMap<String,Object>();
		try {
			publicNoService.downloadFans(publicNo);
		} catch (Exception e) {
			result.put("code", "201");
			result.put("msg", e.getMessage());
			return JSONObject.fromObject(result);
		}
		result.put("code", "200");
		result.put("msg", "执行成功！");
		return JSONObject.fromObject(result);
	}
	
	/**
	 * 同步素材
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="syncMaterial")
	@ResponseBody
	public JSONObject syncMaterial(Model model,HttpServletRequest request,HttpServletResponse response) throws Exception {
		PublicNo publicNo = (PublicNo) request.getSession().getAttribute(ConstantUtil.SESSION_CURRENT_PUBLICNO);
		Map<String,Object> result = new HashMap<String,Object>();
		try {
			publicNoService.downloadMaterial(publicNo);
		} catch (Exception e) {
			result.put("code", "201");
			result.put("msg", e.getMessage());
			return JSONObject.fromObject(result);
		}
		result.put("code", "200");
		result.put("msg", "同步成功！");
		return JSONObject.fromObject(result);
	}
	
	@RequestMapping(value="download/{mediaId}")
	@ResponseBody
	public JSONObject download(@PathVariable String mediaId,Model model,HttpServletRequest request,HttpServletResponse response) throws Exception {
		PublicNo publicNo = (PublicNo) request.getSession().getAttribute(ConstantUtil.SESSION_CURRENT_PUBLICNO);
		Map<String,Object> result = new HashMap<String,Object>();
		String access_token = TokenHolder.getAuthorizerAccessToken(publicNo.getAppid(), publicNo.getAuthorizer_refresh_token());
		PropertiyUtil propUtil = new PropertiyUtil("/wechat.properties");
		//磁盘路径
		String saveDir = propUtil.getString("material.dir")+"/images/"+mediaId+".jpg";
		//图片访问路径url
		String filePath = propUtil.getString("material.url")+"/images/"+mediaId+".jpg";
		try {
			Response res = ApiMaterial.download(access_token, mediaId, saveDir);
			result.put("code", "200");
			result.put("msg", res.toString());
			return JSONObject.fromObject(result);
		} catch (Exception e) {
			result.put("code", "201");
			result.put("msg", e.getMessage());
			return JSONObject.fromObject(result);
		}
	}
	
	
	
	
}
