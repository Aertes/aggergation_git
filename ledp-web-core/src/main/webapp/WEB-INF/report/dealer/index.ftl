<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#assign tag=JspTaglibs["/WEB-INF/taglib/permission.tld"]>
<#include "/layouts/main.ftl"/>
<@html></@html>
<#macro style>
	<link rel="stylesheet" type="text/css" href="${rc.contextPath}/css/repot.css">
	<link rel="stylesheet" type="text/css" href="${rc.contextPath}/css/jquery.bigautocomplete.css">
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
			*overflow:visible;
		}
		.tih2{
			*height:30px;
		}
		.combo-p{
			*overflow:auto;
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
	<script type="text/javascript" src="${rc.contextPath}/js/report/report_dealer.js"></script>
	<script type="text/javascript" src="${rc.contextPath}/js/report/jquery.bigautocomplete.js"></script>
	<script>
		$(function(){
		    $("#auto_tt1").bigAutocomplete({
		    	width:270,
		    	url : "${rc.contextPath}/report/area/autocomplete",
		    	callback:function(data){
		    		$("#dealer1").val(data.id);
				}
			});
		});
	</script>
</#macro>
<#macro content>
	<div class="tih2 clearfix search">
		<#if Session["loginDealer"]?exists>
			<p class="floatL">${Session["loginDealer"].name} - 总览</p>
		<#else>
		<div class="floatL">
			<label>网点：</label>
			<input type="text" class="key" id="auto_tt1" relevanceHiddenId="dealer1" value="${dealer1.name}" autocomplete="off"/>
			<input type="hidden" class="search" id="dealer1" name="dealer1" value="${dealer1.id}" />
		</div>
		<div class="sumbit ml10 floatL">
			<input id="searchBtn" type="button" value="搜 索" class="submit"/>
		</div>
		</#if>
		<@check permissionCodes = permissions permissionCode="report/dealer/media">
		<p class="floatR tabsnav" style="line-height:30px;">
			<a href="${rc.contextPath}/report/dealer/media" title="">媒体分析</a>
		</p>
		</@check>
	</div>
	
	<#if Session["loginDealer"]?exists || dealer1?exists>
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
		<div class="mt20">
			<div class="minheight" id="monthReportLineDiv" style="width:99%;"></div>
		</div>
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
				<div class="clearFix pre">
					<div id="canvas-holder" class="mt20" style="width:99%;">
						<div class="minheight" id="weekPhoneReportPieDiv" style="width:100%;"></div>
					</div>
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
		var total_phone=phoneSuccessRate+phoneFailRate;
	</script>
</#macro>