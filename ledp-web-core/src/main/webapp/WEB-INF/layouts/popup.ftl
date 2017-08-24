<#macro generalMessage >
	<div id="error_message" class="easyui-window" title="消息框" data-options="modal:true,closed:true,iconCls:'icon-save'" style="width:300px;height:150px;padding:10px;">
		<p style="line-height: 80px;">您提交的数据有误，请检查后重新提交！</p>
	</div>
	<div id="success_message" class="easyui-window" title="消息框" data-options="modal:true,closed:true,iconCls:'icon-save'" style="width:300px;height:150px;padding:10px;">
		<p style="line-height: 80px;">您的数据提交成功！</p>
	</div>
	<div id="delete_message" class="easyui-window" title="消息框" data-options="modal:true,closed:true,iconCls:'icon-save'" style="width:500px;height:150px;padding:10px;">
		<p>确认要删除么？</p>
		<div data-options="region:'south',border:false" class="delbtn">
			<a class="easyui-linkbutton btnstyle" data-options="iconCls:'icon-ok'" href="javascript:void(0)" id="delete_ok" style="width:100px">确认</a>
			<a class="easyui-linkbutton cancel" data-options="iconCls:'icon-cancel'" href="javascript:void(0)" id="delete_cancel" style="width:100px">取消</a>
		</div>
	</div>
	<div id="ok_title_message" class="easyui-window" title="消息框" data-options="modal:true,closed:true,iconCls:'icon-save'" style="width:500px;height:150px;padding:10px;">
		<p>导入成功！</p>
		<div data-options="region:'south',border:false" class="delbtn">
			<a class="easyui-linkbutton btnstyle" data-options="iconCls:'icon-ok'" href="javascript:void(0)" id="title_ok" style="width:100px">确认</a>
		</div>
	</div>
	<div id="lockup_callback" class="easyui-window" title="消息框" data-options="modal:true,closed:true,iconCls:'icon-save'" style="width:600px;height:400px;padding:10px;">
		
	</div>
	<div id="input_lockup">
	</div>
	<script type="text/javascript">
		$('#error_message').window({
			collapsible:false,
			minimizable:false,
			maximizable:false
		});
		$('#success_message').window({
			collapsible:false,
			minimizable:false,
			maximizable:false
		});
		$('#delete_message').window({
			collapsible:false,
			minimizable:false,
			maximizable:false
		});
		$('#lockup_callback').window({
			closable:false
		});
		$('#ok_title_message').window({
			collapsible:false,
			minimizable:false,
			maximizable:false
		});
	</script>
</#macro>