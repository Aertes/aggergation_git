<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#include "/layouts/main.ftl"/>
<@html></@html>
<#macro style></#macro>
<#macro script></#macro>
<#macro content>
		<div class="clearfix">
		<div class="dewidth">
			<h2>导入信息</h2>
			<ul class="formlist detailadd leadetail">
			<li class="clearfix">
			<div class="floatL">
				<label>商机ID</label>
				<span  class="iname">${leads.a}</span>
				</div>
				<div class="floatL">
				<label>线索ID</label>
				<span  class="iname">${leads.b}</span>
				</div>
			</li>
			<li class="clearfix">
			<div class="floatL">
				<label>客户编号</label>
				<span  class="iname">${leads.c}</span>
				</div>
				<div class="floatL">
				<label>客户名称</label>
				<span title="姓名" class="iname">${leads.d}</span>
				</div>
			</li>
			<li class="clearfix">
				<div class="floatL">
				<label>手机号码</label>
				<span class="iname">${leads.e}</span>
				</div>
				<div class="floatL">
				<label>首次任务接收时间</label>
				<span  class="iname">${leads.f}</span>
				</div>
			</li>
			<li class="clearfix">
			<div class="floatL">
				<label>首次任务完成时间</label>
				<span  class="iname">${leads.g}</span>
				</div>
				<div class="floatL">
				<label>大区</label>
				<span  class="iname">${leads.h}</span>
				</div>
			</li>
			<li class="clearfix">
			<div class="floatL">
				<label>网点编码</label>
				<span  class="iname">${leads.i}</span>
				</div>
				<div class="floatL">
				<label>网点名称</label>
				<span  class="iname">${leads.j}</span>
				</div>
			</li>
			<li class="clearfix">
			<div class="floatL">
				<label>电销标记</label>
				<span  class="iname">${leads.k}</span>
				</div>
				<div class="floatL">
				<label>一级渠道来源</label>
				<span  class="iname">${leads.l}</span>
				</div>
			</li>
			<li class="clearfix">
			<div class="floatL">
				<label>营销活动名称</label>
				<span  class="iname">${leads.m}</span>
				</div>
				<div class="floatL">
				<label>意向子车系</label>
				<span  class="iname">${leads.n}</span>
				</div>
			</li>
			<li class="clearfix">
			<div class="floatL">
				<label>互动类型</label>
				<span  class="iname">${leads.o}</span>
				</div>
				<div class="floatL">
				<label>是否有订单</label>
				<span  class="iname">${leads.p}</span>
				</div>
			</li>
			<li class="clearfix">
			<div class="floatL">
				<label>申报台数</label>
				<span  class="iname">${leads.q}</span>
				</div>
				<div class="floatL">
				<label>原意向级别</label>
				<span  class="iname">${leads.r}</span>
				</div>
			</li>
			<li class="clearfix">
			<div class="floatL">
				<label>现意向级别</label>
				<span  class="iname">${leads.s}</span>
				</div>
				<div class="floatL">
				<label>任务执行人</label>
				<span  class="iname">${leads.t}</span>
				</div>
			</li>
			<li class="clearfix">
			<div class="floatL">
				<label>战败原因</label>
				<span  class="iname">${leads.u}</span>
				</div>
				<div class="floatL">
				<label>失控原因</label>
				<span  class="iname">${leads.v}</span>
				</div>
			</li>
			<li class="clearfix">
			<div class="floatL">
				<label>访谈内容</label>
				<span  class="iname">${leads.w}</span>
				</div>
				
			</li>
			<li class="clearfix">
			<div class="floatL">
			<label>是否有申报</label>
				<span  class="iname">${leads.x}</span>
				</div>
				<div class="floatL">
				<label>总部下发or网点创建时间</label>
				<span  class="iname">${leads.y}</span>
				</div>
			</li>
			</div>
		<div class="dewidth">
			<h2>操作信息</h2>
			<ul class="formlist detailadd leadetail">
			
			<li class="clearfix">
			<div class="floatL">
				<label>导入时间</label>
				<span  class="iname">${(leads.batch?string("yyyy-MM-dd HH:mm:ss"))!}</span>
				</div>
				<div class="floatL">
				<label>操作人</label>
				<span  class="iname">${leads.userCreate.name}</span>
				</div>
			</li>
			
		</ul>
		</div>
		<div class="btnsumbit btncoloes">
			<a href="${rc.contextPath}/crm/leads/index" class="easyui-linkbutton backbtn">返 回</a>
		</div>
	</div>
</#macro>