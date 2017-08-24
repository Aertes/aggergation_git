<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#assign tag=JspTaglibs["/WEB-INF/taglib/permission.tld"]>
<ul class="formlist">
    <li class="clearfix">
        <label>源网点编号 <em>*</em></label>
        <input class="textbox" id="sourceDealer" name="sourceDealer" ajaxurl="${rc.contextPath}/dealerMapping/check" value="${dealerMapping.sourceDealer}" onkeyup="sourceDealerNameQuery()" style="height: 30px; width: 180px;" datatype="*" nullmsg="请输入网点代码" errormsg="请输入正确的网点代码"/>
	<@spring.bind "dealerMapping.sourceDealer" />
        <span class="Validform_checktip error"><@spring.showError/></span>
    </li>
    <li class="clearfix floatselect">
        <label>源网点名称 </label>
		<span class="iname" id="sourceDealerName_span">${dealerMapping.sourceDealerName}</span>
		<input id="sourceDealerName" name="sourceDealerName" value="${dealerMapping.sourceDealerName}" type="hidden"/>
    </li>

    <li class="clearfix">
        <label>目标网点编号 <em>*</em></label>
        <input class="textbox" id="targetDealer" name="targetDealer" ajaxurl="${rc.contextPath}/dealerMapping/check" value="${dealerMapping.targetDealer}" onkeyup="targetDealerNameQuery()" style="height: 30px; width: 180px;" datatype="*" nullmsg="请输入网点代码" errormsg="请输入正确的网点代码"/>
    <@spring.bind "dealerMapping.targetDealer" />
        <span class="Validform_checktip error"><@spring.showError/></span>
    </li>
    <li class="clearfix floatselect">
        <label>目标网点名称 </label>
		<span class="iname" id="targetDealerName_span">${dealerMapping.targetDealerName}</span>
		<input id="targetDealerName" name="targetDealerName" value="${dealerMapping.targetDealerName}" type="hidden"/>
    </li>

    <li class="clearfix">
        <label>映射原因 <em>*</em></label>
        <input class="textbox" id="mappingReasonPhrase" name="mappingReasonPhrase" value="${dealerMapping.mappingReasonPhrase}" style="height: 30px; width: 180px;" datatype="*" nullmsg="请输入映射原因" />
        <@spring.bind "dealerMapping.mappingReasonPhrase" />
        <span class="Validform_checktip error"><@spring.showError/></span>
    </li>

    <li class="clearfix">
        <label>开始日期</label>
        <input class="easyui-datebox" editable="false" id="mappingBegDate" name="mappingBegDate" value="${dealerMapping.mappingBegDate}" class="easyui-datebox" style="height: 30px; width: 180px;"/>
        <span class="Validform_checktip error"><@spring.showError/></span>
    </li>
    <li class="clearfix">
        <label>结束日期</label>
        <input class="easyui-datebox" editable="false" id="mappingEndDate" name="mappingEndDate" value="${dealerMapping.mappingEndDate}"  style="height: 30px; width: 180px;"/>
        <span class="Validform_checktip error"><@spring.showError/></span>
    </li>
</ul>
