(function($) {
	jQuery.plugin = {
			formId : "",
			datsgridId : "",
			sortName : "",
			sortOrder : "",
			currentPage : 0,
			pageSize : 0,
			conditionMap : {},
			url: "",
			init : function($formId,$dataGridId) {
				var _this = this;
				_this.formId = $formId;
				_this.url = $(_this.formId).attr("action");
				_this.datsgridId = $dataGridId;
				_this.sortName = $(_this.datsgridId).datagrid("options").sortName;
				_this.sortOrder = $(_this.datsgridId).datagrid("options").sortOrder;
				_this.currentPage = $(_this.datsgridId).datagrid("getPager").data(
						"pagination").options.pageNumber;
				_this.pageSize = $(_this.datsgridId).datagrid("getPager").data(
						"pagination").options.pageSize;
				$(_this.formId+" .search").each(function() {
					if ($(this).attr("name") && $(this).val()) {
						_this.conditionMap[$(this).attr("name")] = $(this).val();
					}
				});

				// 绑定方法
				$(_this.formId+" .submit").click(function(event) {
					event.preventDefault();
					_this.currentPage = 1;
					_this.searchByForm();
				});
				_this.firstLoad();
				
				_this.bindValue();
			},
			bindValue : function(){
				var _this =this;
				$(_this.formId+" .search").change(function(){
					_this.conditionMap[$(this).attr("name")] = $(this).val();
				});
			},
			firstLoad : function(){
				var _this =this;
				_this.searchMethod();
			},
			searchMethod : function() {
				var _this = this;
				$(_this.formId+" .search").each(function() {
					if ($(this).attr("name")) {
						_this.conditionMap[$(this).attr("name")] = $(this).val();
					}
				});
				var  data = _this.conditionMap
				data['sortName']  =_this.sortName; 
				data['sortOrder']  =_this.sortOrder; 
				data['pageNumber']  =_this.currentPage; 
				data['pageSize']  =_this.pageSize; 
				
				$.ajax({
					url : _this.url,
					type : "post",
					data : data,
					success : function(data) {
						if (data) {
							//var $json = eval("(" + data + ")");
							_this.setChangeValue(data);
						}
					},
					error  : function(data) {
					}
				});
			},
			setChangeValue : function($json) {
				var _this = this;
				$(_this.datsgridId).datagrid({
					pageNumber : $json["pageNumber"],
					pageSize : $json["pageSize"],
					sortName : $json["sortName"],
					sortOrder : $json["sortOrder"],
					currentPage : $json["currentPage"],
					onSortColumn : function(sortName, sortOrder) {
						_this.sortName =sortName.split(",").length==0 ? null :sortName.split(",").pop();
						_this.sortOrder =sortOrder.split(",").length==0 ? null : sortOrder.split(",").pop();
						_this.searchMethod();
						return false;
					}
				});
				$(_this.datsgridId).datagrid("loadData", $json["rowList"]);
				_this.changePager($json);
			},
			changePager : function($json) {
				var _this = this;
				$(_this.datsgridId).datagrid("getPager").pagination({
					onSelectPage : function(currentPage) {
						_this.currentPage = currentPage;
						_this.searchMethod();
						return false;
					},
					onChangePageSize : function(pageSize) {
						_this.pageSize = pageSize;
						_this.searchMethod();
						return false;
					},
					onRefresh : function(currentPage) {
						_this.currentPage = currentPage;
						_this.searchMethod();
						return false;
					}
				});
			},
			searchByForm : function() {
				var _this = this;
				$(_this.formId).find(".search").each(function() {
					if ($(this).attr("name") && $(this).val()) {
						_this.conditionMap[$(this).attr("name")] = $(this).val();
					}
				});
				_this.searchMethod();
			},
			validate : function(){
				$(".validateCheck").Validform({
					tiptype:function(msg,o,cssctl){
						if(!o.obj.is("form")){
							var objtip=o.obj.siblings(".Validform_checktip");
							cssctl(objtip,o.type);
							objtip.text(msg);
						}
					}
				}); 
			}
		};
})(jQuery);

/**
 * 获取Tree当前选中节点等级
 * @returns {Number}
 */
function getTreeSelectedNodeLevel(){
	var startLevel = 0;
	var exists = $(".tree-node-selected").parent("li");
	while(exists && exists.length > 0){
		//有上一级
		startLevel ++;
		exists = $(exists).parent("ul");
		if(exists && exists.length > 0 && exists[0].className.indexOf("easyui-tree") != - 1){
			//最顶层了
			break;
		}
		exists = $(exists).parent("li");
	}
	return startLevel;
}