<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#include "/layouts/main.ftl"/>
<@html></@html>
<#macro style></#macro>
<#macro script>
	<script type="text/javascript">
		function jy(){
			$('#jy').window('open')
		};
	</script>
	
	<script type="text/javascript">
		$(function(){
			$.plugin.init("#log","#logid");			
		});
	</script>
</#macro>
<#macro content>
	<div class="tih2 clearfix">
		<p class="floatL">日志列表</p>
	</div>
	
	<div class="floatL detaillist" style="width: 100%;">
		<div class="search">
			<form id="log" action="${rc.contextPath}/log/log.json" method="POST">
				<div style="overflow:hidden;*height:100%;">
					<div class="floatL autoFill ieHack">
						<label>日志类型：</label>
						<select class="search searchselect moreWidthselect"  name="type" style="height:30px; line-height:30px;">
							<option value="">---请选择---</option>
							<option value="info">信息</option>
							<option value="warn">警告</option>
							<option value="error">错误</option>
						</select>
					</div>
					<div class="floatL autoFill ieHack">
						<label>操作用户：</label>
						<input class="search key moreWidthText"   name="name"   type="text" style="height: 30px;" />
					</div>
					</div>
					<div style="overflow:hidden;*height:100%;">
					<div class="floatL autoFill ieHack" style="width: 429px;width:600px\0;">
						<label>操作时间：</label>
						<input style="width: 150px; height: 30px; display: none;" type="text" name="date1"  class=" search easyui-datetimebox combo-f textbox-f datetimebox-f">
						<span>-</span>
						<input style="width: 150px; height: 30px; display: none;" type="text" name="date2"   class="search easyui-datetimebox combo-f textbox-f datetimebox-f">
					</div>
					<div class="floatL autoFill ieHack">
						<label>操作对象：</label>
						<input class=" search key moreWidthText" type="text"  name="resource"  style="height: 30px;" />
					</div>
					</div>
					<div style="overflow:hidden;*height:100%;">
					<div class="floatL autoFill ieHack">
						<label>操作类型：</label>
						<select class=" search searchselect moreWidthselect"  name="operation"    style="height:30px; line-height:30px;">
							<option value="">---请选择---</option>
							<option value="create">增加</option>
							<option value="update">修改</option>
							<option value="delete">删除</option>
						</select>
					</div>
					<div class="floatL autoFill ieHack">
						<label>操作结果：</label>
						<select class=" search searchselect moreWidthselect"  name="result" style="height:30px; line-height:30px;">
							<option value="">---请选择---</option>
							<option value="success">成功</option>
							<option value="failure">失败</option>
						</select>
					</div>
					<div class="sumbit floatL ieHack" style="margin:5px 0;">
						<input type="button" class="submit search" value="搜 索" />
					</div>
				</div>
			</form>
		</div>
		<div class="ichtable">
			<table id="logid" width="100%" class="easyui-datagrid" title="日志列表" data-options="singleSelect:true,collapsible:false,method:'get',remoteSort:false,multiSort:true,pagination:true,showRefresh:false">
				<thead>
					<tr>
						<th width="10%" data-options="field:'type',sortable:true">日志类型</th>
						<th width="15%" data-options="field:'operator.name',sortable:true">操作用户</th>
						<th width="15%" data-options="field:'resource',sortable:true">操作对象</th>
						<th width="10%" data-options="field:'operation',sortable:true">操作类型</th>
						<th width="15%" data-options="field:'datetime',sortable:true">操作时间</th>
						<th width="10%" data-options="field:'result',sortable:true">操作结果</th>
						<th width="25%" data-options="field:'comment',sortable:true">备注</th>
					</tr>
				</thead>
			</table>
		</div>
	</div>
</#macro>






