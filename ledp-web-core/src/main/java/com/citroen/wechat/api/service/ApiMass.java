package com.citroen.wechat.api.service;

import java.util.List;

import net.sf.json.JSONObject;

import com.citroen.wechat.api.model.Final;
import com.citroen.wechat.api.model.MaterialNews;
import com.citroen.wechat.api.util.HttpUtil;
import com.citroen.wechat.api.util.ReturnCode;

/**
 * 群发接口
 * */
public class ApiMass {
	
	/***上传图文素材*/
	public static Response upload(String access_token,List<MaterialNews> articles)  {
        String uri = Final.MASS_UPLOAD+access_token;
        StringBuilder params = new StringBuilder("{\"articles\": [");
        for(int i=0;i<articles.size();i++){
        	MaterialNews article = articles.get(i);
        	params.append((i>0)?",{":"{");
        	params.append("\"title\":").append("\""+article.getTitle()+"\"");
        	params.append(",\"thumb_media_id\":").append("\""+article.getThumb_media_id()+"\"");
        	params.append(",\"author\":").append("\""+article.getAuthor()+"\"");
        	params.append(",\"digest\":").append("\""+article.getDigest()+"\"");
        	params.append(",\"show_cover_pic\":").append("\""+article.getShow_cover_pic()+"\"");
        	params.append(",\"content\":").append("\""+article.getContent()+"\"");
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
	
	/**根据group_id群发*/
	public static Response send(String access_token,String msgtype,String content,String group_id,boolean is_to_all){
		String uri = Final.MASS_SENDALL+access_token;
		StringBuilder params = new StringBuilder();
		params.append("{");
		params.append("\"filter\":{").append("\"is_to_all\":").append(is_to_all).append(",\"group_id\":\"").append(group_id).append("\"}");
		if("text".equals(msgtype)){
			params.append(",\"").append(msgtype).append("\":{\"content\":\"").append(content).append("\"}");
		}else{
			params.append(",\"").append(msgtype).append("\":{\"media_id\":\"").append(content).append("\"}");
		}
		params.append(",\"msgtype\":\"").append(msgtype).append("\"");
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
	
	/**根据openid群发*/
	public static Response send(String access_token,String msgtype,String content,String... openid){
		String uri = Final.MASS_SEND+access_token;
		StringBuilder params = new StringBuilder();
		params.append("{");
		params.append("\"touser\":[");
		for(int i=0;i<openid.length;i++){
			if(i>0){params.append(",");}
			params.append("\"").append(openid[i]).append("\"");
		}
		params.append("]");

		if("text".equals(msgtype)){
			params.append(",\"").append(msgtype).append("\":{\"content\":\"").append(content).append("\"}");
		}else{
			params.append(",\"").append(msgtype).append("\":{\"media_id\":\"").append(content).append("\"}");
		}
		params.append(",\"msgtype\":\"").append(msgtype).append("\"");
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
	
	public static Response get(String access_token,String msgtype,String msg_id){
		String uri = Final.MASS_GET+access_token;
		String params = "{\"msg_id\":\""+msg_id+"\"}";
		JSONObject json = null;
		try {
			json = HttpUtil.doPost(uri,params.toString());
		} catch (Exception e) {
			return new Response(Response.ERROR,e.getMessage());
		}
		if(json == null||!json.containsKey("msg_status")||!"SEND_SUCCESS".equals(json.getString("msg_status"))){
			return new Response(Response.ERROR,"操作失败",json);		
		}
		return new Response(Response.SUCCESS,"操作成功",json);
	}
	
	
	/**
	 * 删除群发消息接口
	 * 请注意，只有已经发送成功的消息才能删除删除消息只是将消息的图文详情页失效，已经收到的用户，还是能在其本地看到消息卡片。 </br>
	 * 另外，删除群发消息只能删除图文消息和视频消息，其他类型的消息一经发送，无法删除。
	 * 
	 * */
	public static Response preview(String access_token,String msgtype,String content,String openid){
		String uri = Final.MASS_PREVIEW+access_token;
		StringBuilder params = new StringBuilder();
		params.append("{");
		params.append("\"towxname\":\"").append(openid).append("\"");
		//params.append("\"touser\":\"").append(openid).append("\"");
		if("text".equals(msgtype)){
			params.append(",\"").append(msgtype).append("\":{\"content\":\"").append(content).append("\"}");
		}else{
			params.append(",\"").append(msgtype).append("\":{\"media_id\":\"").append(content).append("\"}");
		}
		params.append(",\"msgtype\":\"").append(msgtype).append("\"");
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
	
	/**
	 * 删除群发消息接口
	 * 请注意，只有已经发送成功的消息才能删除删除消息只是将消息的图文详情页失效，已经收到的用户，还是能在其本地看到消息卡片。 </br>
	 * 另外，删除群发消息只能删除图文消息和视频消息，其他类型的消息一经发送，无法删除。
	 * 
	 * */
	public static Response delete(String access_token,String msg_id){
		String uri = Final.MASS_DELETE+access_token;
		String params = "{\"msg_id\":"+msg_id+"}";
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
		return new Response(Response.ERROR,"操作成功",json);
	}
}
