package com.citroen.ledp.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.citroen.ledp.dao.mybaits.MybaitsGenericDao;
import com.citroen.ledp.domain.Constant;
import com.citroen.ledp.domain.Dealer;
import com.citroen.ledp.domain.Media;
import com.citroen.ledp.domain.Vehicle;
import com.citroen.ledp.domain.VehicleSeries;
import com.citroen.ledp.exception.LedpException;
import com.citroen.ledp.interceptor.Permission;
import com.citroen.ledp.service.ConstantService;
import com.citroen.ledp.service.MediaService;
import com.citroen.ledp.service.PermissionService;
import com.citroen.ledp.util.JSONConverter;

/**
 * @Title: mediaController.java
 * @Package com.citroen.ledp.controller
 * @Description: TODO(媒体管理类)
 * @author 廖启洪
 * @date 2015年1月29日 下午3:24:24
 * @version V1.0
 */
@Controller
@RequestMapping("/media")
public class MediaController {

	@Autowired
	private MybaitsGenericDao<Long> genericDao;

	@Autowired
	public MediaService mediaService;
	@Autowired
	private ConstantService constantService;
	
	@Autowired
	private PermissionService permissionService;

	@RequestMapping(value = { "", "/index" })
	@Permission(code="media/index")
	public String index() throws Exception {
		// 返回到界面
		return "media/index";
	}

	/**
	 * @Title: create
	 * @Description: TODO(添加媒体入口)
	 * @param model
	 *            参数传递容器
	 * @throws Exception
	 * @return String
	 */
	@RequestMapping(value = "/create")
	@Permission(code="media/delete")
	public String create(Model model) throws Exception {
		return "media/create";
	}

	/**
	 * @Title: save
	 * @Description: TODO(新增媒体方法)
	 * @param model
	 *            参数传递容器
	 * @throws Exception
	 * @return String
	 */
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	@Permission(code="media/create")
	public String save(Model model, @ModelAttribute("media") Media media)
			throws Exception {
		return "media/index";
	}

	/**
	 * @Title: update
	 * @Description: TODO(修改媒体入口)
	 * @param model
	 *            参数传递容器
	 * @param id
	 *            媒体ID
	 * @return String
	 * @throws LedpException
	 */
	@RequestMapping(value = "/update/{id}")
	@Permission(code="media/update")
	public String update(Model model, @PathVariable long id)
			throws LedpException {
		// 获取当前用户对象
		Media media = genericDao.get(Media.class, id);
		model.addAttribute("media", media);
		return "media/update";
	}

	/**
	 * @Title: edit
	 * @Description: TODO(修改媒体方法)
	 * @param model
	 *            参数传递容器
	 * @return String
	 * @throws LedpException
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	@Permission(code="media/update")
	public String edit(Model model, @ModelAttribute("media") Media media,
			String start) throws LedpException {

		String query = "select * from  t_constant where code = '" + start + "'";
		Constant constant = genericDao.find(Constant.class, query);
		media.setStatus(constant);
		genericDao.update(media);
		return "redirect:/media/index";

	}

	/**
	 * @Title: detail
	 * @Description: TODO(查看媒体方法)
	 * @param model
	 *            参数传递容器
	 * @param id
	 *            媒体ID
	 * @return String
	 */
	@RequestMapping(value = "/detail/{id}")
	@Permission(code="media/detail")
	public String detail(Model model, @PathVariable long id) {
		Media media = mediaService.getMedia(id);
		if (media != null) {
			model.addAttribute("media", media);
			return "media/detail";
		}
		return "media/index";
	}

	/**
	 * @Title: onOff
	 * @Description: onOff(启用，或者禁用)
	 * @param model
	 *            参数传递容器
	 * @param id
	 *            车系ID
	 */
	@RequestMapping(value = "/onOff/{id}", method = { RequestMethod.GET,
			RequestMethod.POST })
	@ResponseBody
	public JSON onOff(@PathVariable long id) {
		Map<String, String> data = new HashMap<String, String>();
		try {
			mediaService.ONorOFF(id);
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
	@Permission(code="media/update")
	public JSON active(Model model,@PathVariable long id) throws LedpException {
		Media media = mediaService.getMedia(id);
		if(media==null){
			throw new LedpException();
		}
		Constant status = constantService.find("record_status","active");
		media.setStatus(status);
		genericDao.update(media);
		Map json = new HashMap<String,String>();
		json.put("code","success_message");
		json.put("message","媒体"+status.getName()+"成功 ！");
		
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
	@Permission(code="media/update")
	public JSON inactive(Model model,@PathVariable long id) throws LedpException {
		Media media = mediaService.getMedia(id);
		if(media==null){
			throw new LedpException();
		}
		Constant status = constantService.find("record_status","inactive");
		media.setStatus(status);
		genericDao.update(media);
		Map json = new HashMap<String,String>();
		json.put("code","success_message");
		json.put("message","媒体"+status.getName()+"成功 ！");
		
		return (JSON)JSON.toJSON(json);
	}

	@RequestMapping(value = "/media.json", method = { RequestMethod.GET,
			RequestMethod.POST })
	@ResponseBody
	public JSON condition(HttpServletRequest request, String name, String code,
			String status) throws LedpException {
		int pageSize = 0;
		int pageNum = 0;

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
		if (StringUtils.isNotEmpty(request.getParameter("pageNumber"))) {
			pageNum = Integer.parseInt(request.getParameter("pageNumber"));
			if (pageNum > 0) {
				pageNum = (pageNum - 1) * pageSize;
			}
		}
		List<Media> medias = mediaService.insert(params, pageSize, pageNum);
		Integer total = mediaService.getCount(params); // 总行数

		@SuppressWarnings("unchecked")
		JSON data = JSONConverter.convert(pageSize, pageNum, sortName,
				sortOrder, total, medias, new String[] { "name", "code",
						"status.name" }, new JSONConverter.Operation<Media>() {
					public String operate(Media t) {
						if(permissionService.hasAuth("media/detail")){
							return "<a class='ielookDetails' href='"+contextPath+"/media/detail/" + t.getId()
									+ "' title='查看'><img alt='' src='../images/magnifier.png'></a>";
						}
						return "";
					}
				}, new JSONConverter.Operation<Media>() {
					public String operate(Media t) {
						if(permissionService.hasAuth("media/update")){
							Constant status = t.getStatus();
							if ("active".equals(status.getCode())) {
								return "<a href='#' title='禁用' d_id=" + t.getId()
										+ " status_code='active' class='ONOFF' onclick='delete_message(\""+contextPath+"/media/inactive/" + t.getId()
										+ "\",\"确定要禁用吗？\",false,inactiveCallback)'><img alt='' src='../images/jy.png' />";
							}
							return "<a href='#' title='启用' d_id=" + t.getId()
									+ " status_code='active' class='ONOFF' onclick='delete_message(\""+contextPath+"/media/active/" + t.getId()
									+ "\",\"确定要启用吗？\",false,activeCallback)'><img alt='' src='../images/jynone.png' />";
						}
						return "";
					}
				});
		return data;
	}

}
