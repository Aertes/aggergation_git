<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
	<input type="hidden" name="id" value="${customer.id}"/>
	<ul class="formlist">
		<li class="clearfix">
			<label>客户名称<em>*</em></label>
			<input class="textbox" datatype='*' nullmsg='请输入客户名称！' errormsg="请输入正确的客户信息！" name="name" style="height: 30px; width: 200px;" value="${customer.name}"></input>
			<span class="Validform_checktip error "><@spring.showError/></span>
		</li>
		<li class="clearfix">
			<label>电话号码<em>*</em></label>
			<input class="textbox" style="height: 30px; width: 200px;" value="${customer.phone}" disabled="disabled"></input>
		</li>
		<li class="clearfix">
			<label>客户邮件<em>*</em></label>
			<input class="textbox" datatype="e" nullmsg="请输入客户邮箱"  name="email" style="height: 30px; width: 200px;" value="${customer.email}"></input>
			<span class="Validform_checktip error "><@spring.showError/></span>
		</li>
		<li class="clearfix">
			<label>省份<em>*</em></label>
			<input class="textbox" datatype="*" nullmsg="请输入省份"  name="province" style="height: 30px; width: 200px;" value="${customer.province}"></input>
			<#-- <select id="province" name="province.id" style="width:200px; height: 30px;float: left;" class="searchselect" datatype='*' nullmsg='请选择省份'>
				<option value="">--请选择--</option>
				<#list regionList as region>
					<option value="${region.id}">${region.zh_name}</option>
				</#list>
			</select>
			-->
			<span class="Validform_checktip error "><@spring.showError/></span>
		</li>
		<li class="clearfix">
			<label>城市<em>*</em></label>
			<input class="textbox" datatype="*" nullmsg="请输入城市"  name="city" style="height: 30px; width: 200px;" value="${customer.city}"></input>
			<#--<select id="city" name="city.id" style="width:200px; height: 30px;float: left;" class="searchselect" datatype='*' nullmsg='请选择城市'>
				<option value=''>--请选择--</option>
			</select>-->
			<span class="Validform_checktip error "><@spring.showError/></span>
		</li>
		<li class="clearfix">
			<label>区县<em>*</em></label>
			<input class="textbox" datatype="*" nullmsg="请输入区县"  name="district" style="height: 30px; width: 200px;" value="${customer.district}"></input>
			<#--<select id="district" name="district.id" style="width:200px; height: 30px;float: left;" class="searchselect" datatype='*' nullmsg='请选择区县'>
				<option value=''>--请选择--</option>
			</select>-->
			<span class="Validform_checktip error "><@spring.showError/></span>
		</li>
		<li class="clearfix">
			<label>详细地址<em>*</em></label>
			<input class="textbox" datatype="*" nullmsg="请输入详细地址"  name="address" style="height: 30px; width: 200px;" value="${customer.address}"></input>
			<span class="Validform_checktip error "><@spring.showError/></span>
		</li>
	</ul>
