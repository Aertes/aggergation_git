package com.citroen.wechat.controller;

import com.alibaba.fastjson.JSON;
import com.citroen.ledp.dao.mybaits.MybaitsGenericDao;
import com.citroen.ledp.exception.LedpException;
import com.citroen.ledp.service.PermissionService;
import com.citroen.wechat.api.model.Group;
import com.citroen.wechat.api.service.ApiGroup;
import com.citroen.wechat.api.service.Response;
import com.citroen.wechat.api.token.TokenHolder;
import com.citroen.wechat.domain.Fans;
import com.citroen.wechat.domain.FansGroup;
import com.citroen.wechat.domain.PublicNo;
import com.citroen.wechat.service.FansGroupService;
import com.citroen.wechat.service.FansService;
import com.citroen.wechat.util.ConstantUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** 
 * @ClassName: FansGroupController 
 * @Description: 公众号分组控制层
 * @author 杨少波
 * @date 2015年6月28日 下午1:58:02 
 * 
 */
@Controller("fansGroupController")
@RequestMapping("/wechat/fansGroup")
public class FansGroupController {
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
	public ModelAndView index(Model model,HttpServletRequest request,HttpServletResponse response,HttpSession session) throws Exception {
		ModelAndView view = new ModelAndView();
		view.setViewName("wechat/fansGroup/index");
	
		// 返回到界面
		return view;
	}
	@RequestMapping(value = { "search" })
	@ResponseBody
	public JSON search(Model model, HttpServletRequest request) throws Exception {
		
		final String contextPath = request.getContextPath();
		List<FansGroup> rows = new ArrayList<FansGroup>();
		int total = 0;
		Map map=new HashMap();
		List list =new ArrayList();
		HttpSession session = request.getSession();
		
		if(session.getAttribute(ConstantUtil.SESSION_CURRENT_PUBLICNO)!=null){
			PublicNo publicNo=(PublicNo)session.getAttribute(ConstantUtil.SESSION_CURRENT_PUBLICNO);
			params.put("publicNo", publicNo.getId().toString());
			rows = fansGroupService.executeQuery(params);
			//total = fansGroupService.getTotalRow(params);
			
			Map map1=new HashMap();
			List<Fans> fans=fansService.getFans(publicNo.getId());
			map1.put("name", "全部用户");
			map1.put("number", fans.size());
			list.add(map1);
			for(int i=0;i<rows.size();i++){
				FansGroup fansGroup=rows.get(i);
				List<Fans> groupfans=fansService.getFans(fansGroup.getId(),publicNo.getId());
				Map map3=new HashMap();
				map3.put("id", fansGroup.getId());
				map3.put("name", fansGroup.getName());
				map3.put("number", groupfans.size());
				map3.put("wechatgroupid", fansGroup.getWechatgroupid());
				list.add(map3);
			}
		}
		map.put("data",list);
		return (JSON) JSON.toJSON(map);
	}
	@RequestMapping(value="/create")
	public String create(Model model,HttpServletRequest request) throws Exception {
		return "wechat/fansGroup/create";
	}
	@RequestMapping(value = { "save" })
	@ResponseBody
	public JSON save(Model model, HttpServletRequest request) throws Exception {
		
		Map map=new HashMap();
		HttpSession session = request.getSession();
		// 公众号获取
		PublicNo publicno = (PublicNo) request.getSession().getAttribute(ConstantUtil.SESSION_CURRENT_PUBLICNO);
		if(publicno==null){
			map.put("code", "400");
			map.put("value", "在使用公众号相关信息前，请先设置当前公众号！");
			return (JSON) JSON.toJSON(map);
		}
		
		String authorizer_access_token=TokenHolder.getAuthorizerAccessToken(publicno.getAppid(), publicno.getAuthorizer_refresh_token());
		Group group=new Group();
		group.setName(request.getParameter("name"));
		group.setCount("0");
		Response response=ApiGroup.create(authorizer_access_token, group);
		if(response.getStatus()==Response.SUCCESS){
			
			List<FansGroup> fansGroups=fansGroupService.getFansGroup(publicno.getId());
			FansGroup fansGroup=new FansGroup();
			String name = request.getParameter("name");
			fansGroup.setName(request.getParameter("name"));
			//fansGroup.setComment(request.getParameter("comment"));
			fansGroup.setPublicno(publicno);
			fansGroup.setStatus(1);
			fansGroup.setWechatgroupid(response.get("id").toString());
			if(fansGroups.size()>0){
				fansGroup.setSort(fansGroups.get(0).getSort()+1);
			}else{
				fansGroup.setSort(0);
			}
			long id=fansGroupService.save(fansGroup);
			map.put("code", "200");
			map.put("id", id);
			map.put("name", name);
			map.put("value", "保存成功");
		}else{
			map.put("code", "400");
			map.put("value", response.getMessage());
		}
		
		return (JSON) JSON.toJSON(map);
	}
	@RequestMapping(value="/update/{id}")
	public String update(Model model,@PathVariable long id,HttpServletRequest request) throws LedpException {
		FansGroup fansGroup=fansGroupService.fansGroupInstance(id);
		model.addAttribute("fansGroup", fansGroup);
		return "wechat/fansGroup/update";
	}
	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	@ResponseBody
	public JSON edit(Model model,HttpServletRequest request) throws Exception {
		Map map=new HashMap();
		HttpSession session = request.getSession();
		// 公众号获取
		PublicNo publicno = (PublicNo) request.getSession().getAttribute(ConstantUtil.SESSION_CURRENT_PUBLICNO);
		if(publicno==null){
			map.put("code", "400");
			map.put("value", "在使用公众号相关信息前，请先设置当前公众号！");
			return (JSON) JSON.toJSON(map);
		}
	
		String authorizer_access_token=TokenHolder.getAuthorizerAccessToken(publicno.getAppid(), publicno.getAuthorizer_refresh_token());
		
		FansGroup fansGroup=fansGroupService.fansGroupInstance(Long.valueOf(request.getParameter("id")));
		
		Group group=new Group();
		group.setId(fansGroup.getWechatgroupid());
		group.setName(request.getParameter("name"));
		group.setCount("0");
	
		Response response=ApiGroup.update(authorizer_access_token, group);
		
		if(response.getStatus()==Response.SUCCESS){
			fansGroup.setName(request.getParameter("name"));
			genericDao.update(fansGroup);
			map.put("code", "200");
			map.put("value", "修改成功");
		}else{
			map.put("code", "400");
			map.put("value", response.getMessage());
		}
		return (JSON) JSON.toJSON(map);
	}
	@RequestMapping(value="/delete/{id}")
	@ResponseBody
	public JSON delete(@PathVariable long id,HttpServletRequest request) throws LedpException {
		Map map=new HashMap();
		FansGroup fansGroup=fansGroupService.fansGroupInstance(id);
		HttpSession session = request.getSession();
		// 公众号获取
		PublicNo publicno = (PublicNo) request.getSession().getAttribute(ConstantUtil.SESSION_CURRENT_PUBLICNO);
		if(publicno==null){
			map.put("code", "400");
			map.put("value", "在使用公众号相关信息前，请先设置当前公众号！");
			return (JSON) JSON.toJSON(map);
		}
		
		String authorizer_access_token=TokenHolder.getAuthorizerAccessToken(publicno.getAppid(), publicno.getAuthorizer_refresh_token());
		Response response=ApiGroup.delete(authorizer_access_token, fansGroup.getWechatgroupid());

		if(response.getStatus()==Response.SUCCESS){
			List<Fans> fans=fansService.getFans(id, publicno.getId());
			FansGroup fansGroup1=fansGroupService.getFansGroup(publicno.getId(),"0");
			for(int i=0;i<fans.size();i++){
				Fans fan=fans.get(i);
				fan.setFansGroup(fansGroup1);
				genericDao.update(fan);
			}
			fansGroupService.deleteFansGroupInstance(id);
			map.put("code", "200");
			map.put("value", "删除成功");
		}else{
			map.put("code", "400");
			map.put("value", response.getMessage());
		}
		return (JSON) JSON.toJSON(map);
	}
	
	/**
	 * 获取当前公众号下所有组
	 * @param model
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/getGroups", method = RequestMethod.POST)
	@ResponseBody
	public JSON getGroups(Model model,HttpServletRequest request) throws Exception {
		Map map=new HashMap();
		HttpSession session = request.getSession();
		// 公众号获取
		PublicNo publicno = (PublicNo) request.getSession().getAttribute(ConstantUtil.SESSION_CURRENT_PUBLICNO);
		if(publicno==null){
			map.put("code", "400");
			map.put("value", "在使用公众号相关信息前，请先设置当前公众号！");
			return (JSON) JSON.toJSON(map);
		}
	
		
		List<FansGroup> fansGroups = fansGroupService.getFansGroups(publicno.getId());
		map.put("code", 200);
		map.put("value", fansGroups);
		return (JSON) JSON.toJSON(map);
	}
}
