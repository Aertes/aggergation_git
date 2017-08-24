package com.citroen.ledp.tag;

import java.util.List;

import javax.servlet.jsp.tagext.TagSupport;

import com.citroen.ledp.domain.Constant;
import com.citroen.ledp.service.ConstantService;
import com.citroen.ledp.util.SpringContextUtil;

@SuppressWarnings({ "serial" })
public class ConstantSelectorTag extends TagSupport {
	private String id;
	private String type;
	private String name;
	private String style = "searchselect";
	private String attrs;
	private long defaultValue;

	public int doEndTag() {
		ConstantService constantService = SpringContextUtil.getTypeBean("constantServiceImpl");
		try {
			List<Constant> constants = constantService.findAll(type);
			StringBuilder sb = new StringBuilder();
			sb.append("<select name='" + name + "' class='" + style + "' id='" + id + "' "+ attrs +">");
			sb.append("<option value=''>全部</option>");
			for (Constant c : constants) {
				if (defaultValue == c.getId()) {
					sb.append("<option value='" + c.getId() + "' selected>" + c.getName() + "</option>");
				} else {
					sb.append("<option value='" + c.getId() + "'>" + c.getName() + "</option>");
				}
			}
			sb.append("</select>");
			pageContext.getOut().write(sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return EVAL_PAGE;
	}

	public void setId(String id) {
		this.id = id;
	};

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

	public void setAttrs(String attrs) {
		this.attrs = attrs;
	}

}
