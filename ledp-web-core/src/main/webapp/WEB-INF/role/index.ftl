<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#assign tag=JspTaglibs["/WEB-INF/taglib/permission.tld"]>
<#include "/layouts/main.ftl"/>
<@html></@html>
<#import "/layouts/popup.ftl" as massage>
<#macro style></#macro>
<#macro script>
	<script type="text/javascript" src="${rc.contextPath}/js/popup.js"></script>
	<script type="text/javascript">
		$(function(){
			$.plugin.init("#role_form","#role_table");			
		});
		function inactiveCallback(json){
			general_message(json.code,json.message);
			$(".submit").click();
		}
		function activeCallback(json){
			general_message(json.code,json.message);
			$(".submit").click();
		}
	</script>
</#macro>
<@massage.generalMessage />
<#macro content>
	<#if permission?exists>
	<div class="search">
		<form action="${rc.contextPath}/role/search" method="POST" id="role_form">
		<div class="clearfix" style="*height:100%;">
			<div class="floatL ieHack">
				<label>角色名称：</label>
				<input class="key search" type="text" name="name" value="${name}" style="width:150px; height: 30px;" />
			</div>
			<div class="floatL ieHack">
				<label>角色状态：</label>
				<@tag.select type="record_status" style="key search" name="statusId" defaultValue=statusId?default(0)/>
			</div>
			<div class="sumbit ieHack">
				<input type="button" class="submit" value="搜 索" id="formSubmit"/>
			</div>
		</div>
		</form>
	</div>
	<div class="ichtable">
		<table id="role_table" width="100%" class="easyui-datagrid" title="<a href='${rc.contextPath}/role/create' title=''>添加</a>" data-options="singleSelect:true,collapsible:false,method:'get',remoteSort:false,multiSort:true,pagination:true,showRefresh:false" >
			<thead>
				<tr>
					<th width="35%" data-options="field:'name',sortable:true">角色名称</th>
					<th width="35%" data-options="field:'status.name',sortable:true">角色状态</th>
					<th width="30%" data-options="field:'operations',sortable:false">操作</th>
				</tr>
			</thead>
		</table>
	</div>
	</#if>
	<#if !permission?exists>
		<div class="search">
			<form action="${rc.contextPath}/role/search" method="POST" id="role_form">
			<div class="clearfix" style="*height:100%;">
				<div class="floatL ieHack">
					<label>角色名称：</label>
					<input class="key search" type="text" name="name" value="${name}" style="width:150px; height: 30px;" />
				</div>
				<div class="floatL ieHack">
					<label>角色状态：</label>
					<@tag.select type="record_status" style="key search" name="statusId" defaultValue=statusId?default(0)/>
				</div>
				<div class="sumbit ieHack">
					<input type="button" class="submit" value="搜 索" id="formSubmit"/>
				</div>
			</div>
			</form>
		</div>
		<div class="ichtable">
			<table id="role_table" width="100%" class="easyui-datagrid" title="<a ></a>" data-options="singleSelect:true,collapsible:false,method:'get',remoteSort:false,multiSort:true,pagination:true,showRefresh:false" >
				<thead>
					<tr>
						<th width="35%" data-options="field:'name',sortable:true">角色名称</th>
						<th width="35%" data-options="field:'status.name',sortable:true">角色状态</th>
						<th width="30%" data-options="field:'operations',sortable:true">操作</th>
					</tr>
				</thead>
			</table>
		</div>
	</#if>
</#macro>