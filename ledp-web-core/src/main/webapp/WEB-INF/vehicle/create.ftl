<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#include "/layouts/main.ftl"/>
<@html></@html>
<#macro style></#macro>
<#macro script>
	<script type="text/javascript">
		$(function(){
			$('#tt').tree({
				onClick: function(node){
					$("#parentId").val(node.id);
					$("#parentName").val(node.text);
					$("#name").attr('ajaxurl','${rc.contextPath}/vehicle/checkName?id=${vehicle.id?if_exists}&seriesId='+node.id);
					$("#code").attr('ajaxurl','${rc.contextPath}/vehicle/checkName?id=${vehicle.id?if_exists}&seriesId='+node.id);
				},onLoadSuccess :function(node){
					node=$('#tt').tree('find',$('#parentId').val());
					if(node!=null){
						$('#tt').tree('expandTo', node.target).tree('select', node.target);
					}
				}
			});
		});
	</script>
	
	<script type="text/javascript">
		$(function(){
			$.plugin.validate();	
		});
	</script>
</#macro>
<#macro content>
	<div class="floatL treeList">
		  <ul id="tt" class="easyui-tree" data-options="url:'${rc.contextPath}/vehicle/tree',method:'get',animate:true"></ul>		
	</div>
	<div class="detaillist floatL">
		<form id="form_vehicle_create" action="${rc.contextPath}/vehicle/save" class="validateCheck" method="post" data-options="novalidate:true">
			<#include "/vehicle/_form.ftl"/>
			<div class="btnsumbit">
				<a href="javascript:void(0)" class="easyui-linkbutton btnstyle" onClick="$('#form_vehicle_create').submit();">添 加</a>
				<a href="${rc.contextPath}/vehicle/index" class="easyui-linkbutton backbtn">返 回</a>
			</div>
		</form>
	</div>
</#macro>