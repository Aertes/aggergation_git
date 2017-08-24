<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#include "/layouts/main.ftl"/>
<@html></@html>
<#macro style></#macro>
<#macro content>
	<form id="myform" action="${rc.contextPath}/pagination/condition.json">
		<table>
			<tr>
				<td>名称</td>
				<td><input type="text" name="name" class="search"></td>
				<td>年龄</td>
				<td><input type="text" name="age"  class="search"></td>
				<td>地址</td>
				<td><input type="text" name="address"  class="search"></td>
				<td><input type="button" value="搜索" class="submit"></td>
			</tr>
		</table>
	</form>
	
	<table id="mydatagrid" width="50%" class="easyui-datagrid" data-options="singleSelect:true,pagination:true">
		<thead>
			<tr>
				<th width="40%"  data-options="field:'name',sortable:true">名称</th>
				<th width="20%"  data-options="field:'age',sortable:true">年龄</th>
				<th width="40%"  data-options="field:'address',sortable:true">地址</th>
			</tr>
		</thead>
	</table>
</#macro>
<#macro script>
	<script type="text/javascript">
		$(function(){
			$.plugin.init("#myform","#mydatagrid");			
		});
	</script>
</#macro>