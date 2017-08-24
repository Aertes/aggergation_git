<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#assign tag=JspTaglibs["/WEB-INF/taglib/permission.tld"]>
<#include "/wechat/layouts/main.ftl"/>
<@html></@html>
<#macro style>
	<style>
		.pic_upload{
			margin:10px 20px;
		}
		.layui-layer-iframe .layui-layer-content iframe{
			width:60%;
			margin:0 auto;
		}
		.layui-layer-title{
			font-family:"微软雅黑"
		}
		.pDD span{
			font-size:14px;
		}
		.searchForm label{
			font-size:12px;
		}
		.ueserline{
			margin:0 10px;
		}
		.ueserline li{
			padding:0;
		}
	</style>
</#macro>
<#macro content>
<div class="breadcrumbs" id="breadcrumbs">
	<script type="text/javascript">
		try {
			ace.settings.check('breadcrumbs', 'fixed')
		} catch (e) {}
	</script>

	<ul class="breadcrumb">
		<li>
			<i class="icon-home home-icon"></i>
			<span>营销应用</span>
			<span>&gt;&gt;</span>
			<span>粉丝管理</span>
		</li>
	</ul>
	<div class="tabContainer marginLeft25 marginTop20 tabChange">
		<div>
			<div>
				<div>
					<form action="${rc.contextPath}/wechat/fans/search" id="formId" method="post" class="form-search">
						<div class="searchForm clearfix" style="width:100%;">
							<div class="floatL">
								<label>粉丝名称：</label>
								<input type="text" name="name" style="width: 150px;" class="search"/>
								<#if fansgroupId?exists>
									<input type="hidden" name="fansGroupId" id="fansGroupId" value="${fansgroupId}" class="search"/>
								<#else>
									<input type="hidden" name="fansGroupId" id="fansGroupId" class="search"/>
								</#if>
							</div>
							<div class="floatL" style="width:420px;">
								<label class="floatL">关注时间：</label>
								<#if startTime?exists>
									<div id="startTime" style="width: 150px;" class="floatL search" params="{name:'startTime',value:'${startTime}'}"></div>
									<label class="floatL margin10">-</label>
									<div id="endTime" style="width: 150px;" class="floatL search" params="{name:'endTime',value:'${endTime}'}"></div>
								<#else>
									<div id="startTime" style="width: 150px;" class="floatL search" params="{name:'startTime',value:''}"></div>
									<label class="floatL margin10">-</label>
									<div id="endTime" style="width: 150px;" class="floatL search" params="{name:'endTime',value:''}"></div>
								</#if>
							</div>
							<div class="floatL">
								<input type="button" value="搜 索 " class="redButton submit" />
							</div>
						</div>
						<div class="clearfix" style="overflow:hidden;"> 
							<div id="pDD" class="floatL pDD">
								<span style='font-size:12px;'>分组名称：</span>
							</div>
							<div class="floatR">
								<a href="javascript:void(0);" title="" onclick="createFansGroup();" class="redButton" style="color: #fff;">新建分组</a>
							</div>
						</div>
						<div class="clearfix pictureSucai" style="width:80%; border-right: 0px;">
							<div class="floatL">
								<div class="pic_upload clearfix">
									<div class="floatL">
										<label class="inline">
											<input type="checkbox" id="checkboxsFans" class="ace checkAll">
											<span class="lbl" style="line-height:30px;font-size:12px;">全选</span>
										</label>
										<select name="fanGrop" id="fanGroup" disabled="false" style="margin-left:10px; color:#eee;">
											<option value="">添加到</option>
											<#list fansGroups as fansGroup>
												<option value="${fansGroup.id}">${fansGroup.name}</option>
											</#list>
										</select>
									</div>
								</div>
								<div id="tableId">
									<ul class="ueserline">
									</ul>
									<div class="padding15">
										<div class="pagegination floatR">
											<ul>
											</ul>
										</div>
									</div>
								</div>
							</div>
						</div>
					</form>
					<form style="position:absolute;width:20%;right:0px;top:150px;border-top: 1px solid #d9d9d9;" action="${rc.contextPath}/wechat/fansGroup/search" id="formfansGroupId" method="post" class="form-search">
						<div id="gourptableId" class="floatL infore" style="width:100%;">
							<ul class="userGroup">
							</ul>
						</div>
					</form>
				</div>
			</div>
		</div>
	</div>
</div>
</#macro>
<#macro script>
<script src="${rc.contextPath}/wechat/assets/js/bootstrap.min.js"></script>
<script>
	var id_list = [];
	var searchFans;
	$(function() {
        /* 解决IE浏览器Get请求是用缓存问题 */
        $.ajaxSetup({cache:false});

		dateTimeInterface.init("dateTime","startTime");
		dateTimeInterface.init("dateTime","endTime");
		searchFans = new pagination("formId", "tableId", function(value) {
			$("#tableId>ul").children().remove();
			for (var i = 0; i < value.length; i++) {
				var $li = $("<li class='clearfix'>");
				var name = value[i]["name"].replace(/\'/g,"&apos;");
				var address = value[i]["address"].replace(/\'/g,"&apos;");
				var signature = value[i]["signature"].replace(/\'/g,"&apos;");
				var fansgroupName = value[i]["fansgroupName"].replace(/\'/g,"&apos;");
				var strhtlm="<table><tr><td colspan=\"2\">"+name+"</td></tr><tr><td style=\"width:50px;\">地区</td><td>"+address+"</td></tr><tr><td>签名</td><td>"+signature+"</td></tr><tr><td>分组</td><td>"+fansgroupName+"</td></tr></table>";
				var $img = $("<p class='imgprit floatL'><img src='" + value[i]["img"] + "' width='50px' height='50px' class='iconHover fffffff' data-container='body' data-toggle='popover' data-placement='right' title='详细资料'  data-content='"+strhtlm+"' /><span class='lbl' id='value"+value[i]["id"]+"' name='"+value[i]["name"]+"'>" + value[i]["value"] + "</span></p>");
				var $p = $("<p class='textAlign floatL' style='margin: 0 10px;'><input type='checkbox' name='checkboxfans' onclick='getFansIds("+value[i]["id"]+");' attr-id='"+value[i]["id"]+"' class='checks' style='border-radius:0;width:15px;height:15px;'></p>");
				var option="";
				for(var j=0;j<value[i]["groups"].length;j++){
					if(value[i]["fansgroupId"]==value[i]["groups"][j].id){
						option+="<option value='"+value[i]["groups"][j].id+"' selected='selected'>"+value[i]["groups"][j].name+"</option>";
					}else{
						option+="<option value='"+value[i]["groups"][j].id+"'>"+value[i]["groups"][j].name+"</option>";
					}
				}
				
				var $operate = $("<div style='overflow:hidden;'><div class='floatR ikoitl'><span>关注时间："+value[i]["subscribe"]+"</span><select id='"+value[i]["id"]+"' group-id='"+value[i]["fansgroupId"]+"' onchange='chioceGroup("+value[i]["id"]+");' >"+option+"</select><a href='javascript:void(0);' onclick='updateRem("+value[i]["id"]+")'>修改备注</a></div></div>");
				$("#tableId>ul").append($li.append($p).append($img).append($operate));
			}
			$(".fffffff").mouseenter(function(){
				$(this).popover('show');
			});
			
			$(".fffffff").mouseleave(function(){
				$(this).popover('hide');
			});
			 $(function () { $(".iconHover").popover({html : true });});
		});
		/*var page1 = new pagination("formfansGroupId", "gourptableId", function(value) {
			$("#gourptableId>ul").children().remove();
			for (var i = 0; i < value.length; i++) {
				var fansGroupId=$('#fansGroupId').val();
				var $li = $("<li>");
				if(fansGroupId){
					if(value[i]["id"]){
						if(fansGroupId==value[i]["id"]){
							$li = $("<li id='li_"+value[i]["id"]+"' name='"+value[i]["name"]+"' class='active' onclick='getFan("+value[i]["id"]+");'>"+value[i]["name"]+"("+value[i]["number"]+")</li>");
						}else{
							$li = $("<li id='li_"+value[i]["id"]+"' name='"+value[i]["name"]+"' onclick='getFan("+value[i]["id"]+");'>"+value[i]["name"]+"("+value[i]["number"]+")</li>");
						}
					}else{
						$li = $("<li id='li_group' name='"+value[i]["name"]+"' onclick='getFans();'>"+value[i]["name"]+"("+value[i]["number"]+")</li>");
					}
				}else{
					if(i==0){
						$li = $("<li id='li_group' name='"+value[i]["name"]+"' onclick='getFans();' class='active'>"+value[i]["name"]+"("+value[i]["number"]+")</li>");
						$('#pDD').children().remove();
						$groupName=$("<span style='font-size:12px;'>分组名称："+value[i]["name"]+"</span>");
						$('#pDD').append($groupName);
					}else{
						$li = $("<li id='li_"+value[i]["id"]+"' name='"+value[i]["name"]+"' onclick='getFan("+value[i]["id"]+");'>"+value[i]["name"]+"("+value[i]["number"]+")</li>");
					}
				}
				
				
				$("#gourptableId>ul").append($li);
			}
		});*/
		searchFansGroup();
		$('#checkboxsFans').click(function(){
			id_list=[];
			if($(this).is(':checked')){
				$("input[name='checkboxfans']").each(function(){
					 this.checked = true;
					 id_list.push($(this).attr("attr-id"));
				});
			}else{
				$("input[name='checkboxfans']").each(function(){
					 this.checked = false;
				});
			}
			if(id_list.length>0){
				$('#fanGroup').attr("disabled",false);
				$('#fanGroup').css('color','#000');
			}else{
				$('#fanGroup').attr("disabled",true);
				$('#fanGroup').css('color','#eee');
			}
		});
		$('#fanGroup').change(function(){
			var id=$(this).val();
			$.ajax({
				url:"${rc.contextPath}/wechat/fans/updateGroup",
				data:{id:id,list:id_list},
				type:'post',
				cache:false,
				dataType : 'json', 
				success:function(data) {
					var layerAlert = new ElasticLayer("alert",data.value,{title : "信息提示框",dialog : true,time : 2000});
					layerAlert.init();
					if(data.code=='200'){
						window.location.href="${rc.contextPath}/wechat/fans/index?fansgroupId="+$('#fansGroupId').val();
					}
				}
			});
		});
	});
	function searchFansGroup(){
		 $.ajax({
	           type: "POST",
	           dataType: "json",
	           url: "${rc.contextPath}/wechat/fansGroup/search",
	           data: {},
	           success: function(data){
	           		var value=data.data;
	           		$("#gourptableId>ul").children().remove();
		           	for (var i = 0; i < value.length; i++) {
						var fansGroupId=$('#fansGroupId').val();
						var $li = $("<li>");
						if(fansGroupId){
							if(value[i]["id"]){
								if(fansGroupId==value[i]["id"]){
									$li = $("<li id='li_"+value[i]["id"]+"' wechatgroupid='"+value[i]["wechatgroupid"]+"' name='"+value[i]["name"]+"' class='active' onclick='getFan("+value[i]["id"]+");'>"+value[i]["name"]+"("+value[i]["number"]+")</li>");
									$('#pDD').children().remove();
									$groupName=$("<span style='font-size:12px;'>分组名称："+value[i]["name"]+"</span>");
									if(value[i]["wechatgroupid"]!='0' && value[i]["name"]!='1' && value[i]["name"]!='2'){
										$updatName=$("<a href='javascript:void(0);' class='redButton' style='margin:0 10px;' onclick='updateNmae("+value[i]["id"]+");'>修改名称</a>");
										$deletegroup=$("<a href='javascript:void(0);' class='redButton' style='color:#fff;' onclick='deletegroup("+value[i]["id"]+");'>删除</a>");
										$('#pDD').append($groupName).append($updatName).append($deletegroup);
									}else{
										$('#pDD').append($groupName)
									}
								}else{
									$li = $("<li id='li_"+value[i]["id"]+"' wechatgroupid='"+value[i]["wechatgroupid"]+"' name='"+value[i]["name"]+"' onclick='getFan("+value[i]["id"]+");'>"+value[i]["name"]+"("+value[i]["number"]+")</li>");
								}
							}else{
								$li = $("<li id='li_group' wechatgroupid='"+value[i]["wechatgroupid"]+"' name='"+value[i]["name"]+"' onclick='getFans();'>"+value[i]["name"]+"("+value[i]["number"]+")</li>");
							}
						}else{
							if(i==0){
								$li = $("<li id='li_group'  name='"+value[i]["name"]+"' onclick='getFans();' class='active'>"+value[i]["name"]+"("+value[i]["number"]+")</li>");
								$('#pDD').children().remove();
								$groupName=$("<span style='font-size:12px;'>分组名称："+value[i]["name"]+"</span>");
								$('#pDD').append($groupName);
							}else{
								$li = $("<li id='li_"+value[i]["id"]+"' wechatgroupid='"+value[i]["wechatgroupid"]+"' name='"+value[i]["name"]+"' onclick='getFan("+value[i]["id"]+");'>"+value[i]["name"]+"("+value[i]["number"]+")</li>");
							}
						}
						
						
						$("#gourptableId>ul").append($li);
					}
	           }
	       });
	}
	function getFans(){
			$('#fansGroupId').val('');
			$("#gourptableId>ul").children().removeClass('active');
			$('#li_group').addClass('active');
			$('#pDD').children().remove();
			$groupName=$("<span style='font-size:12px;'>分组名称："+$('#li_group').attr("name")+"</span>");
			$('#pDD').append($groupName);
			searchFans.getPageValue();
		}
		function getFan(id){
			$('#fansGroupId').val(id);
			$("#gourptableId>ul").children().removeClass('active');
			$('#li_'+id).addClass('active');
			$('#pDD').children().remove();
			$groupName=$("<span style='font-size:12px;'>分组名称："+$('#li_'+id).attr("name")+"</span>");
			if($('#li_'+id).attr("wechatgroupid")!='0' && $('#li_'+id).attr("wechatgroupid")!='1' && $('#li_'+id).attr("wechatgroupid")!='2'){
				$updatName=$("<a href='javascript:void(0);' class='redButton' style='margin:0 10px; color:#fff;' onclick='updateNmae("+id+");'>修改名称</a>");
				$deletegroup=$("<a href='javascript:void(0);' class='redButton' style='color:#fff;' onclick='deletegroup("+id+");'>删除</a>");
				$('#pDD').append($groupName).append($updatName).append($deletegroup);
			}else{
				$('#pDD').append($groupName)
			}
			
			searchFans.getPageValue();
		}
		function createFansGroup(){
			var layerPage = new ElasticLayer("pageWindow",["${rc.contextPath}/wechat/fansGroup/create","no"],{area:["500px","200px"],offset:["10%","38%"],title:"添加分组"},{
				success : function(layero ,index){
					var body = layer.getChildFrame('body', index);
					$(".svaeConfirm_button",$(body)).click(function(){
						var name=$("#fansGroupNmae",$(body)).val();
						if(name && name.length<=6){
				    		 $.ajax({
						           type: "POST",
						           dataType: "json",
						           url: "${rc.contextPath}/wechat/fansGroup/save",
						           data: {
						           		name:name
						           },
						           success: function(data){
						           		var layerAlert = new ElasticLayer("alert",data.value,{title : "信息提示框",dialog : true,time : 2000});
										layerAlert.init();
						           		if(data.code=='200'){
						           			searchFansGroup();
											/* 新增分组后在下拉列表框中新增相应的值 */
                                            $option = $("<option value='"+data.id+"'>"+data.name+"</option>");
                                            $("select").append($option);
						           			//refreshGroup();
						           		}
						           }
						       });
							layer.close(index);
						}else{
							var layerAlert = new ElasticLayer("alert","分组名为1-6个字符",{title : "信息提示框",dialog : true,time : 2000});
							layerAlert.init();
						}
					});
					$(".selectCancel_button",$(body)).click(function(){
			    		layer.close(index);
			    	});
				}
			})
			layerPage.init();
		}
		function updateNmae(id){
			var layerPage = new ElasticLayer("pageWindow",["${rc.contextPath}/wechat/fansGroup/update/"+id,'no'],{area:["500px","200px"],offset:["10%","38%"],title:"修改分组名称"},{
				success : function(layero ,index){
					var body = layer.getChildFrame('body', index);
					$(".updateConfirm_button",$(body)).click(function(){
						var name=$('#fansGroupNmae',$(body)).val();
						if(name && name.length<=6){
				    		 $.ajax({
						           type: "POST",
						           dataType: "json",
						           url: "${rc.contextPath}/wechat/fansGroup/edit",
						           data: {
						           		id:id,
						           		name:name
						           },
						           success: function(value){
						           		var layerAlert = new ElasticLayer("alert",value.value,{title : "信息提示框",dialog : true,time : 2000});
										layerAlert.init();
						           		if(value.code=='200'){
						           			searchFansGroup();
						           			searchFans.getPageValue();
						           			$('#pDD').find("span").html('分组名称：'+name);
                                            /* 修改分组后在下拉列表中更新相应的值 */
                                            $("option[value='"+id+"']").html(name);
						           		}
						           }
						       });
							layer.close(index);
						}else{
							var layerAlert = new ElasticLayer("alert","分组名为1-6个字符",{title : "信息提示框",dialog : true,time : 2000});
							layerAlert.init();
						}
						$('#pDD').find("a").css("color","#fff");
					});
					$(".selectCancel_button",$(body)).click(function(){
			    		layer.close(index);
			    	});
				}
			})
			layerPage.init();
		}
		var layerConfirm = new ElasticLayer("confirm","删除分组将会把该组已有成员全部移动至未分组里。是否确定删除？",{})
		function deletegroup(id){
		
			layer.confirm("删除分组将会把该组已有成员全部移动至未分组里。是否确定删除？",
				{}, function(index){
						layer.close(index);
							$.ajax({
								url:"${rc.contextPath}/wechat/fansGroup/delete/"+id,
								data:{},
								type:'post',
								cache:false,
								dataType : 'json', 
								success:function(data) {
									var layerAlert = new ElasticLayer("alert",data.value,{title : "信息提示框",dialog : true,time : 2000});
									layerAlert.init();
									if(data.code=='200'){
										$('#fansGroupId').val('');
										searchFans.getPageValue();
										searchFansGroup();
										/* 删除分组后在下拉列表中删除相应的值 */
                                        $("option[value='"+id+"']").remove();
									}
								}
							});
						
					}, function(index){
						layer.close(index);
					}
				);
		
		}
		function getFansIds(id){
			id_list=[];
			$("input[name='checkboxfans']").each(function(){
				if($(this).is(':checked')){
					id_list.push($(this).attr("attr-id"))
				}
			});
			if(id_list.length>0){
				$('#fanGroup').attr("disabled",false);
				$('#fanGroup').css('color','#000');
			}else{
				$('#fanGroup').attr("disabled",true);
				$('#fanGroup').css('color','#eee');
			}
		}
		
		function chioceGroup(id){
			var groupNewId=$('#'+id).val();
			var groupOldId=$('#'+id).attr("group-id");
			if(groupOldId!=groupNewId){
				var list_fans=[];
				list_fans.push(id);
				$.ajax({
					url:"${rc.contextPath}/wechat/fans/updateGroup",
					data:{id:groupNewId,list:list_fans},
					type:'post',
					cache:false,
					dataType : 'json', 
					success:function(data) {
						var layerAlert = new ElasticLayer("alert",data.value,{title : "信息提示框",dialog : true,time : 2000});
						layerAlert.init();
						if(data.code=='200'){
							searchFans.getPageValue();
							searchFansGroup();
						}
					}
				});
				
			}
		}
		
		function updateRem(id){
			var layerPage = new ElasticLayer("pageWindow",["${rc.contextPath}/wechat/fans/updateRemark/"+id,'no'],{area:["500px","200px"],offset:["10%","38%"],title:"修改备注"},{
				success : function(layero ,index){
					var body = layer.getChildFrame('body', index);
					$(".updateRemark_button",$(body)).click(function(){
						var remark=$('#fansRemark',$(body)).val();
			    		 $.ajax({
					           type: "POST",
					           dataType: "json",
					           url: "${rc.contextPath}/wechat/fans/editRemark",
					           data: {
					           		id:id,
					           		remark:remark
					           },
					           success: function(value){
					           		var layerAlert = new ElasticLayer("alert",value.value,{title : "信息提示框",dialog : true,time : 2000});
									layerAlert.init();
					           		if(value.code=='200'){
					           			if(remark){
					           				$('#value'+id).html(remark+"("+$('#value'+id).attr('name')+")");
					           			}else{
					           				$('#value'+id).html($('#value'+id).attr('name'));
					           			}
					           		}
					           }
					       });
						layer.close(index);
					});
					$(".updateRemarkCancel_button",$(body)).click(function(){
			    		layer.close(index);
			    	});
				}
			})
			layerPage.init();
		}
		
		/*function refreshGroup(){
			$("#fanGroup").children().remove();
			$.ajax({
	           type: "POST",
	           dataType: "json",
	           url: "${rc.contextPath}/wechat/fansGroup/getGroups",
	           success: function(value){
	           		if(value.code==200){
	           			if(value.value!=null && value.value!="" && typeof value.value == "string"){
							value = eval("("+value.value+")");
						}
						$("#fanGroup").append("<option value=''>添加到</option>");
						for(int i=0;i<value.length;i++){
							$option = $("<option value='"+value[i]["id"]+"'>"+value[i]["name"]+"</option>");
							$("#fanGroup").append($option);
						}
	           		}
	           }
	       });
		}*/
</script>
</#macro>