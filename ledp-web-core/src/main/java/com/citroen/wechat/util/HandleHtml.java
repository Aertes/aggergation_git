package com.citroen.wechat.util;


import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.citroen.ledp.util.PropertiyUtil;


/** 
 * @ClassName: HandleHtml 
 * @Description: TODO(的大卫杜夫) 
 * @author 杨少波
 * @date 2015年6月26日 上午11:28:44 
 * 
 */
public class HandleHtml {
	public static void main(String[] args) {
		String html="<div><div style='border: 0px none currentcolor; top: 108px; display: none;' class='replaceplugin dragable editorer sitePages siteContainer navList JZ_edit siteHover' name='jianzhan_nav' replacetype='nav' parenttype='base' nodetype='nav' pluid='5' pluginid='410_nav_base_Zindex4'><nav style='height: auto;' class='jian_nav' name='jianzhan_nav'><ul class='navChange'><li><a href='javascript:void(0)' replacevalue='index.html'>首页</a></li><a href='javascript:void(0)' replacevalue='index.html'></a><li><a href='javascript:void(0)' replacevalue='newCarList.html'>车型列表</a></li><a href='javascript:void(0)' replacevalue='newCarList.html'></a><li><a href='javascript:void(0)' replacevalue='newCar.html'>新车展厅</a></li><a href='javascript:void(0)' replacevalue='newCar.html'></a><li><a href='javascript:void(0)' replacevalue='1437893479705.html'>插件活动</a></li><a href='javascript:void(0)' replacevalue='1437893479705.html'></a><li><a href='javascript:void(0)' replacevalue='newCarDetails.html'>新车详情</a></li><a href='javascript:void(0)' replacevalue='newCarDetails.html'></a></ul></nav></div></div>";
		//FileUtil.writeFile("D:/campaign/1435210248461/page/index.html", getNewHtml(html,1));
	}
	
	public static String getNewHtml(String html,long id,String head,String jsPath){
		Document doc = Jsoup.parse(html);
		Elements links=doc.getElementsByClass("replaceplugin");
		for (Element link : links) {
			String pluginid=link.attr("pluginid");
			String type=link.attr("replacetype");
			if(type.equals("button")){
				String href=link.attr("replacevalue");
				link.child(0).child(0).attr("href", link.child(0).child(0).attr("replacevalue"));
			}else if(type.equals("nav")){
				Elements elements=link.child(0).child(0).children();
				for (Element element : elements) {
					if(!"".equals(element.html())){
						element.child(0).attr("href",element.child(0).attr("replacevalue"));
					}
				}
			}else if(type.equals("nav1")){
				Elements elements=link.child(0).child(0).children();
				for (Element element : elements) {
					element.child(0).attr("href",element.child(0).attr("replacevalue"));
				}
			}else if(type.equals("nav2")){
				Elements elements=link.children();
				for (Element element : elements) {
					element.child(0).attr("href",element.child(0).attr("replacevalue"));
				}
			}else if(type.equals("script_newcarlist")){
				PropertiyUtil propUtil = new PropertiyUtil("/wechat.properties");
				String imgDir = propUtil.getString("template.dir");
				String newCarListjs=FileUtil.readFile(imgDir+"/template3/newCarList.txt");
				link.html("");
				link.html(newCarListjs);
			}else if(type.equals("script_newcar")){
				PropertiyUtil propUtil = new PropertiyUtil("/wechat.properties");
				String imgDir = propUtil.getString("template.dir");
				String newCarjs=FileUtil.readFile(imgDir+"/template3/newCar.txt");
				link.html("");
				link.html(newCarjs);
			}else if(type.equals("newCarDetail_button")){
				PropertiyUtil propUtil = new PropertiyUtil("/wechat.properties");
				String imgDir = propUtil.getString("template.dir");
				String newCarDetailjs=FileUtil.readFile(imgDir+"/template3/newCarDetail.txt");
				link.html("");
				link.html(newCarDetailjs);
			}else{
				if(!"".equals(type)){
					if(!type.contains("script_")){
						link.html(getPluginHtml(type));
					}
				}
				
			}
		}
		String js=getPluginHtml("template");
		Element head1=doc.head();
		head1.children().remove();
		//head1.append(head);
		head1.append(js);
		String html1=doc.html().replaceAll("<body>", "<body><input type='hidden' id='liulanPageId' name='liulanPageId' value='"+id+"'>");
		String js1=FileUtil.readFile(jsPath);
		html1=html1.replace("<head>", "<head>"+js1);
		return html1;
	}
	public static Map updateHtml(String oldHtml,String newHtml){
		Map map=new HashMap();
		
		String oldstr="";
		String newstr="";
		Document olddoc = Jsoup.parse(oldHtml);
		Elements oldlinks=olddoc.getElementsByClass("modelPageTitle");
		for (Element link : oldlinks) {
			oldstr=link.child(0).toString();
			for(Element link1:link.children()){
				String type=link1.attr("attr-type");
				if("navtype".equals(type)){
					oldstr+=link1.toString();
					break;
				}
			}
			
		}
		Document newdoc = Jsoup.parse(newHtml);
		Elements newlinks=newdoc.getElementsByClass("modelPageTitle");
		for (Element link : newlinks) {
			newstr=link.child(0).toString();
			for(Element link1:link.children()){
				String type=link1.attr("attr-type");
				if("navtype".equals(type)){
					newstr+=link1.toString();
					break;
				}
			}
		}
		if(newstr.equals(oldstr)){
			map.put("isUpdat", false);
			return map;
		}
		map.put("isUpdat", true);
		map.put("html", newstr);
		return map;
	}
	/**
	 * 替换标题和导航
	 * @param html
	 * @param div
	 * @return
	 */
	public static String newHtml(String html,String div){
		Document doc = Jsoup.parse(html);
		Elements links=doc.getElementsByClass("modelPageTitle");
		//导航中的插件，页面中导航下面如果有插件，插件会和导航在同一层级
		String otherElements = "";
		for (Element link : links) {
			link.child(0).remove();
			for(Element link1:link.children()){
				String type=link1.attr("attr-type");
				if("navtype".equals(type)){
					link1.remove();
				}else{
					otherElements += link1.toString();
				}
			}
		}
		links.empty();
		links.append(div);
		links.append(otherElements);
		return doc.html();
	}
	public static String getPluginHtml(String type){
		PropertiyUtil propUtil = new PropertiyUtil("/wechat.properties");
		String imgDir = propUtil.getString("plugin.dir");
		String plugin=FileUtil.readFile(imgDir+type+".html");
		return plugin;
	}
	public static String getHead(String url){
		String template=FileUtil.readFile(url);
		Document doc = Jsoup.parse(template);
		Element head=doc.head();
		return head.html();
	}
	public static String getsqlHtml(String html){
		Document doc = Jsoup.parse(html);
		Element link=doc.getElementById("replace_srcipt");
		if(link!=null){
		String type=link.attr("replacetype");
		if(type.equals("script_myactive")){
			PropertiyUtil propUtil = new PropertiyUtil("/wechat.properties");
			String imgDir = propUtil.getString("template.dir");
			String js=FileUtil.readFile(imgDir+"/template3/myActive1.txt");
			link.html("");
			link.html(js);
		}else if(type.equals("script_newcar")){
			PropertiyUtil propUtil = new PropertiyUtil("/wechat.properties");
			String imgDir = propUtil.getString("template.dir");
			String js=FileUtil.readFile(imgDir+"/template3/newCar1.txt");
			link.html("");
			link.html(js);
		}else if(type.equals("script_newcardetail")){
			PropertiyUtil propUtil = new PropertiyUtil("/wechat.properties");
			String imgDir = propUtil.getString("template.dir");
			String js=FileUtil.readFile(imgDir+"/template3/newCarDetail1.txt");
			link.html("");
			link.html(js);
		}else if(type.equals("script_newcarlist")){
			PropertiyUtil propUtil = new PropertiyUtil("/wechat.properties");
			String imgDir = propUtil.getString("template.dir");
			String js=FileUtil.readFile(imgDir+"/template3/newCarList1.txt");
			link.html("");
			link.html(js);
		}else if(type.equals("script_myxunjia")){
			PropertiyUtil propUtil = new PropertiyUtil("/wechat.properties");
			String imgDir = propUtil.getString("template.dir");
			String js=FileUtil.readFile(imgDir+"/template3/myXunjia1.txt");
			link.html("");
			link.html(js);
		}else if(type.equals("script_yuyue")){
			PropertiyUtil propUtil = new PropertiyUtil("/wechat.properties");
			String imgDir = propUtil.getString("template.dir");
			String js=FileUtil.readFile(imgDir+"/template3/yuyue1.txt");
			link.html("");
			link.html(js);
		}else if(type.equals("script_xunjia")){
			PropertiyUtil propUtil = new PropertiyUtil("/wechat.properties");
			String imgDir = propUtil.getString("template.dir");
			String js=FileUtil.readFile(imgDir+"/template3/xunjia1.txt");
			link.html("");
			link.html(js);
		}else if(type.equals("script_serviceyuyue")){
			PropertiyUtil propUtil = new PropertiyUtil("/wechat.properties");
			String imgDir = propUtil.getString("template.dir");
			String js=FileUtil.readFile(imgDir+"/template3/serviceYuyue1.txt");
			link.html("");
			link.html(js);
		}else if(type.equals("script_myyuyue")){
			PropertiyUtil propUtil = new PropertiyUtil("/wechat.properties");
			String imgDir = propUtil.getString("template.dir");
			String js=FileUtil.readFile(imgDir+"/template3/myYuyue1.txt");
			link.html("");
			link.html(js);
		}else if(type.equals("script_newpage")){
			PropertiyUtil propUtil = new PropertiyUtil("/wechat.properties");
			String imgDir = propUtil.getString("template.dir");
			String js=FileUtil.readFile(imgDir+"/template3/newpage.txt");
			link.html("");
			link.html(js);
		}else if(type.equals("script_myprize")){
			PropertiyUtil propUtil = new PropertiyUtil("/wechat.properties");
			String imgDir = propUtil.getString("template.dir");
			String js=FileUtil.readFile(imgDir+"/template3/myPrize.txt");
			link.html("");
			link.html(js);
		}
		}
		return doc.html();
	}
}
