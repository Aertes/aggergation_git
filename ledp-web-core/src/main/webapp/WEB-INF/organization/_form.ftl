<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#assign tag=JspTaglibs["/WEB-INF/taglib/permission.tld"]>
	<ul class="formlist">
		<li class="clearfix">
			<label>上级机构 <em>*</em></label>
			<@spring.bind "organization.parent" />
			<input id="parentId" type="hidden" name="parent.id" value="${organization.parent.id}"/>
			<span class="iname" id="parentName0">${organization.parent.name}</span>
			<input id="parentName" type="hidden" name="parent.name" value="${organization.parent.name}"/>
			<span class="Validform_checktip error"><@spring.showError/></span>
		</li>
		<li class="clearfix">
			<label>组织名称 <em>*</em></label>
			<@spring.bind "organization.name" />
			<input class="textbox" type="text" id="name" name="name" value="${organization.name}" style="height: 30px; width: 180px;" datatype="*" nullmsg="请填写组织名称"></input>
			<span class="Validform_checktip error "><@spring.showError/></span>
		</li>
		<li class="clearfix">
			<label>组织别名<em>&nbsp;</em></label>
			<input class="textbox" type="text" name="alias" value="${organization.alias}"  style="height: 30px; width: 180px;"></input>
		</li>
		<li class="clearfix">
			<label>组织负责人 </label>
			<#--<input id="parentId" type="hidden" name="manager.id" value="1${organization.manager.id}"/>-->
			<select id="select_head" name="manager.id" style="width:200px; height: 30px; float:left;"  class="searchselect">
				<option value="">--请选择--</option>
				<#if managerList?if_exists>
					<#list managerList as user>
						<option value="${user.id?if_exists}" <#if organization.manager.id==user.id>selected="selected"</#if>>${user.name}</option>
					</#list>
				</#if>
			</select>
			<@spring.bind "organization.manager" /><@spring.showError/>
			<span class="Validform_checktip error"></span>
		</li>
		<li class="clearfix organtype">
			<label>组织状态 <em>*</em></label>
			<@tag.select type="record_status" name="status.id" attrs="datatype='*' nullmsg='请选择组织状态' errormsg='请选择组织状态' " defaultValue=organization.status.id/>
			<@spring.bind "organization.status" /><@spring.showError/>
			<span class="Validform_checktip error"></span>
		</li>
		<li class="clearfix" style="*height:100px">
			<label>备注 </label>
			<textarea class="textarea" name="comment">${organization.comment}</textarea>	
		</li>
	</ul>