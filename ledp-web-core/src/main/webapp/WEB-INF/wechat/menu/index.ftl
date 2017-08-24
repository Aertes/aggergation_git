<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#assign tag=JspTaglibs["/WEB-INF/taglib/permission.tld"]>
<#include "/wechat/layouts/main.ftl"/>
<@html></@html>
<#macro style>
	<link rel="stylesheet" href="${rc.contextPath}/wechat/css/bootstrap-switch.css" />
	<style>
		.topPar i.adds{
			line-height:34px;
		}
		.animatePicture{
			padding:10px;
		}
		.animatePicture label{
			width:68%;
			float:left;
		}
		.animatePicture_chil{
			width:30%;
			margin-left:1%;
			float:left;
			height:auto;
		}
		.tuwenResult>div:last-child{
			border-bottom:none;
		}
		.menuUpload{
			width:65px;
		}
		.tuwenResult{
			width:100%;
			margin:2% 0 0;
		}
		.menuLeft{
			height:auto;
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
			<span>菜单管理</span>
		</li>
	</ul>
	</div>
	<div style="overflow: hidden;min-width: 900px;"> 
	<div class="menuIntrduce" style="height:70px;">
		<p>可创建最多3个一级菜单，每个一级菜单下可创建最多5个二级菜单。</p>
		<p style="line-height: 15px;color:#d12d32;">菜单选项中黑色字体为系统默认菜单，不可修改。显示为活动网点(自定义)的菜单项即可修改</p>
	</div>
	<div style="width:890px;overflow:hidden;margin:0px auto;">
		<div class="floatL menuLeft">
			<div class="pageCenter" style="margin-left: 0px;margin-bottom: 0px;width: 100%;">
				<p class="phoneStyle">
					<i class="phone1"></i>
					<i class="phone2"></i>
				</p>
				<div class="phoneContent">
					<p class="activeSearch">
						<input type="text" style="width:100%;" placeholder="双击红色菜单名可进行文字修改（其余不可更改）" disabled="disabled">
					</p>
					<div class="jianzhan_content" style="height: 450px;">
						<form id="menuform" method="post">
							<div class="keyBood" style="height:39px; line-height:39px;width:14%;text-align:center;">
								<i class="icon-table" style="font-size:24px;"></i>
							</div>
							<ul id="menulist" style="width: 97%;">
								
							</ul>
						</form>
						
					</div>
					<div class="phoneFoot">
						<i class="circle"></i>
					</div>
				</div>
			</div>
		</div>
		
		<div class="floatL menuRight">
			<div class="menuPar">
				<p  class="menus marginLeft40">点击左侧菜单名称，指定点击后的响应事件。</p>
				<div>
					<div class="floatL marginLeft40 menuMethod displaynone" url="menu_sendMessage">
						<a href="javascript:void(0);">
							<i class="icon-envelope"></i>
							<span>发送消息</span>
						</a>
					</div>
					<div class="floatL marginLeft40 menuMethod displaynone" url="menu_resirectUrl">
						<a href="javascript:void(0);">
							<i class="icon-globe"></i>
							<span>跳转网页</span>
						</a>
					</div>
				</div>
				<p class="colorR menuRedirectError" style='line-height:35px;position:absolute;bottom:50px;text-indent:2em;'></p>
				<div class="menuNew">
					<span style="line-height:25px;">提示：菜单修改后，点击“发布”使其生效。根据微信的机制需要大约24小时内才能生效,或者重新关注公众号。</span>
				</div>
			</div>
			<div class="messagesend displaynone" id="menu_sendMessage">
				<div  class="tabContainer tabSendpage">
					<ul class="tabContiner_nav borderbottom">
						<li class="active" href="#page_icon1">
							<i class=""></i>
							图文信息
						</li>
						<li href="#page_icon2">
							<i class="" style='font-style: inherit;'>图片</i>
						</li>
					</ul>
					<div class="tabContianer_con displayBlock" id="edit">
						<div style="display: block;padding:30px 10px;" id="page_icon1" class="addPiContinaer">
							<div class="addPi1" style='width:60%'>
								<div style="width: 50%;" class="floatL addPar">
									<a class="createSingle" href="">
										<i class="icon-plus add" style='font-size:50px'></i>
										<span class="addTitle">新建单图文信息</span>
									</a>
								</div>
								<div style="width: 50%;" class="floatL addPar">
									<a class="createMultiple" href="">
										<i class="icon-plus add" style='font-size:50px'></i>
										<span class="addTitle">新建多图文信息</span>
									</a>
								</div>
							</div>
							<div class="addPi1" style='width:36%'>
								<div style="width: 100%;" class="floatL addPar">
									<a href="javascript:void(0);">
										<i class="icon-plus add selectFromSucai" style='font-size:50px'></i>
										<span class="addTitle">从素材库选择</span>
									</a>
								</div>
							</div>
						</div>
						<div id="page_icon2" style="display: none; padding: 30px 10px;overflow:hidden">
							<div class="addPi1 ml10">
								<form id="upload_image" style="margin-top:80px;" >
									<input type="file" class='menuUpload' id="uploadImage" name="image" value="上传图片" >
								</form>
							</div>
							<div class="addPi1">
								<div style="width: 100%;" class="floatL addPar">
									<a href="javascript:void(0);">
										<i class="icon-plus add selectFromSucai" style="font-size:50px"></i>
										<span class="addTitle">从素材库选择</span>
									</a>
								</div>
							</div>
						</div>
						<p class='colorR menuError' style='line-height:35px'></p>
					</div>
				</div>
				<a href="javascript:void(0);" class="menu_return">重置</a>
			</div>
			<div class="messagesend displaynone" id="menu_resirectUrl">
				<p style="line-height:30px;">请指定菜单点击后跳转的页面地址：</p>
				<p>
					<input type="text" id="pageurl" style="width: 80%;" class="marginLeft10" />
				</p>
				<p class='messageCheckP'></p>
				<p style="padding:10px;">(请填写完整URL地址，例如：http://www.baidu.com)</p>
				<p class='colorR menuError' style='line-height:35px'></p>
				<p style="text-indent: 2em;padding: 10px;background: #f4f5f9;">注意：链接网页可以是外站网址，您也可以通过本平台中的“微站管理"和“活动管理”功能菜单，快速地创建一个“微站”或者“活动”，提取其页面地址，输入到这里。</p>
				<br/>
				<a href="javascript:void(0)" id="saveurl" style="color:white;" class="redButton">保存</a>
				<a href="javascript:void(0);" class="menu_return">重置</a>
			</div>
			<div id="show" style='overflow:hidden;padding:10px' class="displaynone">
				<p>订阅者点击该子菜单会收到以下消息</p>
				<div class='show_meterial' style='overflow:hidden;width:100%'></div>
				<a href='javascript:void(0)' class="menu_edit" id="modifyMaterial">修 改</a>
			</div>
		</div>
	</div>
	</div>
	<div class="lineHeight40 textAlign" style="margin:20px 0;">
	<a href="javascript:void(0);" class="redButton preview" style="width: 100px;height: 30px;display: inline-block;line-height: 30px;padding:0px;" >预 览</a>
	<a href="javascript:void(0);" id="publish" class="redButton" style="width: 100px;height: 30px;display: inline-block;line-height: 30px;padding:0px;margin-left:20px;" >发 布</a>
	<input type="hidden" id="menuid" value="${menuid?if_exists}" />
	<input type="hidden" id="materialId" value="${materialId?if_exists}">
	</div>
</#macro>
<#macro script>
		<script src="${rc.contextPath}/wechat/js/bootstrap-switch.js"></script>
		<script src="${rc.contextPath}/wechat/js/uedit/ueditor.config.js"></script>
		<script src="${rc.contextPath}/wechat/js/uedit/ueditor.all.js"></script>
		<script src="${rc.contextPath}/wechat/js/uedit/lang/zh-cn/zh-cn.js"></script>
		<script>
		var curOrg = ${curOrg?if_exists?default(0)};
		var curDealer = ${curDealer?if_exists?default(0)};
		//每列序号
		var col1Sort = 0,col2Sort = 0,col3Sort = 0;
			$(function(){
			
				var $publicNoId = "${publicNoId?if_exists}";
				var $publicNo_Verify = "${publicNo_Verify?if_exists}";
				if($publicNoId == "-1"){
					window.location.href = "${rc.contextPath}/wechat/publicno/message";
				}
				if($publicNo_Verify == "-1"){
					window.location.href = "${rc.contextPath}/wechat/publicno/unauth/-1"
				}
				
				$(".preview").click(function(){
					layer.open({
					    type: 2,
					    shadeClose: true,
					    title : false,
					    shade: 0.8,
					    area: ['357px', '680px'],
					    content: '${rc.contextPath}/wechat/menu/preview'
					}); 
				});
				
				$("#page_icon2").delegate(".tuwenResult","mouseenter",function(){
					$(this).find(".tuwenOperate").css({"display":"block"});
				});
				$("#page_icon2").undelegate(".tuwenResult","mouseleave");
				$("#page_icon2").delegate(".tuwenResult","mouseleave",function(){
					$(this).find(".tuwenOperate").css({"display":"none"});
				});
				$("#edit").delegate(".tuwenResult","mouseenter",function(){
					$(this).find(".tuwenOperate").css({"display":"block"});
				});
				$("#edit").undelegate(".tuwenResult","mouseleave");
				$("#edit").delegate(".tuwenResult","mouseleave",function(){
					$(this).find(".tuwenOperate").css({"display":"none"});
				});
	
				
				$(".createSingle").click(function(){
					$(".createSingle").attr("href","${rc.contextPath}/wechat/material/createSingleAndRedirect?menuid="+$("#menuid").val());
				});
				
				$(".createMultiple").click(function(){
					$(".createMultiple").attr("href","${rc.contextPath}/wechat/material/createMultipleAndRedirect?menuid="+$("#menuid").val());
				});
				
				var layerPage;
				//初始化菜单
				var url = "${rc.contextPath}/wechat/menu/search?r="+new Date().getTime();
				$.ajax({
					url:url,
					dataType:'json',
					success:function(result){
						var data = result.data;
						if(data){
							var tem = 1;
							for(var i=0;i<3;i++){
								var len = $("#menulist").find(".topPar").length;
								if(len == 3){
									break;
								}
								var obj = data[i];
								var $emptyli;
								if(obj!=undefined&&obj.col!=(tem)){
									tem = obj.col+1;
									var cols = obj.col-i-1;
									for(var j=0;j<cols;j++){
										if(len<3){
											emptyli = $("<li>").addClass("topPar");
											emptyli.append($("<i class='icon-plus adds'></i>"));
											$("#menulist").append(emptyli);
										}
									}
								}
								
								if(obj == undefined){
									$li = $("<li>").addClass("topPar");
									$li.append($("<i class='icon-plus adds'></i>"));
								}else{
									var material = obj.material;
									if(isEmpty(material)){
										material = {"id":null}
									}
									$li = $("<li mid='"+obj.id+"' isedit="+obj.isEdit+" msgtype='"+obj.event+"' url='"+obj.url+"' material='"+material.id+"'>").addClass("topPar");
									var $a;
									if(obj.isEdit == 1 || curOrg > 0){
										$a = $("<a href='javascript:void(0);' style='color:#cc0000'>"+obj.name+"</a>");
									}else{
										$a = $("<a href='javascript:void(0);'>"+obj.name+"</a>");
									}
									$ul = $("<ul>");
									$li.append($a);
									if(obj.children.length>0){
										if(obj.children.length<5){
											$ul.append("<li class='menu'><i class='icon-plus adds'></i></li>");
										}
										for(var j=0;j<obj.children.length;j++){
											var child=obj.children[j];
											var subMaterial = child.material;
											if(isEmpty(subMaterial)){
												subMaterial = {"id":null}
											}
											if(child.isEdit == 1 || curOrg > 0){
												$sub = $("<li class='menu' isedit="+child.isEdit+" mid='"+child.id+"' msgtype='"+child.event+"' url='"+child.url+"' material='"+subMaterial.id+"'><a href='javascript:void(0);' style='color:#cc0000'>"+child.name+"</a></li>");
											}else{
												$sub = $("<li class='menu' isedit="+child.isEdit+" mid='"+child.id+"' msgtype='"+child.event+"' url='"+child.url+"' material='"+subMaterial.id+"'><a href='javascript:void(0);' >"+child.name+"</a></li>");
											}
											$ul.append($sub);
										}
										$li.append($ul);
									}else{
										$li.append($("<ul><li class='menu'><i class='icon-plus adds'></i></li></ul>"));
									}
								}
								
								$("#menulist").append($li);
								
								$(".menuLeft").delegate("li","mouseover",function(event){
									event.stopPropagation();
									event.preventDefault();
								});
								
								$(".menuLeft").delegate("li","mouseout",function(event){
									event.stopPropagation();
									event.preventDefault();
								});
								
							}
							//如果从素材跳转过来
							var _menuid = $("#menuid").val();
							if(_menuid){
								$("li[mid='"+_menuid+"'] a").trigger("click");
							}
						}
				}});
				
				//图片
				$('#uploadImage').change(function() {
					var url = "${rc.contextPath}/wechat/material/uploadImage";
					$("#upload_image").ajaxSubmit({
						type: "POST",
						url:url,
						dataType: "json",
					    success: function(data){
					     	if(data.code=="success"){
					     		$("#thumbMediaId").val(data.mediaId);
					     		$("#imageurl").val(data.url);
					     		$(".noImg").find("img").attr("src",data.url);
					     		$("#filepath").val(data.filepath);
					     		//如果从素材跳转过来
								var _menuid = $("#menuid").val();
								if(_menuid){
									var $li = $("li[mid='"+_menuid+"']");
									$li.attr("msgtype","click");
									$li.attr("material",data.materialId);
								}
					     		$.ajax({
									type:"post",
									url: "saveOrUpdate",
									data:{
										"id":$("#menuid").val(),
										"material.id":data.materialId,
										"event":"click"
									},
									async:true ,
									success: function(value){
									}
								});
					     		getMaterialById(data.materialId);
					     	}
						}
					});
				});
				
				//发布
				$("#publish").click(function(){
					var state = checkMenu();
					if(state == true){
						$.ajax({
				           type: "POST",
				           dataType: "json",
				           url: "${rc.contextPath}/wechat/menu/publish",
				           success: function(value){
				           		layer.alert(value.message);
				           },
				           error: function(value){
				           		layer.alert("系统繁忙");
				           }
				        });
					}
				});
				
			});
			
			function checkMenu () {
				var state = true;
				$(".menuLeft").find("li").each(function(){
					var title = $(this).find("a").html();
					if(title == '活动(网点自定义)'){
						return true;
					}
					if(state==false){
						return state;
					}
					if($(this).children(".icon-plus").length>0){
						return;
					}
					var isParent = $(this).hasClass("topPar");
					var type = $(this).attr("msgtype"),url = $(this).attr("url"),materialId = $(this).attr("material");
					if(isParent){
						var isChilren = $(this).find("ul>li").length<=0 ;
						if(isChilren){
							state = redirect(type,url,materialId);
						}
					}else{
						state = redirect(type,url,materialId);
					}
					if(state==false){
						$(".messageCheckP").text("");
						if(type == "undefined"){
							$(this).find("a").trigger("click");
							$(".menuRedirectError",$(".menuPar")).text("请选择该菜单项的功能！");
						}else{
							$(this).css("background-color","#ccc");
							var url = type=="view" ? "menu_resirectUrl" :  "menu_sendMessage";
							$(this).find("a").trigger("click");
							$(".messageCheckP").text("请输入正确的链接");
						}
					}
				});
				return state;
			}
			
			function redirect(type,url,meterialId){
				var submitState = true;
				if(type == "view"){
					if(!isURL(url)){
						var urlData = $("#pageurl").val();
						$(".menuPar [url='menu_resirectUrl']",$(".menuRight")).trigger("click");
						$("#show").removeClass("displayblock").addClass("displaynone");
						submitState = false;
						$("#pageurl").val(urlData);
					}
				}else if(type == "click"){
					var state = meterialId=="" || meterialId == "undefined";
					if(state){
						$(".menuPar [url='menu_sendMessage']",$(".menuRight")).trigger("click");
						$("#show").removeClass("displayblock").addClass("displaynone");
						submitState = false;
					}
				}else{
					submitState = false;
				}
				return submitState;
			}
			
		</script>
</#macro>