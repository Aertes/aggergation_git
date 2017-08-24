<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#assign tag=JspTaglibs["/WEB-INF/taglib/permission.tld"]>
<#include "/layouts/main.ftl"/>
<@html></@html>
<#import "/layouts/popup.ftl" as massage>
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
		var parentId = "${parentId}";
		$(function(){
			$('#tt').tree({
				onClick: function(node){
					if(node.option==1){
						$("#parentId").val(node.id);
						$.plugin.searchByForm();
						if(node.isActive!='active'){
							setTimeout(function(){
								$('#a_deler_add').removeAttr('href');
								$('#a_deler_add').removeAttr('onclick');
								$('#a_deler_add').html('');
							}, 200);
						}else{
							$('#a_deler_add').attr('href','javascript:void(0);');
							$('#a_deler_add').attr('onclick','dealer_submitValue();');
							$('#a_deler_add').html('添加');
						}
					}
				},
				onBeforeSelect : function(node){
					if(node){
						if(node.option==0){
							return false;
						}
					}
					
				},
				onLoadSuccess:function(node, data){
					if(!node){
						node = $('#tt').tree('getRoot');
					}
					var childList = $('#tt').tree('getChildren', node.target);
					var node1 = $('#tt').tree('find',(parentId ? parentId : 1));
					if(node1!=null){
						if(node.option==1){
							$('#tt').tree('expandTo', node.target).tree('select', node1.target);
						}else{
							for(var i = 0; i < childList.length; i++){
								var childNode = childList[i];
								if(childNode.option == 1){
									if(parentId==childNode.id){
										node = $('#tt').tree('find',childNode.id)
										break ;
									}
									
								}
							}
							$('#tt').tree('expandTo', node.target).tree('select', node.target);
							if(node.isActive!='active'){
								setTimeout(function(){
									$('#a_deler_add').removeAttr('href');
									$('#a_deler_add').removeAttr('onclick');
									$('#a_deler_add').html('');
								}, 200);
								
							}
							$("#parentId").val(node.id);
						}
						
						
					}				
				}
			});
		});
		
		function dealer_submitValue(){
			var $parentId = $("#parentId");
			window.location = "create?orgId=" + ($parentId.val() ? $parentId.val() : 1);
		}
		
		function onOffCallback(json){
			general_message(json.code,json.message);
			$("#dealer_search").click();
		}
	</script>
</#macro>
<@massage.generalMessage />
<#macro content>
	<#if permission?exists>
	<div class="floatL treeList">
		  <ul id="tt" class="easyui-tree" data-options="url:'${rc.contextPath}/organization/tree?createDe=create',method:'get',animate:true"></ul>		
	</div>
	<div class="floatL detaillist">
		<div class="tih2 clearfix">
			<p class="floatL">网点列表</p>
		</div>
		<div class="search">
			<form id="dealer_form" action="${rc.contextPath}/dealer/queryDealerList.json" method="POST">
			
			<input id="parentId" class="search" type="hidden"  name="organization.id" value="${parentId}"/>
			<div class="clearfix" style="*height:100%;">
				<div class="floatL ieHack">
					<label>网点编号：</label>
					<input class="search key" name="code" type="text" style="width:150px; height: 30px;" />
				</div>
				<div class="floatL ieHack">
					<label>网点名称：</label>
					<input class="search key" type="text" name="name" style="width:150px; height: 30px;" />
				</div>
			</div>
			<div class="mt10 clearfix">
				<div class="floatL ieHack">
					<label>当前状态：</label>
					<@tag.select style="search key moreWidth" type="record_status" name="status.id" defaultValue=status.id?default(0) />
				</div>
				<div class="floatL ieHack">
					<label>网点类型：</label>
					<@tag.select style="search key moreWidth" type="dealer_type" name="type.id" defaultValue=type.id?default(0) />
				</div>
				<div class="sumbit ieHack">
					<input id="dealer_search" type="button" class="submit" value="搜 索" />
				</div>
			</div>
			</form>
		</div>
		<div class="ichtable" >
			<table id="dealer_datagrid" width="100%" class="easyui-datagrid" title="<a href='javascript:void(0)' onClick='dealer_submitValue()' id='a_deler_add'>添加</a>" data-options="singleSelect:true,collapsible:false,remoteSort:false,multiSort:true,pagination:true,showRefresh:false" >
				<thead>
					<tr>
						<th width="20%" data-options="field:'organization.name',sortable:true">所属大区</th>
						<th width="25%" data-options="field:'name',sortable:true">网点名称</th>
						<th width="10%" data-options="field:'code',sortable:true">网点编号</th>
						<th width="10%" data-options="field:'type.name',sortable:true">网点类型</th>
						<th width="10%" data-options="field:'status.name',sortable:true">当前状态</th>
						<th width="10%" data-options="field:'dateCreate',sortable:true">申请时间</th>
						<th width="15%" data-options="field:'operations',sortable:false">操作</th>
					</tr>
				</thead>
			</table>
		</div>
	</div>
	</#if>
	<#if !permission?exists>
		<div class="floatL treeList">
		  <ul id="tt" class="easyui-tree" data-options="url:'${rc.contextPath}/organization/tree',method:'get',animate:true"></ul>		
		</div>
		<div class="floatL detaillist">
			<div class="tih2 clearfix">
				<p class="floatL">网点列表</p>
			</div>
			<div class="search">
				<form id="dealer_form" action="${rc.contextPath}/dealer/queryDealerList.json" method="POST">
				
				<input id="parentId" class="search" type="hidden"  name="organization.id" value="${parentId}"/>
				<div class="clearfix" style="*height:100%;">
					<div class="floatL ieHack">
						<label>网点编号：</label>
						<input class="search key" name="code" type="text" style="width:150px; height: 30px;" />
					</div>
					<div class="floatL ieHack">
						<label>网点名称：</label>
						<input class="search key" type="text" name="name" style="width:150px; height: 30px;" />
					</div>
				</div>
				<div class="mt10 clearfix">
					<div class="floatL ieHack">
						<label>当前状态：</label>
						<@tag.select style="search key moreWidth" type="record_status" name="status.id" defaultValue=status.id?default(0) />
					</div>
					<div class="floatL ieHack">
						<label>网点类型：</label>
						<@tag.select style="search key moreWidth" type="dealer_type" name="type.id" defaultValue=type.id?default(0) />
					</div>
					<div class="sumbit ieHack">
						<input id="dealer_search" type="button" class="submit" value="搜 索" />
					</div>
				</div>
				</form>
			</div>
			<div class="ichtable" >
				<table id="dealer_datagrid" width="100%" class="easyui-datagrid" title="<a ></a>" data-options="singleSelect:true,collapsible:false,remoteSort:false,multiSort:true,pagination:true,showRefresh:false" >
					<thead>
						<tr>
							<th width="20%" data-options="field:'organization.name',sortable:true">所属大区</th>
							<th width="25%" data-options="field:'name',sortable:true">网点名称</th>
							<th width="10%" data-options="field:'code',sortable:true">网点编号</th>
							<th width="10%" data-options="field:'type.name',sortable:true">网点类型</th>
							<th width="10%" data-options="field:'status.name',sortable:true">当前状态</th>
							<th width="10%" data-options="field:'dateCreate',sortable:true">申请时间</th>
							<th width="15%" data-options="field:'operations',sortable:true">操作</th>
						</tr>
					</thead>
				</table>
			</div>
		</div>
	</#if>
	<script type="text/javascript">
		$(function(){
		$.plugin.init("#dealer_form","#dealer_datagrid");
		});
	</script>
</#macro>



