package com.citroen.wechat.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.citroen.ledp.domain.Constant;
import com.citroen.ledp.domain.Dealer;
import com.citroen.ledp.domain.Leads;
import com.citroen.ledp.domain.Media;
import com.citroen.ledp.domain.Vehicle;
import com.citroen.ledp.exception.LedpException;
import com.citroen.ledp.service.DealerService;
import com.citroen.ledp.service.VehicleService;
import com.citroen.wechat.api.util.DateUtil;
import com.citroen.wechat.domain.AnswerConfig;
import com.citroen.wechat.domain.Awards;
import com.citroen.wechat.domain.Campaign;
import com.citroen.wechat.domain.CampaignLeads;
import com.citroen.wechat.domain.CampaignRecord;
import com.citroen.wechat.domain.Coupon;
import com.citroen.wechat.domain.Plugin;
import com.citroen.wechat.domain.RotaryTableConfig;
import com.citroen.wechat.domain.Site;
import com.citroen.wechat.domain.SitePage;
import com.citroen.wechat.domain.WinInfo;
import com.citroen.wechat.dto.AwardsLockDto;
import com.citroen.wechat.exception.WechatException;
import com.citroen.wechat.form.SubmitLeadsForm;
import com.citroen.wechat.service.CampaignService;
import com.citroen.wechat.service.IWebService;
import com.citroen.wechat.service.PluginService;
import com.citroen.wechat.service.SitePageService;
import com.citroen.wechat.service.SiteService;

@Service
public class WebServiceImpl implements IWebService {
    private static Logger logger = Logger.getLogger(WebServiceImpl.class);
	private final static String dateFormat = "yyyy-MM-dd HH:mm:ss";
	//大转盘奖品锁
	private static List<AwardsLockDto> lock = new ArrayList<AwardsLockDto>();
	
	@Autowired
	private PluginService pluginService;
	@Autowired
	private SitePageService sitePageService;
	@Autowired
	private SiteService siteService;
	@Autowired
	private CampaignService campaignService;
	@Autowired
	private DealerService dealerService;
	@Autowired
	private VehicleService vehicleService;
	
	@Transactional(rollbackFor=Exception.class)
	public WinInfo submitLeads(SubmitLeadsForm form) throws Exception {
		Map<String,Object> map = new HashMap<String,Object>();
		Map<String,Object> query = new HashMap<String,Object>();
		//长效平台留资
		Leads leads = new Leads();
		//活动留资
		CampaignLeads campaignLeads = new CampaignLeads();
		//活动记录
		CampaignRecord campaignRecord = new CampaignRecord();
		//获奖信息
		WinInfo winInfo = new WinInfo();
		Dealer dealer = null;
		Campaign campaign = null;
		Site site = null;
		
		String name = new String(((String)form.getName()).getBytes("ISO-8859-1"),"UTF-8" );
		form.setName(name);
		
		map = getDealerAndCampaignAndSiteByPageid(form.getPageId());
		if(map.get("dealer") != null){
			dealer = (Dealer) map.get("dealer");
		}else{
			throw new WechatException("参数错误");
		}
		
		if(map.get("campaign") != null){
			campaign = (Campaign) map.get("campaign");
			query.put("campaign", campaign.getId());
			leads.setCampaign(campaign);
			campaignLeads.setCampaign(campaign);
			winInfo.setSourceName(campaign.getName());
		}else{
			site = (Site) map.get("site");
			winInfo.setSourceName("微站");
		}
		
		query.put("userCode", form.getUuid());
		
		if(form.getPluginId() != 13 && form.getPluginId() != 12){
			//查询本次活动获得的奖品
			List<CampaignRecord> campaignRecords = pluginService.getCampaignRecord(query);
			if(CollectionUtils.isNotEmpty(campaignRecords)){
				campaignLeads.setAwards(campaignRecords.get(0).getAwards());
				campaignRecord.setAwards(campaignRecords.get(0).getAwards());
				winInfo.setAwards(campaignRecords.get(0).getAwards());
				winInfo.setAwardsName(campaignRecords.get(0).getAwards().getName());
			}
		}
		
		//优惠券
		if(form.getPluginId() == 13){
			Map queryCoupon = new HashMap();
			queryCoupon.put("sn", form.getUnionCode());
			Coupon coupon = pluginService.getCoupon(queryCoupon);
			campaignLeads.setCoupon(coupon);
			
			long couponCount = pluginService.getCountCouponByCouponId(coupon.getId());
			if(couponCount < coupon.getQuantity()){
				winInfo.setAwardsName(coupon==null?"":coupon.getDescription());
				winInfo.setCoupon(coupon);
			}
			
		//大转盘
		}else if(form.getPluginId() == 9){
			//解锁奖品
			Iterator<AwardsLockDto> iterator = lock.iterator();
			List<AwardsLockDto> unLock = new ArrayList<AwardsLockDto>();
			boolean flag = true;
			while(iterator.hasNext()){
				AwardsLockDto dto = iterator.next();
				if(dto.getAwardsId() == form.getAwardId() && form.getUuid().equals(dto.getUserCode())){
					flag = false;
					unLock.add(dto);
				}
			}
			if(CollectionUtils.isNotEmpty(unLock)){
				lock.removeAll(unLock);
			}
			if(flag == true){
				campaignLeads.setAwards(null);
			}
		//答题闯关
		}else if(form.getPluginId() == 11){
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("pageId", form.getPageId());
			params.put("pluginId", form.getPluginId());
			AnswerConfig answer = pluginService.getAnswerConfig(params);
			if(answer != null && "all".equals(answer.getWinType())){
				//通关奖品
				campaignLeads.setAwards(answer.getPrize());
				campaignRecord.setAwards(answer.getPrize());
				winInfo.setAwards(answer.getPrize());
				winInfo.setAwardsName(answer.getPrize().getName());
			}
		//团购
		}else if(form.getPluginId() == 12){
			query.put("pluginId", form.getPluginId());
			query.put("pageId", form.getPageId());
			query.put("name", "purchaseTitle");
			winInfo.setAwardsName(pluginService.getVal(query));
			Vehicle vehicle = vehicleService.getVehicle(form.getYixiangchexing());
			if(vehicle != null) {
				campaignLeads.setVehicle(vehicle);
				leads.setLedpVehicle(vehicle);
				leads.setLedpSeries(vehicle.getSeries());
			}
		}
		//参与记录
		campaignRecord.setPageId(form.getPageId());
		campaignRecord.setUserCode(form.getUuid());
		campaignRecord.setDealer(dealer);
		campaignRecord.setLargeArea(dealer.getOrganization());
		campaignRecord.setPluginId(form.getPluginId());
		campaignRecord.setCreateDate(new Date());
		campaignRecord.setUserName(form.getName());
	
		//活动留资
		campaignLeads.setPageid(form.getPageId());
		campaignLeads.setDealer(dealer);
		campaignLeads.setLargeArea(dealer.getOrganization());
		campaignLeads.setDealer(dealer);
		campaignLeads.setUserCode(form.getUuid());
		campaignLeads.setPlugin(new Plugin(form.getPluginId()));
		campaignLeads.setCreateDate(new Date());
		campaignLeads.setLeadsName(form.getName());
		campaignLeads.setLeadsPhone(form.getPhone());
		campaignLeads.setLeadsSex(form.getSex());
		if(form.getVehicleCode() > 0){
			campaignLeads.setVehicle(new Vehicle(form.getVehicleCode()));
		}
		
		//奖品记录
		winInfo.setDealer(dealer);
		winInfo.setOrg(dealer.getOrganization());
		winInfo.setName(form.getName());
		winInfo.setPhone(form.getPhone());
		winInfo.setUuid(form.getUuid());
		winInfo.setPlugin(new Plugin(form.getPluginId()));
		winInfo.setPage(new SitePage(form.getPageId()));
		winInfo.setCreateTime(new Date());
		if(campaign != null){
			winInfo.setSourceType(1);
			winInfo.setSourceId(campaign.getId());
		}else{
			winInfo.setSourceType(0);
			winInfo.setSourceId(site.getId());
		}
		
		//保存到长效
		leads.setCreateTime(DateUtil.convert(new Date(), dateFormat));
		leads.setMedia("微信平台");
		leads.setPhone(form.getPhone());
		leads.setUserGender(("man".equals(form.getSex()))?"0":"1");
		leads.setName(form.getName());
		leads.setDateCreate(new Date());
		leads.setLedpMedia(new Media(4L));
		//待跟进
		leads.setLedpFollow(new Constant(4010L));
		//意向级别A
		leads.setLedpIntent(new Constant(2010L));
		leads.setState("1");
		//插件
		leads.setLedpType(new Constant(3120L));
		leads.setType("插件留资");
		leads.setClueId(3L);
		leads.setDealerId(dealer.getId()+"");
		leads.setLedpDealer(dealer);
		leads.setLedpOrg(dealer.getOrganization());
		
		if(!"pc".equals(form.getState())){
			pluginService.saveLeads(leads);
			//保存到活动留资
			pluginService.saveCampaignLeads(campaignLeads);
			if(StringUtils.isNotBlank(winInfo.getAwardsName())){
				pluginService.saveWinInfo(winInfo);
			}
		}
		return winInfo;
	}
	
	/**
	 * 获取网点及活动及站点
	 * @param pageId
	 * @return
	 * @throws LedpException 
	 * @throws WechatException 
	 */
	private Map<String,Object> getDealerAndCampaignAndSiteByPageid(long pageId) throws Exception{
		Map<String,Object> result = new HashMap<String,Object>();
		Dealer dealer = null;
		Campaign campaign = null;
		Site site = null;
		SitePage page = sitePageService.siteInstance(pageId);
		if(page != null && page.getSite() != null){
			site = page.getSite();
			if(site != null){
				site = siteService.siteInstance(site.getId());
				campaign = site.getCampaign();
			}
			if(campaign != null){
				campaign = page.getSite().getCampaign();
				campaign = campaignService.campaignInstance(campaign.getId());
				dealer = campaign.getDealer();
			}else{
				site = page.getSite();
				dealer = site.getDealer();
				if(dealer != null){
					dealer = dealerService.gets(dealer.getId());
				}
			}
		}else{
			throw new WechatException("未查询到页面信息");
		}
		result.put("dealer", dealer);
		result.put("campaign", campaign);
		result.put("site", site);
		return result;
	}
	
	//抽奖
	public synchronized void isWin(RotaryTableConfig config,boolean flag,String uuid,
                                   Map<String, Object> result,CampaignRecord entity,
                                   Map<String, Object> params,Map<String, Object> json) throws Exception{
		//2生成随机数
		int random = (int) (Math.random()*100);
		//3判断是否中奖，几等奖
		int cur = 0;
		//未中奖的奖项
		Awards notAwards = new Awards();
		if(CollectionUtils.isNotEmpty(config.getItems())){
			for(Awards awards : config.getItems()){
				if(awards.getStatus() == 0){
					notAwards = awards;
					break;
				}
			}
		}
		if(CollectionUtils.isNotEmpty(config.getItems())){
			for(Awards awards : config.getItems()){
				if(random > cur && random <= (int)(cur+awards.getWinRate())){
					//中奖了
					//查看是否还有奖品，没有就没中奖了
					params.remove("userCode");
					params.put("awards", awards.getId());
					//查找是否有需要释放的奖品
					Iterator<AwardsLockDto> iterator = lock.iterator();
					List<AwardsLockDto> unLock = new ArrayList<AwardsLockDto>();
					int size = 0;
					while(iterator.hasNext()){
						AwardsLockDto dto = iterator.next();
						if(awards.getId().equals(dto.getAwardsId())){
							size++;
						}
						//5分钟后过期
						if((System.currentTimeMillis()-dto.getCreateDate().getTime()) >= 1000*60*5){
							//释放奖品
							unLock.add(dto);
						}
					}
					if(CollectionUtils.isNotEmpty(unLock)){
						lock.removeAll(unLock);
					}
					
					long _records = pluginService.getTotalCampaignLeadsRecord(params);
					//该奖项是否还有奖品
					if((_records+size) >= awards.getQuantity() || awards.getStatus() == 0){
						//奖品领完了或抽中了谢谢惠顾
						flag = false;
						break;
					}else{
					 	//领奖
						AwardsLockDto lockDto = new AwardsLockDto();
						lockDto.setAwardsId(awards.getId());
						lockDto.setCreateDate(new Date());
						lockDto.setUserCode(uuid);
						//锁住该奖品
						lock.add(lockDto);
						Map<String,Object> voucher = new HashMap<String,Object>();
						voucher.put("id", awards.getId());
						voucher.put("type", awards.getLevel());
						voucher.put("money", awards.getName());
						voucher.put("levelName", StringUtils.isBlank(awards.getLevelName())?"恭喜您，中奖了":awards.getLevelName());
						result.put("voucher", voucher);
						flag = true;
						entity.setAwards(awards);
						break;
					}
				}
				cur = (int) (cur+awards.getWinRate());
			}
			
			//未中奖或抽中的奖项没有奖品了
			if(flag == false){
				Map<String,Object> voucher = new HashMap<String,Object>();
				voucher.put("type", notAwards.getLevel());
				voucher.put("money", notAwards.getName());
				voucher.put("levelName", StringUtils.isBlank(notAwards.getLevelName())?"谢谢惠顾":notAwards.getLevelName());
				result.put("voucher", voucher);
				entity.setAwards(notAwards);
			}
		}else{
			throw new WechatException("未查到奖项配置信息");
		}
		
		if(flag == true){
			//前台中奖标识标识
			json.put("status", 1);
		}else{
			json.put("status", 0);
		}
		
	}
	
	public static List<AwardsLockDto> getLock() {
		return lock;
	}
	public static void setLock(List<AwardsLockDto> lock) {
		WebServiceImpl.lock = lock;
	}

}
