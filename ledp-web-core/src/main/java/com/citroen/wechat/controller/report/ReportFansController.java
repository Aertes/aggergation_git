package com.citroen.wechat.controller.report;

import com.citroen.ledp.domain.Organization;
import com.citroen.wechat.domain.FansReport;
import com.citroen.wechat.domain.PublicNo;
import com.citroen.wechat.form.ReportFansQuery;
import com.citroen.wechat.service.IReportFansService;
import com.citroen.wechat.util.DateUtil;
import com.citroen.wechat.util.ExcelUtil;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller("reportFansController")
@RequestMapping("/wechat/reportFans")
public class ReportFansController {
    @Autowired
    private IReportFansService reportFansService;

    @RequestMapping(value = {"getpublicno"})
    @ResponseBody
    public List<PublicNo> getPublicNoList(ReportFansQuery query) {
        return reportFansService.getPublicNoList(query);
    }

    @RequestMapping(value = {"fans"})
    @ResponseBody
    public Map fans(ReportFansQuery query, HttpServletRequest request) throws Exception {
        setQueryParameter(query, null, null, null, null, null, null, request);
        List<FansReport> fansReportList = reportFansService.getfReportList(query);
        Map<String, List> fansChart = reportFansService.getFansChart(query);
        List<Object> date = fansChart.get("date");
        List<Object> listSubscribe = fansChart.get("newFans");
        List<Object> listDesubscribe = fansChart.get("cancelFans");
        int subscribes = reportFansService.getNowTotalFans(query);
        Map map = new HashMap();
        map.put("date", date);
        map.put("listSubscribe", listSubscribe);
        map.put("listDesubscribe", listDesubscribe);
        map.put("list", fansReportList);
        map.put("subscribes", subscribes);
        return map;
    }

    @RequestMapping(value = "getFansExport")
    public void getFansExport(ReportFansQuery query, Long corg, Long cdealer, Long publicNoId, Integer effect,
                              HttpServletRequest request, HttpServletResponse response) throws Exception {
        setQueryParameter(query, corg, cdealer, publicNoId, effect, "cstartTime", "cendTime", request);
        Workbook workbook = reportFansService.exportReportFansExcel(query);
        ExcelUtil.exportExcelData("粉丝报表统计", response, request, workbook);
    }

    private void setQueryParameter(ReportFansQuery query, Long orgId, Long dealerId, Long publicNoId,
                                   Integer effect, String startTime, String endTime, HttpServletRequest request) {
        // 手动绑定参数
        if (orgId != null) {
            query.setOrgId(dealerId);
        }
        if (dealerId != null) {
            query.setDealerId(dealerId);
        }
        if (publicNoId != null) {
            query.setPublicNoId(publicNoId);
        }
        if (effect != null) {
            query.setSearchDateType(effect);
        }
        // 判断大区信息
        if (query.getDealerId() == null && query.getOrgId() == null) {
            HttpSession session = request.getSession();
            Organization org = (Organization) session.getAttribute("loginOrg");
            // 此处需要排除超级管理员
            if (org.getId() != 1) {
                query.setOrgId(org.getId());
            }
        }
        Date beginDate = query.getSearchDateBegin();
        Date endDate = query.getSearchDateEnd();
        switch (query.getSearchDateType()) {
            case 1:
                //本周
                beginDate = DateUtil.getFirstDayOfThisWeek();
                endDate = DateUtil.getFirstOfDayByDate(0, new Date());
                break;
            case 2:
                //本月
                beginDate = DateUtil.getFirstDayOfThisMonth();
                endDate = DateUtil.getFirstOfDayByDate(0, new Date());
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
                if (!endDate.before(DateUtil.getFistOfDay(new Date()))) {
                    endDate = DateUtil.getFirstOfDayByDate(0, new Date());
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
