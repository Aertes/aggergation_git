<html>
	<head>
		<meta charset="utf-8">
		<title></title>
		<link rel="stylesheet" href="${rc.contextPath}/wechat/css/custome.css" />
	</head>
	<body>
		<div class="menuPreview">
			<div class="menuList">
			</div>
			<div class="menuPreContent">
			</div>
		</div>
		<script type="text/javascript">
			window.jQuery || document.write("<script src='${rc.contextPath}/wechat/assets/js/jquery-2.0.3.min.js'>"+"<"+"/script>");
		</script>
		<script type="text/javascript">
			$.ajax({
				url : "${rc.contextPath}/wechat/menu/previewData" ,
				type : "POST" ,
				dataType : "json" ,
				success : function(data){
					if(typeof data == "string" ){
						data = eval("("+data+")");
					}
					var $ul = $("<ul>");
					for(var i=0;i<data.length;i++){
						var $li= bindData(data[i] , "parent");
						if(data[i]["children"].length>0){
							var chilren = data[i]["children"];
							var right = 0;
							if(data.length==3){
								right = 30+(79*i);
							}else if(data.length==2 || data.length==1){
								var navlength = data.length-1;
								right = 186-(78*(navlength-i));
							}
							var $ulChil = $("<div class='menuNav' style='right:"+right+"px'></div>");
							var $ulc = $($("<ul>"));
							for(var j=0;j<chilren.length;j++){
								var $lichild = bindData(chilren[j] ,"child");
								$ulc.append($lichild);
							}
							$li.append($ulChil.append($ulc));
						}
						$ul.prepend($li);
					}
					$(".menuList").append($ul);
					showMenuList();
				}
			});
					
					
					
			function bindData(data , message){
				var id = data["id"] ,name = data["name"],type= data["type"] ,content =data["urlOrContent"];
				var $li = $("<li type='"+message+"'>"),$a = $("<a href='javascript:void(0)'>").text(name);
				if(type == "view"){
					$a.prop({"href":content , "target":"_blank"});
					$a.click(function(){
						$(this).parents(".menuNav").css("display","none");
					});
				}else if(type == "click"){
					$a.attr("meritalId",content);
					$a.click(function(){
						getMeterial(this);
					});
				}
				$li.append($a);
				return $li;
			}
			
			function showMenuList(){
				$("[type='parent']").click(function(){
					$(".menuPreview").find(".menuNav").css("display","none");
					$(this).find(".menuNav").css("display","block");
				});
			}
			
			function getMeterial(obj){
				var mertailId = $(obj).attr("meritalId");
				$.ajax({
					url : "${rc.contextPath}/wechat/material/getMaterial",
					type : "POST" ,
					data : {
						id : mertailId
					},
					dataType : "json",
					success : function(data){
						if(typeof data == "string"){
							data = eval("("+data+")");
						}
						var material = data;
							if(material.type.id=="9003"){
							var $div = $("<div class='menuPrevew_publi'></div>");
							var $title = $("<p class='menuPrevew_publi_title'>"+data["title"]+"</p><p class='menuPrevew_publi_content'>"+data["createDate"]+"</p>");
							var $img = $("<img style='width:100%' src='"+data["url"]+"'>");
							var $digest = $("<div><p class='menuPrevew_publi_content' style='text-index:2em'>"+data["digest"]+"</p></div>");
							var child_content_list = $("<div style='overflow:hidden;width:100%'></div>");
							if(data["children"].length>0){
								for(var i=0;i<data["children"].length;i++){
									var child = data["children"][i];
									var $child_div = $("<div style='clear:both; overflow:hidden;font-size:14px;'><p style='float:left;width:45%'>"+child["title"]+"</p><img src='"+child["url"]+"' style='width:45%;float:right;margin:3%'></div>");
									$(child_content_list).append($child_div);
								}
								$div.append($title).append($img).append(child_content_list);
							}else{
								$div.append($title).append($img).append($digest);
							}
							
							$(".menuPreContent").append($div);
							$(obj).parents(".menuNav").css("display","none");
						}else if(material.type.id=="9002"){
							var $div = $("<div class='menuPrevew_publi'></div>");
							var $img = $("<img style='width:100%' src='"+data["url"]+"'>");
							var child_content_list = $("<div style='overflow:hidden;width:100%'></div>");
							$div.append($img)
							$(".menuPreContent").append($div);
							$(obj).parents(".menuNav").css("display","none");
						}
					},fail : function(){}
				});
			}
		</script>
	</body>
</html>