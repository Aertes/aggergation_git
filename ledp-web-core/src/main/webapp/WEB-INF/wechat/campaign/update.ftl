<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#assign tag=JspTaglibs["/WEB-INF/taglib/permission.tld"]>
<#include "/wechat/layouts/main.ftl"/>
<@html></@html>
<#macro style>
	<style>
		.creatlist{
		margin:40px 0 20px 0;
	}
	.creatlist label{
		float:left;
		width:35%;
		line-height:30px;
		text-align:right;
		display:block;
		font-size:12px;
	}
	.creatlist >li>div{
		float:left;
		margin-left:1%;
		width:63%;
		text-align:left;
	}
	.creatlist .Validform_wrong{
		color:red;
		margin:5px 0;
	}
	.creatlist li{
		margin:10px 0 0;
		clear:both;
		overflow:hidden;
	}
	.marginLeft40{
		margin-left:40%;
	}
	</style>
</#macro>
<#macro content>
<div class="breadcrumbs" id="breadcrumbs">
	<script type="text/javascript">
		try{ace.settings.check('breadcrumbs' , 'fixed')}catch(e){}
	</script>

	<ul class="breadcrumb">
		<li>
			<i class="icon-home home-icon"></i>
			<span>营销应用</span>
			<span>&gt;&gt;</span>
			<span><a href="${rc.contextPath}/wechat/campaign/index">活动管理</a></span>
			<span>&gt;&gt;</span>
			<span>填写活动基本信息</span>
		</li>
	</ul>
</div>
<div class="active_slider">
	<div class="sliderBar">
	</div>
	<div class="sliderPointer active marginLeft15">
		<div>
			<span>1</span>
		</div>
		<span>编辑活动基本信息</span>
	</div>
	<div style="position:absolute;left:29%;margin-top:0;"><i style="color:#333;font-size:22px;" class="icon-double-angle-right"></i></div>
	<div class="sliderPointer marginLeft40">
		<div>
			<span>2</span>
		</div>
		<span>选择模版</span>
	</div>
	<div style="position:absolute;left:55%;margin-top:0;"><i style="color:#333;font-size:22px;" class="icon-double-angle-right"></i></div>
	<div class="sliderPointer marginLeft65">
		<div>
			<span>3</span>
		</div>
		<span>界面编辑</span>
	</div>
</div>
<form id="form_camp_save" action="${rc.contextPath}/wechat/campaign/edit" method="post" enctype="multipart/form-data">
<div class="createActiveFrom">
		<input type="hidden" name="id" value="${campaign.id}"/>
		<ul class="creatlist">
			<li>
				<label>活动名称：</label>
				<div>
					<input type="text" name="name" style="width: 300px;" value="${campaign.name}" maxlength="60" dataType="*" errormsg="请填写活动名称"  nullmsg="请填写活动名称"/>
					<div class="Validform_checktip"></div>
					<span style="padding:5px 0;display:block;">最长为60个字符</span>
				</div>
			</li>
			<li>
				<label>活动时间：</label>
				<div>
					<div id="beginDate" style="width: 200px;" class="floatL" params="{name:'beginDate',id:'cbeginDate',value:'${campaign.getBeginDate()?string("yyyy-MM-dd HH:mm")}',validate : 'validate' ,dataType:'*',errormsg:'请选择活动开始时间', nullmsg:'请选择活动开始时间'}"></div>
					<span class="floatL margin10">-</span>
					<div id="endDate" style="width: 200px;" class="floatL" params="{name:'endDate',id:'cendDate',value:'${campaign.getEndDate()?string("yyyy-MM-dd HH:mm")}',validate : 'validate' ,dataType:'*',errormsg:'请选择活动结束时间', nullmsg:'请选择活动结束时间'}"></div>
				</div>
			</li>
			<li>
				<label style="font-size:12px;">活动缩略图：</label>
				<div><input id="imageup" type="file" name="image" /></div>
			</li>
			<li>
				<label></label>
				<div>
					<div class="floatL tdPicture">
						<img id="ImgPr" width="200" height="200" src="${imgDir}${campaign.bak1}" />
					</div>
					<span class="floatL marginLeft10 colorR tdSize">建议图片大小200*200</span>
				</div>
			</li>
			<li>
				<label>活动简介：</label>
				<div>
					<textarea class="textarea"name="comment" maxlength="200" dataType="*" errormsg="请填写活动说明"  nullmsg="请填写活动说明">${campaign.comment}</textarea>
					<div class="Validform_checktip"></div>
					<span style="padding:5px 0;display:block;">最长为200个字符</span>
				</div>
			</li>
		</ul>
	<input type="hidden" name="save_view" id="save_view"/>
</div>
<div class="textAlign" style="margin-bottom:20px;">
	<input type="button" class="redButton" onclick="saveCampaign('saveCreate');" value="保存并继续" />
	<input type="button" class="redButton" onclick="saveCampaign('saveList');" value="保 存" />
	<input type="button" class="redButton" onclick="window.location.href='${rc.contextPath}/wechat/campaign/index'" value="返回" />
</div>
</form>
</#macro>
<#macro script>
	<script src="${rc.contextPath}/wechat/js/jquery-1.7.2.js"></script>
	<script type="text/javascript" src="${rc.contextPath}/wechat/js/Validform_v5.3.2_min.js"></script>
	<script type="text/javascript" src="${rc.contextPath}/wechat/js/validateCheck.js"></script>
	<script src="${rc.contextPath}/wechat/js/uploadPreview.js"></script>
	<script>
		$(function(){
			$.plugin.validate();
			$("#imageup").uploadPreview({ Img: "ImgPr", Width: 200, Height: 200 });
			dateTimeInterface.init("dateTime","beginDate");
			dateTimeInterface.init("dateTime","endDate");
			
			$("#sidebar").removeClass("menu-min");
		});
		function saveCampaign(value){
			$('#save_view').val(value);
			if(!checkEndTime($("#cbeginDate").val(),$("#cendDate").val())){
				var layerAlert = new ElasticLayer("alert","请选择正确的时间区间",{title : "信息提示框",dialog : true,time : 2000});
				layerAlert.init();
				return false;
			}
			$('#form_camp_save').submit();
		}
		function checkEndTime(startTime,endTime){  
		    var start=new Date(startTime.replace("-", "/").replace("-", "/"));  
		    var end=new Date(endTime.replace("-", "/").replace("-", "/"));  
		    if(end<start){  
		        return false;  
		    }  
		    return true;  
		}
	</script>
</#macro>