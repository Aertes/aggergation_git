<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#assign tag=JspTaglibs["/WEB-INF/taglib/permission.tld"]>
<#include "/wechat/layouts/main.ftl"/>
<@html></@html>
<#macro style>
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
		<li><i class="icon-home home-icon"></i> <a href="#">消息中心 >></a> <a
			href="#">群发功能</a></li>
	</ul>
	<h1 class="title_page">群发功能</h1>
	<div class="tabContainer">
		<ul class="tabContiner_nav borderbottom">
			<li class="active" href="#page1"><a>新建群发消息</a></li>
			<li href="#page2"><a>已发送</a></li>
		</ul>
		<div class="tabContianer_con">
			<div id="page1" style="display: block;">
				<div class="messagesend">
					<div class="sendMessagCondition">
						<div>
							<label>群发对象：</label> <select id="form-field-select-1"
								class="form-control">
								<option value="all" selected="selected">全部用户</option>
								<option value="person1">用户1</option>
								<option value="person2">用户2</option>
							</select>
						</div>
						<div>
							<label>群发男女：</label> <select id="form-field-select-1"
								class="form-control">
								<option value="femal" selected="selected">女</option>
								<option value="man">男</option>
							</select>
						</div>
						<div>
							<label>群发国家：</label> <select id="form-field-select-1"
								class="form-control">
								<option value="china" selected="selected">中国</option>
								<option value="Americ">美国</option>
								<option value="Au..">俄罗斯</option>
							</select>
						</div>
						<div class="guize">
							<a href="#">群发消息规则说明 <i class="icon-list-alt"></i>
							</a>
						</div>
					</div>
					<div class="tabContainer tabSendpage">
						<ul class="tabContiner_nav borderbottom">
							<li class="active" href="#page_icon1"><i class=""></i> 图文信息
							</li>
							<li href="#page_icon2"><i class=""></i> 文字信息</li>
						</ul>
						<div class="tabContianer_con displayBlock">
							<div style="display: block;" id="page_icon1"
								class="addPiContinaer">
								<div class="addPi1">
									<div style="width: 50%;" class="floatL addPar">
										<a href="#"> <i class="icon-plus add"></i> <span
											class="addTitle">新建单图文信息</span>
										</a>
									</div>
									<div style="width: 50%;" class="floatL addPar">
										<a href="#"> <i class="icon-plus add"></i> <span
											class="addTitle">新建多图文信息</span>
										</a>
									</div>
								</div>
								<div class="addPi1">
									<div style="width: 100%;" class="floatL addPar">
										<a href="#"> <i class="icon-plus add"></i> <span
											class="addTitle">从素材库选择</span>
										</a>
									</div>
								</div>
							</div>
							<div id="page_icon2">
								<div>
									<textarea class="textArea"></textarea>
								</div>
								<div class="border">
									<span class="floatR">还可以输入1000个字</span>
								</div>
							</div>
						</div>
					</div>
					<div class="lineHeight70">
						<input type="button" value="群 发" class="redButton" /> <span
							class="spanStyle1">您今天还可以发送1条消息</span>
					</div>
				</div>
			</div>
			<div id="page2" class="messagesend">
				<ul class="hadSendMessage">
					<li>
						<div class="sendImg">
							<img
								src="${rc.contextPath}/wechat/assets/images/gallery/image-2.jpg"
								width="100%" />
						</div>
						<div class="sendText">
							<a>[图文消息]不想装嫩？6.1在上海来个高大上的约会</a> <a>关注活动帮，折扣，生活，时尚全网罗不想装嫩？6.1在上海来个高大上的约会青春，背景色你的地盘你做主小编又到了</a>
						</div>
						<div class="sendState">
							<span>发送完毕</span>
						</div>
						<div class="sendState" style="width: 10%">
							<span>星期一 11:45</span>
						</div>
						<div class="sendOperate">
							<a href="#" style="color: #d12d32">删除</a>
						</div>
					</li>
					<li>
						<div class="sendImg">
							<img
								src="${rc.contextPath}/wechat/assets/images/gallery/image-2.jpg"
								width="100%" />
						</div>
						<div class="sendText">
							<a>[图文消息]不想装嫩？6.1在上海来个高大上的约会</a> <a>关注活动帮，折扣，生活，时尚全网罗不想装嫩？6.1在上海来个高大上的约会青春，背景色你的地盘你做主小编又到了</a>
						</div>
						<div class="sendState">
							<span>发送完毕</span>
						</div>
						<div class="sendState" style="width: 10%">
							<span>星期一 11:45</span>
						</div>
						<div class="sendOperate">
							<a href="#" style="color: #d12d32">删除</a>
						</div>
					</li>
					<li>
						<div class="sendImg">
							<img
								src="${rc.contextPath}/wechat/assets/images/gallery/image-2.jpg"
								width="100%" />
						</div>
						<div class="sendText">
							<a>[图文消息]不想装嫩？6.1在上海来个高大上的约会</a> <a>关注活动帮，折扣，生活，时尚全网罗不想装嫩？6.1在上海来个高大上的约会青春，背景色你的地盘你做主小编又到了</a>
						</div>
						<div class="sendState">
							<span>发送完毕</span>
						</div>
						<div class="sendState" style="width: 10%">
							<span>星期一 11:45</span>
						</div>
						<div class="sendOperate">
							<a href="#" style="color: #d12d32">删除</a>
						</div>
					</li>
					<li>
						<div class="sendImg">
							<img
								src="${rc.contextPath}/wechat/assets/images/gallery/image-2.jpg"
								width="100%" />
						</div>
						<div class="sendText">
							<a>[图文消息]不想装嫩？6.1在上海来个高大上的约会</a> <a>关注活动帮，折扣，生活，时尚全网罗不想装嫩？6.1在上海来个高大上的约会青春，背景色你的地盘你做主小编又到了</a>
						</div>
						<div class="sendState">
							<span>发送完毕</span>
						</div>
						<div class="sendState" style="width: 10%">
							<span>星期一 11:45</span>
						</div>
						<div class="sendOperate">
							<a href="#" style="color: #d12d32">删除</a>
						</div>
					</li>
					<li>
						<div class="sendImg">
							<img
								src="${rc.contextPath}/wechat/assets/images/gallery/image-2.jpg"
								width="100%" />
						</div>
						<div class="sendText">
							<a>[图文消息]不想装嫩？6.1在上海来个高大上的约会</a> <a>关注活动帮，折扣，生活，时尚全网罗不想装嫩？6.1在上海来个高大上的约会青春，背景色你的地盘你做主小编又到了</a>
						</div>
						<div class="sendState">
							<span>发送完毕</span>
						</div>
						<div class="sendState" style="width: 10%">
							<span>星期一 11:45</span>
						</div>
						<div class="sendOperate">
							<a href="#" style="color: #d12d32">删除</a>
						</div>
					</li>
					<li>
						<div class="sendImg">
							<img
								src="${rc.contextPath}/wechat/assets/images/gallery/image-2.jpg"
								width="100%" />
						</div>
						<div class="sendText">
							<a>[图文消息]不想装嫩？6.1在上海来个高大上的约会</a> <a>关注活动帮，折扣，生活，时尚全网罗不想装嫩？6.1在上海来个高大上的约会青春，背景色你的地盘你做主小编又到了</a>
						</div>
						<div class="sendState">
							<span>发送完毕</span>
						</div>
						<div class="sendState" style="width: 10%">
							<span>星期一 11:45</span>
						</div>
						<div class="sendOperate">
							<a href="#" style="color: #d12d32">删除</a>
						</div>
					</li>
				</ul>
				<div class="padding30">
					<div class="pagegination floatR">
						<ul>
							<li><a href="#"><<</a></li>
							<li class="active"><a href="#">1</a></li>
							<li><a href="#">2</a></li>
							<li><a href="#">3</a></li>
							<li><a href="#">4</a></li>
							<li><a href="#">5</a></li>
							<li><a href="#">>></a></li>
						</ul>
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
</#macro>