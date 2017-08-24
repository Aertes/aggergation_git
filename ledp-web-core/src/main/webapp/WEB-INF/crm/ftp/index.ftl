<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#include "/layouts/main.ftl"/>
<@html></@html>
<#macro style></#macro>
<#macro script>
	<script type="text/javascript">
		$(function(){
			$.plugin.init("#apilog","#logid");			
		});
	</script>
</#macro>
<#macro content>
	<div class="tih2 clearfix">
		<p class="floatL">报文上传记录</p>
	</div>
	<div class="floatL detaillist" style="width: 100%;">
		<div class="search">
			<form id="apilog" action="${rc.contextPath}/crm/leads/ftpLog" method="POST">
				<div style="overflow:hidden;*height:100%;">
					<div class="floatL autoFill ieHack">
						<label>报文名称：</label>
						<input class="search key"   name="serial"   type="text" style="height: 30px;" />
					</div>
					<div class="floatL autoFill ieHack" style="width: 429px;width:600px\0;">
						<label>上传时间：</label>
						<input style="width: 150px; height: 30px; display: none;" type="text" name="date1"  class=" search easyui-datetimebox combo-f textbox-f datetimebox-f">
						<span>-</span>
						<input style="width: 150px; height: 30px; display: none;" type="text" name="date2"   class="search easyui-datetimebox combo-f textbox-f datetimebox-f">
					</div>
				</div>
				<div style="overflow:hidden;*height:100%;">
					<div class="floatL autoFill ieHack">
						<label>上传结果：</label>
						<select class="search searchselect"  name="state" style="height:30px; line-height:30px;">
							<option value="">---请选择---</option>
							<option value="上传成功">上传成功</option>
							<option value="上传失败">上传失败</option>
						</select>
					</div>
					<div class="sumbit floatL ieHack" style="margin:5px 0;">
						<input type="button" class="submit search" value="搜 索" />
					</div>
				</div>
			</form>
		</div>
		<div class="ichtable">
			<table id="logid" width="100%" class="easyui-datagrid" title="报文上传记录" data-options="singleSelect:true,collapsible:false,method:'get',remoteSort:false,multiSort:true,pagination:true,showRefresh:false">
				<thead>
					<tr>
						<th width="25%" data-options="field:'serial',sortable:true">报文名称</th>
						<th width="13%" data-options="field:'date',sortable:true">上传时间</th>
						<th width="10%" data-options="field:'state',sortable:true">上传结果</th>
						<th width="8%" data-options="field:'total',sortable:true">记录行数</th>
						<th width="15%" data-options="field:'records',sortable:true">记录ID</th>
						<th width="30%" data-options="field:'comment',sortable:true">备注</th>
					</tr>
				</thead>
			</table>
		</div>
	</div>
</#macro>

