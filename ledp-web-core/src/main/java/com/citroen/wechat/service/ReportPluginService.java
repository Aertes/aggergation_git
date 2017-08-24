package com.citroen.wechat.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.citroen.ledp.dao.mybaits.MybaitsGenericDao;
import com.citroen.ledp.util.ExcelUtil;
import com.citroen.wechat.api.util.DateUtil;
import com.citroen.wechat.domain.AnswerConfig;
import com.citroen.wechat.domain.Awards;
import com.citroen.wechat.domain.Coupon;
import com.citroen.wechat.domain.CouponConfig;
import com.citroen.wechat.domain.PluginBaseConfig;
import com.citroen.wechat.domain.RotaryTableConfig;
import com.citroen.wechat.domain.ShareConfig;
import com.citroen.wechat.form.PluginReportForm;

@Service
public class ReportPluginService {
	private static Log logger = LogFactory.getLog(ReportPluginService.class);
	private final static String dateFormat = "yyyy-MM-dd HH:mm:ss";
	@Autowired
	private MybaitsGenericDao<Long> mybaitsGenericDao;
	@Autowired
	private PluginService pluginService;
	
	//查询汇总数据
	public Map<String,Long> queryTotals(PluginReportForm form) throws Exception{
		long usedTotal = 0l;
		long dealerTotal = 0l;
		long leadsTotal = 0l;
		//查询插件累计使用次数
		String usedSql = "select count(id) count from t_plugin_record pr where 1=1";
		//查询使用该插件的网点数量
		String dealerSql = "select count(DISTINCT dealer) count from t_plugin_record pr where 1=1";
		//查询插件获取留资数
		String leadsSql = "select count(id) count from t_campaign_leads where 1=1";
		if(form.getPluginId() != null){
			usedSql += " and pr.plugin="+form.getPluginId();
			dealerSql += " and pr.plugin="+form.getPluginId();
			leadsSql+=" and pluginid = "+form.getPluginId();
		}else{
			leadsSql+=" and pluginid is not null";
		}
		if(form.getOrgId() != null){
			usedSql += " and pr.org="+form.getOrgId();
			dealerSql += " and pr.org="+form.getOrgId();
			leadsSql+=" and large_area="+form.getOrgId();
		}
		if(form.getDealerId() != null){
			usedSql += " and pr.dealer="+form.getDealerId();
			dealerSql += " and pr.dealer="+form.getDealerId();
			leadsSql+=" and dealer="+form.getDealerId();
		}
		if(form.getStartTime() != null){
			usedSql += " and pr.create_date >='"+DateUtil.convert(form.getStartTime(), dateFormat)+"'";
			dealerSql += " and pr.create_date >='"+DateUtil.convert(form.getStartTime(), dateFormat)+"'";
			leadsSql+=" and create_date >='"+DateUtil.convert(form.getStartTime(), dateFormat)+"'";
		}
		if(form.getEndTime() != null){
			usedSql += " and pr.create_date <='"+DateUtil.convert(form.getEndTime(), dateFormat)+"'";
			dealerSql += " and pr.create_date <='"+DateUtil.convert(form.getEndTime(), dateFormat)+"'";
			leadsSql+=" and create_date <='"+DateUtil.convert(form.getEndTime(), dateFormat)+"'";
		}
		Map<String,Long> usedMap = mybaitsGenericDao.find(usedSql);
		if(!usedMap.isEmpty()){
			try{usedTotal = usedMap.get("count");}catch(Exception e){e.printStackTrace();}
		}
		Map<String,Long> dealerMap = mybaitsGenericDao.find(dealerSql);
		if(!dealerMap.isEmpty()){
			try{dealerTotal = dealerMap.get("count");}catch(Exception e){e.printStackTrace();}
		}
		
		Map<String,Long> leadsMap = mybaitsGenericDao.find(leadsSql);
		if(!leadsMap.isEmpty()){
			try{leadsTotal = leadsMap.get("count");}catch(Exception e){e.printStackTrace();}
		}
		Map<String,Long> totals = new HashMap<String,Long>();
		totals.put("usedTotal",usedTotal);
		totals.put("dealerTotal",dealerTotal);
		totals.put("leadsTotal",leadsTotal);
		return totals;
	}
	
	/*********************大转盘总数******************/
	public int dzpTotalRow(PluginReportForm form){
		StringBuilder sql = new StringBuilder();
		sql.append("select count(pr.id) count");
		sql.append(" from t_plugin_record pr");
		sql.append(" where 1=1");
		sql.append(getCondiction(form));
		
		try {
			Map total =mybaitsGenericDao.find(sql.toString());
			return Integer.parseInt(total.get("count").toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	/*********************大转盘******************/
	public List<Map<String,String>> dzpCampaigns(PluginReportForm form) throws Exception{
		StringBuilder sql = new StringBuilder();
		sql.append("select cp.name name,org.name orgName,dealer.name dealerName");
		sql.append(",cp.id campaign,pr.page page,pr.dealer");
		sql.append(",(select count(cr.id) from t_campaign_record cr where cr.pageid=pr.page and cr.pluginid=pr.plugin) viewer");
		sql.append(",(select count(cl.id) from t_campaign_leads  cl where cl.pageid=pr.page and cl.pluginid=pr.plugin) leads");
		sql.append(",(select count(cl.id) from t_wininfo  cl where cl.page=pr.page and cl.plugin=pr.plugin and awards_name is not null) winner");
		sql.append(" from t_plugin_record pr left join t_campaign cp on cp.id=pr.campaign");
		sql.append(" left join t_dealer dealer on dealer.id = pr.dealer ");
		sql.append(" left join t_organization org on org.id = pr.org ");
		sql.append(" where 1=1");
		sql.append(getCondiction(form));
		if(form.getPageSize()>0){
			sql.append(" limit ").append((form.getCurrentPage()-1)*form.getPageSize()).append(",").append(form.getPageSize());
		}
		
		List<Map> list =mybaitsGenericDao.executeQuery(sql.toString());
		List<Map<String,String>> rs = new ArrayList<Map<String,String>>();

		for(Map row:list){
			Map<String,String> map = new HashMap<String,String>();
			map.put("dealer", row.get("dealer").toString());
			map.put("orgName", row.get("orgName")!=null?row.get("orgName").toString():"");
			map.put("dealerName", row.get("dealerName")!=null?row.get("dealerName").toString():"");
			map.put("pageId", row.get("page")!=null?row.get("page").toString():"");
			//插件来源
			if(row.get("name") == null){
				map.put("sourceName","微站");
				map.put("source", "微站");
			}else{
				map.put("sourceName",row.get("name").toString());
				map.put("source", "活动");
			}
			//参与人数
			map.put("viewer",row.get("viewer").toString());
			//留资数
			map.put("leads",row.get("leads").toString());
			//中奖人数
			map.put("winner",row.get("winner").toString());
			//中奖奖品
			String strawards = "";
			Map<String,Object> condiction = new HashMap<String,Object>();
			condiction.put("pluginId", "9");
			condiction.put("pageId", row.get("page"));
			RotaryTableConfig config = pluginService.getRotaryTableConfig(condiction);
			if(config != null && CollectionUtils.isNotEmpty(config.getItems())){
				for(Awards awards : config.getItems()){
					if(awards.getStatus()==1){
						strawards += awards.getName()+",";
					}
				}
				strawards = strawards.substring(0, strawards.length()-1);
			}
			map.put("award",strawards);
			rs.add(map);
		}
		return rs;
	}
	
	/*********************惠团购******************/
	public int htgTotalRow(PluginReportForm form) throws Exception{
		StringBuilder sql = new StringBuilder();
		sql.append("select count(pr.id) count");
		sql.append(" from t_plugin_record pr ");
		sql.append(" where 1=1");
		sql.append(getCondiction(form));
		try {
			Map total =mybaitsGenericDao.find(sql.toString());
			return Integer.parseInt(total.get("count").toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	/*********************惠团购******************/
	public List<Map<String,String>> htgCampaigns(PluginReportForm form) throws Exception{
		StringBuilder sql = new StringBuilder();
		sql.append("select cp.name name,org.name orgName,dealer.name dealerName");
		sql.append(",cp.id campaign,pr.plugin,pr.page,pr.dealer");
		sql.append(",(select count(cr.id) from t_campaign_record cr where cr.pageid=pr.page and cr.pluginid=pr.plugin) viewer");
		sql.append(",(select count(cl.id) from t_campaign_leads  cl where cl.pageid=pr.page and cl.pluginid=pr.plugin) leads");
		sql.append(",(select count(cl.id) from t_wininfo  cl where cl.page=pr.page and cl.plugin=pr.plugin and awards_name is not null) winner");
		sql.append(" from t_plugin_record pr left join t_campaign cp on cp.id=pr.campaign");
		sql.append(" left join t_dealer dealer on dealer.id = pr.dealer ");
		sql.append(" left join t_organization org on org.id = pr.org ");
		sql.append(" where 1=1");
		sql.append(getCondiction(form));
		if(form.getPageSize() > 0){
			sql.append(" limit ").append((form.getCurrentPage()-1)*form.getPageSize()).append(",").append(form.getPageSize());
		}
		List<Map> list =mybaitsGenericDao.executeQuery(sql.toString());
		List<Map<String,String>> rs = new ArrayList<Map<String,String>>();
		for(Map row:list){
			Map<String,String> map = new HashMap<String,String>();
			map.put("dealer", row.get("dealer").toString());
			map.put("orgName", row.get("orgName")!=null?row.get("orgName").toString():"");
			map.put("dealerName", row.get("dealerName")!=null?row.get("dealerName").toString():"");
			map.put("pageId", row.get("page")!=null?row.get("page").toString():"");
			//插件来源
			if(row.get("name") == null){
				map.put("source","微站");
				map.put("sourceName","微站");
			}else{
				map.put("source","活动");
				map.put("sourceName",row.get("name").toString());
			}
			Map<String,Object> queryParams = new HashMap<String,Object>();
			queryParams.put("pluginId", row.get("plugin"));
			queryParams.put("pageId", row.get("page"));
			List<PluginBaseConfig> configs = pluginService.getBaseConfig(queryParams);
			if(CollectionUtils.isNotEmpty(configs)){
				PluginBaseConfig base = configs.get(0);
				if(CollectionUtils.isNotEmpty(base.getParams())){
					String time = "";
					for(PluginBaseConfig config : base.getParams()){
						
						//团购标题
						if("purchaseTitle".equals(config.getName())){
							map.put("title", config.getValue());
						}
						//团购时间
						if("purchaseStartTime".equals(config.getName())){
							time = config.getValue();
						}
						if("purchaseEndTime".equals(config.getName())){
							time += "-"	+ config.getValue();		
						}
						//团购内容
						if("purchaseDescribe".equals(config.getName())){
							map.put("content", config.getValue());
						}
					}
					map.put("time", time);		
				}
			}
			//参与人数
			map.put("viewer",row.get("viewer").toString());
			//留资数
			map.put("leads",row.get("leads").toString());
			//中奖人数
			map.put("winner",row.get("winner").toString());
			rs.add(map);
		}
		return rs;
	}
	
	/*********************优惠券******************/
	public int yhqTotalRow(PluginReportForm form) throws Exception{
		StringBuilder sql = new StringBuilder();
		sql.append("select count(pr.id) count");
		sql.append(" from t_plugin_record pr ");
		sql.append(" where 1=1");
		sql.append(getCondiction(form));
		try {
			Map total =mybaitsGenericDao.find(sql.toString());
			return Integer.parseInt(total.get("count").toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	/*********************优惠券******************/
	public List<Map<String,String>> yhqCampaigns(PluginReportForm form) throws Exception{
		StringBuilder sql = new StringBuilder();
		sql.append("select cp.name name,org.name orgName,dealer.name dealerName");
		sql.append(",cp.id campaign,pr.plugin,pr.page,pr.dealer");
		sql.append(",(select count(cr.id) from t_campaign_record cr where cr.pageid=pr.page and cr.pluginid=pr.plugin) viewer");
		sql.append(",(select count(cl.id) from t_campaign_leads  cl where cl.pageid=pr.page and cl.pluginid=pr.plugin) leads");
		sql.append(",(select count(cl.id) from t_wininfo  cl where cl.page=pr.page and cl.plugin=pr.plugin and awards_name is not null) winner");
		sql.append(" from t_plugin_record pr left join t_campaign cp on cp.id=pr.campaign");
		sql.append(" left join t_dealer dealer on dealer.id = pr.dealer ");
		sql.append(" left join t_organization org on org.id = pr.org ");
		sql.append(" where 1=1");
		sql.append(getCondiction(form));
		if(form.getPageSize() > 0){
			sql.append(" limit ").append((form.getCurrentPage()-1)*form.getPageSize()).append(",").append(form.getPageSize());
		}
				
		List<Map> list =mybaitsGenericDao.executeQuery(sql.toString());
		List<Map<String,String>> rs = new ArrayList<Map<String,String>>();
		for(Map row:list){
			Map<String,String> map = new HashMap<String,String>();
			map.put("dealer", row.get("dealer").toString());
			map.put("orgName", row.get("orgName")!=null?row.get("orgName").toString():"");
			map.put("dealerName", row.get("dealerName")!=null?row.get("dealerName").toString():"");
			map.put("pageId", row.get("page")!=null?row.get("page").toString():"");
			//插件来源
			if(row.get("name") == null){
				map.put("source","微站");
				map.put("sourceName","微站");
			}else{
				map.put("source","活动");
				map.put("sourceName",row.get("name").toString());
			}
			Map<String,Object> queryParams = new HashMap<String,Object>();
			queryParams.put("pluginId", row.get("plugin"));
			queryParams.put("pageId", row.get("page"));
			//查询优惠券
			CouponConfig config = pluginService.getCouponConfig(queryParams);
			if(config != null && CollectionUtils.isNotEmpty(config.getItems())){
				//优惠券张数
				int quantity = 0;
				//优惠券内容
				String content = "";
				//优惠券面值
				String par = "";
				for(Coupon coupon : config.getItems()){
					quantity += coupon.getQuantity();
					content += coupon.getQuantity()+"张"+coupon.getDescription()+"券,";
					par += coupon.getPar()+",";
				}
				map.put("total", quantity+"");
				map.put("content", content.substring(0,content.length()-1));
				map.put("award", par.substring(0,par.length()-1));
			}
			//参与人数
			map.put("viewer",row.get("viewer").toString());
			//留资数
			map.put("leads",row.get("leads").toString());
			//中奖人数
			map.put("winner",row.get("winner").toString());
			rs.add(map);
		}
		return rs;
	}
	
	/*********************分享有礼******************/
	public int fxylTotalRow(PluginReportForm form) throws Exception{
		StringBuilder sql = new StringBuilder();
		sql.append("select count(pr.id) count");
		sql.append(" from t_plugin_record pr");
		sql.append(" where 1=1");
		sql.append(getCondiction(form));
		try {
			Map total =mybaitsGenericDao.find(sql.toString());
			return Integer.parseInt(total.get("count").toString());
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return 0;
	}
	/*********************分享有礼******************/
	public List<Map<String,String>> fxylCampaigns(PluginReportForm form) throws Exception{
		StringBuilder sql = new StringBuilder();
		sql.append("select cp.name name,org.name orgName,dealer.name dealerName");
		sql.append(",cp.id campaign,pr.page,pr.dealer");
		sql.append(",(select count(cr.id) from t_campaign_record cr where cr.pageid=pr.page and cr.pluginid=pr.plugin) viewer");
		sql.append(",(select count(cl.id) from t_campaign_leads  cl where cl.pageid=pr.page and cl.pluginid=pr.plugin) leads");
		sql.append(",(select count(cl.id) from t_wininfo  cl where cl.page=pr.page and cl.plugin=pr.plugin and awards_name is not null) winner");
		sql.append(" from t_plugin_record pr left join t_campaign cp on cp.id=pr.campaign");
		sql.append(" left join t_dealer dealer on dealer.id = pr.dealer ");
		sql.append(" left join t_organization org on org.id = pr.org ");
		sql.append(" where 1=1");
		sql.append(getCondiction(form));
		if(form.getPageSize() > 0){
			sql.append(" limit ").append((form.getCurrentPage()-1)*form.getPageSize()).append(",").append(form.getPageSize());
		}
				
		List<Map> list =mybaitsGenericDao.executeQuery(sql.toString());
		List<Map<String,String>> rs = new ArrayList<Map<String,String>>();
		for(Map row:list){
			Map<String,String> map = new HashMap<String,String>();
			map.put("dealer", row.get("dealer").toString());
			map.put("orgName", row.get("orgName")!=null?row.get("orgName").toString():"");
			map.put("dealerName", row.get("dealerName")!=null?row.get("dealerName").toString():"");
			map.put("pageId", row.get("page")!=null?row.get("page").toString():"");
			//插件来源
			if(row.get("name") == null){
				map.put("source","微站");
				map.put("sourceName","微站");
			}else{
				map.put("sourceName",row.get("name").toString());
				map.put("source","活动");
			}
			//参与人数
			map.put("viewer",row.get("viewer").toString());
			//留资数
			map.put("leads",row.get("leads").toString());
			//中奖人数
			map.put("winner",row.get("winner").toString());
			//中奖奖品
			String strawards = "";
			Map<String,Object> condiction = new HashMap<String,Object>();
			condiction.put("pluginId", "10");
			condiction.put("pageId", row.get("page"));
			ShareConfig config = pluginService.getShareConfig(condiction);
			if(config != null && CollectionUtils.isNotEmpty(config.getItems())){
				for(Awards awards : config.getItems()){
					strawards += awards.getName()+",";
				}
				strawards = strawards.substring(0, strawards.length()-1);
			}
			map.put("award",strawards);
			//分享平台
			if(row.get("page") != null){
				long pageId = 0l;
				List channels = new ArrayList();
				try{
					pageId = Long.valueOf(row.get("page").toString());
					List<String> medias = pluginService.getShareMedia(pageId);
					if(CollectionUtils.isNotEmpty(medias)){
						for(String media : medias){
							if("firendQuan".equals(media)){
								channels.add("朋友圈");
							}
							if("QQ".equals(media)){
								channels.add("QQ");
							}
							if("weibo".equals(media)){
								channels.add("微博");	
							}
							if("friend".equals(media)){
								channels.add("朋友");
							}
						}
					}
					map.put("channels", CollectionUtils.isEmpty(channels)?"":channels.toString().substring(1, channels.toString().length()-1));
					//分享内容
					condiction.put("name", "shareTitle");
					String content = pluginService.getVal(condiction);
					map.put("content", content);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			rs.add(map);
		}
		return rs;
	}
	
	/*********************答题闯关******************/
	public int dtcgTotalRow(PluginReportForm form) throws Exception{
		StringBuilder sql = new StringBuilder();
		sql.append("select count(pr.id) count");
		sql.append(" from t_plugin_record pr");
		sql.append(" where 1=1");
		sql.append(getCondiction(form));
		try {
			Map total =mybaitsGenericDao.find(sql.toString());
			return Integer.parseInt(total.get("count").toString());
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return 0;
	}
	
	/*********************答题闯关******************/
	public List<Map<String,String>> dtcgCampaigns(PluginReportForm form) throws Exception{
		String strawards = "";
		StringBuilder sql = new StringBuilder();
		sql.append("select cp.name name,org.name orgName,dealer.name dealerName");
		sql.append(",cp.id campaign,pr.plugin,pr.page,pr.dealer");
		sql.append(",(select count(cr.id) from t_campaign_record cr where cr.pageid=pr.page and cr.pluginid=pr.plugin) viewer");
		sql.append(",(select count(cl.id) from t_campaign_leads  cl where cl.pageid=pr.page and cl.pluginid=pr.plugin) leads");
		sql.append(",(select count(cl.id) from t_wininfo  cl where cl.page=pr.page and cl.plugin=pr.plugin and awards_name is not null) winner");
		sql.append(" from t_plugin_record pr left join t_campaign cp on cp.id=pr.campaign");
		sql.append(" left join t_dealer dealer on dealer.id = pr.dealer ");
		sql.append(" left join t_organization org on org.id = pr.org ");
		sql.append(" where 1=1");
		sql.append(getCondiction(form));
		if(form.getPageSize() > 0){
			sql.append(" limit ").append((form.getCurrentPage()-1)*form.getPageSize()).append(",").append(form.getPageSize());
		}
		
		List<Map> list =mybaitsGenericDao.executeQuery(sql.toString());
		List<Map<String,String>> rs = new ArrayList<Map<String,String>>();
		for(Map row:list){
			Map<String,String> map = new HashMap<String,String>();
			map.put("dealer", row.get("dealer").toString());
			map.put("orgName", row.get("orgName")!=null?row.get("orgName").toString():"");
			map.put("dealerName", row.get("dealerName")!=null?row.get("dealerName").toString():"");
			map.put("pageId", row.get("page")!=null?row.get("page").toString():"");
			//插件来源
			if(row.get("name") == null){
				map.put("source","微站");
				map.put("sourceName","微站插件");
			}else{
				map.put("source","活动");
				map.put("sourceName",row.get("name").toString());
			}
			Map<String,Object> queryParams = new HashMap<String,Object>();
			queryParams.put("pluginId", row.get("plugin"));
			queryParams.put("pageId", row.get("page"));
			//查询关卡数
			AnswerConfig config = pluginService.getAnswerConfig(queryParams);
			if(config != null && CollectionUtils.isNotEmpty(config.getCheckPoint())){
				map.put("checkpoint", config.getCheckPoint().size()+"");
				for(Awards awards : config.getPrizeOne()){
					strawards += awards.getName()+",";
				}
				if(strawards.length()>0){
					strawards = strawards.substring(0, strawards.length()-1);
				}
				if("all".equals(config.getWinType())){
					strawards = config.getPrize().getName();
				}
			}else{
				map.put("checkpoint","0");
			}
			//中奖奖品
			map.put("award",strawards);
			//参与人数
			map.put("viewer",row.get("viewer").toString());
			//留资数
			map.put("leads",row.get("leads").toString());
			//中奖人数
			map.put("winner",row.get("winner").toString());
			
			rs.add(map);
		}
		return rs;
	}

	/*********************大转盘******************/
	public SXSSFWorkbook dzpExportCampaigns(PluginReportForm form) throws Exception{
		form.setPageSize(0);
		List<Map<String,String>> campaigns = dzpCampaigns(form);		
		SXSSFWorkbook wb = new SXSSFWorkbook();
        Sheet sheet = wb.createSheet("大转盘插件报表");
        //大区	网点名称	插件应用来源	活动/微站名称	参与人数	留资数	中奖人数	中奖奖品
        String[] titles = new String[]{"大区","网点名称","插件应用来源","活动名称","参与人数","留资数","中奖人数","中奖奖品"}; 
        String[] fields = new String[]{"orgName","dealerName","source","sourceName","viewer","leads","winner","award"}; 
        Integer[] titleWidths = new Integer[]{12,12,12, 10, 15,30,10,10};
        Row row = ExcelUtil.createRow(titles, titleWidths, wb, sheet, 0);
        for(int i = 0; i < campaigns.size(); i++){
        	row = sheet.createRow(i+1);
        	Map<String,String> map = campaigns.get(i);
        	for(int j = 0;j<fields.length;j++){
        		row.createCell(j).setCellValue(map.get(fields[j]));
        	}
        }
		return wb;
	}
	/*********************惠团购******************/
	public SXSSFWorkbook htgExportCampaigns(PluginReportForm form) throws Exception{
		form.setPageSize(0);
		List<Map<String,String>> campaigns = htgCampaigns(form);		
		SXSSFWorkbook wb = new SXSSFWorkbook();
        Sheet sheet = wb.createSheet("惠团购插件报表");
        //大区	网点名称	插件应用来源	活动/微站名称	参与人数	团购标题	团购时间	团购内容
        String[] titles = new String[]{"大区","网点名称","插件应用来源","活动名称","参与人数","留资人数","团购标题","团购时间","团购内容"}; 
        String[] fields = new String[]{"orgName","dealerName","source","sourceName","viewer","leads","title","time","content"}; 
        Integer[] titleWidths = new Integer[]{12,12,12, 10, 15,30,10,10,10};
        Row row = ExcelUtil.createRow(titles, titleWidths, wb, sheet, 0);
        for(int i = 0; i < campaigns.size(); i++){
        	row = sheet.createRow(i+1);
        	Map<String,String> map = campaigns.get(i);
        	for(int j = 0;j<fields.length;j++){
        		row.createCell(j).setCellValue(map.get(fields[j]));
        	}
        }
		return wb;
	}
	/*********************优惠券******************/
	public SXSSFWorkbook yhqExportCampaigns(PluginReportForm form) throws Exception{
		form.setPageSize(0);
		List<Map<String,String>> campaigns = dzpCampaigns(form);		
		SXSSFWorkbook wb = new SXSSFWorkbook();
        Sheet sheet = wb.createSheet("优惠券插件报表");
        //大区	网点名称	插件应用来源	活动/微站名称	参与人数	留资数	优惠券张数	优惠券内容	优惠券面值
        String[] titles = new String[]{"大区","网点名称","插件应用来源","活动名称","参与人数","留资数","优惠券张数","优惠券内容","优惠券面值"}; 
        String[] fields = new String[]{"orgName","dealerName","source","sourceName","viewer","leads","total","content","award"}; 
        Integer[] titleWidths = new Integer[]{12,12,12, 10, 15,30,10,10,10};
        Row row = ExcelUtil.createRow(titles, titleWidths, wb, sheet, 0);
        for(int i = 0; i < campaigns.size(); i++){
        	row = sheet.createRow(i+1);
        	Map<String,String> map = campaigns.get(i);
        	for(int j = 0;j<fields.length;j++){
        		row.createCell(j).setCellValue(map.get(fields[j]));
        	}
        }
		return wb;
	}
	/*********************分享有礼******************/
	public SXSSFWorkbook fxylExportCampaigns(PluginReportForm form) throws Exception{
		form.setPageSize(0);
		List<Map<String,String>> campaigns = dzpCampaigns(form);		
		SXSSFWorkbook wb = new SXSSFWorkbook();
        Sheet sheet = wb.createSheet("分享有礼插件报表");
       	//大区	网点名称	插件应用来源	活动/微站名称	参与人数	留资数	分享平台	分享内容	分享礼品
        String[] titles = new String[]{"大区","网点名称","插件应用来源","活动名称","参与人数","留资数","分享平台","分享内容","分享礼品"}; 
        String[] fields = new String[]{"orgName","dealerName","source","sourceName","viewer","leads","channel","content","award"}; 
        Integer[] titleWidths = new Integer[]{12,12,12, 10, 15,30,10,10};
        Row row = ExcelUtil.createRow(titles, titleWidths, wb, sheet, 0);
        for(int i = 0; i < campaigns.size(); i++){
        	row = sheet.createRow(i+1);
        	Map<String,String> map = campaigns.get(i);
        	for(int j = 0;j<fields.length;j++){
        		row.createCell(j).setCellValue(map.get(fields[j]));
        	}
        }
		return wb;
	}
	/*********************答题闯关******************/
	public SXSSFWorkbook dtcgExportCampaigns(PluginReportForm form) throws Exception{
		form.setPageSize(0);
		List<Map<String,String>> campaigns = dzpCampaigns(form);		
		SXSSFWorkbook wb = new SXSSFWorkbook();
        Sheet sheet = wb.createSheet("分享有礼插件报表");
        //大区	网点名称	插件应用来源	活动/微站名称	参与人数	留资数	中奖人数	关卡数	中奖奖品
        String[] titles = new String[]{"大区","网点名称","插件应用来源","活动名称","参与人数","留资数","中奖人数","中奖奖品"}; 
        String[] fields = new String[]{"orgName","dealerName","source","sourceName","viewer","leads","winner","award"};
        Integer[] titleWidths = new Integer[]{12,12,12, 10, 15,30,10,10};
        Row row = ExcelUtil.createRow(titles, titleWidths, wb, sheet, 0);
        for(int i = 0; i < campaigns.size(); i++){
        	row = sheet.createRow(i+1);
        	Map<String,String> map = campaigns.get(i);
        	for(int j = 0;j<fields.length;j++){
        		row.createCell(j).setCellValue(map.get(fields[j]));
        	}
        }
		return wb;
	}
	
	/*********************导出大转盘留资******************/
	public SXSSFWorkbook dzpExportLeads(PluginReportForm form) throws Exception{
		form.setPageSize(0);
		List<Map<String,String>> leads = this.queryLeads(form);
		SXSSFWorkbook wb = new SXSSFWorkbook();
        Sheet sheet = wb.createSheet("大转盘留资报表");
        String[] titles = new String[]{"大区","网点名称","留资来源","活动名称","插件名称","客户姓名","手机号码","留资时间"}; 
        String[] fields = new String[]{"orgName","dealerName","source","sourceName","pluginName","name","phone","time"};
        Integer[] titleWidths = new Integer[]{10,15,10,10,20,10,10,10,15};
        Row row = ExcelUtil.createRow(titles, titleWidths, wb, sheet, 0);
        for(int i = 0; i < leads.size(); i++){
        	row = sheet.createRow(i+1);
        	Map<String,String> map = leads.get(i);
        	for(int j = 0;j<fields.length;j++){
        		row.createCell(j).setCellValue(map.get(fields[j]));
        	}
        }
		return wb;
	}
	
	/*********************导出惠团购留资******************/
	public SXSSFWorkbook htgExportLeads(PluginReportForm form) throws Exception{
		form.setPageSize(0);
		List<Map<String,String>> leads = this.queryLeads(form);
		SXSSFWorkbook wb = new SXSSFWorkbook();
        Sheet sheet = wb.createSheet("惠团购留资报表");
        String[] titles = new String[]{"大区","网点名称","留资来源","活动名称","插件名称","客户姓名","手机号码","留资时间"}; 
        String[] fields = new String[]{"orgName","dealerName","source","sourceName","pluginName","name","phone","time"};
        Integer[] titleWidths = new Integer[]{10,15,10,10,20,10,10,10,15};
        Row row = ExcelUtil.createRow(titles, titleWidths, wb, sheet, 0);
        for(int i = 0; i < leads.size(); i++){
        	row = sheet.createRow(i+1);
        	Map<String,String> map = leads.get(i);
        	for(int j = 0;j<fields.length;j++){
        		row.createCell(j).setCellValue(map.get(fields[j]));
        	}
        }
		return wb;
	}
	
	/*********************导出优惠券留资******************/
	public SXSSFWorkbook yhqExportLeads(PluginReportForm form) throws Exception{
		form.setPageSize(0);
		List<Map<String,String>> leads = this.queryLeads(form);
		SXSSFWorkbook wb = new SXSSFWorkbook();
        Sheet sheet = wb.createSheet("优惠券留资报表");
        String[] titles = new String[]{"大区","网点名称","留资来源","活动名称","插件名称","客户姓名","手机号码","留资时间"}; 
        String[] fields = new String[]{"orgName","dealerName","source","sourceName","pluginName","name","phone","time"};
        Integer[] titleWidths = new Integer[]{10,15,10,10,20,10,10,10,15};
        Row row = ExcelUtil.createRow(titles, titleWidths, wb, sheet, 0);
        for(int i = 0; i < leads.size(); i++){
        	row = sheet.createRow(i+1);
        	Map<String,String> map = leads.get(i);
        	for(int j = 0;j<fields.length;j++){
        		row.createCell(j).setCellValue(map.get(fields[j]));
        	}
        }
		return wb;
	}
	
	/*********************导出分享有礼留资******************/
	public SXSSFWorkbook fxylExportLeads(PluginReportForm form) throws Exception{
		form.setPageSize(0);
		List<Map<String,String>> leads = this.queryLeads(form);
		SXSSFWorkbook wb = new SXSSFWorkbook();
        Sheet sheet = wb.createSheet("分享有礼留资报表");
        String[] titles = new String[]{"大区","网点名称","留资来源","活动名称","插件名称","客户姓名","手机号码","留资时间"}; 
        String[] fields = new String[]{"orgName","dealerName","source","sourceName","pluginName","name","phone","time"};
        Integer[] titleWidths = new Integer[]{10,15,10,10,20,10,10,10,15};
        Row row = ExcelUtil.createRow(titles, titleWidths, wb, sheet, 0);
        for(int i = 0; i < leads.size(); i++){
        	row = sheet.createRow(i+1);
        	Map<String,String> map = leads.get(i);
        	for(int j = 0;j<fields.length;j++){
        		row.createCell(j).setCellValue(map.get(fields[j]));
        	}
        }
		return wb;
	}
	
	/*********************导出答题闯关留资******************/
	public SXSSFWorkbook dtcgExportLeads(PluginReportForm form) throws Exception{
		form.setPageSize(0);
		List<Map<String,String>> leads = this.queryLeads(form);
		SXSSFWorkbook wb = new SXSSFWorkbook();
		Sheet sheet = wb.createSheet("答题闯关留资报表");
		String[] titles = new String[]{"大区","网点名称","留资来源","活动名称","插件名称","客户姓名","手机号码","留资时间"}; 
	    String[] fields = new String[]{"orgName","dealerName","source","sourceName","pluginName","name","phone","time"};
	    Integer[] titleWidths = new Integer[]{10,15,10,10,20,10,10,10,15};
        Row row = ExcelUtil.createRow(titles, titleWidths, wb, sheet, 0);
        for(int i = 0; i < leads.size(); i++){
        	row = sheet.createRow(i+1);
        	Map<String,String> map = leads.get(i);
        	for(int j = 0;j<fields.length;j++){
        		row.createCell(j).setCellValue(map.get(fields[j]));
        	}
        }
		return wb;
	}
	
	//查询留资数据
	public int leadsTotalRow(PluginReportForm form) throws Exception{
		StringBuilder sql = new StringBuilder();
		sql.append("select count(id) count from t_campaign_leads cl where 1=1 ");
		sql.append(getLeadsCondiction(form));
		try {
			Map total =mybaitsGenericDao.find(sql.toString());
			return Integer.parseInt(total.get("count").toString());
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return 0;
	}
	//查询留资数据
	public List<Map<String,String>> queryLeads(PluginReportForm form) throws Exception{
		StringBuilder sql = new StringBuilder();
		sql.append("select cl.pageid,cl.pluginid,p.name pluginName,");
		sql.append("dealer.name dealerName,org.name orgName,cl.name,");
		sql.append("cl.phone,DATE_FORMAT(cl.create_date, '%Y-%m-%d %H:%i:%s') createDate,");
		sql.append("aw.name awardsName,co.description couponDesc,co.par couponPar,cp.name campaginName");
		sql.append(" from t_campaign_leads cl ");
		sql.append(" left join t_campaign cp on cp.id=cl.campaign");
		sql.append(" left join t_dealer dealer on dealer.id = cl.dealer ");
		sql.append(" left join t_organization org on org.id = cl.large_area ");
		sql.append(" left join t_awards aw on aw.id=cl.awards ");
		sql.append(" left join t_coupon co on co.id=cl.coupon ");
		sql.append(" left join t_plugin p on p.id=cl.pluginid ");
		sql.append(" where 1=1");
		sql.append(getLeadsCondiction(form));
		if(form.getPageSize() > 0){
			sql.append(" limit ").append((form.getCurrentPage()-1)*form.getPageSize()).append(",").append(form.getPageSize());
		}
		List<Map> list = mybaitsGenericDao.executeQuery(sql.toString());
		List<Map<String,String>> rs = new ArrayList<Map<String,String>>();
		for(Map row :list){
			Map<String, Object> condiction = new HashMap<String, Object>();
			condiction.put("pluginId", row.get("pluginid"));
			condiction.put("pageId", row.get("pageid"));
			Map<String,String> map = new HashMap<String,String>();
			map.put("pageid", row.get("pageid")!=null?row.get("pageid").toString():"");
			map.put("orgName", row.get("orgName")!=null?row.get("orgName").toString():"");
			map.put("dealerName", row.get("dealerName")!=null?row.get("dealerName").toString():"");
			map.put("pluginName", row.get("pluginName")!=null?row.get("pluginName").toString():"");
			//插件来源
			if(row.get("campaginName") == null){
				map.put("source","微站");
				map.put("sourceName","微站");
			}else{
				map.put("source","活动");
				map.put("sourceName",row.get("campaginName").toString());
			}
			//参与人名称
			map.put("name",row.get("name")!=null?row.get("name").toString():"");
			//手机号码
			map.put("phone",row.get("phone")!=null?row.get("phone").toString():"");
			//参与时间
			map.put("time",row.get("createDate")!=null?row.get("createDate").toString():"");
			//中奖信息
			map.put("award",row.get("awardsName")!=null?row.get("awardsName").toString():"");
			//团购金额
			if("12".equals(form.getPluginId())){
				condiction.put("name", "purchasemoney");
				String money = pluginService.getVal(condiction);
				map.put("money",StringUtils.isNotBlank(money) ? money : "");
			}
			//优惠券面值 内容
			if("13".equals(form.getPluginId())){
				//优惠券面值
				if(row.get("couponDesc") != null){
					map.put("couponContent",row.get("couponDesc").toString());
				}
				//优惠券内容
				if(row.get("couponPar") != null){
					map.put("couponPar",row.get("couponPar").toString());
				}
				
			}
			//答题闯关
			if("11".equals(form.getPluginId())){
				if(row.get("awardsName") != null){
					//中奖信息
					map.put("award",row.get("awardsName").toString());
				}
			}
			
			rs.add(map);
		}
		return rs;
	}
	
	public int getTotalRow(PluginReportForm form){
		StringBuilder sql = new StringBuilder();
		sql.append("select count(ct) ct from (select count(pr.id) ct");
		sql.append(" from t_plugin_record pr");
		sql.append(" where 1=1");
		sql.append(getCondiction(form));
		sql.append(" group by pr.plugin,pr.dealer,pr.org)a");
		try {
			Map total =mybaitsGenericDao.find(sql.toString());
			return Integer.parseInt(total.get("ct").toString());
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return 0;
	}
	
	/*********************全部插件******************/
	public List<Map<String,String>> getCampaigns(PluginReportForm form) throws Exception{
		StringBuilder sql = new StringBuilder();
		sql.append("select count(pr.id) useCount,pl.name name,org.name orgName,dealer.name dealerName,pl.name");
		sql.append(",cp.id campaign,pr.page page,pr.plugin,pr.dealer");
		sql.append(" from t_plugin_record pr left join t_campaign cp on cp.id=pr.campaign");
		sql.append(" left join t_dealer dealer on dealer.id = pr.dealer ");
		sql.append(" left join t_organization org on org.id = pr.org ");
		sql.append(" left join t_plugin pl on pl.id = pr.plugin ");
		sql.append(" left join t_site_page sp on sp.id = pr.page ");
		sql.append(" where 1=1");
		sql.append(getCondiction(form));
		sql.append(" group by pr.plugin,pr.dealer,pr.org");
		if(form.getPageSize()>0){
			sql.append(" limit ").append((form.getCurrentPage()-1)*form.getPageSize()).append(",").append(form.getPageSize());
		}
		List<Map> list =mybaitsGenericDao.executeQuery(sql.toString());
		//查询参与记录
		sql = new StringBuilder();
		sql.append("SELECT count(cr.id) ct FROM t_plugin_record pr ");
		sql.append("left join t_campaign_record cr on pr.plugin = cr.pluginid and pr.page = cr.pageid");
		sql.append(" where 1=1");
		sql.append(getCondiction(form));
		sql.append(" group by pr.plugin,pr.dealer,pr.org");
		if(form.getPageSize()>0){
			sql.append(" limit ").append((form.getCurrentPage()-1)*form.getPageSize()).append(",").append(form.getPageSize());
		}
		List<Map> viewerList = mybaitsGenericDao.executeQuery(sql.toString());
		//查询留资记录
		sql = new StringBuilder();
		sql.append("SELECT count(cr.id) ct FROM t_plugin_record pr ");
		sql.append("left join t_campaign_leads cr on pr.plugin = cr.pluginid and pr.page = cr.pageid");
		sql.append(" where 1=1");
		sql.append(getCondiction(form));
		sql.append(" group by pr.plugin,pr.dealer,pr.org");
		if(form.getPageSize()>0){
			sql.append(" limit ").append((form.getCurrentPage()-1)*form.getPageSize()).append(",").append(form.getPageSize());
		}
		List<Map> leadsList = mybaitsGenericDao.executeQuery(sql.toString());
		List<Map<String,String>> rs = new ArrayList<Map<String,String>>();
		for(int i=0; i<list.size(); i++){
			Map row = list.get(i);
			String viewer = viewerList.get(i).get("ct").toString();
			String leads = leadsList.get(i).get("ct").toString();
			Map<String,String> map = new HashMap<String,String>();
			map.put("dealer", row.get("dealer").toString());
			map.put("orgName", row.get("orgName")!=null?row.get("orgName").toString():"");
			map.put("dealerName", row.get("dealerName")!=null?row.get("dealerName").toString():"");
			map.put("name", row.get("name")!=null?row.get("name").toString():"");
			map.put("pageId", row.get("page")!=null?row.get("page").toString():"");
			map.put("pluginId", row.get("plugin")!=null?row.get("plugin").toString():"");
			map.put("pluginName", row.get("pluginName")!=null?row.get("pluginName").toString():"");
			//参与人数
			map.put("viewer",viewer);
			//留资数
			map.put("leads",leads);
			//使用次数
			map.put("useCount",row.get("useCount").toString());
			rs.add(map);
		}
		return rs;
	}
	
	/*********************答题闯关******************/
	public SXSSFWorkbook exportAllCampaigns(PluginReportForm form) throws Exception{
		form.setPageSize(0);
		List<Map<String,String>> campaigns = getCampaigns(form);		
		SXSSFWorkbook wb = new SXSSFWorkbook();
        Sheet sheet = wb.createSheet("分享有礼插件报表");
       //大区	网点名称	插件名称	使用次数	参与人数	获取留资数
        String[] titles = new String[]{"大区","网点名称","插件名称","使用次数","参与人数","获取留资数"}; 
        String[] fields = new String[]{"orgName","dealerName","name","useCount","viewer","leads"};
        Integer[] titleWidths = new Integer[]{12,12,12, 10, 15,30};
        Row row = ExcelUtil.createRow(titles, titleWidths, wb, sheet, 0);
        for(int i = 0; i < campaigns.size(); i++){
        	row = sheet.createRow(i+1);
        	Map<String,String> map = campaigns.get(i);
        	for(int j = 0;j<fields.length;j++){
        		row.createCell(j).setCellValue(map.get(fields[j]));
        	}
        }
		return wb;
	}
	
	private String getCondiction(PluginReportForm form){
		StringBuilder sql = new StringBuilder();
		if(form.getPluginId() != null){
			sql.append(" and pr.plugin="+form.getPluginId());
		}
		if(form.getOrgId() != null){
			sql.append(" and pr.org="+form.getOrgId());
		}
		if(form.getDealerId() != null){
			sql.append(" and pr.dealer="+form.getDealerId());
		}
		if(form.getStartTime() != null){
			sql.append(" and pr.create_date >='"+DateUtil.convert(form.getStartTime(), dateFormat)+"'");
		}
		if(form.getEndTime() != null){
			sql.append(" and pr.create_date <='"+DateUtil.convert(form.getEndTime(), dateFormat)+"'");
		}
		return sql.toString();
	}
	
	private String getLeadsCondiction(PluginReportForm form){
		StringBuilder sql = new StringBuilder();
		if(form.getPluginId() != null){
			sql.append(" and cl.pluginid="+form.getPluginId());
		}
		if(form.getOrgId() != null){
			sql.append(" and cl.large_area="+form.getOrgId());
		}
		if(form.getDealerId() != null){
			sql.append(" and cl.dealer="+form.getDealerId());
		}
		if(form.getPageId() != null){
			sql.append(" and cl.pageid="+form.getPageId());
		}
		if(StringUtils.isNotBlank(form.getPhone())){
			sql.append(" and cl.phone='"+form.getPhone()+"'");
		}
		if(StringUtils.isNotBlank(form.getCustomName())){
			sql.append(" and cl.name like '%"+form.getCustomName()+"%'");
		}
		if(form.getStartTime() != null){
			sql.append(" and cl.create_date >='"+DateUtil.convert(form.getStartTime(), dateFormat)+"'");
		}
		if(form.getEndTime() != null){
			sql.append(" and cl.create_date <='"+DateUtil.convert(form.getEndTime(), dateFormat)+"'");
		}
		return sql.toString();
	}
	
}
