<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#include "/layouts/main.ftl"/>
<@html></@html>
<#macro style>
	<style>
		.formlist > li{
			*height:auto;
		}
	</style>
</#macro>
<#macro script></#macro>
<#macro content>
	<div class="clearfix">
	
		<#list kpis as kpi>
			<div class="dewidth">
				<h2>
					<#if kpi.type=="leads">线索跟进率KPI设置
					<#elseif kpi.type=="media">
						新闻信息发布量完成率KPI设置
					<#elseif kpi.type=="400phone">
						400电话接起率KPI设置
					</#if>
				</h2>
				<ul class="formlist detailadd leadetail">
					<li class="clearfix">
						<div class="floatL" style="width:20%;">
							<label>阀值 <em>*</em></label>
							<#if kpi.type=="leads">
								<span class="iname">${kpi.threshold?if_exists}%</span>
							<#elseif kpi.type=="media">
								<span class="iname">${kpi.threshold?if_exists}篇/月</span>
							<#elseif kpi.type=="400phone">
								<span class="iname">${kpi.threshold?if_exists}%</span>
							</#if>
						</div>
						<div class="floatL" style="width:75%;">
							<label style="width:20%;">满分 <em>*</em></label>
							<span class="iname" style="width:70%;">${kpi.score?if_exists}</span>
						</div>
					</li>
					<li class="clearfix">
						<div class="floatL" style="width:20%;">
							<label>权重 <em>*</em></label>
							<span class="iname">${kpi.weight?if_exists}%</span>
						</div>
						<div class="floatL" style="width:75%;">
							<label style="width:20%;">评分规则 <em>*</em></label>
							<span class="iname" style="width:70%;">${kpi.regulation?if_exists}</span>
						</div>
					</li>
				</ul>
			</div>
		</#list>
		<div class="btnsumbit btncoloes">
		<@check permissionCodes = permissions permissionCode="kpi/update">
			<a href="${rc.contextPath}/kpi/update" class="easyui-linkbutton btnstyle">编 辑</a>
		</@check>
		</div>
	</div>
</#macro>