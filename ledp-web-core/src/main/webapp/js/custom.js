$(document).ready(function(){
	var $all = $(".chekcAll"),$checkbox = $(".checkR");
	$all.click(function(){
		$checkbox.prop("checked",$(this).prop("checked"));
	});
	$checkbox.click(function(){
		var _this =this;
		$all.prop("checked",$(".checkR:checked").length==$checkbox.length);
	});
	/* 导航 */
    $('.sidelist').mousemove(function(){
		$(this).find('.i-list').show();
		$(this).find('.hoverBg').addClass('hover');
		$(this).find('.first').removeClass('hover');
		});
		$('.sidelist').mouseleave(function(){
		$(this).find('.i-list').hide();
		$(this).find('.hoverBg').removeClass('hover');
		});
		
	/* admin */	
	$('#admin').hover(function() {
			$(this).children('p').show();
		}, function() {
			$(this).children('p').hide();
		});
	
	$("div").delegate(".ONOFF","click",function(){
		if($(this).css("display")=="inline-block" || $(this).css("display")=="inline"){
			$(".window-mask").css("height",document.body.scrollHeight +"px");
			$(".bodyContiner").css("overflow","visible");
			
			$("div").delegate("#delete_cancel","click",function(){
				$(".bodyContiner").css("overflow","auto");
			});
			
			$("div").delegate("#delete_ok","click",function(){
				$(".bodyContiner").css("overflow","auto");
			});
		}
	});
		
	/* tips */
	    $('#dd').tooltip({
		    position: 'right',
		    content: '<span style="color:#fff; line-height:20px;">如您忘记了登陆密码，可以将身份认证信息（如网点编码）<br />发送到sunmingyue@dfhtkg.com.cn，申请重置，<br />如有疑问请致电：027-84300095</span>',
		    onShow: function(){
		  	  $(this).tooltip('tip').css({
				    backgroundColor: '#666',
				    borderColor: '#666'
		   		 });
		    },
		    hideDelay:5000
    });
    
    
    /* 图表 */
   var randomScalingFactor = function(){ return Math.round(Math.random()*1000)};

	var barChartData = {
		labels : ["试驾","意向","线索数量","信息发布量","信息发布完成率","信息推荐量","400 电话来电","400 电话来电","400 电话接听","400 电话接起率"],
		datasets : [
			{
				fillColor : "rgba(151,187,205,0.5)",
				strokeColor : "rgba(151,187,205,0.8)",
				highlightFill : "rgba(151,187,205,0.75)",
				highlightStroke : "rgba(151,187,205,1)",
				data : [randomScalingFactor(),randomScalingFactor(),randomScalingFactor(),randomScalingFactor(),randomScalingFactor(),randomScalingFactor(),randomScalingFactor(),randomScalingFactor(),randomScalingFactor(),randomScalingFactor()]
			}
		]

	}
	var defaults = {
	                
	    // 字体
	    scaleFontFamily : "'微软雅黑'",
	    
	    // 文字大小
	    scaleFontSize : 12,
	    
	    // 文字样式
	    scaleFontStyle : "normal",
	    
	    // 文字颜色
	    scaleFontColor : "#666",	
	    
	    // 是否显示网格
	    scaleShowGridLines : false,
	    
	    // 网格颜色
	    scaleGridLineColor : "rgba(0,0,0,.05)"
	    
	};
	
	function getChecked(){
	var nodes = $('#tt').tree('getChecked');
	var s = '';
	for(var i=0; i<nodes.length; i++){
	if (s != '') s += ',';
	s += nodes[i].text;
	}
	alert(s);
	};
		
	$(function(){
		var pager = $('#dg').datagrid().datagrid('getPager');
			pager.pagination({
			buttons:[{
			iconCls:'icon-search',
			handler:function(){
			alert('search');
			}
			},{
			iconCls:'icon-add',
			handler:function(){
			alert('add');
			}
			},{
			iconCls:'icon-edit',
			handler:function(){
			alert('edit');
			}
			}]
		});
	});
	
	$(".checkboxs>input").click(function(){
		var $href  = $(this).attr("href");
		$(".radion").css("display","none");
		$($href).css("display","block");
	});
	
	
	$('#keyword').keydown(function(e){
		if(e.keyCode==13){
			$('#formLogin').submit();
		}
	});
});
