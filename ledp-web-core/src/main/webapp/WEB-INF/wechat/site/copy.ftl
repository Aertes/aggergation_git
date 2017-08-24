<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#assign tag=JspTaglibs["/WEB-INF/taglib/permission.tld"]>
<#include "/wechat/layouts/main.ftl"/>
<@html></@html>
<#macro style>
	<style>
		.nav-list li.active i.icon-dashboard{
			color:#fff;
		}
		.nav-list li.active .menu-text{
			color:#333;
		}
		.marginLeft40{
			margin-left:40%;
		}
		#navbar{
			margin-bottom:0;
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
			<a href="#">营销应用</a>
			<span>&gt;&gt;</span>
			<span><a href="${rc.contextPath}/wechat/site/index">微站管理</a></span>
			<span>&gt;&gt;</span>
			<span><a href="${rc.contextPath}/wechat/site/update/${site.id}">填写微站基本信息</a></span>
			<span>&gt;&gt;</span>
			<span><a href="${rc.contextPath}/wechat/campaign/callBack?siteId=${site.id}">选择模板</a></span>
			<span>&gt;&gt;</span>
			<span>编辑页面</span>
		</li>
	</ul>
</div>
<div class="active_slider">
	<div class="sliderBar">
	</div>
	<div class="sliderPointer marginLeft15">
		<div>
			<span>1</span>
		</div>
		<span>填写微站基本信息</span>
	</div>
	<div style="position:absolute;left:29%;margin-top:0;"><i style="color:#333;font-size:22px;" class="icon-double-angle-right"></i></div>
	<div class="sliderPointer marginLeft40">
		<div>
			<span>2</span>
		</div>
		<span>选择模版</span>
	</div>
	<div style="position:absolute;left:55%;margin-top:0;"><i style="color:#333;font-size:22px;" class="icon-double-angle-right"></i></div>
	<div class="sliderPointer active marginLeft65">
		<div>
			<span>3</span>
		</div>
		<span>编辑页面</span>
	</div>
</div>
<div class="modelChangeBack">
	先选择左边页面里面的页面进行修改，您可以通过拖拽左边的插件来改变页面中元素显示
</div>
<div style="background: #f4f5f9;">
	<span style="padding-left:65px;text-indent: 5%;color: #8d8d8d;line-height: 25px;font-family:'微软雅黑'">
	1)先在页面列表中点击要调用的页面模板，可以进行页面标题和插件内容设置（插件调用可参见插件列表），页面编辑完后记得保存
</span><br/>
<span style="padding-left:65px;text-indent: 5%;color: #8d8d8d;font-family:'微软雅黑'">
	2)页面列表中的页面，您可以添加、删除（不可撤销），最后仅保留您需要的页面，对每页做好保存，最后创建应用;
</span><br/>
<span style="padding-left:65px;text-indent: 5%;color: #8d8d8d;line-height: 25px;padding-bottom: 10px;font-family:'微软雅黑'">
	3)基础插件只需要进行基础配置;
</span><br/>
</div>
<div class="activemainPages">
	<div class="pageLeft">
		<div class="tabContainer tabChange">
			<ul class="tabContiner_nav borderbottom">
				<li class="active" href="#page1">
					<a >页面列表</a>
				</li>
				<li  href="#page2">
					<a >插件列表</a>
				</li>
			</ul>
				<div class="tabContianer_con borderAround">
					<div id="page1" style="display: block;">
						<ul class="modalPageList JZ_pageList">
							<#list sitePages as sitePage>
								<li class="active" >
									<img src="${imgDir+sitePage.filePath}" onclick="getSitePage('${sitePage.id}');"/>
									<p>
										<#if sitePage.bak1=='new'>
											<input type="checkbox" name="activeModal" class="JZ_activeModal" id="${sitePage.id}"/>
										</#if>
										<span><#if sitePage.name?exists >${sitePage.name}</#if></span>
									</p>
								</li>
							</#list>
							<li style='border:1px solid #d9d9d9' id="modal_add_page"><a href='javascript:void(0)' style='font-size:15px;' class='icon-plus'></a></li>
						</ul>
					</div>
					<div id="page2" class="activePluginList JZ_pluginLi">
					</div>
				</div>
			</div>
		</div>

	<div class="pageCenter">
		<p class="phoneStyle">
			<i class="phone1"></i>
			<i class="phone2"></i>
		</p>
		<div class="phoneContent">
			<p class="activeSearch">
				<input type="hidden" name="sitePageId" id="sitePageId"/>
				<input type="hidden" name="siteId" id="siteId" value="${site.id}"/>
				<input type="hidden" name="liulanPageId" id="liulanPageId"/>
				<input type="text" style="width:94%;" placeholder="请填写您要设置的页面访问路径" name="sitePageName" id="sitePageName"/>
				<a href="javascript:void(0);" class=" icon-repeat"></a>
			</p>
			<div class="jianzhan_content">
			
			</div>
			<div class="phoneFoot">
				<i class="circle"></i>
			</div>
		</div>
	</div>

	<div class="pageRight">
		<div class="tabContainer tabChange">
			<ul class="tabContiner_nav borderbottom">
				<li class="active" href="#page11">
					<a >基础设置</a>
				</li>
				<li  href="#page21">
					<a >高级设置</a>
				</li>
			</ul>
				<div class="tabContianer_con" style='overflow:auto'>
					<input type="hidden" name="currentPluginId" id="currentPluginId" />
					<input type="hidden" name="currentPageId" id="currentPageId" />
					<input type="hidden" name="pluginId" id="pluginId" />
					<input type='hidden' name="currentPluginType" id="currentPluginType">
					<div id="page11" class="JZ_baseCon pluginEditpageTemplate" style="display: block;border:1px solid #d9d9d9d9;border-top:0px;margin-top:10px;">
					</div>
					<div id="page21" class="JZ_gaojiset pluginEditpageTemplate" style="border:1px solid #d9d9d9d9;border-top:0px;margin-top:10px;">
					</div>
				</div>
			</div>
	</div>
	<div class="clearFix" style="padding-left: 380px;">
		<input type="button" class="tongyiButton" onclick="daileSitePage();" style="width:100px;float:left;margin:0px;"  value="预 览" />
		<input type="button" class="tongyiButton" onclick="saveHtml();" style="width:100px;float:left;margin-left:20px;" value="保 存" />
		<input type="button" class="tongyiButton"  style="width:100px;float:left;margin-left:20px;" onclick="window.location.href='${rc.contextPath}/wechat/site/callBack?siteId=${site.id}'" value="返回" />
	</div>
</div>
</#macro>
<#macro script>
	<script type="text/javascript" src="${rc.contextPath}/wechat/js/scollBanner.js" charset="UTF-8"></script>
	<script type="text/javascript" src="${rc.contextPath}/wechat/js/modal.js" charset="UTF-8"></script>
	<script type="text/javascript" src="${rc.contextPath}/wechat/js/dynamicPlugin.js" charset="UTF-8"></script>
	<script type="text/javascript" src="${rc.contextPath}/wechat/js/basePlugin.js" charset="UTF-8"></script>
	<script type="text/javascript" src="${rc.contextPath}/wechat/js/webSite.js" charset="UTF-8"></script>
	<script type="text/javascript" src="${rc.contextPath}/wechat/js/checkdata.js" charset="UTF-8"></script>
	<script>
		var $sitPageName="";
		$(function(){
			/* 解决IE浏览器Get请求是用缓存问题 */
        	$.ajaxSetup({cache:false});
        	
			webSite.init();
			$("#sidebar").addClass("menu-min");
			<#if sitePage1?exists>
				getSitePageHtml('${sitePage1.id}');
				$("#sitePageName").val('${sitePage1.name}');
			</#if>
			$("#sitePageName").blur(function(){
			var name=$("#sitePageName").val();
			if(name==""){
				layer.alert("请输入页面名称",{
					title : "信息提示框" ,
					time : 2000,
					dialog : true
				},function(index){
					layer.close(index);
					$("#sitePageName").focus();
				});
				return false;
			}else{
				if(name!=$sitPageName){
					var id=$("#sitePageId").val();
					$.ajax({
						url:"${rc.contextPath}/wechat/site/saveNewPageNmae",
						data:{name:name,id:id},
						type:'post',
						cache:false,
						dataType : 'json', 
						success:function(data) {
							$("#"+id).next().html(name);
							$sitPageName=name;
							saveHtml();
						}
					});
				}
			}
		})
		
		$(".body").delegate(".tableCheck","click",function(){
			webParams.rightClick();
		});
		
		$(".JZ_pageList").delegate(".JZ_activeModal","click",function(event){
			if($(".JZ_activeModal:checked").length>0  ){
				if($("#modal_delete_page").length<=0){
					var $li_delete = $("<li style='border:1px solid #d9d9d9' onclick='deleteSitePage();' id='modal_delete_page'></li>");
					$li_delete.append($("<a href='javascript:void(0)' style='font-size:15px;'>删除</a>"));
					$(".JZ_pageList").append($li_delete);
				}
			}else{
				$("#modal_delete_page").remove();
			}
		});
		
		
		$("#modal_add_page").click(function(){
			$(".JZ_baseCon").children().remove();
			$(".JZ_gaojiset").children().remove();
			var id = $("#siteId").val();
			var content=$('.jianzhan_content').html();
			if(content.trim()=="" || content==null){
				getNewPage(id);
			}else{
				layer.confirm("确认要保存吗？", {}
					,function(index){
						layer.close(index);
						$(".JZ_baseCon").css("display","none");
						$(".JZ_gaojiset").css("display","none");
						saveHtml();
						getNewPage(id);
					},function(index){
						getNewPage(id);
						$(".JZ_baseCon").css("display","none");
						$(".JZ_gaojiset").css("display","none");
						layer.close(index);
					}
				);
			}
		});
		});
		function deleteSitePage(){
			var id_list = [];
			var objList = [];
			var b=false;
			$(".JZ_activeModal:checked").each(function(){
				id_list.push($(this).attr("id"));
				objList.push($(this));
				if($(this).attr("id")==$("#sitePageId").val()){
					b=true;
				}
			});
			if(id_list.length>=0){
				$.ajax({
					url : "${rc.contextPath}/wechat/campaign/deleteSitePages",
					data : {
						list : id_list
					},
					type : "POST" ,
					success : function(data){
						if(data!="" && data!=null){
							if(typeof data == "string"){
								data = eval("("+data+")");
							}
						}
						layer.alert(data["value"],{
							time : 2000 ,
							animate : true
						});
						if(data["value"]=="删除成功"){
							for(var obj in objList){
								$(objList[obj]).parents("li:first").remove();
							}
						}
						if($(".JZ_activeModal:checked").length<=0){
							$("#modal_delete_page").remove();
						}
						if(b){
							$("#sitePageId").val('');
							$("#sitePageName").val('');
							$("#liulanPageId").val('');
							$('.jianzhan_content').html('');
						}
						$(".JZ_baseCon").css("display","none");
						$(".JZ_gaojiset").css("display","none");
						
					},
					fail : function(){}
				});
			}
		}
		var layerConfirm = new ElasticLayer("confirm","确认要保存吗？",{})
		function getSitePage(id){
			var content=$('.jianzhan_content').html();
			var sitePage = $("[name='sitePageName']").nextAll("span").length;
			if(sitePage>=1){
				$("[name='sitePageName']").trigger("focusout");
			};
			if(content.trim()=="" || content==null){
				getSitePageHtml(id);
			}else{
				if($("#sitePageId").val()!=id){
					if(webParams.saveState == true){
						//layer.close(index);
						//saveHtml();
						//获取form表单的值
						$(".JZ_baseCon").css("display","none");
						$(".JZ_gaojiset").css("display","none");
						$("#currentPageId").val(id);
						getSitePageHtml(id);
					}else{
						layer.confirm("确认要保存吗？", {},
							function(index){
								layer.close(index);
								saveHtml();
								//获取form表单的值
								$(".JZ_baseCon").css("display","none");
								$(".JZ_gaojiset").css("display","none");
								$("#currentPageId").val(id);
								getSitePageHtml(id);
							},function(index){
								$(".JZ_baseCon").css("display","none");
								$(".JZ_gaojiset").css("display","none");
								getSitePageHtml(id);
								layer.close(index);
							}
						);
					}
				}
				
			}
		}
		function saveHtml(){
			var id=$('#sitePageId').val();
			var _script = $("#replace_srcipt").html();
			$("#replace_srcipt").html('');
			//$("body").find(".pageCenter select").children().remove();
			var content=$('.jianzhan_content').html();
			if(_script!=undefined&&_script!=''){
				$("#replace_srcipt").html(_script);
			}
			$.ajax({
				url:"${rc.contextPath}/wechat/campaign/savePage",
				data:{id:id,content:content},
				type:'post',
				cache:false,
				dataType : 'json', 
				success:function(data) {
					webParams.saveState = true;
					var layerAlert = new ElasticLayer("alert",data.value,{title : "信息提示框",dialog : true,time : 2000});
					layerAlert.init();
				}
			});
		}
		function getSitePageHtml(id){
			$.ajax({
				url:"${rc.contextPath}/wechat/campaign/detailSitePage/"+id,
				data:{},
				type:'post',
				cache:false,
				dataType : 'json', 
				success:function(data) {
					webParams.saveState = false;
					$("#sitePageId").val(data.id);
					$("#liulanPageId").val(data.id);
					$("#sitePageName").val(data.name);
					$sitPageName=data.name;
					$('.jianzhan_content').children().remove();
					$('.jianzhan_content').html(data.html);
					if(data.name=='新车展厅' || data.name=='车型详情'){
						$(".JZ_baseCon").css("display","block");
						$('.JZ_baseCon').html("<a href='"+"http://"+window.location.host+"/vehicleSeries/index' target='view_window'>设置相关车系,请点击</a>");
					};
					var sitePage = $("[name='sitePageName']").nextAll("span").length;
					if(sitePage>=1){
						$("[name='sitePageName']").trigger("focusout");
					};
				}
			});
		}
		
		
		
		function getNewPage(id){
			$.ajax({
				url:"${rc.contextPath}/wechat/campaign/createNewPage/"+id,
				data:{},
				type:'post',
				cache:false,
				dataType : 'json', 
				success:function(data) {
					var $li=$("<li class='active' >");
					var $img=$("<img src='"+data.img+"' onclick='getSitePage("+data.id+");' />");
					var $p=$("<p>");
					var $input=$("<input type='checkbox' name='activeModal' class='JZ_activeModal' id='"+data.id+"'/>")
					var $span=$("<span id='sitPage-"+data.id+"'></span>");
					if(data.name){
						$span=$("<span id='sitPage-"+data.id+"'>"+data.name+"</span>");
					}
					$sitPageName="";
					$p.append($input).append($span);
					$li.append($img).append($p);
					$("#modal_add_page").before($li);
					var sitePageId=$("#sitePageId").val();
					if(sitePageId){
						$("#"+sitePageId).attr("checked",false);
					}
					$("#"+data.id).attr("checked",true);
					$("#sitePageId").val(data.id);
					$("#liulanPageId").val(data.id);
					$("#sitePageName").val('');
					$("#sitePageName").focus();
					$('.jianzhan_content').html(data.html);
				}
			});
		}
		function daileSitePage(){
			var id=$('#sitePageId').val();
			if(id){
				var reqUrl = "${rc.contextPath}/wechat/site/getUrlPage/"+id+"?r="+new Date().getTime();
				var layerPage = new ElasticLayer("pageWindow",reqUrl,{area:["400px","80%"],offset:["5%","30%"]},{
					success : function(layero ,index){
						
					}
				})
				layerPage.init();
			}else{
				var layerAlert = new ElasticLayer("alert","请选择页面",{title : "信息提示框",dialog : true,time : 2000});
				layerAlert.init();
			}
		}
	</script>
</#macro>