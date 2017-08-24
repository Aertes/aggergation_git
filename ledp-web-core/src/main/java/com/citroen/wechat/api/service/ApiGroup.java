package com.citroen.wechat.api.service;

import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

import com.citroen.wechat.api.model.Final;
import com.citroen.wechat.api.model.Group;
import com.citroen.wechat.api.util.HttpUtil;
import com.citroen.wechat.api.util.ReturnCode;

/**
 * 分组接口
 * 
 */
public class ApiGroup {
	/**
	 * 创建分组
	 * @param access_token
	 * @param group 分组
	 * @return Group 对象
	 * @ 
	 */
	public static Response create(String access_token, Group group){
		if(group == null || StringUtils.isBlank(group.getName())){
			return new Response(Response.ERROR,"用户组名为空");
		}
		
		String uri = Final.CREATE_GROUPS + access_token;
		String params ="{\"group\":{\"name\":\"" + group.getName()+ "\"}}";
		JSONObject json =null;
		try {
			json = HttpUtil.doPost(uri, params);
		} catch (Exception e) {
			return new Response(Response.ERROR,e.getMessage());
		}
		if(json==null){
			return new Response(Response.ERROR,"接口返回数据为空",json);
		}
		if(json.containsKey("errcode") && json.getInt("errcode")!=0){
			return new Response(Response.ERROR,ReturnCode.get(json.getInt("errcode")),json);
		}
		if(!json.containsKey("group")){
			return new Response(Response.ERROR,"从接口返回数据中未获取到分组",json);
		}
		JSONObject g = json.getJSONObject("group");
		return new Response(Response.SUCCESS,"操作成功",g);
	}

	/**
	 * 获取所有分组
	 * 
	 * @param token
	 * @return List<Group>
	 */
	@SuppressWarnings("unchecked")
	public static List<Group> list(String access_token) throws ApiException{
		JSONObject json = null;
		try {
			String uri = Final.GET_USER_GROUP + access_token;
			json = HttpUtil.doGet(uri);
		} catch (Exception e) {
			throw new ApiException(e.getCause());
		}
		if(json.containsKey("errcode")){
			throw new ApiException(json.getInt("errcode"),json.getString("errmsg"));
		}
		if(!json.containsKey("groups")){
			throw new ApiException("从接口返回数据中未获取到分组");
		}
		JSONArray jsonObject = json.getJSONArray("groups");
		return JSONArray.toList(jsonObject,Group.class);
	}

	/**
	 * 查询用户所在分组
	 * 
	 * @param access_token
	 * @param openId 用户的OpenID
	 * @return groupid
	 * @ 
	 */
	public static Response getGroupIDbyOpenId(String access_token, String openId){
		JSONObject json = null;
		try {
			String uri = Final.GET_GROUPID_BYOPENID + access_token;
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
		if(!json.containsKey("groupid")){
			return new Response(Response.ERROR,"从接口返回数据中未获取到分组");
		}
		return new Response(Response.SUCCESS,"操作成功");
	}
	
	/** 
	 * 修改分组名
	 * @param token
	 * @param Group 新分组(id,name)
	 * @ 
	 */
	public static Response update(String access_token,Group group) {
		if(group == null){
			return new Response(Response.ERROR,"用户组信息为空");
		}
		if(StringUtils.isBlank(group.getName())){
			return new Response(Response.ERROR,"用户组Id为空");
		}
		if(StringUtils.isBlank(group.getName())){
			return new Response(Response.ERROR,"用户组Name为空");
		}
		
		String uri = Final.UPDATE_GROUPNAME + access_token;
		String params ="{\"group\":{\"id\":"+group.getId()+",\"name\":\""+group.getName()+"\"}}";
		JSONObject json =null;
		try {
			json = HttpUtil.doPost(uri, params);
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
	 * 批量移动用户所在分组
	 * @param token
	 * @param openid_list 用户唯一标识符openid的列表（size不能超过50）
	 * @param toGroupId 分组id
	 * @ 
	 */
	public static Response moveMembers(String access_token,String toGroupId,String... openIds) {
		String uri = Final.MOVE_USERS_GROUP + access_token;
		StringBuilder params = new StringBuilder();
		params.append("{\"openid_list\":[");
		for (int i = 0; i < openIds.length; i++) {
			if(i>0){ params.append(","); }
			params.append("\""+openIds[i]+"\"");
		}
		params.append("],\"to_groupid\":").append(toGroupId).append("}");
		
		JSONObject json =null;
		try {
			json = HttpUtil.doPost(uri, params.toString());
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
	 * 删除一个用户分组，删除分组后，所有该分组内的用户自动进入默认分组
	 * @param token
	 * @param groupId 分组ID
	 * @ 
	 */
	public static Response delete(String access_token,String groupId) {
		String uri = Final.DELETE_GROUP + access_token;
		String params ="{\"group\":{\"id\":"+groupId+"}}";
		JSONObject json =null;
		try {
			json = HttpUtil.doPost(uri, params);
		} catch (Exception e) {
			return new Response(Response.ERROR,e.getMessage());
		}
		if(json.isEmpty()||(json.containsKey("errcode") && json.getInt("errcode")==0)){
			return new Response(Response.SUCCESS,"操作成功",json);
		}
		return new Response(Response.ERROR,ReturnCode.get(json.getInt("errcode")),json);
	}
	
}
