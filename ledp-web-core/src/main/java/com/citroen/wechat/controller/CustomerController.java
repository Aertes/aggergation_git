package com.citroen.wechat.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.jboss.logging.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.citroen.ledp.domain.Customer;
import com.citroen.ledp.domain.User;
import com.citroen.ledp.exception.LedpException;
import com.citroen.ledp.interceptor.Permission;
import com.citroen.ledp.service.CustomerService;
import com.citroen.ledp.service.PermissionService;
import com.citroen.ledp.service.RegionService;
import com.citroen.ledp.util.ExcelUtil;
import com.citroen.ledp.util.JSONConverter;
import com.citroen.ledp.util.LedpLogger;
import com.citroen.ledp.util.LedpLogger.Operation;
import com.citroen.ledp.util.LedpLogger.Result;
import com.citroen.ledp.util.SysConstant;
import com.citroen.wechat.util.Pagination;

/**
 * @Title: CustomerController.java
 * @Package com.citroen.ledp.controller
 * @Description: TODO(客户管理类)
 * @author 廖启洪
 * @date 2015年1月29日 下午3:24:24
 * @version V1.0
 */
@Controller("wechatCustomerController")
@RequestMapping("/wechat/customer")
public class CustomerController {
	
	private Logger logger = Logger.getLogger(this.getClass());
	
	private Map<String,Object> params;
	
	@Autowired
	private CustomerService customerService;
	@Autowired
	private RegionService regionService;
	@Autowired
	private PermissionService permissionService;
	
	@RequestMapping(value={"","/index"})
	public String index(Model model) throws Exception {
		// 返回到界面
		return "wechat/customer/index";
	}

	/**
	 * 查询
	 * @param model
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value={"search"})
	@ResponseBody
	public Map<String,Object> search(Model model,HttpServletRequest request) throws Exception {
		
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
		params.put("sortName", sortName);
		params.put("sortOrder", sortOrder);
		try{
			pageSize = Integer.parseInt(request.getParameter("pageSize"));
		}catch(Exception e){}
		try{
			pageNumber = Integer.parseInt(request.getParameter("currentPage"));
		}catch(Exception e){}
		params.put("pageNumber", pageNumber);
		List<Customer> rows = customerService.executeQuery(params);
		
		int total = customerService.getTotalRow(params);
		Pagination pagination = new Pagination(total, pageSize, pageNumber);
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("paginationData", pagination);
        result.put("data", rows);
        return result;
	}
	
	/**
	 * @Title: update
	 * @Description: TODO(修改客户入口)
	 * @param model 参数传递容器
	 * @param id 客户ID
	 * @return String
	 * @throws LedpException 
	 */
	@RequestMapping(value="/update/{id}")
	@Permission(code="customer/update")
	public String update(Model model,@PathVariable long id) throws LedpException {
		if(!permissionService.hasAuth(SysConstant.PERMISSION_CUSTOMER_UPDATE)){
			return "redirect:/wechat/customer/index";
		}
		// 根据客户ID查询
		Customer customer = customerService.get(id);
		model.addAttribute("customer", customer);
		return "wechat/customer/update";
	}
	
	/**
	 * 编辑客户资料
	 * @param model
	 * @param dealer
	 * @param dealerId
	 * @param mediaId
	 * @return
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	@Permission(code="customer/update")
	public String edit(Model model,HttpServletRequest request, Customer customer) {
		HttpSession session = request.getSession();
		try {
			Customer oldCustomer = customerService.get(customer.getId());
			BeanUtils.copyProperties(customer, oldCustomer,new String[]{"intent","follow","phone"});
			customerService.update(oldCustomer);
			//写入日志
			LedpLogger.info((User) session.getAttribute("loginUser"), "修改客户", Operation.update,Result.success);
			return "redirect:/wechat/customer/index";
		} catch (LedpException e) {
			e.printStackTrace();
			String message = "编辑客户资料时发生异常："+e.getMessage();
			logger.error(message,e);
			LedpLogger.info((User) session.getAttribute("loginUser"), "修改客户", Operation.update,Result.failure);
			LedpLogger.error((User) session.getAttribute("loginUser"), "修改客户", Operation.update,Result.failure,"编辑客户资料时发生异常");
			return "redirect:/wechat/customer/update/" + customer.getId();
		}
	}
	
	/**
	 * @Title: detail
	 * @Description: TODO(查看客户方法)
	 * @param model 参数传递容器
	 * @param id 客户ID
	 * @return String
	 * @throws LedpException 
	 */
	@RequestMapping(value="/detail/{id}")
	@Permission(code="customer/detail")
	public String detail(Model model,@PathVariable long id) throws LedpException {
		Customer customer = customerService.get(id);
		model.addAttribute("customer", customer);
		return "wechat/customer/detail";
	}
	
	/**
	 * 导出
	 * @return
	 * @throws LedpException 
	 */
	@RequestMapping(value="/doExport")
	@Permission(code="customer/index")
	public void doExport(HttpServletResponse response,HttpServletRequest request) throws LedpException{
		List<Customer> list = customerService.exportQuery(params);
		SXSSFWorkbook wb = customerService.createWorkbook(list);
		ExcelUtil.exportExcelData("客户导出记录",response,request, wb);
	}
}
