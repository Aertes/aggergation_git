<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#include "/layouts/main.ftl"/>
<@html></@html>
<#macro style></#macro>
<#macro script>

</#macro>
<#macro content>
	<div class="detaillist">
		<form id="form_vehicle_update" action="${rc.contextPath}/vehicle/updatePrice" method="post" data-options="novalidate:true">
			<input  type="hidden"  name="id" value="${vehicle.id?if_exists}" ></input>
			<ul class="formlist">
				<li class="clearfix">
					<label>归属车系<em>*</em></label>
					${vehicle.series.name?if_exists}
				</li>
				<li class="clearfix">
					<label>车型名称<em>*</em></label>
					${vehicle.name?if_exists}
				</li>
				<li class="clearfix">
					<label>车型代码<em>*</em></label>
					${vehicle.code?if_exists}
				</li>
				<li class="clearfix floatselect">
					<label>车型状态<em>*</em></label>
					<select name="onsale" style="height: 30px; width: 200px;">
						<option value="1" <#if vehicle.onsale=='1'>selected</#if>>上架</option>
						<option value="0" <#if vehicle.onsale!='1'>selected</#if>>下架</option>
					</select>
				</li>
				<li class="clearfix">
					<label>车型价格<em></em></label>
	 			    <input  type="text" class="textbox" id="price" name="price"  value="${vehicle.price?if_exists}" maxlength="30" style="height: 30px; width: 180px;"></input>
				</li>
			</ul>
			<div class="btnsumbit">
				<a href="javascript:void(0)" class="easyui-linkbutton btnstyle" onClick="$('#form_vehicle_update').submit();">保 存</a>
				<a href="${rc.contextPath}/vehicle/index?series=${vehicle.series.id?if_exists}" class="easyui-linkbutton backbtn">返 回</a>
			</div>
		</form>
	</div>

</#macro>