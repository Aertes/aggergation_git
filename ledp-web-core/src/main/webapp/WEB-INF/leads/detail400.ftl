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
				</li>
				<li class="clearfix">
					<div class="floatL">
						<label>客户名称 <em>*</em></label>
						<a href="${rc.contextPath}/customer/detailByPhone/${leads.phone}" title="" class="iname">${leads.name}</a>
					</div>
					<div class="floatL">
						<label>客户性别 <em>*</em></label>
						<span class="iname">
							<#if leads.userGender=="0">女
							<#elseif leads.userGender=="1">男
							<#else>未知</#if>
						</span>
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
						<span class="iname">${leads.provinceName}</span>
					</div>
				</li>
			</ul>
		</div>
		<div class="dewidth">
			<h2>意向信息</h2>
			<ul class="formlist detailadd leadetail">
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
						<label>来电时间<em>*</em></label>
						<span class="iname">${leads.createTime}</span>
					</div>
					<div class="floatL">
						<label>用户按键<em>*</em></label>
						<span class="iname">${leads.userKey}</span>
					</div>
				</li>
				<li class="clearfix">
					<div class="floatL">
						<label>分配时间<em>*</em></label>
						<span class="iname"><#if leads.handleTime?exists && leads.handleTime!='null'>${leads.handleTime}</#if></span>
					</div>
					<div class="floatL">
						<label>通话时长<em>*</em></label>
						<span class="iname"><#if leads.keepTime?exists>${leads.keepTime}&nbsp;秒</#if></span>
					</div>
				</li>
				<li class="clearfix">
					<div class="floatL">
						<label>呼叫失败原因<em>*</em></label>
						<span class="iname">${leads.content}</span>
					</div>
					<div class="floatL">
						<label>商家转接电话号码<em>*</em></label>
						<span class="iname">${leads.calledNum}</span>
					</div>
				</li>
				<li class="clearfix">
					<div class="floatL">
						<label>线索同步时间<em>*</em></label>
						<span class="iname"><#if leads.dateCreate?exists>${leads.dateCreate?string("yyyy-MM-dd HH:mm:ss")}</#if></span>
					</div>
				</li>
			</ul>
			<div class="btnsumbit btncoloes">
				<a href="${rc.contextPath}/leads/index" class="easyui-linkbutton backbtn">返 回</a>
			</div>
		</div>
	</div>
</#macro>
