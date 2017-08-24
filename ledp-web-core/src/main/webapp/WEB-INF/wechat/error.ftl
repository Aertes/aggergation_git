
<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#assign tag=JspTaglibs["/WEB-INF/taglib/permission.tld"]>
<#include "/wechat/layouts/main.ftl"/>
<@html></@html>
<#macro style>
</#macro>
<#macro content>
	<center style='padding-top:80px'>
		${message}
	</center>
	<center style='margin-top:20px'>
		<a href="javascript:window.history.go(-1)">返回</a>
	</center>
</#macro>
<#macro script>
</#macro>