<#ftl attributes={'content_type':'text/html; charset=UTF-8'}>
<tr id='row_${series.id}'>
	<td>${series.name}</td>
	<td>
		<input class='searchselect' type='text' name='youhui_${series.id}' value='' style='width:100px;' />
		<label style='display:none;' id='youhui_${series.id}_label'></label>
		<span id="sp_youhui_${series.id}">万元</span>
	</td>
	<td>
		<div class='sale' id='div_jiajia_${series.id}'>
		   <input type='radio' name='youhui_${series.id}' id="jiajia_${series.id}" />
		   <span>加价</span>
		  <input class='searchselect' type='text' name='luoche_jiajia2_${series.id}' value='' style='width:100px;' />
		  <label style='display:none;' id='luoche_jiajia2_${series.id}_label'></label>
		  <span>万元</span>
		</div>
		<div class='sale' id="div_youhui_${series.id}">
			<p id='luoche_youhui_div'>
			  <input type='radio' name='youhui_${series.id}' id="youhui_${series.id}" />
			  <span>优惠</span>
			</p>
			<p>
				<span id="p_changshang_${series.id}">含厂商补贴：</span>
				<input class='searchselect' type='text' name='changshang_${series.id}' value='' style='width:100px;' />
				<label style='display:none;' id='changshang_${series.id}_label'></label>
				<span id="sp_changshang_${series.id}">万元</span>
			</p>
			<p>
				<span id="p_zhengfu_${series.id}">含政府补贴：</span>
				<input class='searchselect' type='text' name='zhengfu_${series.id}' value='' style='width:100px;' />
				<label style='display:none;' id='zhengfu_${series.id}_label'></label>
				<span id="sp_zhengfu_${series.id}">万元</span>
			</p>
			<p>
				<span id="p_huiming_${series.id}">含惠民补贴：</span>
				<input class='searchselect' type='text' name='huiming_${series.id}' value='' style='width:100px;' />
				<label style='display:none;' id='huiming_${series.id}_label'></label>
				<span id="sp_huiming_${series.id}">万元</span>
			</p>
		</div>
	</td>
	<td>
		<input class='searchselect' type='text'  name='libao_${series.id}' value='' style='width:100px;' />
		<label style='display:none;' id='libao_${series.id}_label'></label>
		<span id="sp_libao_${series.id}">万元</span>
	</td>
	<td>
		<select class='searchselect' style='width: 85px;' name='xianche${series.id}' >
			<option value='现车充足'>现车充足</option>
			<option value='少量现车'>少量现车</option>
			<option value='需要预定'>需要预定</option>
		</select>
		<label style='display:none;' id='xianche${series.id}_label'></label>
	</td>
</tr>
