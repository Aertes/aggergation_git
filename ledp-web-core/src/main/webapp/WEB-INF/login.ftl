<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>东风长效媒体平台</title>
		<link rel="stylesheet" type="text/css" href="${rc.contextPath}/css/easyui.css">
		<link rel="stylesheet" type="text/css" href="${rc.contextPath}/css/icon.css">
		<link rel="stylesheet" type="text/css" href="${rc.contextPath}/css/demo.css">
		<link rel="stylesheet" type="text/css" href="${rc.contextPath}/css/custom.css">
		<script type="text/javascript" src="${rc.contextPath}/js/jquery.min.js"></script>
		<script type="text/javascript" src="${rc.contextPath}/js/jquery.easyui.min.js"></script>
		<script type="text/javascript" src="${rc.contextPath}/js/custom.js"></script>
		<script type="text/javascript">
			function flushCode(){
				$('#code').attr('src','${rc.contextPath}/auth/code?time='+new Date().getTime())
			}
		</script>
</head>
<body>
	<div class="loginmain pre">
		<img alt="" src="${rc.contextPath}/images/login.jpg" width="100%" />
		<div class="loginbj">
			<div class="ml pre">
					<#if lockTime?exists>
						<div class="error" style="line-height:16px;margin:0;left:15%;width:80%;top:3%;">该账号连续三次登录失败，已被系统冻结，将于${lockTime}自动解冻！</div>
					<#elseif message?exists>
						<div class="error" style="margin-top:0;">${message?if_exists}</div>
					</#if>
					<form action="${rc.contextPath}/auth/login" method="POST" id='formLogin'>
					<ul class="info">
						<li class="clearfix title">
							<label>用户名</label>
							<div class="floatL rbg">
								<input type="text" name="username" value=""/>
							</div>
						</li>
						<li class="clearfix title">
							<label>密&nbsp;&nbsp;码</label>
							<div class="floatL rbg">
								<input type="password" name="password" value=""/>
							</div>
						</li>
						<li class="clearfix title last">
							<label>验证码</label>
							<div class="rbg floatL">
								<div class="clearfix" style="*zoom:0;">
									<input id="keyword" class="yzm" type="text" name="checkCode" value=""/>
									<div class="floatL yzm">
										<span><img src="${rc.contextPath}/auth/code" width="60px" height="30px" id='code'/></span>
										<a href="#" onClick="flushCode();">换一张！</a>
									</div>
								</div>
							</div>
						</li>
					</ul>
					<div class="clearfix passworad">
						<p class="flogt floatR"><a id="dd" href="javascript:void(0)">忘记密码？</a></p>
					</div>
					<div class="button">
						<a href="javascript:void(0);" title="" onClick="$('#formLogin').submit();">登&nbsp;&nbsp;录</a>
					</div>
				</div>
			</div>
		</div>
	</div>
</body>
</html>