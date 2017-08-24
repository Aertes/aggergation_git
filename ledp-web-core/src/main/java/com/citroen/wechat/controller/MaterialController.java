package com.citroen.wechat.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.fastjson.JSON;
import com.citroen.ledp.domain.Constant;
import com.citroen.ledp.domain.User;
import com.citroen.ledp.interceptor.Permission;
import com.citroen.ledp.service.PermissionService;
import com.citroen.ledp.util.PropertiyUtil;
import com.citroen.wechat.api.model.MaterialNews;
import com.citroen.wechat.api.service.ApiMass;
import com.citroen.wechat.api.service.ApiMaterial;
import com.citroen.wechat.api.service.Response;
import com.citroen.wechat.api.token.TokenHolder;
import com.citroen.wechat.domain.Material;
import com.citroen.wechat.domain.MaterialFolder;
import com.citroen.wechat.domain.PublicNo;
import com.citroen.wechat.domain.WechatMenu;
import com.citroen.wechat.dto.MaterialGroupDto;
import com.citroen.wechat.exception.WechatException;
import com.citroen.wechat.service.AutoReplyService;
import com.citroen.wechat.service.MaterialFolderService;
import com.citroen.wechat.service.MaterialService;
import com.citroen.wechat.service.WechatMenuService;
import com.citroen.wechat.util.ConstantUtil;
import com.citroen.wechat.util.FileUtil;
import com.citroen.wechat.util.JsonUtil;
import com.citroen.wechat.util.Pagination;

/**
 * 素材管理
 * 
 * @author 何海粟
 * @date2015年6月4日
 */
@Controller("materialController")
@RequestMapping("/wechat/material")
public class MaterialController {
	private Map<String, Object> params;

	@Autowired
	private PermissionService permissionService;
	@Autowired
	private MaterialService materialService;
	@Autowired
	private MaterialFolderService materialFolderService;
	@Autowired
	private WechatMenuService wechatMenuService;
	@Autowired
	private AutoReplyService autoReplyService;
	
	private static final String dateFormat = "yyyy-MM-dd HH:mm:ss";

	/**
	 * 公众号素材
	 * 
	 * @param model
	 * @param request
	 * @param response
	 * @param session
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("publicno")
	@Permission(code="wechat/material/publicno")
	public ModelAndView publicno(Model model, HttpServletRequest request,
			HttpServletResponse response, HttpSession session) throws Exception {
		PublicNo publicNo = (PublicNo) request.getSession().getAttribute(ConstantUtil.SESSION_CURRENT_PUBLICNO);
		if(publicNo==null){
			return new ModelAndView("redirect:/wechat/publicno/message");
		}
		
		ModelAndView view = new ModelAndView();
		view.setViewName("wechat/material/publicno");
		
		model.addAttribute("publicNoId",publicNo.getId()); // 在页面校验是否存在公众号
		model.addAttribute("publicNo_Verify",publicNo.getVerify_type_info()); // 认证信息
		
		//获取图片库分组
		List<MaterialGroupDto> groups = new ArrayList<MaterialGroupDto>();
		groups = materialService.searchFileGroup("9002");
		model.addAttribute("groups", groups);
		
		// 返回到界面
		return view;
	}
	
	/**
	 * 跳转到选择图片界面
	 * @return
	 */
	@RequestMapping("redirectToCheckGroup")
	public ModelAndView redirectToCheckGroup(){
		ModelAndView view = new ModelAndView();
		view.setViewName("wechat/material/checkGroup");
		return view;
	}
	

	/**
	 * 查询
	 * 
	 * @param model
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = {"search"})
	@Permission(code="wechat/material/index")
	@ResponseBody
	public void search(Model model, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		int pageSize   = 10;
		int currentPage = 1;
		Map<String,Integer> paginateParams = new HashMap<String,Integer>();
		Map<String, Object> params = new HashMap<String,Object>();
		try {
			currentPage = Integer.parseInt(request.getParameter("currentPage"));
			pageSize = Integer.parseInt(request.getParameter("pageSize"));
		} catch (Exception e) {
		}
		String materialId = request.getParameter("materialId");
		if(StringUtils.isNotBlank(materialId)){
			long id = Long.parseLong(materialId);
			params.put("id", id);
		}
		
		PublicNo publicNo = (PublicNo) request.getSession().getAttribute(ConstantUtil.SESSION_CURRENT_PUBLICNO);
		if(publicNo == null){
			response.getWriter().print(JsonUtil.toJSON(getFailure("未选择当前操作的公众号")));
			return;
		}
		params.put("publicnoId", publicNo.getId());
		
		paginateParams.put("offset", (currentPage-1)*pageSize);
		paginateParams.put("max", pageSize);
		//类型为图文
		params.put("type", "9003");
		String condition = request.getParameter("parameter[condition]");
		if(StringUtils.isNotBlank(condition)){
			condition=new String(condition.getBytes("ISO-8859-1"),"UTF-8" );
			params.put("title", "%"+condition+"%");
			params.put("author", "%"+condition+"%");
			params.put("digest", "%"+condition+"%");
		}
		
		List<Material> rows = materialService.executeQuery(params,paginateParams);
		int count = materialService.getTotalRow(params);
		
		Pagination pagination = new Pagination(count,pageSize,currentPage);
		
		Map<String,Object> json = new HashMap<String,Object>();
		json.put("paginationData", pagination);
		json.put("data", rows);
        response.setContentType("text/html; charset=utf-8");
		response.getWriter().print(JsonUtil.toJSON(dateFormat, json));
	}
	
	/**
	 * 查询
	 * 
	 * @param model
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = {"getMaterial"})
	@Permission(code="wechat/material/index")
	@ResponseBody
	public void getMaterial(long id, HttpServletResponse response)
			throws Exception {
		if(id == 0){
			throw new WechatException("素材标识不能为空");
		}
		Material material = materialService.get(id);
        response.setContentType("text/html; charset=utf-8");
		response.getWriter().print(JsonUtil.toJSON(dateFormat, material));
	}
	
	/**
	 * 查询图片库
	 * 
	 * @param model
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = {"searchImage"})
	@Permission(code="wechat/material/publicno")
	@ResponseBody
	public void searchImage(Model model, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		response.setContentType("text/html; charset=utf-8");
		int pageSize   = 10;
		int currentPage = 1;
		Long groupId = null;
		Map<String,Integer> paginateParams = new HashMap<String,Integer>();
		Map<String, Object> params = new HashMap<String,Object>();
		try {
			currentPage = Integer.parseInt(request.getParameter("currentPage"));
			pageSize = Integer.parseInt(request.getParameter("pageSize"));
		} catch (Exception e) {}
		
		String group = request.getParameter("group");
		if(StringUtils.isNotBlank(group)){
			groupId = Long.parseLong(group);
			params.put("folder", groupId);
		}
		
		PublicNo publicNo = (PublicNo) request.getSession().getAttribute(ConstantUtil.SESSION_CURRENT_PUBLICNO);
		if(publicNo == null){
			response.getWriter().print(JsonUtil.toJSON(getFailure("未选择当前操作的公众号")));
			return;
		}
		params.put("publicnoId", publicNo.getId());
		
		String materialId = request.getParameter("materialId");
		if(StringUtils.isNotBlank(materialId)){
			long id = Long.parseLong(materialId);
			params.put("id", id);
		}
		String condition = request.getParameter("parameter[condition]");
		if(StringUtils.isNotBlank(condition)){
			condition=new String(condition.getBytes("ISO-8859-1"),"UTF-8" );
			params.put("title", "%"+condition+"%");
			params.put("author", "%"+condition+"%");
			params.put("digest", "%"+condition+"%");
		}
		
		paginateParams.put("offset", (currentPage-1)*pageSize);
		paginateParams.put("max", pageSize);
		/**
		 * 查询图片
		 */
		params.put("type", "9002");
		
		List<Material> rows = materialService.executeQuery(params,paginateParams);
		int count = materialService.getTotalRow(params);
		Pagination pagination = new Pagination(count,pageSize,currentPage);
		
		Map<String,Object> json = new HashMap<String,Object>();
		json.put("paginationData", pagination);
		json.put("data", rows);
		response.getWriter().print(JsonUtil.toJSON(dateFormat, json));
	}
	

	/**
	 * 查询总部共享文件
	 * @param model
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = {"searchFile"})
	@Permission(code="wechat/material/publicno")
	@ResponseBody
	public void searchFile(Model model, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		response.setContentType("text/html; charset=utf-8");
		int pageSize   = 10;
		int currentPage = 1;
		Map<String,Integer> paginateParams = new HashMap<String,Integer>();
		Map<String, Object> params = new HashMap<String,Object>();
		String group = request.getParameter("group");
		String condiction = request.getParameter("parameter[condiction]");
		String groupId = request.getParameter("parameter[groupId]");
		
		try {
			currentPage = Integer.parseInt(request.getParameter("currentPage"));
			pageSize = Integer.parseInt(request.getParameter("pageSize"));
		} catch (Exception e) {}
		
		if(StringUtils.isNotBlank(group)){
			params.put("folder", group);
		}else if(StringUtils.isNotBlank(groupId)){
			params.put("folder", groupId);
		}else{
			params.put("folder", 1);
		}
		
		if(StringUtils.isNotBlank(condiction)){
			condiction=new String(condiction.getBytes("ISO-8859-1"),"UTF-8" );
			params.put("sharename", "%"+condiction+"%");
		}
		
		paginateParams.put("offset", (currentPage-1)*pageSize);
		paginateParams.put("max", pageSize);
		/**
		 * 查询总部共享资源
		 */
		params.put("type", "9006");
		
		List<Material> rows = materialService.executeQuery(params,paginateParams);
		int count = materialService.getTotalRow(params);
		Pagination pagination = new Pagination(count,pageSize,currentPage);
		
		Map<String,Object> json = new HashMap<String,Object>();
		json.put("paginationData", pagination);
		json.put("data", rows);
		response.getWriter().print(JsonUtil.toJSON(dateFormat, json));
	}

	/**
	 * 新增单图文素材
	 * 
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/createSingle")
	@Permission(code="wechat/material/create")
	public String createSingle(Model model) throws Exception {
		return "wechat/material/createSingle";
	}

	/**
	 * 新增多图文素材
	 * 
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/createMultiple")
	@Permission(code="wechat/material/create")
	public String createMultiple(Model model) throws Exception {
		return "wechat/material/createMultiple";
	}

	/**
	 * 保存单图文
	 * @param model
	 * @param material
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/save")
	@Permission(code="wechat/material/create")
	@ResponseBody
	public void save(Model model, @ModelAttribute("material") Material material,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		response.setContentType("text/html; charset=utf-8");
		//单图文media_id
		String mediaId = request.getParameter("mediaId");
		PublicNo publicNo = (PublicNo) request.getSession().getAttribute(ConstantUtil.SESSION_CURRENT_PUBLICNO);
		if(publicNo == null){
			response.getWriter().print(JsonUtil.toJSON(getFailure("新增失败,未选择当前操作的公众号")));
			return;
		}

		//替换正文中的可能导致换行的字符
		material.setContent(material.getContent().replace("<p>", "").replace("</p>","").replace("\n","").replace("\t",""));

		String access_token = TokenHolder.getAuthorizerAccessToken(publicNo.getAppid(), publicNo.getAuthorizer_refresh_token());
		//上传到腾讯服务器，返回media_id
		if(StringUtils.isBlank(mediaId)){
			Response rep =	ApiMaterial.upload(access_token, getArticles(material));
			if(rep.getStatus() == Response.SUCCESS){
				mediaId = rep.get("media_id");
				rep = ApiMaterial.getMaterial(access_token, mediaId);
				JSONObject json = null;
				JSONArray newsItems = new JSONArray();
				if(rep.getStatus() == Response.SUCCESS){
					try {
						json = JSONObject.fromObject(rep.getParams().toString());
					} catch (Exception e) {
						e.printStackTrace();
					}
					if(json != null){
						newsItems = json.getJSONArray("news_item");
					}
					if(newsItems != null){
						for(int i=0; i<newsItems.size(); i++){
							JSONObject newsItem = newsItems.getJSONObject(i);
							material.setContentUrl(newsItem.getString("url"));
						}
					}
				}
			}else{
				response.getWriter().print(JsonUtil.toJSON(getFailure("新增图文素材失败,原因:"+rep.getMessage())));
				return;
			}
			
			material.setIsOpen(false);
			material.setCreateDate(new Date());
			material.setPublicNo(publicNo);
			material.setMediaId(mediaId);
			material.setType(new Constant(9003L));
			try {
				materialService.save(material);
			} catch (Exception e) {
				response.getWriter().print(JSON.toJSON(getFailure("保存失败,原因:"+e.getMessage())));
				return;
			}
		}else{
			//mediaId不为空则说明素材在预览的时候已经生成，这时候应该更新
			material = materialService.getByMediaId(mediaId);
			if(material != null){
				material.setPublicNo(publicNo);
			}
			List<MaterialNews> articles = getArticles(material);
			/* 替换正文中的\n标记，防止正文段落间出现多出一个换行 */
			for (MaterialNews article : articles) {
				article.setContent(article.getContent().replace("<p>", "").replace("</p>","").replace("\n","").replace("\t",""));
			}
			if(CollectionUtils.isNotEmpty(articles)){
				for(MaterialNews article : articles){
					Response rep =	ApiMaterial.update(access_token,mediaId, article);
					if(rep.getStatus() != Response.SUCCESS){
						response.getWriter().print(JsonUtil.toJSON(getFailure("保存图文素材失败,原因:"+rep.getMessage())));
						return;
					}else{
						try {
							materialService.update(material);
						} catch (Exception e) {
							e.printStackTrace();
							response.getWriter().print(JSON.toJSON(getFailure("保存失败,原因:"+e.getMessage())));
							return;
						}
					}
				}
			}
		}
		Map map = new HashMap();
		map.put("code", "success");
		map.put("message", "保存成功 ！");
		map.put("mediaId", mediaId);
		response.getWriter().print(JSON.toJSON(map));
	}

	/**
	 * 保存多图文
	 * @param model
	 * @param contentsJson
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "/saveMultiple",method=RequestMethod.POST)
	@Permission(code="wechat/material/create")
	@ResponseBody
	public void saveMultiple(Model model, String contentsJson,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		response.setContentType("text/html; charset=utf-8");
		PublicNo publicNo = (PublicNo) request.getSession().getAttribute(ConstantUtil.SESSION_CURRENT_PUBLICNO);
		if(publicNo == null){
			response.getWriter().print(JsonUtil.toJSON(getFailure("新增失败,未选择当前操作的公众号")));
			return;
		}
		//多图文media_id
		String mediaId = request.getParameter("mediaId");
		String access_token = TokenHolder.getAuthorizerAccessToken(publicNo.getAppid(), publicNo.getAuthorizer_refresh_token());
		
		List<Material> materials = JsonUtil.toList(contentsJson, Material.class);
		materials = getMaterials(materials,publicNo);
		for (Material material : materials) {
			//替换正文中的可能导致换行的字符
			material.setContent(material.getContent().replace("<p>", "").replace("</p>","").replace("\n","").replace("\t",""));
		}
		
		//获取第一条，其他都是孩子
		Material material = getFirst(materials,publicNo);
		materials.remove(material);
		material.setChildren(materials);
		
		if(StringUtils.isBlank(mediaId)){
			//上传到腾讯服务器，返回media_id
			Response rep =	ApiMaterial.upload(access_token, getArticles(material));
			if(rep.getStatus() == Response.SUCCESS){
				mediaId = rep.get("media_id");
				rep = ApiMaterial.getMaterial(access_token, mediaId);
				JSONObject json = null;
				JSONArray newsItems = new JSONArray();
				if(rep.getStatus() == Response.SUCCESS){
					try {
						json = JSONObject.fromObject(rep.getParams().toString());
					} catch (Exception e) {
						e.printStackTrace();
					}
					if(json != null && json.containsKey("news_item")){
						newsItems = json.getJSONArray("news_item");
					}
					List<String> urls = new ArrayList<String>();
					for(int i=0; i<newsItems.size(); i++){
						JSONObject newsItem = newsItems.getJSONObject(i);
						urls.add(newsItem.getString("url"));
					}
					int j = 0;
					material.setContentUrl(urls.get(j++));
					for(Material child : material.getChildren()){
						child.setContentUrl(newsItems.getJSONObject(j++).getString("url"));
					}
				}
			}else{
				response.getWriter().print(JsonUtil.toJSON(getFailure("新增图文素材失败,原因:"+rep.getMessage())));
				return;
			}
			
			try {
				material.setMediaId(mediaId);
				materialService.save(material);
			} catch (Exception e) {
				e.printStackTrace();
				response.getWriter().print(JSON.toJSON(getFailure("保存失败,原因:"+e.getMessage())));
				return;
			}
		}else{
			//mediaId不为空则说明素材在预览的时候已经生成，这时候应该更新
			material = materialService.getByMediaId(mediaId);
			if(material != null){
				material.setPublicNo(publicNo);
			}
			List<MaterialNews> articles = getArticles(material);
			/* 替换正文中的\n标记，防止正文段落间出现多出一个换行 */
			for (MaterialNews article : articles) {
				article.setContent(article.getContent().replace("<p>", "").replace("</p>","").replace("\n","").replace("\t",""));
			}
			if(CollectionUtils.isNotEmpty(articles)){
				for(MaterialNews article : articles){
					Response rep =	ApiMaterial.update(access_token,mediaId, article);
					if(rep.getStatus() != Response.SUCCESS){
						response.getWriter().print(JsonUtil.toJSON(getFailure("保存图文素材失败,原因:"+rep.getMessage())));
						return;
					}else{
						try {
							materialService.update(material);
						} catch (Exception e) {
							e.printStackTrace();
							response.getWriter().print(JSON.toJSON(getFailure("保存失败,原因:"+e.getMessage())));
							return;
						}
					}
				}
			}
		}
		Map map = new HashMap();
		map.put("code", "success");
		map.put("message", "保存成功 ！");
		map.put("mediaId", mediaId);
		response.getWriter().print(JSON.toJSON(map));
	}
	
	/**
	 * 保存单图文并群发
	 * @param model
	 * @param material
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/saveAndMass")
	@Permission(code="wechat/material/create")
	public String saveAndMass(Model model, @ModelAttribute("material") Material material,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		response.setContentType("text/html; charset=utf-8");
		long materialId = 0l;
		//单图文media_id
		String mediaId = request.getParameter("mediaId");
		PublicNo publicNo = (PublicNo) request.getSession().getAttribute(ConstantUtil.SESSION_CURRENT_PUBLICNO);
		if(publicNo == null){
			return "redirect:/wechat/material/createSingle";
		}
		String access_token = TokenHolder.getAuthorizerAccessToken(publicNo.getAppid(), publicNo.getAuthorizer_refresh_token());

		//替换正文中的可能导致换行的字符
		material.setContent(material.getContent().replace("<p>", "").replace("</p>","").replace("\n","").replace("\t",""));

		if(StringUtils.isBlank(mediaId)){
			//上传到腾讯服务器，返回media_id
			Response rep =	ApiMaterial.upload(access_token, getArticles(material));
			if(rep.getStatus() == Response.SUCCESS){
				mediaId = rep.get("media_id");
			}else{
				model.addAttribute("errormsg", rep.getMessage());
				return "redirect:/wechat/material/createSingle";
			}
			
			material.setIsOpen(false);
			material.setCreateDate(new Date());
			material.setPublicNo(publicNo);
			material.setMediaId(mediaId);
			material.setType(new Constant(9003L));
			try {
				materialId = materialService.save(material);
			} catch (Exception e) {
				e.printStackTrace();
				model.addAttribute("errormsg", "新增图文失败,原因:"+e.getMessage());
				return "redirect:/wechat/material/createSingle";
			}
		}else{
			//mediaId不为空则说明素材在预览的时候已经生成，这时候应该更新
			material = materialService.getByMediaId(mediaId);
			if(material != null){
				materialId = material.getId();
				material.setPublicNo(publicNo);
			}
			List<MaterialNews> articles = getArticles(material);
			if(CollectionUtils.isNotEmpty(articles)){
				for(MaterialNews article : articles){
					Response rep =	ApiMaterial.update(access_token,mediaId, article);
					if(rep.getStatus() == Response.SUCCESS){
						try {
							materialService.update(material);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		//跳转到群发界面
		model.addAttribute("materialId", materialId);
		return "redirect:/wechat/messagesendbatch/index";
		
		
	}
	
	/**
	 * 保存多图文并群发
	 * @param model
	 * @param contentsJson
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "/saveMultipleAndMass",method=RequestMethod.POST)
	@Permission(code="wechat/material/create")
	public String saveMultipleAndMass(Model model, String contentsJson,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		response.setContentType("text/html; charset=utf-8");
		PublicNo publicNo = (PublicNo) request.getSession().getAttribute(ConstantUtil.SESSION_CURRENT_PUBLICNO);
		if(publicNo == null){
			return "redirect:/wechat/material/createMultiple";
		}
		String access_token = TokenHolder.getAuthorizerAccessToken(publicNo.getAppid(), publicNo.getAuthorizer_refresh_token());
		long materialId = 0l;
		//多图文media_id
		String mediaId = request.getParameter("mediaId");
		
		List<Material> materials = null;
		try {
			materials = JsonUtil.toList(contentsJson, Material.class);
		} catch (Exception e) {
			model.addAttribute("errormsg", "JSON解析错误");
			return "redirect:/wechat/material/createMultiple";
		}
		materials = getMaterials(materials,publicNo);

		for (Material material : materials) {
			//替换正文中的可能导致换行的字符
			material.setContent(material.getContent().replace("<p>", "").replace("</p>","").replace("\n","").replace("\t",""));
		}
		
		//获取第一条，其他都是孩子
		Material material = getFirst(materials,publicNo);
		materials.remove(material);
		material.setChildren(materials);
		
		//上传到腾讯服务器，返回media_id
		if(StringUtils.isBlank(mediaId)){
			Response rep =	ApiMaterial.upload(access_token, getArticles(material));
			if(rep.getStatus() == Response.SUCCESS){
				mediaId = rep.get("media_id");
				rep = ApiMaterial.getMaterial(access_token, mediaId);
				JSONObject json = null;
				JSONArray newsItems = new JSONArray();
				if(rep.getStatus() == Response.SUCCESS){
					try {
						json = JSONObject.fromObject(rep.getParams().toString());
					} catch (Exception e) {
						e.printStackTrace();
					}
					if(json != null && json.containsKey("news_item")){
						newsItems = json.getJSONArray("news_item");
					}
					List<String> urls = new ArrayList<String>();
					for(int i=0; i<newsItems.size(); i++){
						JSONObject newsItem = newsItems.getJSONObject(i);
						urls.add(newsItem.getString("url"));
					}
					int j = 0;
					material.setContentUrl(urls.get(j++));
					for(Material child : material.getChildren()){
						child.setContentUrl(newsItems.getJSONObject(j++).getString("url"));
					}
				}
			}else{
				//新增图文素材失败;
				model.addAttribute("errormsg", rep.getMessage());
				return "redirect:/wechat/material/createMultiple";
			}
			
			try {
				material.setMediaId(mediaId);
				materialId = materialService.save(material);
			} catch (Exception e) {
				e.printStackTrace();
				model.addAttribute("errormsg", "新增图文失败,原因:"+e.getMessage());
				return "redirect:/wechat/material/createMultiple";
			}
		}else{
			//mediaId不为空则说明素材在预览的时候已经生成，这时候应该更新
			material = materialService.getByMediaId(mediaId);
			if(material != null){
				materialId = material.getId();
				material.setPublicNo(publicNo);
			}
			List<MaterialNews> articles = getArticles(material);
			if(CollectionUtils.isNotEmpty(articles)){
				for(MaterialNews article : articles){
					Response rep =	ApiMaterial.update(access_token,mediaId, article);
					if(rep.getStatus() == Response.SUCCESS){
						try {
							materialService.update(material);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		//跳转到群发界面
		model.addAttribute("materialId", materialId);
		return "redirect:/wechat/messagesendbatch/index";
	}
	
	/**
	 * 修改单图文并群发
	 * @param model
	 * @param material
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "/updateAndMass",method=RequestMethod.POST)
	@Permission(code="wechat/material/create")
	public String updateAndMass(Model model, Material material,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		response.setContentType("text/html; charset=utf-8");
		long materialId = 0l;
		PublicNo publicNo = (PublicNo) request.getSession().getAttribute(ConstantUtil.SESSION_CURRENT_PUBLICNO);
		if(publicNo == null){
			return "redirect:/wechat/material/createSingle";
		}
		String access_token = TokenHolder.getAuthorizerAccessToken(publicNo.getAppid(), publicNo.getAuthorizer_refresh_token());
		//media_id
		String mediaId = material.getMediaId();

		//替换正文中的可能导致换行的字符
		material.setContent(material.getContent().replace("<p>", "").replace("</p>","").replace("\n","").replace("\t",""));

		//上传到腾讯服务器，返回media_id
		MaterialNews article = new MaterialNews();
		article.setIndex(0);
		article.setAuthor(material.getAuthor());
		article.setContent(material.getContent());
		article.setContent_source_url(material.getOriginalUrl());
		article.setDigest(material.getDigest());
		article.setShow_cover_pic("on".equals(material.getShowCoverPic())?1:0);
		article.setThumb_media_id(material.getThumbMediaId());
		article.setTitle(material.getTitle());
		Response rep =	ApiMaterial.update(access_token,mediaId, article);
		if(rep.getStatus() != Response.SUCCESS){
			model.addAttribute("errormsg", rep.getMessage());;
			return "redirect:/wechat/material/createSingle";
		}
				
		material.setType(new Constant(9003L));
		material.setMediaId(mediaId);
		
		try {
			materialId = materialService.update(material);
		} catch (WechatException e) {
			e.printStackTrace();
			model.addAttribute("errormsg", "修改图文素材失败,原因:"+e.getMessage());
			return "redirect:/wechat/material/createSingle";
		}
		//跳转到群发界面
		model.addAttribute("materialId", materialId);
		return "redirect:/wechat/messagesendbatch/index";
	}
	
	/**
	 * 修改多图文并群发
	 * @param model
	 * @param contentsJson
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "/updateMultipleAndMass",method=RequestMethod.POST)
	@Permission(code="wechat/material/create")
	public String updateMultipleAndMass(Model model, String contentsJson,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		response.setContentType("text/html; charset=utf-8");
		long materialId = 0l;
		PublicNo publicNo = (PublicNo) request.getSession().getAttribute(ConstantUtil.SESSION_CURRENT_PUBLICNO);
		String MediaId = request.getParameter("MediaId");
		if(publicNo == null){
			return "redirect:/wechat/material/createMultiple";
		}
		String access_token = TokenHolder.getAuthorizerAccessToken(publicNo.getAppid(), publicNo.getAuthorizer_refresh_token());
		
		List<Material> materials;
		try {
			materials = JsonUtil.toList(contentsJson, Material.class);
		} catch (Exception e) {
			model.addAttribute("errormsg", "JSON解析错误");
			return "redirect:/wechat/material/createMultiple";
		}
		materials = getMaterials(materials,publicNo);

		for (Material material : materials) {
			//替换正文中的可能导致换行的字符
			material.setContent(material.getContent().replace("<p>", "").replace("</p>","").replace("\n","").replace("\t",""));
		}

		//获取第一条，其他都是孩子
		Material material = getFirst(materials,publicNo);
		materials.remove(material);
		material.setChildren(materials);
		
		List<MaterialNews> articles = getArticles(material);
		if(CollectionUtils.isEmpty(articles)){
			model.addAttribute("errormsg", "修改图文素材失败,原因:数据解析错误");
			return "redirect:/wechat/material/createMultiple";
		}
		for(MaterialNews article : articles){
			Response rep =	ApiMaterial.update(access_token,MediaId, article);
			if(rep.getStatus() != Response.SUCCESS){
				model.addAttribute("errormsg", rep.getMessage());
				return "redirect:/wechat/material/createMultiple";
			}
		}
		try {
			material.setType(new Constant(9003L));
			material.setMediaId(MediaId);
			materialId = materialService.update(material);
		} catch (Exception e) {
			model.addAttribute("errormsg", "修改图文素材失败,原因:"+e.getMessage());
			return "redirect:/wechat/material/createMultiple";
		}
		//跳转到群发界面
		model.addAttribute("materialId", materialId);
		return "redirect:/wechat/messagesendbatch/index";
	}
	
	@RequestMapping(value = "/saveAndRedirect")
	@Permission(code="wechat/material/index")
	public String saveAndRedirect(Model model) throws Exception {
		return "wechat/material/saveAndRedirect";
	}
	
	/**
	 * 新增单图文素材并跳转
	 * 
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/createSingleAndRedirect")
	@Permission(code="wechat/material/create")
	public String createSingleAndRedirect(Model model,HttpServletRequest request) throws Exception {
		String menuid = request.getParameter("menuid");
		model.addAttribute("menuid", menuid);
		return "wechat/material/createSingleAndRedirect";
	}
	/**
	 * 新增多图文素材并跳转
	 * 
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/createMultipleAndRedirect")
	@Permission(code="wechat/material/create")
	public String createMultipleAndRedirect(Model model,HttpServletRequest request) throws Exception {
		String menuid = request.getParameter("menuid");
		model.addAttribute("menuid", menuid);
		return "wechat/material/createMultipleAndRedirect";
	}
	
	/**
	 * 保存单图文并跳转
	 * @param model
	 * @param material
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/saveSingleAndRedirect")
	public String saveSingleAndRedirect(Model model, @ModelAttribute("material") Material material,HttpServletRequest request)
			throws Exception {
		
		long materialId = 0l;
		String menuidStr = request.getParameter("menuid");
		Long menuId = 0L;
		try {
			menuId = Long.valueOf(menuidStr);
		} catch (Exception e) {
			throw new WechatException("新建图文信息失败,原因:获取菜单唯一标识失败");
		}
		//单图文media_id
		String mediaId = request.getParameter("mediaId");
		PublicNo publicNo = (PublicNo) request.getSession().getAttribute(ConstantUtil.SESSION_CURRENT_PUBLICNO);
		if(publicNo == null){
			model.addAttribute("errormsg", "请先选择公众号");
			model.addAttribute("menuid", menuId);
			return "redirect:/wechat/material/createSingleAndRedirect";
		}
		String access_token = TokenHolder.getAuthorizerAccessToken(publicNo.getAppid(), publicNo.getAuthorizer_refresh_token());
		//上传到腾讯服务器，返回media_id
		
		if(StringUtils.isBlank(mediaId)){
			Response rep =	ApiMaterial.upload(access_token, getArticles(material));
			if(rep.getStatus() == Response.SUCCESS){
				mediaId = rep.get("media_id");
				mediaId = rep.get("media_id");
				rep = ApiMaterial.getMaterial(access_token, mediaId);
				JSONObject json = null;
				JSONArray newsItems = new JSONArray();
				if(rep.getStatus() == Response.SUCCESS){
					try {
						json = JSONObject.fromObject(rep.getParams().toString());
					} catch (Exception e) {
						e.printStackTrace();
					}
					if(json != null){
						newsItems = json.getJSONArray("news_item");
					}
					if(newsItems != null){
						for(int i=0; i<newsItems.size(); i++){
							JSONObject newsItem = newsItems.getJSONObject(i);
							material.setContentUrl(newsItem.getString("url"));
						}
					}
				}
			}else{
				model.addAttribute("errormsg", "新增图文素材失败,原因:"+rep.getMessage());
				model.addAttribute("menuid", menuId);
				return "redirect:/wechat/material/createSingleAndRedirect";
			}
			
			material.setIsOpen(false);
			material.setCreateDate(new Date());
			material.setPublicNo(publicNo);
			material.setMediaId(mediaId);
			material.setType(new Constant(9003L));
			try {
				materialId = materialService.save(material);
				material.setId(materialId);
				WechatMenu menu = wechatMenuService.get(menuId);
				menu.setEvent("click");
				menu.setMaterial(material);
				menu.setPublicNo(publicNo);
				wechatMenuService.saveOrUpdate(menu);
			} catch (Exception e) {
				e.printStackTrace();
				model.addAttribute("errormsg", "新增图文素材失败,原因:"+e.getMessage());
				model.addAttribute("menuid", menuId);
				return "redirect:/wechat/material/createSingleAndRedirect";
			}
		}else{
			//mediaId不为空则说明素材在预览的时候已经生成，这时候应该更新
			material = materialService.getByMediaId(mediaId);
			if(material != null){
				materialId = material.getId();
				material.setPublicNo(publicNo);
			}
			List<MaterialNews> articles = getArticles(material);
			if(CollectionUtils.isNotEmpty(articles)){
				for(MaterialNews article : articles){
					Response rep =	ApiMaterial.update(access_token,mediaId, article);
					if(rep.getStatus() == Response.SUCCESS){
						try {
							materialService.update(material);
						} catch (Exception e) {
							e.printStackTrace();
							model.addAttribute("errormsg", "新增图文素材失败,原因:"+e.getMessage());
						}
					}else{
						model.addAttribute("errormsg", "新增图文素材失败,原因:"+rep.getMessage());
					}
				}
			}
		}
		//跳转到群发界面
		model.addAttribute("materialId", materialId);
		model.addAttribute("menuid", menuId);
		return "redirect:/wechat/menu/index";
	}
	
	/**
	 * 保存多图文并跳转
	 * @param model
	 * @param contentsJson
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "/saveMultipleAndRedirect",method=RequestMethod.POST)
	@Permission(code="wechat/material/create")
	public String saveMultipleAndRedirect(Model model, String contentsJson,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		response.setContentType("text/html; charset=utf-8");
		String menuidStr = request.getParameter("menuid");
		long materialId = 0l;
		//多图文media_id
		String mediaId = request.getParameter("mediaId");
		Long menuId = 0L;
		try {
			menuId = Long.valueOf(menuidStr);
		} catch (Exception e) {
			throw new WechatException("新建图文信息失败,原因:获取菜单唯一标识错误");
		}
		
		PublicNo publicNo = (PublicNo) request.getSession().getAttribute(ConstantUtil.SESSION_CURRENT_PUBLICNO);
		if(publicNo == null){
			model.addAttribute("errormsg", "请先选择公众号");
			model.addAttribute("menuid", menuId);
			return "redirect:/wechat/material/createMultipleAndRedirect";
		}
		String access_token = TokenHolder.getAuthorizerAccessToken(publicNo.getAppid(), publicNo.getAuthorizer_refresh_token());
		
		List<Material> materials = JsonUtil.toList(contentsJson, Material.class);
		materials = getMaterials(materials,publicNo);
		
		//获取第一条，其他都是孩子
		Material material = getFirst(materials,publicNo);
		materials.remove(material);
		material.setChildren(materials);
		
		if(StringUtils.isBlank(mediaId)){
			//上传到腾讯服务器，返回media_id
			Response rep =	ApiMaterial.upload(access_token, getArticles(material));
			if(rep.getStatus() == Response.SUCCESS){
				mediaId = rep.get("media_id");
				rep = ApiMaterial.getMaterial(access_token, mediaId);
				JSONObject json = null;
				JSONArray newsItems = new JSONArray();
				if(rep.getStatus() == Response.SUCCESS){
					try {
						json = JSONObject.fromObject(rep.getParams().toString());
					} catch (Exception e) {
						e.printStackTrace();
					}
					if(json != null && json.containsKey("news_item")){
						newsItems = json.getJSONArray("news_item");
					}
					List<String> urls = new ArrayList<String>();
					for(int i=0; i<newsItems.size(); i++){
						JSONObject newsItem = newsItems.getJSONObject(i);
						urls.add(newsItem.getString("url"));
					}
					int j = 0;
					material.setContentUrl(urls.get(j++));
					for(Material child : material.getChildren()){
						child.setContentUrl(newsItems.getJSONObject(j++).getString("url"));
					}
				}
			}else{
				model.addAttribute("errormsg", "新增图文素材失败,原因:"+rep.getMessage());
				model.addAttribute("menuid", menuId);
				return "redirect:/wechat/material/createMultipleAndRedirect";
			}
			
			try {
				material.setMediaId(mediaId);
				materialId = materialService.save(material);
				material.setId(materialId);
				WechatMenu menu = wechatMenuService.get(menuId);
				menu.setEvent("click");
				menu.setMaterial(material);
				menu.setPublicNo(publicNo);
				wechatMenuService.saveOrUpdate(menu);
			} catch (Exception e) {
				e.printStackTrace();
				model.addAttribute("errormsg", "保存失败,原因:"+e.getMessage());
				model.addAttribute("menuid", menuId);
				return "redirect:/wechat/material/createMultipleAndRedirect";
			}
		}else{
			//mediaId不为空则说明素材在预览的时候已经生成，这时候应该更新
			material = materialService.getByMediaId(mediaId);
			if(material != null){
				materialId = material.getId();
				material.setPublicNo(publicNo);
			}
			List<MaterialNews> articles = getArticles(material);
			if(CollectionUtils.isNotEmpty(articles)){
				for(MaterialNews article : articles){
					Response rep =	ApiMaterial.update(access_token,mediaId, article);
					if(rep.getStatus() == Response.SUCCESS){
						try {
							materialService.update(material);
						} catch (Exception e) {
							e.printStackTrace();
							model.addAttribute("errormsg", "新增图文素材失败,原因:"+e.getMessage());
						}
					}else{
						model.addAttribute("errormsg", "新增图文素材失败,原因:"+rep.getMessage());
					}
				}
			}
		}
		//跳转到群发界面
		model.addAttribute("materialId", materialId);
		model.addAttribute("menuid", menuId);
		return "redirect:/wechat/menu/index";
	}
	
	
	@RequestMapping(value = "/edit/{id}")
	@Permission(code="wechat/material/create")
	public String edit(Model model, @PathVariable long id,
					   HttpServletRequest request, HttpServletResponse response) throws Exception {
		String cmd = request.getParameter("cmd");
		Material material = materialService.get(id);
		model.addAttribute("material",material);
		model.addAttribute("cmd", cmd);
		if(material != null && CollectionUtils.isEmpty(material.getChildren())){
			return "wechat/material/edit";
		}
		return "wechat/material/editMultiple";
	}

	/**
	 * 修改单图文
	 * @param model
	 * @param material
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "/update",method=RequestMethod.POST)
	@Permission(code="wechat/material/create")
	@ResponseBody
	public void update(Model model, Material material,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		response.setContentType("text/html; charset=utf-8");
		PublicNo publicNo = (PublicNo) request.getSession().getAttribute(ConstantUtil.SESSION_CURRENT_PUBLICNO);
		if(publicNo == null){
			response.getWriter().print(JsonUtil.toJSON(getFailure("请选择当前操作的公众号")));
			return;
		}
		String access_token = TokenHolder.getAuthorizerAccessToken(publicNo.getAppid(), publicNo.getAuthorizer_refresh_token());
		//media_id
		String mediaId = material.getMediaId();
		
		//上传到腾讯服务器，返回media_id
		MaterialNews article = new MaterialNews();
		article.setIndex(0);
		article.setAuthor(material.getAuthor());
		article.setContent(material.getContent());
		article.setContent_source_url(material.getOriginalUrl());
		article.setDigest(material.getDigest());
		article.setShow_cover_pic("on".equals(material.getShowCoverPic())?1:0);
		article.setThumb_media_id(material.getThumbMediaId());
		article.setTitle(material.getTitle());
		Response rep =	ApiMaterial.update(access_token,mediaId, article);
		if(rep.getStatus() != Response.SUCCESS){
			response.getWriter().print(JsonUtil.toJSON(getFailure("修改图文素材失败,原因："+rep.getMessage())));
			return;
		}
				
		material.setType(new Constant(9003L));
		material.setMediaId(mediaId);
		material.setPublicNo(publicNo);
		try {
			materialService.update(material);
		} catch (WechatException e) {
			response.getWriter().print(JsonUtil.toJSON(getFailure(e.getMessage())));
			return;
		}
		Map map = new HashMap();
		map.put("code", "success");
		map.put("message", "修改成功 ！");
		map.put("mediaId", mediaId);
		response.getWriter().print(JSON.toJSON(map));
	}
	
	
	@RequestMapping(value = "/editMultiple/{id}")
	@Permission(code="wechat/material/create")
	public String editMultiple(Model model, @PathVariable long id) throws Exception {
		Material material = materialService.get(id);
		model.addAttribute("material",material);
		return "wechat/material/editeditMultiple";
	}

	/**
	 * 修改多图文
	 * @param model
	 * @param contentsJson
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "/updateMultiple",method=RequestMethod.POST)
	@Permission(code="wechat/material/create")
	@ResponseBody
	public void updateMultiple(Model model, String contentsJson,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		response.setContentType("text/html; charset=utf-8");
		PublicNo publicNo = (PublicNo) request.getSession().getAttribute(ConstantUtil.SESSION_CURRENT_PUBLICNO);
		String mediaId = request.getParameter("MediaId");
		if(StringUtils.isBlank(mediaId)){
			response.getWriter().print(JsonUtil.toJSON(getFailure("更新失败,media_id为空,请刷新后重试")));
			return;
		}
		if(publicNo == null){
			response.getWriter().print(JsonUtil.toJSON(getFailure("修改失败,未选择当前操作的公众号")));
			return;
		}
		String access_token = TokenHolder.getAuthorizerAccessToken(publicNo.getAppid(), publicNo.getAuthorizer_refresh_token());
		
		List<Material> materials = null;;
		try {
			materials = JsonUtil.toList(contentsJson, Material.class);
		} catch (Exception e) {
			response.getWriter().print(JsonUtil.toJSON(getFailure("JSON解析异常,请刷新后重试")));
			return;
		}
		materials = getMaterials(materials,publicNo);
		//获取第一条，其他都是孩子
		Material material = getFirst(materials,publicNo);
		materials.remove(material);
		material.setChildren(materials);
		
		List<MaterialNews> articles = getArticles(material);
		if(CollectionUtils.isEmpty(articles)){
			response.getWriter().print(JsonUtil.toJSON(getFailure("修改图文素材失败,原因:图文列表为空")));
		}
		for(MaterialNews article : articles){
			Response rep =	ApiMaterial.update(access_token,mediaId, article);
			if(rep.getStatus() != Response.SUCCESS){
				response.getWriter().print(JsonUtil.toJSON(getFailure("修改素材失败,原因:"+rep.getMessage())));
				return;
			}
		}
		try {
			material.setType(new Constant(9003L));
			material.setMediaId(mediaId);
			materialService.update(material);
		} catch (Exception e) {
			response.getWriter().print(JSON.toJSON(getFailure("修改失败,原因:"+e.getMessage())));
			return;
		}
		Map map = new HashMap();
		map.put("code", "success");
		map.put("message", "修改成功 ！");
		map.put("mediaId", mediaId);
		response.getWriter().print(JSON.toJSON(map));
	}
	
	
	@RequestMapping(value = "/editAndRedirect/{id}")
	@Permission(code="wechat/material/create")
	public String editAndRedirect(Model model,HttpServletRequest request,@PathVariable long id) throws Exception {
		String menuid = request.getParameter("menuid");
		Material material = materialService.get(id);
		model.addAttribute("materialId", id);
		model.addAttribute("menuid", menuid);
		model.addAttribute("material",material);
		return "wechat/material/editAndRedirect";
	}
	
	/**
	 * 修改图文并跳转到菜单
	 * @param model
	 * @param material
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "/updateAndRedirect",method=RequestMethod.POST)
	@Permission(code="wechat/material/create")
	public String updateAndRedirect(Model model, String contentsJson,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		response.setContentType("text/html; charset=utf-8");
		String menuid = request.getParameter("menuid");
		String mediaId = request.getParameter("MediaId");
		long menuId = 0l;
		try {
			menuId = Long.valueOf(menuid);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String materialId = request.getParameter("materialId");
		long id = 0l;
		
		PublicNo publicNo = (PublicNo) request.getSession().getAttribute(ConstantUtil.SESSION_CURRENT_PUBLICNO);
		if(StringUtils.isBlank(mediaId)){
			model.addAttribute("errmsg", "修改失败,MediaId为空");
			model.addAttribute("menuid", menuid);
			return "redirect:/wechat/material/editAndRedirect/"+materialId;
		}
		if(publicNo == null){
			model.addAttribute("errmsg", "未选择当前公众号");
			model.addAttribute("menuid", menuid);
			return "redirect:/wechat/material/editAndRedirect/"+materialId;
		}
		String access_token = TokenHolder.getAuthorizerAccessToken(publicNo.getAppid(), publicNo.getAuthorizer_refresh_token());
		
		List<Material> materials = JsonUtil.toList(contentsJson, Material.class);
		materials = getMaterials(materials,publicNo);
		//获取第一条，其他都是孩子
		Material material = getFirst(materials,publicNo);
		materials.remove(material);
		material.setChildren(materials);
		
		List<MaterialNews> articles = getArticles(material);
		if(CollectionUtils.isEmpty(articles)){
			model.addAttribute("errmsg", "修改失败,原因:图文列表为空");
			model.addAttribute("menuid", menuid);
			return "redirect:/wechat/material/editAndRedirect/"+materialId;
		}
		for(MaterialNews article : articles){
			Response rep =	ApiMaterial.update(access_token,mediaId, article);
			if(rep.getStatus() != Response.SUCCESS){
				model.addAttribute("errmsg", "修改失败,原因:"+rep.getMessage());
				model.addAttribute("menuid", menuid);
				return "redirect:/wechat/material/editAndRedirect/"+materialId;
			}
		}
		try {
			material.setType(new Constant(9003L));
			material.setMediaId(mediaId);
			id = materialService.update(material);
			material.setId(id);
			WechatMenu menu = wechatMenuService.get(menuId);
			menu.setEvent("click");
			menu.setMaterial(material);
			menu.setPublicNo(publicNo);
			wechatMenuService.saveOrUpdate(menu);
		} catch (Exception e) {
			model.addAttribute("errmsg", "修改失败,原因:"+e.getMessage());
			model.addAttribute("menuid", menuid);
			return "redirect:/wechat/material/editAndRedirect/"+materialId;
		}
		model.addAttribute("materialId", id);
		model.addAttribute("menuid", menuid);
		return "redirect:/wechat/menu/index";
	}


	//删除单个图片
	@RequestMapping(value = "/delete")
	@Permission(code="wechat/material/publicno/delete")
	@ResponseBody
	public JSON delete(String ids,HttpServletRequest request,HttpServletResponse response) throws Exception {
		if(StringUtils.isBlank(ids)){
			return (JSON) JSON.toJSON(getFailure("删除失败，请刷新后重试 ！"));
		}
		
		//调用删除素材接口
		PublicNo publicNo = (PublicNo) request.getSession().getAttribute(ConstantUtil.SESSION_CURRENT_PUBLICNO);
		if(publicNo == null){
			return (JSON) JSON.toJSON(getFailure("删除失败,未选择当前操作的公众号"));
		}
		String access_token = TokenHolder.getAuthorizerAccessToken(publicNo.getAppid(), publicNo.getAuthorizer_refresh_token());
		
		String idsStr[] = ids.split(",");
		Long materialIds[] = new Long[idsStr.length];
		String errMsg = "";
		
		for(int i=0;i<idsStr.length;i++){
			try {
				materialIds[i] = Long.parseLong(idsStr[i]);
			} catch (Exception e) {}
			Material entity = materialService.get(materialIds[i]);
			if(entity == null){
				break;
			}
			//判断菜单或自动回复是否有使用该素材
			long count = wechatMenuService.countByMaterialId(entity.getId());
			if(count > 0){
				//有菜单使用该素材，不让其删除
				materialIds[i] = 0L;
				errMsg += entity.getTitle()+"被菜单使用,不能删除,";
				break;
			}
			count = autoReplyService.countByMaterialId(entity.getId());
			if(count > 0){
				//有自动回复使用该素材，不让其删除
				materialIds[i] = 0L;
				errMsg += entity.getTitle()+"被自动回复使用,不能删除,";
				break;
			}
			if(entity != null && entity.getType() != null && (entity.getType().getId() == 9002 || entity.getType().getId() == 9003)){
				Response rep = ApiMaterial.delete(access_token, entity.getMediaId());
			}
		}
		//备注：之前可能存在微信删除成功了，但是我们这边没删除的情况，如果别人引用了这些微信不存在的素材将会报invaid media_id
		//临时解决：不管微信有没有删除成功，都认为删除成功了
		try {
			materialService.delete(materialIds);
		} catch (WechatException e) {
			return (JSON) JSON.toJSON(getFailure(e.getMessage()));
		}
		if("".equals(errMsg)){
			return (JSON) JSON.toJSON(getSuccess("删除成功"));
		}else{
			return (JSON) JSON.toJSON(getSuccess(errMsg));
		}
		
	}
	
	/**
	 * 上传图片
	 * @param model
	 * @param image
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/uploadImage", method = RequestMethod.POST)
	@Permission(code="wechat/material/create")
	@ResponseBody
	public void uploadImage(Model model,
			@RequestParam(value = "image", required = true) MultipartFile image,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		response.setContentType("text/html; charset=utf-8");
		if(image == null){
		  response.getWriter().print(JsonUtil.toJSON(getFailure("请选择图片")));
		  return;
		}
		Material material = new Material();
		Long materialId = 0l;
		String mediaId = "";
		PublicNo publicNo = (PublicNo) request.getSession().getAttribute(ConstantUtil.SESSION_CURRENT_PUBLICNO);
		if(publicNo == null){
			response.getWriter().print(JsonUtil.toJSON(getFailure("请选择公众号后重试")));
			return;
		}
		String access_token = TokenHolder.getAuthorizerAccessToken(publicNo.getAppid(), publicNo.getAuthorizer_refresh_token());
		
		String group = request.getParameter("group");
		
		//上传文件到本地服务器
		try {
			material = uploadImage(image,material,request);
		} catch (WechatException e) {
			e.printStackTrace();
			response.getWriter().print(JsonUtil.toJSON(getFailure("图片上传失败,原因:"+e.getMessage())));
			return;
		}
		//上传到腾讯服务器，返回media_id
		Response rep =	ApiMaterial.upload(access_token, material.getFilepath());
		if(rep.getStatus() == Response.SUCCESS){
			mediaId = rep.get("media_id");
			String wxUrl = rep.get("url");
			if(StringUtils.isNotBlank(wxUrl)){
				material.setUrl(wxUrl);
				/*try {
					//删除文件
					FileUtil.deleteFile(material.getFilepath());
				} catch (Exception e) {
					e.printStackTrace();
				}*/
			}
			
		}else{
			response.getWriter().print(JsonUtil.toJSON(getFailure("图片上传失败,原因:"+rep.getMessage())));
			return;
		}
		/*if(StringUtils.isBlank(mediaId)){
			try {
				//删除文件
				FileUtil.deleteFile(material.getFilepath());
			} catch (Exception e) {
				e.printStackTrace();
			}
			response.getWriter().print(JsonUtil.toJSON(getFailure("图片上传失败,原因:"+rep.getMessage())));
			return;
		}*/
		//调用新增素材接口
		material.setIsOpen(false);
		material.setCreateDate(new Date());
		material.setPublicNo(publicNo);
		material.setMediaId(mediaId);
		//图片
		material.setType(new Constant(9002L));
		
		Long groupId = 0L;
		try {
			groupId = Long.parseLong(group);
			if(groupId > 0){
				material.setFolder(new MaterialFolder(groupId));
			}
		} catch (Exception e) {
		}
		
		try {
			materialId = materialService.save(material);
			Map json = getSuccess("保存成功 ！");
			json.put("materialId", materialId);
			json.put("title", material.getTitle());
			json.put("mediaId", mediaId);
			json.put("url", material.getUrl());
			json.put("filepath", material.getFilepath());
			response.getWriter().print(JsonUtil.toJSON(json));
		} catch (Exception e) {
			e.printStackTrace();
			response.getWriter().print(JsonUtil.toJSON(getFailure("保存失败,原因:"+e.getMessage())));
			return;
		}
	}
	
	/**
	 * 查询文件组
	 * @param model
	 * @param image
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "/searchFileGroup", method = RequestMethod.POST)
	@Permission(code="wechat/material/publicno")
	@ResponseBody
	public void searchFileGroup(Model model,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		response.setContentType("text/html; charset=utf-8");
		List<MaterialGroupDto> groups = materialService.searchFileGroup("9002");
		response.getWriter().print(JsonUtil.toJSON(groups));
	}
	
	/**
	 * 获取文件夹树
	 * @param model
	 * @param image
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "/getFolderTree", method = RequestMethod.POST)
	@Permission(code="wechat/material/publicno")
	@ResponseBody
	public void getFolderTree(Model model,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		response.setContentType("text/html; charset=utf-8");
		MaterialFolder node = materialFolderService.getFolderTree();
		List<MaterialFolder> nodes = new ArrayList<MaterialFolder>();
		nodes.add(node);
		response.getWriter().print(JsonUtil.toJSON(nodes));
	}
	
	@RequestMapping(value = "file")
	public void file(@RequestParam("file") MultipartFile file,HttpServletRequest request,HttpServletResponse response){  
		//文件保存目录URL
		PropertiyUtil propUtil = new PropertiyUtil("/wechat.properties");
		String saveUrl = propUtil.getString("share.url");
		//文件保存目录路径
		String saveDir = propUtil.getString("share.dir");
        File f1 = new File(saveDir);  
        if (!f1.exists()) {  
            f1.mkdirs();  
        }
		String name = file.getOriginalFilename();
        String randomName = getFileName(name);
		//最大文件
		long folderId = 1;
		//获取用户
		User loginUser = (User) request.getSession().getAttribute("loginUser");
		String folder = request.getParameter("folder");
		try {
			if(StringUtils.isNotBlank(folder))
			folderId = Long.valueOf(folder);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		FileOutputStream fos =null;
	    try {  
	        byte[] bytes = file.getBytes();  
	        fos = new FileOutputStream(saveDir+"/"+randomName);  
	        fos.write(bytes);
	        
			Material material = new Material();
			material.setIsOpen(false);
			material.setFolder(new MaterialFolder(folderId));
			//其它文件
			material.setType(new Constant(9006L));
			material.setCreateDate(new Date());
			material.setCreateUser(loginUser);
			material.setUrl(saveUrl+"/"+randomName);
			material.setFilepath(saveDir+"/"+randomName);
			material.setTitle(name);
			materialService.save(material);
		    response.getWriter().print(name);
	    } catch (Exception e) {
	        e.printStackTrace();  
	    }finally{
	    	if(fos!=null){
	    		try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
	    	}
	    } 
	}  
	
	/**
	 * 上传文件
	 * @param imgFile
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
	@Permission(code="wechat/material/share/create")
	@ResponseBody
	public void uploadFile(Model model,
			@RequestParam(value = "file",required = false) MultipartFile file,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		response.setContentType("text/html; charset=utf-8");
		long folderId = 1;
		String fileName = "";
		//文件保存目录路径
		PropertiyUtil propUtil = new PropertiyUtil("/wechat.properties");
		String saveDir = propUtil.getString("share.dir");
		//文件保存目录URL
		String saveUrl = propUtil.getString("share.url");
		//最大文件
		long maxSize = propUtil.getLong("share.maxSize");
		
		//获取用户
		User loginUser = (User) request.getSession().getAttribute("loginUser");
		String folder = request.getParameter("folder");
		try {
			folderId = Long.valueOf(folder);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			if (file.isEmpty()) {
				response.getWriter().print(JsonUtil.toJSON(getFailure("请选择文件！")));
				return;
			}
			// 文件名
			fileName = file.getOriginalFilename();
			long fileSize = file.getSize();
			// 检查文件大小
			if (fileSize > maxSize) {
				response.getWriter().print(JsonUtil.toJSON(getFailure("上传文件大小不能超过10M")));
				return;
			}
			try {
				String randomName = getFileName(fileName);
				File uploadedFile = new File(saveDir, randomName);
				file.transferTo(uploadedFile);
				Material material = new Material();
				material.setIsOpen(false);
				material.setFolder(new MaterialFolder(folderId));
				//其它文件
				material.setType(new Constant(9006L));
				material.setCreateDate(new Date());
				material.setCreateUser(loginUser);
				material.setUrl(saveUrl+"/"+randomName);
				material.setFilepath(uploadedFile.getPath());
				material.setTitle(fileName);
				materialService.save(material);
				response.getWriter().print(JsonUtil.toJSON(getSuccess("上传成功")));
			} catch (Exception e) {
				response.getWriter().print(JsonUtil.toJSON(getFailure("文件上传错误,原因:"+e.getMessage())));
				return;
			}
		} catch (Exception e) {
			response.getWriter().print(JsonUtil.toJSON(getFailure("文件上传错误,原因:"+e.getMessage())));
		}
	}
	
	/**
	 * 文件下载
	 * @param id
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/download/{id}")
	@Permission(code="wechat/material/publicno")
	public void download(@PathVariable("id") long id,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		response.setContentType("text/html;charset=utf-8");
		request.setCharacterEncoding("UTF-8");
		java.io.BufferedInputStream bis = null;
		java.io.BufferedOutputStream bos = null;
		
		//文件保存目录路径
		Material material = materialService.get(id);
		if(material == null){
			throw new WechatException("文件不存在");
		}
		String fileName = material.getTitle();
		if(StringUtils.isBlank(fileName)){
			throw new WechatException("下载错误，文件名为空");
		}
		String downLoadPath = material.getFilepath();
		try {
			long fileLength = new File(downLoadPath).length();
			response.setContentType("application/x-msdownload;");
			response.setHeader("Content-disposition", "attachment; filename="
					+ new String(fileName.getBytes("gb2312"), "iso-8859-1"));
			response.setHeader("Content-Length", String.valueOf(fileLength));
			bis = new BufferedInputStream(new FileInputStream(downLoadPath));
			bos = new BufferedOutputStream(response.getOutputStream());
			byte[] buff = new byte[2048];
			int bytesRead;
			while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
				bos.write(buff, 0, bytesRead);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bis != null)
				bis.close();
			if (bos != null)
				bos.close();
		}
		return;
	}
	
	/**
	 * 公用组件：从素材库选择素材
	 * @param model
	 * @param request
	 * @param response
	 * @param session
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/selectFromMaterial")
	@Permission(code="wechat/material/publicno")
	public ModelAndView selectFromMaterial(Model model, HttpServletRequest request,
			HttpServletResponse response, HttpSession session) throws Exception {
		ModelAndView view = new ModelAndView();
		view.setViewName("wechat/material/selectFormMaterial");
		
		String type = request.getParameter("type");
		if(StringUtils.isNotBlank(type)){
			if("image".equals(type)){
				model.addAttribute("materialAction", "searchImage");
			}else if("news".equals(type)){
				model.addAttribute("materialAction", "search");
			}
		}else{
			model.addAttribute("materialAction", "search");
		}
		
		// 返回到界面
		return view;
	}
	
	/**
	 * 创建文件夹
	 * @param name
	 * @param parent 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/saveFile", method = RequestMethod.POST)
	@ResponseBody
	public JSON saveFile(Model model,HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map map=new HashMap();
		MaterialFolder mf=materialFolderService.materialFolderInstance(Long.valueOf(request.getParameter("parent")));
		List<MaterialFolder> mfs=materialFolderService.getMaterialFolder(mf.getId());
		MaterialFolder mf1=new MaterialFolder();
		mf1.setName(request.getParameter("name"));
		mf1.setParent(mf);
		if(mfs.size()>0){
			mf1.setSort(mfs.get(0).getSort()+1);
		}else{
			mf1.setSort(1);
		}
		long id=materialFolderService.save(mf1);
		map.put("id",id);
		map.put("value", "添加成功");
		return (JSON) JSON.toJSON(map);
		
	}
	
	/**
	 * 修改文件夹
	 * @param name
	 * @param parent 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/updateFile", method = RequestMethod.POST)
	@ResponseBody
	public JSON updateFile(Model model,HttpServletRequest request, HttpServletResponse response) throws Exception {
		long folderId = 1l;
		try {
			folderId = Long.valueOf(request.getParameter("id"));
		} catch (Exception e) {
			e.printStackTrace();
			return (JSON) JSON.toJSON(getSuccess("失败成功,原因:id不能为空"));
		}
		MaterialFolder folder = materialFolderService.materialFolderInstance(folderId);
		folder.setName(request.getParameter("name"));
		materialFolderService.update(folder);
		return (JSON) JSON.toJSON(getSuccess("修改成功"));
		
	}
	
	/**
	 * 删除文件夹
	 * @param name
	 * @param parent 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/deleteFile", method = RequestMethod.POST)
	@ResponseBody
	public JSON deleteFile(Model model,HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map map=new HashMap();
		long id = 0l;
		try {
			id = Long.valueOf(request.getParameter("id"));
		} catch (Exception e) {
			e.printStackTrace();
			return (JSON) JSON.toJSON(getFailure("删除失败,原因:"+e.getMessage()));
		}
		boolean b = deleteFiles(id);
		map.put("value", "删除成功");
		return (JSON) JSON.toJSON(map);
		
	}
	
	@RequestMapping("preview")
    @ResponseBody
    public Map<String, Object> preview(HttpServletRequest request) {
        PublicNo publicNo = (PublicNo) request.getSession().getAttribute(ConstantUtil.SESSION_CURRENT_PUBLICNO);
        if(publicNo==null){
	        Map<String, Object> resultMap = new HashMap<String, Object>();
	        Response response = new Response(Response.ERROR,"在使用公众号相关信息前，请先设置当前公众号！");
	        resultMap.put("status", String.valueOf(response.getStatus()));
	        resultMap.put("message",response.getMessage());
	        resultMap.put("params", response.getParams());
	        return resultMap;
    	}
        String towxname = request.getParameter("towxname");
        String mediaId = request.getParameter("mediaId");
        
        if(StringUtils.isBlank(towxname)){
	        Map<String, Object> resultMap = new HashMap<String, Object>();
	        Response response = new Response(Response.ERROR,"请输入接收微信号！");
	        resultMap.put("status", String.valueOf(response.getStatus()));
	        resultMap.put("message",response.getMessage());
	        resultMap.put("params", response.getParams());
	        return resultMap;
        }
        
        Response response = new Response(Response.ERROR,"预览失败!");
        String access_token = TokenHolder.getAuthorizerAccessToken(publicNo.getAppid(), publicNo.getAuthorizer_refresh_token());
       
		if(StringUtils.isBlank(mediaId)) {
	        Map<String, Object> resultMap = new HashMap<String, Object>();
	        response = new Response(Response.ERROR,"预览前请先保存！");
	        resultMap.put("status", String.valueOf(response.getStatus()));
	        resultMap.put("message",response.getMessage());
	        resultMap.put("params", response.getParams());
	        return resultMap;
		}
		response = ApiMass.preview(access_token,"mpnews",mediaId,towxname);
    
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("status", String.valueOf(response.getStatus()));
        resultMap.put("message",response.getMessage());
        resultMap.put("params", response.getParams());

        return resultMap;
    }
	
	/**
	 * 上传图片素材
	 * @param imgFile
	 * @param material 
	 * @return
	 * @throws Exception
	 */
	private Material uploadImage(MultipartFile imgFile, Material material, HttpServletRequest request) throws Exception {
		//文件保存目录路径
		PropertiyUtil propUtil = new PropertiyUtil("/wechat.properties");
		//磁盘路径
		String saveDir = propUtil.getString("material.dir")+"/images/";
		//图片访问路径url
		String saveUrl = propUtil.getString("material.url")+"/images/";
		//定义允许上传的文件扩展名
		String[] types = propUtil.getString("material.images.type").split(",");
		//最大文件
		long maxSize = propUtil.getLong("material.images.maxSize");
		// 文件名
		String fileName = "";
		
		try {
			if (imgFile == null || imgFile.isEmpty()) {
				throw new WechatException("请选择图片");
			}
			//文件大小
			long fileSize = imgFile.getSize();
			fileName = imgFile.getOriginalFilename();
			// 检查文件大小
			if (fileSize > maxSize) {
				throw new WechatException("上传文件大小不能超过2M");
			}
			
			if(StringUtils.isBlank(fileName)){
				throw new WechatException("请选择图片");
			}
			
			// 检查扩展名
			String fileExt = fileName.substring(
					fileName.lastIndexOf(".") + 1).toLowerCase();
			
			if (!Arrays.<String> asList(types).contains(fileExt)) {
				throw new WechatException("只能上传 gif,jpg,jpeg,png,bmp类型的图片");
			}
			
			try {
				//通过当前时间生成文件名
				String randomName = getFileName(fileName);
				File uploadedFile = new File(saveDir, randomName);
				if(!uploadedFile.exists()){
					imgFile.transferTo(uploadedFile);
				}
				material.setTitle("未命名");
				material.setUrl(saveUrl+randomName);
				material.setFilepath(saveDir+randomName);
				return material;
			} catch (Exception e) {
				e.printStackTrace();
				throw new WechatException("文件上传错误,"+e.getMessage());
			}
		} catch (WechatException e) {
			e.printStackTrace();
			throw new WechatException(e.getMessage());
		}
	}
	
	/**
	 * 上传图片,上传的图片不占用公众号的素材库中图片数量的5000个的限制。图片仅支持jpg/png格式，大小必须在1MB以下。
	 * @param imgFile
	 * @param material 
	 * @return
	 * @throws Exception
	 */
	private Material uploadImg(MultipartFile imgFile, Material material, HttpServletRequest request) throws Exception {
		//文件保存目录路径
		PropertiyUtil propUtil = new PropertiyUtil("/wechat.properties");
		//磁盘路径
		String saveDir = propUtil.getString("material.dir")+"/images/";
		//图片访问路径url
		String saveUrl = propUtil.getString("material.url")+"/images/";
		//定义允许上传的文件扩展名
		String[] types = {"jpg","png"};
		//最大1M
		long maxSize = 1000000;
		// 文件名
		String fileName = "";
		
		try {
			if (imgFile == null || imgFile.isEmpty()) {
				throw new WechatException("请选择图片");
			}
			//文件大小
			long fileSize = imgFile.getSize();
			fileName = imgFile.getOriginalFilename();
			// 检查文件大小
			if (fileSize > maxSize) {
				throw new WechatException("上传文件大小不能超过1M");
			}
			
			if(StringUtils.isBlank(fileName)){
				throw new WechatException("请选择图片");
			}
			
			// 检查扩展名
			String fileExt = fileName.substring(
					fileName.lastIndexOf(".") + 1).toLowerCase();
			
			if (!Arrays.<String> asList(types).contains(fileExt)) {
				throw new WechatException("只能上传 jpg,png类型的图片");
			}
			
			try {
				//通过当前时间生成文件名
				String randomName = getFileName(fileName);
				File uploadedFile = new File(saveDir, randomName);
				if(!uploadedFile.exists()){
					imgFile.transferTo(uploadedFile);
				}
				material.setTitle(randomName);
				//普通图片
				material.setType(new Constant(9007L));
				material.setUrl(saveUrl+randomName);
				material.setFilepath(saveDir+randomName);
				return material;
			} catch (Exception e) {
				e.printStackTrace();
				throw new WechatException("文件上传错误,"+e.getMessage());
			}
		} catch (WechatException e) {
			e.printStackTrace();
			throw new WechatException(e.getMessage());
		}
	}
	
	private Boolean deleteFiles(Long parentId) throws Exception{
		boolean b=true;
		MaterialFolder folder=materialFolderService.materialFolderInstance(parentId);
		List<MaterialFolder> folders=materialFolderService.getMaterialFolders(parentId);
		if(CollectionUtils.isNotEmpty(folders)){
			for(MaterialFolder f : folders){
				b=deleteFiles(f.getId());
			}
		}
		List<Material> materials = materialFolderService.getMaterials(folder.getId());
		for(Material material : materials){
			FileUtil.deleteFile(material.getFilepath());
			materialService.deleteMaterialInstance(material.getId());
		}
		materialFolderService.deleteMaterialFolderInstance(folder.getId());
		
		return b;
	}
	
	private Material getFirst(List<Material> materials,PublicNo publicNo) {
		if(CollectionUtils.isNotEmpty(materials)){
			for(Material material : materials){
				if(material.getSort() == 0){
					materials.remove(material);
					material.setChildren(materials);
					material.setIsOpen(false);
					material.setCreateDate(new Date());
					material.setPublicNo(publicNo);
					material.setType(new Constant(9003L));
					return material;
				}
			}
		}
		return null;
	}
	

	private List<Material> getMaterials(List<Material> materials, PublicNo publicNo) {
		List<Material> result = new ArrayList<Material>();
		if(CollectionUtils.isEmpty(materials)){
			return null;
		}
		for(Material material : materials){
			material.setIsOpen(false);
			material.setCreateDate(new Date());
			material.setPublicNo(publicNo);
			material.setType(new Constant(9003L));
			//调用上传素材接口
			String mediaId = "";
			material.setMediaId(mediaId);
			result.add(material);
		}
		
		return result;
		
	}
	
	private List<MaterialNews> getArticles(Material material) {
		List<MaterialNews> articles = new ArrayList<MaterialNews>();
		MaterialNews article = new MaterialNews();
		article.setIndex(0);
		article.setAuthor(material.getAuthor());
		article.setContent(material.getContent());
		article.setContent_source_url(material.getOriginalUrl());
		article.setDigest(material.getDigest());
		article.setShow_cover_pic("on".equals(material.getShowCoverPic())?1:0);
		article.setThumb_media_id(material.getThumbMediaId());
		article.setTitle(material.getTitle());
		articles.add(article);
		if(CollectionUtils.isNotEmpty(material.getChildren())){
			int j = 1;
			for(Material child : material.getChildren()){
				MaterialNews art = new MaterialNews();
				art.setIndex(j++);
				art.setIndex(child.getSort());
				art.setAuthor(child.getAuthor());
				art.setContent(child.getContent());
				art.setContent_source_url(child.getOriginalUrl());
				art.setDigest(child.getDigest());
				art.setShow_cover_pic("on".equals(child.getShowCoverPic())?1:0);
				art.setThumb_media_id(child.getThumbMediaId());
				art.setTitle(child.getTitle());
				articles.add(art);
			}
		}
		return articles;
	}
	
	private synchronized String getFileName(String fileName){
		//扩展名
		String ext = "";
		if(StringUtils.isNotBlank(fileName)){
			ext = fileName.substring(
					fileName.lastIndexOf(".") + 1).toLowerCase();
		}
		return System.currentTimeMillis()+"."+ext;
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
	
	/**
	 * 修改图片名
	 * @param model
	 * @param id
	 * @param title
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "/updateImg", method = RequestMethod.POST)
	@Permission(code="wechat/material/index")
	@ResponseBody
	public void updateImg(Model model ,Long id ,String title,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Material material=materialService.get(id);
		model.addAttribute("material", material);
		// 公众号获取
		PublicNo publicNo = (PublicNo) request.getSession().getAttribute(ConstantUtil.SESSION_CURRENT_PUBLICNO);
		//设置
		material.setPublicNo(publicNo);
		material.setTitle(title);
		//更新
	    materialService.updateMaterial(material);
	    //响应
		response.getWriter().print(JsonUtil.toJSON(getSuccess("修改图片名称成功！")));
	}
	
	/**
	 * 上传图文消息内的图片获取URL 
	 * 请注意，本接口所上传的图片不占用公众号的素材库中图片数量的5000个的限制。
	 * 图片仅支持jpg/png格式，大小必须在1MB以下
	 */
	@RequestMapping(value = "/uploadImg", method = RequestMethod.POST)
	@Permission(code="wechat/material/create")
	@ResponseBody
	public void uploadImg(Model model,
			@RequestParam(value = "media", required = true) MultipartFile media,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		response.setContentType("text/html; charset=utf-8");
		if(media == null){
		  response.getWriter().print(JsonUtil.toJSON(getFailure("请选择图片")));
		  return;
		}
		Material material = new Material();
		Long materialId = 0l;
		//微信返回的url
		String wxUrl = "";
		PublicNo publicNo = (PublicNo) request.getSession().getAttribute(ConstantUtil.SESSION_CURRENT_PUBLICNO);
		if(publicNo == null){
			response.getWriter().print(JsonUtil.toJSON(getFailure("请选择公众号后重试")));
			return;
		}
		String access_token = TokenHolder.getAuthorizerAccessToken(publicNo.getAppid(), publicNo.getAuthorizer_refresh_token());
		
		//上传文件到本地服务器
		try {
			material = uploadImg(media,material,request);
		} catch (WechatException e) {
			e.printStackTrace();
			response.getWriter().print(JsonUtil.toJSON(getFailure("图片上传失败,原因:"+e.getMessage())));
			return;
		}
		//上传到腾讯服务器，返回url
		/*Response rep =	ApiMaterial.uploadImg(access_token, material.getFilepath());
		if(rep.getStatus() == Response.SUCCESS){
			wxUrl = rep.get("url");
			if(StringUtils.isNotBlank(wxUrl)){
				material.setUrl(wxUrl);
			}
			
		}
		if(StringUtils.isBlank(wxUrl)){
			try {
				//删除文件
				FileUtil.deleteFile(material.getFilepath());
			} catch (Exception e) {
				e.printStackTrace();
			}
			response.getWriter().print(JsonUtil.toJSON(getFailure("图片上传失败,原因:"+rep.getMessage())));
			return;
		}*/
		material.setIsOpen(false);
		material.setCreateDate(new Date());
		material.setPublicNo(publicNo);
		
		try {
			materialId = materialService.save(material);
			Map json = getSuccess("保存成功 ！");
			json.put("materialId", materialId);
			json.put("title", material.getTitle());
			json.put("url", material.getUrl());
			json.put("filepath", material.getFilepath());
			response.getWriter().print(JsonUtil.toJSON(json));
		} catch (Exception e) {
			e.printStackTrace();
			response.getWriter().print(JsonUtil.toJSON(getFailure("保存失败,原因:"+e.getMessage())));
			return;
		}
	}
	
	/**
	 * 查询图片
	 * 用于图文素材正文图片（包含图片素材和上传到微信的普通图片）
	 * @param model
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = {"searchImg"})
	@Permission(code="wechat/material/publicno")
	@ResponseBody
	public void searchImg(Model model, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		response.setContentType("text/html; charset=utf-8");
		int pageSize   = 10;
		int currentPage = 1;
		Map<String,Integer> paginateParams = new HashMap<String,Integer>();
		Map<String, Object> params = new HashMap<String,Object>();
		try {
			currentPage = Integer.parseInt(request.getParameter("currentPage"));
			pageSize = Integer.parseInt(request.getParameter("pageSize"));
		} catch (Exception e) {}
		
		PublicNo publicNo = (PublicNo) request.getSession().getAttribute(ConstantUtil.SESSION_CURRENT_PUBLICNO);
		if(publicNo == null){
			response.getWriter().print(JsonUtil.toJSON(getFailure("未选择当前操作的公众号")));
			return;
		}
		params.put("publicnoId", publicNo.getId());
		
		String condition = request.getParameter("parameter[condition]");
		if(StringUtils.isNotBlank(condition)){
			condition=new String(condition.getBytes("ISO-8859-1"),"UTF-8" );
			params.put("title", "%"+condition+"%");
			params.put("author", "%"+condition+"%");
			params.put("digest", "%"+condition+"%");
		}
		
		paginateParams.put("offset", (currentPage-1)*pageSize);
		paginateParams.put("max", pageSize);
		
		List<Material> rows = materialService.getImgs(params,paginateParams);
		int count = materialService.getImgTotalRow(params);
		Pagination pagination = new Pagination(count,pageSize,currentPage);
		
		Map<String,Object> json = new HashMap<String,Object>();
		json.put("paginationData", pagination);
		json.put("data", rows);
		response.getWriter().print(JsonUtil.toJSON(dateFormat, json));
	}
}
