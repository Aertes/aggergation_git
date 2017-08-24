<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#include "/layouts/main.ftl"/>
<@html></@html>
<#macro style></#macro>
<#macro content>
	<form action="${rc.contextPath}/pagination/submitValidate" method="post" class="validateCheck">
			<table>
				<tr>
					<td>
						<label>姓名：</label>
					</td>
					<td>
						<input type="text" datatype="*" nullmsg="请输入教学班名称" name="username" id="username"/>
						<div class="Validform_checktip"></div>
					</td>
				</tr>
				<tr>
					<td>
						<label>性别：</label>
					</td>
					<td>
						<input type="text" />
					</td>
				</tr>
				<tr>
					<td>
						<label>年龄：</label>
					</td>
					<td>
						<input type="text" />
					</td>
				</tr>
				<tr>
					<td>
						<label>电话号码：</label>
					</td>
					<td>
						<input type="text" />
					</td>
				</tr>
				<tr>
					<td>
						<label>邮箱：</label>
					</td>
					<td>
						<input type="text" />
					</td>
				</tr>
				<tr>
					<td>
						<label>家庭住址：</label>
					</td>
					<td>
						<input type="text" />
					</td>
				</tr>
			</table>
			<input type="submit" value="提交" />
		</form>
</#macro>
<#macro script>
	<script type="text/javascript">
		$(function(){
			$.plugin.validate();	
		});
	</script>
</#macro>