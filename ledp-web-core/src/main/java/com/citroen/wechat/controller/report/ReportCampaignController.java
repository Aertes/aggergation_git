package com.citroen.wechat.controller.report;

import com.citroen.ledp.domain.Organization;
import com.citroen.wechat.domain.Campaign;
import com.citroen.wechat.domain.CampaignLeads;
import com.citroen.wechat.domain.Plugin;
import com.citroen.wechat.domain.ReportObjectDetail;
import com.citroen.wechat.form.ReportCampaignQuery;
import com.citroen.wechat.service.IReportCampaignService;
import com.citroen.wechat.service.IReportPluginService;
import com.citroen.wechat.util.DateUtil;
import com.citroen.wechat.util.ExcelUtil;
import com.citroen.wechat.util.Pagination;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 活动报告控制器
 */
@Controller("reportCampaignController")
@RequestMapping("/wechat/reportCampaign")
public class ReportCampaignController {
    @Autowired
    private IReportCampaignService reportCampaignService;
    @Autowired
    private IReportPluginService reportPluginService;

    @RequestMapping(value = {"campaign"})
    @ResponseBody
    public Map campaign(ReportCampaignQuery query, HttpServletRequest request) throws Exception {
        setQueryParameter(query, null, null, null, null, null, null, null, null, request);
        Map map = new HashMap();
        // 获取表数据
        List<Campaign> campaignList = reportCampaignService.getCampaignList(query);
        map.put("list", campaignList);
        // 获取总数量
        int campaignCount = reportCampaignService.getCampaignCount(query);
        map.put("campaignCount", campaignCount);
        // 获取图数据
        Map<String, List> campaignChart = reportCampaignService.getCampaignChart(query);
        map.put("date", campaignChart.get("date"));
        map.put("campaigns", campaignChart.get("campaigns"));
        return map;
    }

    @RequestMapping(value = "getCompaignExport")
    public void getCompaignExport(Long cdealer, Long corg, Integer campaigneffect, Long cmcamp,
                                  ReportCampaignQuery query, HttpServletRequest request,
                                  HttpServletResponse response) throws Exception {
        setQueryParameter(query, cdealer, corg, cmcamp, campaigneffect, "cstartTime", "cendTime", null, null, request);
        Workbook workbook = reportCampaignService.exportCampaignExcel(query);
        ExcelUtil.exportExcelData("活动开展统计", response, request, workbook);
    }

    @RequestMapping(value = {"campaignLead"})
    @ResponseBody
    public Map campaignLead(ReportCampaignQuery query, HttpServletRequest request) throws Exception {
        setQueryParameter(query, null, null, null, null, null, null, null, null, request);
        Map map = new HashMap();
        // 取出留资信息
        Map<String, List> leadChart = reportCampaignService.getCampaignLeadChart(query);
        // 取出参与信息
        Map<String, List> recordChart = reportCampaignService.getCampaignRecordChart(query);
        int leadCount = reportCampaignService.getCampaignLeadCount(query);
        int recordCount = reportCampaignService.getCampaignRecordCount(query);
        map.put("date", leadChart.get("date"));
        map.put("leads", leadChart.get("leads"));
        map.put("records", recordChart.get("records"));
        map.put("leadCount", leadCount);
        map.put("recordCount", recordCount);
        // 取出表数据
        List<ReportObjectDetail> reportObjectDetails = reportCampaignService.getCampaignDetial(query);
        map.put("list", reportObjectDetails);
        return map;
    }

    @RequestMapping(value = "getLeadExport")
    public void getLeadExport(ReportCampaignQuery query, Integer campaigneLeadffect, Long lorg, Long ldealer, Long cmlead,
                              HttpServletRequest request, HttpServletResponse response) throws Exception {
        setQueryParameter(query, ldealer, lorg, cmlead, campaigneLeadffect, "lstartTime", "lendTime", null, null, request);
        Workbook workbook = reportCampaignService.exportCampaignLeadAndRecordExcel(query);
        ExcelUtil.exportExcelData("活动留资统计", response, request, workbook);
    }
    
    @RequestMapping(value = "getCampaignLeadExport")
    public void getCampaignLeadExport(ReportCampaignQuery query,
                              HttpServletRequest request, HttpServletResponse response) throws Exception {
    	if(StringUtils.isNotBlank(query.getName())){
    		try {
				query.setName(new String((query.getName()).getBytes("ISO-8859-1"),"UTF-8"));
			} catch (UnsupportedEncodingException e) {}
    	}else{
    		query.setName(null);
    	}
    	if(StringUtils.isBlank(query.getPhone())) {
    		query.setPhone(null);
    	}
        Workbook workbook = reportCampaignService.exportCampaignLeadDetail(query);
        ExcelUtil.exportExcelData("活动留资详情", response, request, workbook);
    }

    @RequestMapping(value = {"campaignPlugin"})
    @ResponseBody
    public Map campaignPlugin(ReportCampaignQuery query, HttpServletRequest request) throws Exception {
        setQueryParameter(query, null, null, null, null, null, null, null, null, request);
        Map map = new HashMap();
        // 取出留资信息
        Map<String, List> leadChart = reportPluginService.getPluginLeadChart(query);
        // 取出参与信息
        Map<String, List> recordChart = reportPluginService.getPluginRecordChart(query);
        int leadCount = reportPluginService.getPluginLeadCount(query);
        int recordCount = reportPluginService.getPluginRecordCount(query);
        map.put("date", leadChart.get("date"));
        map.put("leads", leadChart.get("leads"));
        map.put("records", recordChart.get("records"));
        map.put("leadCount", leadCount);
        map.put("recordCount", recordCount);
        // 取出表数据
        List<ReportObjectDetail> reportObjectDetails = reportPluginService.getPluginRecordDetial(query);
        map.put("list", reportObjectDetails);
        return map;
    }

    @RequestMapping(value = "getPluginExport")
    public void getPluginExport(ReportCampaignQuery query, Integer campaignePluginffect, Long porg, Long pdealer,
                                Long cmplugin, Long pluginType, Long pluginId,
                                HttpServletRequest request, HttpServletResponse response) throws Exception {
        setQueryParameter(query, pdealer, porg, cmplugin, campaignePluginffect, "pstartTime", "pendTime", pluginId, pluginType, request);
        Workbook workbook = reportPluginService.exportPluginRecordExcel(query);
        ExcelUtil.exportExcelData("活动插件统计", response, request, workbook);
    }

    @RequestMapping(value = {"getPlugin"})
    @ResponseBody
    public List<Plugin> getPlugin(Long pluginType) throws Exception {
        return reportPluginService.getPluginList(pluginType);
    }

    @RequestMapping(value = {"campaignLeadList"})
    @ResponseBody
    public List<CampaignLeads> campaignLeadList(Long campaign) {
        return reportCampaignService.getCampaignLeadList(campaign);
    }
    
    @RequestMapping(value = {"campaignLeadDetail"})
    @ResponseBody
    public Map<String, Object> campaignLeadDetail(ReportCampaignQuery query) {
    	int currentPage = 0;
    	if(query != null){
    		if(query.getPageSize() == 0){
    			query.setPageSize(10);
    		}
    		if(query.getCurrentPage() == 0){
    			query.setCurrentPage(0);
    		}else{
    			currentPage = query.getCurrentPage();
    			query.setCurrentPage((query.getCurrentPage()-1)*query.getPageSize());
    		}
    		if(StringUtils.isNotBlank(query.getName())){
    			try {
    				query.setName(new String((query.getName()).getBytes("ISO-8859-1"),"UTF-8"));
    			} catch (UnsupportedEncodingException e) {
    				e.printStackTrace();
    			}
    		}else{
    			query.setName(null);
    		}
    		
    		if(StringUtils.isBlank(query.getPhone())){
    			query.setPhone(null);
    		}
    	}
    	Map<String, Object> result = new HashMap<String, Object>();
        List<CampaignLeads> data = reportCampaignService.getCampaignLead(query);
        int total = reportCampaignService.getCampaignLeadCount(query);
		Pagination pagination=new Pagination(total,query.getPageSize(),currentPage);
		result.put("data", data);
		result.put("paginationData", pagination);
        return result;
    }


    private void setQueryParameter(ReportCampaignQuery query, Long dealerId, Long orgId, Long campaignId,
                                   Integer dateType, String startTime, String endTime, Long pluginId, Long pluginTypeId,
                                   HttpServletRequest request) {
        // 手动绑定参数
        if (dealerId != null) {
            query.setDealerId(dealerId);
        }
        if (orgId != null) {
            query.setOrgId(dealerId);
        }
        if (campaignId != null) {
            query.setCmpaignId(campaignId);
        }
        if (dateType != null) {
            query.setSearchDateType(dateType);
        }
        if (pluginId != null) {
            query.setPluginId(pluginId);
        }
        if (pluginTypeId != null) {
            query.setPluginTypeId(pluginTypeId);
        }
        // 判断大区信息
        if (query.getDealerId() == null && query.getOrgId() == null) {
            HttpSession session = request.getSession();
            Organization org = (Organization) session.getAttribute("loginOrg");
            // 此处需要排除超级管理员
            if (org != null && org.getId() != 1) {
                query.setOrgId(org.getId());
            }
        }
        Date beginDate = query.getSearchDateBegin();
        Date endDate = query.getSearchDateEnd();
        switch (query.getSearchDateType()) {
            case 1:
                //本周
                beginDate = DateUtil.getFirstDayOfThisWeek();
                endDate = DateUtil.getLastDayOfThisWeek();
                break;
            case 2:
                //本月
                beginDate = DateUtil.getFirstDayOfThisMonth();
                endDate = DateUtil.getLastDayOfThisMonth();
                break;
            case 3:
                //上个月
                beginDate = DateUtil.getFirstDayOfLastMonth();
                endDate = DateUtil.getLastDayOfLastMonth();
                break;
            case 4:
                //自定义
                if (startTime != null) {
                    beginDate = DateUtil.parse(request.getParameter(startTime), "yyyy-MM-dd HH:mm:ss");
                }
                if (endTime != null) {
                    endDate = DateUtil.parse(request.getParameter(endTime), "yyyy-MM-dd HH:mm:ss");
                }
                break;
            case 5:
                //上周
                beginDate = DateUtil.getFirstDayOfLastWeek();
                endDate = DateUtil.getLastDayOfLastWeek();
                break;
        }
        query.setSearchDateBegin(beginDate);
        query.setSearchDateEnd(endDate);
    }
}
