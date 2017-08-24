<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#assign tag=JspTaglibs["/WEB-INF/taglib/permission.tld"]>
<#include "/wechat/layouts/main.ftl"/>
<@html></@html>
<#macro style>
<link rel="stylesheet" type="text/css" href="${rc.contextPath}/css/jquery.bigautocomplete.css">
<style>
	.form-search > div >span, .form-search >div>input{
		margin:0 10px;
	}
	.weizhanContianer .weizhanIntruduce{
		width:98%;
		padding:1%;
	}
	.tableId li{
		margin:10px 0;
	}
	.weizhanRon{
		padding:0;
	}
	.weizhanContianer .weizhanIntruduce .content{
		margin-left:1%;
	}
	.weizhanContianer .weizhanIntruduce .content span{
		line-height:25px;
	}
	.searchForm label{
		font-size:12px;
	}
</style>
</#macro>
<#macro content>
<div class="breadcrumbs" id="breadcrumbs">
	<script type="text/javascript">
		try{ace.settings.check('breadcrumbs' , 'fixed')}catch(e){}
	</script>

	<ul class="breadcrumb">
		<li>
			<i class="icon-home home-icon"></i>
			<span>营销应用</span>
			<span>&gt;&gt;</span>
			<span>微站管理</span>
		</li>
	</ul>
</div>
<div class="tabContainer tabChange">
	<div class="tabContianer_con" style="overflow: auto;">
		<div id="page1" style="display: block;min-width: 900px;">
			<div class="weizhanContianer" style="width:95%;">
				<div class="searchForm" style="width:100%;">
					<form action="${rc.contextPath}/wechat/site/search" id="formId" class="form-search">
						<div class="floatL">
							<label>网点名称：</label>
							<#if dealer?exists>
							<span style="padding:0 10px;height:30px;display:inline-block; line-height:30px;color: #939192;background: #f5f5f5!important;border:1px solid #d5d5d5;cursor: default;">${dealer.name}</span>
							<#else>
							<input type="text" id="auto_tt1" relevanceHiddenId="dealer" value="" autocomplete="off" style="width:150px;"/>
							<input type="hidden" id="dealer" name="dealer" value="" class="search"/>
							</#if>
						</div>
						<div class="floatL">
							<label class="floatL">微站名称：</label>
							<input type="text" name="name" class="search" style="width:150px;">
						</div>
						<#--<div class="floatL" style="width:420px;">
							<label class="floatL">创建时间：</label>
							<div id="startTime" style="width: 150px;" class="floatL search" params="{name:'startTime',value:''}"></div>
							<label class="floatL margin10">-</label>
							<div id="endTime" style="width: 150px;" class="floatL search" params="{name:'endTime',value:''}"></div>
						</div>-->
						<div class="floatL">
							<input style="vertical-align:top;" type="button" class="redButton submit" value="搜 索 ">
						</div>
						<div class="floatR">
							<#if permissions?seq_contains('wechat/campaign/create')>
								<#if dealer?exists>
									<a href="${rc.contextPath}/wechat/site/create" title="" class="redButton" style="color: #fff;vertical-align:top;display:inline-block;">新 建</a>
								</#if>
							</#if>
						</div>
					</div>
				</form>
				<div id="tableId" class="tableId">
					<ul>
					</ul>
					<div class="padding30" style="margin:20px 0 0;">
						<div class="pagegination floatR">
							<ul>
							</ul>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
</#macro>
<#macro script>
	<script>
		$(function(){
            /* 解决IE浏览器Get请求是用缓存问题 */
            $.ajaxSetup({cache:false});

			dateTimeInterface.init("dateTime","startTime");
			dateTimeInterface.init("dateTime","endTime");
			var page1 = new pagination("formId" ,"tableId",function(value){
				$("#tableId>ul").children().remove();
				if(value){
					for(var i=0;i<value.length;i++){
						var $li = $("<li>");
						var $div1=$("<div class='weizhanRon'>");
						var $div2=$("<div class='weizhanIntruduce'>");
						var $img = $("<img class='floatL' src='"+value[i]["img"]+"'/>");
						var $div3=$("<div class='floatL content' style='width:50%;word-wrap:break-word;'>");
						var str="<span>"+value[i]["name"]+"</span><span>微站描述："+value[i]["comment"]+"</span>";
						if(value[i]["model"]){
							str += "<span>微站使用模版："+value[i]["model"]+"</span>";
						}else{
							str += "<span>微站使用模版：无</span>";
						}
						if(value[i]["url"]){
							str += "<span>微站URL："+value[i]["url"]+"</span>";
						}
						var $span=$(str)
						$div3.append($span);
						
						var $div4=$("<div class='floatR site_list'>");
						if(value[i]["edit"]){
							$div4.append($("<a class='floatL marginLeft10' style='color:#d12d32;' href='"+value[i]["edit"]+"'>编 辑</a>"))
						}
						if(value[i]["preview"]){
							$div4.append($(value[i]["preview"]))
						}
						if(value[i]["app"]){
							$div4.append($(value[i]["app"]))
						}
						if(value[i]["delete"]){
							$div4.append($(value[i]["delete"]))
						}
						if(value[i]["manage"]){
							$div4.append($(value[i]["manage"]))
						}
						/*var $a_page = "";
						if(value[i]["pageHref"]){
							$a_page = $("<a href='"+value[i]["pageHref"]+"'>界面管理</a>");
							$div4.append($a_page);
						}*/
						$div2.append($img).append($div3).append($div4);
						$div1.append($div2);
						$($li).append($div1);
						$("#tableId>ul").append($li);
					}
				}
			});
			
			$("#auto_tt1").bigAutocomplete({
		    	width:270,
		    	url : "${rc.contextPath}/report/area/autocomplete",
		    	callback:function(data){
		    		$("#dealer").val(data.id);
				}
			});
			
			var layerConfirm = new ElasticLayer("confirm","确认要删除吗？",{});
			var layerConfirm1 = new ElasticLayer("confirm","确认要应用吗？",{});
			$(".site_list").delegate(".siteDelete","click",function(){
				var id=$(this).attr('attr-id');
				successCallBack(id,page1,layerConfirm,layerConfirm1);
			});
			
			
			$(".site_list").delegate(".siteApp","click",function(){
				var id=$(this).attr('attr-id');
				successCallBackUpdate(id,page1,layerConfirm1,layerConfirm);
			});
			$(".site_list").undelegate(".siteDaile","click");
			$(".site_list").delegate(".siteDaile","click",function(){
				var id=$(this).attr('attr-id');
				successCallBackDaile(id);
			});
		});
		
		function successCallBack (id,page1,layerConfirm,layerConfirm1){
				layer.confirm("确认要删除吗？", {
					yes : function(index){
						layer.close(index);
						$.ajax({
							url:"${rc.contextPath}/wechat/site/delete/"+id,
							data:{},
							type:'post',
							cache:false,
							dataType : 'json', 
							success:function(data) {
								page1.getPageValue();
								$(".site_list").delegate(".siteDelete","click",function(){
									var id=$(this).attr('attr-id');
									successCallBack(id,page1,layerConfirm,layerConfirm1);
								});
								$(".site_list").delegate(".siteApp","click",function(){
									var id=$(this).attr('attr-id');
									successCallBackUpdate(id,page1,layerConfirm1,layerConfirm);
								});
								$(".site_list").undelegate(".siteDaile","click");
								$(".site_list").delegate(".siteDaile","click",function(){
									var id=$(this).attr('attr-id');
									successCallBackDaile(id);
								});
								var layerAlert = new ElasticLayer("alert",data.value,{title : "信息提示框",dialog : true,time : 2000});
								layerAlert.init();
							}
						});
						
					},
					no : function(index){
						layer.close(index);
					}
				});
				
		}
		function successCallBackUpdate (id,page1,layerConfirm1,layerConfirm){
				layer.confirm("确认要应用吗？",{
					yes : function(index){
						layer.close(index);
						$.ajax({
							url:"${rc.contextPath}/wechat/site/updateIspublic/"+id,
							data:{},
							type:'post',
							cache:false,
							dataType : 'json', 
							success:function(data) {
								if(data.code=='200'){
									page1.getPageValue();
								}
								$(".site_list").delegate(".siteApp","click",function(){
									var id=$(this).attr('attr-id');
									successCallBackUpdate(id,page1,layerConfirm1,layerConfirm);
								});
								$(".site_list").delegate(".siteDelete","click",function(){
									var id=$(this).attr('attr-id');
									successCallBack(id,page1,layerConfirm,layerConfirm1);
								});
								$(".site_list").undelegate(".siteDaile","click");
								$(".site_list").delegate(".siteDaile","click",function(){
									var id=$(this).attr('attr-id');
									successCallBackDaile(id);
								});
								var layerAlert = new ElasticLayer("alert",data.value,{title : "信息提示框",dialog : true,time : 2000});
								layerAlert.init();
							}
						});
						
					},
					no : function(index){
						layer.close(index);
					}
				});
				
		}
		function successCallBackDaile(id){
			$.ajax({
				url:"${rc.contextPath}/wechat/site/selectSitePage/"+id,
				data:{},
				type:'post',
				cache:false,
				dataType : 'json', 
				success:function(data) {
					if(data.code=='200'){
						var layerPage = new ElasticLayer("pageWindow","${rc.contextPath}/wechat/site/getUrl/"+id,{area:["500px","80%"],offset:["5%","30%"]},{
							success : function(layero ,index){
								
							}
						})
						layerPage.init();
					}else{
						var layerAlert = new ElasticLayer("alert",data.value,{title : "信息提示框",dialog : true,time : 2000});
						layerAlert.init();
					}
					$(".site_list").undelegate(".siteDaile","click");
					$(".site_list").delegate(".siteDaile","click",function(){
						var id=$(this).attr('attr-id');
						successCallBackDaile(id)
					});
				}
			});
		
		}
	</script>
	<script type="text/javascript" src="${rc.contextPath}/js/report/jquery.bigautocomplete.js"></script>
</#macro>