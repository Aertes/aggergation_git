$(document).ready(function(){
	initMonthLineReport();	//线索量 - 近一月趋势总览
	initWeekNewsLineReport();	//信息 - 本周发布量
	initWeekNewsPieReport();	//信息 - 本周完成率
	initWeekPhoneLineReport();	//400来电 - 本周来电量趋势图
	initWeekPhonePieReport();	//400来电 - 本周接起率趋势
});

/**
 * 线索量 - 近一月趋势总览
 */
function initMonthLineReport(){
	var data = [];
	for(var i = 0;i<reportLeadsMonthLineY.length;i++){
		data.push({y:reportLeadsMonthLineY[i],fillColor:'#CC0000'});
	}
	
	$('#monthReportLineDiv').highcharts({ 
		chart: { type: 'areaspline' }, 
		title: { text: '' }, 
		legend: { enabled : false}, 
		xAxis: { 
			labels: {
				formatter: function() {
					return reportLeadsMonthLineX[this.value];
				}
			},
			tickInterval:1
		}, 
		yAxis: { 
			title: {text: '线索量'},
			minPadding:0,
			startOnTick:false
		}, 
		tooltip: { shared: true, valueSuffix: '条',formatter:function(){return (this.x + 1) + '日<br/>线索量：' + this.y + '条';} }, 
		credits: { enabled: false }, 
		plotOptions: { 
			areaspline: { fillOpacity: 0.5,color:'#EEC5C5',lineColor:'#CE0808'}
		}, 
		series: [{name:"线索量",data:data}]
	});	
}

/**
 * 信息 - 本周发布量
 */
function initWeekNewsLineReport(){
	var categories = ["周一","周二","周三","周四","周五","周六","周日"];
	
	var data = [];
	for(var i = 1;i<=7;i++){
		var d = {y:reportNewsWeekLineY[i-1],fillColor:'#CC0000'};
		data.push(d);
	}
	
	$('#weekNewsReportLineDiv').highcharts({ 
		chart: { type: 'areaspline' }, 
		title: { text: '' }, 
		legend: { enabled : false}, 
		xAxis: { 
			labels: {
				formatter: function() {
					return categories[this.value];
				}
			},
			tickInterval:1
		}, 
		yAxis: { 
			title: { text: '信息量' },
			minPadding:0,
			startOnTick:false
		}, 
		tooltip: { shared: true, valueSuffix: '条',formatter:function(){return categories[this.x] + '<br/>信息量：' + this.y + '条';} }, 
		credits: { enabled: false }, 
		plotOptions: { 
			areaspline: { fillOpacity: 0.5,color:'#EEC5C5',lineColor:'#CE0808',pointStart:0 } 
		}, 
		series: [{name:"信息量",data:data}]
	});	
}

/**
 * 信息 - 本周完成率
 */
function initWeekNewsPieReport(){
	$('#weekNewsReportPieDiv').highcharts({ 
		/*chart: { type: 'pie',plotBackgroundColor: null, plotBorderWidth: null, plotShadow: false}, 
		title: { text: '' }, 
		credits: { enabled: false }, 
		tooltip: { pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>' }, 
		plotOptions: { 
			pie: { 
				allowPointSelect: true, 
				cursor: 'pointer', 
				depth: 35, 
				size:'80%',
				colors:['#FF5A5E','#616774'],
				dataLabels: { enabled: true, format: '<b>{point.name}</b>: {point.percentage:.1f} %' } 
			} 
		}, 
		series: [
			{ 
				type: 'pie', 
				name: '完成率', 
				data: [ ['未完成',mediaUnfinishRate],['完成',mediaFinishRate]]
			}
		] */
		chart: {
            type: 'gauge',
            plotBackgroundColor: null,
            plotBackgroundImage: null,
            plotBorderWidth: 0,
            plotShadow: false
        },
        title: {
            text: ''
        },
        pane: {
            startAngle: -150,
            endAngle: 150,
            background: [{
                backgroundColor: {
                    linearGradient: {
                        x1: 0,
                        y1: 0,
                        x2: 0,
                        y2: 1
                    },
                    stops: [[0, '#FFF'], [1, '#333']]
                },
                borderWidth: 0,
                outerRadius: '109%'
            }, {
                backgroundColor: {
                    linearGradient: {
                        x1: 0,
                        y1: 0,
                        x2: 0,
                        y2: 1
                    },
                    stops: [[0, '#333'], [1, '#FFF']]
                },
                borderWidth: 1,
                outerRadius: '107%'
            }, {}, {
                backgroundColor: '#DDD',
                borderWidth: 0,
                outerRadius: '105%',
                innerRadius: '103%'
            }]
        },
        yAxis: {
            min: 0,
            max: total_media,
            minorTickInterval: 'auto',
            minorTickWidth: 1,
            minorTickLength: 10,
            minorTickPosition: 'inside',
            minorTickColor: '#666',
            tickPixelInterval: 30,
            tickWidth: 2,
            tickPosition: 'inside',
            tickLength: 10,
            tickColor: '#666',
            labels: {
                step: 2,
                rotation: 'auto'
            },
            title: {
                text: '率'
            },
            plotBands: [{
                from: 0,
                to: total_media,
                color: '#55BF3B'
            }]
        },
		exporting:false,
		credits: { enabled: false }, 
        series: [{
            name: '信息完成率',
            data: [mediaFinishRate],
            tooltip: {
                valueSuffix: ' %'
            }
        }]
	});
}


/**
 * 400来电 - 本周来电量趋势图
 */
function initWeekPhoneLineReport(){
	var categories = ["周一","周二","周三","周四","周五","周六","周日"];
	
	var data = [];
	for(var i = 1;i<=7;i++){
		var d = {y:reportPhoneWeekLineY[i-1],fillColor:'#CC0000'};
		data.push(d);
	}
	
	$('#weekPhoneReportLineDiv').highcharts({ 
		chart: { type: 'areaspline' }, 
		title: { text: '' }, 
		legend: { enabled : false}, 
		xAxis: { 
			labels: {
				formatter: function() {
					return categories[this.value];
				}
			},
			tickInterval:1
		}, 
		yAxis: { 
			title: { text: '来电量' },
			minPadding:0,
			startOnTick:false
		}, 
		tooltip: { shared: true, valueSuffix: '次',formatter:function(){return categories[this.x] + '<br/>来电量：' + this.y + '条';} }, 
		credits: { enabled: false }, 
		plotOptions: { 
			areaspline: { fillOpacity: 0.5,color:'#EEC5C5',lineColor:'#CE0808',pointStart:0 } 
		}, 
		series: [{name:"来电量",data:data}]
	});	
}

/**
 * 400来电 - 本周接起率
 */
function initWeekPhonePieReport(){
	$('#weekPhoneReportPieDiv').highcharts({ 
		chart: {
            type: 'gauge',
            plotBackgroundColor: null,
            plotBackgroundImage: null,
            plotBorderWidth: 0,
            plotShadow: false
        },
        title: {
            text: ''
        },
        pane: {
            startAngle: -150,
            endAngle: 150,
            background: [{
                backgroundColor: {
                    linearGradient: {
                        x1: 0,
                        y1: 0,
                        x2: 0,
                        y2: 1
                    },
                    stops: [[0, '#FFF'], [1, '#333']]
                },
                borderWidth: 0,
                outerRadius: '109%'
            }, {
                backgroundColor: {
                    linearGradient: {
                        x1: 0,
                        y1: 0,
                        x2: 0,
                        y2: 1
                    },
                    stops: [[0, '#333'], [1, '#FFF']]
                },
                borderWidth: 1,
                outerRadius: '107%'
            }, {}, {
                backgroundColor: '#DDD',
                borderWidth: 0,
                outerRadius: '105%',
                innerRadius: '103%'
            }]
        },
        yAxis: {
            min: 0,
            max: 100,
            minorTickInterval: 'auto',
            minorTickWidth: 1,
            minorTickLength: 10,
            minorTickPosition: 'inside',
            minorTickColor: '#666',
            tickPixelInterval: 30,
            tickWidth: 2,
            tickPosition: 'inside',
            tickLength: 10,
            tickColor: '#666',
            labels: {
                step: 2,
                rotation: 'auto'
            },
            title: {
                text: '率'
            },
            plotBands: [{
                from: 0,
                to: 100,
                color: '#55BF3B'
            }]
        },
		exporting:false,
		credits: { enabled: false }, 
        series: [{
            name: '本周接起率',
            data: [phoneSuccessRate],
            tooltip: {
                valueSuffix: ' %'
            }
        }]
	});
}
