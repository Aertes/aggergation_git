<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#assign tag=JspTaglibs["/WEB-INF/taglib/permission.tld"]>
<#include "/wechat/layouts/main.ftl"/>
<@html></@html>
<#macro style>
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
            <span><a href="${rc.contextPath}/wechat/report/leads/crm">回流列表</a></span>
            <span>&gt;&gt;</span>
            <span>回流详情</span>
        </li>
    </ul>
</div>
    <div>
		<div>
			<h2 class="infojb">导入信息</h2>
			<ul class="listinfo">
			<li class="clearfix">
				<div class="floatL">
					<label>商机ID:</label>
					<span>${leads.a}</span>
				</div>
				<div class="floatL">
					<label>线索ID:</label>
					<span>${leads.b}</span>
				</div>
			</li>
			<li class="clearfix">
			<div class="floatL">
				<label>客户编号:</label>
				<span  class="iname">${leads.c}</span>
				</div>
				<div class="floatL">
				<label>客户名称:</label>
				<span title="姓名" class="iname">${leads.d}</span>
				</div>
			</li>
			<li class="clearfix">
				<div class="floatL">
				<label>手机号码:</label>
				<span class="iname">${leads.e}</span>
				</div>
				<div class="floatL">
				<label>首次任务接收时间:</label>
				<span  class="iname">${leads.f}</span>
				</div>
			</li>
			<li class="clearfix">
			<div class="floatL">
				<label>首次任务完成时间:</label>
				<span  class="iname">${leads.g}</span>
				</div>
				<div class="floatL">
				<label>大区:</label>
				<span  class="iname">${leads.h}</span>
				</div>
			</li>
			<li class="clearfix">
			<div class="floatL">
				<label>网点编码:</label>
				<span  class="iname">${leads.i}</span>
				</div>
				<div class="floatL">
				<label>网点名称:</label>
				<span  class="iname">${leads.j}</span>
				</div>
			</li>
			<li class="clearfix">
			<div class="floatL">
				<label>电销标记:</label>
				<span  class="iname">${leads.k}</span>
				</div>
				<div class="floatL">
				<label>一级渠道来源:</label>
				<span  class="iname">${leads.l}</span>
				</div>
			</li>
			<li class="clearfix">
			<div class="floatL">
				<label>营销活动名称:</label>
				<span  class="iname">${leads.m}</span>
				</div>
				<div class="floatL">
				<label>意向子车系:</label>
				<span  class="iname">${leads.n}</span>
				</div>
			</li>
			<li class="clearfix">
			<div class="floatL">
				<label>互动类型:</label>
				<span  class="iname">${leads.o}</span>
				</div>
				<div class="floatL">
				<label>是否有订单:</label>
				<span  class="iname">${leads.p}</span>
				</div>
			</li>
			<li class="clearfix">
			<div class="floatL">
				<label>申报台数:</label>
				<span  class="iname">${leads.q}</span>
				</div>
				<div class="floatL">
				<label>原意向级别:</label>
				<span  class="iname">${leads.r}</span>
				</div>
			</li>
			<li class="clearfix">
			<div class="floatL">
				<label>现意向级别:</label>
				<span  class="iname">${leads.s}</span>
				</div>
				<div class="floatL">
				<label>任务执行人:</label>
				<span  class="iname">${leads.t}</span>
				</div>
			</li>
			<li class="clearfix">
			<div class="floatL">
				<label>战败原因:</label>
				<span  class="iname">${leads.u}</span>
				</div>
				<div class="floatL">
				<label>失控原因:</label>
				<span  class="iname">${leads.v}</span>
				</div>
			</li>
			<li class="clearfix">
			<div class="floatL">
				<label>访谈内容:</label>
				<span  class="iname">${leads.w}</span>
				</div>
				
			</li>
			<li class="clearfix">
			<div class="floatL">
			<label>是否有申报:</label>
				<span  class="iname">${leads.x}</span>
				</div>
				<div class="floatL">
				<label>总部下发or网点创建时间:</label>
				<span  class="iname">${leads.y}</span>
				</div>
			</li>
			</div>
			<h2 class="infojb">操作信息</h2>
			<ul class="listinfo">
				<li class="clearfix">
					<div class="floatL">
						<label>导入时间:</label>
						<span  class="iname">${(leads.batch?string("yyyy-MM-dd HH:mm:ss"))!}</span>
					</div>
					<div class="floatL">
						<label>操作人:</label>
						<span class="iname">${leads.userCreate.name}</span>
					</div>
				</li>
			</ul>
	</div>
</#macro>
<#macro script>
</#macro>