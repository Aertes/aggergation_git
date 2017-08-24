<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#assign tag=JspTaglibs["/WEB-INF/taglib/permission.tld"]>
<#include "/wechat/layouts/main.ftl"/>
<@html></@html>
<#macro style>
	<style>
		.messagesend{
			padding:0 0 20px 0;
		}
		.sendMessagCondition>div{
			margin-bottom:20px;
		}
		.sendMessagCondition label{
			margin:0;
			padding:0;
			height:30px;
			line-height:30px;
		}
		.sendMessagCondition select{
			float:left;
			width:150px;
			margin:0 10px;
		}
		.tabContianer_con{
		}
		.addPiContinaer{
			padding:0;
		}
		.addPiContinaer .addPi1{
			width:50%;
		}
		.addPiContinaer .addPi1:nth-of-type(2){
			margin-left:1%;
			width:49%;
		}
		.tuwen li{
			margin-top:0;
		}
		.tuwen li>p{
			padding:10px;
		}
		.borderbottom{
			border-bottom:1px solid #d9d9d9;
		}
		.tabliChange>.tabContiner_nav>li.active{
			border-bottom:none;
		}
		.animatePicture{
			padding:10px;
		}
		.animatePicture label{
			width:68%;
			height:45px;
			overflow:hidden;
			float:left;
			line-height:1.5;
		}
		.animatePicture_chil{
			width:30%;
			margin-left:1%;
			float:left;
			height:auto;
		}
		.tuwenOperate{
			display:block;
		}
		.tuwenOperate a{
			width:100%;
		}
		.sendMessagCondition label{
			font-size:12px;
		}
		.titlelist div{
			padding:0 1%;
			height:39px;
			line-height:39px;
			border-right:1px solid #ccc;
			text-align:center;
			float:left;
		}
		.sendState{
			padding:2% 0;
			color:#0A0A0A;
		}
		
		.icon{width:80px;height:80px;vertical-align:middle;display:inline-block;background-color:#d7d8da!important}
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
            <span>群发管理</span>
        </li>
    </ul>
    <div class="message">
        <a href="${rc.contextPath}/wechat/autoreply/index" class=" marginLeft10">自动回复</a>
        <a href="${rc.contextPath}/wechat/message/index" class=" marginLeft10">消息管理</a>
    </div>
        <div class="tabContainer tabChange marginTop10" style="margin-left:20px;">
            <ul class="tabContiner_nav borderbottom">
                <li <#if (entity.id?exists && entity.id>0)==false> class="active"</#if> href="#page1">
                    <a>新建消息</a>
                </li>
                <li href="#page2" <#if (entity.id?exists && entity.id>0)> class="active"</#if>>
                    <a>已发送</a>
                </li>
            </ul>
            <div class="tabContianer_con" style="padding:0;">
                <div id="page1" <#if (entity.id?exists && entity.id>0)==false> style="display: block;padding-top:20px"</#if>>
                <form id="messagesendbatchForm" action="${rc.contextPath}/wechat/messagesendbatch/save" method="post">
                    <div class="messagesend">
                        <div class="sendMessagCondition">
                            <div>
                                <label class="floatL">发送对象：</label>
                                <select id="groupId" name="groupId.id" class="form-control" style="font-size:12px;">
                                        <option value="-1" selected="selected">全部</option>
                                        <#list groupList as group>
                                            <option value="${group.id}">${group.name}</option>
                                        </#list>
                                </select>
                            </div>
                        </div>
                        <div class="tabContainer tabSendpage tabliChange">
                            <ul class="tabContiner_nav borderbottom">
                                <li class="active" href="#page_icon1">
                                    <i class=""></i>
                                    图文信息(使用中)
                                </li>
                                <li href="#page_icon2">
                                    <i class=""></i>
                                    文字信息
                                </li>
                            </ul>
                            <div class="tabContianer_con displayBlock">
                                <div style="display: block;" id="page_icon1" class="addPiContinaer">
                                    <div class="addPi1">
                                    <div style="width: 100%;" class="floatL addPar">
                                    <a href="${rc.contextPath}/wechat/material/createMultiple" target="_blank"
                                    class="selectFromSucai">
                                    <i class="icon-plus add"></i>
                                    <span class="addTitle">新建图文信息</span>
                                    </a>
                                    </div>
                                    </div>
                                    <div class="addPi1">
                                    <div style="width: 100%;" class="floatL addPar">
                                    <a href="javascript:void(0);" onclick="layerPage.init();"
                                    class="selectFromSucai">
                                    <i class="icon-plus add"></i>
                                    <span class="addTitle">从素材库选择</span>
                                    </a>
                                    <input type="hidden" name="materialId">
                                    </div>
                                    </div>
                                </div>
                                <div id="page_icon2" class="textareaConsulte">
                                    <div>
                                        <textarea name="content" class="textArea changeTextarea">${entity.content}</textarea>
                                    </div>
                                    <div class="border">
                                        <span class="floatR">还可以输入<span class="remain_text_textarea">600</span>个字</span>
                                    </div>
                                </div>
                            </div>
                            <div style="margin:0 0 20px 20px;">
                                <input type="button" value="群 发" onclick="MessageSendBatch.save();" class="redButton">
                                <input style="margin-left:10px;" type="button" value="预 览" onclick="MessageSendBatch.preview();" class="redButton">
                                <span class="spanStyle1">您今天还可以发送${remainTodayCount}条消息<#if session_current_publicno.type.code='service'>，每个粉丝每月最多只能接受到4条</#if></span>
                            </div>
                        </div>
                    </div>
                </div>
                <input type="hidden" name="msgtype" id="msgType" value="mpnews">
                <input type="hidden" name="towxname" id="wxname_">
                <input type="hidden" id="publicNoId" value="${publicNoId?if_exists}">
                <input type="hidden" id="materialId" value="${materialId?if_exists}">
                </form>
                <div id="page2" class="messagesend" style="padding-top:20px;line-height:30px;">
                    <form action="${rc.contextPath}/wechat/messagesendbatch/query" method="post" id="_0" class="form-search">
                        <div class="sendMessagCondition">
                            <div style="width:40%;">
                                <label class="floatL">发送用户组：</label>
                                <select name="groupId" class="search" style="font-family:'微软雅黑';">
                                    <option value="-1" selected="selected">全部</option>
                                    <#list groupList as group>
                                        <option value="${group.id}">${group.name}</option>
                                    </#list>
                                </select>
                                <input class="redButton submit" style="margin:0 0 0 10px;vertical-align:top;" type="button" value="搜索"  id="btnQuery">
                            </div>
                        </div>
                    </form>
                        <div class="titlelist clearfix" style="background:#f0f0f0;border:1px solid #ccc;margin:0px 0px 0;height:40px;" >
							<div style="width:30%;">消息类型</div>
							<div style="width:20%;">发送对象</div>
							<div style="width:20%;">发送时间</div>
							<div style="width:20%;">发送状态</div>
							<div style="width:10%;">操作</div>
						</div>
                        <div id="_1">
                            <ul class="hadSendMessage">
                            </ul>
                            <div class="padding30">
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
</#macro>
<#macro script>
<script src="${rc.contextPath}/wechat/js/bootstrap-switch.js"></script>
<script src="${rc.contextPath}/wechat/assets/js/bootstrap.min.js"></script>
<script type="text/javascript">
	$(function(){
	<#if message?exists>
		layer.open({
            title: "提示",
            content: "${message}"
        });
	</#if>
	});

    (function (global, $) {
        var MessageSendBatch = {};
        MessageSendBatch.save = function () {
			$("#messagesendbatchForm").trigger("submit1");
        };

        var index;
        MessageSendBatch.preview = function() {
            index = layer.open({
                type: 1,
                skin: "layui-layer-rim", //加上边框
                area: ["400px" , "200px"], //宽高
                 offset : ["20%","40%"],
                content: "<input id='wxname' placeholder='请输入当前公众号下粉丝的微信号' style='display:block;width:90%;margin:10px auto;height:30px;padding:0 10px;border:1px solid #d9d9d9'><div style='line-height:50px;text-align:center;'><button onclick='MessageSendBatch.ok();' class='redButton' style='margin-right:10px' >确定</button><button class='redButton' onclick='MessageSendBatch.cancel();'>取消</button></div>"
            });
            JPlaceHolder.init();
        }

        MessageSendBatch.ok = function() {
            var wxname = $("#wxname").val();
            if(wxname) {
                $("#wxname_").val($("#wxname").val());
                $.ajax({
                    type : "POST",
                    url : "${rc.contextPath}/wechat/messagesendbatch/preview",
                    data : $("#messagesendbatchForm").serialize(),
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

        MessageSendBatch.cancel = function() {
            layer.close(index);
        }

        MessageSendBatch.clearInput = function () {

        };

        global.MessageSendBatch = MessageSendBatch;

    })(this, jQuery);

    var _old_html = null;

    var layerPage = new ElasticLayer("pageWindow", "${rc.contextPath}/wechat/material/selectFromMaterial?type=news", {
        type: "pageWindow",
        area: ["60%", "80%"],
        dialog: true,
        offset: ["5%", "20%"]
    }, {
        success: function (layero, index) {
            var body = layer.getChildFrame('body', index);
            $(".selectConfirm_button", $(body)).click(function () {
                var body = layer.getChildFrame('body', index);
                var nodes = $(".select_tuwen .selectPageShadow:has(i)", $(body));
                var parent = $(nodes).parents("li:first");
                //返回值 素材id 和 mediaId
                var id = $("input[name='id']", $(parent)).val();
                var mediaId = $("input[name='mediaId']", $(parent)).val();
                if(id) {
                    $.ajax({
                        url: "${rc.contextPath}/wechat/material/getMaterial",
                        data : {"id":id},
                        success : function(data) {
                        	if(typeof data == "string"){
								data = eval("("+data+")");
								if(data==null){
									return;
								}
							}
							var news="<div style='display: block;' id='page_icon1' class='addPiContinaer'><input type='hidden' name='materialId' value='"+data.id+"'><input type='hidden' name='mediaId' value='"+data.mediaId+"'>";
					     	//单图文
					     	if(data.children==undefined||data.children.length==0){
								var digest = data.digest;
								if(digest == undefined){
									digest = "";
								}
								news+="<ul class='tuwen'>";
								news+="		<li><p>"+data.title+"</p>";
								news+="			<img src='" + data["url"]+ "'>   <p>"+digest+"</p>";
								news+="			<div class='tuwenChild'>   </div>";
								news+="			<div class='tuwenOperate'>";
								news+="				<a href='javascript:void(0);' onclick='_delete();'>";
								news+="				<i value='删除' class='icon-remove iconHover'></i>";
								news+="				</a>";
								news+="			</div>";
								news+="		</li>";
								news+="</ul>";
							}else{
								//多图文
								news+="<ul class='tuwen'>";
								news+="		<li><p>"+data.title+"</p>";
								news+="			<img src='" + data["url"]+ "'>";
								news+="			<div class='tuwenChild'>   ";
								for(var i=0;i<data.children.length;i++){
									var child = data.children[i];
									news+="<div class='animatePicture pictureCheck'><label>"+child.title+"</label><div class='animatePicture_chil'><img  width='100%' src='"+child.url+"'/></div></div>";
								}
								news+="</div>";
								news+="			<div class='tuwenOperate'>";
								news+="				<a href='javascript:void(0);' onclick='_delete();'>";
								news+="				<i value='删除' class='icon-remove iconHover'></i>";
								news+="				</a>";
								news+="			</div>";
								news+="		</li>";
								news+="</ul>";
							}
							news+="</div><div id='page_icon2' class='textareaConsulte'><div><textarea name='content' class='textArea changeTextarea'></textarea></div><div class='border'><span class='floatR'>还可以输入<span class='remain_text_textarea'>600</span>个字</span></div></div>";
							$(".tabContianer_con.displayBlock").html(news);
                            //$(".tabContianer_con.displayBlock").html("<div style='display: block;' id='page_icon1' class='addPiContinaer'><input type='hidden' name='materialId' value='"+id+"'><input type='hidden' name='mediaId' value='"+(data["mediaId"]||'')+"'><ul class='tuwen'><li><h1>"+(data["title"]||'')+"</h1><p></p><img src='"+(data["url"]||'')+"'> <p></p><div class='tuwenChild'><h4 style='width:100%;'><a href='#'>"+data["content"]+"</a></h4></div><div class='tuwenOperate'><a href='javascript:void(0);' onclick='_delete();' style='margin: 0 auto;width: 50%;display: block;float: none;'><i value='删除' class='icon-remove iconHover'></i></a></div></li></ul></div><div id='page_icon2' class='textareaConsulte'><div><textarea name='content' class='textArea changeTextarea'></textarea></div><div class='border'><span class='floatR'>还可以输入<span class='remain_text_textarea'>600</span>个字</span></div></div>");
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

    function _delete() {
        $(".tabContianer_con.displayBlock").html(_old_html);
    }

    function _0() {
        $(".tabContiner_nav.borderbottom li[href='#page_icon1']").html("<i class=''></i>图文信息(当前采用)")
        $(".tabContiner_nav.borderbottom li[href='#page_icon2']").html("<i class=''></i>文字信息")
        $(".tuwen").css("display","block");
    }

    function _1() {
        $(".tabContiner_nav.borderbottom li[href='#page_icon1']").html("<i class=''></i>图文信息");
        $(".tabContiner_nav.borderbottom li[href='#page_icon2']").html("<i class=''></i>文字信息(当前采用)");
        $(".tuwen").css("display","none");
    }

    $(function() {
    	<#if (entity.id?exists && entity.id>0)==false>
        var _materialId = $('#materialId').val();
        if(_materialId) {
            $.ajax({
                url : "${rc.contextPath}/wechat/material/getMaterial",
                data : {"id":_materialId},
                success : function(data) {
                	if(typeof data == "string"){
						data = eval("("+data+")");
						if(data==null){
							return;
						}
					}
					var news="<div style='display: block;' id='page_icon1' class='addPiContinaer'><input type='hidden' name='materialId' value='"+data.id+"'><input type='hidden' name='mediaId' value='"+data.mediaId+"'>";
			     	//单图文
			     	if(data.children==undefined||data.children.length==0){
						var digest = data.digest;
						if(digest == undefined){
							digest = "";
						}
						news+="<ul class='tuwen'>";
						news+="		<li><p>"+data.title+"</p>";
						news+="			<img src='" + data["url"]+ "'>   <p>"+digest+"</p>";
						news+="			<div class='tuwenChild'>   </div>";
						news+="			<div class='tuwenOperate'>";
						news+="				<a href='javascript:void(0);' onclick='_delete();'>";
						news+="				<i value='删除' class='icon-remove iconHover'></i>";
						news+="				</a>";
						news+="			</div>";
						news+="		</li>";
						news+="</ul>";
					}else{
						//多图文
						news+="<ul class='tuwen'>";
						news+="		<li><p>"+data.title+"</p>";
						news+="			<img src='" + data["url"]+ "'>";
						news+="			<div class='tuwenChild'>   ";
						for(var i=0;i<data.children.length;i++){
							var child = data.children[i];
							news+="<div class='animatePicture pictureCheck'><label>"+child.title+"</label><div class='animatePicture_chil'><img  width='100%' src='"+child.url+"'/></div></div>";
						}
						news+="</div>";
						news+="			<div class='tuwenOperate'>";
						news+="				<a href='javascript:void(0);' onclick='_delete();'>";
						news+="				<i value='删除' class='icon-remove iconHover'></i>";
						news+="				</a>";
						news+="			</div>";
						news+="		</li>";
						news+="</ul>";
					}
					news+="</div><div id='page_icon2' class='textareaConsulte'><div><textarea name='content' class='textArea changeTextarea'></textarea></div><div class='border'><span class='floatR'>还可以输入<span class='remain_text_textarea'>600</span>个字</span></div></div>";
					$(".tabContianer_con.displayBlock").html(news);
                    //$(".tabContianer_con.displayBlock").html("<div style='display: block;' id='page_icon1' class='addPiContinaer'><input type='hidden' name='materialId' value='"+_materialId+"'><input type='hidden' name='mediaId' value='"+(data["mediaId"]||'')+"'><ul class='tuwen'><li><h1>"+(data["title"]||'')+"</h1><p></p><img src='"+(data["url"]||'')+"'> <p></p><div class='tuwenChild'><h4 style='width:100%;'><a href='#'>"+data["content"]+"</a></h4></div><div class='tuwenOperate'><a href='javascript:void(0);' onclick='_delete();' style='margin: 0 auto;width: 50%;display: block;float: none;'><i value='删除' class='icon-remove iconHover'></i></a></div></li></ul></div><div id='page_icon2' class='textareaConsulte'><div><textarea name='content' class='textArea changeTextarea'></textarea></div><div class='border'><span class='floatR'>还可以输入<span class='remain_text_textarea'>600</span>个字</span></div></div>");
                }
            });
        }
		</#if>
        _old_html = $(".tabContianer_con.displayBlock").html();
        var page = new pagination("_0", "_1", function (value) {
            $("#_1 ul").children().remove();
            if (value) {
                var obj;
                for (var i = 0; i < value.length; i++) {
                    obj = value[i];
                    var $li = $("<li></li>");
                    var $img = $("<div class='sendImg' style='width:10%'><a href='${rc.contextPath}/wechat/material/edit/"+obj.materialId+"'  target='view_window'><img class='icon' src='"+(obj["url"]||'')+"' width='100%'></a></div>");
                    var $sendText = $("<div class='sendText' style='width:20%'>"+(obj["content"]||'')+"</div>");
                    var $sendObj = $("<div class='sendState'  style='width:20%'>"+(obj["groupId"]||'')+"</div>");
                    var $sendState = $("<div class='sendState' style='width:20%'><span>"+(obj["sendTime"]||'')+"</span></div>");
                    var $sendDate = $("<div class='sendState' style='width:20%'><div  class='iconHover fffffff' style='cursor:pointer' data-container='body' data-toggle='popover' data-placement='bottom'  data-toggle='popover'  data-content='<div><span>成功发送人数:"+(obj.sentCount||'0')+"</span><br><span>失败人数:"+(obj.errorCount||'0')+"</span></div>' style='cursor:hand'>"+(obj["status"]||'')+"<span><i class='icon-bell-alt'></i></span></div></div>");
                    var $sendOperate = $("<div class='sendOperate' style='width:10%'><a href='#' onclick='deleteMass("+obj["id"]+");' style='color:#d12d32'>删除</a></div>");
                    var materialId = obj.materialId;
                    var material;
                    if(undefined!=materialId){
                    	var materialUrl = "../material/getMaterial?id=" + materialId + "&r=" + new Date().getTime();
						$.ajax({
							type : "get",
							url : materialUrl,
							async : false,
							success : function(data) {
								if (!isEmpty(data) && typeof data == "string") {
									data = eval("(" + data + ")");
									if (data == null) {
										return;
									}
									material = data;
									if(material.children.length>0){
										$sendText = $("<div class='sendText' style='width:20%'><span style='100%'>[图文消息1]"+(material.title||'')+"</span><br></div>");
										for(var i=0;i<material.children.length;i++){
											var child = material.children[i];
											var $childText = $("<span style='100%'>[图文消息"+(i+2)+"]"+(child.title||'')+"</span><br>");
											$sendText.append($childText);
										}
									}else{
										$sendText = $("<div class='sendText' style='width:20%'>[图文消息]"+(material.title||'')+"</div>");
									}
								}
							}
						});
                    }else if(obj["content"] != undefined){
                    	$img = $("<div class='sendImg' style='width:10%'><img class='icon' src='${rc.contextPath}/wechat/img/icon_text.jpg' width='100%'></div>");
                    	$sendText = $("<div class='sendText' style='width:20%'>[文字]"+(obj["content"]||'')+"</div>");	
                    }
                    $("#_1 ul").append($li.append($img).append($sendText).append($sendObj).append($sendState).append($sendDate).append($sendOperate));
                    $(".fffffff").mouseenter(function(){
						$(this).popover('show');
					});
			
					$(".fffffff").mouseleave(function(){
						$(this).popover('hide');
					});
					$(function () { $(".iconHover").popover({html : true });});
                }
            }
        });


        $("#messagesendbatchForm").on("submit1",function(event) {
        	var text = $("#groupId").find("option:selected").text();
            var groupId = $("#groupId").val();
        	layer.confirm("确定群发给群组\""+text+"\"吗？",{
				yes : function(index){
					layer.close(index);
					$("#messagesendbatchForm").submit();
				},
				no : function(index){
					layer.close(index);
				}
			});
        });

        // 监听tab选择
        $(".tabContiner_nav.borderbottom li").on("click",function(event) {
            var _href = $(this).attr("href");
            if(_href === "#page_icon1") {
                $("#msgType").val("mpnews");
                _0();
            } else if (_href === "#page_icon2") {
                $("#msgType").val("text");
                _1();
            } else if(_href === "#page1") {
            	$("#page1").css("display","block");
            	$("#page2").css("display","none");
            	//$(".tab_page1").css("display","block");
            } else if (_href === "#page2") {
                $("#page1").css("display","none");
            	$("#page2").css("display","block");
            	//$(".tab_page1").css("display","none");
            }

            //console.debug(_href);
        });
        
        
    });
    
    function deleteMass(id){
        layer.confirm("是否确认删除群发？",{
			yes : function(index){
				layer.close(index);
	            $.ajax({
	                url : "${rc.contextPath}/wechat/messagesendbatch/delete",
	                data : {"id":id},
	                success : function(data) {
	                	layer.alert(data.message,{time:1000});
	                	$("#btnQuery").trigger("click");
	                }
	            });
			},
			no : function(index){
				layer.close(index);
			}
		});
    }
</script>
</#macro>