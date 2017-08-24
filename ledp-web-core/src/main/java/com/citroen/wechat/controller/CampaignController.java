package com.citroen.wechat.controller;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.citroen.ledp.dao.mybaits.MybaitsGenericDao;
import com.citroen.ledp.domain.Dealer;
import com.citroen.ledp.domain.Organization;
import com.citroen.ledp.exception.LedpException;
import com.citroen.ledp.interceptor.Permission;
import com.citroen.ledp.service.PermissionService;
import com.citroen.ledp.util.DateUtil;
import com.citroen.ledp.util.PropertiyUtil;
import com.citroen.wechat.domain.Campaign;
import com.citroen.wechat.domain.Plugin;
import com.citroen.wechat.domain.PluginRecord;
import com.citroen.wechat.domain.PublicNo;
import com.citroen.wechat.domain.Site;
import com.citroen.wechat.domain.SitePage;
import com.citroen.wechat.domain.Template;
import com.citroen.wechat.domain.TemplatePage;
import com.citroen.wechat.service.CampaignService;
import com.citroen.wechat.service.PluginService;
import com.citroen.wechat.service.SitePageService;
import com.citroen.wechat.service.SiteService;
import com.citroen.wechat.service.TemplatePageService;
import com.citroen.wechat.service.TemplateService;
import com.citroen.wechat.util.ConstantUtil;
import com.citroen.wechat.util.FileUtil;
import com.citroen.wechat.util.HandleHtml;
import com.citroen.wechat.util.Pagination;

/**
 * 活动管理
 * @author 何海粟
 * @date2015年6月4日
 */
@Controller("campaignController")
@RequestMapping("/wechat/campaign")
public class CampaignController {
	private Map<String, Object> params;
	@Autowired
	private PermissionService permissionService;
	@Autowired
	private CampaignService campaignService;
	@Autowired
	private MybaitsGenericDao<Long> genericDao;
	@Autowired
	private TemplateService templateService;
	@Autowired
	private SitePageService sitePageService;
	@Autowired
	private SiteService siteService;
	@Autowired
	private TemplatePageService templatePageService;
	@Autowired
	private PluginService pluginService;
	
	@RequestMapping(value={"","index"})
	public ModelAndView index(Model model,HttpServletRequest request,HttpServletResponse response,HttpSession session) throws Exception {
		PublicNo publicNo = (PublicNo) request.getSession().getAttribute(ConstantUtil.SESSION_CURRENT_PUBLICNO);
		if(publicNo==null){
			return new ModelAndView("redirect:/wechat/publicno/message");
		}
		
		ModelAndView view = new ModelAndView();
		view.setViewName("wechat/campaign/index");
		/*if(permissionService.hasAuth("news/auth")){
			model.addAttribute("permission", "permission");
		}*/
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
		String imgDir = propUtil.getString("campaign.images.url");
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
		List<Campaign> rows = campaignService.executeQuery(params);
		int total = campaignService.getTotalRow(params);
		boolean editHref=false;
		if (permissionService.hasAuth("wechat/campaign/update")) {
			editHref=true;
		}
		boolean deleteHref=false;
		if (permissionService.hasAuth("wechat/campaign/delete")) {
			deleteHref=true;
		}
		boolean pageHref=true;
		/*if (permissionService.hasAuth("wechat/campaign/update")) {
			pageHref=true;
		}*/
		List list=new ArrayList();
		Date date=new Date();
		for(int i=0;i<rows.size();i++){
			Campaign campaign=rows.get(i);
			Map map=new HashMap();
			map.put("img", imgDir+campaign.getBak1());
			String content="";
			if(campaign.getComment().length()>20){
				content=campaign.getComment().substring(0, 19);
				content+="......";
			}else{
				content=campaign.getComment();
			}
			map.put("name", campaign.getName());
			if(campaign.getBeginDate().after(date)){
				map.put("state", "未开始");
			}
			if(campaign.getBeginDate().before(date) && campaign.getEndDate().after(date)){
				map.put("state", "进行中");
			}
			if(campaign.getEndDate().before(date)){
				map.put("state", "已结束");
			}
			map.put("content", content);
			map.put("time", DateUtil.format(campaign.getBeginDate(),"yyyy-MM-dd HH:mm")+" - "+DateUtil.format(campaign.getEndDate(),"yyyy-MM-dd HH:mm"));
			map.put("lookHref",  contextPath + "/wechat/campaign/detail/" + campaign.getId());
			if(session.getAttribute("loginDealer")!=null){
				if(editHref){
					map.put("editHref", contextPath + "/wechat/campaign/update/" + campaign.getId());
				}
				if(deleteHref){
					map.put("deleteHref", "<a  href='javascript:void(0);' title='删除' class='activeDelete' attr-id='" + campaign.getId()+ "' >删除</a>");
				}
			}
			if(pageHref){
				map.put("pageHref", contextPath + "/wechat/campaign/pageManage/" + campaign.getId());
			}
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
	@RequestMapping(value="/detail/{id}")
	public String detail(Model model,@PathVariable long id,HttpServletRequest request) throws LedpException {
		PropertiyUtil propUtil = new PropertiyUtil("/wechat.properties");
		String imgDir = propUtil.getString("campaign.images.url");
		model.addAttribute("imgDir", imgDir);
		Campaign campaign=campaignService.campaignInstance(id);
		model.addAttribute("campaign", campaign);
		if(new Date().before(campaign.getBeginDate())){
			model.addAttribute("isshow", "isshow");
		}
		return "wechat/campaign/detail";
	}
	@RequestMapping(value="/create")
	@Permission(code="wechat/campaign/create")
	public String create(Model model,HttpServletRequest request) throws Exception {
		return "wechat/campaign/create";
	}
	@RequestMapping(value="/save", method = RequestMethod.POST)
	@Permission(code="wechat/campaign/create")
	public String save(Model model,@RequestParam(value = "image", required = false) MultipartFile image,HttpServletRequest request, HttpServletResponse response) throws Exception {
		String code=FileUtil.getNowDateH();
		String saveView=request.getParameter("save_view");
		SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd HH:mm" );
		Campaign campaign=new Campaign();
		campaign.setName(request.getParameter("name"));
		campaign.setBeginDate(sdf.parse(request.getParameter("beginDate")));
		campaign.setEndDate(sdf.parse(request.getParameter("endDate")));
		campaign.setComment(request.getParameter("comment"));
		campaign.setCode(code);
		if(image!=null && image.getSize() > 0){
			String bak1=uploadImage(image,code);
			campaign.setBak1(bak1);
		}
		HttpSession session = request.getSession();
		PublicNo publicNo = (PublicNo) session.getAttribute(ConstantUtil.SESSION_CURRENT_PUBLICNO);
		campaign.setPublicNo(publicNo);
		if(session.getAttribute("loginDealer")!=null){
			campaign.setDealer((Dealer)session.getAttribute("loginDealer"));
		}else{
			//大区，总部创建活动
			campaign.setOrg((Organization) session.getAttribute("loginOrg"));
		}
		if(campaign!=null){
			Long campaignId = campaignService.save(campaign);
			Campaign campaign1=campaignService.campaignInstance(campaignId);
			model.addAttribute("campaign", campaign1);
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
				return "wechat/campaign/copyTemplate";
			}else{
				return "redirect:/wechat/campaign/index";
			}
		}
		return "redirect:/wechat/campaign/create";
	}
	@RequestMapping(value="/update/{id}")
	@Permission(code="wechat/campaign/update")
	public String update(Model model,@PathVariable long id,HttpServletRequest request) throws LedpException {
		PropertiyUtil propUtil = new PropertiyUtil("/wechat.properties");
		String imgDir = propUtil.getString("campaign.images.url");
		model.addAttribute("imgDir", imgDir);
		Campaign campaign=campaignService.campaignInstance(id);
		model.addAttribute("campaign", campaign);
		return "wechat/campaign/update";
	}
	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	@com.citroen.ledp.interceptor.Permission(code={"wechat/campaign/update"})
	public String edit(Model model,@RequestParam(value = "image", required = false) MultipartFile image,HttpServletRequest request) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd HH:mm" );
		String saveView=request.getParameter("save_view");
		Campaign campaign=campaignService.campaignInstance(Long.valueOf(request.getParameter("id")));
		campaign.setName(request.getParameter("name"));
		campaign.setBeginDate(sdf.parse(request.getParameter("beginDate")));
		campaign.setEndDate(sdf.parse(request.getParameter("endDate")));
		campaign.setComment(request.getParameter("comment"));
		if(image!=null && image.getSize() > 0){
			String bak1=uploadImage(image,campaign.getCode());
			campaign.setBak1(bak1);
		}
		genericDao.update(campaign);
		Campaign campaign1=campaignService.campaignInstance(campaign.getId());
		model.addAttribute("campaign", campaign1);
		if(saveView.equals("saveCreate")){
			PropertiyUtil propUtil = new PropertiyUtil("/wechat.properties");
			String imgDir = propUtil.getString("template.images.url");
			model.addAttribute("imgDir",imgDir);
			Site site=siteService.getSite(campaign1.getId());
			if(site!=null){
				model.addAttribute("site",site);
			}
			params.put("type",1);
			List<Template> templates=campaignService.getTemplates(params);
			model.addAttribute("templates",templates);
			params.put("type",0);
			List<Template> template1s=campaignService.getTemplates(params);
			model.addAttribute("template1s",template1s);
			return "wechat/campaign/copyTemplate";
		}else{
			return "redirect:/wechat/campaign/index";
		}
	}
	
	@RequestMapping(value = "/callBack")
	public String callBack(Model model,HttpServletRequest request) throws Exception {
		Long campaignId=null;
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
			campaignId=Long.valueOf(request.getParameter("campaignId"));
		}catch(Exception e){
			campaignId=null;
		}
		try{
			siteId=Long.valueOf(request.getParameter("siteId"));
		}catch(Exception e){
			siteId=null;
		}
		if(campaignId!=null){
			Campaign campaign1=campaignService.campaignInstance(campaignId);
			model.addAttribute("campaign", campaign1);
			Site site=siteService.getSite(campaign1.getId());
			if(site!=null){
				model.addAttribute("site",site);
			}
			return "wechat/campaign/copyTemplate";
		}else{
			Site site1=siteService.siteInstance(siteId);
			model.addAttribute("site", site1);
			return "wechat/site/copyTemplate";
		}
	}
	
	@RequestMapping(value="/delete/{id}")
	@Permission(code="wechat/campaign/delete")
	@ResponseBody
	public JSON delete(@PathVariable long id) throws LedpException {
		Campaign campaign=campaignService.campaignInstance(id);
		//删除有关活动关联的表数据及其网站界面
		Site site=siteService.getSite(campaign.getId());
		if(site!=null){
			String path=site.getUrl();
			String sql="delete from t_site_page where site="+site.getId();
			genericDao.execute(sql);
			siteService.deleteSiteInstance(site.getId());
			File file=new File(path);
			FileUtil.deleteFiles(file);
		}
		//删除活动的缩略图
		FileUtil.deleteFile(campaign.getBak1());
		campaignService.deleteCampaignInstance(id);
		Map map=new HashMap();
		map.put("code", 200);
		map.put("value", "删除成功");
		return (JSON) JSON.toJSON(map);
	}
	@RequestMapping(value="/pageUpdate")
	public String pageUpdate(Model model,HttpServletRequest request) throws LedpException {
		Campaign campaign=campaignService.campaignInstance(Long.valueOf(request.getParameter("campaign")));
		Site site=siteService.getSite(Long.valueOf(request.getParameter("campaign")));
		model.addAttribute("campaign", campaign);
		/*if(site==null){
			params.put("type",1);
			List<Template> templates=campaignService.getTemplates(params);
			model.addAttribute("templates",templates);
			params.put("type",0);
			List<Template> template1s=campaignService.getTemplates(params);
			model.addAttribute("template1s",template1s);
			return "wechat/campaign/copyTemplate";
		}*/
		PropertiyUtil propUtil = new PropertiyUtil("/wechat.properties");
		String imgDir = propUtil.getString("campaign.url");
		model.addAttribute("imgDir",imgDir+"/"+campaign.getCode());
		
		SitePage sitePage=sitePageService.siteInstance(Long.valueOf(request.getParameter("sitePageId")));
		List<SitePage> sitePages=sitePageService.getSitePages(site.getId());
		model.addAttribute("site",site);
		model.addAttribute("sitePages",sitePages);
		model.addAttribute("sitePage1",sitePage);
		return "wechat/campaign/copy";
	}
	
	@RequestMapping(value="/copy", method = RequestMethod.POST)
	public String copy(Model model,HttpServletRequest request, HttpServletResponse response) throws Exception {
		Template template=templateService.templateInstance(Long.valueOf(request.getParameter("templateId")));
		Campaign campaign=campaignService.campaignInstance(Long.valueOf(request.getParameter("campaignId")));
		Site site1=siteService.getSite(Long.valueOf(request.getParameter("campaignId")));
		
		PropertiyUtil propUtil = new PropertiyUtil("/wechat.properties");
		String imgDir = propUtil.getString("campaign.url");
		model.addAttribute("imgDir",imgDir+"/"+campaign.getCode());
		if(site1==null){
			
			Site site=campaignService.copyTemplate(campaign, template);
			List<SitePage> sitePages=sitePageService.getSitePages(site.getId());
			model.addAttribute("site",site);
			model.addAttribute("sitePages",sitePages);
		}else{
			if(site1.getTemplate().getId()!=Long.valueOf(request.getParameter("templateId"))){
				//选择不同模板时的方法
				Site site=siteService.getSite(campaign.getId());
				if(site!=null){
					String path=site.getUrl();
					String sql="delete from t_site_page where site="+site.getId();
					genericDao.execute(sql);
					siteService.deleteSiteInstance(site.getId());
					File file=new File(path);
					FileUtil.deleteFiles(file);
				}
				Site s=campaignService.copyTemplate(campaign, template);
				List<SitePage> sitePages=sitePageService.getSitePages(s.getId());
				model.addAttribute("site",s);
				model.addAttribute("sitePages",sitePages);
				
			}else{
				List<SitePage> sitePages=sitePageService.getSitePages(site1.getId());
				model.addAttribute("site",site1);
				model.addAttribute("sitePages",sitePages);
			}
		}
		
		return "wechat/campaign/copy";
	}
	
	@RequestMapping(value="/detailSitePage/{id}")
	@ResponseBody
	public JSON detailSitePage(@PathVariable long id) throws LedpException {
		
		Map map=new HashMap();
		SitePage sitePage=sitePageService.siteInstance(id);
		String html = "";
		
		if(sitePage != null && sitePage.getSite() != null){
			String editFilePath = sitePage.getSite().getUrl()+"/"+sitePage.getUrl().split(".html")[0]+".edit";
			html = FileUtil.readFile(editFilePath);
		}
		map.put("html", StringUtils.isBlank(html)?sitePage.getHtml():html);
		map.put("id", sitePage.getId());
		map.put("name", sitePage.getName());
		return (JSON) JSON.toJSON(map);
	}
	@RequestMapping(value = { "savePage" })
	@ResponseBody
	public JSON savePage(Model model, HttpServletRequest request) throws Exception {
		HttpSession session=request.getSession();
		Map map=new HashMap();
		SitePage sitePage=sitePageService.siteInstance(Long.valueOf(request.getParameter("id")));
		
		String html=FileUtil.readFile(sitePage.getSite().getUrl()+sitePage.getUrl().split(".html")[0]+".edit");
		//读取文件读不到再查数据库
		if(StringUtils.isBlank(html)){
			html = sitePage.getHtml();
		}
		String conten=request.getParameter("content");
		
		Map map1=new HashMap();
		map1=HandleHtml.updateHtml(html, conten);
		if(Boolean.valueOf(map1.get("isUpdat").toString())){
			List<SitePage> sitePages=sitePageService.getSitePages(sitePage.getSite().getId(), sitePage.getId());
			Site site=siteService.siteInstance(sitePage.getSite().getId());
			for(int i=0;i<sitePages.size();i++){
				SitePage child=sitePages.get(i);
				String oldChildHtml = FileUtil.readFile(child.getSite().getUrl()+child.getUrl().split(".html")[0]+".edit");
				String childhtml=HandleHtml.newHtml(oldChildHtml, map1.get("html").toString());
				child.setHtml(childhtml);
				genericDao.update(child);
				/**
				 * 说明：以前模板编辑时是从数据库读取html字符串在前台编辑
				 * 现在修改为读取文件，暂时保留数据的，以保证以前创建的微站能正常编辑
				 */
				String editFilePath = site.getUrl()+child.getUrl().split(".html")[0]+".edit";
				FileUtil.writeOverRideFile(editFilePath, childhtml);
				File file = new File(site.getTemplate().getUrl()+child.getUrl());
				if(file.isDirectory()){
					childhtml=getStringText(childhtml,child.getId(),site.getTemplate().getUrl()+child.getUrl(),site.getUrl()+"/modalPageTemplate.text");
				}else{
					TemplatePage templatePage=templatePageService.getTemplatePageN(site.getTemplate().getId());
					childhtml=getStringText(childhtml,child.getId(),site.getTemplate().getUrl()+templatePage.getUrl(),site.getUrl()+"/modalPageTemplate.text");
				}
				FileUtil.writeFile(site.getUrl()+child.getUrl(), childhtml);	
			}
		}
		conten = HandleHtml.getsqlHtml(conten);
		sitePage.setHtml(conten);
		genericDao.update(sitePage);
		/**
		 * 说明：以前模板编辑时是从数据库读取html字符串在前台编辑
		 * 现在修改为读取文件，暂时保留数据的，以保证以前创建的微站能正常编辑
		 */
		String editFilePath = sitePage.getSite().getUrl()+sitePage.getUrl().split(".html")[0]+".edit";
		FileUtil.writeOverRideFile(editFilePath, conten);
		List<Plugin> list=getPlugins(conten);
		File file = new File(sitePage.getSite().getTemplate().getUrl()+sitePage.getUrl());
		if(file.isDirectory()){
			conten=getStringText(conten,sitePage.getId(),sitePage.getSite().getTemplate().getUrl()+sitePage.getUrl(),sitePage.getSite().getUrl()+"/modalPageTemplate.text");
		}else{
			TemplatePage templatePage=templatePageService.getTemplatePageN(sitePage.getSite().getTemplate().getId());
			conten=getStringText(conten,sitePage.getId(),sitePage.getSite().getTemplate().getUrl()+templatePage.getUrl(),sitePage.getSite().getUrl()+"/modalPageTemplate.text");
		}
		FileUtil.writeFile(sitePage.getSite().getUrl()+sitePage.getUrl(), conten);
		pluginService.deletePluginRecord(sitePage.getId());
		for(int i=0;i<list.size();i++){
			Plugin plugin=list.get(i);
			PluginRecord pr=new PluginRecord();
			if(sitePage.getSite().getCampaign()!=null){
				pr.setCampaign(sitePage.getSite().getCampaign());
			}
			pr.setCreateDate(new Date());
			pr.setPage(sitePage);
			pr.setPlugin(plugin);
			if(session.getAttribute("loginDealer")!=null){
				pr.setDealer((Dealer)session.getAttribute("loginDealer"));
				pr.setOrg(((Dealer)session.getAttribute("loginDealer")).getOrganization());
			}else{
				pr.setOrg((Organization) session.getAttribute("loginOrg"));
			}
			pluginService.savePluginRecord(pr);
		}
		
		
		map.put("value", "保存成功");
		return (JSON) JSON.toJSON(map);
	}
	
	@RequestMapping(value="/searchSitePage")
	@ResponseBody
	public JSON searchSitePage(Model model, HttpServletRequest request) throws LedpException {
		
		List list=new ArrayList();
		PropertiyUtil propUtil = new PropertiyUtil("/wechat.properties");
		String imgDir = propUtil.getString("campaign.url");
		String webDir = propUtil.getString("campaign.url");
		Site site1=siteService.getSite(Long.valueOf(request.getParameter("parameter[campaignId]")));
		
		List<SitePage> sitePages=new ArrayList<SitePage>();
		
		HttpSession session=request.getSession();
		if(site1!=null){
			sitePages=sitePageService.getSitePages(site1.getId());
			for(int i=0;i<sitePages.size();i++){
				SitePage sitePage = sitePages.get(i);
				Map map=new HashMap();
				map.put("id", sitePages.get(i).getId());
				map.put("imgSrc", imgDir+"/"+site1.getCampaign().getCode()+sitePages.get(i).getFilePath());
				map.put("title", sitePages.get(i).getName());
				map.put("webUrl", webDir+"/"+site1.getCampaign().getCode()+sitePages.get(i).getUrl());
				map.put("canDelete", StringUtils.isNotBlank(sitePage.getBak1())?1:0);
				if(new Date().before(site1.getCampaign().getBeginDate())){
					map.put("end","end");
				}
				if(session.getAttribute("loginDealer")!=null){
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
		Campaign campaign=campaignService.campaignInstance(id);
		model.addAttribute("campaign", campaign);
		return "wechat/campaign/pageManage";
	}
	
	@RequestMapping(value="/deleteSitePage/{id}")
	@ResponseBody
	public JSON deleteSitePage(@PathVariable long id) throws LedpException {
		Map map=new HashMap();
		SitePage sitePage=sitePageService.siteInstance(id);
		
		FileUtil.deleteFile(sitePage.getSite().getUrl()+sitePage.getUrl());
		sitePageService.deletesitePageInstance(id);
		map.put("value", "删除成功");
		return (JSON) JSON.toJSON(map);
	}
	
	@RequestMapping(value="/deleteSitePages")
	@ResponseBody
	public JSON deleteSitePages(Model model, HttpServletRequest request) throws LedpException {
		String[] list=request.getParameterValues("list[]");
		Map map=new HashMap();
		if(list.length>0){
			for(int i=0;i<list.length;i++){
				long id=Long.valueOf(list[i]);
				SitePage sitePage=sitePageService.siteInstance(id);
				FileUtil.deleteFile(sitePage.getSite().getUrl()+sitePage.getUrl());
				sitePageService.deletesitePageInstance(id);
			}
			map.put("value", "删除成功");
		}else{
			map.put("value", "请选择要删除的界面");
		}
		return (JSON) JSON.toJSON(map);
	}

	@RequestMapping(value="/createNewPage/{id}")
	@ResponseBody
	public JSON createNewPage(@PathVariable long id) throws Exception {
		Map map=new HashMap();
		Site site=siteService.siteInstance(id);
		TemplatePage templatePage=templatePageService.getTemplatePageN(site.getTemplate().getId());
		PropertiyUtil propUtil = new PropertiyUtil("/wechat.properties");
		String imgDir = propUtil.getString("campaign.url");
		String imgDir1 = propUtil.getString("site.url");
		String fileName=getFileName(templatePage.getUrl());
		
		SitePage sitePage=new SitePage();
		sitePage.setFilePath(templatePage.getThumbPicUrl());
		sitePage.setIsHome(templatePage.getIsHome());
		sitePage.setSite(site);
		sitePage.setUrl("/"+fileName);
		sitePage.setBak1("new");
		String html=FileUtil.readFile(templatePage.getTemplate().getUrl()+templatePage.getUrl().split(".html")[0]+".edit");
		if(site.getCampaign()!=null){
			html=html.replaceAll("href=\"css", "href=\""+imgDir+"/"+site.getCampaign().getCode()+"/css");
		    html=html.replaceAll("src=\"js", "src=\""+imgDir+"/"+site.getCampaign().getCode()+"/js");
		    html=html.replaceAll("src=\"image", "src=\""+imgDir+"/"+site.getCampaign().getCode()+"/image");
		}else{
			html=html.replaceAll("href=\"css", "href=\""+imgDir1+"/"+site.getCode()+"/css");
		    html=html.replaceAll("src=\"js", "src=\""+imgDir1+"/"+site.getCode()+"/js");
		    html=html.replaceAll("src=\"image", "src=\""+imgDir1+"/"+site.getCode()+"/image");
		}
		
		//sitePage.setHtml(html);
		long sitePageId=sitePageService.save(sitePage);
		sitePage=sitePageService.siteInstance(sitePageId);
		FileUtil.copyFile(site.getTemplate().getUrl()+templatePage.getUrl(), site.getUrl()+sitePage.getUrl());
		/**
		 * 说明：以前模板编辑时是从数据库读取html字符串在前台编辑
		 * 现在修改为读取文件，暂时保留数据的，以保证以前创建的微站能正常编辑
		 */
		String editFilePath = site.getUrl()+sitePage.getUrl().split(".html")[0]+".edit";
		FileUtil.writeOverRideFile(editFilePath, html);
		
		
		map.put("id", sitePage.getId());
		if(site.getCampaign()==null){
			FileUtil.updateFile(site.getUrl()+sitePage.getUrl(), sitePageId,site.getCode(),"site");
			map.put("img", imgDir1+"/"+site.getCode()+sitePage.getFilePath());
		}else{
			FileUtil.updateFile(site.getUrl()+sitePage.getUrl(), sitePageId,site.getCampaign().getCode(),"campaign");
			map.put("img", imgDir+"/"+site.getCampaign().getCode()+sitePage.getFilePath());
		}
		
		map.put("html", html);
		return (JSON) JSON.toJSON(map);
	}
	
	@RequestMapping(value="/saveNewPageNmae")
	@ResponseBody
	public JSON saveNewPageNmae(Model model,HttpServletRequest request) throws Exception {
		Map map=new HashMap();

		SitePage sitePage=sitePageService.siteInstance(Long.valueOf(request.getParameter("id")));
		sitePage.setName(request.getParameter("name"));
		genericDao.update(sitePage);
		map.put("name", sitePage.getId());
		return (JSON) JSON.toJSON(map);
	}
	
	
	@RequestMapping(value="/getSitePageNameUrl/{id}")
	@ResponseBody
	public JSON getSitePageNameUrl(@PathVariable long id) throws LedpException {
		Map<String,String> map=sitePageService.getNameValue(id);
		return (JSON) JSON.toJSON(map);
	}
	
	@RequestMapping(value="/getUrl/{id}")
	public String getUrl(Model model,@PathVariable long id,HttpServletRequest request) throws LedpException {
		PropertiyUtil propUtil = new PropertiyUtil("/wechat.properties");
		String saveDir = propUtil.getString("campaign.url");
		String saveDir1 = propUtil.getString("site.url");
		SitePage sitePage=sitePageService.sitePageInstance(id);
		if(sitePage.getSite().getCampaign()==null){
			model.addAttribute("url", saveDir1+"/"+sitePage.getSite().getCode()+sitePage.getUrl());
		}else{
			model.addAttribute("url", saveDir+"/"+sitePage.getSite().getCampaign().getCode()+sitePage.getUrl());
		}
		return "wechat/site/layer";
	}
	
	private String getStringText(String content,long id,String url,String jsPath) throws Exception{
		String head=HandleHtml.getHead(url);
		String html=HandleHtml.getNewHtml(content,id,head,jsPath);
		return html;
	}
	private String getFileName(String str) throws Exception{
		String path=str.substring(0, str.lastIndexOf("/")+1);	
		String suffix=str.substring(str.indexOf("."));
		String name=FileUtil.getNowDateH();
		return path+name+suffix;
	}
	private String uploadImage(MultipartFile imgFile,String code) throws Exception {
		
		PropertiyUtil propUtil = new PropertiyUtil("/wechat.properties");
		String saveDir = propUtil.getString("campaign.images.dir");
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
	public List<Plugin> getPlugins(String html){
		Document doc = Jsoup.parse(html);
		Elements links=doc.getElementsByClass("replacePlugin");
		List<Plugin> list=new ArrayList<Plugin>();
		for (Element link : links) {
			String type=link.attr("replacetype");
			if("rotatyTable".equals(type) || "share".equals(type) || "answer".equals(type) 
					|| "purchase".equals(type) || "coupon".equals(type)){
				Plugin plugin=null;
				try {
					plugin = pluginService.getByCode(type);
					if(plugin != null){
						list.add(plugin);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		}
		return list;
	}
}
