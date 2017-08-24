<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#include "/layouts/main.ftl"/>
<@html></@html>
<#import "/layouts/popup.ftl" as massage>
<#macro style>
<style>
	div .searchselect{
		*height:34px;
	}
</style>
</#macro>
<#macro script>
	<script type="text/javascript" src="${rc.contextPath}/js/popup.js"></script>
	<script type="text/javascript">
		$(function(){
			$.plugin.init("#vehicleSeries","#vehicleSeriesid");			
		});
		function inactiveCallback(json){
			general_message(json.code,json.message);
			$("#formSubmit").click();
		}
		function activeCallback(json){
			general_message(json.code,json.message);
			$("#formSubmit").click();
		}
		$(".edit,.ONOFF").delegate(function(){
			$(".window-mask").css("height",document.body.clientHeight);
		});
	</script>
</#macro>
<@massage.generalMessage />
<#macro content>
	<#if permission?exists>
	<div class="detaillist ollright">
		<div class="tih2 clearfix">
			<p class="floatL">车系管理</p>
		</div>
		<div class="search">
			<form  id="vehicleSeries" action="${rc.contextPath}/vehicleSeries/vehicleSeries.json" method="POST">
				<div style="overflow:hidden;*height:40px;">
					<div class="floatL autoFill ieHack" style="width:auto;">
						<label>车系名称：</label>
						<input type="text"  name="name"  maxlength="30"  class="search key" style="width:120px;"  />
					</div>
					<div class="floatL autoFill ieHack" style="width:auto;">
						<label>车系代码：</label>
						<input type="text" name="code"   maxlength="30" class="search key" style="width:120px;" />
					</div>
					<div class="floatL autoFill ieHack" style="width:auto;">
						<label>车系状态：</label>
						<select name="status" class="searchselect search" style="width:120px;">
							<option value="" >-- 请选择 --</option>
							<option value="active" >启用</option>
							<option value="inactive" >禁用</option>
						</select>
					</div>
					<div class="sumbit floatL autoFill ieHack" style="width:100px">
						<input type="button" class="submit search" value="搜 索"  id="formSubmit" />
					</div>
				</div>
			</form>
		</div>
		
		<div class="ichtable">
			<table id="vehicleSeriesid" class="easyui-datagrid" title="<a href='${rc.contextPath}/vehicleSeries/create' title=''>添加</a>" data-options="singleSelect:true,collapsible:false,method:'get',remoteSort:false,multiSort:true,pagination:true,showRefresh:false">
				<thead>
					<tr>
						<#if loginDealer?exists>
							<th width="30%" data-options="field:'name',sortable:true">车系名称</th>
							<th width="20%" data-options="field:'code',sortable:true">车系代码</th>
							<th width="15%" data-options="field:'status.name',sortable:true">车系状态</th>
							<th width="15%" data-options="field:'onsale'">在售状态</th>
							<th width="20%" data-options="field:'operations',sortable:false">操作</th>
						<#else>
							<th width="30%" data-options="field:'name',sortable:true">车系名称</th>
							<th width="25%" data-options="field:'code',sortable:true">车系代码</th>
							<th width="25%" data-options="field:'status.name',sortable:true">车系状态</th>
							<th width="20%" data-options="field:'operations',sortable:false">操作</th>
						</#if>
					</tr>
				</thead>
			</table>
		</div>
	</div>
	</#if>
	<#if !permission?exists>
		<div class="detaillist ollright">
			<div class="tih2 clearfix">
				<p class="floatL">车系管理</p>
			</div>
			<div class="search">
				<form  id="vehicleSeries" action="${rc.contextPath}/vehicleSeries/vehicleSeries.json" method="POST">
					<div style="overflow:hidden;*height:40px;">
						<div class="floatL autoFill ieHack" style="width:auto;">
							<label>车系名称：</label>
							<input type="text"  name="name"  maxlength="30"  class="search key" style="width:120px;"  />
						</div>
						<div class="floatL autoFill ieHack" style="width:auto;">
							<label>车系代码：</label>
							<input type="text" name="code"   maxlength="30" class="search key" style="width:120px;" />
						</div>
						<div class="floatL autoFill ieHack" style="width:auto;">
							<label>车系状态：</label>
							<select name="status" class="searchselect search" style="width:120px;">
								<option value="" >-- 请选择 --</option>
								<option value="active" >启用</option>
								<option value="inactive" >禁用</option>
							</select>
						</div>
						<div class="sumbit floatL autoFill ieHack" style="width:100px">
							<input type="button" class="submit search" value="搜 索"  id="formSubmit" />
						</div>
					</div>
				</form>
			</div>
			
			<div class="ichtable">
				<table id="vehicleSeriesid" width="98%" class="easyui-datagrid" title="<a></a>" data-options="singleSelect:true,collapsible:false,method:'get',remoteSort:false,multiSort:true,pagination:true,showRefresh:false">
					<thead>
						<tr>
						<#if loginDealer?exists>
							<th width="30%" data-options="field:'name',sortable:true">车系名称</th>
							<th width="20%" data-options="field:'code',sortable:true">车系代码</th>
							<th width="15%" data-options="field:'status.name',sortable:true">车系状态</th>
							<th width="15%" data-options="field:'onsale'">在售状态</th>
							<th width="20%" data-options="field:'operations',sortable:false">操作</th>
						<#else>
							<th width="30%" data-options="field:'name',sortable:true">车系名称</th>
							<th width="25%" data-options="field:'code',sortable:true">车系代码</th>
							<th width="25%" data-options="field:'status.name',sortable:true">车系状态</th>
							<th width="20%" data-options="field:'operations',sortable:false">操作</th>
						</#if>
						</tr>
					</thead>
				</table>
			</div>
		</div>
	</#if>
</#macro>
