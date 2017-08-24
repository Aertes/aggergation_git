package com.citroen.wechat.service;

import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import com.citroen.wechat.domain.Campaign;
import com.citroen.wechat.domain.CampaignLeads;
import com.citroen.wechat.domain.ReportObjectDetail;
import com.citroen.wechat.form.ReportCampaignQuery;

/**
 * Created by cyberoller on 2015/11/3.
 */
public interface IReportCampaignService {
    List<Campaign> getCampaignList(ReportCampaignQuery query);

    Map<String, List> getCampaignChart(ReportCampaignQuery query);

    int getCampaignCount(ReportCampaignQuery query);

    Workbook exportCampaignExcel(ReportCampaignQuery query);

    Map<String, List> getCampaignLeadChart(ReportCampaignQuery query);

    List<ReportObjectDetail> getCampaignDetial(ReportCampaignQuery query);

    int getCampaignLeadCount(ReportCampaignQuery query);
    
    List<CampaignLeads> getCampaignLead(ReportCampaignQuery query);

    Map<String, List> getCampaignRecordChart(ReportCampaignQuery query);

    int getCampaignRecordCount(ReportCampaignQuery query);

    Workbook exportCampaignLeadAndRecordExcel(ReportCampaignQuery query);
    
    SXSSFWorkbook exportCampaignLeadDetail(ReportCampaignQuery query) throws Exception;

    List<CampaignLeads> getCampaignLeadList(Long id);
    
}
