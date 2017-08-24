<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#assign tag=JspTaglibs["/WEB-INF/taglib/permission.tld"]>
<#include "/layouts/main.ftl"/>
<@html></@html>
<#import "/layouts/popup.ftl" as massage>
<#macro style></#macro>
<#macro script>
<script type="text/javascript" src="${rc.contextPath}/js/popup.js"></script>
<script type="text/javascript">
    $(function(){
        $.plugin.init("#dealerMapping_form","#deaperMapping_table");
    });
    function deleteCallback(json){
        general_message(json.code,json.message);
        $("#formSubmit").click();
    }
</script>
</#macro>
<@massage.generalMessage />
<#macro content>
<div class="search">
    <form action="${rc.contextPath}/dealerMapping/search" method="POST" id="dealerMapping_form">

            <div style="overflow:hidden;*height:40px;">
                <div class="floatL autoFill ieHack" style="width:auto;">
                    <label>源网点编号：</label>
                    <input type="text"  name="sourceDealer"  maxlength="30"  class="search key" style="width:120px;"  />
                </div>
                <div class="floatL autoFill ieHack" style="width:auto;">
                    <label>源网点名称：</label>
                    <input type="text" name="sourceDealerName"   maxlength="30" class="search key" style="width:120px;" />
                </div>
            </div>
        <div style="overflow:hidden;*height:40px;">
                <div class="floatL autoFill ieHack" style="width:auto;">
                    <label>目标网点编号：</label>
                    <input type="text"  name="targetDealer"  maxlength="30"  class="search key" style="width:120px;"  />
                </div>
                <div class="floatL autoFill ieHack" style="width:auto;">
                    <label>目标网点名称：</label>
                    <input type="text" name="targetDealerName"   maxlength="30" class="search key" style="width:120px;" />
                </div>
        </div>
            <div style="overflow:hidden;*height:40px;">
                <div class="sumbit floatL autoFill ieHack" style="width:100px">
                    <input type="button" class="submit search" value="搜 索"  id="formSubmit" />
                </div>
            </div>
    </form>
</div>
<div class="ichtable">
    <table id="deaperMapping_table" width="100%" class="easyui-datagrid" title="<a href='${rc.contextPath}/dealerMapping/create' title=''>添加</a>" data-options="singleSelect:true,collapsible:false,method:'get',remoteSort:false,multiSort:true,pagination:true,showRefresh:false" >
        <thead>
        <tr>
            <th width="10%" data-options="field:'sourceDealer',sortable:false">源网点编号</th>
            <th width="18%" data-options="field:'sourceDealerName',sortable:false">源网点名称</th>
            <th width="10%" data-options="field:'targetDealer',sortable:false">目标网点编号</th>
            <th width="18%" data-options="field:'targetDealerName',sortable:false">目标网点名称</th>
            <th width="30%" data-options="field:'mappingReasonPhrase',sortable:false">映射原因</th>
            <th width="10%" data-options="field:'mappingBegDate',sortable:false,formatter:formatDate">开始日期</th>
            <th width="10%" data-options="field:'mappingEndDate',sortable:false,formatter:formatDate">结束日期</th>
            <th width="6%" data-options="field:'operations',sortable:false">操作</th>
        </tr>
        </thead>
    </table>
    <script>
        function formatDate(val,row) {
            if (val) {
                return val.substring(0, 10);
            }
        }
    </script>
</div>

</#macro>