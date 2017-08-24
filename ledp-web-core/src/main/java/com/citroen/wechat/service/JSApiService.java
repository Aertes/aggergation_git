package com.citroen.wechat.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.citroen.ledp.dao.mybaits.MybaitsGenericDao;
import com.citroen.ledp.util.PropertiyUtil;
import com.citroen.wechat.api.token.TokenHolder;
import com.citroen.wechat.api.util.SHA1;
import com.citroen.wechat.domain.Campaign;
import com.citroen.wechat.domain.PublicNo;
import com.citroen.wechat.domain.Site;
import com.citroen.wechat.domain.SitePage;

@Service
public class JSApiService {
	@Autowired
	private MybaitsGenericDao<Long> mybaitsGenericDao;
	@Autowired
	private PluginService pluginService;
	
	/**根据页面ID获取公众号AppId***/
	public PublicNo getPublicNo(long pageId){
		try {
			SitePage sitePage = mybaitsGenericDao.get(SitePage.class,pageId);
			Site site = mybaitsGenericDao.get(Site.class,sitePage.getSite().getId());
			Campaign campaign = site.getCampaign();
			if(campaign!=null){
				if(campaign.getOrg()!=null){
					PublicNo publicNo = mybaitsGenericDao.find(PublicNo.class,"select * from t_publicno p where p.authorized = 1 and org = "+campaign.getOrg().getId()+" order by verify_type_info desc");
					return publicNo;
				}else if(campaign.getDealer()!=null){
					PublicNo publicNo = mybaitsGenericDao.find(PublicNo.class,"select * from t_publicno p where p.authorized = 1 and dealer = "+campaign.getDealer().getId()+" order by verify_type_info desc");
					return publicNo;
				}
			}else{
				/*if(site.getOrg()!=null){
					PublicNo publicNo = mybaitsGenericDao.find(PublicNo.class,"select * from t_publicno where org="+site.getOrg().getId());
					return publicNo.getAppid();
				}else */
				if(site.getDealer()!=null){
					PublicNo publicNo = mybaitsGenericDao.find(PublicNo.class,"select * from t_publicno p where p.authorized = 1 and dealer = "+site.getDealer().getId()+" order by verify_type_info desc");
					return publicNo;
				}
			}
			
			
		} catch (Exception e) {
		}
		return null;
	}
	public String getJSApiNonceStr(){
		return UUID.randomUUID().toString().replace("-","");
	}
	
	/**获取网页JS签名测试接口***/
	public Map<String,String> getJSApiSignature(String authorizer_refresh_token,String appid,String noncestr,String timestamp,String url){
		Map<String,String> rs = new HashMap<String,String>();
		String jsapi_ticket = TokenHolder.getAuthorizerJsapiTicket(appid,authorizer_refresh_token);
		if(StringUtils.isBlank(jsapi_ticket)){
			return rs;
		}
		StringBuilder signature = new StringBuilder();
		signature.append("jsapi_ticket=").append(jsapi_ticket);
		signature.append("&noncestr=").append(noncestr);
		signature.append("&timestamp=").append(timestamp);
		signature.append("&url=").append(url);
		String digest = new SHA1().getDigestOfString(signature.toString().getBytes());
		rs.put("signature",digest.toLowerCase());
		rs.put("signature_str",signature.toString());
		return rs;
	}
	
	/**获取网页JS签名***/
	public String getJSApiSignatureBK(String authorizer_refresh_token,String appid,String noncestr,String timestamp,String url){
		String jsapi_ticket = TokenHolder.getAuthorizerJsapiTicket(appid,authorizer_refresh_token);
		if(StringUtils.isBlank(jsapi_ticket)){
			return "";
		}
		StringBuilder signature = new StringBuilder();
		signature.append("jsapi_ticket=").append(jsapi_ticket);
		signature.append("&noncestr=").append(noncestr);
		signature.append("&timestamp=").append(timestamp);
		signature.append("&url=").append(url);
		
		String digest = new SHA1().getDigestOfString(signature.toString().getBytes());
		return digest.toLowerCase();
	}
	/**根据页面Id获取分享渠道列表 
	 * @throws Exception */
	public List<String> getJSApiList(long pageId) throws Exception {
		List<String> channels = pluginService.getShareMedia(pageId);
		List<String> jsApiList = new ArrayList<String>();
		if(CollectionUtils.isNotEmpty(channels)){
			for(String str : channels){
				if("firendQuan".equals(str)){
					jsApiList.add("onMenuShareTimeline");
				}
				if("QQ".equals(str)){
					jsApiList.add("onMenuShareQQ");
				}
				if("weibo".equals(str)){
					jsApiList.add("onMenuShareWeibo");			
				}
				if("friend".equals(str)){
					jsApiList.add("onMenuShareAppMessage");
				}
			}
		}
		
		return jsApiList;
	}
	/**根据页面Id获取分享渠道列表 
	 * @throws Exception */
	public Map<String,Object> getShareTo(long pageId) throws Exception {
		List<String> channels = pluginService.getShareMedia(pageId);
		Map<String, Object> params = new HashMap<String, Object>();
		PropertiyUtil propUtil = new PropertiyUtil("/wechat.properties");
		String url = "";
		String imgUrl = "";
		String desc = "";
		String title = "";
		params.put("pluginId", 10);
		params.put("pageId", pageId);
		params.put("name", "guize");
		desc = pluginService.getVal(params);
		params.put("name", "tuwenImg");
		imgUrl = pluginService.getVal(params);
		if(StringUtils.isBlank(imgUrl)){
			imgUrl = propUtil.getString("plugin.url")+"/images/share.jpg";
		}
		params.put("name", "shareTitle");
		title = pluginService.getVal(params);
		SitePage sitePage = mybaitsGenericDao.get(SitePage.class,pageId);
		if(sitePage != null && sitePage.getSite() != null){
			if(sitePage.getSite().getCampaign() != null){
				Campaign campaign = sitePage.getSite().getCampaign();
				url += propUtil.getString("campaign.url")+"/"+campaign.getCode();
			}else{
				Site site = sitePage.getSite();
				url += propUtil.getString("site.url")+"/"+site.getCode();
			}
		}
		Map<String,Object> to = new HashMap<String,Object>();
		if(CollectionUtils.isNotEmpty(channels)){
			for(String str : channels){
				if("firendQuan".equals(str)){
					Map<String,String> onMenuShare = new HashMap<String,String>();
					onMenuShare.put("title",title);
					onMenuShare.put("desc",desc);
					onMenuShare.put("link",url+sitePage.getUrl());
					onMenuShare.put("imgUrl",imgUrl);
					to.put("onMenuShareTimeline",onMenuShare);
				}
				if("QQ".equals(str)){
					Map<String,String> onMenuShare = new HashMap<String,String>();
					onMenuShare.put("title",title);
					onMenuShare.put("desc",desc);
					onMenuShare.put("link",url+sitePage.getUrl());
					onMenuShare.put("imgUrl",imgUrl);
					to.put("onMenuShareQQ",onMenuShare);
				}
				if("weibo".equals(str)){
					Map<String,String> onMenuShare = new HashMap<String,String>();
					onMenuShare.put("title",title);
					onMenuShare.put("desc",desc);
					onMenuShare.put("link",url+sitePage.getUrl());
					onMenuShare.put("imgUrl",imgUrl);
					to.put("onMenuShareWeibo",onMenuShare);
				}
				if("friend".equals(str)){
					Map<String,String> onMenuShare = new HashMap<String,String>();
					onMenuShare.put("title",title);
					onMenuShare.put("desc",desc);
					onMenuShare.put("link",url+sitePage.getUrl());
					onMenuShare.put("imgUrl",imgUrl);
					to.put("onMenuShareAppMessage",onMenuShare);
				}
			}
		}
		return to;
	}
	
}
