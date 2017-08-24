<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#assign tag=JspTaglibs["/WEB-INF/taglib/permission.tld"]>
	<ul class="formlist">
		<li class="clearfix">
			<label>所属大区<em>*</em></label>
			<span class="iname" id='parentName'>${organization.name?if_exists}</span>
			<input id="parentId" type="hidden" name="organization.id" value="${organization.id?if_exists}"/>
		</li>
		<li class="clearfix">
			<label>网点名称<em>*</em></label>
			<@spring.bind "dealer.name" />
			<input class="textbox" datatype='*' nullmsg='请输入网点名称' name="name" style="height: 30px; width: 180px;" value="${dealer.name?if_exists}"></input>
			<span class="Validform_checktip error"><@spring.showError/></span>
		</li>
		<li class="clearfix">
			<label>网点简称</label>
			<input class="textbox" name="alias" style="height: 30px; width: 180px;" value="${dealer.alias?if_exists}"></input>
		</li>
		<li class="clearfix">
			<label>网点编号<em>*</em></label>
			<@spring.bind "dealer.code" />
			<input class="textbox"  datatype='*' nullmsg="请输入网点编号" ajaxurl="${rc.contextPath}/dealer/checkCode?id=${dealer.id?if_exists}" type="text" name="code" style="height: 30px; width: 180px;" value="${dealer.code?if_exists}"></input>
			<span class="Validform_checktip error"><@spring.showError/></span>
		</li>
		<li class="clearfix">
			<label>小区经理</label>
			<select id="select_manager" name="manager.id" style="width:200px; height: 30px; float:left;" class="searchselect">
				<option value="">--请选择--</option>
				<#if managerList?if_exists>
					<#list managerList as manager>
						<option value="${manager.id?if_exists}" <#if dealer.manager.id==manager.id>selected="selected"</#if>>${manager.name?if_exists}</option>
					</#list>
				</#if>
			</select>
			<div class="Validform_checktip error"></div>
		</li>
		<li class="clearfix floatselect">
			<label>网点类型<em>*</em></label>
			<@tag.select type="dealer_type" attrs="datatype='*' nullmsg='请选择网点类型' errormsg='请选择网点类型' " name="type.id" defaultValue=dealer.type.id?default(0) />
			<@spring.bind "dealer.type" /><@spring.showError/>
			<span class="Validform_checktip error"></span>
		</li>
		<li class="clearfix">
			<label>合作媒体</label>
			<div class="floatL dx">
				<#if mediaList?if_exists>
					<#if dealerMediaIds?if_exists>
						<#list mediaList as media>
							<input class="mediatest" type="checkbox" name="mediaId" <#if dealerMediaIds?seq_contains(media.id)>checked="checked"</#if>  value="${media.id?if_exists}" /><span>${media.name?if_exists}</span>
						</#list>
						<#else>
						<#list mediaList as media>
							<input class="mediatest" type="checkbox" name="mediaId" value="${media.id?if_exists}" /><span>${media.name?if_exists}</span>
						</#list>
					</#if>
				</#if>
			</div>
		</li>
		<li class="clearfix">
			<label>网点地址<em>*</em></label>
			<@spring.bind "dealer.address" />
			<input class="textbox"  datatype="*" nullmsg="请输入网点地址"  type="text" name="address" style="height: 30px; width: 180px;" value="${dealer.address?if_exists}"></input>
			<span class="Validform_checktip error"><@spring.showError/></span>
		</li>
		<li class="clearfix">
			<label>网点电话<em>*</em></label>
			<@spring.bind "dealer.phone" />
			<input class="textbox"  datatype="*"  nullmsg="请输入网点电话" type="text" name="phone" style="height: 30px; width: 180px;" value="${dealer.phone?if_exists}"></input>
			<span class="Validform_checktip error"><@spring.showError/></span>
		</li>
		<li class="clearfix">
			<label>电子邮件<em>*</em></label>
			<@spring.bind "dealer.email" />
			<input class="textbox" datatype="email" errormsg="邮件格式不正确" nullmsg="请输入电子邮件" type="text" name="email" style="height: 30px; width: 180px;" value="${dealer.email?if_exists}"></input>
			<span class="Validform_checktip error"><@spring.showError/></span>
		</li>
		<li class="clearfix floatselect">
			<label>网点状态<em>*</em></label>
			<@tag.select type="record_status" name="status.id" attrs="datatype='*' nullmsg='请选择网点状态' errormsg='请选择网点状态' " defaultValue=dealer.status.id?default(1010) />
			<@spring.bind "dealer.status" /><@spring.showError/>
			<span class="Validform_checktip error"></span>
		</li>
	</ul>
