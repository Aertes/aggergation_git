<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#assign tag=JspTaglibs["/WEB-INF/taglib/permission.tld"]>
<html lang="en">
	<head>
		<meta charset="utf-8" />
		<link rel="stylesheet" href="${rc.contextPath}/wechat/css/commom.css" />
		<link rel="stylesheet" href="${rc.contextPath}/wechat/css/color.css" />
		<link rel="stylesheet" href="${rc.contextPath}/wechat/css/custome.css" />
	</head>

	<body>
		<div style="font-size:14px;margin:20px auto;">
			分组名称：<input style="height:30px; line-height:30px;border:1px solid #d5d5d5;padding:0 10px;" type="text" name="fansGroupNmae" id="fansGroupNmae" value="${fansGroup.name}">
		</div>
		<div style="margin:20px auto; width:70%;">
			<a href="javascript:void(0)" style="text-decoration:none;" class="redButton updateConfirm_button">确定</a>					
			<a href="javascript:void(0)" style="text-decoration:none;" class="redButton marginLeft10 selectCancel_button">取消</a>
		</div>
	</body>
</html>