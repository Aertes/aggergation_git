<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#include "/layouts/main.ftl"/>
<@html></@html>
<#macro style></#macro>
<#macro script></#macro>
<#macro content>
	<center style='margin-top:80px'>
		${message}
	</center>
	<center style='margin-top:20px'>
		<a href="javascript:window.history.go(-1)">返回</a>
	</center>
</#macro>