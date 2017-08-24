package com.citroen.wechat.controller;

import com.alibaba.fastjson.JSON;
import com.citroen.ledp.dao.mybaits.MybaitsGenericDao;
import com.citroen.ledp.domain.Dealer;
import com.citroen.ledp.domain.Organization;
import com.citroen.ledp.domain.User;
import com.citroen.ledp.exception.LedpException;
import com.citroen.ledp.interceptor.Permission;
import com.citroen.ledp.service.PermissionService;
import com.citroen.ledp.util.PropertiyUtil;
import com.citroen.wechat.domain.*;
import com.citroen.wechat.service.*;
import com.citroen.wechat.util.ConstantUtil;
import com.citroen.wechat.util.FileUtil;
import com.citroen.wechat.util.Pagination;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 站点管理
 * @author 何海粟
 * @date2015年6月4日
 */
@Controller("siteController")
@RequestMapping("/wechat/site")
public class SiteController {
	private Map<String, Object> params;
	@Autowired
	private PermissionService permissionService;
	@Autowired
	private SiteService siteService;
	@Autowired
	private SitePageService sitePageService;
	@Autowired
	private CampaignService campaignService;
	@Autowired
	private MybaitsGenericDao<Long> genericDao;
	@Autowired
	private TemplateService templateService;
	@Autowired
	private WechatMenuService wechatMenuService;
	
	@RequestMapping(value={"","index"})
	public ModelAndView index(Model model,HttpServletRequest request,HttpServletResponse response,HttpSession session) throws Exception {
		PublicNo publicNo = (PublicNo) request.getSession().getAttribute(ConstantUtil.SESSION_CURRENT_PUBLICNO);
		if(publicNo==null){
			return new ModelAndView("redirect:/wechat/publicno/message");
		}
		
		ModelAndView view = new ModelAndView();
		view.setViewName("wechat/site/index");
		if(session.getAttribute("loginDealer")!=null){
			model.addAttribute("dealer", (Dealer)session.getAttribute("loginDealer"));
		}
		// 返回到界面
		return view;
	}
	@RequestMapping(value = { "search" })
	@ResponseBody
	public JSON search(Model model, HttpServletRequest request) throws Exception {
		
		final String contextPath = request.getContextPath();
		PropertiyUtil propUtil = new PropertiyUtil("/wechat.properties");
		String saveDir = propUtil.getString("site.url");
		String imgDir = propUtil.getString("site.images.url");
		int pageSize = 10;
		int pageNumber = 1;
		String sortName = request.getParameter("sortName");
		String sortOrder = request.getParameter("sortOrder");
		if (StringUtils.isBlank(sortName)) {
			sortName = "begindate";
		}
		if (StringUtils.isBlank(sortOrder)) {
			sortOrder = "desc";
		}
		try {
			pageSize = Integer.parseInt(request.getParameter("pageSize"));
		} catch (Exception e) {
		}
		try {
			pageNumber = Integer.parseInt(request.getParameter("currentPage"));
		} catch (Exception e) {
		}
		HttpSession session = request.getSession();
		if(session.getAttribute("loginDealer")!=null){
			params.put("type_user", "dealer");
			Dealer dealer=(Dealer) session.getAttribute("loginDealer");
			params.put("userLogin", dealer.getId());
		}else{
			params.put("type_user", "org");
			Organization org=(Organization) session.getAttribute("loginOrg");
			params.put("userLogin", org.getId());
		}
		params.put("pageSize", pageSize);
		params.put("pageNumber", pageNumber);
		params.put("sortName", sortName);
		params.put("sortOrder", sortOrder);
		List<Site> rows = siteService.executeQuery(params);
		int total = siteService.getTotalRow(params);
		boolean editHref=false;
		if (permissionService.hasAuth("wechat/site/update")) {
			editHref=true;
		}
		boolean deleteHref=false;
		if (permissionService.hasAuth("wechat/site/delete")) {
			deleteHref=true;
		}
		boolean pageHref=false;
		if (permissionService.hasAuth("wechat/site/update")) {
			pageHref=true;
		}
		List list=new ArrayList();
		Date date=new Date();
		for(int i=0;i<rows.size();i++){
			Site site=rows.get(i);
			Map map=new HashMap();
			map.put("img", imgDir+site.getBak1());
			map.put("name", site.getName());
			String content="";
			if(site.getBak2().length()>20){
				content=site.getBak2().substring(0, 19);
				content+="......";
			}else{
				content=site.getBak2();
			}
			map.put("comment", content);
			map.put("id",site.getId());
			if(site.getTemplate()!=null){
				map.put("model", site.getTemplate().getName());
			}
			if(site.getIsPublish()==1){
				SitePage sitePage=sitePageService.getSitePage(site.getId());
				if(sitePage!=null){
					map.put("url", saveDir+"/"+site.getCode()+sitePage.getUrl());
				}
			}
			map.put("preview",  "<a  href='javascript:void(0);' title='预览' style='color:#d12d32;' class='floatL marginLeft10 siteDaile' attr-id='" + site.getId()+ "' >预览</a>");
			if(session.getAttribute("loginDealer")!=null){
				if(editHref){
					map.put("edit", contextPath + "/wechat/site/update/" + site.getId());
				}
				if(deleteHref){
					map.put("delete", "<a  href='javascript:void(0);' title='删除' style='color:#d12d32;' class=' floatL marginLeft10 siteDelete' attr-id='" + site.getId()+ "' >删除</a>");
				}
				if(pageHref){
					map.put("app", "<a  href='javascript:void(0);' title='应用' style='color:#d12d32;' class='floatL marginLeft10 siteApp' attr-id='" + site.getId()+ "' >应用</a>");
				}
			}
			map.put("manage", "<a  href='/wechat/site/pageManage/" + site.getId() +"' title='界面管理' style='color:#d12d32;float:right;margin-left:5px;'>界面管理</a>");
			list.add(map);
		}
		Pagination page=new Pagination(total,pageSize,pageNumber);
		/*Map map2=new HashMap();
		map2.put("currentPage", page.getCurrentPage());
		map2.put("pageNumber", page.getPageNumber());
		map2.put("pages", page.getPages());*/
		Map map1=new HashMap();
		map1.put("paginationData", page);
		map1.put("data", list);
		return (JSON) JSON.toJSON(map1);
	}
	@RequestMapping(value="/create")
	@Permission(code="wechat/site/create")
	public String create(Model model,HttpServletRequest request) throws Exception {
		return "wechat/site/create";
	}
	@RequestMapping(value="/save", method = RequestMethod.POST)
	@Permission(code="wechat/campaign/create")
	public String save(Model model,@RequestParam(value = "image", required = false) MultipartFile image,HttpServletRequest request, HttpServletResponse response) throws Exception {
		String code=FileUtil.getNowDateH();
		HttpSession session = request.getSession();
		PublicNo publicNo = (PublicNo) session.getAttribute(ConstantUtil.SESSION_CURRENT_PUBLICNO);
		String saveView=request.getParameter("save_view");
		SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd HH:mm" );
		Site site=new Site();
		site.setName(request.getParameter("name"));
		site.setCreateTime(new Date());
		site.setIsPublish(0);;
		site.setCreateUser((User)session.getAttribute("loginUser"));
		site.setBak2(request.getParameter("bak2"));
		site.setCode(code);
		site.setPublicNo(publicNo);
		if(image!=null && image.getSize() > 0){
			String bak1=uploadImage(image,code);
			site.setBak1(bak1);
		}
		site.setDealer((Dealer) session.getAttribute("loginDealer"));
		if(site!=null){
			Long siteId = siteService.save(site);
			Site site1=siteService.siteInstance(siteId);
			model.addAttribute("site", site1);
			if(saveView.equals("saveCreate")){
				PropertiyUtil propUtil = new PropertiyUtil("/wechat.properties");
				String imgDir = propUtil.getString("template.images.url");
				model.addAttribute("imgDir",imgDir);
				params.put("type",1);
				List<Template> templates=campaignService.getTemplates(params);
				model.addAttribute("templates",templates);
				params.put("type",0);
				List<Template> template1s=campaignService.getTemplates(params);
				model.addAttribute("template1s",template1s);
				return "wechat/site/copyTemplate";
			}else{
				return "redirect:/wechat/site/index";
			}
		}
		return "redirect:/wechat/site/create";
	}
	
	
	@RequestMapping(value="/update/{id}")
	@Permission(code="wechat/site/update")
	public String update(Model model,@PathVariable long id,HttpServletRequest request) throws LedpException {
		PropertiyUtil propUtil = new PropertiyUtil("/wechat.properties");
		String imgDir = propUtil.getString("site.images.url");
		model.addAttribute("imgDir", imgDir);
		PublicNo publicNo = (PublicNo) request.getSession().getAttribute(ConstantUtil.SESSION_CURRENT_PUBLICNO);
		Site site=siteService.siteInstance(id);
		site.setPublicNo(publicNo);
		model.addAttribute("site", site);
		return "wechat/site/update";
	}
	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	@com.citroen.ledp.interceptor.Permission(code={"wechat/site/update"})
	public String edit(Model model,@RequestParam(value = "image", required = false) MultipartFile image,HttpServletRequest request) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd HH:mm" );
		PublicNo publicNo = (PublicNo) request.getSession().getAttribute(ConstantUtil.SESSION_CURRENT_PUBLICNO);
		String saveView = request.getParameter("save_view");
		Site site = siteService.siteInstance(Long.valueOf(request.getParameter("id")));
		site.setPublicNo(publicNo);
		site.setName(request.getParameter("name"));
		site.setBak2(request.getParameter("bak2"));
		if(image!=null && image.getSize() > 0) {
			String bak1=uploadImage(image,site.getCode());
			site.setBak1(bak1);
		}
		genericDao.update(site);
		Site site1=siteService.siteInstance(site.getId());
		model.addAttribute("site", site1);
		if(saveView.equals("saveCreate")){
			PropertiyUtil propUtil = new PropertiyUtil("/wechat.properties");
			String imgDir = propUtil.getString("template.images.url");
			model.addAttribute("imgDir",imgDir);
			model.addAttribute("site",site1);
			params.put("type",1);
			List<Template> templates=campaignService.getTemplates(params);
			model.addAttribute("templates",templates);
			params.put("type",0);
			List<Template> template1s=campaignService.getTemplates(params);
			model.addAttribute("template1s",template1s);
			return "wechat/site/copyTemplate";
		}else{
			return "redirect:/wechat/site/index";
		}
	}
	@RequestMapping(value="/delete/{id}")
	@Permission(code="wechat/site/delete")
	@ResponseBody
	public JSON delete(@PathVariable long id) throws LedpException {
		Site site=siteService.siteInstance(id);
		
		List<SitePage> sitePage=sitePageService.getSitePages(id);
		if(sitePage.size()>0){
			String sql="delete from t_site_page where site="+site.getId();
			genericDao.execute(sql);
			File file=new File(site.getUrl());
			FileUtil.deleteFiles(file);
		}
		//删除活动的缩略图
		FileUtil.deleteFile(site.getBak1());
		siteService.deleteSiteInstance(site.getId());
		Map map=new HashMap();
		map.put("code", 200);
		map.put("value", "删除成功");
		return (JSON) JSON.toJSON(map);
	}
	
	@RequestMapping(value="/copy", method = RequestMethod.POST)
	public String copy(Model model,HttpServletRequest request, HttpServletResponse response) throws Exception {
		Template template=templateService.templateInstance(Long.valueOf(request.getParameter("templateId")));
		Site site1=siteService.siteInstance(Long.valueOf(request.getParameter("siteId")));
		PropertiyUtil propUtil = new PropertiyUtil("/wechat.properties");
		String imgDir = propUtil.getString("site.url");
		model.addAttribute("imgDir", imgDir + "/" + site1.getCode());

		if (site1.getTemplate()==null){
			Site site = siteService.copyTemplate(site1, template);
			List<SitePage> sitePages=sitePageService.getSitePages(site.getId());
			model.addAttribute("site", site);
			model.addAttribute("sitePages", sitePages);
		}else{
			if(site1.getTemplate().getId()!=Long.valueOf(request.getParameter("templateId"))){
				//选择不同模板时的方法
				List<SitePage> sitePage=sitePageService.getSitePages(site1.getId());
				if(sitePage.size()>0){
					String sql="delete from t_site_page where site="+site1.getId();
					genericDao.execute(sql);
					File file=new File(site1.getUrl());
					FileUtil.deleteFiles(file);
				}
				Site site=siteService.copyTemplate(site1, template);
				List<SitePage> sitePages=sitePageService.getSitePages(site.getId());
				model.addAttribute("site",site);
				model.addAttribute("sitePages",sitePages);
				
			}else{
				List<SitePage> sitePages=sitePageService.getSitePages(site1.getId());
				model.addAttribute("site",site1);
				model.addAttribute("sitePages",sitePages);
			}
		}
		
		return "wechat/campaign/copy";
	}
	@RequestMapping(value="/updateIspublic/{id}")
	@ResponseBody
	public JSON updateIspublic(@PathVariable long id,HttpServletRequest request) throws Exception {
		Map map=new HashMap();
		Site site=siteService.siteInstance(id);
		List<SitePage> sitePages=sitePageService.getSitePages(site.getId());
		PublicNo publicNo = (PublicNo) request.getSession().getAttribute(ConstantUtil.SESSION_CURRENT_PUBLICNO);
		List<WechatMenu> wechatMenus = null;
		String oldCode = "";
		if(publicNo!=null){
			String query = "select code from t_site where ispublic=1 and dealer="+site.getDealer().getId();
			Map codeMap = genericDao.find(query);
			if(codeMap != null){
				try {
					oldCode = codeMap.get("code").toString();
				} catch (Exception e) {}
			}
			wechatMenus=wechatMenuService.getMenus(null, publicNo.getId());
			if(CollectionUtils.isNotEmpty(wechatMenus)){
				for(WechatMenu menu : wechatMenus){
					if(StringUtils.isNotBlank(menu.getUrl())){
						if(StringUtils.isNotBlank(oldCode)){
							String url = menu.getUrl().replace(oldCode,site.getCode());
							menu.setUrl(url);
						}
						genericDao.update(menu);
					}
					if(CollectionUtils.isNotEmpty(menu.getChildren())){
						for(WechatMenu child : menu.getChildren()){
							if(StringUtils.isNotBlank(child.getUrl())){
								if(StringUtils.isNotBlank(oldCode)){
									String url = child.getUrl().replace(oldCode,site.getCode());
									child.setUrl(url);
								}
								genericDao.update(child);
							}
						}
					}
				}
			}
		}
		if(sitePages.size()>0){
			String sql="update t_site set ispublic=0 where dealer="+site.getDealer().getId();
			genericDao.execute(sql);
			site.setIsPublish(1);
			genericDao.update(site);
			map.put("code", "200");
			map.put("value", "应用成功");
		}else{
			map.put("code", "400");
			map.put("value", "请先选择模板");
		}
		return (JSON) JSON.toJSON(map);
	}
	
	@RequestMapping(value="/selectSitePage/{id}")
	@ResponseBody
	public JSON selectSitePage(@PathVariable long id) throws LedpException {
		Map map=new HashMap();
		List<SitePage> sitePages=sitePageService.getSitePages(id);
		if(sitePages.size()>0){
			map.put("code", "200");
		}else{
			map.put("code", "400");
			map.put("value", "请先选择模板");
		}
		return (JSON) JSON.toJSON(map);
	}
	@RequestMapping(value="/getUrl/{id}")
	public String getUrl(Model model,@PathVariable long id,HttpServletRequest request) throws LedpException {
		PropertiyUtil propUtil = new PropertiyUtil("/wechat.properties");
		String saveDir = propUtil.getString("site.url");
		Site site=siteService.siteInstance(id);
		SitePage sitePage=sitePageService.getSitePage(id);
		model.addAttribute("url", saveDir+"/"+site.getCode()+sitePage.getUrl());
		return "wechat/site/layer";
	}

	@RequestMapping(value="/getUrlPage/{id}")
	public String getUrlPage(Model model, @PathVariable long id,HttpServletRequest request) throws LedpException {
		PropertiyUtil propUtil = new PropertiyUtil("/wechat.properties");
		String saveDir = propUtil.getString("site.url");
		SitePage sitePage=sitePage=sitePageService.sitePageInstance(id);
		Site site=sitePage.getSite();
		model.addAttribute("url", saveDir + "/" + site.getCode() + sitePage.getUrl());
		return "wechat/site/layer";
	}

	@RequestMapping(value="/searchSitePage")
	@ResponseBody
	public JSON searchSitePage(Model model, HttpServletRequest request) throws LedpException {
		List list=new ArrayList();
		PropertiyUtil propUtil = new PropertiyUtil("/wechat.properties");
		String imgDir = propUtil.getString("site.url");
		String webDir = propUtil.getString("site.url");
		Long siteId = Long.valueOf(request.getParameter("parameter[siteId]"));
		Site site1=siteService.siteInstance(siteId);
		List<SitePage> sitePages=new ArrayList<SitePage>();

		HttpSession session=request.getSession();
		if(site1!=null){
			sitePages=sitePageService.getSitePages(site1.getId());
			for(int i=0;i<sitePages.size();i++){
				SitePage sitePage = sitePages.get(i);
				Map map=new HashMap();
				map.put("id", sitePages.get(i).getId());
				map.put("imgSrc", imgDir+"/"+site1.getCode()+sitePages.get(i).getFilePath());
				map.put("title", sitePages.get(i).getName());
				map.put("webUrl", webDir+"/"+site1.getCode()+sitePages.get(i).getUrl());
				map.put("canDelete", StringUtils.isNotBlank(sitePage.getBak1())?1:0);
				if(session.getAttribute("loginDealer")!=null) {
					map.put("dealer", "dealer");
				}
				list.add(map);
			}
		}
		Pagination page=new Pagination(sitePages.size(),sitePages.size(),1);

		Map map1=new HashMap();
		map1.put("paginationData", page);
		map1.put("data", list);
		return (JSON) JSON.toJSON(map1);
	}

	@RequestMapping(value="/pageManage/{id}")
	public String pageManage(Model model,@PathVariable long id,HttpServletRequest request) throws LedpException {
		Site site = siteService.siteInstance(id);
		List<SitePage> sitePages = sitePageService.getSitePages(id);
		model.addAttribute("sitePages", sitePages);
		model.addAttribute("site", site);
		return "wechat/site/pageManage";
	}

	@RequestMapping(value="/pageUpdate")
	public String pageUpdate(Model model,HttpServletRequest request) throws LedpException {
		//Site site=siteService.siteInstance(Long.valueOf(request.getParameter("site")));
		Long siteId = Long.valueOf(request.getParameter("site"));
		Site site = siteService.siteInstance(siteId);
		model.addAttribute("site", site);
		PropertiyUtil propUtil = new PropertiyUtil("/wechat.properties");
		String imgDir = propUtil.getString("site.url");
		model.addAttribute("imgDir",imgDir + "/" + site.getCode());

		SitePage sitePage=sitePageService.siteInstance(Long.valueOf(request.getParameter("sitePageId")));
		List<SitePage> sitePages=sitePageService.getSitePages(site.getId());
		model.addAttribute("site",site);
		model.addAttribute("sitePages",sitePages);
		model.addAttribute("sitePage1",sitePage);
		return "wechat/site/copy";
	}

	@RequestMapping(value = "/callBack")
	public String callBack(Model model,HttpServletRequest request) throws Exception {
		Long siteId=null;
		PropertiyUtil propUtil = new PropertiyUtil("/wechat.properties");
		String imgDir = propUtil.getString("template.images.url");
		model.addAttribute("imgDir",imgDir);

		params.put("type",1);
		List<Template> templates=campaignService.getTemplates(params);
		model.addAttribute("templates",templates);
		params.put("type",0);
		List<Template> template1s=campaignService.getTemplates(params);
		model.addAttribute("template1s",template1s);
		try{
			siteId=Long.valueOf(request.getParameter("siteId"));
		}catch(Exception e){
			siteId=null;
		}
		Site site1=siteService.siteInstance(siteId);
		model.addAttribute("site", site1);
		return "wechat/site/copyTemplate";
	}

	private String uploadImage(MultipartFile imgFile,String code) throws Exception {
			
		PropertiyUtil propUtil = new PropertiyUtil("/wechat.properties");
		String saveDir = propUtil.getString("site.images.dir");
		// 文件名
		File file1=new File(saveDir);
		if(!file1.exists()  && !file1.isDirectory()){
			file1.mkdir();
		}
		
		String	fileName = imgFile.getOriginalFilename();
		String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
		File file=new File(saveDir,code+"."+fileExt);
		imgFile.transferTo(file);
		return "/"+code+"."+fileExt;
	}
}
