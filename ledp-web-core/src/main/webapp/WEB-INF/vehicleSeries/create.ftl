<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#include "/layouts/main.ftl"/>
<@html></@html>
<#macro style></#macro>
<#macro script>
	<script type="text/javascript">
		$(function(){
			$.plugin.validate();	
		});
	</script>
</#macro>
<#macro content>
	<div class="detaillist floatL">
		<form id="form_vehicleSeries_create" class="validateCheck" action="${rc.contextPath}/vehicleSeries/save" method="post" data-options="novalidate:true">
			<#include "/vehicleSeries/_form.ftl"/>
			<div class="btnsumbit">
				<a href="javascript:void(0)" class="easyui-linkbutton btnstyle" onClick="$('#form_vehicleSeries_create').submit();">添 加</a>
				<a href="${rc.contextPath}/vehicleSeries/index" class="easyui-linkbutton backbtn">返 回</a>
			</div>
		</form>
	</div>
</#macro>