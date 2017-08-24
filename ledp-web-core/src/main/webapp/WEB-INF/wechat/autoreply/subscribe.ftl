<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#assign tag=JspTaglibs["/WEB-INF/taglib/permission.tld"]>
<#include "/wechat/layouts/main.ftl"/>
<@html></@html>
<#macro style>
<style type="text/css">
    .addPiContinaer .tuwen {
        width: 30%;
    }

    .addPiContinaer .tuwen li {
        margin: 0;
    }
    .tuwenChild h4{
    	width:100%;
    }
    .animatePicture label{
    	width:69%;
    	height:45px;
    	overflow:hidden;
    }
    .animatePicture_chil{
    	width:30%;
    	height:auto;
    	margin-left:1%;
    }
    .animatePicture{
    	padding:10px;
    }
    .tuwen >li >p{
    	padding:0 10px 10px;
    }
    .borderbottom{
    	border-bottom:1px solid #d9d9d9;
    }
</style>
</#macro>
<#macro content>
<div class="breadcrumbs" id="breadcrumbs">
    <script type="text/javascript">
        try {
            ace.settings.check('breadcrumbs', 'fixed')
        } catch (e) {
        }
    </script>

    <ul class="breadcrumb">
        <li>
            <i class="icon-home home-icon"></i>
            <span>营销应用</span>
            <span>&gt;&gt;</span>
            <span>消息中心</span>
            <span>&gt;&gt;</span>
            <span>自动回复功能</span>
        </li>
    </ul>
</div>
<h1 class="title_page pr">自动回复</h1>
<div class="message" style="line-height: 50px;">
    <a href="${rc.contextPath}/wechat/messagesendbatch/index" class=" marginLeft10">群发功能</a>
    <a href="${rc.contextPath}/wechat/message/index" class=" marginLeft10">消息管理</a>
</div>
<div class="switch" style="padding-left: 26px;">
    <span>自动回复：</span>
    <label class="marginLeft10">
    	<input class="ace ace-switch ace-switch-5" type="checkbox" <#if autoReply.status==1>checked</#if>/>
        <span class="lbl"></span>
    </label>
</div>
<form id="autoReplyForm" action="${rc.contextPath}/wechat/autoreply/subscribe" enctype="multipart/form-data" method="POST">
	<input name="type" value="subscribe" type="hidden"/>
	<input name="status" value="${autoReply.status?default('0')}" type="hidden"/>
	<input name="msgType" value="${autoReply.msgType?default('news')}" type="hidden"/>
	<input name="materialId" value="${autoReply.materialId}" type="hidden"/>
    <div class="tabContainer autoReply" style="border: 0px; width:96%;margin-top: 10px;">
        <ul class="tabContiner_nav">
            <li href="#tab_contianer" class="active">订阅自动回复</li>
            <li href="#" onClick="changeType('message')">消息自动回复</li>
        </ul>
        <div class="tabContianer_con displayBlock">
            <div style="display: block;" id="tab_contianer">
                <div class="tabContainer tabSendpage tabliChange" style="padding: 10px;">
                    <ul class="tabContiner_nav borderbottom">
                        <li <#if autoReply.msgType=="news"> class="active"</#if> href="#tab_news">图文信息<#if autoReply.msgType=="news">（当前采用）</#if></li>
                        <li <#if autoReply.msgType=="text"> class="active"</#if> href="#tab_text">文字信息<#if autoReply.msgType=="text">（当前采用）</#if></li>
                    </ul>
                    <div class="tabContianer_con displayBlock">
                        <div style="display: block;" id="tab_news" class="addPiContinaer">
                            <div class="addPi1">
                                <div style="width: 100%;" class="floatL addPar">
                                    <a href="javascript:void(0);" style='width: 100px;margin:150px auto;display: block;'>
                                        <input type="file" name="image" style="width:68px;" id="image_tab" class="floatL" />
                                    </a>
                                </div>
                            </div>
                            <div class="addPi1">
                                <div style="width: 100%;" class="floatL addPar">
                                    <a href="javascript:void(0);" onclick="layerPage.init();" class="selectFromSucai">
                                        <i class="icon-plus add"></i>
                                        <span class="addTitle">从素材库选择</span>
                                    </a>
                                </div>
                            </div>
                        </div>
                        <div id="tab_text" class="textareaConsulte">
                            <div>
                                <textarea id="content" name="content" class="changeTextarea textArea">${autoReply.content?if_exists}</textarea>
                            </div>
                            <div class="border">
                                <span class="floatR">还可以输入<span class="remain_text_textarea">600</span>个字</span>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
       <!-- <div class="border border0"></div>-->
        <div class="buttons" style="margin:20px 0 0;">
            <input type="button" onclick="saveReply();" value="保存" class="redButton"/>
            <input type="button" onclick="clearActive();" value="清除内容" class="redButton"/>
        </div>
    </div>
    </div>

</form>
</#macro>
<#macro script>
<script src="${rc.contextPath}/wechat/js/bootstrap-switch.js"></script>
<script type="text/javascript">
	$(function(){
	<#if message?exists>
		layer.open({
            title: "提示",
            content: "${message}"
        });
	</#if>
	<#if autoReply.materialId?exists>
        $.ajax({
            url: "${rc.contextPath}/wechat/material/getMaterial",
            data: {"id":"${autoReply.materialId}"},
            success: function (data) {
            	addNews(data);
            }
        });
	</#if>
	<#if autoReply.msgType=="text">
    	$(".tabContiner_nav.borderbottom li[href='#tab_text']").click();
    <#else>
    	$(".tabContiner_nav.borderbottom li[href='#tab_news']").click();
    </#if>
	});
	
	function changeType(type){
		$("input[name='type']").val(type);
		$("#autoReplyForm").attr("action","${rc.contextPath}/wechat/autoreply/index");
		$("#autoReplyForm").submit();
	}
	
	//保存回复内容到数据库
	function saveReply(){
		//判断字段回复开启状态，如果开启启动当前活跃状态的回复类型
		if($(".ace.ace-switch.ace-switch-5").is(":checked")){
			$("input[name='status']").val("1");
		}else{
			$("input[name='status']").val("0");
		}
        var tab_text  = $(".tabContiner_nav.borderbottom li[href='#tab_text']");
		if(tab_text.hasClass("active")){
			$("input[name='msgType']").val("text");
		}else{
			$("input[name='msgType']").val("news");
		}
		//提交表单，保存信息
		$("#autoReplyForm").submit();
	}

	//清除当前活跃界面选素材或文本
	function clearActive(){
        var tab_text  = $(".tabContiner_nav.borderbottom li[href='#tab_text']");
		if(tab_text.hasClass("active")){
	    	layer.confirm("确定清除内容吗？",{},
				function(index){
					layer.close(index);
					var content = $("textarea[id='content']",$("#tab_text"));
					content.val("");
				},
				function(index){
					layer.close(index);
				}
			);
		}else{
			removeNews();
		}
	}
	
	//填充已选素材
   function addNews(data){
		if(typeof data == "string"){
			data = eval("("+data+")");
			if(data==null){
				return;
			}
		}
		
		$("input[name='materialId']").val(data.id);
		$("input[name='msgType']").val("news");
     	//单图文
     	if(data.type.id=='9003'){
	     	if(data.children==undefined||data.children.length==0){
				var news = "";
				var digest = data.digest;
				if(digest == undefined){
					digest = "";
				}
				news+="<ul class='tuwen' id='ul_"+tab_news+"'>";
				news+="		<li><p>"+data.title+"</p>";
				news+="			<img src='" + data["url"]+ "'>   <p>"+digest+"</p>";
				news+="			<div class='tuwenChild'>   </div>";
				news+="			<div class='tuwenOperate'>";
                news+="				<a href='${rc.contextPath}/wechat/material/edit/"+data["id"]+"?cmd=autoreply'>";
                news+="				<i value='编辑' class='icon-edit iconHover'></i>";
                news+="				</a>";
				news+="				<a href='javascript:void(0);' onclick='removeNews();'>";
				news+="				<i value='删除' class='icon-remove iconHover'></i>";
				news+="				</a>";
				news+="			</div>";
				news+="		</li>";
				news+="</ul>";
			}else if(data.children!=undefined&&data.children.length>0){
				//多图文
				var news = "";
				news+="<ul class='tuwen' id='ul_"+tab_news+"'>";
				news+="		<li><p>"+data.title+"</p>";
				news+="			<img src='" + data["url"]+ "'>";
				news+="			<div class='tuwenChild'>   ";
				for(var i=0;i<data.children.length;i++){
					var child = data.children[i];
					news+="<div class='animatePicture pictureCheck'><label class='marginTop20'>"+child.title+"</label><div class='animatePicture_chil'><img  width='100px' height='110px' src='"+child.url+"'/></div></div>";
				}
				news+="</div>";
				news+="			<div class='tuwenOperate'>";
                news+="				<a href='${rc.contextPath}/wechat/material/edit/"+data["id"]+"?cmd=autoreply'>";
                news+="				<i value='编辑' class='icon-edit iconHover'></i>";
                news+="				</a>";
				news+="				<a href='javascript:void(0);' onclick='removeNews();'>";
				news+="				<i value='删除' class='icon-remove iconHover'></i>";
				news+="				</a>";
				news+="			</div>";
				news+="		</li>";
				news+="</ul>";
			}
		}else if(data.type.id=='9002'){
			var news = "";
			news+="<ul class='tuwen'>";
			news+="		<li>";
			news+="			<img src='" + data["url"]+ "'>";
			news+="			<div class='tuwenOperate'>";
			news+="				<a href='javascript:void(0);' onclick='removeNews();'>";
			news+="				<i value='删除' class='icon-remove iconHover'></i>";
			news+="				</a>";
			news+="			</div>";
			news+="		</li>";
			news+="</ul>";
		}
		$("#tab_news").html(news);
    }
    
    //清除已选素材
    function removeNews(){
    	layer.confirm("确定清除内容吗？",{},
			function(index){
				layer.close(index);
		     	$("input[name='materialId']").val("");
		     	$("input[name='msgType']").val("");
				var news_continer = "";
				news_continer+="<div class='addPi1'>";
				news_continer+="	<div style='width:100%;' class='floatL addPar' style='width: 100px;margin:150px auto;display: block;'>";
				news_continer+="		<a href='javascript:void(0);' style='width: 100px;margin:150px auto;display: block;'>";
				news_continer+="			<input type='file' name='image' style='width:68px;' id='image_tab' class='floatL' />";
				news_continer+="		</a>";
				news_continer+="	</div>";
				news_continer+="</div>";
				news_continer+="<div class='addPi1'>";
				news_continer+="	<div style='width: 100%;' class='floatL addPar'>";
				news_continer+="		<a href='javascript:void(0);' onclick='layerPage.init();' class='selectFromSucai'>";
				news_continer+="			<i class='icon-plus add'></i>";
				news_continer+="			<span class='addTitle'>从素材库选择</span>";
				news_continer+="		</a>";
				news_continer+="	</div>";
				news_continer+="</div>";
		    	$("#tab_news").html(news_continer);
		    	//监听选择图片上传
		    	//$("#image_tab").unbind("change");
			    $("#image_tab").on("change", function () {
			        $("#autoReplyForm").ajaxSubmit({
				        type: "POST",
				        url: "${rc.contextPath}/wechat/material/uploadImage",
				        dataType: "json",
				        success: function (data) {
				            if (data.code == 'success') {
				            	$("input[name='materialId']").val(data.materialId);
                				$("input[name='msgType']").val("image");
				                var layerAlert = new ElasticLayer("alert", data.message, {title: "上传成功", dialog: true, time: 2000});
				                layerAlert.init();
				                var news="<ul class='tuwen' id='ul_"+tab_news+"'>";
								news+="		<li>";
								news+="			<img src='" + data["url"]+ "'>";
								news+="			<div class='tuwenOperate'>";
								news+="				<a href='javascript:void(0);' onclick='removeNews();'>";
								news+="				<i value='删除' class='icon-remove iconHover'></i>";
								news+="				</a>";
								news+="			</div>";
								news+="		</li>";
								news+="</ul>";
								$("#tab_news").html(news);
				            }else {
				                var layerAlert = new ElasticLayer("alert", data.message, {title: "保存失败", dialog: true, time: 2000});
				                layerAlert.init();
				            }
				         }
					});
				});
			},
			function(index){
				layer.close(index);
			}
		);
    }
    
	$(function () {
		var maxlen = 600;
		var value = $("#content").val();
		var length = value.length;
		if (length > maxlen) {
			$(this).val(value.substring(0, maxlen))
		}
		var remainLenth = maxlen - length;
		$("#content").parents(".textareaConsulte").find(
				".remain_text_textarea").text(remainLenth);
    	//监听选择图片上传
	    $("#image_tab").on("change", function () {
	        $("#autoReplyForm").ajaxSubmit({
		        type: "POST",
		        url: "${rc.contextPath}/wechat/material/uploadImage",
		        dataType: "json",
		        success: function (data) {
		            if (data.code == 'success') {
		            	$("input[name='materialId']").val(data.materialId);
        				$("input[name='msgType']").val("image");
		                var layerAlert = new ElasticLayer("alert", data.message, {title: "上传成功", dialog: true, time: 2000});
		                layerAlert.init();
		                //addNews(data);
		                var news="<ul class='tuwen' id='ul_"+tab_news+"'>";
						news+="		<li>";
						news+="			<img src='" + data["url"]+ "'>";
						news+="			<div class='tuwenOperate'>";
						news+="				<a href='javascript:void(0);' onclick='removeNews();'>";
						news+="				<i value='删除' class='icon-remove iconHover'></i>";
						news+="				</a>";
						news+="			</div>";
						news+="		</li>";
						news+="</ul>";
						$("#tab_news").html(news);
		            }else {
		                var layerAlert = new ElasticLayer("alert", data.message, {title: "保存失败", dialog: true, time: 2000});
		                layerAlert.init();
		            }
		         }
			});
		});
	});
	
	//选择数据库素材
    var layerPage = new ElasticLayer("pageWindow", "${rc.contextPath}/wechat/material/selectFromMaterial?type=news", 
    	{
	        type: "pageWindow",
	        area: ["60%", "80%"],
	        dialog: true,
	        offset: ["5%", "20%"]
	    } ,
	    {success: function (layero, index) {
        	var body = layer.getChildFrame('body', index);
        	$(".selectConfirm_button", $(body)).unbind("click");
            $(".selectConfirm_button", $(body)).click(function () {
                var body = layer.getChildFrame('body', index);
                var nodes = $(".select_tuwen .selectPageShadow:has(i)", $(body));
                var parent = $(nodes).parents("li:first");
                //返回值 素材id 和 mediaId
                var id = $("input[name='id']", $(parent)).val();
                if (id) {
                    $.ajax({
                        url: "${rc.contextPath}/wechat/material/getMaterial",
                        data: {"id": id},
                        success: function (data) {
                        	addNews(data);
                        }
                    });
                }
                layer.close(index);
            });
            $(".selectCancel_button", $(body)).click(function () {
                layer.close(index);
            });
        }
        
    });
    $(function(){
    	$(".addPiContinaer").undelegate(".tuwen>li","mouseenter");
        $(".addPiContinaer").delegate(".tuwen>li","mouseenter",function(){
			$(this).find(".tuwenOperate").css({"display":"block"});
		});
		$(".addPiContinaer").undelegate(".tuwen>li","mouseleave");
		$(".addPiContinaer").delegate(".tuwen>li","mouseleave",function(){
			$(this).find(".tuwenOperate").css({"display":"none"});
		});
    });
</script>
</#macro>