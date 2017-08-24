package com.citroen.wechat.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.citroen.ledp.dao.mybaits.MybaitsGenericDao;
import com.citroen.ledp.exception.LedpException;
import com.citroen.ledp.service.PermissionService;
import com.citroen.ledp.util.DateUtil;
import com.citroen.ledp.util.PropertiyUtil;
import com.citroen.wechat.api.service.ApiFans;
import com.citroen.wechat.api.service.ApiGroup;
import com.citroen.wechat.api.service.Response;
import com.citroen.wechat.api.token.TokenHolder;
import com.citroen.wechat.domain.Fans;
import com.citroen.wechat.domain.FansGroup;
import com.citroen.wechat.domain.PublicNo;
import com.citroen.wechat.service.FansGroupService;
import com.citroen.wechat.service.FansService;
import com.citroen.wechat.util.ConstantUtil;
import com.citroen.wechat.util.Pagination;

/** 
 * @ClassName: FansController 
 * @Description: TODO(的大卫杜夫) 
 * @author 杨少波
 * @date 2015年6月28日 下午3:19:25 
 * 
 */
@Controller("fansController")
@RequestMapping("/wechat/fans")
public class FansController {
	
	private Map<String, Object> params;
	@Autowired
	private PermissionService permissionService;
	@Autowired
	private FansGroupService fansGroupService;
	@Autowired
	private FansService fansService;
	@Autowired
	private MybaitsGenericDao<Long> genericDao;
	
	@RequestMapping(value={"","index"})
	public String index(Model model,HttpServletRequest request,HttpServletResponse response,HttpSession session) throws Exception {
		// 公众号获取
		PublicNo publicNo = (PublicNo) request.getSession().getAttribute(ConstantUtil.SESSION_CURRENT_PUBLICNO);
		if(publicNo==null){
			return "redirect:/wechat/publicno/message";
		}
		if("-1".equals(publicNo.getVerify_type_info())){
			return "redirect:/wechat/publicno/unauth/"+publicNo.getVerify_type_info();
		}
		if(request.getParameter("time")!=null){
			model.addAttribute("startTime", DateUtil.format(new Date(), "yyyy-MM-dd")+" 00:00:00");
			
			Date date=DateUtil.getAfterDay(new Date());
			String strDate=DateUtil.format(date, "yyyy-MM-dd");
			Calendar c = Calendar.getInstance(Locale.CHINA);
			c.setTime(DateUtil.parse(strDate, "yyyy-MM-dd"));
			Date endDate=c.getTime();
			strDate=DateUtil.format(endDate,"yyyy-MM-dd 00:00:00");
			
			model.addAttribute("endTime",strDate);
		}
		model.addAttribute("fansGroups", fansGroupService.getFansGroups(publicNo.getId()));
		if(!"".equals(request.getParameter("fansgroupId"))){
			model.addAttribute("fansgroupId", request.getParameter("fansgroupId"));
		}
		// 返回到界面
		return "wechat/fans/index";
	}
	@RequestMapping(value = { "search" })
	@ResponseBody
	public JSON search(Model model, HttpServletRequest request) throws Exception {
		
		final String contextPath = request.getContextPath();
		PropertiyUtil propUtil = new PropertiyUtil("/wechat.properties");
		String imgDir = propUtil.getString("campaign.images.url");
		int pageSize = 10;
		int pageNumber = 1;
		String sortName = request.getParameter("sortName");
		String sortOrder = request.getParameter("sortOrder");
		if (StringUtils.isBlank(sortName)) {
			sortName = "subscribe_time";
		}
		if (StringUtils.isBlank(sortOrder)) {
			sortOrder = "desc";
		}
		try {
			pageSize = Integer.parseInt(request.getParameter("pageSize"));
		} catch (Exception e) {
		}
		try {
			pageNumber = Integer.parseInt(request.getParameter("currentPage"));
		} catch (Exception e) {
		}
		List<Fans> rows = new ArrayList<Fans>();
		int total = 0;
		List list =new ArrayList();
		HttpSession session = request.getSession();
		if(session.getAttribute(ConstantUtil.SESSION_CURRENT_PUBLICNO)!=null){
			PublicNo publicNo=(PublicNo)session.getAttribute(ConstantUtil.SESSION_CURRENT_PUBLICNO);
			params.put("publicNo", publicNo.getId().toString());
			params.put("pageSize", pageSize);
			params.put("pageNumber", pageNumber);
			params.put("sortName", sortName);
			params.put("sortOrder", sortOrder);
			rows = fansService.executeQuery(params);
			total = fansService.getTotalRow(params);
			/*List<FansGroup> list1=new ArrayList<FansGroup>();
			list1=fansGroupService.getFansGroups(publicNo.getId());
			List list2=new ArrayList();
			for(int j=0;j<list1.size();j++){
				Map map3=new HashMap();
				map3.put("id",list1.get(j).getId());
				map3.put("name",list1.get(j).getName());
				list2.add(map3);
			}*/
			for(int i=0;i<rows.size();i++){
				Fans fans=rows.get(i);
				Map map=new HashMap();
				map.put("id", fans.getId());
				map.put("name",fans.getNickName());
				map.put("value", fans.getNickName());
				map.put("img", fans.getHeadImgUrl());
				map.put("fansgroupId", fans.getFansGroup().getId());
				map.put("fansgroupName", fans.getFansGroup().getName());
				map.put("address", fans.getCountry()+" "+fans.getProvince()+" "+fans.getCity());
				map.put("signature", fans.getRemark());
				map.put("groups", fansGroupService.getFansGroups(publicNo.getId()));
				map.put("subscribe", DateUtil.format(fans.getSubscribeTime(),"yyyy-MM-dd HH:mm:ss"));
				list.add(map);
			}
		}
		Pagination page=new Pagination(total,pageSize,pageNumber);
		/*Map map2=new HashMap();
		map2.put("currentPage", page.getCurrentPage());
		map2.put("pageNumber", page.getPageNumber());
		map2.put("pages", page.getPages());*/
		Map map1=new HashMap();
		map1.put("paginationData", page);
		map1.put("data", list);
		return (JSON) JSON.toJSON(map1);
	}
	@RequestMapping(value="/updateRemark/{id}")
	public String update(Model model,@PathVariable long id,HttpServletRequest request) throws LedpException {
		Fans fans=fansService.fansInstance(id);
		model.addAttribute("fans", fans);
		return "wechat/fans/updateRemark";
	}
	@RequestMapping(value = { "editRemark" })
	@ResponseBody
	public JSON editRemark(Model model, HttpServletRequest request) throws Exception {
		
		Map map=new HashMap();
		HttpSession session = request.getSession();
		// 公众号获取
		PublicNo publicNo = (PublicNo) request.getSession().getAttribute(ConstantUtil.SESSION_CURRENT_PUBLICNO);
		if(publicNo==null){
			map.put("code", "400");
			map.put("value", "在使用公众号相关信息前，请先设置当前公众号！");
			return (JSON) JSON.toJSON(map);
		}
		
		String authorizer_access_token=TokenHolder.getAuthorizerAccessToken(publicNo.getAppid(), publicNo.getAuthorizer_refresh_token());
		
		Fans fans=fansService.fansInstance(Long.valueOf(request.getParameter("id")));
		
		com.citroen.wechat.api.model.Fans fan1=new com.citroen.wechat.api.model.Fans();
		fan1.setOpenid(fans.getOpenId());
		fan1.setRemark(request.getParameter("remark"));
		
		Response response =ApiFans.update(authorizer_access_token, fan1);
		if(response.getStatus()==Response.SUCCESS){
			fans.setRemark(request.getParameter("remark"));
			genericDao.update(fans);
			map.put("code", "200");
			map.put("value", "修改成功");
		}else{
			map.put("code", "400");
			map.put("value", response.getMessage());
		}
		
		return (JSON) JSON.toJSON(map);
	}
	@RequestMapping(value = { "updateGroup" })
	@ResponseBody
	public JSON updateGroup(Model model, HttpServletRequest request) throws Exception {
		Map map=new HashMap();
		HttpSession session = request.getSession();
		// 公众号获取
		PublicNo publicNo = (PublicNo) request.getSession().getAttribute(ConstantUtil.SESSION_CURRENT_PUBLICNO);
		if(publicNo==null){
			map.put("code", "400");
			map.put("value", "在使用公众号相关信息前，请先设置当前公众号！");
			return (JSON) JSON.toJSON(map);
		}
		
		String[] list=request.getParameterValues("list[]");
		FansGroup fansGroup=fansGroupService.fansGroupInstance(Long.valueOf(request.getParameter("id")));
		String[] list1=new String[list.length];
		for(int j=0;j<list.length;j++){
			Fans fans=fansService.fansInstance(Long.valueOf(list[j]));
			list1[j]=fans.getOpenId();
		}
		
		String authorizer_access_token=TokenHolder.getAuthorizerAccessToken(publicNo.getAppid(), publicNo.getAuthorizer_refresh_token());
		Response response =ApiGroup.moveMembers(authorizer_access_token, fansGroup.getWechatgroupid(), list1);
		if(response.getStatus()==Response.SUCCESS){
			for(int i=0;i<list.length;i++){
				Fans fans=fansService.fansInstance(Long.valueOf(list[i]));
				fans.setFansGroup(fansGroup);
				genericDao.update(fans);
			}
			map.put("code", "200");
			map.put("value", "修改成功");
		}else{
			map.put("code", "400");
			map.put("value", response.getMessage());
		}
		return (JSON) JSON.toJSON(map);
	}
}
