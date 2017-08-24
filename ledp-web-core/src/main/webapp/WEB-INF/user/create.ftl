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
					if(node.isForbid=='active'){
						var userOrginazation =$("#userOrginazation");
						userOrginazation.empty();
						
						var html="<label>归属机构 <em>*</em></label>";
						html+="<span class='iname' id='orgName0'>"+node.text+"</span>";
						if(node.type=="headquarters" || node.type=="largeArea" ){
							html+="<input type='hidden' name='org.id' id='orgId' value='"+node.id+"'>";
							html+="<input type='hidden' name='org.name' id='orgName' value='"+node.text+"'>"
						}
						if(node.type=='dealer'){
							html+="<input type='hidden' name='dealer.id' id='orgId' value='"+node.id+"'>";
							html+="<input type='hidden' name='dealer.name' id='orgName' value='"+node.text+"'>"
						}
						userOrginazation.html(html);
					}
				},
				onLoadSuccess :function(node){
					node=$('#tt').tree('find',$('#orgId').val());
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
		  <ul id="tt" class="easyui-tree" data-options="url:'${rc.contextPath}/organization/treeUser',method:'get',animate:true"></ul>		
	</div>
	<div class="floatL detaillist">
		
		<form id="form_user_create" class="validateCheck" action="${rc.contextPath}/user/save" method="post">
			<#include "/user/_form.ftl"/>
			<div class="btnsumbit">
				<a href="javascript:void(0)" class="easyui-linkbutton btnstyle" onClick="$('#form_user_create').submit();">添 加</a>
				<a href="${rc.contextPath}/user/index" class="easyui-linkbutton backbtn">返 回</a>
			</div>
		</form>
	</div>
</#macro>