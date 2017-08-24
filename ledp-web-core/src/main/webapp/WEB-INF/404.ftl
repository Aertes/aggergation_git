<!DOCTYPE html>
<html>

	<head>
		<meta charset="UTF-8">
		<title>东风长效媒体平台</title>
		<link rel="stylesheet" type="text/css" href="${rc.contextPath}/css/easyui.css">
		<link rel="stylesheet" type="text/css" href="${rc.contextPath}/css/icon.css">
		<link rel="stylesheet" type="text/css" href="${rc.contextPath}/css/demo.css">
		<link rel="stylesheet" type="text/css" href="${rc.contextPath}/css/custom.css">
		<link rel="stylesheet" type="text/css" href="${rc.contextPath}/css/chart.css">
		<script type="text/javascript" src="${rc.contextPath}/js/jquery.min.js"></script>
		<script type="text/javascript" src="${rc.contextPath}/js/jquery.easyui.min.js"></script>
		<script type="text/javascript" src="${rc.contextPath}/js/custom.js"></script>
		<!--[if lte IE 8]>
			<script src="excanvas.js"></script>
		<![endif]-->
	</head>
	<body class="easyui-layout">
		<div class="header" data-options="region:'north',border:false">
			<div class="header clearFix">
				<div class="floatL title">
					东风雪铁龙经销商长效媒体管理大数据平台
				</div>
				<div class="floatR tr">
					<a href="${rc.contextPath}/member.html" title="admin">
						<img alt="" src="${rc.contextPath}/css/images/user_phone.png" style="width: 16px; height: 13px;" class="pr5" />admin
					</a>
					<a href="${rc.contextPath}/download/download" title="用户手册">用户手册</a>
					<div class="floatL">
						<a href="${rc.contextPath}/member.html" title="admin" class="admin pre">
							<img alt="" src="${rc.contextPath}/css/images/user_name.gif" style="width: 16px; height: 13px;" class="pr5" />设置
						</a>
					</div>
					<a href="${rc.contextPath}/#" title="">
						<img alt="注销" src="${rc.contextPath}/css/images/logout.gif" style="width: 16px; height: 13px;" class="pr5" />注销
					</a>
				</div>
			</div>
		</div>
		<div data-options="region:'west',split:false,title:false" class="leftMain">
			<ul>
				<li class="sidelist activeBg">
					<a class="hoverBg first" href="index.html" title="">首页</a>
				</li>
				<li class="sidelist">
					<a class="hoverBg" href="" title="">数据管理</a>
					<ul class="i-list">
						<li>
							<div>
								<h1>媒体线索</h1>
								<p>
									<a href="${rc.contextPath}/#" title="">线索列表</a>
								</p>
							</div>
							<div>
								<h1>客户管理</h1>
								<p>
									<a href="${rc.contextPath}/#" title="">客户列表</a>
								</p>
							</div>
						</li>
					</ul>
				</li>
				<li class="sidelist">
					<a class="hoverBg" href="" title="">信息发布</a>
					<ul class="i-list">
						<li>
							<div>
								<h1>公告信息</h1>
								<p>
									<a href="${rc.contextPath}/#" title="">信息列表</a>
								</p>
							</div>
							<div>
								<h1>促销信息</h1>
								<p>
									<a href="${rc.contextPath}/#" title="">信息列表</a>
								</p>
							</div>
							<div>
								<h1>推荐文章</h1>
								<p>
									<a href="${rc.contextPath}/#" title="">信息列表</a>
								</p>
							</div>
						</li>
					</ul>
				</li>
				<li class="sidelist">
					<a class="hoverBg" href="" title="">报表管理</a>
					<ul class="i-list">
						<li>
							<div>
								<h1>表现趋势分析</h1>
								<p>
									<a href="${rc.contextPath}/#" title="">线索数量</a>
								</p>
							</div>
							<div>
								<h1>媒体表现分析</h1>
								<p>
									<a href="${rc.contextPath}/#" title="">信息发布量</a>
								</p>
								<p>
									<a href="${rc.contextPath}/#" title="">信息推荐量</a>
								</p>
								<p>
									<a href="${rc.contextPath}/#" title="">400 电话</a>
								</p>
							</div>
							<div>
						</li>
					</ul>
				</li>
				<li class="sidelist">
					<a class="hoverBg" href="#" title="">系统管理</a>
					<ul class="i-list">
						<li>
							<div>
								<h1>系统管理</h1>
								<p>
									<a href="${rc.contextPath}/organization/index.html" title="">组织管理</a>
								</p>
								<p>
									<a href="${rc.contextPath}/dealer/index.html" title="">网点管理</a>
								</p>
								<p>
									<a href="${rc.contextPath}/#" title="">用户管理</a>
								</p>
								<p>
									<a href="${rc.contextPath}/#" title="">角色管理</a>
								</p>
								<p>
									<a href="${rc.contextPath}/#" title="">日志管理</a>
								</p>
							</div>
						</li>
					</ul>
				</li>
				<li class="sidelist">
					<a class="hoverBg" href="" title="">基础信息</a>
					<ul class="i-list">
						<li>
							<div>
								<h1>字典表管理</h1>
								<p>
									<a href="${rc.contextPath}/#" title="">网点字典表</a>
								</p>
								<p>
									<a href="${rc.contextPath}/#" title="">车系字典表</a>
								</p>
								<p>
									<a href="${rc.contextPath}/#" title="">车型字典表</a>
								</p>
								<p>
									<a href="${rc.contextPath}/#" title="">线索字典表</a>
								</p>
								<p>
									<a href="${rc.contextPath}/#" title="">信息字典表</a>
								</p>
							</div>
							<div>
								<h1>接口管理</h1>
								<p>
									<a href="${rc.contextPath}/#" title="">线索接口</a>
								</p>
								<p>
									<a href="${rc.contextPath}/#" title="">信息接口</a>
								</p>
							</div>
						</li>
					</ul>
				</li>
			</ul>
		</div>
		<div data-options="region:'center',title:false" class="rightMain">
			<div class="main errormain">
				<div>
					<img alt="" src="${rc.contextPath}/images/404.jpg" width="446px"/>
				</div>
				<div class="btnsumbit">
					<a href="${rc.contextPath}/index.html" class="easyui-linkbutton btnstyle">返回首页</a>
				</div>
			</div>
		</div>
	</body>

</html>