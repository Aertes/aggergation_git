package com.citroen.wechat.service;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.citroen.ledp.dao.mybaits.MybaitsGenericDao;
import com.citroen.ledp.exception.LedpException;
import com.citroen.ledp.service.ConstantService;
import com.citroen.ledp.util.MapUtil;
import com.citroen.ledp.util.PropertiyUtil;
import com.citroen.wechat.domain.Site;
import com.citroen.wechat.domain.SitePage;
import com.citroen.wechat.domain.Template;
import com.citroen.wechat.domain.TemplatePage;
import com.citroen.wechat.util.FileUtil;

/** 
 * @ClassName: SiteService 
 * @Description: TODO(的大卫杜夫) 
 * @author 杨少波
 * @date 2015年6月15日 下午8:30:15 
 * 
 */
@Service
public class SiteService {
    private static Logger logger = Logger.getLogger(SiteService.class);
	@Autowired
	private MybaitsGenericDao<Long> mybaitsGenericDao;
	@Autowired
	private ConstantService constantService;
	@Autowired
	private TemplatePageService templatePageService;
	@Autowired
	private SitePageService sitePageService;
	
	public Long save(Site site) throws LedpException {
		return mybaitsGenericDao.save(site);
	}
	public Site siteInstance(Long siteId) throws LedpException {
		return mybaitsGenericDao.get(Site.class, siteId);
	}
	public Site getSite(Long campaignd) throws LedpException {
		String sql="select * from t_site where campaign="+campaignd;
		return mybaitsGenericDao.find(Site.class, sql);
	}
	public void deleteSiteInstance(Long siteId) throws LedpException {
		 mybaitsGenericDao.delete(Site.class, siteId);
	}
	public List<Site> executeQuery(Map params) throws LedpException {
		Map<String, Object> condition = getCondition(params);
		Map namedParams = (Map) condition.get("namedParams");
		String sql = "select * from t_site where campaign is null " + condition.get("namedSql");
		sql +=" order by date_create desc";
		Map paginateParams = new HashMap();
		int pageNumber = (Integer) params.get("pageNumber");
		int pageSize = (Integer) params.get("pageSize");
		int offset = (pageNumber - 1) * pageSize;
		paginateParams.put("max", pageSize);
		paginateParams.put("offset", offset);
		return mybaitsGenericDao.executeQuery(Site.class, sql, namedParams, paginateParams);
		
	}
	public int getTotalRow(Map params) throws LedpException {
		Map<String, Object> condition = getCondition(params);
		Map namedParams = (Map) condition.get("namedParams");
		String sql = "select count(id) count from t_site where campaign is null " + condition.get("namedSql");
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
					namedSql.append(" and (exists(select 1 from t_dealer d where id= dealer and id in(select id from t_dealer where organization =:orgId )))");
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
			namedSql.append(" and date_create >=:date1 ");
		}
		if(!mapUtil.isBlank("parameter[endTime]")){
			String date2 = (String) params.get("parameter[endTime]");
			namedParams.put("date2",date2);
			namedSql.append(" and date_create <=:date2 ");
		}
		
	
		Map<String, Object> rs = new HashMap<String, Object>();
		rs.put("namedSql", namedSql);
		rs.put("namedParams", namedParams);
		return rs;
	}
	
	public Site copyTemplate(Site site,Template template) throws LedpException{
		TemplatePage templatePage=templatePageService.getTemplatePage(template.getId());
		PropertiyUtil propUtil = new PropertiyUtil("/wechat.properties");
		String saveDir = propUtil.getString("site.dir");
		String saveDir1 = propUtil.getString("site.url");
		
		site.setTemplate(template);
		site.setUrl(saveDir+"/"+site.getCode());
		mybaitsGenericDao.update(site);
		site=siteInstance(site.getId());
		
		FileUtil.copy(template.getUrl(), saveDir+"/"+site.getCode()+"/",site.getCode(),"site");
		
		List<TemplatePage> templatePages=templatePageService.getTemplatePages(template.getId());
		for(int i=0;i<templatePages.size();i++){
			SitePage sitePage=new SitePage();
			sitePage.setFilePath(templatePages.get(i).getThumbPicUrl());
			sitePage.setIsHome(templatePages.get(i).getIsHome());
			sitePage.setName(templatePages.get(i).getName());
			sitePage.setSite(site);
			sitePage.setUrl("/"+templatePages.get(i).getUrl());
			
			TemplatePage tempPage = templatePages.get(i);
			
			String html = FileUtil.readFile(template.getUrl()+tempPage.getUrl().split(".html")[0]+".edit");
			html=html.replaceAll("href=\"css", "href=\""+saveDir1+"/"+site.getCode()+"/css");
		    html=html.replaceAll("src=\"js", "src=\""+saveDir1+"/"+site.getCode()+"/js");
		    html=html.replaceAll("src=\"image", "src=\""+saveDir1+"/"+site.getCode()+"/image");
			sitePage.setHtml(html);
			long id=sitePageService.save(sitePage);
			FileUtil.updateFile(saveDir+"/"+site.getCode()+"/"+templatePages.get(i).getUrl(), id,site.getCode(),"site");
			/**
			 * 说明：以前模板编辑时是从数据库读取html字符串在前台编辑
			 * 现在修改为读取文件，暂时保留数据的，以保证以前创建的微站能正常编辑
			 */
			FileUtil.writeOverRideFile(saveDir+"/"+site.getCode()+"/"+templatePages.get(i).getUrl().split(".html")[0]+".edit",html);
		}
		
		return site;
	}
	
	public void buildSite(long publicNoId){
		
	}
}
