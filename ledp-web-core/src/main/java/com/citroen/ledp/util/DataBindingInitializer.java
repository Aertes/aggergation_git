/**
 * @Title: DateBindingInitializer.java
 * @Package com.cyberoller.demo.util
 * @Description: TODO(用一句话描述该文件做什么)
 * @author qihong.liao@cyberoller.com
 * @date 2015年1月25日 下午9:45:54
 * @version V1.0
 */
package com.citroen.ledp.util;

import java.beans.PropertyEditorSupport;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebBindingInitializer;
import org.springframework.web.context.request.WebRequest;

import com.sun.org.apache.xerces.internal.impl.xpath.regex.ParseException;
/**
 * @Title: DateBindingInitializer.java
 * @Package com.cyberoller.demo.util
 * @Description: TODO(用一句话描述该文件做什么)
 * @author 廖启洪
 * @date 2015年1月25日 下午9:45:54
 * @version V1.0
 */
public class DataBindingInitializer implements WebBindingInitializer {

	/* (非Javadoc注释)
	 * <p>Title: initBinder</p>
	 * <p>Description: </p>
	 * @param arg0
	 * @param arg1
	 * @see org.springframework.web.bind.support.WebBindingInitializer#initBinder(org.springframework.web.bind.WebDataBinder, org.springframework.web.context.request.WebRequest)
	 */
	public void initBinder(WebDataBinder binder, WebRequest request) {
	    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	    dateFormat.setLenient(false);
	    SimpleDateFormat datetimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    datetimeFormat.setLenient(false);
	    binder.registerCustomEditor(java.util.Date.class, new CustomDateEditor(dateFormat, true));
	    binder.registerCustomEditor(java.sql.Timestamp.class, new CustomTimestampEditor(datetimeFormat, true));
	}

	class CustomTimestampEditor extends PropertyEditorSupport {
		private final SimpleDateFormat dateFormat;
		private final boolean allowEmpty;
		private final int exactDateLength;

		public CustomTimestampEditor(SimpleDateFormat dateFormat, boolean allowEmpty) {
		    this.dateFormat = dateFormat;
		    this.allowEmpty = allowEmpty;
		    this.exactDateLength = -1;
		}

		public CustomTimestampEditor(SimpleDateFormat dateFormat,
		        boolean allowEmpty, int exactDateLength) {
		    this.dateFormat = dateFormat;
		    this.allowEmpty = allowEmpty;
		    this.exactDateLength = exactDateLength;
		}

		public void setAsText(String text) throws IllegalArgumentException {
		    if ((this.allowEmpty) && (!(StringUtils.hasText(text)))) {
		        setValue(null);
		    } else {
		        if ((text != null) && (this.exactDateLength >= 0)
		                && (text.length() != this.exactDateLength)) {
		            throw new IllegalArgumentException(
		                    "Could not parse date: it is not exactly"
		                            + this.exactDateLength + "characters long");
		        }
		        try {
		            setValue(new Timestamp(this.dateFormat.parse(text).getTime()));
		        } catch (ParseException ex) {
		            throw new IllegalArgumentException("Could not parse date: "
		                    + ex.getMessage(), ex);
		        } catch (java.text.ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		        
		    }
		}

		public String getAsText() {
		    Timestamp value = (Timestamp) getValue();
		    return ((value != null) ? this.dateFormat.format(value) : "");
		}
		}
}
