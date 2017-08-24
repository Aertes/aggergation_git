<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#assign tag=JspTaglibs["/WEB-INF/taglib/permission.tld"]>
<#include "/wechat/layouts/main.ftl"/>
<@html></@html>
<#macro style>
<style>
	.yulanMain{
		margin:20px 20px 0 20px;
	}
	.yulanMain span, .yulanMain label{
		font-size:12px;
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
			<span>活动管理</span>
		</li>
	</ul>
</div>

<div class="yulanMain">
	<table width="100%">
		<tr>
			<td style="text-align:center;">
				<span>活动名称：</span>
			</td>
			<td>
				<label>${campaign.name}</label>
			</td>
		</tr>
		<tr>
			<td style="text-align:center;">
				<span>活动时间：</span>
			</td>
			<td>
				<label>${campaign.getBeginDate()?string("yyyy-MM-dd HH:mm")} -  ${campaign.getEndDate()?string("yyyy-MM-dd HH:mm")}</label>
			</td>
		</tr>
		<tr>
			<td style="text-align:center;">
				<span>活动缩略图</span>
			</td>
			<td>
				<label>
					<img style="width: 200px;" src="${imgDir}${campaign.bak1}" />
				</label>
			</td>
		</tr>
		<tr>
			<td style="text-align:center;">
				<span>活动简介：</span>
			</td>
			<td>
				<label>
					${campaign.comment}
				</label>
			</td>
		</tr>
	</table>
</div>
<div class="lineHeight70">
	<#if isshow?exists>
		<#if loginDealer?exists>
			<a href="${rc.contextPath}/wechat/campaign/update/${campaign.id}" class="redButton marginLeft200">编 辑</a>
			<a href="${rc.contextPath}/wechat/campaign/index" class="redButton marginLeft10">返 回</a>
		<#else>
			<a href="${rc.contextPath}/wechat/campaign/index" class="redButton marginLeft200">返 回</a>
		</#if>
	<#else>
		<a href="${rc.contextPath}/wechat/campaign/index" class="redButton marginLeft200">返 回</a>
	</#if>
	
</div>
</#macro>
<#macro script>
<script>
	$(function(){
		dateTimeInterface.init("dateTime","startTime");
		dateTimeInterface.init("dateTime","endTime");
		
		$("#sidebar").removeClass("menu-min");
	});
</script>
</#macro>