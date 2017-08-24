<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#include "/layouts/main.ftl"/>
<@html></@html>
<#macro style></#macro>
<#macro script></#macro>
<#macro content>
	<div class="tit clearfix" style="margin:0; padding:0;">
		<h2>基本信息</h2>
	</div>
	<div class="dewidth">
		<ul class="formlist">
			<li class="clearfix">
				<label>客户名称</label>
				<a href="#" title="姓名" class="iname">${customer.name}</a>
			</li>
			<li class="clearfix">
				<label>电话号码</label>
				<span class="iname">${customer.phone}</span>
			</li>
			<li class="clearfix">
				<label>客户邮件</label>
				<span class="iname">${customer.email}</span>
			</li>
			<li class="clearfix">
				<label>省份</label>
				<span class="iname">${customer.province}</span>
			</li>
			<li class="clearfix">
				<label>城市</label>
				<span class="iname">${customer.city}</span>
			</li>
			<li class="clearfix">
				<label>区县</label>
				<span class="iname">${customer.district}</span>
			</li>
			<li class="clearfix">
				<label>详细地址</label>
				<span class="iname">${customer.address}</span>
			</li>
		</ul>
		<div class="easyui-tabs">
			<div title="销售线索列表" class="listsale">
				<div class="ichtable mt20">
					<form action="${rc.contextPath}/leads/search?phone=${customer.phone?default('-1')}" method="POST" id="customer_leads_form">
					</form>
					<table id="customer_leads_table" width="100%" class="easyui-datagrid" title="&nbsp;" data-options="singleSelect:true,collapsible:false,method:'get',remoteSort:false,multiSort:true,pagination:true,showRefresh:false" >
						<thead>
							<tr>
								<th width="20%" data-options="field:'ledpDealer.name',sortable:true">网点名称</th>
								<th width="15%" data-options="field:'ledpType.name',sortable:true">线索类型</th>
								<th width="15%" data-options="field:'ledpMedia.name',sortable:true">媒体渠道</th>
								<th width="15%" data-options="field:'ledpIntent.name',sortable:true">意向级别</th>
								
								<th width="10%" data-options="field:'ledpFollow.name',sortable:true">跟进状态</th>
								<th width="15%" data-options="field:'createTime',sortable:true">留资时间 </th>
								<th width="10%" data-options="field:'operations',sortable:true">操作 </th>
							</tr>
						</thead>
					</table>
				</div>
			</div>
		</div>
		<div class="btnsumbit btncoloes">
			<@check permissionCodes = permissions permissionCode="customer/update">
				<a href="${rc.contextPath}/customer/update/${customer.id}" class="easyui-linkbutton btnstyle">编 辑</a>
			</@check>
			<a href="${rc.contextPath}/customer/index" class="easyui-linkbutton backbtn">返 回</a>
		</div>
	</div>
	<script type="text/javascript">
		$(function(){
			$.plugin.init("#customer_leads_form","#customer_leads_table");			
		});
	</script>
</#macro>