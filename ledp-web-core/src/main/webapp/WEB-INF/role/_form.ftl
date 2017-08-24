<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#assign tag=JspTaglibs["/WEB-INF/taglib/permission.tld"]>
	<ul class="formlist">
		<li class="clearfix">
			<label>角色名称 <em>*</em></label>
			<input class="textbox" name="name" value="${role.name}" style="height: 30px; width: 180px;" datatype="*" nullmsg="请输入角色名称" errormsg="请输入正确的角色名称"></input>
			<@spring.bind "role.name" />
			<span class="Validform_checktip error"><@spring.showError/></span>
		</li>
		<li class="clearfix floatselect">
			<label>角色状态 <em>*</em></label>
			<@tag.select type="record_status"  name="status.id" attrs="datatype='*' nullmsg='请选择角色状态' errormsg='请选择角色状态' "  defaultValue=role.status.id?default(0)/>
			<@spring.bind "role.status" />
			<span class="Validform_checktip error"><@spring.showError/></span>
		</li>
		<li class="clearfix" style="height:410px;">
			<label>角色权限 <em>*</em></label>
			<div class="treeList floatL" style="margin:10px;" name="permissionList">
				  <ul id="tt" class="easyui-tree" data-options="url:'${rc.contextPath}/role/tree?roleId=${role.id}',method:'get',animate:false,checkbox:true,cascadeCheck: false"></ul>		
			</div>
		</li>
	</ul>
	<input type="hidden" name="checkeds" value="${checkeds?default("")}" id="checkeds"/>