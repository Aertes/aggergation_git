<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#assign tag=JspTaglibs["/WEB-INF/taglib/permission.tld"]>
	<ul class="formlist">
		<li class="clearfix" id="userOrginazation">
			<label>归属机构 <em>*</em></label>
			<#if createType=="org">
				<span class="iname" id="orgName0">${user.org.name}</span>
				<input type="hidden" id="orgId" name="org.id" value="${user.org.id}"/>
				<input type="hidden" id="orgName" name="org.name" value="${user.org.name}"/>
			</#if>
			<#if createType=="dealer">
				<span class="iname" id="orgName0">${user.dealer.name}</span>
				<input type="hidden" id="orgId" name="dealer.id" value="${user.dealer.id}"/>
				<input type="hidden" id="orgName" name="dealer.name" value="${user.dealer.name}"/>
			</#if>
			
		</li>
		<li class="clearfix">
			<@spring.bind "user.name" />
			<label>用户名称 <em>*</em></label>
			<input class="textbox" name="name" value="${user.name}" style="height: 30px; width: 180px;" datatype="*" nullmsg="请输入用户姓名" errormsg="请输入正确的用户姓名"></input>
			<span class="Validform_checktip error"><@spring.showError/></span>
		</li>
		<li class="clearfix">
			<@spring.bind "user.code" />
			<label>用户编号 <em>*</em></label>
			<input class="textbox" name="code" value="${user.code}" style="height: 30px; width: 180px;"  datatype="*" nullmsg="请输入用户编号" errormsg="请输入正确的用户编号"></input>
			<span class="Validform_checktip error"><@spring.showError/></span>
		</li>
		<li class="clearfix">
		<@spring.bind "user.username" />
			<label>登录账号 <em>*</em></label>
			<input class="textbox" name="username" value="${user.username}" style="height: 30px; width: 180px;"  datatype="*" nullmsg="请输入登陆账号" errormsg="请输入正确的登陆账号"></input>
			<span class="Validform_checktip error"><@spring.showError/></span>
		</li>
		<li class="clearfix">
		<@spring.bind "user.password" />
			<label>登录密码 <em>*</em></label>
			<input class="textbox" type="password" name="password"  datatype="*8-16" nullmsg="请输入登录密码" errormsg="请输入正确的登录密码" value="${user.password}" style="height: 30px; width: 180px;"></input>
			<span class="easyright">(必须是8至16位大小写字母和数字)</span>
			<span class="Validform_checktip error"><@spring.showError/></span>
		</li>
		<li class="clearfix floatselect">
		<@spring.bind "user.role" />
			<label>用户角色 <em>*</em></label>
			<select class="searchselect" name="role.id" style="width: 200px; height: 30px; line-height: 30px;" datatype="*" nullmsg="请选择用户角色" errormsg="请选择用户角色" >
				<option value="">---请选择---</option>
				<#list roles as role>
					<#if user.role?exists && role.id==user.role.id>
						<option value="${role.id}" selected>${role.name}</option>
					<#else>
						<option value="${role.id}">${role.name}</option>
					</#if>
				</#list>
			</select>
			<span class="Validform_checktip error"><@spring.showError/></span>
		</li>
		<li class="clearfix floatselect">
			<label>用户状态 <em>*</em></label>
			<@spring.bind "user.status" />
			<@tag.select type="record_status" attrs='datatype="*" nullmsg="请选择用户状态" errormsg="请选择用户状态" ' name="status.id" defaultValue=user.status.id/>
			<span class="Validform_checktip error" style="float:none"><@spring.showError/></span>
		</li>
		<li class="clearfix">
		<@spring.bind "user.phone" />
			<label>手机号码 <em>*</em></label>
			<input class="textbox" datatype="*" nullmsg="请输入手机号码" errormsg="请输入手机号码"  name="phone" value="${user.phone}" style="height: 30px; width: 180px;"></input>
			<span class="Validform_checktip error" style="float:none"><@spring.showError/></span>
		</li>
		<li class="clearfix">
		<@spring.bind "user.email" />
			<label>电子邮箱 <em>*</em></label>
			<input class="textbox" datatype="email" nullmsg="请输入电子邮箱" errormsg="请输入正确的电子邮箱" name="email" value="${user.email}" style="height: 30px; width: 180px;"></input>
			<span class="Validform_checktip error"><@spring.showError/></span>
		</li>
	</ul>