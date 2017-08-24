<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#include "/layouts/main.ftl"/>
<@html></@html>
<#macro style></#macro>
<#macro script>
	<script type="text/javascript">
		$(function(){
			$('#tt').tree({
				onClick: function(node){
					$("#typeId").val(node.id);
					$("#typeName").val(node.text);
					$("#typeName1").html(node.text);
					loadRecords(node.id);
				},onLoadSuccess :function(node){
					node=$('#tt').tree('find',$('#typeId').val());
					if(node!=null){
						$('#tt').tree('expandTo', node.target).tree('select', node.target);
					}
				}
			});
			loadRecords(${mapping.type.id});
			$.plugin.validate();	
		});
		
		function loadRecords(typeId){
			$.ajax({
			    url:"${rc.contextPath}/mapping/records/"+typeId,
				success:function(json){
					$("#records").html(json);
				}
			});
		}
		
		function submit(){
			var typeId = $("#typeId").val();
			var recordId = $("select[name='record']").val();
			$.ajax({
			    url:"${rc.contextPath}/mapping/validate",
			    data:{
			    	"typeId":typeId,
			    	"recordId":recordId
			    },
				success:function(json){
					if(json.code == true){//验证通过
						$('#form_mapping_create').submit();
					}else{
						alert("已存在映射关系，不允许重复添加");
					}
				},
				error:function(){
					alert("请示异常");
				}
			});
		}
		
	</script>
</#macro>
<#macro content>
	<div class="floatL treeList">
		  <ul id="tt" class="easyui-tree" data-options="url:'${rc.contextPath}/mapping/type',method:'get',animate:true,checkbox:false"></ul>		
	</div>
	<div class="floatL detaillist">
		<form id="form_mapping_create" class="validateCheck" action="${rc.contextPath}/mapping/save" method="POST" data-options="novalidate:true">
			<#include "/mapping/_form.ftl"/>
			<div class="btnsumbit">
				<a href="javascript:void(0)" class="easyui-linkbutton btnstyle" onClick="submit();">添 加</a>
				<a href="${rc.contextPath}/mapping/index" class="easyui-linkbutton backbtn">返 回</a>
			</div>
		</form>
	</div>
</#macro>