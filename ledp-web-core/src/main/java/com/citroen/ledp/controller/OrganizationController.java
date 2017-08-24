package com.citroen.ledp.controller;

import java.util.ArrayList;
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
import com.citroen.ledp.domain.Constant;
import com.citroen.ledp.domain.Dealer;
import com.citroen.ledp.domain.Organization;
import com.citroen.ledp.domain.User;
import com.citroen.ledp.exception.LedpException;
import com.citroen.ledp.interceptor.Permission;
import com.citroen.ledp.service.ConstantService;
import com.citroen.ledp.service.DealerService;
import com.citroen.ledp.service.OrganizationService;
import com.citroen.ledp.service.PermissionService;
import com.citroen.ledp.util.JSONConverter;
import com.citroen.ledp.util.LedpLogger;
import com.citroen.ledp.util.LedpLogger.Operation;
import com.citroen.ledp.util.LedpLogger.Result;

/**
 * @Title: OrganizationController.java
 * @Package com.citroen.ledp.controller
 * @Description: TODO(机构管理类)
 * @author 廖启洪
 * @date 2015年1月29日 下午3:24:24
 * @version V1.0
 */
@Controller
@RequestMapping("/organization")
public class OrganizationController {
	private Map<String, Object> params;

	@Autowired
	private OrganizationService organizationService;
	@Autowired
	private ConstantService constantService;
	@Autowired
	private DealerService dealerService;

	@Autowired
	private PermissionService permissionService;

	@RequestMapping(value = { "", "/index" })
	@Permission(code={"organization/index"})
	public String index(Model model, String parent) throws Exception {
		if (parent == null) {
			Organization organization = organizationService.getRoot();
			parent = 1 + "";
		}
		if(permissionService.hasAuth("organization/create")){
			model.addAttribute("permission","permission");
		}
		model.addAttribute("parent", parent);
		model.addAttribute(params);
		return "organization/index";
	}

	@RequestMapping(value = { "search" })
	@ResponseBody
	@Permission(code={"organization/index"})
	public JSON search(Model model, HttpServletRequest request) throws Exception {
		// 返回到界面
		String parent = request.getParameter("parent");
		if (StringUtils.isBlank(parent)) {
			Organization organization = organizationService.getRoot();
			params.put("parent", organization.getId());
		}

		final String contextPath = request.getContextPath();
		int pageSize = 10;
		int pageNumber = 1;
		String sortName = request.getParameter("sortName");
		String sortOrder = request.getParameter("sortOrder");
		if (StringUtils.isBlank(sortName)) {
			sortName = "name";
		}
		if (StringUtils.isBlank(sortOrder)) {
			sortOrder = "asc";
		}
		try {
			pageSize = Integer.parseInt(request.getParameter("pageSize"));
		} catch (Exception e) {
		}
		try {
			pageNumber = Integer.parseInt(request.getParameter("pageNumber"));
		} catch (Exception e) {
		}
		params.put("pageSize", pageSize);
		params.put("pageNumber", pageNumber);
		params.put("sortName", sortName);
		params.put("sortOrder", sortOrder);

		List<Organization> rows = organizationService.executeQuery(params);
		int total = organizationService.getTotalRow(params);
		JSON data = JSONConverter.convert(pageSize, pageNumber, sortName, sortOrder, total, rows, new String[] { "parent.name",
				"name", "status.name" }, new JSONConverter.Operation<Organization>() {
			public String operate(Organization t) {
				if (permissionService.hasAuth("organization/detail")) {
					return "<a class='ielookDetails' href='" + contextPath + "/organization/detail/" + t.getId()
							+ "' title='查看'><img alt='' src='../images/magnifier.png'></a>";
				}
				return "";
			}
		}, new JSONConverter.Operation<Organization>() {
			public String operate(Organization t) {
				if (permissionService.hasAuth("organization/update")) {
					return "<a href='" + contextPath + "/organization/update/" + t.getId()
							+ "' title='编辑' class='edit'><img alt='' src='../images/edit.png' /></a>";
				}
				return "";
			}
		}, new JSONConverter.Operation<Organization>() {
			public String operate(Organization t) {
				if (permissionService.hasAuth("organization/update")) {
					Constant status = t.getStatus();
					if ("active".equals(status.getCode())) {
						return "<a href='#' title='禁用' d_id=" + t.getId()
								+ " status_code='active' class='ONOFF' onclick='delete_message(\"" + contextPath
								+ "/organization/inactive/" + t.getId()
								+ "\",\"确定要禁用吗？\",false,inactiveCallback)'><img alt='' src='../images/jy.png' />";
					}
					return "<a href='#' title='启用' d_id=" + t.getId()
							+ " status_code='active' class='ONOFF' onclick='delete_message(\"" + contextPath
							+ "/organization/active/" + t.getId()
							+ "\",\"确定要启用吗？\",false,activeCallback)'><img alt='' src='../images/jynone.png' />";
				}
				return "";
			}
		});
		return data;
	}

	/**
	 * @Title: create
	 * @Description: TODO(添加机构入口)
	 * @param model
	 *            参数传递容器
	 * @throws Exception
	 * @return String
	 */
	@RequestMapping(value = "/create")
	@Permission(code={"organization/create"})
	public String create(Model model, @ModelAttribute("organization") Organization organization) throws Exception {

		if (organization.getParent() != null && organization.getParent().getId() > 0) {
			Organization parent = organizationService.get(organization.getParent().getId());
			organization.setParent(parent);
		}
		Constant status = constantService.find("record_status", "active");
		organization.setStatus(status);
		/* 机构负责人 */
		List<User> managerList = organizationService.getUserList(organization.getId());
		
		model.addAttribute("managerList", managerList);
		model.addAttribute("organization", organization);
		return "organization/create";
	}

	/**
	 * @Title: save
	 * @Description: TODO(新增机构方法)
	 * @param model
	 *            参数传递容器
	 * @throws Exception
	 * @return String
	 */
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	@Permission(code={"organization/save"})
	public String save(Model model, @Valid @ModelAttribute("organization") Organization organization, BindingResult br,HttpServletRequest request)
			throws Exception {
		HttpSession session = request.getSession();
		if (br.hasErrors()) {
			model.addAttribute("organization", organization);
			LedpLogger.info((User) session.getAttribute("loginUser"), "增加组织", Operation.create,Result.failure);
			return "organization/create";
		}
		organization.setName(organization.getName().trim());

		Organization org = organizationService.find(organization.getParent().getId(), organization.getName());
		if (org != null) {
			br.rejectValue("name", "organization.name.already.exists", "名称已经存在，请重新输入！");
			model.addAttribute("organization", organization);
			LedpLogger.info((User) session.getAttribute("loginUser"), "增加组织", Operation.create,Result.failure,"名称已经存在");
			return "organization/create";
		}
		organizationService.save(organization);
		model.addAttribute("org", "机构添加成功");
		model.addAttribute("parent","1");
		if(permissionService.hasAuth("organization/create")){
			model.addAttribute("permission","permission");
		}
		LedpLogger.info((User) session.getAttribute("loginUser"), "增加组织", Operation.create,Result.success);
		return "organization/index";
	}

	/**
	 * @Title: update
	 * @Description: TODO(修改机构入口)
	 * @param model
	 *            参数传递容器
	 * @param id
	 *            机构ID
	 * @return String
	 * @throws LedpException
	 */
	@RequestMapping(value = "/update/{id}")
	@Permission(code={"organization/update"})
	public String update(Model model, @PathVariable long id) throws LedpException {
		Organization organization = organizationService.get(id);
		
		List<User> managerList = organizationService.getUserList(organization.getParent().getId());
		
		model.addAttribute("organization", organization);
		model.addAttribute("managerList", managerList);
		return "organization/update";
	}

	/**
	 * @Title: edit
	 * @Description: TODO(修改机构方法)
	 * @param model
	 *            参数传递容器
	 * @return String
	 * @throws LedpException
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	@Permission(code={"organization/edit"})
	public String edit(Model model, @Valid @ModelAttribute("organization") Organization organization, BindingResult br,HttpServletRequest request)
			throws LedpException {
		HttpSession session = request.getSession();
		if (br.hasErrors()) {
			model.addAttribute("organization", organization);
			LedpLogger.info((User) session.getAttribute("loginUser"), "修改组织", Operation.update,Result.failure);
			return "organization/update";
		}

		Organization org = organizationService.find(organization.getParent().getId(), organization.getName());
		
		if (org != null && org.getId() != organization.getId()) {
			br.rejectValue("name", "organization.name.already.exists", "名称已经存在，请重新输入！");
			model.addAttribute("organization", organization);
			LedpLogger.info((User) session.getAttribute("loginUser"), "修改组织", Operation.update,Result.failure,"名称已经存在");
			return "organization/update";
		}
		
		Organization organization2 = organizationService.get(organization.getId());
		FieldBinder.bind(organization2, params);
		organization2.setName(organization2.getName().trim());
		try{
			String manager=request.getParameter("manager.id");
			if(manager.equals("") || manager==null){
				organization2.setManager(null);
			}
		}catch(Exception e){
			organization2.setManager(null);
		}
		organizationService.update(organization2);

		model.addAttribute("org", "机构修改成功");
		model.addAttribute("parent","1");
		if(permissionService.hasAuth("organization/create")){
			model.addAttribute("permission","permission");
		}
		LedpLogger.info((User) session.getAttribute("loginUser"), "修改组织", Operation.update,Result.success);
		return "organization/index";
	}

	/**
	 * @Title: detail
	 * @Description: TODO(查看机构方法)
	 * @param model
	 *            参数传递容器
	 * @param id
	 *            机构ID
	 * @return String
	 * @throws LedpException
	 */
	@RequestMapping(value = "/detail/{id}")
	@Permission(code={"organization/detail"})
	public String detail(Model model, @PathVariable long id) throws LedpException {
		Organization organization = organizationService.get(id);
		model.addAttribute("organization", organization);
		return "organization/detail";
	}

	@RequestMapping(value = "/head/{id}", method = RequestMethod.POST)
	@ResponseBody
	public JSON manager(@PathVariable long id) {
		List<User> userList = new ArrayList<User>();
		try {
			userList = organizationService.getUserList(id);
		} catch (LedpException e) {
			e.printStackTrace();
		}
		return (JSON) JSON.toJSON(userList);
	}

	/**
	 * @Title: detail
	 * @Description: TODO(查看机构方法)
	 * @param model
	 *            参数传递容器
	 * @param id
	 *            机构ID
	 * @return String
	 * @throws LedpException
	 */
	@RequestMapping(value = "/active/{id}")
	@ResponseBody
	@Permission(code={"organization/update"})
	public JSON active(Model model, @PathVariable long id) throws LedpException {
		Organization organization = organizationService.get(id);
		if (organization == null) {
			throw new LedpException();
		}
		Constant status = constantService.find("record_status", "active");
		organization.setStatus(status);
		organizationService.update(organization);
		Map json = new HashMap<String, String>();
		json.put("code", "success_message");
		json.put("message", "机构" + status.getName() + "成功 ！");

		return (JSON) JSON.toJSON(json);
	}

	/**
	 * @Title: detail
	 * @Description: TODO(查看机构方法)
	 * @param model
	 *            参数传递容器
	 * @param id
	 *            机构ID
	 * @return String
	 * @throws LedpException
	 */
	@RequestMapping(value = "/inactive/{id}")
	@ResponseBody
	@Permission(code={"organization/update"})
	public JSON inactive(Model model, @PathVariable long id) throws LedpException {
		Organization organization = organizationService.get(id);
		if (organization == null) {
			throw new LedpException();
		}
		Constant status = constantService.find("record_status", "inactive");
		organization.setStatus(status);
		organizationService.update(organization);
		Map json = new HashMap<String, String>();
		json.put("code", "success_message");
		json.put("message", "机构" + status.getName() + "成功 ！");

		return (JSON) JSON.toJSON(json);
	}

	/**
	 * @Title: detail
	 * @Description: TODO(查看机构方法)
	 * @param model
	 *            参数传递容器
	 * @param id
	 *            机构ID
	 * @return String
	 * @throws LedpException
	 */
	@RequestMapping(value = "/tree")
	@ResponseBody
	public JSON tree(Model model, HttpServletRequest reqquest) throws LedpException {
		Long parent = null;
		try {
			String parentId = reqquest.getParameter("id");
			if (parentId != null) {
				parent = Long.parseLong(parentId);
			}
		} catch (Exception e) {
			Organization organization = (Organization) reqquest.getSession().getAttribute("organization");
			if (organization != null) {
				parent = organization.getId();
			}
		}
		Long checked = 0l;
		try {
			checked = Long.parseLong(reqquest.getParameter("checked"));
		} catch (Exception e) {
		}
		if(null != parent){
			Organization item = organizationService.get(parent);
			if(item.getLevel() == 2){
				List<Map<String, Object>> nodes = new ArrayList<Map<String, Object>>();
				List<Dealer> children = dealerService.getByOrganizationId(item.getId());
				for (Dealer child : children) {
					Map<String, Object> node = new HashMap<String, Object>();
					node.put("id", child.getId());
					node.put("text", child.getName());
					node.put("checked","false");
					node.put("state","open");
					node.put("option", "0");
					node.put("type", "dealer");
					nodes.add(node);
				}
				return (JSON) JSON.toJSON(nodes);
			}
		}
		List<Map<String, Object>> nodes = new ArrayList<Map<String, Object>>();
		List<Organization> children = organizationService.getChildren(parent);
		for (Organization child : children) {
			Map<String, Object> node = new HashMap<String, Object>();
			node.put("id", child.getId());
			node.put("text", child.getName());
			node.put("type", "org");
			node.put("checked", checked == child.getId());
			if(checked==1){
				if(child.getId()==1){
					node.put("option", "1");
				}else{
					node.put("option", "0");
				}
			}else{
				if(child.getId()==1){
					node.put("option", "0");
				}else{
					node.put("option", "1");
				}
			}
			if(child.getLevel() == 2){
				if(child.getId()==1){
					node.put("state", "open");
				}else{
					node.put("state", dealerService.getChildrenCount(child.getId()) > 0 ? "closed" : "open");
				}
			}else{
				if(child.getId()==1){
					node.put("state", "open");
				}else{
					node.put("state", organizationService.hasChildren(child.getId()) ? "closed" : "open");
				}
			}
			if(child.getId()==1){
				String create=reqquest.getParameter("createDe");
				List<Map<String, Object>> nodes1 = new ArrayList<Map<String, Object>>();
				List<Organization> children1 = new ArrayList<Organization>();
				if(create==null){
					children1 = organizationService.getChildren(child.getId());
				}else{
					children1 = organizationService.getActiviteChildren(child.getId());
				}
				for (Organization child1 : children1) {
					Map<String, Object> node1 = new HashMap<String, Object>();
					node1.put("id", child1.getId());
					node1.put("text", child1.getName());
					node1.put("checked", checked == child.getId());
					if(checked==1){
						node1.put("option", "0");
					}else{
						node1.put("option", "1");
						node1.put("isActive", child1.getStatus().getCode());
					}
					if(child1.getLevel() == 2){
						if(create==null){
							node1.put("state", "open");
						}else{
							node1.put("state", dealerService.getChildrenCount(child1.getId()) > 0 ? "closed" : "open");
						}
					}else{
						node1.put("state", organizationService.hasChildren(child1.getId()) ? "closed" : "open");
					}
					
					nodes1.add(node1);
				}
				node.put("children", nodes1);
			}
			nodes.add(node);
		}
		return (JSON) JSON.toJSON(nodes);
	}
	
	
	/**
	 * 用户显示的树
	 * @param model
	 * @param reqquest
	 * @return
	 * @throws LedpException
	 */
	@RequestMapping(value = "/treeUser")
	@ResponseBody
	public JSON treeUser(Model model, HttpServletRequest reqquest) throws LedpException {
		
		long checked = 0;
		//结点ID
		String id = reqquest.getParameter("id");
		try {
			checked = Long.parseLong(reqquest.getParameter("checked"));
			
		} catch (Exception e) {
			
		}
		
		//获取用户
		User loginUser = (User) reqquest.getSession().getAttribute("loginUser");
		//获取组织机构
		Organization org = loginUser.getOrg();
		//网点
		Dealer dealer = loginUser.getDealer();
		List<Map<String, Object>> nodes = new ArrayList<Map<String, Object>>();
		if(null == org && null!= dealer){ //网点用户
			Map<String, Object> node = new HashMap<String, Object>();
			node.put("id", dealer.getId());
			node.put("text", dealer.getName());
			node.put("checked","true");
			node.put("state","open");
			node.put("isForbid", dealer.getStatus().getCode());
			node.put("type", "dealer");
			nodes.add(node);
			return (JSON) JSON.toJSON(nodes);
		}
		if(null!=org && null == dealer){  //大区用户
			List<Organization> root =null;
			if(1 == org.getLevel() && StringUtils.isEmpty(id)){ //总部
				root = organizationService.getChildren(null);
				for (Organization child : root) {
					Map<String, Object> node = new HashMap<String, Object>();
					node.put("id", child.getId());
					node.put("text", child.getName());
					node.put("checked", checked == child.getId());
					node.put("isForbid", child.getStatus().getCode());
					if(child.getLevel() == 2){
						if(child.getId()==1){
							node.put("state", "open");
						}else{
							node.put("state", dealerService.getChildrenCount(child.getId()) > 0 ? "closed" : "open");
						}
						node.put("type", "largeArea");
					}else{
						if(child.getId()==1){
							node.put("state", "open");
						}else{
							node.put("state", organizationService.hasChildren(child.getId()) ? "closed" : "open");
						}
						node.put("type", "headquarters");
					}
					if(child.getId()==1){
						List<Map<String, Object>> nodes1 = new ArrayList<Map<String, Object>>();
						List<Organization> children1 = organizationService.getActiviteChildren(child.getId());
						for (Organization child1 : children1) {
							Map<String, Object> node1 = new HashMap<String, Object>();
							node1.put("id", child1.getId());
							node1.put("text", child1.getName());
							node1.put("checked", checked == child.getId());
							if(checked==1){
								node1.put("option", "0");
							}else{
								node1.put("option", "1");
							}
							node1.put("isForbid", child1.getStatus().getCode());;
							if(child1.getLevel() == 2){
								node1.put("type", "largeArea");
								node1.put("state", dealerService.getChildrenCount(child1.getId()) > 0 ? "closed" : "open");
							}else{
								node1.put("state", organizationService.hasChildren(child1.getId()) ? "closed" : "open");
								node1.put("type", "headquarters");
							}
							
							nodes1.add(node1);
						}
						node.put("children", nodes1);
					}
					
					nodes.add(node);
				}
				
				return (JSON) JSON.toJSON(nodes);
			}else if(1!=loginUser.getId() && 2 == org.getLevel()){
					Map<String, Object> node = new HashMap<String, Object>();
					node.put("id", org.getId());
					node.put("text", org.getName());
					node.put("checked", checked == org.getId());
					node.put("isForbid", org.getStatus().getCode());
					if(org.getLevel() == 2){
						if(org.getId()==1){
							node.put("state", "open");
						}else{
							node.put("state", dealerService.getChildrenCount(org.getId()) > 0 ? "closed" : "open");
						}
						node.put("type", "largeArea");
					}else{
						if(org.getId()==1){
							node.put("state", "open");
						}else{
							node.put("state", organizationService.hasChildren(org.getId()) ? "closed" : "open");
						}
						node.put("type", "headquarters");
					}
					if(org.getLevel()==2){
						List<Map<String, Object>> nodes1 = new ArrayList<Map<String, Object>>();
						List<Dealer> children1 =dealerService.getByActivitOrganizationId(org.getId());
						for (Dealer child1 : children1) {
							Map<String, Object> node1 = new HashMap<String, Object>();
							node1.put("id", child1.getId());
							node1.put("text", child1.getName());
							node1.put("checked", checked == child1.getId());
							if(checked==1){
								node1.put("option", "0");
							}else{
								node1.put("option", "1");
							}
							node1.put("isForbid", child1.getStatus().getCode());;
							node1.put("type", "dealer");
						    node1.put("state","open");
							nodes1.add(node1);
						}
						node.put("children", nodes1);
					}
					
					nodes.add(node);
				
				return (JSON) JSON.toJSON(nodes);
			}else{  //网点
				List<Dealer> children = dealerService.getByActivitOrganizationId(Long.parseLong(id));
				for (Dealer child : children) {
					Map<String, Object> node = new HashMap<String, Object>();
					node.put("id", child.getId());
					node.put("text", child.getName());
					node.put("checked","false");
					node.put("state","open");
					node.put("isForbid", child.getStatus().getCode());
					node.put("type", "dealer");
					
					nodes.add(node);
				}
				return (JSON) JSON.toJSON(nodes);
			}
			
		}
		return null;
	}
	
	@RequestMapping(value = "/treeReport")
	@ResponseBody
	public JSON treeReport(Model model, HttpServletRequest reqquest) throws LedpException {
		Long parent = null;
		try {
			String parentId = reqquest.getParameter("id");
			if (parentId != null) {
				parent = Long.parseLong(parentId);
			}
		} catch (Exception e) {
			Organization organization = (Organization) reqquest.getSession().getAttribute("organization");
			if (organization != null) {
				parent = organization.getId();
			}
		}

		Long checked = 0l;
		try {
			checked = Long.parseLong(reqquest.getParameter("checked"));
		} catch (Exception e) {
		}
		
		List<Map<String, Object>> nodes = new ArrayList<Map<String, Object>>();
		Organization org = organizationService.get(parent);
		if(org.getLevel() == 2){
			//是大区，查询经销商表数据
			List<Dealer> children = dealerService.getByOrganizationId(parent);
			for (Dealer child : children) {
				Map<String, Object> node = new HashMap<String, Object>();
				node.put("id", child.getId());
				node.put("text", child.getName());
				node.put("checked", checked == child.getId());
				node.put("state", "open");
				nodes.add(node);
			}
		}else{
			List<Organization> children = organizationService.getChildren(parent);
			for (Organization child : children) {
				Map<String, Object> node = new HashMap<String, Object>();
				node.put("id", child.getId());
				node.put("text", child.getName());
				node.put("checked", checked == child.getId());
				node.put("state", organizationService.hasChildren(child.getId()) ? "closed" : "open");
				nodes.add(node);
			}
		}
		
		
		
		return (JSON) JSON.toJSON(nodes);
	}
	
	/**
	 * 查询大区数据
	 * @param model
	 * @param reqquest
	 * @return
	 * @throws LedpException
	 */
	@RequestMapping(value = "/treeRegionReport")
	@ResponseBody
	public JSON treeRegionReport(Model model, HttpServletRequest reqquest) throws LedpException {
		Long parent = null;
		try {
			String parentId = reqquest.getParameter("id");
			if (parentId != null) {
				parent = Long.parseLong(parentId);
			}
		} catch (Exception e) {
		}

		String checkedNodes = reqquest.getParameter("checkedNodes");
		if(StringUtils.isBlank(checkedNodes)){
			checkedNodes = "";
		}
		
		List<Map<String, Object>> nodes = new ArrayList<Map<String, Object>>();
		List<Organization> children = organizationService.getChildren(parent);
		for (Organization child : children) {
			Map<String, Object> node = new HashMap<String, Object>();
			node.put("id", child.getId());
			node.put("text", child.getName());
			node.put("checked", checkedNodes.contains(child.getId() + ","));
			node.put("state", "open");
			nodes.add(node);
		}
		
		return (JSON) JSON.toJSON(nodes);
	}
	
	@RequestMapping(value = "/treeDealerReport")
	@ResponseBody
	public JSON treeDealerReport(Model model, HttpServletRequest reqquest) throws LedpException {
		Long parent = null;
		try {
			String parentId = reqquest.getParameter("id");
			if (parentId != null) {
				parent = Long.parseLong(parentId);
			}
		} catch (Exception e) {
		}

		String checkedNodes = reqquest.getParameter("checkedNodes");
		if(StringUtils.isBlank(checkedNodes)){
			checkedNodes = "";
		}
		
		if(null != parent){
			Organization org = organizationService.get(parent);
			if(org.getLevel() == 1){
				//总部，查询所有经销商
				parent = null;
			}
		}
		
		List<Map<String, Object>> nodes = new ArrayList<Map<String, Object>>();
		//是大区，查询经销商表数据
		List<Dealer> children = dealerService.getByOrganizationId(parent);
		for (Dealer child : children) {
			Map<String, Object> node = new HashMap<String, Object>();
			node.put("id", child.getId());
			node.put("text", child.getName());
			node.put("checked", checkedNodes.contains(child.getId() + ","));
			node.put("state", "open");
			nodes.add(node);
		}
		
		
		
		return (JSON) JSON.toJSON(nodes);
	}


	private List<Map<String, Object>> getParent(long id, long checked) throws LedpException {
		List<Map<String, Object>> nodes = new ArrayList<Map<String, Object>>();
		Organization organization = organizationService.get(id);
		Organization parent = organization.getParent();
		if (parent != null) {
			List<Map<String, Object>> brothers = getChildren(parent.getId(), checked);
			for (Map<String, Object> brother : brothers) {
				Long bid = Long.parseLong(brother.get("id").toString());
				if (bid == id && brother.containsKey("parent")) {
					Long pid = Long.parseLong(brother.get("parent").toString());
					List<Map<String, Object>> parents = getParent(pid, checked);
					for (Map<String, Object> p : parents) {
						Long ppid = Long.parseLong(p.get("id").toString());
						if (pid == ppid) {
							p.put("children", brothers);
							break;
						}
					}
					break;
				}
			}
		}

		return nodes;
	}

	private List<Map<String, Object>> getChildren(long parent, long checked) throws LedpException {
		List<Organization> children = organizationService.getChildren(parent);
		List<Map<String, Object>> nodes = new ArrayList<Map<String, Object>>();
		for (Organization child : children) {
			Map<String, Object> node = new HashMap<String, Object>();
			node.put("id", child.getId());
			node.put("text", child.getName());
			node.put("checked", checked == child.getId());
			node.put("state", "open");
			if (organizationService.hasChildren(child.getId())) {
				node.put("state", "closed");
			}

			if (child.getParent() != null) {
				node.put("parent", child.getParent().getId());
			}

			nodes.add(node);
		}
		return nodes;
	}

}
