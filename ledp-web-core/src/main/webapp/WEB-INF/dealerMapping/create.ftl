<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#include "/layouts/main.ftl"/>
<@html></@html>
<#macro style></#macro>
<#macro script>
	<script type="text/javascript">
        $(function(){$.plugin.validate();});

        function sourceDealerNameQuery() {
            var sourceDealer = $("#sourceDealer").val();
			$("#sourceDealerName_span").html("");
			$("#sourceDealerName").val("");

            if (null == sourceDealer || "" == sourceDealer) {
                return;
            }
            dealerName(sourceDealer, $("#sourceDealerName"), $("#sourceDealerName_span"));
        }
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
		<form id="form_dealerMapping_create" class="validateCheck" action="${rc.contextPath}/dealerMapping/save" method="POST" data-options="novalidate:true">
			<#include "/dealerMapping/_form.ftl"/>
			<div class="btnsumbit">
				<a href="javascript:void(0)" class="easyui-linkbutton btnstyle" onClick="$('#form_dealerMapping_create').submit();">添 加</a>
				<a href="${rc.contextPath}/dealerMapping/index" class="easyui-linkbutton backbtn">返 回</a>
			</div>
		</form>
	</div>
</#macro>