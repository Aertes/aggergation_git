<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#assign tag=JspTaglibs["/WEB-INF/taglib/permission.tld"]>
	<ul class="formlist">
		<li class="clearfix">
			<label>车型代码 <em>*</em></label>
            <input class="textbox" id="vehicleSeriesCode" name="vehicleSeriesCode" ajaxurl="${rc.contextPath}/intention/check" value="${intention.vehicleSeriesCode}" onkeyup="vehicleSeriesName()" style="height: 30px; width: 180px;" datatype="*" nullmsg="请输入车型代码" errormsg="请输入正确的车型代码"/>
			<@spring.bind "intention.vehicleSeriesCode" />
            <span class="Validform_checktip error"><@spring.showError/></span>
		</li>
        <li class="clearfix floatselect">
            <label>车型名称 </label>
            <span class="iname" id="vehicleSeriesName"></span>
        </li>
	</ul>
