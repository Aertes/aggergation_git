package com.citroen.ledp.controller;

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
import com.citroen.ledp.dao.mybaits.MybaitsGenericDao;
import com.citroen.ledp.domain.Constant;
import com.citroen.ledp.domain.Dealer;
import com.citroen.ledp.domain.User;
import com.citroen.ledp.domain.VehicleSeries;
import com.citroen.ledp.exception.LedpException;
import com.citroen.ledp.service.ConstantService;
import com.citroen.ledp.service.PermissionService;
import com.citroen.ledp.service.VehicleSeriesService;
import com.citroen.ledp.util.JSONConverter;
import com.citroen.ledp.util.LedpLogger;
import com.citroen.ledp.util.LedpLogger.Operation;
import com.citroen.ledp.util.LedpLogger.Result;

/**
 * @Title: vehicleSeriesController.java
 * @Package com.citroen.ledp.controller
 * @Description: TODO(车系管理类)
 * @author 廖启洪
 * @date 2015年1月29日 下午3:24:24
 * @version V1.0
 */
@Controller
@RequestMapping("/vehicleSeries")
public class VehicleSeriesController {

	@Autowired
	private MybaitsGenericDao<Long> genericDao;
	@Autowired
	public VehicleSeriesService vehicleSeriesService;
	@Autowired
	private ConstantService constantService;
	
	@Autowired
	private PermissionService permissionService;


	@RequestMapping(value = { "", "/index" })
	@com.citroen.ledp.interceptor.Permission(code={"vehicleSeries/index"})
	public String index(Model model) throws Exception {
		// 返回到界面
		if(permissionService.hasAuth("vehicleSeries/create")){
			model.addAttribute("permission","permission");
		}
		return "vehicleSeries/index";
	}

	/**
	 * @Title: create
	 * @Description: TODO(添加车系入口)
	 * @param model
	 *            参数传递容器
	 * @throws Exception
	 * @return String
	 */
	@RequestMapping(value = "/create")
	@com.citroen.ledp.interceptor.Permission(code={"vehicleSeries/create"})
	public String create(Model model,VehicleSeries vehicleSeries ) throws Exception {
		Constant status = constantService.find("record_status","active");
		vehicleSeries.setStatus(status);
		model.addAttribute("vehicleSeries",vehicleSeries);
		
		return "vehicleSeries/create";
	}

	/**
	 * @Title: save
	 * @Description: TODO(新增车系方法)
	 * @param model
	 *            参数传递容器
	 * @throws Exception
	 * @return String
	 */
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	@com.citroen.ledp.interceptor.Permission(code={"vehicleSeries/create"})
	public String save(Model model,@Valid @ModelAttribute("vehicleSeries") VehicleSeries vehicleSeries,BindingResult br,HttpServletRequest request) throws Exception {
		HttpSession session = request.getSession();
		vehicleSeries.setName(vehicleSeries.getName().trim()); //去除空格
		VehicleSeries vehN = vehicleSeriesService.findN(vehicleSeries.getName()); //去除m名字重复
		VehicleSeries vehC = vehicleSeriesService.findC(vehicleSeries.getCode()); //去除code重复
		
		if(vehN !=null){  
			br.rejectValue("name","vehicleSeries.name.already.exists","名称已经存在，请重新输入！");
			model.addAttribute("vehicleSeries",vehicleSeries);
			LedpLogger.info((User) session.getAttribute("loginUser"), "增加车系", Operation.create,Result.failure,"名称已经存在");
			return "vehicleSeries/create";
		}
		if(vehC !=null){  
			br.rejectValue("code","vehicleSeries.code.already.exists","代码已经存在，请重新输入！");
			model.addAttribute("vehicleSeries",vehicleSeries);
			LedpLogger.info((User) session.getAttribute("loginUser"), "增加车系", Operation.create,Result.failure,"代码已经存在");
			return "vehicleSeries/create";
		}
		
		if (br.hasErrors()) {
			model.addAttribute("vehicleSeries", vehicleSeries);
			LedpLogger.info((User) session.getAttribute("loginUser"), "增加车系", Operation.create,Result.failure);
			return "vehicleSeries/create";
		}

		Long id = genericDao.save(vehicleSeries);
		if (id > 0) {
			LedpLogger.info((User) session.getAttribute("loginUser"), "增加车系", Operation.create,Result.success);
			return "redirect:/vehicleSeries/index";
		} else {
			model.addAttribute("vehicleSreies", vehicleSeries);
			LedpLogger.info((User) session.getAttribute("loginUser"), "增加车系", Operation.create,Result.failure);
			return "vehicleSreies/create";
		}
	}

	/**
	 * @Title: update
	 * @Description: TODO(修改车系入口)
	 * @param model
	 *            参数传递容器
	 * @param id
	 *            车系ID
	 * @return String
	 * @throws LedpException
	 */
	@RequestMapping(value = "/update/{id}")
	@com.citroen.ledp.interceptor.Permission(code={"vehicleSeries/update"})
	public String update(Model model, @PathVariable long id)
			throws LedpException {
		// 获取当前用户对象
		VehicleSeries vehicleSeries = genericDao.get(VehicleSeries.class, id);
		model.addAttribute("vehicleSeries", vehicleSeries);
		return "vehicleSeries/update";
	}

	/**
	 * @Title: edit
	 * @Description: TODO(修改车系方法)
	 * @param model
	 *            参数传递容器
	 * @return String
	 * @throws LedpException
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	@com.citroen.ledp.interceptor.Permission(code={"vehicleSeries/update"})
	public String edit(Model model,VehicleSeries vehicleSeries,BindingResult br,HttpServletRequest request) throws LedpException {
		HttpSession session = request.getSession();
		VehicleSeries vehN = vehicleSeriesService.findN(vehicleSeries.getName()); //去除m名字重复
		VehicleSeries vehC = vehicleSeriesService.findC(vehicleSeries.getCode()); //去除code重复
		
		if(vehN !=null && vehN.getId()!=vehicleSeries.getId()){  
			br.rejectValue("name","vehicleSeries.name.already.exists","名称已经存在，请重新输入！");
			model.addAttribute("vehicleSeries",vehicleSeries);
			LedpLogger.info((User) session.getAttribute("loginUser"), "修改车系", Operation.update,Result.failure,"名称已经存在");
			return "vehicleSeries/update";
		}
		if(vehC !=null  && vehC.getId()!=vehicleSeries.getId()){  
			br.rejectValue("code","vehicleSeries.code.already.exists","代码已经存在，请重新输入！");
			model.addAttribute("vehicleSeries",vehicleSeries);
			LedpLogger.info((User) session.getAttribute("loginUser"), "修改车系", Operation.update,Result.failure,"代码已经存在");
			return "vehicleSeries/update";
		}
		
		if (br.hasErrors()) {
			model.addAttribute("vehicleSeries", vehicleSeries);
			LedpLogger.info((User) session.getAttribute("loginUser"), "修改车系", Operation.update,Result.failure);
			return "vehicleSeries/update";
		}
		genericDao.update(vehicleSeries);
		LedpLogger.info((User) session.getAttribute("loginUser"), "修改车系", Operation.update,Result.success);
		return "redirect:/vehicleSeries/index";

	}

	/**
	 * @Title: detail
	 * @Description: TODO(查看车系方法)
	 * @param model
	 *            参数传递容器
	 * @param id
	 *            车系ID
	 * @return String
	 * @throws LedpException
	 */
	@RequestMapping(value = "/detail/{id}")
	@com.citroen.ledp.interceptor.Permission(code={"vehicleSeries/detail"})
	public String detail(Model model, @PathVariable long id)
			throws LedpException {
		VehicleSeries vehicleSeries = vehicleSeriesService.getVehicleSeries(id);
		if (vehicleSeries != null) {
			model.addAttribute("vehicleSeries", vehicleSeries);
			return "vehicleSeries/detail";
		}
		return "redirect:/vehicleSeries/index";
	}

	/**
	 * @Title: delete
	 * @Description: delete(删除车系方法)
	 * @param model
	 *            参数传递容器
	 * @param id 车系ID
	 * @return String
	 * @throws LedpException
	 */
	@RequestMapping(value = "/deletes/{id}",method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	@com.citroen.ledp.interceptor.Permission(code={"vehicleSeries/create"})
	public JSON delete(@PathVariable long id) throws LedpException {
		
		Map<String,String> data = new HashMap<String,String>();
		try {
			vehicleSeriesService.deleteVS(id);
			data.put("code", "200");
			data.put("message", "操作成功！");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return (JSON) JSON.toJSON(data);
	}
	
	
	
	
	/**
	 * @Title: detail
	 * @Description: TODO(查看机构方法)
	 * @param model 参数传递容器
	 * @param id 机构ID
	 * @return String
	 * @throws LedpException 
	 */
	@RequestMapping(value="/active/{id}")
	@ResponseBody
	@com.citroen.ledp.interceptor.Permission(code={"vehicleSeries/update"})
	public JSON active(Model model,@PathVariable long id) throws LedpException {
		VehicleSeries vehicleSeries = vehicleSeriesService.getVehicleSeries(id);
		if(vehicleSeries==null){
			throw new LedpException();
		}
		Constant status = constantService.find("record_status","active");
		vehicleSeries.setStatus(status);
		genericDao.update(vehicleSeries);
		Map json = new HashMap<String,String>();
		json.put("code","success_message");
		json.put("message","车系"+status.getName()+"成功 ！");
		
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
	@com.citroen.ledp.interceptor.Permission(code={"vehicleSeries/update"})
	public JSON inactive(Model model,@PathVariable long id) throws LedpException {
		VehicleSeries vehicleSeries = vehicleSeriesService.getVehicleSeries(id);
		if(vehicleSeries==null){
			throw new LedpException();
		}
		Constant status = constantService.find("record_status","inactive");
		vehicleSeries.setStatus(status);
		genericDao.update(vehicleSeries);
		Map json = new HashMap<String,String>();
		json.put("code","success_message");
		json.put("message","车系"+status.getName()+"成功 ！");
		
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
	@RequestMapping(value="/onsale/{id}")
	@ResponseBody
	@com.citroen.ledp.interceptor.Permission(code={"vehicleSeries/onsale"})
	public JSON onsale(Model model,@PathVariable long id,HttpSession session) throws LedpException {
		Dealer dealer = (Dealer)session.getAttribute("loginDealer");
		if(dealer==null){
			Map json = new HashMap<String,String>();
			json.put("code","success_message");
			json.put("message","非网点用户，不能上架车系 ！");
			return (JSON)JSON.toJSON(json);
		}
		try{
			vehicleSeriesService.onsale(dealer,id);
			Map json = new HashMap<String,String>();
			json.put("code","success_message");
			json.put("message","车系上架成功 ！");
			return (JSON)JSON.toJSON(json);
		}catch(Exception e){
			e.printStackTrace();
			Map json = new HashMap<String,String>();
			json.put("code","success_message");
			json.put("message","车系上架失败 ！");
			return (JSON)JSON.toJSON(json);
		}
	}
	
	/**
	 * @Title: detail
	 * @Description: TODO(查看机构方法)
	 * @param model 参数传递容器
	 * @param id 机构ID
	 * @return String
	 * @throws LedpException 
	 */
	@RequestMapping(value="/unsale/{id}")
	@ResponseBody
	@com.citroen.ledp.interceptor.Permission(code={"vehicleSeries/onsale"})
	public JSON unsale(Model model,@PathVariable long id,HttpSession session) throws LedpException {
		Dealer dealer = (Dealer)session.getAttribute("loginDealer");
		if(dealer==null){
			Map json = new HashMap<String,String>();
			json.put("code","success_message");
			json.put("message","非网点用户，不能下架车系 ！");
			return (JSON)JSON.toJSON(json);
		}
		try{
			vehicleSeriesService.unsale(dealer,id);
			Map json = new HashMap<String,String>();
			json.put("code","success_message");
			json.put("message","车系下架成功 ！");
			return (JSON)JSON.toJSON(json);
		}catch(Exception e){
			Map json = new HashMap<String,String>();
			json.put("code","success_message");
			json.put("message","车系下架失败 ！");
			return (JSON)JSON.toJSON(json);
		}
	}
	
	
	
	@RequestMapping(value = "/checkCode", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public JSON checkCode(HttpServletRequest request, String param) throws LedpException {

		String dealerId = request.getParameter("id");
		Long id = StringUtils.isNotBlank(dealerId) ? Long.parseLong(dealerId) : null;

		Map<String, String> checkResult = new HashMap<String, String>();
		checkResult.put("status", "n");
		checkResult.put("info", "车系代码已存在");

		List<VehicleSeries> vehicles = vehicleSeriesService.checkCode(param, id);
		if (StringUtils.isNotBlank(param)) {
			if (vehicles.isEmpty()) {
				checkResult.put("status", "y");
				checkResult.put("info", "");
			}
		}
		return (JSON) JSON.toJSON(checkResult);
	}
	
	
	
	@RequestMapping(value = "/checkName", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public JSON checkName(HttpServletRequest request, String param) throws LedpException {

		String dealerId = request.getParameter("id");
		Long id = StringUtils.isNotBlank(dealerId) ? Long.parseLong(dealerId) : null;

		Map<String, String> checkResult = new HashMap<String, String>();
		checkResult.put("status", "n");
		checkResult.put("info", "车系名称已存在");

		List<VehicleSeries> vehicles = vehicleSeriesService.checkName(param, id);
		if (StringUtils.isNotBlank(param)) {
			if (vehicles.isEmpty()) {
				checkResult.put("status", "y");
				checkResult.put("info", "");
			}
		}
		return (JSON) JSON.toJSON(checkResult);
	}
	
	
	


	@RequestMapping(value = "/vehicleSeries.json", method = {RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public JSON condition(HttpServletRequest request, String name, String code,
			String status) throws LedpException {
		int pageSize = 10;
		int pageNum = 1;

		String sortName = request.getParameter("sortName");
		String sortOrder = request.getParameter("sortOrder");
		
		final String contextPath = request.getContextPath();

		Map<String, String> params = new HashMap<String, String>(); // 穿给Servlert的方法
		params.put("name", name);
		params.put("code", code);
		params.put("status", status);
		
		if (request.getParameter("pageSize") != null) {
			pageSize = Integer.parseInt(request.getParameter("pageSize"));
		}
		Integer pageStart = 10;
		if (StringUtils.isNotEmpty(request.getParameter("pageNumber"))) {
			pageNum = Integer.parseInt(request.getParameter("pageNumber"));
			if(pageNum>0){
				pageStart = (pageNum - 1) * pageSize;
			}
		}
		Dealer dealer = (Dealer)request.getSession().getAttribute("loginDealer");
		if(dealer!=null){
			params.put("dealerId",dealer.getId().toString());	
		}
		
		List<VehicleSeries> vehicleSerieses = vehicleSeriesService.insert(params, pageSize, pageStart);
		Integer total = vehicleSeriesService.getCount(params); // 总行数

		@SuppressWarnings("unchecked")
		JSON data = JSONConverter.convert(pageSize, pageNum, sortName,
				sortOrder, total, vehicleSerieses, new String[] { "name",
						"code", "status.name","onsale"},
				 new JSONConverter.Operation<VehicleSeries>() {
					public String operate(VehicleSeries t) {
						if(permissionService.hasAuth("vehicleSeries/update")){
							return "<a href='"+contextPath+"/vehicleSeries/update/" + t.getId()
									+ "' title='编辑' class='edit'><img alt='' src='../images/edit.png' /></a>";
						}
						return "";
					}
				},new JSONConverter.Operation<VehicleSeries>() {
					public String operate(VehicleSeries t) {
						if(permissionService.hasAuth("vehicleSeries/update")){
							Constant status = t.getStatus();
							if ("active".equals(status.getCode())) {
								return "<a href='#' title='禁用' d_id=" + t.getId()
										+ " status_code='active' class='ONOFF' onclick='delete_message(\""+contextPath+"/vehicleSeries/inactive/" + t.getId()
										+ "\",\"确定要禁用吗？\",false,inactiveCallback)'><img alt='' src='../images/jy.png' />";
							}
							return "<a href='#' title='启用' d_id=" + t.getId()
									+ " status_code='active' class='ONOFF' onclick='delete_message(\""+contextPath+"/vehicleSeries/active/" + t.getId()
									+ "\",\"确定要启用吗？\",false,activeCallback)'><img alt='' src='../images/jynone.png' />";
						}
						return "";
					}
				},new JSONConverter.Operation<VehicleSeries>() {
					public String operate(VehicleSeries t) {
						if(permissionService.hasAuth("vehicleSeries/onsale")){
							if ("下架".equals(t.getOnsale())) {
								return "<a href='#' title='上架' d_id=" + t.getId()
										+ " status_code='active' class='ONOFF' onclick='delete_message(\""+contextPath+"/vehicleSeries/onsale/" + t.getId()
										+ "\",\"确定要上架吗？\",false,inactiveCallback)'><img alt='' src='../images/up.png' />";
							}
							return "<a href='#' title='下架' d_id=" + t.getId()
									+ " status_code='active' class='ONOFF' onclick='delete_message(\""+contextPath+"/vehicleSeries/unsale/" + t.getId()
									+ "\",\"确定要下架吗？\",false,activeCallback)'><img alt='' src='../images/down.png' />";
						}
						return "";
					}
				});

		return data;
	}
}





