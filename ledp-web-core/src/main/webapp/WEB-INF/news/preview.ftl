<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<script>
	$(".panel-tool-close").click(function(){
		$('#insertContent').dialog('close');
		$('.bodyContiner').css('overflow','auto');
		$('.body_shadow').css('display','none');
	});
</script>
	<div class="main clearfix">
		<div class="newspre">
			<div class="tile">
				<h2>${news.title}</h2>
				<div class="clearfix">
					<p class="floatL">来源：经销商供稿</p>
					<p class="floatR">${news.dateCreate?string("yyyy-MM-dd")}</p>
				</div>
				<div class="cmtimee">
					<span>经销商：${news.dealer.name}</span>
					<span>类型：${news.type.name}</span>
				</div>
				<div class="lmin" style="text-align:left;text-indent:0;">
					<div class="clearfix abstract">
						<p class="floatL">信息摘要：</p>
						<p class="floatL">${news.summary}</p>
					</div>
					${news.content}
					<#if (news.includePhone?exists && news.includePhone=="true") ||(news.includeAddress?exists && news.includeAddress=="true")>
					<div class="accountinfo" style="margin:5px 0px;">
						<#if news.includeAddress?exists && news.includeAddress=="true">
						<p>商家地址：${news.dealer.address}</p>
						</#if>
						<#if news.includePhone?exists && news.includePhone=="true" && news.dealer.phone?exists>
						<p>咨询电话：<span class="phone">${news.dealer.phone}</span></p>
						</#if>
					</div>
					</#if>
				</div>
			</div>
			<div class="btnsumbit" style="margin:20px 0 0 305px;">
				<a href="javascript:void(0);" class="easyui-linkbutton backbtn l-btn l-btn-small" onclick="$('#insertContent').dialog('close');$('.bodyContiner').css('overflow','auto');$('.body_shadow').css('display','none');"><span class="l-btn-left"><span class="l-btn-text">返 回</span></span></a>
			</div>
		</div>
	</div>
