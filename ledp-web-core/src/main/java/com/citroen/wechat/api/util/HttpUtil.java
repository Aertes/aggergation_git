package com.citroen.wechat.api.util;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.citroen.ledp.dao.mybaits.MybaitsGenericDao;
import com.citroen.ledp.exception.LedpException;
import com.citroen.ledp.util.SpringContextUtil;
import com.citroen.wechat.api.token.TokenHolder;
import com.citroen.wechat.domain.WechatApiSendLog;

public class HttpUtil {
	
	//处理get请求
	public static JSONObject doGet(String uri) throws Exception{
		return  doGet(uri,"");
	}
	
	//处理get请求
	public static JSONObject doGet(String uri,Map<String,String> params) throws Exception{
		String args =getParams(params);
		return  doGet(uri,args);
	}
	
	//处理post请求
	public static JSONObject doPost(String uri) throws Exception{
		return  doPost(uri,"");
	}
	//处理post请求
	public static JSONObject doPost(String uri,Map<String,String> params) throws Exception{
		String args =getParams(params);
		return  doPost(uri,args);
	}
	
	//处理get请求
	public static JSONObject doGet(String uri,String params){
		//记录请求日志信息
		WechatApiSendLog log = new WechatApiSendLog();
	    Map<String,String> uriParams = getParams(uri);
		log.setSendtime(new Date());
		log.setUri(uri);
		log.setParams(params);
	    String access_token = uriParams.get("access_token");
	    String access_appid = TokenHolder.getAuthorizerAccessAppid(access_token);
		log.setAppid(access_appid);
		
		JSONObject json = new JSONObject();
		try{
			DefaultHttpClient httpclient = new DefaultHttpClient();
			//请求超时
			httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 30000); 
			//读取超时
			httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 30000);
			HttpGet httpGet = new HttpGet(uri);
			HttpResponse httpResponse = httpclient.execute(httpGet);
			if(httpResponse==null){
				throw new Exception("接口访问失败");
			}
			String jsonStr = EntityUtils.toString(httpResponse.getEntity(),"utf-8");
		    json = new JSONObject().fromObject(jsonStr);
			//记录请求日志信息
	    	if(json.containsKey("errcode")){
			    log.setErrcode(json.getString("errcode"));
	    	}
	    	if(json.containsKey("errmsg")){
			    log.setErrmsg(json.getString("errmsg"));
	    	}
	    	log.setComment("接口返回结果："+jsonStr);
		}catch(Exception e){
			e.printStackTrace();
			log.setComment("接口异常信息："+e.getMessage());
		}
		
		//记录请求日志信息
	    log.setBacktime(new Date());
	    MybaitsGenericDao<Long> genericDao = SpringContextUtil.getTypeBean("mybaitsGenericDao"); 
	    try {
			genericDao.save(log);
		} catch (Exception e) {
			e.printStackTrace();
			log.setComment("日志保存异常:"+e.getMessage());
		}
	    return json;
	}
	
	//处理post请求
	public static JSONObject doPost(String uri,String params){
		//记录请求日志信息
		WechatApiSendLog log = new WechatApiSendLog();
	    Map<String,String> uriParams = getParams(uri);
		log.setSendtime(new Date());
		log.setUri(uri);
		log.setParams(params);
	    String access_token = uriParams.get("access_token");
	    String access_appid = TokenHolder.getAuthorizerAccessAppid(access_token);
		log.setAppid(access_appid);
		
		JSONObject json = new JSONObject();
		try{
			DefaultHttpClient client = new DefaultHttpClient();
			//请求超时
			client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 30000); 
			//读取超时
			client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 30000);
			HttpPost httpPost = new HttpPost(uri);
			httpPost.addHeader(HTTP.CONTENT_TYPE, "application/json");
			if(!"".equals(params)){
				StringEntity stringEntity = new StringEntity(params,"utf-8");
				stringEntity.setContentType("text/json");
			    stringEntity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
			    httpPost.setEntity(stringEntity);
			}
		    HttpResponse response = client.execute(httpPost);
		    String jsonStr = EntityUtils.toString(response.getEntity(),"utf-8");
		    json = new JSONObject().fromObject(jsonStr);
		    
		  //记录请求日志信息
	    	if(json.containsKey("errcode")){
			    log.setErrcode(json.getString("errcode"));
	    	}
	    	if(json.containsKey("errmsg")){
			    log.setErrmsg(json.getString("errmsg"));
	    	}
	    	log.setComment("接口返回结果："+jsonStr);
		}catch(Exception e){
			log.setComment("接口异常信息："+e.getMessage());
		}
		
		//记录请求日志信息
	    log.setBacktime(new Date());
	    MybaitsGenericDao<Long> genericDao = SpringContextUtil.getTypeBean("mybaitsGenericDao"); 
	    try {
			genericDao.save(log);
		} catch (Exception e) {
			log.setComment("日志保存异常："+e.getMessage());
		}
	    
		return  json;
	}
	
	//处理post请求
	public static JSONObject doUpload(String uri,String filePath) throws Exception{
		//记录请求日志信息
		//记录请求日志信息
	    MybaitsGenericDao<Long> genericDao = SpringContextUtil.getTypeBean("mybaitsGenericDao"); 
		WechatApiSendLog log = new WechatApiSendLog();
	    Map<String,String> uriParams = getParams(uri);
		log.setSendtime(new Date());
		log.setUri(uri);
		log.setParams(filePath);
	    String access_token = uriParams.get("access_token");
	    String access_appid = TokenHolder.getAuthorizerAccessAppid(access_token);
		log.setAppid(access_appid);
		
		try{
			File file = new File(filePath);
			if (!file.exists() || !file.isFile()) {
				log.setBacktime(new Date());
				log.setComment("上传的文件"+filePath+"不存在");
				genericDao.save(log);
				throw new Exception("上传的文件"+filePath+"不存在");
			}
		        
			URL url = new URL(uri);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST"); // 以Post方式提交表单，默认get方式
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setUseCaches(false); // post方式不能使用缓存
			// 设置请求头信息
			connection.setRequestProperty("Connection", "Keep-Alive");
			connection.setRequestProperty("Charset", "UTF-8");
			// 设置边界
			String BOUNDARY = "----------" + System.currentTimeMillis();
			connection.setRequestProperty("Content-Type","multipart/form-data; boundary=" + BOUNDARY);
			// 请求正文信息
			// 第一部分：
			StringBuilder sb = new StringBuilder();
			sb.append("--"); // 必须多两道线
			sb.append(BOUNDARY);
			sb.append("\r\n");
			sb.append("Content-Disposition: form-data;name=\"media\";filename=\""+ file.getName() + "\" \r\n");
			sb.append("Content-Type:application/octet-stream\r\n\r\n");
			byte[] head = sb.toString().getBytes("utf-8");
			// 获得输出流
			OutputStream out = new DataOutputStream(connection.getOutputStream());
			// 输出表头
			out.write(head);
			// 文件正文部分
			// 把文件已流文件的方式 推入到url中
			DataInputStream in = new DataInputStream(new FileInputStream(file));
			int bytes = 0;
			byte[] bufferOut = new byte[1024];
			while ((bytes = in.read(bufferOut)) != -1) {
			        out.write(bufferOut, 0, bytes);
			}
			in.close();
			// 结尾部分
			byte[] foot = ("\r\n--" + BOUNDARY + "--\r\n").getBytes("utf-8");// 定义最后数据分隔线
			out.write(foot);
			out.flush();
			out.close();
			StringBuffer buffer = new StringBuffer();
			BufferedReader reader =  new BufferedReader(new InputStreamReader(connection .getInputStream()));
			// 定义BufferedReader输入流来读取URL的响应
			String line = null;
			while ((line = reader.readLine()) != null) {
			        buffer.append(line);
			}
			JSONObject json = JSONObject.fromObject(buffer.toString());
			
			//记录日志信息
			log.setBacktime(new Date());
			log.setComment(buffer.toString());
			try{
				genericDao.save(log);
			}catch(Exception e){}
			
			return json;
		}catch(Exception e){
			log.setBacktime(new Date());
			log.setComment(e.getMessage());
			try{
				genericDao.save(log);
			}catch(Exception ee){}
			throw e;
		}
	}
	
	private static String getParams(Map<String,String> params){
		if(params==null ||params.isEmpty()){
			return "";
		}
		int index = 0;
		StringBuilder sb = new StringBuilder("{");
		for(Map.Entry<String,String> entry:params.entrySet()){
			if(index++>0){
				sb.append(",");
			}
			sb.append("\"").append(entry.getKey()).append("\":");
			sb.append("\"").append(entry.getValue()).append("\"");
		}
		sb.append("}");
		return sb.toString();
	}
	
	private static Map<String,String> getParams(String url){
		Map<String,String> params = new HashMap<String,String>();
		if(url==null || !url.contains("?")){
			return params;
		}
		String[] keyValues= url.substring(url.indexOf("?")+1).split("&");
		for(String keyValue:keyValues){
			if(keyValue.contains("=")){
				String[] kv = keyValue.split("=");
				params.put(kv[0],kv[1]);
			}
		}
		return params;
	}
	
	
}

