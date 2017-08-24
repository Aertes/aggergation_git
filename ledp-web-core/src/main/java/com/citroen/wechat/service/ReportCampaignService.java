package com.citroen.wechat.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.citroen.ledp.dao.mybaits.MybaitsGenericDao;
import com.citroen.ledp.domain.Dealer;
import com.citroen.ledp.domain.Organization;
import com.citroen.ledp.exception.LedpException;
import com.citroen.ledp.service.DealerService;
import com.citroen.ledp.service.OrganizationService;
import com.citroen.ledp.util.DateUtil;
import com.citroen.ledp.util.ExcelUtil;
import com.citroen.wechat.domain.Campaign;
import com.citroen.wechat.domain.Plugin;
import com.citroen.wechat.domain.PluginType;

/** 
 * @ClassName: ReportCampaignService 
 * @Description: TODO(的大卫杜夫) 
 * @author 杨少波
 * @date 2015年6月30日 下午9:24:38 
 * 
 */
@Service
public class ReportCampaignService {
    private static Logger logger = Logger.getLogger(ReportCampaignService.class);
	@Autowired
	private MybaitsGenericDao<Long> mybaitsGenericDao;
	@Autowired
	private OrganizationService organizationService;
	@Autowired
	private DealerService dealerService;
	@Autowired
	private CampaignService campaignService;
	@Autowired
	private PluginService pluginService;
	
	public Map getListCampaign(Long daelerId,Long orgId,Long cmpaignId,Date statDate,Date endDate){
		Map map=new HashMap();
		
		Date date=DateUtil.getAfterDay(new Date());
		String strDate=DateUtil.format(date, "yyyy-MM-dd");
		Calendar c = Calendar.getInstance(Locale.CHINA);
		c.setTime(DateUtil.parse(strDate, "yyyy-MM-dd"));
		Date endDate1=c.getTime();
		
		strDate=DateUtil.format(endDate1,"yyyy-MM-dd 00:00:00");
		
		String sql1=getSql(daelerId,orgId,cmpaignId,statDate,endDate);
		String sqlpe=getPeoSql(daelerId,orgId,cmpaignId,statDate,endDate);
		String sqlle=getLeadsSql(daelerId,orgId,cmpaignId,statDate,endDate);
		String sql="select SUBSTRING(DATE_FORMAT( begindate, '%Y-%m-%d %h:%i:%s'),1,10) as day,count(id) as ct from t_campaign where 1=1 "+sql1+" group by SUBSTRING(DATE_FORMAT( begindate, '%Y-%m-%d %h:%i:%s'),1,10) order by SUBSTRING(DATE_FORMAT( begindate, '%Y-%m-%d %h:%i:%s'),1,10)";
		String peSql="select SUBSTRING(DATE_FORMAT( cr.create_date, '%Y-%m-%d %h:%i:%s'),1,10) as day,count(cr.id) as ct from t_campaign_record cr LEFT JOIN t_campaign c on c.id=cr.campaign where c.begindate<'"+strDate+"' and cr.campaign is not null "+sqlpe+" group by SUBSTRING(DATE_FORMAT( cr.create_date, '%Y-%m-%d %h:%i:%s'),1,10) order by SUBSTRING(DATE_FORMAT( cr.create_date, '%Y-%m-%d %h:%i:%s'),1,10)";
		String leSql="select SUBSTRING(DATE_FORMAT( cl.create_date, '%Y-%m-%d %h:%i:%s'),1,10) as day,count(cl.id) as ct from t_campaign_leads cl LEFT JOIN t_campaign c on c.id=cl.campaign where c.begindate<'"+strDate+"' and cl.campaign is not null "+sqlle+" group by SUBSTRING(DATE_FORMAT( cl.create_date, '%Y-%m-%d %h:%i:%s'),1,10) order by SUBSTRING(DATE_FORMAT( cl.create_date, '%Y-%m-%d %h:%i:%s'),1,10)";
		
		String csqls="select count(id) as ct from t_campaign where 1=1 "+sql1;
		String pesqls="select count(cr.id) as ct from t_campaign_record cr LEFT JOIN t_campaign c on c.id=cr.campaign where c.begindate<'"+strDate+"' and  cr.campaign is not null " +sqlpe;
		String lesqls="select count(cl.id) as ct from t_campaign_leads cl LEFT JOIN t_campaign c on c.id=cl.campaign where c.begindate<'"+strDate+"' and cl.campaign is not null "+sqlle;
		
		try {
			List<Map> list = mybaitsGenericDao.executeQuery(sql);
			List<Map> peList=mybaitsGenericDao.executeQuery(peSql);
			List<Map> leList=mybaitsGenericDao.executeQuery(leSql);
			
			List<Map> clists = mybaitsGenericDao.executeQuery(csqls);
			List<Map> peLists=mybaitsGenericDao.executeQuery(pesqls);
			List<Map> leLists=mybaitsGenericDao.executeQuery(lesqls);
			
			String statDateStr=DateUtil.format(statDate,"yyyy-MM-dd");
			String endDateStr=DateUtil.format(endDate,"yyyy-MM-dd");
			
			List<Map> list1=new ArrayList<Map>();
			List<String> listDate=new ArrayList<String>();
			List<Integer> listCampaign=new ArrayList<Integer>();
			List<Integer> listPepole=new ArrayList<Integer>();
			List<Integer> listLead=new ArrayList<Integer>();
			
			
			String dealerName="";
			String orgName="";
			if(daelerId!=null){
				map.put("daelerId", daelerId);
				Dealer dealer=dealerService.getDealerBydealerId(daelerId);
				dealerName=dealer.getName();
				orgName=dealer.getOrganization().getName();
			}else{
				if(orgId!=1){
					map.put("orgId", orgId);
					Organization org=organizationService.get(orgId);
					orgName=org.getName();
				}
			}
			
			Map<String,Integer> cmap=new HashMap<String,Integer>();
			Map<String,Integer> pemap=new HashMap<String,Integer>();
			Map<String,Integer> lemap=new HashMap<String,Integer>();
			
			//初始化
			
			while(statDate.before(endDate)){
				
				String day=DateUtil.format(statDate,"yyyy-MM-dd");
				cmap.put(day, 0);
				pemap.put(day, 0);
				lemap.put(day, 0);
				statDate=DateUtil.getAfterDay(statDate);
			}
			for(Map m:list){
				String day = m.get("day").toString();
				int count = Integer.parseInt(m.get("ct").toString());
				cmap.put(day, count);
			}
			for(Map m:peList){
				String day = m.get("day").toString();
				int count = Integer.parseInt(m.get("ct").toString());
				pemap.put(day, count);
			}
			int les=0;
			for(Map m:leList){
				String day = m.get("day").toString();
				int count = Integer.parseInt(m.get("ct").toString());
				les+=count;
				lemap.put(day, count);
			}
			
			for(Map mapList:clists){
				map.put("ccampaigns", mapList.get("ct"));
			}
			for(Map mapList:peLists){
				map.put("cpepoles", mapList.get("ct"));
			}
			for(Map mapList:leLists){
				map.put("cleads", mapList.get("ct"));
			}
			
			Map map2=sortMapByKey(cmap);
			Set<String> keySet = map2.keySet(); 
			Iterator<String> iter = keySet.iterator(); 
			while(iter.hasNext()){
				String day=iter.next();;
				Map map1=new HashMap();
				map1.put("day", day);
				map1.put("orgName", orgName);
				map1.put("dealerName", dealerName);
				map1.put("cm", cmap.get(day));
				map1.put("pe", pemap.get(day));
				map1.put("le", lemap.get(day));
				list1.add(map1);
				
				listDate.add(day);
				listCampaign.add( cmap.get(day));
				listPepole.add(pemap.get(day));
				listLead.add(lemap.get(day));
				
			}
			map.put("list", list1);
			map.put("date", listDate);
			map.put("campaigns", listCampaign);
			map.put("pepoles",listPepole);
			map.put("leads",listLead);
		} catch (LedpException e) {
            logger.error("异常信息：" + e.getMessage());
		}
		
		return map;
	}
	
	public Map getListCampaignLeads(Long daelerId,Long orgId,Long cmpaignId,Date statDate,Date endDate){
		Map map=new HashMap();
		Date date=DateUtil.getAfterDay(new Date());
		String strDate=DateUtil.format(date, "yyyy-MM-dd");
		Calendar c = Calendar.getInstance(Locale.CHINA);
		c.setTime(DateUtil.parse(strDate, "yyyy-MM-dd"));
		Date endDate1=c.getTime();
		
		String sqlpe=getLeadsSql(daelerId,orgId,cmpaignId,statDate,endDate);
		String leSql="select SUBSTRING(DATE_FORMAT( cl.create_date, '%Y-%m-%d %h:%i:%s'),1,10) as day,count(cl.id) as ct from t_campaign_leads cl LEFT JOIN t_campaign c on c.id=cl.campaign where c.begindate<'"+strDate+"' and cl.campaign is not null "+sqlpe+" group by SUBSTRING(DATE_FORMAT( cl.create_date, '%Y-%m-%d %h:%i:%s'),1,10) order by SUBSTRING(DATE_FORMAT( cl.create_date, '%Y-%m-%d %h:%i:%s'),1,10)";
		
		String lesqls="select count(cl.id) as ct from t_campaign_leads cl LEFT JOIN t_campaign c on c.id=cl.campaign where c.begindate<'"+strDate+"' and cl.campaign is not null "+sqlpe;
		try {
			List<Map> leList=mybaitsGenericDao.executeQuery(leSql);
			List<Map> leLists=mybaitsGenericDao.executeQuery(lesqls);
			
			String statDateStr=DateUtil.format(statDate,"yyyy-MM-dd");
			String endDateStr=DateUtil.format(endDate,"yyyy-MM-dd");
			
			List<Map> list1=new ArrayList<Map>();
			List<String> listDate=new ArrayList<String>();
			List<Integer> listLead=new ArrayList<Integer>();
			
			
			String dealerName="";
			String orgName="";
			if(daelerId!=null){
				map.put("daelerId", daelerId);
				Dealer dealer=dealerService.getDealerBydealerId(daelerId);
				dealerName=dealer.getName();
				orgName=dealer.getOrganization().getName();
			}else{
				if(orgId!=1){
					map.put("orgId", orgId);
					Organization org=organizationService.get(orgId);
					orgName=org.getName();
				}
			}
			
			Map<String,Integer> lemap=new HashMap<String,Integer>();
			
			//初始化
			
			while(statDate.before(endDate)){
				
				String day=DateUtil.format(statDate,"yyyy-MM-dd");
				lemap.put(day, 0);
				statDate=DateUtil.getAfterDay(statDate);
			}
			int les=0;
			for(Map m:leList){
				String day = m.get("day").toString();
				int count = Integer.parseInt(m.get("ct").toString());
				les+=count;
				lemap.put(day, count);
			}
			
			for(Map mapList:leLists){
				map.put("lleads", mapList.get("ct"));
			}
			
			Map map2=sortMapByKey(lemap);
			Set<String> keySet = map2.keySet(); 
			Iterator<String> iter = keySet.iterator(); 
			while(iter.hasNext()){
				String day=iter.next();;
				Map map1=new HashMap();
				map1.put("day", day);
				map1.put("orgName", orgName);
				map1.put("dealerName", dealerName);
				map1.put("le", lemap.get(day));
				list1.add(map1);
				
				listDate.add(day);
				listLead.add(lemap.get(day));
				
			}
			map.put("list", list1);
			map.put("date", listDate);
			map.put("leads",listLead);
		} catch (LedpException e) {
            logger.error("异常信息：" + e.getMessage());
		}
		return map;
	}
	
	public Map getListCampaignPlugins(Long daelerId,Long orgId,Long cmpaignId,Date statDate,Date endDate,Long pluginTypeId,Long pluginId){
		Map map =new HashMap();
		
		Date date=DateUtil.getAfterDay(new Date());
		String strDate=DateUtil.format(date, "yyyy-MM-dd");
		Calendar c1 = Calendar.getInstance(Locale.CHINA);
		c1.setTime(DateUtil.parse(strDate, "yyyy-MM-dd"));
		Date endDate1=c1.getTime();
		
		String sqlpe=getPluginSql(daelerId,orgId,cmpaignId,statDate,endDate,pluginTypeId,pluginId);
		String peSql="select SUBSTRING(DATE_FORMAT( cl.create_date, '%Y-%m-%d %h:%i:%s'),1,10) as day,count(cl.id) as ct from t_campaign_record cl LEFT JOIN t_campaign c on c.id=cl.campaign where c.begindate<'"+strDate+"' and cl.pluginid  is not null and cl.campaign is not null "+sqlpe+" group by SUBSTRING(DATE_FORMAT( cl.create_date, '%Y-%m-%d %h:%i:%s'),1,10) order by SUBSTRING(DATE_FORMAT( cl.create_date, '%Y-%m-%d %h:%i:%s'),1,10)";
		String leSql="select SUBSTRING(DATE_FORMAT( cl.create_date, '%Y-%m-%d %h:%i:%s'),1,10) as day,count(cl.id) as ct from t_campaign_leads cl LEFT JOIN t_campaign c on c.id=cl.campaign where c.begindate<'"+strDate+"' and cl.pluginid  is not null and cl.campaign is not null "+sqlpe+" group by SUBSTRING(DATE_FORMAT( cl.create_date, '%Y-%m-%d %h:%i:%s'),1,10) order by SUBSTRING(DATE_FORMAT( cl.create_date, '%Y-%m-%d %h:%i:%s'),1,10)";
		String pesqls="select count(cl.id) as ct from t_campaign_record cl LEFT JOIN t_campaign c on c.id=cl.campaign where c.begindate<'"+strDate+"' and cl.pluginid is not null and  cl.campaign is not null " +sqlpe;
		String pluginLeadssql="select count(cl.id) as ct from t_campaign_leads cl LEFT JOIN t_campaign c on c.id=cl.campaign where c.begindate<'"+strDate+"' and cl.pluginid is not null and cl.campaign is not null "+sqlpe;
		try {;
			List<Map> peList=mybaitsGenericDao.executeQuery(peSql);
			List<Map> leList=mybaitsGenericDao.executeQuery(leSql);
			List<Map> peLists=mybaitsGenericDao.executeQuery(pesqls);
			List<Map> lePluginList=mybaitsGenericDao.executeQuery(pluginLeadssql);
			
			String statDateStr=DateUtil.format(statDate,"yyyy-MM-dd");
			String endDateStr=DateUtil.format(endDate,"yyyy-MM-dd");
			
			List<Map> list1=new ArrayList<Map>();
			List<String> listDate=new ArrayList<String>();
			List<Integer> listPepole=new ArrayList<Integer>();
			List<Integer> listLead=new ArrayList<Integer>();
			
			
			String dealerName="";
			String orgName="";
			if(daelerId!=null){
				map.put("daelerId", daelerId);
				Dealer dealer=dealerService.getDealerBydealerId(daelerId);
				dealerName=dealer.getName();
				orgName=dealer.getOrganization().getName();
			}else{
				if(orgId!=1){
					map.put("orgId", orgId);
					Organization org=organizationService.get(orgId);
					orgName=org.getName();
				}
			}
			String cmpaignName="";
			if(cmpaignId!=null){
				map.put("cmpaignId", cmpaignId);
				Campaign c=campaignService.campaignInstance(cmpaignId);
				cmpaignName=c.getName();
			}
			String pluginName="";
			if(pluginId!=null){
				try {
					Plugin p=pluginService.get(pluginId);
					pluginName=p.getName();
					map.put("pluginId", pluginId);
				} catch (Exception e) {
                    logger.error("异常信息：" + e.getMessage());
				}
			}
			Map<String,Integer> pemap=new HashMap<String,Integer>();
			Map<String,Integer> lemap=new HashMap<String,Integer>();
			
			//初始化
			
			while(statDate.before(endDate)){
				
				String day=DateUtil.format(statDate,"yyyy-MM-dd");
				pemap.put(day, 0);
				lemap.put(day, 0);
				statDate=DateUtil.getAfterDay(statDate);
			}
			for(Map m:peList){
				String day = m.get("day").toString();
				int count = Integer.parseInt(m.get("ct").toString());
				pemap.put(day, count);
			}
			int les=0;
			for(Map m:leList){
				String day = m.get("day").toString();
				int count = Integer.parseInt(m.get("ct").toString());
				les+=count;
				lemap.put(day, count);
			}
			
			for(Map mapList:peLists){
				map.put("ppepoles", mapList.get("ct"));
			}
			for(Map mapList:lePluginList){
				map.put("lePlugins", mapList.get("ct"));
			}
			Map map2=sortMapByKey(pemap);
			Set<String> keySet = map2.keySet(); 
			Iterator<String> iter = keySet.iterator(); 
			while(iter.hasNext()){
				String day=iter.next();;
				Map map1=new HashMap();
				map1.put("day", day);
				map1.put("orgName", orgName);
				map1.put("dealerName", dealerName);
				map1.put("cmpaignName", cmpaignName);
				map1.put("pluginName", pluginName);
				map1.put("pe", pemap.get(day));
				map1.put("le", lemap.get(day));
				list1.add(map1);
				
				listDate.add(day);
				listPepole.add(pemap.get(day));
				listLead.add(lemap.get(day));
				
			}
			map.put("list", list1);
			map.put("date", listDate);
			map.put("pepoles",listPepole);
			map.put("leads",listLead);
		} catch (LedpException e) {
            logger.error("异常信息：" + e.getMessage());
		}
		return map;
	}
	public static String getSql(Long daelerId,Long orgId,Long cmpaignId,Date statDate,Date endDate){
		StringBuffer query = new StringBuffer();
		
		if(daelerId!=null){
			query.append(" and dealer="+daelerId);
		}else{
			if(orgId!=1){
				query.append(" and (org="+orgId+" or exists(select 1 from t_dealer where id= dealer and id in(select id from t_dealer where organization="+orgId+" )))");
			}
		}
		
		if(cmpaignId!=null){
			query.append(" and id="+cmpaignId);
		}
		
		if(statDate!=null){
			query.append(" and begindate>='"+DateUtil.format(statDate,"yyyy-MM-dd 00:00:00")+"'");
		}
		if(endDate!=null){
			query.append(" and begindate<'"+DateUtil.format(endDate,"yyyy-MM-dd 00:00:00")+"'");
		}
		return query.toString();
	}
	public String getPeoSql(Long daelerId,Long orgId,Long cmpaignId,Date statDate,Date endDate){
		StringBuffer query = new StringBuffer();
		
		if(daelerId!=null){
			query.append(" and cr.dealer="+daelerId);
		}else{
			if(orgId!=1){
				query.append(" and (cr.large_area="+orgId+" or exists(select 1 from t_dealer where id= cr.dealer and id in(select id from t_dealer where organization="+orgId+" )))");
			}
		}
		
		if(cmpaignId!=null){
			query.append(" and cr.campaign="+cmpaignId);
		}
		
		if(statDate!=null){
			query.append(" and cr.create_date>='"+DateUtil.format(statDate,"yyyy-MM-dd 00:00:00")+"'");
		}
		if(endDate!=null){
			query.append(" and cr.create_date<'"+DateUtil.format(endDate,"yyyy-MM-dd 00:00:00")+"'");
		}
		return query.toString();
	}
	public String getLeadsSql(Long daelerId,Long orgId,Long cmpaignId,Date statDate,Date endDate){
		StringBuffer query = new StringBuffer();
		
		if(daelerId!=null){
			query.append(" and cl.dealer="+daelerId);
		}else{
			if(orgId!=1){
				query.append(" and (cl.large_area="+orgId+" or exists(select 1 from t_dealer where id= cl.dealer and id in(select id from t_dealer where organization="+orgId+" )))");
			}
		}
		
		if(cmpaignId!=null){
			query.append(" and cl.campaign="+cmpaignId);
		}
		
		if(statDate!=null){
			query.append(" and cl.create_date>='"+DateUtil.format(statDate,"yyyy-MM-dd 00:00:00")+"'");
		}
		if(endDate!=null){
			query.append(" and cl.create_date<'"+DateUtil.format(endDate,"yyyy-MM-dd 00:00:00")+"'");
		}
		return query.toString();
	}
	public String getPluginSql(Long daelerId,Long orgId,Long cmpaignId,Date statDate,Date endDate,Long pluginTypeId,Long pluginId){
		StringBuffer query = new StringBuffer();
		
		if(daelerId!=null){
			query.append(" and cl.dealer="+daelerId);
		}else{
			if(orgId!=1){
				query.append(" and (cl.large_area="+orgId+" or exists(select 1 from t_dealer where id= cl.dealer and id in(select id from t_dealer where organization="+orgId+" )))");
			}
		}
		
		if(cmpaignId!=null){
			query.append(" and cl.campaign="+cmpaignId);
		}
		
		if(statDate!=null){
			query.append(" and cl.create_date>='"+DateUtil.format(statDate,"yyyy-MM-dd 00:00:00")+"'");
		}
		if(endDate!=null){
			query.append(" and cl.create_date<'"+DateUtil.format(endDate,"yyyy-MM-dd 00:00:00")+"'");
		}
		if(pluginId!=null){
			query.append(" and cl.pluginid="+pluginId);
		}else{
			if(pluginTypeId!=null){
				query.append(" and cl.pluginid in (select id from t_plugin where type="+pluginTypeId+") ");
			}
		}
		return query.toString();
	}
	public static Map<String, Integer> sortMapByKey(Map<String, Integer> map) {  
        if (map == null || map.isEmpty()) {  
            return null;  
        }  
        Map<String, Integer> sortMap = new TreeMap<String, Integer>(new MapKeyComparator());  
        sortMap.putAll(map);  
        return sortMap;  
    }  
	public List<PluginType> getPlugin() throws Exception{
		String query = "select * from t_plugin_type where id>1 order by sort";
		List<PluginType> pluginTypes = mybaitsGenericDao.executeQuery(PluginType.class, query);
		return pluginTypes;
	}
	public List<Plugin> getPlugins(Long pluginTypeId) throws Exception{
		String query = "select * from t_plugin where type="+pluginTypeId+" order by sort";
		List<Plugin> plugins = mybaitsGenericDao.executeQuery(Plugin.class, query);
		return plugins;
	}
	public HSSFWorkbook createCampaignWorkbook(Map map) {
		HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet("活动效果");
        int lenght=4;
        if(map.containsKey("daelerId")){
        	lenght+=2;
        }else if(map.containsKey("orgId")){
        	lenght+=1;
        }
        String[] titles =new String[lenght];
        Integer[] titleWidths = new Integer[lenght];
        titles[0]="时间";
        titleWidths[0]=15;
        if(map.containsKey("daelerId")){
        	titles[1]="大区";
        	titles[2]="网点";
        	titles[3]="开展活动数量";
        	titles[5]="活动留资数";
        	titles[4]="活动参与人数";
        	
        	titleWidths[1]=30;
        	titleWidths[2]=30;
        	titleWidths[3]=15;
        	titleWidths[4]=15;
        	titleWidths[5]=15;
        }else if(map.containsKey("orgId")){
        	titles[1]="大区";
        	titles[2]="开展活动数量";
        	titles[4]="活动留资数";
        	titles[3]="活动参与人数";
        	
        	titleWidths[1]=30;
        	titleWidths[2]=15;
        	titleWidths[3]=15;
        	titleWidths[4]=15;
        }else{
        	titles[1]="开展活动数量";
        	titles[3]="活动留资数";
        	titles[2]="活动参与人数";
        	
        	titleWidths[1]=15;
        	titleWidths[2]=15;
        	titleWidths[3]=15;
        }
        HSSFRow row = ExcelUtil.createHSSFRow(titles, titleWidths, wb, sheet, 0);
        int rowCount = 1;
        List list=(List) map.get("list");
        for(int i=0;i<list.size();i++){
        	Map map1=(Map) list.get(i);
        	row = sheet.createRow(rowCount);
        	row.createCell(0).setCellValue(map1.get("day").toString());
        	if(!"".equals(map1.get("dealerName"))){
        		row.createCell(1).setCellValue(map1.get("orgName").toString());
        		row.createCell(2).setCellValue(map1.get("dealerName").toString());
        		row.createCell(3).setCellValue(Integer.valueOf(map1.get("cm").toString()));
        		row.createCell(4).setCellValue(Integer.valueOf(map1.get("pe").toString()));
        		row.createCell(5).setCellValue(Integer.valueOf(map1.get("le").toString()));
        	}else if(!"".equals(map1.get("orgName"))){
        		row.createCell(1).setCellValue(map1.get("orgName").toString());
        		row.createCell(2).setCellValue(Integer.valueOf(map1.get("cm").toString()));
        		row.createCell(3).setCellValue(Integer.valueOf(map1.get("pe").toString()));
        		row.createCell(4).setCellValue(Integer.valueOf(map1.get("le").toString()));
        	}else{
        		row.createCell(1).setCellValue(Integer.valueOf(map1.get("cm").toString()));
        		row.createCell(2).setCellValue(Integer.valueOf(map1.get("pe").toString()));
        		row.createCell(3).setCellValue(Integer.valueOf(map1.get("le").toString()));
        	}
        	rowCount++;
        }
        
        return wb;
	}
	public HSSFWorkbook createCampaignLeadWorkbook(Map map) {
		HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet("活动留资");
        int lenght=2;
        if(map.containsKey("daelerId")){
        	lenght+=2;
        }else if(map.containsKey("orgId")){
        	lenght+=1;
        }
        String[] titles =new String[lenght];
        Integer[] titleWidths = new Integer[lenght];
        titles[0]="时间";
        titleWidths[0]=15;
        if(map.containsKey("daelerId")){
        	titles[1]="大区";
        	titles[2]="网点";
        	titles[3]="留资数";
        	
        	titleWidths[1]=30;
        	titleWidths[2]=30;
        	titleWidths[3]=15;
        }else if(map.containsKey("orgId")){
        	titles[1]="大区";
        	titles[2]="留资数";
        	
        	titleWidths[1]=30;
        	titleWidths[2]=15;
        }else{
        	titles[1]="留资数";
        	
        	titleWidths[1]=15;
        }
        HSSFRow row = ExcelUtil.createHSSFRow(titles, titleWidths, wb, sheet, 0);
        int rowCount = 1;
        List list=(List) map.get("list");
        for(int i=0;i<list.size();i++){
        	Map map1=(Map) list.get(i);
        	row = sheet.createRow(rowCount);
        	row.createCell(0).setCellValue(map1.get("day").toString());
        	if(!"".equals(map1.get("dealerName"))){
        		row.createCell(1).setCellValue(map1.get("orgName").toString());
        		row.createCell(2).setCellValue(map1.get("dealerName").toString());
        		row.createCell(3).setCellValue(Integer.valueOf(map1.get("le").toString()));
        	}else if(!"".equals(map1.get("orgName"))){
        		row.createCell(1).setCellValue(map1.get("orgName").toString());
        		row.createCell(2).setCellValue(Integer.valueOf(map1.get("le").toString()));
        	}else{
        		row.createCell(1).setCellValue(Integer.valueOf(map1.get("le").toString()));
        	}
        	rowCount++;
        }
        
        return wb;
	}
	public HSSFWorkbook createCampaignPluginWorkbook(Map map) {
		HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet("活动插件");
        int lenght=3;
        if(map.containsKey("daelerId")){
        	lenght+=2;
        }else if(map.containsKey("orgId")){
        	lenght+=1;
        }
        if(map.containsKey("cmpaignId")){
        	lenght+=1;
        }
        if(map.containsKey("pluginId")){
        	lenght+=1;
        }	
        String[] titles =new String[lenght];
        Integer[] titleWidths = new Integer[lenght];
        titles[0]="时间";
        titleWidths[0]=15;
        if(map.containsKey("daelerId")){
        	if(map.containsKey("cmpaignId")){
        		if(map.containsKey("pluginId")){
        			titles[1]="大区";
                	titles[2]="网点";
                	titles[3]="活动名称";
                	titles[4]="插件名称";
                	titles[5]="插件参与人数";
                	titles[6]="插件留资数";
                	
                	titleWidths[1]=30;
                	titleWidths[2]=30;
                	titleWidths[3]=30;
                	titleWidths[4]=30;
                	titleWidths[5]=15;
                	titleWidths[6]=15;
        		}else{
        			titles[1]="大区";
                	titles[2]="网点";
                	titles[3]="活动名称";
                	titles[4]="插件参与人数";
                	titles[5]="插件留资数";
                	
                	titleWidths[1]=30;
                	titleWidths[2]=30;
                	titleWidths[3]=30;
                	titleWidths[4]=15;
                	titleWidths[5]=15;
        		}
        	}else{
        		if(map.containsKey("pluginId")){
        			titles[1]="大区";
                	titles[2]="网点";
                	titles[3]="插件名称";
                	titles[4]="插件参与人数";
                	titles[5]="插件留资数";
                	
                	titleWidths[1]=30;
                	titleWidths[2]=30;
                	titleWidths[3]=30;
                	titleWidths[4]=15;
                	titleWidths[5]=15;
        		}else{
        			titles[1]="大区";
                	titles[2]="网点";
                	titles[3]="插件参与人数";
                	titles[4]="插件留资数";
                	
                	titleWidths[1]=30;
                	titleWidths[2]=30;
                	titleWidths[3]=15;
                	titleWidths[4]=15;
        		}
        	}
        	
        }else if(map.containsKey("orgId")){
        	if(map.containsKey("cmpaignId")){
        		if(map.containsKey("pluginId")){
        			titles[1]="大区";
                	titles[2]="活动名称";
                	titles[3]="插件名称";
                	titles[4]="插件参与人数";
                	titles[5]="插件留资数";
                	
                	titleWidths[1]=30;
                	titleWidths[2]=30;
                	titleWidths[3]=30;
                	titleWidths[4]=15;
                	titleWidths[5]=15;
        		}else{
        			titles[1]="大区";
                	titles[2]="活动名称";
                	titles[3]="插件参与人数";
                	titles[4]="插件留资数";
                	
                	titleWidths[1]=30;
                	titleWidths[2]=30;
                	titleWidths[3]=15;
                	titleWidths[4]=15;
        		}
        	}else{
        		if(map.containsKey("pluginId")){
        			titles[1]="大区";
                	titles[2]="插件名称";
                	titles[3]="插件参与人数";
                	titles[4]="插件留资数";
                	
                	titleWidths[1]=30;
                	titleWidths[2]=30;
                	titleWidths[3]=15;
                	titleWidths[4]=15;
        		}else{
        			titles[1]="大区";
                	titles[2]="插件使用次数";
                	titles[3]="插件参与人数";
                	
                	titleWidths[1]=30;
                	titleWidths[2]=15;
                	titleWidths[3]=15;
        		}
        	}
        }else{
        	if(map.containsKey("cmpaignId")){
        		if(map.containsKey("pluginId")){
                	titles[1]="活动名称";
                	titles[2]="插件名称";
                	titles[3]="插件参与人数";
                	titles[4]="插件留资数";
                	
                	titleWidths[1]=30;
                	titleWidths[2]=30;
                	titleWidths[3]=15;
                	titleWidths[4]=15;
        		}else{
                	titles[1]="活动名称";
                	titles[2]="插件参与人数";
                	titles[3]="插件留资数";
                	
                	titleWidths[1]=30;
                	titleWidths[2]=15;
                	titleWidths[3]=15;
        		}
        	}else{
        		if(map.containsKey("pluginId")){
                	titles[1]="插件名称";
                	titles[2]="插件参与人数";
                	titles[3]="插件留资数";
                	
                	titleWidths[1]=30;
                	titleWidths[2]=15;
                	titleWidths[3]=15;
        		}else{
        			titles[1]="插件参与人数";
                	titles[2]="插件留资数";
                	
                	titleWidths[1]=15;
                	titleWidths[2]=15;
        		}
        	}
        }
        HSSFRow row = ExcelUtil.createHSSFRow(titles, titleWidths, wb, sheet, 0);
        int rowCount = 1;
        List list=(List) map.get("list");
        for(int i=0;i<list.size();i++){
        	Map map1=(Map) list.get(i);
        	row = sheet.createRow(rowCount);
        	row.createCell(0).setCellValue(map1.get("day").toString());
        	if(!"".equals(map1.get("dealerName"))){
        		if(!"".equals(map1.get("cmpaignName"))){
        			if(!"".equals(map1.get("pluginName"))){
        				row.createCell(1).setCellValue(map1.get("orgName").toString());
                		row.createCell(2).setCellValue(map1.get("dealerName").toString());
                		row.createCell(3).setCellValue(map1.get("cmpaignName").toString());
                		row.createCell(4).setCellValue(map1.get("pluginName").toString());
                		row.createCell(5).setCellValue(Integer.valueOf(map1.get("pe").toString()));
                		row.createCell(6).setCellValue(Integer.valueOf(map1.get("le").toString()));
        			}else{
        				row.createCell(1).setCellValue(map1.get("orgName").toString());
                		row.createCell(2).setCellValue(map1.get("dealerName").toString());
                		row.createCell(3).setCellValue(map1.get("cmpaignName").toString());
                		row.createCell(4).setCellValue(Integer.valueOf(map1.get("pe").toString()));
                		row.createCell(5).setCellValue(Integer.valueOf(map1.get("le").toString()));
        			}
        		}else{
        			if(!"".equals(map1.get("pluginName"))){
        				row.createCell(1).setCellValue(map1.get("orgName").toString());
                		row.createCell(2).setCellValue(map1.get("dealerName").toString());
                		row.createCell(3).setCellValue(map1.get("pluginName").toString());
                		row.createCell(4).setCellValue(Integer.valueOf(map1.get("pe").toString()));
                		row.createCell(5).setCellValue(Integer.valueOf(map1.get("le").toString()));
        			}else{
        				row.createCell(1).setCellValue(map1.get("orgName").toString());
                		row.createCell(2).setCellValue(map1.get("dealerName").toString());
                		row.createCell(3).setCellValue(Integer.valueOf(map1.get("pe").toString()));
                		row.createCell(4).setCellValue(Integer.valueOf(map1.get("le").toString()));
        			}
        		}
        		
        	}else if(!"".equals(map1.get("orgName"))){
        		if(!"".equals(map1.get("cmpaignName"))){
        			if(!"".equals(map1.get("pluginName"))){
        				row.createCell(1).setCellValue(map1.get("orgName").toString());
                		row.createCell(2).setCellValue(map1.get("cmpaignName").toString());
                		row.createCell(3).setCellValue(map1.get("pluginName").toString());
                		row.createCell(4).setCellValue(Integer.valueOf(map1.get("pe").toString()));
                		row.createCell(5).setCellValue(Integer.valueOf(map1.get("le").toString()));
        			}else{
        				row.createCell(1).setCellValue(map1.get("orgName").toString());
                		row.createCell(2).setCellValue(map1.get("cmpaignName").toString());
                		row.createCell(3).setCellValue(Integer.valueOf(map1.get("pe").toString()));
                		row.createCell(4).setCellValue(Integer.valueOf(map1.get("le").toString()));
        			}
        		}else{
        			if(!"".equals(map1.get("pluginName"))){
        				row.createCell(1).setCellValue(map1.get("orgName").toString());
                		row.createCell(2).setCellValue(map1.get("pluginName").toString());
                		row.createCell(3).setCellValue(Integer.valueOf(map1.get("pe").toString()));
                		row.createCell(4).setCellValue(Integer.valueOf(map1.get("le").toString()));
        			}else{
        				row.createCell(1).setCellValue(map1.get("orgName").toString());
                		row.createCell(2).setCellValue(Integer.valueOf(map1.get("pe").toString()));
                		row.createCell(3).setCellValue(Integer.valueOf(map1.get("le").toString()));
        			}
        		}
        	}else{
        		if(!"".equals(map1.get("cmpaignName"))){
        			if(!"".equals(map1.get("pluginName"))){
                		row.createCell(1).setCellValue(map1.get("cmpaignName").toString());
                		row.createCell(2).setCellValue(map1.get("pluginName").toString());
                		row.createCell(3).setCellValue(Integer.valueOf(map1.get("pe").toString()));
                		row.createCell(4).setCellValue(Integer.valueOf(map1.get("le").toString()));
        			}else{
                		row.createCell(1).setCellValue(map1.get("cmpaignName").toString());
                		row.createCell(2).setCellValue(Integer.valueOf(map1.get("pe").toString()));
                		row.createCell(3).setCellValue(Integer.valueOf(map1.get("le").toString()));
        			}
        		}else{
        			if(!"".equals(map1.get("pluginName"))){
                		row.createCell(1).setCellValue(map1.get("pluginName").toString());
                		row.createCell(2).setCellValue(Integer.valueOf(map1.get("pe").toString()));
                		row.createCell(3).setCellValue(Integer.valueOf(map1.get("le").toString()));
        			}else{
                		row.createCell(1).setCellValue(Integer.valueOf(map1.get("pe").toString()));
                		row.createCell(2).setCellValue(Integer.valueOf(map1.get("le").toString()));
        			}
        		}
        	}
        	rowCount++;
        }
        
        return wb;
	}
}
class MapKeyComparator implements Comparator<String>{  
    public int compare(String str1, String str2) {  
        return str1.compareTo(str2);  
    }  
} 