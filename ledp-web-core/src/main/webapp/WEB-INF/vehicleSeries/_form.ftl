<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#assign tag=JspTaglibs["/WEB-INF/taglib/permission.tld"]>
	<ul class="formlist detaillist ollright">
	
		<li class="clearfix">
			<label style="*position:relative;">车系名称<em>*</em></label>
			<input class="textbox" type="text" maxlength="30" name="name" style="height: 30px; width: 180px;" datatype="*" nullmsg="车系名称" ajaxurl="${rc.contextPath}/vehicleSeries/checkName?id=${vehicleSeries.id?if_exists}"  value="${vehicleSeries.name?if_exists}" ></input>
			<@spring.bind "vehicleSeries.name" />
			<span class="Validform_checktip error"><@spring.showError/></span>
		</li>
		<li class="clearfix">
			<label>车系代码<em>*</em></label>
			<input class="textbox" type="text" maxlength="30" name="code" style="height: 30px; width: 180px;" datatype="*" nullmsg="车系代码" ajaxurl="${rc.contextPath}/vehicleSeries/checkCode?id=${vehicleSeries.id?if_exists}"  value="${vehicleSeries.code?if_exists}" />
			<@spring.bind "vehicleSeries.code" />
			<span class="Validform_checktip error"><@spring.showError/></span>
		</li>
		<li class="clearfix floatselect">
			<label>车系状态<em>*</em></label>
			<@tag.select type="record_status"   name="status.id" attrs="datatype='*' nullmsg='请选择车系状态' errormsg='请选择车系状态' " defaultValue=vehicleSeries.status.id?default(0) />
			<@spring.bind "vehicleSeries.status" />
			<span class="Validform_checktip error" style="float:none;"><@spring.showError/></span>
		</li>
	</ul>
