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
		<div style="width:100%; margin:20px auto;">
			<div style="font-size:14px;">
				<label>分组名称：</label><input type="type" name="fansGroupNmae" id="fansGroupNmae" style="padding:0 10px;height:30px; line-height:30px;border:1px solid #d5d5d5;">
			</div>
			<div style="margin:20px auto; width:70%;">
				<a href="javascript:void(0)" class="redButton svaeConfirm_button" style="text-decoration:none;">确定</a>					
				<a href="javascript:void(0)" class="redButton marginLeft10 selectCancel_button" style="text-decoration:none;">取消</a>
			</div>
		</div>
	</body>
</html>