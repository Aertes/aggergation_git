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
			$('#tt').tree({
				onClick: function(node){
					$("#parentId").val(node.id);
					$("#parentName").html(node.text);
					
					var hrefAdd = $("#organization_add");
					hrefAdd.attr("href","${rc.contextPath}/organization/create?parent.id="+node.id);
				},
				onSelect:function(node){
					var level = getTreeSelectedNodeLevel();
					// 节点等级
					$("#organizationLevel").val(level);
					// 节点ID
					$("#organizationSelectedId").val(node.id);
					$("#searchBtn").click();
				},onLoadSuccess :function(node){
					if(!node){
						node = $('#tt').tree('getRoot');
					}
					/*if(node!=null){
						$('#tt').tree('update', {target:node.target,'tree-node-selected',true});
					}*/
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
		<div class="tih2 clearfix" style="margin:0;">
			<p class="floatL">留资列表</p>
		</div>
		<div class="search">
			<form action="${rc.contextPath}/leads/search" method="POST" id="leads_form">
				<#-- 选中节点的等级 -->
				<input id="organizationLevel" class="key search" type="hidden" name="organizationLevel"/>
				<#-- 组织ID -->
				<input id="organizationSelectedId" class="key search" type="hidden" name="organizationSelectedId"/>
				<div style="overflow:hidden;*height:100%;">
					<div class="floatL autoFill ieHack">
						<label>线索类型：</label>
						<@tag.select type="leads_type" style="key search" name="typeId" defaultValue=typeId?default(0)/>
					</div>
					<div class="floatL  autoFill ieHack">
						<label>媒体渠道：</label>
						<select class="key search" name="ledpMediaId">
							<option value="">--请选择--</option>
							<#list mediaList as media>
								<option <#if ledpMediaId == media.id>selected='selected'</#if> value="${media.id}">${media.name}</option>
							</#list>
						</select>
					</div>
					<#-- <div class="floatL" style="margin:10px 0;">
						<label>意向级别：</label>
						<@tag.select type="leads_intent" style="key search" name="ledpIntentId" defaultValue=ledpFollowId?default(0)/>
					</div> -->
					
					<div class="floatL autoFill" style="margin:5px 0;">
						<label>跟进状态：</label>
						<@tag.select type="lesds_state" style="key search" name="ledpFollowId" defaultValue=ledpFollowId?default(0)/>
					</div>
					</div>
					<div style="overflow:hidden;*height:100%;">
					<div class="floatL autoFill ieHack" style="margin:10px 0px;width:307px;*width:260px;" >
						<label>客户名称：</label>
						<input class="key search" name="name" style="width:200px; height: 30px;">
					</div>
					<div class="floatL autoFill ieHack" style="margin:10px 0px;width:307px;*width:260px;" >
						<label>手机号码：</label>
						<input class="key search" name="phone" style="width:200px; height: 30px;">
					</div>
					</div>
					<div style="overflow:hidden;*height:100%;">
					<div class="floatL ieHack" style="margin:10px 0 0;">
						<label >留资时间：</label>
						<input class="key search easyui-datetimebox" id="beginDate" name="beginDate" value="${beginDate?if_exists}" style="width:120px; height: 30px;">
						<span>-</span>
						<input class="key search easyui-datetimebox" id="endDate" name="endDate" value="${endDate?if_exists}" style="width:120px; height: 30px;">
					</div>
					
					<div class="sumbit ieHack floatL" style="margin:10px 0 0;">
						<input id="searchBtn" type="button" value="搜 索" class="submit" />
					</div>
				</div>
			</form>
		</div>
		<div class="ichtable">
			<table id="leads_table" width="98%" class="easyui-datagrid" title="<a href='javascript:doExport();' title='导出'>导出</a>" data-options="singleSelect:true,collapsible:false,method:'get',remoteSort:true,multiSort:true,pagination:true,showRefresh:false">
				<thead>
					<tr>
						<th width="10%" data-options="field:'ledpType.name',sortable:true">线索类型</th>
						<th width="12%" data-options="field:'name',sortable:true">客户名称</th>
						<th width="12%" data-options="field:'phone',sortable:true">手机号码</th>
						<th width="10%" data-options="field:'ledpMedia.name',sortable:true">媒体渠道</th>
						<th width="25%" data-options="field:'ledpDealer.name',sortable:true">网点名称</th>
						<th width="17%" data-options="field:'createTime',sortable:true">留资时间</th>
						<#-- 
						<th width="10%" data-options="field:'ledpIntent.name',sortable:true">意向级别</th>-->
						<th width="10%" data-options="field:'ledpFollow.name',sortable:true">跟进状态</th> 
						
						<th width="5%" data-options="field:'operations',sortable:false">操作</th>
					</tr>
				</thead>
			</table>
		</div>
	</div>
	<script type="text/javascript">
		$(function(){
			$.plugin.init("#leads_form","#leads_table");			
		});
		
		// 导出
		function doExport(){
            /*var win = $.messager.progress({
                title:'Please waiting',
                msg:'Loading data...'
            });*/
            $.messager.alert('提示','正在导出数据，请耐心等待!','info');
			$("#leads_form").attr("action","${rc.contextPath}/leads/doExport");
			$("#leads_form").submit();
			$("#leads_form").attr("action","${rc.contextPath}/leads/search");
		}
	</script>
</#macro>






