<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#assign tag=JspTaglibs["/WEB-INF/taglib/permission.tld"]>
<#include "/layouts/main.ftl"/>
<@html></@html>
<#macro style></#macro>
<style>
	.swfupload{
		cursor:pointer;
		float:left;
		margin:0 10px;
	}
	.swfupload:hover{
		background:url("${rc.contextPath}/images/bt.png") no-repeat 0 0;
		cursor:pointer;
	}
	.l-btn-icon-left .l-btn-text{
		margin-left:4px;
	}
	.datebox{
		*left:10px;
	}
</style>
<#macro script>
	<script type="text/javascript">
		$(function(){
			$('#tt').tree({
				onClick: function(node){
					$("#parentId").val(node.id);
					$("#parentName").html(node.text);
					
					var hrefAdd = $("#organization_add");
					hrefAdd.attr("href","${rc.contextPath}/organization/create?parent.id="+node.id);
					
					var level = getTreeSelectedNodeLevel();
					$("#organizationLevel").val(level);
					$("#organizationSelectedId").val(node.id);
					$("#searchBtn").click();
						
				},onLoadSuccess :function(node,data){
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
						$("#searchBtn").click();
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
<#macro content>
	<div id="div_tree" class="floatL treeList">
		  <ul id="tt" class="easyui-tree" data-options="url:'${rc.contextPath}/organization/treeUser?checked=${parent}',method:'get',animate:true"></ul>		
	</div>
	<div class="detaillist floatL">
		<div class="tih2 clearfix">
			<p class="floatL">留资合并列表</p>
		</div>
		<div class="search">
			<form action="${rc.contextPath}/leadsMerged/search" method="POST" id="leadsMerged_form">
				<#-- 选中节点的等级 -->
				<input id="organizationLevel" class="key search" type="hidden" name="organizationLevel"/>
				<#-- 组织ID -->
				<input id="organizationSelectedId" class="key search" type="hidden" name="organizationSelectedId"/>
				<div style="*height:100%">
					<div class="clearfix">
						<div class="floatL ieHack">
							<label>媒体渠道：</label>
							<select class="key search" name="ledpMediaId" style="width:160px;">
								<option value="">--请选择--</option>
								<#list mediaList as media>
									<option value="${media.id}">${media.name}</option>
								</#list>
							</select>
						</div>
						<div class="floatL ieHack">
							<label>意向级别：</label>
							<@tag.select type="leads_intent" style="key search" name="ledpIntentId" defaultValue=ledpFollowId?default(0)/>
						</div>
					</div>
					<div class="clearfix mt10">
						<div class="floatL ieHack">
							<label>客户名称：</label>
							<input class="key search" name="name" style="width:150px; height: 30px;">
						</div>
						<div class="floatL ieHack">
							<label>跟进状态：</label>
							<@tag.select type="lesds_state" style="key search" name="ledpFollowId" defaultValue=ledpFollowId?default(0)/>
						</div>
					</div>
					<div class="clearfix mt10">
						<div class="floatL ieHack">
							<label>电话号码：</label>
							<input class="key search" name="phone" style="width:150px; height: 30px;">
						</div>
						<div class="floatL ieHack">
							<label >留资时间：</label>
							<input class="key search easyui-datetimebox" name="beginDate" value="${beginDate?if_exists}" style="width:150px; height: 30px;">
							<span>-</span>
							<input class="key search easyui-datetimebox" name="endDate" value="${endDate?if_exists}" style="width:150px; height: 30px;">
						</div>
						
						<div class="sumbit floatL ieHack">
							<input id="searchBtn" type="button" value="搜 索" class="submit" />
						</div>
					</div>
				</div>
			</form>
		</div>
		<div class="ichtable">
			<table id="leadsMerged_table" width="100%" class="easyui-datagrid" title="&nbsp;" data-options="singleSelect:true,collapsible:false,method:'get',remoteSort:false,multiSort:true,pagination:true,showRefresh:false">
				<thead>
					<tr>
						<th width="10%" data-options="field:'name',sortable:true">客户名称</th>
						<th width="10%" data-options="field:'phone',sortable:true">电话号码</th>
						<th width="10%" data-options="field:'ledpMedia.name',sortable:true">媒体渠道</th>
						<th width="30%" data-options="field:'ledpDealer.name',sortable:true">网点名称</th>
						
						<th width="15%" data-options="field:'createTime',sortable:true">留资时间</th>
						<th width="10%" data-options="field:'ledpIntent.name',sortable:true">意向级别</th>
						<th width="10%" data-options="field:'ledpFollow.name',sortable:true">跟进状态</th>
						<th width="5%" data-options="field:'operations',sortable:false">操作</th>
					</tr>
				</thead>
			</table>
		</div>
	</div>
	<div id="importDialog" class="easyui-window" title="消息框" data-options="modal:true,closed:true,iconCls:'icon-save'" style="width:600px;height:150px;padding:10px;">
		<form id="importForm" action="${rc.contextPath}/leadsMerged/doImport" method="post">
			<div class="clearfix" style="margin:10px 0;">
				<label class="floatL" style="line-height:30px; display:inline-block;">选择导入文件：</label>
				<input class="floatL" type="hidden" id="hidFilePath" name="filePath"/>
				<input class="floatL" id="txtFileName" name="" value="" style="width:150px; height: 30px;border-radius:5px;border:1px solid #b2b2b2;">
				<span class="floatL" id="spanButtonPlaceholder"></span>
				<a class="easyui-linkbutton btnstyle floatL" data-options="iconCls:'icon-ok'" href="javascript:void(0)" id="doImportBtn" style="width:100px; text-align:center;border-radius:5px;">导入</a>
				<span class="flash hide" id="fsUploadProgress">
						<!-- 上传中 UI显示的区域 -->
				</span>
			</div>
		</form>
	</div>
	<link href="${rc.contextPath}/js/loadmask/jquery.loadmask.css" rel="stylesheet" />
	<script type="text/javascript" src="${rc.contextPath}/js/loadmask/jquery.loadmask.min.js" charset="utf-8"></script>
	<script type="text/javascript" src="${rc.contextPath}/js/swfupload/swfupload.js" charset="utf-8"></script>
	<script type="text/javascript" src="${rc.contextPath}/js/swfupload/handlers.js" charset="utf-8"></script>
	<script type="text/javascript">
		$(function(){
			$.plugin.init("#leadsMerged_form","#leadsMerged_table");	
			// 初始化上传控件
			initUpload();
			$("#doImportBtn").click(doImportFn);
		});
		
		function toImport(){
			$("#importDialog").parent(".panel").show();
		}

		/**
		 * 初始化上传控件
		 * @return
		 */
		function initUpload(){
			swfu = new SWFUpload({
				upload_url : "${rc.contextPath}/leadsMerged/uploadImportFile;jsessionid=" + window["sessionId"],
				flash_url : "${rc.contextPath}/js/swfupload/swfupload.swf",
				// 最大上传文件大小
				file_size_limit : 10240, // 10 MB
				// 允许上传的文件类型
				file_types : "*.xls", // or you could use something like:
				// 文件类型描述
				file_types_description : "Excel 97-2003 文档",
				file_queue_limit:0,
				button_image_url :  "${rc.contextPath}/images/bt.png",
				button_width : 100,
				button_height : 30,
				custom_settings : {
					progress_target : "fsUploadProgress"
				}
			});
		}
				
		/**
		 * 导入
		 * @return
		 */
		function doImportFn(){
			if(null == swfu.getFile()){
				alert("请选择要上传的文件");
				return false;
			}
			
			//执行上传操作
			$("body").mask("正在上传文件，请稍后...");
			swfu.startUpload();
		}
		
		/**
		 * 上传成功后执行的函数
		 * @return
		 */
		function uploadDone() {
			$("body").mask("正在导入数据，请稍后...");
			$("#importForm").submit();
		}
	</script>
</#macro>






