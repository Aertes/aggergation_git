package com.citroen.wechat.mapper;

import com.citroen.wechat.domain.Campaign;
import com.citroen.wechat.domain.CampaignLeads;
import com.citroen.wechat.form.ReportCampaignQuery;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Created by cyberoller on 2015/11/3.
 */
@Repository
public interface ReportCampaignMapper {
    List<Campaign> selectCampaignList(ReportCampaignQuery query);

    Integer selectCampaignChart(ReportCampaignQuery query);

    int selectCampaignCount(ReportCampaignQuery query);

    Integer selectCountLeadByCampaign(ReportCampaignQuery query);

    List<Map<String, Object>> selectCampaignLeadChart(ReportCampaignQuery query);

    int selectCampaignLeadCount(ReportCampaignQuery query);
    //分页查询
    List<CampaignLeads> selectCampaignLead(ReportCampaignQuery query);

    Integer selectCountRecordByCampaignt(ReportCampaignQuery query);

    List<Map<String, Object>> selectCampaignRecordChart(ReportCampaignQuery query);

    int selectCampaignRecordCount(ReportCampaignQuery query);

    List<CampaignLeads> selectCampaignLeadListByCampaignId(@Param(value="id")Long id);
}
