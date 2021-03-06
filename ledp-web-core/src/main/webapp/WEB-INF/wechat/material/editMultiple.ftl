<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#assign tag=JspTaglibs["/WEB-INF/taglib/permission.tld"]>
<#include "/wechat/layouts/main.ftl"/>
<@html></@html>
<#macro style>
<style>
.icon-edit {
  display: block;
  float: left;
  width: 100%;
  font-size: 25px;
  margin-top: 50px;
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
.subtitle{
	max-height: 30px;
	width: 120px;
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
		try{ace.settings.check('breadcrumbs' , 'fixed')}catch(e){}
	</script>

	<ul class="breadcrumb">
		<li>
			<i class="icon-home home-icon"></i>
			<span>基础工具</span>
			<span>&gt;&gt;</span>
			<span><a href="${rc.contextPath}/wechat/material/publicno">素材中心</a></span>
			<span>&gt;&gt;</span>
			<span>编辑图文信息</span>
		</li>
	</ul>
	<div class="tabContainer marginLeft25 marginTop10 tabChange">
		<ul class="tabContiner_nav borderbottom">
			<li class="active" href="#page1">
				<a >图文信息</a>
			</li>
		</ul>
		<div class="tabContianer_con">
			<div id="page1" style="display: block;" class="messagesend">
				<div class="tuwenNewDiv">
					<div class="floatL tuwenResult addLotTu" >
						<div class="position-relative tutu pictureCheck dataitem" index=0 id="item0" >
							<input type="hidden" id="id0" value="${material.id}"/>
							<input type="hidden" checkdata="notNull" checkName="title" id="title0" value="${material.title?if_exists}"/>
							<input type="hidden" id="author0" value="${material.author?if_exists}"/>
							<input type="hidden" id="showCoverPic0" value="${material.showCoverPic?if_exists}"/>
							<input type="hidden" id="digest0" value="${material.digest?if_exists}"/>
							<input type="hidden" checkdata="notNull" checkName="content" id="content0" value="${material.content?if_exists?html}"/>
							<input type="hidden" id="originalUrl0"  checkdata="originalUrl" checkName="originalUrl"  value="${material.originalUrl?if_exists}"/>
							<input type='hidden' checkdata="notNull" checkName="thumbMediaId"  value="${material.thumbMediaId?if_exists}" id='thumbMediaId0'/>
							<input type='hidden' value="${material.url?if_exists}" id='url0'/>
							<input type='hidden' value="${material.filepath?if_exists}" id='filepath0'/>
							<h1  class="title">${material.title}</h1>
							<div class="noImg"><img width="100%" height="121px" src="${material.url?if_exists}"/></div>
							<p class="textAlign digest">您上传的内容</p>
							<div class='shadowHead'>
								<a class='icon-edit' title='编辑' style="width: 100%;"></a>
							</div>
						</div>
						<#if (material.children?size > 0)>
							<#list material.children?sort_by("sort")  as child>
								<div class='animatePicture pictureCheck dataitem' index=${child_index+1} id='item${child_index+1}'>
									<input type='hidden' value="${child.id}" id='id${child_index+1}'/>
									<input type='hidden' value="${child.title?if_exists}" checkdata="notNull" checkName="title"  id='title${child_index+1}'/>
									<input type='hidden' value="${child.author?if_exists}" id='author${child_index+1}'/>
									<input type='hidden' value="${child.showCoverPic?if_exists}" id='showCoverPic${child_index+1}'/>
									<input type='hidden' value="${child.digest?if_exists}" id='digest${child_index+1}'/>
									<input type='hidden' value="${child.content?if_exists?html}" checkdata="notNull" checkName="content"  id='content${child_index+1}'/>
									<input type='hidden' value="${child.originalUrl?if_exists}" checkdata="originalUrl" checkName="originalUrl"  id='originalUrl${child_index+1}'/>
									<input type='hidden' value="${child.thumbMediaId?if_exists}" checkdata="notNull" checkName="thumbMediaId" id='thumbMediaId${child_index+1}'/>
									<input type='hidden' value="${child.url?if_exists}" id='url${child_index+1}'/>
									<input type='hidden' value="${child.filepath?if_exists}" id='filepath${child_index+1}'/>
									<label class='marginTop20 subtitle'>${child.title?if_exists}</label>
									<div class='animatePicture_chil'><img width='100px' height='110px' src="${child.url?if_exists}"/></div>
									<div class='shadow'>
										<a class='icon-edit' title='编辑'  style='width: 100%;'></a>
									</div>
								</div>
							</#list>
						</#if>
					</div>
					
					<div class="floatL tuwenNew" id="pictureForId0">
						<div class="tuwen_option">
							<label>标题：</label>
							<input type="text" id="title" placeholder="请输入1-64长度的标题" maxlength="64" value="${material.title?if_exists}" />
							<span class='messageCheckP' style='margin-left:45px;'></span>
						</div>
						<div class="tuwen_option">
							<label>作者：</label>
							<input type="text" id="author" placeholder="请输入1-8长度的作者名称" maxlength="8" value="${material.author?if_exists}" />
							<span class='messageCheckP' style='margin-left:45px;'></span>
						</div>
						<div class="tuwen_option">
							<label>封面：</label>
							<form id="materialForm" method="post" enctype="multipart/form-data">
								<input type="file" style="line-height:20px;height:20px;" id="upload_image" name="image" value="上传" />
								<input type="button" value="从素材库选择" class="redButton selectFromSucai" />
								<input type="hidden" name="thumbMediaId" id="thumbMediaId"/>
							</form>
						</div>
						<div class="tuwen_option">
							<div class="floatL uploadImg">
							</div>
							<span class="floatL paddingLeft30">（大图片建议尺寸：900像素 * 500像素）</span>
							<p class="clear paddingLeft30" style="line-height: 10px;">
								<label>
									<input type="checkbox" id="showCoverPic" class="ace">
									<span class="lbl" style="font-size: 12px;">封面图片显示在正文中</span>
								</label>
							</p>
						</div>
						<div class="tuwen_option">
							<label class="displayBlock">摘要：</label>
							<textarea placeholder="请输入1-120长度的摘要" id="digest" maxlength="120"> ${material.digest?if_exists}</textarea>
						</div>
						<div class="tuwen_option">
							<label class="displayBlock">正文：</label>
							<textarea id="editor_id" name="content" style="width:670px;height:300px;" maxlength="20000">${material.content?if_exists}</textarea>
							<span class='messageCheckP'></span>
						</div>
						<div class="tuwen_option">
							<label>原文链接（选填）</label>
							<input type="text"  placeholder="请输入http://或https://开头的链接"  value="${material.originalUrl?if_exists}" checkdata="originalUrl" id="originalUrl" />
							<span class='messageCheckP' style='margin-left:120px;'></span>
						</div>
					</div>
				</div>
				<div class="textAlign newbutton">
					<input type="button" class="redButton saveMulite" onclick="save();" value="保 存" />
					<input type="button" class="redButton" onclick="preview();" value="预 览" /> 
					<input type="button" class="redButton" onclick="saveAndMass();" value="保存并群发" />
					<#if cmd="autoreply">
						<input type="button" class="redButton" onclick="window.location.href='${rc.contextPath}/wechat/autoreply/index'" value="返回" />
					<#else>
                        <input type="button" class="redButton" onclick="window.location.href='${rc.contextPath}/wechat/material/publicno'" value="返回" />
					</#if>
				</div>
			</div>
		</div>
	</div>
</div>
<form id="multipleForm" action="${rc.contextPath}/wechat/material/updateMultipleAndMass"  method="POST">
	<input type="hidden" id="MediaId" name="MediaId" value="${material.mediaId?if_exists}"/>
	<input type="hidden" id="contentsJson" name="contentsJson"/>
	<input type="hidden" id="menuid" name="menuid" value="${menuid?if_exists}"/>
	<input type="hidden" id="id" name="materialId" value="${materialId?if_exists}"/>
</form>
</#macro>
<#macro script>
<script type="text/javascript" src="${rc.contextPath}/wechat/js/report/highcharts.js" ></script>
		<script type="text/javascript" src="${rc.contextPath}/wechat/js/report/highcharts-more.js" ></script>
		<script type="text/javascript" src="${rc.contextPath}/wechat/js/report/exporting.js" ></script>
		<script type="text/javascript" src="${rc.contextPath}/wechat/js/report/gray.js" ></script>
		<script type="text/javascript" src="${rc.contextPath}/wechat/js/report/Chart.js" ></script>
		<script type="text/javascript" src="${rc.contextPath}/wechat/js/checkdata.js" ></script>
		<script charset="utf-8" src="${rc.contextPath }/wechat/js/kindeditor-4.1.10/kindeditor.js"></script>
		<script charset="utf-8" src="${rc.contextPath }/wechat/js/kindeditor-4.1.10/lang/zh_CN.js"></script>
<script>
		//总图文数
		var total = ${material.children?size};
		//当前实际index的值
		var curIndex = 0;
		//添加时用的索引
		var addIndex = '${material.children?size}';
		KindEditor.lang({material : '选择图片素材'});
		KindEditor.ready(function(K) {
                window.editor = K.create('#editor_id',{
                	themeType : 'simple',
                	allowImageUpload : false, //不允许上传图片
                	items : [
						'undo','redo','preview','|','fontname', 'fontsize','hilitecolor','forecolor','bold', 'italic', 'underline',
						'removeformat','quickformat', '|', 'justifyleft', 'justifycenter', 'justifyright', 'insertorderedlist',
						'insertunorderedlist', '|','material', 'emoticons','table','link','|','fullscreen'],
                	afterBlur: function(){
                		this.sync();
                		$("#content"+curIndex).val(K('#editor_id').val());
                	}
                });
        });
        
		$(function(){
			var tips = null;
			//设置第一个图文
			var firstChk = $("#showCoverPic0").val();
			if(firstChk=="on"){
				$("#showCoverPic").attr("checked",true);
			}
			$("#showCoverPic").val();
			
			$(".iconHover").hover(function(){
				var value = $(this).attr("value");
				tips = layer.tips(value, this, {
				    tips: [3, '#666']
				});
			},function(){
				layer.close(tips);
			});
			
			//从素材库选择
			var layerPage = new ElasticLayer("pageWindow","${rc.contextPath}/wechat/material/selectFromMaterial?type=image",{type:"pageWindow",area:["60%" ,"80%" ],dialog : true,offset  : ["5%","20%"]},{
				success : function(layero ,index){
					var body = layer.getChildFrame('body', index);
			    	$(".selectConfirm_button",$(body)).click(function(){
			    		var body = layer.getChildFrame('body', index);
			    		 var nodes = $(".select_tuwen .selectPageShadow:has(i)",$(body));
			    		 var parent = $(nodes).parents("li:first");
			    		 var id = $("input[name='id']",$(parent)).val();
			    		 var mediaId = $("input[name='mediaId']",$(parent)).val();
			    		 $("#thumbMediaId"+curIndex).val(id);
			    		 //清空文件名
			    		 $("#upload_image").val('');
			    		 //根据素材id查询素材url
			    		 $.ajax({
					           type: "POST",
					           dataType: "json",
					           timeout:5000,
					           url: "${rc.contextPath}/wechat/material/searchImage",
					           data: {
					           		"materialId":id
					           },
					           success: function(value){
					           		var data = value.data;
					           		if(data && data.length > 0){
					           			var obj = data[0];
					           			var url = obj.url;
					           			if(curIndex == 0){
								     		$("#thumbMediaId"+curIndex).val(obj.mediaId);
								     		$(".noImg").find("img").attr("src",obj.url);
								     		$("#url"+curIndex).val(obj.url);
											$("#filepath"+curIndex).val(obj.filepath);
							     		}else{
							     			$("#thumbMediaId"+curIndex).val(obj.mediaId);
							     			$("#item"+curIndex+" .animatePicture_chil").find("img").attr("src",obj.url);
							     			$("#url"+curIndex).val(obj.url);
											$("#filepath"+curIndex).val(obj.filepath);
							     		}
					           		}
					           },
					           error:function(){
					           		layer.alert("系统繁忙,请稍候再试");
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
			
			$("a[class='icon-plus']",$(".addLotTu")).click(function(){
				$(".messageCheckP").text("");
				//清除原来的值
				clearForm();
				//最多只能加入8条图文
				if(total>=8){
					alert("最多只能加入8条图文");
					return false;
				}
				total++;
				addIndex++;
				curIndex = addIndex;
				
				var $div = $("<div class='animatePicture pictureCheck dataitem' id='item"+addIndex+"'></div>").attr({"index":addIndex});
				var $content = $("<input type='hidden' id='title"+addIndex+"'/><input type='hidden' id='author"+addIndex+"'/><input type='hidden' id='showCoverPic"+addIndex+"'/><input type='hidden' id='digest"+addIndex+"'/><input type='hidden' id='content"+addIndex+"'/><input type='hidden' id='originalUrl"+addIndex+"'/><input type='hidden' id='thumbMediaId"+addIndex+"'/><input type='hidden' id='filepath"+addIndex+"'/><input type='hidden' id='url"+addIndex+"'/><label class='marginTop20 subtitle'>标题</label><div class='animatePicture_chil'><img width='100px' height='110px' src=''/></div>");
				var $shadow = $("<div class='shadow'><a class='icon-edit' title='编辑'  style='width: 100%;'></a></div>");
				
				$div.hover(function(){
					$(this).find(".shadow").addClass("displayBlock").removeClass("displaynone");
				},function(){
					$(this).find(".shadow").addClass("displaynone").removeClass("displayBlock");
				});
				
				$(this).parents(".tuwenAdd:first").before($div.append($content).append($shadow));
				$(".tuwenNew").css("margin-top",$(".animatePicture").length*150);
				
				//删除
				$($shadow).delegate(".icon-remove",'click',function(){
					total--;
					var parent = $(this).parents(".animatePicture:first");
					var delIndex = parent.attr("index");
					deleteMaterialById($("#id"+delIndex).val());
					parent.remove();
					var lastPar = $(".animatePicture:last",$(".tuwenResult"));
					var index = lastPar.attr("index");
					if(index==undefined){
						index = 0;
					}
					curIndex = index;
					$(".tuwenNew").css("margin-top",(total)*150);
					//读取原来的值
					setOrigForm(index);
				});
				//编辑
				$($shadow).delegate(".icon-edit",'click',function(){
					$(".messageCheckP").text("");
					//清除原来的值
					clearForm();
					var parent = $(this).parents(".animatePicture:first");
					var index = parent.attr("index");
					curIndex = index;
					var _index = parent.index();
					$(".pictureCheck:first",$(".tuwenResult")).removeClass("pictureCheck")
					$(".tuwenNew").css("margin-top",_index*150);
					setOrigForm(index);
				});
				
			});
			
			$(".tutu").hover(function(){
				$(this).find(".shadowHead").addClass("displayBlock").removeClass("displaynone");
			},function(){
				$(this).find(".shadowHead").addClass("displaynone").removeClass("displayBlock");
			});
			
			$(".shadowHead>.icon-edit").click(function(){
			    $(".messageCheckP").text("");
				//清除原来的值
				clearForm();
				var parent = $(this).parents(".tutu:first");
				var index = parent.attr("index");
				curIndex = index;
				$(".tuwenNew").css("margin-top",index*150);
				$(".pictureCheck:first",$(".tuwenResult")).removeClass("pictureCheck");
				setOrigForm(index);
			});
			
			
			$(".newGroup").click(function(){
				var $li = $("<li></li>");
				var $input = $("<input type='text' class='newGroupInput' width='90%' />");
				$input.blur(function(){
					var value = $(this).val();
					var parent = $(this).parent();
					parent.children();
					$(parent).text(value);
				});
				$li.append($input);
				$(this).before($li);
			});
			
			$(".animatePicture").hover(function(){
				$(this).find(".shadow").addClass("displayBlock").removeClass("displaynone");
			},function(){
				$(this).find(".shadow").addClass("displaynone").removeClass("displayBlock");
			});
			
			//删除
			$(".shadow").delegate(".icon-remove",'click',function(){
				total--;
				var parent = $(this).parents(".animatePicture:first");
				var delIndex = parent.attr("index");
				deleteMaterialById($("#id"+delIndex).val());
				parent.remove();
				var lastPar = $(".animatePicture:last",$(".tuwenResult"));
				var index = lastPar.attr("index");
				if(index==undefined){
					index = 0;
				}
				curIndex = index;
				$(".tuwenNew").css("margin-top",(total)*150);
				//读取原来的值
				setOrigForm(index);
			});
			//编辑
			$(".shadow").delegate(".icon-edit",'click',function(){
				$(".messageCheckP").text("");
				//清除原来的值
				clearForm();
				var parent = $(this).parents(".animatePicture:first");
				var index = parent.attr("index");
				curIndex = index;
				var _index = parent.index();
				$(".pictureCheck:first",$(".tuwenResult")).removeClass("pictureCheck")
				$(".tuwenNew").css("margin-top",_index*150);
				setOrigForm(index);
			});
			
			$('#title').keyup(function() {
				$("#title"+curIndex).val($(this).val());
				$("#item"+curIndex).find("label,h1").html($(this).val());
			});
			$('#author').keyup(function() {
				$("#author"+curIndex).val($(this).val());
			});
			$('#showCoverPic').click(function() {
				if($("#showCoverPic").is(":checked")==true){
					$("#showCoverPic"+curIndex).val("on");
				}else{
					$("#showCoverPic"+curIndex).val("off");
				}
			});
			$('#digest').keyup(function() {
				$("#digest"+curIndex).val($(this).val());
			});
			$('#content').keyup(function() {
				$("#content"+curIndex).val($(this).val());
			});
			$('#originalUrl').keyup(function() {
				$("#originalUrl"+curIndex).val($(this).val());
			});
			
			//图片上传
			$('#upload_image').change(function() {
				var url = "${rc.contextPath}/wechat/material/uploadImage";
				$("#materialForm").ajaxSubmit({
					type: "POST",
					url:url,
					dataType: "json",
				    success: function(data){
				     	if(data.code=="success"){
				     		if(curIndex == 0){
					     		$("#thumbMediaId0").val(data.mediaId);
					     		$(".noImg").find("img").attr("src",data.url);
					     		$("#url0").val(data.url);
								$("#filepath0").val(data.filepath);
				     		}else{
				     			$("#thumbMediaId"+curIndex).val(data.mediaId);
				     			$("#item"+curIndex+" .animatePicture_chil").find("img").attr("src",data.url);
				     			$("#url"+curIndex).val(data.url);
								$("#filepath"+curIndex).val(data.filepath);
				     		}
				     	}else{
				     		layer.alert(data.message);
				     	}
					},
					error: function(){
						layer.alert("系统繁忙,请稍候再试");
					}
				});
			});
			
		});
		
		/**
		 * 保存多图文
		 *
		 */
		function save(){
			$(".messageCheckP").text("");
			var isRight = $(".tuwenNewDiv>.tuwenResult").checkDataDom();
			if(isRight){
				var parent = $(isRight).parents(".dataitem:first");
				var index = $(parent).attr("index"), checkName = $(isRight).attr("checkName");
				parent.find(".icon-edit").trigger("click");
				var errorMsg = "";
				switch (checkName) {
					case "title":
						errorMsg = "请输入1-64位的标题";
						break;
					case "content":
						errorMsg = "请输入1-1700的内容";
						break;
					case "thumbMediaId":
						errorMsg ="图片内容不能为空";
						break;
					case "originalUrl":
						errorMsg ="请输入正确的原文链接";
						break;
				}
				$(".tuwenNew").find("#"+checkName).nextAll(".messageCheckP:first").text(errorMsg);
				$(".tuwenNew").find("#"+checkName).focus();
				return false;
			}else{
				setJson();
				var url = "${rc.contextPath}/wechat/material/updateMultiple";
				$("#multipleForm").ajaxSubmit({
					type: "POST",
					url:url,
					data:$('#multipleForm').serialize(),
					dataType: "json",
				    success: function(data){
			     		if(data.code=='success'){
				     		layer.alert(data.message);
				     		setTimeout(function(){
								<#if cmd="autoreply">
                                    window.location.href="${rc.contextPath}/wechat/autoreply/index"
								<#else>
                                    window.location.href="${rc.contextPath}/wechat/material/publicno"
								</#if>
				     		},3000);
				   		 }else{
				    		layer.alert(data.message);
				    	}
					},
					error: function(){
						layer.alert("保存出错,请稍候重试");
					}
				});
			}
		}
		
		function setJson(){
			//图文json数据数组
			var newsJson = [];
			var len = $(".dataitem").length;
			//遍历图文消息
			$(".dataitem").each(function(i){
				var index = $(".dataitem").eq(i).attr("index");
				var id = $("#id"+index).val();
				var title = $("#title"+index).val();
				var author = $("#author"+index).val();
				var showCoverPic = $("#showCoverPic"+index).val();
				var digest = $("#digest"+index).val();
				var content = $("#content"+index).val();
				content = content.replace(/\"/g,"'");
				var originalUrl = $("#originalUrl"+index).val();
				var thumbMediaId = $("#thumbMediaId"+index).val();
				var imageUrl = $("#url"+index).val();
				var filePath = $("#filepath"+index).val();
				var json = {"id":id,"title":title,"author":author,"showCoverPic":showCoverPic,"digest":digest,"content":content,"originalUrl":originalUrl,"thumbMediaId":thumbMediaId,"url":imageUrl,"filepath":filePath,"sort":i};
				var str = JSON.stringify(json);
				if(i != len-1){
					str+=",";
				}
				newsJson[i]=str;
			});
			
			if(newsJson.length > 0){
				$("#contentsJson").val("["+newsJson.join("")+"]");
			}
		}
		
		//清除表单的值
		function clearForm(){
			$("#title").val("");
			$("#author").val("");
			$("#upload_image").val("");
			$("#showCoverPic").attr("checked",false);
			$("#digest").val("");
			$("#content").val("");
			editor.html("");
			$("#originalUrl").val("");
		}
		
		//设置原来表单的值
		function setOrigForm(index){
			$("#title").val($("#title"+index).val());
			$("#author").val($("#author"+index).val());
			if($("#showCoverPic"+index).val()=='on'){
				//$("#showCoverPic").attr("checked","true");
				//为啥不起作用？
				document.getElementById("showCoverPic").checked=true;
			}else{
				//$("#showCoverPic").attr("checked","false");
				//为啥不起作用？
				document.getElementById("showCoverPic").checked=false;
			}
			$("#digest").val($("#digest"+index).val());
			//$("#content").val($("#content"+index).val());
			editor.html($("#content"+index).val());
			$("#originalUrl").val($("#originalUrl"+index).val());
			$("#thumbMediaId").val($("#thumbMediaId"+index).val());
		}
		
		//单个删除
		function deleteMaterialById(id){
			var url = "${rc.contextPath}/wechat/material/delete";
			$.ajax({ url: url, 
					dataType: "json", 
					timeout:5000,
					data:{"ids":id},
					success: function(data,textStatus){
     				},
     				error: function(data){
     					
     				}
     		});
		}
		
		/**
		 * 保存多图文
		 *
		 */
		function saveMultiple(){
			$(".messageCheckP").text("");
			var isRight = $(".tuwenNewDiv>.tuwenResult").checkDataDom();
			if(isRight){
				var parent = $(isRight).parents(".dataitem:first");
				var index = $(parent).attr("index"), checkName = $(isRight).attr("checkName");
				parent.find(".icon-edit").trigger("click");
				var errorMsg = "";
				switch (checkName) {
					case "title":
						errorMsg = "内容不能为空";
						break;
					case "content":
						errorMsg = "内容不能为空";
						break;
					case "thumbMediaId":
						errorMsg ="内容不能为空";
						break;
					case "originalUrl":
						errorMsg ="请输入正确的地址";
						break;
				}
				$(".tuwenNew").find("#"+checkName).nextAll(".messageCheckP:first").text(errorMsg);
				$(".tuwenNew").find("#"+checkName).focus();
				return false;
			}else{
			setJson();
				var url = "${rc.contextPath}/wechat/material/updateMultiple";
				$("#multipleForm").ajaxSubmit({
					type: "POST",
					url:url,
					data:$('#multipleForm').serialize(),
					dataType: "json",
				    success: function(data){
			     		console.log(data.message);
					},
					error: function(){
						console.log("保存出错,请稍候重试");
					}
				});
			}
		}
		
		function preview() {
			var flag = saveMultiple();
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
            var mediaId = $("#MediaId").val();
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
        
        //保存并群发
		function saveAndMass(){
			$(".messageCheckP").text("");
			var isRight = $(".tuwenNewDiv>.tuwenResult").checkDataDom();
			if(isRight){
				var parent = $(isRight).parents(".dataitem:first");
				var index = $(parent).attr("index"), checkName = $(isRight).attr("checkName");
				parent.find(".icon-edit").trigger("click");
				var errorMsg = "";
				switch (checkName) {
					case "title":
						errorMsg = "请输入1-64位的标题";
						break;
					case "content":
						errorMsg = "请输入1-1700的内容";
						break;
					case "thumbMediaId":
						errorMsg ="图片内容不能为空";
						break;
					case "originalUrl":
						errorMsg ="请输入正确的地址";
						break;
				}
				$(".tuwenNew").find("#"+checkName).nextAll(".messageCheckP:first").text(errorMsg);
				$(".tuwenNew").find("#"+checkName).focus();
			}else{
				setJson();
				$("#multipleForm").attr("action","${rc.contextPath}/wechat/material/updateMultipleAndMass");
				$("#multipleForm").submit();
			}
		}
		</script>
</#macro>