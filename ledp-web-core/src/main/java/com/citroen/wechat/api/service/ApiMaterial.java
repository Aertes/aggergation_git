package com.citroen.wechat.api.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.citroen.wechat.api.model.Final;
import com.citroen.wechat.api.model.Material;
import com.citroen.wechat.api.model.MaterialNews;
import com.citroen.wechat.api.util.HttpUtil;
import com.citroen.wechat.api.util.ReturnCode;

/**
 * 永久素材接口
 * */

public class ApiMaterial {
	
	/***上传普通素材*/
	public static Response upload(String access_token,String filePath)  {
		if(!filePath.contains(".")){
			return new Response(Response.ERROR,"上传文件没有后缀");
		}
		String suffix = filePath.substring(filePath.lastIndexOf(".")+1);
		if(!types.containsKey(suffix)){
			return new Response(Response.ERROR,"不支持文件格式+"+suffix);
		}
		
        String uri = Final.MATERIAL_ADD+access_token+"&type="+types.get(suffix);
        try {
			JSONObject json = HttpUtil.doUpload(uri,filePath);
			if(json==null){
				return new Response(Response.ERROR,"接口返回数据为空",json);
			}
			if(json.containsKey("errcode")&&json.getInt("errcode")!=0){
				return new Response(Response.ERROR,ReturnCode.get(json.getInt("errcode")),json);
			}
			return new Response(Response.SUCCESS,"操作成功",json);
		} catch (Exception e) {
			return new Response(Response.ERROR,e.getMessage());
		}
	}
	
	/***上传图片*/
	public static Response uploadImg(String access_token,String filePath)  {
		if(!filePath.contains(".")){
			return new Response(Response.ERROR,"上传文件没有后缀");
		}
		String suffix = filePath.substring(filePath.lastIndexOf(".")+1);
		if(!types.containsKey(suffix)){
			return new Response(Response.ERROR,"不支持文件格式+"+suffix);
		}
		
        String uri = Final.UPLOAD_IMG+access_token;
        try {
			JSONObject json = HttpUtil.doUpload(uri,filePath);
			if(json==null){
				return new Response(Response.ERROR,"接口返回数据为空",json);
			}
			if(json.containsKey("errcode")&&json.getInt("errcode")!=0){
				return new Response(Response.ERROR,ReturnCode.get(json.getInt("errcode")),json);
			}
			return new Response(Response.SUCCESS,"操作成功",json);
		} catch (Exception e) {
			return new Response(Response.ERROR,e.getMessage());
		}
	}
	
	/***上传图文素材*/
	public static Response upload(String access_token,List<MaterialNews> articles)  {
        String uri = Final.MATERIAL+access_token;
        StringBuilder params = new StringBuilder("{\"articles\": [");
        for(int i=0;i<articles.size();i++){
        	MaterialNews article = articles.get(i);
        	params.append((i>0)?",{":"{");
        	params.append("\"title\":").append("\""+article.getTitle()+"\"");
        	params.append(",\"thumb_media_id\":").append("\""+article.getThumb_media_id()+"\"");
        	params.append(",\"author\":").append("\""+article.getAuthor()+"\"");
        	params.append(",\"digest\":").append("\""+article.getDigest()+"\"");
        	params.append(",\"show_cover_pic\":").append("\""+article.getShow_cover_pic()+"\"");
        	/* 替换正文中的标记，防止正文段落间出现多出一个换行 */
			params.append(",\"content\":").append("\""+article.getContent().replace("<p>", "").replace("</p>","").replace("\n","").replace("\t","")+"\"");
        	params.append(",\"content_source_url\":").append("\""+article.getContent_source_url()+"\"");
        	params.append("}");
        }
        params.append("]}");
        try {
			JSONObject json = HttpUtil.doPost(uri,params.toString());
			if(json==null){
				return new Response(Response.ERROR,"接口返回数据为空",json);
			}
			if(json.containsKey("errcode") && json.getInt("errcode")!=0){
				return new Response(Response.ERROR,ReturnCode.get(json.getInt("errcode")),json);
			}
			return new Response(Response.SUCCESS,"操作成功",json);
		} catch (Exception e) {
			return new Response(Response.ERROR,e.getMessage());
		}
	}
	
	/**
	 * 获取永久素材
	 * @param access_token
	 * @param mediaId
	 * @param filePath
	 * @return
	 */
	public static Response getMaterial(String access_token,String mediaId)  {
		String uri = Final.MATERIAL_GET+access_token;
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(uri);
		String params = "{\"media_id\":\""+mediaId+"\"}";
		try{
			if(StringUtils.isNotBlank(params)){
				StringEntity stringEntity = new StringEntity(params,"utf-8");
				stringEntity.setContentType("text/json");
			    stringEntity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
			    httpPost.setEntity(stringEntity);
			}
		    CloseableHttpResponse httpResponse = httpclient.execute(httpPost);
		    httpPost.addHeader(HTTP.CONTENT_TYPE, "application/json");
			if(!"".equals(params)){
				StringEntity stringEntity = new StringEntity(params,"utf-8");
				stringEntity.setContentType("text/json");
			    stringEntity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
			    httpPost.setEntity(stringEntity);
			}
		    HttpResponse response = httpclient.execute(httpPost);
		    String jsonStr = EntityUtils.toString(response.getEntity(),"utf-8");
		    JSONObject json = new JSONObject();
		    json = JSONObject.fromObject(jsonStr);
		    
		    if(json==null){
				return new Response(Response.ERROR,"接口返回数据为空",json);
			}
			if(json.containsKey("errcode") && json.getInt("errcode")!=0){
				return new Response(Response.ERROR,ReturnCode.get(json.getInt("errcode")),json);
			}
			return new Response(Response.SUCCESS,"操作成功",json);
		} catch (Exception e) {
			e.printStackTrace();
			 return new Response(Response.ERROR,e.getMessage());
		}
	}
	
	
	public static Response download(String access_token,String mediaId,String filePath)  {
        String uri = Final.MATERIAL_GET+access_token;
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(uri);
		String string = "{\"media_id\":\""+mediaId+"\"}";
		try{
			if(!"".equals(string)){
				StringEntity stringEntity = new StringEntity(string,"utf-8");
				stringEntity.setContentType("text/json");
			    stringEntity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
			    httpPost.setEntity(stringEntity);
			}
		    //得到文件名及其后缀
			//String fileName = mediaId+".jpg";
			File file = new File(filePath);  
			//读取文件流，生成文件写入磁盘
		    FileEntity entity = new FileEntity(file, "text/plain; charset=\"UTF-8\"");
		    CloseableHttpResponse httpResponse = httpclient.execute(httpPost);
		    //HttpResponse httpResponse = httpClient.execute(httpPost);
			Map<String,String> params = new HashMap<String,String>();
			for(Header header:httpResponse.getAllHeaders()){
				params.put(header.getName(), header.getValue());
			}
			
			InputStream is = httpResponse.getEntity().getContent();
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
		} catch (Exception e) {
			e.printStackTrace();
			 return new Response(Response.ERROR,e.getMessage());
		}
	}
	
	public static Response delete(String access_token,String mediaId){
		String uri = Final.MATERIAL_DELETE+access_token;
		String params = "{\"media_id\":\""+mediaId+"\"}";
		JSONObject json = null;
		try {
			json = HttpUtil.doPost(uri,params);
		} catch (Exception e) {
			return new Response(Response.ERROR,e.getMessage());
		}
		if(json==null){
			return new Response(Response.ERROR,"接口返回数据为空",json);
		}
		if(json.containsKey("errcode") && json.getInt("errcode")!=0){
			return new Response(Response.ERROR,ReturnCode.get(json.getInt("errcode")),json);
		}
		return new Response(Response.SUCCESS,"操作成功",json);
	}
	
	public static Response update(String access_token,String mediaId,MaterialNews material){
		String uri = Final.UPDATE_MATERIAL+access_token;
		StringBuilder params = new StringBuilder();
		params.append("{");
			params.append("\"media_id\":").append("\""+mediaId+"\"");
			params.append(",\"index\":").append("\""+material.getIndex()+"\"");
			params.append(",\"articles\":{");
				params.append("\"title\":").append("\""+material.getTitle()+"\"");
				params.append(",\"thumb_media_id\":").append("\""+material.getThumb_media_id()+"\"");
				params.append(",\"author\":").append("\""+material.getAuthor()+"\"");
				params.append(",\"digest\":").append("\""+material.getDigest()+"\"");
				params.append(",\"show_cover_pic\":").append(material.getShow_cover_pic());
				params.append(",\"content\":").append("\""+material.getContent()+"\"");
				params.append(",\"content_source_url\":").append("\""+material.getContent_source_url()+"\"");
			params.append("}");
		params.append("}");
		
		JSONObject json = null;
		try {
			json = HttpUtil.doPost(uri,params.toString());
		} catch (Exception e) {
			return new Response(Response.ERROR,e.getMessage());
		}
		if(json==null){
			return new Response(Response.ERROR,"接口返回数据为空",json);
		}
		if(json.containsKey("errcode") && json.getInt("errcode")!=0){
			return new Response(Response.ERROR,ReturnCode.get(json.getInt("errcode")),json);
		}
		return new Response(Response.SUCCESS,"操作成功",json);
	}
	/**获取素材总数
	 * 成功返回格式：
	 * {status=200，message=操作成功,params={voice_count=2, news_count=6, image_count=13, video_count=0}}
	*/

	public static Response getTotal(String access_token){
		String uri = Final.GET_MATERIALCOUNT+access_token;
		JSONObject json = null;
		try {
			json = HttpUtil.doPost(uri);
		} catch (Exception e) {
			return new Response(Response.ERROR,e.getMessage());
		}
		if(json==null){
			return new Response(Response.ERROR,"接口返回数据为空",json);
		}
		if(json.containsKey("errcode") && json.getInt("errcode")!=0){
			return new Response(Response.ERROR,ReturnCode.get(json.getInt("errcode")),json);
		}
		return new Response(Response.SUCCESS,"操作成功",json);
	}
	
	public static List<Material> getList(String access_token,String type,int offset,int max) throws ApiException{
		String uri = Final.BATCHGET_MATERIAL+access_token;
		StringBuilder params = new StringBuilder();
		params.append("{");
		params.append("\"type\":").append("\""+type+"\"");
		params.append(",\"offset\":").append("\""+offset+"\"");
		params.append(",\"count\":").append("\""+max+"\"");
		params.append("}");
		JSONObject json = null;
		try {
			json = HttpUtil.doPost(uri,params.toString());
		} catch (Exception e) {
			throw new ApiException(e.getMessage());
		}
		if(json == null||!json.containsKey("item")){
			throw new ApiException("操作失败,接口返回："+json);
		}
		JSONArray items = json.getJSONArray("item");
		List<Material> list = new ArrayList<Material>();
		if("news".equals(type)){
			for(int i=0;i<items.size();i++){
				JSONObject item = items.getJSONObject(i);
				if(!item.containsKey("content")){
					continue;
				}
				Material material = new Material();
				if(item.containsKey("media_id")){
					material.setMedia_id(item.getString("media_id"));
				}
				if(item.containsKey("update_time")){
					material.setUpdate_time(item.getString("update_time"));
				}
				JSONObject content = item.getJSONObject("content");
				JSONArray newsItems = content.getJSONArray("news_item");
				for(int j=0;j<newsItems.size();j++){
					JSONObject newsItem = newsItems.getJSONObject(j);
					MaterialNews news = new MaterialNews();
					if(newsItem.containsKey("title")){
						news.setTitle(newsItem.getString("title"));
					}
					if(newsItem.containsKey("thumb_media_id")){
						news.setThumb_media_id(newsItem.getString("thumb_media_id"));
					}
					if(newsItem.containsKey("show_cover_pic")){
						news.setShow_cover_pic(newsItem.getInt("show_cover_pic"));
					}
					if(newsItem.containsKey("author")){
						news.setAuthor(newsItem.getString("author"));
					}
					if(newsItem.containsKey("digest")){
						news.setDigest(newsItem.getString("digest"));
					}
					if(newsItem.containsKey("content")){
						news.setContent(newsItem.getString("content"));
					}
					if(newsItem.containsKey("url")){
						news.setUrl(newsItem.getString("url"));
					}
					if(newsItem.containsKey("content_source_url")){
						news.setContent_source_url(newsItem.getString("content_source_url"));
					}
					material.getListNews().add(news);
				}
				list.add(material);
			}
		}else{
			for(int i=0;i<items.size();i++){
				JSONObject item = items.getJSONObject(i);
				Material material = new Material();
				if(item.containsKey("media_id")){
					material.setMedia_id(item.getString("media_id"));
				}
				if(item.containsKey("name")){
					material.setName(item.getString("name"));
				}
				if(item.containsKey("update_time")){
					material.setUpdate_time(item.getString("update_time"));
				}
				if(item.containsKey("url")){
					material.setUrl(item.getString("url"));
				}
				list.add(material);
			}
		}
		return list;
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
