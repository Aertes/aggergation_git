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
			<span>首页</span>
		</li>
	</ul>
</div>
<div class="totalCount">
	<ul>
		<li>
			<p>
				<i class="icon-comment icons"></i>
				<#if curOrg?exists>
					<span class="numbers">${currDayMsg?default(0)}</span>
				<#else>
					<span class="numbers"><a href="${rc.contextPath}/wechat/message/index?time=time">${currDayMsg?default(0)}</a></span>
				</#if>
			</p>
			<p class="msg">今日粉丝消息</p>
		</li>
		<li>
			<p>
				<i class="icon-user icons"></i>
				<#if curOrg?exists>
					<span class="numbers">${currDayNewFans?default(0)}</span>
				<#else>
					<span class="numbers"><a href="${rc.contextPath}/wechat/fans/index?time=time">${currDayNewFans?default(0)}</a></span>
				</#if>
			</p>
			<p class="msg">今日新增粉丝</p>
		</li>
		<li style="border-right: 0px;">
			<p>
				<i class="icon-user icons"></i>
				<i class="icon-user icons" style="font-size: 18px;margin-left: -12px;"></i>
				<#if curOrg?exists>
					<span class="numbers">${totalFans?default(0)}</span>
				<#else>
					<span class="numbers"><a href="${rc.contextPath}/wechat/fans/index">${totalFans?default(0)}</a></span>
				</#if>
			</p>
			<p class="msg">总粉丝数</p>
		</li>
	</ul>
</div>
<div class="mainPage">
	<ul>
		<li>
			<div class="pageTitle">
				<p>
					<i class="fensi icon_page"></i>
					<span>本周营销插件使用情况</span>
				</p>
			</div>
			<div class="report">
				<#if ispluginPie?exists>
					<div id="canvas2" style="min-width:400px;height:270px"></div>
				<#else>
					<div>
						<span style="display: block;line-height: 270px;text-align: center;">
						暂无数据
						</span>
					</div>
				</#if>
			</div>
		</li>
		<li>
			<div class="pageTitle">
				<p>
					<i class="chajian icon_page"></i>
					<span>本周粉丝增长趋势图</span>
				</p>
			</div>
			<div class="report">
				<div id="canvas3" style="min-width:400px;height:270px"></div>
			</div>
		</li>
		<li>
			<div class="pageTitle">
				<p>
					<i class="xiansuo icon_page"></i>
					<span>本周线索数量</span>
				</p>
			</div>
			<div class="report">
				<#if isleadsPie?exists>
					<div id="canvas1" style="min-width:400px;height:270px"></div>
				<#else>
					<div>
						<span style="display: block;line-height: 270px;text-align: center;">
						暂无数据
						</span>
					</div>
				</#if>
			</div>
		</li>
		<li>
			<div class="pageTitle">
				<p>
					<i class="paiming icon_page"></i>
						<span>本周活动开展统计</span>
				</p>
			</div>
			<div class="report">
				<div id="canvas4" style="min-width:400px;height:270px"></div>
			</div>
		</li>
	</ul>
</div>

</#macro>
<#macro script>

		<!--[if lte IE 8]>
		  <script src="${rc.contextPath}/wechat/assets/js/excanvas.min.js"></script>
		<![endif]-->

		<script src="${rc.contextPath}/wechat/assets/js/flot/jquery.flot.min.js"></script>
		<script src="${rc.contextPath}/wechat/assets/js/flot/jquery.flot.pie.min.js"></script>
		<script src="${rc.contextPath}/wechat/assets/js/flot/jquery.flot.resize.min.js"></script>

		<!-- ace scripts -->

		<script src="${rc.contextPath}/wechat/assets/js/ace-elements.min.js"></script>

		<!-- inline scripts related to this page -->

		<script type="text/javascript" src="${rc.contextPath}/wechat/js/report/highcharts.js" ></script>
		<script type="text/javascript" src="${rc.contextPath}/wechat/js/report/highcharts-more.js" ></script>
		<script type="text/javascript" src="${rc.contextPath}/wechat/js/report/exporting.js" ></script>
		<script type="text/javascript" src="${rc.contextPath}/wechat/js/report/gray.js" ></script>
		<script type="text/javascript" src="${rc.contextPath}/wechat/js/report/Chart.js" ></script>
		<script>
			$(function(){
				var chart = new Chart(null);
				<#if isleadsPie?exists>
				$('#canvas1').highcharts({
		            tooltip: {
		                pointFormat: '{point.y}({point.percentage:.1f}%)'
		            },
		            title : {
		            	text : null
		            },
		            legend : {
		            	align : 'right',
		            	verticalAlign : 'middle',
		            	layout : 'vertical'
		            },
		            exporting : false,
		            credits: false,
					inverted : false, //饼图里面表示是否半圆
					stacking : "percent",
		            plotOptions: {
		                pie: {
		                	size : '80%',
		                    allowPointSelect: true,
		                    cursor: 'pointer',
		                    dataLabels: {
			                    enabled: false,
			                    format: '',
			                },
		                    showInLegend: true
		                }
		            },
		            series: [{
		                type: 'pie',
		                data:  ${leadsPie?if_exists}
		            }]
		        });
				</#if>
				
				<#if ispluginPie?exists>
				$('#canvas2').highcharts({
		            tooltip: {
		                pointFormat: '{point.y}({point.percentage:.1f}%)'
		            },
		            title : {
		            	text : null
		            },
		            legend : {
		            	align : 'right',
		            	verticalAlign : 'middle',
		            	layout : 'vertical'
		            },
		            exporting : false,
		            credits: false,
					inverted : false, //饼图里面表示是否半圆
					stacking : "percent",
		            plotOptions: {
		                pie: {
		                	size : '80%',
		                    allowPointSelect: true,
		                    cursor: 'pointer',
		                    dataLabels: {
			                    enabled: false,
			                    format: '',
			                },
		                    showInLegend: true
		                }
		            },
		            series: [{
		                type: 'pie',
		                data:  ${pluginPie?if_exists}
		            }]
		        });
				</#if>
				var areaValue1 = {
					categories : ["周一","周二","周三","周四","周五","周六","周日"],
					yText : "粉丝增长趋势图",
					subffixValue : "人",
					data : [
						{
							name: '粉丝人数',
							color:"#d12d32",
	            			data: ${fansGrow?if_exists}
						}
					],
					inverted : false,
					exporting: false
				}
				chart.drawColumn("canvas3","area",areaValue1);
				
				
				
				var areaValue2 = {
					categories : ["周一","周二","周三","周四","周五","周六","周日"],
					yText : "本周活动开展统计",
					subffixValue : "人",
					data : [
						{
							name: '活动个数',
							color:"#d12d32",
	            			data: ${totalCampaign?if_exists}
						}
					],
					inverted : false,
					exporting: false
				}
				chart.drawColumn("canvas4","area",areaValue2);
			});
		</script>
</#macro>