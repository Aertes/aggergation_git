package com.citroen.wechat.service;

import com.citroen.wechat.domain.Plugin;
import com.citroen.wechat.domain.ReportObjectDetail;
import com.citroen.wechat.form.ReportCampaignQuery;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.List;
import java.util.Map;

/**
 * Created by cyberoller on 2015/11/3.
 */
public interface IReportPluginService {
    Map<String, List> getPluginRecordChart(ReportCampaignQuery query);

    int getPluginRecordCount(ReportCampaignQuery query);

    Map<String, List> getPluginLeadChart(ReportCampaignQuery query);

    int getPluginLeadCount(ReportCampaignQuery query);

    List<ReportObjectDetail> getPluginRecordDetial(ReportCampaignQuery query);

    Workbook exportPluginRecordExcel(ReportCampaignQuery query);

    List<Plugin> getPluginList(Long id);
}
