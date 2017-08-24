<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#assign tag=JspTaglibs["/WEB-INF/taglib/permission.tld"]>
<#include "/wechat/layouts/main.ftl"/>
<@html></@html>
<#macro style>
<link rel="stylesheet" href="${rc.contextPath}/wechat/css/zTreeStyle/zTreeStyle.css" />
<style>
	.tuwenChild{
		padding:10px 0;
		border-bottom:none;
	}
	.tuwenChild h4{
		padding:0;
	}
	.tuwen li{
		margin-top:20px;
	}
	.tuwen p{
		font-size:12px;
		word-wrap:break-word;
	}
	.tuwen{
		width:23%;
	}
	.resourceList>ul{
		margin:0 0 0 10px;
	}
	.resourceList>ul>li{
		margin:0 10px 10px 10px;
		padding:1%;
	}
	.resourceList li img{
		height:200px;
	}
	.tuwen .matetime{
		font-size:12px;
		color:#ccc;
		padding:4px 0;
	}
	.inline span{
		font-size:12px;
	}
	.tuwenInfo > li {
    	position: relative;
	}
	.tuwenOperate{
		z-index: 20;
	}
	.ztree li span.button.add {margin-left:2px; margin-right: -1px; background-position:-144px 0; vertical-align:top; *vertical-align:middle}
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
			<span>素材中心</span>
		</li>
	</ul>
	<div class="tabContainer marginTop20 tabChange" style="margin:20px 0 20px 20px;">
		<ul class="tabContiner_nav borderbottom">
			<li class="active" href="#page1">
				<a >网点素材库</a>
			</li>
			<li  href="#page2">
				<a >总部共享素材</a>
			</li>
		</ul>
		<div class="tabContianer_con borderAround">
			<div id="page1" style="display: block;" class="messagesend">
				<div class="tabContainer tabChangeChild">
					<ul class="tabContiner_nav borderbottom">
						<li class="active" href="#page1child1">
							<a >图文信息</a>
						</li>
						<li href="#page1child2">
							<a >图片库</a>
						</li>
					</ul>
					<div class="tabContianer_con">
						<div id="page1child1" style="display: block;" class="messagesend">
							<form class="form-search" action="${rc.contextPath}/wechat/material/search" method="post" id="form1Id" >
								<div class="row">
									<div class="col-xs-12 col-sm-8">
										<div class="input-group" style="width:81%;">
											<input type="text" style="width: 250px;margin:0;font-family:'微软雅黑'" name="condition" placeholder="标题/作者/摘要" class="form-control search-query search marginTop2">
											<span class="input-group-btn">
												<button style="vertical-align:top\9;" class="btn btn-purple btn-sm submit" type="button">
													搜索
													<i class="icon-search icon-on-right bigger-110"></i>
												</button>
											</span>
										</div>
									</div>
									<div class="floatR">
										<a href="${rc.contextPath}/wechat/material/createSingle" style="color: #fff;padding: 0; height:30px;line-height:30px;margin-right:10px; display: block;float: left;width: 120px;text-align: center;" class="redButton">新建单图文信息</a>
										<a href="${rc.contextPath}/wechat/material/createMultiple" style="color: #fff;padding:0;height:30px;line-height:30px;margin-right: 30px; display: block;float: left;width: 120px;text-align: center;" class="redButton">新建多图文信息</a>
									</div>
								</div>
							</form>
							<div id="table1Id">
								<div class="tuwenParent">
									<ul id="content1" class="tuwen"></ul>
									<ul id="content2" class="tuwen"></ul>
									<ul id="content3" class="tuwen"></ul>
									<ul id="content4" class="tuwen"></ul>
								</div>
								<div>
									<div class="pagegination floatR" style="margin:20px;">
										<ul>
										</ul>
									</div>
								</div>
							</div>
						</div>
						<div id="page1child2" class="messagesend">
							<form action="${rc.contextPath}/wechat/material/searchImage" id="formId" method="post"></form>
								<div class="pictureSucai">
									<div style="width:100%;">
										<div class="pic_upload clearFix" style="margin:10px 10px 0;">
											<div class="floatR allpage">
												<label class="inline floatL" style="line-height:30px;">
													<input type="checkbox" class="ace checkAll">
													<span class="lbl" style="line-height:30px;">全选</span>
												</label>
												<a style="display: block;line-height: 30px;margin: 0 10px;margin-top:1px;" class="floatL" href="javascript:void(0);" id="batchDelete" onclick="deleteMaterialByIds();" >删除</a>
											</div>
											<form id="uploadForm" method="post" class="floatL uploadForm" enctype="multipart/form-data" action="${rc.contextPath}/wechat/material/uploadImage" >
												<input id="imgId" type="file" name="image" value="图片上传" class="floatL" style="width:200px;height:30px;line-height:30px;" />
												<input type="button" onclick="uploadImage();" value="上传" class="floatL redButton"/>
											</form>
										</div>
										<div id="tableId">
											<ul class="tuwenInfo">
											</ul>
											<div>
												<div class="pagegination floatR" style="margin:20px;">
													<ul>
													</ul>
												</div>
											</div>
										</div>
									</div>
								</div>
						</div>
					</div>
				</div>
			</div>
			<div id="page2" class="messagesend">
					<div class="row">
						<form class="form-search" action="${rc.contextPath}/wechat/material/searchFile" method="post" id="formResource" >
							<div class="col-xs-12 col-sm-8">
								<div class="input-group" style="width:81%;">
									<input type="text" style="width: 250px; margin:0;font-size:12px;" name="condiction" placeholder="请输入文件名" class="form-control search-query search marginTop2">
									<input type="hidden" name="groupId" id="groupId" class="form-control search-query search marginTop2">
									<span class="input-group-btn">
										<button style="vertical-align:top\9;" class="btn btn-purple btn-sm submit" type="button">
											搜索
											<i class="icon-search icon-on-right bigger-110"></i>
										</button>
									</span>
								</div>
							</div>
						</form>
						<div class="floatR checkAll" style="margin:0 20px 0 0;">
							<#if permissions?exists && permissions?seq_contains('wechat/material/share/delete')>
								<label class="inline" style="line-height:30px;">
									<input type="checkbox" class="ace checkAll">
									<span class="lbl">全选</span>
								</label>
								<a style="line-height:30px;" href="javascript:void(0)" id="deleteResource">删除</a>
							</#if>
						</div>
						<div class="floatL marginRight15" style="margin-left:20px;">
							<#if permissions?exists && permissions?seq_contains("wechat/material/share/create")>
								<form class="clearFix" id="uploadFileForm" method="post" enctype="multipart/form-data" action="${rc.contextPath}/wechat/material/uploadFile" >
									<input id="fileId" type="file" name="file" value="图片上传" class="floatL" style="width:150px;height:30px;" />
									<input type="button" onclick="uploadFile();" onclick="AutoReply.clearInput();" value="上传" style="margin-left:30px;" class="redButton floatL"/>
								</form>
							</#if>
						</div>
					</div>
				<div class="sucaiMain">
					<div class="resourceTreeLeft floatL">
						<div class="tableTitle">资源节点树</div>
						<div id="rightTree" class="ztree"></div>
					</div>
					<div class="resourceRight floatR">
						<div id="resourceList">
							<div class="resourceList">
								<ul></ul>
							</div>
							<div>
								<div class="pagegination floatR">
									<ul>
									</ul>
								</div>
							</div>
						</div>
					</div>
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
		<script type="text/javascript" src="${rc.contextPath}/wechat/js/zTree/jquery.ztree.core-3.5.js"></script>
		<script type="text/javascript" src="${rc.contextPath}/wechat/js/zTree/jquery.ztree.excheck-3.5.js"></script>
		<script type="text/javascript" src="${rc.contextPath}/wechat/js/zTree/jquery.ztree.exedit.js"></script>
		<script type="text/javascript" src="${rc.contextPath}/wechat/js/zTree/jquery.ztree.exhide-3.5.js"></script>
		<script>
		var page2Instance = null;
		$(function(){
			var tips = null;
			
			$(".tuwenInfo").undelegate("li","mouseenter");
			$(".tuwenInfo").delegate("li","mouseenter",function(){
				$(this).find(".tuwenOperate").css("display","block");
			});
			
			$(".tuwenInfo").undelegate("li","mouseleave");
			$(".tuwenInfo").delegate("li","mouseleave",function(){
				$(this).find(".tuwenOperate").css("display","none");
			});
			
			$("body").delegate(".iconHover","hover",function(){
				var value = $(this).attr("value");
				tips = layer.tips(value, this, {
				    tips: [3, '#666']
				});
			},function(){
				layer.close(tips);
			});
			var setting = {
			<#if permissions?exists && permissions?seq_contains('wechat/material/share/delete')>
		      view: {
		      	addHoverDom: addHoverDom,
				removeHoverDom: removeHoverDom,
				selectedMulti: false
		      },
		      edit: {
				enable: true,
				editNameSelectAll: false,
				showRemoveBtn: showRemoveBtn,
			  },
			  </#if>
		      async : {
		      	enable : true
		      },
		      callback: {
		      	<#if permissions?exists && permissions?seq_contains('wechat/material/share/delete')>
		       	beforeDrag: beforeDrag,
				beforeEditName: beforeEditName,
				beforeRemove: beforeRemove,
				beforeRename: beforeRename,
				onRemove: onRemove,
				onRename: onRename,
				</#if>
				onClick: onClick
		      }
		    };
		    
		     //获取共享资源树
			 $.ajax({
	            type: "POST",
	            dataType: "json",
	            url: "${rc.contextPath}/wechat/material/getFolderTree",
	            data: {
	            	"group":"9006"
	            },
	            success: function(data){
	            	var node = data;
	                $.fn.zTree.init($("#rightTree"), setting, node);
	            },
	            error: function(){
	            	layer.alert("系统繁忙");
	            }
	         });
	         
			initPage1();
			page2Instance = initPage2();
			pageResource = initPageResource();
		});
		
		function initPage2(){
			var page2 = new pagination("formId","tableId",function(value){
				$("#tableId>ul").children().remove();
                
				if(value){
				    
					for(var i=0;i<value.length;i++){
						var obj = value[i];
						var $li = $("<li>");
						var $img = $("<img src='"+value[i]["url"]+"'>");
						var $p = $("<p class='textAlign'><label class='inline'><input type='checkbox' name='pictureLib' mid="+value[i]["id"]+" class='ace checks'><span class='lbl margin-5' title='"+value[i]["title"]+"' style='width: 90%;overflow: hidden;word-break: break-all;height: 20px;'>"+value[i]["title"]+"</span></label></p>");
                        var strhtlma='<table><tr><td><div>修改名称：</div></td><td><input type="hidden" class="imglibId" value="'+obj.id+'" /><input style="margin-left:-30px;" type="text" class="invalue" value="'+value[i]["title"]+'" /></td></tr><tr><td><span style="width:90px;margin:10px; display: block;cursor:pointer;" class="li_ok redButton" onclick="updateImgLib(' + obj.id + ')">确定</span></td><td><span style="width:90px;margin:10px 0; display: block;cursor:pointer;" class="li_cancel redButton">取消</span></td></tr></table>';
                        var $operate = "<div class='tuwenOperate'><a><i class='icon-edit iconHover popover-destroy' value='编辑' data-container='body' data-content='"+strhtlma+"</div>'></i></a><a href='javascript:void(0);' onclick='deleteMaterialById(" + obj.id + ")'><i class='icon-remove iconHover' value='删除'></i></a></div>";
                        $("#tableId>ul").append($li.append($img).append($p).append($operate));
					}
                    
                    
                    $("li .iconHover").click(function(){
                    	var _this = this;
                        $(this).popover({placement:"bottom",html:true})
                        $(this).popover('show');
                        $("li .iconHover").each(function(){
                        	if(_this == this){
                        		return;
                        	}
                    		$(this).popover('destroy');
                    	});
                    });
                    
                    
                    $("body").delegate(".li_cancel","click",function(){
                        $('.popover-destroy').popover("destroy");
                    });

                    $("body").delegate(".li_ok","click",function(){
                    	$('.popover-destroy').popover("destroy");
                    });
                
				}
			});
			
			$(".tabContiner_nav").click(function(){
				$('.popover-destroy').popover("destroy");
			});
			//刷新图片库分组
			//getGroups()
			page2.checkAll("page1child2");
			return page2;
		}
		
		function initPage1(){
			$("#form1Id").attr("action",$("#form1Id").attr("action")+"?r="+(new Date().getTime()));
			var page = new pagination("form1Id","table1Id",function(value){
				var dom = value;
				$("#table1Id>div>ul").children().remove();
				if(dom){
					for(var i=0;i<dom.length;i++){
					var obj = dom[i];
					var $li = $("<li>");
					var h1 = $("<h1>").text(obj["title"]);
					var p = $("<p class='matetime'>").text(obj["createDate"]);
					var img = $("<img src='"+obj.url+"'>");
					var p_content = $("<p>").text(obj.digest);
					$li.append(h1).append(p).append(img);
					if(obj.children.length>0){
						for(var j=0;j<obj.children.length;j++){
							var childValue = obj.children[j];
							var $child_h4 = $("<h4 style='word-wrap:break-word;'><a href='#'>"+childValue["title"]+"</a></h4>");
							var $child_img = $("<img src='"+childValue["url"]+"' />");
							$li.append($("<div class='tuwenChild'>").append($child_h4).append($child_img));
						}
					}else{
						$li.append(p_content);
					}
					var operate = $("<div class='tuwenOperate'><a href='${rc.contextPath}/wechat/material/edit/" + obj.id + "'><i class='icon-edit iconHover' value='编辑'></i></a><a href='#' onclick='deleteMaterialById(" + obj.id + ")'><i class='icon-remove iconHover' value='删除'></i></a></div>");
					$li.append(operate);
					if(i%4==0){
						$("#content1").append($li);
					}else if(i%4==1){
						$("#content2").append($li);
					}else if(i%4==2){
						$("#content3").append($li);
					}else if(i%4==3){
						$("#content4").append($li);
					}
				}
				}
			});
		}
		
		function initPageResource(){
			var pageResource = new pagination("formResource","resourceList",function(value){
				$("#resourceList ul").children().remove();
				if(value.length>0){
					for(var i=0;i<value.length;i++){
						var $li = $("<li>");
						var $title = $("<h4 style='word-wrap:break-word;'>"+value[i]['title']+"</h4>");
						var $date = $("<span>"+value[i]["createDate"]+"</span>");
						//gif,jpg,jpeg,png,bmp
						var imgUrl = value[i]["url"];
						if(endWith(imgUrl,".gif")||endWith(imgUrl,".jpg")||endWith(imgUrl,".jpeg")||endWith(imgUrl,".png")||endWith(imgUrl,".bmp")){
							var $img  = $("<img src='"+value[i]["url"]+"'>");
						}else{
							var $img  = $("<img src='${rc.contextPath}/wechat/img/otherfile.png'>");
						}
						var $ope = $("<div class='operate' ><a href='javascript:void(0)' style='width:100%'><#if permissions?exists && permissions?seq_contains('wechat/material/share/delete')><input type='checkbox' name='resourcecheck' class='checks' value='"+value[i]["id"]+"'></#if></a></div>");
						var downLoad = $("<a href='"+"${rc.contextPath}/wechat/material/download/"+value[i]["id"]+"' ><i title='下载' class='icon-cloud-download' ></i></a>");
						
						var deletes = "";
						
						<#if permissions?exists && permissions?seq_contains('wechat/material/share/delete')>
							deletes = $("<a href='javascript:void(0)' mid="+value[i]["id"]+" ><i title='删除' class='icon-remove' ></i></a>");
									deletes.click(function(){
								deleteShareMaterial($(this).attr("mid"));
							});
						</#if>
						
						$ope.append(downLoad).append(deletes);
						$li.append($li).append($title).append($date).append($img).append($ope);
						$("#resourceList ul").append($li);
					}
				}
			});
			pageResource.checkAll("page2");
			//获取页面删除事件
			$("#deleteResource").click(function(){
				layer.confirm("确定删除？",{},
					function(index){
						layer.close(index);
						var url = "${rc.contextPath}/wechat/material/delete";
						var ids = "";
						$("input[name='resourcecheck']:checked").each(function(){
							if(ids!=""){
								ids = ids + "," + $(this).attr("value");
							}else{
								ids =  $(this).attr("value");
							}
						});
						
						$.ajax({ url: url, 
							dataType: "json", 
							data:{"ids":ids},
							success: function(data,textStatus){
								layer.alert(data.message);
								var treeObj = $.fn.zTree.getZTreeObj("rightTree");
								var node = treeObj.getSelectedNodes();
								onClick(null, "rightTree", node)
		     				},
		     				error: function(data){
		     					layer.alert("系统繁忙");
								initPageResource();
		     				}
			     		});
			     	},
					function(index){
						layer.close(index);
					}
				);
			});
			return pageResource;
		}
		
		//设置图片库分组
		/*function getGroups(){
			var url = "${rc.contextPath}/wechat/material/searchFileGroup";
			$.ajax({
				type: "POST",
				url:url,
				data:{'group':0},
				dataType: "json",
			    success: function(value){
			    	$("#groups").children().remove();
			    	if(value!=null && value!="" && typeof value == "string"){
						value = eval("("+value+")");
					}
					var $group = "";
					if(value==null||value==""||value==undefined||value.length==0){
						$group ="<li class='active'><a href='javascript:void(0);' class='submitValue' gid='0'>未分组(0)</a></li>";
						$("#groups").append($group);
					}else{
						for(var i=0;i<value.length;i++){
							if(i==0){
								$group=$("<li class='active'><a href='javascript:void(0);' class='submitValue' gid="+value[i].id+"></a>"+value[i].groupName+"("+value[i].total+")</li>");
							}else{
								$group=$("<li><a href='javascript:void(0);' class='submitValue' gid="+value[i].id+"></a>"+value[i].groupName+"("+value[i].total+")</li>");
							}
							//绑定事件
							$group.click(function(){
								$("#groups li").removeClass("active");
								$(this).addClass("active");
								var gid = $(this).find("a").attr("gid");
								var url = "${rc.contextPath}/wechat/material/searchImage";
								$("#formId").ajaxSubmit({
									type: "POST",
									url:url,
									cache: false,
									data:{'group':gid},
									dataType: "json",
								    success: function(value){
								    	if(value!=null && value!="" && typeof value == "string"){
											value =eval("("+value+")");
										}
										page2Instance.bindData(value);
									}
								});
							});
							$("#groups").append($group);
						}
					}
				}
			});
		}*/
		
		//删除总部共享素材
		function deleteShareMaterial(id){
			layer.confirm("确定删除？",{},
				function(index){
					layer.close(index);
					var url = "${rc.contextPath}/wechat/material/delete";
					$.ajax({ url: url, 
						dataType: "json", 
						data:{"ids":id},
						success: function(data,textStatus){
							layer.alert(data.message);
							var treeObj = $.fn.zTree.getZTreeObj("rightTree");
							var node = treeObj.getSelectedNodes();
							onClick(null, "rightTree", node)
	     				},
	     				error: function(data){
							layer.alert("系统繁忙");
	     				}
		     		});
		     	},
				function(index){
					layer.close(index);
				}
			);
		}
		
		
		//单个删除(图文素材与图片库共用)
		function deleteMaterialById(id){
			layer.confirm("确定删除？",{},
				function(index){
					layer.close(index);
					var url = "${rc.contextPath}/wechat/material/delete";
					$.ajax({ url: url, 
							dataType: "json", 
							data:{"ids":id},
							success: function(data,textStatus){
								layer.alert(data.message);
								initPage1();
								initPage2();
								//initPageResource();
		     				},
		     				error: function(data){
		     					layer.alert("系统繁忙");
		     				}
		     		});
		     	},
				function(index){
					layer.close(index);
				}
			);
		}
		
		//批量删除图片
		function deleteMaterialByIds(){
			layer.confirm("确定删除？",{},
				function(index){
					layer.close(index);
					var ids = "";
					$("input[name='pictureLib']:checked").each(function(){
						if(ids!=""){
							ids = ids + "," + $(this).attr("mid");
						}else{
							ids =  $(this).attr("mid");
						}
					});
					var url = "${rc.contextPath}/wechat/material/delete";
					$.ajax({ url: url, 
							dataType: "json", 
							data:{"ids":ids},
							success: function(data,textStatus){
								layer.alert(data.message);
								initPage2();
		     				},
		     				error: function(data){
		     					layer.alert("系统繁忙");
		     				}
		     		});
		     	}, 
		     	function(index){
					layer.close(index);
				}
			);
			
		}
		
		//上传图片
		function uploadImage(){
			var group = $("#groups").find(".active a").attr("gid");
			var url = "${rc.contextPath}/wechat/material/uploadImage";
			//当用户没有选择需要上传的图片时不允许提交表单
			var imgVal=$("#imgId").val();
			if(imgVal=="" || imgVal==null){
			  layer.alert("请选择你要上传图片");
			  return false;
			}
		//提交表单
		$("#uploadForm").ajaxSubmit({
				type: "POST",
				url:url,
				dataType: "json",
				data:{
					'group':group
				},
			    success: function(data){
			     	if(data.code=='success'){
			     		layer.alert("图片上传成功");
			     		//清空文件名称
						$("#imgId").val("");
			     		initPage2();
			   		 }
			    	else{
			    		layer.alert(data.message);
			    	}
				},
				error : function(){
					layer.alert("系统繁忙");
				}
			});
		}
		
		//上传文件
		function uploadFile(){
			var url = "${rc.contextPath}/wechat/material/uploadFile";
			var treeObj = $.fn.zTree.getZTreeObj("rightTree");
			var nodes = treeObj.getSelectedNodes();
			var folder = 1;
			if(nodes!=null&& nodes!=undefined&&nodes.length>0){
				folder = nodes[0].id;
			}
			//当用户没有选择需要上传的图片时不允许提交表单
			var fileVal=$("#fileId").val();
			if(fileVal=="" || fileVal==null){
			  layer.alert("请选择资源目录及图片");
			  return false;
			}
			$("#uploadFileForm").ajaxSubmit({
				type: "POST",
				url:url,
				data:{
					"folder":folder	
				},
				dataType: "json",
			    success: function(data){
			     	if(data.code=='success'){
			     		layer.alert("上传成功");
			     		var treeObj = $.fn.zTree.getZTreeObj("rightTree");
						var node = treeObj.getSelectedNodes();
						if(node.length > 0){
							var nodes = treeObj.getNodes();
							onClick(null, "rightTree", nodes[0]);
						}else{
							onClick(null, "rightTree", node);
						}
						
			     		$("#fileId").val("");
			   		 }
			    	else{
			    		layer.alert(data.message);
			    	}
				},
				error : function(){
					layer.alert("系统繁忙");
				}
			});
		}
		
		//更新图片库
	   	function updateImgLib(id){
			var title=$(".invalue").val();
			$.ajax({ url:"${rc.contextPath}/wechat/material/updateImg", 
				dataType: "json", 
				method:"post",
				data:{"id":id,"title":title},
				success: function(data,textStatus){
					layer.alert(data.message);
					initPage2()
				},
				error: function(data){
					layer.alert("修改失败，重新试一试！");
				}
	 		});
	    }
	    
	    
	    function onClick(event, treeId, treeNode, clickFlag) {
		    	var treeObj = $.fn.zTree.getZTreeObj("rightTree");
				var nodes = treeObj.getSelectedNodes();
				var id=1;
				if(nodes.length>0 && nodes[0].id != undefined){
					id = nodes[0].id;
					$("#groupId").val(id);
				}else{
					id = 1;
				}
				//获取共享资源树
				$.ajax({
		           type: "POST",
		           dataType: "json",
		           url: "${rc.contextPath}/wechat/material/searchFile",
		           data: {
		           		"group":id
		           },
		           success: function(value){
		           		if(value!=null && value!="" && typeof value == "string"){
							value =eval("("+value+")");
						}
		           		pageResource.bindData(value);
		           },
		           error: function(){
		           		layer.alert("系统繁忙");
		           }
		        });
				
		    }
		    
			function beforeDrag(treeId, treeNodes) {
				return false;
			}
			function beforeEditName(treeId, treeNode) {
				return true;
			}
			function beforeRemove(treeId, treeNode) {
				return true;
			}
			function onRemove(e, treeId, treeNode) {
				var treeObj = $.fn.zTree.getZTreeObj("rightTree");
		        var nodeId = treeNode.id;
        		if(nodeId != 1){
        			deleteFile(treeObj,nodeId,$(this),treeNode);
        		}else{
        			layer.alert("根文件夹不允许删除！");
        		}
			}
			function beforeRename(treeId, treeNode, newName, isCancel) {
				if (newName.length == 0) {
					alert("节点名称不能为空.");
					var zTree = $.fn.zTree.getZTreeObj("rightTree");
					setTimeout(function(){zTree.editName(treeNode)}, 10);
					return false;
				}
				return true;
			}
			function onRename(e, treeId, treeNode, isCancel) {
				var nodeId = treeNode.id;
        		var treeObj = $.fn.zTree.getZTreeObj("rightTree");
				treeObj.editName(treeNode);
	        	if(nodeId == null || nodeId == undefined || nodeId == ''){
	        		layer.alert("未选择修改的节点");
	        		return;
	        	}
	        	$.ajax({
		           type: "POST",
		           dataType: "json",
		           url: "${rc.contextPath}/wechat/material/updateFile",
		           data: {
		           		name:treeNode.name,
		           		id:nodeId
		           },
		           success: function(data){
		           		layer.alert("修改成功");
		           },
		           error: function(){
		           		layer.alert("系统繁忙");
		           }
		        });
			}
			function showRemoveBtn(treeId, treeNode) {
		        var nodeId = treeNode.id;
		        if(nodeId != 1){
		        	return true;
		        }else{
		        	return false;
		        }
			}
			function addHoverDom(treeId, treeNode) {
				var sObj = $("#" + treeNode.tId + "_span");
				if (treeNode.editNameFlag || $("#addBtn_"+treeNode.tId).length>0) return;
				var addStr = "<span class='button add' id='addBtn_" + treeNode.tId
					+ "' title='add node' onfocus='this.blur();'></span>";
				sObj.after(addStr);
				var btn = $("#addBtn_"+treeNode.tId);
				if (btn) btn.bind("click", function(){
					var nodeId = treeNode.id;
	        		var treeObj = $.fn.zTree.getZTreeObj("rightTree");
					$.ajax({
			           type: "POST",
			           dataType: "json",
			           url: "${rc.contextPath}/wechat/material/saveFile",
			           data: {
			           		name:"新建目录",
			           		parent:nodeId
			           },
			           success: function(data){
							layer.alert(data.value);
			           		var newNode = {name:"新建目录",id:data.id};
							newNode = treeObj.addNodes(treeNode, newNode);
			           },
			           error: function(){
			           		layer.alert("系统繁忙");
			           }
			        });
					return false;
				});
			};
			function removeHoverDom(treeId, treeNode) {
				$("#addBtn_"+treeNode.tId).unbind().remove();
			};
		    
		    
		    function deleteFile(treeObj,nodeId,$this,treeNode){
		    	layer.confirm("确定删除？",{},
					function(index){
						layer.close(index);
						$.ajax({
				           type: "POST",
				           dataType: "json",
				           url: "${rc.contextPath}/wechat/material/deleteFile",
				           data: {
				           		id:nodeId
				           },
				           success: function(data){
								layer.alert(data.value);
				           		treeObj.removeNode(treeNode);
								initPageResource();
								return;
				           },
				           error: function(){
				           		layer.alert("系统繁忙");
				           }
				        });
					},
					function(index){
						layer.close(index);
					}
				);
		  	 }
		  	 
		  	 function endWith(str,endStr){
				 var d=str.length-endStr.length;
				 return (d>=0&&str.lastIndexOf(endStr)==d)
			 }
		</script>
</#macro>
