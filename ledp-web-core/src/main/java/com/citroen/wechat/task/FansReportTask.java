package com.citroen.wechat.task;

import com.citroen.wechat.domain.FansReport;
import com.citroen.wechat.domain.PublicNo;
import com.citroen.wechat.form.ReportFansQuery;
import com.citroen.wechat.service.IReportFansService;
import com.citroen.wechat.util.DateUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * Created by YuanSongMing on 2015/12/2 14:41.
 */
@Component
public class FansReportTask {
    private static Logger logger = Logger.getLogger(FansReportTask.class);
    @Autowired
    private IReportFansService reportFansService;

    // 定时任务，每天0点开始运行
    @Scheduled(cron = "0 0 0 * * ?")
    public void getFansReport() {
        logger.info("统计粉丝定时任务开启");
        try {
            // 获取所有公众号
            List<PublicNo> publicNoList = reportFansService.getPublicNoList(new ReportFansQuery());
            ReportFansQuery query = new ReportFansQuery();
            // 获取前一天的0点和24点
            Date date = DateUtil.getAddDays(-1, new Date());
            query.setSearchDateBegin(DateUtil.getFirstOfDayByDate(0, date));
            query.setSearchDateEnd(DateUtil.getLastOfDayByDate(0, date));
            for (PublicNo publicNo : publicNoList) {
                // 设置公众号
                query.setPublicNoId(publicNo.getId());
                int totalNewUser = reportFansService.getTotalNewUser(query);
                int totalCancelNewUser = reportFansService.getTotalCancelNewUser(query);
                int totalCancelOldUser = reportFansService.getTotalCancelOldUser(query);
                int totalUser = reportFansService.getTotalUser(query);
                FansReport fansReport = new FansReport();
                fansReport.setCumulateUser(totalUser);
                fansReport.setCancelNewUser(totalCancelNewUser);
                fansReport.setCancelOldUser(totalCancelOldUser);
                fansReport.setNewUser(totalNewUser);
                fansReport.setCancelUser(totalCancelOldUser + totalCancelNewUser);
                publicNo.setId(publicNo.getId());
                fansReport.setPublicNo(publicNo);
                fansReport.setRefDate(date);
                reportFansService.addFansReport(fansReport);
            }
        } catch (Exception e) {
            logger.info("统计粉丝定时任务失败");
            e.printStackTrace();
        }
        logger.info("统计粉丝定时任务完成");
    }
}
