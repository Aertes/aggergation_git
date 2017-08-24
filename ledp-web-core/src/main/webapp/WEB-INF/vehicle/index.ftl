<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#include "/layouts/main.ftl"/>
<@html></@html>
<#import "/layouts/popup.ftl" as massage>
<#macro style></#macro>
<#macro script></#macro>
<@massage.generalMessage />
<#macro content>
	<#if permission?exists>
	<div class="floatL treeList">
		  <ul id="tt" class="easyui-tree" data-options="url:'${rc.contextPath}/vehicle/tree?checked=${series}',method:'get',animate:true"></ul>		
	</div>
	
	<div class="detaillist floatL">
		<div class="tih2 clearfix">
			<p class="floatL">车型管理</p>
		</div>
		
		<div class="search">
			<form  id="vehicle" action="${rc.contextPath}/vehicle/vehicle.json" method="POST">
				<input id="parentId" class="search" type="hidden" name="seriesId" value="${series}"/>
				<div style="overflow: hidden;*height:100%;">
					<div class="floatL autoFill ieHack">
						<label>车型名称：</label>
						<input type="text"  name="name"  maxlength="30"  class="search key moreWidthText"/>
					</div>
					<div class="floatL autoFill ieHack">
						<label>车型代码：</label>
						<input type="text" name="code"   maxlength="30" class="search key moreWidthText"/>
					</div>
					<div class="floatL autoFill ieHack">
						<label>车型状态：</label>
						<select name="status" class="searchselect search moreWidthselect">
							<option value="" >-- 请选择 --</option>
							<option value="active" >启用</option>
							<option value="inactive" >禁用</option>
						</select>
					</div>
					<div class="sumbit floatL autoFill ieHack" style="width:100px" >
						<input type="button" class="submit search" value="搜 索" id="formSubmit" />
					</div>
				</div>
			</form>
		</div>
		<div class="ichtable">
			<table id="vehicleid" width="98%" class="easyui-datagrid" title="<a href='javascript:void(0);' onclick='add_vehicle();' id='organization_add'>添加</a>" data-options="singleSelect:true,collapsible:false,method:'get',remoteSort:false,multiSort:true,pagination:true,showRefresh:false">
				<thead>
					<tr>
						<#if loginDealer?exists>
							<th width="20%" data-options="field:'series.name',sortable:true">归属车系</th>
							<th width="30%" data-options="field:'name',sortable:true">车型名称</th>
							<th width="15%" data-options="field:'code',sortable:true">车型代码</th>
							<th width="10%" data-options="field:'status.name',sortable:true">车型状态</th>
							<th width="10%" data-options="field:'onsale'">在售状态</th>
							<th width="15%" data-options="field:'operations',sortable:false">操作</th>
						<#else>
							<th width="20%" data-options="field:'series.name',sortable:true">归属车系</th>
							<th width="35%" data-options="field:'name',sortable:true">车型名称</th>
							<th width="15%" data-options="field:'code',sortable:true">车型代码</th>
							<th width="15%" data-options="field:'status.name',sortable:true">车型状态</th>
							<th width="15%" data-options="field:'operations',sortable:false">操作</th>
						</#if>
					</tr>
				</thead>
			</table>
		</div>
	</div>
	
	<div id="jy" class="easyui-window" title="消息框" data-options="modal:true,closed:true,iconCls:'icon-save'" style="width:500px;height:150px;padding:10px;">
		<p>确认要禁用么？</p>
		<div data-options="region:'south',border:false" class="delbtn">
			<a class="easyui-linkbutton btnstyle" data-options="iconCls:'icon-ok'" href="javascript:void(0)" style="width:100px">确认</a>
			<a class="easyui-linkbutton cancel" data-options="iconCls:'icon-cancel'" href="javascript:void(0)" onclick="$('#jy').window('close')" style="width:100px">取消</a>
		</div>
	</div>
	</#if>
	<#if !permission?exists>
		<div class="floatL treeList">
			  <ul id="tt" class="easyui-tree" data-options="url:'${rc.contextPath}/vehicle/tree?checked=${series}',method:'get',animate:true"></ul>		
		</div>
		
		<div class="detaillist floatL">
			<div class="tih2 clearfix">
				<p class="floatL">车型管理</p>
			</div>
			
			<div class="search">
				<form  id="vehicle" action="${rc.contextPath}/vehicle/vehicle.json" method="POST">
					<input id="parentId" class="search" type="hidden" name="seriesId" value="${series}"/>
					<div style="overflow: hidden;*height:100%;">
						<div class="floatL autoFill ieHack">
							<label>车型名称：</label>
							<input type="text"  name="name"  maxlength="30"  class="search key moreWidthText"/>
						</div>
						<div class="floatL autoFill ieHack">
							<label>车型代码：</label>
							<input type="text" name="code"   maxlength="30" class="search key moreWidthText"/>
						</div>
						<div class="floatL autoFill ieHack">
							<label>车型状态：</label>
							<select name="status" class="searchselect search moreWidthselect">
								<option value="" >-- 请选择 --</option>
								<option value="active" >启用</option>
								<option value="inactive" >禁用</option>
							</select>
						</div>
						<div class="sumbit floatL autoFill ieHack" style="width:100px" >
							<input type="button" class="submit search" value="搜 索" id="formSubmit" />
						</div>
					</div>
				</form>
			</div>
			<div class="ichtable">
				<table id="vehicleid" width="98%" class="easyui-datagrid" title="<a></a>" data-options="singleSelect:true,collapsible:false,method:'get',remoteSort:false,multiSort:true,pagination:true,showRefresh:false">
					<thead>
						<tr>
							<#if loginDealer?exists>
								<th width="20%" data-options="field:'series.name',sortable:true">归属车系</th>
								<th width="30%" data-options="field:'name',sortable:true">车型名称</th>
								<th width="15%" data-options="field:'code',sortable:true">车型代码</th>
								<th width="10%" data-options="field:'status.name',sortable:true">车型状态</th>
								<th width="10%" data-options="field:'onsale'">在售状态</th>
								<th width="15%" data-options="field:'operations',sortable:false">操作</th>
							<#else>
								<th width="20%" data-options="field:'series.name',sortable:true">归属车系</th>
								<th width="35%" data-options="field:'name',sortable:true">车型名称</th>
								<th width="15%" data-options="field:'code',sortable:true">车型代码</th>
								<th width="15%" data-options="field:'status.name',sortable:true">车型状态</th>
								<th width="15%" data-options="field:'operations',sortable:false">操作</th>
							</#if>
						</tr>
					</thead>
				</table>
			</div>
		</div>
		
		<div id="jy" class="easyui-window" title="消息框" data-options="modal:true,closed:true,iconCls:'icon-save'" style="width:500px;height:150px;padding:10px;">
			<p>确认要禁用么？</p>
			<div data-options="region:'south',border:false" class="delbtn">
				<a class="easyui-linkbutton btnstyle" data-options="iconCls:'icon-ok'" href="javascript:void(0)" style="width:100px">确认</a>
				<a class="easyui-linkbutton cancel" data-options="iconCls:'icon-cancel'" href="javascript:void(0)" onclick="$('#jy').window('close')" style="width:100px">取消</a>
			</div>
		</div>
	</#if>
	<script type="text/javascript" src="${rc.contextPath}/js/popup.js"></script>
	<script type="text/javascript">
		function jy(){
			$('#jy').window('open')
		};
		
		$(function(){
			$.plugin.init("#vehicle","#vehicleid");			
		});
		
		$(function(){
			$('#tt').tree({
				onClick: function(node){
					$("#parentId").val(node.id);
					$.plugin.searchByForm();
					
					
				},
				onLoadSuccess :function(node){
					if(!node){
						node = $('#tt').tree('getRoot');
					}
					node=$('#tt').tree('find',$('#parentId').val());
					if(node!=null){
						$('#tt').tree('expandTo', node.target).tree('select', node.target);
					}
				}
			});
		});
		
		function add_vehicle(){
			var value = $("#parentId").val();
			window.location = "${rc.contextPath}/vehicle/create?series.id="+value;
		}
		
		
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
