<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#assign tag=JspTaglibs["/WEB-INF/taglib/permission.tld"]>
<#include "/layouts/main.ftl"/>
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
	<script type="text/javascript" src="${rc.contextPath}/js/report/report_area_media.js"></script>
</#macro>
<#macro content>
	<div class="tih2 clearfix">
		<#if Session["loginOrg"]?exists && Session["loginOrg"].level == 2>
		<p class="floatL">${Session["loginOrg"].name}-媒体分析</p>
		<#else>
			<p class="floatL">
			媒体分析
			</p>
		</#if>
		<@check permissionCodes = permissions permissionCode="report/area/index">
		<p class="floatR tabsnav">
			<a href="${rc.contextPath}/report/area/index" title="">大区总览</a>
		</p>
		</@check>
		<@check permissionCodes = permissions permissionCode="report/area/comparison">
		<p class="floatR tabsnav">
			<a href="${rc.contextPath}/report/area/comparison" title="">网点对比</a>
		</p>
		</@check>
	</div>
	<form id="searchForm" action="doMedia" method="post">
	<div class="search">
		<div class="clearfix">
			<div class="floatL">
				<#if Session["loginOrg"]?exists && Session["loginOrg"].level == 1>
				<div class="floatL">
					<label>大区：</label>
					<select id="searchRegion" name="searchRegion" class="easyui-combotree" data-options="url:'${rc.contextPath}/organization/treeRegionReport?id=${Session["loginOrg"].id}&checkedNodes=<#if searchRegion?exists>${searchRegion},</#if>',method:'get'" multiple style="width:200px; height: 30px; line-height:30px;"></select>
				</div>
				</#if>
				<label>报表内容：</label>
				<select name="reportType" class="searchselect">
					<option value="1" <#if reportType == 1>selected="selected"</#if> >线索获取情况</option>
					<option value="2" <#if reportType == 2>selected="selected"</#if> >线索处理情况</option>
					<option value="3" <#if reportType == 3>selected="selected"</#if> >400来电情况</option>
					<option value="4" <#if reportType == 4>selected="selected"</#if> >信息发布情况</option>
				</select>
			</div>
			<div class="floatL ml10">
				<div class="floatL">
					<label class="t2">合作媒体：</label>
				</div>
				
				<div class="floatL mediatj">
					<input type="checkbox" id="checkbox_all" class="chekcAll search"  <#if mediaIds?exists && mediaIds=="1,2,3">checked="checked"</#if>  />
					<label>全选</label>
				</div>					
				<#list mediaList as curMedia>
				<div class="floatL mediatj">
					<input name="mediaIds" value="${curMedia.id}" type="checkbox" class="checkR search" <#if mediaIdArray?exists && mediaIdArray?seq_contains(curMedia.id)>checked="checked"</#if>  />
					<label>${curMedia.name}</label>
				</div>
				</#list>	
			</div>
		</div>
		
		<div class="mt10 clearfix">
			<div class="floatL dateradio">
				<label style="*margin-bottom:6px; display:inline-block;">报表日期：</label>
				<input type="radio" name="searchDateType" value="1" class="mediatest search" <#if !searchDateType?exists || searchDateType == 1>checked="checked"</#if> ><span style="*margin-bottom:6px; display:inline-block;">本周</span>
				<input type="radio" name="searchDateType" value="2" class="mediatest search" <#if searchDateType == 2>checked="checked"</#if> ><span style="*margin-bottom:6px; display:inline-block;">本月</span>
				<input type="radio" name="searchDateType" value="3" class="mediatest search" <#if searchDateType == 3>checked="checked"</#if> ><span style="*margin-bottom:6px; display:inline-block;">上月</span>
			</div>
			<div class="floatL customdate">
				<input type="radio" name="searchDateType" value="4" class="mediatest search" <#if searchDateType == 4>checked="checked"</#if> >
				<label>自定义：</label>
				<input id="searchDateBegin" name="searchDateBegin" value="${searchDateBegin}" class="easyui-datebox search" style="width:150px; height: 30px;">
				<span>-</span>
				<input id="searchDateEnd" name="searchDateEnd" value="${searchDateEnd}" class="easyui-datebox search" style="width:150px; height: 30px;">
			</div>
			<div class="sumbit ml10 floatL">
				<input id="searchBtn" type="button" value="搜 索" class="submit"/>
			</div>
		</div>
	</div>
	</form>
	<#if mediaX?exists>
	<div class="sum bordertop">
		<div class="minheight" id="mainReportDiv" style="width:98%;"></div>
	</div>
	<div class="idtable total" style="margin:10px 0; padding-bottom:10px;">
		<table width="100%">
			<thead>
				<tr>
					<th>合作媒体</th>
					<th>总数</th>
				</tr>
				<#list mediasMaps as medias1>
				<tr>
					<td>${medias1.name}</td>
					<td>${medias1.mediasCount}</td>
				</tr>
				</#list>
				<tr>
					<td>总计</td>
					<td>${cout}</td>
				<tr>
			</thead>
		</table>
	</div>
	<div class="idtable">
		<p>
			<a id="exportBtn" href="javascript:void(0);" title="导出报表">导出报表</a>
		</p>
		<table width="100%" class="reportable">
			<thead>
				<tr>
					<th width="20%" data-options="field:'time',sortable:true">时间</th>
					<th width="20%" data-options="field:'mediacome',sortable:true">媒体</th>
					<th width="20%" id="th_name" data-options="field:'xsl',sortable:true">线索量</th>
				</tr>
				<#list tableMap?keys as day>
					<#assign tableDataMap=tableMap[day]>
					<#list tableDataMap?keys as curMediaName>
						<#assign mediaCountList=tableDataMap[curMediaName]>
						<#list mediaCountList as curCount>
						<tr>
							<td>${day}</td>
							<td>${curMediaName}</td>
							<td>${curCount}</td>
						</tr>
						</#list>
					</#list>
				</#list>
			</thead>
		</table>
	</div>
	</#if>
	<script type="text/javascript">
	<#if mediaX?exists>
	var mediaX = ${mediaX};
	var seriesArray = ${seriesArray};
	</#if>
	var mediaCheckLen = $("input[name='mediaIds']:checked").length;
	if(mediaCheckLen == 0){
		$('#checkbox_all').attr("checked", true);
		$("input[name='mediaIds']").each(function(){
			$(this).attr("checked", true);
		})
	}
	<#if reportType?exists>
	var reportType=${reportType};
		if(reportType==1){
			$('#th_name').html('线索获取量');
		}
		if(reportType==2){
			$('#th_name').html('线索处理量');
		}
		if(reportType==3){
			$('#th_name').html('400来电量');
		}
		if(reportType==4){
			$('#th_name').html('信息发布量');
		}
	</#if>
	</script>
</#macro>