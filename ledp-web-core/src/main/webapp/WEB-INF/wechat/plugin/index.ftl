<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#assign tag=JspTaglibs["/WEB-INF/taglib/permission.tld"]>
<#include "/wechat/layouts/main.ftl"/>
<@html></@html>
<#macro style>
	<style>
		.plugin img{
			width:100%;
		}
		.tuwen li{
			margin-top:20px;
		}
	</style>
</#macro>
<#macro content>
<div class="breadcrumbs" id="breadcrumbs">
	<script type="text/javascript">
		try{ace.settings.check('breadcrumbs' , 'fixed')}catch(e){}
	</script>

	<ul class="breadcrumb">
		<li>
			<i class="icon-home home-icon"></i>
			<a href="#">营销中心 >></a>
			<a href="#">插件中心</a>
		</li>
	</ul>
</div>
<div>
	<form class="checkboxs" id="formId" action="${rc.contextPath}/wechat/plugin/getPluginList"  class="form-search">
		<div class="floatL"><span class="floatL">插件类型：</span>
		<input name="type"  id="pluginType" type="hidden"  class="search" /></div>
		<#if types?exists && (types?size > 0)>
			<#list types?sort_by("id") as item>
				<#if item.code = 'base'>
				<#else>
				<div class="pluginBtton">
					<input code="${item.id}" type="checkbox"  class="submit" />
					<span>${item.name}</span>
				</div>
				</#if>
			</#list>
		</#if>
	</form>
	<div class="tuwenParent marginLeft25" id="tableId">
		<ul class="tuwen plugin">
		</ul>
		<div class="clearfix">
			<div class="pagegination floatR" style="display:none">
				<ul>
				</ul>
			</div>
		</div>
	</div>
</div>
</#macro>
<#macro script>
<script>
$(function(){
	var page = new pagination("formId","tableId",function(value){
		$("#tableId>ul").children().remove();
		for(var i=0;i<value.length;i++){
			var obj = value[i];
			var $li = $("<li>");
			var $h1 = $("<h1>").addClass("pluginFontsize").text(obj.name+"("+obj.type.name+")");
			var $img = $("<img height='131px'>").attr("src",${pluginUrl}obj.thumb);
			var $content = $("<p style='height:75px;overflow:hidden;'>").text(obj.description);
			var $ope = $("<p class='pluginYulan'><a href='${rc.contextPath}/wechat/plugin/detail/"+obj.id+"' class='pluginReturn'>查看详情</a></p>");
			$("#tableId>ul").append($li.append($h1).append($img).append($content).append($ope));
		}
	});
	
	$(".pluginBtton").delegate("input[type='checkbox']",'click',function(){
		$('#pluginType').val('');
		var type = [];
		var index = 0;
		$(".pluginBtton input[type='checkbox']").each(function(){
			var $this=$(this);
			if($this.is(':checked')){
				type[index++] = $this.attr('code');
				$('#pluginType').val(type);
			}
		});
		page.getPageValue();
	});
});

</script>
</#macro>