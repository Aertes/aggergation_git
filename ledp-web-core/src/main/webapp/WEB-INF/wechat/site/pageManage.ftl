<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#assign tag=JspTaglibs["/WEB-INF/taglib/permission.tld"]>
<#include "/wechat/layouts/main.ftl"/>
<@html></@html>
<#macro style>

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
            <span>营销应用</span>
            <span>&gt;&gt;</span>
            <span><a href="${rc.contextPath}/wechat/site/index">微站管理</a></span>
            <span>&gt;&gt;</span>
            <span>界面管理</span>
        </li>
    </ul>
</div>
<h1 class="title_page">微站管理 >> ${site.name} >> 界面列表</h1>
<form action="${rc.contextPath}/wechat/site/searchSitePage" class="form-search" method="post" id="formId">
    <intput type="hidden" id="siteId" name="siteId" class="search" value="${site.id}"/>
</form>
<div id="tableId">
    <ul class="activePageList">
    </ul>
    <div class="padding30">
        <div class="pagegination floatR" style="display:none;">
            <ul>
            </ul>
        </div>
    </div>
</div>
<div class="textAlign" style="margin-bottom:20px;">
    <input type="button" class="redButton" onclick="window.location.href='${rc.contextPath}/wechat/site/index'"
           value="返回"/>
</div>
<form id="form_camp" action="${rc.contextPath}/wechat/campaign/pageUpdate" method="post">
    <intput type="hidden" name="site" value="${site.id}"/>
    <intput type="hidden" id="sitePageId" name="sitePageId"/>
</form>
</#macro>
<#macro script>
<script src="${rc.contextPath}/wechat/js/jquery.zclip.min.js"></script>
<script>
    var cfg = {
        path: "${rc.contextPath}/wechat/js/ZeroClipboard.swf",
        copy: function () {
            return $(this).parents("li:first").find(".getCopyPageSites:first").val();
        },
        afterCopy: function () {/* 复制成功后的操作 */
            layer.alert("链接复制成功！", {
                title: "信息提示框",
                time: 2000,
                skin: "layui-layer-lan",
                icon: 1,
                shift: 3
            });
        }
    };
    $(document).ready(function () {

    });
    $(function () {
    	$.ajaxSetup({cache:false});
        $('#siteId').val('${site.id}');
        var layerConfirm = new ElasticLayer("confirm", "确认要删除吗？", {})
        var page = new pagination("formId", "tableId", function (value) {
            $("#tableId>ul").children().remove();
            if (value.length > 0) {
                for (var i = 0; i < value.length; i++) {
                    var $li = $("<li>"), $back = $("<div class='backImg'>");
                    var $hidenUrl = $("<input type='hidden' class='getCopyPageSites' value='" + value[i]['webUrl'] + "'>")
                    var $img = $("<img>").attr("src", value[i]["imgSrc"]);
                    var str = "<div class='backShadow backShadow_hide'>";
                        if (value[i]["dealer"]) {
                            str += "<a href='${rc.contextPath}/wechat/site/pageUpdate?site=${site.id}&sitePageId=" + value[i]["id"] + "' ><i class='icon-edit' title='编辑'></i></a>";
                            if(value[i]["canDelete"]=="1") {
                            	str += "<a href='javascript:void(0)' class='delete' attr-id='" + value[i]["id"] + "'><i title='删除' class='icon-remove'></i></a>";
                            }
                            str += "<a href='javascript:void(0)' onclick='daileSitePage(" + value[i]["id"] + ")'><i class=' icon-search' title='预览'></i></a>";
                            str += "<a href='javascript:void(0)' class='copyPageLianjie' title='复制链接'><i class=' icon-file-alt' title='复制链接'></i></a></div>";
                        } else {
                            str += "<a href='javascript:void(0)' style='text-align: center;width: 50%;'  onclick='daileSitePage(" + value[i]["id"] + ")'><i class=' icon-search' title='预览'></i></a>";
                            str += "<a href='javascript:void(0)' style='text-align: center;width: 50%;' class='copyPageLianjie' title='复制链接'><i class=' icon-file-alt' title='复制链接'></i></a></div>";
                        }
                    var $ope = $(str);
                    $back.append($img).append($ope);
                    var $title = $("<p class='title'>").text(value[i]['title']);
                    $li.append($hidenUrl).append($back).append($title);
                    $("#tableId>ul").append($li);
                }
                $(".copyPageLianjie").each(function (i) {
                    $(this).zclip(cfg);
                });
                $(".activePageList").delegate(".copyPageLianjie", "click", function () {
                    if (!$(this).data('init')) {
                        $(this).zclip(cfg);
                        $(this).data('init', true)
                    }
                });
                $("#tableId").undelegate("ul>li","mouseover");
				$("#tableId").delegate("ul>li","mouseover",function(){
					$(this).find(".backShadow").removeClass("backShadow_hide");
					$(this).find(".backShadow").addClass("backShadow_show");
				});
				
				$("#tableId").undelegate("ul>li","mouseout");
				$("#tableId").delegate("ul>li","mouseout",function(){
					$(this).find(".backShadow").removeClass("backShadow_show")
					$(this).find(".backShadow").addClass("backShadow_hide");
				});
            }
        });
        $("#tableId").delegate(".delete", "click", function () {
            var id = $(this).attr('attr-id');
            deleteCallBack(id, page, layerConfirm);
        });
    });
    function updateSitePage(id) {
        $('#sitePageId').val(id);
        $('#form_camp').submit();
    }
    function deleteCallBack(id, page, layerConfirm) {
        layer.confirm("确认要删除吗？", {
            yes: function (index) {
                layer.close(index);
                $.ajax({
                    url: "${rc.contextPath}/wechat/campaign/deleteSitePage/" + id,
                    data: {},
                    type: 'post',
                    cache: false,
                    dataType: 'json',
                    success: function (data) {
                        page.getPageValue();
                        $("#tableId").delegate(".activeDelete", "click", function () {
                            var id = $(this).attr('attr-id');
                            deleteCallBack(id, page, layerConfirm);
                        });
                        var layerAlert = new ElasticLayer("alert", data.value, {
                            title: "信息提示框",
                            dialog: true,
                            time: 2000
                        });
                        layerAlert.init();
                    }
                });

            },
            no: function (index) {
                layer.close(index);
            }
        });
    }
    function daileSitePage(id) {
    	var reqUrl = "${rc.contextPath}/wechat/site/getUrlPage/" + id+"?r="+new Date().getTime();;
        var layerPage = new ElasticLayer("pageWindow", reqUrl, {
            area: ["400px", "80%"],
            offset: ["5%", "40%"]
        }, {
            success: function (layero, index) {

            }
        })
        layerPage.init();
    }
</script>
</#macro>