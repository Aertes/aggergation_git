<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>

<input  type="hidden"  name="id"  value="${media.id?if_exists}" ></input>
<input  type="hidden"  name="serial"  value="${media.serial?if_exists}" ></input>

	<ul class="formlist">
		<li class="clearfix">
			<label>媒体名称 <em>*</em></label>
			<input class="easyui-textbox inputlist"  maxlength="30" type="text" name="name" style="height: 30px; width: 200px;" datatype="*" nullmsg="媒体名称" value="${media.name?if_exists}" ></input>
			<div class="Validform_checktip"></div>
		</li>
		<li class="clearfix">
			<label>媒体代码 <em>*</em></label>
			<input class="easyui-textbox inputlist" maxlength="30" type="text" name="code" style="height: 30px; width: 200px;"  datatype="*" nullmsg="媒体代码 " value="${media.code?if_exists}" ></input>
			<div class="Validform_checktip"></div>
		</li>
		<li class="clearfix">
			<label>媒体状态 <em>*</em></label>
			<div class="floatL dx">
				<input type="radio"   name="start" value="active"   class="mediatest" checked="checked"><span>启用</span>
				<input type="radio" name="start" value="inactive"  class="mediatest"<#if media.status?if_exists.code?if_exists == "inactive"> checked="checked" </#if> ><span>禁用</span>
			</div>
		</li>
	</ul>
