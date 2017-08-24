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
						if(node.isActive=='active'){
							$("#parentId").val(node.id);
							$("#parentName").html(node.text);
							manager(node.id);
						}
					}
				},
				onBeforeSelect : function(node){
					if(node){
						if(node.option==0){
							return false;
						}
					}
					
				},
				onLoadSuccess:function(node, data){
					if(!node){
						node = $('#tt').tree('getRoot');
					}
					var childList = $('#tt').tree('getChildren', node.target);
					if(node!=null){
						if(node.option==1){
							$('#tt').tree('expandTo', node.target).tree('select', node.target);
						}else{
							for(var i = 0; i < childList.length; i++){
								var childNode = childList[i];
								if(childNode.option == 1){
									node = $('#tt').tree('find',childNode.id)
									break ;
								}
							}
							node=$('#tt').tree('find',$('#parentId').val());
							$('#tt').tree('expandTo', node.target).tree('select', node.target);
						}
					}				
				}
			});
		});
		function manager(orgId){
			if(orgId){
				$.ajax( {    
					    url:'${rc.contextPath}/dealer/manager/'+ orgId,    
					    data:{},    
					    type:'post',    
					    cache:false,    
					    success:function(data) {  
					    	var $select_manager = $('#select_manager');
					    	var $option = "<option value=''>--请选择--</option>";
					    	$.each(data,function(i,value){
					    		$option += "<option value='"+ value.id +"'>"+ value.name +"</option>";
					    	});
					    	$select_manager.html($option);
					     },    
					     error : function() {    
				          	alert("获取小区经理异常！");    
					     }    
				});  
			}
		}
	</script>
</#macro>
<#macro content>
	<div class="floatL treeList">
		<ul id="tt" class="easyui-tree" data-options="url:'${rc.contextPath}/organization/tree?createDe=create',method:'get',animate:true"></ul>	
	</div>
	<div class="floatL detaillist">
		<form id="form_dealer_update" class="validateCheck"  action="${rc.contextPath}/dealer/edit" method="post" data-options="novalidate:true">
			<input name="dealerId" type="hidden" value="${dealer.id}" />
			<#include "/dealer/_form.ftl"/>
			<div class="btnsumbit">
				<a href="javascript:void(0)" class="easyui-linkbutton btnstyle" onclick="$('#form_dealer_update').submit();">保 存</a>
				<a href="${rc.contextPath}/dealer/index" class="easyui-linkbutton backbtn">返 回</a>
			</div>
		</form>
	</div>
</#macro>