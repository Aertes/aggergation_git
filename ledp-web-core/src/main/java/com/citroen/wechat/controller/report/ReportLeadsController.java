package com.citroen.wechat.controller.report;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.citroen.ledp.dao.mybaits.MybaitsGenericDao;
import com.citroen.ledp.domain.Constant;
import com.citroen.ledp.domain.CrmLeads;
import com.citroen.ledp.domain.Dealer;
import com.citroen.ledp.domain.Leads;
import com.citroen.ledp.domain.LeadsMerged;
import com.citroen.ledp.domain.Media;
import com.citroen.ledp.domain.Organization;
import com.citroen.ledp.domain.User;
import com.citroen.ledp.exception.LedpException;
import com.citroen.ledp.interceptor.Permission;
import com.citroen.ledp.service.ConstantService;
import com.citroen.ledp.service.CrmLeadsService;
import com.citroen.ledp.service.DealerService;
import com.citroen.ledp.service.LeadsMergedService;
import com.citroen.ledp.service.LeadsService;
import com.citroen.ledp.service.MediaService;
import com.citroen.ledp.service.OrganizationService;
import com.citroen.ledp.service.PermissionService;
import com.citroen.ledp.util.DateUtil;
import com.citroen.ledp.util.ExcelUtil;
import com.citroen.ledp.util.JSONConverter;
import com.citroen.ledp.util.SysConstant;
import com.citroen.wechat.form.LeadsReportForm;
import com.citroen.wechat.service.PluginService;
import com.citroen.wechat.service.ReportCampaignService;
import com.citroen.wechat.util.Pagination;

/**
 * 留资报表管理
 *
 * @author 何海粟
 * @date2015年6月4日
 */
@Controller
@RequestMapping("/wechat/report/leads")
public class ReportLeadsController {
	
	private final static String dateFormat = "yyyy-MM-dd HH:mm";

    @Autowired
    private PermissionService permissionService;
    @Autowired
    private LeadsService leadsService;
    @Autowired
    private OrganizationService organizationService;
    @Autowired
    private MediaService mediaService;
    @Autowired
    private DealerService dealerService;
    @Autowired
    PluginService pluginService;
    @Autowired
    private MybaitsGenericDao<Long> mybaitsGenericDao;
    @Autowired
    private ReportCampaignService reportCampaignService;
    @Autowired
    private ConstantService constantService;
    @Autowired
	private LeadsMergedService leadsMergedService;
    @Autowired
	private CrmLeadsService crmLeadsService;

    @RequestMapping(value = {"", "index"})
    public ModelAndView index(Model model, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {
        ModelAndView view = new ModelAndView();
        view.setViewName("wechat/report/leads/index");

        // 媒体渠道
        Media mediaList = mediaService.getMedia(4L);
        model.addAttribute("mediaList", mediaList);

        List<Constant> constants = constantService.findAll("leads_type");
        model.addAttribute("constants", constants);

        //获取用户
        User loginUser = (User) request.getSession().getAttribute("loginUser");
        //获取组织机构
        Organization org = loginUser.getOrg();
        //网点
        Dealer dealer = loginUser.getDealer();
        if (null == org && null != dealer) { //网点用户
            model.addAttribute("dealer", dealer);
            model.addAttribute("orgs", new ArrayList<Organization>());
        } else {
            model.addAttribute("dealer", new Dealer());
            List<Organization> organizations = new ArrayList<Organization>();

            organizations.add(org); // 当前大区
            if (org.getLevel() == 1) { // 如果是总部
                organizations = organizationService.getChildren(org.getId());
            }

            model.addAttribute("orgs", organizations);
        }
        // 返回到界面
        return view;
    }

    @RequestMapping(value = {"search"})
    @ResponseBody
    public Map<String, Object> search(Model model, HttpServletRequest request) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        int pageSize = 10;
        int pageNumber = 1;
        String sortName = request.getParameter("sortName");
        String sortOrder = request.getParameter("sortOrder");
        if (StringUtils.isBlank(sortName)) {
            sortName = "ledpDealer.dateCreate";
        }
        if (StringUtils.isBlank(sortOrder)) {
            sortOrder = "desc";
        }

        try {
            pageSize = Integer.parseInt(request.getParameter("pageNumber"));
        } catch (Exception e) {
        }
        try {
            pageNumber = Integer.parseInt(request.getParameter("currentPage"));
        } catch (Exception e) {
        }
        Map<String, String[]> _m = request.getParameterMap();

        if (null != _m) {
            String beginDate = null, endDate = null, organizationLevel = null, organizationSelectedId = null;
            boolean flag0 = true, flag1 = true; // 标识部分字段是否需要赋值

            // 对查询参数进行处理，让其能调用之前方法
            for (Iterator<Map.Entry<String, String[]>> iterator = _m.entrySet().iterator(); iterator.hasNext(); ) {
                Map.Entry<String, String[]> entry = iterator.next();
                String key = entry.getKey();
                if (key.startsWith("parameter[")) {
                    key = key.substring(key.indexOf('[') + 1, key.indexOf(']'));
                    if (key.equals("beginDate")) {
                        if (flag1) {
                            beginDate = entry.getValue()[0];
                        }
                    } else if (key.equals("endDate")) {
                        if (flag1) {
                            endDate = entry.getValue()[0];
                        }
                    } else if (key.equals("effect")) {
                        String[] v = entry.getValue();
                        if ("0".equals(v[0])) {
                            beginDate = DateUtil.getFirstDay();
                            endDate = DateUtil.getLastDay();
                            flag1 = false;
                        } else if ("-1".equals(v[0])) {
                            Map<String, String> _0 = DateUtil.getFirstday_Lastday_Month();
                            beginDate = _0.get("first");
                            endDate = _0.get("last");
                            flag1 = false;
                        } else if ("01".equals(v[0])) {
                            endDate = DateUtil.format(DateUtil.getSundayOfThisWeek());
                            beginDate = DateUtil.format(DateUtil.getMondayOfThisWeek());
                            flag1 = false;
                        } else if ("2".equals(v[0])) {
                            beginDate = DateUtil.format(DateUtil.getMondayOfLastWeek());
                            endDate = DateUtil.format(DateUtil.getSundayOfLastWeek());
                            flag1 = false;
                        } else if ("99".equals(v[0])) {
                            /*beginDate = entry.getValue()[0];
                            endDate = entry.getValue()[0];*/
                        }
                    } else if (key.equals("organizationLevel")) {
                        if (flag0) {
                            if (request.getParameter("organizationSelectedId") != null) {
                                organizationLevel = entry.getValue()[0];
                            }
                        }
                    } else if (key.equals("organizationSelectedId")) {
                        if (flag0) {
                            organizationSelectedId = entry.getValue()[0];
                            if ("".equals(organizationSelectedId)) { // 总部
                                organizationLevel = "1";
                            } else {
                                organizationLevel = "2";
                            }
                        }
                    } else if (key.equals("ledpDealer")) {
                        String dealer = entry.getValue()[0];
                        if (!dealer.isEmpty()) {
                            organizationLevel = "3"; // 网点
                            organizationSelectedId = dealer;
                            flag0 = false;
                        }
                    } else {
                        params.put(key, new String((entry.getValue()[0]).getBytes("ISO-8859-1"), "UTF-8"));
                    }
                }
            }
            params.put("organizationLevel", organizationLevel);
            params.put("organizationSelectedId", organizationSelectedId);
            params.put("beginDate", beginDate);
            params.put("endDate", endDate);
        }

        params.put("sortOrder", sortOrder);
        params.put("sortName", sortName);
        params.put("pageNumber", pageNumber);
        params.put("pageSize", pageSize);
        params.put("ledpMediaId", "4");
        params.put("phone", request.getParameter("phone"));
        List<Leads> rows = leadsService.executeQuery(params);

        int total = leadsService.getTotalRow(params);

        Map<String, Object> result = new HashMap<String, Object>();
        result.put("leads", total);
        params.put("cmpandsite", "cmp");
        int cleads = leadsService.getTotalRow(params);
        result.put("cleads", cleads);
        params.put("cmpandsite", "site");
        int sleads = total-cleads;
        result.put("sleads", sleads);
        Pagination pagination = new Pagination(total, pageSize, pageNumber);
        Map<String, Object> pd = new HashMap<String, Object>();

        result.put("paginationData", pagination);

        final String contextPath = request.getContextPath();
        JSON data = JSONConverter.convertLeads(pageSize, pageNumber, sortName, sortOrder, total, cleads, sleads, rows,
                new String[]{
                        "name", "phone", "ledpType.name", "ledpMedia.name", "ledpDealer.name","ledpOrg.name",
                        "ledpIntent.name", "createTime", "ledpFollow.name", "campaign"
                },
                new JSONConverter.Operation<Leads>() {
                    public String operate(Leads leads) {
                        if (permissionService.hasAuth(SysConstant.PERMISSION_LEADS_DETAIL)) {
                            return "<a target='_blank' href='" + contextPath + "/wechat/report/leads/detail/" + leads.getId() + "' title='查看'><img alt='' src='" + contextPath + "/images/magnifier.png'></a>";
                        }
                        return "";
                    }
                }
        );
        result.put("data", data);

        return result;
    }
    
    @RequestMapping(value = {"searchLeads"})
    @ResponseBody
    public Map<String, Object> searchLeads(Model model, HttpServletRequest request) throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        int pageSize = 10;
        int pageNumber = 1;
        String sortName = request.getParameter("sortName");
        String sortOrder = request.getParameter("sortOrder");
        if (StringUtils.isBlank(sortName)) {
            sortName = "ledpDealer.dateCreate";
        }
        if (StringUtils.isBlank(sortOrder)) {
            sortOrder = "desc";
        }

        try {
            pageSize = Integer.parseInt(request.getParameter("pageNumber"));
        } catch (Exception e) {
        }
        try {
            pageNumber = Integer.parseInt(request.getParameter("currentPage"));
        } catch (Exception e) {
        }
        Map<String, String[]> _m = request.getParameterMap();

        if (null != _m) {
            String beginDate = null, endDate = null, organizationLevel = null, organizationSelectedId = null;
            boolean flag0 = true, flag1 = true; // 标识部分字段是否需要赋值

            // 对查询参数进行处理，让其能调用之前方法
            for (Iterator<Map.Entry<String, String[]>> iterator = _m.entrySet().iterator(); iterator.hasNext(); ) {
                Map.Entry<String, String[]> entry = iterator.next();
                String key = entry.getKey();
                if (key.startsWith("parameter[")) {
                    key = key.substring(key.indexOf('[') + 1, key.indexOf(']'));
                    if (key.equals("beginDate")) {
                        if (flag1) {
                            beginDate = entry.getValue()[0];
                        }
                    } else if (key.equals("endDate")) {
                        if (flag1) {
                            endDate = entry.getValue()[0];
                        }
                    } else if (key.equals("effect")) {
                        String[] v = entry.getValue();
                        if ("0".equals(v[0])) {
                            beginDate = DateUtil.getFirstDay();
                            endDate = DateUtil.getLastDay();
                            flag1 = false;
                        } else if ("-1".equals(v[0])) {
                            Map<String, String> _0 = DateUtil.getFirstday_Lastday_Month();
                            beginDate = _0.get("first");
                            endDate = _0.get("last");
                            flag1 = false;
                        } else if ("01".equals(v[0])) {
                            endDate = DateUtil.format(DateUtil.getSundayOfThisWeek());
                            beginDate = DateUtil.format(DateUtil.getMondayOfThisWeek());
                            flag1 = false;
                        } else if ("2".equals(v[0])) {
                            beginDate = DateUtil.format(DateUtil.getMondayOfLastWeek());
                            endDate = DateUtil.format(DateUtil.getSundayOfLastWeek());
                            flag1 = false;
                        } else if ("99".equals(v[0])) {
                            beginDate = entry.getValue()[0];
                            endDate = entry.getValue()[0];
                        }
                    } else if (key.equals("organizationLevel")) {
                        if (flag0) {
                            if (request.getParameter("organizationSelectedId") != null) {
                                organizationLevel = entry.getValue()[0];
                            }
                        }
                    } else if (key.equals("organizationSelectedId")) {
                        if (flag0) {
                            organizationSelectedId = entry.getValue()[0];
                            if ("".equals(organizationSelectedId)) { // 总部
                                organizationLevel = "1";
                            } else {
                                organizationLevel = "2";
                            }
                        }
                    } else if (key.equals("ledpDealer")) {
                        String dealer = entry.getValue()[0];
                        if (!dealer.isEmpty()) {
                            organizationLevel = "3"; // 网点
                            organizationSelectedId = dealer;
                            flag0 = false;
                        }
                    } else {
                        params.put(key, new String((entry.getValue()[0]).getBytes("ISO-8859-1"), "UTF-8"));
                    }
                }
            }
            params.put("organizationLevel", organizationLevel);
            params.put("organizationSelectedId", organizationSelectedId);
            params.put("beginDate", beginDate);
            params.put("endDate", endDate);
        }

        params.put("sortOrder", sortOrder);
        params.put("sortName", sortName);
        params.put("pageNumber", pageNumber);
        params.put("pageSize", pageSize);
        params.put("ledpMediaId", "4");
        params.put("phone", request.getParameter("phone"));
        List<Leads> rows = leadsService.executeQuery(params);

        int total = leadsService.getTotalRow(params);

        Map<String, Object> result = new HashMap<String, Object>();
        Pagination pagination = new Pagination(total, pageSize, pageNumber);
        Map<String, Object> pd = new HashMap<String, Object>();

        result.put("paginationData", pagination);

        result.put("data", rows);
        return result;
    }

    @RequestMapping(value = "/autocomplete")
    @ResponseBody
    public JSON autocomplete(HttpServletRequest request) {
        String id = request.getParameter("id");
        if (null != id && id.matches("^\\d+$")) {
            List<Map> list = dealerService.queryListByOrganization(Long.parseLong(id));
            return (JSON) JSON.toJSON(list);
        }

        return null;
    }

    /**
     * 导出留资报表
     *
     * @param response
     * @param request
     * @throws LedpException
     * @throws UnsupportedEncodingException
     */
    @RequestMapping("doExportLeads")
    public void doExportLeads(HttpServletResponse response, HttpServletRequest request) throws LedpException, UnsupportedEncodingException {
        Map<String, Object> params = new HashMap<String, Object>();
        String sortName = request.getParameter("sortName");
        String sortOrder = request.getParameter("sortOrder");
        if (StringUtils.isBlank(sortName)) {
            sortName = "ledpDealer.dateCreate";
        }
        if (StringUtils.isBlank(sortOrder)) {
            sortOrder = "desc";
        }

        Map<String, String[]> _m = request.getParameterMap();

        if (null != _m) {
            String beginDate = null, endDate = null, organizationLevel = null, organizationSelectedId = null;
            boolean flag0 = true, flag1 = true; // 标识部分字段是否需要赋值

            // 对查询参数进行处理，让其能调用之前方法
            for (Iterator<Map.Entry<String, String[]>> iterator = _m.entrySet().iterator(); iterator.hasNext(); ) {
                Map.Entry<String, String[]> entry = iterator.next();
                String key = entry.getKey();
                if ("beginDate".equals(key)) {
                    if (flag1) {
                        beginDate = entry.getValue()[0];
                    }
                } else if ("endDate".equals(key)) {
                    if (flag1) {
                        endDate = entry.getValue()[0];
                    }
                } else if ("effect".equals(key)) {
                    String[] v = entry.getValue();
                    if ("0".equals(v[0])) {
                        beginDate = DateUtil.getFirstDay();
                        endDate = DateUtil.getLastDay();
                        flag1 = false;
                    } else if ("-1".equals(v[0])) {
                        Map<String, String> _0 = DateUtil.getFirstday_Lastday_Month();
                        beginDate = _0.get("first");
                        endDate = _0.get("last");
                        flag1 = false;
                    } else if ("01".equals(v[0])) {
                        endDate = DateUtil.format(DateUtil.getSundayOfThisWeek());
                        beginDate = DateUtil.format(DateUtil.getMondayOfThisWeek());
                        flag1 = false;
                    } else if ("2".equals(v[0])) {
                        beginDate = DateUtil.format(DateUtil.getMondayOfLastWeek());
                        endDate = DateUtil.format(DateUtil.getSundayOfLastWeek());
                        flag1 = false;
                    } else if ("99".equals(v[0])) {
                        beginDate = entry.getValue()[0];
                        endDate = entry.getValue()[0];
                    }
                } else if ("organizationLevel".equals(key)) {
                    if (flag0) {
                        if (request.getParameter("organizationSelectedId") != null) {
                            organizationLevel = entry.getValue()[0];
                        }
                    }
                } else if ("organizationSelectedId".equals(key)) {
                    if (flag0) {
                        organizationSelectedId = entry.getValue()[0];
                        if ("".equals(organizationSelectedId)) { // 总部
                            organizationLevel = "1";
                        } else {
                            organizationLevel = "2";
                        }
                    }
                } else if ("ledpDealer".equals(key)) {
                    String dealer = entry.getValue()[0];
                    if (!dealer.isEmpty()) {
                        organizationLevel = "3"; // 网点
                        organizationSelectedId = dealer;
                        flag0 = false;
                    }
                } else {
                    params.put(key, new String((entry.getValue()[0]).getBytes("ISO-8859-1"), "UTF-8"));
                }
            }
            params.put("organizationLevel", organizationLevel);
            params.put("organizationSelectedId", organizationSelectedId);
            params.put("beginDate", beginDate);
            params.put("endDate", endDate);
        }

        params.put("sortOrder", sortOrder);
        params.put("sortName", sortName);
        List<Map> rows = leadsService.exportQuery(params);
        SXSSFWorkbook wb = leadsService.createWechatWorkbook(rows);
        ExcelUtil.exportExcelData("留资导出记录", response, request, wb);
    }
    
    @RequestMapping(value = "/detail/{id}")
	public String detail(Model model, @PathVariable long id,
					   HttpServletRequest request, HttpServletResponse response) throws Exception {
    	Leads leads = leadsService.get(id);
    	model.addAttribute("leads", leads);
		return "wechat/report/leads/detail";
	}
    
    //合并留资
    @RequestMapping(value = "/merged")
	public String merged(Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
    	// 媒体渠道
        Media media = mediaService.getMedia(4L);
        model.addAttribute("media", media);
		
        List<Constant> constants = constantService.findAll("leads_type");
        model.addAttribute("constants", constants);
		//获取用户
        User loginUser = (User) request.getSession().getAttribute("loginUser");
        //获取组织机构
        Organization org = loginUser.getOrg();
        //网点
        Dealer dealer = loginUser.getDealer();
        if (null == org && null != dealer) { //网点用户
            model.addAttribute("dealer", dealer);
            model.addAttribute("orgs", new ArrayList<Organization>());
        } else {
            model.addAttribute("dealer", new Dealer());
            List<Organization> organizations = new ArrayList<Organization>();

            organizations.add(org); // 当前大区
            if (org.getLevel() == 1) { // 如果是总部
                organizations = organizationService.getChildren(org.getId());
            }

            model.addAttribute("orgs", organizations);
        }
		return "wechat/report/leads/merged";
	}
    @RequestMapping(value = {"mergedSearch"})
    @ResponseBody
    public Map<String, Object> mergedSearch(Model model, HttpServletRequest request, LeadsReportForm form) throws Exception {
    	Map<String, Object> result = new HashMap<String, Object>();
    	form = getDateArea(form);
		List<Map> rows = leadsMergedService.executeQuery(form);
		int total = leadsMergedService.getTotalRow(form);
		Pagination pagination = new Pagination(total, form.getPageSize(), form.getCurrentPage());
        result.put("paginationData", pagination);
        result.put("data", rows);
		return result;
    }
    
    @RequestMapping(value = "/mergedDetail/{id}")
	public String mergedDetail(Model model, @PathVariable long id,
					   HttpServletRequest request, HttpServletResponse response) throws Exception {
    	LeadsMerged leadsMerged = leadsMergedService.get(id);
    	Dealer dealer = leadsMerged.getLedpDealer();
		if(dealer != null){
			dealer = dealerService.getDealerBydealerId(dealer.getId());
			leadsMerged.setLedpDealer(dealer);
		}
		model.addAttribute("leads", leadsMerged);
		return "wechat/report/leads/mergedDetail";
	}
    
    
    //数据回流
    @RequestMapping(value = "/crm")
	public String crm(Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
    	// 媒体渠道
        Media media = mediaService.getMedia(4L);
        model.addAttribute("media", media);
		
        List<Constant> constants = constantService.findAll("leads_type");
        model.addAttribute("constants", constants);
		//获取用户
        User loginUser = (User) request.getSession().getAttribute("loginUser");
        //获取组织机构
        Organization org = loginUser.getOrg();
        //网点
        Dealer dealer = loginUser.getDealer();
        if (null == org && null != dealer) { //网点用户
            model.addAttribute("dealer", dealer);
            model.addAttribute("orgs", new ArrayList<Organization>());
        } else {
            model.addAttribute("dealer", new Dealer());
            List<Organization> organizations = new ArrayList<Organization>();

            organizations.add(org); // 当前大区
            if (org.getLevel() == 1) { // 如果是总部
                organizations = organizationService.getChildren(org.getId());
            }

            model.addAttribute("orgs", organizations);
        }
		return "wechat/report/leads/crm";
	}
    @RequestMapping(value = {"crmSearch"})
    @ResponseBody
    public Map<String, Object> crmSearch(Model model, HttpServletRequest request, LeadsReportForm form) throws Exception {
    	Map<String, Object> result = new HashMap<String, Object>();
    	form = getDateArea(form);
		int total = crmLeadsService.getTotalRow(form);
		List<CrmLeads> rows = crmLeadsService.executeQuery(form);
		Pagination pagination = new Pagination(total, form.getPageSize(), form.getCurrentPage());
        result.put("paginationData", pagination);
        result.put("data", rows);
		return result;
    }
    
    @RequestMapping(value = "/crmDetail/{id}")
	public String crmDetail(Model model, @PathVariable long id) throws Exception {
    	CrmLeads leads = crmLeadsService.get(id);
		model.addAttribute("leads", leads);
		return "wechat/report/leads/crmDetail";
	}
    
    
    @RequestMapping(value = "/openImportForm")
	public String openImportForm() throws Exception {
		return "wechat/report/leads/import";
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
        	e.printStackTrace();
        	model.addAttribute("messages", "导入失败,成功"+(r-1)+"条数据。");
        	return "wechat/report/leads/crm";
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
		return "wechat/report/leads/crm";
	}
    
    private LeadsReportForm getDateArea(LeadsReportForm form){
    	int pageSize  = form.getPageSize();
		int pageNumber = form.getCurrentPage();
		if(pageSize == 0){
			form.setPageSize(10);
		}
		if(pageNumber == 0){
			form.setCurrentPage(1);
		}
		if(StringUtils.isBlank(form.getSortName())){
			form.setSortName("name");
		}
		if(StringUtils.isBlank(form.getSortOrder())){
			form.setSortOrder("asc");
		}
		if(form.getOrganizationSelectedId() == null){
			form.setOrganizationLevel(1);
		}
		
		switch(form.getEffect()){
		case 1://上周
			form.setBeginDate(DateUtil.getMondayOfLastWeek());
			form.setEndDate(DateUtil.getSundayOfLastWeek());
			break;
		case 2://本周
			form.setBeginDate(DateUtil.getMondayOfThisWeek());
			form.setEndDate(DateUtil.getSundayOfThisWeek());
			break;
		case 3://本月
			form.setBeginDate(com.citroen.wechat.api.util.DateUtil.convert(DateUtil.getFirstDay(), dateFormat));
			form.setEndDate(com.citroen.wechat.api.util.DateUtil.convert(DateUtil.getLastDay(), dateFormat));
			break;
		case 4://上月
			Map<String, String> dateMap = DateUtil.getFirstday_Lastday_Month();
			form.setBeginDate(com.citroen.wechat.api.util.DateUtil.convert(dateMap.get("first"), dateFormat));
			form.setEndDate(com.citroen.wechat.api.util.DateUtil.convert(dateMap.get("last"), dateFormat));
			break;
		case 5://自定义
			break;
		}
		return form;
	}
}
