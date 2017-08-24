<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#setting classic_compatible=true>
<#import "/layouts/spring.ftl" as spring />
<#macro html title="微信矩阵平台">
<!DOCTYPE html>
<html>
	<head>
		<meta charset="utf-8">
		<title>东风雪铁龙SCRM微信矩阵平台</title>
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
		<link rel="stylesheet" href="${rc.contextPath}/wechat/css/dateTime.css" />
		<link rel="stylesheet" href="${rc.contextPath}/wechat/js/dateTimePicker/css/bootstrap-datetimepicker.min.css" />
		<style>
			.nav_bottom a{
				float:right;
			}
		</style>
		<@style/>
		
		<script type="text/javascript">
			window.jQuery || document.write("<script src='${rc.contextPath}/wechat/assets/js/jquery-2.0.3.min.js'>"+"<"+"/script>");
		</script>
		<script type="text/javascript">
			if("ontouchend" in document) document.write("<script src='${rc.contextPath}/wechat/assets/js/jquery.mobile.custom.min.js'>"+"<"+"/script>");
		</script>
		<script src="${rc.contextPath}/wechat/assets/js/ace-extra.min.js"></script>
		<script src="${rc.contextPath}/wechat/assets/js/bootstrap.min.js"></script>
		<script src="${rc.contextPath}/wechat/assets/js/typeahead-bs2.min.js"></script>
		
		<script src="${rc.contextPath}/wechat/assets/js/jquery-ui-1.10.3.custom.min.js"></script>
		<script src="${rc.contextPath}/wechat/assets/js/jquery.ui.touch-punch.min.js"></script>
		<script src="${rc.contextPath}/wechat/assets/js/jquery.slimscroll.min.js"></script>
		<script src="${rc.contextPath}/wechat/assets/js/jquery.easy-pie-chart.min.js"></script>
		<script src="${rc.contextPath}/wechat/assets/js/jquery.sparkline.min.js"></script>
		<script src="${rc.contextPath}/wechat/assets/js/ace-elements.min.js"></script>
		<script src="${rc.contextPath}/wechat/assets/js/ace.min.js"></script>
		<script type="text/javascript" src="${rc.contextPath}/wechat/assets/js/jquery-1.10.2.min.js" charset="UTF-8"></script>
		<script type="text/javascript" src="${rc.contextPath}/wechat/assets/js/jquery.validate.min.js" charset="UTF-8"></script>
		<script type="text/javascript" src="${rc.contextPath}/wechat/assets/js/bootstrap.min.js"></script>
		<script type="text/javascript" src="${rc.contextPath}/wechat/js/jquery.form.js" charset="UTF-8"></script>
		<script type="text/javascript" src="${rc.contextPath}/wechat/js/dateTimePicker/bootstrap-datetimepicker.js" charset="UTF-8"></script>

		<script src="${rc.contextPath}/wechat/js/dateTimeInterface.js"></script>
		<script type="text/javascript" src="${rc.contextPath}/wechat/js/commom.js" ></script>
		<script type="text/javascript" src="${rc.contextPath}/wechat/js/layer/layer.js" ></script>
		<script type="text/javascript" src="${rc.contextPath}/wechat/js/ElasticLayer.js" ></script>
		<script type="text/javascript" src="${rc.contextPath}/wechat/js/pagination.js"></script>
		
		<@script/>
	</head>
	<body>
		<div class="navbar navbar-default" id="navbar" style="background:none;">
			<script type="text/javascript">
				try{ace.settings.check('navbar' , 'fixed')}catch(e){}
			</script>

			<nav class="bColor" style="background:url(${rc.contextPath}/wechat/img/bColor.jpg) repeat; height:70px;">
				<div class="floatL logo">
					<img src="${rc.contextPath}/wechat/img/login/logo.png" />
				</div>
				<div class="floatR smallLogo">
					<img src="${rc.contextPath}/wechat/img/login/smallLogo.png" />
				</div>
                <div class="floatR marginRight15">
				<!-- <div class="floatR marginRight15" style="width: 265px;"> -->
					<div class="nav_top" style="overflow: hidden;">
						<span class="floatR curPublicNo" style="height: 36px;display: block;float: left;margin-right: 10px;font-family:'微软雅黑'">当前公众号：${session_current_publicno.nick_name?if_exists}</span>
						<div class="portial" style="float: left;">
							<i class="icon-user" style="line-height:36px;display:block;font-size:16px;color:#c8c8c8;"></i>
						</div>
						<span class="floatL marginLeft10">${loginUser.username}</span>
					</div>
					<div class="nav_bottom">
						<a href="${rc.contextPath}/wechat/auth/logout">注销</a>
						<a href="${rc.contextPath}/wechat/downloadWeixin/download">用户手册</a>
						<a href="http://ledp.dongfeng-citroen.com.cn/auth/${loginUser.id}"  target="view_window">切换至：长效媒体平台</a>
					</div>
				</div>
			</nav>
		</div>

		<div class="main-container" id="main-container">
			<script type="text/javascript">
				try{ace.settings.check('main-container' , 'fixed')}catch(e){}
			</script>

			<div class="main-container-inner">
				<a class="menu-toggler" id="menu-toggler" href="#">
					<span class="menu-text"></span>
				</a>

				<div class="sidebar" id="sidebar">
					<#include "/wechat/layouts/menu.ftl"/>
				</div>

				<div class="main-content" style="padding-right: 10px;">
					<@content/>
				</div>
			</div>
		</div>
	</body>
</html>
</#macro>