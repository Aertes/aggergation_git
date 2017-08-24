package com.citroen.ledp.tag;

import java.util.List;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

@SuppressWarnings({ "unchecked", "serial" })
public class PermissionTag extends TagSupport{
	/* 权限代码*/
	private String action;
	
	public int doStartTag() throws JspException {
		HttpSession session = pageContext.getSession();
		List<String> actions = (List<String>)session.getAttribute("permissions");
		if(actions==null || !actions.contains(action)){
			return EVAL_BODY_INCLUDE;
		}
		return EVAL_BODY_INCLUDE;
	}
	
	public int doEndTag(){
		return EVAL_PAGE;
	}
	public void setAction(String action) {
		this.action = action;
	}
}
