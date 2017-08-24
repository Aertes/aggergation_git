package com.citroen.ledp.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.citroen.ledp.domain.Log;
import com.citroen.ledp.exception.LedpException;
import com.citroen.ledp.interceptor.Permission;
import com.citroen.ledp.service.LogService;
import com.citroen.ledp.util.JSONConverter;

/**
 * @Title: logController.java
 * @Package com.citroen.ledp.controller
 * @Description: TODO(日志管理类)
 * @author 廖启洪
 * @date 2015年1月29日 下午3:24:24
 * @version V1.0
 */
@Controller
@RequestMapping("/log")
public class LogController {
	private Map<String,Object> params;
	
	@Autowired
	LogService   logService;
	
	@RequestMapping(value={"","/index"})
	@Permission(code="log/index")
	public String index(Model model) throws Exception {
		// 返回到界面
		return "log/index";
	}
	
	/**
	 * @Title: detail
	 * @Description: TODO(查看日志方法)
	 * @param model 参数传递容器
	 * @param id 日志ID
	 * @return String
	 */
	@RequestMapping(value="/detail/{id}")
	@Permission(code="log/detail")
	public String detail(Model model,@PathVariable long id) {
		return "log/detail";
	}

	
	@RequestMapping(value = "/log.json", method = {	RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public JSON condition(HttpServletRequest request,Log log ) throws LedpException {
		int pageSize = 10;
		int pageNum = 1;

		String sortName = request.getParameter("sortName");
		String sortOrder = request.getParameter("sortOrder");
		if (request.getParameter("pageSize") != null) {
			pageSize = Integer.parseInt(request.getParameter("pageSize"));
		}
		int pageNum1=0;
		Integer pageStart=10;
		if (StringUtils.isNotEmpty(request.getParameter("pageNumber"))) {
			pageNum = Integer.parseInt(request.getParameter("pageNumber"));
			if(pageNum>0){
				pageStart = (pageNum - 1) * pageSize;
			}
		}
		String name = (String) params.get("name");
		String date1 =params.get("date2")!=null ?(String) params.get("date1"):null;
		String date2 =params.get("date2") != null?(String) params.get("date2"):null;
		List<Log> logs = logService.queryLog(log, pageSize, pageStart,name,date1,date2,sortName,sortOrder);
		Integer total = logService.getTotal(log, name,date1,date2); // 总行数

		@SuppressWarnings("unchecked")
		JSON data = JSONConverter.convert(pageSize, pageNum, sortName,sortOrder, total, logs, new String[] { 
				"type",	"operator.name", "resource","operation","datetime[yyyy-MM-dd HH:mm:ss]","result","comment" },
				new JSONConverter.Operation<Log >() {
					public String operate(Log t) {
						return "<a href='../log/detail/"
								+ t.getId()
								+ "' title='查看'><img alt='' src='../images/magnifier.png'></a>";
					}
				}, new JSONConverter.Operation<Log>() {
					public String operate(Log t) {
						return "<a href='#' title='' class='ONOFF' onclick='jy()'><img alt='' src='../images/jy.png' />";
					}
				});

		return data;
	}

}
