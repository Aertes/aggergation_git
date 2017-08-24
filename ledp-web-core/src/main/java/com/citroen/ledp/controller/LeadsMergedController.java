package com.citroen.ledp.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.alibaba.fastjson.JSON;
import com.citroen.ledp.domain.Dealer;
import com.citroen.ledp.domain.LeadsMerged;
import com.citroen.ledp.domain.Media;
import com.citroen.ledp.domain.Organization;
import com.citroen.ledp.domain.User;
import com.citroen.ledp.exception.LedpException;
import com.citroen.ledp.interceptor.Permission;
import com.citroen.ledp.service.LeadsMergedService;
import com.citroen.ledp.service.MediaService;
import com.citroen.ledp.service.OrganizationService;
import com.citroen.ledp.service.PermissionService;
import com.citroen.ledp.util.JSONConverter;
import com.citroen.ledp.util.SysConstant;

/**
 * 合并后留资管理类
 * @author      miaoshuai
 * @email       miaoshuai@tocersfot.com
 * @company		www.tocersoft.com
 * @create-time 2015年3月18日 上午10:47:04
 * @version     1.0
 */
@Controller
@RequestMapping("/leadsMerged")
public class LeadsMergedController {
    private static Logger logger = Logger.getLogger(LeadsMergedController.class);
	private Map<String,Object> params;
	
	@Autowired
	private LeadsMergedService leadsMergedService;
	@Autowired
	private OrganizationService organizationService;
	@Autowired
	private MediaService mediaService;
	@Autowired
	private PermissionService permissionService;
	
	@RequestMapping(value={"","/index"})
	@Permission(code="leadsMerged/index")
	public String index(Model model,String parent,HttpServletRequest request) throws Exception {
		// 媒体渠道
		List<Media> mediaList = mediaService.listAll();
		model.addAttribute("mediaList",mediaList);
		
		model.addAttribute("parent",parent);
		model.addAttribute(params);
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
		return "leadsMerged/index";
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value={"search"})
	@ResponseBody
	@Permission(code="leadsMerged/index")
	public JSON search(Model model,HttpServletRequest request) throws Exception {
		

		int pageSize   = 10;
		int pageNumber = 1;
		String sortName  = request.getParameter("sortName");
		String sortOrder = request.getParameter("sortOrder");
		if(StringUtils.isBlank(sortName)){
			sortName = "name";
		}
		if(StringUtils.isBlank(sortOrder)){
			sortOrder = "asc";
		}
		try{
			pageSize = Integer.parseInt(request.getParameter("pageSize"));
		}catch(Exception e){}
		try{
			pageNumber = Integer.parseInt(request.getParameter("pageNumber"));
		}catch(Exception e){}
		params.put("sortOrder", sortOrder);
		params.put("sortName", sortName);
		final String contextPath = request.getContextPath();
		List<LeadsMerged> rows = leadsMergedService.executeQuery(params);
		int total = leadsMergedService.getTotalRow(params);
		JSON data = JSONConverter.convert(pageSize,pageNumber,sortName,sortOrder, total,rows,
				new String[] {
					"name","phone", "ledpMedia.name","ledpDealer.name",
					"ledpIntent.name","createTime","ledpFollow.name"
				},
				new JSONConverter.Operation<LeadsMerged>() {
					public String operate(LeadsMerged t) {
						if(permissionService.hasAuth(SysConstant.PERMISSION_LEADSMERGED_DETAIL)){
							return "<a href='"+contextPath+"/leadsMerged/detail/" + t.getId()
									+ "' title='查看'><img alt='' src='"+contextPath+"/images/magnifier.png'></a>";
						}
						return "";
					}
				});
		return data;
	}
	
	/**
	 * @Title: detail
	 * @Description: TODO(查看线索方法)
	 * @param model 参数传递容器
	 * @param id 线索ID
	 * @return String
	 * @throws LedpException 
	 */
	@RequestMapping(value="/detail/{id}")
	@Permission(code="leadsMerged/detail")
	public String detail(Model model,@PathVariable long id) throws LedpException {
		if(!permissionService.hasAuth(SysConstant.PERMISSION_LEADSMERGED_DETAIL)){
			return "redirect:/leadsMerged/index";
		}
		LeadsMerged leadsMerged = leadsMergedService.get(id);
		model.addAttribute("leadsMerged", leadsMerged);
		return "leadsMerged/detail";
	}
	
	/**
	 * 上传导入文件
	 * @return
	 */
	@RequestMapping(value="/uploadImportFile")
	@ResponseBody
	@Permission(code="leadsMerged/import")
	public String uploadImportFile(HttpServletRequest request){
		String uuid = UUID.randomUUID().toString().replaceAll("-", "");
		
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;  
        CommonsMultipartFile file = (CommonsMultipartFile) multipartRequest.getFile("file");//这里是表单的名字，在swfupload.js中this.ensureDefault("file_post_name", "filedata");
        
        String fileName = file.getOriginalFilename();
		String suffixName = StringUtils.substring(fileName,fileName.lastIndexOf("."),fileName.length());
		
		Map<String, Object> result = new HashMap<String, Object>();
		String destPath = System.getProperty("java.io.tmpdir") + File.separator + uuid + suffixName;
		File dest = new File(destPath);
		uploadFile(file, dest);
		
		//FileUtils.deleteQuietly(file);
		
		result.put("path", destPath);
		result.put("status", "success");
		return JSON.toJSONString(result);
	}
	
	/**
	 * 导入
	 * @param filePath
	 * @return
	 */
	@RequestMapping(value="/doImport")
	@Permission(code="leadsMerged/import")
	public String doImport(String filePath){
		leadsMergedService.importLeadsMerged(filePath);
		return "redirect:/leadsMerged/index";
	}
	
	/**
	 * 上传文件
	 * 
	 * @param src
	 *            源文件
	 * @param dst
	 *            目标文件
	 */
	private static void uploadFile(CommonsMultipartFile src, File dst) {
		try {
			InputStream in = null;
			OutputStream out = null;
			try {
				in = src.getInputStream();
				out = new BufferedOutputStream(new FileOutputStream(dst),2048000);
				byte[] buffer = new byte[2048000];
				int length = 0;
				while ((length = in.read(buffer)) != -1) {
					out.write(buffer, 0, length);
				}
				out.flush();
			} finally {
				IOUtils.closeQuietly(in);
				IOUtils.closeQuietly(out);
			}
		} catch (Exception e) {
			logger.error("异常信息：" + e.getMessage());
		}
	}

}
