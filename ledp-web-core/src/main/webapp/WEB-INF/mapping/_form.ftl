<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
	<ul class="formlist">
		<li class="clearfix">
			<label>代码类型 <em>*</em></label>
			<span class="iname" id="typeName1">${mapping.type.name}</span>
			<input type="hidden" id="typeId" name="type.id" value="${mapping.type.id}"/>
			<input type="hidden" id="typeName" name="type.name" value="${mapping.type.name}" />
		</li>
		<li class="clearfix" id="records">
		</li>
		<li class="clearfix">
			<label>东雪代码 <em>*</em></label>
			<input class="textbox" name="code" value="${mapping.code}" style="height: 30px; width: 200px;" datatype="*" nullmsg="请输入东雪代码" errormsg="请输入正确的东雪代码" ></input>
			<@spring.bind "mapping.code" />
			<span class="Validform_checktip error"><@spring.showError/></span>
		</li>
		<li class="clearfix">
			<label>官网代码 <em>*</em></label>
			<input class="textbox" type="text" name="code1" value="${mapping.code1}" style="height: 30px; width: 200px;" datatype="*" nullmsg="请输入官网代码" errormsg="请输入正确的官网代码" ></input>
			<@spring.bind "mapping.code1" />
			<span class="Validform_checktip error"><@spring.showError/></span>
		</li>
		<li class="clearfix">
			<label>易车代码<em>*</em></label>
			<input class="textbox" type="text" name="code3" value="${mapping.code3}" style="height: 30px; width: 200px;" datatype="*" nullmsg="请输入易车代码" errormsg="请输入正确的易车代码" ></input>
			<@spring.bind "mapping.code3" />
			<span class="Validform_checktip error"><@spring.showError/></span>
		</li>
		<li class="clearfix">
			<label>汽车之家代码<em>*</em></label>
			<input class="textbox" type="text" name="code2" value="${mapping.code2}" style="height: 30px; width: 200px;" datatype="*" nullmsg="请输入汽车之家代码" errormsg="请输入正确的汽车之家代码" ></input>
			<@spring.bind "mapping.code2" />
			<span class="Validform_checktip error"><@spring.showError/></span>
		</li>
	</ul>
