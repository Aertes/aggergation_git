<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#setting classic_compatible=true>
	<#assign titles = [] />
	<#assign fields = [] />

	<#if plugin.code=="rotatyTable">
		<#assign titles = ["大区","网点名称","插件应用来源","活动名称","参与人数","留资数","中奖人数","中奖奖品"]/>
		<#assign fields = ["orgName","dealerName","source","sourceName","viewer","leads","winner","award"] />
	<#elseif plugin.code=="purchase">
		<#assign titles = ["大区","网点名称","插件应用来源","活动名称","参与人数",'留资人数',"团购标题","团购时间","团购内容"]/>
		<#assign fields = ["orgName","dealerName","source","sourceName","viewer","leads","title","time","content"] />
	<#elseif plugin.code=="coupon">
		<#assign titles = ["大区","网点名称","插件应用来源","活动名称","参与人数","留资数","优惠券张数","优惠券内容","优惠券面值"]/>
		<#assign fields = ["orgName","dealerName","source","sourceName","viewer","leads","total","content","award"] />
	<#elseif plugin.code=="share">
		<#assign titles = ["大区","网点名称","插件应用来源","活动名称","参与人数","留资数","分享平台","分享内容","分享礼品"]/>
		<#assign fields = ["orgName","dealerName","source","sourceName","viewer","leads","channels","content","award"] />
	<#elseif plugin.code=="answer">
		<#assign titles = ["大区","网点名称","插件应用来源","活动名称","参与人数","留资数","中奖人数","关卡数","中奖奖品"]/>
		<#assign fields = ["orgName","dealerName","source","sourceName","viewer","leads","winner","checkpoint","award"] />
	<#else>
		<#assign titles = ["大区","网点名称","插件名称","使用次数","参与人数","获取留资数"]/>
		<#assign fields = ["orgName","dealerName","name","useCount","viewer","leads"] />
	</#if>

		<div>
			<table class="reportData" width="100%">
				<thead>
					<tr>
						<#list titles as title><th>${title}</th></#list>
					</tr>
				</thead>
				<tbody>
					<#list campaigns as campaign>
					<tr>
						<#list fields as field>
							<#if plugin.code=="rotatyTable" && field=="leads">
								<td><a class="openLeads" <#if dealer?exists>pageid="${campaign['pageId']}"</#if> dealer="${campaign['dealer']}" style="cursor:pointer;color:red;">${campaign[field]}</a></td>
							<#elseif plugin.code=="purchase" && field=="leads">
								<td><a class="openLeads" <#if dealer?exists>pageid="${campaign['pageId']}"</#if> dealer="${campaign['dealer']}" style="cursor:pointer;color:red;">${campaign[field]}</a></td>
							<#elseif plugin.code=="coupon" && field=="leads">
								<td><a class="openLeads" <#if dealer?exists>pageid="${campaign['pageId']}"</#if> dealer="${campaign['dealer']}" style="cursor:pointer;color:red;">${campaign[field]}</a></td>
							<#elseif plugin.code=="share" && field=="leads">
								<td><a class="openLeads" <#if dealer?exists>pageid="${campaign['pageId']}"</#if> dealer="${campaign['dealer']}" style="cursor:pointer;color:red;">${campaign[field]}</a></td>
							<#elseif plugin.code=="answer" && field=="leads">
								<td><a class="openLeads" <#if dealer?exists>pageid="${campaign['pageId']}"</#if> dealer="${campaign['dealer']}" style="cursor:pointer;color:red;">${campaign[field]}</a></td>
							<#elseif plugin==null && field=="leads">
								<td><a class="openLeads" <#if dealer?exists>pageid="${campaign['pageId']}"</#if> pluginid="${campaign['pluginId']}" dealer="${campaign['dealer']}" style="cursor:pointer;color:red;">${campaign[field]}</a></td>
							<#else>
								<td>${campaign[field]}</td>
							</#if>
						</#list>
					</tr>
					</#list>
				</tbody>
			</table>
			<div class="padding30 marginTop20">
				<div class="pagegination floatR">
					<ul>
					<li class="page_head <#if pagination.currentPage == 1>gray</#if>" onClick="campaignPage(1);"><a href="javascript:void(0);">&lt;&lt;</a></li>
					<#list pagination.pages as page>
						<#if page==pagination.currentPage>
							<li class="active"><a href="javascript:void(0);">${page}</a></li>
						<#else>
							<li onClick="campaignPage(${page});"><a href="javascript:void(0);">${page}</a></li>
						</#if>
					</#list>
					<li class="page_foot <#if pagination.currentPage gte pagination.total>gray</#if>" onClick="campaignPage(${pagination.total});"><a href="javascript:void(0)">&gt;&gt;</a></li></ul>
				</div>
			</div>
		</div>
		<script type="text/javascript">
			function campaignPage(currentPage){
				if((currentPage == undefined || currentPage <= 0 || currentPage > ${pagination.total} || ${pagination.total} >= ${pagination.currentPage})){
					return false;
				}
				$('#currentPage').val(currentPage);
				loadCampigns();
			}
			$(function(){
				$(".openLeads").click(function(){
					var pageid = $(this).attr("pageid");
					var pluginid = $(this).attr("pluginid");
					var dealerId = $(this).attr("dealer");
					var params = $('#formId').serialize();
					if(pageid == null || pageid == undefined){
						pageid = '';
					}else{
						params = params+"&pageId="+pageid;
					}
					if(pluginid != '' && pluginid != undefined){
						params = params.replace("pluginId=","pluginId="+pluginid+"&r=");
					}
					if(dealerId != '' && dealerId != undefined){
						params = params.replace("dealerId=","dealerId="+dealerId+"&r=");
					}
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
		                	layer.open({
			                	title:'留资信息',
    							shadeClose: true,
    							closeBtn: 1,
							    type: 1,
							    area: ['1000px', '600px'], //宽高
							    content: div
							});
						}
					});
					
				});
			});
		</script>