<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#include "/layouts/main.ftl"/>
<#import "/layouts/popup.ftl" as massage>
<@html></@html>
<#macro style>
	<style>
		.lineHeight30{
			*line-height:30px;
		}
	</style>
</#macro>
<#macro script>
</#macro>
<@massage.generalMessage />
<#macro content>
	<div class="tit clearfix">
		<h2>个人信息修改</h2>
	</div>
	<div class="editinfo">
		<form id="form_user_setting" action="${rc.contextPath}/user/doSetting" class="easyui-form formlist validateCheck" method="post" data-options="novalidate:true">
			<table cellpadding="5" class="clearfix tabadd">
				<tr>
					<td>
						<label>用户名</label>
					</td>
					<td>
						<input class="textbox lineHeight30" value="${user.name}" datatype='*' nullmsg='请输入用户名！' errormsg="请输入正确的用户名！" style="height: 30px; width: 200px;" name="name"/>
					</td>
				</tr>
				<tr>
					<td>
						<label>登录账号 <em></em>
						</label>
					</td>
					<td style="padding-left:15px">
						${user.username}
					</td>
				</tr>
				<tr>
					<td>
						<label>旧密码 <em></em>
						</label>
					</td>
					<td>
						<input id="oldPassword" class="textbox lineHeight30" value="${user.password}" type="password" style="height: 30px; width: 200px;" name="oldPassword" data-options="required:true" />
					</td>
				</tr>
				<tr>
					<td>
						<label>新密码 <em></em>
						</label>
					</td>
					<td>
						<input id="newPassowrd" class="textbox lineHeight30" name="newPassowrd" type="password" style="height: 30px; width: 200px;" />
						<label style="margin-left:0px;height:30px;color:gray;width:230px;">(必须是8至16位大小写字母和数字)</label>
					</td>
				</tr>
				<tr>
					<td>
						<label>确认新密码 <em></em>
						</label>
					</td>
					<td>
						<input id="rePassword" class="textbox lineHeight30" name="rePassword" type="password" datatype="" recheck="newPassowrd" errormsg="您两次输入的账号密码不一致！" style="height: 30px; width: 200px;" />
						<span class="Validform_checktip error"><@spring.showError/></span>
					</td>
				</tr>
			</table>
		</form>
		<div class="btnsumbit">
			<a href="javascript:void(0)" class="easyui-linkbutton btnstyle" onclick="submitForm();">确 认</a>
			<a href="${rc.contextPath}/home/index" class="easyui-linkbutton backbtn">返 回</a>
		</div>
	</div>
	<script type="text/javascript" src="${rc.contextPath}/js/popup.js"></script>
	<script type="text/javascript">
		$(function(){
			$.plugin.validate();
			
			$("#newPassowrd").blur(function(){
				var newPassowrd = $(this).val();
				if(newPassowrd && $.trim(newPassowrd).length > 0){
					var rePassword = $("#rePassword").val();
					if(newPassowrd != rePassword){
						$("#rePassword").next("span").html("两次密码输入不一致");
					}else{
						$("#rePassword").next("span").html("");
					}
				}
			});
			
			$("#rePassword").blur(function(){
				var rePassword = $(this).val();
				if(rePassword && $.trim(rePassword).length > 0){
					var newPassowrd = $("#newPassowrd").val();
					if(newPassowrd != rePassword){
						$("#rePassword").next("span").html("两次密码输入不一致");
					}else{
						$("#rePassword").next("span").html("");
					}
				}
			});
		});
		var submited = false;
		function submitForm(){
            debugger;
			if(submited){ return; }
			
			var rePassword = $("#rePassword").val();
			var newPassowrd = $("#newPassowrd").val();
			if((rePassword && $.trim(rePassword).length > 0) || (newPassowrd && $.trim(newPassowrd).length > 0)){
				if(newPassowrd != rePassword){
					$("#rePassword").next("span").html("两次密码输入不一致");
					return false;
				}else{
					$("#rePassword").next("span").html("");
				}
			}
			var params = $("#form_user_setting").serializeArray();
			submited = true;
			$.post("${rc.contextPath}/user/doSetting",params,function(data){
				general_message(data.code,data.message);
				submited = false;
			},"json");
		}
	</script>
</#macro>