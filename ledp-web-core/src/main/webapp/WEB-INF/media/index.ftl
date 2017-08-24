<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#include "/layouts/main.ftl"/>
<@html></@html>
<#import "/layouts/popup.ftl" as massage>
<#macro style>
	<style>
		.search .autoFill{
			*height:35px;
		}
	</style>
</#macro>
<#macro script></#macro>
<@massage.generalMessage />
<#macro content>
	<div class="tih2 clearfix">
		<p class="floatL">媒体列表</p>
	</div>
	<div class="detaillist ollright">
		<div class="search">
			<form  id ="media"  action="${rc.contextPath}/media/media.json"    method="POST">
				<div style="overflow:hidden;*height:100%;">
					<div class="floatL autoFill ieHack">
						<label>媒体名称：</label>
						<input type="text" name="name"  maxlength="30"  class="search key moreWidthText" style="height: 30px;" />
					</div>
					<div class="floatL autoFill ieHack">
						<label>媒体代码：</label>
						<input type="text" name="code"  maxlength="30"  class="search key moreWidthText" style="height: 30px;" />
					</div>
					<div class="floatL autoFill ieHack">
						<label>媒体状态：</label>
						<select  name="status" class="searchselect search moreWidthselect">
							<option value="">-- 请选择 --</option>
							<option value="active" >启用</option>
							<option value="inactive" >禁用</option>
						</select>
					</div>
					
					<div class="sumbit floatL autoFill ieHack" style="width:100px;">
						<input type="button" class="submit search" value="搜 索"  id="formSubmit"  />
					</div>
				</div>
			</form>
		</div>
		<div class="ichtable">
			<table id="mediaid" width="100%" class="easyui-datagrid" title="&nbsp;" data-options="singleSelect:true,collapsible:false, method:'get',remoteSort:false,multiSort:true,pagination:true,showRefresh:false" >
				<thead>
					<tr>
						<th width="25%" data-options="field:'name',sortable:true">媒体名称</th>
						<th width="25%" data-options="field:'code',sortable:true">媒体代码</th>
						<th width="25%" data-options="field:'status.name',sortable:true">媒体状态</th>
						<th width="25%" data-options="field:'operations',sortable:false">操作</th>
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
	
	<script type="text/javascript" src="${rc.contextPath}/js/popup.js"></script>
	<script type="text/javascript">
		function jy(){
			$('#jy').window('open')
		};
	</script>
	
	<script type="text/javascript">
		$(function(){
			$.plugin.init("#media","#mediaid");			
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
