package com.citroen.wechat.controller.report;


import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.citroen.ledp.domain.Dealer;
import com.citroen.ledp.domain.Organization;
import com.citroen.ledp.domain.User;
import com.citroen.ledp.service.DealerService;
import com.citroen.ledp.service.LeadsService;
import com.citroen.ledp.service.MediaService;
import com.citroen.ledp.service.OrganizationService;
import com.citroen.ledp.service.PermissionService;
import com.citroen.ledp.util.DateUtil;
import com.citroen.ledp.util.ExcelUtil;
import com.citroen.wechat.domain.Plugin;
import com.citroen.wechat.domain.PublicNo;
import com.citroen.wechat.form.PluginReportForm;
import com.citroen.wechat.service.PluginService;
import com.citroen.wechat.service.ReportPluginService;
import com.citroen.wechat.util.ConstantUtil;
import com.citroen.wechat.util.Pagination;

/**
 * 报表管理
 * @author 何海粟
 * @date2015年6月4日
 */
@Controller
@RequestMapping("/wechat/report/plugin")
public class ReportPluginController {
	
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
    ReportPluginService reportPluginService;
    
	@RequestMapping(value={"","index"})
	public ModelAndView index(Model model,HttpServletRequest request,HttpServletResponse response,HttpSession session) throws Exception {
		PublicNo publicNo = (PublicNo) request.getSession().getAttribute(ConstantUtil.SESSION_CURRENT_PUBLICNO);
		if(publicNo==null){
			return new ModelAndView("redirect:/wechat/publicno/message");
		}
		
		ModelAndView view = new ModelAndView();
		Map params = new HashMap();
		Map<String,Integer> paginateParams = new HashMap<String,Integer>();
		List<Plugin> plugins = pluginService.executeQuery(params, paginateParams);
		//获取用户
		User loginUser = (User) request.getSession().getAttribute("loginUser");
		//获取组织机构
		Organization org = loginUser.getOrg();
		//网点
		Dealer dealer = loginUser.getDealer();
		
		if(org != null && org.getLevel() == 1){//总部
			view.addObject("type",1);
			view.addObject("orgs",organizationService.getChildren(1L));
		}else if(org != null && org.getLevel() == 2){//大区
			view.addObject("type",2);
			view.addObject("orgs",org);
		}else{//网点
			view.addObject("type",3);
			view.addObject("dealer",dealer);
			view.addObject("orgs",org);
		}
		view.addObject("plugins",plugins);
		view.setViewName("wechat/report/plugin/index");
		// 返回到界面
		return view;
	}
	
	//查询汇总数据
	@RequestMapping(value="totals")
	public ModelAndView totals(Model model,HttpServletRequest request,HttpServletResponse response,HttpSession session, PluginReportForm form) throws Exception {
		ModelAndView view = new ModelAndView();
		form = getDateArea(form);
		Map<String,?> totals = reportPluginService.queryTotals(form);
		view.addObject("totals",totals);
		view.setViewName("wechat/report/plugin/plugin_totals");
		return view;
	}
	
	//查询活动数据
	@RequestMapping(value="campigns")
	public ModelAndView campigns(Model model, PluginReportForm form,HttpServletRequest request) throws Exception {
		ModelAndView view = new ModelAndView();
		List<Map<String,String>> campaigns = new ArrayList<Map<String,String>>();
		
		//获取用户
		User loginUser = (User) request.getSession().getAttribute("loginUser");
		//网点
		Dealer dealer = loginUser.getDealer();
		
		if(dealer != null){
			model.addAttribute("dealer", dealer);
		}
		Plugin plugin = null;
		if(form.getPluginId() != null){
			plugin = pluginService.get(form.getPluginId());
		}
		int totalRow = 0;
		int pageSize = 10;
		int currentPage = 1;
		
		if(form.getPageSize() > 0){
			pageSize = form.getPageSize();
		}
		if(form.getCurrentPage() <= 0){
			form.setCurrentPage(1);
		}else{
			currentPage = form.getCurrentPage();
		}
		
		form = getDateArea(form);
		
		if(plugin != null){
			if("rotatyTable".equals(plugin.getCode())){
				/*********大转盘***********/
				totalRow  = reportPluginService.dzpTotalRow(form);
				campaigns = reportPluginService.dzpCampaigns(form);
			}
			else if("purchase".equals(plugin.getCode())){
				/*********惠团购***********/
				totalRow  = reportPluginService.htgTotalRow(form);
				campaigns = reportPluginService.htgCampaigns(form);
			}
			else if("coupon".equals(plugin.getCode())){
				/*********优惠券***********/
				totalRow  = reportPluginService.yhqTotalRow(form);
				campaigns = reportPluginService.yhqCampaigns(form);
			}
			else if("share".equals(plugin.getCode())){
				/*********分享有礼***********/
				totalRow  = reportPluginService.fxylTotalRow(form);
				campaigns = reportPluginService.fxylCampaigns(form);
			}
			else if("answer".equals(plugin.getCode())){
				/*********答题闯关***********/
				totalRow  = reportPluginService.dtcgTotalRow(form);
				campaigns = reportPluginService.dtcgCampaigns(form);
			}
		}else{
			//全部
			totalRow  = reportPluginService.getTotalRow(form);
			campaigns = reportPluginService.getCampaigns(form);
		}
		
		Pagination pagination=new Pagination(totalRow,pageSize,currentPage);
		view.addObject("pagination", pagination);
		view.addObject("plugin", plugin);
		view.addObject("campaigns", campaigns);
		view.setViewName("wechat/report/plugin/plugin_campign");
		return view;
	}
	
	//导出活动数据
	@RequestMapping(value="export")
	public void export(Model model,HttpServletRequest request, HttpServletResponse response, PluginReportForm form) throws Exception {
		Plugin plugin = null;
		if(form.getPluginId() != null){
			plugin = pluginService.get(form.getPluginId());
		}
		form = getDateArea(form);
		
		if(plugin != null){
			if("rotatyTable".equals(plugin.getCode())){
				/*********大转盘***********/
				SXSSFWorkbook wb = reportPluginService.dzpExportCampaigns(form);
				ExcelUtil.exportExcelData("插件统计详情",response,request,wb);
			}
			else if("purchase".equals(plugin.getCode())){
				/*********惠团购***********/
				SXSSFWorkbook wb = reportPluginService.htgExportCampaigns(form);
				ExcelUtil.exportExcelData("插件统计详情",response,request,wb);
			}
			else if("coupon".equals(plugin.getCode())){
				/*********优惠券***********/
				SXSSFWorkbook wb = reportPluginService.yhqExportCampaigns(form);
				ExcelUtil.exportExcelData("插件统计详情",response,request,wb);
			}
			else if("share".equals(plugin.getCode())){
				/*********分享有礼***********/
				SXSSFWorkbook wb = reportPluginService.fxylExportCampaigns(form);
				ExcelUtil.exportExcelData("插件统计详情",response,request,wb);
			}
			else if("answer".equals(plugin.getCode())){
				/*********答题闯关***********/
				SXSSFWorkbook wb = reportPluginService.dtcgExportCampaigns(form);
				ExcelUtil.exportExcelData("插件统计详情",response,request,wb);
			}
		}else{
			/*********全部插件***********/
			SXSSFWorkbook wb = reportPluginService.exportAllCampaigns(form);
			ExcelUtil.exportExcelData("插件统计详情",response,request,wb);
		}
	}
	
	//导出留资数据
	@RequestMapping(value="exportLeads")
	public void exportLeads(Model model,HttpServletRequest request, HttpServletResponse response, PluginReportForm form) throws Exception {
		Plugin plugin = null;
		if(form.getPluginId() != null){
			plugin = pluginService.get(form.getPluginId());
		}
		form = getDateArea(form);
		if(StringUtils.isNotBlank(form.getCustomName())){
			try {
				form.setCustomName(new String(form.getCustomName().getBytes("ISO-8859-1"), "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				
			}
		}
		if(plugin != null){
			if("rotatyTable".equals(plugin.getCode())){
				/*********大转盘***********/
				SXSSFWorkbook wb = reportPluginService.dzpExportLeads(form);
				ExcelUtil.exportExcelData("插件留资记录",response,request,wb);
			}
			else if("purchase".equals(plugin.getCode())){
				/*********惠团购***********/
				SXSSFWorkbook wb = reportPluginService.htgExportLeads(form);
				ExcelUtil.exportExcelData("插件统计详情",response,request,wb);
			}
			else if("coupon".equals(plugin.getCode())){
				/*********优惠券***********/
				SXSSFWorkbook wb = reportPluginService.yhqExportLeads(form);
				ExcelUtil.exportExcelData("插件留资详情",response,request,wb);
			}
			else if("share".equals(plugin.getCode())){
				/*********分享有礼***********/
				SXSSFWorkbook wb = reportPluginService.fxylExportLeads(form);
				ExcelUtil.exportExcelData("插件留资详情",response,request,wb);
			}
			else if("answer".equals(plugin.getCode())){
				/*********答题闯关***********/
				SXSSFWorkbook wb = reportPluginService.dtcgExportLeads(form);
				ExcelUtil.exportExcelData("插件留资详情",response,request,wb);
			}
		}
	}
	
	//查询线索数据
	@RequestMapping(value="leads")
	@ResponseBody
	public ModelAndView leads(Model model, HttpServletRequest request, HttpServletResponse response, PluginReportForm form) throws Exception {
		ModelAndView view = new ModelAndView();
		List<Map<String,String>> leadsList = new ArrayList<Map<String,String>>();
		int totalRow = 0;
		Plugin plugin = null;
		int pageSize = 10;
		int pageNumber = 0;
		if(form.getPageSize() <= 0){
			form.setPageSize(pageSize);
		}
		if(form.getCurrentPage() <= 0){
			form.setCurrentPage(1);
		}else{
			pageNumber = (form.getCurrentPage()-1)*form.getPageSize();
		}
		form = getDateArea(form);
		totalRow = reportPluginService.leadsTotalRow(form);
		leadsList = reportPluginService.queryLeads(form);
		Pagination pagination=new Pagination(totalRow,form.getPageSize(),form.getCurrentPage());
		view.addObject("pagination", pagination);
		view.addObject("plugin", plugin);
		view.addObject("leadsList", leadsList);
		view.addObject("form", form);
		view.setViewName("wechat/report/plugin/plugin_leads");
		return view;
	}
	
	private PluginReportForm getDateArea(PluginReportForm form){
		switch(form.getEffect()){
		case 1://上周
			form.setStartTime(DateUtil.getMondayOfLastWeek());
			form.setEndTime(DateUtil.getSundayOfLastWeek());
			break;
		case 2://本周
			form.setStartTime(DateUtil.getMondayOfThisWeek());
			form.setEndTime(DateUtil.getSundayOfThisWeek());
			break;
		case 3://本月
			form.setStartTime(com.citroen.wechat.api.util.DateUtil.convert(DateUtil.getFirstDay(), dateFormat));
			form.setEndTime(com.citroen.wechat.api.util.DateUtil.convert(DateUtil.getLastDay(), dateFormat));
			break;
		case 4://上月
			Map<String, String> dateMap = DateUtil.getFirstday_Lastday_Month();
			form.setStartTime(com.citroen.wechat.api.util.DateUtil.convert(dateMap.get("first"), dateFormat));
			form.setEndTime(com.citroen.wechat.api.util.DateUtil.convert(dateMap.get("last"), dateFormat));
			break;
		case 5://自定义
			break;
		}
		return form;
	}
	
}
