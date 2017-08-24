package com.citroen.wechat.service.impl;

import com.citroen.wechat.domain.Plugin;
import com.citroen.wechat.domain.PluginRecord;
import com.citroen.wechat.domain.ReportObjectDetail;
import com.citroen.wechat.mapper.ReportPluginMapper;
import com.citroen.wechat.form.ReportCampaignQuery;
import com.citroen.wechat.service.IReportPluginService;
import com.citroen.wechat.util.DateUtil;
import com.citroen.wechat.util.ExcelUtil;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by cyberoller on 2015/11/9.
 */
@Service
public class ReportPluginServiceImpl implements IReportPluginService {
    private static Logger logger = Logger.getLogger(ReportPluginServiceImpl.class);

    @Resource
    private ReportPluginMapper reportPluginMapper;

    
    public Map<String, List> getPluginRecordChart(ReportCampaignQuery query) {
        // 获取查询结果
        List<Map<String, Object>> mapList = reportPluginMapper.selectPluginRecordChart(query);
        // 将结果按列放入list
        List<String> date = new ArrayList<String>();
        List<Object> records = new ArrayList<Object>();
        // 先根据时间初始化MAP
        Date beginDate = query.getSearchDateBegin();
        Date endDate = query.getSearchDateEnd();
        Map<String, Integer> cmap = new TreeMap<String, Integer>();
        while (beginDate.before(endDate)) {
            String day = DateUtil.format(beginDate, "yyyy-MM-dd");
            cmap.put(day, 0);
            beginDate = DateUtil.getAfterDay(beginDate);
        }
        for (Map<String, Object> objectMap : mapList) {
            String day = objectMap.get("day").toString();
            int count = Integer.parseInt(objectMap.get("ct").toString());
            cmap.put(day, count);
        }
        for (String key : cmap.keySet()) {
            date.add(key);
            records.add(cmap.get(key));
        }
        // 再将list放入map返回
        Map<String, List> resultMap = new HashMap<String, List>();
        resultMap.put("date", date);
        resultMap.put("records", records);
        return resultMap;
    }

    
    public int getPluginRecordCount(ReportCampaignQuery query) {
        return reportPluginMapper.selectPluginRecordCount(query);
    }

    
    public Map<String, List> getPluginLeadChart(ReportCampaignQuery query) {
        // 获取查询结果
        List<Map<String, Object>> mapList = reportPluginMapper.selectPluginLeadChart(query);
        // 将结果按列放入list
        List<String> date = new ArrayList<String>();
        List<Object> leads = new ArrayList<Object>();
        // 先根据时间初始化MAP
        Date beginDate = query.getSearchDateBegin();
        Date endDate = query.getSearchDateEnd();
        Map<String, Integer> cmap = new TreeMap<String, Integer>();
        while (beginDate.before(endDate)) {
            String day = DateUtil.format(beginDate, "yyyy-MM-dd");
            cmap.put(day, 0);
            beginDate = DateUtil.getAfterDay(beginDate);
        }
        for (Map<String, Object> objectMap : mapList) {
            String day = objectMap.get("day").toString();
            int count = Integer.parseInt(objectMap.get("ct").toString());
            cmap.put(day, count);
        }
        for (String key : cmap.keySet()) {
            date.add(key);
            leads.add(cmap.get(key));
        }
        // 再将list放入map返回
        Map<String, List> resultMap = new HashMap<String, List>();
        resultMap.put("date", date);
        resultMap.put("leads", leads);
        return resultMap;
    }

    
    public int getPluginLeadCount(ReportCampaignQuery query) {
        return reportPluginMapper.selectPluginLeadCount(query);
    }

    
    public List<ReportObjectDetail> getPluginRecordDetial(ReportCampaignQuery query) {
        List<ReportObjectDetail> objectDetails = new ArrayList<ReportObjectDetail>();
        List<PluginRecord> pluginRecords = reportPluginMapper.selectPluginRecordList(query);
        for (PluginRecord pluginRecord : pluginRecords) {
            ReportObjectDetail objectDetail = new ReportObjectDetail();
            List<Integer> count = new ArrayList<Integer>();
            query.setCmpaignId(pluginRecord.getCampaign().getId());
            query.setPluginId(pluginRecord.getPlugin().getId());
            //query.setPluginRecordId(pluginRecord.getId());
            // 获取活动参与人数和留资人数
            Integer countRecord = reportPluginMapper.selectPluginRecordCount(query);
            Integer countLead = reportPluginMapper.selectPluginLeadCount(query);
            count.add(countRecord == null ? 0 : countRecord);
            count.add(countLead == null ? 0 : countLead);
            objectDetail.setCount(count);
            objectDetail.setObject(pluginRecord);
            objectDetails.add(objectDetail);
        }
        return objectDetails;
    }

    
    public Workbook exportPluginRecordExcel(ReportCampaignQuery query) {
        int rowaccess = 100;    // 内存中缓存记录行数
        // 创建工作簿
        // 内存中限制行数为100，当行号到达101时，行号为0的记录刷新到硬盘并从内存中删除，
        // 当行号到达102时，行号为1的记录刷新到硬盘，并从内存中删除，以此类推。
        SXSSFWorkbook workbook = new SXSSFWorkbook(rowaccess);
        // 创建工作表
        Sheet sheet = workbook.createSheet("活动留资统计");
        List<String> headTitle = new ArrayList<String>();
        headTitle.add("开始时间");
        headTitle.add("大区");
        headTitle.add("网点名称");
        headTitle.add("开展活动名称");
        headTitle.add("插件名称");
        headTitle.add("活动参与人数");
        headTitle.add("活动留资数");
        // 创建表头
        Row headRow = ExcelUtil.createHeadRow(workbook, sheet, headTitle);
        Cell cell;
        // 设置单元格的格式
        CellStyle bodyStyle = ExcelUtil.getBodyStyle(workbook);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<PluginRecord> pluginRecords = reportPluginMapper.selectPluginRecordList(query);
        for (int i = 0; i < pluginRecords.size(); i++) {
            //query.setPluginRecordId(pluginRecords.get(i).getId());

            query.setCmpaignId(pluginRecords.get(i).getCampaign().getId());
            query.setPluginId(pluginRecords.get(i).getPlugin().getId());
            Integer countRecord = reportPluginMapper.selectPluginRecordCount(query);
            Integer countLead = reportPluginMapper.selectPluginLeadCount(query);
            Row bodyRow;
            // 第一次查询，有表头
            // 时间紧急，未过滤异常
            bodyRow = sheet.createRow(i + 1);
            PluginRecord pluginRecord = pluginRecords.get(i);

            cell = bodyRow.createCell(0);
            cell.setCellStyle(bodyStyle);
            cell.setCellValue(sdf.format(pluginRecord.getCreateDate()));

            cell = bodyRow.createCell(1);
            cell.setCellStyle(bodyStyle);
            cell.setCellValue(pluginRecord.getDealer().getOrganization().getName());

            cell = bodyRow.createCell(2);
            cell.setCellStyle(bodyStyle);
            cell.setCellValue(pluginRecord.getDealer().getName());

            cell = bodyRow.createCell(3);
            cell.setCellStyle(bodyStyle);
            cell.setCellValue(pluginRecord.getCampaign().getName());

            cell = bodyRow.createCell(4);
            cell.setCellStyle(bodyStyle);
            cell.setCellValue(pluginRecord.getPlugin().getName());

            cell = bodyRow.createCell(5);
            cell.setCellStyle(bodyStyle);
            cell.setCellValue(countRecord == null ? 0 : countRecord);

            cell = bodyRow.createCell(6);
            cell.setCellStyle(bodyStyle);
            cell.setCellValue(countLead == null ? 0 : countLead);

            // 每当行数达到设置的值就刷新数据到硬盘,以清理内存
            if (i % rowaccess == 0) {
                try {
                    ((SXSSFSheet) sheet).flushRows();
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }
            }
        }
        return workbook;
    }

    
    public List<Plugin> getPluginList(Long id) {
        return reportPluginMapper.selectPluginList(id);
    }
}
