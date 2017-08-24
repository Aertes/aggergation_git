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
						<label>线索类型 <em>*</em></label>
						<span class="iname">${leads.ledpType.name}</span>
					</div>
					<div class="floatL">
						<label>媒体渠道<em>*</em></label>
						<a href="${rc.contextPath}/media/detail/${leads.ledpMedia.id}" title="" class="iname">${leads.ledpMedia.name}</a>
					</div>
				</li>
				<li class="clearfix">
					<div class="floatL">
						<label>网点名称 <em>*</em></label>
						<a href="${rc.contextPath}/dealer/detail/${leads.ledpDealer.id}" title="" class="iname">${leads.ledpDealer.name}</a>
					</div>
					<div class="floatL">
						<label>客户名称 <em>*</em></label>
						<a href="${rc.contextPath}/customer/detailByPhone/${leads.phone}" title="" class="iname">${leads.name}</a>
					</div>
				</li>
				<li class="clearfix">
					<div class="floatL">
						<label>客户性别 <em>*</em></label>
						<span class="iname">
							<#if leads.userGender=="0">女
							<#elseif leads.userGender=="1">男
							<#else>未知</#if>
						</span>
					</div>
                    <div class="floatL">
                        <label>备注 <em>*</em></label>
                        <span class="iname">${leads.remark}</span>
                    </div>
				</li>
				<li class="clearfix">
					<div class="floatL">
						<label>手机号码 <em>*</em></label>
						<span class="iname">${leads.phone}</span>
					</div>
					<div class="floatL">
						<label>客户邮件 <em>*</em></label>
						<span class="iname">${leads.userMail}</span>
					</div>
				</li>
				<li class="clearfix">
					<div class="floatL">
						<label>省份 <em>*</em></label>
						<span class="iname">${leads.provinceName}</span>
					</div>
					<div class="floatL">
						<label>城市<em>*</em></label>
						<span class="iname">${leads.cityName}</span>
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
						<span class="iname">${leads.ledpVehicle.name}</span>
					</div>
					<div class="floatL">
						<label>车系名称<em>*</em></label>
						<span class="iname">${leads.ledpSeries.name}</span>
					</div>
				</li>
				<li class="clearfix">
					<div class="floatL">
						<label>线索编号 <em>*</em></label>
						<span class="iname">${leads.clueId}</span>
					</div>
					<div class="floatL">
						<label>网点编号 <em>*</em></label>
						<span class="iname">${leads.ledpDealer.code}</span>
					</div>
				</li>
				<li class="clearfix">
					<div class="floatL">
						<label>线索内容 <em>*</em></label>
						<span class="iname">${leads.content}</span>
					</div>
					<div class="floatL">
						<label>订购价格 <em>*</em></label>
						<span class="iname">${leads.carPrice}</span>
					</div>
				</li>
				<li class="clearfix">
					<div class="floatL">
						<label>线索状态 <em>*</em></label>
						<span class="iname">${leads.ledpFollow.name}</span>
					</div>
					<div class="floatL">
						<label>意向级别 <em>*</em></label>
						<span class="iname">${leads.ledpIntent.name}</span>
					</div>
				</li>
				<li class="clearfix">
					<div class="floatL">
						<label>期望优惠金额 <em>*</em></label>
						<span class="iname">${leads.buyPriceOff}</span>
					</div>
					<div class="floatL">
						<label>处理网点编号 <em>*</em></label>
						<span class="iname">${leads.ledpHanldeDealer.code}</span>
					</div>
				</li>
				<li class="clearfix">
					<div class="floatL">
						<label>线索处理时间<em>*</em></label>
						<span class="iname"><#if leads.handleTime?exists && leads.handleTime!='null'>${leads.handleTime}</#if></span>
					</div>
					<div class="floatL">
						<label>是否为置换订单<em>*</em></label>
						<span class="iname">
							<#if leads.orderType == '1'>
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
						<span class="iname">${leads.ledpVehicle.id}</span>
					</div>
					<div class="floatL">
						<label>车系ID<em>*</em></label>
						<span class="iname">${leads.ledpSeries.id}</span>
					</div>
				</li>
				<li class="clearfix">
					<div class="floatL">
						<label>线索留资时间<em>*</em></label>
						<span class="iname">${leads.createTime}</span>
					</div>
					<div class="floatL">
						<label>线索同步时间<em>*</em></label>
						<span class="iname"><#if leads.dateCreate?exists>${leads.dateCreate?string("yyyy-MM-dd HH:mm:ss")}</#if></span>
					</div>
				</li>
				<li class="clearfix">
					<div class="floatL">
						<label>预计提车时间 <em>*</em></label>
						<span class="iname">${leads.buyTime}</span>
					</div>
				</li>
			</ul>
		</div>
	</div>
	<div class="btnsumbit">
		<a href="${rc.contextPath}/leads/index" class="easyui-linkbutton backbtn">返 回</a>
	</div>
</div>
</#macro>
