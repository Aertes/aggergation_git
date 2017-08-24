package com.citroen.ledp.service.impl;

import com.citroen.ledp.dao.mybaits.MybaitsGenericDao;
import com.citroen.ledp.domain.Dealer;
import com.citroen.ledp.domain.Leads;
import com.citroen.ledp.domain.Organization;
import com.citroen.ledp.domain.User;
import com.citroen.ledp.exception.LedpException;
import com.citroen.ledp.service.ConstantService;
import com.citroen.ledp.service.DealerService;
import com.citroen.ledp.service.LeadsService;
import com.citroen.ledp.service.OrganizationService;
import com.citroen.ledp.util.DateUtil;
import com.citroen.ledp.util.ExcelUtil;
import com.citroen.ledp.util.MapUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 留资服务类
 *
 * @author miaoshuai
 * @version 1.0
 * @email miaoshuai@tocersfot.com
 * @company www.tocersoft.com
 * @create-time 2015年3月13日 下午1:58:34
 */
@Service
public class LeadsServiceImpl implements LeadsService{

    @Autowired
    private MybaitsGenericDao<Long> mybaitsGenericDao;
    @Autowired
    private ConstantService constantService;
    @Autowired
    private OrganizationService organizationService;
    @Autowired
    private DealerService dealerService;


    public List<Leads> executeQuery(Map params) throws LedpException {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT o1.`id`,o1.`media`,o1.`type`,");
        sql.append(" o1.`dealerId`,o1.`specId`,o1.`seriesId`,");
        sql.append(" o1.`ProvinceId`,o1.`CityId`,o1.`countyId`,");
        sql.append(" o1.`content`,o1.`buyTime`,o1.`buyPriceOff`,");
        sql.append(" o1.`phone`,o1.`name`,o1.`createTime`,");
        sql.append(" o1.`state`,o1.`ReDealerId`,o1.`handleTime`,");
        sql.append(" o1.`orderType`,o1.`userGender`,o1.`userMail`,");
        sql.append(" o1.`carPrice`,o1.`calledNum`,o1.`userKey`,");
        sql.append(" o1.`keepTime`,o1.`waitTime`,o1.`date_create`,");
        sql.append(" o1.`ledp_media`,o1.`ledp_type`,o1.`ledp_follow`,");
        sql.append(" o1.`ledp_intent`,o1.`ledp_dealer`,o1.`ledp_series`,");
        sql.append(" o1.`ledp_vehicle`,o1.`ledp_province`,o1.`ledp_city`,");
        sql.append(" o1.`ledp_district`,o1.campaign_source,o1.ledp_org");
        sql.append(" FROM t_leads o1");
        sql.append(" LEFT JOIN t_media o2 ON o2.id = o1.ledp_media");        // 媒体渠道
        sql.append(" LEFT JOIN t_constant o3 ON o3.id = o1.ledp_type");        // 线索类型
        sql.append(" LEFT JOIN t_constant o4 ON o4.id = o1.ledp_follow");    // 跟进状态
        sql.append(" LEFT JOIN t_dealer o5 ON o5.id = o1.ledp_dealer");    // 网点
        sql.append(" LEFT JOIN t_organization org ON org.id = o1.ledp_org");    // 大区
/*        sql.append(" LEFT JOIN t_vehicle_series o6 ON o6.id = o1.ledp_series");    // 车系
        sql.append(" LEFT JOIN t_vehicle o7 ON o7.id = o1.ledp_vehicle");    // 车型*/
        sql.append(" LEFT JOIN t_constant o8 ON o8.id = o1.ledp_intent");    // 意向类别
        sql.append(" WHERE 1=1");
        // 获取查询条件
        Map<String, Object> condition = getCondition(params);
        Map namedParams = (Map) condition.get("namedParams");
        sql.append(condition.get("namedSql"));
        if (params.containsKey("sortName")) {
            String sortName = "";
            if (params.get("sortName").equals("ledpType.name")) {
                sortName = "ledp_type";
            } else if (params.get("sortName").equals("ledpDealer.name")) {
                sortName = "ledp_dealer";
            } else if (params.get("sortName").equals("ledpMedia.name")) {
                sortName = "ledp_media";
            } else if (params.get("sortName").equals("ledpFollow.name")) {
                sortName = "ledp_follow";
            } else if (params.get("sortName").equals("ledpDealer.dateCreate")) {
                sortName = "date_create";
            } else {
                sortName = params.get("sortName").toString();
            }
            sql.append(" order by o1." + sortName);
        }
        if (params.containsKey("sortOrder")) {
            sql.append(" " + params.get("sortOrder"));
        }
        Map paginateParams = new HashMap();
        try {
            Integer pageSize = Integer.valueOf(params.get("pageSize").toString());
            Integer pageNumber = Integer.valueOf(params.get("pageNumber").toString());
            int offset = (pageNumber - 1) * pageSize;
            paginateParams.put("offset", offset);
            paginateParams.put("max", pageSize);
        } catch (Exception e1) {
            paginateParams.put("offset", 10);
            paginateParams.put("max", 0);
        }
        return mybaitsGenericDao.executeQuery(Leads.class, sql.toString(), namedParams, paginateParams);
    }

    public int getTotalRow(Map params) throws LedpException {
        Map<String, Object> condition = getCondition(params);
        Map namedParams = (Map) condition.get("namedParams");

        StringBuffer sql = new StringBuffer();
        sql.append("SELECT COUNT(1) as count");
        sql.append(" FROM t_leads o1");
        /*sql.append(" LEFT JOIN t_media o2 ON o2.id = o1.ledp_media");        // 媒体渠道
        sql.append(" LEFT JOIN t_constant o3 ON o3.id = o1.ledp_type");        // 线索类型
        sql.append(" LEFT JOIN t_constant o4 ON o4.id = o1.ledp_follow");    // 跟进状态
        sql.append(" LEFT JOIN t_dealer o5 ON o5.id = o1.ledp_dealer");    // 网点
        sql.append(" LEFT JOIN t_organization org ON org.id = o1.ledp_org");    // 大区
*//*        sql.append(" LEFT JOIN t_vehicle_series o6 ON o6.id = o1.ledp_series");    // 车系
        sql.append(" LEFT JOIN t_vehicle o7 ON o7.id = o1.ledp_vehicle");    // 车型*//*
        sql.append(" LEFT JOIN t_constant o8 ON o8.id = o1.ledp_intent");    // 意向类别*/
        sql.append(" WHERE 1=1");
        sql.append(condition.get("namedSql"));

        List<Map> list = mybaitsGenericDao.executeQuery(sql.toString(), namedParams);
        if (list.isEmpty()) {
            return 0;
        }
        Map map = list.get(0);
        return map.isEmpty() ? 0 : Integer.parseInt(map.get("count").toString());
    }

    public Map<String, Object> getCondition(Map<String, Object> params) {
        MapUtil<String, Object> mapUtil = new MapUtil<String, Object>(params);
        Map<String, Object> namedParams = new HashMap<String, Object>();

        StringBuilder namedSql = new StringBuilder();

        // 线索类型
        if (!mapUtil.isBlank("typeId")) {
            Long type = Long.parseLong(mapUtil.get("typeId").toString());
            namedParams.put("typeId", type);
            namedSql.append(" and o1.ledp_type =:typeId");
        }
        // 媒体渠道
        if (!mapUtil.isBlank("ledpMediaId")) {
            Long media = Long.parseLong(params.get("ledpMediaId").toString());
            namedParams.put("ledpMediaId", media);
            namedSql.append(" and o1.ledp_media =:ledpMediaId");
        }
        // 意向类别
        if (!mapUtil.isBlank("ledpIntentId")) {
            Long ledpIntentId = Long.parseLong(params.get("ledpIntentId").toString());
            namedParams.put("ledpIntentId", ledpIntentId);
            namedSql.append(" and o1.ledp_intent =:ledpIntentId");
        }
        // 跟进状态
        if (!mapUtil.isBlank("ledpFollowId")) {
            Long ledpFollowId = Long.parseLong(params.get("ledpFollowId").toString());
            namedParams.put("ledpFollowId", ledpFollowId);
            namedSql.append(" and o1.ledp_follow =:ledpFollowId");
        }
        // 留资开始时间
        if (!mapUtil.isBlank("beginDate")) {
            String beginDate = params.get("beginDate").toString();
            namedParams.put("beginDate", beginDate);
            namedSql.append(" and o1.createTime >=:beginDate");
        }
        // 留资结束时间
        if (!mapUtil.isBlank("endDate")) {
            String endDate = params.get("endDate").toString();
            namedParams.put("endDate", endDate);
            namedSql.append(" and o1.createTime <=:endDate");
        }
        // 手机号码
        if (!mapUtil.isBlank("phone")) {
            String phone = params.get("phone").toString();
            namedParams.put("phone", phone);
            namedSql.append(" and o1.phone =:phone");
        }

        HttpSession session = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getSession();
        User user = (User) session.getAttribute("loginUser");
        Organization org = user.getOrg();
        // 组织ID
        if (org != null) {
            if (!mapUtil.isBlank("organizationSelectedId") && !mapUtil.isBlank("organizationLevel")) {
                String organization = params.get("organizationSelectedId").toString();
                String level = params.get("organizationLevel").toString();
                // 用户如果选择总部
                if (StringUtils.equals(level, "1")) {
                    if (!"1".equals(organization)) {
                        // 大区
                        namedParams.put("organization", organization);
                        namedSql.append(" and o1.ledp_org =:organization");
                    } else if (null == org) {
                        namedParams.put("organization", organization);
                        namedSql.append(" and o1.ledp_dealer =:organization");
                    }

                } else if (StringUtils.equals(level, "2")) {
                    if (2 == org.getLevel()) {
                        namedParams.put("organization", organization);
                        namedSql.append(" and o1.ledp_org =:organization");
                    } else {
                        namedParams.put("organization", organization);
                        namedSql.append(" and o1.ledp_org =:organization");
                    }

                } else if (StringUtils.equals(level, "3")) {
                    Dealer dealer = dealerService.gets(Long.valueOf(organization));
                    // 网点
                    namedParams.put("organization", organization);
                    namedSql.append(" and o1.ledp_dealer =:organization");
                }
                /*if(StringUtils.equals(level,"2")){
                    // 大区
					namedParams.put("organization",organization);
					namedSql.append(" and o5.organization =:organization");
				}else if(StringUtils.equals(level, "3")){
					// 网点
					namedParams.put("organization",organization);
					namedSql.append(" and o1.ledp_dealer =:organization");
				}*/
            } else {
                if (null != user.getOrg()) {
                    if (user.getOrg().getLevel() != 1) {
                        namedParams.put("organization", user.getOrg().getId());
                        namedSql.append(" and o1.ledp_org =:organization");
                    }
                } else if (null != user.getDealer()) {
                    namedParams.put("organization", user.getDealer().getId());
                    namedSql.append(" and o1.ledp_dealer =:organization");
                }
            }
        } else {
            namedParams.put("organization", user.getDealer().getId());
            namedSql.append(" and o1.ledp_dealer =:organization");
        }

        // 合并留资ID
        if (!mapUtil.isBlank("leadsMergedId")) {
            String leadsMergedId = params.get("leadsMergedId").toString();
            namedParams.put("leadsMergedId", leadsMergedId);
            namedSql.append(" and o1.leads_merged =:leadsMergedId");
        }
        // 客户名称
        if (!mapUtil.isBlank("name")) {
            String name = params.get("name").toString();
            namedParams.put("name", name);
            namedSql.append(" and o1.name =:name");
        }
        if (!mapUtil.isBlank("source")) {
            if (params.get("source").toString().equals("0")) {
                namedSql.append(" and o1.campaign_source is not null");
            } else {
                namedSql.append(" and o1.campaign_source is null");
            }
        }
        if (!mapUtil.isBlank("cmpandsite")) {
            if (params.get("cmpandsite").toString().equals("cmp")) {
                namedSql.append(" and o1.campaign_source is not null and o1.ledp_type in (3010,3020,3120,3110)");
            } else {
                namedSql.append(" and o1.campaign_source is null and o1.ledp_type in (3010,3020,3120,3110)");
            }
        }

        Map<String, Object> rs = new HashMap<String, Object>();
        rs.put("namedSql", namedSql);
        rs.put("namedParams", namedParams);
        return rs;
    }

    public Leads get(long id) throws LedpException {
        return mybaitsGenericDao.get(Leads.class, id);
    }

    /**
     * 导出查询
     *
     * @param params
     * @return
     * @throws LedpException
     */
    public List<Map> exportQuery(Map params) throws LedpException {
        Map<String, Object> condition = getCondition(params);
        Map namedParams = (Map) condition.get("namedParams");

        StringBuffer sql = new StringBuffer();
        sql.append("SELECT ");
    /*    sql.append(" o1.`dealerId`,o1.`specId`,o1.`seriesId`,");
        sql.append(" o1.`ProvinceId`,o1.`CityId`,o1.`countyId`,")*/;
        /*sql.append(" o1.`content`,o1.`buyTime`,o1.`buyPriceOff`,");*/
        sql.append(" o1.`phone`,o1.`name`,o1.createTime,");
       /* sql.append(" o1.`state`,o1.`ReDealerId`,o1.`handleTime`,");*/
        /*sql.append(" o1.`orderType`,o1.`userGender`,o1.`userMail`,");
        sql.append(" o1.`carPrice`,o1.`calledNum`,o1.`userKey`,");*/
        /*sql.append(" o1.`keepTime`,o1.`waitTime`,o1.`date_create`,");*/
        sql.append(" o2.name media,o3.name ledpType,o4.name ledpFollow,");
        sql.append(" o8.name ledpIntent,org.name orgName,o5.name dealerName,o6.name seriesName,o9.name campaign ");
        /*sql.append(" o1.`ledp_vehicle`,o1.`ledp_province`,o1.`ledp_city`,");*/
        /*sql.append(" o1.`ledp_district`,o1.campaign_source");*/
        sql.append(" FROM t_leads o1");
        sql.append(" LEFT JOIN t_media o2 ON o2.id = o1.ledp_media");        // 媒体渠道
        sql.append(" LEFT JOIN t_constant o3 ON o3.id = o1.ledp_type");        // 线索类型
        sql.append(" LEFT JOIN t_constant o4 ON o4.id = o1.ledp_follow");    // 跟进状态
        sql.append(" LEFT JOIN t_dealer o5 ON o5.id = o1.ledp_dealer");    // 网点
        sql.append(" LEFT JOIN t_organization org ON org.id = o5.organization");    // 大区
        sql.append(" LEFT JOIN t_vehicle_series o6 ON o6.id = o1.ledp_series");    // 车系
        /*sql.append(" LEFT JOIN t_vehicle o7 ON o7.id = o1.ledp_vehicle");    // 车型*/
        sql.append(" LEFT JOIN t_constant o8 ON o8.id = o1.ledp_intent");    // 意向类别
        sql.append(" LEFT JOIN t_campaign o9 ON o9.id = o1.campaign_source");
        sql.append(" WHERE 1=1");
        sql.append(condition.get("namedSql"));
        //加上排序会全表扫描，暂时先去掉
        /*sql.append(" order by o1.date_create desc");*/
        if (params.get("offset") != null && params.get("pageSize") != null) {
            sql.append(" limit " + params.get("offset") + "," + params.get("pageSize"));
        }

        return mybaitsGenericDao.executeQuery(sql.toString(), namedParams);
        //return mybaitsGenericDao.executeQuery(Leads.class, sql.toString(), namedParams);
    }

    /**
     * 创建导出Excel文件
     *
     * @return
     */
    public SXSSFWorkbook createWorkbook(Map params) throws LedpException {
        int maxnum = 100;
        List<Map> leadsList = null;
        SXSSFWorkbook wb = new SXSSFWorkbook(maxnum);
        Sheet sheet = wb.createSheet("留资信息导出记录");
        String[] titles = new String[]{
                "客户名称", "手机号码", "线索类型", "媒体渠道", "大区", "网点名称",
                "留资时间", "意向级别", "跟进状态","车系"
        };
        Integer[] titleWidths = new Integer[]{12, 12, 12, 10, 10, 15, 30, 10, 10,10};
        // 列头
        Row row = ExcelUtil.createRow(titles, titleWidths, wb, sheet, 0);
        leadsList = this.exportQuery(params);

        for (int i = 0; i < leadsList.size(); i++) {
            row = sheet.createRow(i + 1);
            Map<String,String> leads = leadsList.get(i);
            // 客户名称
            row.createCell(0).setCellValue(leads.get("name"));
            // 手机号码
            row.createCell(1).setCellValue(leads.get("name"));
            // 线索类型
            /*if (leads.getLedpType() !=  null) {
                row.createCell(2).setCellValue(leads.getLedpType().getName());
            }*/
            row.createCell(2).setCellValue(leads.get("ledpType"));/**/
            // 媒体渠道
            /*if (leads.getLedpMedia() != null) {
                row.createCell(3).setCellValue(leads.getLedpMedia().getName());
            }*/
            row.createCell(3).setCellValue(leads.get("media"));
            // 大区名称
           /* String ledpOrgName = "";
            if (null != leads.getLedpOrg()) {
                ledpOrgName = leads.getLedpOrg().getName();
            }*/
            // 网点名称
           /* String ledpDealerName = "";
            if (null != leads.getLedpDealer()) {
                ledpDealerName = leads.getLedpDealer().getName();
            }*/
            row.createCell(4).setCellValue(leads.get("orgName"));
            row.createCell(5).setCellValue(leads.get("dealerName"));
            // 留资时间
            row.createCell(6).setCellValue(leads.get("createTime"));
            // 意向级别
           /* String ledpIntentName = "";
            if (null != leads.getLedpIntent()) {
                ledpIntentName = leads.getLedpIntent().getName();
            }*/
            row.createCell(7).setCellValue(leads.get("ledpIntent"));
            // 跟进状态
            /*String ledpFollowName = "";
            if (null != leads.getLedpFollow()) {
                ledpFollowName = leads.getLedpFollow().getName();
            }
            row.createCell(8).setCellValue(ledpFollowName);*/
            row.createCell(8).setCellValue(leads.get("ledpFollow"));
            //车第
            /*if (leads.getLedpSeries() != null) {
                row.createCell(9).setCellValue(leads.getLedpSeries().getName());
            }*/
            row.createCell(9).setCellValue(leads.get("seriesName"));
            // 每当行数达到设置的值就刷新数据到硬盘,以清理内存
            if (i % maxnum == 0) {
                try {
                    ((SXSSFSheet) sheet).flushRows();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return wb;
    }

    /**
     * 创建导出Excel文件
     *
     * @return
     */
    public SXSSFWorkbook createWechatWorkbook(List<Map> leadsList) {
        SXSSFWorkbook wb = new SXSSFWorkbook();
        Sheet sheet = wb.createSheet("留资信息导出记录");
        String[] titles = new String[]{"大区", "网点名称", "留资类型", "客户名称", "手机号码", "留资来源", "留资时间", "跟进状态"};
        Integer[] titleWidths = new Integer[]{12, 12, 12, 12, 10, 15, 30, 10};
        // 列头
        Row row = ExcelUtil.createRow(titles, titleWidths, wb, sheet, 0);
        for (int i = 0; i < leadsList.size(); i++) {
            row = sheet.createRow(i + 1);
            Map<String,String> leads = leadsList.get(i);
            // 大区
            row.createCell(0).setCellValue(leads.get("orgName"));

            // 网点名称
            row.createCell(1).setCellValue(leads.get("dealerName"));
            // 留资类型
            row.createCell(2).setCellValue(leads.get("leadsType"));
            row.createCell(3).setCellValue(leads.get("name"));
            // 手机号码
            row.createCell(4).setCellValue(leads.get("phone"));

            // 留资来源
            row.createCell(5).setCellValue(StringUtils.isNotBlank(leads.get("campaign"))? leads.get("campaign"): "微站");
            // 留资时间
            row.createCell(6).setCellValue(leads.get("createTime"));
            // 跟进状态
            row.createCell(7).setCellValue(leads.get("ledpFollow"));
        }
        return wb;
    }

    public int getLeadsCountByDate(Long dealerId, Long regionId, Date beginDate, Date endDate) throws LedpException {
        StringBuffer query = new StringBuffer();
        if (null != dealerId) {
            query.append("SELECT COUNT(DISTINCT o0.id) as ct ");
            query.append("FROM t_leads o0 ");
            query.append("WHERE o0.ledp_dealer = " + dealerId + "  ");
            query.append("AND o0.createTime >= '" + DateUtil.format(beginDate, "yyyy-MM-dd 00:00:00") + "' AND o0.createTime <= '" + DateUtil.format(endDate, "yyyy-MM-dd 23:59:59") + "' ");
        } else if (null != regionId) {
            query.append("SELECT COUNT(DISTINCT o0.id) as ct ");
            query.append("FROM t_leads o0 ");
            query.append("INNER JOIN t_dealer o1 ON o0.ledp_dealer = o1.id ");
            query.append("WHERE o1.organization = " + regionId + "  ");
            query.append("AND o0.createTime >= '" + DateUtil.format(beginDate, "yyyy-MM-dd 00:00:00") + "' AND o0.createTime <= '" + DateUtil.format(endDate, "yyyy-MM-dd 23:59:59") + "' ");
        } else {
            query.append("SELECT COUNT(DISTINCT o0.id) as ct ");
            query.append("FROM t_leads o0 ");
            query.append("WHERE 1=1  ");
            query.append("AND o0.createTime >= '" + DateUtil.format(beginDate, "yyyy-MM-dd 00:00:00") + "' AND o0.createTime <= '" + DateUtil.format(endDate, "yyyy-MM-dd 23:59:59") + "' ");
        }
        int totalCount = 0;
        Map row = mybaitsGenericDao.find(query.toString());
        try {
            totalCount = Integer.parseInt(row.get("ct").toString());
        } catch (Exception e) {
        }
        return totalCount;
    }
}
