
function general_message(code,message){
	if(!code){
		return;
	}
	var code_message=$('#'+code).find("p").html();
	if(message){
		$('#'+code).find("p").html(message);
	}
	$('#'+code).window('open');
	$(".bodyContiner").css("overflow","visible");
	setTimeout(function(){ 
		$('#'+code).window('close');
		$('#'+code).find("p").html(code_message); 
		$(".bodyContiner").css("overflow","auto");
	},1500);
}
function s(){
	alert(1);
}
function general_title_message(code,message){
	if(!code){
		return;
	}
	var code_message=$('#'+code).find("p").html();
	if(message){
		$('#'+code).find("p").html(message);
	}
	$('#'+code).window('open');
	$(".bodyContiner").css("overflow","visible");
	$('#title_ok').click(function (){
		$('#'+code).window('close');
		$('#'+code).find("p").html(code_message); 
		$(".bodyContiner").css("overflow","auto");
		$('#title_ok').unbind("click");
	});
}
/**
 * 删除信息提示框的方法
 * delete_url：是访问后台的路径
 * message:是提示框中的内容
 * isMessage:是是否引用一般信息提示框方法，true为引用，false为不引用
 * data_function：是点击确定后ajax执行成功后引用自己写的方法
 * */
function delete_message(delete_url,message,isMessage,callback){
	if(!delete_url){
		return;
	}
	var code_message=$('#delete_message').find("p").html();
	if(message){
		$('#delete_message').find("p").html(message);
	}
	$('#delete_message').window('open');
	$('#delete_ok').click(function (){
		$('#delete_message').window('close');
		$.ajax({
			url:delete_url,
			data:{},
			type:'post',
			cache:false,
			 dataType : 'json', 
			success:function(data) {
				if(isMessage){
					general_message(data.code,data.message);
				}
				if(callback){
					callback(data);
				}
				$('#delete_ok').unbind("click");
				$('#delete_cancel').unbind("click");
			}
			
		});
	});
	$('#delete_cancel').click(function (){
		$('#delete_message').window('close');
		closeMessage();
	});
}

/* 启用/禁用 */
function onOff_message(delete_url,message,callback){
	if(!delete_url){
		return;
	}
	var code_message=$('#delete_message').find("p").html();
	if(message){
		$('#delete_message').find("p").html(message);
	}
	$('#delete_message').window('open');
	$('#delete_ok').click(function (){
		$('#delete_message').window('close');
		$.ajax({
			url:delete_url,
			data:{},
			type:'post',
			cache:false,
			 dataType : 'json', 
			success:function(data) {
				window.location="index";
				$('#delete_ok').unbind("click");
				$('#delete_cancel').unbind("click");
			}
			
		});
	});
	$('#delete_cancel').click(function (){
		$('#delete_message').window('close');
		closeMessage();
	});
}



function lockup(url1,lockupGroup){
	$.ajax({
		url:url1,
		data:{lockupGroup:lockupGroup},
		type:'post',
		cache:false,
		success:function(data) {
			showLockup(data);
		}
		
	});
}
function showLockup(data1){
	$('#lockup_callback').html(data1);
	 $('#lockup_callback').window('open');
}

function callbacku_ok(json,lockupGroup,callback){
	if(callback){
		callback(json);
		closeLockup_callback();
	}else{
		$.each(json, function(key, value) {   
			  $("input[name='"+lockupGroup+"."+key+"']").val(value);
		});
		closeLockup_callback();
	}
	
}
function checkbox_select(checkbox_id,json){
	var checkboxid=$('#'+checkbox_id).is(':checked');
	if(checkboxid){ 
		$.each(json, function(key, value) {   
			var key_id=$('#'+key+'_id');
			var string_html="";
			var input_lockup=$('#input_lockup');
			if(key_id.length<=0){
				$("<input type='hidden' name='"+key+"_id' id='"+key+"_id' lockup="+key+">").appendTo(input_lockup);
				key_id=$('#'+key+'_id');
			}
			var key_value=key_id.val();
			if(!key_value){
				key_id.val(value);
			}else{
				if(key_value.indexOf(value)<0){
					key_id.val(key_value+','+value);
				}
			}
		});
	}else{
		$.each(json, function(key, value) {   
			var key_id=$('#'+key+'_id');
			var key_value=key_id.val();
			var arr_values=key_value.split(',');
			if($.inArray(value,arr_values)<0){
				return false;
			}
			if(key_value.indexOf(value)==0){
				if(key_value.indexOf(value+",")<0){
					key_value=key_value.replace(value,"");
				}else{
					key_value=key_value.replace(value+",","");
				}
			}else{
				key_value=key_value.replace(","+value,"");
			}
			key_id.val(key_value);
		});
	}
	
}

function callbackup_oks(lockupGroup,callback){
	if(callback){
		var data={};
		$('#input_lockup').children('input').each(function (){
			data[$(this).attr('lockup')]=$(this).val();
		});
		callback(data);
	}else{
		if($('#input_lockup').children('input').length<=0){
			general_message('error_message','请选择数据！');
			return false;
		}
		var b1_1=true
		$('#input_lockup').children('input').each(function (){
			if(!$(this).val()){
				b1_1=false;
			}
			 $("input[name='"+lockupGroup+"."+$(this).attr('lockup')+"']").val($(this).val());
		});
		if(b1_1){
			closeLockup_callback();
		}else{
			general_message('error_message','请选择数据！');
			return false;
		}
	}
}
function checkbox_selectall(select_id,select_all_id){
	var selectId=$('#'+select_id).is(':checked');
	if(selectId){
		$('#'+select_all_id).find("input[type='checkbox']").each(function (){
			$(this).prop("checked", false);
			$(this).click();
		});
	}else{
		$('#'+select_all_id).find("input[type='checkbox']").each(function(){
			$(this).prop("checked", true);
			$(this).click();
		});
	}
	
}
function closeLockup_callback(){
	$('#lockup_callback').window('close');
	$('#lockup_callback').children().remove();
	$('#input_lockup').children().remove();
}
function closeMessage(){
	$('#delete_ok').unbind("click");
	$('#delete_cancel').unbind("click");
}