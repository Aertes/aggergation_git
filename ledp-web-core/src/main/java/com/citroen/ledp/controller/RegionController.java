package com.citroen.ledp.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.citroen.ledp.domain.Region;
import com.citroen.ledp.service.RegionService;

/**
 * 地区管理类
 * @author      miaoshuai
 * @email       miaoshuai@tocersfot.com
 * @company		www.tocersoft.com
 * @create-time 2015年3月14日 下午4:55:57
 * @version     1.0
 */
@Controller
@RequestMapping("/region")
public class RegionController {
	
	private Logger logger = Logger.getLogger(this.getClass());
	
	private Map<String,Object> params;
	
	@Autowired
	private RegionService regionService;

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
	public JSON search(Model model,HttpServletRequest request) throws Exception {
		// 查询地区
		List<Region> regionList = regionService.executeQuery(params);
		// 根节点
		Map<String, Object> root = new HashMap<String, Object>();
		JSONArray jsonArray = new JSONArray();
		if(!CollectionUtils.isEmpty(regionList)){
			for (Region region : regionList) {
				Map<String, Object> item = new HashMap<String, Object>();
				item.put("id", region.getId());
				item.put("zh_name", region.getZh_name());
				
				jsonArray.add(JSON.toJSON(item));
			}
		}
		root.put("resultList", jsonArray);
		return (JSON) JSON.toJSON(root);
	}
}
