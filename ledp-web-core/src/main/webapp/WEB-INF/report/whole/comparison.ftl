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
	<script type="text/javascript" src="${rc.contextPath}/js/report/report_whole_comparison.js"></script>
</#macro>
<#macro content>
	<div class="tih2 clearfix">
		<p class="floatL">总部-大区对比</p>
		<@check permissionCodes = permissions permissionCode="report/whole/media">
		<p class="floatR tabsnav">
			<a href="${rc.contextPath}/report/whole/media" title="">媒体分析</a>
		</p>
		</@check>
		<@check permissionCodes = permissions permissionCode="report/whole/comparison">
		<p class="floatR tabsnav">
			<a href="${rc.contextPath}/report/whole/index" title="">总览</a>
		</p>
		</@check>
	</div>
	<form id="searchForm" action="doComparison" method="post">
	<input type="hidden" id="searchRegionHid" name="searchRegion" value="${searchRegion}" class="search"/>
	<div class="search">
		<div class="clearfix">
			<div class="floatL">
				<label>大区：</label>
				<select id="searchRegion" class="easyui-combotree" data-options="url:'${rc.contextPath}/organization/treeRegionReport?id=1&checkedNodes=<#if searchRegion?exists>${searchRegion},</#if>',method:'get'" multiple style="width:200px; height: 30px; line-height:30px;"></select>
			</div>
			<div class="floatL ml10">
				<label style="*margin-bottom:6px; *display:inline-block;">报表内容：</label>
				<select id="reportType" name="reportType" class="searchselect">
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
				<label style="*display:inline-block; margin-bottom:6px;">报表日期：</label>
				<input type="radio" name="searchDateType" value="1" class="mediatest search" <#if !searchDateType?exists || searchDateType == 1>checked="checked"</#if> ><span style="*display:inline-block; *margin-bottom:6px;">本周</span>
				<input type="radio" name="searchDateType" value="2" class="mediatest search" <#if searchDateType == 2>checked="checked"</#if> ><span style="*display:inline-block; *margin-bottom:6px;">本月</span>
				<input type="radio" name="searchDateType" value="3" class="mediatest search" <#if searchDateType == 3>checked="checked"</#if> ><span style="*display:inline-block; *margin-bottom:6px;">上月</span>
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
	<div class="sum bordertop">
		<#if regionX?exists>
		<div id="mainReportDiv" class="minheight" style="width:98%;"></div>
		</#if>
		<#if mediasMaps?exists>
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
			</thead>
		</table>
	</div>
	</#if>
		<!-- 
		<div class="underlist">
			<div class="detailcolor">
				<span>
					<em class="jxs1"></em>
					<span>山西东联汽车销售有限公司</span>
				</span>
				<span>
					<em class="jxs2"></em>
					<span>山西汽车销售有限公司</span>
				</span>
			</div>
		</div>
		-->
	</div>
	<#if regionX?exists>
	<div class="idtable">
		<p>
			<a id="exportBtn" href="javascript:void(0);" title="导出报表">导出报表</a>
		</p>
		<table id="report_table" class="reportable" width="100%">
			<thead>
				<tr>
					<th width="20%" data-options="field:'time',sortable:true">时间</th>
					<th width="20%" data-options="field:'mediacome',sortable:true">大区</th>
					<th width="20%" data-options="field:'mediacome',sortable:true">媒体</th>
					<th width="20%" id="dq_th_name" data-options="field:'dqxsl',sortable:true">大区线索量</th>
					<th width="20%" id="com_th_name" data-options="field:'zbxsl',sortable:true">总部线索量均值</th>
				</tr>
				<#list tableList as curItem>
				<tr>
					<td>${curItem.day}</td>
					<td>${curItem.regionName}</td>
					<td>${curItem.mediaName}</td>
					<td>${curItem.regionCount}</td>
					<td>${curItem.allCount}</td>
				</tr>
				</#list>
			</thead>
		</table>
	</div>
	</#if>
	<script type="text/javascript">
	<#if regionX?exists>
	var regionX = ${regionX};
	var region1Y = ${region1Y};
	var region2Y = ${region2Y};
	var region3Y = ${region3Y};
	var regionNames = '${regionNames}';
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
			$('#dq_th_name').html('大区线索获取量');
			$('#com_th_name').html('全国大区线索获取量均值');
		}
		if(reportType==2){
			$('#dq_th_name').html('大区线索处理量');
			$('#com_th_name').html('全国大区线索处理量均值');
		}
		if(reportType==3){
			$('#dq_th_name').html('大区400来电量');
			$('#com_th_name').html('全国大区400来电量均值');
		}
		if(reportType==4){
			$('#dq_th_name').html('大区信息发布量');
			$('#com_th_name').html('全国大区信息发布量均值');
		}
	</#if>
	</script>
</#macro>