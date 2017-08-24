$(document).ready(function(){
    initMainReport();
	initOperateBtn();
});

function initOperateBtn(){
	$("#searchBtn").bind("click",searchReport);
	$("#exportBtn").bind("click",exportReport);
}

//搜索
function searchReport(){
	//判断合作媒体是否选择
	var mediaCheckLen = $("input[name='mediaIds']:checked").length;
	if(mediaCheckLen == 0){
		alert("请选择合作媒体");
		return;
	}
	
	var $dealer = $("#autoHiddenId1").val();
	
	//判断经销商是否为空
	if($dealer==null || $dealer==""){
		alert("请选择网点");
		return;
	}
	
	//判断报表日期类型
	var searchDateType = $("input[name='searchDateType']:checked").val();
	if(searchDateType == 4){
		//判断开始和结束日期
		var searchDateBegin = $("#searchDateBegin").datebox("getValue");
		if(!searchDateBegin || searchDateBegin.length == 0){
			alert("请选择开始日期");
			return;
		}
		var searchDateEnd = $("#searchDateEnd").datebox("getValue");
		if(!searchDateEnd || searchDateEnd.length == 0){
			alert("请选择结束日期");
			return;
		}
	}
	
	$("#searchForm").attr("action","doMedia");
	$("#searchForm").submit();
}

function exportReport(){
	//判断合作媒体是否选择
	var mediaCheckLen = $("input[name='mediaIds']:checked").length;
	if(mediaCheckLen == 0){
		alert("请选择合作媒体");
		return;
	}
	
	//判断报表日期类型
	var searchDateType = $("input[name='searchDateType']:checked").val();
	if(searchDateType == 4){
		//判断开始和结束日期
		var searchDateBegin = $("#searchDateBegin").datebox("getValue");
		if(!searchDateBegin || searchDateBegin.length == 0){
			alert("请选择开始日期");
			return;
		}
		var searchDateEnd = $("#searchDateEnd").datebox("getValue"); 
		if(!searchDateEnd || searchDateEnd.length == 0){
			alert("请选择结束日期");
			return;
		}
	}
	
	if($("#searchDealer").length > 0){
		var dealerSelected = $("#searchDealer").combo("getValues");
		if(dealerSelected == null || dealerSelected == ""){
			alert("请选择经销商");
			return;
		}
	}
	
	
	$("#searchForm").attr("action","doExportMedia");
	$("#searchForm").submit();
}

/**
 * 初始化报表
 */
function initMainReport(){
	if(typeof mediaX == "undefined"){
		return;
	}
	
    $('#mainReportDiv').highcharts({
        chart: {
            type: 'column'
        },
        title: {
            text: ''
        },
        xAxis: {
            categories: mediaX
        },
        yAxis: {
            min: 0,
            title: {
                text: ''
            }
        },
		tooltip: { shared: true, valueSuffix: '条' }, 
        plotOptions: {
            column: {
                pointPadding: 0.2,
                borderWidth: 0
            }
        },
		credits: { enabled: false }, 
        series: seriesArray
    });
}