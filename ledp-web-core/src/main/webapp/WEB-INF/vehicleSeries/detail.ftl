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
				<label>车系名称<em>*</em></label>
				<span class="iname">${vehicleSeries.name?if_exists}</span>
			</li>
			<li class="clearfix">
				<label>车系代码<em>*</em></label>
				<span class="iname">${vehicleSeries.code?if_exists}</span>
			</li>
			<li class="clearfix">
				<label>车系状态<em>*</em></label>
				<span class="iname">${vehicleSeries.status.name?if_exists}</span>
			</li>
		</ul>
		<div class="btnsumbit btncoloes">
			<a href="${rc.contextPath}/vehicleSeries/update/${vehicleSeries.id}" class="easyui-linkbutton btnstyle" onclick="submitForm()">编 辑</a>
			<a href="${rc.contextPath}/vehicleSeries/index" class="easyui-linkbutton backbtn">返 回</a>
		</div>
	</div>
</#macro>
