package com.citroen.wechat.api.service;

import net.sf.json.JSONObject;

import com.citroen.wechat.api.model.Final;
import com.citroen.wechat.api.util.HttpUtil;
import com.citroen.wechat.api.util.ReturnCode;

/**
 * 信息响应接口
 * */
public class ApiReply {
	
	public static Response reply(String access_token,com.citroen.wechat.api.model.Message message){
		try {
			String uri = Final.MESSAGE_SEND+access_token;
			StringBuilder params = new StringBuilder();
			if("text".equals(message.getMsgType())){
				params.append("{");
				params.append("\"touser\":\"").append(message.getToUserName()).append("\"");
				params.append(",\"msgtype\":\"text\"");
				params.append(",\"text\":{");
				params.append("\"content\":\"").append(message.getContent()).append("\"");
				params.append("}");
				params.append("}");
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
					e.printStackTrace();
					return new Response(Response.ERROR,e.getMessage());
				}
			}
			return new Response(Response.SUCCESS,"操作成功");
		} catch (Exception e) {
			return new Response(Response.ERROR,e.getMessage());
		}
	}
	
}
