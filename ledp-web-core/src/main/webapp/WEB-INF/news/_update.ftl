<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#macro script>
	<script>
		window.UEDITOR_HOME_URL = "${rc.contextPath}/js/ue/";
	</script>
	<script type="text/javascript" charset="utf-8" src="${rc.contextPath}/js/ue/ueditor.config.js"></script>
	<script type="text/javascript" charset="utf-8" src="${rc.contextPath}/js/ue/ueditor.all.js"></script>
	<script type="text/javascript" charset="utf-8" src="${rc.contextPath}/js/ue/lang/zh-cn/zh-cn.js"></script>
	<script>
			var ue = UE.getEditor('editor1');
			$('#edui1').css('width','300px');
			$('#edui1_iframeholder').css('width','300px');
			
			var ue = UE.getEditor('editor2');
			$('#edui1').css('width','300px');
			$('#edui1_iframeholder').css('width','300px');
			var ue = UE.getEditor('editor3');
			$('#edui1').css('width','300px');
			$('#edui1_iframeholder').css('width','300px');
	</script>
</#macro>
<div class="clearfix tabadd messadd">
	<ul class="formlist">
		<li class="clearfix">
			<label>关联车系<em>*</em></label>
			<div class="floatL glcx">
				<ul class="clearFix">
					<li style="margin-left:0;">
						<input type="checkbox" checked="checked" />
						<span>全新爱丽舍</span>
					</li>
					<li>
						<input type="checkbox" />
						<span>赛纳</span>
					</li>
					<li>
						<input type="checkbox" />
						<span>毕加索</span>
					</li>
					<li>
						<input type="checkbox" />
						<span>富康</span>
					</li>
					<li>
						<input type="checkbox" />
						<span>凯旋</span>
					</li>
				</ul>
			</div>
		</li>
		<li class="clearfix">
			<label>公司电话<em>*</em></label>
			<div class="floatL dx">
				<ul class="clearFix">
					<li>
						<input type="checkbox" checked="checked" />
						<span>021-68988390</span>
					</li>
				</ul>
			</div>
		</li>
		<li class="clearfix">
			<label>公司地址<em>*</em></label>
			<div class="floatL dx">
				<ul class="clearFix">
					<li>
						<input type="checkbox" checked="checked" />
						<span>上海市长宁区873弄22号</span>
					</li>
				</ul>
			</div>
		</li>
		<li class="clearfix">
			<label>标题<em>*</em></label>
			<div class="floatL" style="width:70%;">
				<p class="clearfix iname">
					<input class="easyui-textbox inputlist" type="text" name="name" style="height: 30px; width: 300px;" value="如何保养汽车？"></input></p>
				<p class="mi10">标题必须在 16-18字范围内 </p>
			</div>
		</li>
		<li class="clearfix">
			<label>内容<em>*</em></label>
			<div class="floatL">
				<p class="clearfix iname">
					<ueditor id="editor1" type="text/plain"></ueditor>
				</p>
			</div>
		</li>
	</ul>
</div>
