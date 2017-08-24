<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#assign tag=JspTaglibs["/WEB-INF/taglib/permission.tld"]>
<#include "/layouts/main.ftl"/>
<#import "/layouts/popup.ftl" as massage>
<@html></@html>
<#macro style></#macro>
<#macro script>
	<script type="text/javascript" src="${rc.contextPath}/js/popup.js"></script>
	<script type="text/javascript">
		<#--
		$(function(){
			$('#tt').tree({
				onClick: function(node){
					$("#parentId").val(node.id);
					$("#parentName").html(node.text);
					
					var hrefAdd = $("#organization_add");
					hrefAdd.attr("href","${rc.contextPath}/organization/create?parent.id="+node.id);
				}
			});
		});-->
	</script>
</#macro>
<@massage.generalMessage />
<#macro content>
	<#-- <div class="floatL treeList">
		  <ul id="tt" class="easyui-tree" data-options="url:'${rc.contextPath}/organization/tree?checkeds=${parent}',method:'get',animate:true"></ul>		
	</div> -->
		<div class="tih2 clearfix">
			<p class="floatL">客户列表</p>
		</div>
		<div class="search">
			<form action="${rc.contextPath}/customer/search" method="POST" id="customer_form">
				<div style="overflow:hidden;*height:80px;">
					<div class="floatL autoFill ieHack">
						<label>客户名称：</label>
						<input type="text" class="key search moreWidthText" name="name" />
					</div>
					<div class="floatL autoFill ieHack">
						<label>电话号码：</label>
						<input type="text" class="key search moreWidthText" name="phone" />
					</div>
					<div class="floatL autoFill ieHack">
						<label>跟进状态：</label>
						<@tag.select type="lesds_state" style="key search moreWidthselect " name="follow" defaultValue=intent?default(0)/>
					</div>
					<div class="floatL autoFill ieHack">
						<label>电子邮箱：</label>
						<input type="text" class="key search moreWidthText" name="email"/>
					</div>
					<div class="floatL autoFill ieHack">
						<label>意向级别：</label>
						<@tag.select type="leads_intent" style="key search moreWidthselect " name="intent" defaultValue=intent?default(0)/>
					</div>
					<div class="sumbit floatL autoFill ieHack" style="width:100px;">
						<input type="button" value="搜 索" class="submit" />
					</div>
				</div>
			</form>
		</div>
		<div class="ichtable">
			<table id="customer_table" width="98%" class="easyui-datagrid" title="<a href='javascript:doExport();' title='导出'>导出</a>" data-options="singleSelect:true,collapsible:false,method:'get',remoteSort:false,multiSort:true,pagination:true,showRefresh:false">
				<thead>
					<tr>
						<th width="20%" data-options="field:'name',sortable:true">客户名称</th>
						<th width="20%" data-options="field:'phone',sortable:true">手机号码</th>
						<th width="20%" data-options="field:'email',sortable:true">电子邮箱</th>
						<th width="10%" data-options="field:'intent.name',sortable:true">意向级别</th>
						<th width="10%" data-options="field:'follow.name',sortable:true">跟进状态</th>
						<th width="20%" data-options="field:'operations',sortable:false">操作</th>
					</tr>
				</thead>
			</table>
		</div>
	<script type="text/javascript">
		$(function(){
			$.plugin.init("#customer_form","#customer_table");			
		});
		// 导出
		function doExport(){
			$("#customer_form").attr("action","${rc.contextPath}/customer/doExport");
			$("#customer_form").submit();
			$("#customer_form").attr("action","${rc.contextPath}/customer/search");
		}
	</script>
</#macro>