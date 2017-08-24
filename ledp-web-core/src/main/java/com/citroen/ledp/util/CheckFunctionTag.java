package com.citroen.ledp.util;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.alibaba.druid.util.StringUtils;

import freemarker.core.Environment;
import freemarker.template.SimpleSequence;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
/**
 * freemarker自定义标签
 * @author 郭威
 *
 */
public class CheckFunctionTag implements TemplateDirectiveModel {
	//验证权限控制按钮链接显示
	public void execute(Environment env, Map params, TemplateModel[] model1,
			TemplateDirectiveBody body) throws TemplateException, IOException {
		//用户权限集合
		List<String> permissions =  ((SimpleSequence)getRequiredParam(params,"permissionCodes")).toList();
		//单个权限Code
		String permissionCode = getRequiredParam(params,"permissionCode").toString();
		for (String code:permissions) {
			if(permissionCode.equals(code)){ //权限匹配
				 body.render(env.getOut()); 
			}
		}
	}
	//参数取值
	private static Object getRequiredParam(Map params,String key) throws TemplateException {  
	        Object value = params.get(key);  
	        if(value == null || StringUtils.isEmpty(value.toString())) {  
	            throw new TemplateModelException("not found required parameter:"+key+" for directive");  
	        }  
	        return value;  
	    }  

}
