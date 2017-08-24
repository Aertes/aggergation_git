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
		<div style="font-size:14px; margin:20px auto;">
			<#if fans.remark?exists>
				用户备注：<input style="height:30px; line-height:30px;border:1px solid #d5d5d5; color:#d5d5d5;padding:0 10px;"  type="text" name="fansRemark" id="fansRemark" value="${fans.remark}">
			<#else>
				用户备注：<input style="height:30px; line-height:30px;border:1px solid #d5d5d5; color:#d5d5d5;padding:0 10px;"  type="text" name="fansRemark" id="fansRemark" >
			</#if>
		</div>
		<div style="margin:20px auto; width:70%;">
			<a style="text-decoration:none;" href="javascript:void(0)" class="redButton updateRemark_button">确定</a>					
			<a style="text-decoration:none;" href="javascript:void(0)" class="redButton marginLeft10 updateRemarkCancel_button">取消</a>
		</div>
	</body>
</html>