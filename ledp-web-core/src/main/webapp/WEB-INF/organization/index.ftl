<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#assign tag=JspTaglibs["/WEB-INF/taglib/permission.tld"]>
<#include "/layouts/main.ftl"/>
<#import "/layouts/popup.ftl" as massage>
<@html></@html>
<#macro style></#macro>
<#macro script>
	<script type="text/javascript" src="${rc.contextPath}/js/popup.js"></script>
	<script type="text/javascript">
		var parentId = "${parent}";
		$(function(){
			$('#tt').tree({
				onClick: function(node){
					if(node.option=="1"){
						$("#parentId").val(node.id);
						$("#parentName").html(node.text);
						if(node.id==1){
							var hrefAdd = $("#organization_add");
							hrefAdd.attr("href","${rc.contextPath}/organization/create?parent.id="+node.id);
						}else{
							setTimeout(function(){
								var hrefAdd = $("#organization_add");
								hrefAdd.attr("href","");
								hrefAdd.html('');
							}, 50);
							
						}
						$("#formSubmit").click();
					}
					
				},
				onBeforeSelect : function(node){
					if(node.option==0){
						return false;
					}
				},
				onLoadSuccess:function(node, data){
					var node = $('#tt').tree('find',(parentId ? parentId : 1));
					if(node!=null){
						$('#tt').tree('expandTo', node.target).tree('select', node.target);
					}				
				}
			});
		});
		$(function(){
			$.plugin.init("#organization_form","#organization_table");			
		});
		function inactiveCallback(json){
			general_message(json.code,json.message);
			$("#formSubmit").click();
		}
		function activeCallback(json){
			general_message(json.code,json.message);
			$("#formSubmit").click();
		}
	</script>
</#macro>
<@massage.generalMessage />
<#macro content>
	<#if permission?exists>
	<div class="floatL treeList">
		  <ul id="tt" class="easyui-tree" data-options="url:'${rc.contextPath}/organization/tree?checked=${parent}',method:'get',animate:true"></ul>		
	</div>
	<div class="floatL detaillist">
		<div class="tih2">
			<p>组织列表</p>
		</div>
		<div class="search">
			<form action="${rc.contextPath}/organization/search" method="POST" id="organization_form">
				<div style="overflow:hidden;*height:38px;">
					<div class="floatL autoFill ieHack" style="width: 260px;">
						<label>组织名称：</label>
						<input class="key search" type="text" name="name" value="${name?if_exists}" style="width:150px; height: 30px;" />
					</div>
					<div class="floatL autoFill ieHack" style="width: 260px;">
						<label>组织状态：</label>
						<@tag.select type="record_status" style="key search moreWidth1" name="statusId" defaultValue=statusId?default(0)/>
					</div>
					<div class="sumbit floatL autoFill ieHack" style="width:100px">
						<input type="hidden" class="key search" name="parent" value="${parent}" id="parentId"/>
						<input type="button" value="搜 索" class="submit" id="formSubmit"/>
					</div>
				</div>
			</form>
		</div>
		<div class="ichtable">
			<table id="organization_table" width="100%" class="easyui-datagrid" title="<a href='${rc.contextPath}/organization/create?parent.id=${parent}' id='organization_add'>添加</a>" data-options="singleSelect:true,collapsible:false,method:'get',remoteSort:false,multiSort:true,pagination:true,showRefresh:false">
				<thead>
					<tr>
						<th width="25%" data-options="field:'name',sortable:true">组织名称</th>
						<th width="25%" data-options="field:'parent.name',sortable:true">上级机构</th>
						<th width="25%" data-options="field:'status.name',sortable:true">组织状态</th>
						<th width="25%" data-options="field:'operations',sortable:false">操作</th>
					</tr>
				</thead>
			</table>
		</div>
	</div>
	</#if>
	<#if !permission?exists>
		<div class="floatL treeList">
		  <ul id="tt" class="easyui-tree" data-options="url:'${rc.contextPath}/organization/tree?checked=${parent}',method:'get',animate:true"></ul>		
		</div>
		<div class="floatL detaillist">
			<div class="tih2">
				<p>组织列表</p>
			</div>
			<div class="search">
				<form action="${rc.contextPath}/organization/search" method="POST" id="organization_form">
					<div style="overflow:hidden;*height:38px;">
						<div class="floatL autoFill ieHack" style="width: 260px;">
							<label>组织名称：</label>
							<input class="key search" type="text" name="name" value="${name?if_exists}" style="width:150px; height: 30px;" />
						</div>
						<div class="floatL autoFill ieHack" style="width: 260px;">
							<label>组织状态：</label>
							<@tag.select type="record_status" style="key search moreWidth1" name="statusId" defaultValue=statusId?default(0)/>
						</div>
						<div class="sumbit floatL autoFill ieHack" style="width:100px">
							<input type="hidden" class="key search" name="parent" value="${parent}" id="parentId"/>
							<input type="button" value="搜 索" class="submit" id="formSubmit"/>
						</div>
					</div>
				</form>
			</div>
			<div class="ichtable">
				<table id="organization_table" width="100%" class="easyui-datagrid" title="<a ></a>" data-options="singleSelect:true,collapsible:false,method:'get',remoteSort:false,multiSort:true,pagination:true,showRefresh:false">
					<thead>
						<tr>
							<th width="25%" data-options="field:'name',sortable:true">组织名称</th>
							<th width="25%" data-options="field:'parent.name',sortable:true">上级机构</th>
							<th width="25%" data-options="field:'status.name',sortable:true">组织状态</th>
							<th width="25%" data-options="field:'operations',sortable:true">操作</th>
						</tr>
					</thead>
				</table>
			</div>
		</div>
	</#if>
</#macro>