package com.citroen.wechat.api.model;

import java.util.ArrayList;
import java.util.List;

public class Menu {
	private String type;
	private String name;
	private String key;
	private String url;
	private String mediaId;
	private List<Menu> children = new ArrayList<Menu>();
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getMediaId() {
		return mediaId;
	}
	public void setMediaId(String mediaId) {
		this.mediaId = mediaId;
	}
	public List<Menu> getChildren() {
		return children;
	}
	public void setChildren(List<Menu> children) {
		this.children = children;
	}
	
	public static enum Type{
		click("click"),//点击推事件
		view("view"),//跳转URL
		scancode_push("scancode_push"),//扫码推事件
		scancode_waitmsg("scancode_waitmsg"),//扫码推事件且弹出“消息接收中”提示框
		pic_sysphoto("pic_sysphoto"),//弹出系统拍照发图
		pic_photo_or_album("pic_photo_or_album"),//弹出拍照或者相册发图
		pic_weixin("pic_weixin"),//弹出微信相册发图器
		location_select("location_select"),//弹出地理位置选择器
		media_id("media_id"),//下发消息（除文本消息）
		view_limited("view_limited");//跳转图文消息URL
		private String value;
		private Type(String value){this.value = value;}
		public String getValue() {
			return value;
		}
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("\"name\":\"").append(name).append("\"");
		if(type!=null && !"".equals(type.trim())){
			sb.append(",\"type\":\"").append(type).append("\"");
			if(key!=null && !"".equals(key.trim())){
				sb.append(",\"key\":\"").append(key).append("\"");
			}
			if(url!=null && !"".equals(url.trim())){
				sb.append(",\"url\":\"").append(url).append("\"");
			}
			if(mediaId!=null && !"".equals(mediaId.trim())){
				sb.append(",\"media_id\":\"").append(mediaId).append("\"");
			}
		}
		if(children.size()>0){
			sb.append(",\"sub_button\":[");
			for(int i = 0;i<children.size();i++){
				if(i>0){sb.append(",");}
				Menu menu = children.get(i);
				sb.append(menu.toString());
			}
			sb.append("]");
		}
		sb.append("}");
		return sb.toString();
	}
	
}
