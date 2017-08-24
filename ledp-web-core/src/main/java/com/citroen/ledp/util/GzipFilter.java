package com.citroen.ledp.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 * 对图片，css，js,ftl模板压缩提高访问效率
 * @author 郭威
 *
 */
public class GzipFilter implements Filter {

	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub
		
	}
	
	//gzip压缩实现
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		  HttpServletResponse resp = (HttpServletResponse) response;
	        HttpServletRequest req=(HttpServletRequest)request;
	        if(isGZipEncoding(req)){
	            Wrapper wrapper = new Wrapper(resp);
	            chain.doFilter(request, (ServletResponse) wrapper);
	            byte[] gzipData = gzip(wrapper.getResponseData());
	            resp.addHeader("Content-Encoding", "gzip");
	            resp.setContentLength(gzipData.length);
	            ServletOutputStream output = response.getOutputStream();
	            output.write(gzipData);
	            output.flush();
	        } else {
	            chain.doFilter(request, response);
	        }        

		
	}

	public void destroy() {
		
		
	}
	 /**
     * 判断浏览器是否支持GZIP
     * @param request
     * @return
     */
    private static boolean isGZipEncoding(HttpServletRequest request){
      boolean flag=false;
      String encoding=request.getHeader("Accept-Encoding");
      if(encoding.indexOf("gzip")!=-1){
        flag=true;
      }
      return flag;
    }
    private byte[] gzip(byte[] data) {
        ByteArrayOutputStream byteOutput = new ByteArrayOutputStream(10240);
        GZIPOutputStream output = null;
        try {
            output = new GZIPOutputStream(byteOutput);
            output.write(data);
        } catch (IOException e) {
        } finally {
            try {
                output.close();
            } catch (IOException e) {
            }
        }
        return byteOutput.toByteArray();
    }


}
