package com.citroen.wechat.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;

import com.citroen.ledp.domain.Dealer;
import com.citroen.ledp.domain.Organization;
import com.citroen.ledp.domain.User;

/**
 * @Title: DownloadController.java
 * @Package com.citroen.ledp.controller
 * @Description: TODO(文件上传类)
 * @author 郭威
 * @date 2015年4月19日
 * @version V1.0
 */

@Controller("downloadWeixinController")
@RequestMapping("/wechat/downloadWeixin")
public class DownloadWeixinController {
	@RequestMapping(value = "download")
	// 上传图片
	public ModelAndView downloadDocument(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("loginUser");
		String fileName = "";
		if(null == user){
			request.setAttribute("message", "验证登录失败，请重新登录！");
			request.getRequestDispatcher("/").forward(request, response);
			return null;
		}else{
			response.setContentType("text/html;charset=utf-8");
			BufferedInputStream bis = null;
			BufferedOutputStream bos = null;
			//文档保存路径
			String ctxPath = request.getSession().getServletContext()
					.getRealPath("/") + "attached/document/";
			
			//网点
			Dealer loginDealer = (Dealer) session.getAttribute("loginDealer");
			//组织
			Organization loginOrg = (Organization) session.getAttribute("loginOrg");
			//获取用户手册姓名
			if(null != loginDealer && null == loginOrg){
				fileName = "微信矩阵用户手册-网点篇.pptx";
			}else{
				if(null != loginOrg && "1".equals(user.getId().toString())){
					fileName = "微信矩阵用户手册 -系统管理员篇.pptx";
				}else if(null != loginOrg && 1 == loginOrg.getLevel()){
					fileName = "微信矩阵用户手册 -总部篇.pptx";
				}else if(null != loginOrg && 2 == loginOrg.getLevel()){
					fileName = "微信矩阵用户手册 -大区篇.pptx";
				}
			}
			//文件下载路径
			String downLoadPath = ctxPath + fileName;  
			try {   
	            long fileLength = new File(downLoadPath).length();   
	            response.setContentType("application/vnd.ms-powerpoint;charset=UTF-8");
	            response.setHeader("Content-disposition", "attachment; filename="+new String(fileName.getBytes("GBK"), "ISO-8859-1"));

	            response.setHeader("Content-Length", String.valueOf(fileLength));   
	            bis = new BufferedInputStream(new FileInputStream(downLoadPath));   
	            bos = new BufferedOutputStream(response.getOutputStream());   
	            byte[] buff = new byte[2048];   
	            int bytesRead;   
	            while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {   
	                bos.write(buff, 0, bytesRead);   
	            }   
	        } catch (Exception e) {   
	            e.printStackTrace();   
	        } finally {   
	            if (bis != null)   
	                bis.close();   
	            if (bos != null)   
	                bos.close();   
	        }   
	        return null;   
		}
		

	}

}
