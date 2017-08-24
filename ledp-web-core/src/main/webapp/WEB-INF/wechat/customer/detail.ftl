<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#assign tag=JspTaglibs["/WEB-INF/taglib/permission.tld"]>
<#include "/wechat/layouts/main.ftl"/>
<@html></@html>
<#macro style>
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
		clear: both;
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
            <span>查看客户</span>
        </li>
    </ul>
</div>
<div>
    <div>
			<h1 class="infojb">基本信息</h1>
			<div>
				<ul class="listinfo">
					<li>
						<div>
							<label>客户名称：</label>
							<span>${customer.name?if_exists}</span>
						</div>
					</li>
					<li>
						<div>
							<label>电话号码：</label>
							<span>${customer.phone?if_exists}</span>
						</div>
					</li>
					<li>
						<div>
							<label>客户邮件：</label>
							<span>${customer.email?if_exists}</span>
						</div>
					</li>
					<li>
						<div>
							<label>省份：</label>
							<span>${customer.province?if_exists}</span>
						</div>
					</li>
					<li>
						<div>
							<label>城市：</label>
							<span>${customer.city}</span>
						</div>
					</li>
					<li>
						<div>
							<label>区县：</label>
							<span>${customer.district?if_exists}</span>
						</div>
					</li>
					<li>
						<div>
							<label>详细地址：</label>
							<span>${customer.address?if_exists}</span>
						</div>
					</li>
				</ul>
			</div>
		<div>
		<h1 class="infojb">销售线索列表</h1>
		<form id="leadsForm" action="${rc.contextPath}/wechat/report/leads/searchLeads">
			<input type="hidden" class="search" name="phone" value="${customer.phone?if_exists}"/>
		</form>
		<div id="leadsTable" style="margin-left:20px;">
			<table cellpadding="0" cellspacing="0" class="reportData tablelist" width="100%">
				<thead>
					<th width="10%">大区</th>
					<th width="20%">网点名称</th>
					<th width="15%">线索类型</th>
					<th width="16%">意向级别</th>
					<th width="17%">跟进状态</th>
					<th width="15%">留资时间</th>
					<th width="10%">操作</th>
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
        	var page = new pagination("leadsForm", "leadsTable", function (rows) {
                var $_tbody = $("#tbody1");
                $_tbody.children().remove();
                var obj;
                for (var i = 0; i < rows.length; ++i) {
                    obj = rows[i];
                    var $_tr = $("<tr></tr>");
                    var $_td = $("<td>"+obj.ledpOrg.name+"</td>");
                    var $_td0 = $("<td>"+obj.ledpDealer.name+"</td>");
                    var $_td1 = $("<td>"+obj.ledpType.name+"</td>");
                    var $_td2 = $("<td>"+obj.ledpIntent.name+"</td>");
                    var $_td3 = $("<td>"+obj.ledpFollow.name+"</td>");
                    var $_td4 = $("<td>"+obj.createTime+"</td>");
                    var $_td5 = $("<td><a href='${rc.contextPath}/wechat/report/leads/detail/"+obj.id+"'><img src='${rc.contextPath}/images/magnifier.png'></a></td>");
                    $_tr.append($_td).append($_td0).append($_td1).append($_td2)
                            .append($_td3).append($_td4).append($_td5).appendTo($_tbody);
                }
            });
            
        });
    </script>
</#macro>