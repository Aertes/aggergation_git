<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#include "/layouts/main.ftl"/>
<#import "/layouts/popup.ftl" as massage>
<@html></@html>
<#macro style>
	<style>
		.messadd .dx, .messadd label{
			margin:0;
		}
		.messadd label{
			margin-right:10px;
		}
		.glcx li{
			margin:0;
			width:150px;
		}
		.body_shadow{
			position:absolute;
			left:0px;
 			top: 0px;
			opacity: 0.05;
			background: #000;
			display:none;
			filter:alpha(opacity:5)
		}
	</style>
</#macro>
<#macro script>
	<script type="text/javascript" src="${rc.contextPath}/js/popup.js"></script>
	<!--UE-->
	<!--<script>
		window.UEDITOR_HOME_URL = "${rc.contextPath }/js/ue/";
	</script>-->
	<!--<script type="text/javascript" charset="utf-8" src="${rc.contextPath }/js/ue/ueditor.config.js"></script>-->
	<!--<script type="text/javascript" charset="utf-8" src="${rc.contextPath }/js/ue/ueditor.all.js"></script>-->
	<!--<script type="text/javascript" charset="utf-8" src="${rc.contextPath }/js/ue/lang/zh-cn/zh-cn.js"></script>-->
	<!--kindeditor-->
	<script type="text/javascript" charset="utf-8" src="${rc.contextPath }/js/kindeditor/kindeditor.js"></script>
	
	<script type="text/javascript">
		/*$(".").click(function(){
			$('#insertContent').dialog('close');
			$('.bodyContiner').css('overflow','auto');
			$('.body_shadow').css('display','none');
		});*/
		//kindeditor实现
		KE.show({
				id : 'content1',
				//cssPath : './index.css',
				allowUpload : true, //允许上传图片
                imageUploadJson : '${rc.contextPath }/upload/image', //服务端上传图片处理URI
				items : [
				'fontname', 'fontsize', '|', 'textcolor', 'bgcolor', 'bold', 'italic', 'underline',
				'removeformat', '|', 'justifyleft', 'justifycenter', 'justifyright', 'insertorderedlist',
				'insertunorderedlist', '|', 'emoticons', 'image', 'link']
			});
		
			$(function() {
				//添加信息
				$("#new_add").click(function(){
					if(validate()){
		    			var content = KE.html('content1');
						$("#content").val(content);
						$('#form_news_create').submit();
					}
				});
				
				$("div").delegate(".panel-tool-close","click",function(){
					alert("zhangsan");
				});
				
			});
			
		//提交验证
		function validate(){
			if($("input[name='type.id']:checked").size()==0){
				general_message('error_message','请选择信息类型！');
				return false;
			}
			if($("input[name='medias']:checked").size()==0){
				general_message('error_message','请选择合作媒体！');
				return false;
			}else{
				$("input[name='medias']:checked").each(function(){
					var id=$(this).val();
					$('#medias'+id).val(id);
				});
			}
			if($("input[name='type.id']:checked").attr("code")=="2" && $("input[name='serieses']:checked").size()==0){
				general_message('error_message','请选择关联车系！');
				return false;
			}
			var title = $("input[name='title']").val();
			if(title==""){
				general_message('error_message','请输入信息标题！');
				return false;
			}
			if(title.length<10){
				general_message('error_message','信息标题不能少于10个字符！');
				return false;
			}
			if($("textarea[name='summary']").val()==""){
				general_message('error_message','请输入信息摘要！');
				return false;
			}
			
			var content = KE.html('content1');
			if(content==""){
				general_message('error_message','请输入信息内容！');
				return false;
			}
			if(content.length<400){
				general_message('error_message','信息内容不能少于400个字符！');
				return false;
			}
			return true;
		}
	</script>
</#macro>
<@massage.generalMessage />
<#macro content>
<div class="detaillist ollright">
	<div class="floatL detaillist">
		<form id="form_news_create" action="${rc.contextPath}/news/save" method="post" data-options="novalidate:true" class="validateCheck formlist">
			<#include "/news/_form.ftl"/>
			<div class="btnsumbit" style="margin:20px 0 0 305px;">
			<@check permissionCodes = permissions permissionCode="news/create">
					<a href="javascript:void(0)" class="easyui-linkbutton btnstyle" onClick="previewNews();">预 览</a>
					<a href="javascript:void(0)" class="easyui-linkbutton btnstyle" id="new_add" >添 加</a>
			</@check>
				<a href="${rc.contextPath}/news/index" class="easyui-linkbutton backbtn">返 回</a>
			</div>
		</form>
	</div>
</div>

<div class="body_shadow" >
</div>
</#macro>

