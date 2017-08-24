<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#include "/layouts/main.ftl"/>
<@html></@html>
<#macro style></#macro>
<#macro script>
<script>
	$(function(){
		$.plugin.validate();	
	});
	function getChecked(){
		var nodes = $('#tt').tree('getChecked');
		var s = '';
		for(var i=0; i<nodes.length; i++){
			if (s != '') s += ',';
			s += nodes[i].id;
		}
		$("#checkeds").val(s);
	}
</script>
</#macro>
<#macro content>
	<div class="detaillist">
		<form id="form_role_create"  class="validateCheck" action="${rc.contextPath}/role/save" method="post" data-options="novalidate:true">
			<#include "/role/_form.ftl"/>
			<div class="btnsumbit">
				<a href="javascript:void(0)" class="easyui-linkbutton btnstyle"onClick="getChecked();$('#form_role_create').submit();">添 加</a>
				<a href="${rc.contextPath}/role/index" class="easyui-linkbutton backbtn">返 回</a>
			</div>
		</form>
	</div>
</#macro>