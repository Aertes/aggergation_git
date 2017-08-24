package com.citroen.wechat.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.citroen.ledp.dao.mybaits.MybaitsGenericDao;
import com.citroen.ledp.domain.Constant;
import com.citroen.ledp.domain.Dealer;
import com.citroen.ledp.domain.Organization;
import com.citroen.ledp.domain.User;
import com.citroen.ledp.exception.LedpException;
import com.citroen.ledp.service.ConstantService;
import com.citroen.ledp.util.LedpLogger;
import com.citroen.ledp.util.PropertiyUtil;
import com.citroen.ledp.util.SpringContextUtil;
import com.citroen.wechat.api.model.Fans;
import com.citroen.wechat.api.model.Group;
import com.citroen.wechat.api.model.MaterialNews;
import com.citroen.wechat.api.service.ApiAccessToken;
import com.citroen.wechat.api.service.ApiFans;
import com.citroen.wechat.api.service.ApiGroup;
import com.citroen.wechat.api.service.ApiMaterial;
import com.citroen.wechat.api.service.Response;
import com.citroen.wechat.api.token.TokenHolder;
import com.citroen.wechat.api.util.DateUtil;
import com.citroen.wechat.domain.FansGroup;
import com.citroen.wechat.domain.Material;
import com.citroen.wechat.domain.PublicNo;
import com.citroen.wechat.domain.PublicnoOnActive;
import com.citroen.wechat.domain.Site;
import com.citroen.wechat.domain.Template;
import com.citroen.wechat.domain.WechatMenu;
import com.citroen.wechat.exception.WechatException;
import com.citroen.wechat.util.FileUtil;
import org.springframework.transaction.annotation.Transactional;

/**
 * 素材服务类
 * @author 何海粟
 * @date2015年6月8日
 */
@Service
public class PublicNoService {
    private static Logger logger = Logger.getLogger(PublicNoService.class);
	@Autowired
	private MybaitsGenericDao<Long> mybaitsGenericDao;
	@Autowired
	private ConstantService constantService;
	@Autowired
	private SiteService siteService;
	@Autowired
	private TemplateService templateService;
	
	public Long save(Material entity) throws Exception {
		return mybaitsGenericDao.save(entity);
	}
	
	public List<Constant> getPublicNoType() throws LedpException{
		return constantService.findAll("publicno_type",1);
	}
	
	public PublicNo getCurrentActive(User user){
		PublicnoOnActive pa;
		try {
			pa = mybaitsGenericDao.find(PublicnoOnActive.class,"select * from t_publicno_onactive where user="+user.getId());
			if(pa!=null){
				PublicNo publicNo = pa.getPublicno();
				if(publicNo == null){
					return null;
				}
				if(publicNo.getAuthorized()==1){
					return pa.getPublicno();
				}
			}
		} catch (LedpException e) {
            logger.error("异常信息：" + e.getMessage());
		}
		return null;
	}
	
	public List<PublicNo> executeQuery(Map<String,String> params, Map<String,Integer> paginateParams) throws Exception{
		StringBuffer sql = new StringBuffer();
		Map<String,String> namedPrams = new HashMap<String,String>();
		sql.append("select pn.* from t_publicno pn where 1=1 ");
		if(StringUtils.isNotBlank(params.get("type"))){
			sql.append(" and pn.type=:type ");
			namedPrams.put("type", params.get("type"));
		}
		if(StringUtils.isNotBlank(params.get("alias_name"))){
			sql.append(" and pn.alias like :alias_name");
			namedPrams.put("alias_name", "%"+params.get("alias_name")+"%");
		}
		if(StringUtils.isNotBlank(params.get("user_name"))){
			sql.append(" and pn.user_name like :user_name");
			namedPrams.put("user_name", "%"+params.get("user_name")+"%");
		}
		if(StringUtils.isNotBlank(params.get("orgId"))){
			sql.append(" and pn.org =:orgId");
			namedPrams.put("orgId",params.get("orgId"));
		}
		if(StringUtils.isNotBlank(params.get("dealerId"))){
			sql.append(" and pn.dealer =:dealerId");
			namedPrams.put("dealerId",params.get("dealerId"));
		}
		if(StringUtils.isNotBlank(params.get("statusId"))){
			sql.append(" and pn.status =:statusId");
			namedPrams.put("statusId",params.get("statusId"));
		}
		String sortName = (String)params.get("sortName");
		String sortOrder = (String)params.get("sortOrder");
		if (StringUtils.isBlank(sortName)) {
			sortName = "pn.user_name";
		}
		if (StringUtils.isBlank(sortOrder)) {
			sortOrder = "desc";
		}
		sql.append(" order by ").append(sortName).append(" ").append(sortOrder);
		return mybaitsGenericDao.executeQuery(PublicNo.class,sql.toString(), namedPrams, paginateParams);
	}
	
	public int getTotalRow(Map<String,String> params) throws LedpException{
		StringBuffer sql = new StringBuffer();
		Map<String,String> namedPrams = new HashMap<String,String>();
		sql.append("select count(pn.id) count from t_publicno pn where 1=1 ");
		if(StringUtils.isNotBlank(params.get("type"))){
			sql.append(" and pn.type=:type ");
			namedPrams.put("type", params.get("type"));
		}
		if(StringUtils.isNotBlank(params.get("alias_name"))){
			sql.append(" and pn.alias like :alias_name");
			namedPrams.put("alias_name", "%"+params.get("alias_name")+"%");
		}
		if(StringUtils.isNotBlank(params.get("user_name"))){
			sql.append(" and pn.user_name like :user_name");
			namedPrams.put("user_name", "%"+params.get("user_name")+"%");
		}
		if(StringUtils.isNotBlank(params.get("orgId"))){
			sql.append(" and pn.org =:orgId");
			namedPrams.put("orgId",params.get("orgId"));
		}
		if(StringUtils.isNotBlank(params.get("dealerId"))){
			sql.append(" and pn.dealer =:dealerId");
			namedPrams.put("dealerId",params.get("dealerId"));
		}
		if(StringUtils.isNotBlank(params.get("statusId"))){
			sql.append(" and pn.status =:statusId");
			namedPrams.put("statusId",params.get("statusId"));
		}
		List<Map> list = mybaitsGenericDao.executeQuery(sql.toString(), namedPrams);
		if(list.isEmpty()){
			return 0;
		}
		Map map = list.get(0);
		return map.isEmpty()?0:Integer.parseInt(map.get("count").toString());
	}

	public void delete(Long id) throws Exception {
		try {
			mybaitsGenericDao.delete(PublicNo.class, id);
		} catch (Exception e) {
			throw new WechatException("删除失败");
		}
	}
	
	public boolean exist(String appid) throws Exception {
		Map map = mybaitsGenericDao.find("select count(id) count from t_publicno where authorized=1 and appid='"+appid+"'");
		if(map!=null && map.containsKey("count")){
			try{
				return Integer.parseInt(map.get("count").toString())>0;
			}catch(Exception e){
			}
		}
		return false;
	}

	public PublicNo get(Long id) throws Exception {
		return mybaitsGenericDao.get(PublicNo.class, id);
	}

	public PublicNo getByAppid(String appid) throws Exception {
		return mybaitsGenericDao.find(PublicNo.class,"select * from t_publicno where appid='"+appid+"'");
	}

	public void update(PublicNo publicNo) throws Exception {
		try {
			mybaitsGenericDao.update(publicNo);
		} catch (Exception e) {
			throw new WechatException("更新失败！");
		}
	}
    @Transactional
	public void inactive(Organization org, Dealer dealer) throws LedpException {
		if(org!=null){
			if(org.getLevel()==1){
				mybaitsGenericDao.execute("update t_publicno set org1_state=0");
			}else if(org.getLevel()==2){
				mybaitsGenericDao.execute("update t_publicno set org2_state=0 where org="+org.getId());
			}
		}
		if(dealer!=null){
			mybaitsGenericDao.execute("update t_publicno set dealer_state=0 where dealer="+dealer.getId());
		}
	}
	
	public void setActivePublicno(User user,PublicNo publicno) throws LedpException {
		mybaitsGenericDao.execute("delete from  t_publicno_onactive where user = "+user.getId());
		PublicnoOnActive pa = new PublicnoOnActive();
		pa.setUser(user);
		pa.setPublicno(publicno);
		mybaitsGenericDao.save(pa);
	}
	
	
	public void init(HttpServletRequest req) throws Exception{
		String authorization_code = req.getParameter("auth_code");
		String expires_in = req.getParameter("expires_in");
		
		/******************获取公众号授权信息**********************/
		logger.info("开始获取公众号授权信息，authorization_code="+authorization_code);
		Response<?> response = ApiAccessToken.getAuthorizerToken(authorization_code);
		logger.info("获取公众号授权信息，response="+response);
		if(response.getStatus()!=Response.SUCCESS){
			logger.error("公众号授权失败,获取公众号认证信息失败");
			LedpLogger.error(new User(1l),"公众号授权",LedpLogger.Operation.create,LedpLogger.Result.failure,"获取公众号认证信息失败");
			throw new Exception("公众号授权失败,获取公众号认证信息失败");
		}
		//得到认证信息
		String appid = response.get("authorizer_appid");
		String authorizer_refresh_token = response.get("authorizer_refresh_token");
		String funcscope_categorys = response.get("funcscope_categorys");
		/******************绑定众号到机构或网点**********************/
		//开通授权第一步输入authorizer_appid，授权页面跳转前将authorizer_appid和dealer/org存入ServletContext，
		Object object = req.getSession().getServletContext().getAttribute(appid);
		String app_secret = (String)req.getSession().getServletContext().getAttribute(appid+"app_secret");
		if(object==null){
			logger.error("公众号授权失败。接口返回AppId["+appid+"]没匹配到用户输入AppId");
			LedpLogger.error(new User(1l),"公众号授权",LedpLogger.Operation.create,LedpLogger.Result.failure,"公众号授权失败。接口返回AppId["+appid+"]没匹配到用户输入AppId");
			throw new Exception("公众号授权失败。接口返回AppId["+appid+"]没匹配到用户输入AppId");
		}
		req.getSession().getServletContext().removeAttribute(appid);
		req.getSession().getServletContext().removeAttribute(appid+"app_secret");
		/******************同步公众号账号信息**********************/
		Response<?> response2 = ApiAccessToken.getAuthorizerInfo(appid);
		if(response2.getStatus()!=Response.SUCCESS){
			logger.error("公众号授权失败,获取公众号账号信息失败");
			LedpLogger.error(new User(1l),"公众号授权",LedpLogger.Operation.create,LedpLogger.Result.failure,"获取公众号账号信息失败");
			throw new Exception("公众号授权失败,获取公众号账号信息失败");
		}
		//得到账号信息
		String nick_name = response2.get("nick_name");
		String head_img = response2.get("head_img");
		String service_type_info = response2.get("service_type_info");
		String verify_type_info = response2.get("verify_type_info");
		String user_name = response2.get("user_name");
		String alias = response2.get("alias");
		String qrcode_url = response2.get("qrcode_url");
		if(StringUtils.isBlank(head_img)){
			head_img = "/wechat/img/publicno_head.jpg";
		}
		//持久化公众号信息到数据库
		MybaitsGenericDao<Long> mybaitsGenericDao = SpringContextUtil.getTypeBean("mybaitsGenericDao");
		PublicNo publicNo = null;
		try {
			publicNo = mybaitsGenericDao.find(PublicNo.class,"select * from t_publicno where appid='"+appid+"'");
			if(publicNo == null){
				logger.info("第一次开通更新公众号，新建公众号记录，response="+response);
				publicNo = new PublicNo();
				publicNo.setStatus(new Constant(10101L));
				publicNo.setDateCreate(new Date());
				Long id = mybaitsGenericDao.save(publicNo);
				publicNo.setId(id);
			}
		} catch (LedpException e1) {}
		//保存认证信息
		publicNo.setAppid(appid);
		publicNo.setApp_secret(app_secret);
		publicNo.setAuthorization_code(authorization_code);
		publicNo.setAuthorizer_refresh_token(authorizer_refresh_token);
		publicNo.setFuncscope_categorys(funcscope_categorys);
		publicNo.setAuthorized(1);
		//publicNo.setAuthorized_time(new Date());
		//保存账号信息
		publicNo.setNick_name(nick_name);
		publicNo.setHead_img(head_img);
		publicNo.setService_type_info(service_type_info);
		publicNo.setVerify_type_info(verify_type_info);
		publicNo.setUser_name(user_name);
		publicNo.setAlias(StringUtils.isBlank(alias)?"（无）":alias);
		publicNo.setQrcode_url(qrcode_url);
		publicNo.setDateUpdate(new Date());
		//机构网点信息
		if(object instanceof Organization){
			Organization org = (Organization)object;
			publicNo.setOrg(org);
			publicNo.setDealer(null);
		}else{
			Dealer dealer = (Dealer)object;
			publicNo.setDealer((Dealer)object);
			publicNo.setOrg(dealer.getOrganization());
		}
		//微信号类型
		if("0".equals(service_type_info)||"1".equals(service_type_info)){
			publicNo.setType(new Constant(10001L));
		}
		if("2".equals(service_type_info)){
			publicNo.setType(new Constant(10002L));
		}
		logger.info("更新公众号信息，response="+response);
		try {
			logger.info("开始持久化公众号信息到数据库，response="+response);
			mybaitsGenericDao.update(publicNo);
			logger.info("持久化公众号信息到数据库完成，response="+response);
		} catch (LedpException e) {
			logger.error("持久化公众号信息到数据库失败，response="+response);
		}
		
		//刷新缓存中authorizer_access_token
		String access_token = response.get("authorizer_access_token");
		TokenHolder.setAuthorizerAccessToken(appid, access_token);
		
		Date dateUpdate = new Date();
		String date = DateUtil.convert(dateUpdate,"yyyy-MM-dd HH:mm:ss");
		
		/******************初始化公众号菜单**********************/
		try{
			List<WechatMenu> wechatMenus = mybaitsGenericDao.executeQuery(WechatMenu.class,"select * from t_wechat_menu where publicno=-1 and parent is null order by sort asc");
			List<WechatMenu> wechatMenusPublicno=mybaitsGenericDao.executeQuery(WechatMenu.class,"select * from t_wechat_menu where publicno="+publicNo.getId()+" order by sort asc");
			if(wechatMenusPublicno.size()<=0){
				/*********************自动建站*************************/
				String code=FileUtil.getNowDateH();
				HttpSession session = req.getSession();
				SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd HH:mm" );
				Site site=new Site();
				site.setName("微站");
				site.setCreateTime(new Date());
				site.setIsPublish(1);;
				site.setCreateUser((User)session.getAttribute("loginUser"));
				site.setBak2("授权自动建站");
				site.setCode(code);
				site.setBak1("/default.png");
				Dealer curDealer = (Dealer)session.getAttribute("loginDealer");
				site.setDealer(curDealer);
				if(curDealer != null){
					mybaitsGenericDao.execute("update t_site set ispublic=0 where dealer="+curDealer.getId());
				}
				
				long siteId = siteService.save(site);
				
				Site site1=siteService.siteInstance(siteId);
				Template template=templateService.templateInstance(1L);
				site=siteService.copyTemplate(site1, template);
				
				String siteCode = site.getCode();
				
				for(WechatMenu wm:wechatMenus){
					//复制一级菜单
					WechatMenu parentMenu = new WechatMenu();
					BeanUtils.copyProperties(wm,parentMenu);
					String url = parentMenu.getUrl();
					if(StringUtils.isNotBlank(url)){
						url = url.replace("default",siteCode);
						parentMenu.setUrl(url);
					}
					//设置菜单公众号
					parentMenu.setPublicNo(publicNo);
					//保存一级菜单
					long id = mybaitsGenericDao.save(parentMenu);
					parentMenu.setId(id);
					//复制二级菜单
					List<WechatMenu> children = mybaitsGenericDao.executeQuery(WechatMenu.class,"select * from t_wechat_menu where parent ="+wm.getId()+" order by sort asc");
					for(WechatMenu child:children){
						WechatMenu childMenu = new WechatMenu();
						BeanUtils.copyProperties(child,childMenu);
						String childUrl = childMenu.getUrl();
						if(StringUtils.isNotBlank(childUrl)){
							childUrl = childUrl.replace("default",siteCode);
							childMenu.setUrl(childUrl);
						}
						if("click".equals(child.getEvent())){
							//生成一个素材 
							childMenu.setMaterial(createMaterial(publicNo));
						}
						//设置菜单公众号
						childMenu.setPublicNo(publicNo);
						//设置父级菜单
						childMenu.setParent(parentMenu);
						mybaitsGenericDao.save(childMenu);
					}
				}
			}
		}catch(Exception e){
			String message = "公众号号："+publicNo.getNick_name()+"，异常信息："+e.getMessage();
            logger.error(message);
			LedpLogger.error("开通公众号同步菜单",LedpLogger.Operation.create,LedpLogger.Result.failure,message);
		}
		/************************同步化公众号粉丝组*****************************/
		logger.info("******************同步化公众号粉丝组**********************");
		downloadFansGroup(publicNo);
		/************************同步化公众号粉丝*****************************/
		logger.info("******************同步化公众号粉丝1**********************");
		downloadFans(publicNo);
		logger.info("******************同步化公众号粉丝2**********************");
		logger.info("******************同步公众号已有素材**********************");
		downloadMaterial(publicNo);
		logger.info("******************同步公众号已有素材**********************");
	}
	
	public void downloadFansGroup(PublicNo publicNo){
		/******************同步化公众号分组**********************/
		Date dateUpdate = new Date();
		String date = DateUtil.convert(dateUpdate,"yyyy-MM-dd HH:mm:ss");
		try {
			String access_token = TokenHolder.getAuthorizerAccessToken(publicNo.getAppid(),publicNo.getAuthorizer_refresh_token());
			List<Group> groups = ApiGroup.list(access_token);
			List<FansGroup> fansGroups = mybaitsGenericDao.executeQuery(FansGroup.class,"select * from t_fans_group where publicno="+publicNo.getId());
			int sort = 0;
			
			for(Group group:groups){
				FansGroup fansGroup=null;
				for(FansGroup g:fansGroups){
					if(group.getId().equals(g.getWechatgroupid())){
						fansGroup = g;
						break;
					}
				}
				if(fansGroup==null){
					fansGroup = new FansGroup();
					fansGroup.setName(group.getName());
					fansGroup.setPublicno(publicNo);
					fansGroup.setStatus(1);
					fansGroup.setWechatgroupid(group.getId());
					fansGroup.setSort(++sort);
					fansGroup.setDateUpdate(dateUpdate);
					mybaitsGenericDao.save(fansGroup);
				}else{
					fansGroup.setName(group.getName());
					fansGroup.setPublicno(publicNo);
					fansGroup.setStatus(1);
					fansGroup.setWechatgroupid(group.getId());
					fansGroup.setSort(++sort);
					fansGroup.setDateUpdate(dateUpdate);
					mybaitsGenericDao.update(fansGroup);
				}
			}
			mybaitsGenericDao.execute("update t_fans_group set status=0 where publicno="+publicNo.getId()+" and date_update<>'"+date+"'");
			
		} catch (Exception e) {
			String message = "公众号号："+publicNo.getNick_name()+"，异常信息："+e.getMessage();
            logger.error(message);
			LedpLogger.error("开通公众号同步分组",LedpLogger.Operation.create,LedpLogger.Result.failure,message);
		}
	}
	
	private Material createMaterial(PublicNo publicNo) throws LedpException{
		//文件保存目录路径
		PropertiyUtil propUtil = new PropertiyUtil("/wechat.properties");
		//磁盘路径
		String saveDir = propUtil.getString("material.dir")+"/images/pleaseWait.jpg";
		//图片访问路径url
		String saveUrl = propUtil.getString("material.url")+"/images/pleaseWait.jpg";
		String access_token = TokenHolder.getAuthorizerAccessToken(publicNo.getAppid(), publicNo.getAuthorizer_refresh_token());
		//上传到腾讯服务器，返回media_id
		String mediaId = "";
		Response rep =	ApiMaterial.upload(access_token, saveUrl);
		if(rep.getStatus() == Response.SUCCESS){
			mediaId = rep.get("media_id");
		}
		
		Material material = new Material();
		material.setUrl(saveUrl);
		material.setType(new Constant(9002L));
		material.setIsOpen(false);
		material.setCreateDate(new Date());
		material.setPublicNo(publicNo);
		material.setMediaId(mediaId);
		long id = mybaitsGenericDao.save(material);
		return mybaitsGenericDao.get(Material.class, id);
	}
	
	public void downloadFans(final PublicNo publicNo){
		new Thread(){
			public void run(){
				/******************同步化公众号粉丝**********************/
				logger.info("******************同步化公众号粉丝1**********************");
				try {
					String access_token = TokenHolder.getAuthorizerAccessToken(publicNo.getAppid(),publicNo.getAuthorizer_refresh_token());
					//批次获取粉丝数量，官方接口规定
					int fetchSize = 10000;
					//批次获取粉丝批次起始openid
					String next_openid = null;
					//批次获取粉丝列表,每次获取10000个
					List<com.citroen.wechat.api.model.Fans> apiFansList = null;
					Date dateUpdate = new Date();
					String date = DateUtil.convert(dateUpdate,"yyyy-MM-dd HH:mm:ss");
					do{
						//按批次获取粉丝
						Response<ArrayList<Fans>> res = ApiFans.list(access_token,next_openid);
						logger.info("Response<ArrayList<Fans>> res="+res);
						next_openid = res.get("next_openid");
						apiFansList = res.getObject();
						for(com.citroen.wechat.api.model.Fans f:apiFansList){
							com.citroen.wechat.api.model.Fans fs = null;
							//尝试重新获取次数
							int retryCount = 0;
							do{
								try{
									fs = ApiFans.getFansByOpenId(access_token,f.getOpenid());
									if(fs==null&& retryCount<5){
										long start = System.currentTimeMillis();
										while(System.currentTimeMillis()-start<2000){}
									}
								}catch(Exception e){
                                    logger.error("异常信息：" + e.getMessage());
								}
							}while(fs==null && retryCount++<5);
							//粉丝获取失败，跳过这个粉丝
							if(fs==null){
								continue;
							}
							
							logger.info("com.citroen.wechat.api.model.Fans fs="+fs);
							com.citroen.wechat.domain.Fans fans = mybaitsGenericDao.find(com.citroen.wechat.domain.Fans.class,"select * from t_fans where openid='"+fs.getOpenid()+"' and publicno="+publicNo.getId());
							logger.info("com.citroen.wechat.domain.Fans fans="+fans);
							FansGroup fansGroup = mybaitsGenericDao.find(FansGroup.class,"select * from t_fans_group where wechatgroupid='"+fs.getGroupid()+"' and publicno="+publicNo.getId());
							if(fans==null){
								fans = new com.citroen.wechat.domain.Fans();
								fans.setPublicNo(publicNo);
								fans.setOpenId(fs.getOpenid());
								fans.setUnionid(fs.getUnionid());
								fans.setNickName(fs.getNickname());
								fans.setSex(fs.getSex());
								fans.setCity(fs.getCity());
								fans.setCountry(fs.getCountry());
								fans.setProvince(fs.getProvince());
								fans.setLanguage(fs.getLanguage());
								fans.setHeadImgUrl(fs.getHeadimgurl());
								fans.setSubscribe(true);
								try{
									fans.setSubscribeTime(new Date(Long.parseLong(fs.getSubscribe_time())*1000));
								}catch(Exception e){
									fans.setSubscribeTime(new Date());
								}
								fans.setRemark("公众号开通，同步粉丝");
								fans.setWechatgroupid(fs.getGroupid());
								fans.setDateUpdate(dateUpdate);
								fans.setUnionid(fs.getUnionid());
								fans.setFansGroup(fansGroup);
								try{
									logger.info("mybaitsGenericDao.save(fans) fans1="+fans);
									com.citroen.wechat.domain.Fans _fans = mybaitsGenericDao.find(com.citroen.wechat.domain.Fans.class,"select * from t_fans where openId='"+fs.getOpenid()+"' and publicno="+publicNo.getId());
									if(_fans != null){
										continue;
									}
									mybaitsGenericDao.save(fans);
									logger.info("mybaitsGenericDao.save(fans) fans2="+fans);
								}catch(Exception e){
									String message = "公众号号："+publicNo.getNick_name()+"粉丝openid="+f.getOpenid()+"，异常信息："+e.getMessage();
                                    logger.error(message);
									LedpLogger.error("开通公众号同步粉丝",LedpLogger.Operation.create,LedpLogger.Result.failure,message);
									fans.setNickName("粉丝名");
									logger.info("mybaitsGenericDao.save(fans) fans3="+fans);
									com.citroen.wechat.domain.Fans _fans = mybaitsGenericDao.find(com.citroen.wechat.domain.Fans.class,"select * from t_fans where openId='"+fs.getOpenid()+"' and publicno="+publicNo.getId());
									if(_fans != null){
										continue;
									}
									mybaitsGenericDao.save(fans);
									logger.info("mybaitsGenericDao.save(fans) fans4="+fans);
								}
							}else{
								fans.setPublicNo(publicNo);
								fans.setOpenId(fs.getOpenid());
								fans.setNickName(fs.getNickname());
								fans.setSex(fs.getSex());
								fans.setCity(fs.getCity());
								fans.setCountry(fs.getCountry());
								fans.setProvince(fs.getProvince());
								fans.setLanguage(fs.getLanguage());
								fans.setHeadImgUrl(fs.getHeadimgurl());
								fans.setSubscribe(true);
								try{
									fans.setSubscribeTime(new Date(Long.parseLong(fs.getSubscribe_time())*1000));
								}catch(Exception e){
									fans.setSubscribeTime(new Date());
								}
								fans.setRemark("公众号开通，同步粉丝");
								fans.setWechatgroupid(fs.getGroupid());
								fans.setDateUpdate(dateUpdate);
								fans.setUnionid(fs.getUnionid());
								fans.setFansGroup(fansGroup);
								try{
									logger.info("mybaitsGenericDao.update(fans) fans1="+fans);
									mybaitsGenericDao.update(fans);
									logger.info("mybaitsGenericDao.update(fans) fans2="+fans);
								}catch(Exception e){
									String message = "公众号号："+publicNo.getNick_name()+"粉丝openid="+f.getOpenid()+"，异常信息："+e.getMessage();
									LedpLogger.error("开通公众号同步粉丝",LedpLogger.Operation.create,LedpLogger.Result.failure,message);
									fans.setNickName(fs.getOpenid());
									logger.info("mybaitsGenericDao.update(fans) fans3="+fans);
									mybaitsGenericDao.update(fans);
									logger.info("mybaitsGenericDao.update(fans) fans4="+fans);
								}
							}
						}
					}while(apiFansList.size()>=fetchSize);
					//删除粉丝
					logger.info("update t_fans set subscribe=0 where publicno="+publicNo.getId()+" and date_update<>'"+date+"'");
					mybaitsGenericDao.execute("update t_fans set subscribe=0 where publicno="+publicNo.getId()+" and date_update<>'"+date+"'");
					//更新粉丝所在组
					//mybaitsGenericDao.execute("update t_fans f,t_fans_group g set f.fans_group=g.id where f.wechatgroupid=g.wechatgroupid and f.publicno="+publicNo.getId());
				} catch (Exception e) {
					String message = "公众号号："+publicNo.getNick_name()+"，异常信息："+e.getMessage();
                    logger.error(message);
					LedpLogger.error("开通公众号同步粉丝",LedpLogger.Operation.create,LedpLogger.Result.failure,message);
				}
			}
		}.start();
	}
	
	public void downloadMaterial(final PublicNo publicNo){
		String access_token = TokenHolder.getAuthorizerAccessToken(publicNo.getAppid(),publicNo.getAuthorizer_refresh_token());
		final Response<?> resp = ApiMaterial.getTotal(access_token);
		logger.info("ApiMaterial.getTotal()="+resp);
		if(resp.getStatus()==Response.SUCCESS){
			new Thread(){
				public void run(){
					int total = 0;
					try{total = Integer.parseInt(resp.get("voice_count"));}catch(Exception e){}
					downloadMaterial(publicNo,"voice",0,total);
				}
			}.start();
			new Thread(){
				public void run(){
					int total = 0;
					try{total = Integer.parseInt(resp.get("image_count"));}catch(Exception e){}
					downloadMaterial(publicNo,"image",0,total);
					downloadMaterial(publicNo,"news",0,total);
				}
			}.start();
			new Thread(){
				public void run(){
					int total = 0;
					try{total = Integer.parseInt(resp.get("video_count"));}catch(Exception e){}
					downloadMaterial(publicNo,"video",0,total);
				}
			}.start();
		}
	}
	private void downloadMaterial(PublicNo publicNo,String type,int offset,int total){
		PropertiyUtil propUtil = new PropertiyUtil("/wechat.properties");
		//磁盘路径
		String saveDir = propUtil.getString("material.dir")+"/images/";
		//图片访问路径url
		String filePath = propUtil.getString("material.url")+"/images/";
		if(offset>=total){ return; }
		int max = 20;
		try {
			String access_token = TokenHolder.getAuthorizerAccessToken(publicNo.getAppid(),publicNo.getAuthorizer_refresh_token());
			List<com.citroen.wechat.api.model.Material> materials = ApiMaterial.getList(access_token, type, offset, max);
			for(com.citroen.wechat.api.model.Material m:materials){
				String filepath = "";
				if("news".equals(type) && !m.getListNews().isEmpty()){
					List<MaterialNews> mnews = m.getListNews();
					//已存在数据库的素材不需要同步
					Map existMaterial = mybaitsGenericDao.find("select id from t_material where media_id='"+m.getMedia_id()+"' and publicno="+publicNo.getId());
					if(existMaterial!=null && existMaterial.containsKey("id")){
						continue;
					}
					//保存不存在数据库的素材
					Material parent =null;
					for(int i=0;i<mnews.size();i++){
						
						MaterialNews newsItem = mnews.get(i);
						Material material = new Material();
						if(i==0){
							material.setMediaId(m.getMedia_id());
						}
						material.setPublicNo(publicNo);
						material.setType(new Constant(9003L));
						material.setTitle(newsItem.getTitle());		
						material.setThumbMediaId(newsItem.getThumb_media_id());
						material.setAuthor(newsItem.getAuthor());
						material.setDigest(newsItem.getDigest());
						material.setShowCoverPic(String.valueOf(newsItem.getShow_cover_pic()));
						material.setContent(newsItem.getContent());
						material.setContentUrl(newsItem.getUrl());
						
						Map map = mybaitsGenericDao.find("select url from t_material where media_id='"+newsItem.getThumb_media_id()+"' and publicno="+publicNo.getId());
						if(map!=null && map.get("url")!=null && StringUtils.isNotBlank(map.get("url").toString())){
							material.setUrl(map.get("url").toString());
						}else{
							String fileName = newsItem.getThumb_media_id()+".jpg";
							Response response = ApiMaterial.download(access_token, newsItem.getThumb_media_id(), saveDir+fileName);
							material.setUrl(filePath+fileName);
						}
						
						material.setOriginalUrl(newsItem.getContent_source_url());
						material.setParent(parent);
						try{
							/*existMaterial = mybaitsGenericDao.find("select id from t_material where media_id='"+m.getMedia_id()+"' and publicno="+publicNo.getId());
							if(existMaterial!=null && existMaterial.containsKey("id")){
								continue;
							}*/
							long id = mybaitsGenericDao.save(material);
							if(i==0){
								parent = new Material();
								parent.setId(id);
							}
						}catch(Exception e){
                            logger.error("异常信息：" + e.getMessage());
						}
					}
				}else{
					//已存在数据库的素材不需要同步
					Map existMaterial = mybaitsGenericDao.find("select id from t_material where media_id='"+m.getMedia_id()+"' and publicno="+publicNo.getId());
					if(existMaterial!=null && existMaterial.containsKey("id")){
						continue;
					}
					//保存不存在数据库的素材
					Material material = new Material();
					/*
						9002	图片	picture
						9003	图文	news
						9004	音频	voice
						9005	视频	video
					 * */
					if("voice".equals(type)){
						material.setType(new Constant(9004L));
					}
					if("video".equals(type)){
						material.setType(new Constant(9005L));
					}
					if("image".equals(type)){
						material.setType(new Constant(9002L));
						if(StringUtils.isBlank(m.getUrl())){
							String fileName = m.getMedia_id()+".jpg";
							Response response = ApiMaterial.download(access_token, m.getMedia_id(), saveDir+fileName);
							material.setUrl(filePath+fileName);
						}
					}
					
					material.setPublicNo(publicNo);
					material.setFilepath(filepath);
					if(StringUtils.isNotBlank(m.getUrl())){
						material.setUrl(m.getUrl());
					}
					material.setContent(m.getContent());
					material.setCreateDate(new Date());
					material.setTitle(m.getName());
					material.setMediaId(m.getMedia_id());
					try{
						existMaterial = mybaitsGenericDao.find("select id from t_material where media_id='"+m.getMedia_id()+"' and publicno="+publicNo.getId());
						if(existMaterial!=null && existMaterial.containsKey("id")){
							continue;
						}
						mybaitsGenericDao.save(material);
					}catch(Exception e){}
				}
			}
			downloadMaterial(publicNo,type,offset+max,total);
		} catch (Exception e) {
			logger.error(e);
			LedpLogger.error("开通公众号同步素材",LedpLogger.Operation.create,LedpLogger.Result.failure,e.getMessage());
		}
	}
}
