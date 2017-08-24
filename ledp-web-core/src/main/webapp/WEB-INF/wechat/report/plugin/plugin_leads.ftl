<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#setting classic_compatible=true>
	<#assign titles = [] />
	<#assign fields = [] />

	<#if plugin.code=="rotatyTable">
		<#assign titles = ["大区","网点名称","插件应用来源","活动名称","参与人名称","手机号码","参与时间","中奖信息"]/>
		<#assign fields = ["orgName","dealerName","source","sourceName","name","phone","time","award"] />
	<#elseif plugin.code=="purchase">
		<#assign titles = ["大区","网点名称","插件应用来源","活动名称","参与人名称","手机号码","团购时间","团购金额"]/>
		<#assign fields = ["orgName","dealerName","source","sourceName","name","phone","time","money"] />
	<#elseif plugin.code=="coupon">
		<#assign titles = ["大区","网点名称","插件应用来源","活动名称","参与人名称","手机号码","下载时间","优惠券面值","优惠券内容"]/>
		<#assign fields = ["orgName","dealerName","source","sourceName","name","phone","time","couponPar","couponContent"] />
	<#elseif plugin.code=="share">
		<#assign titles = ["大区","网点名称","插件应用来源","活动名称","参与人名称","手机号码","分享时间","奖品信息"]/>
		<#assign fields = ["orgName","dealerName","source","sourceName","name","phone","time","award"] />
	<#elseif plugin.code=="answer">
		<#assign titles = ["大区","网点名称","插件应用来源","活动名称","参与人名称","手机号码","参与时间","中奖信息"]/>
		<#assign fields = ["orgName","dealerName","source","sourceName","name","phone","time","award"] />
	<#else>
		<#assign titles = ["大区","网点名称","留资来源","活动名称","插件名称","客户姓名","手机号码","留资时间"]/>
		<#assign fields = ["orgName","dealerName","source","sourceName","pluginName","name","phone","time"] />
	</#if>
	<div id="leadsDiv">
		<form id="leadsDetailForm">
			<input type="hidden" value="5" name="effect" />
        	<input type="hidden" class="search" value="${form.pluginId}" name="pluginId" />
        	<input type="hidden" class="search" value="${form.pageId?if_exists}" name="pageId" />
        	<input type="hidden" class="search" value="${form.orgId?if_exists}" name="orgId" />
        	<input type="hidden" class="search" value="${form.dealerId?if_exists}" name="dealerId" />
        	<input type="hidden" id="_customName" name="customName" />
        	<input type="hidden" id="_phone" name="phone"/>
		</form>
		<div class="title" style=" width: 100%;height: 40px;line-height: 40px;padding-left: 20px;">
			 <div class="floatL" >
	            <label>客户名称：</label>
	            <input type="text" id="customName" value="${form.customName?if_exists}" class="search" style="width: 183px;"/>
	        </div>
			<div class="floatL" style="margin-left:5px;">
	            <label>手机号码：</label>
	            <input type="text" id="phone" class="search" value="${form.phone?if_exists}" style="width: 183px;"/>
	        </div>
	        <div class="floatL" style="margin:5px;">
				<input type="button" value="搜 索 " class="redButton submit" style="float:left;display:block;font-size:12px;" />
			</div>
		</div>
   		<div style="width:100%;margin:10px 10px;">
   			<a href="javascript:void(0)" onclick="exportLeads();" class="redButton" style="color: #fff;">导 出</a>
   		</div>
		<table id="leadsTable" class="reportData shareList" style="width:98%;border:1px solid #d9d9d9;margin:0 10px;">
			<thead>
				<tr>
					<#list titles as title><th style="width:10%;">${title}</th></#list>
				</tr>
			</thead>
			<tbody>
				<#list leadsList as leads>
				<tr>
					<#list fields as field><td>${leads[field]}</td></#list>
				</tr>
				</#list>
			</tbody>
		</table>
		<div class="padding30 marginTop20">
			<div class="pagegination floatR">
				<ul id="subpage">
				<li class="page_head <#if form.currentPage == 1>gray</#if>" onClick="loadPage(1);"><a href="#" class="gray">&lt;&lt;</a></li>
				<#list pagination.pages as page>
					<#if page==form.currentPage>
						<li page="${page}" onClick="loadPage(${page});" class="active"><a href="#">${page}</a></li>
					<#else>
						<li page="${page}" onClick="loadPage(${page});"><a href="#">${page}</a></li>
					</#if>
				</#list>
				<li class="page_foot <#if pagination.currentPage gte pagination.total>gray</#if>" onClick="loadPage(${pagination.total});"><a href="#">&gt;&gt;</a></li></ul>
			</div>
		</div>
	</div>
	<script type="text/javascript">
		function loadPage(currentPage,flag){
			if(!flag && (currentPage == undefined || currentPage <= 0 || currentPage > ${pagination.total} || ${pagination.total} <= ${pagination.currentPage})){
				return false;
			}
			var pluginid = '${form.pluginId}';
			var pageid = '${form.pageId}';
			var dealerId = '${form.dealerId}';
			var customName = $("#customName").val()
			var phone = $("#phone").val()
			if(dealerId == null || dealerId == undefined){
				dealerId = '';
			}
			if(customName == null || customName == undefined){
				customName = '';
			}
			if(phone == null || phone == undefined){
				phone = '';
			}
			
			var params = $('#formId').serialize() + "&customName="+customName+"&phone="+phone;
			if(pageid != '' && pageid != undefined){
				params = params+"&pageId="+pageid;
			}
			if(pluginid != '' && pluginid != undefined){
				params = params.replace("pluginId=","pluginId="+pluginid+"&r=");
			}
			if(dealerId != '' && dealerId != undefined){
				params = params.replace("dealerId=","dealerId="+dealerId+"&r=");
			}
			//params = params.replace("pageSize=","pageSize=10&r=");
			params = params.replace("currentPage=","currentPage="+currentPage+"&r=");
			$.ajax({
				cache:false,
				async:true,
				type:'post',
				data:params,
				url:'${rc.contextPath}/wechat/report/plugin/leads',
				error:function(request) {
                	alert("服务器正忙...");
                },
                success:function(div) {
                	$(".layui-layer-content").html(div);
                	$("#subpage>li").removeClass("active");
					$("#subpage>li[page='"+currentPage+"']").addClass("active");
				}
			});
		}
		function exportLeads(){
			$("#leadsDetailForm").attr("action","${rc.contextPath}/wechat/report/plugin/exportLeads");
			$("#_phone").val($("#phone").val());
			$("#_customName").val($("#customName").val());
			$("#leadsDetailForm").submit();
		}
		
		$(function(){
			$(".submit").click(function(){
				loadPage(1,true);
			});
		});
		
	</script>
