package com.citroen.wechat.service;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.citroen.ledp.dao.mybaits.MybaitsGenericDao;
import com.citroen.ledp.exception.LedpException;
import com.citroen.ledp.service.ConstantService;
import com.citroen.wechat.domain.Template;
import com.citroen.wechat.domain.TemplatePage;

/** 
 * @ClassName: TemplateService 
 * @Description: TODO(的大卫杜夫) 
 * @author 杨少波
 * @date 2015年6月15日 下午7:43:26 
 * 
 */
@Service
public class TemplateService {
	@Autowired
	private MybaitsGenericDao<Long> mybaitsGenericDao;
	@Autowired
	private ConstantService constantService;
	
	public Template templateInstance(Long templateId) throws LedpException {
		return mybaitsGenericDao.get(Template.class, templateId);
	}
	
	/**
	 * 获取模板列表
	 * @return
	 * @throws Exception
	 */
	public List<Template> getTemplates() throws Exception {
		String sql = "select * from t_template t where t.status=1 order by t.id asc ";
		List<Template> templates = mybaitsGenericDao.executeQuery(Template.class, sql);
		if(CollectionUtils.isNotEmpty(templates)){
			for(Template template : templates){
				sql = "select * from t_template_page p where p.template="+template.getId()+" and visible=1 order by sort asc";
				List<TemplatePage> pages = mybaitsGenericDao.executeQuery(TemplatePage.class, sql);
				template.setPages(pages);
			}
		}
		return templates;
	}
	
	/**
	 * 新建页面
	 * @param templateId
	 * @return
	 * @throws Exception
	 */
	public TemplatePage getNewPage(long templateId) throws Exception {
		String sql = "select * from t_template_page p where p.template="+templateId+" and visible=0 order by sort asc";
		List<TemplatePage> pages = mybaitsGenericDao.executeQuery(TemplatePage.class, sql);
		if(CollectionUtils.isNotEmpty(pages)){
			return pages.get(0);
		}
		return null;
	}
}
