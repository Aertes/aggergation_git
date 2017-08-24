<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#include "/layouts/main.ftl"/>
<@html></@html>
<#macro style></#macro>
<#macro script>
<script>
	$(function(){$.plugin.validate();});

	function vehicleSeriesName() {
	    var code = $("#vehicleSeriesCode").val();
        $("#vehicleSeriesName").html("");

	    if (null == code || "" == code) {
	        return;
		}

        $.getJSON(
            "${rc.contextPath}/intention/name/"+code,
				function(result){
                    $("#vehicleSeriesName").html(result.name);
				}
            );
	}
</script>
</#macro>
<#macro content>
	<div class="detaillist">
		<form id="form_intention_create"  class="validateCheck" action="${rc.contextPath}/intention/save" method="post" data-options="novalidate:true">
			<#include "/intention/_form.ftl"/>
			<div class="btnsumbit">
				<a href="javascript:void(0)" class="easyui-linkbutton btnstyle"onClick="$('#form_intention_create').submit();">添 加</a>
				<a href="${rc.contextPath}/intention/index" class="easyui-linkbutton backbtn">返 回</a>
			</div>
		</form>
	</div>
</#macro>