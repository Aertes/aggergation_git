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
import com.citroen.ledp.dao.mybaits.MybaitsGenericDao;
import com.citroen.ledp.domain.Constant;
import com.citroen.ledp.domain.Dealer;
import com.citroen.ledp.domain.User;
import com.citroen.ledp.domain.Vehicle;
import com.citroen.ledp.domain.VehicleSeries;
import com.citroen.ledp.exception.LedpException;
import com.citroen.ledp.service.ConstantService;
import com.citroen.ledp.service.PermissionService;
import com.citroen.ledp.service.VehicleService;
import com.citroen.ledp.util.JSONConverter;
import com.citroen.ledp.util.LedpLogger;
import com.citroen.ledp.util.LedpLogger.Operation;
import com.citroen.ledp.util.LedpLogger.Result;

/**
 * @Title: VehicleController.java
 * @Package com.citroen.ledp.controller
 * @Description: TODO(车辆管理类)
 * @author 廖启洪
 * @date 2015年1月29日 下午3:24:24
 * @version V1.0
 */
@Controller
@RequestMapping("/vehicle")
public class VehicleController {
	private Map<String,Object> params;

	@Autowired
	private VehicleService vehicleService;

	@Autowired
	private MybaitsGenericDao<Long> mybaitsGenericDao;
	@Autowired
	private ConstantService constantService;
	
	@Autowired
	private PermissionService permissionService;

	@RequestMapping(value = { "", "/index" })
	@com.citroen.ledp.interceptor.Permission(code={"vehicle/index"})
	public String index(Model model,String series) throws Exception {
		if(series==null){
			VehicleSeries vs = vehicleService.getVehicleSeries();
			if(vs!=null){
				series = vs.getId()+"";
			}
		}
		if(permissionService.hasAuth("vehicle/create")){
			model.addAttribute("permission","permission");
		}
		model.addAttribute("series",series);
		model.addAttribute(params);
		// 返回到界面
		return "vehicle/index";
	}

	/**
	 * @Title: create
	 * @Description: TODO(添加车辆入口)
	 * @param model
	 *            参数传递容器
	 * @throws Exception
	 * @return String
	 */
	@RequestMapping(value = "/create")
	@com.citroen.ledp.interceptor.Permission(code={"vehicle/create"})
	public String create(Model model,Vehicle vehicle) throws Exception {
		
		if(vehicle.getSeries()!=null && vehicle.getSeries().getId() > 0){
			VehicleSeries vehicleSeries = vehicleService.get(vehicle.getSeries().getId());
			vehicle.setSeries(vehicleSeries);
		}
		
		Constant status = constantService.find("record_status","active");
		vehicle.setStatus(status);
		model.addAttribute("vehicle",vehicle);
		return "vehicle/create";
	}

	/**
	 * @Title: save
	 * @Description: TODO(新增车辆方法)
	 * @param model
	 *            参数传递容器
	 * @throws Exception
	 * @return String
	 */
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	@com.citroen.ledp.interceptor.Permission(code={"vehicle/create"})
	public String save(Model model, @Valid Vehicle vehicle, BindingResult br,HttpServletRequest request) throws Exception {
		HttpSession session = request.getSession();
		Long vsId = vehicle.getSeries().getId();
		VehicleSeries  vs =  mybaitsGenericDao.get(VehicleSeries.class, vsId);
	    vehicle.setSeries(vs);
		vehicle.setName(vehicle.getName().trim()); //去除空格
		
		Vehicle vehN = vehicleService.findN(vehicle.getName(),vehicle.getSeries().getId()); //去除名称重复
		
		Vehicle vehC = vehicleService.findC(vehicle.getCode(),vehicle.getSeries().getId()); //去除代码重复
		
		
		if(vehN !=null && vehicle.getSeries().getId() == vehN.getSeries().getId() ){  
			br.rejectValue("name","vehicle.name.already.exists","名称已经存在，请重新输入！");
			model.addAttribute("vehicle",vehicle);
			LedpLogger.info((User) session.getAttribute("loginUser"), "增加车型", Operation.create,Result.failure,"名称已经存在");
			return "vehicle/create";
		}
		if(vehC !=null && vehicle.getSeries().getId() == vehC.getSeries().getId() ){  
			br.rejectValue("code","vehicle.code.already.exists","代码已经存在，请重新输入！");
			model.addAttribute("vehicle",vehicle);
			LedpLogger.info((User) session.getAttribute("loginUser"), "增加车型", Operation.create,Result.failure,"代码已经存在");
			return "vehicle/create";
		}
		if (br.hasErrors()) {
			model.addAttribute("vehicle", vehicle);
			LedpLogger.info((User) session.getAttribute("loginUser"), "增加车型", Operation.create,Result.failure);
			return "vehicle/create";
		}

		Long id = mybaitsGenericDao.save(vehicle);
		if (id > 0) {
			model.addAttribute("org","车型添加成功");
			LedpLogger.info((User) session.getAttribute("loginUser"), "增加车型", Operation.create,Result.success);
			return "redirect:/vehicle/index?series="+vehicle.getSeries().getId();
		} else {
			LedpLogger.info((User) session.getAttribute("loginUser"), "增加车型", Operation.create,Result.failure);
			return "vehicle/create";
		}

	}

	/**
	 * @Title: update
	 * @Description: TODO(修改车辆入口)
	 * @param model
	 *            参数传递容器
	 * @param id
	 *            车辆ID
	 * @return String
	 * @throws LedpException
	 */
	@RequestMapping(value = "/update/{id}")
	@com.citroen.ledp.interceptor.Permission(code={"vehicle/update"})
	public String update(Model model, @PathVariable long id)
			throws LedpException {
		// 获取当前用户对象
		Vehicle vehicles = mybaitsGenericDao.get(Vehicle.class, id);
		model.addAttribute("vehicle", vehicles);
		return "vehicle/update";
	}

	/**
	 * @Title: edit
	 * @Description: TODO(修改车辆方法)
	 * @param model
	 *            参数传递容器
	 * @return String
	 * @throws LedpException
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	@com.citroen.ledp.interceptor.Permission(code={"vehicle/update"})
	public String edit(Model model,	@Valid @ModelAttribute("vehicle") Vehicle vehicle,BindingResult br,HttpServletRequest request) throws LedpException {
		HttpSession session = request.getSession();
		Long vsId = vehicle.getSeries().getId();
		VehicleSeries  vs =  mybaitsGenericDao.get(VehicleSeries.class, vsId);
	    vehicle.setSeries(vs);
		
		Vehicle vehN = vehicleService.findN(vehicle.getName(),vehicle.getSeries().getId()); //去除名称重复
		
		Vehicle vehC = vehicleService.findC(vehicle.getCode(),vehicle.getSeries().getId()); //去除代码重复
		
		if(vehN != null && vehN.getId() == vehicle.getId() && vehicle.getSeries().getId() != vehN.getSeries().getId() ){  
			br.rejectValue("name","vehicle.name.already.exists","名称已经存在，请重新输入！");
			model.addAttribute("vehicle",vehicle);
			LedpLogger.info((User) session.getAttribute("loginUser"), "修改车型", Operation.update,Result.failure,"名称已经存在");
			return "vehicle/update";
		}
		if(vehC != null && vehC.getId() == vehicle.getId() && vehicle.getSeries().getId() != vehC.getSeries().getId() ){  
			br.rejectValue("code","vehicle.code.already.exists","代码已经存在，请重新输入！");
			model.addAttribute("vehicle",vehicle);
			LedpLogger.info((User) session.getAttribute("loginUser"), "修改车型", Operation.update,Result.failure,"代码已经存在");
			return "vehicle/update";
		}
		
		//判断是否有错误
		if (br.hasErrors()) {
			model.addAttribute("vehicle", vehicle);
			LedpLogger.info((User) session.getAttribute("loginUser"), "修改车型", Operation.update,Result.failure);
			return "vehicle/update";
		}
	
		mybaitsGenericDao.update(vehicle);
		LedpLogger.info((User) session.getAttribute("loginUser"), "修改车型", Operation.update,Result.success);
		return "redirect:/vehicle/index?series="+vehicle.getSeries().getId();
	}
	
	@RequestMapping(value = "/editPrice/{id}")
	@com.citroen.ledp.interceptor.Permission(code={"vehicle/update"})
	public String editPrice(Model model, @PathVariable long id,HttpSession session)
			throws LedpException {
		// 获取当前用户对象
		Dealer dealer =  (Dealer)session.getAttribute("loginDealer");
		if(dealer==null){
			return "redirect:/vehicle/index?series=1";
		}
		Vehicle vehicle = vehicleService.getVehicle(dealer.getId(), id);
		model.addAttribute("vehicle", vehicle);
		return "vehicle/update_price";
	}
	
	@RequestMapping(value = "/updatePrice", method = RequestMethod.POST)
	@com.citroen.ledp.interceptor.Permission(code={"vehicle/update"})
	public String updatePrice(Model model,HttpServletRequest request) throws LedpException {
		try{
		String vehicleId = request.getParameter("id");
		String price = request.getParameter("price");
		String onsale = request.getParameter("onsale");
		HttpSession session = request.getSession();
		Dealer dealer =  (Dealer)session.getAttribute("loginDealer");
		Vehicle vehicle = null;
		if(dealer!=null){
			try{
				vehicle = vehicleService.getVehicle(Long.parseLong(vehicleId));
			}catch(Exception e){
				return "redirect:/vehicle/index?series=1";
			}
			vehicleService.updatePricer(dealer,vehicle.getId(),onsale,price);
		}
		
		if(vehicle==null){
			return "redirect:/vehicle/index?series=1";
		}
		return "redirect:/vehicle/index?series="+vehicle.getSeries().getId();
		}catch(Exception e){
			e.printStackTrace();
		}
		return "";
	}
	/**
	 * @Title: detail
	 * @Description: TODO(查看车辆方法)
	 * @param model
	 *            参数传递容器
	 * @param id
	 *            车辆ID
	 * @return String
	 */
	@RequestMapping(value = "/detail/{id}")
	@com.citroen.ledp.interceptor.Permission(code={"vehicle/detail"})
	public String detail(Model model, @PathVariable long id,HttpSession session) {
		Long dealerId = null;
		Dealer dealer =  (Dealer)session.getAttribute("loginDealer");
		if(dealer!=null){
			dealerId = dealer.getId();
		}
		Vehicle vehicle = vehicleService.getVehicle(dealerId,id);
		if (vehicle != null) {
			model.addAttribute("vehicle", vehicle);
			return "vehicle/detail";
		}
		return "redirect:/vehicle/index";

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
	@com.citroen.ledp.interceptor.Permission(code={"vehicle/create"})
	public JSON delete(@PathVariable long id) throws LedpException {
		
		Map<String,String> data = new HashMap<String,String>();
		try {
			vehicleService.deleteVS(id);
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
	@com.citroen.ledp.interceptor.Permission(code={"vehicle/update"})
	public JSON active(Model model,@PathVariable long id) throws LedpException {
		Vehicle vehicle = vehicleService.getVehicle(id);
		if(vehicle ==null){
			throw new LedpException();
		}
		Constant status = constantService.find("record_status","active");
		vehicle.setStatus(status);
		mybaitsGenericDao.update(vehicle);
		Map json = new HashMap<String,String>();
		json.put("code","success_message");
		json.put("message","车型"+status.getName()+"成功 ！");
		
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
	@com.citroen.ledp.interceptor.Permission(code={"vehicle/update"})
	public JSON inactive(Model model,@PathVariable long id) throws LedpException {
		Vehicle vehicle = vehicleService.getVehicle(id);
		if(vehicle==null){
			throw new LedpException();
		}
		Constant status = constantService.find("record_status","inactive");
		vehicle.setStatus(status);
		mybaitsGenericDao.update(vehicle);
		Map json = new HashMap<String,String>();
		json.put("code","success_message");
		json.put("message","车型"+status.getName()+"成功 ！");
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
	@com.citroen.ledp.interceptor.Permission(code={"vehicle/onsale"})
	public JSON onsale(Model model,@PathVariable long id,HttpSession session) throws LedpException {
		Dealer dealer = (Dealer)session.getAttribute("loginDealer");
		if(dealer==null){
			Map json = new HashMap<String,String>();
			json.put("code","success_message");
			json.put("message","非网点用户，不能上架车型 ！");
			return (JSON)JSON.toJSON(json);
		}
		try{
			vehicleService.onsale(dealer,id);
			Map json = new HashMap<String,String>();
			json.put("code","success_message");
			json.put("message","车型上架成功 ！");
			return (JSON)JSON.toJSON(json);
		}catch(Exception e){
			Map json = new HashMap<String,String>();
			json.put("code","success_message");
			json.put("message","车型上架失败 ！");
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
	@com.citroen.ledp.interceptor.Permission(code={"vehicle/onsale"})
	public JSON unsale(Model model,@PathVariable long id,HttpSession session) throws LedpException {
		Dealer dealer = (Dealer)session.getAttribute("loginDealer");
		if(dealer==null){
			Map json = new HashMap<String,String>();
			json.put("code","success_message");
			json.put("message","非网点用户，不能下架车型 ！");
			return (JSON)JSON.toJSON(json);
		}
		try{
			vehicleService.unsale(dealer,id);
			Map json = new HashMap<String,String>();
			json.put("code","success_message");
			json.put("message","车型下架成功 ！");
			return (JSON)JSON.toJSON(json);
		}catch(Exception e){
			Map json = new HashMap<String,String>();
			json.put("code","success_message");
			json.put("message","车型下架失败 ！");
			return (JSON)JSON.toJSON(json);
		}
	}
	
	/**
	 * @Title: onOff
	 * @Description: onOff(启用，或者禁用)
	 * @param model
	 *            参数传递容器
	 * @param id 车系ID
	 */
	@RequestMapping(value = "/onOff/{id}", method = {RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public JSON onOff(@PathVariable long id) {
		Map<String, String> data = new HashMap<String, String>();
		try {
			vehicleService.ONorOFF(id);
			data.put("code", "200");
			data.put("message", "操作成功！");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return (JSON) JSON.toJSON(data);
	}
	

	@RequestMapping(value = "/vehicle.json", method = { RequestMethod.GET,RequestMethod.POST })
	@ResponseBody
	public JSON condition(HttpServletRequest request, String name, String code,
			String status, String seriesId) throws LedpException {
		int pageSize = 10;
		int pageNum = 1;
		String sortName = request.getParameter("sortName");
		String sortOrder = request.getParameter("sortOrder");
		Map<String, String> params = new HashMap<String, String>(); // 穿给Servlert的方法
		params.put("name", name);
		params.put("code", code);
		params.put("status", status);
		params.put("series", seriesId);
		final Dealer dealer = (Dealer)request.getSession().getAttribute("loginDealer");
		if(dealer!=null){
			params.put("dealerId",dealer.getId().toString());	
		}
		
		final String contextPath = request.getContextPath();


		if (request.getParameter("pageSize") != null) {
			pageSize = Integer.parseInt(request.getParameter("pageSize"));
		}
		if (StringUtils.isNotEmpty(request.getParameter("pageNumber"))) {
			pageNum = Integer.parseInt(request.getParameter("pageNumber"));
		}

		List<Vehicle> vehicles = vehicleService.insert(params, pageSize,pageNum);
		Integer total = vehicleService.getCount(params); // 总行数

		@SuppressWarnings("unchecked")
		JSON data = JSONConverter.convert(pageSize, pageNum, sortName,
				sortOrder, total, vehicles, new String[] { "series.name","name", "code", "status.name","onsale" },
				 new JSONConverter.Operation<Vehicle>() {
					public String operate(Vehicle t) {
						if(permissionService.hasAuth("vehicle/update")){
							if(dealer!=null){
								return "<a href='"+contextPath+"/vehicle/editPrice/" + t.getId()
										+ "' title='编辑' class='edit'><img alt='' src='../images/edit.png' /></a>";
							}
							return "<a href='"+contextPath+"/vehicle/update/" + t.getId()
									+ "' title='编辑' class='edit'><img alt='' src='../images/edit.png' /></a>";
						}
						return "";
					}
				}, new JSONConverter.Operation<Vehicle>() {
					public String operate(Vehicle t) {
							if(permissionService.hasAuth("vehicle/update") && dealer==null){
								Constant status = t.getStatus();
								if ("active".equals(status.getCode())) {
									return "<a href='#' title='禁用' d_id=" + t.getId()
											+ " status_code='active' class='ONOFF' onclick='delete_message(\""+contextPath+"/vehicle/inactive/" + t.getId()
											+ "\",\"确定要禁用吗？\",false,inactiveCallback)'><img alt='' src='../images/jy.png' />";
								}
								return "<a href='#' title='启用' d_id=" + t.getId()
										+ " status_code='active' class='ONOFF' onclick='delete_message(\""+contextPath+"/vehicle/active/" + t.getId()
										+ "\",\"确定要启用吗？\",false,activeCallback)'><img alt='' src='../images/jynone.png' />";
							}
							return "";
						}
				}, new JSONConverter.Operation<Vehicle>() {
					public String operate(Vehicle t) {
						if(permissionService.hasAuth("vehicle/onsale")){
							if ("下架".equals(t.getOnsale())) {
								return "<a href='#' title='上架' d_id=" + t.getId()
										+ " status_code='active' class='ONOFF' onclick='delete_message(\""+contextPath+"/vehicle/onsale/" + t.getId()
										+ "\",\"确定要上架吗？\",false,inactiveCallback)'><img alt='' src='../images/up.png' />";
							}
							return "<a href='#' title='下架' d_id=" + t.getId()
									+ " status_code='active' class='ONOFF' onclick='delete_message(\""+contextPath+"/vehicle/unsale/" + t.getId()
									+ "\",\"确定要下架吗？\",false,activeCallback)'><img alt='' src='../images/down.png' />";
						}
						return "";
					}
				},
				new JSONConverter.Operation<Vehicle>() {
					public String operate(Vehicle t) {
						if(permissionService.hasAuth("vehicle/detail")){
							return "<a href='"+contextPath+"/vehicle/detail/" + t.getId()
									+ "' title='查看'><img alt='' src='"+contextPath+"/images/magnifier.png'></a>";
						}
						return "";
					}
				});
		return data;
	}
	
	@RequestMapping(value = "/checkCode", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public JSON checkCode(HttpServletRequest request, String param) throws LedpException {

		String dealerId = request.getParameter("id");
		Long id = StringUtils.isNotBlank(dealerId) ? Long.parseLong(dealerId) : null;
		String seriesId=request.getParameter("seriesId");
		Map<String, String> checkResult = new HashMap<String, String>();
		checkResult.put("status", "n");
		checkResult.put("info", "车型代码已存在");

		List<Vehicle> vehicles = vehicleService.checkCode(param, id, Long.parseLong(seriesId));
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
		String seriesId=request.getParameter("seriesId");
		Map<String, String> checkResult = new HashMap<String, String>();
		checkResult.put("status", "n");
		checkResult.put("info", "车型名称已存在");

		List<Vehicle> vehicles = vehicleService.checkName(param, id,Long.parseLong(seriesId));
		if (StringUtils.isNotBlank(param)) {
			if (vehicles.isEmpty()) {
				checkResult.put("status", "y");
				checkResult.put("info", "");
			}
		}
		return (JSON) JSON.toJSON(checkResult);
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
	public JSON tree(Model model, HttpServletRequest reqquest)
			throws LedpException {
		String parentId = reqquest.getParameter("id");
		Long parent = null;
		try {
			parent = Long.parseLong(parentId);
		} catch (Exception e) {
			VehicleSeries vehicleSeries = (VehicleSeries) reqquest.getSession()
					.getAttribute("vehicleSeries");
			if (vehicleSeries != null) {
				parent = vehicleSeries.getId();
			}
		}
		List<VehicleSeries> children = vehicleService.getChildren(parent);

		List<Map<String, Object>> nodes = new ArrayList<Map<String, Object>>();

		
		for(int i=0;i<children.size();i++){
			Map<String, Object> node = new HashMap<String, Object>();
			if(i==0){
				node.put("checked", true);
			}
			node.put("id", children.get(i).getId());
			node.put("text", children.get(i).getName());
			nodes.add(node);
			
		}
		return (JSON) JSON.toJSON(nodes);
	}

}
