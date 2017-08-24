<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#include "/layouts/main.ftl"/>
<@html></@html>
<#macro style></#macro>
<#macro script></#macro>
<#macro content>
	<div class="clearfix">
		<div class="dewidth">
			<h2>基本信息</h2>
			<ul class="formlist detailadd leadetail">
				<li class="clearfix">
					<div class="floatL">
						<label>媒体渠道<em>*</em></label>
						<a href="${rc.contextPath}/media/detail/${leadsMerged.ledpMedia.id}" title="" class="iname">${leadsMerged.ledpMedia.name}</a>
					</div>
					<div class="floatL">
						<label>网点名称 <em>*</em></label>
						<a href="${rc.contextPath}/dealer/detail/${leadsMerged.ledpDealer.id}" title="" class="iname">${leadsMerged.ledpDealer.name}</a>
					</div>
				</li>
				<li class="clearfix">
					<div class="floatL">
						<label>客户名称 <em>*</em></label>
						<a href="${rc.contextPath}/customer/detailByPhone/${leadsMerged.phone}" title="" class="iname">${leadsMerged.name}</a>
					</div>
					<div class="floatL">
						<label>客户性别 <em>*</em></label>
						<span class="iname">
							<#if leadsMerged.userGender=="0">女
							<#elseif leadsMerged.userGender=="1">男
							<#else>未知</#if>
						</span>
					</div>
				</li>
				<li class="clearfix">
					<div class="floatL">
						<label>手机号码 <em>*</em></label>
						<span class="iname">${leadsMerged.phone}</span>
					</div>
					<div class="floatL">
						<label>客户邮件 <em>*</em></label>
						<span class="iname">${leadsMerged.userMail}</span>
					</div>
				</li>
				<li class="clearfix">
					<div class="floatL">
						<label>省份 <em>*</em></label>
						<span class="iname">${leadsMerged.provinceName}</span>
					</div>
					<div class="floatL">
						<label>城市<em>*</em></label>
						<span class="iname">${leadsMerged.cityName}</span>
					</div>
				</li>
			</ul>
		</div>
		<div class="dewidth">
			<h2>意向信息</h2>
			<ul class="formlist detailadd leadetail">
				<li class="clearfix">
					<div class="floatL">
						<label>车型名称 <em>*</em></label>
						<span class="iname">${leadsMerged.ledpVehicle.name}</span>
					</div>
					<div class="floatL">
						<label>车系名称<em>*</em></label>
						<span class="iname">${leadsMerged.ledpSeries.name}</span>
					</div>
				</li>
				<li class="clearfix">
					<div class="floatL">
						<label>线索编号 <em>*</em></label>
						<span class="iname">${leadsMerged.clueId}</span>
					</div>
					<div class="floatL">
						<label>网点编号 <em>*</em></label>
						<span class="iname">${leadsMerged.ledpDealer.code}</span>
					</div>
				</li>
				<li class="clearfix">
					<div class="floatL">
						<label>订购价格 <em>*</em></label>
						<span class="iname">${leadsMerged.carPrice}</span>
					</div>
					<div class="floatL">
						<label>线索状态 <em>*</em></label>
						<span class="iname">${leadsMerged.ledpFollow.name}</span>
					</div>
				</li>
				<li class="clearfix">
					<div class="floatL">
						<label>预计提车时间 <em>*</em></label>
						<span class="iname">${leadsMerged.buyTime}</span>
					</div>
					<div class="floatL">
						<label>期望优惠金额 <em>*</em></label>
						<span class="iname">${leadsMerged.buyPriceOff}</span>
					</div>
				</li>
				<li class="clearfix">
					<div class="floatL">
						<label>处理网点编号 <em>*</em></label>
						<span class="iname">${leadsMerged.ledpHanldeDealer.code}</span>
					</div>
					<div class="floatL">
						<label>线索处理时间<em>*</em></label>
						<span class="iname"><#if leadsMerged.handleTime!='null'>${leadsMerged.handleTime}</#if></span>
					</div>
				</li>
				<li class="clearfix">
					<div class="floatL">
						<label>是否为置换订单<em>*</em></label>
						<span class="iname">
							<#if leadsMerged.orderType == '1'>
								是
							<#else>
								否
							</#if>
						</span>
					</div>
				</li>
				<li class="clearfix">
					<div class="floatL">
						<label>车型ID<em>*</em></label>
						<span class="iname">${leadsMerged.ledpVehicle.id}</span>
					</div>
					<div class="floatL">
						<label>车系ID<em>*</em></label>
						<span class="iname">${leadsMerged.ledpSeries.id}</span>
					</div>
				</li>
				<li class="clearfix">
					<div class="floatL">
						<label>线索留资时间<em>*</em></label>
						<span class="iname">${leadsMerged.createTime}</span>
					</div>
				</li>
			</ul>
		</div>
		<div class="easyui-tabs">
			<div title="销售线索列表" class="listsale">
				<div class="ichtable mt20">
					<form action="${rc.contextPath}/leads/search?leadsMergedId=${leadsMerged.id}" method="POST" id="leads_form">
					</form>
					<table id="leads_table" width="100%" class="easyui-datagrid" title="&nbsp;" data-options="singleSelect:true,collapsible:false,method:'get',remoteSort:false,multiSort:true,pagination:true,showRefresh:false" >
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
	</div>
	<div class="btnsumbit">
		<a href="${rc.contextPath}/leadsMerged/index" class="easyui-linkbutton backbtn">返 回</a>
	</div>
	<script type="text/javascript">
		$(function(){
			$.plugin.init("#leads_form","#leads_table");			
		});
	</script>
</#macro>
