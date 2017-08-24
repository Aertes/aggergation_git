<html lang="en">
	<head>
		<meta charset="utf-8" />
		<title></title>

		<meta name="description" content="overview &amp; stats" />
		<meta name="viewport" content="width=device-width, initial-scale=1.0" />
		<link href="${rc.contextPath}/wechat/assets/css/bootstrap.min.css" rel="stylesheet" />
		<link rel="stylesheet" href="${rc.contextPath}/wechat/assets/css/font-awesome.min.css" />
		<link rel="stylesheet" href="${rc.contextPath}/wechat/css/reset.css" />
		<link rel="stylesheet" href="${rc.contextPath}/wechat/css/commom.css" />
		<link rel="stylesheet" href="${rc.contextPath}/wechat/css/color.css" />
		<link rel="stylesheet" href="${rc.contextPath}/wechat/css/custome.css" />
		<link rel="stylesheet" href="${rc.contextPath}/wechat/css/category.css" />

		<link rel="stylesheet" href="${rc.contextPath}/wechat/assets/css/ace-fonts.css" />

		<link rel="stylesheet" href="${rc.contextPath}/wechat/assets/css/ace.min.css" />
		<link rel="stylesheet" href="${rc.contextPath}/wechat/assets/css/ace-rtl.min.css" />
		<link rel="stylesheet" href="${rc.contextPath}/wechat/assets/css/ace-skins.min.css" />

		<script src="${rc.contextPath}/wechat/assets/js/ace-extra.min.js"></script>
		
	</head>

	<body>
		<div>
			<div class="select_back">选择素材</div>
			<form  class="form-search formSelect" action="${rc.contextPath}/wechat/material/searchImg" method="post" id="selectGroupForm" style="width:100%;">
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
					<div class="floatR marginRight15">
						<input type="file" id="media" style="line-height:20px;height:20px;" name="media" value="上传" />
					</div>
				</div>
			</form>
			<div id="selectGroupTable" class="seectContent" style="width:100%">
				<div style="padding-bottom:125px;">
					<ul></ul>
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
		<script type="text/javascript" src="${rc.contextPath}/wechat/js/jquery.form.js" charset="UTF-8"></script>
		<script type="text/javascript" src="${rc.contextPath}/wechat/js/layer/layer.js" ></script>
		<script type="text/javascript" src="${rc.contextPath}/wechat/js/ElasticLayer.js" ></script>
		<script type="text/javascript" src="${rc.contextPath}/wechat/js/pagination.js"></script>
		<script>
			$(function(){
				function pageInit(){
					var page1 = new pagination("selectGroupForm" , "selectGroupTable" , function(data){
						var $ul = $("#selectGroupTable>div>ul").length>0 ? $("#selectGroupTable>div>ul") : $("ul");
						$ul.addClass("selectUserGroup");
						$ul.children().remove();
						$("#selectGroupTable").find(">div:nth-of-type(2)").addClass("pageSelect");
		
						for(var i=0;i<data.length;i++){
							var $li = $("<li>").attr("id",data[i]["id"]).attr("data-url",data[i]["url"]).attr("mediaid",data[i]["mediaId"]);
							var $container = $("<div><img src="+data[i]["url"]+"><p>"+data[i]["title"]+"</p></div>");
							var $shadow = $("<div class='selectPageShadow' style='width:96%;'>");
							$li.append($container).append($shadow);
							$ul.append($li);
						};
		
						$("#selectGroupTable>div>ul>li").click(function(){
							var shadow = $(this).find(".selectPageShadow");
							var hasChildren = $(shadow).children().length>0;
							if(hasChildren){
								$(shadow).children().remove();
								$(shadow).addClass("displaynone").removeClass("displayBlock");
							}else{
								var $right = $("<i class='icon-ok' style='top:20%;right:20%;' class='rightIcon'>");
								$(shadow).append($right);
								$(shadow).addClass("displayBlock").removeClass("displaynone");
							}
						});
					});
				}
				pageInit();
				//图片上传
				$('#media').change(function() {
					var url = "${rc.contextPath}/wechat/material/uploadImg";
					$("#selectGroupForm").ajaxSubmit({
						type: "POST",
						url:url,
						dataType: "json",
					    success: function(data){
					    	layer.alert(data.message);
					     	pageInit();
						},
						error: function(){
					   	    layer.alert("图片上传失败,请刷新后重试");
					    }
					});
				});
		});
		</script>
	</body>
</html>