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
		});
		
		function loadRecords(typeId){
			$.ajax({
			    url:"${rc.contextPath}/mapping/records/"+typeId,
			    data:{record:${mapping.type.id}},
				success:function(json){
					$("#records").html(json);
				}
			});
		}
		
	</script>
</#macro>
<#macro content>
	<div class="floatL detaillist">
		<form id="form_mapping_update" class="validateCheck" action="${rc.contextPath}/mapping/edit" method="POST" data-options="novalidate:true">
		<ul class="formlist">
			<li class="clearfix">
				<label>代码类型 <em>*</em></label>
				<span class="iname">${mapping.type.name}</span>
				<input type="hidden" name="type.id" value="${mapping.type.id}"/>
				<input type="hidden" name="type.name" value="${mapping.type.name}"/>
			</li>
			<li class="clearfix">
				<label>${mapping.type.name}名称 <em>*</em></label>
				<span class="iname">${record.name}</span>
			</li>
			<li class="clearfix">
				<label>东雪代码 <em>*</em></label>
				<input class="textbox" name="code" value="${mapping.code}" style="height: 30px; width: 200px;"  datatype="*" nullmsg="请输入东雪代码" errormsg="请输入正确的东雪代码"></input>
				<@spring.bind "mapping.code" /><@spring.showError/>
				<span class="Validform_checktip error "></span>
			</li>
			<li class="clearfix">
				<label>官网代码 <em>*</em></label>
				<input class="textbox" name="code1" value="${mapping.code1}" style="height: 30px; width: 200px;"  datatype="*" nullmsg="请输入官网代码" errormsg="请输入正确的官网代码"></input>
				<@spring.bind "mapping.code1" /><@spring.showError/>
				<span class="Validform_checktip error "></span>
			</li>
			<li class="clearfix">
				<label>易车代码<em>*</em></label>
				<input class="textbox" name="code3" value="${mapping.code3}" style="height: 30px; width: 200px;" datatype="*" nullmsg="请输入易车代码" errormsg="请输入正确的易车代码"></input>
				<@spring.bind "mapping.code3" /><@spring.showError/>
				<span class="Validform_checktip error "></span>
			</li>
			<li class="clearfix">
				<label>汽车之家代码<em>*</em></label>
				<input class="textbox" name="code2" value="${mapping.code2}" style="height: 30px; width: 200px;"  datatype="*" nullmsg="请输入汽车之家代码" errormsg="请输入正确的汽车之家代码"></input>
				<@spring.bind "mapping.code2" /><@spring.showError/>
				<span class="Validform_checktip error "></span>
			</li>
		</ul>
			<input type="hidden" name="id" value="${mapping.id}"/>
			<div class="btnsumbit">
				<a href="javascript:void(0)" class="easyui-linkbutton btnstyle" onClick="$('#form_mapping_update').submit();">保 存</a>
				<a href="${rc.contextPath}/mapping/index" class="easyui-linkbutton backbtn">返 回</a>
			</div>
		</form>
	</div>
</#macro>