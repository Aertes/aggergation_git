package com.citroen.wechat.service;

import com.citroen.wechat.domain.FansReport;
import com.citroen.wechat.domain.PublicNo;
import com.citroen.wechat.form.ReportFansQuery;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.List;
import java.util.Map;

/**
 * Created by cyberoller on 2015/11/10.
 */
public interface IReportFansService {
    void addFansReport(FansReport fansReport);

    List<PublicNo> getPublicNoList(ReportFansQuery query);

    List<FansReport> getfReportList(ReportFansQuery query) throws Exception;

    Map<String, List> getFansChart(ReportFansQuery query);

    int getNowTotalFans(ReportFansQuery query);

    Workbook exportReportFansExcel(ReportFansQuery query);

    int getTotalNewUser(ReportFansQuery query);

    int getTotalCancelNewUser(ReportFansQuery query);

    int getTotalCancelOldUser(ReportFansQuery query);

    int getTotalUser(ReportFansQuery query);
}
