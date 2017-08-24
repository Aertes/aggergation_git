<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#assign tag=JspTaglibs["/WEB-INF/taglib/permission.tld"]>
<#include "/wechat/layouts/main.ftl"/>
<@html></@html>
<#macro style>
<link rel="stylesheet" href="${rc.contextPath}/wechat/css/category.css" />
<link rel="stylesheet" type="text/css" href="${rc.contextPath}/css/jquery.bigautocomplete.css">
<style>
.hadSendMessage{
	margin:0 30px;
}
.hadSendMessage img{
	margin:0;
}
.sendState{
	padding:2% 0;
}
.titlelist div{
	padding:0 1%;
	height:39px;
	line-height:39px;
	border-right:1px solid #ccc;
	float:left;
}
.public .sendMessagCondition{
	margin:0 0 5px 0;
}
.sendMessagCondition > div select, .sendMessagCondition >div input{
	margin:0 10px;
	width:150px;
}
.hadSendMessage li{
	padding:1% 0;
}
.hadSendMessage{
	border: 1px solid #ccc;
	margin: 0 20px;
	border-top: none;
	padding:0 1%;
}
.hadSendMessage li:last-child{
	border:none;
}
.titlelist >div:last-child{
	border-right:none;
}
.titlelist div{
	font-family:"微软雅黑";
	font-weight:bold;
	text-align:center;
}
.public .sendMessagCondition label{
	font-size:12px;
}
</style>
</#macro>
<#macro content>
	<div class="breadcrumbs" id="breadcrumbs">
		<script type="text/javascript">
			try{ace.settings.check('breadcrumbs' , 'fixed')}catch(e){}
		</script>

		<ul class="breadcrumb">
			<li>
				<i class="icon-home home-icon"></i>
				<span>公众号管理</span>
			</li>
		</ul>
	</div>
	<div class="tabContainer">
		<form class="overflow-hidden form-search" action="${rc.contextPath}/wechat/wininfo/search" id="formId">
	        <div class="analysis times">
	            <div class="floatL marginLeft20">
	                <label style="float:left;padding:0 5px;">中奖时间:</label>
	                <div id="beginDate" style="width: 150px;float:left;" params="{name:'beginDate',id:'cbeginDate',value:''}"></div>
	                <label style="float:left;padding:0 5px;">-</label>
	                <div id="endDate" style="width: 150px;float:left;"  params="{name:'endDate',id:'cendDate',value:''}"></div>
	            </div>
	        </div>
	        <div class="floatL">
	            <label>客户名称:</label>
	            <input type="text" name="name" id="name" class="search" style="width: 150px;"/>
	        </div>
	        <div class="analysis">
	            <label>手机号码:</label>
	            <input type="text" name="phone" id="phone" class="search" style="width: 150px;"/>
	        </div>
	        <div class="floatL">
	            <label>状态:</label>
	            <select name="status" id="status" class="search" style="width:150px;">
	            	<option value="">全部</option>
	            	<option value="0">已领取</option>
	            	<option value="1">未领取</option>
	            </select>
	        </div>
	        <div class="floatL">
	            <label>来源:</label>
	            <select name="sourceType" id="sourceType" class="search" style="width:150px;">
	            	<option value="">全部</option>
	            	<option value="0">活动</option>
	            	<option value="1">微站</option>
	            </select>
	        </div>
	        <div class="analysis">
	            <input type="button" value="搜 索" class="redButton submit"/>
	        </div>
    	</form>
	</div>
	<div class="titlelist clearfix" style="background:#f0f0f0;border:1px solid #ccc;margin:40px 20px 0;height:40px;" >
		<div style="width:20%;">客户名称</div>
		<div style="width:15%;">手机</div>
		<div style="width:10%;">参与来源</div>
		<div style="width:10%;">状态</div>
		<div style="width:15%;">中奖时间</div>
        <div style="width:20%;">中奖奖品</div>
		<div style="width:10%;">操作</div>
	</div>
	<div id="tableId">
		<ul>
		</ul>
		<div class="padding30 marginTop20">
			<div class="pagegination floatR">
				<ul>
				</ul>
			</div>
		</div>
	</div>
</#macro>
<#macro script>
		<script type="text/javascript" src="${rc.contextPath}/wechat/js/layer/layer.js"></script>
		<script type="text/javascript" src="${rc.contextPath}/wechat/js/ElasticLayer.js"></script>
		<script>
			$(function(){
				dateTimeInterface.init("dateTime","beginDate");
				dateTimeInterface.init("dateTime","endDate");
				var page = new pagination("formId","tableId",function(value){
					$("#tableId>ul").children().remove();
					for(var i=0;i<value.length;i++){
						var obj = value[i];
						var $li = $("<li>");
						var $name = $("<div style='width:10%'>"+obj.name+"</div>")
						var $phone = $("<div style='width:10%'>"+obj.phone+"</div>")
						var $source = $("<div style='width:10%'>"+obj.source+"</div>")
						var $status = $("<div style='width:10%'>"+obj.status+"</div>")
                        var $createTime = $("<div style='width:10%'>"+obj.createTime+"</div>")
						var $awardsName = $("<div style='width:10%'>"+obj.awardsName+"</div>")
						$li.append($name).append($phone).append($source).append($status).append($awardsName).append($awardsName);
						$("#tableId>ul").append($li);
					}
					
				});
			})
		</script>
</#macro>