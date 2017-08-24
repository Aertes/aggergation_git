package com.citroen.ledp.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.citroen.ledp.domain.Constant;
import com.citroen.ledp.domain.Permission;
import com.citroen.ledp.domain.Role;
import com.citroen.ledp.domain.User;
import com.citroen.ledp.exception.LedpException;
import com.citroen.ledp.service.ConstantService;
import com.citroen.ledp.service.PermissionService;
import com.citroen.ledp.service.RoleService;
import com.citroen.ledp.util.JSONConverter;
import com.citroen.ledp.util.LedpLogger;
import com.citroen.ledp.util.LedpLogger.Operation;
import com.citroen.ledp.util.LedpLogger.Result;

/**
 * @Title: roleController.java
 * @Package com.citroen.ledp.controller
 * @Description: TODO(角色管理类)
 * @author 廖启洪
 * @date 2015年1月29日 下午3:24:24
 * @version V1.0
 */
@Controller
@RequestMapping("/role")
public class RoleController {
	private Map<String,Object> params;
	@Autowired
	private RoleService roleService;
	@Autowired
	private ConstantService constantService;
	@Autowired
	private PermissionService permissionService;
	
	@RequestMapping(value={"","/index"})
	@com.citroen.ledp.interceptor.Permission(code={"role/index"})
	public String index(Model model) throws Exception {
		// 返回到界面
		if(permissionService.hasAuth("role/create")){
			model.addAttribute("permission","permission");
		}
		return "role/index";
	}
	@RequestMapping(value={"search"})
	@ResponseBody
	@com.citroen.ledp.interceptor.Permission(code={"role/index"})
	public JSON search(Model model,HttpServletRequest request) throws Exception {
		// 返回到界面
		
		final String contextPath = request.getContextPath();

		int pageSize   = 10;
		int pageNumber = 1;
		String sortName  = request.getParameter("sortName");
		String sortOrder = request.getParameter("sortOrder");
		if(StringUtils.isBlank(sortName)){
			sortName = "name";
		}
		if(StringUtils.isBlank(sortOrder)){
			sortOrder = "asc";
		}
		try{
			pageSize = Integer.parseInt(request.getParameter("pageSize"));
		}catch(Exception e){}
		try{
			pageNumber = Integer.parseInt(request.getParameter("pageNumber"));
		}catch(Exception e){}
		params.put("pageSize", pageSize);
		params.put("pageNumber", pageNumber);
		params.put("sortName", sortName);
		params.put("sortOrder", sortOrder);
		List<Role> rows = roleService.executeQuery(params);
		int total = roleService.getTotalRow(params);
		JSON data = JSONConverter.convert(pageSize,pageNumber,sortName,sortOrder, total,rows,new String[] {"name", "status.name"},
				new JSONConverter.Operation<Role>() {
					public String operate(Role t) {
						if(permissionService.hasAuth("role/detail")){
							return "<a class='ielookDetails' href='"+contextPath+"/role/detail/" + t.getId()
									+ "' title='查看'><img alt='' src='../images/magnifier.png'></a>";
						}
						return "";
					}
				}, new JSONConverter.Operation<Role>() {
					public String operate(Role t) {
						if(permissionService.hasAuth("role/update")){
							return "<a href='"+contextPath+"/role/update/" + t.getId()
									+ "' title='编辑' class='edit'><img alt='' src='../images/edit.png' /></a>";
						}
						return "";
					}
				}, new JSONConverter.Operation<Role>() {
					public String operate(Role t) {
						if(permissionService.hasAuth("role/update")){
							Constant status = t.getStatus();
							if ("active".equals(status.getCode())) {
								return "<a href='#' title='禁用' d_id=" + t.getId()
										+ " status_code='active' class='ONOFF' onclick='delete_message(\""+contextPath+"/role/inactive/" + t.getId()
										+ "\",\"确定要禁用吗？\",false,inactiveCallback)'><img alt='' src='../images/jy.png' />";
							}
							return "<a href='#' title='启用' d_id=" + t.getId()
									+ " status_code='active' class='ONOFF' onclick='delete_message(\""+contextPath+"/role/active/" + t.getId()
									+ "\",\"确定要启用吗？\",false,activeCallback)'><img alt='' src='../images/jynone.png' />";
						}
						return "";
					}
				});
		return data;
	}
	/**
	 * @Title: create
	 * @Description: TODO(添加角色入口)
	 * @param model 参数传递容器
	 * @throws Exception
	 * @return String
	 */
	@RequestMapping(value="/create")
	@com.citroen.ledp.interceptor.Permission(code={"role/create"})
	public String create(Model model,@ModelAttribute("role") Role role) throws Exception {
		Constant status = constantService.find("record_status","active");
		role.setStatus(status);
		model.addAttribute("role",role);
		return "role/create";
	}

	/**
	 * @Title: save
	 * @Description: TODO(新增角色方法)
	 * @param model 参数传递容器
	 * @throws Exception
	 * @return String
	 */
	@RequestMapping(value="/save",method=RequestMethod.POST)
	@com.citroen.ledp.interceptor.Permission(code={"role/create"})
	public String save(Model model,HttpServletRequest request,@Valid @ModelAttribute("role") Role role,BindingResult br) throws Exception {
		HttpSession session = request.getSession();
		if(br.hasErrors()){
			model.addAttribute("role",role);
			LedpLogger.info((User) session.getAttribute("loginUser"), "增加角色", Operation.create,Result.failure);
			return "role/create";
		}
		if(role.getName()==null && role.getName().trim()==""){
			br.rejectValue("name","role.name.already.exists","角色名称不能为空，请输入！");
			model.addAttribute("role",role);
			LedpLogger.info((User) session.getAttribute("loginUser"), "增加角色", Operation.create,Result.failure,"角色名称不能为空");
			return "role/create";
		}
		
		Role nameExistRole = roleService.find(role.getName());
		if(nameExistRole!=null && nameExistRole.getId()!=role.getId()){
			br.rejectValue("name","role.name.already.exists","角色名称已经存在，请重新输入！");
			model.addAttribute("role",role);
			LedpLogger.info((User) session.getAttribute("loginUser"), "增加角色", Operation.create,Result.failure,"角色名称已经存在");
			return "role/create";
		}
		if(role.getStatus().getId()==null || role.getStatus().getId().toString().trim()==""){
			br.rejectValue("status.id","status.id is not null","角色状态不能为空，请选择！！");
			model.addAttribute("role",role);
			LedpLogger.info((User) session.getAttribute("loginUser"), "增加角色", Operation.create,Result.failure,"角色状态不能为空");
			return "role/create";
		}
		
		String checkeds = request.getParameter("checkeds");
		if(checkeds==null || checkeds.trim()==""){
			br.rejectValue("name","checkeds is null","角色权限不能为空！！");
			model.addAttribute("role",role);
			LedpLogger.info((User) session.getAttribute("loginUser"), "增加角色", Operation.create,Result.failure,"角色权限不能为空");
			return "role/create";
		}
		String[] permissionIds=checkeds.split(",");
		Set<String> checke=new HashSet<String>();
		for(int i=0;i<permissionIds.length;i++){
			checke=getPermissionIds(checke,Long.parseLong(permissionIds[i]));
		}
		if(StringUtils.isBlank(checkeds)){
			roleService.save(role,null);
		}else{
			roleService.save(role,checke);
		}
		LedpLogger.info((User) session.getAttribute("loginUser"), "增加角色", Operation.create,Result.success);
		if(permissionService.hasAuth("role/create")){
			model.addAttribute("permission","permission");
		}
		return "role/index";
	}
	
	/**
	 * @Title: update
	 * @Description: TODO(修改角色入口)
	 * @param model 参数传递容器
	 * @param id 角色ID
	 * @return String
	 * @throws LedpException 
	 */
	@RequestMapping(value="/update/{id}")
	@com.citroen.ledp.interceptor.Permission(code={"role/update"})
	public String update(Model model,@PathVariable long id) throws LedpException {
		Role role = roleService.get(id);
		model.addAttribute("role",role);
		return "role/update";
	}
	
	/**
	 * @Title: edit
	 * @Description: TODO(修改角色方法)
	 * @param model 参数传递容器
	 * @return String
	 * @throws LedpException 
	 */
	@RequestMapping(value="/edit",method=RequestMethod.POST)
	@com.citroen.ledp.interceptor.Permission(code={"role/update"})
	public String edit(Model model,HttpServletRequest request,@Valid @ModelAttribute("role") Role role,BindingResult br) throws LedpException {
		HttpSession session = request.getSession();
		if(br.hasErrors()){
			model.addAttribute("role",role);
			LedpLogger.info((User) session.getAttribute("loginUser"), "修改角色", Operation.update,Result.failure);
			return "role/update";
		}
		/*Role nameExistRole = roleService.find(role.getName());
		if(nameExistRole!=null && nameExistRole.getId()!=role.getId()){
			br.rejectValue("name","role.name.already.exists","角色名称已经存在，请重新输入！");
			model.addAttribute("role",role);
			LedpLogger.info((User) session.getAttribute("loginUser"), "修改角色", Operation.update,Result.failure,"角色名称已经存在");
			return "role/update";
		}*/
		
		if(role.getStatus().getId()==null || role.getStatus().getId().toString().trim()==""){
			br.rejectValue("status.id","status.id is not null","角色状态不能为空，请选择！！");
			model.addAttribute("role",role);
			LedpLogger.info((User) session.getAttribute("loginUser"), "修改角色", Operation.update,Result.failure,"角色状态不能为空");
			return "role/update";
		}
		
		String checkeds = request.getParameter("checkeds");
		if(checkeds==null || checkeds.trim()==""){
			br.rejectValue("name","checkeds is null","角色权限不能为空！！");
			model.addAttribute("role",role);
			LedpLogger.info((User) session.getAttribute("loginUser"), "修改角色", Operation.update,Result.failure,"角色权限不能为空");
			return "role/update";
		}
		String[] permissionIds=checkeds.split(",");
		Set<String> checke=new HashSet<String>();
		for(int i=0;i<permissionIds.length;i++){
			checke=getPermissionIds(checke,Long.parseLong(permissionIds[i]));
		}
		if(StringUtils.isBlank(checkeds)){
			roleService.update(role,null);
		}else{
			roleService.update(role,checke);
		}
		if(permissionService.hasAuth("role/create")){
			model.addAttribute("permission","permission");
		}
		LedpLogger.info((User) session.getAttribute("loginUser"), "修改角色", Operation.update,Result.success);
		return "role/index";
	}
	
	/**
	 * @Title: detail
	 * @Description: TODO(查看角色方法)
	 * @param model 参数传递容器
	 * @param id 角色ID
	 * @return String
	 * @throws LedpException 
	 */
	@RequestMapping(value="/detail/{id}")
	@com.citroen.ledp.interceptor.Permission(code={"role/detail"})
	public String detail(Model model,@PathVariable long id) throws LedpException {
		Role role = roleService.get(id);
		model.addAttribute("role",role);
		return "role/detail";
	}
	@RequestMapping(value="/active/{id}")
	@ResponseBody
	@com.citroen.ledp.interceptor.Permission(code={"role/update"})
	public JSON active(Model model,@PathVariable long id) throws LedpException {
		Role role = roleService.get(id);
		if(role==null){
			throw new LedpException();
		}
		Constant status = constantService.find("record_status","active");
		role.setStatus(status);
		roleService.update(role);
		Map json = new HashMap<String,String>();
		json.put("code","success_message");
		json.put("message","角色"+status.getName()+"成功 ！");
		
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
	@RequestMapping(value="/inactive/{id}")
	@ResponseBody
	@com.citroen.ledp.interceptor.Permission(code={"role/update"})
	public JSON inactive(Model model,@PathVariable long id) throws LedpException {
		Role role = roleService.get(id);
		if(role==null){
			throw new LedpException();
		}
		Constant status = constantService.find("record_status","inactive");
		role.setStatus(status);
		roleService.update(role);
		Map json = new HashMap<String,String>();
		json.put("code","success_message");
		json.put("message","角色"+status.getName()+"成功 ！");
		
		return (JSON)JSON.toJSON(json);
	}
	
	@RequestMapping(value="/tree")
	@ResponseBody
	public JSON tree(HttpServletRequest reqquest) throws LedpException{
		Long targetRoleId = null;
		Long loginRoleId = null;
		Long parent = null;
		try{targetRoleId = Long.parseLong(reqquest.getParameter("roleId"));}catch(Exception e){}
		try{
			Role loginRole =(Role)reqquest.getSession().getAttribute("loginRole");
			loginRoleId  = loginRole.getId();
		}catch(Exception e){}
		try{parent = Long.parseLong(reqquest.getParameter("id"));}catch(Exception e){}
		List<Map<String,Object>> nodes = getNewRoleNodes(parent,loginRoleId,targetRoleId);
		return (JSON)JSON.toJSON(nodes);
	}
	
	private List<Map<String,Object>> getNewRoleNodes(Long parent,Long loginRoleId,Long targetRoleId) throws LedpException{
		List<Permission> checkeds = roleService.getRolePermission(parent,targetRoleId);
		List<Long> list = new ArrayList<Long>();
		for(Permission p:checkeds){
			list.add(p.getId());
		}
		List<Map<String,Object>> nodes = new ArrayList<Map<String,Object>>();
		List<Permission> children = roleService.getRolePermission(parent,loginRoleId);
		for(Permission p:children){
			Map<String,Object> node = new HashMap<String,Object>();
			node.put("id",p.getId());
			node.put("text",p.getName());
			node.put("state","open");
			node.put("checked",list.contains(p.getId()));
			if(roleService.hasChildren(p.getId())){
				node.put("children",getNewRoleNodes(p.getId(),loginRoleId,targetRoleId));
			}
			nodes.add(node);
		}
		return nodes;
	} 
	
	@RequestMapping(value="/treeDetail")
	@ResponseBody
	public JSON treeDetail(HttpServletRequest reqquest) throws LedpException{
		Long roleId = null;
		Long parent = null;
		try{roleId = Long.parseLong(reqquest.getParameter("roleId"));}catch(Exception e){}
		try{parent = Long.parseLong(reqquest.getParameter("id"));}catch(Exception e){}
		List<Map<String,Object>> nodes = getShowRoleNodes(parent,roleId);
		return (JSON)JSON.toJSON(nodes);
	}
	
	@RequestMapping(value="/treeDetails")
	@ResponseBody
	public JSON treeDetails(HttpServletRequest reqquest) throws LedpException{
		Long roleId = null;
		Long parent = null;
		try{roleId = Long.parseLong(reqquest.getParameter("roleId"));}catch(Exception e){}
		try{parent = Long.parseLong(reqquest.getParameter("id"));}catch(Exception e){}
		List<Map<String,Object>> nodes = getShowRoleNodes(parent,roleId);
		return (JSON)JSON.toJSON(nodes);
	}
	
	private List<Map<String,Object>> getShowRoleNodes(Long parent,Long roleId) throws LedpException{
		List<Map<String,Object>> nodes = new ArrayList<Map<String,Object>>();
		List<Permission> children = roleService.getRolePermission(parent,roleId);
		for(Permission p:children){
			Map<String,Object> node = new HashMap<String,Object>();
			node.put("id",p.getId());
			node.put("text",p.getName());
			node.put("state","open");
			node.put("checked",true);
			if(roleService.hasChildren(p.getId())){
				node.put("children",getShowRoleNodes(p.getId(),roleId));
			}	
			nodes.add(node);
		}
		return nodes;
	} 
	private Set<String> getPermissionIds(Set<String>checkeds,Long perId) throws LedpException{
		Permission permission=permissionService.get(perId);
		checkeds.add(permission.getId().toString());
		if(permission.getParent()!=null){
			Permission parent=permission.getParent();
			return getPermissionIds(checkeds,parent.getId());
		}
		return checkeds;
	}
	
}
