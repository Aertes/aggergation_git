package com.citroen.wechat.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.citroen.ledp.dao.mybaits.MybaitsGenericDao;
import com.citroen.ledp.exception.LedpException;
import com.citroen.ledp.service.ConstantService;
import com.citroen.wechat.domain.PublicNo;
import com.citroen.wechat.domain.Site;
import com.citroen.wechat.domain.SitePage;

/** 
 * @ClassName: SitePageService 
 * @Description: TODO(的大卫杜夫) 
 * @author 杨少波
 * @date 2015年6月15日 下午8:33:09 
 * 
 */
@Service
public class SitePageService {
	@Autowired
	private MybaitsGenericDao<Long> mybaitsGenericDao;
	@Autowired
	private ConstantService constantService;
	@Autowired
	private SiteService siteService;
	
	public Long save(SitePage sitePage) throws LedpException {
		return mybaitsGenericDao.save(sitePage);
	}
	public SitePage siteInstance(Long sitePageId) throws LedpException {
		return mybaitsGenericDao.get(SitePage.class, sitePageId);
	}
	public List<SitePage> getSitePages(Long siteId) throws LedpException{
		String sql="select * from t_site_page where site="+siteId+" order by id asc";
		return mybaitsGenericDao.executeQuery(SitePage.class,sql);
	}
	public List<SitePage> getSitePages(Long siteId,Long sitePageId) throws LedpException{
		String sql="select * from t_site_page where site="+siteId+" and id!="+sitePageId+" order by id asc";
		return mybaitsGenericDao.executeQuery(SitePage.class,sql);
	}
	public SitePage sitePageInstance(Long sitePageId) throws LedpException {
		return mybaitsGenericDao.get(SitePage.class, sitePageId);
	}
	public void deletesitePageInstance(Long sitePageId) throws LedpException {
		 mybaitsGenericDao.delete(SitePage.class, sitePageId);
	}
	public SitePage getSitePage(Long siteId) throws LedpException {
		String sql="select * from t_site_page where ishome=true and site="+siteId;
		return mybaitsGenericDao.find(SitePage.class, sql);
	}
	public 	Map<String,String> getNameValue(Long siteId) throws LedpException {
		Map<String,String> map=new HashMap<String,String>();
		Site site=siteService.siteInstance(siteId);
		List<SitePage> sitePages=getSitePages(siteId);
		for(int i=0;i<sitePages.size();i++){
			if(!"".equals(sitePages.get(i).getName())){
				map.put(sitePages.get(i).getName(), sitePages.get(i).getUrl().substring(1));
			}
		}
		return map;
	}
	
	/**
	 * 通过页面id获取公众号信息
	 * @param pageId
	 * @return
	 * @throws LedpException
	 */
	public PublicNo getPublicNoByPageId(Long pageId) throws LedpException {
		StringBuffer sb = new StringBuffer();
		sb.append("select p.* from t_site_page sp ")
		  .append("inner join t_site s on s.id = sp.site ")
		  .append("left join t_publicno p on p.id = s.publicno")
		  .append("where sp.id=").append(pageId);
		
		return mybaitsGenericDao.find(PublicNo.class, sb.toString());
	}
}
