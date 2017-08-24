<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#assign tag=JspTaglibs["/WEB-INF/taglib/permission.tld"]>
<#include "/wechat/layouts/main.ftl"/>
<@html></@html>
<#macro style>
<link rel="stylesheet" href="${rc.contextPath}/wechat/css/category.css" />
<style>
	.public table{
		margin: 20px auto;
	}
	.publicinfo{
		width: 53%;
		margin: 10px auto;
	}
	.publicinfo p{
		text-align: left;
	}
	.infotitle{
		text-align: left;
		margin: 10px auto;
	}
	.publicinfo th, td{
		border:none;
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
					<span>公众号管理</span>
				</li>
			</ul>
		</div>
		<div class="createActiveFrom public">
			<form action="${rc.contextPath}/wechat/publicno/doAuthorize" method="POST" id="doAuthorizeForm">
				<table>
					<tr>
						<td><div style="line-height:30px;height:30px;">公众号 AppID(应用ID)：</div></td>
						<td colspan='2'><input type="text" style="width: 300px;" name="appid" value="${appid}" id='appid'/></td>
					</tr>
					<tr>
						<td><div style="line-height:30px;height:30px;margin-top:10px;">公众号AppSecret(应用密钥)：</div></td>
						<input type="hidden" name="reAuth" value="${reAuth}" id='reAuth'/>
						<td><div style="margin-top:10px;"><input type="text" style="width: 300px;" name="app_secret" value="${app_secret}" id='app_secret'/></div></td>
						<td style="text-align:left;"><div style="margin:10px 0 0 10px;"><a href="#" class="redButton" onClick="javascript:doAuthorize();" style="height:30px;line-height:30px;display:block;width:120px;padding:0;text-align:center;">下一步</a></div></td>
					</tr>
				</table>
			</form>
			<div class="publicinfo">
				<p class="infotitle">如何找到AppleID和AppSecret？</p>
				<p>1、登录入微信公众号平台；<a href="https://mp.weixin.qq.com/">点击登录</a></p>
				<p>2、找到左边导航栏最下方的开发者中心，点击进入；</p>
				<p>3、找到当前页面下开发者ID下的AppID，对应的一串字符拷贝过来即可。</p>
				<p>4、找到当前页面下开发者ID下的AppSecret，完整显示后的一串字符拷贝过来即可。</p>
			</div>
		</div>
</#macro>
<#macro script>
	<script type="text/javascript" src="${rc.contextPath}/wechat/js/report/highchad" ></script>
	<script type="text/javascript" src="${rc.contextPath}/wechat/js/layer/layer.js"></script>
	<script type="text/javascript" src="${rc.contextPath}/wechat/js/ElasticLayer.js"></script>
	<script type="text/javascript">
		function doAuthorize(){
			if($('#appid').val()==''){
				layer.alert("请输入公众号 AppID(应用ID)！",{time:1000});
				return;
			}
			if($('#app_secret').val()==''){
				layer.alert("请输入公众号AppSecret(应用密钥)！",{time:1000});
				return;
			}
			$('#doAuthorizeForm').submit();
		}
		
		$(function(){
			<#if message?exists>
				layer.alert("${message}",{time:1000});
			</#if>
			<#if reAuth?exists && (reAuth=="confirm")>
            	layer.confirm("公众号 AppID已被授权，是否需要重新授权？",{
					yes : function(index){
						layer.close(index);
						$("#reAuth").val("true");
						$('#doAuthorizeForm').submit();
					},
					no : function(index){
						layer.close(index);
						$("#reAuth").val("false");
					}
				});
			</#if>
		})
		
	</script>
</#macro>