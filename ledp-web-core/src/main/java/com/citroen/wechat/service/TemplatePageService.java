package com.citroen.wechat.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.citroen.ledp.dao.mybaits.MybaitsGenericDao;
import com.citroen.ledp.exception.LedpException;
import com.citroen.ledp.service.ConstantService;
import com.citroen.wechat.domain.Template;
import com.citroen.wechat.domain.TemplatePage;

/** 
 * @ClassName: TemplatePageService 
 * @Description: TODO(的大卫杜夫) 
 * @author 杨少波
 * @date 2015年6月15日 下午8:11:54 
 * 
 */
@Service
public class TemplatePageService {
	@Autowired
	private MybaitsGenericDao<Long> mybaitsGenericDao;
	@Autowired
	private ConstantService constantService;
	
	public TemplatePage getTemplatePage(Long templateId) throws LedpException{
		String sql="select * from t_template_page where ishome=true and template="+templateId;
		return mybaitsGenericDao.find(TemplatePage.class,sql);
	}
	public List<TemplatePage> getTemplatePages(Long templateId) throws LedpException{
		String sql="select * from t_template_page where visible=1 and template="+templateId+" order by id asc";
		return mybaitsGenericDao.executeQuery(TemplatePage.class,sql);
	}
	public TemplatePage getTemplatePageN(Long templateId) throws LedpException{
		String sql="select * from t_template_page where visible=0 and template="+templateId+" order by id asc";
		return mybaitsGenericDao.find(TemplatePage.class,sql);
	}
}
