<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#include "/layouts/main.ftl"/>
<@html></@html>
<#macro style>
<style>
	.addStyle{
		font-weight: bold;
		position: absolute;
		top: 9px;
		left: 11px;
		z-index: 10;
		color: #cc0000;
	}
</style>
</#macro>
<#macro script>
	<script type="text/javascript" src="${rc.contextPath}/js/popup.js"></script>
	<script type="text/javascript">
		var mappingTypeId;
		$(function(){
			$.plugin.init("#mapping_form","#mapping_table");			
		});
		$(function(){
			$('#tt').tree({
				onClick: function(node){
					$("#typeId").val(node.id);
					mappingTypeId=node.id;
					$("#navName").html(node.text+"代码列表");
					$("#headName").html(node.text+"名称");
					$("#mappingName").html("东雪"+node.text+"名称：");
					$("#mappingCode").html("东雪"+node.text+"代码：");
					$("#name").val("");
					$("#code").val("");
					$("#mapping_add").attr("href","${rc.contextPath}/mapping/create?type.id="+node.id);
					$(".submit").trigger("click");
				},
				onLoadSuccess :function(node){
					if(!node){
						node = $('#tt').tree('getRoot');
					}
					if(node!=null){
						$('#tt').tree('expandTo', node.target).tree('select', node.target);
						$("#typeId").val(node.id);
						mappingTypeId=node.id;
						$("#navName").html(node.text+"代码列表");
						$("#headName").html(node.text+"名称");
						$("#mappingName").html("东雪"+node.text+"名称：");
						$("#mappingCode").html("东雪"+node.text+"代码：");
						$("#name").val("");
						$("#code").val("");
						$("#mapping_add").attr("href","${rc.contextPath}/mapping/create?type.id="+node.id);
						$(".submit").trigger("click");
					}
				}
			});
		});
		function submitValue(){
			window.location.href=$("#mapping_add").attr("href");
		}
		function activeCallback(json){
			general_message(json.code,json.message);
			$("#formSubmit").click();
		}
	</script>
</#macro>
<#macro content>
	<#if permission?exists>
	<div class="floatL treeList">
		  <ul id="tt" class="easyui-tree" data-options="url:'${rc.contextPath}/mapping/type',method:'get',animate:true"></ul>		
	</div>
	<div class="detaillist floatL">
		<div class="tih2 clearfix">
			<p class="floatL" id="navName">网点代码列表</p>
		</div>
		<div class="search">
			<form action="${rc.contextPath}/mapping/search" method="POST" id="mapping_form">
			<input type="hidden" id="typeId" name="type.id" value="${type.id}" class="search" />
			<div class="clearfix" style="*height:100%;">
				<div class="floatL ieHack">
					<label id="mappingName">东雪名称：</label>
					<input class="key search" type="text" name="name" style="width:150px; height: 30px;" id="name"/>
				</div>
				<div class="floatL ieHack">
					<label id="mappingCode">东雪代码：</label>
					<input class="key search" type="text" name="code1" style="width:150px; height: 30px;" id="code" />
				</div>
				<div class="sumbit floatL ieHack">
					<input type="button" class="submit" value="搜 索" id="formSubmit"/>
				</div>
			</div>
			</form>
		</div>
		<div class="ichtable" >
			<a  id='mapping_add' class="addStyle"></a>
			<table id="mapping_table" width="100%" class="easyui-datagrid" title="<a href='javascript:void(0)' onClick='submitValue()'>添加</a>" data-options="singleSelect:true,collapsible:false,method:'get',remoteSort:false,multiSort:true,pagination:true,showRefresh:false" >
				<thead>
					<tr>
						<th width="14%" data-options="field:'typeName',sortable:true">代码类型</th>
						<th width="35%" data-options="field:'name',sortable:true">东雪名称</th>
						<th width="10%" data-options="field:'code',sortable:true">东雪代码</th>
						<th width="10%" data-options="field:'code1',sortable:true">官网代码</th>
						<th width="10%" data-options="field:'code3',sortable:true">易车网代码</th>
						<th width="11%" data-options="field:'code2',sortable:true">汽车之家代码</th>
						<th width="10%" data-options="field:'operations',sortable:false">操作</th>
					</tr>
				</thead>
			</table>
		</div>
	</div>
	</#if>
	<#if !permission?exists>
		<div class="floatL treeList">
		  <ul id="tt" class="easyui-tree" data-options="url:'${rc.contextPath}/mapping/type',method:'get',animate:true"></ul>		
		</div>
		<div class="detaillist floatL">
			<div class="tih2 clearfix">
				<p class="floatL" id="navName">网点代码列表</p>
			</div>
			<div class="search">
				<form action="${rc.contextPath}/mapping/search" method="POST" id="mapping_form">
				<input type="hidden" id="typeId" name="type.id" value="${type.id}" class="search" />
				<div class="clearfix" style="*height:100%;">
					<div class="floatL ieHack">
						<label id="mappingName">东雪名称：</label>
						<input class="key search" type="text" name="name" style="width:150px; height: 30px;" id="name"/>
					</div>
					<div class="floatL ieHack">
						<label id="mappingCode">东雪代码：</label>
						<input class="key search" type="text" name="code1" style="width:150px; height: 30px;" id="code" />
					</div>
					<div class="sumbit floatL ieHack">
						<input type="button" class="submit" value="搜 索" id="formSubmit"/>
					</div>
				</div>
				</form>
			</div>
			<div class="ichtable" >
				<a  id='mapping_add' class="addStyle"></a>
				<table id="mapping_table" width="100%" class="easyui-datagrid" title="<a></a>" data-options="singleSelect:true,collapsible:false,method:'get',remoteSort:false,multiSort:true,pagination:true,showRefresh:false" >
					<thead>
						<tr>
							<th width="15%" data-options="field:'typeName',sortable:true">代码类型</th>
							<th width="35%" data-options="field:'name',sortable:true">东雪名称</th>
							<th width="10%" data-options="field:'code',sortable:true">东雪代码</th>
							<th width="10%" data-options="field:'code1',sortable:true">官网代码</th>
							<th width="10%" data-options="field:'code3',sortable:true">易车网代码</th>
							<th width="10%" data-options="field:'code2',sortable:true">汽车之家代码</th>
							<th width="10%" data-options="field:'operations',sortable:true">操作</th>
						</tr>
					</thead>
				</table>
			</div>
		</div>
	</#if>
</#macro>