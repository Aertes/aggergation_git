<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#assign tag=JspTaglibs["/WEB-INF/taglib/permission.tld"]>
<script type="text/javascript">
    $(function () {
        //添加优惠按钮默认隐藏
        $("#pop").hide();
        //控制消息类型展示
        toggleShowSeries(${news.type.code?default(1)});
        //未选中
        $("input[name='medias']").not("input:checked").each(function () {
            var uncheck = "#types" + $(this).val();
            var arr = $(uncheck).val().split(",");
            for (var i in arr) {
                spn = "#spn" + arr[i];
                $(spn).css("display", "none");
            }
        });
        //已选中的
        $(":checkbox[name='medias'][checked]").each(function () {
            var uncheck = "#types" + $(this).val();
            var arr = $(uncheck).val().split(",");
            for (var i in arr) {
                spn = "#spn" + arr[i];
                $(spn).css("display", "block");
            }
        });
        //控制优惠话术显示
        $(":radio[name='type.id']:checked").each(function () {
            var code = $(this).attr("code");
            if (code == "2") {
                $("#series_li").show();
                $("#series_pop").show();
                $("#pop").show();
            } else {
                $("#series_li").hide();
                $("#pop").hide();
            }
        });

        //隐藏优惠话术
        $("#insertContent").dialog({
            autoOpen: false,
            show: {
                effect: "blind",
                duration: 1000
            },
            hide: {
                effect: "explode",
                duration: 1000
            }
        });
        $("#series_pop").click(function () {
            var serieses = "";
            $("input[name='serieses']:checked").each(function (i, s) {
                if (i > 0) {
                    serieses += ",";
                }
                serieses += s.value;
            });
            $.ajax({
                url: '${rc.contextPath}/promotion/index',
                data: {serieses: serieses},
                success: function (content) {
                    $(".panel-title").html("添加促销信息");
                    $("#insertContent").html(content);
                    $('#insertContent').window('open');
                    $(".body_shadow").css("display", "block");
                    $(".body_shadow").css("width", document.body.scrollWidth);
                    $(".body_shadow").css("height", window.screen.height);
                    $(".bodyContiner").css("overflow", "visible");
                }
            });
        });
        $("#insertContent").dialog("close");
        $(".body_shadow").css("display", "none");
    });

    function toggleShowSeries(code) {
        var carer = false;
        $("input:checkbox[name='medias']:checked").each(function () {
            if ($(this).val() == 5) {
                carer = true;
            }
        });
        if (code == "2" || carer) {
            $("#series_li").show();
            $("#series_pop").show();
            $("#pop").show();
        } else {
            $("#series_li").hide();
            $("#pop").hide();
        }
    }
    function typeshow() {
        //清空信息类型
        $("input:radio[name='type.id']").attr('checked',false);
        $("input:checkbox[name='serieses']").attr('checked',false);
        //$("#series_li").css("display","none");
        //未选中
        $("input[name='medias']").not("input:checked").each(function () {
            var uncheck = "#types" + $(this).val();
            var arr = $(uncheck).val().split(",");
            for (var i in arr) {
                spn = "#spn" + arr[i];
                $(spn).css("display", "none");
            }
        });
        //已选中的
        $("input:checkbox[name='medias']:checked").each(function () {
            var uncheck = "#types" + $(this).val();
            var arr = $(uncheck).val().split(",");
            for (var i in arr) {
                spn = "#spn" + arr[i];
                $(spn).css("display", "block");
            }
        });
    }
    function previewNews() {
        if (validate()) {
            $("#content").val(KE.html('content1'));
            $.ajax({
                url: '${rc.contextPath}/news/preview',
                data: $("form").serialize(),
                method: 'post',
                success: function (html) {
                    $(".panel-title").html("信息预览");
                    $("#insertContent").html(html);
                    $("#insertContent").dialog("open");
                    $(".body_shadow").css("display", "block");
                    $(".body_shadow").css("width", document.body.scrollWidth);
                    $(".body_shadow").css("height", window.screen.height);
                    $(".bodyContiner").css("overflow", "visible");
                }
            });
        }
    }

</script>
<div class="clearFix tabadd messadd">
    <ul>
        <li class="clearFix">
            <label>合作媒体<em>*</em></label>
            <div class="floatL dx">
            <#list medias as media>
                <input class="mediatest" type="checkbox" onClick="typeshow()" name="medias"
                       <#if checkedMedias?exists && checkedMedias?seq_contains(media.id)>checked="checked"</#if>
                       value="${media.id}"/><span>${media.ext_str1}.${media.name}</span>
                <input type="hidden" name="medias${media.id}" id="medias${media.id}">
                <input type="hidden" name="types${media.id}" id="types${media.id}" value="${media.newsType}">
            </#list>
            </div>
        </li>
        <li class="clearFix" style="*height:80px;">
            <label>信息类型<em>*</em></label>
            <div class="floatL dx checkboxs glcx">
                <ul>
                <#list typeList as type>
                    <li>
						<span id="spn${type.id}">
						<input class="mediatest" type="radio" code="${type.code}" name="type.id" value="${type.id}"
                               <#if news.type.id==type.id>checked</#if> onClick="toggleShowSeries('${type.code}')"/>
                        ${type.name}&nbsp;&nbsp;
						</span>
                    </li>

                </#list>
                </ul>
            </div>
        </li>
        <li class="clearFix" id="series_li" <#if news.type.code!="2">style="display:none;"</#if>>
            <label>关联车系<em>*</em></label>
            <div class="floatL dx" style="width:70%;">
                <ul class="glcx">
                <#list seriesList as series>
                    <li>
                        <input class="mediatest" type="checkbox" name="serieses"
                               <#if checkedSerieses?exists && checkedSerieses?seq_contains(series.id)>checked="checked"</#if>
                               value="${series.id}"/><span>${series.name}</span>
                    </li>
                </#list>
                </ul>
            </div>
        </li>
        <li class="clearFix">
            <label>公司信息<em></em></label>
            <div class="floatL glcx">
                <ul class="clearFix">
                    <li>
                        <input type="checkbox" name="includePhone" value="true"
                               <#if news.includePhone=="true">checked</#if>>
                        <span>添加公司电话</span>
                    </li>
                    <li>
                        <input type="checkbox" name="includeAddress" value="true"
                               <#if news.includeAddress=="true">checked</#if>>
                        <span>添加公司地址</span>
                    </li>
                </ul>
            </div>
        </li>
        <li class="clearFix">
            <label>信息标题<em>*</em></label>
            <div class="floatL glcx">
            <@spring.bind "news.title" />
                <input class="textbox" type="text" name="title" value="${news.title}"
                       style="height: 30px; width: 300px;" datatype="*" nullmsg="请填写信息标题"></input>
                （注意：请至少输入10个字符）<span class="Validform_checktip error "><@spring.showError/></span>
            </div>
        </li>
        <li class="clearFix">
            <label>信息摘要<em>*</em></label>
            <div class="floatL glcx">
                <textarea name="summary" cols="45" rows="2" style="margin:0;">${news.summary}</textarea>
            </div>
        </li>
        <li class="clearFix">
            <label>信息内容<em>*</em></label>
            <div class="floatL" style="text-align:center;width:70%;margin-left:10px;">
                <div class="clearFix">
                    <input type="hidden" id="content" name="content" value=""/>
                    <!--<ueditor id="editor1" type="text/plain"></ueditor>-->
                    <!--kindeditor引入-->
                    <textarea id="content1" name="content1" cols="100" rows="8"
                              style="width:900px;height:500px;visibility:hidden;">${news.content}</textarea>

                    <div id="pop" style="text-align:right; margin:10px 0;"><a href="#" id="series_pop"
                                                                              style="color:#cc0000; text-decoration:underline;">添加优惠信息</a><span
                            style="font-size:11px;"> (当信息类型为“优惠促销”时使用)</span></div>
                </div>
            <@spring.bind "news.content" />
                <span class="Validform_checktip error "><@spring.showError/></span>
            </div>
        </li>
    </ul>
    <div id="insertContent" title="添加优惠信息"
         style="width:900px;height:500px;overflow:auto; border:1px solid #000000;"></div>
