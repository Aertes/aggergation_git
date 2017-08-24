package com.citroen.wechat.filter;
import java.io.IOException;
import java.net.URLDecoder;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * url拦截器，图片路径带有中文的图片显示不出来
 * @author 何海粟
 * @date2015年6月21日
 */
public class UrlFilter implements Filter {

  public final static String DEFAULT_URI_ENCODE = "UTF-8";
  
  private FilterConfig config = null;
  private String encode = null;
  
  public void init(FilterConfig config) throws ServletException {
    this.config = config;
    this.encode = config.getInitParameter("DEFAULT_URI_ENCODE");
    if(this.encode == null) {
      this.encode = DEFAULT_URI_ENCODE;
    }
  }

  public void doFilter(ServletRequest req, ServletResponse res,
      FilterChain chain) throws IOException, ServletException {
    HttpServletRequest request = (HttpServletRequest) req;
    String uri = request.getRequestURI();
    String ch = URLDecoder.decode(uri, encode);
    if(uri.equals(ch)) {
      chain.doFilter(req, res);
      return;
    }
    ch = ch.substring(request.getContextPath().length());
    config.getServletContext().getRequestDispatcher(ch).forward(req, res);
  }	

  public void destroy() {
    config = null;
  }
}