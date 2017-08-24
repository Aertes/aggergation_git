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
	//判断经销商
	var $dealer1 = $("#autoHiddenId1").val();
	var $dealer2 = $("#autoHiddenId2").val();
	
	//判断经销商是否为空
	if($dealer1==null || $dealer1==""){
		alert("请选择网点");
		return;
	}
	if($dealer2==null || $dealer2==""){
		alert("请选择网点");
		return;
	}
	if($dealer2 == $dealer1){
		alert("请选择两个不同的网点进行比较");
		return;
	}
	var reportType = $("#reportType").val();
	if(reportType != 5){
		//判断合作媒体是否选择
		var mediaCheckLen = $("input[name='mediaIds']:checked").length;
		if(mediaCheckLen == 0){
			alert("请选择合作媒体");
			return;
		}
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
	//设置大区
	$("#searchForm").attr("action","doComparison");
	$("#searchForm").submit();
}

function exportReport(){
	//判断经销商
	var $dealer1 = $("#autoHiddenId1").val();
	var $dealer2 = $("#autoHiddenId2").val();
	
	//判断经销商是否为空
	if($dealer1==null || $dealer1==""){
		alert("请选择网点");
		return;
	}
	if($dealer2==null || $dealer2==""){
		alert("请选择网点");
		return;
	}
	if($dealer2 == $dealer1){
		alert("请选择两个不同的网点进行比较");
		return;
	}
	
	var reportType = $("#reportType").val();
	if(reportType != 5){
		//判断合作媒体是否选择
		var mediaCheckLen = $("input[name='mediaIds']:checked").length;
		if(mediaCheckLen == 0){
			alert("请选择合作媒体");
			return;
		}
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
	
	//设置大区
	//$("#searchDealerHid").val(dealerSelected.join(","));
	$("#searchForm").attr("action","doExportComparison");
	$("#searchForm").submit();
}

/**
 * 初始化报表
 */
function initMainReport(){
	if(typeof dealerX == "undefined"){
		return;
	}
	
	var categories = [];
	for(var i = 0;i<dealerX.length;i++){
		categories.push(dealerX[i]+"日");
	}
	
	var data1 = [];
	for(var i = 0;i<dealer1Y.length;i++){
		//data1.push({y:dealer1Y[i],'color':'#F93E3E'});
		data1.push({y:dealer1Y[i]});
	}
	var data2 = [];
	for(var i = 0;i<dealer2Y.length;i++){
		//data2.push({y:dealer2Y[i],'color':'#AFCAD8'});
		data2.push({y:dealer2Y[i]});
	}
	
	var dataNames = dealerNames.split(",");

    $('#mainReportDiv').highcharts({
        chart: {
            type: 'column'
        },
        title: {
            text: ''
        },
		credits: { enabled: false }, 
        xAxis: {
            categories: categories
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
        series: [{
            name: dataNames[0],
            data: data1
        }, {
            name: dataNames[1],
            data: data2
        }, { type: 'spline', name: name_avg, data: dealer3Y, marker: { lineWidth: 2, lineColor: Highcharts.getOptions().colors[3], fillColor: 'white' } }]
    });
}
