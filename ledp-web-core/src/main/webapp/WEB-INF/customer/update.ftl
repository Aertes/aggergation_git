<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#include "/layouts/main.ftl"/>
<@html></@html>
<#macro style></#macro>
<#macro script></#macro>
<#macro content>
	<script type="text/javascript">
		$(function(){
			$.plugin.validate();	
		});
	</script>
	<div class="detaillist">
		<form id="form_customer_save" class="validateCheck" action="${rc.contextPath}/customer/edit" method="post" data-options="novalidate:true">
			<input type="hidden" name="id" value="${customer.id?if_exists}"/>
			<#include "/customer/_form.ftl"/>
			<div class="btnsumbit">
				<a href="javascript:void(0)" class="easyui-linkbutton btnstyle" onclick="$('#form_customer_save').submit();">保 存</a>
				<a href="${rc.contextPath}/customer/index" class="easyui-linkbutton backbtn">返 回</a>
			</div>
		</form>
	</div>
</#macro>