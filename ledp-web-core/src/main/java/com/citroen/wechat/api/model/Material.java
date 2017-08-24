package com.citroen.wechat.api.model;

import java.util.ArrayList;
import java.util.List;
/**
 * 素材
 */
public class Material {
	//标题
	private String media_id;
	//文件名称
	private String name;
	//图文消息的具体内容，支持HTML标签，必须少于2万字符，小于1M，且此处会去除JS
	private String content;
	//这篇图文消息素材的最后更新时间
	private String update_time;
	//图文页的URL，或者，当获取的列表是图片素材列表时，该字段是图片的URL
	private String url;
	//图文素材片段
	private List<MaterialNews> listNews = new ArrayList<MaterialNews>();
	public String getMedia_id() {
		return media_id;
	}
	public void setMedia_id(String media_id) {
		this.media_id = media_id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getUpdate_time() {
		return update_time;
	}
	public void setUpdate_time(String update_time) {
		this.update_time = update_time;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public List<MaterialNews> getListNews() {
		return listNews;
	}
	public void setListNews(List<MaterialNews> listNews) {
		this.listNews = listNews;
	}
	
	public static enum Type{
		image(),video(),voice(),news();
		
	}
	
}
