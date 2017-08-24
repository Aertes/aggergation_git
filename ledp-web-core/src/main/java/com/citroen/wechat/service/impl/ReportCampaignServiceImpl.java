package com.citroen.wechat.service.impl;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Service;

import com.citroen.wechat.domain.Campaign;
import com.citroen.wechat.domain.CampaignLeads;
import com.citroen.wechat.domain.ReportObjectDetail;
import com.citroen.wechat.mapper.ReportCampaignMapper;
import com.citroen.wechat.form.ReportCampaignQuery;
import com.citroen.wechat.service.IReportCampaignService;
import com.citroen.wechat.util.DateUtil;
import com.citroen.wechat.util.ExcelUtil;

/**
 * Created by cyberoller on 2015/11/3.
 */
@Service
public class ReportCampaignServiceImpl implements IReportCampaignService {
    private static Logger logger = Logger.getLogger(ReportCampaignServiceImpl.class);

	private final static String dateFormat = "yyyy-MM-dd HH:mm:ss";
    @Resource
    private ReportCampaignMapper reportCampaignMapper;

    
    public List<Campaign> getCampaignList(ReportCampaignQuery query) {
        return reportCampaignMapper.selectCampaignList(query);
    }

    
    public Map<String, List> getCampaignChart(ReportCampaignQuery query) {
        // 此处可以用存储过程在数据库中实现，但需要注意第一天不是以0点开始，最后一天不是以24点结束
        // 此处选择在程序中实现，以时间换时间
        // 获取时间差
        Date beginDate = query.getSearchDateBegin();
        Date endDate = query.getSearchDateEnd();
        int days = DateUtil.getSubDaysByReport(beginDate, endDate);
        Map<String, Integer> cmap = new TreeMap<String, Integer>();
        String day = null;
        // 时间差为1代表客户开始时间和结束时间是在一天
        if (days == 1) {
            day = DateUtil.format(beginDate, "yyyy-MM-dd");
            cmap.put(day, reportCampaignMapper.selectCampaignChart(query));
        } else {
            Date now = query.getSearchDateBegin();
            for (int i = 0; i < days; i++) {
                if (i == 0) {
                    // 计算第一天
                    query.setSearchDateEnd(DateUtil.getLastOfDay(now));
                } else if (i == (days - 1)) {
                    // 计算最后一天
                    query.setSearchDateBegin(DateUtil.getFistOfDay(now));
                    query.setSearchDateEnd(endDate);
                } else {
                    // 其他时间
                    query.setSearchDateBegin(DateUtil.getFistOfDay(now));
                    query.setSearchDateEnd(DateUtil.getLastOfDay(now));
                }
                day = DateUtil.format(now, "yyyy-MM-dd");
                cmap.put(day, reportCampaignMapper.selectCampaignChart(query));
                // 时间加一天
                now = DateUtil.getAfterDay(now);
            }
        }
        query.setSearchDateBegin(beginDate);
        query.setSearchDateEnd(endDate);
        List<Object> campaigns = new ArrayList<Object>();
        List<String> date = new ArrayList<String>();
        for (String key : cmap.keySet()) {
            date.add(key);
            campaigns.add(cmap.get(key));
        }
        // 再将list放入map返回
        Map<String, List> resultMap = new HashMap<String, List>();
        resultMap.put("date", date);
        resultMap.put("campaigns", campaigns);
        return resultMap;
    }

    
    public int getCampaignCount(ReportCampaignQuery query) {
        return reportCampaignMapper.selectCampaignCount(query);
    }

    
    public Workbook exportCampaignExcel(ReportCampaignQuery query) {
        int rowaccess = 100;    // 内存中缓存记录行数
        // 创建工作簿
        // 内存中限制行数为100，当行号到达101时，行号为0的记录刷新到硬盘并从内存中删除，
        // 当行号到达102时，行号为1的记录刷新到硬盘，并从内存中删除，以此类推。
        SXSSFWorkbook workbook = new SXSSFWorkbook(rowaccess);
        // 创建工作表
        Sheet sheet = workbook.createSheet("活动开展统计");
        List<String> headTitle = new ArrayList<String>();
        headTitle.add("大区");
        headTitle.add("网点名称");
        headTitle.add("开展活动名称");
        headTitle.add("活动开始时间");
        headTitle.add("活动结束时间");
        // 创建表头
        Row headRow = ExcelUtil.createHeadRow(workbook, sheet, headTitle);
        Cell cell;
        // 设置单元格的格式
        CellStyle bodyStyle = ExcelUtil.getBodyStyle(workbook);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<Campaign> campaignList = reportCampaignMapper.selectCampaignList(query);
        if (campaignList != null && !campaignList.isEmpty()) {
            for (int i = 0; i < campaignList.size(); i++) {
                Row bodyRow;
                // 第一次查询，有表头
                // 时间紧急，未过滤异常
                bodyRow = sheet.createRow(i + 1);
                Campaign campaign = campaignList.get(i);
                cell = bodyRow.createCell(0);
                cell.setCellStyle(bodyStyle);
                cell.setCellValue(campaign.getDealer().getOrganization().getName());

                cell = bodyRow.createCell(1);
                cell.setCellStyle(bodyStyle);
                cell.setCellValue(campaign.getDealer().getName());

                cell = bodyRow.createCell(2);
                cell.setCellStyle(bodyStyle);
                cell.setCellValue(campaign.getName());

                cell = bodyRow.createCell(3);
                cell.setCellStyle(bodyStyle);
                cell.setCellValue(sdf.format(campaign.getBeginDate()));

                cell = bodyRow.createCell(4);
                cell.setCellStyle(bodyStyle);
                cell.setCellValue(sdf.format(campaign.getEndDate()));

                // 每当行数达到设置的值就刷新数据到硬盘,以清理内存
                if (i % rowaccess == 0) {
                    try {
                        ((SXSSFSheet) sheet).flushRows();
                    } catch (IOException e) {
                        logger.error(e.getMessage());
                    }
                }
            }
        }
        return workbook;
    }

    
    public Map<String, List> getCampaignLeadChart(ReportCampaignQuery query) {
        // 获取查询结果
        List<Map<String, Object>> mapList = reportCampaignMapper.selectCampaignLeadChart(query);
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

    
    public List<ReportObjectDetail> getCampaignDetial(ReportCampaignQuery query) {
        List<ReportObjectDetail> objectDetails = new ArrayList<ReportObjectDetail>();
        // 先获取活动列表
        List<Campaign> campaignList = reportCampaignMapper.selectCampaignList(query);
        for (Campaign campaign : campaignList) {
            ReportObjectDetail objectDetail = new ReportObjectDetail();
            List<Integer> count = new ArrayList<Integer>();
            query.setCmpaignId(campaign.getId());
            // 获取活动参与人数和留资人数
            Integer countLead = reportCampaignMapper.selectCountLeadByCampaign(query);
            Integer countRecord = reportCampaignMapper.selectCountRecordByCampaignt(query);
            count.add(countRecord == null ? 0 : countRecord);
            count.add(countLead == null ? 0 : countLead);
            objectDetail.setObject(campaign);
            objectDetail.setCount(count);
            objectDetails.add(objectDetail);
        }
        return objectDetails;
    }

    
    public int getCampaignLeadCount(ReportCampaignQuery query) {
        return reportCampaignMapper.selectCampaignLeadCount(query);
    }

    
    public Map<String, List> getCampaignRecordChart(ReportCampaignQuery query) {
        // 获取查询结果
        List<Map<String, Object>> mapList = reportCampaignMapper.selectCampaignRecordChart(query);
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

    
    public int getCampaignRecordCount(ReportCampaignQuery query) {
        return reportCampaignMapper.selectCampaignRecordCount(query);
    }
    
	public SXSSFWorkbook exportCampaignLeadDetail(ReportCampaignQuery query) throws Exception{
		int rowaccess = 100;    // 内存中缓存记录行数
		List<CampaignLeads> leads = this.getCampaignLead(query);	
		SXSSFWorkbook wb = new SXSSFWorkbook();
        Sheet sheet = wb.createSheet("留资详情");
        String[] titles = new String[]{"大区","网点名称","留资来源","活动名称","插件名称","客户姓名","手机号码","留资时间"}; 
        Integer[] titleWidths = new Integer[]{10,15,10,10,20,10,10,10,15};
        Row row = com.citroen.ledp.util.ExcelUtil.createRow(titles, titleWidths, wb, sheet, 0);
        for(int i = 0; i < leads.size(); i++){
        	row = sheet.createRow(i+1);
        	CampaignLeads obj = leads.get(i);
    		row.createCell(0).setCellValue(obj.getLargeArea()!=null?obj.getLargeArea().getName():"");
    		row.createCell(1).setCellValue(obj.getDealer()!=null?obj.getDealer().getName():"");
    		row.createCell(2).setCellValue((obj.getCampaign()!=null)?"活动":"插件");
    		row.createCell(3).setCellValue((obj.getCampaign()!=null)?obj.getCampaign().getName():"插件");
    		row.createCell(4).setCellValue(obj.getPlugin()!=null?obj.getPlugin().getName():"");
    		row.createCell(5).setCellValue(obj.getLeadsName());
    		row.createCell(6).setCellValue(obj.getLeadsPhone());
    		row.createCell(7).setCellValue(com.citroen.ledp.util.DateUtil.format(obj.getCreateDate(), dateFormat));
    		// 每当行数达到设置的值就刷新数据到硬盘,以清理内存
            if (i % rowaccess == 0) {
                try {
                    ((SXSSFSheet) sheet).flushRows();
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }
            }
        }
		return wb;
	}

    
	
    public Workbook exportCampaignLeadAndRecordExcel(ReportCampaignQuery query) {
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
        headTitle.add("活动参与人数");
        headTitle.add("活动留资数");
        // 创建表头
        Row headRow = ExcelUtil.createHeadRow(workbook, sheet, headTitle);
        Cell cell;
        // 设置单元格的格式
        CellStyle bodyStyle = ExcelUtil.getBodyStyle(workbook);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<Campaign> campaignList = reportCampaignMapper.selectCampaignList(query);
        for (int i = 0; i < campaignList.size(); i++) {
            query.setCmpaignId(campaignList.get(i).getId());
            Integer countLead = reportCampaignMapper.selectCountLeadByCampaign(query);
            Integer countRecord = reportCampaignMapper.selectCountRecordByCampaignt(query);
            Row bodyRow;
            // 第一次查询，有表头
            // 时间紧急，未过滤异常
            bodyRow = sheet.createRow(i + 1);
            Campaign campaign = campaignList.get(i);

            cell = bodyRow.createCell(0);
            cell.setCellStyle(bodyStyle);
            cell.setCellValue(sdf.format(campaign.getBeginDate()));

            cell = bodyRow.createCell(1);
            cell.setCellStyle(bodyStyle);
            cell.setCellValue(campaign.getDealer().getOrganization().getName());

            cell = bodyRow.createCell(2);
            cell.setCellStyle(bodyStyle);
            cell.setCellValue(campaign.getDealer().getName());

            cell = bodyRow.createCell(3);
            cell.setCellStyle(bodyStyle);
            cell.setCellValue(campaign.getName());

            cell = bodyRow.createCell(4);
            cell.setCellStyle(bodyStyle);
            cell.setCellValue(countRecord == null ? 0 : countRecord);

            cell = bodyRow.createCell(5);
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

    
    public List<CampaignLeads> getCampaignLeadList(Long id) {
        return reportCampaignMapper.selectCampaignLeadListByCampaignId(id);
    }

	public List<CampaignLeads> getCampaignLead(ReportCampaignQuery query) {
		return reportCampaignMapper.selectCampaignLead(query);
	}


}
