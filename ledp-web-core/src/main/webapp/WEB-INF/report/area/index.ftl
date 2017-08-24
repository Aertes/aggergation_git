<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#include "/layouts/main.ftl"/>
<#assign tag=JspTaglibs["/WEB-INF/taglib/permission.tld"]>
<@html></@html>
<#macro style>
	<link rel="stylesheet" type="text/css" href="${rc.contextPath}/css/repot.css">
	<style>
		.minheight{
			*min-height:400px;
		}
		.layout-panel{
			*overflow:auto;
		}
		.rightMain{
			*overflow:visible;
			*background:#eee;
		}
		.layout-panel-west{
			*overflow:visible;
		}
		.textbox .textbox-addon{
			*position:absolute;
		}
		.textbox{
			*position:relative;
		}
		.combo-p .tree{
			*overflow:auto;
		}
		.tih2{
			*height:30px;
		}
		.highcharts-contextmenu{
			*width:200px;
		}
		.total th{
			padding:10px;
			border-bottom:1px solid #ccc;
			border-right:1px solid #ccc;
		}
		.total td{
			padding:10px;
			background:#fff;
			border-bottom:1px solid #ccc;
			border-right:1px solid #ccc;
		}
	</style>
</#macro>
<#macro script>
	<script type="text/javascript" src="${rc.contextPath}/js/Chart.js"></script>
	<script src="${rc.contextPath}/js/raphael.2.1.0.min.js"></script>
	<script src="${rc.contextPath}/js/justgage.1.0.1.min.js"></script>
	<script src="${rc.contextPath}/js/highcharts/highcharts-more.js"></script>
	<script type="text/javascript" src="${rc.contextPath}/js/report/report_area.js"></script>
</#macro>
<#macro content>
	<div class="tih2 clearfix">
		<#if Session["loginOrg"]?exists>
			<#if Session["loginOrg"].level == 1>
				<div class="floatL">
					<label>大区：</label>
					<select id="searchRegion" class="easyui-combotree" data-options="url:'${rc.contextPath}/organization/treeRegionReport?id=1&checkedNodes=<#if searchRegion?exists>${searchRegion},</#if>',method:'get'" multiple style="width:200px; height: 30px; line-height:30px;"></select>
				</div>
				<div class="sumbit ml10 floatL">
					<input id="searchBtn" type="button" value="搜 索" class="submit"/>
				</div>
			<#else>
				<p class="floatL">${Session["loginOrg"].name} - 总览</p>	
			</#if>
		</#if>
		<@check permissionCodes = permissions permissionCode="report/area/media">
		<p class="floatR tabsnav" style="line-height:30px;">
			<a href="${rc.contextPath}/report/area/media" title="">媒体分析</a>
		</p>
		</@check>
		<@check permissionCodes = permissions permissionCode="report/area/comparison">
		<p class="floatR tabsnav" style="line-height:30px;">
			<a href="${rc.contextPath}/report/area/comparison" title="">网点对比</a>
		</p>
		</@check>
	</div>
	
	<#if (Session["loginOrg"]?exists && Session["loginOrg"].level == 2) || searchRegion?exists>
	
	<div class="overview">
		<div class="clearFix">
			<div class="graph">
				<h2><em class="sprite"></em>汇总数据</h2>
				<div class="idtable total" style="margin:10px 0; padding-bottom:10px;">
					<table style="width:100%">
						<thead>
							<th>近一月线索量</th>
							<th>本周信息发布量</th>
							<th>本周400来电量</th>
						</thead>
						<tbody>
							<tr><td>${totalLeads}条</td>
							<td>${totalNews}条</td>
							<td>${total400}条</td></tr>
						</tbody>
					</table>
				</div>
			</div>
		</div>
	</div>
	<!-- part 3 -->
	<div class="graph">
		<h2><em class="sprite"></em>线索量 - 近一月趋势总览</h2>
		<div class="minheight mt20" id="monthReportLineDiv" style="width:99%;"></div>
	</div>
	<!-- part 3 -->
	
	<!-- part 1 -->
	<div class="overview">
		<div class="clearFix">
			<div class="floatR infometion">
				<h2><em class="sprite"></em>信息 - 本周发布量完成率</h2>
				<div class="clearFix pre mt20">
					<div class="minheight" id="weekNewsReportPieDiv" style="width:99%;"></div>
				</div>
			</div>
			<div class="floatL inforelease">
				<h2><em class="sprite"></em>信息 - 本周发布量</h2>
				<div class="pre mt20">
					<div class="minheight" id="weekNewsReportLineDiv" style="width:99%;"></div>
				</div>
			</div>
		</div>
	</div>				
	<!-- part 1 -->
	<!-- part 2 -->
	<div class="overview">
		<div class="clearFix">
			<div class="floatL inforelease">
				<h2><em class="sprite"></em>400来电 -  本周来电量趋势图</h2>
				<div class="pre mt20">
					<div class="minheight" id="weekPhoneReportLineDiv" style="width:100%;"></div>
				</div>
			</div>
			<div class="floatR infometion">
				<h2><em class="sprite"></em>400来电 -  本周接起率</h2>
				<div class="clearFix pre mt20">
					<div class="minheight" id="weekPhoneReportPieDiv" style="width:100%;"></div>
				</div>
			</div>
		</div>
	</div>
	<!-- part 3 -->
	</#if>
	<!-- part 2 -->
	<script>
		var reportLeadsMonthLineX = ${reportLeadsMonthLineX};
		var reportLeadsMonthLineY = ${reportLeadsMonthLineY};
		var reportNewsWeekLineY = ${reportNewsWeekLineY};
		var reportPhoneWeekLineY = ${reportPhoneWeekLineY};
		var mediaFinishRate = ${mediaFinishRate};
		var mediaUnfinishRate = ${mediaUnfinishRate};
		var total_media=mediaFinishRate+mediaUnfinishRate;
		var phoneSuccessRate = ${phoneSuccessRate};
		var randomScalingFactor = function(){ return Math.round(Math.random()*1000)};
	</script>
</#macro>