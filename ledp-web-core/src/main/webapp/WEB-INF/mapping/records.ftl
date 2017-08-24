<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<label>${type.name}名称 <em>*</em></label>
<select name="record" style="width:200px; height: 30px; line-height:30px;">
<#list records as record>
	<option value="${record.id}">${record.name}</option>
</#list>
</select>