<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#include "/layouts/main.ftl"/>
<@html></@html>
<#macro style></#macro>
<#macro script></#macro>
<#macro content>
	<div class="floatL detaillist">
		<form id="form_media_update" action="${rc.contextPath}/media/edit"  method="post" data-options="novalidate:true"  class="validateCheck">
			<#include "/media/_form.ftl"/>
		</form>
		<div class="btnsumbit">
			<a href="javascript:void(0)" class="easyui-linkbutton btnstyle" onClick="$('#form_media_update').submit();">保 存</a>
			<a href="${rc.contextPath}/media/index" class="easyui-linkbutton backbtn">返 回</a>
		</div>
	</div>
</#macro>
	<#macro script>
	<script type="text/javascript">
		$(function(){
			$.plugin.validate();	
		});
	</script>
</#macro>