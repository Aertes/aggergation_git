package com.citroen.wechat.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.citroen.wechat.api.model.Final;
import com.citroen.wechat.api.model.Message;
import com.citroen.wechat.api.security.WXBizMsgCrypt;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class ReplyXMLUtil {
	private static Log logger = LogFactory.getLog(ReplyXMLUtil.class);
	/**
	 * 解析微信服务器推送过来的消息
	 * */
	public static Message resolve(HttpServletRequest request){
        //将POST流转换为XStream对象  
        XStream xs = new XStream(new DomDriver());  
        //将指定节点下的xml节点数据映射为对象  
        xs.alias("xml", Message.class);
    	ServletInputStream in = null;
        try { 
        	in = request.getInputStream();
        	logger.info("request.getInputStream()="+request.getInputStream());
        	if(in!=null){
	            //将流转换为字符串 
	            StringBuilder encryptXml = new StringBuilder();
	            byte[] b = new byte[4096];
		        for (int n; (n = in.read(b)) != -1;) {
		        	encryptXml.append(new String(b, 0, n, "UTF-8"));
		        }
		        logger.info("encryptXml="+encryptXml);
		        //获取解析xml对象
				WXBizMsgCrypt cxb = new WXBizMsgCrypt(Final.getValue(Final.COMPONENT_TOKEN),Final.getValue(Final.COMPONENT_ENCODING_AES_KEY), Final.getValue(Final.COMPONENT_APPID));
				//解析xml
		        Message message = (Message) xs.fromXML(encryptXml.toString());
		        logger.info("message="+message);
		        //解密xml
				String decryptXml = cxb.decrypt(message.getEncrypt());
		        logger.info("decryptXml="+decryptXml);
				//解析xml
				message = (Message) xs.fromXML(decryptXml);
				//保存加密xml
				message.setDecryptXml(encryptXml.toString());
				//保存解密xml
				message.setEncryptXml(decryptXml);
				//保存公众号appid
				String authorizer_appid = request.getRequestURI().replace("/msgcallback","");
				authorizer_appid = authorizer_appid.substring(authorizer_appid.lastIndexOf("/")+1);
				message.setAppid(authorizer_appid);
				logger.info("message="+message);
				//返回消息对象
				return message;
        	}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(in!=null){
		        try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		        in = null;
			}
		}
		return null;
	}
	/**
	 * 生成推送到微信服务器的消息XML
	 * */
	public static String wrapXml(String MsgType,String ToUserName,String FromUserName,Map<String,String>... items) throws Exception{
		if(!apiUtils.containsKey(MsgType)){
			throw new Exception("消息类型为"+MsgType+"的接口未实现");
		}
		if(items.length==0){
			throw new Exception("消息内容不能为空");
		}
		ApiUtil apiUtil = apiUtils.get(MsgType);
		
		//生成明文XML
		String xml = apiUtil.factory.getXml(MsgType, ToUserName, FromUserName, items);
		//生成密文XML
		WXBizMsgCrypt cxb = new WXBizMsgCrypt(Final.getValue(Final.COMPONENT_TOKEN),Final.getValue(Final.COMPONENT_ENCODING_AES_KEY),Final.getValue(Final.COMPONENT_APPID));
		return cxb.encryptMsg(xml, "1434647494","123");
	}
	
	
	private static Map<String,ApiUtil> apiUtils = new HashMap<String,ApiUtil>();
	static{
		apiUtils.put("text", new ApiUtil(new String[]{"Content"},"?"));
		apiUtils.put("image",new ApiUtil(new String[]{"MediaId"},"<Image>?</Image>"));
		apiUtils.put("voice",new ApiUtil(new String[]{"MediaId"},"<Voice>?</Voice>"));
		apiUtils.put("video",new ApiUtil(new String[]{"MediaId","Title","Description"},"<Video>?</Video>"));
		apiUtils.put("music",new ApiUtil(new String[]{"Title","Description","MusicUrl","HQMusicUrl","ThumbMediaId"},"<Music>?</Music>"));
		apiUtils.put("news",new ApiUtil(new XmlFactory(){
			public String getXml(String MsgType,String ToUserName,String FromUserName,Map<String,String>... items) throws Exception{
				String[] params = {"Title","Description","PicUrl","Url"};
				String template = "<item>?</item>";
				StringBuilder rs = new StringBuilder();
				for(Map<String,String> item:items){
					StringBuilder sb = new StringBuilder();
					for(String param:params){
						if(item.containsKey(param)){
							sb.append("<").append(param).append(">");
							sb.append("<![CDATA[").append(item.get(param)).append("]]>");
							sb.append("</").append(param).append(">");
						}
					}
					rs.append(template.replace("?",sb));
				}
				long CreateTime = System.currentTimeMillis()/1000;
				StringBuilder xml = new StringBuilder();
				xml.append("<xml>");
				xml.append("<ToUserName><![CDATA[").append(ToUserName).append("]]></ToUserName>");
				xml.append("<FromUserName><![CDATA[").append(FromUserName).append("]]></FromUserName>");
				xml.append("<CreateTime>").append(CreateTime).append("</CreateTime>");
				xml.append("<MsgType><![CDATA[").append(MsgType).append("]]></MsgType>");
				xml.append("<ArticleCount>"+items.length+"</ArticleCount><Articles>"+rs+"</Articles>");
				xml.append("</xml>");
				return xml.toString();
			}
		}));
	}
	
	//XML生成接口
	private interface XmlFactory{
		public String getXml(String MsgType,String ToUserName,String FromUserName,Map<String,String>... items) throws Exception;
	}
	
	private static class ApiUtil{
		XmlFactory factory;
		ApiUtil(XmlFactory factory){
			this.factory = factory;
		}
		ApiUtil(final String[] params,final String template){
			this.factory = new XmlFactory(){
				public String getXml(String MsgType,String ToUserName,String FromUserName,Map<String,String>... items) throws Exception{
					StringBuilder rs = new StringBuilder();
					for(Map<String,String> item:items){
						StringBuilder sb = new StringBuilder();
						for(String param:params){
							if(item.containsKey(param)){
								sb.append("<").append(param).append(">");
								sb.append("<![CDATA[").append(item.get(param)).append("]]>");
								sb.append("</").append(param).append(">");
							}
						}
						rs.append(template.replace("?",sb));
					}
					
					long CreateTime = System.currentTimeMillis()/1000;
					StringBuilder xml = new StringBuilder();
					xml.append("<xml>");
					xml.append("<ToUserName><![CDATA[").append(ToUserName).append("]]></ToUserName>");
					xml.append("<FromUserName><![CDATA[").append(FromUserName).append("]]></FromUserName>");
					xml.append("<CreateTime>").append(CreateTime).append("</CreateTime>");
					xml.append("<MsgType><![CDATA[").append(MsgType).append("]]></MsgType>");
					xml.append(rs);
					xml.append("</xml>");
					
					return xml.toString();
				}
			};
		}
	}
}
