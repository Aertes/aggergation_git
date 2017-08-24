<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<style>
	.provar td{
		padding:5px; 
		background:#e5e5e5; 
		border:1px solid #b2b2b2;
	}
	.window{
		*overflow:visible;
	}
	.detab td{
		border:1px solid #b2b2b2;
		border-left:none;
		border-bottom:none;
		padding:0 5px;
	}
	.detab th{
		border-right:1px solid #b2b2b2;
	}
	.itobdy td{
		border-bottom:1px solid #e9e9e9;
		border-right:1px solid #e9e9e9;
		padding:5px 10px;
	}
</style>
<script type="text/javascript">
	$(".panel-tool-close").click(function(){
		$(".bodyContiner").css("overflow","auto");
		$(".body_shadow").css("display","none");
	});
	function submitContent(){
		$(".body_shadow").css("display","none");
		$(".bodyContiner").css("overflow","auto");
	
		var contentTable = $("#content_table");
		$("input[type='radio']:checked",contentTable).each(function(i,checkbox){
			$(checkbox).remove();
		});
		$("input[type='radio']",contentTable).not("input:checked").each(function(i,checkbox){
			var divId = "div_"+checkbox.id;
			$("#"+divId).remove();
		});
		
		$("select",contentTable).each(function(i,input){
			var labelId = input.name+"_label";
			$("#"+labelId).html(input.value).show();
			$(input).remove();
		});
		$("input[type='text']",contentTable).each(function(i,input){
			if(""==input.value){
				$(input).remove();
				//移除所有显示
				$("#p_"+input.name).remove();
				$("#sp_"+input.name).remove();
			}else{
				var labelId = input.name+"_label";
				$("#"+labelId).html(input.value).show();
				$(input).remove();
			}
			
		});
		//动态添加HTML代码
		var content = contentTable.html();
		KE.appendHtml("content1","</br>"+content+"</br>");
		$( "#insertContent" ).dialog("close");
		$( "#insertContent" ).html("");
		$("input[name='toggleSeries']:checked").each(function(i,s){
			$("input[value='"+s.value+"']").attr("checked",true);
		});
	}
	function toggleSeries(checked,seriesId){
		var row = $("#row_"+seriesId);
		if(checked && row.size()==0){
    		$.ajax({
    			url:"${rc.contextPath }/promotion/series/"+seriesId,
    			success:function(rows){
    				$("#t_table_trs").append(rows);
    			}
    		});
		}else{
			row.remove();
		}
	}
</script>

<div id="w" class="easyui-window" title="添加优惠" data-options="modal:true,closed:true,iconCls:'icon-save'" style="padding:10px;">
	<div class="clearfix messadd formlist">
		<label style="line-height:35px; text-align:left; width:8%;">关联车系<em>*</em></label>
		<div class="floatL glcx" style="width:82%;">
		<table width="100%" class="provar">
			<tr>
				<td style="width:20%;">车系</td>
				<td style="width:80%;">车型</td>
			</tr>
			<#if seriesesMap?exists>
			   <#list seriesesMap?keys as key>
			   <tr>
				<td>${key}</td>
				<td>
				<ul class="clearfix">
					<#list seriesesMap[key] as vehicle >
						<li>
							<input style="margin-right:5px;" name="toggleSeries" type="checkbox" value="${vehicle.id}" onClick="toggleSeries(this.checked,${vehicle.id});"/>
							<span>${vehicle.name}</span>
						</li>
					</#list>
				</ul>
				</td>
				</tr>
			   </#list>
			  </#if>
		</table>
		</div>
	</div>
	<div class="detab" id="content_table">
		<table border='0' cellpadding='0' cellspacing='0' width='100%' class='detab' style='margin:5px 0;border-left:1px solid #e9e9e9;border-top:3px solid #e9e9e9'>
			<thead>
				<th width='25%' style='border-bottom:1px solid #e9e9e9; padding:5px 10px;'>车型</th>
				<th width='10%' style='border-bottom:1px solid #e9e9e9; padding:5px 10px;'>裸车价</th>
				<th width='30%' style='border-bottom:1px solid #e9e9e9; padding:5px 10px;'>现金优惠</th>
				<th width='15%' style='border-bottom:1px solid #e9e9e9; padding:5px 10px;'>礼包设置</th>
				<th width='10%' style='border-bottom:1px solid #e9e9e9; padding:5px 10px;'>现车情况</th>
			</thead>
			<tbody id="t_table_trs" class='itobdy'>
			</tbody>
		</table>
	</div>
	<div class="btnsumbit" style="margin:20px 0 0 305px;">
		<a href="javascript:void(0);" class="btnstyle l-btn l-btn-small" onClick="submitContent();">确 定</a>
		<a href="javascript:void(0);" class="easyui-linkbutton backbtn l-btn l-btn-small"  onClick="$('#insertContent').dialog('close');$('.bodyContiner').css('overflow','auto');$('.body_shadow').css('display','none');" ><span class="l-btn-left"><span class="l-btn-text">返 回</span></span></a>
	</div>
</div>


