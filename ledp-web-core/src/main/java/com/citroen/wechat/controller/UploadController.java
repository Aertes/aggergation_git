package com.citroen.wechat.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.citroen.ledp.util.PropertiyUtil;
import com.citroen.wechat.util.JsonUtil;

/**
 * 文件上传
 * @author 何海粟
 * @date2015年6月21日
 */
@Controller("wechatUploadController")
@RequestMapping("/wechat/upload")
public class UploadController {
	
	private Map<String,Object> params;
	
	/**
	 * 上传图片
	 * @param request
	 * @param files
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "image")
	public void uploadImage(
			HttpServletRequest request,HttpServletResponse response, 
			@RequestParam(value = "image", required = false) CommonsMultipartFile[] files) throws Exception {
		
		response.setContentType("text/html; charset=utf-8");
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		// 文件保存目录路径
		PropertiyUtil propUtil = new PropertiyUtil("/wechat.properties");
		String saveDir = propUtil.getString("site.images.dir");
		// 文件保存目录URL
		String saveUrl = propUtil.getString("site.images.url");
		// 定义允许上传的文件扩展名
		String[] types = propUtil.getString("site.images.type").split(",");
		// 最大文件
		long maxSize = propUtil.getLong("site.images.maxSize");
		long fileSize = 0l;
		if (files == null || files.length == 0) {
			response.getWriter().print(JsonUtil.toJSON(getFailure("请选择文件！")));
			return;
		}
		//验证
		for (int i = 0; i < files.length; i++) {
			MultipartFile image = files[i];
			fileSize += image.getSize();
			String fileName = "";
			if (image.isEmpty()) {
				response.getWriter().print(JsonUtil.toJSON(getFailure("请选择文件！")));
				return;
			}
			
			// 检查文件大小
			if (fileSize > maxSize) {
				response.getWriter().print(JsonUtil.toJSON(getFailure(image.getName() + "文件大小超过限制!")));
				return;
			}
			fileName = image.getOriginalFilename();
			// 检查扩展名
			String fileExt = fileName.substring(
					fileName.lastIndexOf(".") + 1).toLowerCase();
			if (!Arrays.<String> asList(types).contains(fileExt)) {
				response.getWriter().print(JsonUtil.toJSON(getFailure("只能上传 gif,jpg,jpeg,png,bmp类型的图片")));
				return;
			}
		}
		for (int i = 0; i < files.length; i++) {
			MultipartFile image = files[i];
			String fileName = "";
			// 文件名
			fileName = image.getOriginalFilename();
			// 通过当前时间生成文件名
			String ext = fileName.substring(
					fileName.lastIndexOf(".") + 1).toLowerCase();
			String randomName = UUID.randomUUID().toString()+"."+ext;
			
			try {
				File uploadedFile = new File(saveDir, randomName);
				image.transferTo(uploadedFile);
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("url", saveUrl +"/"+ randomName);
				map.put("name", randomName);
				result.add(map);
			} catch (Exception e) {
				e.printStackTrace();
				response.getWriter().print(JsonUtil.toJSON(getFailure("文件上传错误！")));
				return;
			}
		}
		response.getWriter().print(JsonUtil.toJSON(result));

	}
	
	private synchronized String getFileName(String fileName){
		//扩展名
		String ext = "";
		if(StringUtils.isNotBlank(fileName)){
			ext = fileName.substring(
					fileName.lastIndexOf(".") + 1).toLowerCase();
		}
		return System.currentTimeMillis()+"."+ext;
	}
	
	private Map getSuccess(String message){
		Map json = new HashMap();
		json.put("code", "success");
		json.put("message", message);
		return json;
	}
	
	private Map getFailure(String message) {
		Map json = new HashMap();
		json.put("code", "failure");
		json.put("message", message);
		return json;
	}
}
