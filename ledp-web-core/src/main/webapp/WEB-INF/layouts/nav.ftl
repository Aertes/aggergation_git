<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<span class="spriteIcon" style="*display:inline-block;*height:40px;*float:left;*margin-right:10px;"><em></em><#if !menu?exists>首页</#if></span>
<#macro nav menu>
	<span>${menu.name}</span>
</#macro>
<#if menu?exists>
	<#if menu.parent?exists>
		<@nav menu.parent/><span>&gt;&gt;</span>
	</#if>
	<@nav menu/>
</#if>
