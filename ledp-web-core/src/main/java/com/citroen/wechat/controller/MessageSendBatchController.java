package com.citroen.wechat.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.jsoup.helper.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.citroen.ledp.exception.LedpException;
import com.citroen.ledp.service.PermissionService;
import com.citroen.wechat.api.service.ApiException;
import com.citroen.wechat.api.service.ApiMass;
import com.citroen.wechat.api.service.Response;
import com.citroen.wechat.api.token.TokenHolder;
import com.citroen.wechat.domain.FansGroup;
import com.citroen.wechat.domain.Material;
import com.citroen.wechat.domain.MessageSendBatch;
import com.citroen.wechat.domain.PublicNo;
import com.citroen.wechat.service.FansGroupService;
import com.citroen.wechat.service.MaterialService;
import com.citroen.wechat.service.MessageSendBatchService;
import com.citroen.wechat.util.ConstantUtil;

/**
 * Created by vpc on 2015/6/19.
 */
@Controller("messageSendBatchController")
@RequestMapping("/wechat/messagesendbatch")
public class MessageSendBatchController {
    @Autowired
    private PermissionService permissionService;
    @Autowired
    private MessageSendBatchService messageSendBatchService;
    @Resource
    private MaterialService materialService;
	@Autowired
	private FansGroupService fansGroupService;
	
	private Map<String, Object> params;

    @RequestMapping("index")
    public String index(@RequestParam(defaultValue = "1")int pageNumber, @RequestParam(defaultValue = "2")int pageSize,HttpServletRequest request,HttpServletResponse response,HttpSession session,Model model) throws ApiException {
		// 公众号获取
		PublicNo publicNo = (PublicNo) request.getSession().getAttribute(ConstantUtil.SESSION_CURRENT_PUBLICNO);
		if(publicNo==null){
			return "redirect:/wechat/publicno/message";
		}
		if("-1".equals(publicNo.getVerify_type_info())){
			return "redirect:/wechat/publicno/unauth/"+publicNo.getVerify_type_info();
		}
        try {
            Map params = new HashMap();
            params.put("publicNo",publicNo.getId().toString());
            List<FansGroup> groupList = fansGroupService.executeQuery(params);
			model.addAttribute("groupList", groupList);
		} catch (LedpException e) {
			e.printStackTrace();
		}

        String materialId = request.getParameter("materialId");
        if(null != materialId && materialId.matches("^\\d+$")) {
            model.addAttribute("materialId",materialId);
        }
        int remainTodayCount = messageSendBatchService.remainTodayCount(publicNo);
        model.addAttribute("remainTodayCount",remainTodayCount);
        
        // 返回到界面
        return "wechat/messagesendbatch/index";
    }

    @RequestMapping("save")
    public String save(Model model,@ModelAttribute("entity") MessageSendBatch entity,HttpServletRequest request) {
		PublicNo publicNo = (PublicNo) request.getSession().getAttribute(ConstantUtil.SESSION_CURRENT_PUBLICNO);
		if(publicNo==null){
			return "redirect:/wechat/publicno/message";
		}
		if("-1".equals(publicNo.getVerify_type_info())){
			return "redirect:/wechat/publicno/unauth/"+publicNo.getVerify_type_info();
		}
		List<FansGroup> groupList = new ArrayList<FansGroup>();
        try {
            Map params = new HashMap();
            params.put("publicNo",publicNo.getId().toString());
            groupList = fansGroupService.executeQuery(params);
			model.addAttribute("groupList", groupList);
		} catch (LedpException e) {
			e.printStackTrace();
		}

        model.addAttribute("materialId",request.getParameter("materialId"));
        model.addAttribute("mediaId",request.getParameter("mediaId"));
        model.addAttribute("entity",entity);
        int remainTodayCount= messageSendBatchService.remainTodayCount(publicNo);
        model.addAttribute("remainTodayCount",remainTodayCount);
        if(entity.getId()!=null){
			model.addAttribute("message","群发已经完成，请勿重复提交！");
			return "wechat/messagesendbatch/index";
        }
        
        if(remainTodayCount<=0){
			model.addAttribute("message","今天群发条数已用完！");
			return "wechat/messagesendbatch/index";
        }
        
		String mediaId = request.getParameter("mediaId");
		if("text".equals(entity.getMsgtype())){
			if(StringUtil.isBlank(entity.getContent())){
				model.addAttribute("message","请输入文字信息");
				return "wechat/messagesendbatch/index";
			}
			if(entity.getContent().length()>600){
				model.addAttribute("message","文字信息长度不能超过600个字符");
				return "wechat/messagesendbatch/index";
			}
		}else{
			if(StringUtil.isBlank(mediaId)){
				model.addAttribute("message","请填写图文信息");
				return "wechat/messagesendbatch/index";
			}
		}

        FansGroup group = null;
        String wechatgroupid = null;
        try {
        	group = fansGroupService.get(entity.getGroupId().getId());
        	if(group!=null){
        		wechatgroupid = group.getWechatgroupid();
        	}
		} catch (Exception e) {
		}
        
		entity.setIsToAll(group==null);
        entity.setPublicno(publicNo.getId());
        entity.setSendTime(new Date());
        long id = messageSendBatchService.save(entity);
        entity.setId(id);
        String access_token = TokenHolder.getAuthorizerAccessToken(publicNo.getAppid(), publicNo.getAuthorizer_refresh_token());
        if("text".equals(entity.getMsgtype())) {
        	Response response= ApiMass.send(access_token,entity.getMsgtype(),entity.getContent(),wechatgroupid,entity.getIsToAll());
        	if(response.getStatus()==Response.SUCCESS){
        		entity.setStatus("1");
        		entity.setMsgId(response.get("msg_id"));
        		messageSendBatchService.update(entity);
        	}
        }else {
            Response response= ApiMass.send(access_token,entity.getMsgtype(),mediaId,wechatgroupid,entity.getIsToAll());
        	if(response.getStatus()==Response.SUCCESS){
        		entity.setStatus("1");
        		entity.setMsgId(response.get("msg_id"));
        		messageSendBatchService.update(entity);
        	}
        }
		model.addAttribute("message","群发消息发送成功！");
        return "wechat/messagesendbatch/index";
    }

    @RequestMapping("preview")
    @ResponseBody
    public Map<String, Object> preview(MessageSendBatch entity,HttpServletRequest request) {
        PublicNo publicNo = (PublicNo) request.getSession().getAttribute(ConstantUtil.SESSION_CURRENT_PUBLICNO);
        if(publicNo==null){
	        Map<String, Object> resultMap = new HashMap<String, Object>();
	        Response response = new Response(Response.ERROR,"在使用公众号相关信息前，请先设置当前公众号！");
	        resultMap.put("status", String.valueOf(response.getStatus()));
	        resultMap.put("message",response.getMessage());
	        resultMap.put("params", response.getParams());
	        return resultMap;
    	}
        String towxname = request.getParameter("towxname");
        String mediaId = request.getParameter("mediaId");
        
        if(StringUtils.isBlank(towxname)){
	        Map<String, Object> resultMap = new HashMap<String, Object>();
	        Response response = new Response(Response.ERROR,"请输入接受微信号！");
	        resultMap.put("status", String.valueOf(response.getStatus()));
	        resultMap.put("message",response.getMessage());
	        resultMap.put("params", response.getParams());
	        return resultMap;
        }
        
        Response response = new Response(Response.ERROR,"消息发送失败!");
        String access_token = TokenHolder.getAuthorizerAccessToken(publicNo.getAppid(), publicNo.getAuthorizer_refresh_token());
        if("text".equals(entity.getMsgtype())) {
	       	 if(StringUtils.isBlank(entity.getContent())) {
	  	        Map<String, Object> resultMap = new HashMap<String, Object>();
	  	        response = new Response(Response.ERROR,"选择输入要发送的文字信息！");
	  	        resultMap.put("status", String.valueOf(response.getStatus()));
	  	        resultMap.put("message",response.getMessage());
	  	        resultMap.put("params", response.getParams());
	  	        return resultMap;
	     	 }
	       	 if(entity.getContent().length()>600) {
	  	        Map<String, Object> resultMap = new HashMap<String, Object>();
	  	        response = new Response(Response.ERROR,"输入发送文字信息不能超过600字！");
	  	        resultMap.put("status", String.valueOf(response.getStatus()));
	  	        resultMap.put("message",response.getMessage());
	  	        resultMap.put("params", response.getParams());
	  	        return resultMap;
	     	 }
            response = ApiMass.preview(access_token,entity.getMsgtype(),entity.getContent(),towxname);
        }else{
        	 if(StringUtils.isBlank(mediaId)) {
     	        Map<String, Object> resultMap = new HashMap<String, Object>();
     	        response = new Response(Response.ERROR,"选择素材要发送的素材！");
     	        resultMap.put("status", String.valueOf(response.getStatus()));
     	        resultMap.put("message",response.getMessage());
     	        resultMap.put("params", response.getParams());
     	        return resultMap;
        	 }
        	response = ApiMass.preview(access_token,entity.getMsgtype(),mediaId,towxname);
        }
    
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("status", String.valueOf(response.getStatus()));
        resultMap.put("message",response.getMessage());
        resultMap.put("params", response.getParams());

        return resultMap;
    }

    @RequestMapping("query")
    @ResponseBody
    public Map<String, Object> query(@RequestParam(value="currentPage",defaultValue = "1")int pageNumber, @RequestParam(defaultValue = "2")int pageSize,HttpServletRequest request,HttpServletResponse response,HttpSession session) {
        Map<String,String[]> parameterMap = request.getParameterMap();
        PublicNo publicNo=(PublicNo) session.getAttribute(ConstantUtil.SESSION_CURRENT_PUBLICNO);
        Map<String,Object> json = new HashMap<String,Object>();
        if(publicNo==null){
        	return json;
        }
        json = messageSendBatchService.query(pageNumber, pageSize,parameterMap,publicNo.getId());
        return json;
    }

    
    @RequestMapping("delete")
    @ResponseBody
    public Map<String, Object> delete(@RequestParam(required = true) long id,HttpSession session) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
		MessageSendBatch messageSendBatch =messageSendBatchService.get(id);
		String  msg_id = messageSendBatch.getMsgId();
		if(StringUtils.isNotBlank(msg_id)){
			PublicNo publicNo=(PublicNo) session.getAttribute(ConstantUtil.SESSION_CURRENT_PUBLICNO);
			String access_token = TokenHolder.getAuthorizerAccessToken(publicNo.getAppid(), publicNo.getAuthorizer_refresh_token());
			Response response = ApiMass.delete(access_token, msg_id);
		}
        boolean rs = messageSendBatchService.delete(id);
        if(rs){
            map.put("code","200");
            map.put("message","删除成功！");
        }else{
            map.put("code","201");
            map.put("message","删除失败！");
        }
        return map;
    }
    @RequestMapping("findMaterial")
    @ResponseBody
    public Map<String, Object> findMaterial(@RequestParam(required = true) long id) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        Material entity = materialService.get(id);
        if(entity != null) {
            map.put("author",entity.getAuthor());
            map.put("content",entity.getContent());
            map.put("url",entity.getUrl());
            map.put("title",entity.getTitle());
            map.put("date",entity.getCreateDate());
            map.put("mediaId",entity.getMediaId());
            map.put("materialId",entity.getId());
        }
        return map;
    }
}
