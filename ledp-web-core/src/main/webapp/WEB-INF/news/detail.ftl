<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#include "/layouts/main.ftl"/>
<#assign tag=JspTaglibs["/WEB-INF/taglib/permission.tld"]>
<#import "/layouts/popup.ftl" as massage>
<@html></@html>
<#macro style>
	<style>
		.ichtable th, .ichtable td{
			text-align:left;
			padding:5px 10px;
		}
		.ichtable th{
			background:#eee;
		}
	</style>
</#macro>
<#macro script>
	<script type="text/javascript" src="${rc.contextPath}/js/popup.js"></script>
	<script type="text/javascript">
		function authSuccess(id){
			$.ajax({
				url:'${rc.contextPath}/news/authSuccess/'+id,
				success:function(json){
					general_message(json.code,json.message);
					window.location.href='${rc.contextPath}/news/detail/'+id+'?home=${home}';
				}
			});
		}
		function authFailure(id){
			$.ajax({
				url:'${rc.contextPath}/news/authFailure/'+id,
				success:function(json){
					general_message(json.code,json.message);
					window.location.href='${rc.contextPath}/news/detail/'+id+'?home=${home}';
				}
			});
		}
	</script>
</#macro>
<@massage.generalMessage />
<#macro content>
	<div class="main clearfix">
		<div class="tih2 clearfix">
			<p class="floatL">${news.type.name}详情</p>
		</div>
		<div class="newspre">
			<div class="tile">
				<h2>${news.title}</h2>
				<div class="clearfix mt20">
					<p class="floatL">来源：经销商供稿</p>
					<p style="  width: 500px;float: left;margin-left: 20px;">
					合作媒体：<#list medias as media>
								<#if checkedMedias?exists && checkedMedias?seq_contains(media.id)>
									${media.name}
								</#if>
							</#list>
					</p>
					<p class="floatR">${news.dateCreate?string("yyyy-MM-dd")}</p>
				</div>
				<div class="cmtimee">
					<span>经销商：${news.dealer.name}</span>
					<span>类型：${news.type.name}</span>
				</div>
				<div class="lmin" style="text-align:left;text-indent:0;">
					<div class="clearfix abstract">
						<p class="floatL">信息摘要：</p>
						<p class="floatL">${news.summary}</p>
					</div>
					${news.content}
					<#if news.includePhone=="true" ||news.includeAddress=="true">
					<div class="accountinfo">
						<#if news.includeAddress=="true">
						<p>商家地址：${news.dealer.address}</p>
						</#if>
						<#if news.includePhone=="true">
						<p>咨询电话：<span class="phone">${news.dealer.phone}</span></p>
						</#if>
					</div>
					</#if>
				</div>
			</div>
		</div>
		<div class="btnsumbit btncoloes" style="margin:20px 0px 0px 400px;">
		<#if news.state.code=="wait_auth"||news.state.code=="auth_failure">
		<@check permissionCodes = permissions permissionCode="news/auth">
			<a href="#" onClick="authSuccess(${news.id});" class="easyui-linkbutton btnstyle">审核成功</a>
			<a href="#" onClick="authFailure(${news.id});" class="easyui-linkbutton btnstyle">审核失败</a>
		</@check>
		</#if>
		<#if !(news.state.code=="destroy_whole"||news.state.code=="destroy_part"||news.state.code=="destroy_none") && user.code != "admin">
			<@check permissionCodes = permissions permissionCode="news/update">
			<a href="${rc.contextPath}/news/update/${news.id}?home=${home}" class="easyui-linkbutton btnstyle">编 辑</a>
			</@check>
		</#if>
		<#if home?exists>
			<a href="${rc.contextPath}/home/index?home=${home}" class="easyui-linkbutton btnstyle" >返回</a>
		</#if>
		<#if !home?exists>
			<a href="${rc.contextPath}/news/index" class="easyui-linkbutton btnstyle" >返回</a>
		</#if>
		</div>
		</br>
		<div class="dewidth idtable total">
			<h2>发布详情</h2>
			<table width="100%" border='0' class='ichtable'>
				<thead>
					<th width="25%">合作媒体</th>
					<th width="25%">发布状态</th>
					<th width="25%">发布时间</th>
					<th width="25%">备注</th>
				</thead>
				<tbody>
				<#list newsMedias as newsMedia>
					<tr>
						<td>${newsMedia.media.name}</td><td>${newsMedia.state.name}</td><td><#if newsMedia.datetime?exists>${newsMedia.datetime?string("yyyy-MM-dd HH:mm:ss")}</#if></td><td><div style="width:200px;text-align:left;margin:0;">${newsMedia.comment}</div></td>
					</tr>
				</#list>
				</tbody>
			</table>
		</div>
	</div>
</#macro>