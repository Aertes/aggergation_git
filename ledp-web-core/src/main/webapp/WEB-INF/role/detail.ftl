<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#include "/layouts/main.ftl"/>
<@html></@html>
<#macro style></#macro>
<#macro script></#macro>
<#macro content>
	<div class="tit clearfix" style="margin:0; padding:0;">
		<h2>基本信息</h2>
	</div>
	<div class="detaillist">
		<ul class="formlist">
			<li class="clearfix">
				<label>角色名称 <em>*</em></label>
				<span class="iname">${role.name}</span>
			</li>
			<li class="clearfix">
				<label>角色状态 <em>*</em></label>
				<span class="iname">${role.status.name}</span>
			</li>
			<li class="clearfix" style="height:410px;">
				<label>角色权限 <em>*</em></label>
				<div class="treeList floatL ieHackTree" style="margin:10px;">
					  <ul id="tt" class="easyui-tree" data-options="url:'${rc.contextPath}/role/treeDetail?roleId=${role.id}',method:'get',animate:false,checkbox:false"></ul>		
				</div>
			</li>
		</ul>
		<div class="btnsumbit">
			<@check permissionCodes = permissions permissionCode="role/update">
				<a href="${rc.contextPath}/role/update/${role.id}" class="easyui-linkbutton btnstyle">编 辑</a>
			</@check>
			<a href="${rc.contextPath}/role/index" class="easyui-linkbutton backbtn">返 回</a>
		</div>
	</div>
</#macro>
