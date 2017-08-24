<form id="importForm" action="${rc.contextPath}/wechat/report/leads/importXls" style="margin:10px" method="post" enctype="multipart/form-data">
	<div class="clearfix" style="margin:10px 0;">
		<label class="floatL" style="line-height:30px; display:inline-block;">选择导入文件：</label>
		<input class="floatL" type="hidden" id="hidFilePath" name="filePath"/>
		<input class="floatL" type="file" id="txtFileName" name="file" value="" style="width:150px; height: 30px;border-radius:5px;">
		<span class="floatL" id="spanButtonPlaceholder"></span>
		<div><a class="redButton floatL" data-options="iconCls:'icon-ok'" href="javascript:void(0)" id="doImportBtn" style="width:100px; text-align:center;border-radius:5px;">导入</a>
		</div><span class="flash hide" id="fsUploadProgress">
				<!-- 上传中 UI显示的区域 -->
		</span>
		<span class="floatL" style="display:block; line-height:30px; padding:0 10px;">
			文件只能是：csv文件
		</span>
	</div>
	
</form>
<script>
$(function(){
	$("#importForm").delegate("#doImportBtn","click",function(){
		var fileName=$("#txtFileName").val();
		
		if(fileName==null || fileName==""){
				layer.alert("请选择文件！");
		}else{
			var fileNameIndex=fileName.indexOf(".");
			if(fileName.substring(fileNameIndex+1)!='csv'){
				layer.alert("请选择csv文件");
				return false;
			}
			$("#importForm").submit();
			//$("#importDialog").parent(".panel").hide();
		}
		
	});
});
</script>