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
import com.citroen.ledp.domain.Mapping;
import com.citroen.ledp.domain.User;
import com.citroen.ledp.exception.LedpException;
import com.citroen.ledp.interceptor.Permission;
import com.citroen.ledp.service.ConstantService;
import com.citroen.ledp.service.MappingService;
import com.citroen.ledp.service.PermissionService;
import com.citroen.ledp.util.JSONConverter;
import com.citroen.ledp.util.LedpLogger;
import com.citroen.ledp.util.LedpLogger.Operation;
import com.citroen.ledp.util.LedpLogger.Result;

/**
 * @Title: mappingController.java
 * @Package com.citroen.ledp.controller
 * @Description: TODO(代码映射管理类)
 * @author 廖启洪
 * @date 2015年1月29日 下午3:24:24
 * @version V1.0
 */
@Controller
@RequestMapping("/mapping")
public class MappingController {
	private Map<String,Object> params;
	@Autowired
	private ConstantService constantService;
	@Autowired
	private PermissionService permissionService;
	@Autowired
	private MappingService mappingService;

	@RequestMapping(value={"","/index"})
	@Permission(code="mapping/index")
	public String index(Model model) throws Exception {
		Constant type = constantService.find("mapping_type","dealer");
		model.addAttribute("type",type);
		if(permissionService.hasAuth("mapping/create")){
			model.addAttribute("permission","permission");
		}
		return "mapping/index";
	}

	@RequestMapping(value={"search"})
	@ResponseBody
	@Permission(code="mapping/index")
	public JSON search(Model model,HttpServletRequest request) throws Exception {
		final String contextPath = request.getContextPath();
		int pageSize   = 10;
		int pageNumber = 0;
		String sortName  = request.getParameter("sortName");
		String sortOrder = request.getParameter("sortOrder");
		if(StringUtils.isBlank(sortName)){
			sortName = "code1";
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
		params.put("pageSize", pageSize);
		params.put("pageNumber", pageNumber);
		
		String typeId = request.getParameter("type.id");
		if(StringUtils.isBlank(typeId)){
			typeId =  constantService.findAll("mapping_type",2).get(0).getId().toString();
		}
		
		params.put("type.id", typeId);
		List<Map> rows = mappingService.executeQuery(params);
		int total = mappingService.getTotalRow(params);
			JSON data = JSONConverter.convert(pageSize,pageNumber,sortName,sortOrder, total,rows,new String[] {"typeName","name","code","code1","code2","code3","code4"},
					new JSONConverter.Operation<Map>() {
				public String operate(Map t) {
					if(permissionService.hasAuth("mapping/update")){
						return "<a href='"+contextPath+"/mapping/update/" + t.get("id")
								+ "' title='编辑' class='edit'><img alt='' src='../images/edit.png' /></a>";
					}
					return "";
				}
			});
			return data;
	}

	/**
	 * @Title: create
	 * @Description: TODO(添加代码映射入口)
	 * @param model 参数传递容器
	 * @throws Exception
	 * @return String
	 */
	@RequestMapping(value="/create")
	@Permission(code="mapping/create")
	public String create(Model model,@ModelAttribute("mapping") Mapping mapping) throws Exception {
		List<Map> records = null;
		if(mapping.getType()!=null){
			Constant type = constantService.get(mapping.getType().getId());
			mapping.setType(type);
		}else{
			Constant type = constantService.find("mapping_type","dealer");
			mapping.setType(type);
		}
		model.addAttribute("mapping",mapping);
		return "mapping/create";
	}

	/**
	 * @Title: save
	 * @Description: TODO(新增代码映射方法)
	 * @param model 参数传递容器
	 * @throws Exception
	 * @return String
	 */
	@RequestMapping(value="/save",method=RequestMethod.POST)
	@Permission(code="mapping/create")
	public String save(Model model,@Valid @ModelAttribute("mapping") Mapping mapping,BindingResult br,HttpServletRequest request) throws Exception {
		HttpSession session = request.getSession();
		if(br.hasErrors()){
			model.addAttribute("mapping",mapping);
			return "mapping/create";
		}
/*		if(mappingService.finds1(mapping.getType(), "code", mapping.getCode()) != null){
			br.rejectValue("code","mapping.name.already.exists","冬雪代码已经存在，请重新输入！");
			model.addAttribute("mapping",mapping);
			LedpLogger.info((User) session.getAttribute("loginUser"), "增加代码", Operation.create,Result.failure,"冬雪代码已经存在");
			return "mapping/create";
		}
		if(mappingService.finds1(mapping.getType(), "code1", mapping.getCode1()) != null){
			br.rejectValue("code1","mapping.name.already.exists","官网代码已经存在，请重新输入！");
			model.addAttribute("mapping",mapping);
			LedpLogger.info((User) session.getAttribute("loginUser"), "增加代码", Operation.create,Result.failure,"官网代码已经存在");
			return "mapping/create";
		}
		if(mappingService.finds1(mapping.getType(), "code2", mapping.getCode2()) != null){
			br.rejectValue("code2","mapping.name.already.exists","汽车之家代码已经存在，请重新输入！");
			model.addAttribute("mapping",mapping);
			LedpLogger.info((User) session.getAttribute("loginUser"), "增加代码", Operation.create,Result.failure,"汽车之家代码已经存在");
			return "mapping/create";
		}
		if(mappingService.finds1(mapping.getType(), "code3", mapping.getCode3()) != null){
			br.rejectValue("code3","mapping.name.already.exists","易车代码已经存在，请重新输入！");
			model.addAttribute("mapping",mapping);
			LedpLogger.info((User) session.getAttribute("loginUser"), "增加代码", Operation.create,Result.failure,"易车代码已经存在");
			return "mapping/create";
		}*/
		mappingService.save(mapping);
		model.addAttribute("org","代码映射添加成功");
		if(permissionService.hasAuth("mapping/create")){
			model.addAttribute("permission","permission");
		}
		LedpLogger.info((User) session.getAttribute("loginUser"), "增加代码", Operation.create,Result.success);
		return "mapping/index";
	}
	
	/**
	 * @Title: update
	 * @Description: TODO(修改代码映射入口)
	 * @param model 参数传递容器
	 * @param id 代码映射ID
	 * @return String
	 * @throws LedpException 
	 */
	@RequestMapping(value="/update/{id}")
	@Permission(code="mapping/update")
	public String update(Model model,@PathVariable long id) throws LedpException {
		Mapping mapping = mappingService.get(id);
		Map record = mappingService.getRecord(mapping);
		model.addAttribute("record",record);
		model.addAttribute("mapping",mapping);
		return "mapping/update";
	}
	
	/**
	 * @Title: edit
	 * @Description: TODO(修改代码映射方法)
	 * @param model 参数传递容器
	 * @return String
	 * @throws LedpException 
	 */
	@RequestMapping(value="/edit",method=RequestMethod.POST)
	@Permission(code="mapping/update")
	public String edit(Model model,@Valid @ModelAttribute("mapping") Mapping mapping,BindingResult br,HttpServletRequest request) throws LedpException {
		
		HttpSession session = request.getSession();
		if(br.hasErrors()){
			model.addAttribute("mapping",mapping);
			return "mapping/create";
		}
		Mapping mapping1 = mappingService.get(mapping.getId());
		Map record = mappingService.getRecord(mapping1);
		model.addAttribute("record",record);
		Mapping mp = mappingService.find(mapping.getType(),mapping.getRecord());
/*		if(mappingService.finds(mapping.getType(), "code", mapping.getCode(),mapping.getId()) != null){
			br.rejectValue("code","mapping.name.already.exists","冬雪代码已经存在，请重新输入！");
			model.addAttribute("mapping",mapping);
			LedpLogger.info((User) session.getAttribute("loginUser"), "修改代码", Operation.update,Result.failure,"冬雪代码已经存在");
			return "mapping/update";
		}
		if(mappingService.finds(mapping.getType(), "code1", mapping.getCode1(),mapping.getId()) != null){
			br.rejectValue("code1","mapping.name.already.exists","官网代码已经存在，请重新输入！");
			LedpLogger.info((User) session.getAttribute("loginUser"), "修改代码", Operation.update,Result.failure,"官网代码已经存在");
			model.addAttribute("mapping",mapping);
			return "mapping/update";
		}
		if(mappingService.finds(mapping.getType(), "code2", mapping.getCode2(),mapping.getId()) != null){
			br.rejectValue("code2","mapping.name.already.exists","汽车之家代码已经存在，请重新输入！");
			model.addAttribute("mapping",mapping);
			LedpLogger.info((User) session.getAttribute("loginUser"), "修改代码", Operation.update,Result.failure,"汽车之家代码已经存在");
			return "mapping/update";
		}
		if(mappingService.finds(mapping.getType(), "code3", mapping.getCode3(),mapping.getId()) != null){
			br.rejectValue("code3","mapping.name.already.exists","易车代码已经存在，请重新输入！");
			model.addAttribute("mapping",mapping);
			LedpLogger.info((User) session.getAttribute("loginUser"), "修改代码", Operation.update,Result.failure,"易车代码已经存在");
			return "mapping/update";
		}*/
		Mapping mapping2 = mappingService.get(mapping.getId());
		FieldBinder.bind(mapping2, params);
		mappingService.update(mapping2);
		
		model.addAttribute("org","代码映射添加成功");
		if(permissionService.hasAuth("mapping/create")){
			model.addAttribute("permission","permission");
		}
		LedpLogger.info((User) session.getAttribute("loginUser"), "修改代码", Operation.update,Result.success);
		return "mapping/index";
	}
	@RequestMapping(value="/detail/{id}")
	@Permission(code="mapping/detail")
	public String detail(Model model,@PathVariable long id) throws LedpException {
		Mapping mapping = mappingService.get(id);
		Map record = mappingService.getRecord(mapping);
		model.addAttribute("mapping",mapping);
		model.addAttribute("record",record);
		
		return "mapping/detail";
	}
	
	/**
	 * @Title: delete
	 * @Description: TODO(修改代码映射方法)
	 * @param model 参数传递容器
	 * @return String
	 * @throws LedpException 
	 */
	@RequestMapping(value="/delete/{id}")
	@Permission(code="mapping/delete")
	public String delete(@PathVariable long id) throws LedpException {
		mappingService.delete(id);
		return "mapping/index";
	}
	
	@RequestMapping(value="/records/{typeId}")
	public String records(Model model,@PathVariable long typeId) throws LedpException {
		Constant type = constantService.get(typeId);
		if(type!=null){
			List<Map> records = mappingService.getUnbindRecords(type);
			model.addAttribute("type",type);
			model.addAttribute("records",records);
		}
		return "mapping/records";
	}
	
	@RequestMapping(value="/type")
	@ResponseBody
	public JSON type() throws LedpException {
		List<Map<String,Object>> nodes = new ArrayList<Map<String,Object>>();
		List<Constant> mappingTypes = constantService.findAll("mapping_type",2);
		
		for(int index = 0;index<mappingTypes.size();index++){
			Constant type = mappingTypes.get(index);
			Map<String,Object> node = new HashMap<String,Object>();
			node.put("id",type.getId());
			node.put("code",type.getCode());
			node.put("text",type.getName());
			node.put("state","open");
			node.put("checked",(index==0));
			nodes.add(node);
		}
		return (JSON)JSON.toJSON(nodes);
	}
	
	/**
	 * 验证映射不允许重复
	 * @param model
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value={"validate"})
	@ResponseBody
	@Permission(code="mapping/index")
	public Map validate(Model model,HttpServletRequest request) throws Exception {
		Map<String,Object> result = new HashMap<String,Object>();
		String typeId  = request.getParameter("typeId");
		String recordId = request.getParameter("recordId");
		if(StringUtils.isBlank(typeId) || StringUtils.isBlank(recordId)){
			result.put("code", false);
			return result;
		}
		Map mapping = mappingService.getMapping(typeId, recordId);
		if(mapping != null){
			result.put("code", false);
			return result;
		}
		result.put("code", true);
		return result;
	}
}
