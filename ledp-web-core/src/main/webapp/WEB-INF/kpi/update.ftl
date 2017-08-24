<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#include "/layouts/main.ftl"/>
<@html></@html>
<#macro style>
	<style>
		.formlist > li{
			*height:auto;
		}
	</style>
</#macro>
<#macro script>
		<script type="text/javascript">
			$(function(){
				$.plugin.validate();	
			});
		</script>
</#macro>
<#macro content>
	<form id="form_kip_update" class="validateCheck" action="${rc.contextPath}/kpi/edit" method="post" data-options="novalidate:true">
		<div class="clearfix kpicustom">
			<div class="dewidth floatL">
				<h2>线索跟进率KPI设置</h2>
				<input id="leadsId" type="hidden" name="leadsId" value="${leads.id?if_exists}" />
				<ul cellpadding="5" class="clearfix tabadd formlist">
					<li class="clearfix">
						<label>阀值 <em>*</em>
						</label>
						<input type="text" datatype="n" nullmsg="请输入阀值" errormsg="请输入数字值" style="height: 30px; width: 100px;" class="textbox" name="leads_v" value="${leads.threshold?if_exists}" />
						<span class="Validform_checktip error"></span>
					</li>
					<li  class="clearfix">
						<label>满分 <em>*</em>
						</label>
						<input type="text" datatype="n" nullmsg="请输入满分值" errormsg="请输入数字值" style="height: 30px; width: 100px;" class="textbox" name="leads_s" value="${leads.score?if_exists}" />
						<span class="Validform_checktip error"></span>
					</li>
					<li  class="clearfix">
						<label>权重 <em>*</em>
						</label>
						<input type="text" datatype="n" nullmsg="请输入权重值" errormsg="请输入数字值" style="height: 30px; width: 100px;" class="textbox" name="leads_w" value="${leads.weight?if_exists}" />
						<span class="Validform_checktip error"></span>
					</li>
					<li  class="clearfix">
						<label>评分规则</label>
						<span style="margin-top: 15px;display: inline-block; width:300px;">线索跟进率(A)=24小时内跟进线索量/总线索量，最终得分a，A>=80%,a=A*100%,A<80%,a=A*100*0.8</span>
					</li>
				</ul>
			</div>
			<div class="dewidth floatR">
				<h2>400电话接起率KPI设置</h2>
				<input id="phoneId" type="hidden" name="phoneId" value="${phone.id?if_exists}" />
				<ul cellpadding="5" class="clearfix tabadd formlist">
					<li class="clearfix">
						<label>阀值 <em>*</em>
						</label>
						<input type="text" datatype="n" nullmsg="请输入阀值" errormsg="请输入数字值" class="textbox" style="height: 30px; width: 100px;"  name="phone_v" value="${phone.threshold?if_exists}" />
						<span class="Validform_checktip error"></span>
					</li>
					<li  class="clearfix">
						<label>满分 <em>*</em>
						</label>
						<input type="text" datatype="n" nullmsg="请输入满分值" errormsg="请输入数字值" class="textbox" style="height: 30px; width: 100px;"  name="phone_s" value="${phone.score?if_exists}" />
						<span class="Validform_checktip error"></span>
					</li>
					<li  class="clearfix">
						<label>权重 <em>*</em>
						</label>
						<input type="text" datatype="n" nullmsg="请输入权重值" errormsg="请输入数字值" class="textbox"  style="height: 30px; width: 100px;"  name="phone_w" value="${phone.weight?if_exists}" />
						<span class="Validform_checktip error"></span>
					</li>
					<li  class="clearfix">
						<label>评分规则</label>
						<span style="margin-top: 15px;display: inline-block; width:300px;">接起率(B)=400接起量/400来电量，最终得分b，B>=80%,b=B*100%,B<80%,b=B*100*0.8</span>
					</li>
				</ul>
			</div>
		</div>
		<div class="clearfix kpicustom mt20">
			<div class="dewidth floatL">
				<h2>新闻信息发布量完成率KPI设置</h2>
				<input id="mediaId" type="hidden" name="mediaId" value="${media.id?if_exists}" />
				<ul cellpadding="5" class="clearfix tabadd formlist">
					<li class="clearfix">
						<label>阀值 <em>*</em>
						</label>
						<input type="text" datatype="n" nullmsg="请输入阀值" errormsg="请输入数字值" class="textbox" style="height: 30px; width: 100px;"  name="media_v" value="${media.threshold?if_exists}" />
						<span class="Validform_checktip error"></span>
					</li>
					<li  class="clearfix">
						<label>满分 <em>*</em>
						</label>
						<input type="text" datatype="n" nullmsg="请输入满分值" errormsg="请输入数字值" class="textbox" style="height: 30px; width: 100px;" name="media_s" value="${media.score?if_exists}" />
						<span class="Validform_checktip error"></span>
					</li>
					<li  class="clearfix">
						<label>权重 <em>*</em>
						</label>
						<input type="text" datatype="n" nullmsg="请输入权重值" errormsg="请输入数字值" class="textbox" style="height: 30px; width: 100px;" name="media_w" value="${media.weight?if_exists}" />
						<span class="Validform_checktip error"></span>
					</li>
					<li  class="clearfix">
						<label>评分规则</label>
						<span style="margin-top: 15px;display: inline-block; width:300px;">信息发布量完成率(C)=信息发布量/信息发布目标，最终得分c，C>=100%,c=100,C<100%,c=C*100*0.8</span>
					</li>
				</ul>
			</div>
		</div>
		<div class="btnsumbit btncoloes" style="*clear:both;">
			<a href="#" class="easyui-linkbutton btnstyle" onClick="$('#form_kip_update').submit();">保 存</a>
			<a href="${rc.contextPath}/kpi/index" class="easyui-linkbutton backbtn">返 回</a>
		</div>
	</form>
</#macro>