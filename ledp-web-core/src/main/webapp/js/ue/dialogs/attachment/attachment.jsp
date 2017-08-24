<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Insert title here</title>
 <script type="text/javascript" src="../internal.js"></script>

    <!-- jquery -->
    <script type="text/javascript" src="../../third-party/jquery-1.10.2.min.js"></script>

    <!-- webuploader -->
    <script src="../../third-party/webuploader/webuploader.min.js"></script>
    <link rel="stylesheet" type="text/css" href="../../third-party/webuploader/webuploader.css">

    <!-- attachment dialog -->
    <link rel="stylesheet" href="attachment.css" type="text/css" />
     <link rel="stylesheet" href="../../../../css/common.css" type="text/css" />
     <link rel="stylesheet" href="../../../../css/custom.css" type="text/css" />
</head>
<body>
<div class="wrapper">
        <div id="tabhead" class="tabhead">
            <span class="tab focus" data-content-id="upload">选择优惠信息</span>
        </div>
        <div id="tabbody" class="tabbody">
            <!-- 上传图片 -->
            <div id="upload" class="panel focus">
    			<div id="w" class="easyui-window" title="添加优惠" data-options="modal:true,closed:true,iconCls:'icon-save'" style="width:750px;padding:10px;">
					<div class="clearfix messadd formlist">
						<label style="line-height:35px;">关联车系<em>*</em></label>
						<div class="floatL glcx">
							<ul class="clearFix">
								<li>
									<input type="checkbox" id="1" onclick="getCar('1','全新爱丽舍');"/>
									<span>全新爱丽舍</span>
								</li>
								<li>
									<input type="checkbox" id="2" onclick="getCar('2','赛纳');"/>
									<span>赛纳</span>
								</li>
								<li>
									<input type="checkbox" id="3" onclick="getCar('3','毕加索');"/>
									<span>毕加索</span>
								</li>
								<li>
									<input type="checkbox" id="4" onclick="getCar('4','富康');"/>
									<span>富康</span>
								</li>
								<li>
									<input type="checkbox" checked="checked" />
									<span>凯旋</span>
								</li>
							</ul>
						</div>
					</div>
					<div class="detab">
						<table cellpadding="0" cellspacing="0" width="100%">
							<thead>
								<th width="10%">车系</th>
								<th width="20%">现金优惠</th>
								<th width="30%">裸车价</th>
								<th width="20%">礼包设置</th>
								<th width="10%">现车情况</th>
							</thead>
							<tbody id="t_table_tr">
								<tr>
									<td>凯旋</td>
									<td>
										<input class="searchselect" type="text" value="" style="width:100px;" />
										<span>万元</span>
									</td>
									<td>
										<div class="sale">
											<input type="radio" name="1" />
											<span>优惠</span>
										</div>
										<div class="sale">
											<p>
												<input type="radio" name="1" />
												<span>加价</span>
												<input class="searchselect" type="text" value="" style="width:100px;" />
												<span>万元</span>
											</p>
											<p>
											</p>
											<p>
												<span>含厂商补贴：</span>
												<input class="searchselect" type="text" value="" style="width:100px;" />
												<span>万元</span>
											</p>
											<p>
												<span>含政府补贴：</span>
												<input class="searchselect" type="text" value="" style="width:100px;" />
												<span>万元</span>
											</p>
											<p>
												<span>含惠民补贴：</span>
												<input class="searchselect" type="text" value="" style="width:100px;" />
												<span>万元</span>
											</p>
										</div>
									</td>
									<td>
										<input class="searchselect" type="text" value="" style="width:100px;" />
										<span>万元</span>
									</td>
									<td>
										<select class="searchselect" style="width: 85px;">
											<option>现车充足</option>
											<option>少量现车</option>
											<option>需要预定</option>
										</select>
									</td>
								</tr>
							</tbody>
						</table>
					</div>
				</div>
            </div>

            
        </div>
    </div>
    <script type="text/javascript" src="attachment.js"></script>
    <script type="text/javascript">
    	function getCar(id,name){
    		var ids=$('#ids').val();
    		if($('#'+id).attr('checked')){
	    		if(ids){
	    			ids=ids+","+id
	    		}else{
	    			ids=id;
	    		}
	    		var t_table=$('#t_table_tr');
	    		var str='<tr name="tr_name" id="tr_'+id+'">';
	    		str=str+'';
	    		str=str+'</tr>';
    		}else{
    			
    		}
    	}
    </script>
</body>
</html>