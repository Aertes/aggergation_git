<script type="text/javascript">
	<#if !loginUser?exists>
		parent.window.location.href="${rc.contextPath}/wechat/";
	</#if>
	window.onload=function(){
    	window.frames[0].location.href="${url}?preview=true&new="+Math.random();
	}
</script>
<iframe src="" id="preview" width="100%" height="500"> 
</iframe> 