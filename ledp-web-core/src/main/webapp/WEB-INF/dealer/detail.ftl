<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#include "/layouts/main.ftl"/>
<@html></@html>
<#macro style></#macro>
<#macro script></#macro>
<#macro content>
	<div class="tit clearfix" style="margin:0; padding:0;">
		<h2>基本信息</h2>
	</div>
	<div class="floatL detaillist">
		<ul class="formlist">
			<li class="clearfix">
				<label>所属大区<em>*</em></label>
				<span class="iname">${dealer.organization.name?if_exists}</span>
			</li>
			<li class="clearfix">
				<label>网点名称<em>*</em></label>
				<span class="iname">${dealer.name?if_exists}</span>
			</li>
			<li class="clearfix">
				<label>网点简称</label>
				<span class="iname">${dealer.alias?if_exists}</span>
			</li>
			<li class="clearfix">
				<label>网点编号<em>*</em></label>
				<span class="iname">${dealer.code?if_exists}</span>
			</li>
			<li class="clearfix">
				<label>小区经理</label>
				<span class="iname">${dealer.manager.name?if_exists}</span>
			</li>
			<li class="clearfix">
				<label>网点类型<em>*</em></label>
				<span class="iname">${dealer.type.name?if_exists}</span>
			</li>
			<li class="clearfix">
				<label>合作媒体</label>
				<#if dmList?if_exists>
					<#list dmList as dealerMedia>
						<span class="iname">${dealerMedia.media.name?if_exists}</span>
					</#list>
				</#if>
			</li>
			<li class="clearfix">
				<label>网点地址<em>*</em></label>
				<span class="iname">${dealer.address?if_exists}</span>
			</li>
			<li class="clearfix">
				<label>网点电话<em>*</em></label>
				<span class="iname">${dealer.phone?if_exists}</span>
			</li>
			<li class="clearfix">
				<label>电子邮件<em>*</em></label>
				<span class="iname">${dealer.email?if_exists}</span>
			</li>
			<li class="clearfix">
				<label>网点状态<em>*</em></label>
				<span class="iname">${dealer.status.name?if_exists}</span>
			</li>
		</ul>
		<div class="btnsumbit btncoloes">
			<@check permissionCodes = permissions permissionCode="dealer/update">
			<a href="${rc.contextPath}/dealer/update/${dealer.id}" class="easyui-linkbutton btnstyle">编 辑</a>
			</@check>
			<a href="${rc.contextPath}/dealer/index?parentId=${dealer.organization.id?if_exists}" class="easyui-linkbutton backbtn">返 回</a>
		</div>
	</div>
</#macro>