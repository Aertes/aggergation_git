<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#assign tag=JspTaglibs["/WEB-INF/taglib/permission.tld"]>
<#include "/wechat/layouts/main.ftl"/>
<@html></@html>
<#macro style>
<link rel="stylesheet" href="${rc.contextPath}/wechat/assets/css/font-awesome.min.css"/>
<link rel="stylesheet" href="${rc.contextPath}/wechat/css/reset.css"/>
<link rel="stylesheet" href="${rc.contextPath}/wechat/css/commom.css"/>
<link rel="stylesheet" href="${rc.contextPath}/wechat/css/color.css"/>
<link rel="stylesheet" href="${rc.contextPath}/wechat/css/custome.css"/>
<link rel="stylesheet" href="${rc.contextPath}/wechat/assets/css/ace-fonts.css"/>
<link rel="stylesheet" href="${rc.contextPath}/wechat/assets/css/ace.min.css"/>
<link rel="stylesheet" href="${rc.contextPath}/wechat/assets/css/ace-rtl.min.css"/>
<link rel="stylesheet" href="${rc.contextPath}/wechat/assets/css/ace-skins.min.css"/>
<script src="${rc.contextPath}/wechat/assets/js/ace-extra.min.js"></script>
<link rel="stylesheet" href="${rc.contextPath}/wechat/css/bootstrap-switch.css"/>
<link rel="stylesheet" href="${rc.contextPath}/wechat/css/report.css"/>
<link rel="stylesheet" type="text/css" href="${rc.contextPath}/css/jquery.bigautocomplete.css">
<style>
	.times >div > input{
		margin:9px 0 0;
	}
	.times >div{
		line-height:30px;
	}
	.leadstotal{
		width:70%;
		margin:10px auto;
		border:1px solid #d9d9d9;
	}
	.borderbottom{
		border-bottom:1px solid #d9d9d9;
	}
	.analysis label{
		font-size:12px;
	}
	.reportData thead th{
		font-weight:bold;
		font-size:12px;
		background:#fbfbfb;
	}
	.leadstotal{
		margin:30px auto;
	}
</style>
</#macro>
<#macro content>
<div class="breadcrumbs" id="breadcrumbs">
    <script type="text/javascript">
        try {
            ace.settings.check('breadcrumbs', 'fixed')
        } catch (e) {
        }
    </script>

    <ul class="breadcrumb">
        <li>
            <i class="icon-home home-icon"></i>
            <span>数据中心</span>
            <span>&gt;&gt;</span>
            <span>留资报表</span>
        </li>
    </ul>
</div>
<div>
    <h1 class="title_page pr">留资管理</h1>
    <div class="message" style="line-height: 40px;">
    	<a class=" marginLeft10" href="${rc.contextPath}/wechat/report/leads/merged">合并留资</a>
        <a class=" marginLeft10" href="${rc.contextPath}/wechat/report/leads/crm">回流列表</a>
        <a class=" marginLeft10" href="${rc.contextPath}/wechat/customer/index">客户列表</a>
    </div>
    <form class="overflow-hidden form-search" style="margin-left:20px;" action="${rc.contextPath}/wechat/report/leads/search" id="_0">
        <div class="analysis times">
            <div class="floatL marginLeft20">
                <input type="radio" class="search" value="2" name="effect"/>
                <label>上周</label>
            </div>
        	 <div class="floatL marginLeft20">
                <input type="radio" class="search" value="01" checked="checked"  name="effect"/>
                <label>本周</label>
            </div>
            <div class="floatL marginLeft20">
                <input type="radio" class="search" value="0" name="effect"/>
                <label>本月</label>
            </div>
           
            <div class="floatL marginLeft20">
                <input type="radio" class="search" value="-1" name="effect"/>
                <label>上月</label>
            </div>
            <div class="floatL marginLeft20">
                <input type="radio" class="search floatL" style="display:block;" value="99" name="effect"/>
                <label style="float:left;padding:0 5px;">自定义</label>
                <div id="beginDate" style="width: 150px;float:left;" class="search" params="{name:'beginDate',id:'cbeginDate',value:''}"></div>
                <label style="float:left;padding:0 5px;">-</label>
                 <div id="endDate" style="width: 150px;float:left;" class="search" params="{name:'endDate',id:'cendDate',value:''}"></div>
            </div>
        </div>
        <#if loginOrg?exists && loginOrg.id==1>
	        <div class="analysis">
	            <label>大区：</label>
	            <input type="hidden" class="search" name="organizationLevel" value="2">
	            <select name="organizationSelectedId" id="orgId" class="search" style="width: 150px;">
	                <option value="">全部</option>
	                <#list orgs as org>
	                <option value="${org.id}">${org.name}</option>
	                </#list>
	            </select>
	
	        </div>
	        <div class="analysis" id="cdealerid" style="display:none;">
	            <label>网点名称：</label>
	            <input style="width:150px;" type="text" class="key"  id="auto_tt" relevanceHiddenId="autoHiddenId" value="${dealer.name?if_exists}" autocomplete="off"/>
	            <input type="hidden" class="search" id="autoHiddenId" name="ledpDealer" value="${dealer.id?if_exists}" />
	        </div>
	    <#elseif loginOrg?exists && loginOrg.id!=1>
	        <div class="analysis">
	            <label>大区：${loginOrg.name}</label>
	            <input type="hidden" name="organizationSelectedId" id="orgId" value="${loginOrg.id}" class="search" >
	        </div>
	        <div class="analysis">
	            <label>网点名称：</label>
	            <input type="text" class="key"  id="auto_tt" relevanceHiddenId="autoHiddenId" value="${dealer.name?if_exists}" autocomplete="off"/>
	            <input type="hidden" class="search" id="autoHiddenId" name="ledpDealer" value="${dealer.id?if_exists}" />
	        </div>
	    <#else>
	     	<div class="analysis">
	            <label>网点名称：<span style="padding:0 10px;height:30px;display:inline-block; line-height:30px;color: #939192;background: #f5f5f5!important;border:1px solid #d5d5d5;cursor: default;">${loginDealer.name}</span></label>
	            <input type="hidden" class="search" id="autoHiddenId" name="ledpDealer" value="${loginDealer.id}" />
	        </div>
        </#if>
        <div class="analysis">
            <label>留资来源：</label>
            <select name="source" class="search" style="width:150px;">
            	<option value="">全部</option>
            	<option value="0">活动</option>
            	<option value="1">微站</option>
            </select>
            <input type="hidden" class="search" name="ledpMediaId" value="${mediaList.id}" />
        </div>
        <div class="analysis">
            <label>留资类型：</label>
            <select name="typeId" class="search" style="width:150px;">
            	<option value="">全部</option>
            	<#list constants as constant>
            		<#if constant.id==3010 || constant.id==3020 || constant.id==3120 || constant.id==3110>
            			<option value="${constant.id}">${constant.name}</option>
            		</#if>
            	</#list>
            </select>
        </div>
        <div class="analysis">
            <label>跟进状态：</label>
            <@tag.select type="lesds_state" style="search" name="ledpFollowId" defaultValue=ledpFollowId?default(0)/>
        </div>
        <div class="analysis">
            <label>客户名称：</label>
            <input type="text" name="name" class="search" style="width: 150px;"/>
        </div>
        <div class="analysis">
            <label>手机号码：</label>
            <input type="text" name="phone" class="search" style="width: 150px;"/>
        </div>
        <div class="analysis">
            <input type="button" id="lead_button" value="搜 索" class="redButton submit"/>
        </div>
    </form>
    <div class="totalCount leadstotal" id="ltotalCount" style="display:none;">
		<ul>
			<li>
				<p>
					<span class="numbers" id="leads"></span>
				</p>
				<p class="msg">总留资数</p>
			</li>
			<li>
				<p>
					<span class="numbers" id="cleads"></span>
				</p>
				<p class="msg">活动留资数</p>
			</li>
			<li style="border-right: 0px;">
				<p>
					<span class="numbers" id="sleads"></span>
				</p>
				<p class="msg">常规留资数</p>
			</li>
		</ul>
	</div>
    <div style="margin-left:20px; display:none;" id="lead_table" >
        <div id="_1" class="overflow-hidden activeTable" style="margin:0;">
            <div class="title">
                <a href="javascript:void(0)" onclick="doExport();" class="redButton" style="color: #fff;">导 出</a>
            </div>
            <div>
                <table class="reportData" width="100%">
                    <thead>
                    <tr>
                    	<th width="10%">大区</th>
                        <th width="20%">网点名称</th>
                        <th width="10%">留资类型</th>
                        <th width="10%">客户名称</th>
                        <th width="10%">手机号码</th>
                        <th width="10%">留资来源</th>
                        <th width="15%">留资时间</th>
                        <th width="10%">跟进状态</th>
                        <th width="5%">操作</th>
                    </tr>
                    </thead>
                    <tbody id="tbody1">
                    </tbody>
                </table>
            </div>
            <div class="padding30 marginTop20">
                <div class="pagegination floatR">
                    <ul></ul>
                </div>
            </div>
        </div>
    </div>

</#macro>
<#macro script>
    <script type="text/javascript" src="${rc.contextPath}/js/report/jquery.bigautocomplete.js"></script>
    <script type="text/javascript" charset="utf-8">
        // 导出
        function doExport(){
            $("#_0").attr("action","${rc.contextPath}/wechat/report/leads/doExportLeads");
            $("#_0").submit();
            $("#_0").attr("action","${rc.contextPath}/wechat/report/leads/search");
        }

        function _autoComplete(org) {
            $("#auto_tt").bigAutocomplete({
                width:270,
                url : "${rc.contextPath}/wechat/report/autocomplete?id=" + org,
                callback:function(data){
                    $("#autoHiddenId").val(data.id);
                }
            });
        }

        var _dealer = {};

        $(function () {
        	dateTimeInterface.init("dateTime","beginDate");
			dateTimeInterface.init("dateTime","endDate");
            _dealer.id = $("#autoHiddenId").val();
            _dealer.name = $("#auto_tt").val();

            var _org = $("#orgId").val();
            if(_org) {
                _autoComplete(_org);
            }
			<#if loginOrg?exists && loginOrg.id==1>
            $("#orgId").on("change",function() {
	             if($(this).val()){
					$('#autoHiddenId').val('');
					$('#auto_tt').val('');
					$("#auto_tt").bigAutocomplete({
				    	width:270,
				    	url : "${rc.contextPath}/report/area/autocomplete?orgId="+$('#orgId').val(),
				    	callback:function(data){
				    		$("#autoHiddenId").val(data.id);
						}
					});
					$('#cdealerid').css("display","block");
				}else{
					$('#cdealerid').css("display","none");
					$('#autoHiddenId').val('');
					$('#auto_tt').val('');
				}
            });
            <#elseif loginOrg?exists && loginOrg.id!=1>
           	 	$("#auto_tt").bigAutocomplete({
			    	width:270,
			    	url : "${rc.contextPath}/report/area/autocomplete?orgId="+$('#orgId').val(),
			    	callback:function(data){
			    		$("#autoHiddenId").val(data.id);
					}
				});
			</#if>
            $("#lead_button").click(function(){
            	if(!validate()){
            		return false;
            	}
            	$("#ltotalCount").css("display","block");
            	$("#lead_table").css("display","block");
	            var page = new pagination("_0", "_1", function (value) {
	            	$("#leads").html(value.leads);
	            	$("#cleads").html(value.cleads);
	            	$("#sleads").html(value.sleads);
	                if ( value ) {
	                    var $_tbody = $("#tbody1");
	                    $_tbody.children().remove();
	                    var rows = value.rowList.rows;
	                    var obj;
	                    for (var i = 0; i < rows.length; ++i) {
	                        obj = rows[i];
	                        var $_tr = $("<tr></tr>");
	                        var $_td = $("<td>"+obj["ledpOrg.name"]+"</td>");
	                        var $_td0 = $("<td>"+obj["ledpDealer.name"]+"</td>");
	                        var $_td1 = $("<td>"+obj["ledpType.name"]+"</td>");
	                        var $_td2 = $("<td>"+obj["name"]+"</td>");
	                        var $_td3 = $("<td>"+obj["phone"]+"</td>");
	                        var $_td4="";
	                        if(obj["campaign"]){
	                        	$_td4 = $("<td>活动</td>");
	                        }else{
	                        	$_td4 = $("<td>微站</td>");
	                        }
	                        var $_td5 = $("<td>"+obj["createTime"]+"</td>");
	                        var $_td6 = $("<td>"+obj["ledpFollow.name"]+"</td>");
	                        var $_td7 = $("<td>"+obj["operations"]+"</td>");
	
	                        $_tr.append($_td).append($_td0).append($_td1).append($_td2)
	                                .append($_td3).append($_td4).append($_td5).append($_td6)
	                                .append($_td7).appendTo($_tbody);
	                    }
	                }
	            },validate());
            });
            
        });
        function validate(){
        	var searchDateType=$("input[name='effect']:checked").val();
			var searchDateBegin=$("#cbeginDate").val();
			var searchDateEnd=$("#cendDate").val();
			if(searchDateType=='99'){
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
			return true;
        }
        function checkEndTime(startTime,endTime){  
		    var start=new Date(startTime.replace("-", "/").replace("-", "/"));  
		    var end=new Date(endTime.replace("-", "/").replace("-", "/"));  
		    if(end<start){  
		        return false;  
		    }  
		    return true;  
		}
    </script>
</#macro>