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
            <span><a href="${rc.contextPath}/wechat/report/leads/index">留资列表</a></span>
            <span>&gt;&gt;</span>
            <span>客户列表</span>
        </li>
    </ul>
</div>
<div>
    <h1 class="title_page pr">客户列表</h1>
    <div class="message" style="line-height: 40px;">
    	<a class=" marginLeft10" href="${rc.contextPath}/wechat/report/leads/index">留资列表</a>
    	<a class=" marginLeft10" href="${rc.contextPath}/wechat/report/leads/merged">合并留资</a>
        <a class=" marginLeft10" href="${rc.contextPath}/wechat/report/leads/crm">回流列表</a>
    </div>
    <form class="overflow-hidden form-search" style="margin-left:20px;" action="${rc.contextPath}/wechat/customer/search" id="_0">
        <div class="analysis">
            <label>客户名称：</label>
            <input type="text" name="name" class="search" style="width: 150px;"/>
        </div>
        <div class="analysis">
            <label>手机号码：</label>
            <input type="text" name="phone" class="search" style="width: 150px;"/>
        </div>
        <div class="analysis">
            <label>跟进状态：</label>
            <@tag.select type="lesds_state" style="search" name="follow" defaultValue=ledpFollowId?default(0)/>
        </div>
        <div class="analysis">
            <label>电子邮箱：</label>
            <input type="text" name="email" class="search" style="width: 150px;"/>
        </div>
        <div class="analysis">
			<label>意向级别：</label>
			<@tag.select type="leads_intent" style="key search moreWidthselect " name="intent" defaultValue=intent?default(0)/>
		</div>
        <div class="analysis">
            <input type="button" id="lead_button" value="搜 索" class="redButton submit"/>
        </div>
    </form>
    <div style="margin-left:20px; display:none;" id="lead_table" >
        <div id="_1" class="overflow-hidden activeTable" style="margin:0;">
            <div class="title">
                <a href="javascript:void(0)" onclick="doExport();" class="redButton" style="color: #fff;">导 出</a>
            </div>
            <div>
                <table class="reportData" width="100%">
                    <thead>
                    <tr>
                        <th width="20%">客户名称</th>
                        <th width="15%">手机号码</th>
                        <th width="15%">电子邮箱</th>
                        <th width="15%">意向级别</th>
                        <th width="15%">跟进状态</th>
                        <th width="120%">操作</th>
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
    <script type="text/javascript" charset="utf-8">
        // 导出
        function doExport(){
            $("#_0").attr("action","${rc.contextPath}/customer/doExport");
            $("#_0").submit();
            $("#_0").attr("action","${rc.contextPath}/leads/search");
        }


        $(function () {
        	loadTable();
            $("#lead_button").click(function(){
	            loadTable();
            });
            
        });
        
        function loadTable(){
        	$("#lead_table").css({"display":""});
        	var page = new pagination("_0", "_1", function (value) {
                if (value) {
                    var $_tbody = $("#tbody1");
                    $_tbody.children().remove();
                    var obj;
                    for (var i = 0; i < value.length; ++i) {
                        obj = value[i];
                        var $_tr = $("<tr></tr>");
                        var $_td = $("<td>"+obj.name+"</td>");
                        var $_td0 = $("<td>"+obj.phone+"</td>");
                        var $_td1 = $("<td>"+(obj.email==null?'':obj.email)+"</td>");
                        var $_td2 = $("<td>"+obj.intent.name+"</td>");
                        var $_td3 = $("<td>"+(obj.follow==null?'':obj.follow.name)+"</td>");
                        var $_td4 = $("<td><a href='${rc.contextPath}/wechat/customer/detail/"+obj.id+"'><img src='${rc.contextPath}/images/magnifier.png'></a><a href='${rc.contextPath}/wechat/customer/update/"+obj.id+"'><img src='${rc.contextPath}/images/edit.png'></a></td>");
                        $_tr.append($_td).append($_td0).append($_td1).append($_td2)
                                .append($_td3).append($_td4).appendTo($_tbody);
                    }
                }
            });
        }
    </script>
</#macro>