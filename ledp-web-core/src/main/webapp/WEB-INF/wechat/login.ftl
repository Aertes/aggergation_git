<!DOCTYPE html>
<html>
	<head>
		<meta charset="utf-8" />
		<title>东风雪铁龙SCRM微信矩阵平台</title>
		<link rel="stylesheet" href="${rc.contextPath}/wechat/css/bootstrap.min.css" />
		<link rel="stylesheet" href="${rc.contextPath}/wechat/css/Font-Awesome-3.2.1/css/font-awesome.css" />
		<link rel="stylesheet" href="${rc.contextPath}/wechat/css/commom.css" />
		<link rel="stylesheet" href="${rc.contextPath}/wechat/css/color.css" />
		<script src='${rc.contextPath}/wechat/assets/js/jquery-2.0.3.min.js'></script>
		<script src="${rc.contextPath}/wechat/assets/js/bootstrap.min.js"></script>
		<script type="text/javascript">
			if("ontouchend" in document) document.write("<script src='${rc.contextPath}/wechat/assets/js/jquery.mobile.custom.min.js'>"+"<"+"/script>");
		</script>
		<script type="text/javascript">
			function flushCode(){
				$('#code').attr('src','${rc.contextPath}/wechat/auth/code?time='+new Date().getTime())
			}
			function clearText(id){
				$('#'+id).val('');
			}
		</script>
	</head>
	<body>
		<img class="bg" src="${rc.contextPath}/wechat/img/login/back.jpg" height="100%" width="100%" />
		<nav style="height:70px;">
			<div class="floatL logo">
				<img src="${rc.contextPath}/wechat/img/login/logo.png" />
			</div>
			<div class="floatR smallLogo">
				<img src="${rc.contextPath}/wechat/img/login/smallLogo.png" />
			</div>
			<div class="floatR"></div>
		</nav>
		<section class="login_content">
			
			<div  class="backImg">
				<div class="lolgin">
					<form action="${rc.contextPath}/wechat/auth/login" method="POST">
						<div class="textAlignC">
							<#if message?exists>
								<p class="btn1">${message?if_exists}</p>
							
							<#else>
								<br/>
							</#if>
						</div>
						<div class="borderL">
							<label class="icon-user form_icon1"></label>
							<input type="text"  name="username" id="username" placeholder="用户名" />
							<i class="icon-remove-sign form_icon2 colorR" onClick="clearText('username');"></i>
						</div>
						<div  class="borderL">
							<label class="icon-lock form_icon1"></label>
							<input type="password" name="password" id="password" placeholder="密码" value="" />
							<i class="icon-remove-sign form_icon2 colorR" onClick="clearText('password');"></i>
						</div>
						<div class="borderL">
							<label class="icon-info-sign form_icon1"></label>
							<input type="text" name="checkCode" id="checkCode" placeholder="验证码" />
							<i class="check_img"></i>
							<img src="${rc.contextPath}/wechat/auth/code" width="60px" height="30px" id='code'/>
							<i class="icon-refresh form_icon2" onClick="flushCode();" style="cursor:pointer;"></i>
						</div>
						<div class="borderL">
							<input type="submit" value="登 录" class="btn2" style="border:none;" />
						</div>
						<div class="borderM" style="position:relative">
							<a href="javascript:void(0)" class="floatR forgetPassword">忘记密码?</a>
							<div style="display:none;right: -300px;line-height: 20px;width: 280px;position: absolute;background: #fff;top: -30px;border: 1px solid #d9d9d9;padding: 10px;">
								<p class="longinsansan">
									<i class="loginHoverSan"></i>
								</p>
								<p>
								如您忘记了登陆密码，可以将身份认证信息（如网点编码）
								发送到feixiaojing@dfhtkg.com.cn，申请重置，
								如有疑问请致电：027-84300095
								</p>
							</div>
						</div>
					</form>
				</div>
			</div>
		</section>
		<script>
			$(function(){
				$(".forgetPassword").mouseover(function(){
					$(this).next().css("display","block");
				});
				
				$(".forgetPassword").mouseout(function(){
					$(this).next().css("display","none");
				});
			});
		</script>
	</body>
</html>
