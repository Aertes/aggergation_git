<#ftl attributes={"content_type":"text/html; charset=UTF-8"}>
<#setting classic_compatible=true>
		<ul>
			<li style="width: 33%;margin:10px 0;">
				<p>
					<span class="numbers">${totals.usedTotal?default('0')}</span>
				</p>
				<p class="msg">插件使用次数</p>
			</li>
			<li  style="width: 33%;margin:10px 0;">
				<p>
					<span class="numbers">${totals.dealerTotal?default('0')}</span>
				</p>
				<p class="msg">使用插件的网点数量</p>
			</li>
			<li  style="width: 32%;border-right: 0px;margin:10px 0;">
				<p>
					<span class="numbers">${totals.leadsTotal?default('0')}</span>
				</p>
				<p class="msg">插件获取留资数</p>
			</li>
		</ul>
