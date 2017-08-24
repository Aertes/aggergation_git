<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#assign tag=JspTaglibs["/WEB-INF/taglib/permission.tld"]>
<#include "/wechat/layouts/main.ftl"/>
<@html></@html>
<#macro style>
<link rel="stylesheet" href="${rc.contextPath}/wechat/css/bootstrap-switch.css" />
<link rel="stylesheet" href="${rc.contextPath}/wechat/css/dateTime.css" />
<style>
	.titlelist td{
		height:40px;
		line-height:40px;
		border-right:1px solid #ccc;
		padding:0 0 0 1%;
		text-algin:center;
		font-family:"微软雅黑"
	}
	.sendMember span{
		display:block;
		font-size:14px;
	}
	.hadSendMessage li{
		width:100%;
	}
	.replay{
		margin:0 auto;
		width:90%;
	}
	.hadSendMessage{
		border:1px solid #d9d9d9;
		border-top:none;
	}
	.messagesend{
		padding:20px 0;
	}
	.messageManage>div{
		margin-bottom:18px;
	}
	.hadSendMessage >li:last-child{
		border-bottom:none;
	}
	.messageManage label{
		font-size:12px;
		height:30px;
		line-height:30px;
	}
	.sendMember{
		padding-left:1%;
	}
	.hadSendMessage .sendImg{
		width:12%;
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
            <span>营销应用</span>
            <span>&gt;&gt;</span>
            <span>消息中心</span>
            <span>&gt;&gt;</span>
            <span>消息管理</span>
		</li>
	</ul>
</div>
<h1 class="title_page pr">消息管理</h1>
<div class="message" style="line-height:50px;">
	<a href="${rc.contextPath}/wechat/messagesendbatch/index" class=" marginLeft10">群发功能</a>
	<a href="${rc.contextPath}/wechat/autoreply/index" class=" marginLeft10">自动回复</a>
</div>
<div class="tabContainer tabChange" style="margin-left:20px;">
	<ul class="tabContiner_nav borderbottom">
		<li class="active" href="#page1">
			<a id="rows_message">消息列表(${messageNumbers}条)</a>
		</li>
	</ul>
	<div class="tabContianer_con">
		<div id="page1" style="display: block;">
			<div class="messagesend">
				<form action="${rc.contextPath}/wechat/message/search" id="form1Id" class="form-search">
					<div class="messageManage clearfix">
						<div class="floatL">
							<label class="floatL">时间：</label>
							<#if startTime?exists>
								<div id="startTime" style="width: 150px;" class="floatL" params="{name:'startTime',value:'${startTime}'}"></div>
								<label class="floatL margin10">-</label>
								<div id="endTime" style="width: 150px;" class="floatL" params="{name:'endTime',value:'${endTime}'}"></div>
							<#else>
								<div id="startTime" style="width: 150px;" class="floatL" params="{name:'startTime',value:''}"></div>
								<label class="floatL margin10">-</label>
								<div id="endTime" style="width: 150px;" class="floatL" params="{name:'endTime',value:''}"></div>
							</#if>
						</div>
						<div class="floatL margin10">
							<label>用户名：</label>
							<input type="text" name="fanname" class="search" style="width:150px;"  />
						</div>
						<div class="floatL margin10">
							<label>消息内容：</label>
							<input type="text" name="content" class="search" style="width:150px;" />
						</div>
						<div class="floatL margin10">
							<input type="button" value="搜 索" class="redButton submit" />
						</div>
					</div>
				</form>
				 <table width="100%" style="background:#f0f0f0;border:1px solid #ccc;" class="titlelist">
				 	<tbody>
				 		<#if session_current_publicno?exists && session_current_publicno.verify_type_info!="-1">
				 		<tr>
				 			<td width="12%" style="text-align:center;font-weight:bold;">用户信息</td>
				 			<td width="45%" style="text-align:center;padding-left:1%;font-weight:bold;">消息内容</td>
				 			<td width="15%" style="text-align:center;font-weight:bold;">消息时间</td>
				 			<td width="10%" style="text-align:center;font-weight:bold;">消息状态</td>
							<td width="10%" style="text-align:center;font-weight:bold;">操作</td>
				 		</tr>
				 		<#else>
				 		<tr>
				 			<td width="12%" style="text-align:center;font-weight:bold;">用户信息</td>
				 			<td width="50%" style="text-align:center;padding-left:1%;font-weight:bold;">消息内容</td>
				 			<td width="25%" style="text-align:center;font-weight:bold;">消息时间</td>
				 			<td width="10%" style="text-align:center;font-weight:bold;">消息状态</td>
				 		</tr>
				 		</#if>
				 	</tbody>
				</table>
				<div id="table1Id">
					<ul class="hadSendMessage">
					</ul>
					<div class="padding30">
						<div class="pagegination floatR">
							<ul>
							</ul>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
</#macro>
<#macro script>
		<script type="text/javascript" src="${rc.contextPath}/wechat/js/report/highcharts.js" ></script>
		<script type="text/javascript" src="${rc.contextPath}/wechat/js/report/highcharts-more.js" ></script>
		<script type="text/javascript" src="${rc.contextPath}/wechat/js/report/exporting.js" ></script>
		<script type="text/javascript" src="${rc.contextPath}/wechat/js/report/gray.js" ></script>
		<script type="text/javascript" src="${rc.contextPath}/wechat/js/report/Chart.js" ></script>
		<script type="text/javascript" src="${rc.contextPath}/wechat/js/commom.js" ></script>
		
		<script type="text/javascript" src="${rc.contextPath}/wechat/assets/js/jquery-1.10.2.min.js" charset="UTF-8"></script>
		<script src="${rc.contextPath}/wechat/assets/js/jquery-ui-1.10.3.full.min.js"></script>
		<script type="text/javascript" src="${rc.contextPath}/wechat/js/dateTimePicker/bootstrap-datetimepicker.js" charset="UTF-8"></script>
		<script type="text/javascript" src="${rc.contextPath}/wechat/js/dateTimePicker/locales/bootstrap-datetimepicker.fi.js" charset="UTF-8"></script>
		
		
		<script src="${rc.contextPath}/wechat/js/dateTimeInterface.js"></script>
		<script src="${rc.contextPath}/wechat/assets/js/bootstrap.min.js"></script>
		<script>
			$(function(){
				/* 解决IE浏览器Get请求是用缓存问题 */
	        	$.ajaxSetup({cache:false});
				dateTimeInterface.init("dateTime","startTime");
				dateTimeInterface.init("dateTime","endTime");
				var page1 =new pagination("form1Id","table1Id",function(value){
					$("#table1Id>ul").children().remove();
					for(var i=0;i<value.length;i++){
						var $li = $("<li class='paddingMar'>");
						var strhtlm="<table><tr><td>"+value[i]["name"]+"</td><td></td></tr><tr><td>地区</td><td>"+value[i]["address"]+"</td></tr><tr><td>签名</td><td>"+value[i]["signature"]+"</td></tr><tr><td>分组</td><td>"+value[i]["group"]+"</td></tr></table>";
						var $img = $("<div class='sendImg'><img src='"+value[i]["portal"]+"' width='75%' height='80%' class='iconHover fffffff' style='margin-top:0px' data-container='body' data-toggle='popover' data-placement='right' title='详细资料'  data-content='"+strhtlm+"' /></div>");
						var sendMemeber = $("<div class='sendMember' style='width: 50%;'><span>"+value[i]["name"]+"</span><span>"+value[i]["content"]+"</span></div>");
						var time = $("<div class='sendState sendTime sendTime' style='width: 17%;'><span>"+value[i]["time"]+"</span></div>");
						var huifu = "";
						if(value[i]["favorate"]){
							huifu = $("<div class='sendOperate'><a href='javascript:void(0)' id='"+value[i]["id"]+"' class='huifu' style='color:#d12d32'>回复</a></div>")
						}
						var $state=$("<div class='floatL' style='width:10%;padding:2%;color:#cc0000;' id='message_state_"+value[i]["id"]+"'></div>");
						if(value[i]["state"]){
							$state=$("<div class='floatL' style='width:10%;padding:2%;color:#cc0000;' id='message_state_"+value[i]["id"]+"'>"+value[i]["state"]+"</div>");
						}
						$li.append($img).append(sendMemeber).append(time).append($state);
						<#if session_current_publicno?exists && session_current_publicno.verify_type_info!="-1">
						$li.append(huifu);
						</#if>
						$("#table1Id>ul").append($li);
					}
				});
				$(function () { $(".iconHover").popover({html : true });});
				
				$(".fffffff").mouseenter(function(){
					$(this).popover('show');
				});
				
				$(".fffffff").mouseleave(function(){
					$(this).popover('hide');
				});
				
				
			});
			function sendMessageF(id){
				var content=$("#textArea-sendMassage").val();
				$("#textArea-sendMassage").val('');
				$.ajax({
					url:"${rc.contextPath}/wechat/message/reply/",
					data:{messageId:id,content:content},
					type:'post',
					cache:false,
					dataType : 'json', 
					success:function(data) {
						if(data.code==200){
                            getNewMessages();
							$("#message_state_"+id).html("已回复");
						}
						var layerAlert = new ElasticLayer("alert",data.value,{title : "信息提示框",dialog : true,time : 2000});
						layerAlert.init();
					}
				});
			}

            function getNewMessages() {
                $.ajaxSetup({cache:false});
                //发送post请求
                $.get("${rc.contextPath}/wechat/message/newMessage", function(data){
                    if(data["newMessage"]){
                        $("#messageI").css("display","inline-block")
                    }
					if(!data["newMessage"]){
                        $("#messageI").css("display","none")
					}
                });
            };
			
		</script>
</#macro>