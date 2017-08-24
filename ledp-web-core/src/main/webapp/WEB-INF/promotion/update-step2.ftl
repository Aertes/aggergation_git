<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#include "/layouts/main.ftl"/>
<@html></@html>
<#macro style></#macro>
<#macro script></#macro>
<#macro content>
	<div class="detaillist ollright">
		<div class="tih2 clearfix">
			<p class="floatL">优惠促销预览</p>
		</div>
		<div class="infometionlist">
			<div>
				<div class="tit clearfix titwidth">
					<h2>车型信息</h2>
				</div>
				<div class="previewI">
					<div class="ichtable" style="min-height:auto; max-height:500px;">
						<table id="dg" width="100%" class="easyui-datagrid" title="<a href='#' title=''>添加</a>" data-options="url:'${rc.contextPath}/${rc.contextPath}/json/datagrid_data9.json',singleSelect:true,collapsible:false,method:'get',remoteSort:false,multiSort:true,pagination:true,showRefresh:false">
							<thead>
								<tr>
									<th width="10%" data-options="field:'particular',sortable:true">年份</th>
									<th width="15%" data-options="field:'models',sortable:true">车型</th>
									<th width="10%" data-options="field:'instructions',sortable:true">厂商指导价</th>
									<th width="10%" data-options="field:'cash',sortable:true">现金优惠</th>
									<th width="10%" data-options="field:'barecar',sortable:true">裸车价</th>
									<th width="10%" data-options="field:'concessions',sortable:true">赠送优惠</th>
									<th width="10%" data-options="field:'reference',sortable:true">参考成交价</th>
									<th width="10%" data-options="field:'gift',sortable:true">赠送礼包</th>
									<th width="10%" data-options="field:'condition',sortable:true">现车情况</th>
									<th width="5%" data-options="field:'operation',sortable:true">操作</th>
								</tr>
							</thead>
						</table>
					</div>
					<div>
						<div class="searchlist">
							<p>置换补贴</p>
							<ul class="clearFix">
								<li>
									<a title="" href="#">补贴金额</a>
									<input class="searchselect" type="text" style="width:100px;" />
								</li>
							</ul>
						</div>
						<div class="searchlist">
							<p>优惠备注</p>
							<ul class="clearFix">
								<li>
									<textarea maxlength="1000" class="supplementary"></textarea>
								</li>
							</ul>
						</div>
					</div>
				</div>
				</div>
				<div>
					<div class="tit clearfix titwidth">
						<h2>维修保养</h2>
					</div>
					<form id="ff" method="post" data-options="novalidate:true">
						<div class="clearfix tabadd messadd">
							<ul>
								<li class="clearfix">
									<label>质保期</label>
									<div class="floatL">
										<select class="searchselect repair">
											<option>请选择年份</option>
											<option>一年</option>
											<option>二年</option>
										</select>
										<span>或</span>
										<input class="searchselect" type="text" style="width:100px;" />
										<span>公里</span>
									</div>
								</li>
								<li class="clearfix">
									<label>店内建议保养周期</label>
									<div class="floatL">
										<input type="radio" name="1" />
										<span>5000公里</span>
										<input type="radio" name="1" />
										<span>7500公里</span>
										<input type="radio" name="1" />
										<span>10000公里</span>
										<input type="radio" name="1" />
										<span>其他</span>
										<input class="searchselect" type="text" style="width:100px;" />
										<span>公里</span>
									</div>
								</li>
								<li class="clearfix">
									<label>更换机油机滤费用</label>
									<div class="floatL">
										<input class="searchselect" type="text" style="width:100px;" />
										<span>元左右（以4S店价格为准）</span>
									</div>
								</li>
								<li class="clearfix">
									<label>更换机油三滤费用</label>
									<div class="floatL">
										<input class="searchselect" type="text" style="width:100px;" />
										<span>元左右（以4S店价格为准）</span>
									</div>
								</li>
								<li class="clearfix">
									<label>店内提供保险公司</label>
									<div class="floatL glcx">
										<ul class="clearFix">
											<li>
												<input type="checkbox" />
												<span>中保</span>
											</li>
											<li>
												<input type="checkbox" />
												<span>人保</span>
											</li>
											<li>
												<input type="checkbox" />
												<span>平安</span>
											</li>
											<li>
												<input type="checkbox" />
												<span>其他</span>
											</li>
										</ul>
									</div>
								</li>
								<li class="clearfix">
									<label>保险费用</label>
									<div class="floatL">
										<input class="searchselect" type="text" style="width:100px;" />
										<span>元左右（以4S店价格为准）</span>
									</div>
								</li>
								<li class="clearfix">
									<label>金融公司</label>
									<div class="floatL">
										<input class="searchselect" type="text" style="width:100px;" />
									</div>
								</li>
								<li class="clearfix">
									<label>贷款方式</label>
									<div class="floatL way">
										<p>
											<span>首付</span>
											<input class="searchselect" type="text" style="width:100px;" />
											<span>%</span>
										</p>
										<p class="mt10">
											<span>时间</span>
											<input class="searchselect" type="text" style="width:100px;" />
											<span>月</span>
										</p>
										<p class="mt10">
											<span>月供</span>
											<input class="searchselect" type="text" style="width:100px;" />
											<span>元左右</span>
										</p>
									</div>
								</li>
							</ul>
						</div>
					</form>
				</div>
				<div>
					<div class="tit clearfix titwidth">
						<h2>文章内容</h2>
					</div>
					<form id="ff" method="post" data-options="novalidate:true">
						<div class="clearfix tabadd messadd">
							<ul>
								<li class="clearfix">
									<label>标题</label>
									<div class="floatL">
										<input class="searchselect" type="text" style="width:100px;" />
									</div>
								</li>
								<li class="clearfix">
									<label>短标题</label>
									<div class="floatL">
										<input class="searchselect" type="text" style="width:100px;" />
									</div>
								</li>
								<li class="clearfix">
									<label>内容</label>
									<div class="floatL">
										<p class="clearfix">
											<textarea style="width:500px; height: 100px;"></textarea>
										</p>
										<p class="mi10 tipsfont">已输入<span>60</span>个字符，最多1000个字符</p>
									</div>
								</li>
								<li class="clearfix">
									<label>展示大图</label>
									<div class="floatL">
										<input type="file" style="margin:0 10px;" />
									</div>
								</li>
								<li class="clearfix">
									<label>展示小图</label>
									<div class="floatL">
										<input type="file" style="margin:0 10px;" />
									</div>
								</li>
								<li class="clearfix">
									<label>本店特色</label>
									<div class="floatL">
										<p class="clearfix">
											<textarea style="width:500px; height: 100px;"></textarea>
										</p>
										<p class="mi10 tipsfont">已输入<span>60</span>个字符，最多1000个字符</p>
									</div>
								</li>
								<li class="clearfix">
									<label>公司信息</label>
									<div class="floatL glcx">
										<ul class="clearFix">
											<li>
												<input type="checkbox" />
												<span>添加公司销售电话</span>
											</li>
											<li>
												<input type="checkbox" />
												<span>添加公司地址</span>
											</li>
										</ul>
									</div>
								</li>
							</ul>
						</div>
					</form>
				</div>
			</div>
			<div class="sumbit presumbit">
				<input type="button" value="预览" class="btnstyle">
				<input type="button" value="保存" class="btnstyle">
			</div>
		</div>
	</div>
</#macro>