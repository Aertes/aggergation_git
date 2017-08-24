package com.citroen.wechat.controller;

import com.alibaba.fastjson.JSON;
import com.citroen.ledp.dao.mybaits.MybaitsGenericDao;
import com.citroen.ledp.domain.User;
import com.citroen.ledp.service.PermissionService;
import com.citroen.ledp.util.DateUtil;
import com.citroen.wechat.api.service.ApiReply;
import com.citroen.wechat.api.service.Response;
import com.citroen.wechat.api.token.TokenHolder;
import com.citroen.wechat.domain.Fans;
import com.citroen.wechat.domain.FansGroup;
import com.citroen.wechat.domain.Message;
import com.citroen.wechat.domain.PublicNo;
import com.citroen.wechat.service.FansGroupService;
import com.citroen.wechat.service.MessageService;
import com.citroen.wechat.util.ConstantUtil;
import com.citroen.wechat.util.Pagination;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * 消息管理
 * @author 何海粟
 * @date2015年6月8日
 */
@Controller("messageController")
@RequestMapping("/wechat/message")
public class MessageController {
	private Map<String, Object> params;
	@Autowired
	private PermissionService permissionService;
	@Autowired
	private MessageService messageService;
	@Autowired
	private MybaitsGenericDao<Long> genericDao;
	@Autowired
	private FansGroupService fansGroupService;
	@Autowired
	private MybaitsGenericDao<Long> mybaitsGenericDao;
	
	@RequestMapping(value={"","index"})
	public String index(Model model,HttpServletRequest request,HttpServletResponse response,HttpSession session) throws Exception {
		// 公众号获取
		PublicNo publicNo = (PublicNo) request.getSession().getAttribute(ConstantUtil.SESSION_CURRENT_PUBLICNO);
		if(publicNo==null){
			return "redirect:/wechat/publicno/message";
		}
/*		if("-1".equals(publicNo.getVerify_type_info())){
			return "redirect:/wechat/publicno/unauth/"+publicNo.getVerify_type_info();
		}*/
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
		
		String sql="select count(id) as ct from t_message where parent is null and transtype=0 and publicno="+publicNo.getId();
		
		List<Map> listY = mybaitsGenericDao.executeQuery(sql);
		for(Map map:listY){
			model.addAttribute("messageNumbers", map.get("ct"));
		}
		// 返回到界面
		return "wechat/message/index";
	}
	@RequestMapping(value = { "search" })
	@ResponseBody
	public JSON search(Model model, HttpServletRequest request) throws Exception {
		
		final String contextPath = request.getContextPath();
		int pageSize = 10;
		int pageNumber = 1;
		try {
			pageSize = Integer.parseInt(request.getParameter("pageSize"));
		} catch (Exception e) {
		}
		try {
			pageNumber = Integer.parseInt(request.getParameter("currentPage"));
		} catch (Exception e) {
		}
		HttpSession session = request.getSession();
		if(session.getAttribute(ConstantUtil.SESSION_CURRENT_PUBLICNO)!=null){
			PublicNo publicNo=(PublicNo)session.getAttribute(ConstantUtil.SESSION_CURRENT_PUBLICNO);
			params.put("publicNo", publicNo.getId());
		}else{
			params.put("publicNo", 1L);
		}
		params.put("pageSize", pageSize);
		params.put("pageNumber", pageNumber);
		List<Message> rows = messageService.executeQuery(params);
		int total = messageService.getTotalRow(params);
		boolean editHref=false;
		if (permissionService.hasAuth("wechat/message/reply")) {
			editHref=true;
		}
		List list=new ArrayList();
		Date date=new Date();
		for(int i=0;i<rows.size();i++){
			Message message=rows.get(i);
			Map map=new HashMap();
			map.put("id", message.getId());
			Fans fans = message.getFan();
			if(fans!=null){
				if(StringUtils.isBlank(fans.getHeadImgUrl())){
					String portal = request.getContextPath()+"/wechat/img/fans.png";
					map.put("portal",portal);
				}else{
					map.put("portal", fans.getHeadImgUrl());
				}
				if(StringUtils.isBlank(fans.getNickName())){
					map.put("name","匿名");
				}else{
					map.put("name", fans.getNickName());
				}
				if(StringUtils.isBlank(fans.getCountry())){
					map.put("address","");
				}else{
					map.put("address", fans.getCountry()+" "+fans.getProvince()+" "+fans.getCity());
				}
				
				if(StringUtils.isBlank(fans.getRemark())){
					map.put("signature","");
				}else{
					map.put("signature", fans.getRemark());
				}
				
				FansGroup fansGroup = fansGroupService.getGoupByFans(fans.getId());
				map.put("group",fansGroup==null?"":fansGroup.getName());
			}else{
				String portal = request.getContextPath()+"/wechat/img/fans.png";
				map.put("portal",portal);
				map.put("name","匿名");
				map.put("address","");
				map.put("signature","");
				map.put("group","");
			}
			
			if(message.getCanstant()==1){
				map.put("state", "已回复");
			}
			map.put("type", message.getType()==null?"":message.getType().getCode());
			if(!"".equals(message.getContent())){
				map.put("content", message.getContent());
			}else{
				map.put("content", message.getMid());
			}
			map.put("time", DateUtil.format(message.getCreateTime(),"yyyy-MM-dd HH:mm"));
			if(editHref){
				if(DateUtil.isDate(message.getCreateTime(), new Date())){
					map.put("favorate",  "favorate");
				}
			}


			list.add(map);
		}
		Pagination page=new Pagination(total,pageSize,pageNumber);
//		Map map2=new HashMap();
//		map2.put("currentPage", page.getCurrentPage());
//		map2.put("pageNumber", page.getPageNumber());
//		map2.put("pages", page.getPages());
		Map map1=new HashMap();
		map1.put("paginationData", page);
		map1.put("data", list);
		return (JSON) JSON.toJSON(map1);
	}
	@RequestMapping(value = { "reply" })
	@ResponseBody
	public JSON reply(Model model, HttpServletRequest request) throws Exception {
		Map map=new HashMap();
		//调用发送消息接口
		HttpSession session = request.getSession();
		Message message=messageService.messageInstance(Long.valueOf(request.getParameter("messageId")));
		message.setCanstant(1);
		genericDao.update(message);
		
		Message message1=new Message();
		message1.setPublicNo(message.getPublicNo());
		message1.setType(message.getType());
		message1.setContent(request.getParameter("content"));
		message1.setFan(message.getFan());
		message1.setCanstant(2);
		message1.setComment("111");
		message1.setParent(message);
		message1.setTransType(1);
		message1.setCreateUser((User) session.getAttribute("loginUser"));
		message1.setCreateTime(new Date());
		messageService.save(message1);
		
		PublicNo publicNo=(PublicNo) session.getAttribute(ConstantUtil.SESSION_CURRENT_PUBLICNO);
		if(publicNo==null){
			map.put("code", "201");
			map.put("value", "在使用公众号相关信息前，请先设置当前公众号！");
			return (JSON) JSON.toJSON(map);
		}
		
		String authorizer_access_token=TokenHolder.getAuthorizerAccessToken(publicNo.getAppid(), publicNo.getAuthorizer_refresh_token());
		com.citroen.wechat.api.model.Message m = new com.citroen.wechat.api.model.Message();
		m.setToUserName(message.getFan().getOpenId());
		m.setMsgType("text");
		m.setContent(message1.getContent());
		Response response = ApiReply.reply(authorizer_access_token,m);
		if(response.getStatus()==Response.SUCCESS){
			map.put("code", 200);
			map.put("value", "消息发送成功");
		}else{
			map.put("code", 201);
			map.put("value", "消息发送失败");
		}
		return (JSON) JSON.toJSON(map);
	}

	@RequestMapping(value = { "newMessage" })
	@ResponseBody
	public JSON newMessage(Model model, HttpServletRequest request)  throws Exception {
		Map map=new HashMap();
		//调用发送消息接口
		final String contextPath = request.getContextPath();
		int pageSize = 10;
		int pageNumber = 1;
		try {
			pageSize = Integer.parseInt(request.getParameter("pageSize"));
		} catch (Exception e) {
		}
		try {
			pageNumber = Integer.parseInt(request.getParameter("currentPage"));
		} catch (Exception e) {
		}
		HttpSession session = request.getSession();
		if(session.getAttribute(ConstantUtil.SESSION_CURRENT_PUBLICNO)!=null){
			PublicNo publicNo=(PublicNo)session.getAttribute(ConstantUtil.SESSION_CURRENT_PUBLICNO);
			params.put("publicNo", publicNo.getId());
		}else{
			params.put("publicNo", 1L);
		}
		params.put("pageSize", pageSize);
		params.put("pageNumber", pageNumber);
		int total = messageService.getNewMessage(params);
		if(total > 0 ){
			map.put("newMessage",true);
		} else {
			map.put("newMessage",false);
		}
		return (JSON) JSON.toJSON(map);
	}
}
