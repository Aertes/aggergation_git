<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#import "/layouts/spring.ftl" as spring />
<#setting classic_compatible=true>
<#macro html title="东风雪铁龙经销商长效媒体管理大数据平台">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<meta http-equiv="X-UA-Compatible" content="IE=8" >
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<title>${title}</title>
		<meta name="description" content="${title}" />
		<link rel="stylesheet" type="text/css" href="${rc.contextPath}/css/easyui.css">
		<link rel="stylesheet" type="text/css" href="${rc.contextPath}/css/icon.css">
		<link rel="stylesheet" type="text/css" href="${rc.contextPath}/css/demo.css">
		<link rel="stylesheet" type="text/css" href="${rc.contextPath}/css/custom.css">
		<@style/>
		<script type="text/javascript" src="${rc.contextPath}/js/jquery.min.js"></script>
		<script type="text/javascript" src="${rc.contextPath}/js/jquery.easyui.min.js"></script>
		<script type="text/javascript" src="${rc.contextPath}/js/easyui-lang-zh_CN.js"></script>
		<script type="text/javascript" src="${rc.contextPath}/js/custom.js"></script>
		<script type="text/javascript" src="${rc.contextPath}/js/date.js"></script>
		<script type="text/javascript" src="${rc.contextPath}/js/pagination/Commom.js"></script>
		<script type="text/javascript" src="${rc.contextPath}/js/pagination/Validform_v5.3.2_min.js"></script>
		<script type="text/javascript" src="${rc.contextPath}/js/highcharts/highcharts.js"></script>
		<script type="text/javascript" src="${rc.contextPath}/js/highcharts/exporting.js"></script>
		<@script/>
		<!--[if lte IE 8]>
			<script src="excanvas.js"></script>
		<![endif]-->
	</head>
	<body class="easyui-layout bodyContiner" >
		<div class="header" data-options="region:'north',border:false" style="*height:40px">
			<div class="header clearFix">
				<div class="floatL title">${title}</div>
				<#if loginUser?exists&&loginUser.username?exists>
					<div class="floatR tr">
						<a href="${rc.contextPath}/user/setting" title="${loginUser.username}">
							<img alt="" src="${rc.contextPath}/css/images/user_phone.png" style="width: 16px; height: 13px;" class="pr5" />${loginUser.username}
						</a>
						<span style="color:#ececec;"><a href="${rc.contextPath}/download/download">用户手册</a></span>
						<a href="${rc.contextPath}/auth/logout" title="">
							<img alt="注销" src="${rc.contextPath}/css/images/logout.gif" style="width: 16px; height: 13px;" class="pr5" />注销
						</a>
					</div>
				<#else>
					<div class="floatR tr">
					<a href="${rc.contextPath}" title="">
						<span style="color:#ececec;">登陆</span>
					</a>
					</div>
				</#if>
			</div>
		</div>
		<div data-options="region:'west',split:false,title:false" class="leftMain">
			<#include "/layouts/menu.ftl"/>
		</div>
		<div data-options="region:'center',title:false" class="rightMain" id="rightMain">
			<div class="breaktop">
				<#include "/layouts/nav.ftl"/>
			</div>
			<div class="main clearfix">
				<@content/>
			</div>
		</div>
	</body>
</html>
</#macro> 