package com.citroen.ledp.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;



@Controller
@RequestMapping("/pagination")
public class PaginationController {
	
	/**
	 * 页面跳转
	 * @return
	 */
	@RequestMapping(value="/index")
	public String pageRedirect(){
		return "/paginationDemo/detail";
	}
	
	@RequestMapping(value="validateRedirect")
	public String validateRedirect(){
		return "/paginationDemo/validate";
	}

	@RequestMapping(value="/condition.json",method=RequestMethod.POST)
	@ResponseBody
	public Map condition(HttpServletRequest request){
		List lists =new ArrayList();
		for(int i=0;i<20;i++){
			if(request.getParameter("currentPage").toString().equals("2")){
				Map map =new HashMap();
				map.put("name", i);
				map.put("age", request.getParameter("age")+"2");
				map.put("address", request.getParameter("address")+"2");
				lists.add(map);
			}else{
				Map map =new HashMap();
				map.put("name",i);
				map.put("age", request.getParameter("age")+"1");
				map.put("address", request.getParameter("address")+"1");
				lists.add(map);
			}
		}
		
		Map ss= new HashMap();
		ss.put("rowList", lists);
		ss.put("total", 20);
		
		Map map =new HashMap();
		map.put("rows", ss);
		map.put("sortName",request.getParameter("sortName"));
		map.put("sortOrder", request.getParameter("sortOrder"));
		map.put("pageSize", request.getParameter("pageSize"));
		map.put("pageNumber",request.getParameter("currentPage"));
		try {
			return map;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;
	}
	
	@RequestMapping(value="submitValidate")
	@ResponseBody
	public String submitValidate(){
		return "提交表单验证成功！！";
	}
}
