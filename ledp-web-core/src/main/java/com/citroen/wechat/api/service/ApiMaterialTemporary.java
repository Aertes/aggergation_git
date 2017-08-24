package com.citroen.wechat.api.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.citroen.wechat.api.model.Final;
import com.citroen.wechat.api.util.HttpUtil;
/**
 * 临时素材接口
 * */

public class ApiMaterialTemporary {
	/**上传临时素材到微信服务器*/
	public static Response upload(String access_token,String filePath)  {
		if(!filePath.contains(".")){
			return new Response(Response.ERROR,"上传文件没有后缀");
		}
		String suffix = filePath.substring(filePath.lastIndexOf(".")+1);
		if(!types.containsKey(suffix)){
			return new Response(Response.ERROR,"不支持文件格式+"+suffix);
		}
		
        String uri = Final.UPLOAD_MATERIAL+access_token+"&type="+types.get(suffix);
        try {
			JSONObject json = HttpUtil.doUpload(uri,filePath);
			return new Response(Response.SUCCESS,"操作成功",json);
		} catch (Exception e) {
			return new Response(Response.ERROR,e.getMessage());
		}
	}
	
	/**下载临时素材到文件夹
	 * 成功后返回参数，例如：
	 *  Content-Type: image/jpeg 
		Content-disposition: attachment; filename="MEDIA_ID.jpg"
		Date: Sun, 06 Jan 2013 10:20:18 GMT
		Cache-Control: no-cache, must-revalidate
		Content-Length: 339721
	 * 
	 * */
	public static Response download(String access_token,String mediaId,String filePath)  {
        String uri = Final.GET_MATERIAL_T+access_token+"&media_id="+mediaId;
        HttpPost get = new HttpPost(uri);
        try {
			HttpResponse httpResponse = new DefaultHttpClient().execute(get);
			Map<String,String> params = new HashMap<String,String>();
			for(Header header:httpResponse.getAllHeaders()){
				params.put(header.getName(), header.getValue());
			}
			if("text/plain".equals(params.get("Content-Type"))){
			    String jsonStr = EntityUtils.toString(httpResponse.getEntity(),"utf-8");
			    JSONObject json= new JSONObject().fromObject(jsonStr);
			    return new Response(Response.ERROR,"操作失败",json);
			}

			String contentDisposition = params.get("Content-disposition");
			if(contentDisposition.contains("attachment")){
				//得到文件名及其后缀
				String fileName = contentDisposition.substring(contentDisposition.indexOf("\"")+1,contentDisposition.lastIndexOf("\""));
				//读取文件流，生成文件写入磁盘
				InputStream is = httpResponse.getEntity().getContent();
				File file = new File(filePath+fileName);  
	            FileOutputStream fileout = new FileOutputStream(file);  
	            byte[] buffer=new byte[10*1024];
	            int ch = 0;  
	            while ((ch = is.read(buffer)) != -1) {  
	                fileout.write(buffer,0,ch);  
	            }  
	            is.close();  
	            fileout.flush();  
	            fileout.close();
	            return new Response(Response.SUCCESS,"操作成功",params);
			}
		} catch (Exception e) {
			 return new Response(Response.ERROR,e.getMessage());
		}
        return new Response(Response.ERROR,"返回数据不包含文件");
	}
	
	private static Map<String,String> types = new HashMap<String,String>();
	static{
		//支持上传的图片格式
		types.put("bmp","image");
		types.put("png","image");
		types.put("jpeg","image");
		types.put("jpg","image");
		types.put("gif","image");
		
		//支持上传的语音格式
		types.put("mp3","voice");
		types.put("wma","voice");
		types.put("wav","voice");
		types.put("amr","voice");

		//支持上传的视频格式
		types.put("mp4","video");

		//支持上传的缩略图格式
		types.put("jpg","thumb");
	}
	
}
