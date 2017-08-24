<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#assign tag=JspTaglibs["/WEB-INF/taglib/permission.tld"]>
<#include "/layouts/main.ftl"/>
<#import "/layouts/popup.ftl" as massage>
<@html></@html>
<#macro style>
	<style>
		.l-btn-icon-left .l-btn-text, .btnstyle .l-btn-text, .backbtn .l-btn-text{
			margin:0;
		}
	</style>
</#macro>
<#macro script>
	<script type="text/javascript" src="${rc.contextPath}/js/popup.js"></script>
	<script type="text/javascript">
	$(function(){
		$("#importForm").delegate("#doImportBtn","click",function(){
			var fileName=$("#txtFileName").val();
			
			if(fileName==null || fileName==""){
					alert("请选择文件！");
			}else{
				var fileNameIndex=fileName.indexOf(".");
				if(fileName.substring(fileNameIndex+1)!='csv'){
					general_message("success_message","请选择csv文件");
					return false;
				}
				$("#importForm").submit();
				$("#importDialog").parent(".panel").hide();
			}
			
		});
	});
	$(function(){
			$('#tt').tree({
				onClick: function(node){
					if(node.type=='largeArea'){
						$('#form_H').val(node.id);
						$('#form_J').val('');
					}else if(node.type=='dealer'){
						$('#form_H').val('');
						$('#form_J').val(node.id);
					}else{
						$('#form_J').val('');
						$('#form_H').val('');
					}
					$("#formSubmit").click();
				},
				onSelect:function(node){
					var level = getTreeSelectedNodeLevel();
					// 节点等级
					$("#organizationLevel").val(level);
					// 节点ID
					$("#organizationSelectedId").val(node.type);
				},onLoadSuccess :function(node){
					if(!node){
						node = $('#tt').tree('getRoot');
					}
					if(node!=null){
						$('#tt').tree('expandTo', node.target).tree('select', node.target);
					}
					var level = getTreeSelectedNodeLevel();
					// 节点等级
					$("#organizationLevel").val(node.type);
					// 节点ID
					$("#organizationSelectedId").val(node.id);
					if(node.type=='largeArea'){
						$('#form_H').val(node.id);
					}else if(node.type=='dealer'){
						$('#form_J').val(node.id);
					}
				}
			});
			//网点隐藏树结构
			if(${dealer}){
				$("#div_tree").hide();
				$(".detaillist").css("width","100%");
			}
			
		});
	</script>	
</#macro>
<@massage.generalMessage />
<#macro content>
<div id="div_tree" class="floatL treeList">
		  <ul id="tt" class="easyui-tree" data-options="url:'${rc.contextPath}/organization/treeUser?checked=${parent}',method:'get',animate:true"></ul>		
	</div>
	<div class="detaillist floatL">
		<div class="tih2 clearfix">
			<p class="floatL">回流列表</p>
		</div>
		<div class="search">
			<form action="${rc.contextPath}/crm/leads/search" method="POST" id="customer_form">
			<input type="hidden" value="${messages}" id="msg"/>
			<#-- 选中节点的等级 -->
				<input id="organizationLevel" class="key search" type="hidden" name="organizationLevel"/>
				<#-- 组织ID -->
				<input id="organizationSelectedId" class="key search" type="hidden" name="organizationSelectedId"/>
				<input type="hidden" class="key search moreWidthText" id="form_J" name="J"/>
				<input type="hidden" class="key search moreWidthText" id="form_H" name="H"/>
				<div style="overflow:hidden;*height:80px;">
					<div style="float:left;">
						<label>导入时间：</label>
						<input type="text" class="easyui-datetimebox" name="startTime" style="width:150px; height: 30px;"/>-
						
					</div>
					<div style="float:left;margin-right:10px;">
					<input type="text" class="easyui-datetimebox" name="endTime" style="width:150px; height: 30px;"/>	
					</div>
					<div class="floatL autoFill ieHack">
						<label>客户名称：</label>
						<input type="text" class="key search moreWidthText" name="D" />
					</div>
					<div class="floatL autoFill ieHack" style="margin-right:120px;">
						<label>手机号码：</label>
						<input type="text" class="key search moreWidthText" name="E" />
					</div>
					<div class="floatL autoFill ieHack">
						<label>意向级别：</label>
						<input type="text" class="key search moreWidthText" name="S" />
					</div>
					
					<div class="sumbit floatL autoFill ieHack" style="width:100px;">
						<input type="button" value="搜 索" class="submit" id="formSubmit"/>
					</div>
				</div>
			</form>
		</div>
		<div class="ichtable">
			<table id="customer_table" width="98%" class="easyui-datagrid" title="<a href='javascript:toImport();' title='导入'>导入</a>" data-options="singleSelect:true,collapsible:false,method:'get',remoteSort:false,multiSort:true,pagination:true,showRefresh:false">
				<thead>
					<tr>
						<th width="20%" data-options="field:'batch',sortable:true">导入时间</th>
						<th width="10%" data-options="field:'D',sortable:true">客户名称</th>
						<th width="15%" data-options="field:'E',sortable:true">手机号码</th>
						<th width="25%" data-options="field:'J',sortable:true">网点名称</th>
						<th width="10%" data-options="field:'S',sortable:true">意向级别</th>
						<th width="10%" data-options="field:'Y',sortable:true">创建时间</th>
						<th width="10%" data-options="field:'operations',sortable:false">操作</th>
					</tr>
				</thead>
			</table>
		</div></div>
	<div id="importDialog" class="easyui-window" title="消息框" data-options="modal:true,closed:true,iconCls:'icon-save'" style="width:600px;height:150px;padding:10px;">
		<form id="importForm" action="${rc.contextPath}/crm/leads/importXls" method="post" enctype="multipart/form-data">
			<div class="clearfix" style="margin:10px 0;">
				<label class="floatL" style="line-height:30px; display:inline-block;">选择导入文件：</label>
				<input class="floatL" type="hidden" id="hidFilePath" name="filePath"/>
				<input class="floatL" type="file" id="txtFileName" name="file" value="" style="width:150px; height: 30px;border-radius:5px;">
				<span class="floatL" id="spanButtonPlaceholder"></span>
				<div><a class="easyui-linkbutton btnstyle floatL" data-options="iconCls:'icon-ok'" href="javascript:void(0)" id="doImportBtn" style="width:100px; text-align:center;border-radius:5px;">导入</a>
				</div><span class="flash hide" id="fsUploadProgress">
						<!-- 上传中 UI显示的区域 -->
				</span>
				<span class="floatL" style="display:block; line-height:30px; padding:0 10px;">
					文件只能是：csv文件
				</span>
			</div>
			
		</form>
	</div>
	<script type="text/javascript">
		$(function(){
			var msg=$("#msg").val();
			if(msg!=""){
				general_title_message("ok_title_message",msg);
			}
			$.plugin.init("#customer_form","#customer_table");
		});
		function toImport(){
			$("#importDialog").parent(".panel").show();
		}
		
	</script>
</#macro>