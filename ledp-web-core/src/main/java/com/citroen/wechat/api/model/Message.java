package com.citroen.wechat.api.model;

/* 
 * 微信公众平台(JAVA)
 * 
 */  
  
/** 
 * POST的XML数据包转换为消息接受对象 
 * 
 * <p>由于POST的是XML数据包，所以不确定为哪种接受消息，<br/> 
 * 所以直接将所有字段都进行转换，最后根据<tt>MsgType</tt>字段来判断取何种数据</p> 
 * 
 */  
public class Message {  
  
    private String ToUserName;  
    private String FromUserName;  
    private Long CreateTime;  
    private String MsgType = "text";  
    private Long MsgId;
    private String MediaId; 
    private String Encrypt;
    
    /*加密XML */
    private String encryptXml;
    /*解密XML */
    private String decryptXml;
    /*公众号AppId*/
    private String appid;
    
    /*文本消息 */
    // 消息内容
    private String Content;
    /*文本消息 */
    
    /*图片消息 */
    // 图片链接
    private String PicUrl;
    /*图片消息 */
    
    /*语音消息 */
    // 语音格式，如amr，speex等
    private String Format;
    /*语音消息 */
    
    /*小视频消息*/
    // 视频消息缩略图的媒体id，可以调用多媒体文件下载接口拉取数据。
    private String ThumbMediaId;
    /*小视频消息*/

    /*地理位置消息*/
    // 地理位置维度
    private String Location_X;
    // 地理位置经度
    private String Location_Y;
    // 地图缩放大小
    private String Scale;
    // 地理位置信息
    private String Label;
    /*地理位置消息*/
    private String Latitude;
    private String Longitude;
    private String Precision;
    /*链接消息*/
    // 消息标题
    private String Title;
    // 消息描述
    private String Description;
    // 消息链接
    private String Url;
    /*链接消息*/

    /*点击菜单事件\菜单跳转链接事件*/
    // 事件类型
    private String Event;
    // 事件KEY值，与自定义菜单接口中KEY值对应
    private String EventKey;
    /*点击菜单事件\菜单跳转链接事件*/
    /**
     * 群发的消息ID
     */
    private Long MsgID;
    /** 
     * 群发的结构，为“send success”或“send fail”或“err(num)”。
     * 但send success时，也有可能因用户拒收公众号的消息、系统错误等原因造成少量用户接收失败。
     * err(num)是审核失败的具体原因，可能的情况如下：
     * err(10001), //涉嫌广告 err(20001), //涉嫌政治 err(20004), 
     * //涉嫌社会 err(20002), //涉嫌色情 err(20006), //涉嫌违法犯罪 err(20008), 
     * //涉嫌欺诈 err(20013), //涉嫌版权 err(22000), //涉嫌互推(互相宣传) err(21000), //涉嫌其他
     */
    private String Status;
    /**
     * group_id下粉丝数；或者openid_list中的粉丝数
     */
    private String TotalCount;
    /**
     * 过滤（过滤是指特定地区、性别的过滤、用户设置拒收的过滤，用户接收已超4条的过滤）后，
     * 准备发送的粉丝数，原则上，FilterCount = SentCount + ErrorCount
     */
    private String FilterCount;
    /**
     * 发送成功的粉丝数
     */
    private String SentCount;
    /**
     * 发送失败的粉丝数
     */
    private String ErrorCount;
    
    private String CardId;
    
	public String getCardId() {
		return CardId;
	}

	public void setCardId(String cardId) {
		CardId = cardId;
	}

	public String getToUserName() {
		return ToUserName;
	}

	public void setToUserName(String toUserName) {
		ToUserName = toUserName;
	}

	public String getFromUserName() {
		return FromUserName;
	}

	public void setFromUserName(String fromUserName) {
		FromUserName = fromUserName;
	}

	public Long getCreateTime() {
		return CreateTime;
	}

	public void setCreateTime(Long createTime) {
		CreateTime = createTime;
	}

	public String getMsgType() {
		return MsgType;
	}

	public void setMsgType(String msgType) {
		MsgType = msgType;
	}

	public Long getMsgId() {
		return MsgID==null?MsgId:MsgID;
	}

	public void setMsgId(Long msgId) {
		MsgId = msgId;
	}

	public String getMediaId() {
		return MediaId;
	}

	public void setMediaId(String mediaId) {
		MediaId = mediaId;
	}

	public String getEncrypt() {
		return Encrypt;
	}

	public void setEncrypt(String encrypt) {
		this.Encrypt = encrypt;
	}

	public String getContent() {
		return Content;
	}

	public void setContent(String content) {
		Content = content;
	}

	public String getPicUrl() {
		return PicUrl;
	}

	public void setPicUrl(String picUrl) {
		PicUrl = picUrl;
	}

	public String getFormat() {
		return Format;
	}

	public void setFormat(String format) {
		Format = format;
	}

	public String getThumbMediaId() {
		return ThumbMediaId;
	}

	public void setThumbMediaId(String thumbMediaId) {
		ThumbMediaId = thumbMediaId;
	}

	public String getLocation_X() {
		return Location_X;
	}

	public void setLocation_X(String location_X) {
		Location_X = location_X;
	}

	public String getLocation_Y() {
		return Location_Y;
	}

	public void setLocation_Y(String location_Y) {
		Location_Y = location_Y;
	}

	public String getLatitude() {
		return Latitude;
	}

	public void setLatitude(String latitude) {
		Latitude = latitude;
	}

	public String getLongitude() {
		return Longitude;
	}

	public void setLongitude(String longitude) {
		Longitude = longitude;
	}

	public String getPrecision() {
		return Precision;
	}

	public void setPrecision(String precision) {
		Precision = precision;
	}

	public String getScale() {
		return Scale;
	}

	public void setScale(String scale) {
		Scale = scale;
	}

	public String getLabel() {
		return Label;
	}

	public void setLabel(String label) {
		Label = label;
	}

	public String getTitle() {
		return Title;
	}

	public void setTitle(String title) {
		Title = title;
	}

	public String getDescription() {
		return Description;
	}

	public void setDescription(String description) {
		Description = description;
	}

	public String getUrl() {
		return Url;
	}

	public void setUrl(String url) {
		Url = url;
	}

	public String getEvent() {
		return Event;
	}

	public void setEvent(String event) {
		Event = event;
	}

	public String getEventKey() {
		return EventKey;
	}

	public void setEventKey(String eventKey) {
		EventKey = eventKey;
	}

	public String getEncryptXml() {
		return encryptXml;
	}

	public void setEncryptXml(String encryptXml) {
		this.encryptXml = encryptXml;
	}

	public String getDecryptXml() {
		return decryptXml;
	}

	public void setDecryptXml(String decryptXml) {
		this.decryptXml = decryptXml;
	}

	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}
	
	public Long getMsgID() {
		return MsgID==null?MsgId:MsgID;
	}

	public void setMsgID(Long msgID) {
		MsgID = msgID;
	}
	
	public String getStatus() {
		return Status;
	}

	public void setStatus(String status) {
		Status = status;
	}
	

	public String getTotalCount() {
		return TotalCount;
	}

	public void setTotalCount(String totalCount) {
		TotalCount = totalCount;
	}
	
	public String getFilterCount() {
		return FilterCount;
	}

	public void setFilterCount(String filterCount) {
		FilterCount = filterCount;
	}
	
	public String getSentCount() {
		return SentCount;
	}

	public void setSentCount(String sentCount) {
		SentCount = sentCount;
	}

	public String getErrorCount() {
		return ErrorCount;
	}

	public void setErrorCount(String errorCount) {
		ErrorCount = errorCount;
	}

	/*地理位置消息*/
    public String toString() {
    	StringBuilder sb = new StringBuilder();
    	sb.append("{ToUserName:'").append(ToUserName  );
    	sb.append("',FromUserName:'").append(FromUserName);
    	sb.append("',CreateTime:'").append(CreateTime  );
    	sb.append("',MsgType:'").append(MsgType        );
    	sb.append("',MsgId:'").append(MsgId            );
    	sb.append("',Content:'").append(Content        );
    	sb.append("',PicUrl:'").append(PicUrl          );
    	sb.append("',LocationX:'").append(Location_X   );
    	sb.append("',LocationY:'").append(Location_Y   );
    	sb.append("',Scale:'").append(Scale            );
    	sb.append("',Label:'").append(Label            );
    	sb.append("',Title:'").append(Title            );
    	sb.append("',Description:'").append(Description);
    	sb.append("',Url:'").append(Url                );
    	sb.append("',MediaId:'").append(MediaId        );
    	sb.append("',Format:'").append(Format          );
    	sb.append("',ThumbMediaId:'").append(ThumbMediaId);
    	sb.append("',Event:'").append(Event            );
    	sb.append("',EventKey:'").append(EventKey      );
    	sb.append("',MsgID:'").append(MsgID      );
    	sb.append("',Status:'").append(Status      );
    	sb.append("',TotalCount:'").append(TotalCount      );
    	sb.append("',FilterCount:'").append(FilterCount      );
    	sb.append("',SentCount:'").append(SentCount      );
    	sb.append("',ErrorCount:'").append(ErrorCount      );
    	sb.append("'}");
    	
    	return sb.toString();
    }
}  