<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#assign tag=JspTaglibs["/WEB-INF/taglib/permission.tld"]>

	<ul class="formlist">
		<li class="clearfix">
			<label>归属车系<em>*</em></label>
			<@spring.bind "vehicle.series" />
			<input id="parentId" type="hidden" name="series.id" value="${vehicle.series.id?if_exists}"/>
			<input  class="textbox" name="series.name"  style="height: 30px; width: 180px;"  disabled="true" type="text"  id='parentName'  value="${vehicle.series.name?if_exists}"/>
			
			<span class="Validform_checktip error"><@spring.showError/></span>
		</li>
		<li class="clearfix">
			<label>车型名称<em>*</em></label>
 			    <input  type="text" class="textbox" id="name" name="name" maxlength="30" datatype="*" nullmsg="车型名称" ajaxurl="${rc.contextPath}/vehicle/checkName?id=${vehicle.id?if_exists}&seriesId=${vehicle.series.id?if_exists}" value="${vehicle.name?if_exists}" style="height: 30px; width: 180px;"></input>
				<@spring.bind "vehicle.name" />
				<span class="Validform_checktip error"><@spring.showError/></span>
		</li>
		<li class="clearfix">
			<label>车型代码<em>*</em></label>
			<input maxlength="30" type="text" class="textbox" id="code" name="code"  datatype="*" nullmsg="车型代码" ajaxurl="${rc.contextPath}/vehicle/checkCode?id=${vehicle.id?if_exists}&seriesId=${vehicle.series.id?if_exists}" value="${vehicle.code?if_exists}"  style="height: 30px; width: 180px;" />
				<@spring.bind "vehicle.code" />
				<span class="Validform_checktip error"><@spring.showError/></span>
		</li>
		<li class="clearfix floatselect">
			<label>车型状态<em>*</em></label>
			<div class="floatL">
				<@tag.select type="record_status" name="status.id" attrs="datatype='*' nullmsg='请选择车型状态' errormsg='请选择车型状态' " defaultValue=vehicle.status.id?default(0) />
				<@spring.bind "vehicle.status" />
				<span class="Validform_checktip error"> <@spring.showError/></span>
			</div>
		</li>
		<li class="clearfix">
			<label>车型价格<em></em></label>
 			    <input  type="text" class="textbox" id="price" name="price"  value="${vehicle.price?if_exists}" maxlength="30" style="height: 30px; width: 180px;"></input>
				<@spring.bind "vehicle.price" />
				<span class="Validform_checktip error"><@spring.showError/></span>
		</li>
	</ul>