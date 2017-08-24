$(document).ready(function(){
    initMainReport();
	initOperateBtn();
	//initTable();
});

//初始化查询报表表格数据
function initTable(tableId){
	var $table = $("#"+tableId);
	var url =$table.attr("url");
	
	var sortName = $table.datagrid("options").sortName; //派序列名称
	var sortOrder = $table.datagrid("options").sortOrder; //排序顺序
	var currentPage = $table.datagrid("getPager").data("pagination").options.pageNumber; //当前页
	var pageSize = $table.datagrid("getPager").data("pagination").options.pageSize; //每页显示大小
	
	$.ajax({
		url : url ,
		data : {
			
		},
		type : "POST"
	}).done(function($json){
		$table.datagrid({
			pageNumber : $json["pageNumber"],
			pageSize : $json["pageSize"],
			sortName : $json["sortName"],
			sortOrder : $json["sortOrder"]
		});
		$table.datagrid("loadData", $json["rows"]);
	}).fail(function(){
		
	});
	
}

function initOperateBtn(){
	$("#searchBtn").bind("click",searchReport);
	$("#exportBtn").bind("click",exportReport);
}

//搜索
function searchReport(){
	//判断大区
	var regionSelected = $("#searchRegion").combo("getValues");
	if(regionSelected == null || regionSelected == ""){
		alert("请选择大区");
		return;
	}
	if(regionSelected.length != 2){
		alert("请选择两个大区进行比较");
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
	$("#searchRegionHid").val(regionSelected.join(","));
	$("#searchForm").attr("action","doComparison");
	$("#searchForm").submit();
}

function exportReport(){
	//判断大区
	var regionSelected = $("#searchRegion").combo("getValues");
	if(regionSelected == null || regionSelected == ""){
		alert("请选择大区");
		return;
	}
	if(regionSelected.length != 2){
		alert("请选择两个大区进行比较");
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
	$("#searchRegionHid").val(regionSelected.join(","));
	$("#searchForm").attr("action","doExportComparison");
	$("#searchForm").submit();
}

/**
 * 初始化报表
 */
function initMainReport(){
	if(typeof regionX == "undefined"){
		return;
	}
	
	var categories = [];
	for(var i = 0;i<regionX.length;i++){
		categories.push(regionX[i]+"日");
	}
	
	var data1 = [];
	for(var i = 0;i<region1Y.length;i++){
		//data1.push({y:region1Y[i],'color':'#F93E3E'});
		data1.push({y:region1Y[i]});
	}
	var data2 = [];
	for(var i = 0;i<region2Y.length;i++){
		//data2.push({y:region2Y[i],'color':'#AFCAD8'});
		data2.push({y:region2Y[i]});
	}
	
	var dataNames = regionNames.split(",");

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
        }, { type: 'spline', name: '全国平均值', data: region3Y, marker: { lineWidth: 2, lineColor: Highcharts.getOptions().colors[3], fillColor: 'white' } }]
    });
}
