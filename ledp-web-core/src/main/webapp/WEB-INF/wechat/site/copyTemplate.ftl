<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#assign tag=JspTaglibs["/WEB-INF/taglib/permission.tld"]>
<#include "/wechat/layouts/main.ftl"/>
<@html></@html>
<#macro style>
<style>
	.marginLeft40{
		margin-left:40%;
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
			<span>营销应用</span>
			<span>&gt;&gt;</span>
			<span><a href="${rc.contextPath}/wechat/site/index">微站管理</a></span>
			<span>&gt;&gt;</span>
			<span><a href="${rc.contextPath}/wechat/site/update/${site.id}">填写微站基本信息</a></span>
			<span>&gt;&gt;</span>
			<span>选择模板</span>
		</li>
	</ul>
</div>
<div class="active_slider">
	<div class="sliderBar">
	</div>
	<div class="sliderPointer marginLeft15">
		<div>
			<span>1</span>
		</div>
		<span>填写微站基本信息</span>
	</div>
	<div style="position:absolute;left:29%;margin-top:0;"><i style="color:#333;font-size:22px;" class="icon-double-angle-right"></i></div>
	<div class="sliderPointer active marginLeft40">
		<div>
			<span>2</span>
		</div>
		<span>选择模版</span>
	</div>
	<div style="position:absolute;left:55%;margin-top:0;"><i style="color:#333;font-size:22px;" class="icon-double-angle-right"></i></div>
	<div class="sliderPointer marginLeft65">
		<div>
			<span>3</span>
		</div>
		<span>编辑页面</span>
	</div>
</div>
<div class="tabContainer tabChange marginLeft25" style="border: 0px;">
	<ul class="tabContiner_nav borderbottom">
		<li class="active" href="#page_icon1">
			<i class=""></i>
			新建多个页面
		</li>
		<li href="#page_icon2">
			<i class=""></i>
			新建单个页面
		</li>
	</ul>
	<div class="tabContianer_con displayBlock tabSendpage" >
		<div style="display: block;" class="padding20" id="page_icon1">
			<span>* 请至少选择一个模板后进行页面编辑</span>
			<#list templates as template>
				<p class="lineHeight40">
					<#if site.template?exists >
						<#if site.template.id==template.id>
							<input type="radio" name="checked" onclick="chioceId('${template.id}')" checked="checked" />
						<#else>
							<input type="radio" name="checked" onclick="chioceId('${template.id}')" />
						</#if>
						
					<#else>
						<#if template.id==1>
							<input type="radio" name="checked" onclick="chioceId('${template.id}')" checked="checked"/>
						<#else>
							<input type="radio" name="checked" onclick="chioceId('${template.id}')" />
						</#if>
					</#if>
					<span class="marginLeft10">${template.style}</span>
				</p>
				<div class="modelSelect">
					<img src="${imgDir+'/'+template.bak1+'/'+template.thumbPicUrl}" />
				</div>
			</#list>
			<div class="buttons">
				<input type="button" class="redButton" onclick="submitForm();" value="保 存" />
				<input type="button" class="redButton" onclick="window.location.href='${rc.contextPath}/wechat/site/update/${site.id}'" value="返回" />
			</div>
		</div>
		<div id="page_icon2" class="padding20">
			<span>* 请至少选择一个页面后进行页面编辑</span>
			<#list template1s as template1>
				<p class="lineHeight40">
					<#if site.template?exists>
						<#if site.template.id==template1.id>
							<input type="radio" name="checked" onclick="chioceId('${template1.id}')" checked="checked"/>
						<#else>
							<input type="radio" name="checked" onclick="chioceId('${template1.id}')"/>
						</#if>
					<#else>
						<input type="radio" name="checked" onclick="chioceId('${template1.id}')"/>
					</#if>
					<span class="marginLeft10">${template1.style}</span>
				</p>
				<div class="modelSelect">
					<img src="${imgDir+'/'+template1.bak1+'/'+template1.thumbPicUrl}" />
				</div>
			</#list>
			<div class="buttons">
				<input type="button" class="redButton" onclick="submitForm();" value="保 存" />
				<input type="button" class="redButton" onclick="window.location.href='${rc.contextPath}/wechat/site/update/${site.id}'" value="返回">
			</div>
		</div>
	</div>
</div>
<form id="cpytTemplate" class="validateCheck" action="${rc.contextPath}/wechat/site/copy" method="post">
	<input type="hidden" name="siteId" value="${site.id}"/>
	<#if site.template?exists>
		<input type="hidden" name="templateId" id="templateId" value="${site.template.id}"/>
	<#else>
		<input type="hidden" name="templateId" id="templateId" value="1"/>
	</#if>
</form>
</#macro>
<#macro script>
<script>
	$(function(){
		$("#sidebar").removeClass("menu-min");
	});
	function chioceId(id){
		$('#templateId').val(id);
	}
	function submitForm(){
		var value=$('#templateId').val();
		if(value){
			$('#cpytTemplate').submit();
		}else{
			var layerAlert = new ElasticLayer("alert","请选择模板",{title : "信息提示框",dialog : true,time : 2000});
			layerAlert.init();
		}
	}
</script>
</#macro>