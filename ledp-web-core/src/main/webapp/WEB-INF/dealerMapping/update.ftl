<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#include "/layouts/main.ftl"/>
<@html></@html>
<#macro style></#macro>
<#macro script>
	<script type="text/javascript">
        $(function(){$.plugin.validate();});

        function targetDealerNameQuery() {
            var targetDealer = $("#targetDealer").val();
            $("#targetDealerName_span").html("");
            $("#targetDealerName").val("");

            if (null == targetDealer || "" == targetDealer) {
                return;
            }
            dealerName(targetDealer, $("#targetDealerName"), $("#targetDealerName_span"));
        }

        function dealerName(dealer, target_txt, target_span) {
            if (null == dealer || "" == dealer) {
                return;
            }
            $.getJSON(
                    "${rc.contextPath}/dealerMapping/name/"+dealer,
                    function(result){
						target_txt.val(result.name);
						target_span.html(result.name);
                    }
            );
        }
		
	</script>
</#macro>
<#macro content>
	<div class="floatL detaillist">
		<form id="form_dealerMapping_update" class="validateCheck" action="${rc.contextPath}/dealerMapping/edit" method="POST" data-options="novalidate:true">
            <ul class="formlist">
                <li class="clearfix">
                    <label>源网点编号 <em>*</em></label>
                    <span class="iname" id="sourceDealer">${dealerMapping.sourceDealer}</span>
                    <input name="sourceDealer" value="${dealerMapping.sourceDealer}" type="hidden"/>
                </li>
                <li class="clearfix floatselect">
                    <label>源网点名称 </label>
                    <span class="iname" id="sourceDealerName">${dealerMapping.sourceDealerName}</span>
                    <input name="sourceDealerName" value="${dealerMapping.sourceDealerName}" type="hidden"/>
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
					<@spring.bind "dealerMapping.mappingBegDate" />
                    <span class="Validform_checktip error"><@spring.showError/></span>
                </li>
                <li class="clearfix">
                    <label>结束日期</label>
                    <input class="easyui-datebox" editable="false" id="mappingEndDate" name="mappingEndDate" value="${dealerMapping.mappingEndDate}"  style="height: 30px; width: 180px;"/>
					<@spring.bind "dealerMapping.mappingEndDate" />
                    <span class="Validform_checktip error"><@spring.showError/></span>
                </li>
            </ul>
			<input type="hidden" name="id" value="${dealerMapping.id}"/>
			<div class="btnsumbit">
				<a href="javascript:void(0)" class="easyui-linkbutton btnstyle" onClick="$('#form_dealerMapping_update').submit();">保 存</a>
				<a href="${rc.contextPath}/dealerMapping/index" class="easyui-linkbutton backbtn">返 回</a>
			</div>
		</form>
	</div>
</#macro>