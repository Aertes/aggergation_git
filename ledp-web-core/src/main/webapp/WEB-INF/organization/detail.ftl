<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#include "/layouts/main.ftl"/>
<@html></@html>
<#macro style></#macro>
<#macro script></#macro>
<#macro content>
	<div class="tit clearfix" style="margin:0; padding:0;">
		<h2>基本信息</h2>
	</div>
	<div class="detaillist ollright">
		<ul class="formlist">
			<li class="clearfix">
				<label>上级机构 <em>*</em></label>
				<span class="iname">${organization.parent.name?if_exists}</span>
			</li>
			<li class="clearfix">
				<label>组织名称 <em>*</em></label>
				<span class="iname">${organization.name}</span>
			</li>
			<li class="clearfix">
				<label>组织别名</label>
				<span class="iname">${organization.alias}</span>
			</li>
			<li class="clearfix">
				<label>组织负责人</label>
				<span class="iname">
				<a href="${rc.contextPath}/user/detail/${organization.manager.id}" style="color:#CC0000" >${organization.manager.name}</a>
				</span>
			</li>
			<li class="clearfix">
				<label>组织状态 <em>*</em></label>
				<span class="iname">${organization.status.name}</span>
			</li>
			<li class="clearfix">
				<label>备注</label>
				<span class="iname">${organization.comment}</span>
			</li>
		</ul>
		<div class="btnsumbit btncoloes">
			<@check permissionCodes = permissions permissionCode="organization/update">
				<a href="${rc.contextPath}/organization/update/${organization.id}" class="easyui-linkbutton btnstyle">编 辑</a>
			</@check>
			<a href="${rc.contextPath}/organization/index?parent.id=${organization.parent.id}" class="easyui-linkbutton backbtn">返 回</a>
		</div>
	</div>
</#macro>