<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#include "/layouts/main.ftl"/>
<@html></@html>
<#macro style></#macro>
<#macro script>
	<script type="text/javascript">
		$(function(){
			$.plugin.validate();	
		});
		$(function(){
			$('#tt').tree({
				onClick: function(node){
					if(node.option==1){
						$("#parentId").val(node.id);
						$("#parentName0").html(node.text);
						$("#parentName").val(node.text);
						organizationHead(node.id);
					}
				},
				onBeforeSelect : function(node){
					if(node.option==0){
						return false;
					}
				},
				onLoadSuccess:function(node, data){
					if(!node){
						node = $('#tt').tree('getRoot');
					}
					if(node!=null){
							$('#tt').tree('expandTo', node.target).tree('select', node.target);
					}				
				}
			});
		});
		
		function organizationHead(orgId){
			if(orgId){
				$.ajax( {    
						url:'${rc.contextPath}/organization/head/'+ orgId,    
						data:{},    
						type:'post',    
						cache:false,    
						success:function(data) {  
							var $select_manager = $('#select_head');
							var $option = "<option value=''>--请选择--</option>";
							$.each(data,function(i,value){
								$option += "<option value='"+ value.id +"'>"+ value.name +"</option>";
							});
							$select_manager.html($option);
						 },    
						 error : function() {    
							alert("获取机构负责人异常！");    
					 }    
				});  
			}
		}
		
	</script>
</#macro>
<#macro content>
	<div class="floatL treeList">
		<ul id="tt" class="easyui-tree" data-options="url:'${rc.contextPath}/organization/tree?checked=${organization.parent.id}',method:'get',animate:true"></ul>		
	</div>
	<div class="floatL detaillist">
		<form id="form_organization_update" action="${rc.contextPath}/organization/edit" method="post" data-options="novalidate:true" class="validateCheck">
			<input type="hidden" name="id" value="${organization.id}"/>
			<#include "/organization/_form.ftl"/>
			<div class="btnsumbit">
				<a href="javascript:void(0)" class="easyui-linkbutton btnstyle" onClick="$('#form_organization_update').submit();">保 存</a>
				<a href="${rc.contextPath}/organization/index" class="easyui-linkbutton backbtn">返 回</a>
			</div>
		</form>
	</div>
</#macro>