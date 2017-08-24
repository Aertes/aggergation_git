<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#assign tag=JspTaglibs["/WEB-INF/taglib/permission.tld"]>
<#include "/wechat/layouts/main.ftl"/>
<@html></@html>
<#macro style>
	<link rel="stylesheet" href="${rc.contextPath}/wechat/css/report.css" />
	<link rel="stylesheet" type="text/css" href="${rc.contextPath}/css/jquery.bigautocomplete.css">
	<style>
		.acReport{
			margin:20px;
		}
		.baseSet{
			padding:20px;
		}
		.acReport .totalCount{
			border:1px solid #d9d9d9;
			margin:20px auto;
			width:70%;
		}
		.borderbottom{
			border-bottom:1px solid #d9d9d9;
		}
		.analysis label{
			font-size:12px;
		}
		.reportData thead th{
			background:#fbfbfb;
			line-height:35px;
			height:35px;
			border-bottom:1px solid #d9d9d9;
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
				<span>数据中心</span>
				<span>&gt;&gt;</span>
				<span>活动报表</span>
			</li>
		</ul>
	</div>
	
	<div class="tabContainer tabChange acReport">
		<ul class="tabContiner_nav borderbottom">
			<li class="active" href="#effectAnalysis">
				<a>活动开展统计报表</a>
			</li>
			<li  href="#informationAnalysis">
				<a >活动留资报表</a>
			</li>
			<li  href="#pluginAnalysis">
				<a >活动插件报表</a>
			</li>
		</ul>
			<div class="tabContianer_con borderAround">
				<div id="effectAnalysis" class="baseSet" style="display: block;">
					<form class="overflow-hidden	" id="cm-form">
						<div class="analysis times">
                            <div class="floatL" style="line-height:30px;">
                                <input type="radio" name="campaigneffect" checked="checked" value="5"/>
                                <label>上周</label>
                            </div>
							<div class="floatL marginLeft20" style="line-height:30px;">
								<input type="radio" name="campaigneffect" checked="checked" value="1"/>
								<label>本周</label>
							</div>
							<div class="floatL marginLeft20" style="line-height:30px;">
								<input type="radio" name="campaigneffect" value="2"/>
								<label>本月</label>
							</div>
							<div class="floatL marginLeft20" style="line-height:30px;">
								<input type="radio" name="campaigneffect" value="3"/>
								<label>上月</label>
							</div>
							<div class="floatL marginLeft20" style="line-height:30px;">
								<input style="margin:9px 5px 0 0;" class="floatL" type="radio" name="campaigneffect" value="4"/>
								<label style="margin:0 5px;" class="floatL">自定义</label>
								<div id="startTime1" style="width: 150px;" class="floatL search" params="{name:'cstartTime',value:'',id:'cstartTime'}"></div>
								<label class="floatL margin10">-</label>
								<div id="endTime1" style="width: 150px;" class="floatL search" params="{name:'cendTime',value:'',id:'cendTime'}"></div>
							</div>
						</div>
						<div style="float:none; width:100%;" class="clearfix">
						<#if loginOrg?exists && loginOrg.id==1>
							<div class="analysis">
								<label>大区：</label>
								<select style="width: 150px;" name="corg" id="corg">
									<option value="">全部</option>
									<#list orgs as org>
										<option value="${org.id}">${org.name}</option>
									</#list>
								</select>
							</div>
							<div class="analysis" id="cdealerid" style="display:none;">
								<label>网点：</label>
								<input type="text" id="auto_tt1" relevanceHiddenId="cdealer" value="" autocomplete="off" style="min-width: 150px;"/>
								<input type="hidden" id="cdealer" name="cdealer" value="" class="search"/>
							</div>
						<#elseif loginOrg?exists && loginOrg.id!=1>
							<div class="analysis" style="line-height:30px;">
								<label>大区：${loginOrg.name}</label>
								<input type="hidden" name="corg" id="corg" value="${loginOrg.id}">
							</div>
							<div class="analysis">
								<label>网点：</label>
								<input type="text" id="auto_tt1" relevanceHiddenId="cdealer" value="" autocomplete="off" style="width: 150px;"/>
								<input type="hidden" id="cdealer" name="cdealer" value="" class="search"/>
							</div>
						<#else>
							<div class="analysis" style="line-height:30px;">
								<label>网点：<span style="padding:0 10px;height:30px;display:inline-block; line-height:30px;color: #939192;background: #f5f5f5!important;border:1px solid #d5d5d5;cursor: default;">${loginDealer.name}</span></label>
								<input type="hidden" id="cdealer" name="cdealer" value="${loginDealer.id}" class="search"/>
							</div>
						</#if>
						<div class="analysis">
							<label>活动名称：</label>
							<select style="width: 150px;" name="cmcamp" id="cmcamp">
								<option value="">全部</option>
								<#list cms as cm>
									<#list cm?keys as c>
										<#if c=='id'>
											<option value="${cm[c]}">
										<#elseif c=='name'>
											${cm[c]}</option>
										</#if>
									</#list>
								</#list>
							</select>
						</div>
						<div class="analysis">
							<input type="button" id="cm_button" value="搜 索" class="redButton" />
						</div>
						</div>
					</form>
					<div class="totalCount" id="ctotalCount" style="display:none; margin:30px auto;">
						<ul>
							<li style="width:100%;border-right:none;">
								<p>
									<span class="numbers" id="campaigns"></span>
								</p>
								<p class="msg">开展活动数量</p>
							</li>
						</ul>
					</div>
					<div class="overflow-hidden">
						<div id="canvasEffect"></div>
					</div>
					<div class="overflow-hidden activeTable" id="cm-div" style="display:none;">
						<div class="title">
							<a href="javascript:void(0)" id="cm-export" class="redButton" style="color: #fff;font-family: '微软雅黑';">导 出</a>
						</div>
						<div>
							<table class="reportData" id="campaign" width="100%">
								<thead>
									
								</thead>
								<tbody>
									
								</tbody>
							</table>
						</div>
					</div>
				</div>
				<div id="informationAnalysis" class="baseSet">
					<form class="overflow-hidden" id="le-from">
						<div class="analysis times">
                            <div class="floatL" style="line-height:30px;">
                                <input type="radio" name="campaigneLeadffect" checked="checked" value="5"/>
                                <label>上周</label>
                            </div>
							<div class="floatL marginLeft20" style="line-height:30px;">
								<input type="radio" name="campaigneLeadffect" checked="checked" value="1"/>
								<label>本周</label>
							</div>
							<div class="floatL marginLeft20" style="line-height:30px;">
								<input type="radio" name="campaigneLeadffect" value="2"/>
								<label>本月</label>
							</div>
							<div class="floatL marginLeft20" style="line-height:30px;">
								<input type="radio" name="campaigneLeadffect" value="3"/>
								<label>上月</label>
							</div>
							<div class="floatL marginLeft20" style="line-height:30px;">
								<input class="floatL" style="margin:9px 5px 0 0;" type="radio" name="campaigneLeadffect" value="4"/>
								<label class="floatL" style="margin:0 5px;">自定义</label>
								<div id="startTime2" style="width: 150px;" class="floatL" params="{name:'lstartTime',value:'',id:'lstartTime'}"></div>
								<label class="floatL margin10">-</label>
								<div id="endTime2" style="width: 150px;" class="floatL" params="{name:'lendTime',value:'',id:'lendTime'}"></div>
							</div>
						</div>
						<#if loginOrg?exists && loginOrg.id==1>
							<div class="analysis">
								<label>大区：</label>
								<select style="width: 150px;" name="lorg" id="lorg">
									<option value="">全部</option>
									<#list orgs as org>
										<option value="${org.id}">${org.name}</option>
									</#list>
								</select>
							</div>
							<div class="analysis" id="ldealerid" style="display:none;">
								<label>网点：</label>
								<input type="text" id="auto_tt2" relevanceHiddenId="ldealer" value="" autocomplete="off" style="width: 150px;"/>
								<input type="hidden" id="ldealer" name="ldealer" value="" class="search"/>
							</div>
						<#elseif loginOrg?exists && loginOrg.id!=1>
							<div class="analysis" style="line-height:30px;">
								<label>大区：${loginOrg.name}</label>
								<input type="hidden" name="lorg" id="lorg" value="${loginOrg.id}">
							</div>
							<div class="analysis">
								<label>网点：</label>
								<input type="text" id="auto_tt2" relevanceHiddenId="ldealer" value="" autocomplete="off" style="width: 150px;"/>
								<input type="hidden" id="ldealer" name="ldealer" value="" class="search"/>
							</div>
						<#else>
							<div class="analysis" style="line-height:30px;">
								<label>网点：<span style="padding:0 10px;height:30px;display:inline-block; line-height:30px;color: #939192;background: #f5f5f5!important;border:1px solid #d5d5d5;cursor: default;">${loginDealer.name}</span></label>
								<input type="hidden" id="ldealer" name="ldealer" value="${loginDealer.id}" class="search"/>
							</div>
						</#if>
						<div class="analysis">
							<label>活动名称：</label>
							<select style="width:150px;" name="cmlead" id="cmlead">
								<option value="">全部</option>
								<#list cms as cm>
									<#list cm?keys as c>
										<#if c=='id'>
											<option value="${cm[c]}">
										<#elseif c=='name'>
											${cm[c]}</option>
										</#if>
									</#list>
								</#list>
							</select>
						</div>
						<div class="analysis">
							<input type="button" id="le_button" value="搜 索" class="redButton" />
						</div>
					</form>
					<div class="totalCount" id="ltotalCount" style="display:none;">
						<ul>
							<li style="width: 50%;">
							<p>
								<span class="numbers" id="lleads"></span>
								</p>
								<p class="msg">留资数</p>
							</li>
                            <li style="width: 48%; border-right: 0;">
                                <p>
                                    <span class="numbers" id="lrecord"></span>
                                </p>
                                <p class="msg">参与数</p>
                            </li>
						</ul>
					</div>
					<div class="overflow-hidden">
						<div id="canvasInformation"></div>
					</div>
					<div class="overflow-hidden activeTable" id="le-div" style="display:none;">
						<div class="title">
							<a href="javascript:void(0)" id="le-export" class="redButton" style="color: #fff;font-family:'微软雅黑';">导 出</a>
						</div>
						<div>
							<table class="reportData" id="lead" width="100%">
								<thead>
									
								</thead>
								<tbody>
									
								</tbody>
							</table>
						</div>
					</div>
				</div>
				<div id="pluginAnalysis">
					<div id="effectAnalysis" class="baseSet" style="display: block;">
						<form class="overflow-hidden	" id="pl-form">
							<div class="analysis times">
                                <div class="floatL" style="line-height:30px;">
                                    <input type="radio" name="campaignePluginffect" checked="checked" value="5"/>
                                    <label>上周</label>
                                </div>
								<div class="floatL marginLeft20" style="line-height:30px;">
									<input type="radio" name="campaignePluginffect" checked="checked" value="1"/>
									<label>本周</label>
								</div>
								<div class="floatL marginLeft20" style="line-height:30px;">
									<input type="radio" name="campaignePluginffect" value="2"/>
									<label>本月</label>
								</div>
								<div class="floatL marginLeft20" style="line-height:30px;">
									<input type="radio" name="campaignePluginffect" value="3"/>
									<label>上月</label>
								</div>
								<div class="floatL marginLeft20" style="line-height:30px;">
									<input class="floatL" style="margin:9px 5px 0 0;" type="radio" name="campaignePluginffect" value="4"/>
									<label class="floatL" style="margin:0 5px;">自定义</label>
									<div id="startTime3" style="width: 150px;" class="floatL" params="{name:'pstartTime',value:'',id:'pstartTime'}"></div>
									<label class="floatL margin10">-</label>
									<div id="endTime3" style="width: 150px;" class="floatL" params="{name:'pendTime',value:'',id:'pendTime'}"></div>
								</div>
							</div>
							<#if loginOrg?exists && loginOrg.id==1>
								<div class="analysis">
									<label>大区：</label>
									<select style="width: 150px;" name="porg" id="porg">
										<option value="">全部</option>
										<#list orgs as org>
											<option value="${org.id}">${org.name}</option>
										</#list>
									</select>
								</div>
								<div class="analysis" id="pdealerid" style="display:none;">
									<label>网点：</label>
									<input type="text" id="auto_tt3" relevanceHiddenId="pdealer" value="" autocomplete="off" style="width: 150px;"/>
									<input type="hidden" id="pdealer" name="pdealer" value="" class="search"/>
								</div>
							<#elseif loginOrg?exists && loginOrg.id!=1>
								<div class="analysis" style="line-height:30px;">
									<label>大区：${loginOrg.name}</label>
									<input type="hidden" name="porg" id="porg" value="${loginOrg.id}">
								</div>
								<div class="analysis">
									<label>网点：</label>
									<input type="text" id="auto_tt3" relevanceHiddenId="pdealer" value="" autocomplete="off" style="width: 150px;"/>
									<input type="hidden" id="pdealer" name="pdealer" value="" class="search"/>
								</div>
							<#else>
								<div class="analysis" style="line-height:30px;">
									<label>网点：<span style="padding:0 10px;height:30px;display:inline-block; line-height:30px;color: #939192;background: #f5f5f5!important;border:1px solid #d5d5d5;cursor: default;">${loginDealer.name}</span></label>
									<input type="hidden" id="pdealer" name="pdealer" value="${loginDealer.id}" class="search"/>
								</div>
							</#if>
							<div class="analysis">
								<label>活动名称：</label>
								<select style="width:150px;" name="cmplugin" id="cmplugin">
									<option value="">全部</option>
									<#list cms as cm>
										<#list cm?keys as c>
											<#if c=='id'>
												<option value="${cm[c]}">
											<#elseif c=='name'>
												${cm[c]}</option>
											</#if>
										</#list>
									</#list>
								</select>
							</div>
							<div class="analysis" id="plugin_s" style="line-height:30px;">
								<label class="floatL">插件类型：</label>
								<select class="floatL" name="pluginType" id="pluginType" style="width:150px;">
									<option value="">全部</option>
									<#list pluginTypes as pluginType>
										<option value="${pluginType.id}">${pluginType.name}</option>
									</#list>
								</select>
								<select class="floatL" name="pluginId" id="pluginId" style="display:none;margin:0 5px;">
								</select>
							</div>
							<div class="analysis">
								<input type="button" id="pl_button" value="搜 索" class="redButton" />
							</div>
						</form>
						<div class="totalCount" id="ptotalCount" style="display:none;">
							<ul>
								<li style="width: 50%;">
									<p>
										<span class="numbers" id="ppepoles"></span>
									</p>
									<p class="msg">插件参与人数</p>
								</li>
								<li style="width: 48%; border-right: none;">
								<p>
									<span class="numbers" id="lePlugins"></span>
									</p>
									<p class="msg">插件留资数</p>
								</li>
							</ul>
						</div>
						<div class="overflow-hidden">
							<div id="canvasPlugin"></div>
						</div>
						<div class="overflow-hidden activeTable" id="pm-div" style="display:none;">
							<div class="title">
								<a href="javascript:void(0)" id="pl-export" class="redButton" style="color: #fff;font-family:'微软雅黑';">导 出</a>
							</div>
							<div>
								<table class="reportData" id="plugin" width="100%">
									<thead>
										
									</thead>
									<tbody>
										
									</tbody>
								</table>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</#macro>
<#macro script>
		<script type="text/javascript" src="${rc.contextPath}/wechat/js/report/highcharts.js" ></script>
		<script type="text/javascript" src="${rc.contextPath}/wechat/js/report/highcharts-more.js" ></script>
		<script type="text/javascript" src="${rc.contextPath}/wechat/js/report/exporting.js" ></script>
		<script type="text/javascript" src="${rc.contextPath}/wechat/js/report/gray.js" ></script>
		<script type="text/javascript" src="${rc.contextPath}/wechat/js/report/Chart.js" ></script>
		<script type="text/javascript" src="${rc.contextPath}/js/report/jquery.bigautocomplete.js"></script>
		<script>
			$(function(){
				dateTimeInterface.init("dateTime","startTime1");
				dateTimeInterface.init("dateTime","endTime1");
				dateTimeInterface.init("dateTime","startTime2");
				dateTimeInterface.init("dateTime","endTime2");
				dateTimeInterface.init("dateTime","startTime3");
				dateTimeInterface.init("dateTime","endTime3");
				var chart = new Chart();
				<#if loginOrg?exists && loginOrg.id==1>
				$("#corg").change(function(){
					if($(this).val()){
						$('#cdealer').val('');
						$('#auto_tt1').val('');
						$("#auto_tt1").bigAutocomplete({
					    	width:270,
					    	url : "${rc.contextPath}/report/area/autocomplete?orgId="+$('#corg').val(),
					    	callback:function(data){
					    		$("#cdealer").val(data.id);
							}
						});
						$('#cdealerid').css("display","block");
					}else{
						$('#cdealerid').css("display","none");
						$('#cdealer').val('');
						$('#auto_tt1').val('');
					}
				});
				<#elseif loginOrg?exists && loginOrg.id!=1>
					$("#auto_tt1").bigAutocomplete({
					    	width:270,
					    	url : "${rc.contextPath}/report/area/autocomplete?orgId="+$('#corg').val(),
					    	callback:function(data){
					    		$("#cdealer").val(data.id);
							}
						});
				</#if>
				$('#cm_button').click(function(){
					var dealerId=$("#cdealer").val();
					var orgId=$("#corg").val();
					var searchDateType=$("input[name='campaigneffect']:checked").val();
					var searchDateBegin=$("#cstartTime").val();
					var searchDateEnd=$("#cendTime").val();
					var cmpaignId=$("#cmcamp").val();
					if(searchDateType=='4'){
						if(searchDateBegin==""){
							var layerAlert = new ElasticLayer("alert","请选择开始时间",{title : "信息提示框",dialog : true,time : 2000});
							layerAlert.init();
							return false;
						}
						if(searchDateEnd==""){
							var layerAlert = new ElasticLayer("alert","请选择结束时间",{title : "信息提示框",dialog : true,time : 2000});
							layerAlert.init();
							return false;
						}
						if(!checkEndTime(searchDateBegin,searchDateEnd)){
							var layerAlert = new ElasticLayer("alert","请选择正确的时间区间",{title : "信息提示框",dialog : true,time : 2000});
							layerAlert.init();
							return false;
						}
					}
					$.ajax({
						url:"${rc.contextPath}/wechat/reportCampaign/campaign",
						data:{dealerId:dealerId,orgId:orgId,searchDateType:searchDateType,searchDateBegin:searchDateBegin,searchDateEnd:searchDateEnd,cmpaignId:cmpaignId},
						type:'post',
						cache:false,
						dataType : 'json', 
						success:function(ms) {
							$("#cm-div").css("display","block");
							$("#campaign>thead").children().remove();
							$("#campaign>tbody").children().remove();
							$("#ctotalCount").css("display","block");
							$("#campaigns").html(ms.campaignCount);
							$("#pepoles").html(ms.cpepoles);
							$("#leads").html(ms.cleads);
							var $tr="";
							$tr=$("<tr><th width='20%'>大区</th><th width='20%'>网点名称</th><th width='20%'>开展活动名称</th><th width='20%'>活动开始时间</th><th width='20%'>活动结束时间</th></tr>");
							$("#campaign>thead").append($tr);
							for(var i=0;i<ms.list.length;i++){
								var $tr1=$("<tr></tr>");
								var $org=$("<td>"+ms.list[i]["dealer"]["organization"]["name"]+"</td>");
								var $dealer=$("<td>"+ms.list[i]["dealer"]["name"]+"</td>");
								var $cmname=$("<td>"+ms.list[i]["name"]+"</td>");
								var $starttime=$("<td>"+ms.list[i]["beginDate"]+"</td>");
								var $endtime=$("<td>"+ms.list[i]["endDate"]+"</td>");
								$tr1.append($org).append($dealer).append($cmname).append($starttime).append($endtime);
								$("#campaign>tbody").append($tr1);
							}
							var value1 = {
								title : "" ,
								subtitle : "" ,
								labelsText : "" ,
								credits : {
									creditsText : "",
									creditsHref : ""
								},
								categories : ms.date,
								yText : "活动数量/个",
								subffixValue : "个",
								data : [
									{
										name: '开展活动数量',
										color:"#e57d7e",
				            			data: ms.campaigns
									}
								],
								inverted : false,
								stacking : "percent" ,
								exporting : false
							}
							chart.drawColumn("canvasEffect","areaspline",value1);
						}
					});
				});
				<#if loginOrg?exists && loginOrg.id==1>
				$("#lorg").change(function(){
					if($(this).val()){
						$('#ldealer').val('');
						$('#auto_tt2').val('');
						$("#auto_tt2").bigAutocomplete({
					    	width:270,
					    	url : "${rc.contextPath}/report/area/autocomplete?orgId="+$('#lorg').val(),
					    	callback:function(data){
					    		$("#ldealer").val(data.id);
							}
						});
						$('#ldealerid').css("display","block");
					}else{
						$('#ldealerid').css("display","none");
						$('#ldealer').val('');
						$('#auto_tt2').val('');
					}
				});
				<#elseif loginOrg?exists && loginOrg.id!=1>
					$("#auto_tt2").bigAutocomplete({
				    	width:270,
				    	url : "${rc.contextPath}/report/area/autocomplete?orgId="+$('#lorg').val(),
				    	callback:function(data){
				    		$("#ldealer").val(data.id);
						}
					});
				</#if>
				$("#le_button").click(function(){
					var dealerId=$("#ldealer").val();
					var orgId=$("#lorg").val();
					var searchDateType=$("input[name='campaigneLeadffect']:checked").val();
					var searchDateBegin=$("#lstartTime").val();
					var searchDateEnd=$("#lendTime").val();
					var cmpaignId=$("#cmlead").val();
					if(searchDateType=='4'){
						if(searchDateBegin==""){
							var layerAlert = new ElasticLayer("alert","请选择开始时间",{title : "信息提示框",dialog : true,time : 2000});
							layerAlert.init();
							return false;
						}
						if(searchDateEnd==""){
							var layerAlert = new ElasticLayer("alert","请选择结束时间",{title : "信息提示框",dialog : true,time : 2000});
							layerAlert.init();
							return false;
						}
						if(!checkEndTime(searchDateBegin,searchDateEnd)){
							var layerAlert = new ElasticLayer("alert","请选择正确的时间区间",{title : "信息提示框",dialog : true,time : 2000});
							layerAlert.init();
							return false;
						}
					}
					$.ajax({
						url:"${rc.contextPath}/wechat/reportCampaign/campaignLead",
						data:{dealerId:dealerId,orgId:orgId,searchDateType:searchDateType,searchDateBegin:searchDateBegin,searchDateEnd:searchDateEnd,cmpaignId:cmpaignId},
						type:'post',
						cache:false,
						dataType : 'json', 
						success:function(ms) {
							$("#le-div").css("display","block");
							$("#lead>thead").children().remove();
							$("#lead>tbody").children().remove();
							$("#ltotalCount").css("display","block");
							$("#lleads").html(ms.leadCount);
							$("#lrecord").html(ms.recordCount);
							var $tr=$("<th width='15%'>日期</th><th width='10%'>大区</th><th width='20%'>网点名称</th><th width='25%'>开展活动名称</th><th width='15%'>活动参与人数</th><th width='15%'>活动留资数</th>");
							$("#lead>thead").append($tr);
							for(var i=0;i<ms.list.length;i++){
								var count = ms.list[i]["count"];
								var $tr1=$("<tr></tr>");
								var $day=$("<td>"+ms.list[i]["object"]["beginDate"]+"</td>");
                                var $org=$("<td>"+ms.list[i]["object"]["dealer"]["organization"]["name"]+"</td>");
                                var $dealer=$("<td>"+ms.list[i]["object"]["dealer"]["name"]+"</td>");
                                var $cmname=$("<td>"+ms.list[i]["object"]["name"]+"</td>");
								var $record=$("<td>"+count[0]+"</td>");
                                var $lead=$("<td><a class='openLeads' cid='"+ms.list[i]["object"]["id"]+"' style='cursor:pointer;color:red;'>"+count[1]+"</a></td>");
								$tr1.append($day).append($org).append($dealer).append($cmname).append($record).append($lead);
								$("#lead>tbody").append($tr1);
							}
							var value1 = {
								title : "" ,
								subtitle : "" ,
								labelsText : "" ,
								credits : {
									creditsText : "",
									creditsHref : ""
								},
								categories : ms.date,
								yText : "人数/个",
								subffixValue : "个",
								data : [
									{
										name : "留资数量",
										color : "#e57d7e" ,
										data : ms.leads
									},{
                                        name : "参与数量",
                                        color : "#947a7b" ,
                                        data : ms.records
                                    }
								],
								inverted : false,
								stacking : "percent" ,
								exporting : false
							}
							chart.drawColumn("canvasInformation","areaspline",value1);
						}
					});
				});
				<#if loginOrg?exists && loginOrg.id==1>
				$("#porg").change(function(){
					if($(this).val()){
						$('#pdealer').val('');
						$('#auto_tt3').val('');
						$("#auto_tt3").bigAutocomplete({
					    	width:270,
					    	url : "${rc.contextPath}/report/area/autocomplete?orgId="+$('#porg').val(),
					    	callback:function(data){
					    		$("#pdealer").val(data.id);
							}
						});
						$('#pdealerid').css("display","block");
					}else{
						$('#pdealerid').css("display","none");
						$('#pdealer').val('');
						$('#auto_tt3').val('');
					}
				});
				<#elseif loginOrg?exists && loginOrg.id!=1>
					$("#auto_tt3").bigAutocomplete({
				    	width:270,
				    	url : "${rc.contextPath}/report/area/autocomplete?orgId="+$('#porg').val(),
				    	callback:function(data){
				    		$("#pdealer").val(data.id);
						}
					});
				</#if>
				$('#pluginType').change(function(){
					if($(this).val()){
						$('#pluginId').children().remove();
						$.ajax({
							url:"${rc.contextPath}/wechat/reportCampaign/getPlugin",
							data:{pluginType:$(this).val()},
							type:'post',
							cache:false,
							dataType : 'json', 
							success:function(date) {
								$('#pluginId').append("<option value=''>全部</option>");
								for(var i=0;i<date.plugins.length;i++){
									$option=$("<option value='"+date.plugins[i].id+"'>"+date.plugins[i].name+"</option>");
									$('#pluginId').append($option);
								}
							}
						});
						$('#pluginId').css("display","block");
					}else{
						$('#pluginId').css("display","none");
						$('#pluginId').children().remove();
					}
				});
				$("#pl_button").click(function(){
					var dealerId=$("#pdealer").val();
					var orgId=$("#porg").val();
					var searchDateType=$("input[name='campaignePluginffect']:checked").val();
					var searchDateBegin=$("#pstartTime").val();
					var searchDateEnd=$("#pendTime").val();
					var pluginTypeId=$("#pluginType").val();
					var pluginId=$("#pluginId").val();
					var cmpaignId=$("#cmplugin").val();
					if(searchDateType=='4'){
						if(searchDateBegin==""){
							var layerAlert = new ElasticLayer("alert","请选择开始时间",{title : "信息提示框",dialog : true,time : 2000});
							layerAlert.init();
							return false;
						}
						if(searchDateEnd==""){
							var layerAlert = new ElasticLayer("alert","请选择结束时间",{title : "信息提示框",dialog : true,time : 2000});
							layerAlert.init();
							return false;
						}
						if(!checkEndTime(searchDateBegin,searchDateEnd)){
							var layerAlert = new ElasticLayer("alert","请选择正确的时间区间",{title : "信息提示框",dialog : true,time : 2000});
							layerAlert.init();
							return false;
						}
					}
					$.ajax({
						url:"${rc.contextPath}/wechat/reportCampaign/campaignPlugin",
						data:{dealerId:dealerId,orgId:orgId,searchDateType:searchDateType,searchDateBegin:searchDateBegin,searchDateEnd:searchDateEnd,pluginTypeId:pluginTypeId,pluginId:pluginId,cmpaignId:cmpaignId},
						type:'post',
						cache:false,
						dataType : 'json', 
						success:function(ms) {
							$("#pm-div").css("display","block");
							$("#plugin>thead").children().remove();
							$("#plugin>tbody").children().remove();
							$("#ptotalCount").css("display","block");
							$("#ppepoles").html(ms.recordCount);
							$("#lePlugins").html(ms.leadCount);
							var $tr=$("<tr>");
							$tr.append("<th width='15%'>日期</th><th width='10%'>大区</th><th width='20%'>网点名称</th><th width='25%'>开展活动名称</th><th width='10%'>插件名称</th><th width='10%'>插件参与人数</th><th width='10%'>插件留资数</th>");
							$("#plugin>thead").append($tr);
							for(var i=0;i<ms.list.length;i++){
                                var count = ms.list[i]["count"];
								var $tr1=$("<tr></tr>");
								var $day=$("<td>"+ms.list[i]["object"]["createDate"]+"</td>");
								var $org=$("<td>"+ms.list[i]["object"]["dealer"]["organization"]["name"]+"</td>");
								var $dealer=$("<td>"+ms.list[i]["object"]["dealer"]["name"]+"</td>");
								var $campaign=$("<td>"+ms.list[i]["object"]["campaign"]["name"]+"</td>");
								var $plugin=$("<td>"+ms.list[i]["object"]["plugin"]["name"]+"</td>");
								var $pe=$("<td>"+count[0]+"</td>");
								var $le=$("<td><a class='openLeads' cid='"+ms.list[i]["object"]["campaign"]["id"]+"' pid='"+ms.list[i]["object"]["plugin"]["id"]+"' style='cursor:pointer;color:red;'>"+count[1]+"</a></td>");
								$tr1.append($day).append($org).append($dealer).append($campaign).append($plugin).append($pe).append($le);
								$("#plugin>tbody").append($tr1);
							}
							var value1 = {
								title : "" ,
								subtitle : "" ,
								labelsText : "" ,
								credits : {
									creditsText : "",
									creditsHref : ""
								},
								categories : ms.date,
								yText : "人数/个",
								subffixValue : "个",
								data : [
									{
										name : "插件参与人数",
										color : "#947a7b" ,
										data : ms.records
									},
									{
										name : "插件留资数",
										color : "#e0e0e0" ,
										data : ms.leads
									}
								],
								inverted : false,
								stacking : "percent" ,
								exporting : false
							}
							chart.drawColumn("canvasPlugin","areaspline",value1);
						}
					});
				});
				
				$("#cm-export").click(function(){
					$("#cm-form").attr("action","${rc.contextPath}/wechat/reportCampaign/getCompaignExport")
					$("#cm-form").submit();
				});
				$("#le-export").click(function(){
					$("#le-from").attr("action","${rc.contextPath}/wechat/reportCampaign/getLeadExport")
					$("#le-from").submit();
				});
				$("#pl-export").click(function(){
					$("#pl-form").attr("action","${rc.contextPath}/wechat/reportCampaign/getPluginExport")
					$("#pl-form").submit();
				});
				
				$("body").delegate(".openLeads","click",function(){
                	layer.open({
	                	title:'留资信息',
						shadeClose: true,
						closeBtn: 1,
					    type: 1,
					    area: ['1000px', '600px'], //宽高
					    content: getLeadsTableHtml()
					});
					
					$("#campaignId").val($(this).attr("cid"));
					$("#_campaignId").val($(this).attr("cid"));
					if($(this).attr("pid") != undefined){
						$("#_pluginId").val($(this).attr("pid"));
						$("#pluginId_").val($(this).attr("pid"));
					}
					//var params = $('#subleadsForm').serialize()
					//params = params.replace("pageSize=","pageSize=5&r=");
					initPage();
				});
			});
			function checkEndTime(startTime,endTime){  
			    var start=new Date(startTime.replace("-", "/").replace("-", "/"));  
			    var end=new Date(endTime.replace("-", "/").replace("-", "/"));  
			    if(end<start){  
			        return false;  
			    }  
			    return true;  
			}
			
			function initPage(){
				var page = new pagination("subleadsForm","subleadsTable",function(value){
					$("#tbody1").children().remove();
					for(var i=0;i<value.length;i++){
						var obj = value[i];
						var $tr = $("<tr></tr>");
						var $org = $("<td>"+obj.largeArea.name+"</td>");
						var $dealer = $("<td>"+obj.dealer.name+"</td>");
						var $source = $("<td>"+(obj.campaign==undefined?"微站":"活动")+"</td>");
						var $sourceName = $("<td style='text-align:center;'>"+(obj.campaign==undefined?"微站":obj.campaign.name)+"</td>");
						var $pluginName = $("<td style='text-align:center;'>"+(obj.plugin==undefined?"":obj.plugin.name)+"</td>");
						var $name = $("<td>"+obj.leadsName+"</td>")
						var $phone = $("<td>"+obj.leadsPhone+"</td>");
                        var $time = $("<td>"+obj.createDate+"</td>");
						$tr.append($org).append($dealer).append($source).append($sourceName).append($pluginName).append($name).append($phone).append($time);
						$("#tbody1").append($tr);
					}
					
				});
			}
			
			function exportLeads(){
				$("#leadsDetailForm").attr("action","${rc.contextPath}/wechat/reportCampaign/getCampaignLeadExport")
				$("#_phone").val($("#phone").val());
				$("#_customName").val($("#name").val());
				console.log($("#leadsDetailForm").serialize());
				$("#leadsDetailForm").submit();
			}
			
			function getLeadsTableHtml(){
				return '<div>'
				+ '	<form id="leadsDetailForm">'
				+ '		<input type="hidden" value="5" name="effect" />'
			    + '    	<input type="hidden" class="search" id="pluginId_" value="${form.pluginId?if_exists}" name="pluginId" />'
			    + '    	<input type="hidden" class="search" id="_campaignId" value="${form.campaignId?if_exists}" name="cmpaignId" />'
			    + '    	<input type="hidden" class="search" value="0" name="searchDateType" />'
			    + '    	<input type="hidden" id="_customName" name="name" />'
			    + '    	<input type="hidden" id="_phone" name="phone"/>'
				+ '	</form>'
				+ '<div class="title" style=" width: 100%;height: 40px;line-height: 40px;padding-left: 20px;">'
				+ '		<form id="subleadsForm" action="${rc.contextPath}/wechat/reportCampaign/campaignLeadDetail" method="post" >'
				+ '			 <div class="floatL" >'
				+ '				<label>客户名称:</label>'
				+ '				<input type="text" id="name" name="name" class="search" style="width: 183px;"/>'
				+ '			</div>'
				+ '			<div class="floatL" style="margin-left:5px;">'
				+ '				<label>手机号码:</label>'
				+ '				<input type="text" id="phone" name="phone" class="search" value="${form.phone?if_exists}" style="width: 183px;"/>'
				+ '			</div>'
				+ '			<div class="floatL" style="margin:5px;">'
				+ '				<input type="hidden" class="search" id="campaignId" name="cmpaignId"/><input type="hidden" class="search" id="_pluginId" name="pluginId"/>'
				+ '				<input type="button" value="搜 索 " class="redButton submit" style="float:left;display:block;font-size:12px;" />'
				+ '			</div>'
				+ '		</form>'
				+ '	</div>'
				+ '	<div id="subleadsTable">'
				+ '	<div style="width:100%;margin:10px 10px;">'
			   	+ '		<a href="javascript:void(0);" onclick="exportLeads();" class="redButton" style="color: #fff;">导 出</a>'
			   	+ '	</div>'
				+ '		<table  class="reportData shareList" style="width:100%;border:1px solid #d9d9d9;margin:0 10px;text-align:center">'
				+ '			<thead>'
				+ '			<th width="10%">大区</th><th width="15%">网点名称</th><th style="text-align:center" width="10%">留资来源</th><th width="20%">活动名称</th><th width="10%">插件名称</th><th width="10%">客户姓名</th><th width="10%">手机号码</th><th  width="15%">留资时间</th>'
				+ '			</thead>'
				+ '			<tbody id="tbody1">'
				+ '			</tbody>'
				+ '		</table>'
				+ '		<div class="padding30 marginTop20">'
				+ '			<div class="pagegination floatR">'
				+ '				<ul>'
				+ '				</ul>'
				+ '			</div>'
				+ '		</div>'
				+ '	</div>'
				+ '</div>'
			}
		</script>
</#macro>