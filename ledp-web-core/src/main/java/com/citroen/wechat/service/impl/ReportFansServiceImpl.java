package com.citroen.wechat.service.impl;

import com.citroen.wechat.domain.FansReport;
import com.citroen.wechat.domain.PublicNo;
import com.citroen.wechat.mapper.ReportFansMapper;
import com.citroen.wechat.form.ReportFansQuery;
import com.citroen.wechat.service.IReportFansService;
import com.citroen.wechat.util.DateUtil;
import com.citroen.wechat.util.ExcelUtil;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * Created by cyberoller on 2015/11/10.
 */
@Service
public class ReportFansServiceImpl implements IReportFansService {
    private static Logger logger = Logger.getLogger(ReportFansServiceImpl.class);
    @Resource
    private ReportFansMapper reportFansMapper;


    public void addFansReport(FansReport fansReport) {
        reportFansMapper.insert(fansReport);
    }


    public List<PublicNo> getPublicNoList(ReportFansQuery query) {
        return reportFansMapper.selectPublicNoList(query);
    }


    public List<FansReport> getfReportList(ReportFansQuery query) throws Exception {
        List<FansReport> fansReportList = reportFansMapper.selectListByByQuery(query);
        return fansReportList;
    }


    public Map<String, List> getFansChart(ReportFansQuery query) {
        // 先根据时间初始化MAP
        Date beginDate = query.getSearchDateBegin();
        Date endDate = query.getSearchDateEnd();
        Map<String, Integer> newMap = new TreeMap<String, Integer>();
        Map<String, Integer> cancelMap = new TreeMap<String, Integer>();
        while (beginDate.before(endDate)) {
            String day = DateUtil.format(beginDate, "yyyy-MM-dd");
            cancelMap.put(day, 0);
            newMap.put(day, 0);
            beginDate = DateUtil.getAfterDay(beginDate);
        }
        // 获取查询结果
        List<FansReport> fansReportList = reportFansMapper.selectCharByByQuery(query);
        List<String> date = new ArrayList<String>();
        List<Integer> newFans = new ArrayList<Integer>();
        List<Integer> cancelFans = new ArrayList<Integer>();
        List<Object> objects = new ArrayList<Object>();
        for (FansReport fansReport : fansReportList) {
            String day = DateUtil.format(fansReport.getRefDate(), DateUtil.FORMAT_SHORT);
            newMap.put(day, fansReport.getNewUser());
            cancelMap.put(day, fansReport.getCancelUser());
        }
        for (String key : newMap.keySet()) {
            date.add(key);
            newFans.add(newMap.get(key));
        }
        for (String key : cancelMap.keySet()) {
            cancelFans.add(cancelMap.get(key));
        }
        // 再将list放入map返回
        Map<String, List> resultMap = new HashMap<String, List>();
        resultMap.put("date", date);
        resultMap.put("newFans", newFans);
        resultMap.put("cancelFans", cancelFans);
        return resultMap;
    }


    public int getNowTotalFans(ReportFansQuery query) {
        return reportFansMapper.selectTotalFansByQuery(query);
    }


    public Workbook exportReportFansExcel(ReportFansQuery query) {
        int rowaccess = 100;    // 内存中缓存记录行数
        // 创建工作簿
        // 内存中限制行数为100，当行号到达101时，行号为0的记录刷新到硬盘并从内存中删除，
        // 当行号到达102时，行号为1的记录刷新到硬盘，并从内存中删除，以此类推。
        SXSSFWorkbook workbook = new SXSSFWorkbook(rowaccess);
        // 创建工作表
        Sheet sheet = workbook.createSheet("粉丝报表统计");
        List<String> headTitle = new ArrayList<String>();
        headTitle.add("时间");
        headTitle.add("大区");
        headTitle.add("网点名称");
        headTitle.add("公众号");
        headTitle.add("新增关注人数");
        headTitle.add("取消关注人数");
        headTitle.add("当前关注人数");
        // 创建表头
        Row headRow = ExcelUtil.createHeadRow(workbook, sheet, headTitle);
        Cell cell;
        // 设置单元格的格式
        CellStyle bodyStyle = ExcelUtil.getBodyStyle(workbook);
        List<FansReport> fansReportList = reportFansMapper.selectListByByQuery(query);
        if (!fansReportList.isEmpty()) {
            for (int i = 0; i < fansReportList.size(); i++) {
                Row bodyRow = sheet.createRow(i + 1);
                FansReport fansReport = fansReportList.get(i);
                cell = bodyRow.createCell(0);
                cell.setCellStyle(bodyStyle);
                cell.setCellValue(DateUtil.format(fansReport.getRefDate(), DateUtil.FORMAT_SHORT));

                cell = bodyRow.createCell(1);
                cell.setCellStyle(bodyStyle);
                cell.setCellValue(fansReport.getPublicNo().getDealer().getOrganization().getName());

                cell = bodyRow.createCell(2);
                cell.setCellStyle(bodyStyle);
                cell.setCellValue(fansReport.getPublicNo().getDealer().getName());

                cell = bodyRow.createCell(3);
                cell.setCellStyle(bodyStyle);
                cell.setCellValue(fansReport.getPublicNo().getNick_name());

                cell = bodyRow.createCell(4);
                cell.setCellStyle(bodyStyle);
                cell.setCellValue(fansReport.getNewUser());

                cell = bodyRow.createCell(5);
                cell.setCellStyle(bodyStyle);
                cell.setCellValue(fansReport.getCancelUser());

                cell = bodyRow.createCell(6);
                cell.setCellStyle(bodyStyle);
                cell.setCellValue(fansReport.getCumulateUser());
            }

        }
        return workbook;
    }


    public int getTotalNewUser(ReportFansQuery query) {
        return reportFansMapper.selectNewUserListByQuery(query).size();
    }


    public int getTotalCancelNewUser(ReportFansQuery query) {
        return reportFansMapper.selectCancelNewUserListByQuery(query).size();
    }


    public int getTotalCancelOldUser(ReportFansQuery query) {
        return reportFansMapper.selectCancelOldUserListByQuery(query).size();
    }


    public int getTotalUser(ReportFansQuery query) {
        Integer total = reportFansMapper.selectCumulateUserByQuery(query);
        return total == null ? 0 : total;
    }
}
