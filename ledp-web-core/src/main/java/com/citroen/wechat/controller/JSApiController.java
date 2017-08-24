package com.citroen.wechat.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.citroen.wechat.api.util.DateUtil;
import com.citroen.wechat.api.util.HttpUtil;
import com.citroen.wechat.domain.Campaign;
import com.citroen.wechat.domain.Fans;
import com.citroen.wechat.domain.PublicNo;
import com.citroen.wechat.domain.Site;
import com.citroen.wechat.domain.SitePage;
import com.citroen.wechat.service.CampaignService;
import com.citroen.wechat.service.FansService;
import com.citroen.wechat.service.JSApiService;
import com.citroen.wechat.service.PluginService;
import com.citroen.wechat.service.PublicNoService;
import com.citroen.wechat.service.SitePageService;
import com.citroen.wechat.service.SiteService;

/**
 * 网页JS验证，粉丝授权，粉丝信息接口
 * @date2015年6月2日
 */
@Controller("jsapiController")
@RequestMapping("/wechat/jsapi")
public class JSApiController {
	private final static String dateFormat = "yyyy-MM-dd HH:mm:ss";
	private Map<String, Object> params;
	@Autowired
	private PublicNoService publicNoService;
	@Autowired
	private FansService fansService;
	@Autowired
	private JSApiService jsapiService;
	@Autowired
	private PluginService pluginService;
	@Autowired
	private SitePageService sitePageService;
	@Autowired
	private SiteService siteService;
	@Autowired
	private CampaignService campaignService;
	/*		
	{
	    debug: true, // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
	    appId: '', // 必填，公众号的唯一标识
	    timestamp: , // 必填，生成签名的时间戳
	    nonceStr: '', // 必填，生成签名的随机串
	    signature: '',// 必填，签名，见附录1
	    jsApiList: [] // 必填，需要使用的JS接口列表，所有JS接口列表见附录2
	}*/
	//获取网页config接口注入权限验证配置
	@RequestMapping(value="config/{pageId}")
	@ResponseBody
	public JSONObject getJSApiConfig(@PathVariable long pageId,HttpServletRequest request) throws Exception{
		//shareConfig
		JSONObject shareConfig = new JSONObject();
		
		PublicNo publicNo  = jsapiService.getPublicNo(pageId);
		if(publicNo == null){
			return shareConfig;
		}
		String url    = StringUtils.isBlank(request.getParameter("url"))?request.getRequestURL().toString():request.getParameter("url");
		String debug  = request.getParameter("debug");
		String timestamp = String.valueOf(System.currentTimeMillis()/1000);
		String nonceStr  = jsapiService.getJSApiNonceStr();
		Map<String,String> signatureMap = jsapiService.getJSApiSignature(publicNo.getAuthorizer_refresh_token(),publicNo.getAppid(),nonceStr,timestamp,url);
		List<String> jsApiList = jsapiService.getJSApiList(pageId);
		
		boolean debugBoolean = "true".equals(debug)?true:false;
		//shareConfig-config
		JSONObject config = new JSONObject();
		config.put("debug",debugBoolean);
		config.put("appId",publicNo.getAppid());
		config.put("timestamp",timestamp);
		config.put("nonceStr",nonceStr);
		config.put("signature",signatureMap.get("signature"));
		config.put("jsApiList",jsApiList);
		
		//shareConfig-share
		JSONObject share = new JSONObject();
		//shareConfig-share-to
		 Map<String,Object> to =  jsapiService.getShareTo(pageId);
		share.put("to",to);
		
		Map<String,Object> condiction = new HashMap<String, Object>();
		condiction.put("pluginId", 10);
		condiction.put("pageId", pageId);
		condiction.put("name", "shareStartTime");
		String shareStartTime = pluginService.getVal(condiction);
		condiction.put("name","shareEndTime");
		String shareEndTime = pluginService.getVal(condiction);
		condiction.put("name", "guize");
		//规则描述
		String description = pluginService.getVal(condiction);
		Date curDate = new Date();
		Date shareStartDate =  DateUtil.convert(shareStartTime+":00",dateFormat);
		Date shareEndDate =  DateUtil.convert(shareEndTime+":00",dateFormat);
		
		if(curDate.before(shareStartDate)){
			shareConfig.put("status", "punstart");
		}else if(curDate.after(shareEndDate)){
			shareConfig.put("status", "phasEnd");
		}else{
			shareConfig.put("status", "normal");
		}
		
		SitePage page = sitePageService.siteInstance(pageId);
		if(page.getSite() != null && page.getSite().getCampaign()!=null && "normal".equals(shareConfig.getString("status"))){
			Campaign campaign = page.getSite().getCampaign();
			//活动开始时间
			Date startDate = campaign.getBeginDate();
			//活动结束时间
			Date endDate = campaign.getEndDate();
			if(curDate.before(startDate)){
				shareConfig.put("status", "unstart");
			}else if(curDate.after(endDate)){
				shareConfig.put("status", "hasEnd");
			}else{
				shareConfig.put("status", "normal");
			}
		}
		
		shareConfig.put("description", description);
		shareConfig.put("shareStartTime", shareStartTime);
		shareConfig.put("shareEndTime", shareEndTime);
		shareConfig.put("config",config);
		shareConfig.put("share",share);
		if(debugBoolean){
			shareConfig.put("signatureTestMap",signatureMap);
		}
		return shareConfig;
	}
	
	@RequestMapping(value="getAppid/{pageId}")
	@ResponseBody
	public JSONObject getAppid(@PathVariable long pageId,HttpServletRequest request) throws Exception{
		//shareConfig
		JSONObject json = new JSONObject();
		SitePage sitePage = sitePageService.sitePageInstance(pageId);
		if(sitePage == null){
			json.put("state","201");
			json.put("message","授权失败，用户不同意授权");
		}else{
			try {
				PublicNo publicNo = null;
				Site site = sitePage.getSite();
				if(site != null){
					publicNo = sitePage.getSite().getPublicNo();
				}
				if(publicNo == null){
					Campaign campaign = site.getCampaign();
					if(campaign != null){
						campaign = campaignService.campaignInstance(campaign.getId());
						if(campaign != null){
							publicNo = campaign.getPublicNo();
						}else{
							json.put("state","201");
							json.put("message","授权失败，获取公众号信息失败");
							return json;
						}
					}
				}
				json.put("state","200");
				json.put("appid",publicNo.getAppid());
				
				if("2".equals(publicNo.getService_type_info())){
					json.put("type", 1);
				}else{
					json.put("type", 2);
				}
			} catch (Exception e) {
				e.printStackTrace();
				json.put("state","201");
				json.put("message","授权失败，获取公众号信息失败");
			}
		}
		return  json;
		
	}
	
	//微信网页授权回调接口，获取网页access_token
	@RequestMapping(value="snsapi_base/{appid}")
	@ResponseBody
	public JSONObject snsapi_base(Model model,@PathVariable String appid,HttpServletRequest request) throws Exception {
		//得到回调接口推送值
		String code   = request.getParameter("code");
		String refresh_token = request.getParameter("refresh_token");
		if(StringUtils.isBlank(code)){
			JSONObject json = new JSONObject();
			json.put("state","201");
			json.put("message","授权失败，用户不同意授权");
			return  json;
		}
		//获取公众号信息，要改成从数据库取，在公众号授权第一步需要网点填写
		PublicNo publicNo = publicNoService.getByAppid(appid);
		String appSecret = publicNo.getApp_secret();
		String url = "";
		//调用接口
		JSONObject json = null;
		if(StringUtils.isNotBlank(refresh_token)){
			url = "https://api.weixin.qq.com/sns/oauth2/refresh_token?appid="+appid+"&grant_type=refresh_token&refresh_token="+refresh_token;
			json = HttpUtil.doGet(url);
			//refresh_token失效了
			if(json.containsKey("errcode")){
				url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid="+appid+"&secret="+appSecret+"&code="+code+"&grant_type=authorization_code";
				json = HttpUtil.doGet(url);
			}
		}else{
			url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid="+appid+"&secret="+appSecret+"&code="+code+"&grant_type=authorization_code";
			json = HttpUtil.doGet(url);
		}
		
		//调用接口
		if(json.containsKey("errcode")){
			json.put("state","201");
			json.put("message","授权失败，接口调用出错");
		}else{
			String openid  = json.getString("openid");
			Fans fans = fansService.getFans(appid, openid);
			if(fans==null){
				//第一次授权成功保存粉丝
				fansService.save(appid,json);
			}else{
				//再次授权成功修改粉丝
				fansService.update(fans,json);
			}
			json.put("state","200");
			json.put("message","授权成功");
		}
		return  json;
	}
	
	//微信网页授权回调接口，获取网页access_token
	@RequestMapping(value="snsapi_userinfo/{pageid}")
	@ResponseBody
	public JSONObject snsapi_userinfo(Model model,@PathVariable long pageid,HttpServletRequest request) throws Exception {
		//得到回调接口推送值
		String code   = request.getParameter("code");
		String refresh_token = request.getParameter("refresh_token");
		if(StringUtils.isBlank(code)){
			JSONObject json = new JSONObject();
			json.put("state","201");
			json.put("message","授权失败，用户不同意授权");
			return  json;
		}
		SitePage sitePage = sitePageService.sitePageInstance(pageid);
		if(StringUtils.isBlank(code) || sitePage == null){
			JSONObject json = new JSONObject();
			json.put("state","201");
			json.put("message","授权失败，用户不同意授权");
			return  json;
		}
		PublicNo publicNo = null;
		try {
			Site site = sitePage.getSite();
			if(site != null){
				publicNo = sitePage.getSite().getPublicNo();
			}else{
				JSONObject json = new JSONObject();
				json.put("state","201");
				json.put("message","授权失败，获取公众号信息失败");
				return json;
			}
			if(publicNo == null){
				Campaign campaign = site.getCampaign();
				if(campaign != null){
					campaign = campaignService.campaignInstance(campaign.getId());
					if(campaign != null){
						publicNo = campaign.getPublicNo();
					}else{
						JSONObject json = new JSONObject();
						json.put("state","201");
						json.put("message","授权失败，获取公众号信息失败");
						return json;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			JSONObject json = new JSONObject();
			json.put("state","201");
			json.put("message","授权失败，获取公众号信息失败");
			return  json;
		}
		
		//获取公众号信息，要改成从数据库取，在公众号授权第一步需要网点填写
		String appSecret = publicNo.getApp_secret();
		String appid = publicNo.getAppid();
		//调用接口
		String url = "";
		//调用接口
		JSONObject json = null;
		if(StringUtils.isNotBlank(refresh_token)){
			url = "https://api.weixin.qq.com/sns/oauth2/refresh_token?appid="+appid+"&grant_type=refresh_token&refresh_token="+refresh_token;
			json = HttpUtil.doGet(url);
			//refresh_token失效了
			if(json.containsKey("errcode")){
				url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid="+appid+"&secret="+appSecret+"&code="+code+"&grant_type=authorization_code";
				json = HttpUtil.doGet(url);
			}
		}else{
			url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid="+appid+"&secret="+appSecret+"&code="+code+"&grant_type=authorization_code";
			json = HttpUtil.doGet(url);
		}
		if(json.containsKey("errcode")){
			json.put("state","201");
			json.put("message","授权失败，接口调用出错");
		}else{
			String openid  = json.getString("openid");
			String access_token  = json.getString("access_token");
			Fans fans = fansService.getFans(publicNo.getAppid(), openid);
			if(fans==null){
				//第一次授权成功保存粉丝
				String url2 = "https://api.weixin.qq.com/sns/userinfo?access_token="+access_token+"&openid="+openid+"&lang=zh_CN";
				JSONObject json2 = HttpUtil.doGet(url2);
				json2.putAll(json);
				fansService.save(publicNo.getAppid(),json2);
			}else{
				//再次授权成功修改粉丝
				fansService.update(fans,json);
			}
			json.put("state","200");
			json.put("message","授权成功");
		}
		return  json;
	}
}
