<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#assign tag=JspTaglibs["/WEB-INF/taglib/permission.tld"]>
<#include "/wechat/layouts/main.ftl"/>
<@html></@html>
<#macro style>
	<link rel="stylesheet" href="${rc.contextPath}/wechat/css/report.css" />
	<link rel="stylesheet" type="text/css" href="${rc.contextPath}/css/jquery.bigautocomplete.css">
	 <style>
		.acReport .totalCount{
			border:1px solid #d9d9d9;
			margin:10px auto;
			width:70%;
		}
		.activeTable .title{
			font-family:"微软雅黑";
		}
		.activeTable h2{
			background:#fbfbfb;
			font-size:14px;
			line-height:30px;
			padding:5px 10px;
			font-family:"微软雅黑";
			border:1px solid #d9d9d9;
			border-bottom:0;
		}
		.analysis label{
			font-size:12px;
		}
		.reportData thead th{
			background:#fbfbfb;
		}
		.pagegination ul li{
			margin:0;
		}
		.public .sendMessagCondition{
			margin:0 0 5px 0;
		}
		.sendMessagCondition > div select, .sendMessagCondition >div input{
			margin:0 0px;
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
				<span>数据中心</span>
				<span>&gt;&gt;</span>
				<span>插件报表</span>
			</li>
		</ul>
	</div>
	<div  class="baseSet acReport" >
		<form class="overflow-hidden" name="${rc.contextPath}/wechat/report/plugin" method="post" id="formId">
			<div class="sendMessagCondition clearfix">
                <div class="floatL" style="line-height:30px;">
                    <input type="radio" name="effect" checked="checked" value="1"/>
                    <label>上周</label>
                </div>
				<div class="floatL marginLeft20" style="line-height:30px;">
					<input type="radio" name="effect" checked="checked" value="2"/>
					<label>本周</label>
				</div>
				<div class="floatL marginLeft20" style="line-height:30px;">
					<input type="radio" name="effect" value="3"/>
					<label>本月</label>
				</div>
				<div class="floatL marginLeft20" style="line-height:30px;">
					<input type="radio" name="effect" value="4"/>
					<label>上月</label>
				</div>
				<div class="floatL marginLeft20" style="line-height:30px;">
					<input style="margin:9px 5px 0 0;" class="floatL" type="radio" name="effect" value="5"/>
					<label style="margin:0 5px;" class="floatL">自定义</label>
					<div id="startTime" style="width: 150px;" class="floatL search" data-date-format="yyyy-mm-dd hh:ii:ss" params="{name:'startTime',value:'',id:'cstartTime'}"></div>
					<label style="float:left;padding:0 5px;">-</label>
					<div id="endTime" style="width: 150px;" class="floatL search" data-date-format="yyyy-mm-dd hh:ii:ss" params="{name:'endTime',value:'',id:'cendTime'}"></div>
				</div>
				<div class="analysis">
					<label>插件：</label>
					<select name="pluginId" style="min-width: 100px;">
						<option value="">全部</option>
						<#list plugins as plugin>
							<option value="${plugin.id}">${plugin.name}</option>
						</#list>
					</select>
				</div>
				<#if orgs?exists>
				<div class="analysis">
					<label>大区：</label>
					<#if type==1><#--总部-->
						<select style="min-width: 100px;" class="search" name="orgId" onChange="bigAutoDealer();" id="orgId">
							<option value="">全部</option>
							<#list orgs as org>
								<option value="${org.id}">${org.name}</option>
							</#list>
						</select>
					<#else><#--大区 和 网点-->
						<select style="min-width: 100px;" class="search" readOnly=true name="orgId" onChange="bigAutoDealer();" id="orgId">
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
						<input style="min-width:170px;height:30px;font-family:'Microsoft YaHei','微软雅黑';font-size:12px;" type="text" class="key" readOnly id="auto_tt1" relevanceHiddenId="autoHiddenId1" value="${dealer.name}" autocomplete="off" id="dealerName"/>
						<input type="hidden" class="search" id="autoHiddenId1" name="dealerId" value="${dealer.id}" id="dealerId"/>
					<#else>
						<input style="min-width:170px;height:30px;font-family:'Microsoft YaHei','微软雅黑';font-size:12px;" type="text" class="key" id="auto_tt1" relevanceHiddenId="autoHiddenId1" value="${dealer.name}" autocomplete="off" id="dealerName"/>
						<input type="hidden" class="search" id="autoHiddenId1" name="dealerId" value="${dealer.id}" id="dealerId"/>
					</#if>
				</div>
				<div class="floatL marginLeft20">
					<input type="hidden" name="pageSize" value="10" id="pageSize"/>
					<input type="hidden" name="currentPage" value="1" id="currentPage"/>
					<input type="button" value="搜 索" class="redButton" id="btnSearch"/>
				</div>
			</div>
		</form>
		<div>
		<div class="totalCount" id = "totalsDiv">
		</div>
		<div class="overflow-hidden activeTable">
			<h2>插件统计详情</h2>
			<div class="title">
				 <a href="javascript:exportCampaigns();" class="redButton" style="color: #fff;">导 出</a>
			</div>
			<div id="campaignDiv"> 
			</div>
		</div>
	</div>
</#macro>
<#macro script>
		<script src="${rc.contextPath}/wechat/js/layer/layer.js"></script>
		<script src="${rc.contextPath}/wechat/js/ElasticLayer.js"></script>
		<script type="text/javascript" src="${rc.contextPath}/js/report/jquery.bigautocomplete.js"></script>
		<script>
			$(function(){
				$("input").keydown(function(event){
					if(event.keyCode == 13){
						$("#btnSearch").click();
						//return false;
					}
				});
			    bigAutoDealer();
			    dateTimeInterface.init("dateTime","startTime");
				dateTimeInterface.init("dateTime","endTime");
			});
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
		<script type="text/javascript">
			$(function(){
				$("#btnSearch").click(function(){
                    var searchDateType = $("input[name='effect']:checked").val();
                    var searchDateBegin = $("#cstartTime").val();
                    var searchDateEnd = $("#cendTime").val();
                    if (searchDateType == '5') {
                        if (searchDateBegin == "") {
                            var layerAlert = new ElasticLayer("alert", "请选择开始时间", {title: "信息提示框", dialog: true, time: 2000});
                            layerAlert.init();
                            return false;
                        }
                        if (searchDateEnd == "") {
                            var layerAlert = new ElasticLayer("alert", "请选择结束时间", {title: "信息提示框", dialog: true, time: 2000});
                            layerAlert.init();
                            return false;
                        }
                        if (!checkEndTime(searchDateBegin, searchDateEnd)) {
                            var layerAlert = new ElasticLayer("alert", "请选择正确的时间区间", {
                                title: "信息提示框",
                                dialog: true,
                                time: 2000
                            });
                            layerAlert.init();
                            return false;
                        }
                    }
					$("#pageSize").val("10");
					$("#currentPage").val("1");
					loadAll();
				});
				loadAll();
			});
		
		
			function loadAll(){
				loadTotals();
				loadCampigns();
				//loadLeads();
			}
		
			function exportCampaigns(){
				$("#formId").attr("action","${rc.contextPath}/wechat/report/plugin/export");
				$("#formId").submit();
				$("#formId").attr("action","${rc.contextPath}/wechat/report/plugin");
			}
			
			//加载使用次数
			function loadTotals(){
				$.ajax({
					cache:false,
					async:true,
					type:'post',
					data:$('#formId').serialize(),
					url:'${rc.contextPath}/wechat/report/plugin/totals',
					error:function(request) {
                    	alert("服务器正忙...");
	                },
	                success:function(div) {
	                    $("#totalsDiv").html(div);
	                }
				});
			}
			
			//加载活动列表
			function loadCampigns(){
				$.ajax({
					cache:false,
					async:true,
					type:'post',
					data:$('#formId').serialize(),
					url:'${rc.contextPath}/wechat/report/plugin/campigns',
					error:function(request) {
                    	alert("服务器正忙...");
	                },
	                success:function(div) {
	                    $("#campaignDiv").html(div);
	                }
				});
			}
		
			//加载线索列表
			function loadLeads(){
				$.ajax({
					cache:false,
					async:true,
					type:'post',
					data:$('#formId').serialize(),
					url:'${rc.contextPath}/wechat/report/plugin/leads',
					error:function(request) {
                    	alert("服务器正忙...");
	                },
	                success:function(div) {
	                    $("#leadsDiv").html(div);
	                }
				});
			}
            function checkEndTime(startTime, endTime) {
                var start = new Date(startTime.replace("-", "/").replace("-", "/"));
                var end = new Date(endTime.replace("-", "/").replace("-", "/"));
                if (end < start) {
                    return false;
                }
                return true;
            }
		</script>
</#macro>