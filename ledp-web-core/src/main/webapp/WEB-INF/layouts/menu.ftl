<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<ul>
	<li class="sidelist<#if !choose?exists> activeBg</#if>">
		<a class="hoverBg first" href="${rc.contextPath}/home/index" title="">首页</a>
	</li>
	<#if menus?exists && permissions?exists> 
	<#list menus as menu1>
		<#if permissions?seq_contains(menu1.code) && menu1.wechat!=1>
		<li class="sidelist<#if choose?exists && choose==menu1.choose> activeBg</#if>">
			<a class="hoverBg" href="#" title="">${menu1.name}</a>
			<#if menu1.children?has_content>
			<ul class="i-list">
				<li>
				<#list menu1.children as menu2>
					<#if permissions?seq_contains(menu2.code)>
					<div>
						<h1>${menu2.name}</h1>
						<#list menu2.children as menu3>
						<#if permissions?seq_contains(menu3.code)>
						<p><a href="${rc.contextPath}/${menu3.action}" title="">${menu3.name}</a></p>
						</#if>
						</#list>
					</div>
					</#if>
				</#list>
				</li>
			</ul>
			</#if>
		</li>
		</#if>
	</#list>
	</#if>
	<li class="sidelist"><a href="http://scrm.dfinfo.cn/wechat/auth/${loginUser.id}" target="view_window" title="微信矩阵平台">微信矩阵</a></li>
</ul>