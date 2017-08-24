package com.citroen.ledp.controller;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.alibaba.fastjson.JSONObject;
import com.citroen.ledp.util.PropertiyUtil;

/**
 * @Title: AuthController.java
 * @Package com.citroen.ledp.controller
 * @Description: TODO(文件上传类)
 * @author 郭威
 * @date 2015年3月27日
 * @version V1.0
 */

@Controller
@RequestMapping("/upload")
public class UploadController {
	
	
	@RequestMapping(value="image")
	//上传图片
	public void uploadImage(HttpServletRequest request,HttpServletResponse response){
		//文件保存目录路径
		PropertiyUtil propUtil = new PropertiyUtil("/config.properties");
		String saveDir = propUtil.getString("news.images.dir");
		//文件保存目录URL
		String saveUrl = propUtil.getString("news.images.url");
		//定义允许上传的文件扩展名
		String[] types = propUtil.getString("news.images.type").split(",");
		//最大文件大小
		long maxSize = 5000000;
		response.setContentType("text/html; charset=UTF-8");
		try{
			if(!ServletFileUpload.isMultipartContent(request)){
				response.getWriter().write("请选择文件。");
			}
			//检查目录
			File uploadDir = new File(saveDir);
			if(!uploadDir.isDirectory()){
				uploadDir.mkdirs();
			}
			//检查目录写入权限
			if(!uploadDir.canWrite()){
				response.getWriter().write(getError("上传目录没有写权限。"));
				return;
			}
			//上传操作
			//转型为MultipartHttpRequest
	        MultipartHttpServletRequest multipartRequest  =  (MultipartHttpServletRequest) request;  
	        MultipartFile imgFile = multipartRequest.getFile("imgFile");
	        //文件名
	        String fileName = imgFile.getOriginalFilename();
			long fileSize = imgFile.getSize();
				if (!imgFile.isEmpty()) {
					//检查文件大小
					if(fileSize > maxSize){
						response.getWriter().write(getError("上传文件大小超过限制。"));
						return;
					}
					//检查扩展名
					String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
					if(!Arrays.<String>asList(types).contains(fileExt)){
						response.getWriter().write(getError("上传文件扩展名是不允许的扩展名。"));
						return;
					}
					SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
					String newFileName = df.format(new Date()) + "_" + new Random().nextInt(1000) + "." + fileExt;
					try{
						
						File uploadedFile = new File(saveDir, newFileName);
						imgFile.transferTo(uploadedFile);
					}catch(Exception e){
						response.getWriter().write(getError("上传文件失败。"));
						return;
					}
					JSONObject obj = new JSONObject();
					obj.put("error", 0);
					obj.put("url", saveUrl + newFileName);
					response.getWriter().write(obj.toJSONString());
				}
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	//错误信息json化
	private String getError(String message) {
		JSONObject obj = new JSONObject();
		obj.put("error", 1);
		obj.put("message", message);
		return obj.toJSONString();
	}
}
