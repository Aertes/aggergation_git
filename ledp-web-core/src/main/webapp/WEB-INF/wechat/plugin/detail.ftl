<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#assign tag=JspTaglibs["/WEB-INF/taglib/permission.tld"]>
<#include "/wechat/layouts/main.ftl"/>
<@html></@html>
<#macro style>
</#macro>
<#macro content>
<div class="breadcrumbs" id="breadcrumbs">
	<script type="text/javascript">
		try{ace.settings.check('breadcrumbs' , 'fixed')}catch(e){}
	</script>

	<ul class="breadcrumb">
		<li>
			<i class="icon-home home-icon"></i>
			<a href="#">营销中心</a>>><a href="${rc.contextPath}/wechat/plugin/index">插件中心</a>>><a href="#">插件预览</a>
		</li>
	</ul>
</div>
<h1 class="title_page">插件预览</h1>
<div class="pluginYlTitle">
	<div class="floatL">
		<img src="${pluginUrl}/${plugin.thumb}" />
	</div>
	<div class="floatL">
		<p>插件名称：${plugin.name}</p>
		<p>插件类型：${plugin.type.name}</p>
	</div>
</div>
<div class="width90" style="width:80%;margin: 1% 0px 5% 5%;float: left;">
	<p class="pluginY1">
		插件描述：
	</p>
	<p class="pluginY2">
		${plugin.description}
	</p>
	<p class="pluginY1">
		要点说明：
	</p>
	<p class="pluginY2">
		${plugin.function}
	</p>
	<p class="pluginY1">
		场景举例：
	</p>
	<p class="pluginY2">
		${plugin.instruction}
	</p>
	<br>
	<p>如有疑问，请查看图文详解<a>使用说明</a>。</p>
</div>
<#--<div class="plugin_selider marginBottomTop30" id="pluginYulan" style="width:30%">
<img style="width:100%" src="${pluginUrl}/plugin/${plugin.type.code}/${plugin.code}/thumb-1.jpg"/>
<img style="width:100%" src="${pluginUrl}/plugin/${plugin.type.code}/${plugin.code}/thumb-2.jpg"/>
<img style="width:100%" src="${pluginUrl}/plugin/${plugin.type.code}/${plugin.code}/thumb-3.jpg"/>
<img style="width:100%" src="${pluginUrl}/plugin/${plugin.type.code}/${plugin.code}/thumb-4.jpg"/>
<img style="width:100%" src="${pluginUrl}/plugin/${plugin.type.code}/${plugin.code}/thumb-5.jpg"/>
</div>-->
<p class="textAlign lineHeight40" style="clear:both">
	<input type="button" class="redButton" onclick="window.location.href='${rc.contextPath}/wechat/plugin/index'" value="返回" />
</p>
</br>
</#macro>
<#macro script>
<script src="${rc.contextPath}/wechat/js/scollBanner.js"></script>
<script>
	$(function(){
		$('#pluginYulan').scollBanner(0, 1000);
	});
</script>
</#macro>