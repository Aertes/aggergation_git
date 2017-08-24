<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#include "/layouts/main.ftl"/>
<@html></@html>
<#macro style>
	<style>
		.panel-body{
			overflow:inherit;
			*overflow:visible;
		}
		.leftMain{
			min-height:800px;
		}
		.datagrid-view2 .datagrid-body{
			*overflow:visible;
		}
	</style>
</#macro>
<#macro script>
		<script type="text/javascript" src="${rc.contextPath}/js/Chart.js"></script>
		<script src="${rc.contextPath}/js/raphael.2.1.0.min.js"></script>
		<script src="${rc.contextPath}/js/justgage.1.0.1.min.js"></script>
		<script src="${rc.contextPath}/js/highcharts/highcharts-more.js"></script>
		<script src="${rc.contextPath}/js/home.js"></script>
		<script type="text/javascript">
		$(function(){
			<#if Session["loginDealer"]?exists && permission?exists >
				$.plugin.init("#news_form","#new_dg_info");	
			</#if>
			<#if loginUser?exists && (loginUser.username=='admin')>
			$.plugin.init("#log_form","#log_tab");	
			</#if>
			var home='${home}';
			if(home=='news'){
				$('.tabs').find('li').each(function(){
					var $this=$(this);
					$this.removeClass();
				})
				var new_i=0;
				var new_j=0;
				$('.tabs').find('li').each(function(){
					var $this=$(this);
					if($this.find('a').find('span:first').find('span:first').text()=='信息审核'){
						$this.addClass('tabs-selected');
						new_j=new_i;
					}
					new_i++;
				})
				$('#ledp_tabs').tabs("select",new_j);
			}
		});
		</script>
</#macro>
<#macro content>
	<div id="ledp_tabs" class="easyui-tabs" data-options="tabWidth:100,tabHeight:60">
		<div title="<span class='tt-inner'><img src='${rc.contextPath}/images/modem.png'/><br>仪表盘</span>" style="padding:10px;">
			<div class="clearfix ybp">
				<div class="clearFix publicframe">
					<div class="easyui-panel" title="本周信息发布量">
						<#if Session["loginOrg"]?exists>
							<#if Session["loginOrg"].level == 1>
								<a href="${rc.contextPath}/report/whole/index" title="" class="more">更多</a>
							<#else>
								<a href="${rc.contextPath}/report/area/index" title="" class="more">更多</a>
							</#if>
						<#else>
							<a href="${rc.contextPath}/report/dealer/index" title="" class="more">更多</a>
						</#if>
						
						<div class="clearFix pre">
							<div id="canvas-holder" class="mt20">
								<div id="weekMediaReportGaugeDiv" style="height:250px;margin-top:20px;"></div>
							</div>
						</div>
					</div>
					<div class="easyui-panel" title="本周400接起率" style="padding:0;">
						<#if Session["loginOrg"]?exists>
							<#if Session["loginOrg"].level == 1>
								<a href="${rc.contextPath}/report/whole/index" title="" class="more">更多</a>
							<#else>
								<a href="${rc.contextPath}/report/area/index" title="" class="more">更多</a>
							</#if>
						<#else>
							<a href="${rc.contextPath}/report/dealer/index" title="" class="more">更多</a>
						</#if>
						<div style="width: 400px;height: 200px;" >
							<div id="weekPhoneSuccessReportLineDiv" style="height:250px;margin-top:20px;"></div>
						</div>
					</div>
				</div>
				<div class="clearFix publicframe">
					<div class="easyui-panel" title="本周线索数量" style="padding:0 10px;">
						<#if Session["loginOrg"]?exists>
							<#if Session["loginOrg"].level == 1>
								<a href="${rc.contextPath}/report/whole/index" title="" class="more">更多</a>
							<#else>
								<a href="${rc.contextPath}/report/area/index" title="" class="more">更多</a>
							</#if>
						<#else>
							<a href="${rc.contextPath}/report/dealer/index" title="" class="more">更多</a>
						</#if>
						<div class="clearFix pre">
							<div id="canvas-holder2ty" class="mt20">
								<div id="weekLeadsReportGaugeDiv" style="height:250px;margin-top:20px;"></div>
							</div>
						</div>
					</div>
					<div class="easyui-panel" title="本周400来电量" style="padding:0 10px;">
						<#if Session["loginOrg"]?exists>
							<#if Session["loginOrg"].level == 1>
								<a href="${rc.contextPath}/report/whole/index" title="" class="more">更多</a>
							<#else>
								<a href="${rc.contextPath}/report/area/index" title="" class="more">更多</a>
							</#if>
						<#else>
							<a href="${rc.contextPath}/report/dealer/index" title="" class="more">更多</a>
						</#if>
						<div style="width: 400px;height: 200px;">
							<div id="weekPhoneReportLineDiv" style="height:200px;margin-top:20px;"></div>
						</div>
					</div>
				</div>
			</div>
		</div>
		<#if Session["loginDealer"]?exists && permission?exists >
		<div title="<span class='tt-inner'><img src='${rc.contextPath}/images/pda.png'/><br>信息审核</span>" style="padding:10px">
			<form action="${rc.contextPath}/news/search1" method="POST" id="news_form">
				<input type="hidden" class="search" value="6020,6040" name="state">
				<input type="hidden" class="search" value="wait_auth" name="auth">
			<div class="clearfix ybp">
				<div class="tit clearfix">
					<h2>信息审核</h2>
				</div>
				<div class="ichtable">
					<table id="new_dg_info" width="100%" class="easyui-datagrid" title="待审核信息" data-options="singleSelect:true,collapsible:false,method:'get',remoteSort:false,multiSort:true,pagination:true,showRefresh:false" >
						<thead>
							<tr>
								<th width="20%" data-options="field:'dealer.name',sortable:true">网点名称</th>
								<th width="15%" data-options="field:'type.name',sortable:true">信息类型</th>
								<th width="20%" data-options="field:'title',sortable:true">信息标题</th>
								<th width="15%" data-options="field:'state.name',sortable:true">信息状态</th>
								<th width="15%" data-options="field:'dateCreate',sortable:true">创建时间</th>
								<th width="15%" data-options="field:'operations',sortable:true">操作</th>
							</tr>
						</thead>
					</table>
				</div>
			</div>
			</form>
		</div>
		</#if>
		<#if loginUser?exists && (loginUser.username=='admin')>
		<div title="<span class='tt-inner'><img src='${rc.contextPath}/images/scanner.png'/><br>异常报警</span>" style="padding:10px">
			<form action="${rc.contextPath}/log/log.json" method="POST" id="log_form">
			<input type="hidden" class="search" value="error" name="type">
			<div class="clearfix ybp">
				<div class="tit clearfix" style="width:100%;">
					<h2>异常报警</h2>
				</div>
				<div class="ichtable">
					<table id="log_tab" width="100%" class="easyui-datagrid" title="异常列表" data-options="singleSelect:true,collapsible:false,method:'get',remoteSort:false,multiSort:true,pagination:true,showRefresh:false">
						<thead>
							<tr>
								<th width="10%" data-options="field:'type',sortable:true">日志类型</th>
								<th width="15%" data-options="field:'operator.name',sortable:true">操作用户</th>
								<th width="15%" data-options="field:'resource',sortable:true">操作对象</th>
								<th width="10%" data-options="field:'operation',sortable:true">操作类型</th>
								<th width="20%" data-options="field:'datetime',sortable:true">操作时间</th>
								<th width="10%" data-options="field:'result',sortable:true">操作结果</th>
								<th width="20%" data-options="field:'comment',sortable:true">备注</th>
							</tr>
						</thead>
					</table>
				</div>
			</div>
			</form>
		</div>
		</#if>
	</div>
	<script type="text/javascript">
		var mediaCount = ${mediaCount};
		var leadsCount = ${leadsCount};
		var totalLeadsCount = ${totalLeadsCount};
		var reportPhoneWeekLineY = ${reportPhoneWeekLineY};
		var reportPhoneSuccessWeekLineY = ${reportPhoneSuccessWeekLineY};
	</script>
</#macro>