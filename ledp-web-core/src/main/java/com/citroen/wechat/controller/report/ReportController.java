package com.citroen.wechat.controller.report;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.citroen.ledp.dao.mybaits.MybaitsGenericDao;
import com.citroen.ledp.domain.Dealer;
import com.citroen.ledp.domain.Organization;
import com.citroen.ledp.domain.User;
import com.citroen.ledp.exception.LedpException;
import com.citroen.ledp.service.ConstantService;
import com.citroen.ledp.service.DealerService;
import com.citroen.ledp.service.LeadsService;
import com.citroen.ledp.service.MediaService;
import com.citroen.ledp.service.OrganizationService;
import com.citroen.ledp.service.PermissionService;
import com.citroen.ledp.util.DateUtil;
import com.citroen.ledp.util.ExcelUtil;
import com.citroen.wechat.domain.PluginType;
import com.citroen.wechat.domain.PublicNo;
import com.citroen.wechat.service.FansService;
import com.citroen.wechat.service.IReportFansService;
import com.citroen.wechat.service.PluginService;
import com.citroen.wechat.service.ReportCampaignService;
import com.citroen.wechat.util.ConstantUtil;

/**
 * 报表管理
 *
 * @author 何海粟
 * @date2015年6月4日
 */
@Controller("weChatReportController")
@RequestMapping("/wechat/report")
public class ReportController {

    @Autowired
    private PermissionService permissionService;
    @Autowired
    private LeadsService leadsService;
    @Autowired
    private OrganizationService organizationService;
    @Autowired
    private MediaService mediaService;
    @Autowired
    private DealerService dealerService;
    @Autowired
    PluginService pluginService;

    @Resource
    private FansService fansService;

    private IReportFansService reportFansService;

    @Autowired
    private MybaitsGenericDao<Long> mybaitsGenericDao;
    @Autowired
    private ReportCampaignService reportCampaignService;
    @Autowired
    private ConstantService constantService;


    @RequestMapping(value = "/autocomplete")
    @ResponseBody
    public JSON autocomplete(HttpServletRequest request) {
        String id = request.getParameter("id");
        if (null != id && id.matches("^\\d+$")) {
            List<Map> list = dealerService.queryListByOrganization(Long.parseLong(id));
            return (JSON) JSON.toJSON(list);
        }

        return null;
    }

    @RequestMapping(value = {"", "campaign"})
    public ModelAndView campaign(Model model, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {
        PublicNo publicNo = (PublicNo) request.getSession().getAttribute(ConstantUtil.SESSION_CURRENT_PUBLICNO);
        if (publicNo == null) {
            return new ModelAndView("redirect:/wechat/publicno/message");
        }

        ModelAndView view = new ModelAndView();
        view.setViewName("wechat/report/campaign");

        Date date = DateUtil.getAfterDay(new Date());
        String strDate = DateUtil.format(date, "yyyy-MM-dd");
        Calendar c = Calendar.getInstance(Locale.CHINA);
        c.setTime(DateUtil.parse(strDate, "yyyy-MM-dd"));
        Date endDate = c.getTime();

        strDate = DateUtil.format(endDate, "yyyy-MM-dd 00:00:00");

        c.setTime(DateUtil.parse(DateUtil.format(new Date(), "yyyy-MM-dd"), "yyyy-MM-dd"));
        Date startDate = c.getTime();

        Organization org = (Organization) session.getAttribute("loginOrg");
        Long orgId = null;
        Long dealerId = null;
        if (org != null) {
            orgId = org.getId();
            if (org.getId() == 1) {
                List<Organization> orgs = organizationService.getChildren(org.getId());
                model.addAttribute("orgs", orgs);
                String csql = "select id as id,name as name from t_campaign where begindate <'" + strDate + "'";
                List<Map> cms = mybaitsGenericDao.executeQuery(csql);
                model.addAttribute("cms", cms);
            } else {
                String csql = "select id as id,name as name from t_campaign where begindate <'" + strDate + "' and (org=" + org.getId() + " or exists(select 1 from t_dealer where id= dealer and id in(select id from t_dealer where organization=" + org.getId() + " )))";
                List<Map> cms = mybaitsGenericDao.executeQuery(csql);
                model.addAttribute("cms", cms);
            }
        } else {
            Dealer dealer = (Dealer) session.getAttribute("loginDealer");
            dealerId = dealer.getId();
            String csql = "select id as id,name as name from t_campaign where begindate <'" + strDate + "' and dealer=" + dealer.getId();
            List<Map> cms = mybaitsGenericDao.executeQuery(csql);
            model.addAttribute("cms", cms);
        }
//		String sqlc=reportCampaignService.getSql(dealerId, orgId, null, null,endDate);
//		String sqlp=reportCampaignService.getPeoSql(dealerId, orgId, null, null, endDate);
//		String sqll=reportCampaignService.getLeadsSql(dealerId, orgId, null, startDate, endDate);
//		String sqlplugin=reportCampaignService.getPluginSql(dealerId, orgId, null, startDate, endDate,null,null);
//		
//		String sql="select count(id) as ct from t_campaign where 1=1 "+sqlc;
//		String sql1="select count(cr.id) as ct from t_campaign_record cr LEFT JOIN t_campaign c on c.id=cr.campaign where c.begindate<'"+strDate+"' and  cr.campaign is not null " +sqlp;
//		String sql2="select count(cl.id) as ct from t_campaign_leads cl LEFT JOIN t_campaign c on c.id=cl.campaign where c.begindate<'"+strDate+"' and cl.campaign is not null "+sqll;
//		String sql3="select count(cl.id) as ct from t_campaign_leads cl LEFT JOIN t_campaign c on c.id=cl.campaign where c.begindate<'"+strDate+"' and cl.campaign is not null "+sqll;
//		String sql4="select count(cl.id) as ct from t_campaign_leads cl LEFT JOIN t_campaign c on c.id=cl.campaign where c.begindate<'"+strDate+"' and cl.pluginid is not null and cl.campaign is not null "+sqlplugin;
//		
//		List<Map> list = mybaitsGenericDao.executeQuery(sql);
//		List<Map> peList=mybaitsGenericDao.executeQuery(sql1);
//		List<Map> leList=mybaitsGenericDao.executeQuery(sql2);
//		List<Map> nowLeList=mybaitsGenericDao.executeQuery(sql3);
//		List<Map> nowLePluginList=mybaitsGenericDao.executeQuery(sql4);
//		
        List<PluginType> pluginTypes = reportCampaignService.getPlugin();
        model.addAttribute("pluginTypes", pluginTypes);
//		for(Map map:list){
//			model.addAttribute("campaigns", map.get("ct"));
//		}
//		for(Map map:peList){
//			model.addAttribute("pepoles", map.get("ct"));
//		}
//		for(Map map:leList){
//			model.addAttribute("leads", map.get("ct"));
//		}
//		for(Map map:nowLeList){
//			model.addAttribute("newLeand", map.get("ct"));
//		}
//		for(Map map:nowLePluginList){
//			model.addAttribute("nowLePlugin", map.get("ct"));
//		}
        // 返回到界面
        return view;
    }

    @RequestMapping(value = {"", "fans"})
    public String fans(Model model, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {
//        PublicNo publicNo = (PublicNo) request.getSession().getAttribute(ConstantUtil.SESSION_CURRENT_PUBLICNO);
//        if (publicNo == null) {
//            return "redirect:/wechat/publicno/message";
//        }
//        if ("-1".equals(publicNo.getVerify_type_info())) {
//            return "redirect:/wechat/publicno/unauth/" + publicNo.getVerify_type_info();
//        }
//        model.addAttribute("publicNo", publicNo);
		/*ModelAndView view = new ModelAndView();
		view.setViewName("wechat/report/fans");*/

        //获取用户
        User loginUser = (User) request.getSession().getAttribute("loginUser");
        //获取组织机构
        Organization org = loginUser.getOrg();
        //网点
        Dealer dealer = loginUser.getDealer();
        if (null == org && null != dealer) { //网点用户
            model.addAttribute("dealer", dealer);
            model.addAttribute("orgs", new ArrayList<Organization>());
        } else {
            model.addAttribute("dealer", new Dealer());
            List<Organization> organizations = new ArrayList<Organization>();

            organizations.add(org); // 当前大区
            if (org.getLevel() == 1) { // 如果是总部
                organizations.addAll(organizationService.getActiviteChildren(org.getId()));
            }

            model.addAttribute("orgs", organizations);
        }
        Date date = DateUtil.getAfterDay(new Date());
        String strDate = DateUtil.format(date, "yyyy-MM-dd");
        Calendar c = Calendar.getInstance(Locale.CHINA);
        c.setTime(DateUtil.parse(strDate, "yyyy-MM-dd"));
        Date endDate = c.getTime();

        strDate = DateUtil.format(endDate, "yyyy-MM-dd 00:00:00");

        c.setTime(DateUtil.parse(DateUtil.format(new Date(), "yyyy-MM-dd"), "yyyy-MM-dd"));
        Date startDate = c.getTime();

        org = (Organization) session.getAttribute("loginOrg");
        Long orgId = null;
        Long dealerId = null;
        if (org != null) {
            orgId = org.getId();
            if (org.getId() == 1) {
                List<Organization> orgs = organizationService.getChildren(org.getId());
                model.addAttribute("orgs", orgs);
                String csql = "select id as id,name as name from t_campaign where begindate <'" + strDate + "'";
                List<Map> cms = mybaitsGenericDao.executeQuery(csql);
                model.addAttribute("cms", cms);
            } else {
                String csql = "select id as id,name as name from t_campaign where begindate <'" + strDate + "' and (org=" + org.getId() + " or exists(select 1 from t_dealer where id= dealer and id in(select id from t_dealer where organization=" + org.getId() + " )))";
                List<Map> cms = mybaitsGenericDao.executeQuery(csql);
                model.addAttribute("cms", cms);
            }
        } else {
            dealer = (Dealer) session.getAttribute("loginDealer");
            dealerId = dealer.getId();
            String csql = "select id as id,name as name from t_campaign where begindate <'" + strDate + "' and dealer=" + dealer.getId();
            List<Map> cms = mybaitsGenericDao.executeQuery(csql);
            model.addAttribute("cms", cms);
        }

//		String sqlYs=reportFansService.getSqlY(dealerId, orgId,  null,endDate,publicNo.getId());
//		String sqlNs=reportFansService.getSqlN(dealerId, orgId, null, endDate,publicNo.getId());
//		String sqlNows=reportFansService.getSqlY(dealerId, orgId,  startDate, endDate,publicNo.getId());
//
//		String sql="select count(id) as ct from t_fans where 1=1 "+sqlYs;
//		String sql1="select count(id) as ct from t_fans where 1=1 " +sqlNs;
//		String sql2="select count(id) as ct from t_fans where 1=1 "+sqlNows;
//		List<Map> listY = mybaitsGenericDao.executeQuery(sql);
//		List<Map> listN=mybaitsGenericDao.executeQuery(sql1);
//		List<Map> listNow=mybaitsGenericDao.executeQuery(sql2);
//		for(Map map:listY){
//			model.addAttribute("subscribes", map.get("ct"));
//		}
//		for(Map map:listN){
//			model.addAttribute("desubscribes", map.get("ct"));
//		}
//		for(Map map:listNow){
//			model.addAttribute("nowSubscribes", map.get("ct"));
//		}
        // 返回到界面
        return "wechat/report/fans";
    }

    @RequestMapping(value = {"searchFans"})
    @ResponseBody
    public Map<String, Object> searchFans(Model model, HttpServletRequest request) {
        return fansService.statistics(request.getParameterMap());
    }

    @RequestMapping("doExport")
    public void doExport(HttpServletResponse response, HttpServletRequest request) throws LedpException {
    	SXSSFWorkbook wb = fansService.createWorkbook(request.getParameterMap());
        ExcelUtil.exportExcelData("粉丝导出记录", response, request, wb);
    }

}
