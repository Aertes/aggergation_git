package com.citroen.ledp.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.alibaba.fastjson.JSON;
import com.citroen.ledp.domain.ApiLog;
import com.citroen.ledp.domain.CrmLeads;
import com.citroen.ledp.domain.Dealer;
import com.citroen.ledp.domain.Organization;
import com.citroen.ledp.domain.User;
import com.citroen.ledp.exception.LedpException;
import com.citroen.ledp.interceptor.Permission;
import com.citroen.ledp.service.CrmLeadsService;
import com.citroen.ledp.service.PermissionService;
import com.citroen.ledp.service.RegionService;
import com.citroen.ledp.util.JSONConverter;
import com.citroen.ledp.util.SysConstant;

/**
 * @Title: CustomerController.java
 * @Package com.citroen.ledp.controller
 * @Description: TODO(客户管理类)
 * @author 廖启洪
 * @date 2015年1月29日 下午3:24:24
 * @version V1.0
 */
@Controller
@RequestMapping("/crm/leads")
public class CrmLeadsController {
    private static Logger logger = Logger.getLogger(CrmLeadsController.class);
	private Map<String,Object> params;
	@Autowired
	private CrmLeadsService crmLeadsService;
	@Autowired
	private RegionService regionService;
	@Autowired
	private PermissionService permissionService;
	
	@RequestMapping(value={"","/index"})
	@Permission(code="crm/leads/index")
	public String index(Model model,HttpServletRequest request) throws Exception {
		// 返回到界面
		//获取用户
		User loginUser = (User) request.getSession().getAttribute("loginUser");
		//获取组织机构
		Organization org = loginUser.getOrg();
		//网点
		Dealer dealer = loginUser.getDealer();
		if(null == org && null!= dealer){ //网点用户
			model.addAttribute("dealer","true");
		}else{
			model.addAttribute("dealer","false");
		}
		return "crm/leads/index";
	}

	/**
	 * 查询
	 * @param model
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value={"search"})
	@ResponseBody
	@Permission(code="crm/leads/index")
	public JSON search(Model model,HttpServletRequest request) throws Exception {
		if(params.get("organizationSelectedId")!=null){
			if(params.get("organizationSelectedId").toString().equals("headquarters")){
				params.put("H", "");
				params.put("J", "");
			}else if(params.get("organizationSelectedId").toString().equals("largeArea")){
				params.put("J", "");
			}else if(params.get("organizationSelectedId").toString().equals("dealer")){
				params.put("H", "");
			}
		}
		int total = crmLeadsService.getTotalRow(params);
		final String contextPath = request.getContextPath();

		int pageSize   = 10;
		int pageNumber = 1;
		String sortName  = request.getParameter("sortName");
		String sortOrder = request.getParameter("sortOrder");
		if(StringUtils.isBlank(sortName)){
			sortName = "batch";
		}
		if(StringUtils.isBlank(sortOrder)){
			sortOrder = "asc";
		}
		params.put("sortName", sortName);
		params.put("sortOrder", sortOrder);
		List<CrmLeads> rows = crmLeadsService.executeQuery(params);
		try{
			pageSize = Integer.parseInt(request.getParameter("pageSize"));
		}catch(Exception e){}
		try{
			pageNumber = Integer.parseInt(request.getParameter("pageNumber"));
		}catch(Exception e){}
		
		JSON data = JSONConverter.convert(pageSize,pageNumber,sortName,sortOrder, total,rows,
				new String[] {
					"D", "S", "E","J","batch[yyyy-MM-dd HH:mm:ss]","Y"
				},
				new JSONConverter.Operation<CrmLeads>() {
					public String operate(CrmLeads t) {
						if(permissionService.hasAuth(SysConstant.PERMISSION_CRM_DETAIL)){
							return "<a class='ielookDetails' href='"+contextPath+"/crm/leads/detail/" + t.getId()
									+ "' title='查看'><img alt='' src='"+contextPath+"/images/magnifier.png'></a>";
						}
						return "";
					}
				}, new JSONConverter.Operation<CrmLeads>() {
					public String operate(CrmLeads t) {
						return "";
					}
				});
		return data;
	}
	
	/**
	 * @Title: update
	 * @Description: TODO(修改客户入口)
	 * @param model 参数传递容器
	 * @param id 客户ID
	 * @return String
	 * @throws LedpException 
	 */
	@RequestMapping(value="upload")
	@Permission(code="crm/leads/upload")
	public String upload(Model model,@PathVariable long id) throws LedpException {
		// 根据客户ID查询
		CrmLeads leads = crmLeadsService.get(id);
		model.addAttribute("customer", leads);
		return "crm/leads/update";
	}
	
	/**
	 * 上传文件
	 * @return
	 * @throws LedpException 
	 */
	@RequestMapping(value="/doUpload")
	@Permission(code="crm/leads/upload")
	public void doUpload(HttpServletResponse response,MultipartHttpServletRequest request) throws LedpException{
        CommonsMultipartFile file = (CommonsMultipartFile) request.getFile("file");//这里是表单的名字，在swfupload.js中this.ensureDefault("file_post_name", "filedata");
        String fileName = file.getOriginalFilename();
		
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("status", "success");
	}
	/**
	 * @Title: detail
	 * @Description: TODO(查看客户方法)
	 * @param model 参数传递容器
	 * @param id 客户ID
	 * @return String
	 * @throws LedpException 
	 */
	@RequestMapping(value="/detail/{id}")
	@Permission(code="crm/leads/detail")
	public String detail(Model model,@PathVariable long id) throws LedpException {
		CrmLeads leads = crmLeadsService.get(id);
		model.addAttribute("leads", leads);
		return "crm/leads/detail";
	}
	
	
	/**
	 * 数据导入
	 */
	@RequestMapping(value="/importXls")
	@Permission(code="crm/leads/upload")
	public String importXls(Model model,HttpServletResponse response,MultipartHttpServletRequest request) throws LedpException{
		User loginUser = (User) request.getSession().getAttribute("loginUser");
		//获取组织机构
		Organization org = loginUser.getOrg();
		//网点
		Dealer dealer = loginUser.getDealer();
		if(null == org && null!= dealer){ //网点用户
			model.addAttribute("dealer","true");
		}else{
			model.addAttribute("dealer","false");
		}
		final Date date=new Date();//批次时间
		MultipartFile file =request.getFile("file");
		// 根据上述创建的输入流 创建工作簿对象
        BufferedReader br=null;
        int r=0;
        try { 
        	InputStreamReader isr = new InputStreamReader(file.getInputStream(),"GBK");
        	br = new BufferedReader(isr);
        	Pattern pattern = Pattern.compile("(,)?((\"[^\"]*(\"{2})*[^\"]*\")*[^,]*)");  
            String line = ""; 
            String[] en={"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"}; 
            while ((line = br.readLine()) != null) {
            	String[] s=line.split(",");
            	if(s.length>0){
            	if(r!=0){
	            	Matcher matcher = pattern.matcher(line);
	            	CrmLeads leads=new CrmLeads();
	            	leads.setBatch(date);
	            	int i=0;
	            	while(matcher.find()) {  
	                    String cell = matcher.group(2);//group(2) is ((\"[^\"]*(\"{2})*[^\"]*\")*[^,]*)  
	                    Pattern pattern2 = Pattern.compile("\"((.)*)\"");  
	                    Matcher matcher2 = pattern2.matcher(cell);  
	                    if(matcher2.find()) {  
	                        cell = matcher2.group(1);  
	                    } 
	                    //通过反射去set属性值
	                    Method method = null;
	                    Object param = null;
	                    method = CrmLeads.class.getMethod("set" + en[i], new Class[] { String.class });
	                    param = cell;//每一列的数据
	                    method.invoke(leads, new Object[] { param }); 
	                    i++;
	            	} 
	            	//拿到一行数据进行添加操作
	            	if(!"".equals(leads.getI())){
		            	List<Dealer> dealers=crmLeadsService.queryByCondition(leads.getI());
		            	if(!dealers.isEmpty()){
			            	leads.setDealer(dealers.get(0));
			            	leads.setOrganization(dealers.get(0).getOrganization());
			            	crmLeadsService.saves(leads);
		            	}
	            	}
            	}
            	r++;
            	}
            }
            model.addAttribute("messages", "导入成功,成功"+(r-1)+"条数据。");
           
        }catch (Exception e) {
        	logger.error("异常信息：" + e.getMessage());
        	model.addAttribute("messages", "导入失败,成功"+(r-1)+"条数据。");
        	return "crm/leads/index";
        }finally{
            if(br!=null){
                try {
                    br.close();
                    br=null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        new Thread(){
        	public void run(){
        		 //导入成功以后执行同步状态方法
            	crmLeadsService.merges(date);
        	}
        	
        }.start();
		return "crm/leads/index";
	}
	
	@RequestMapping(value={"ftp"})
	public String log(Model model,HttpServletRequest request) throws Exception {
		return "crm/ftp/index";
	}

	@RequestMapping(value={"ftpLog"})
	@ResponseBody
	public JSON logSearch(Model model,HttpServletRequest request) throws Exception {
		int total = crmLeadsService.getApiLogTotal(params);
		int pageSize   = 10;
		int pageNumber = 1;
		String sortName  = request.getParameter("sortName");
		String sortOrder = request.getParameter("sortOrder");
		if(StringUtils.isBlank(sortName)){
			sortName = "date";
		}
		if(StringUtils.isBlank(sortOrder)){
			sortOrder = "desc";
		}
		params.put("sortName", sortName);
		params.put("sortOrder", sortOrder);
		List<ApiLog> rows = crmLeadsService.executeApiLogQuery(params);
		try{
			pageSize = Integer.parseInt(request.getParameter("pageSize"));
		}catch(Exception e){}
		try{
			pageNumber = Integer.parseInt(request.getParameter("pageNumber"));
		}catch(Exception e){}
		
		JSON data = JSONConverter.convert(pageSize,pageNumber,sortName,sortOrder, total,rows,new String[] {"api", "total", "serial","state","date[yyyy-MM-dd HH:mm:ss]","comment","records"});
		return data;
	}
}
