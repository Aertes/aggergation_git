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
	<div class="detaillist ollright">
		<form id="form_vehicleSeries_update" action="${rc.contextPath}/vehicleSeries/edit" method="post" data-options="novalidate:true"  class="validateCheck">
			<input  type="hidden"  name="id"  value="${vehicleSeries.id?if_exists}" ></input>
			<#include "/vehicleSeries/_form.ftl"/>
			<div class="btnsumbit">
				<a href="javascript:void(0)" class="easyui-linkbutton btnstyle" onClick="$('#form_vehicleSeries_update').submit();">保 存</a>
				<a href="${rc.contextPath}/vehicleSeries/index" class="easyui-linkbutton backbtn">返 回</a>
			</div>
		</form>
	</div>
</#macro>
