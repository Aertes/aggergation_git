<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<script type="text/javascript">
	try{ace.settings.check('sidebar' , 'fixed')}catch(e){}
</script>
<ul class="nav nav-list">
	<li class="<#if !choose?exists || choose gt 200 || choose lt 100>active</#if>">
		<a href="${rc.contextPath}/wechat/home/index">
			<i class="icon-dashboard"></i>
				<span class="menu-text">首 页</span>
		</a>
	</li>
	
	<#if menus?exists && permissions?exists> 
		<#list menus as menu1>
			<#if permissions?seq_contains(menu1.code) && menu1.wechat=1>
				<li class="<#if choose?exists && choose=menu1.choose>active</#if>">
						<#if menu1.weight==1>
							<a href="${rc.contextPath}/wechat/publicno/index">
								<i class="icon-comments"></i>
								<span class="menu-text">${menu1.name}</span>
							</a>
						</#if>
					
						<#if menu1.weight==2>
						<a href="#" class="dropdown-toggle">
							<i class="icon-th-large"></i>
							<span class="menu-text">${menu1.name}</span>
							<b class="arrow icon-angle-down"></b>
						</a>
						</#if>
						<#if menu1.weight==3>
						<a href="#" class="dropdown-toggle">
							<i class="icon-wrench"></i>
							<span class="menu-text">${menu1.name}</span>
							<b class="arrow icon-angle-down"></b>
						</a>
						</#if>
						<#if menu1.weight==4>
						<a href="#" class="dropdown-toggle">
							<i class="icon-list"></i>
							<span class="menu-text">${menu1.name}</span>
							<b class="arrow icon-angle-down"></b>
						</a>
						</#if>
						<#if menu1.weight==5>
						<a href="#" class="dropdown-toggle">
							<i class="icon-cogs"></i>
							<span class="menu-text">${menu1.name}</span>
							<b class="arrow icon-angle-down"></b>
						</a>
						</#if>
					<#if menu1.children?has_content>
						<ul class="submenu" <#if menu?exists && (menu1.id=menu.parent.id || (menu.parent.parent?exists && menu1.id=menu.parent.parent.id))>style="display: block;"</#if>>
							<#list menu1.children as menu2>
								<#if permissions?seq_contains(menu2.code)>
									<#if menu2.parent?exists && (menu2.parent.id?number=103)>
										<li <#if choose?exists && choose=menu2.choose>class="active"</#if>>
											<a href="${rc.contextPath}/${menu2.action}" target="view_window">
												<i class="icon-double-angle-right"></i>
												${menu2.name}
											</a>
										</li>
									<#else>
										<#if menu2.action?exists>
											<li class="<#if choose?exists && choose=menu2.choose>active</#if>">
												<a href="${rc.contextPath}/${menu2.action}">
													<i class="icon-double-angle-right"></i>
													${menu2.name}
												</a>
											</li>
										<#else>
											<li>
												<a href="#" class="dropdown-toggle">
													<i class="icon-edit"></i>
													<span class="menu-text">${menu2.name}</span>
													<#if menu2.name=="消息中心">
                                                        <i id="messageI" style="border-radius: 50%; display: none; width: 10px; height: 10px; background: none repeat scroll 0% 0% rgb(209, 45, 50); margin-left: 5px;"></i>
													</#if>
													<b class="arrow icon-angle-down"></b>
												</a>
												<ul class="submenu" <#if menu?exists && menu2.id=menu.parent.id>style="display: block;"</#if>>
													<#list menu2.children as menu3>
														<#if permissions?seq_contains(menu3.code)>
															<li class="<#if choose?exists && choose=menu3.choose>active</#if>">
																<a href="${rc.contextPath}/${menu3.action}">
																	<i class="icon-double-angle-right"></i>
																	${menu3.name}
																</a>
															</li>
														</#if>
													</#list>
												</ul>
											</li>
										</#if>
									</#if>
								</#if>
							</#list>
						</ul>
					</#if>
				</li>
			</#if>
		</#list>
	</#if>

</ul><!-- /.nav-list -->
<div class="sidebar-collapse" id="sidebar-collapse">
	<i class="icon-double-angle-left" data-icon1="icon-double-angle-left" data-icon2="icon-double-angle-right"></i>
</div>
<script type="text/javascript">
	try{ace.settings.check('sidebar' , 'collapsed')}catch(e){}
</script>
<script type="text/javascript">
	<!-- 每隔10秒请求服务器是否有新粉丝消息 -->
    $(function() {
        getNewMessage();
		var a = $("#sidebar a");
		for(var i = 0; i < a.length; i++){
			var url= $(a[i]).attr("href");
			if(url != "#" && url != "javascript:void(0);" && typeof(url) != "undefined"){
                $(a[i]).attr("href",url +"?_="+ jQuery.now());;
			}
		}
        setInterval(getNewMessage, 30000);//每隔30秒调用一次
    });
    function getNewMessage() {
        $.ajaxSetup({cache:false});
        //发送post请求
        $.get("${rc.contextPath}/wechat/message/newMessage", function(data){
            if(data["newMessage"]){
				$("#messageI").css("display","inline-block")
			}
        });
	};

	function getRandomUrl(){
		return jQuery.now();
	}
</script>