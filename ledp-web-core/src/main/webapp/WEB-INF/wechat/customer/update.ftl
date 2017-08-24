<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#assign tag=JspTaglibs["/WEB-INF/taglib/permission.tld"]>
<#include "/wechat/layouts/main.ftl"/>
<@html></@html>
<#macro style>
<style>
	.Validform_wrong{
		color:#cc0000;
	}
	.creatlist{
		margin:40px 0 20px 0;
	}
	.creatlist label{
		float:left;
		width:35%;
		line-height:30px;
		text-align:right;
		display:block;
		font-size:14px;
	}
	.creatlist >li>div{
		float:left;
		margin-left:1%;
		width:63%;
		text-align:left;
	}
	.creatlist .Validform_wrong{
		color:red;
		margin:5px 0;
	}
	.creatlist li{
		margin:10px 0 0;
		width:100%;
		overflow:hidden;
	}
	.marginLeft40{
		margin-left:40%;
	}
	.infojb{
		font-family: "微软雅黑";
		font-size: 14px;
		line-height: 50px;
		background: #fbfbfb;
		text-indent: 2em;
	}
	.creatlist li em{
		color:#d12d32;
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
            <span><a href="${rc.contextPath}/wechat/report/leads/index">留资列表</a></span>
            <span>&gt;&gt;</span>
            <span>编辑客户</span>
        </li>
    </ul>
</div>
<div>
    <div>
			<h1 class="infojb">基本信息</h1>
			<form id="form_customer_save" class="validateCheck" action="${rc.contextPath}/wechat/customer/edit" method="post">
			<input type="hidden" name="id" value="${customer.id}"/>
			<div>
				<ul class="creatlist">
					<li>
						<label>客户名称<em>*</em>：</label>
						<div>
						<input type="text" class="textbox" checkdata="notNull"  maxlength="50" dataType="*" errormsg="请输入客户名称"  nullmsg="请输入客户名称" name="name" style="width: 300px;height:30px"  value="${customer.name}"></input>
						<div class="Validform_checktip"></div>
						</div>
					</li>
					<li>
						<label>电话号码<em>*</em>：</label>
						<div>
						<input type="text" class="textbox" style="width: 300px;height:30px"  value="${customer.phone}" readOnly></input>
						<div class="Validform_checktip"></div>
						</div>
					</li>
					<li>
						<label>客户邮件：</label>
						<div>
						<input type="text" class="textbox" datatype="e" name="email" style="width: 300px;height:30px" value="${customer.email}"></input>
						<div class="Validform_checktip"></div>
						</div>
					</li>
					<li>
						<label>省份：</label>
						<div>
						<input type="text" name="province" style="width: 300px;height:30px"  value="${customer.province}"></input>
						<div class="Validform_checktip"></div>
						<#-- <select id="province" name="province.id" style="width:200px; height: 30px;float: left;" class="searchselect" datatype='*' nullmsg='请选择省份'>
							<option value="">--请选择--</option>
							<#list regionList as region>
								<option value="${region.id}">${region.zh_name}</option>
							</#list>
						</select>
						-->
						</div>
					</li>
					<li>
						<label>城市：</label>
						<div>
						<input type="text" class="textbox" name="city" style="width: 300px;height:30px" value="${customer.city}"></input>
						<div class="Validform_checktip"></div>
						<#--<select id="city" name="city.id" style="width:200px; height: 30px;float: left;" class="searchselect" datatype='*' nullmsg='请选择城市'>
							<option value=''>--请选择--</option>
						</select>-->
						</div>
					</li>
					<li>
						<label>区县：</label>
						<div>
						<input type="text" class="textbox" name="district" style="width: 300px;height:30px" value="${customer.district}"></input>
						<div class="Validform_checktip"></div>
						<#--<select id="district" name="district.id" style="width:200px; height: 30px;float: left;" class="searchselect" datatype='*' nullmsg='请选择区县'>
							<option value=''>--请选择--</option>
						</select>-->
						</div>
					</li>
					<li>
						<label>详细地址：</label>
						<div>
						<input type="text" class="textbox" name="address" style="width: 300px;height:30px"  value="${customer.address}"></input>
						<div class="Validform_checktip"></div>
						</div>
					</li>
				</ul>
			</div>
			<div class="textAlign" style="margin:20px 0;">
				<button class="redButton" onclick="$('#form_customer_save').submit();">保 存</button>
				<input type="button" class="redButton" onclick="window.location.href='${rc.contextPath}/wechat/customer/index'" value="返回" />
			</div>
			</form>
</#macro>
<#macro script>
	<script type="text/javascript" src="${rc.contextPath}/wechat/js/Validform_v5.3.2_min.js"></script>
	<script type="text/javascript" src="${rc.contextPath}/wechat/js/validateCheck.js"></script>
    <script type="text/javascript" charset="utf-8">

        $(function () {
            $.plugin.validate();	
        });
    </script>
</#macro>