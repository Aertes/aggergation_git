<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#include "/layouts/main.ftl"/>
<@html></@html>
<#macro style></#macro>
<#macro script>
	<script type="text/javascript">
		$(function(){
			$.plugin.validate();	
			$('#tt').tree({
				onClick: function(node){
					$("#parentId").val(node.id);
					$("#parentName").val(node.text);
				},onLoadSuccess :function(node){
					node=$('#tt').tree('find',$('#parentId').val());
					if(node!=null){
						$('#tt').tree('expandTo', node.target).tree('select', node.target);
					}
				}
			});
		});
	</script>

</#macro>
<#macro content>
	<div class="floatL treeList">
		  <ul id="tt" class="easyui-tree" data-options="url:'${rc.contextPath}/vehicle/tree',method:'get',animate:true"></ul>		
	</div>
	
	<div class="detaillist floatL">
		<form id="form_vehicle_update" class="validateCheck" action="${rc.contextPath}/vehicle/edit" method="post" data-options="novalidate:true">
			<input  type="hidden"  name="id" id="_id0"  value="${vehicle.id?if_exists}" ></input>
			<#include "/vehicle/_form.ftl"/>
			<div class="btnsumbit">
				<a href="javascript:void(0)" class="easyui-linkbutton btnstyle" onClick="$('#form_vehicle_update').submit();">保 存</a>
				<a href="${rc.contextPath}/vehicle/index?series=${vehicle.series.id?if_exists}" class="easyui-linkbutton backbtn">返 回</a>
			</div>
		</form>
	</div>

</#macro>