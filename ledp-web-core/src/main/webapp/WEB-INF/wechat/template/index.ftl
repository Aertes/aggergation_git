<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#assign tag=JspTaglibs["/WEB-INF/taglib/permission.tld"]>
<#include "/wechat/layouts/main.ftl"/>
<@html></@html>
<#macro style>
	<style>
		.weizhanContianer{
			width:98%;
			margin:1%;
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
			<span>基础工具</span>
			<span>&gt;&gt;</span>
			<span>模板中心</span>
		</li>
	</ul>
</div>
<div class="tabContainer tabChange marginTop20" style="margin-left:20px;">
	<ul class="tabContiner_nav borderbottom">
		<li class="active" href="#page1">
			<a >多页面模板</a>
		</li>
		<li href="#page2">
			<a >单页面模板</a>
		</li>
	</ul>
	<div class="tabContianer_con borderAround" style="overflow: auto;">
		<div id="page1" style="display: block;min-width: 900px;">
			<div class="weizhanContianer">
				<ul>
					<#if (templates?size > 0)>
						<#list templates as template>
							<#if template.type=1>
							<li>
								<p class="weizhanSelect">
									<input type="radio" name="weizhan" />
									<span class="marginLeft10" >${template.style?if_exists}</span>
								</p>
								<div class="weizhanRon">
									<div class="weizhanIntruduce">
										<img class="floatL" src="${templateUrl}/${template.bak1}/${template.thumbPicUrl}" />
										<div class="floatL content">
											<span>${template.name?if_exists}</span>
											<span>模版描述： ${template.description}</span>
											<span>页面数量： ${template.pages?size}张</span>
											<span>页面名称：   
												<#if (template.pages?size>0)>
													<#list template.pages as page>
														<label>${page_index+1}、${page.name}</label>
													</#list>
												</#if>
											</span>
										</div>
									</div>
									<div class="activePluginList">
										<div class="pluginL active">
											<h1>
												模版页面缩略图预览<i class="icon-angle-down"></i>
											</h1>
											<div class="suolue" style="display:none;">
												<div class="sliderbar_nav">
													<div class="weizhanNavs sliderbar_left">
														<i class="icon-double-angle-left" style="left:10%;z-index:100"></i>
													</div>
													<div class="weizhanNavs sliderbar_right">
														<i class="icon-double-angle-right"  style="right:10%;z-index:100"></i>
													</div>
													<div class='scroller_navs'>
														<ul style="position: relative;left:0">
															<#if (template.pages?size>0)>
																<#list template.pages as page>
																	<li>
																		<img src="${templateUrl}/${template.bak1}${page.thumbPicUrl}" />
																	</li>
																</#list>
															</#if>
														</ul>
													</div>
												</div>
											</div>
										<div>
									<div>
								</div>
							</li>
							</#if>
						</#list>
					</#if>
				</ul>
			</div>
		</div>
		<div id="page2" style="min-width: 900px;">
			<div class="weizhanContianer">
				<ul>
				<#if (templates?size > 0)>
					<#list templates as template>
						<#if template.type=0>
							<li>
								<div class="weizhanRon">
									<div class="weizhanIntruduce">
										<img class="floatL" src="${templateUrl}/${template.bak1}/${template.thumbPicUrl}" />
										<div class="floatL content">
											<span>${template.name?if_exists}</span>
											<span>模版描述： ${template.description?if_exists}</span>
											<span>页面名称:
											<#if (template.pages?size>0)>
												<#list template.pages as page>
													${page.name}
												</#list>
											</#if>
											</span>
										</div>
									</div>
								</div>
							</li>
						</#if>
						</#list>
					</#if>
				</ul>
			</div>
		</div>
	</div>
</div>
</#macro>
<#macro script>
	<script src="${rc.contextPath}/wechat/js/bootstrap-switch.js"></script>
	<script src="${rc.contextPath}/wechat/js/uedit/ueditor.config.js"></script>
	<script src="${rc.contextPath}/wechat/js/uedit/ueditor.all.js"></script>
	<script src="${rc.contextPath}/wechat/js/uedit/lang/zh-cn/zh-cn.js"></script>
	<script src="${rc.contextPath}/wechat/js/scollBanner.js" type="text/javascript" charset="utf-8"></script>
	<script type="text/javascript">
		$(function() {
			scroll_oneCap.init();
		});
	</script>
</#macro>