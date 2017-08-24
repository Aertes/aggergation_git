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
<style>
	.infojb{
		font-family: "微软雅黑";
		font-size: 14px;
		line-height: 50px;
		background: #fbfbfb;
		text-indent: 2em;
	}
	.clearfix{
		content: '';
		overflow: hidden;
	}
	.floatL{
		float:left;
	}
	ul,li{
		list-style: none;
	}
	.listinfo{
		margin:10px;
	}
	.listinfo >li >div{
		width: 45%;
		margin: 1% 0;
	}
	.listinfo >li >div>label{
		width: 30%;
		font-size: 12px;
		text-align: right;
		display: inline-block;
	}
	.listinfo >li >div>span{
		font-size: 12px;
	}
	.tablelist{
		border:1px solid #d9d9d9;
		border-right: none;
	}
	.tablelist th{
		padding: 10px;
		border-right:1px solid #d9d9d9;
		text-align: center;
		font-size: 14px;
	}
	.tablelist td{
		padding: 10px;
		text-align: center;
		font-size: 12px;
		border-top:1px solid #d9d9d9;
		border-right:1px solid #d9d9d9;
	}
	.reportData thead th{
		font-weight:bold;
		font-size:12px;
		background:#fbfbfb;
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
            <span>留资详情</span>
        </li>
    </ul>
</div>
<div>
    <div>
			<h1 class="infojb">基本信息</h1>
			<div>
				<ul class="listinfo">
					<li class="clearfix">
						<div class="floatL">
							<label>大区：</label>
							<span>${leads.ledpOrg.name?if_exists}</span>
						</div>
						<div class="floatL">
							<label>网点名称：</label>
							<span>${leads.ledpDealer.name?if_exists}</span>
						</div>
					</li>
					<li class="clearfix">
						<div class="floatL">
							<label>网点编号：</label>
							<span>${leads.ledpDealer.code?if_exists}</span>
						</div>
					</li>
					<li class="clearfix">
						<div class="floatL">
							<label>客户名称：</label>
							<span>${leads.name?if_exists}</span>
						</div>
						<div class="floatL">
							<label>客户性别：</label>
							<span><#if leads.userGender?if_exists && leads.userGender==0>男<#else>女</#if></span>
						</div>
					</li>
					<li class="clearfix">
						<div class="floatL">
							<label>省份：</label>
							<span>${leads.ledpProvince.name?if_exists}</span>
						</div>
						<div class="floatL">
							<label>城市：</label>
							<span>${leads.ledpCity.name?if_exists}</span>
						</div>
					</li>
					<li class="clearfix">
						<div class="floatL">
							<label>手机号码：</label>
							<span>${leads.phone?if_exists}</span>
						</div>
						<div class="floatL">
							<label>客户邮件：</label>
							<span>${leads.userMail?if_exists}</span>
						</div>
					</li>
				</ul>
			</div>
			<h1 class="infojb">意向信息</h1>
			<div>
				<ul class="listinfo">
					<li class="clearfix">
						<div class="floatL">
							<label>车系名称：</label>
							<span>${leads.ledpSeries.name?if_exists}</span>
						</div>
						<div class="floatL">
							<label>车型名称：</label>
							<span>${leads.ledpVehicle.name?if_exists}</span>
						</div>
					</li>
					<li class="clearfix">
						<div class="floatL">
							<label>留资类型：</label>
							<span>${leads.ledpType.name?if_exists}</span>
						</div>
						<div class="floatL">
							<label>留资来源：</label>
							<#if !leads.campaign?exists>
								<span>微站</span>
							<#else>
								<span>活动</span>
							</#if>
						</div>
					</li>
					<li class="clearfix">
						<div class="floatL">
							<label>线索状态：</label>
							<span>${leads.ledpFollow.name?if_exists}</span>
						</div>
						<div class="floatL">
							<label>意向级别：</label>
							<span>${leads.ledpIntent.name?if_exists}</span>
						</div>
					</li>
					<li class="clearfix">
						<div class="floatL">
							<label>预约时间：</label>
							<span>${leads.planTime}</span>
						</div>
						<div class="floatL">
							<label>线索留资时间：</label>
							<span>${leads.dateCreate?string("yyyy-MM-dd HH:mm:ss")}</span>
						</div>
					</li>
					<li class="clearfix">
						<div class="floatL">
							<label>备注：</label>
							<span>${leads.remark?if_exists}</span>
						</div>
						<div class="floatL">
						</div>
					</li>
				</ul>
			</div>
		</div>
		<div>
			<h1 class="infojb">活动线索列表</h1>
			<form id="winForm" action="${rc.contextPath}/wechat/wininfo/search">
				<input type="hidden" class="search" name="phone" value="${leads.phone?if_exists}"/>
				<input type="hidden" class="search" name="org" value="${leads.ledpDealer.org.id?if_exists}"/>
				<input type="hidden" class="search" name="dealer" value="${leads.ledpDealer.id?if_exists}"/>
			</form>
			<div id="winTable" style="margin-left:20px;">
				<table cellpadding="0" cellspacing="0" class="reportData tablelist" width="100%">
					<thead>
						<th>时间</th>
						<th>大区</th>
						<th>网点名称</th>
						<th>活动名称</th>
						<th>中奖信息</th>
					</thead>
					<tbody id="tbody1">
					</tbody>
				</table>
				<div>
					<div class="pagegination floatR" style="margin:20px;">
						<ul>
						</ul>
					</div>
				</div>
			</div>
		</div>
</#macro>
<#macro script>
    <script type="text/javascript" charset="utf-8">

        $(function () {
        	var page = new pagination("winForm", "winTable", function (rows) {
                var $_tbody = $("#tbody1");
                $_tbody.children().remove();
                var obj;
                for (var i = 0; i < rows.length; ++i) {
                    obj = rows[i];
                    var $_tr = $("<tr></tr>");
                    var $_td = $("<td>"+obj.createTime+"</td>");
                    var $_td0 = $("<td>"+obj.dealer.organization.name+"</td>");
                    var $_td1 = $("<td>"+obj.dealer.name+"</td>");
                    var $_td2 = $("<td>"+obj.sourceName+"</td>");
                    var $_td3 = $("<td>"+obj.awardsName+"</td>");
                    $_tr.append($_td).append($_td0).append($_td1).append($_td2)
                            .append($_td3).appendTo($_tbody);
                }
            });
            
        });
    </script>
</#macro>