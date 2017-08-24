package com.citroen.ledp.interceptor;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import com.citroen.ledp.domain.User;
public class DxSessionListener implements HttpSessionListener {
	
	public void sessionCreated(HttpSessionEvent event) {
	}

	public void sessionDestroyed(HttpSessionEvent event) {
		HttpSession    session = event.getSession();
		ServletContext context = session.getServletContext();
		User user = (User)session.getAttribute("loginUser");
		if(user!=null){
			context.removeAttribute(user.getId().toString());
		}
	}

}
