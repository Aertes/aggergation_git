<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#include "/layouts/main.ftl"/>
<#import "/layouts/popup.ftl" as massage>
<@html></@html>
<#macro style></#macro>
<#macro script>
	<script type="text/javascript" src="${rc.contextPath}/js/popup.js"></script>
	<script type="text/javascript">

	function deleteUserCallback(json){
		general_message(json.code,json.message);
		window.location.href="${rc.contextPath}/user/index";
	}
	 
	</script>
</#macro>
<@massage.generalMessage />
<#macro content>
	<div class="tit clearfix" style="margin:0; padding:0;">
		<h2>基本信息</h2>
	</div>
	<div class="floatL detaillist">
		<ul class="formlist">
			<li class="clearfix">
				<label>归属机构<em>*</em></label>
				<#if user.org!=null>
					<span class="iname">${user.org.name}</span>
				</#if>
				<#if user.dealer!=null>
					<span class="iname">${user.dealer.name}</span>
				</#if>
			</li>
			<li class="clearfix">
				<label>用户姓名 <em>*</em></label>
				<span class="iname">${user.name}</span>
			</li>
			<li class="clearfix">
				<label>用户编号 <em>*</em></label>
				<span class="iname">${user.code}</span>
			</li>
			<li class="clearfix">
				<label>登录账号<em>*</em></label>
				<span class="iname">${user.username}</span>
			</li>
			<li class="clearfix">
				<label>用户角色 <em>*</em></label>
				<span class="iname">${user.role.name}</span>
			</li>
			<li class="clearfix">
				<label>用户状态  <em>*</em></label>
				<span class="iname">${user.status.name}</span>
			</li>
			<li class="clearfix">
				<label>手机号码 <em>*</em></label>
				<span class="iname">${user.phone}</span>
			</li>
			<li class="clearfix">
				<label>电子邮箱 <em>*</em></label>
				<span class="iname">${user.email}</span>
			</li>
		</ul>
		<div class="btnsumbit btncoloes">
			<@check permissionCodes = permissions permissionCode="user/update">
				<a href="${rc.contextPath}/user/update/${user.id}" class="easyui-linkbutton btnstyle">编 辑</a>
			</@check>
			<a href="${rc.contextPath}/user/index" class="easyui-linkbutton backbtn">返 回</a>
		</div>
	</div>
</#macro>
