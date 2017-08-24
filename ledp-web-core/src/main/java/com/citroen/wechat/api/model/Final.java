package com.citroen.wechat.api.model;

import com.citroen.ledp.util.PropertiyUtil;

public class Final {

	/***********************第三方平台基本信息部分*******************************/
	//公众号消息校验Token
	public static final String COMPONENT_TOKEN = "api.component.token";
	// 第三方平台appid
	public static final String COMPONENT_APPID = "api.component.appid";
	// 第三方平台APPSECRET
	public static final String COMPONENT_APPSECRET = "api.component.appsecret";
	// 公众号消息加解密Key
	public static final String COMPONENT_ENCODING_AES_KEY = "api.component.encoding_aes_key";
	
	public static String getValue(String key){
		PropertiyUtil propUtil = new PropertiyUtil("/wechat.properties");
		return propUtil.getString(key);
	}
	
	/***********************第三方平台基本信息部分*******************************/
	
	
	/***********************公众号授权部分*******************************/
	// 获取第三方平台tokon
	public static final String GET_COMPONENT_ACCESS_TOKEN = "https://api.weixin.qq.com/cgi-bin/component/api_component_token";
	// 获取第三方平台预授权码
	public static final String API_CREATE_PREAUTHCODE = "https://api.weixin.qq.com/cgi-bin/component/api_create_preauthcode?component_access_token=";
	// 获取公众号授权信息
	public static final String API_QUERY_AUTH = "https://api.weixin.qq.com/cgi-bin/component/api_query_auth?component_access_token=";
	// 获取公众号账户信息
	public static final String API_AUTHORIZER_INFO = "https://api.weixin.qq.com/cgi-bin/component/api_get_authorizer_info?component_access_token=";
	// 获取（刷新）授权公众号的令牌
	public static final String API_AUTHORIZER_TOKEN = "https://api.weixin.qq.com/cgi-bin/component/api_authorizer_token?component_access_token=";
	// 公众号前往授权url
	public static final String COMPONENT_LOGINPAGE = "https://mp.weixin.qq.com/cgi-bin/componentloginpage?component_appid="+getValue(COMPONENT_APPID);
	// 公众号前往授权url回调地址
	public static final String COMPONENT_LOGINPAGE_REDIRECT_URI = "api.component.loginpage_redirect_url";
	// 获取授权公众号的信息
	public static final String API_GET_AUTHORIZER_INFO = "https://api.weixin.qq.com/cgi-bin/component/api_get_authorizer_info?component_access_token=";

	public static final String ACCESS_TOKEN = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="+ getValue(COMPONENT_APPID) + "&secret=" + getValue(COMPONENT_APPSECRET);
	//公众号JS接口的临时票据
	public static final String API_AUTHORIZER_JSAPI_TICKET = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?type=jsapi&access_token=";
	/***********************公众号授权部分*******************************/
	

	/***********************公众号接口操作部分*******************************/
	//获取用户基本信息
	public static final String GET_USER_INFO="https://api.weixin.qq.com/cgi-bin/user/info?access_token=";
	//开发者可以通过该接口对指定用户设置备注名，该接口暂时开放给微信认证的服务号。 
	public static final String UPDATEREMARK="https://api.weixin.qq.com/cgi-bin/user/info/updateremark?access_token=";
	public static final String GET_USER_SUM="https://api.weixin.qq.com/cgi-bin/user/get?access_token=";
	//创建分组
	public static final String CREATE_GROUPS="https://api.weixin.qq.com/cgi-bin/groups/create?access_token=";
	// 获取分组
	public static final String GET_USER_GROUP = "https://api.weixin.qq.com/cgi-bin/groups/get?access_token=";
	//通过用户的OpenID查询其所在的GroupID
	public static final String GET_GROUPID_BYOPENID="https://api.weixin.qq.com/cgi-bin/groups/getid?access_token=";
	//修改分组名
	public static final String UPDATE_GROUPNAME="https://api.weixin.qq.com/cgi-bin/groups/update?access_token=";
	//移动用户分组
	public static final String MOVE_USER_GROUP="https://api.weixin.qq.com/cgi-bin/groups/members/update?access_token=";
	//批量移动用户分组
	public static final String MOVE_USERS_GROUP="https://api.weixin.qq.com/cgi-bin/groups/members/batchupdate?access_token=";
	//删除分组
	public static final String DELETE_GROUP="https://api.weixin.qq.com/cgi-bin/groups/delete?access_token=";
	
	// 获取菜单信息
	public static final String GET_MENU = "https://api.weixin.qq.com/cgi-bin/menu/get?access_token=";
	// 创建自定义菜单
	public static final String MENU_CREATE = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=";
	// 删除菜单
	public static final String MENU_DELETE = "https://api.weixin.qq.com/cgi-bin/menu/delete?access_token=";
	
	// 获取二维码Code
	public static final String CREATE_QRCEDE = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=";
	// 获取二维码图片
	public static final String GET_SHOWQRCODE = "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=";
	

	// 群发T图文上传
	public static final String MASS_UPLOAD = "https://api.weixin.qq.com/cgi-bin/media/uploadnews?access_token=";
	// 群发，按分组
	public static final String MASS_SENDALL = "https://api.weixin.qq.com/cgi-bin/message/mass/sendall?access_token=";
	// 群发，按OPENID
	public static final String MASS_SEND = "https://api.weixin.qq.com/cgi-bin/message/mass/send?access_token=";
	// 群发删除
	public static final String MASS_DELETE = "https://api.weixin.qq.com/cgi-bin/message/mass/delete?access_token=";
	// 群发预览
	public static final String MASS_PREVIEW = "https://api.weixin.qq.com/cgi-bin/message/mass/preview?access_token=";
	// 群发查询
	public static final String MASS_GET = "https://api.weixin.qq.com/cgi-bin/message/mass/get?access_token=";
	

	// 新增永久图文素材
	public static final String MATERIAL = "https://api.weixin.qq.com/cgi-bin/material/add_news?access_token=";
	//新增永久素材
	public static final String MATERIAL_ADD="http://api.weixin.qq.com/cgi-bin/material/add_material?access_token=";
	// 获取永久素材
	public static final String MATERIAL_GET = "https://api.weixin.qq.com/cgi-bin/material/get_material?access_token=";
	// 删除永久素材
	public static final String MATERIAL_DELETE = "https://api.weixin.qq.com/cgi-bin/material/del_material?access_token=";
	// 获取素材总数量
	public static final String GET_MATERIALCOUNT = "https://api.weixin.qq.com/cgi-bin/material/get_materialcount?access_token=";
	// 获取素材列表
	public static final String BATCHGET_MATERIAL = "https://api.weixin.qq.com/cgi-bin/material/batchget_material?access_token=";
	// 修改图文素材
	public static final String UPDATE_MATERIAL = "https://api.weixin.qq.com/cgi-bin/material/update_news?access_token=";
	//新增临时素材
	public static final String UPLOAD_MATERIAL="https://api.weixin.qq.com/cgi-bin/media/upload?access_token=";
	//获取临时素材
	public static final String GET_MATERIAL_T="https://api.weixin.qq.com/cgi-bin/media/get?access_token=";
	//上传图片
	public static final String UPLOAD_IMG="https://api.weixin.qq.com/cgi-bin/media/uploadimg?access_token=";
	/***********************公众号接口操作部分*******************************/
	//客服接口-发消息
	public static final String MESSAGE_SEND= "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=";

	//用户分析数据接口-获取用户增减数据
	public static final String USER_SUMMARY = "https://api.weixin.qq.com/datacube/getusersummary?access_token=";

	//用户分析数据接口-获取累计用户数据
	public static final String USER_CUMULATE = "https://api.weixin.qq.com/datacube/getusercumulate?access_token=";
}
