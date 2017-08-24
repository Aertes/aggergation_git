<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#assign tag=JspTaglibs["/WEB-INF/taglib/permission.tld"]>
<html lang="en">
	<head>
		<meta charset="utf-8" />
		<title>Dashboard - Ace Admin</title>

		<meta name="description" content="overview &amp; stats" />
		<meta name="viewport" content="width=device-width, initial-scale=1.0" />
		<link href="${rc.contextPath}/wechat/assets/css/bootstrap.min.css" rel="stylesheet" />
		<link rel="stylesheet" href="${rc.contextPath}/wechat/assets/css/font-awesome.min.css" />
		<link rel="stylesheet" href="${rc.contextPath}/wechat/css/reset.css" />
		<link rel="stylesheet" href="${rc.contextPath}/wechat/css/commom.css" />
		<link rel="stylesheet" href="${rc.contextPath}/wechat/css/color.css" />
		<link rel="stylesheet" href="${rc.contextPath}/wechat/css/custome.css" />

		<link rel="stylesheet" href="${rc.contextPath}/wechat/assets/css/ace-fonts.css" />

		<link rel="stylesheet" href="${rc.contextPath}/wechat/assets/css/ace.min.css" />
		<link rel="stylesheet" href="${rc.contextPath}/wechat/assets/css/ace-rtl.min.css" />
		<link rel="stylesheet" href="${rc.contextPath}/wechat/assets/css/ace-skins.min.css" />
		<link rel="stylesheet" href="${rc.contextPath}/wechat/css/zTreeStyle/zTreeStyle.css" />

		<script src="${rc.contextPath}/wechat/assets/js/ace-extra.min.js"></script>
		<style>
			.tuwen li{
				padding:0;
			}
			.tuwen li >p{
				padding:10px;
			}
			.tuwenChild{
				padding:10px;
				border-top:0;
			}
			.tuwen .tuwenChild:first-child{
				border-top:1px solid #d9d9d9;
			}
			.select_tuwen .tuwen li{
				padding:10px;
			}
			.select_tuwen>ul{
				margin:1%;
			}
			.tuwenChild h4{
				padding:0;
			}
		</style>
		
	</head>

	<body>

		<div>
			<div class="select_back">选择素材</div>
				<form class="form-search formSelect" action="${rc.contextPath}/wechat/material/${materialAction}" method="post" enctype="multipart/form-data" id="form1Id" >
					<div class="row lineHeight70 marginLeft10">
						<div class="col-xs-12 col-sm-8">
							<div class="input-group">
								<input type="text" style="width: 250px;" name="condition" placeholder="标题/作者/摘要" class="form-control search-query search marginTop2">
								<span class="input-group-btn">
									<button class="btn btn-purple btn-sm submit" type="button">
										搜索
										<i class="icon-search icon-on-right bigger-110"></i>
									</button>
								</span>
							</div>
						</div>
						<#if materialAction?exists && materialAction == "search">
							<div class="floatR marginRight15">
								<a href="${rc.contextPath}/wechat/material/createMultiple" class="redButton" target="_blank">新建图文信息</a>
							</div>
						<#else>
							<div class="floatR marginRight15">
								<input style="line-height:20px;height:20px;" type="file" name="image" id="upload_image" value="上传"/>
							</div>
						</#if>
					</div>
				</form>
			</div>
			<div id="table1Id" class="seectContent" style="width:100%">
				<div class="tuwenParent select_tuwen" style="padding-bottom:125px;">
					<ul id="content1" class="tuwen"></ul>
					<ul id="content2" class="tuwen"></ul>
					<ul id="content3" class="tuwen"></ul>
					<ul id="content4" class="tuwen"></ul>
				</div>
				<div class="padding15">
					<div class="pagegination floatR">
						<ul>
						</ul>
					</div>
				</div>
			</div>
			<div class="select_back textAlign" style="bottom: 0px;">
				<a href="javascript:void(0)" class="redButton selectConfirm_button">确定</a>					
				<a href="javascript:void(0)" class="redButton marginLeft10 selectCancel_button">取消</a>
			</div>
		</div>

		<script type="text/javascript">
			window.jQuery || document.write("<script src='${rc.contextPath}/wechat/assets/js/jquery-2.0.3.min.js'>"+"<"+"/script>");
		</script>

		<script type="text/javascript">
			if("ontouchend" in document) document.write("<script src='${rc.contextPath}/wechat/assets/js/jquery.mobile.custom.min.js'>"+"<"+"/script>");
		</script>
		<script src="${rc.contextPath}/wechat/assets/js/bootstrap.min.js"></script>
		<script src="${rc.contextPath}/wechat/assets/js/typeahead-bs2.min.js"></script>
		<script src="${rc.contextPath}/wechat/assets/js/jquery-ui-1.10.3.custom.min.js"></script>
		<script src="${rc.contextPath}/wechat/assets/js/jquery.ui.touch-punch.min.js"></script>
		<script src="${rc.contextPath}/wechat/assets/js/jquery.slimscroll.min.js"></script>
		<script src="${rc.contextPath}/wechat/assets/js/jquery.easy-pie-chart.min.js"></script>
		<script src="${rc.contextPath}/wechat/assets/js/jquery.sparkline.min.js"></script>
		<script src="${rc.contextPath}/wechat/assets/js/ace-elements.min.js"></script>
		<script src="${rc.contextPath}/wechat/assets/js/ace.min.js"></script>
		<script type="text/javascript" src="${rc.contextPath}/wechat/js/report/highcharts.js" ></script>
		<script type="text/javascript" src="${rc.contextPath}/wechat/js/report/highcharts-more.js" ></script>
		<script type="text/javascript" src="${rc.contextPath}/wechat/js/report/exporting.js" ></script>
		<script type="text/javascript" src="${rc.contextPath}/wechat/js/report/gray.js" ></script>
		<script type="text/javascript" src="${rc.contextPath}/wechat/js/report/Chart.js" ></script>
		<script type="text/javascript" src="${rc.contextPath}/wechat/js/commom.js" ></script>
		<script type="text/javascript" src="${rc.contextPath}/wechat/js/layer/layer.js" ></script>
		<script type="text/javascript" src="${rc.contextPath}/wechat/js/pagination.js"></script>
		<script type="text/javascript" src="${rc.contextPath}/wechat/js/jquery.form.js" charset="UTF-8"></script>
		<script type="text/javascript" src="${rc.contextPath}/wechat/js/zTree/jquery.ztree.core-3.5.js"></script>
		<script type="text/javascript" src="${rc.contextPath}/wechat/js/zTree/jquery.ztree.excheck-3.5.js"></script>
		<script type="text/javascript" src="${rc.contextPath}/wechat/js/zTree/jquery.ztree.exedit.js"></script>
		<script type="text/javascript" src="${rc.contextPath}/wechat/js/zTree/jquery.ztree.exhide-3.5.js"></script>
		<script>
		$(function(){
			pageInit();
			function pageInit(){
				var page1 = new pagination("form1Id" , "table1Id" , function(value){
					$("#content1").find("li").remove();
					$("#content2").find("li").remove();
					$("#content3").find("li").remove();
					$("#content4").find("li").remove();
					$("#table1Id").find(">div:nth-of-type(2)").addClass("pageSelect");
					
					var dom = value;
					for(var i=0;i<dom.length;i++){
						var obj = dom[i];
						var $li = $("<li>");
						var h1 = $("<h1>").text(obj["title"]);
						var p = $("<p>").text(obj["date"]);
						var img = $("<img src='"+obj.url+"'>");
						
						$li.append(h1).append(p).append(img).append(p_content);
						
						if(obj.children!=null &&　obj.children!=undefined &&　obj.children.length>0){
							for(var j=0;j<obj.children.length;j++){
								var childValue = obj.children[j];
								var $child_h4 = $("<h4><a href='#'>"+childValue["title"]+"</a></h4>");
								var $child_img = $("<img src='"+childValue["url"]+"' />");
								$li.append($("<div class='tuwenChild'>").append($child_h4).append($child_img));
							}
						}else{
							var p_content = $("<p>").text(obj.digest);
							$li.append(p_content);
						}
						var $hiddenInput = $("<input type='hidden' name='mediaId' value='"+value[i]["mediaId"]+"' /><input type='hidden' name='id' value='"+value[i]["id"]+"' />");
						var $shadow = $("<div class='selectPageShadow'>");
						$li.append($shadow).append($hiddenInput);
						if(i%4==0){
							$("#content1").append($li);
						}else if(i%4==1){
							$("#content2").append($li);
						}else if(i%4==2){
							$("#content3").append($li);
						}else if(i%4==3){
							$("#content4").append($li);
						}
					}
					
					
					$(".select_tuwen>ul>li").hover(function(){
						$(this).find(".selectPageShadow").addClass("displayBlock").removeClass("displaynone");
					},function(){
						if($(this).find(".selectPageShadow:first").children().length<=0){
							$(this).find(".selectPageShadow").addClass("displaynone").removeClass("displayBlock");
						}
					});
					
					$(".select_tuwen>ul>li").click(function(){
						var shadow = $(this).find(".selectPageShadow");
						$(this).parents(".select_tuwen:first").find(".selectPageShadow").children().remove();
						if($(shadow).find(".icon-ok").length==0){
							var $right = $("<i class='icon-ok' class='rightIcon'>");
							$(shadow).append($right);
						}
						$(".select_tuwen>ul>li").find(".selectPageShadow").removeClass("displayBlock").addClass("displaynone");
						$(shadow).addClass("displayBlock").removeClass("displaynone");
					});
				});
			}
			
			//图片上传
			$('#upload_image').change(function() {
				var url = "${rc.contextPath}/wechat/material/uploadImage";
				$("#form1Id").ajaxSubmit({
					type: "POST",
					url:url,
					timeout:5000,
					dataType: "json",
				    success: function(data){
				     	if(data.code=="success"){
				     		pageInit();
				     	}else{
				     		layer.alert(data.message);
				     	}
					},
					error: function(){
 				   	    layer.alert("系统繁忙,请稍侯重试！");
 				    }
				});
			});
			
			function getValue(){
				return "zhanfan"
			}
		});
		</script>
	</body>
</html>