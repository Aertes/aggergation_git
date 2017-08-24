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
<#if (total==0)>
	<div class="tabContainer" style="margin-top: 20px;">
		<p style="text-align: center; margin: 20px 0 0;">您还没有进行公众号授权，请点击 &nbsp; &nbsp;<a class="redButton" style="color: #fff;" href="${rc.contextPath}/wechat/publicno/authorize" title="公众号授权" >公众号授权</a> </p>
	</div>
<#else>
	<div class="tabContainer">
		<div class="public" style="margin:20px;">
		<form action="${rc.contextPath}/wechat/publicno/search" method="post" id="formId">
		<#if org?exists>
			<div class="sendMessagCondition clearfix">
				<div>
					<label class="floatL">类&nbsp;&nbsp;别：</label>
					<@tag.select type="publicno_type" style="form-control search floatL" name="type" defaultValue=type?default(0)/>
				</div>
				<#if orgs?exists>
				<div>
					<label class="floatL">大&nbsp;&nbsp;区：</label>
					<select class="search floatL" name="orgId" onChange="bigAutoDealer();" id="orgId">
							<option value="">--全部--</option>
						<#list orgs as org>
							<option value="${org.id}">${org.name}</option>
						</#list>
					</select>
				</div>
				<#else>
					<input type="hidden" class="search" name="orgId" value="${org.id}">
				</#if>
				<div class="ieHack">
					<label class="floatL">网点名称：</label>
					<input style="font-family:'微软雅黑'" type="text" class="key" id="auto_tt1" relevanceHiddenId="autoHiddenId1" value="${dealer.name}" autocomplete="off" id="dealerName"/>
					<input type="hidden" class="search" id="autoHiddenId1" name="dealerId" value="${dealer.id}" id="dealerId"/>
				</div>
			</div>
			<div>
				<div class="floatL">
					<label class="floatL" style="line-height:30px;font-size:12px;">微信号：</label>
					<input type="text" class="search" name="alias_name" style="width:150px;margin:0 10px;">
				</div>
				<div class="floatL" style="margin-left:5px;">
					<input type="button" value="搜 索 " class="redButton submit" style="float:left;display:block;font-size:12px;" />
					<a style="display:block;float:left; margin-left:10px; color:#fff;font-size:12px;" class="redButton" href="${rc.contextPath}/wechat/publicno/authorize" title="添加公众号" style="color:#fff;">添加公众号</a> 
				</div>
			</div>
			<#else>
			<div>
				<div class="floatL">
					<a style="display:block;float:left; margin-left:0; color:#fff;" class="redButton" href="${rc.contextPath}/wechat/publicno/authorize" title="添加公众号" style="color:#fff;">添加公众号</a> 
				</div>
			</div>
			</#if>
			</form>
		</div>
	</div>
	<div class="titlelist clearfix" style="background:#f0f0f0;border:1px solid #ccc;margin:60px 20px 0;height:40px;" >
		<div style="width:11%;">公众号头像</div>
		<div style="width:21%;">所在机构</div>
		<div style="width:15%;">微信名称</div>
		<div style="width:10%;">微信号</div>
		<div style="width:9%;">微信类型</div>
        <div style="width:12%;">授权时间</div>
		<div style="width:10%;">认证状态</div>
		<div style="width:10%;">操作</div>
	</div>
	<div id="tableId">
		<ul class="hadSendMessage">
		</ul>
		<div class="padding30" style="margin:20px 0 0;">
			<div class="pagegination floatR">
				<ul>
				</ul>
			</div>
		</div>
	</div>
</#if>
</#macro>
<#macro script>
		<script type="text/javascript" src="${rc.contextPath}/wechat/js/report/highcharts.js" ></script>
		<script type="text/javascript" src="${rc.contextPath}/wechat/js/layer/layer.js"></script>
		<script type="text/javascript" src="${rc.contextPath}/wechat/js/ElasticLayer.js"></script>
		<script type="text/javascript" src="${rc.contextPath}/js/report/jquery.bigautocomplete.js"></script>
		<script>
			//当前公众号
			var currentPublicno = "${session_current_publicno.id}";
			$(function(){
			    bigAutoDealer();
			});
			function bigAutoDealer(){
				$("#dealerId").val("");
				$("#dealerName").val("");
				var orgId = $("#orgId").val();
			    $("#auto_tt1").bigAutocomplete({
			    	width:270,
			    	url : "${rc.contextPath}/report/area/autocomplete?orgId="+orgId,
			    	callback:function(data){
			    		$("#autoHiddenId1").val(data.id);
					}
				});
			}
		</script>
		<script>
			$(function(){
				<#if (total!=0)>
				var pager = new pagination("formId","tableId",function(value){
					$("#tableId>ul").children().remove();
					for(var i=0;i<value.length;i++){
						var $li = $("<li>");
						var $head_img = $("<div class='sendImg' style='width:10%'><img src='"+value[i]["head_img"]+"' width='75%' /></div>")
						var $nick_name = $("<div class='sendState' style='width:15%;text-align:left;padding:2% 1%;'><span>"+value[i]["nick_name"]+"</span></div>");
						var $alias = $("<div class='sendState' style='width:11%;text-align:left;padding:2% 1%;'><span>"+value[i]["alias"]+"</span></div>");
						var $type = $("<div class='sendState' style='width:9%;text-align:left;padding:2% 1%;'><span>"+value[i]["type.name"]+"</span></div>");
                        var $time = $("<div class='sendState' style='width:12%;text-align:left;padding:2% 1%;'><span>"+value[i]["dateCreate"]+"</span></div>");
						var $status = $("<div class='status' style='width:10%'><span>"+value[i]["status.name"]+"</span></div>");
						
						$li.append($head_img);
						if(!value[i]["dealer.name"]==false){
							var $dealer = $("<div class='status' style='float:left;width:22%;padding:2% 1%;'><span>"+value[i]["dealer.name"]+"</span></div>");
						 	$li.append($dealer);
						}else
						if(!value[i]["org.name"]==false){
							var $org = $("<div class='status' style='float:left;width:22%;padding:2% 1%;'><span>"+value[i]["org.name"]+"</span></div>");
						 	$li.append($org);
						}
						$li.append($nick_name).append($alias).append($type).append($time);
						if(value[i]["authorized"]=="0"){
							var $verify_type_info = $("<div class='sendState' style='width:9%'><span>已取消授权</span></div>");
							$li.append($verify_type_info);
						}else{
							if(value[i]["verify_type_info"]=='-1'){
								var $verify_type_info = $("<div class='sendState' style='width:9%;padding:2%;text-align:left;'><span>未认证</span></div>");
								$li.append($verify_type_info);
							}else{
								var $verify_type_info = $("<div class='sendState' style='width:9%;padding:2%;text-align:left;'><span>已认证</span></div>");
								$li.append($verify_type_info);
							}
							
							if(value[i]["id"]==currentPublicno){
								var $setting = $("<div style='width:12%;float:left;padding:2%;' nickname='"+value[i]["nick_name"]+"' id='"+value[i]["id"]+"'>当前公众号</div>");
								$li.append($setting);
							}else{
								var $setting = $("<div class='currentPublicno' style='width:12%;float:left;padding:2%;' id='"+value[i]["id"]+"' nickname='"+value[i]["nick_name"]+"'><a href='#' style='color:#d12d32'>设为当前公众号</a></div>");
								$li.append($setting);
							}
						}
						$("#tableId>ul").append($li);
					}
					
				});
				</#if>
				$(".hadSendMessage").delegate(".currentPublicno","click",function(){
					var $this = $(this);
					$.ajax({
						url:'${rc.contextPath}/wechat/publicno/setCurrent',
						data:{id:$this.attr("id")},
						success:function(json){
							layer.alert("公众号设置成功！",{time:1000});
							currentPublicno = $this.attr("id");
							var nickname = $this.attr("nickname");
							$(".curPublicNo").html("当前公众号："+nickname);
							pager.getAjaxValue();
						}
					});
				});
			<#if message?exists>
				layer.open({
		            title: "提示",
		            content: "${message}"
		        });
			</#if>
			})
		</script>
</#macro>