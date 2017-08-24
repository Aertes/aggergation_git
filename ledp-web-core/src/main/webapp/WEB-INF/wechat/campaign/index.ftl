<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#assign tag=JspTaglibs["/WEB-INF/taglib/permission.tld"]>
<#include "/wechat/layouts/main.ftl"/>
<@html></@html>
<#macro style>
<link rel="stylesheet" type="text/css" href="${rc.contextPath}/css/jquery.bigautocomplete.css">
<style>
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
			<span>活动管理</span>
		</li>
	</ul>
</div>
<form action="${rc.contextPath}/wechat/campaign/search" id="formId" class="form-search">
	<div class="searchForm">
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
			<label>活动名称：</label>
			<input type="text" name="name" class="search" style="width:150px;"/>
			<intput type="hidden" id="unbegin" name="unbegin" class="search"></input>
			<intput type="hidden" id="in" name="in" class="search"></input>
			<intput type="hidden" id="end" name="end" class="search"></input>
		</div>
		<div class="floatL">
			<label class="floatL">开始时间：</label>
			<div id="startTime" style="width: 150px;" class="floatL search" params="{name:'startTime',value:''}"></div>
			<label class="floatL margin10">-</label>
			<div id="endTime" style="width: 150px;" class="floatL search" params="{name:'endTime',value:''}"></div>
		</div>
		<div class="floatL">
			<input type="button" value="搜 索 " class="redButton submit" />
		</div>
	</div>
	<div class="width90 activeManage" style="width:96%;">
		<#if permissions?seq_contains('wechat/campaign/create')>
			<#if loginDealer?exists>
				<a href="${rc.contextPath}/wechat/campaign/create" class="redButton floatL">新建活动</a>
			</#if>
			
		</#if>
		<p class="floatR">
			<input type="checkbox" name="activ_checkbox" attr_id="unbegin"/>
			<span>未开始</span>
			<input type="checkbox" name="activ_checkbox" attr_id="in"/>
			<span>进行中</span>
			<input type="checkbox" name="activ_checkbox" attr_id="end"/>
			<span>已结束</span>
		</p>
	</div>
</form>
<div class="activeLie" id="tableId">
	<ul>
	</ul>
	<div class="padding30">
		<div class="pagegination floatR">
			<ul>
			</ul>
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
						var $div1 = $("<div class='floatL width13'><img src='"+value[i]["img"]+"' /></div>");
						var $div2 = $("<div class='floatL width30'><p>"+value[i]["name"]+"</p><p>"+value[i]["content"]+"</p></div>");
						var $time = $("<div class='floatL'>"+value[i]["time"]+"</div>");
						var $container = $("<div class='floatL active_ope'>");
						var $a_look = $("<a href='"+value[i]["lookHref"]+"'>查看</a>");
						var $a_edit = "";
						if(value[i]["editHref"]){
							if(value[i]["state"]!="已结束" && value[i]["state"]!="进行中"){
								$a_edit=$("<a href='"+value[i]["editHref"]+"'>编辑</a>");
							}
						}
						var $a_delete = "";
						if(value[i]["deleteHref"]){
							if(value[i]["state"]!="已结束" && value[i]["state"]!="进行中"){
								$a_delete = $(value[i]["deleteHref"]);
							}
						}
						var $a_page = "";
						if(value[i]["pageHref"]){
							$a_page = $("<a href='"+value[i]["pageHref"]+"'>界面管理</a>");
						}
						var $statediv = $("<div class='activeCenter'></div>");
						if(value[i]["state"]=="未开始"){
							$statediv.append($("<img style='width: 90px;' src='${rc.contextPath}/wechat/img/state3.png'>"))
						}else if(value[i]["state"]=="进行中"){
							$statediv.append($("<img style='width: 90px;' src='${rc.contextPath}/wechat/img/state1.png'>"))
						}else if(value[i]["state"]=="已结束"){
							$statediv.append($("<img style='width: 90px;' src='${rc.contextPath}/wechat/img/state2.png'>"))
						}
						$($li).append($div1).append($div2).append($time).append($($container).append($a_look).append($a_edit).append($a_delete).append($a_page).append($statediv));
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
			
			$("[name='activ_checkbox']").click(function(){
				$('#unbegin').val('');
				$('#in').val('');
				$('#end').val('');
				$("input[name='activ_checkbox']").each(function(){
					var $this=$(this);
					if($this.is(':checked')){
						$('#'+$this.attr('attr_id')).val('123');
					}
				});
				page1.getPageValue();
			});
			
			var layerConfirm = new ElasticLayer("confirm","确认要删除吗？",{})
			$(".active_ope").delegate(".activeDelete","click",function(){
				var id=$(this).attr('attr-id');
				successCallBack(id,page1,layerConfirm);
			});
		});
		
		function successCallBack (id,page1,layerConfirm){
				layer.confirm("确认要删除吗？",{
					yes : function(index){
						layer.close(index);
						$.ajax({
							url:"${rc.contextPath}/wechat/campaign/delete/"+id,
							data:{},
							type:'post',
							cache:false,
							dataType : 'json', 
							success:function(data) {
								page1.getPageValue();
								$(".active_ope").delegate(".activeDelete","click",function(){
									var id=$(this).attr('attr-id');
									successCallBack(id,page1,layerConfirm);
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
		
	</script>
	<script type="text/javascript" src="${rc.contextPath}/js/report/jquery.bigautocomplete.js"></script>
</#macro>