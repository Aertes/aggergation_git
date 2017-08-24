<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#assign tag=JspTaglibs["/WEB-INF/taglib/permission.tld"]>
<#include "/wechat/layouts/main.ftl"/>
<@html></@html>
<#macro style>
<link rel="stylesheet" href="${rc.contextPath}/wechat/css/report.css"/>
<link rel="stylesheet" type="text/css" href="${rc.contextPath}/css/jquery.bigautocomplete.css">
<style>
    .acReport .totalCount {
        border: 1px solid #d9d9d9;
        margin: 10px auto;
        width: 70%;
    }

    .analysis label {
        font-size: 12px;
    }

    .reportData thead th {
        background: #fbfbfb;
    }
</style>
</#macro>
<#macro content>
<div class="breadcrumbs" id="breadcrumbs">
    <script type="text/javascript">
        try {
            ace.settings.check('breadcrumbs', 'fixed')
        } catch (e) {
        }
    </script>

    <ul class="breadcrumb">
        <li>
            <i class="icon-home home-icon"></i>
            <span>数据中心</span>
            <span>&gt;&gt;</span>
            <span>粉丝报表</span>
        </li>
    </ul>
</div>
<div class="baseSet acReport">
    <form id="fans-form" class="overflow-hidden">
        <div class="analysis times">
            <div class="floatL" style="line-height:30px;">
                <input type="radio" checked="checked" class="search" value="5" name="effect"/>
                <label>上周</label>
            </div>
            <div class="floatL marginLeft20" style="line-height:30px;">
                <input type="radio" checked="checked" class="search" value="1" name="effect"/>
                <label>本周</label>
            </div>
            <div class="floatL marginLeft20" style="line-height:30px;">
                <input type="radio" class="search" value="2" name="effect"/>
                <label>本月</label>
            </div>
            <div class="floatL marginLeft20" style="line-height:30px;">
                <input type="radio" class="search" value="3" name="effect"/>
                <label>上月</label>
            </div>
            <div class="floatL marginLeft20" style="line-height:30px;">
                <input class="floatL" style="margin:9px 5px 0 0;" type="radio" class="search" value="4" name="effect"/>
                <label class="floatL" style="margin:0 5px;">自定义</label>

                <div id="startTime1" style="width: 150px;" class="floatL search"
                     params="{name:'cstartTime',value:'',id:'cstartTime'}"></div>
                <label class="floatL margin10">-</label>

                <div id="endTime1" style="width: 150px;" class="floatL search"
                     params="{name:'cendTime',value:'',id:'cendTime'}"></div>
            </div>
        </div>
        <#if loginOrg?exists && loginOrg.id==1>
            <div class="analysis">
                <label>大区：</label>
                <select style="min-width: 100px;" name="corg" id="corg">
                    <option value="">全部</option>
                    <#list orgs as org>
                        <option value="${org.id}">${org.name}</option>
                    </#list>
                </select>
            </div>
            <div class="analysis" id="cdealerid" style="display:none;">
                <label>网点：</label>
                <input type="text" id="auto_tt1" relevanceHiddenId="cdealer" value="" autocomplete="off"
                       style="min-width: 100px;"/>
                <input type="hidden" id="cdealer" name="cdealer" value="" class="search"/>
            </div>
        <#elseif loginOrg?exists && loginOrg.id!=1>
            <div class="analysis" style="line-height:30px;">
                <label>大区：<span style="padding:0 10px;height:30px;display:inline-block; line-height:30px;color: #939192;background: #f5f5f5!important;border:1px solid #d5d5d5;cursor: default;">${loginOrg.name}</span></label>
                <input type="hidden" name="corg" id="corg" value="${loginOrg.id}">
            </div>
            <div class="analysis">
                <label>网点：</label>
                <input type="text" id="auto_tt1" relevanceHiddenId="cdealer" value="" autocomplete="off"
                       style="min-width: 100px;"/>
                <input type="hidden" id="cdealer" name="cdealer" value="" class="search"/>
            </div>
        <#else>
            <div class="analysis" style="line-height:30px;">
                <label>网点：<span style="padding:0 10px;height:30px;display:inline-block; line-height:30px;color: #939192;background: #f5f5f5!important;border:1px solid #d5d5d5;cursor: default;">${loginDealer.name}</span></label>
                <input type="hidden" id="cdealer" name="cdealer" value="${loginDealer.id}" class="search"/>
            </div>
        </#if>
        <div class="analysis" id="analysisPublicNo" style="display:none">
            <label>公众号：</label>
            <select style="min-width: 100px;" name="publicNoId" id="publicNoId">
                <option value="">全部</option>
            </select>
        </div>
        <div class="analysis">
            <input type="button" value="搜 索" id="fans_button" class="redButton submit"/>
        </div>
    </form>
    <div class="totalCount" id="ftotalCount" style="display:none;">
        <ul>
            <li style="width: 33%;margin:10px 0;">
                <p>
                    <span class="numbers" id="nowSubscribes"></span>
                </p>

                <p class="msg">新增关注人数</p>
            </li>
            <li style="width: 33%;margin:10px 0;">
                <p>
                    <span class="numbers" id="subscribes"></span>
                </p>

                <p class="msg">当前关注人数</p>
            </li>
            <li style="width: 33%;border-right: 0px;margin:10px 0;">
                <p>
                    <span class="numbers" id="desubscribes"></span>
                </p>

                <p class="msg">取消关注人数</p>
            </li>
        </ul>
    </div>
    <div class="overflow-hidden">
        <div id="canvasEffect"></div>
    </div>
    <div class="overflow-hidden activeTable" id="fans-div" style="display:none;">
        <div class="title">
            <a href="javascript:void(0)" id="fans-export" class="redButton" style="color: #fff;">导 出</a>
        </div>
        <div id="_1">
            <table class="reportData" id="fans" width="100%">
                <thead>

                </thead>
                <tbody id="tbody0">

                </tbody>
            </table>
        </div>
    </div>
</div>
</#macro>
<#macro script>
<script type="text/javascript" src="${rc.contextPath}/wechat/js/report/highcharts.js"></script>
<script type="text/javascript" src="${rc.contextPath}/wechat/js/report/highcharts-more.js"></script>
<script type="text/javascript" src="${rc.contextPath}/wechat/js/report/exporting.js"></script>
<script type="text/javascript" src="${rc.contextPath}/wechat/js/report/gray.js"></script>
<script type="text/javascript" src="${rc.contextPath}/wechat/js/report/Chart.js"></script>
<script type="text/javascript" src="${rc.contextPath}/wechat/js/commom.js"></script>
<script src="${rc.contextPath}/wechat/js/bootstrap-switch.js"></script>
<script src="${rc.contextPath}/wechat/js/uedit/ueditor.config.js"></script>
<script src="${rc.contextPath}/wechat/js/uedit/ueditor.all.js"></script>
<script src="${rc.contextPath}/wechat/js/uedit/lang/zh-cn/zh-cn.js"></script>
<script src="${rc.contextPath}/wechat/js/layer/layer.js"></script>
<script src="${rc.contextPath}/wechat/js/ElasticLayer.js"></script>
<script type="text/javascript" src="${rc.contextPath}/js/report/jquery.bigautocomplete.js"></script>
<script type="text/javascript" charset="utf-8">
    $(function () {
        dateTimeInterface.init("dateTime", "startTime1");
        dateTimeInterface.init("dateTime", "endTime1");
        var chart = new Chart();
        <#if loginOrg?exists && loginOrg.id==1>
            $("#corg").change(function () {
                $("#analysisPublicNo").css("display", "none");
                $("#publicNoId").empty();
                if ($(this).val()) {
                    $('#cdealer').val('');
                    $('#auto_tt1').val('');
                    $("#auto_tt1").bigAutocomplete({
                        width: 270,
                        url: "${rc.contextPath}/report/area/autocomplete?orgId=" + $('#corg').val(),
                        callback: function (data) {
                            $("#cdealer").val(data.id);
                            $("#analysisPublicNo").css("display", "");
                            $.post("${rc.contextPath}/wechat/reportFans/getpublicno", {dealerId: data.id}, function (data) {
                                $("#publicNoId").empty();
                                $("#publicNoId").append("<option value=''>全部</option>");
                                for (var i = 0; i < data.length; i++) {
                                    $("#publicNoId").append("<option value='" + data[i]["id"] + "'>" + data[i]["nick_name"] + "</option>");
                                }
                            }, "JSON");
                        }
                    });
                    $('#cdealerid').css("display", "block");
                } else {
                    $('#cdealerid').css("display", "none");
                    $('#cdealer').val('');
                    $('#auto_tt1').val('');
                }
            });
        <#elseif loginOrg?exists && loginOrg.id!=1>
            $("#auto_tt1").bigAutocomplete({
                width: 270,
                url: "${rc.contextPath}/report/area/autocomplete?orgId=" + $('#corg').val(),
                callback: function (data) {
                    $("#cdealer").val(data.id);
                    $.post("${rc.contextPath}/wechat/reportFans/getpublicno", {dealerId: data.id}, function (data) {
                        $("#publicNoId").empty();
                        $("#analysisPublicNo").css("display", "");
                        $("#publicNoId").append("<option value=''>全部</option>");
                        for (var i = 0; i < data.length; i++) {
                            $("#publicNoId").append("<option value='" + data[i]["id"] + "'>" + data[i]["nick_name"] + "</option>");
                        }
                    }, "JSON");
                }
            });
        </#if>
        <#if loginDealer?exists>
            $.post("${rc.contextPath}/wechat/reportFans/getpublicno", {dealerId: $("#cdealer").val()}, function (data) {
                $("#analysisPublicNo").css("display", "");
                $("#publicNoId").empty();
                $("#publicNoId").append("<option value=''>全部</option>");
                for (var i = 0; i < data.length; i++) {
                    $("#publicNoId").append("<option value='" + data[i]["id"] + "'>" + data[i]["nick_name"] + "</option>");
                }
            }, "JSON");
        </#if>
        $('#fans_button').click(function () {
            var dealerId = $("#cdealer").val();
            var orgId = $("#corg").val();
            var searchDateType = $("input[name='effect']:checked").val();
            var searchDateBegin = $("#cstartTime").val();
            var searchDateEnd = $("#cendTime").val();
            var publicNoId = $("#publicNoId").val();
            if (searchDateType == '4') {
                if (searchDateBegin == "") {
                    var layerAlert = new ElasticLayer("alert", "请选择开始时间", {title: "信息提示框", dialog: true, time: 2000});
                    layerAlert.init();
                    return false;
                }
                if (searchDateEnd == "") {
                    var layerAlert = new ElasticLayer("alert", "请选择结束时间", {title: "信息提示框", dialog: true, time: 2000});
                    layerAlert.init();
                    return false;
                }
                if (!checkEndTime(searchDateBegin, searchDateEnd)) {
                    var layerAlert = new ElasticLayer("alert", "请选择正确的时间区间", {
                        title: "信息提示框",
                        dialog: true,
                        time: 2000
                    });
                    layerAlert.init();
                    return false;
                }
            }
//            if (publicNoId == -1 || publicNoId == null) {
//                var layerAlert = new ElasticLayer("alert", "请选择选择公众号", {title: "信息提示框", dialog: true, time: 2000});
//                layerAlert.init();
//                return false;
//            }
            $.ajax({
                url: "${rc.contextPath}/wechat/reportFans/fans",
                data: {
                    dealerId: dealerId,
                    orgId: orgId,
                    searchDateType: searchDateType,
                    searchDateBegin: searchDateBegin,
                    searchDateEnd: searchDateEnd,
                    publicNoId: publicNoId
                },
                type: 'post',
                cache: false,
                dataType: 'json',
                success: function (ms) {
                    $("#fans-div").css("display", "block");
                    $("#fans>thead").children().remove();
                    $("#fans>tbody").children().remove();
                    $("#ftotalCount").css("display", "block");
                    var $tr = $("<tr><th width='15%'>日期</th><th width='15%'>大区</th><th width='20%'>网点名称</th><th width='20%'>公众号</th><th width='10%'>新增关注人数</th><th width='10%'>取消关注人数</th><th width='10%'>当前关注人数</th></tr>");
                    $("#fans>thead").append($tr);
                    var totalNew = 0;
                    var totalCancel = 0;
                    for (var i = 0; i < ms.list.length; i++) {
                        var orgName = ms.list[i]["publicNo"]["dealer"]["organization"]["name"];
                        var dealerName = ms.list[i]["publicNo"]["dealer"]["name"];
                        var publicNoName = ms.list[i]["publicNo"]["nick_name"];
                        var newUser = ms.list[i]["newUser"];
                        totalNew += newUser;
                        var cancelUser = ms.list[i]["cancelUser"];
                        totalCancel += cancelUser;
                        var $tr1 = $("<tr></tr>");
                        var $day = $("<td>" + ms.list[i]["refDate"] + "</td>");
                        var $org = $("<td>" + orgName + "</td>");
                        var $dealer = $("<td>" + dealerName + "</td>");
                        var $publicNo = $("<td>" + publicNoName + "</td>");
                        var $new = $("<td>" + newUser + "</td>");
                        var $cancel = $("<td>" + cancelUser + "</td>");
                        var $cumulateUser = $("<td>" + ms.list[i]["cumulateUser"] + "</td>");
                        $tr1.append($day).append($org).append($dealer).append($publicNo).append($new).append($cancel).append($cumulateUser);
                        $("#fans>tbody").append($tr1);
                    }
                    $("#subscribes").html(ms["subscribes"]);
                    $("#desubscribes").html(totalCancel);
                    $("#nowSubscribes").html(totalNew);
                    var value1 = {
                        title: "",
                        subtitle: "",
                        labelsText: "",
                        credits: {
                            creditsText: "",
                            creditsHref: ""
                        },
                        categories: ms.date,
                        yText: "人数/个",
                        subffixValue: "个",
                        data: [
                            {
                                name: '新增关注人数',
                                color: "#e57d7e",
                                data: ms.listSubscribe
                            }, {
                                name: "取消关注人数",
                                color: "#e0e0e0",
                                data: ms.listDesubscribe
                            }
                        ],
                        inverted: false,
                        stacking: "percent",
                        exporting: false
                    }
                    chart.drawColumn("canvasEffect", "areaspline", value1);
                }
            });
        });


        $("#fans-export").click(function () {
            $("#fans-form").attr("action", "${rc.contextPath}/wechat/reportFans/getFansExport")
            $("#fans-form").submit();
        });

    });
    function checkEndTime(startTime, endTime) {
        var start = new Date(startTime.replace("-", "/").replace("-", "/"));
        var end = new Date(endTime.replace("-", "/").replace("-", "/"));
        if (end < start) {
            return false;
        }
        return true;
    }
</script>
</#macro>