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
.tablelist{
		border:1px solid #d9d9d9;
		border-right: none;
		font-family:"微软雅黑";
	}
	.tablelist th{
		padding: 10px;
		border-right:1px solid #d9d9d9;
		text-align: center;
		font-size: 14px;
	}
	.tablelist td{
		padding: 10px;
		text-align: center;
		font-size: 12px;
		border-top:1px solid #d9d9d9;
		border-right:1px solid #d9d9d9;
	}
	.reportData thead th{
		font-weight:bold;
		font-size:12px;
		background:#fbfbfb;
	}
	.public .sendMessagCondition{
		margin:0 0 5px 0;
	}
	.sendMessagCondition > div select, .sendMessagCondition >div input{
		margin:0 0px;
	}
	.hadSendMessage li:last-child{
		border:none;
	}
	.titlelist >div:last-child{
		border-right:none;
	}
	.titlelist div th{
		font-family:"微软雅黑";
		font-weight:bold;
		text-align:center;
	}
	.public .sendMessagCondition label{
		font-size:12px;
	}
	.sendMessagCondition div{
		margin:0 20px 8px 0;
	}
	.marginLeft20{
		margin-left:20px;
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
				<span>中奖信息管理</span>
			</li>
		</ul>
	</div>
	<div class="tabContainer">
		<div class="public" style="margin:20px;">
			<form action="${rc.contextPath}/wechat/wininfo/search" method="post" id="formId">
				<div class="sendMessagCondition clearfix">
					<#if orgs?exists>
					<div class="analysis">
						<label>大&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;区：</label>
						<#if type==1><#--总部-->
							<select style="min-width: 183px;" name="org" class="search" onChange="bigAutoDealer();" id="orgId">
								<option value="">全部</option>
								<#list orgs as org>
									<option value="${org.id}">${org.name}</option>
								</#list>
							</select>
						<#else><#--大区 和 网点-->
							<select style="min-width: 183px;" disabled="disabled"  class="search" readOnly=true name="org" onChange="bigAutoDealer();" id="orgId">
								<#list orgs as org>
									<option value="${org.id}">${org.name}</option>
								</#list>
							</select>
						</#if>
					</div>
					</#if>
					<div class="floatL ieHack marginLeft20">
						<label style="font-size:12px;">网点名称：</label>
						<#if type==3>
							<input style="min-width:183px;height:30px;font-family:'Microsoft YaHei','微软雅黑';font-size:12px;" type="text"readOnly id="auto_tt1" relevanceHiddenId="autoHiddenId1" value="${dealer.name}" autocomplete="off" id="dealerName"/>
							<input type="hidden" class="search" id="autoHiddenId1" name="dealer" value="${dealer.id}" id="dealerId"/>
						<#else>
							<input style="min-width:183px;height:30px;font-family:'Microsoft YaHei','微软雅黑';font-size:12px;" type="text" id="auto_tt1" relevanceHiddenId="autoHiddenId1" value="${dealer.name}" autocomplete="off" id="dealerName"/>
							<input type="hidden" class="search" id="autoHiddenId1" name="dealer" value="${dealer.id}" id="dealerId"/>
						</#if>
					</div>
					<div class="floatL marginLeft20">
			            <label>客户名称：</label>
			            <input type="text" name="name" class="search" style="width: 183px;"/>
			        </div>
					<div class="floatL marginLeft20">
			            <label>手&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;机：</label>
			            <input type="text" name="phone" class="search" style="width: 183px;"/>
			        </div>
			        <div class="floatL marginLeft20">
			            <label>状&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;态：</label>
			            <select name="status" class="search" style="width: 183px;">
			            	<option value="">全部</option>
			            	<option value="0">未兑领</option>
			            	<option value="1">已兑领</option>
			            </select>
			        </div>
			        <div class="floatL marginLeft20">
			            <label>来&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;源：</label>
			            <select name="sourceType" class="search" style="width: 183px;">
			            	<option value="">全部</option>
			            	<option value="0">微站</option>
			            	<option value="1">活动</option>
			            </select>
			        </div>
					<div class="floatL marginLeft20">
						<label class="floatL" style="line-height:30px;font-size:12px;">中奖时间：</label>
						<div id="beginDate" style="width: 184px;padding-left:2px;" class="floatL" params="{name:'beginDate',value:''}" data-date-format="yyyy-mm-dd hh:ii:ss"></div>
						<label class="floatL">-</label>
						<div id="endDate" style="width: 183px;" class="floatL" params="{name:'endDate',value:''}" data-date-format="yyyy-mm-dd hh:ii:ss"></div>
					</div>
					<div class="floatL marginLeft20" style="margin-left:5px;">
						<input type="button" value="搜 索 " class="redButton submit" style="float:left;display:block;font-size:12px;" />
					</div>
				</div>
			</form>
		</div>
	</div>
	<div class="titlelist clearfix" id="tableId" style="background:#f0f0f0;margin:0px 20px 0;height:40px;" >
		<table cellpadding="0" cellspacing="0" class="reportData tablelist" width="100%">
			<thead>
				<th style="width:10%;">客户名称</th>
				<th style="width:10%;">手机</th>
                <th style="width:8%;">来源</th>
                <th style="width:15%;">活动/微站名称</th>
				<th style="width:9%;">插件名称</th>
				<th style="width:15%;">中奖时间</th>
		        <th style="width:20%;">中奖奖品</th>
                <th style="width:8%;">兑领状态</th>
				<th style="width:10%;">操作</th>
			</thead>
			<tbody id="tbody1">
			</tbody>
		</table>
		<div>
			<div class="pagegination floatR" style="margin:20px;">
				<ul>
				</ul>
			</div>
		</div>
	</div>
</#macro>
<#macro script>
		<script type="text/javascript" src="${rc.contextPath}/wechat/js/report/highcharts.js" ></script>
		<script type="text/javascript" src="${rc.contextPath}/wechat/js/layer/layer.js"></script>
		<script type="text/javascript" src="${rc.contextPath}/wechat/js/ElasticLayer.js"></script>
		<script type="text/javascript" src="${rc.contextPath}/js/report/jquery.bigautocomplete.js"></script>
		
		<script>
			$(function(){
				dateTimeInterface.init("dateTime","beginDate");
				dateTimeInterface.init("dateTime","endDate");
				initPage();
				bigAutoDealer();
			})
			//兑领奖品操作
			function operate(id){
				layer.confirm('确认兑领吗？', {
				    btn: ['确认','取消'] //按钮
				}, function(){
					$.getJSON('${rc.contextPath}/wechat/wininfo/edit',{id:id},function(result){
						if(result.status == 200){
							layer.alert("兑领成功");
							initPage();
						}else{
							layer.alert(result.errMsg);
						}
					});
				}, function(){
				    layer.close();
				});
				
			}
			
			function initPage(){
				var page = new pagination("formId","tableId",function(value){
					$("#tbody1").children().remove();
					for(var i=0;i<value.length;i++){
						var obj = value[i];
						var $tr = $("<tr></tr>");
						var $name = $("<td>"+obj.name+"</td>")
						var $phone = $("<td>"+obj.phone+"</td>");
						var $plugin = $("<td>"+obj.plugin.name+"</td>");
						var $source = $("<td>"+(obj.sourceType==0?"微站":"活动")+"</td>");
						var $sourceName = $("<td>"+obj.sourceName+"</td>");
						var $status = $("<td>"+(obj.status==0?"未兑领":"已兑领")+"</td>");
                        var $time = $("<td>"+obj.createTime+"</td>");
						var $awards = $("<td>"+obj.awardsName+"</td>");
						
						$tr.append($name).append($phone).append($source).append($sourceName).append($plugin).append($time).append($awards).append($status);
						if(obj.status == 0){//未兑领
							var $opt = $("<td><a href='javascript:void(0);' onclick='operate(\""+obj.id+"\");'>兑领</a></td>")
							$tr.append($opt);
						}else{
							var $opt = $("<td></td>")
							$tr.append($opt);
						}
						$("#tbody1").append($tr);
					}
					
				});
			}
			function bigAutoDealer(){
				var orgId = $("#orgId").val();
			    $("#auto_tt1").bigAutocomplete({
			    	width:270,
			    	url : "${rc.contextPath}/report/area/autocomplete?orgId="+orgId,
			    	callback:function(data){
			    		$("#autoHiddenId1").val(data.id);
					}
				});
				$("#dealerId").val("");
				$("#dealerName").val("");
			}
		</script>
</#macro>