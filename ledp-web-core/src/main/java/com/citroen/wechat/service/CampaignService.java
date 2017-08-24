package com.citroen.wechat.service;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.citroen.ledp.dao.mybaits.MybaitsGenericDao;
import com.citroen.ledp.exception.LedpException;
import com.citroen.ledp.service.ConstantService;
import com.citroen.ledp.util.DateUtil;
import com.citroen.ledp.util.MapUtil;
import com.citroen.ledp.util.PropertiyUtil;
import com.citroen.wechat.domain.Campaign;
import com.citroen.wechat.domain.Site;
import com.citroen.wechat.domain.SitePage;
import com.citroen.wechat.domain.Template;
import com.citroen.wechat.domain.TemplatePage;
import com.citroen.wechat.util.FileUtil;
import org.springframework.transaction.annotation.Transactional;

/** 
 * @ClassName: CampaignService 
 * @Description: TODO(的大卫杜夫) 
 * @author 杨少波
 * @date 2015年6月12日 下午2:17:30 
 * 
 */
@Service
public class CampaignService {
    private static Logger logger = Logger.getLogger(CampaignService.class);
	@Autowired
	private MybaitsGenericDao<Long> mybaitsGenericDao;
	@Autowired
	private ConstantService constantService;
	@Autowired
	private TemplatePageService templatePageService;
	@Autowired
	private SiteService siteService;
	@Autowired
	private SitePageService sitePageService;
	
	public List<Campaign> executeQuery(Map params) throws LedpException {
		Map<String, Object> condition = getCondition(params);
		Map namedParams = (Map) condition.get("namedParams");
		String sql = "select * from t_campaign where 1=1" + condition.get("namedSql");
		sql +=" order by begindate desc";
		Map paginateParams = new HashMap();
		int pageNumber = (Integer) params.get("pageNumber");
		int pageSize = (Integer) params.get("pageSize");
		int offset = (pageNumber - 1) * pageSize;
		paginateParams.put("max", pageSize);
		paginateParams.put("offset", offset);
		return mybaitsGenericDao.executeQuery(Campaign.class, sql, namedParams, paginateParams);
		
	}
	public int getTotalRow(Map params) throws LedpException {
		Map<String, Object> condition = getCondition(params);
		Map namedParams = (Map) condition.get("namedParams");
		String sql = "select count(id) count from t_campaign where 1=1" + condition.get("namedSql");
		List<Map> list = mybaitsGenericDao.executeQuery(sql, namedParams);
		if (list.isEmpty()) {
			return 0;
		}
		Map map = list.get(0);
		return map.isEmpty() ? 0 : Integer.parseInt(map.get("count").toString());
	}

	private Map<String, Object> getCondition(Map<String, Object> params) {
		MapUtil<String, Object> mapUtil = new MapUtil<String, Object>(params);
		Map<String, Object> namedParams = new HashMap<String, Object>();

		StringBuilder namedSql = new StringBuilder();
		if(params.get("type_user").equals("dealer")){
			Long dealerId=(Long) params.get("userLogin");
			namedParams.put("dealerId", dealerId);
			namedSql.append(" and dealer =:dealerId");
		}else{
			if(!params.get("userLogin").toString().equals("1")){
				if(mapUtil.isBlank("parameter[dealer]")){
					Long orgId=(Long) params.get("userLogin");
					namedParams.put("orgId", orgId);
					namedSql.append(" and (exists(select 1 from t_dealer where id= dealer and id in(select id from t_dealer where organization =:orgId )) or org=:orgId )");
				}else{
					Long dealerId=Long.valueOf((String) params.get("parameter[dealer]")) ;
					namedParams.put("dealerId", dealerId);
					namedSql.append(" and dealer =:dealerId");
				}
			}else{
				if(!mapUtil.isBlank("parameter[dealer]")){
					Long dealerId=Long.valueOf((String) params.get("parameter[dealer]")) ;
					namedParams.put("dealerId", dealerId);
					namedSql.append(" and dealer =:dealerId");
				}
			}
		}
		if(!mapUtil.isBlank("parameter[name]")){
			String name;
			try {
				name = new String(((String)params.get("parameter[name]")).getBytes("ISO-8859-1"),"UTF-8" );
				namedParams.put("name", "%" + name + "%");
				namedSql.append(" and name like :name ");
			} catch (UnsupportedEncodingException e) {
				logger.error("异常信息：" + e.getMessage());
			}
			
		}
		if(!mapUtil.isBlank("parameter[startTime]")){
			String date1 = (String) params.get("parameter[startTime]");
			namedParams.put("date1",date1);
			namedSql.append(" and begindate >=:date1 ");
		}
		if(!mapUtil.isBlank("parameter[endTime]")){
			String date2 = (String) params.get("parameter[endTime]");
			namedParams.put("date2",date2);
			namedSql.append(" and begindate <=:date2 ");
		}
		
		if((!mapUtil.isBlank("parameter[unbegin]") && !mapUtil.isBlank("parameter[in]") && !mapUtil.isBlank("parameter[end]")) || !(mapUtil.isBlank("parameter[unbegin]")&& mapUtil.isBlank("parameter[in]") && mapUtil.isBlank("parameter[end]"))){
			namedParams.put("date2",DateUtil.format(new Date(),"yyyy-MM-dd HH:mm"));
			if(!mapUtil.isBlank("parameter[unbegin]")){
				namedSql.append(" and (begindate >:date2 ");
				if(!mapUtil.isBlank("parameter[in]")){
					namedParams.put("date1",DateUtil.format(new Date(),"yyyy-MM-dd HH:mm"));
					namedSql.append(" or (begindate <=:date2 and enddate >:date1 ) ) ");
				}else if(!mapUtil.isBlank("parameter[end]")){
					namedParams.put("date1",DateUtil.format(new Date(),"yyyy-MM-dd HH:mm"));
					namedSql.append(" or  enddate <=:date1 ) ");
				}else{
					namedSql.append(" ) ");
				}
			}else if(!mapUtil.isBlank("parameter[in]")){
				namedSql.append(" and (begindate <=:date2 and enddate >:date2 ");
				if(!mapUtil.isBlank("parameter[end]")){
					namedParams.put("date1",DateUtil.format(new Date(),"yyyy-MM-dd HH:mm"));
					namedSql.append(" or  enddate <=:date1 ) ");
				}else{
					namedSql.append(" ) ");
				}
			}else{
				namedSql.append(" and  enddate <=:date2 ");
			}
		}
		Map<String, Object> rs = new HashMap<String, Object>();
		rs.put("namedSql", namedSql);
		rs.put("namedParams", namedParams);
		return rs;
	}
	public Long save(Campaign campaign) throws LedpException {
		return mybaitsGenericDao.save(campaign);
	}
	public Campaign campaignInstance(Long campaignId) throws LedpException {
		return mybaitsGenericDao.get(Campaign.class, campaignId);
	}
	public void deleteCampaignInstance(Long campaignId) throws LedpException {
		 mybaitsGenericDao.delete(Campaign.class, campaignId);
	}
	public List<Template> getTemplates(Map params) throws LedpException{
		String sql="select * from t_template where status=1 and type="+params.get("type")+" order by id asc";
		return mybaitsGenericDao.executeQuery(Template.class,sql);
	}

    @Transactional
	public Site copyTemplate(Campaign campaign,Template template) throws LedpException{
		TemplatePage templatePage=templatePageService.getTemplatePage(template.getId());
		PropertiyUtil propUtil = new PropertiyUtil("/wechat.properties");
		String saveDir = propUtil.getString("campaign.dir");
		String saveDir1 = propUtil.getString("campaign.url");
		Site site=new Site();
		site.setCampaign(campaign);
		site.setIsPublish(1);
		site.setTemplate(template);
		site.setUrl(saveDir+"/"+campaign.getCode());
		long siteId=siteService.save(site);
		site=siteService.siteInstance(siteId);
		
		FileUtil.copy(template.getUrl(), saveDir+"/"+campaign.getCode()+"/",campaign.getCode(),"campaign");
		
		List<TemplatePage> templatePages=templatePageService.getTemplatePages(template.getId());
		for(int i=0;i<templatePages.size();i++){
			SitePage sitePage=new SitePage();
			sitePage.setFilePath(templatePages.get(i).getThumbPicUrl());
			sitePage.setIsHome(templatePages.get(i).getIsHome());
			sitePage.setName(templatePages.get(i).getName());
			sitePage.setSite(site);
			sitePage.setUrl("/"+templatePages.get(i).getUrl());
			
			String html=FileUtil.readFile(template.getUrl()+sitePage.getUrl().split(".html")[0]+".edit");
			html=html.replaceAll("href=\"css", "href=\""+saveDir1+"/"+campaign.getCode()+"/css");
		    html=html.replaceAll("src=\"js", "src=\""+saveDir1+"/"+campaign.getCode()+"/js");
		    html=html.replaceAll("src=\"image", "src=\""+saveDir1+"/"+campaign.getCode()+"/image");
			
			sitePage.setHtml(html);
			long id=sitePageService.save(sitePage);
			FileUtil.updateFile(saveDir+"/"+campaign.getCode()+"/"+templatePages.get(i).getUrl(),id,campaign.getCode(),"campaign");
			/**
			 * 说明：以前模板编辑时是从数据库读取html字符串在前台编辑
			 * 现在修改为读取文件，暂时保留数据的，以保证以前创建的微站能正常编辑
			 */
			FileUtil.writeOverRideFile(saveDir+"/"+campaign.getCode()+"/"+templatePages.get(i).getUrl().split(".html")[0]+".edit",html);
		}
		
		return site;
	}
}
