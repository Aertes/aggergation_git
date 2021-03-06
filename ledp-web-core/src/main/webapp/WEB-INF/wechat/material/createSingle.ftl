<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#assign tag=JspTaglibs["/WEB-INF/taglib/permission.tld"]>
<#include "/wechat/layouts/main.ftl"/>
<@html></@html>
<#macro style>
<style>
.Validform_checktip{
	color:red
}
.title{
	max-height: 30px;
    overflow: hidden;
    word-break: break-all;
    word-wrap: break-word;
}
.digest{
	max-height: 30px;
    overflow: hidden;
    word-break: break-all;
    word-wrap: break-word;
}
.tuwenNewDiv{
	padding:0 10px 10px 0px;
}
.tuwenNew{
	margin-left:1%;
}
.tuwenNew .tuwen_option{
	margin:10px 20px;
}
.tuwen_option label{
	margin:0;
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
			<span>基础工具</span>
			<span>&gt;&gt;</span>
			<span><a href="${rc.contextPath}/wechat/material/publicno">素材中心</a></span>
			<span>&gt;&gt;</span>
			<span>新建图文信息</span>
		</li>
	</ul>
	<div class="tabContainer marginLeft25 marginTop10">
		<ul class="tabContiner_nav borderbottom">
			<li class="active" href="#page1"><a>图文信息</a></li>
		</ul>
		<div class="tabContianer_con">
			<div id="page1" style="display: block;" class="messagesend">
				<div class="tuwenNewDiv">
					<div class="floatL tuwenResult">
						<h1 class="title">标题</h1>
						<div class="noImg">
							<img width="100%" height="121px" src=""/>
						</div>
						<p class="textAlign digest">您上传的内容</p>
					</div>
					<form id="materialForm" class="validateCheck" method="post" enctype="multipart/form-data">
						<input type="hidden" name="mediaId" id="mediaId" />
						<div class="floatL tuwenNew">
							<div class="tuwen_option">
								<label>标题：</label> <input type="text" placeholder="请输入1-64长度的标题" checkdata="notNull" name="title" id="news_title" maxlength="64" />
								<span class='messageCheckP' style='margin-left:45px;'></span>
							</div>
							<div class="tuwen_option">
								<label>作者：</label> <input type="text" name="author" placeholder="请输入1-8长度的作者名称" checkdata="notNull" maxlength="8" />
								<span class='messageCheckP' style='margin-left:45px;'></span>
							</div>
							<div class="tuwen_option">
								<label>封面：</label> <input style="line-height:20px;height:20px;" type="file" name="image" id="upload_image" value="上传"/>
								 <input type="button" value="从素材库选择"
									class="redButton selectFromSucai" style="margin:20px 0 0;" />
									<input type="hidden" checkdata="notNull" name="thumbMediaId" id="thumbMediaId"/>
									<span class='messageCheckP'></span>
									<input type="hidden" name="filepath" id="filepath"/>
									<input type="hidden" name="url" id="imageurl"/>
							</div>
							<div class="tuwen_option" style="margin:0;">
								<div class="floatL uploadImg"></div>
								<span class="floatL paddingLeft30">（大图片建议尺寸：900像素 * 500像素）</span>
								<p class="clear paddingLeft30" style="line-height: 10px;">
									<label> <input type="checkbox" name="showCoverPic" class="ace"> <span
										class="lbl" style="font-size: 12px;">封面图片显示在正文中</span>
									</label>
								</p>
							</div>
							<div class="tuwen_option">
								<label class="displayBlock">摘要：</label>
								<textarea name="digest" placeholder="请输入1-120长度的摘要" id="digest" maxlength="120" checkdata="notNull"></textarea>
								<span class='messageCheckP'></span>
							</div>
							<div class="tuwen_option">
								<label class="displayBlock">正文：</label>
								<textarea id="editor_id" name="content" style="width:670px;height:300px;" maxlength="20000" checkdata="notNull"></textarea>
								<span class='messageCheckP'></span>
							</div>
							<div class="tuwen_option">
								<label>原文链接（选填）</label> <input type="text"  placeholder="请输入http://或https://开头的链接" checkdata="originalUrl" name="originalUrl" />
								<span class='messageCheckP' style='margin-left:120px;'></span>
							</div>
						</div>
					</form>
				</div>
				<div class="textAlign newbutton">
					<input type="button" class="redButton" onclick="save();" value="保 存" /> 
					<input type="button" class="redButton" onclick="preview();" value="预 览" />
					<input type="button" class="redButton" onclick="window.location.href='${rc.contextPath}/wechat/material/publicno'" value="返回" />
				</div>
			</div>
		</div>
	</div>
</div>
</#macro>
<#macro script>
<script type="text/javascript" src="${rc.contextPath}/wechat/js/report/highcharts.js" ></script>
		<script type="text/javascript" src="${rc.contextPath}/wechat/js/report/highcharts-more.js" ></script>
		<script type="text/javascript" src="${rc.contextPath}/wechat/js/report/exporting.js" ></script>
		<script type="text/javascript" src="${rc.contextPath}/wechat/js/report/gray.js" ></script>
		<script type="text/javascript" src="${rc.contextPath}/wechat/js/report/Chart.js" ></script>
		<script type="text/javascript" src="${rc.contextPath}/wechat/js/Validform_v5.3.2_min.js" ></script>
		<script type="text/javascript" src="${rc.contextPath}/wechat/js/validateCheck.js" ></script>
		<script type="text/javascript" src="${rc.contextPath}/wechat/js/checkdata.js" ></script>
		<script charset="utf-8" src="${rc.contextPath }/wechat/js/kindeditor-4.1.10/kindeditor.js"></script>
		<script charset="utf-8" src="${rc.contextPath }/wechat/js/kindeditor-4.1.10/lang/zh_CN.js"></script>
		<script>
		KindEditor.lang({material : '选择图片素材'});
		KindEditor.ready(function(K) {
                window.editor = K.create('#editor_id',{
                	themeType : 'simple',
                	allowImageUpload : false, //不允许上传图片
                	items : [
						'undo','redo','preview','|','fontname', 'fontsize','hilitecolor','forecolor','bold', 'italic', 'underline',
						'removeformat','quickformat', '|', 'justifyleft', 'justifycenter', 'justifyright', 'insertorderedlist',
						'insertunorderedlist', '|','material', 'emoticons','table','link','|','fullscreen'],
                	afterChange: function(){
                		this.sync();
                		$("#editor_id").val(this.html());
                	}
                });
        });
        
		$(function(){
			$.plugin.validate();
			var tips = null;
			$(".iconHover").hover(function(){
				var value = $(this).attr("value");
				tips = layer.tips(value, this, {
				    tips: [3, '#666']
				});
			},function(){
				layer.close(tips);
			});
			
			<#if message?exists>
			layer.open({
	            title: "提示",
	            content: "${message}"
	        });
			</#if>
			
			//从素材库选择
			var layerPage = new ElasticLayer("pageWindow","${rc.contextPath}/wechat/material/selectFromMaterial?type=image",{type:"pageWindow",area:["60%" ,"80%" ],dialog : true,offset  : ["5%","20%"]},{
				success : function(layero ,index){
					var body = layer.getChildFrame('body', index);
			    	$(".selectConfirm_button",$(body)).click(function(){
			    		var body = layer.getChildFrame('body', index);
			    		 var nodes = $(".select_tuwen .selectPageShadow:has(i)",$(body));
			    		 var parent = $(nodes).parents("li:first");
			    		 //返回值 素材id 和 mediaId
			    		 var id = $("input[name='id']",$(parent)).val();
			    		 var mediaId = $("input[name='mediaId']",$(parent)).val();
			    		 $("input[name='thumbMediaId']").val(mediaId);
			    		 //清空文件名
			    		 $("#upload_image").val('');
			    		 //根据素材id查询素材url
			    		 $.ajax({
					           type: "POST",
					           timeout:5000,
					           dataType: "json",
					           url: "${rc.contextPath}/wechat/material/searchImage",
					           data: {
					           		"materialId":id
					           },
					           success: function(value){
					           		var data = value.data;
					           		if(data && data.length > 0){
					           			var obj = data[0];
					           			var url = obj.url;
					           			$(".noImg").find("img").attr("src",url);
					           			$("#imageurl").val(url);
				     					$("#filepath").val(obj.filepath);
					           		}
					           },
					           error: function(){
					           		layer.alert("系统繁忙,请稍侯再试");
					           }
					       });
			    		layer.close(index);
			    	});
			    	
			    	$(".selectCancel_button",$(body)).click(function(){
			    		layer.close(index);
			    	});
				}
			})
			$(".selectFromSucai").click(function(){
				layerPage.init();
			});
			
			//标题
			$('#news_title').keyup(function() {
				$(".tuwenResult").find("h1").html($(this).val())
			});
			//摘要
			$('#digest').keyup(function() {
				$(".tuwenResult").find("p").html($(this).val())
			});
			//图片
			$('#upload_image').change(function() {
				var url = "${rc.contextPath}/wechat/material/uploadImage";
				$("#materialForm").ajaxSubmit({
					type: "POST",
					url:url,
					dataType: "json",
				    success: function(data){
				     	if(data.code=="success"){
				     		$("#thumbMediaId").val(data.mediaId);
				     		$("#imageurl").val(data.url);
				     		$(".noImg").find("img").attr("src",data.url);
				     		$("#filepath").val(data.filepath);
				     	}else{
				     		layer.alert(data.message);
				     	}
					},
					error: function(){
 				   	    layer.alert("图片上传失败,请刷新后重试");
 				    }
				});
			});
			
		});
		
		function save(){
			var isRight = $(".tuwenNew").checkDataDom();
			if(isRight){
				$(isRight).focus();
				return false;
			}else{
				$("#editor_id").val($("#editor_id").val().replace(/\"/g,"'"));
				var url = "${rc.contextPath}/wechat/material/save";
				$("#materialForm").ajaxSubmit({
					type: "POST",
					url:url,
					dataType: "json",
				    success: function(data){
				     	if(data.code=='success'){
				     		layer.alert(data.message);
				     		$("#mediaId").val(data.mediaId);
				     		setTimeout(function(){
				     			window.location.href="${rc.contextPath}/wechat/material/publicno"
				     		},3000);
							/*$("#materialForm")[0].reset();
							$(".tuwenResult").find("h1").html("标题");
							$(".tuwenResult").find("p").html("您上传的内容");
							$(".noImg").find("img").attr("src","");*/
				   		 }
				    	else{
				    		layer.alert(data.message);
				    	}
					},
					error: function(){
						layer.alert("系统繁忙,请稍候再试");
					}
				});
			}
		}
		
		function saveSingle(){
			var isRight = $(".tuwenNew").checkDataDom();
			if(isRight){
				$(isRight).focus();
				return false;
			}else{
				$("#editor_id").val($("#editor_id").val().replace(/\"/g,"'"));
				var url = "${rc.contextPath}/wechat/material/save";
				$("#materialForm").ajaxSubmit({
					type: "POST",
					url:url,
					dataType: "json",
				    success: function(data){
				     	if(data.code=='success'){
				     		console.log(data.message);
				     		$("#mediaId").val(data.mediaId);
				   		 }
				    	else{
				    		console.log(data.message);
				    	}
					},
					error: function(){
						console.log("系统繁忙,请稍候再试");
					}
				});
			}
		}
		
    	function preview() {
    		//保存
    		var flag = saveSingle();
    		if(flag != false){
	            index = layer.open({
	                type: 1,
	                skin: "layui-layer-rim", //加上边框
	                area: ["400px" , "200px"], //宽高
	                 offset : ["20%","40%"],
	                content: "<input id='wxname' placeholder='请输入当前公众号下粉丝的微信号' style='display:block;width:90%;margin:10px auto;height:30px;padding:0 10px;border:1px solid #d9d9d9'><div style='line-height:50px;text-align:center;'><button onclick='ok();' class='redButton' style='margin-right:10px' >确定</button><button class='redButton' onclick='cancel();'>取消</button></div>"
	            });
	            JPlaceHolder.init();
            }
        }

       function ok() {
            var towxname = $("#wxname").val();
            var mediaId = $("#mediaId").val();
            if(mediaId == undefined){
            	layer.alert("请先保存素材！");
            }
            if(wxname) {
                $.ajax({
                    type : "POST",
                    url : "${rc.contextPath}/wechat/material/preview",
                    data : {
                    	"towxname":towxname,
                    	"mediaId":mediaId
                    },
                    success : function(data) {
                    	layer.close(index);
                        if(data) {
                            layer.alert(data["message"]); // 这个提示不一定正确
                            //console.debug(data["params"]); // 这个是正确的
                        }
                    }
                });
            } else {
                layer.alert('请输入微信号!');
            }
        }

       function cancel() {
            layer.close(index);
        }
		
		</script>
</#macro>