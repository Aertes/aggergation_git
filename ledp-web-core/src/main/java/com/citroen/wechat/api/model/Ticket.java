package com.citroen.wechat.api.model;

public class Ticket {
		//第三方平台appid
		private String AppId;
		//时间
		private String CreateTime;
		//类型
		private String InfoType;
		//内容
		private String ComponentVerifyTicket;
		//密文
		private String Encrypt;
		
	    private String AuthorizerAppid;
		
		public String getAppId() {
			return AppId;
		}
		public void setAppId(String appId) {
			AppId = appId;
		}
		public String getCreateTime() {
			return CreateTime;
		}
		public void setCreateTime(String createTime) {
			CreateTime = createTime;
		}
		public String getInfoType() {
			return InfoType;
		}
		public void setInfoType(String infoType) {
			InfoType = infoType;
		}
		public String getComponentVerifyTicket() {
			return ComponentVerifyTicket;
		}
		public void setComponentVerifyTicket(String componentVerifyTicket) {
			ComponentVerifyTicket = componentVerifyTicket;
		}
		public String getEncrypt() {
			return Encrypt;
		}
		public void setEncrypt(String encrypt) {
			Encrypt = encrypt;
		}
		public String getAuthorizerAppid() {
			return AuthorizerAppid;
		}
		public void setAuthorizerAppid(String authorizerAppid) {
			AuthorizerAppid = authorizerAppid;
		}
		@Override
		public String toString() {
			return "Ticket [AppId=" + AppId + ", CreateTime=" + CreateTime
					+ ", InfoType=" + InfoType + ", ComponentVerifyTicket="
					+ ComponentVerifyTicket + ", Encrypt=" + Encrypt + "]";
		}
		
	
		
}
