$(document).ready(function(){
	initWeekMediaGaugeReport();	//本周信息发布量
	initWeekLeadsGaugeReport();	//本周线索数量
	initWeekPhoneSuccessLineReport();	//400接起率
	initWeekPhoneLineReport();	//400来电 - 本周来电量趋势图
});

function initWeekPhoneSuccessLineReport(){
	var categories = ["周一","周二","周三","周四","周五","周六","周日"];
	
	var data = [];
	for(var i = 1;i<=7;i++){
		var d = {y:reportPhoneSuccessWeekLineY[i-1],fillColor:'#CC0000'};
		data.push(d);
	}
	
	$('#weekPhoneSuccessReportLineDiv').highcharts({ 
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
			title: { text: '接起率' },
			minPadding:0,
			startOnTick:false
		}, 
		tooltip: { shared: true, valueSuffix: '%',formatter:function(){return categories[this.x] + '<br/>接起率：' + this.y + '%';} }, 
		credits: { enabled: false }, 
		plotOptions: { 
			areaspline: { fillOpacity: 0.5,color:'#EEC5C5',lineColor:'#CE0808',pointStart:0 } 
		}, 
		series: [{name:"来电量",data:data}],
		exporting:false
	});	
}

function initWeekMediaGaugeReport(){
    $('#weekMediaReportGaugeDiv').highcharts({
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
            max: mediaCount+100,
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
                text: '条'
            },
            plotBands: [{
                from: 0,
                to: mediaCount+100,
                color: '#55BF3B'
            }]
        },
		exporting:false,
		credits: { enabled: false }, 
        series: [{
            name: '信息量',
            data: [mediaCount],
            tooltip: {
                valueSuffix: ' 条'
            }
        }]
    });
}

function initWeekLeadsGaugeReport(){
	 $('#weekLeadsReportGaugeDiv').highcharts({
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
            max: leadsCount+1000,
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
                text: '条'
            },
            plotBands: [{
                from: 0,
                to: leadsCount+1000,
                color: '#55BF3B'
            }]
        },
		exporting:false,
		credits: { enabled: false }, 
        series: [{
            name: '线索量',
            data: [leadsCount],
            tooltip: {
                valueSuffix: ' 条'
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
		tooltip: { shared: true, valueSuffix: '次',formatter:function(){return categories[this.x] + '<br/>来电量：' + this.y + '次';} }, 
		credits: { enabled: false }, 
		plotOptions: { 
			areaspline: { fillOpacity: 0.5,color:'#EEC5C5',lineColor:'#CE0808',pointStart:0 } 
		}, 
		series: [{name:"来电量",data:data}],
		exporting:false
	});	
}

