<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#assign tag=JspTaglibs["/WEB-INF/taglib/permission.tld"]>
<#include "/layouts/main.ftl"/>
<#import "/layouts/popup.ftl" as massage>
<@html></@html>
<#macro style>
	<style>
	.addStyle{
		font-weight: bold;
		position: absolute;
		top: 10px;
		top: 9px\9;
		left: 11px;
		z-index: 10;
		color: #cc0000;
	}
	</style>
</#macro>
<#macro script>
	<script type="text/javascript" src="${rc.contextPath}/js/popup.js"></script>
	<script type="text/javascript">
		var nodeId=1;
		$(function(){
			$('#tt').tree({
				onBeforeExpand : function(node,param){
				},
				onClick: function(node){
					nodeId =node.id;
					$("#usernodeId").val(node.id);
					$("#node_type").val(node.type);
					$("#orgId").val(node.id);
					var hrefAdd = $("#user_add");
					if(node.isForbid=='active'){
						hrefAdd.attr("href","${rc.contextPath}/user/create?org.id="+node.id+"&nodeType="+node.type);
					}else{
						hrefAdd.attr("href","javascript:void(0)");
					}
					$("#formSubmit").click();
				},
				onLoadSuccess :function(node){
					if(nodeId==1){
						$("#userSubmit").triggerHandler("click");
					}
					if(!node){
						node = $('#tt').tree('getRoot');
					}
					if(node!=null){
						$('#tt').tree('expandTo', node.target).tree('select', node.target);
						nodeId =node.id;
						$("#usernodeId").val(node.id);
						$("#node_type").val(node.type);
						$("#orgId").val(node.id);
						var hrefAdd = $("#user_add");
						if(node.isForbid=='active'){
							hrefAdd.attr("href","${rc.contextPath}/user/create?org.id="+node.id+"&nodeType="+node.type);
						}else{
							hrefAdd.attr("href","javascript:void(0)");
						}
					}
				}
			});
		});
		$(function(){
			$.plugin.init("#user_form","#user_table");			
		});
		function user_submitValue(){
			window.location.href=$("#user_add").attr("href");
		}
		function inactiveCallback(json,obj){
			general_message(json.code,json.message);
			$("#formSubmit").click();
		}
		function activeCallback(json,obj){
			general_message(json.code,json.message);
			$("#formSubmit").click();
		}
		
		function deleteUserCallback(json){
			general_message(json.code,json.message);
			$("#formSubmit").click();
		}
	</script>
</#macro>
<@massage.generalMessage />
<#macro content>
	<#if permission?exists>
	<div class="floatL treeList">
		  <ul id="tt" class="easyui-tree" data-options="url:'${rc.contextPath}/organization/treeUser?checked=${parent}',method:'get',animate:true"></ul>		
	</div>
	<div class="floatL detaillist">
			<div class="tih2 clearfix">
				<p class="floatL">用户列表</p>
			</div>
			<div class="search">
			<form action="${rc.contextPath}/user/search" method="POST" id="user_form">
				<input type="hidden" id="usernodeId" name="usernodeId" value="${usernodeId}" class="key search" />
				<input type="hidden" id="node_type" name="node_type" value="${nodeType}" class="key search" />
				<input type="hidden" id="orgId" name="orgId" value="${orgId}" class="key search" />
				<div style="overflow:hidden;*height:100%;">
					<div class="floatL autoFill ieHack">
					<label>用户名称：</label>
					<input class="key search moreWidthText" type="text" name="name" style=" height: 30px;" />
				</div> 
				<div class="floatL autoFill ieHack">
					<label>用户编号：</label>
					<input class="key search moreWidthText" type="text" name="code" style="height: 30px;" />
				</div>
				<div class="floatL autoFill ieHack">
					<label>登录账号：</label>
					<input class="key search moreWidthText" type="text" name="username" style="height: 30px;" />
				</div> 
				<div class="floatL autoFill ieHack">
					<label>用户角色：</label>
					<select class="borderRadius5 key moreWidthselect search" name="roleId">
						<option selected="selected" value="">---请选择---</option>
						<#list roles as role>
							<option value="${role.id}">${role.name}</option>
							<#if user.role?exists && role.id==user.role.id>
								<option value="${role.id}" selected>${role.name}</option>
							</#if>
						</#list>
					</select>
				</div>
				<div class="floatL autoFill ieHack">
					<label>用户状态：</label>
					<@tag.select type="record_status" style="key search moreWidthselect" name="statusId" defaultValue=statusId?default(0)/>
				</div>
				<div class="sumbit floatL autoFill ieHack">
					<input type="button" class="submit" id="formSubmit" value="搜 索" />
				</div>
			</form>
		</div>
		<div class="ichtable" style="margin:10px 0 0;">
			<a  id='user_add' class="addStyle"></a>
			<table id="user_table" width="100%" class="easyui-datagrid" title="<a href='javascript:void(0)' onClick='user_submitValue()'>添加</a>" data-options="singleSelect:true,collapsible:false,method:'get',remoteSort:false,multiSort:true,pagination:true,showRefresh:false" >
				<thead>
					<tr>
						<th width="25%" data-options="field:'name',sortable:true">用户名称</th>
						<th width="20%" data-options="field:'code',sortable:true">用户编号</th>
						<th width="25%" data-options="field:'username',sortable:true">登录账号</th>
						<th width="10%" data-options="field:'status.name',sortable:true">用户状态</th>
						<th width="20%" data-options="field:'operations',sortable:false">操作</th>
					</tr>
				</thead>
			</table>
		</div>
	</div>
	</#if>
	<#if !permission?exists>
			<div class="floatL treeList">
				  <ul id="tt" class="easyui-tree" data-options="url:'${rc.contextPath}/organization/treeUser?checked=${parent}',method:'get',animate:true"></ul>		
			</div>
			<div class="floatL detaillist">
					<div class="tih2 clearfix">
						<p class="floatL">用户列表</p>
					</div>
					<div class="search">
					<form action="${rc.contextPath}/user/search" method="POST" id="user_form">
						<input type="hidden" id="usernodeId" name="usernodeId" value="${usernodeId}" class="search" />
						<input type="hidden" id="node_type" name="node_type" value="${nodeType}" class="search" />
						<div style="overflow:hidden;*height:100%;">
							<div class="floatL autoFill ieHack">
							<label>用户名称：</label>
							<input class="key search moreWidthText" type="text" name="name" style=" height: 30px;" />
						</div> 
						<div class="floatL autoFill ieHack">
							<label>用户编号：</label>
							<input class="key search moreWidthText" type="text" name="code" style="height: 30px;" />
						</div>
						<div class="floatL autoFill ieHack">
							<label>登录账号：</label>
							<input class="key search moreWidthText" type="text" name="username" style="height: 30px;" />
						</div> 
						<div class="floatL autoFill ieHack">
							<label>用户角色：</label>
							<select class="borderRadius5 key moreWidthselect search" name="roleId">
								<option selected="selected" value="">---请选择---</option>
								<#list roles as role>
									<option value="${role.id}">${role.name}</option>
									<#if user.role?exists && role.id==user.role.id>
										<option value="${role.id}" selected>${role.name}</option>
									</#if>
								</#list>
							</select>
						</div>
						<div class="floatL autoFill ieHack">
							<label>用户状态：</label>
							<@tag.select type="record_status" style="key search moreWidthselect" name="statusId" defaultValue=statusId?default(0)/>
						</div>
						<div class="sumbit floatL autoFill ieHack">
							<input type="button" class="submit" id="formSubmit" value="搜 索" />
						</div>
					</form>
				</div>
				<div class="ichtable" style="margin:10px 0 0;">
					<a  id='user_add' class="addStyle"></a>
					<table id="user_table" width="100%" class="easyui-datagrid" title="<a ></a>" data-options="singleSelect:true,collapsible:false,method:'get',remoteSort:false,multiSort:true,pagination:true,showRefresh:false" >
						<thead>
							<tr>
								<th width="25%" data-options="field:'name',sortable:true">用户名称</th>
								<th width="20%" data-options="field:'code',sortable:true">用户编号</th>
								<th width="25%" data-options="field:'username',sortable:true">登录账号</th>
								<th width="10%" data-options="field:'status.name',sortable:true">用户状态</th>
								<th width="20%" data-options="field:'operations',sortable:false">操作</th>
							</tr>
						</thead>
					</table>
				</div>
			</div>
	</#if>
	<script type="text/javascript">
		$(function(){
			$.plugin.init("#user_form","#user_table");
		});
	</script>
</#macro>
