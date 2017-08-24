package com.citroen.ledp.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.citroen.ledp.binder.FieldBinder;
import com.citroen.ledp.dao.mybaits.MybaitsGenericDao;
import com.citroen.ledp.domain.Constant;
import com.citroen.ledp.domain.Dealer;
import com.citroen.ledp.domain.Media;
import com.citroen.ledp.domain.News;
import com.citroen.ledp.domain.Organization;
import com.citroen.ledp.domain.User;
import com.citroen.ledp.domain.VehicleSeries;
import com.citroen.ledp.exception.LedpException;
import com.citroen.ledp.interceptor.Permission;
import com.citroen.ledp.service.ConstantService;
import com.citroen.ledp.service.DealerService;
import com.citroen.ledp.service.MediaService;
import com.citroen.ledp.service.NewsService;
import com.citroen.ledp.service.PermissionService;
import com.citroen.ledp.service.VehicleSeriesService;
import com.citroen.ledp.util.JSONConverter;
import com.citroen.ledp.util.LedpLogger;
import com.citroen.ledp.util.LedpLogger.Operation;
import com.citroen.ledp.util.LedpLogger.Result;

/**
 * @Title: NewsController.java
 * @Package com.citroen.ledp.controller
 * @Description: TODO(信息管理类)
 * @author 廖启洪
 * @date 2015年1月29日 下午3:24:24
 * @version V1.0
 */
@Controller
@RequestMapping("/news")
public class NewsController {
	private Map<String,Object> params;
	@Autowired
	private VehicleSeriesService vehicleSeriesService;
	@Autowired
	private MediaService mediaService;
	@Autowired
	private NewsService newsService;
	@Autowired
	private DealerService dealerService;
	@Autowired
	private ConstantService constantService;
	@Autowired
	private PermissionService permissionService;
	@Autowired
	private MybaitsGenericDao<Long> mybaitsGenericDao;
	
	@RequestMapping(value={"","/index"})
	@Permission(code="news/index")
	public String index(Model model,HttpServletRequest request) throws Exception {
		// 返回到界面
		if(permissionService.hasAuth("news/create")){
			model.addAttribute("permission","permission");
			model.addAttribute("parent", null);
		}
		//获取用户
		User loginUser = (User) request.getSession().getAttribute("loginUser");
		//获取组织机构
		Organization org = loginUser.getOrg();
		//网点
		Dealer dealer = loginUser.getDealer();
		if(null == org && null!= dealer){ //网点用户
			model.addAttribute("dealer","true");
		}else{
			model.addAttribute("dealer","false");
		}
		return "news/index";
	}

	/**
	 * @Title: create
	 * @Description: TODO(添加信息入口)
	 * @param model 参数传递容器
	 * @throws Exception
	 * @return String
	 */
	@RequestMapping(value="/create")
	@Permission(code="news/create")
	public String create(Model model,HttpServletRequest request) throws Exception {
		News news = new News();
		List<Long> checkedMedias = new ArrayList<Long>();
		
		List<VehicleSeries> seriesList = mybaitsGenericDao.executeQuery(VehicleSeries.class,"select * from t_vehicle_series where status=1010 order by id asc");
		model.addAttribute("seriesList",seriesList);
		model.addAttribute("typeList",constantService.findAll("news_type"));
		model.addAttribute("checkedMedias",checkedMedias);
		model.addAttribute("checkedSerieses",new ArrayList<Long>());
		Dealer dealer = (Dealer)request.getSession().getAttribute("loginDealer");
		if(dealer!=null){
			model.addAttribute("medias",newsService.getDealerMediaList(dealer.getId()));
		}else{
			model.addAttribute("medias",new ArrayList<Media>());
		}
		model.addAttribute("news",news);
		return "news/create";
	}

	/**
	 * @Title: save
	 * @Description: TODO(新增信息方法)
	 * @param model 参数传递容器
	 * @throws Exception
	 * @return String
	 */
	@RequestMapping(value="/save",method=RequestMethod.POST)
	@Permission(code="news/create")
	public String save(Model model,@Valid @ModelAttribute("news") News news,HttpServletRequest request,BindingResult br) throws Exception {
		HttpSession session = request.getSession();
		String medias = "";
		Dealer dealer = (Dealer)request.getSession().getAttribute("loginDealer");
		List<Media> medias1=new ArrayList<Media>(); 
		List<Long> checkedMedias = new ArrayList<Long>();
		if(dealer!=null){
			medias1=newsService.getDealerMediaList(dealer.getId());
			for(Media media:medias1){
				String media1=request.getParameter("medias"+media.getId());
				if(StringUtils.isNotBlank(media1)){
					try{checkedMedias.add(Long.parseLong(media1));}catch(Exception e){}
					if(!"".equals(medias)){
						medias+=",";
					}
					medias+=media1;
				}
			}
		}
		model.addAttribute("checkedMedias",checkedMedias);
		
		if(medias==null || "".equals(medias)){
			model.addAttribute("medias",mediaService.listAll());
			model.addAttribute("typeList",constantService.findAll("news_type"));
			model.addAttribute("news",news);
			LedpLogger.info((User) session.getAttribute("loginUser"), "增加信息", Operation.create,Result.failure,"合作媒体不存在");
			return "news/create";
		}
		if(dealer==null){
			br.rejectValue("dealer","news.dealer.not.exists","网点不存在！");
			model.addAttribute("medias",mediaService.listAll());
			model.addAttribute("typeList",constantService.findAll("news_type"));
			model.addAttribute("news",news);
			LedpLogger.info((User) session.getAttribute("loginUser"), "增加信息", Operation.create,Result.failure,"网点不存在");
			return "news/create";
		}

		if(StringUtils.isBlank(news.getTitle())||news.getTitle().length()<10){
			br.rejectValue("title","The title should not be less than 10 characters!","信息标题不能少于10个字符！");
			model.addAttribute("medias",mediaService.listAll());
			model.addAttribute("typeList",constantService.findAll("news_type"));
			model.addAttribute("news",news);
			return "news/create";
		}
		if(StringUtils.isBlank(news.getContent())||news.getContent().length()<400){
			br.rejectValue("content","The content should not be less than 10 characters!","信息内容不能少于400个字符！");
			model.addAttribute("medias",mediaService.listAll());
			model.addAttribute("typeList",constantService.findAll("news_type"));
			model.addAttribute("news",news);
			return "news/create";
		}
		news.setDealer(dealer);
		//加入大区
		Organization org = dealer.getOrganization();
		news.setOrg(org.getId());
		news.setState(constantService.find("news_state","wait_submit"));
		
		news.setSerieses(request.getParameter("serieses"));
		news.getContent().length();
		news.setOrg(dealer.getOrganization().getId());
		Long newId=newsService.save(news,medias);
		LedpLogger.info((User) session.getAttribute("loginUser"), "增加信息", Operation.create,Result.success);
		return "redirect:/news/index";
	}
	
	/**
	 * @Title: update
	 * @Description: TODO(修改信息入口)
	 * @param model 参数传递容器
	 * @param id 信息ID
	 * @return String
	 * @throws LedpException 
	 */
	@RequestMapping(value="/update/{id}")
	@Permission(code="news/update")
	public String update(Model model,@PathVariable long id,HttpServletRequest request) throws LedpException {
		News news = newsService.get(id);
		List<VehicleSeries> seriesList = mybaitsGenericDao.executeQuery(VehicleSeries.class,"select * from t_vehicle_series where status=1010 order by id asc");
		if(!StringUtils.isBlank(news.getSerieses())){
			List<Long> checkedSerieses = new ArrayList<Long>();
			String [] array = news.getSerieses().split(",");
			for(String s: array){
				try{
					checkedSerieses.add(Long.parseLong(s));
				}catch(Exception e){}
			}
			model.addAttribute("checkedSerieses",checkedSerieses);
		}else{
			model.addAttribute("checkedSerieses",new ArrayList<Long>());
		}
		String home="";
		try{
			home=request.getParameter("home");
			if(!home.equals("")){
				model.addAttribute("home", home);
			}
		}catch(Exception e){
			
		}
		model.addAttribute("seriesList",seriesList);
		model.addAttribute("typeList",constantService.findAll("news_type"));
		model.addAttribute("checkedMedias",newsService.getNewsMediaList(id));
		Dealer dealer = (Dealer)request.getSession().getAttribute("loginDealer");
		if(dealer!=null){
			model.addAttribute("medias",newsService.getDealerMediaList(dealer.getId()));
		}else{
			model.addAttribute("medias",mediaService.listAll());
		}
		model.addAttribute("news",news);
		
		return "news/update";
	}
	
	/**
	 * @Title: edit
	 * @Description: TODO(修改信息方法)
	 * @param model 参数传递容器
	 * @return String
	 * @throws LedpException 
	 */
	@RequestMapping(value="/edit",method=RequestMethod.POST)
	@Permission(code="news/update")
	public String edit(Model model,@Valid @ModelAttribute("news") News news,HttpServletRequest request,BindingResult br) throws LedpException {
		HttpSession session = request.getSession();
		if(br.hasErrors()){
			model.addAttribute("medias",mediaService.listAll());
			model.addAttribute("typeList",constantService.findAll("news_type"));
			model.addAttribute("news",news);
			LedpLogger.info((User) session.getAttribute("loginUser"), "修改信息", Operation.update,Result.failure);
			return "redirect:/news/update";
		}
		String medias = "";
		Dealer dealer = (Dealer)request.getSession().getAttribute("loginDealer");
		List<Media> medias1=new ArrayList<Media>(); 
		List<Long> checkedMedias = new ArrayList<Long>();
		if(dealer!=null){
			medias1=newsService.getDealerMediaList(dealer.getId());
			for(Media media:medias1){
				String media1=request.getParameter("medias"+media.getId());
				if(StringUtils.isNotBlank(media1)){
					try{checkedMedias.add(Long.parseLong(media1));}catch(Exception e){}
					if(!"".equals(medias)){
						medias+=",";
					}
					medias+=media1;
				}
			}
		}
		model.addAttribute("checkedMedias",checkedMedias);
		if(medias==null || "".equals(medias)){
			model.addAttribute("medias",mediaService.listAll());
			model.addAttribute("typeList",constantService.findAll("news_type"));
			model.addAttribute("news",news);
			LedpLogger.info((User) session.getAttribute("loginUser"), "修改信息", Operation.update,Result.failure,"合作媒体不存在");
			return "news/update";
		}
		if(StringUtils.isBlank(news.getTitle())||news.getTitle().length()<10){
			br.rejectValue("title","The title should not be less than 10 characters!","信息标题不能少于10个字符！");
			model.addAttribute("medias",mediaService.listAll());
			model.addAttribute("typeList",constantService.findAll("news_type"));
			model.addAttribute("news",news);
			return "news/update";
		}
		if(StringUtils.isBlank(news.getContent())||news.getContent().length()<400){
			br.rejectValue("content","The content should not be less than 10 characters!","信息内容不能少于400个字符！");
			model.addAttribute("medias",mediaService.listAll());
			model.addAttribute("typeList",constantService.findAll("news_type"));
			model.addAttribute("news",news);
			return "news/update";
		}
		News news2 = newsService.get(news.getId());
		//将信息状态重置为待审核
		news2.setState(constantService.get(6010));
		FieldBinder.bind(news2, params);
		
		Object objectSerieses = params.get("serieses");
		if(objectSerieses!=null){
			if(objectSerieses instanceof String[]){
				String[] array = (String[])objectSerieses;
				news2.setSerieses(StringUtils.join(array,","));
			}else{
				news2.setSerieses(objectSerieses.toString());
			}
		}
		
		if(!params.containsKey("includeAddress")){
			news2.setIncludeAddress(null);
		}else{
			news2.setIncludeAddress("true");
		}
		if(!params.containsKey("includePhone")){
			news2.setIncludePhone(null);
		}else{
			news2.setIncludePhone("true");
		}
		
		newsService.update(news2,medias);
		model.addAttribute("message","信息修改成功");
		String home="";
		try{
			home=request.getParameter("home");
			if(!"".equals(home)){
				model.addAttribute("home", home);
				return "redirect:/home/index";
			}
		}catch(Exception e){
			
		}
		LedpLogger.info((User) session.getAttribute("loginUser"), "修改信息", Operation.update,Result.success);
		return "redirect:/news/index";
	}
	@RequestMapping(value={"search"})
	@ResponseBody
	@Permission(code="news/index")
	public JSON search(Model model,HttpServletRequest request) throws Exception {
		// 返回到界面
		String organation = request.getParameter("organizationSelectedId");
		Dealer dealer = (Dealer)request.getSession().getAttribute("loginDealer");
		User user = (User) request.getSession().getAttribute("loginUser");
		final User sysAdmin = user;
		String level =  request.getParameter("organizationLevel");
		Organization org = (Organization)request.getSession().getAttribute("loginOrg");
		//将数据回显到页面
		request.setAttribute("organation", organation);
		request.setAttribute("level", level);
	    params.put("organation", organation); //结点ID
	    params.put("level", level);
	    params.put("user", user);
	    params.put("org", org);
		if(dealer!=null){
			params.put("dealer",dealer.getId());
		}
		final String contextPath = request.getContextPath();
		int pageSize   = 10;
		int pageNumber = 1;
		String sortName  = request.getParameter("sortName");
		String sortOrder = request.getParameter("sortOrder");
		String realSortName  = request.getParameter("sortName");
		String state ="";
		String auth="";
		try{
			state =request.getParameter("state");
			auth=request.getParameter("auth");
			if(state!=null || !state.equals("")){
				params.put("stateId1", state);
			}
			if(auth!=null || !auth.equals("")){
				params.put("auth", auth);
			}
		}catch(Exception e){
		}
		if(StringUtils.isBlank(sortName)){
			sortName = "dateCreate";
			realSortName = "date_create";
			//默认设置倒序
			sortOrder = "desc";
		}
		if("dealer.name".equals(sortName)){
			realSortName = "dealer";
		}
		if("type.name".equals(sortName)){
			realSortName = "type";
		}
		if("state.name".equals(sortName)){
			realSortName = "state";
		}
		if("dateCreate".equals(sortName)){
			realSortName = "date_create";
		}
		if(StringUtils.isBlank(sortOrder)){
			sortOrder = "desc";
		}
		try{
			pageSize = Integer.parseInt(request.getParameter("pageSize"));
		}catch(Exception e){
			pageSize   = 10;
		}
		try{
			pageNumber = Integer.parseInt(request.getParameter("pageNumber"));
		}catch(Exception e){
			pageNumber = 1;
		}
		params.put("pageSize", pageSize);
		params.put("pageNumber", pageNumber);
		params.put("sortName", realSortName);
		params.put("sortOrder", sortOrder);
		List<Map<String,String>> rows = newsService.executeMapQuery(params);
		int total = newsService.getTotalRow(params);
		if(auth==null){
		JSON data = JSONConverter.convert(pageSize,pageNumber,sortName,sortOrder, total,rows,new String[] {"dealer.name", "type.name", "title", "state.name","result","dateCreate"},
				new JSONConverter.Operation<Map<String,String>>() {
					public String operate(Map<String,String> t) {
						String id = t.get("id");
						String statusCode = t.get("statusCode");
						if(permissionService.hasAuth("news/detail")){
							return "<a href='"+contextPath+"/news/detail/" + id
									+ "' title='查看'><img alt='' src='../images/magnifier.png'></a>";
						}
						return "";
					}
				}, new JSONConverter.Operation<Map<String,String>>() {
					public String operate(Map<String,String> t) {
						String id = t.get("id");
						String statusCode = t.get("statusCode");
						//如果是否系统管理员，如果是，则不给编辑按钮权限
						if(permissionService.hasAuth("news/update") && !"admin".equalsIgnoreCase(sysAdmin.getCode())){
							if(!("destroy_whole".equals(statusCode)||"destroy_part".equals(statusCode)||"destroy_none".equals(statusCode))){
								return "<a href='"+contextPath+"/news/update/" + id+ "' title='编辑' class='edit'><img alt='' src='../images/edit.png' /></a>";
							}
						}
						return "";
					}
				}, new JSONConverter.Operation<Map<String,String>>() {
					public String operate(Map<String,String> t) {
						String id = t.get("id");
						String statusCode = t.get("statusCode");
						if(permissionService.hasAuth("news/create")){
							if("wait_submit".equals(statusCode)){
									return "<a href='javascript:void(0);' title='提交' d_id=" + id
											+ " status_code='active' class='ONOFF' onclick='delete_message(\""+contextPath+"/news/submit1/" + id
											+ "\",\"确定要提交吗？\",false,submitCallback)'><img alt='' src='../images/right.png' />";
							}
						}
						return "";
					}
				}, new JSONConverter.Operation<Map<String,String>>() {
					public String operate(Map<String,String> t) {
						String id = t.get("id");
						String statusCode = t.get("statusCode");
						if(permissionService.hasAuth("news/auth")){
							if("wait_auth".equals(statusCode)){
								//去审核页面
								return "<a href='"+contextPath+"/news/detail/" + id+ "' class='ONOFF'><img alt='' src='../images/sh.png' />";
							}
							if("auth_failure".equals(statusCode)){
								//去审核页面
								return "<a href='"+contextPath+"/news/detail/" + id+ "' class='ONOFF'><img alt='' src='../images/sh.png' />";
							}
						}
						return "";
					}
				}, new JSONConverter.Operation<Map<String,String>>() {
					public String operate(Map<String,String> t) {
						String id = t.get("id");
						String statusCode = t.get("statusCode");
						if((permissionService.hasAuth("news/destroy"))){
							if("publish_whole".equals(statusCode)||"publish_part".equals(statusCode)||"update_whole".equals(statusCode)||"update_part".equals(statusCode)||"update_none".equals(statusCode)){
								//撤销
								return "<a href='#' title='撤销' d_id=" + id
										+ " status_code='active' class='ONOFF' onclick='delete_message(\""+contextPath+"/news/destroy/" + id
										+ "\",\"确定要撤销吗？\",false,destroyCallback)'><img alt='' src='../images/cx.png' />";
							}
						}
						return "";
					}
				}, new JSONConverter.Operation<Map<String,String>>() {
					public String operate(Map<String,String> t) {
						String id = t.get("id");
						String statusCode = t.get("statusCode");
						String scanCount = t.get("scanCount");
						if((permissionService.hasAuth("news/delete"))){
							if("wait_submit".equals(statusCode)||"wait_auth".equals(statusCode)||"auth_failure".equals(statusCode)||"publish_none".equals(statusCode)){
								if(StringUtils.isBlank(scanCount) && Integer.parseInt(scanCount)==0){
								//删除
								return "<a href='#' title='删除' d_id=" + id
										+ " status_code='active' class='ONOFF' onclick='delete_message(\""+contextPath+"/news/delete/" + id
										+ "\",\"确定要删除吗？\",false,deleteCallback)'><img alt='' src='../images/del.png' />";
								}
							}
						}
						return "";
					}
				}
				
				);
		return data;
		}else{
			JSON data = JSONConverter.convert(pageSize,pageNumber,sortName,sortOrder, total,rows,new String[] {"dealer.name", "type.name", "title", "state.name","result","dateCreate"},
					 new JSONConverter.Operation<Map<String,String>>() {
						public String operate(Map<String,String> t) {
							String id = t.get("id");
							String statusCode = t.get("statusCode");
								//去审核页面
							return "<a href='"+contextPath+"/news/detail/" + id+ "?home=news ' class='ONOFF'><img alt='' src='../images/sh.png' />";
							
						}
					});
			return data;
		}
	}
	/**
	 * @Title: detail
	 * @Description: TODO(查看信息方法)
	 * @param model 参数传递容器
	 * @param id 信息ID
	 * @return String
	 * @throws LedpException 
	 */
	@RequestMapping(value="/detail/{id}")
	@Permission(code="news/detail")
	public String detail(Model model,@PathVariable long id,HttpServletRequest request) throws LedpException {
		News news = newsService.get(id);
		User user = (User) request.getSession().getAttribute("loginUser");
		Dealer dealer = (Dealer)request.getSession().getAttribute("loginDealer");
		if(dealer!=null){
			model.addAttribute("medias",newsService.getDealerMediaList(dealer.getId()));
		}else{
			model.addAttribute("medias",mediaService.listAll());
		}
		String home="";
		try{
			home=request.getParameter("home");
			if(!home.equals("")){
				model.addAttribute("home", home);
			}
		}catch(Exception e){
			
		}
		model.addAttribute("checkedMedias",newsService.getNewsMediaList(id));
		model.addAttribute("newsMedias",newsService.getNewsMedia(id));
		model.addAttribute("news",news);
		model.addAttribute("user", user);
		return "news/detail";
	}
	
	@RequestMapping(value="/submit1/{id}")
	@ResponseBody
	public JSON submit1(Model model,@PathVariable long id) throws LedpException {
		News news = newsService.get(id);
		if(news==null){
			throw new LedpException();
		}
		Constant state = constantService.find("news_state","wait_auth");
		news.setState(state);
		newsService.update(news);
		Map json = new HashMap<String,String>();
		json.put("code","success_message");
		json.put("message","信息提交成功 ！");
		
		return (JSON)JSON.toJSON(json);
	}
	
	
	/**
	 * @Title: detail
	 * @Description: TODO(查看机构方法)
	 * @param model 参数传递容器
	 * @param id 机构ID
	 * @return String
	 * @throws LedpException 
	 */
	@RequestMapping(value="/authSuccess/{id}")
	@ResponseBody
	public JSON authSuccess(Model model,@PathVariable long id) throws LedpException {
		News news = newsService.get(id);
		if(news==null){
			throw new LedpException();
		}
		if(news.getScanCount()>0){
			news.setState(new Constant(6120L));//待修改状态
		}else{
			news.setState(new Constant(6030L));//待发布状态
		}
		news.setScanCount(0);//重新设置可扫描次数
		newsService.update(news);
		Map json = new HashMap<String,String>();
		json.put("code","success_message");
		json.put("message","信息审核成功！");
		
		return (JSON)JSON.toJSON(json);
	}
	
	@RequestMapping(value="/authFailure/{id}")
	@ResponseBody
	public JSON authFailure(Model model,@PathVariable long id) throws LedpException {
		News news = newsService.get(id);
		if(news==null){
			throw new LedpException();
		}
		Constant state = constantService.find("news_state","auth_failure");
		news.setState(state);
		newsService.update(news);
		Map json = new HashMap<String,String>();
		json.put("code","success_message");
		json.put("message","信息"+state.getName()+"！");
		return (JSON)JSON.toJSON(json);
	}
	
	@RequestMapping(value="/preview")
	public String preview(Model model,@Valid @ModelAttribute("news") News news,HttpServletRequest request,BindingResult br) throws Exception {
		String medias = request.getParameter("medias");
		if(medias==null || "".equals(medias)){
			//br.rejectValue("medias","news.medias.not.exists","合作媒体不存在！");
			model.addAttribute("medias",mediaService.listAll());
			model.addAttribute("typeList",constantService.findAll("news_type"));
			model.addAttribute("news",news);
			return "news/create";
		}
		Dealer dealer = (Dealer)request.getSession().getAttribute("loginDealer");
		if(dealer==null){
			br.rejectValue("dealer","news.dealer.not.exists","网点不存在！");
			model.addAttribute("medias",mediaService.listAll());
			model.addAttribute("typeList",constantService.findAll("news_type"));
			model.addAttribute("news",news);
			return "news/create";
		}
		List<Media> medias1=new ArrayList<Media>(); 
		List<Long> checkedMedias = new ArrayList<Long>();
		medias1=newsService.getDealerMediaList(dealer.getId());
		for(Media media:medias1){
			String media1=request.getParameter("medias"+media.getId());
			if(StringUtils.isNotBlank(media1)){
				try{checkedMedias.add(Long.parseLong(media1));}catch(Exception e){}
			}
		}
		model.addAttribute("checkedMedias",checkedMedias);
		if(StringUtils.isBlank(news.getTitle())||news.getTitle().length()<10){
			br.rejectValue("title","The title should not be less than 10 characters!","信息标题不能少于10个字符！");
			model.addAttribute("medias",mediaService.listAll());
			model.addAttribute("typeList",constantService.findAll("news_type"));
			model.addAttribute("news",news);
			return "news/create";
		}
		if(StringUtils.isBlank(news.getContent())||news.getContent().length()<400){
			br.rejectValue("content","The content should not be less than 10 characters!","信息内容不能少于400个字符！");
			model.addAttribute("medias",mediaService.listAll());
			model.addAttribute("typeList",constantService.findAll("news_type"));
			model.addAttribute("news",news);
			return "news/create";
		}
		news.setDateCreate(new Date());
		news.setDealer(dealer);
		news.setType(constantService.get(news.getType().getId()));
		news.setState(constantService.find("news_state","new"));
		news.setSerieses(request.getParameter("serieses"));
		
		request.getSession().setAttribute("preview_medias",medias);
		request.getSession().setAttribute("preview_news",news);
		
		model.addAttribute("news",news);
		return "news/preview";
	}

	@RequestMapping(value="/previewSave")
	@ResponseBody
	@Permission(code={"news/create","news/update"})
	public JSON previewSave(HttpServletRequest request) throws Exception {
		Object medias = request.getSession().getAttribute("preview_medias");
		News news = (News)request.getSession().getAttribute("preview_news");
		Long id = news.getId();
		if(medias!=null && news!=null && !StringUtils.isBlank(medias.toString())){
			if(news.getId()!=null && news.getId()>0){
				newsService.update(news);
			}else{
				id = newsService.save(news,medias.toString());
			}
			request.getSession().removeAttribute("preview_medias");			
			request.getSession().removeAttribute("preview_news");
			Map json = new HashMap<String,String>();
			json.put("id",id);
			json.put("code","success_message");
			json.put("message","信息保存成功 ！");
			return (JSON)JSON.toJSON(json);
		}
		Map json = new HashMap<String,String>();
		json.put("code","error_message");
		json.put("message","信息保存失败 ！");
		return (JSON)JSON.toJSON(json);
	}
	//删除信息
	@Permission(code="news/delete")
	@RequestMapping(value="/delete/{id}")
	@ResponseBody
	public JSON delete(@PathVariable long id) throws LedpException {
		newsService.delete(id);
		Map json = new HashMap<String,String>();
		json.put("code","success_message");
		json.put("message","信息删除成功 ！");
		return (JSON)JSON.toJSON(json);
	}
	//撤销信息
	@Permission(code="news/destroy")
	@RequestMapping(value="/destroy/{id}")
	@ResponseBody
	public JSON destroy(Model model,@PathVariable long id) throws LedpException {
		newsService.destroy(id);
		Map json = new HashMap<String,String>();
		json.put("code","success_message");
		json.put("message","信息撤销成功 ！");
		
		return (JSON)JSON.toJSON(json);
	}
	@RequestMapping(value={"search1"})
	@ResponseBody
	@Permission(code="news/index")
	public JSON search1(Model model,HttpServletRequest request) throws Exception {
		// 返回到界面
		Dealer dealer = (Dealer)request.getSession().getAttribute("loginDealer");
		if(dealer!=null){
			params.put("dealer",dealer.getId());
		}
		final String contextPath = request.getContextPath();
		int pageSize   = 10;
		int pageNumber = 1;
		String sortName  = request.getParameter("sortName");
		String sortOrder = request.getParameter("sortOrder");
		String state ="";
		String auth="";
		try{
			state =request.getParameter("state");
			auth=request.getParameter("auth");
			if(state!=null || !state.equals("")){
				params.put("stateId1", state);
			}
			if(auth!=null || !auth.equals("")){
				params.put("auth", auth);
			}
		}catch(Exception e){
		}
		if(StringUtils.isBlank(sortName)){
			sortName = "title";
		}
		if(StringUtils.isBlank(sortOrder)){
			sortOrder = "asc";
		}
		try{
			pageSize = Integer.parseInt(request.getParameter("pageSize"));
		}catch(Exception e){
			pageSize   = 10;
		}
		try{
			pageNumber = Integer.parseInt(request.getParameter("pageNumber"));
		}catch(Exception e){
			pageNumber = 1;
		}
		params.put("pageSize", pageSize);
		params.put("pageNumber", pageNumber);
		params.put("sortName", sortName);
		params.put("sortOrder", sortOrder);
		List<News> rows = newsService.executeQuery1(params);
		int total = newsService.getTotalRow1(params);
		if(auth==null){
		JSON data = JSONConverter.convert(pageSize,pageNumber,sortName,sortOrder, total,rows,new String[] {"dealer.name", "type.name", "title", "state.name","dateCreate"},
				new JSONConverter.Operation<News>() {
					public String operate(News t) {
						if(permissionService.hasAuth("news/detail")){
							return "<a href='"+contextPath+"/news/detail/" + t.getId()
									+ "' title='查看'><img alt='' src='../images/magnifier.png'></a>";
						}
						return "";
					}
				}, new JSONConverter.Operation<News>() {
					public String operate(News t) {
						if(permissionService.hasAuth("news/update")){
							return "<a href='"+contextPath+"/news/update/" + t.getId()
									+ "' title='编辑' class='edit'><img alt='' src='../images/edit.png' /></a>";
						}
						return "";
					}
				}, new JSONConverter.Operation<News>() {
					public String operate(News t) {
						Constant status = t.getState();
						if("new".equals(status.getCode())){
							return "<a href='javascript:void(0);' title='提交' d_id=" + t.getId()
									+ " status_code='active' class='ONOFF' onclick='delete_message(\""+contextPath+"/news/submit1/" + t.getId()
									+ "\",\"确定要提交吗？\",false,submitCallback)'><img alt='' src='../images/right.png' />";
						}
						if(permissionService.hasAuth("news/auth")){
							if("wait_auth".equals(status.getCode())){
								//去审核页面
								return "<a href='"+contextPath+"/news/detail/" + t.getId()+ "' class='ONOFF'><img alt='' src='../images/sh.png' />";
							}
							
							if("wait_failure".equals(status.getCode())){
								//去审核页面
								return "<a href='"+contextPath+"/news/detail/" + t.getId()+ "' class='ONOFF'><img alt='' src='../images/sh.png' />";
							}
						}

						if("publish_success".equals(status.getCode())){
							//撤销
							return "<a href='#' title='撤销' d_id=" + t.getId()
									+ " status_code='active' class='ONOFF' onclick='delete_message(\""+contextPath+"/news/destroy/" + t.getId()
									+ "\",\"确定要撤销吗？\",false,destroyCallback)'><img alt='' src='../images/jy.png' />";
						}
						return "";
					}
				});
		return data;
		}else{
			JSON data = JSONConverter.convert(pageSize,pageNumber,sortName,sortOrder, total,rows,new String[] {"dealer.name", "type.name", "title", "state.name","dateCreate"},
					 new JSONConverter.Operation<News>() {
						public String operate(News t) {
								//去审核页面
							return "<a href='"+contextPath+"/news/detail/" + t.getId()+ "?home=news ' class='ONOFF'><img alt='' src='../images/sh.png' />";
							
						}
					});
			return data;
		}
	}
	
}
