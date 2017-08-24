<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#assign tag=JspTaglibs["/WEB-INF/taglib/permission.tld"]>
<#include "/layouts/main.ftl"/>
<#import "/layouts/popup.ftl" as massage>
<@html></@html>
<#macro style></#macro>
<#macro script>
	<script type="text/javascript" src="${rc.contextPath}/js/popup.js"></script>
	<script type="text/javascript">
		$(function(){
			$.plugin.init("#news_form","#news_table");	
			
			//组织结构树
			$('#tt').tree({
				onClick: function(node){
					$("#parentId").val(node.id);
					$("#parentName").html(node.text);
					
					var hrefAdd = $("#organization_add");
					hrefAdd.attr("href","${rc.contextPath}/organization/create?parent.id="+node.id);
					
					var level = getTreeSelectedNodeLevel();
					$("#organizationLevel").val(level);
					$("#organizationSelectedId").val(node.id);
					$("#formSubmit").click();
				},
				onLoadSuccess :function(node, data){
					if(!node){
						node = $('#tt').tree('getRoot');
					}
					/*if(node!=null){
						$('#tt').tree('expandTo', node.target).tree('select', node.target);
					}*/
					
					if(data.length>0){
						var id =data[0].id;
						var level = getTreeSelectedNodeLevel();
						$("#organizationLevel").val(level);
						$("#organizationSelectedId").val(id);
						$("#formSubmit").click();
					}
				}
			});
			//网点隐藏树结构
			if(${dealer}){
				$("#div_tree").hide();
				$(".detaillist").css("width","100%");
			}
			
		});
		function submitCallback(json){
			general_message(json.code,json.message);
			$("#formSubmit").click();
		}
		function destroyCallback(json){
			general_message(json.code,json.message);
			$("#formSubmit").click();
		}
		function deleteCallback(json){
			general_message(json.code,json.message);
			$("#formSubmit").click();
		}

	</script>
</#macro>
<@massage.generalMessage />
<#macro content>
	<div id="div_tree" class="floatL treeList">
		    <ul id="tt" class="easyui-tree" data-options="url:'${rc.contextPath}/organization/treeUser?checked=${parent}',method:'get',animate:true"></ul>		
	</div>
	<div class="detaillist floatL">
	<#if permission?exists>
		<div class="tih2 clearfix">
			<h2>信息列表</h2>
		</div>
		<div class="search">
			<form action="${rc.contextPath}/news/search" method="get" id="news_form">
			<#-- 选中节点的等级 -->
			<input id="organizationLevel" class="key search" type="hidden" name="organizationLevel" value="${level}"/>
			<#-- 组织ID -->
			<input id="organizationSelectedId" class="key search" type="hidden" name="organizationSelectedId" value="${organation}"/>
			<div class="clearfix" style="*height:40px">
				<div class="floatL ieHack">
					<label>信息类型：</label>
					<@tag.select type="news_type" style="key search" name="type" defaultValue=type?default(0)/>
				</div>
				<div class="floatL ieHack customdate">
					<label>创建时间：</label>
					<input class="key search easyui-datebox" id="date1" style="width:150px; height: 30px;" name="date1">
					<span>-</span>
					<input class="key search easyui-datebox" id="date2" style="width:150px; height: 30px;" name="date2">
				</div>
			</div>
			<div class="clearfix  mt10" style="*height:40px">
				<div class="floatL ieHack">
					<label>信息状态：</label>
					<@tag.select type="news_state" style="key search" name="stateId" defaultValue=stateId?default(0)/>
				</div>
				<div class="floatL ieHack customdate">
					<label>信息标题：</label>
					<input class="key search" type="text" style="width:150px; height: 30px;" name="title" />
				</div>
				<div class="sumbit ieHack floatL ml10">
					<input type="button" class="submit" value="搜 索" id="formSubmit"/>
				</div>
			</div>
			</form>
		</div>
		<div class="ichtable">
			<table id="news_table" width="100%" class="easyui-datagrid" title="    " data-options="singleSelect:true,collapsible:false,method:'get',remoteSort:true,multiSort:false,pagination:true,showRefresh:false" >
				<thead>
					<tr>
						<th width="20%" data-options="field:'dealer.name',sortable:true">网点名称</th>
						<th width="10%" data-options="field:'type.name',sortable:true">信息类型</th>
						<th width="18%" data-options="field:'title',sortable:true">信息标题</th>
						<th width="10%" data-options="field:'state.name',sortable:true">信息状态</th>
						<th width="19%" data-options="field:'result',sortable:false">发送结果</th>
						<th width="12%" data-options="field:'dateCreate',sortable:true">创建时间</th>
						<th width="12%" data-options="field:'operations',sortable:false">操作</th>
					</tr>
				</thead>
			</table>
		</div>
	</#if>
	<#if !permission?exists>
		<div class="tih2 clearfix">
			<h2>信息列表</h2>
		</div>
		<div class="search">
			<form action="${rc.contextPath}/news/search" method="get" id="news_form">
			<#-- 选中节点的等级 -->
			<input id="organizationLevel" class="key search" type="hidden" name="organizationLevel"/>
			<#-- 组织ID -->
			<input id="organizationSelectedId" class="key search" type="hidden" name="organizationSelectedId"/>
			<div class="clearfix" style="*height:40px">
				<div class="floatL ieHack">
					<label>信息类型：</label>
					<@tag.select type="news_type" style="key search" name="type" defaultValue=type?default(0)/>
				</div>
				<div class="floatL ieHack customdate">
					<label>创建时间：</label>
					<input class="key search easyui-datebox" id="date1" style="width:150px; height: 30px;" name="date1">
					<span>-</span>
					<input class="key search easyui-datebox" id="date2" style="width:150px; height: 30px;" name="date2">
				</div>
			</div>
			<div class="clearfix  mt10" style="*height:40px">
				<div class="floatL ieHack">
					<label>信息状态：</label>
					<@tag.select type="news_state" style="key search" name="stateId" defaultValue=stateId?default(0)/>
				</div>
				<div class="floatL ieHack customdate">
					<label>信息标题：</label>
					<input class="key search" type="text" style="width:150px; height: 30px;" name="title" />
				</div>
				<div class="sumbit ieHack floatL ml10">
					<input type="button" class="submit" value="搜 索" id="formSubmit"/>
				</div>
			</div>
			</form>
		</div>
		<div class="ichtable">
			<table id="news_table" width="100%" class="easyui-datagrid" title="<a></a>" data-options="singleSelect:true,collapsible:false,method:'get',remoteSort:true,multiSort:false,pagination:true,showRefresh:false" >
				<thead>
					<tr>
						<th width="20%" data-options="field:'dealer.name',sortable:true">网点名称</th>
						<th width="10%" data-options="field:'type.name',sortable:true">信息类型</th>
						<th width="18%" data-options="field:'title',sortable:true">信息标题</th>
						<th width="10%" data-options="field:'state.name',sortable:true">信息状态</th>
						<th width="19%" data-options="field:'result',sortable:false">发送结果</th>
						<th width="12%" data-options="field:'dateCreate',sortable:true">创建时间</th>
						<th width="12%" data-options="field:'operations',sortable:false">操作</th>
					</tr>
				</thead>
			</table>
		</div>
	</div>	
	</#if>
</#macro>