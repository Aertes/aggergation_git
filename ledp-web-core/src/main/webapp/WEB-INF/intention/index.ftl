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
        $.plugin.init("#intention_form","#intention_table");
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
        <form action="${rc.contextPath}/intention/search" method="POST" id="intention_form">
            <div class="clearfix" style="*height:100%;">
                <div class="floatL ieHack">
                    <label>车型编号：</label>
                    <input class="key search" type="text" name="vehicleSeriesCode" value="${vehicleSeriesCode}" style="width:150px; height: 30px;" />
                </div>
                <div class="floatL ieHack">
                    <label>车型名称：</label>
                    <input class="key search" type="text" name="vehicleSeriesName" value="${vehicleSeriesName}" style="width:150px; height: 30px;" />
                </div>
                <div class="sumbit ieHack">
                    <input type="button" class="submit" value="搜 索" id="formSubmit"/>
                </div>
            </div>
        </form>
    </div>
    <div class="ichtable">
        <table id="intention_table" width="100%" class="easyui-datagrid" title="<a href='${rc.contextPath}/intention/create' title=''>添加</a>" data-options="singleSelect:true,collapsible:false,method:'get',remoteSort:false,multiSort:true,pagination:true,showRefresh:false" >
            <thead>
            <tr>
                <th width="35%" data-options="field:'vehicleSeriesCode',sortable:false">车型编号</th>
                <th width="35%" data-options="field:'vehicleSeriesName',sortable:false">车型名称</th>
                <th width="30%" data-options="field:'operations',sortable:false">操作</th>
            </tr>
            </thead>
        </table>
    </div>

</#macro>