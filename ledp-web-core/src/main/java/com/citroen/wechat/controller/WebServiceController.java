package com.citroen.wechat.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.citroen.ledp.domain.Constant;
import com.citroen.ledp.domain.Dealer;
import com.citroen.ledp.domain.Leads;
import com.citroen.ledp.domain.Media;
import com.citroen.ledp.domain.Organization;
import com.citroen.ledp.domain.Region;
import com.citroen.ledp.domain.Vehicle;
import com.citroen.ledp.domain.VehicleSeries;
import com.citroen.ledp.exception.LedpException;
import com.citroen.ledp.service.DealerService;
import com.citroen.ledp.service.DealerVehicleStatusService;
import com.citroen.ledp.service.RegionService;
import com.citroen.ledp.service.VehicleImageService;
import com.citroen.ledp.service.VehicleParamCategoryService;
import com.citroen.ledp.service.VehicleSeriesImageService;
import com.citroen.ledp.service.VehicleSeriesService;
import com.citroen.ledp.service.VehicleService;
import com.citroen.wechat.api.util.DateUtil;
import com.citroen.wechat.domain.AnswerConfig;
import com.citroen.wechat.domain.AnswerRecord;
import com.citroen.wechat.domain.Awards;
import com.citroen.wechat.domain.Campaign;
import com.citroen.wechat.domain.CampaignLeads;
import com.citroen.wechat.domain.CampaignRecord;
import com.citroen.wechat.domain.CheckPoint;
import com.citroen.wechat.domain.Coupon;
import com.citroen.wechat.domain.CouponConfig;
import com.citroen.wechat.domain.Fans;
import com.citroen.wechat.domain.Plugin;
import com.citroen.wechat.domain.PluginBaseConfig;
import com.citroen.wechat.domain.PublicNo;
import com.citroen.wechat.domain.RotaryTableConfig;
import com.citroen.wechat.domain.ShareConfig;
import com.citroen.wechat.domain.Site;
import com.citroen.wechat.domain.SitePage;
import com.citroen.wechat.domain.Topic;
import com.citroen.wechat.domain.TopicOption;
import com.citroen.wechat.domain.WinInfo;
import com.citroen.wechat.exception.WechatException;
import com.citroen.wechat.form.SubmitLeadsForm;
import com.citroen.wechat.form.WinInfoForm;
import com.citroen.wechat.service.CampaignService;
import com.citroen.wechat.service.FansService;
import com.citroen.wechat.service.IWebService;
import com.citroen.wechat.service.IWinInfoService;
import com.citroen.wechat.service.PluginService;
import com.citroen.wechat.service.SitePageService;
import com.citroen.wechat.util.JsonUtil;

/**
 * 微站相关请求接口
 *
 * @author 何海粟
 * @date2015年6月25日
 */
@Controller
@RequestMapping("/wechat/services")
public class WebServiceController {
    private final static String dateFormat = "yyyy-MM-dd HH:mm:ss";

    private Logger logger = Logger.getLogger(WebServiceController.class);

    private Map<String, Object> params;

    @Autowired
    private PluginService pluginService;
    @Autowired
    private SitePageService sitePageService;
    @Autowired
    private RegionService regionService;
    @Autowired
    private VehicleService vehicleService;
    @Autowired
    private VehicleSeriesService vehicleSeriesService;
    @Autowired
    private VehicleImageService vehicleImageService;
    @Autowired
    private VehicleSeriesImageService vehicleSeriesImageService;
    @Autowired
    private VehicleParamCategoryService vehicleParamCategoryService;
    @Autowired
    private DealerVehicleStatusService dealerVehicleStatusService;
    @Autowired
    private CampaignService campaignService;
    @Autowired
    private DealerService dealerService;
    @Autowired
    private FansService fansService;
    @Autowired
    private IWebService webService;
    @Autowired
    private IWinInfoService winInfoService;

    @RequestMapping("init")
    @ResponseBody
    public void init(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html; charset=utf-8");
        //跨域问题
        response.setHeader("Access-Control-Allow-Origin", "*");

        String code = request.getParameter("code");
        Map<String, Object> params = new HashMap<String, Object>();
        Map<String, Object> json = new HashMap<String, Object>();

        String pluginId = request.getParameter("pluginId");
        String pageId = request.getParameter("pageId");
        String pluginUnion = request.getParameter("pluginUnion");
        String state = request.getParameter("state");

        //用户唯一标识
        String uuid = getUserCode(request, response, pageId);
        //是否可以玩多次
        String isMulite = "";
        if (StringUtils.isNotBlank(pluginId)) {
            params.put("pluginId", pluginId);
        } else {
            throw new WechatException("参数错误");
        }
        if (StringUtils.isNotBlank(pageId)) {
            params.put("pageId", pageId);
        } else {
            throw new WechatException("参数错误");
        }
        if (StringUtils.isNotBlank(pluginUnion)) {
            params.put("pluginUnion", pluginUnion);
        } else {
            throw new WechatException("参数错误");
        }

        params.put("userCode", uuid);
        SitePage page = sitePageService.siteInstance(Long.valueOf(pageId));

        if (page != null && page.getSite() != null && page.getSite().getCampaign() != null) {
            Campaign campaign = page.getSite().getCampaign();
            Date curDate = new Date();
            //活动开始时间
            Date startDate = campaign.getBeginDate();
            //活动结束时间
            Date endDate = campaign.getEndDate();
            if ("purchase".equals(code)) {
                params.put("name", "purchaseStartTime");
                Date purStartDate = DateUtil.convert(pluginService.getVal(params) + ":00", dateFormat);
                params.put("name", "purchaseEndTime");
                Date purEndDate = DateUtil.convert(pluginService.getVal(params) + ":00", dateFormat);
                params.remove("name");
                if (curDate.before(startDate)) {
                    json.put("status", "unstart");
                } else if (curDate.after(endDate)) {
                    json.put("status", "hasEnd");
                } else if (curDate.before(purStartDate)) {
                    //插件活动未开始
                    json.put("status", "punstart");
                } else if (curDate.after(purEndDate)) {
                    //插件活动未开始
                    json.put("status", "phasEnd");
                } else {
                    json.put("status", "normal");
                }
            } else {
                if (curDate.before(startDate)) {
                    json.put("status", "unstart");
                } else if (curDate.after(endDate)) {
                    json.put("status", "hasEnd");
                } else {
                    json.put("status", "normal");
                }
            }
        } else {
            json.put("status", "normal");
        }

        try {
            List<PluginBaseConfig> baseConfigs = pluginService.getBaseConfig(params);
            if (CollectionUtils.isNotEmpty(baseConfigs)) {
                for (PluginBaseConfig base : baseConfigs) {
                    if (StringUtils.isNotBlank(base.getName())) {
                        json.put(base.getName(), base.getValue());
                    }
                }
            }
        } catch (Exception e) {
            throw new WechatException("查询基础配置信息失败");
        }
        try {
            switch (getNameByCode(code)) {
                case 0:
                    throw new WechatException("插件code不能为空或错误");
                case 1:
                    //基本元素插件
                    break;
                case 2:
                    //大转盘
                    RotaryTableConfig config = pluginService.getRotaryTableConfig(params);
                    if (config == null) {
                        throw new WechatException("未查询到大转盘配置参数");
                    }
                    params.put("name", "multiple");
                    //是否可以玩多次
                    isMulite = pluginService.getVal(params);
                    params.put("userCode", uuid);
                    long answerLeads = pluginService.getTotalCampaignRecord(params);
                    if (!"1".equals(isMulite) && answerLeads > 0) {
                        json.put("state", "no");
                    } else {
                        json.put("state", "yes");
                    }
                    //共几个将项
                    json.put("levelCount", config.getLevel());
                    json.put("levels", config.getItems());
                    json.put("MutilpeLottery", isMulite);
                    break;
                case 3:
                    //分享有礼
                    ShareConfig share = pluginService.getShareConfig(params);
                    if (share == null) {
                        throw new WechatException("未查询到分享有礼配置参数");
                    }
                    json.put("advance", share);
                    json.put("type", "share");
                    break;
                case 4:
                    //答题闯关
                    //判断是否答过题了
                    params.put("name", "isAllowLots");
                    //查询是否可以玩多次
                    isMulite = pluginService.getVal(params);
                    params.put("userCode", uuid);
                    long campaignRecords = pluginService.getTotalCampaignRecord(params);
                    if (!"yes".equals(isMulite) && campaignRecords > 0 && !"pc".equals(state)) {
                        json.put("state", "out");
                        response.getWriter().print(JsonUtil.toJSON(json));
                        return;
                    }
                    AnswerConfig answer = pluginService.getAnswerConfig(params);
                    if (answer == null) {
                        throw new WechatException("未查询到答题闯关配置参数");
                    }
                    json.put("question", getQuestion(answer.getCheckPoint()));
                    break;
                case 5:
                    //惠团购在基本元素中处理
                    //参加次数
                    params.put("userCode", uuid);
                    long count = pluginService.getTotalCampaignLeadsRecord(params);
                    params.remove("userCode");
                    long peopleCount = pluginService.getTotalCampaignLeadsRecord(params);
                    //已参加活动人数
                    json.put("peopleCount", peopleCount);

                    //是否参加过团购了
                    if (count > 0 && !"pc".equals(state)) {
                        json.put("state", "no");
                    } else {
                        json.put("state", "yes");
                    }
                    break;
                case 6:
                    //优惠券
                    params.remove("userCode");
                    CouponConfig coupon = pluginService.getCouponConfig(params);
                    if (coupon != null && CollectionUtils.isNotEmpty(coupon.getItems())) {
                        List<Map<String, Object>> maps = new ArrayList<Map<String, Object>>();
                        for (Coupon item : coupon.getItems()) {
                            Map<String, Object> couponMap = new HashMap<String, Object>();
                            couponMap.put("description", item.getDescription());
                            couponMap.put("ruleDesc", item.getRuleDesc());
                            couponMap.put("par", item.getPar());
                            couponMap.put("startDate", item.getStartDate());
                            couponMap.put("endDate", item.getEndDate());
                            couponMap.put("unionCode", item.getSn());
                            params.put("coupon", item.getId());
                            //查询已经领取了多少
                            long records = pluginService.getCountCouponByCouponId(item.getId());
                            if (item.getQuantity() > records) {
                                //查询是否可以玩多次
                                if (1 == item.getStatus()) {
                                    couponMap.put("state", "yes");
                                } else {
                                    //参加次数
                                    params.put("userCode", uuid);
                                    long joinCount = pluginService.getTotalCampaignLeadsRecord(params);
                                    if (joinCount > 0) {
                                        couponMap.put("state", "out");
                                    } else {
                                        //已经参加过了
                                        couponMap.put("state", "yes");
                                    }

                                }
                            } else {
                                couponMap.put("state", "no");
                            }
                            maps.add(couponMap);
                        }
                        json.put("items", maps);
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new WechatException("接口调用异常");
        }
        response.getWriter().print(JsonUtil.toJSON(dateFormat, json));

    }

    /**
     * 1.大转盘抽奖接口
     *
     * @param model
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping("lottery")
    @ResponseBody
    public void lottery(Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html; charset=utf-8");
        //跨域问题
        response.setHeader("Access-Control-Allow-Origin", "*");

        Map<String, Object> params = new HashMap<String, Object>();
        Map<String, Object> json = new HashMap<String, Object>();
        Map<String, Object> result = new HashMap<String, Object>();
        CampaignRecord entity = new CampaignRecord();
        boolean flag = false;
        String state = request.getParameter("state");
        String pluginIdStr = request.getParameter("pluginId");
        String pageIdStr = request.getParameter("pageId");
        String pluginUnion = request.getParameter("pluginUnion");
        //用户唯一标识
        String uuid = getUserCode(request, response, pageIdStr);
        long pluginId = 0L;
        long pageId = 0L;
        try {
            pluginId = Long.valueOf(pluginIdStr);
            pageId = Long.valueOf(pageIdStr);
            params.put("pluginId", pluginId);
            params.put("pageId", pageId);
        } catch (Exception e) {
            throw new WechatException("参数错误");
        }
        if (StringUtils.isNotBlank(pluginUnion)) {
            params.put("pluginUnion", pluginUnion);
        } else {
            throw new WechatException("参数错误");
        }
        params.put("userCode", uuid);
        Organization org = null;
        Dealer dealer = null;
        Campaign campaign = null;
        SitePage page = sitePageService.siteInstance(pageId);
        if (page != null && page.getSite() != null) {
            if (page.getSite().getCampaign() != null) {
                campaign = page.getSite().getCampaign();
                campaign = campaignService.campaignInstance(campaign.getId());
                params.put("campaign", campaign.getId());
                entity.setCampaign(campaign);
                dealer = campaign.getDealer();
                if (dealer != null) {
                    entity.setDealer(dealer);
                    entity.setLargeArea(dealer.getOrganization());
                }
                org = campaign.getOrg();
                if (org != null) {
                    entity.setLargeArea(org);
                }
            } else {
                dealer = page.getSite().getDealer();
                if (dealer != null) {
                    dealer = dealerService.gets(dealer.getId());
                    entity.setDealer(dealer);
                    entity.setLargeArea(dealer.getOrganization());
                }
            }
        } else {
            throw new WechatException("未查询到页面信息");
        }
        params.put("name", "multiple");
        //是否可以玩多次
        String isMulite = pluginService.getVal(params);
        //获取大转盘高级配置信息
        RotaryTableConfig config = pluginService.getRotaryTableConfig(params);
        if (config == null) {
            throw new WechatException("未查到奖项配置信息");
        }
        //获取大转盘基本配置信息
        //1查询参与的活动记录
        long records = pluginService.getTotalCampaignRecord(params);
        if ("pc".equals(state)) {
            //测试用 可以一直进行
            result.put("residueCount", 1);
        } else if ("0".equals(isMulite) && records == 0) {
            //抽完这次就没了
            result.put("residueCount", 0);
        } else if (!"0".equals(isMulite)) {
            result.put("residueCount", 1);
        } else {
            //用完了
            json.put("residueCount", 0);
            response.getWriter().print(JsonUtil.toJSON(json));
            return;
        }

        //抽奖
        webService.isWin(config, flag, uuid, result, entity, params, json);

        json.put("statusCode", 200);
        json.put("statusMsg", "抽奖成功");
        json.put("result", result);
        //4保存抽奖信息
        entity.setUserCode(uuid);
        entity.setCreateDate(new Date());
        entity.setPageId(pageId);
        entity.setPluginId(pluginId);
        entity.setPluginUnion(pluginUnion);
        if (!"pc".equals(state)) {
            pluginService.saveCampaignRecord(entity);
        }
        response.getWriter().print(JsonUtil.toJSON(json));
    }


    /**
     * 3.答题闯关-答题接口
     * state
     * yes:可以继续
     * no:结束
     * out:已经答过了
     *
     * @param request
     * @param response
     * @param session
     * @throws Exception
     */
    @RequestMapping("answer")
    @ResponseBody
    public void answer(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {
        response.setContentType("text/html; charset=utf-8");
        //跨域问题
        response.setHeader("Access-Control-Allow-Origin", "*");

        Map<String, Object> params = new HashMap<String, Object>();
        Map<String, Object> json = new HashMap<String, Object>();
        CampaignRecord entity = new CampaignRecord();
        //本关答对总数
        int rightCount = 0;
        //答错总数
        int errCount = 0;
        long pluginId = 0L;
        long pageId = 0L;
        //关卡id
        long levelId = 0L;
        //题目id
        long questionId = 0L;
        //答题的版本
        String answerSn = getSn(request, response);
        //测试标识
        String state = request.getParameter("state");
        String pluginUnion = request.getParameter("pluginUnion");
        //选项 "a b c d"
        String optionKey = request.getParameter("optionKey");

        try {
            pluginId = Long.valueOf(request.getParameter("pluginId"));
            pageId = Long.valueOf(request.getParameter("pageId"));
            levelId = Long.valueOf(request.getParameter("levelKey"));
            questionId = Long.valueOf(request.getParameter("questionKey"));
            params.put("pluginId", pluginId);
            params.put("pageId", pageId);
            params.put("levelId", levelId);
            params.put("questionId", questionId);
        } catch (Exception e) {
            throw new WechatException("参数错误");
        }

        //用户唯一标识
        String uuid = getUserCode(request, response, pageId + "");

        if (StringUtils.isNotBlank(optionKey)) {
            params.put("optionKey", optionKey);
        } else {
            throw new WechatException("参数错误");
        }
        if (StringUtils.isNotBlank(pluginUnion)) {
            params.put("pluginUnion", pluginUnion);
        } else {
            throw new WechatException("参数错误");
        }
        params.put("userCode", uuid);
        params.put("name", "isAllowLots");
        //是否可以玩多次
        String isMulite = pluginService.getVal(params);

        //判断是否答过题了
        long campaignRecords = pluginService.getTotalCampaignRecord(params);
        if (!"yes".equals(isMulite) && campaignRecords > 0 && !"pc".equals(state)) {
            json.put("state", "out");
            cleanSn(request, response);
            response.getWriter().print(JsonUtil.toJSON(json));
            return;
        }

        Organization org = null;
        Dealer dealer = null;
        Campaign campaign = null;
        SitePage page = sitePageService.siteInstance(pageId);
        if (page != null && page.getSite() != null) {
            if (page.getSite().getCampaign() != null) {
                campaign = page.getSite().getCampaign();
                campaign = campaignService.campaignInstance(campaign.getId());
                params.put("campaign", campaign.getId());
                entity.setCampaign(campaign);
                dealer = campaign.getDealer();
                if (dealer != null) {
                    entity.setDealer(dealer);
                    entity.setLargeArea(dealer.getOrganization());
                }
                org = campaign.getOrg();
                if (org != null) {
                    entity.setLargeArea(org);
                }
            } else {
                dealer = page.getSite().getDealer();
                if (dealer != null) {
                    dealer = dealerService.gets(dealer.getId());
                    entity.setDealer(dealer);
                    entity.setLargeArea(dealer.getOrganization());
                }
            }
        } else {
            cleanSn(request, response);
            throw new WechatException("未查询到页面信息");
        }

        entity.setCreateDate(new Date());
        entity.setUserCode(uuid);
        entity.setPageId(pageId);
        entity.setPluginId(pluginId);
        entity.setPluginUnion(pluginUnion);

        //查询是否答对
        boolean flag = pluginService.isAnswerSuccess(params);
        //保存答题记录
        AnswerRecord record = new AnswerRecord();
        record.setCreateDate(new Date());
        record.setLevelId(levelId);
        record.setQuestion(questionId);
        record.setOption(optionKey);
        record.setUserCode(uuid);
        record.setSn(answerSn);
        ;
        if (flag == true) {
            //设置答题情况
            record.setIsRight(1);
        } else {
            record.setIsRight(0);
        }
        if (!"pc".equals(state)) {
            pluginService.saveAnswerRecord(record);
        }
        params.put("sn", answerSn);
        //读取游戏配置信息
        AnswerConfig config = pluginService.getAnswerConfig(params);
        //查询本关答题情况
        List<AnswerRecord> answerRecord = pluginService.getAnswerRecord(params);
        //本关答对的数量
        rightCount = getAnswerCount(answerRecord, 1);
        //本关答错的数量
        errCount = getAnswerCount(answerRecord, 0);
        //最后一关
        int lastLevel = config.getCheckPoint().size();
        //本关
        CheckPoint checkpoint = pluginService.getCheckPoint(levelId);

        if (checkpoint == null) {
            throw new WechatException("未查到关卡信息");
        }

        if (config != null) {
            //满足过关需求,并且不是最后一关
            if (rightCount >= checkpoint.getTotal() && checkpoint.getLevel() != lastLevel) {
                //进入下一关
                json.put("isNext", "yes");
                json.put("state", "yes");
            } else if (rightCount >= checkpoint.getTotal() && checkpoint.getLevel() == lastLevel) {
                //满足过关要求，并且已经是最后一关了
                if (!"all".equals(config.getWinType())) {
                    if (CollectionUtils.isNotEmpty(config.getPrizeOne())) {
                        //发奖品退出
                        for (Awards awards : config.getPrizeOne()) {
                            if (awards.getLevel() == checkpoint.getLevel() && awards.getLevel() != 0) {
                                json.put("prize", awards.getName());
                                json.put("checkpoint", checkpoint.getName());
                                entity.setAwards(awards);
                                break;
                            }
                        }
                    }
                } else {
                    json.put("prize", config.getPrize().getName());
                    json.put("checkpoint", "通关");
                    entity.setAwards(config.getPrize());
                }
                json.put("isNext", "no");
                json.put("state", "no");
            } else if ((rightCount + errCount) < checkpoint.getTopics().size()) {
                //继续答题
                json.put("isNext", "no");
                json.put("state", "yes");
            } else {
                //获取奖品奖品退出
                if ("one".equals(config.getWinType())) {
                    if (CollectionUtils.isNotEmpty(config.getPrizeOne())) {
                        //发奖品
                        for (Awards awards : config.getPrizeOne()) {
                            if (awards.getLevel() == (checkpoint.getLevel() - 1) && awards.getLevel() != 0) {
                                json.put("prize", awards.getName());
                                //已经过了的关
                                CheckPoint successCheckPoint = pluginService.getCheckPointByAwards(awards.getId());
                                json.put("checkpoint", successCheckPoint != null ? successCheckPoint.getName() : "上一关");
                                json.put("successCheckPoint", successCheckPoint != null ? successCheckPoint.getName() : "上一关");
                                json.put("failCheckPoint", checkpoint.getName());
                                entity.setAwards(awards);
                                break;
                            }
                        }
                    }
                }
                json.put("isNext", "no");
                json.put("state", "no");
            }

        } else {
            throw new WechatException("未找到配置信息");
        }

        //活动结束，保存记录
        if ("no".equals(json.get("state"))) {
            if (!"pc".equals(state)) {
                //保存活动记录
                pluginService.saveCampaignRecord(entity);
            }
            cleanSn(request, response);
        }

        response.getWriter().print(JsonUtil.toJSON(json));
    }


    /**
     * 4.分享有礼接口
     *
     * @param model
     * @param request
     * @param response
     * @param session
     * @throws Exception
     */
    @RequestMapping("share")
    @ResponseBody
    public void share(Model model, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {
        response.setContentType("text/html; charset=utf-8");
        //跨域问题
        response.setHeader("Access-Control-Allow-Origin", "*");

        Map<String, Object> condiction = new HashMap<String, Object>();
        Map<String, Object> json = new HashMap<String, Object>();
        Map<String, Object> result = new HashMap<String, Object>();
        CampaignRecord entity = new CampaignRecord();
        String state = request.getParameter("state");
        String pluginIdStr = request.getParameter("attr[pluginId]");
        String pageIdStr = request.getParameter("attr[pageId]");
        String pluginUnion = request.getParameter("attr[pluginUnion]");
        //用户唯一标识
        String uuid = getUserCode(request, response, pageIdStr);
        long pluginId = 0L;
        long pageId = 0L;
        try {
            pluginId = Long.valueOf(pluginIdStr);
            pageId = Long.valueOf(pageIdStr);
            condiction.put("pluginId", pluginId);
            condiction.put("pageId", pageId);
        } catch (Exception e) {
            throw new WechatException("参数错误");
        }
        if (StringUtils.isNotBlank(pluginUnion)) {
            condiction.put("pluginUnion", pluginUnion);
        } else {
            throw new WechatException("参数错误");
        }

        Organization org = null;
        Dealer dealer = null;
        Campaign campaign = null;
        SitePage page = sitePageService.siteInstance(pageId);
        if (page != null && page.getSite() != null) {
            if (page.getSite().getCampaign() != null) {
                campaign = page.getSite().getCampaign();
                campaign = campaignService.campaignInstance(campaign.getId());
                condiction.put("campaign", campaign.getId());
                entity.setCampaign(campaign);
                dealer = campaign.getDealer();
                if (dealer != null) {
                    entity.setDealer(dealer);
                    entity.setLargeArea(dealer.getOrganization());
                }
                org = campaign.getOrg();
                if (org != null) {
                    entity.setLargeArea(org);
                }
            } else {
                dealer = page.getSite().getDealer();
                if (dealer != null) {
                    dealer = dealerService.gets(dealer.getId());
                    entity.setDealer(dealer);
                    entity.setLargeArea(dealer.getOrganization());
                }
            }
        } else {
            throw new WechatException("未查询到页面信息");
        }

        //获取分享配置信息
        ShareConfig config = pluginService.getShareConfig(condiction);
        if (config == null) {
            throw new WechatException("未查到分享有礼配置信息");
        }
        Awards awards = new Awards();
        if (CollectionUtils.isNotEmpty(config.getItems())) {
            awards = config.getItems().get(0);
        } else {
            throw new WechatException("未查到奖项配置信息");
        }
        //获取分享有礼基本配置信息
        //是否还有奖品可以领取
        boolean isDraw = true;
        //1查询参与的活动记录
        long records = pluginService.getTotalCampaignLeadsRecord(condiction);
        if (records >= awards.getQuantity()) {
            isDraw = false;
        }

        if (isDraw == true) {
            //前台中奖标识标识
            json.put("status", 1);
            Map<String, Object> voucher = new HashMap<String, Object>();
            voucher.put("type", awards.getLevel());
            voucher.put("money", awards.getName());
            voucher.put("levelName", StringUtils.isBlank(awards.getLevelName()) ? "恭喜您，中奖了" : awards.getLevelName());
            result.put("voucher", voucher);
            entity.setAwards(awards);
        } else {
            json.put("status", 0);
        }
        condiction.put("name", "shareStartTime");
        String shareStartTime = pluginService.getVal(condiction);
        condiction.put("name", "shareEndTime");
        String shareEndTime = pluginService.getVal(condiction);

        json.put("shareStartTime", shareStartTime);
        json.put("shareEndTime", shareEndTime);
        json.put("statusCode", 200);
        json.put("statusMsg", "抽奖成功");
        json.put("result", result);
        //4保存抽奖信息
        entity.setUserCode(uuid);
        entity.setCreateDate(new Date());
        entity.setPageId(pageId);
        entity.setPluginId(pluginId);
        entity.setPluginUnion(pluginUnion);
        if (!"pc".equals(state)) {
            pluginService.saveCampaignRecord(entity);
        }

        response.getWriter().print(JsonUtil.toJSON(json));
    }

    /**
     * 5.惠团购接口
     *
     * @param model
     * @param request
     * @param response
     * @param session
     * @throws Exception
     */
    @RequestMapping("purchase")
    @ResponseBody
    public void purchase(Model model, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {
        response.setContentType("text/html; charset=utf-8");
        //跨域问题
        response.setHeader("Access-Control-Allow-Origin", "*");

        Map<String, Object> params = new HashMap<String, Object>();
        CampaignRecord entity = new CampaignRecord();
        long pluginId = 0L;
        long pageId = 0L;

        //测试标识
        String state = request.getParameter("state");
        String pluginIdStr = request.getParameter("pluginId");
        String pageIdStr = request.getParameter("pageId");
        String pluginUnion = request.getParameter("pluginUnion");
        //用户唯一标识
        String uuid = getUserCode(request, response, pageIdStr);

        try {
            pluginId = Long.valueOf(pluginIdStr);
            pageId = Long.valueOf(pageIdStr);
            params.put("pluginId", pluginId);
            params.put("pageId", pageId);
        } catch (Exception e) {
            throw new WechatException("参数错误");
        }

        Organization org = null;
        Dealer dealer = null;
        Campaign campaign = null;
        SitePage page = sitePageService.siteInstance(pageId);
        if (page != null && page.getSite() != null) {
            if (page.getSite().getCampaign() != null) {
                campaign = page.getSite().getCampaign();
                campaign = campaignService.campaignInstance(campaign.getId());
                params.put("campaign", campaign.getId());
                entity.setCampaign(campaign);
                dealer = campaign.getDealer();
                if (dealer != null) {
                    entity.setDealer(dealer);
                    entity.setLargeArea(dealer.getOrganization());
                }
                org = campaign.getOrg();
                if (org != null) {
                    entity.setLargeArea(org);
                }
            } else {
                dealer = page.getSite().getDealer();
                if (dealer != null) {
                    dealer = dealerService.gets(dealer.getId());
                    entity.setDealer(dealer);
                    entity.setLargeArea(dealer.getOrganization());
                }
            }
        } else {
            throw new WechatException("未查到页面信息");
        }
        params.put("userCode", uuid);
        long record = pluginService.getTotalCampaignRecord(params);
        if (record > 0 && !"pc".equals(state)) {
            //已经参加过了
            response.getWriter().print(JsonUtil.toJSON("no"));
            return;
        }

        //记录日志
        entity.setPageId(pageId);
        entity.setUserCode(uuid);
        entity.setCreateDate(new Date());
        entity.setPageId(pageId);
        entity.setPluginId(pluginId);
        entity.setPluginUnion(pluginUnion);

        try {
            if (!"pc".equals(state)) {
                pluginService.saveCampaignRecord(entity);
            }
            response.getWriter().print(JsonUtil.toJSON("yes"));
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().print(JsonUtil.toJSON("no"));
        }

    }

    /**
     * 6.优惠券接口
     *
     * @param model
     * @param request
     * @param response
     * @param session
     * @throws Exception
     */
    @RequestMapping("getCoupon")
    @ResponseBody
    public void getCoupon(Model model, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {
        response.setContentType("text/html; charset=utf-8");
        //跨域问题
        response.setHeader("Access-Control-Allow-Origin", "*");

        Map<String, Object> params = new HashMap<String, Object>();
        CampaignRecord entity = new CampaignRecord();
        //测试标识
        String state = request.getParameter("state");
        String pluginIdStr = request.getParameter("pluginId");
        String pageIdStr = request.getParameter("pageId");
        String pluginUnion = request.getParameter("pluginUnion");
        String unionCode = request.getParameter("unionCode");
        //用户唯一标识
        String uuid = getUserCode(request, response, pageIdStr);
        long pluginId = 0L;
        long pageId = 0L;
        try {
            pluginId = Long.valueOf(pluginIdStr);
            pageId = Long.valueOf(pageIdStr);
            params.put("pluginId", pluginId);
            params.put("pageId", pageId);
        } catch (Exception e) {
            throw new WechatException("参数错误");
        }

        if (StringUtils.isNotBlank(pluginUnion)) {
            params.put("pluginUnion", pluginUnion);
        } else {
            throw new WechatException("参数错误");
        }
        if (StringUtils.isNotBlank(unionCode)) {
            params.put("sn", unionCode);
        } else {
            throw new WechatException("参数错误");
        }

        Dealer dealer = null;
        Campaign campaign = null;
        SitePage page = sitePageService.siteInstance(pageId);
        if (page != null && page.getSite() != null) {
            campaign = page.getSite().getCampaign();
            if (campaign != null) {
                campaign = page.getSite().getCampaign();
                campaign = campaignService.campaignInstance(campaign.getId());
                entity.setCampaign(campaign);
                dealer = campaign.getDealer();
                if (dealer != null) {
                    entity.setDealer(dealer);
                    entity.setLargeArea(dealer.getOrganization());
                }
            } else {
                dealer = page.getSite().getDealer();
                if (dealer != null) {
                    dealer = dealerService.gets(dealer.getId());
                    entity.setDealer(dealer);
                    entity.setLargeArea(dealer.getOrganization());
                }
            }
        } else {
            throw new WechatException("未查询到页面信息");
        }

        long records = pluginService.getTotalCampaignRecord(params);
        Coupon coupon = pluginService.getCoupon(params);
        if (coupon == null) {
            response.getWriter().print(JsonUtil.toJSON("no"));
        } else if (coupon.getQuantity() <= records) {
            //领完了
            //奖留资中的coupon改为null
            List<CampaignLeads> campaignLeads = pluginService.getCampaignLeads(uuid, 0);
            if (CollectionUtils.isNotEmpty(campaignLeads)) {
                CampaignLeads _campaignLeads = campaignLeads.get(0);
                _campaignLeads.setCoupon(null);
                pluginService.updateCampaignLeads(_campaignLeads);
            }
            entity.setCouponCode(null);
            entity.setCoupon(null);
            entity.setPageId(Long.valueOf(pageId));
            entity.setUserCode(uuid);
            entity.setCreateDate(new Date());
            entity.setPageId(pageId);
            entity.setPluginId(pluginId);
            entity.setPluginUnion(pluginUnion);
            if (!"pc".equals(state)) {
                pluginService.saveCampaignRecord(entity);
            }
            response.getWriter().print(JsonUtil.toJSON("no"));
        } else {
            //记录日志
            entity.setCouponCode(coupon.getSn());
            entity.setCoupon(coupon);
            entity.setPageId(Long.valueOf(pageId));
            entity.setUserCode(uuid);
            entity.setCreateDate(new Date());
            entity.setPageId(pageId);
            entity.setPluginId(pluginId);
            entity.setPluginUnion(pluginUnion);
            if (!"pc".equals(state)) {
                pluginService.saveCampaignRecord(entity);
            }
            response.getWriter().print(JsonUtil.toJSON("yes"));
        }

    }

    /**
     * 跳转到留资页面
     *
     * @param model
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping("goLeads")
    public String goLeads(Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html; charset=utf-8");
        //跨域问题
        response.setHeader("Access-Control-Allow-Origin", "*");

        String pluginId = request.getParameter("pluginId");
        String pageId = request.getParameter("pageId");
        String pluginUnion = request.getParameter("pluginUnion");
        String code = request.getParameter("code");
        String url = request.getParameter("submitUrl");
        StringBuilder _url = new StringBuilder();
        _url.append(url).append("?pluginId=").append(pluginId)
                .append("&pageId=").append(pageId)
                .append("&pluginUnion=").append(pluginUnion)
                .append("&code=").append(code);
        return "redirect:" + _url.toString();
    }

    /**
     * 留资表单
     *
     * @param model
     * @param request
     * @param response
     * @param session
     * @throws Exception
     */
    @RequestMapping("leadsForm")
    public void leadsForm(Model model, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws Exception {
        response.setContentType("text/html; charset=utf-8");
        //跨域问题
        response.setHeader("Access-Control-Allow-Origin", "*");
        Map<String, Object> params = new HashMap<String, Object>();
        Map<String, Object> json = new HashMap<String, Object>();
        String pluginId = request.getParameter("pluginId");
        String pageId = request.getParameter("pageId");
        String pluginUnion = request.getParameter("pluginUnion");
        String code = request.getParameter("code");

        if (StringUtils.isNotBlank(pluginId)) {
            params.put("pluginId", pluginId);
        } else {
            throw new WechatException("参数错误");
        }
        if (StringUtils.isNotBlank(pageId)) {
            params.put("pageId", pageId);
        } else {
            throw new WechatException("参数错误");
        }
        if (StringUtils.isNotBlank(pluginUnion)) {
            params.put("pluginUnion", pluginUnion);
        } else {
            throw new WechatException("参数错误");
        }
        //判断是否收集意向车型
        if (StringUtils.isNotBlank(code)) {
            //如果是惠团购
            if ("purchase".equals(code)) {
                //是否收集意向车型
                params.put("name", "purchaseYixiang");
                String purchaseYixiang = pluginService.getVal(params);

                if ("yes".equals(purchaseYixiang)) {
                    List<Vehicle> vehicles = pluginService.getVehicles(0l);
                    json.put("series", vehicles);
                }
            } else {
                json.put("state", "yes");
            }
        }
        response.getWriter().print(JsonUtil.toJSON(json));
    }


    /**
     * 留资接口
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping("submitLeads")
    public void submitLeads(HttpServletRequest request, HttpServletResponse response, SubmitLeadsForm form) throws Exception {
        response.setContentType("text/html; charset=utf-8");
        //跨域问题
        response.setHeader("Access-Control-Allow-Origin", "*");

        if (form == null || form.getPageId() <= 0 || form.getPageId() <= 0) {
            throw new WechatException("参数错误");
        }

        //用户唯一标识
        String uuid = getUserCode(request, response, form.getPageId() + "");
        form.setUuid(uuid);

        if (form.getPluginId() == 13) {
            Map queryCoupon = new HashMap();
            queryCoupon.put("sn", form.getUnionCode());
            Coupon coupon = pluginService.getCoupon(queryCoupon);
            if (coupon == null) {
                response.getWriter().print(JsonUtil.toJSON("no"));
                return;
            }
            long couponCount = pluginService.getCountCouponByCouponId(coupon.getId());
            if (couponCount >= coupon.getQuantity()) {
                response.getWriter().print(JsonUtil.toJSON("no"));
                return;
            }
        }

        WinInfo winInfo = webService.submitLeads(form);

        if (form.getPluginId() != 9) {
            response.getWriter().print(JsonUtil.toJSON("yes"));
        } else {
            Map json = new HashMap();
            json.put("state", "yes");
            if (winInfo.getAwards() == null) {
                json.put("msg", "奖品领取超时");
            }
            response.getWriter().print(JsonUtil.toJSON(json));
        }

    }


    /**
     * 获取车系列表
     *
     * @throws WechatException
     * @throws IOException
     */
    @RequestMapping("getVehicleSeries")
    @ResponseBody
    public void getVehicleSeries(HttpServletResponse response) throws Exception {
        response.setContentType("text/html; charset=utf-8");
        //跨域问题
        response.setHeader("Access-Control-Allow-Origin", "*");
        try {
            List<VehicleSeries> series = pluginService.getVehicleSeries();
            response.getWriter().print(JsonUtil.toJSON(series));
        } catch (LedpException e) {
            e.printStackTrace();
            throw new WechatException("查询出错");
        }

    }

    /**
     * 通过车系获取车型列表
     *
     * @throws Exception
     */
    @RequestMapping("getVehicle")
    @ResponseBody
    public void getVehicle(long seriesId, HttpServletResponse response) throws Exception {
        response.setContentType("text/html; charset=utf-8");
        //跨域问题
        response.setHeader("Access-Control-Allow-Origin", "*");
        try {
            List<Vehicle> vehicles = pluginService.getVehicles(seriesId);
            response.getWriter().print(JsonUtil.toJSON(vehicles));
        } catch (LedpException e) {
            e.printStackTrace();
            throw new WechatException("查询出错");
        }
    }


    /**
     * 新车展厅
     *
     * @throws Exception
     */
    @RequestMapping("newCarShow")
    @ResponseBody
    public void newCarShow(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html; charset=utf-8");
        //跨域问题
        response.setHeader("Access-Control-Allow-Origin", "*");
        String pageIdStr = request.getParameter("pageId");
        List<Map> maps = new ArrayList<Map>();
        List<Map> vehicleSeries = new ArrayList<Map>();
        long pageId = 0L;
        Dealer dealer = null;
        try {
            pageId = Long.valueOf(pageIdStr);
            SitePage page = sitePageService.siteInstance(pageId);
            if (page.getSite() != null && page.getSite().getCampaign() != null) {
                Campaign campaign = campaignService.campaignInstance(page.getSite().getCampaign().getId());
                dealer = campaign.getDealer();
            } else {
                dealer = page.getSite().getDealer();
            }
        } catch (Exception e) {
        }
        //查询所有车系
        Map<String, Object> params = new HashMap<String, Object>();
        if (dealer != null) {
            params.put("dealer", dealer.getId());
            vehicleSeries = dealerVehicleStatusService.getDealerVehicleSeries(dealer, null);
        } else {
            vehicleSeries = dealerVehicleStatusService.getDealerDefaultVehicleSeries(null, null);
        }

        if (!vehicleSeries.isEmpty()) {
            for (Map vehicle : vehicleSeries) {
                if (vehicle != null && !vehicle.isEmpty() && vehicle.get("series") != null) {
                    Map map = new HashMap();
                    map.put("id", vehicle.get("series"));
                    Map image = vehicleSeriesImageService.getBySeries((Long) vehicle.get("series"));
                    if (image == null || image.isEmpty()) {
                        map.put("imgSrc", "");
                    } else {
                        map.put("imgSrc", request.getContextPath() + image.get("webpath"));
                    }
                    map.put("title", vehicle.get("name"));
                    map.put("LessMoney", vehicle.get("minmoney"));
                    map.put("lotMoney", vehicle.get("maxmoney"));
                    map.put("totalType", vehicle.get("ct"));
                    maps.add(map);
                }
            }
        }
        response.getWriter().print(JsonUtil.toJSON(maps));

    }

    /**
     * 新车展厅详细
     *
     * @throws Exception
     */
    @RequestMapping("newCarShowDetail")
    @ResponseBody
    public void newCarShowDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html; charset=utf-8");
        //跨域问题
        response.setHeader("Access-Control-Allow-Origin", "*");
        Map json = new HashMap();
        List<Map> vehicleSeries = new ArrayList<Map>();
        List<Map> vehicles = new ArrayList<Map>();
        List<Map> maps = new ArrayList<Map>();
        // all 1.6. 1.8
        String typeStr = request.getParameter("type");
        String pageIdStr = request.getParameter("pageId");
        String idStr = request.getParameter("id");
        Dealer dealer = null;
        long id = 0l;
        long pageId = 0l;

        if (StringUtils.isBlank(typeStr)) {
            throw new WechatException("参数错误");
        }
        try {
            id = Long.valueOf(idStr);
            pageId = Long.valueOf(pageIdStr);
            SitePage page = sitePageService.siteInstance(pageId);
            if (page.getSite() != null && page.getSite().getCampaign() != null) {
                Campaign campaign = campaignService.campaignInstance(page.getSite().getCampaign().getId());
                dealer = campaign.getDealer();
            } else {
                dealer = page.getSite().getDealer();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        json.put("type", typeStr);

        if ("all".equals(typeStr)) {
            typeStr = "";
        }

        //根据网点查车系、车型
        if (dealer != null) {
            vehicleSeries = dealerVehicleStatusService.getDealerVehicleSeries(dealer, id);
            vehicles = dealerVehicleStatusService.getDealerVehicles(dealer, id, typeStr);
        } else {
            vehicleSeries = dealerVehicleStatusService.getDealerDefaultVehicleSeries(null, id);
            vehicles = dealerVehicleStatusService.getDealerDefaultVehicles(null, id, typeStr);
        }

        if (!vehicleSeries.isEmpty()) {
            Map vehicle = vehicleSeries.get(0);
            if (vehicle != null && !vehicle.isEmpty() && vehicle.get("series") != null) {
                Map map = new HashMap();
                map.put("id", vehicle.get("series"));
                Map image = vehicleSeriesImageService.getBySeries((Long) vehicle.get("series"));
                if (image == null || image.isEmpty()) {
                    map.put("imgSrc", "");
                } else {
                    map.put("imgSrc", request.getContextPath() + image.get("webpath"));
                }
                map.put("title", vehicle.get("name"));
                map.put("LessMoney", vehicle.get("minmoney"));
                map.put("lotMoney", vehicle.get("maxmoney"));
                map.put("totalType", vehicle.get("ct"));
                json.put("service", map);
            }
        }
        //根据车系查该网点所有车型
        if (CollectionUtils.isNotEmpty(vehicles)) {
            for (Map vehicle : vehicles) {
                if (vehicle != null && vehicle.get("vehicle") != null) {
                    Map map = new HashMap();
                    map.put("id", vehicle.get("vehicle"));
                    map.put("title", vehicle.get("name"));
                    map.put("guidePrice", vehicle.get("guidePrice"));
                    maps.add(map);
                }
            }
        }
        json.put("carList", maps);
        response.getWriter().print(JsonUtil.toJSON(json));
    }

    /**
     * 获取车型详细配置参数
     *
     * @throws Exception
     */
    @RequestMapping("getVehicleDetail")
    @ResponseBody
    public void getVehicleDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html; charset=utf-8");
        //跨域问题
        response.setHeader("Access-Control-Allow-Origin", "*");
        Map<String, Object> json = new HashMap<String, Object>();
        Map<String, Object> vehicle = new HashMap<String, Object>();
        String vehicleIdStr = request.getParameter("vehicleId");
        Long vehicleId = 0L;
        try {
            vehicleId = Long.valueOf(vehicleIdStr);
        } catch (Exception e) {
            throw new WechatException("参数错误");
        }
        Vehicle ve = vehicleService.getVehicle(vehicleId);
        if (ve != null) {
            vehicle.put("id", ve.getId());
            vehicle.put("title", ve.getName());
            vehicle.put("LessMoney", ve.getPrice());
            Map image = vehicleImageService.getSeriesImages(ve.getSeries().getId());
            if (image != null && !image.isEmpty()) {
                vehicle.put("imgSrc", request.getContextPath() + image.get("webpath"));
            }
        }
        json.put("service", vehicle);
        json.put("base", vehicleParamCategoryService.get(vehicleId));
        response.getWriter().print(JsonUtil.toJSON(json));
    }

    /**
     * 预约试驾
     *
     * @throws Exception
     */
    @RequestMapping("orderDrive")
    @ResponseBody
    public void orderDrive(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html; charset=utf-8");
        //跨域问题
        response.setHeader("Access-Control-Allow-Origin", "*");

        CampaignLeads entity = new CampaignLeads();
        CampaignRecord record = new CampaignRecord();
        Leads leads = new Leads();
        //测试标识 pc为测试，不记录数据
        String state = request.getParameter("state");
        //页面编号
        String pageIdStr = (String) params.get("pageId");
        //试驾车型编号
        String shijiaCatType = (String) params.get("shijiaCatType");
        //姓名
        String name = (String) params.get("name");
        //手机
        String phone = (String) params.get("phone");
        //性别
        String sex = (String) params.get("sex");
        //城市编号
        String cityList = (String) params.get("cityList");
        //经销商编号
        String distributor = (String) params.get("distributor");
        //预约时间
        String time = (String) params.get("time");
        long pageId = 0L;
        Region ledpCity = null;
        Vehicle ledpVehicle = null;
        Dealer dealer = null;
        long dealerId = 0L;
        try {
            pageId = Long.valueOf(pageIdStr);
            ledpCity = new Region(Long.valueOf(cityList));
            ledpVehicle = vehicleService.getVehicle(Long.valueOf(shijiaCatType));
            entity.setPageid(pageId);
            dealerId = Long.valueOf(distributor);
            dealer = dealerService.gets(dealerId);
            entity.setDealer(dealer);
            entity.setLargeArea(dealer.getOrganization());
            leads.setLedpOrg(dealer.getOrganization());
            leads.setLedpDealer(dealer);
            leads.setDealerId(dealer.getId() + "");
            leads.setLedpSeries(ledpVehicle.getSeries());
        } catch (Exception e) {
            throw new WechatException("参数错误");
        }
        String userCode = getUserCode(request, response, pageIdStr);
        Campaign campaign = null;
        SitePage page = sitePageService.siteInstance(pageId);
        if (page != null && page.getSite() != null) {
            campaign = page.getSite().getCampaign();
            if (campaign != null) {
                entity.setCampaign(campaign);
                leads.setCampaign(campaign);
                record.setCampaign(campaign);
                record.setDealer(dealer);
                record.setLargeArea(dealer.getOrganization());
                record.setUserCode(userCode);
                record.setCreateDate(new Date());
                record.setPageId(pageId);
                if (!"pc".equals(state)) {
                    pluginService.saveCampaignRecord(record);
                }
            }
        } else {
            throw new WechatException("未查询到页面信息");
        }

        entity.setLeadsName(name);
        entity.setUserCode(userCode);
        entity.setCreateDate(new Date());
        entity.setLeadsPhone(phone);
        entity.setLeadsSex(sex);
        entity.setLeadsType(2);
        entity.setVehicle(ledpVehicle);
        if (!"pc".equals(state)) {
            //保存活动留资
            pluginService.saveCampaignLeads(entity);
        }

        //保存留资
        leads.setCreateTime(DateUtil.convert(new Date(), dateFormat));
        leads.setMedia("微信平台");
        leads.setName(name);
        leads.setPhone(phone);
        leads.setUserGender(("man".equals(sex)) ? "0" : "1");
        leads.setDateCreate(new Date());
        leads.setPlanTime(time);
        leads.setLedpCity(ledpCity);
        leads.setLedpVehicle(ledpVehicle);
        leads.setLedpMedia(new Media(4L));
        //待跟进
        leads.setLedpFollow(new Constant(4010L));
        //意向级别A
        leads.setLedpIntent(new Constant(2010L));
        leads.setState("1");
        leads.setType("试驾");
        leads.setLedpType(new Constant(3020L));
        leads.setClueId(3L);
        pluginService.saveLeads(leads);
        response.getWriter().print("yes");
    }

    /**
     * 询价
     *
     * @throws Exception
     */
    @RequestMapping("queryPrice")
    @ResponseBody
    public void queryPrice(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html; charset=utf-8");
        //跨域问题
        response.setHeader("Access-Control-Allow-Origin", "*");

        CampaignLeads entity = new CampaignLeads();
        Leads leads = new Leads();
        //测试标识 pc为测试，不记录数据
        String state = request.getParameter("state");
        //页面编号
        String pageIdStr = (String) params.get("pageId");
        //试驾车型编号
        String shijiaCatType = (String) params.get("shijiaCatType");
        //姓名
        String name = (String) params.get("name");
        //手机
        String phone = (String) params.get("phone");
        //性别
        String sex = (String) params.get("sex");
        //城市编号
        String cityList = (String) params.get("cityList");
        //经销商编号
        String distributor = (String) params.get("distributor");
        //预约时间
        String time = (String) params.get("time");
        long pageId = 0L;
        Region ledpCity = null;
        Vehicle ledpVehicle = null;
        Dealer dealer = null;
        long dealerId = 0L;
        try {
            pageId = Long.valueOf(pageIdStr);
            ledpCity = new Region(Long.valueOf(cityList));
            ledpVehicle = vehicleService.getVehicle(Long.valueOf(shijiaCatType));
            entity.setPageid(pageId);
            dealerId = Long.valueOf(distributor);
            dealer = dealerService.gets(dealerId);
            entity.setDealer(dealer);
            entity.setLargeArea(dealer.getOrganization());
            leads.setLedpOrg(dealer.getOrganization());
            leads.setLedpDealer(dealer);
            leads.setDealerId(dealer.getId() + "");
            leads.setLedpSeries(ledpVehicle.getSeries());
        } catch (Exception e) {
            throw new WechatException("参数错误");
        }

        String userCode = getUserCode(request, response, pageIdStr);

        Campaign campaign = null;
        SitePage page = sitePageService.siteInstance(pageId);
        if (page != null && page.getSite() != null) {
            campaign = page.getSite().getCampaign();
            if (campaign != null) {
                entity.setCampaign(campaign);
                leads.setCampaign(campaign);
            }
        } else {
            throw new WechatException("未查询到页面信息");
        }

        entity.setLeadsName(name);
        entity.setUserCode(userCode);
        entity.setCreateDate(new Date());
        entity.setLeadsPhone(phone);
        entity.setLeadsSex(sex);
        entity.setLeadsType(1);
        entity.setVehicle(ledpVehicle);
        if (!"pc".equals(state)) {
            //保存活动留资
            pluginService.saveCampaignLeads(entity);
        }

        //保存留资
        leads.setCreateTime(DateUtil.convert(new Date(), dateFormat));
        leads.setMedia("微信平台");
        leads.setName(name);
        leads.setPhone(phone);
        leads.setUserGender(("man".equals(sex)) ? "0" : "1");
        leads.setDateCreate(new Date());
        leads.setPlanTime(time);
        leads.setLedpCity(ledpCity);
        leads.setLedpVehicle(ledpVehicle);
        leads.setLedpMedia(new Media(4L));
        //待跟进
        leads.setLedpFollow(new Constant(4010L));
        //意向级别A
        leads.setLedpIntent(new Constant(2010L));
        leads.setState("1");
        //询价3010 试驾3020
        leads.setType("询价");
        leads.setLedpType(new Constant(3010L));
        leads.setClueId(3L);
        pluginService.saveLeads(leads);
        response.getWriter().print("yes");
    }

    /**
     * 服务预约
     *
     * @throws Exception
     */
    @RequestMapping("orderServices")
    @ResponseBody
    public void orderServices(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html; charset=utf-8");
        //跨域问题
        response.setHeader("Access-Control-Allow-Origin", "*");

        CampaignLeads entity = new CampaignLeads();
        Leads leads = new Leads();
        //页面编号
        String pageIdStr = (String) params.get("pageId");
        //保养车型
        String baoyangchexing = (String) params.get("baoyangchexing");
        //姓名
        String name = (String) params.get("name");
        //手机
        String phone = (String) params.get("phone");
        //性别
        String sex = (String) params.get("sex");
        //预约时间
        String yuyuetime = (String) params.get("yuyuetime ");
        //留言
        String remainVa = (String) params.get("remainVa");

        long pageId = 0L;
        Vehicle ledpVehicle = null;
        Dealer dealer = null;
        try {
            pageId = Long.valueOf(pageIdStr);
            ledpVehicle = vehicleService.getVehicle(Long.valueOf(baoyangchexing));
            entity.setPageid(pageId);
        } catch (Exception e) {
            throw new WechatException("参数错误");
        }

        String userCode = getUserCode(request, response, pageIdStr);

        Campaign campaign = null;
        SitePage page = sitePageService.siteInstance(pageId);
        if (page != null && page.getSite() != null) {
            campaign = page.getSite().getCampaign();
            if (campaign != null) {
                campaign = campaignService.campaignInstance(campaign.getId());
                entity.setCampaign(campaign);
                leads.setCampaign(campaign);

                dealer = campaign.getDealer();
                if (dealer != null) {
                    entity.setDealer(dealer);
                    entity.setLargeArea(dealer.getOrganization());
                    leads.setLedpOrg(dealer.getOrganization());
                    leads.setDealerId(dealer.getId() + "");
                    leads.setLedpDealer(dealer);
                }
            } else {
                dealer = page.getSite().getDealer();
                if (dealer != null) {
                    dealer = dealerService.gets(dealer.getId());
                    entity.setDealer(dealer);
                    entity.setLargeArea(dealer.getOrganization());
                    leads.setLedpOrg(dealer.getOrganization());
                    leads.setLedpDealer(dealer);
                    leads.setDealerId(dealer.getId() + "");
                }
            }
        } else {
            throw new WechatException("未查询到页面信息");
        }

        entity.setLeadsName(name);
        entity.setUserCode(userCode);
        entity.setCreateDate(new Date());
        entity.setLeadsPhone(phone);
        entity.setLeadsSex(sex);
        entity.setLeadsType(3);
        entity.setVehicle(ledpVehicle);
        entity.setRemark(remainVa);
        //保存活动留资
        pluginService.saveCampaignLeads(entity);

        //保存留资
        leads.setCreateTime(DateUtil.convert(new Date(), dateFormat));
        leads.setMedia("微信平台");
        leads.setName(name);
        leads.setPhone(phone);
        leads.setUserGender(("man".equals(sex)) ? "0" : "1");
        leads.setDateCreate(new Date());
        leads.setPlanTime(yuyuetime);
        if (ledpVehicle != null) {
            leads.setLedpVehicle(ledpVehicle);
            leads.setLedpSeries(ledpVehicle.getSeries());
        }
        leads.setLedpMedia(new Media(4L));
        //待跟进
        leads.setLedpFollow(new Constant(4010L));
        //意向级别A
        leads.setLedpIntent(new Constant(2010L));
        leads.setState("1");
        //服务预约
        leads.setType("服务预约");
        leads.setLedpType(new Constant(3110L));
        leads.setClueId(3L);
        leads.setRemark(remainVa);
        pluginService.saveLeads(leads);
        response.getWriter().print("yes");
    }


    /**
     * 获取省份
     *
     * @throws Exception
     */
    @RequestMapping("getProvince")
    @ResponseBody
    public void getProvince(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html; charset=utf-8");
        //跨域问题
        response.setHeader("Access-Control-Allow-Origin", "*");
        String province = request.getParameter("province");
        Long provinceId = 0L;
        try {
            provinceId = Long.valueOf(province);
        } catch (Exception e) {
        }
        response.getWriter().print(JsonUtil.toJSON(regionService.getProvinces(provinceId)));
    }

    /**
     * 获取城市
     *
     * @throws Exception
     */
    @RequestMapping("getCitys")
    @ResponseBody
    public void getCitys(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html; charset=utf-8");
        //跨域问题
        response.setHeader("Access-Control-Allow-Origin", "*");
        Map<String, Object> json = new HashMap<String, Object>();
        String pageIdStr = request.getParameter("pageId");
        Long pageId = 0L;
        Dealer dealer = new Dealer();
        try {
            pageId = Long.valueOf(pageIdStr);
            Campaign campaign = null;
            SitePage page = sitePageService.siteInstance(pageId);
            if (page != null && page.getSite() != null) {
                if (page.getSite().getCampaign() != null) {
                    campaign = page.getSite().getCampaign();
                    campaign = campaignService.campaignInstance(campaign.getId());
                    dealer = campaign.getDealer();
                } else {
                    dealer = page.getSite().getDealer();
                    if (dealer != null) {
                        dealer = dealerService.gets(dealer.getId());
                    }
                }
            } else {
                throw new WechatException("未查询到页面信息");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        json.put("cityList", regionService.getCitys(0l));
        //当前城市id，没有就为0
        json.put("checked", dealer != null ? (dealer.getCity() != null ? dealer.getCity().getId() : 0) : 0);
        Map map = new HashMap();
        map.put("id", dealer.getId());
        map.put("name", dealer.getName());
        json.put("distributor", map);
        response.getWriter().print(JsonUtil.toJSON(json));
    }

    /**
     * 获取经销商
     *
     * @throws Exception
     */
    @RequestMapping("getDealers")
    @ResponseBody
    public void getDealers(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html; charset=utf-8");
        //跨域问题
        response.setHeader("Access-Control-Allow-Origin", "*");
        String cityName = request.getParameter("city");
        response.getWriter().print(JsonUtil.toJSON(pluginService.getDealers(cityName)));
    }


    /**
     * 获取试驾车型
     *
     * @throws Exception
     */
    @RequestMapping("getOrderDriveVehicles")
    @ResponseBody
    public void getOrderDriveVehicles(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html; charset=utf-8");
        //跨域问题
        response.setHeader("Access-Control-Allow-Origin", "*");
        String dealerStr = request.getParameter("dealer");
        long dealerId = 0;
        try {
            dealerId = Long.valueOf(dealerStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (dealerId > 0) {
            response.getWriter().print(JsonUtil.toJSON(dealerVehicleStatusService.getDealerVehicles(new Dealer(dealerId), null, "")));
        } else {
            response.getWriter().print(JsonUtil.toJSON(pluginService.getVehiclesMaps()));
        }
    }

    /**
     * 获取所有车型
     *
     * @throws Exception
     */
    @RequestMapping("getVehicles")
    @ResponseBody
    public void getVehicles(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html; charset=utf-8");
        //跨域问题
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.getWriter().print(JsonUtil.toJSON(pluginService.getVehicles()));
    }


    /**
     * 判断活动是否结束
     *
     * @throws Exception
     */
    @RequestMapping("isTimeOut")
    @ResponseBody
    public void isTimeOut(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html; charset=utf-8");
        //跨域问题
        response.setHeader("Access-Control-Allow-Origin", "*");
        Map<String, String> map = new HashMap<String, String>();
        Long pageId = null;
        try {
            pageId = Long.valueOf(request.getParameter("pageId"));
        } catch (Exception e) {
            throw new WechatException("得不到参数");
        }
        SitePage page = sitePageService.siteInstance(pageId);
        Campaign campaign = page.getSite().getCampaign();
        if (campaign != null) {
            //系统当前时间
            Date curDate = new Date();
            //活动开始时间
            Date startDate = campaign.getBeginDate();
            //活动结束时间
            Date endDate = campaign.getEndDate();
            if (curDate.before(startDate)) {
                map.put("status", "unstart");
            } else if (curDate.after(endDate)) {
                map.put("status", "hasEnd");
            } else {
                map.put("status", "normal");
            }
        } else {
            map.put("status", "normal");
        }
        response.getWriter().print(JsonUtil.toJSON(map));
    }

    /**
     * 个人中心
     * type:
     * 1:我的预约
     * 2：我的询价
     * 3：我的活动
     * 4：我的奖品
     */
    @RequestMapping("persionCenter")
    public void persionCenter(HttpServletRequest request, HttpServletResponse response, int type, String pageid) throws Exception {
        response.setContentType("text/html; charset=utf-8");
        //跨域问题
        response.setHeader("Access-Control-Allow-Origin", "*");
        Map<String, Object> json = new HashMap<String, Object>();
        Map<String, Object> params = new HashMap<String, Object>();
        List<Map<String, Object>> orderDrives = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> queryPrices = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> campaignRecords = new ArrayList<Map<String, Object>>();
        String userCode = getUserCode(request, response, pageid);
        params.put("userCode", userCode);
        Map myInfo = pluginService.getLeadsInfo(userCode);
        if (myInfo != null) {
            json.put("userName", myInfo.get("name"));
        }
        logger.error(userCode);
        switch (type) {
            case 1:
                //查询我的所有预约试驾
                List<CampaignLeads> drives = pluginService.getCampaignLeads(userCode, 2);
                if (CollectionUtils.isNotEmpty(drives)) {
                    for (CampaignLeads leads : drives) {
                        Map<String, Object> map = new HashMap<String, Object>();
                        map.put("date", DateUtil.format(leads.getCreateDate(), dateFormat));
                        if (leads.getVehicle() != null) {
                            map.put("carType", leads.getVehicle().getName());
                        } else {
                            map.put("carType", "微站活动");
                        }
                        map.put("name", "预约试驾");
                        orderDrives.add(map);
                    }
                }

                //我的服务预约
                List<CampaignLeads> orderServices = pluginService.getCampaignLeads(userCode, 3);
                if (CollectionUtils.isNotEmpty(orderServices)) {
                    for (CampaignLeads leads : orderServices) {
                        Map<String, Object> map = new HashMap<String, Object>();
                        map.put("date", DateUtil.format(leads.getCreateDate(), dateFormat));
                        if (leads.getVehicle() != null) {
                            map.put("carType", leads.getVehicle().getName());
                        } else {
                            map.put("carType", "微站活动");
                        }
                        map.put("name", "服务预约");
                        orderDrives.add(map);
                    }
                }
                //排序
                Collections.sort(orderDrives, new Comparator<Map<String, Object>>() {
                    public int compare(Map<String, Object> arg0, Map<String, Object> arg1) {
                        return ((Date) arg1.get("date")).compareTo(((Date) arg0.get("date")));
                    }
                });
                json.put("myApointer", orderDrives);
                break;
            case 2:
                //查询我的询价
                List<CampaignLeads> inquirys = pluginService.getCampaignLeads(userCode, 1);
                if (CollectionUtils.isNotEmpty(inquirys)) {
                    for (CampaignLeads leads : inquirys) {
                        Map<String, Object> map = new HashMap<String, Object>();
                        map.put("date", DateUtil.format(leads.getCreateDate(), dateFormat));
                        if (leads.getVehicle() != null) {
                            map.put("carType", leads.getVehicle().getName());
                        } else {
                            map.put("carType", "微站活动");
                        }
                        queryPrices.add(map);
                    }
                }
                json.put("inquiry", queryPrices);
                break;
            case 3:
                //我参加过的活动
                List<CampaignRecord> campaigns = pluginService.getCampaignRecord(params);
                if (CollectionUtils.isNotEmpty(campaigns)) {
                    for (CampaignRecord record : campaigns) {
                        Map<String, Object> map = new HashMap<String, Object>();
                        map.put("date", DateUtil.format(record.getCreateDate(), dateFormat));
                        if (record.getCampaign() != null) {
                            map.put("carType", record.getCampaign().getName());
                        } else {
                            map.put("carType", "微站活动");
                        }
                        campaignRecords.add(map);
                    }
                }
                json.put("active", campaignRecords);
                break;
            case 4:
                //我的奖品
                List<Map<String, Object>> receivedList = new ArrayList<Map<String, Object>>();
                List<Map<String, Object>> receiveingList = new ArrayList<Map<String, Object>>();
                WinInfoForm form = new WinInfoForm();
                form.setCurrentPage(0);
                form.setPageSize(100);
                form.setUserCode(userCode);
                //已领取
                form.setStatus(1);
                List<WinInfo> received = winInfoService.getWinInfos(form);
                if (CollectionUtils.isNotEmpty(received)) {
                    for (WinInfo record : received) {
                        Map<String, Object> map = new HashMap<String, Object>();
                        //奖品名称
                        map.put("prize", record.getAwardsName());
                        //获取时间
                        map.put("date", DateUtil.format(record.getCreateTime(), dateFormat));
                        //有效期
                        if (record.getCoupon() != null) {
                            Map couponParam = new HashedMap();
                            couponParam.put("id",record.getCoupon().getId());
                            Coupon coupon = pluginService.getCoupon(couponParam);
                            try{
                                logger.debug("优惠券开始时间："+coupon.getStartDate());
                                logger.info("优惠券结束时间：" + coupon.getEndDate());
                                map.put("period", DateUtil.format(coupon.getEndDate(), dateFormat));
                            }catch (Exception e){
                                logger.error(e.getCause());
                                map.put("period", " ");
                            }

                        } else {
                            Campaign campaign = campaignService.campaignInstance(record.getSourceId());
                            if (campaign != null) {
                                try{
                                    logger.info("活动开始时间："+campaign.getBeginDate());
                                    logger.info("活动开始结束："+campaign.getEndDate());
                                    map.put("period", DateUtil.format(campaign.getEndDate(), dateFormat));
                                }catch (Exception e){
                                    logger.error(e.getCause());
                                    map.put("period", " ");
                                }
                            } else {
                                map.put("period", " ");
                            }
                        }
                        receivedList.add(map);
                    }
                }
                json.put("received", receivedList);
                //未领取
                form.setStatus(0);
                List<WinInfo> receiveing = winInfoService.getWinInfos(form);
                if (CollectionUtils.isNotEmpty(receiveing)) {
                    for (WinInfo record : receiveing) {
                        Map<String, Object> map = new HashMap<String, Object>();
                        //奖品名称
                        map.put("prize", record.getAwardsName());
                        //获取时间
                        map.put("date", DateUtil.format(record.getCreateTime(), dateFormat));
                        //有效期
                        if (record.getCoupon() != null) {
                            Map couponParam = new HashedMap();
                            couponParam.put("id",record.getCoupon().getId());
                            Coupon coupon = pluginService.getCoupon(couponParam);
                            try{
                                logger.debug("优惠券开始时间："+coupon.getStartDate());
                                logger.info("优惠券结束时间：" + coupon.getEndDate());
                                map.put("period", DateUtil.format(coupon.getEndDate(), dateFormat));
                            }catch (Exception e){
                                logger.error(e.getCause());
                                map.put("period", " ");
                            }

                        } else {
                            Campaign campaign = campaignService.campaignInstance(record.getSourceId());
                            if (campaign != null) {
                                try{
                                    logger.info("活动开始时间："+campaign.getBeginDate());
                                    logger.info("活动开始结束："+campaign.getEndDate());
                                    map.put("period", DateUtil.format(campaign.getEndDate(), dateFormat));
                                }catch (Exception e){
                                    logger.error(e.getCause());
                                    map.put("period", " ");
                                }
                            } else {
                                map.put("period", " ");
                            }
                        }
                        receiveingList.add(map);
                    }
                }
                json.put("receiveing", receiveingList);
                break;
            default:
        }

        response.getWriter().print(JsonUtil.toJSON(json));
    }

    /**
     * 通过code获取插件id
     *
     * @throws Exception
     */
    @RequestMapping("getPluginByCode")
    public void getPluginByCode(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html; charset=utf-8");
        //跨域问题
        response.setHeader("Access-Control-Allow-Origin", "*");
        String pluginCode = request.getParameter("code");
        Plugin plugin = pluginService.getByCode(pluginCode);
        if (plugin == null) {
            response.getWriter().print(JsonUtil.toJSON(0));
        } else {
            response.getWriter().print(JsonUtil.toJSON(plugin.getId()));
        }

    }

    /**
     * 通过openid获取用户信息
     *
     * @throws Exception
     */
    @RequestMapping("getUserInfoByOpenid")
    public void getUserInfoByOpenid(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html; charset=utf-8");
        //跨域问题
        response.setHeader("Access-Control-Allow-Origin", "*");
        String wxopenid = request.getParameter("wxopenid");
        Map json = new HashMap();
        if (StringUtils.isNotBlank(wxopenid)) {
            Fans fans = fansService.getFansByOpenid(wxopenid);
            if (fans == null) {
                json.put("code", "failure");
                json.put("message", "微信身份识别失败");
            } else {
                json.put("code", "success");
                json.put("userinfo", fans);
            }
        } else {
            json.put("code", "failure");
            json.put("message", "微信身份识别失败");
        }
        response.getWriter().print(JsonUtil.toJSON(json));

    }


    /********************************************************************************/
    /********************************************************************************/
    /**
     * 临时解决
     * key:appid
     * value:openid/uuid
     * 因为订阅号不能进行网页授权，在手机网页获取不到openid
     *
     * @throws LedpException
     */
    private String getUserCode(HttpServletRequest request, HttpServletResponse response, String pageid) throws LedpException {
        String uuid = "";
        String appid = getAppIdByPageId(pageid);
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            //先获取用户openid
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(appid)) {
                    uuid = cookie.getValue();
                    break;
                }
            }
        }
        //上面都获取不到则生成一个id放入cookie
        if (StringUtils.isBlank(uuid)) {
            uuid = UUID.randomUUID().toString();
            Cookie cookie = new Cookie(appid, uuid);
            cookie.setMaxAge(Integer.MAX_VALUE);
            cookie.setPath("/");
            response.addCookie(cookie);
        }
        return uuid;
    }

    private String getAppIdByPageId(String pageId) throws LedpException {
        long pageid = 0l;
        if (StringUtils.isBlank(pageId)) {
            return "";
        } else {
            try {
                pageid = Long.valueOf(pageId);
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        }

        SitePage sitePage = sitePageService.sitePageInstance(pageid);
        if (sitePage == null) {
            return "";
        } else {
            try {
                PublicNo publicNo = null;
                Site site = sitePage.getSite();
                if (site != null) {
                    publicNo = sitePage.getSite().getPublicNo();
                }
                if (publicNo == null) {
                    Campaign campaign = site.getCampaign();
                    if (campaign != null) {
                        campaign = campaignService.campaignInstance(campaign.getId());
                        if (campaign != null) {
                            publicNo = campaign.getPublicNo();
                        }
                    }
                }
                if (publicNo != null) {
                    return publicNo.getAppid();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }


    private String getSn(HttpServletRequest request, HttpServletResponse response) {
        String uuid = "";
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if ("answer_sn".equals(cookie.getName())) {
                    uuid = cookie.getValue();
                    break;
                }
            }
        }
        if (StringUtils.isBlank(uuid)) {
            uuid = UUID.randomUUID().toString();
            Cookie cookie = new Cookie("answer_sn", uuid);
            cookie.setMaxAge(Integer.MAX_VALUE);
            cookie.setPath("/");
            response.addCookie(cookie);
        }
        return uuid;
    }

    private void cleanSn(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            Cookie cookie = new Cookie("answer_sn", null);
            cookie.setMaxAge(0);
            cookie.setPath("/");
            response.addCookie(cookie);
        }

    }


    private int getNameByCode(String code) {
        if (StringUtils.isBlank(code)) {
            return 0;
        } else if ("carousel".equals(code) || "button".equals(code)
                || "image".equals(code) || "imgText".equals(code)
                || "nav".equals(code) || "table".equals(code)
                || "title".equals(code) || "text".equals(code)) {
            //基本元素插件
            return 1;
        } else if ("rotatyTable".equals(code)) {
            //大转盘
            return 2;
        } else if ("share".equals(code)) {
            //分享有礼
            return 3;
        } else if ("answer".equals(code)) {
            //答题闯关
            return 4;
        } else if ("purchase".equals(code)) {
            //团购
            return 5;
        } else if ("coupon".equals(code)) {
            //优惠券
            return 6;
        }
        return 0;
    }

    private Map getQuestion(List<CheckPoint> checkPoints) {
        Map<String, Object> json = new HashMap<String, Object>();
        List<Map> list = new ArrayList<Map>();
        if (CollectionUtils.isNotEmpty(checkPoints)) {
            for (CheckPoint checkpoint : checkPoints) {
                Map<String, Object> cpMap = new HashMap<String, Object>();
                cpMap.put("level", checkpoint.getId());
                cpMap.put("levelText", checkpoint.getName());
                if (CollectionUtils.isNotEmpty(checkpoint.getTopics())) {
                    List<Map> question = new ArrayList<Map>();
                    for (Topic topic : checkpoint.getTopics()) {
                        Map<String, Object> topicMap = new HashMap<String, Object>();
                        topicMap.put("key", topic.getId());
                        topicMap.put("title", topic.getTitle());
                        topicMap.put("content", topic.getContent());
                        List<Map> options = new ArrayList<Map>();
                        for (TopicOption option : topic.getOption()) {
                            Map<String, Object> optionMap = new HashMap<String, Object>();
                            optionMap.put("key", option.getOption());
                            optionMap.put("text", option.getText());
                            optionMap.put("type", "src".equals(option.getType()) ? "image" : option.getType());
                            options.add(optionMap);
                        }
                        topicMap.put("option", options);
                        question.add(topicMap);
                    }
                    cpMap.put("question", question);
                }
                list.add(cpMap);
            }
        }
        json.put("question", list);
        return json;
    }

    /**
     * 获取指定状态的答题情况
     * 1表示答对的
     *
     * @param answerRecord
     * @param isRight
     * @return
     */
    private int getAnswerCount(List<AnswerRecord> answerRecord, int isRight) {
        int result = 0;
        if (CollectionUtils.isNotEmpty(answerRecord)) {
            for (AnswerRecord record : answerRecord) {
                if (record.getIsRight() == isRight) {
                    result++;
                }
            }
        }
        return result;
    }

}
