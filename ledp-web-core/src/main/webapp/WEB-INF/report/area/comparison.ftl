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
	<script type="text/javascript" src="${rc.contextPath}/js/report/report_area_comparison.js"></script>
	<script type="text/javascript" src="${rc.contextPath}/js/report/jquery.bigautocomplete.js"></script>
	<script>
		$(function(){
		    $("#auto_tt1").bigAutocomplete({
		    	width:270,
		    	url : "${rc.contextPath}/report/area/autocomplete",
		    	callback:function(data){
		    		$("#autoHiddenId1").val(data.id);
				}
			});
			 $("#auto_tt2").bigAutocomplete({
		    	width:270,
		    	url : "${rc.contextPath}/report/area/autocomplete",
		    	callback:function(data){
		    		$("#autoHiddenId2").val(data.id);
				}
			});
		});
	</script>
</#macro>
<#macro content>
	<div class="tih2 clearfix">
		<p class="floatL">网点对比</p>
		<@check permissionCodes = permissions permissionCode="report/area/media">
		<p class="floatR tabsnav">
			<a href="${rc.contextPath}/report/area/media" title="">媒体分析</a>
		</p>
		</@check>
		<@check permissionCodes = permissions permissionCode="report/area/index">
		<p class="floatR tabsnav">
			<a href="${rc.contextPath}/report/area/index" title="">大区总览</a>
		</p>
		</@check>
	</div>
	<form id="searchForm" action="doComparison" method="post">
	<input type="hidden" id="searchDealerHid" name="searchDealer" value="${searchDealer}" class="search"/>
	<div class="search">
		<div class="clearfix">
			<div class="floatL ieHack">
				<label>选择网点：</label>
				<!--<select id="searchDealer" class="easyui-combotree" data-options="url:'${rc.contextPath}/organization/treeDealerReport?id=${Session["loginOrg"].id}&checkedNodes=<#if searchDealer?exists>${searchDealer},</#if>',method:'get'" multiple style="width:200px; height: 30px; line-height:30px;"></select>-->
				<input type="text" class="key" id="auto_tt1" relevanceHiddenId="autoHiddenId1" value="${dealer1.name}" autocomplete="off"/>
				<input type="hidden" class="search" id="autoHiddenId1" name="dealer1" value="${dealer1.id}"/>
				
				<input type="text" class="key"  id="auto_tt2" relevanceHiddenId="autoHiddenId2" value="${dealer2.name}" autocomplete="off"/>
				<input type="hidden" class="search" id="autoHiddenId2" name="dealer2" value="${dealer2.id}" />
			</div>
			<div class="floatL ml10 ieHack">
				<label>报表内容：</label>
				<select id="reportType" name="reportType" class="searchselect">
					<option value="1" <#if reportType == 1>selected="selected"</#if> >线索获取情况</option>
					<option value="2" <#if reportType == 2>selected="selected"</#if> >线索处理情况</option>
					<option value="3" <#if reportType == 3>selected="selected"</#if> >400来电情况</option>
					<option value="4" <#if reportType == 4>selected="selected"</#if> >信息发布情况</option>
				</select>
			</div>
		</div>
		<div class="mt10 clearfix">
			<div class="floatL dateradio ieHack">
				<label style="*margin-bottom:6px; display:inline-block;">报表日期：</label>
				<input type="radio" name="searchDateType" value="1" class="mediatest search" <#if !searchDateType?exists || searchDateType == 1>checked="checked"</#if> ><span style="*margin-bottom:6px; display:inline-block;">本周</span>
				<input type="radio" name="searchDateType" value="2" class="mediatest search" <#if searchDateType == 2>checked="checked"</#if> ><span style="*margin-bottom:6px; display:inline-block;">本月</span>
				<input type="radio" name="searchDateType" value="3" class="mediatest search" <#if searchDateType == 3>checked="checked"</#if> ><span style="*margin-bottom:6px; display:inline-block;">上月</span>
			</div>
			<div class="floatL customdate ieHack">
				<input type="radio" name="searchDateType" value="4" class="mediatest search" <#if searchDateType == 4>checked="checked"</#if> >
				<label>自定义：</label>
				<input id="searchDateBegin" name="searchDateBegin" value="${searchDateBegin}" class="easyui-datebox search" style="width:150px; height: 30px;">
				<span>-</span>
				<input id="searchDateEnd" name="searchDateEnd" value="${searchDateEnd}" class="easyui-datebox search" style="width:150px; height: 30px;">
			</div>
		</div>
		<div class="mt10 clearfix">
			<div class="floatL">
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
			<div class="sumbit ml10 floatL ieHack">
				<input id="searchBtn" type="button" value="搜 索" class="submit"/>
			</div>
		</div>
	</div>
	</form>
	<div class="sum bordertop">
		<#if dealerX?exists>
		<div class="minheight" id="mainReportDiv" style="width:98%;"></div>
		</#if>
	</div>
	<#if dealerX?exists>
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
	<div class="idtable">
		<p>
			<a id="exportBtn" href="javascript:void(0);" title="导出报表">导出报表</a>
		</p>
		<table id="report_table" class="reportable" width="100%">
			<thead>
				<tr>
					<th width="20%" data-options="field:'time',sortable:true">时间</th>
					<th width="20%" data-options="field:'mediacome',sortable:true">网点</th>
					<th width="20%" data-options="field:'mediacome',sortable:true">媒体</th>
					<th width="20%" id="com_th_name" data-options="field:'dqxsl',sortable:true">网点线索量</th>
					<th width="20%" id="dq_th_name" data-options="field:'dqxsl',sortable:true">大区线索量均值</th>
				</tr>
				<#list tableList as curItem>
				<tr>
					<td>${curItem.day}</td>
					<td>${curItem.dealerName}</td>
					<td>${curItem.mediaName}</td>
					<td>${curItem.dealerCount}</td>
					<td>${curItem.allCount}</td>
				</tr>
				</#list>
			</thead>
		</table>
	</div>
	</#if>
	<script type="text/javascript">
		var name_avg='${name_avg}';
		var name_table_th='';
		if(name_avg=='大区均值'){
			name_table_th='大区';
		}else{
			name_table_th='全国';
		}
		<#if dealerX?exists>
		var dealerX = ${dealerX};
		var dealer1Y = ${dealer1Y};
		var dealer2Y = ${dealer2Y};
		var dealer3Y = ${dealer3Y};
		var dealerNames = '${dealerNames}';
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
				$('#com_th_name').html('网点线索获取量');
				$('#dq_th_name').html(name_table_th+'线索获取量均值');
			}
			if(reportType==2){
				$('#com_th_name').html('网点线索处理量');
				$('#dq_th_name').html(name_table_th+'线索处理量均值');
			}
			if(reportType==3){
				$('#com_th_name').html('网点400来电量');
				$('#dq_th_name').html(name_table_th+'400来电量均值');
			}
			if(reportType==4){
				$('#com_th_name').html('网点信息发布量');
				$('#dq_th_name').html(name_table_th+'信息发布量均值');
			}
		</#if>
	</script>
</#macro>