<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#include "/layouts/main.ftl"/>
<@html></@html>
<#macro style></#macro>
<#macro script></#macro>
<#macro content>
	<div class="detaillist ollright">
		<div class="tih2 clearfix">
			<p class="floatL">添加优惠促销</p>
		</div>
		<div class="infometionlist">
			<div class="floatL detaillist">
				<form id="ff" method="post" data-options="novalidate:true">
					<div class="clearfix tabadd messadd">
						<ul>
							<li class="clearfix">
								<label>关联车系<em>*</em></label>
								<div class="floatL glcx">
									<ul class="clearFix">
										<li>
											<input type="checkbox" />
											<span>c3-r</span>
										</li>
										<li>
											<input type="checkbox" />
											<span>c3-r</span>
										</li>
										<li>
											<input type="checkbox" />
											<span>c3-r</span>
										</li>
										<li>
											<input type="checkbox" />
											<span>c3-r</span>
										</li>
										<li>
											<input type="checkbox" />
											<span>c3-r</span>
										</li>
									</ul>
								</div>
							</li>
							<li class="clearfix">
								<label>厂商指导价<em>*</em></label>
								<div class="floatL glcx">
									<ul>
										<li>
											<input class="key" type="text" style="width:80px; height: 17px; margin:0 5px;" />
											<span>万元</span>
										</li>
									</ul>
								</div>
							</li>
							<li class="clearfix">
								<label>现金优惠<em>*</em></label>
								<div class="floatL glcx">
									<ul class="clearFix">
										<li>
											<input name="1" type="radio" />
											<span>优惠</span>
										</li>
									</ul>
									<ul>
										<li>
											<input type="radio" name="1" />
											<span>加价</span>
											<input class="key" type="text" style="width:80px; height: 17px; margin:0 5px;" />
											<span>万元</span>
										</li>
										<li style="margin-left: 20px;">
											<span>含厂商补贴</span>
											<input class="key" type="text" style="width:80px; height: 17px; margin:0 5px;" />
											<span>万元</span>
										</li>
										<li>
											<span>含政府补贴</span>
											<input class="key" type="text" style="width:80px; height: 17px; margin:0 5px;" />
											<span>万元</span>
										</li>
										<li>
											<span>含惠民补贴</span>
											<input class="key" type="text" style="width:80px; height: 17px; margin:0 5px;" />
											<span>万元</span>
										</li>
									</ul>
								</div>
							</li>
							<li class="clearfix">
								<label>裸车价<em>*</em></label>
								<div class="floatL glcx">
									<ul>
										<li>
											<span>补贴金额</span>
											<input class="key" type="text" style="width:80px; height: 17px; margin:0 5px;" />
											<span>元</span>
										</li>
									</ul>
								</div>
							</li>
							<li class="clearfix">
								<label>礼包设置<em>*</em></label>
								<div class="floatL glcx">
									<ul>
										<li>
											<input name="1" type="radio" />
											<span>不赠送礼包</span>
										</li>
										<li>
											<input onclick="del()" name="1" type="radio" />
											<span>赠送礼包</span>
										</li>
									</ul>
								</div>
							</li>
							<li class="clearfix">
								<label>请选择有效期<em>*</em></label>
								<div class="floatL glcx">
									<ul>
										<li>
											<div class="clearFix">
												<input style="width: 150px; height: 20px; display: none;" class="easyui-datetimebox combo-f textbox-f datetimebox-f">
												<p class="floatL" style="margin:0 5px;">至</p>
												<input style="width: 150px; height: 20px; display: none;" class="easyui-datetimebox combo-f textbox-f datetimebox-f">
											</div>
											<p class="mt10">温馨提示：早8：30至晚17：30之间发布推荐的文章，当天审核完毕，非工作时间及节假日除外。</p>
										</li>
									</ul>
								</div>
							</li>
						</ul>
					</div>
				</form>
				<div class="btnsumbit">
					<a href="${rc.contextPath}/promotion/createStep2" class="easyui-linkbutton btnstyle">下一步</a>
					<a href="${rc.contextPath}/promotion/index" class="easyui-linkbutton backbtn">返 回</a>
				</div>
			</div>
		</div>
	</div>
	<div id="ws" class="easyui-window" title="" data-options="modal:true,closed:true,iconCls:'icon-save'" style="width:1000px;padding:10px;">
		<div class="give">
			<h2>选择赠送礼包</h2>
			<table cellpadding="0" cellspacing="0" width="100%">
				<thead>
					<th>赠送礼包名称</th>
					<th>价格</th>
					<th>赠送礼包内容</th>
					<th>添加时间</th>
					<th>操作</th>
				</thead>
				<tbody>
					<tr>
						<td></td>
					</tr>
				</tbody>
			</table>
		</div>
		<div data-options="region:'south',border:false" class="delbtn">
			<a class="easyui-linkbutton btnstyle" data-options="iconCls:'icon-ok'" href="javascript:void(0)" style="width:100px">确认</a>
			<a class="easyui-linkbutton cancel" data-options="iconCls:'icon-cancel'" href="javascript:void(0)" onclick="$('#ws').window('close')" style="width:100px">取消</a>
		</div>
	</div>
</#macro>