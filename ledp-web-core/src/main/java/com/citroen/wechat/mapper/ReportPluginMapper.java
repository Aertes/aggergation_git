package com.citroen.wechat.mapper;

import com.citroen.wechat.domain.Plugin;
import com.citroen.wechat.domain.PluginRecord;
import com.citroen.wechat.form.ReportCampaignQuery;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Created by cyberoller on 2015/11/6.
 */
@Repository
public interface ReportPluginMapper {
    List<PluginRecord> selectPluginRecordList(ReportCampaignQuery query);

    List<Map<String, Object>> selectPluginLeadChart(ReportCampaignQuery query);

    int selectPluginLeadCount(ReportCampaignQuery query);

    List<Map<String, Object>> selectPluginRecordChart(ReportCampaignQuery query);

    int selectPluginRecordCount(ReportCampaignQuery query);

    List<Plugin> selectPluginList(Long id);
}
