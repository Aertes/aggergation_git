package com.citroen.ledp.tag;

import java.util.List;

import javax.servlet.jsp.tagext.TagSupport;

import com.citroen.ledp.domain.Constant;
import com.citroen.ledp.service.ConstantService;
import com.citroen.ledp.util.SpringContextUtil;

@SuppressWarnings({"serial" })
public class ConstantRadioTag extends TagSupport{
	private String type;
	private String name;
	private String style="mediatest";
	private long  defaultValue;
	public int doEndTag(){
		ConstantService constantService = SpringContextUtil.getTypeBean("constantService");
		try {
			List<Constant> constants = constantService.findAll(type);
			StringBuilder sb = new StringBuilder();
			for(Constant c : constants){
				if(defaultValue==c.getId()){
					sb.append("<input class="+style+" type='radio' name='"+name+"' value='"+c.getId()+"' checked/>");
				}else{
					sb.append("<input class="+style+" type='radio' name='"+name+"' value='"+c.getId()+"'/>");
				}
				sb.append("<span>"+c.getName()+"</span>");
			}
			pageContext.getOut().write(sb.toString());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return EVAL_PAGE;
	}

	public void setDefaultValue(long defaultValue) {
		this.defaultValue = defaultValue;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setStyle(String style) {
		this.style = style;
	}
	public void setType(String type) {
		this.type = type;
	}
	
}
