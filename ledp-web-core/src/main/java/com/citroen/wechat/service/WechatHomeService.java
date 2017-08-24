package com.citroen.wechat.service;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.citroen.ledp.dao.mybaits.MybaitsGenericDao;
import com.citroen.ledp.domain.Dealer;
import com.citroen.ledp.domain.Organization;
import com.citroen.ledp.exception.LedpException;
import com.citroen.ledp.service.ConstantService;
import com.citroen.ledp.util.DateUtil;
/**
 * 首页服务
 * @author 何海粟
 * @date2015年7月1日
 */
@Service
public class WechatHomeService {

    private static Logger logger = Logger.getLogger(WechatHomeService.class);
	@Autowired
	private MybaitsGenericDao<Long> mybaitsGenericDao;
	@Autowired
	private ConstantService constantService;
	@Autowired
	private FansGroupService fansGroupService;
	
	/**
	 * 获取今天新增的信息
	 * @param org
	 * @param dealer
	 * @return
	 * @throws LedpException
	 */
	public Long getCurrDayMessages(Organization org, Dealer dealer,long publicNoId) throws LedpException {
		StringBuilder sql = new StringBuilder();
		
		if(dealer != null){
			sql.append(" select count(m.id) as ct from t_message m ");
			sql.append(" inner join t_publicno p on m.publicno = p.id and p.authorized=1 and p.id = "+publicNoId);
			sql.append(" where m.date_create >='"+DateUtil.format(new Date(),"yyyy-MM-dd 00:00:00")+"' and transtype=0 ");
			sql.append(" and m.parent is null ");
		}else if(org != null && org.getLevel() == 2){
			//大区
			sql.append(" select count(m.id) as ct from t_message m ");
			sql.append(" inner join t_publicno p on m.publicno = p.id and p.authorized=1 and p.org = "+org.getId());
			sql.append(" where m.date_create >='"+DateUtil.format(new Date(),"yyyy-MM-dd 00:00:00")+"' and transtype=0 ");
			sql.append(" and m.parent is null ");
		}else{
			//总部
			sql.append(" select count(m.id) as ct from t_message m ");
			sql.append(" inner join t_publicno p on m.publicno = p.id and p.authorized=1 ");
			sql.append(" where m.date_create >='"+DateUtil.format(new Date(),"yyyy-MM-dd 00:00:00")+"' and transtype=0 ");
			sql.append(" and m.parent is null ");
		}
		
		Map count = mybaitsGenericDao.find(sql.toString());
		if(!count.isEmpty()){
			try{
				return Long.parseLong(count.get("ct").toString());
			}catch(Exception e){}
		}
		return 0L;
		
	}
	
	/**
	 * 获取粉丝
	 * @param org
	 * @param dealer
	 * @param beginDate
	 * @param endDate
	 * @return
	 * @throws LedpException
	 */
	public Long getFansByDate(Organization org, Dealer dealer,long publicNoId, Date beginDate, Date endDate) throws LedpException {
		StringBuilder sql = new StringBuilder();
		
		if(dealer != null){
			sql.append(" select count(f.id) as ct from t_fans f ");
			sql.append(" inner join t_publicno p on f.publicno = p.id and p.authorized=1 ");
			sql.append(" where 1=1 and f.publicno="+publicNoId);
		}else if(org != null && org.getLevel() == 2){
			//大区
			sql.append(" select count(f.id) as ct from t_fans f ");
			sql.append(" inner join t_publicno p on f.publicno = p.id and p.org = "+org.getId());
			sql.append(" where 1=1 ");
		}else{
			//总部
			sql.append(" select count(f.id) as ct from t_fans f ");
			sql.append(" inner join t_publicno p on f.publicno = p.id and p.authorized=1 ");
			sql.append(" where 1=1 ");
		}
		
		if(beginDate == null || endDate == null){
			sql.append(" and f.subscribe = 1 and f.fans_group is not null");
		}else{
			sql.append(" and f.subscribe = 1 and f.fans_group is not null and f.subscribe_time>='"+DateUtil.format(beginDate,"yyyy-MM-dd 00:00:00")+"' and f.subscribe_time<='"+DateUtil.format(endDate,"yyyy-MM-dd 23:59:59")+"'");
		}
		Map count = mybaitsGenericDao.find(sql.toString());
		if(!count.isEmpty()){
			try{
				return Long.parseLong(count.get("ct").toString());
			}catch(Exception e){}
		}
		return 0L;
		
	}
	
	/**
	 * 获取粉丝增长
	 * @param org
	 * @param dealer
	 * @param beginDate
	 * @param endDate
	 * @return
	 * @throws LedpException 
	 */
	public Map<String,String> getFansGrowByDate(Organization org, Dealer dealer,long publicNoId, Date beginDate, Date endDate) throws LedpException{
		Map<String,String> resultMap = new LinkedHashMap<String, String>();
		StringBuilder sql = new StringBuilder();
		if(dealer != null){
			sql.append(" select  SUBSTRING(DATE_FORMAT( f.subscribe_time, '%Y-%m-%d %h:%i:%s'),1,10) as day,count(f.id) as ct from t_fans f ");
			sql.append(" inner join t_publicno p on p.id=f.publicno and p.authorized=1 and p.id="+publicNoId);
		}else if(org != null && org.getLevel() == 2){
			//大区
			sql.append(" select  SUBSTRING(DATE_FORMAT( f.subscribe_time, '%Y-%m-%d %h:%i:%s'),1,10) as day,count(f.id) as ct from t_fans f ");
			sql.append(" inner join t_publicno p on p.id=f.publicno and p.authorized=1 and p.org="+org.getId());
		}else{
			//总部
			sql.append(" select  SUBSTRING(DATE_FORMAT( f.subscribe_time, '%Y-%m-%d %h:%i:%s'),1,10) as day,count(f.id) as ct from t_fans f ");
			sql.append(" inner join t_publicno p on f.publicno = p.id and p.authorized=1 ");
		}
		if(beginDate == null || endDate == null){
			sql.append(" where f.subscribe = 1 and f.fans_group is not null");
		}else{
			sql.append(" where f.subscribe = 1 and f.fans_group is not null and f.subscribe_time>='"+DateUtil.format(beginDate,"yyyy-MM-dd 00:00:00")+"' and f.subscribe_time<='"+DateUtil.format(endDate,"yyyy-MM-dd 23:59:59")+"'");
		}
		sql.append(" group by substring(f.subscribe_time,1,10) ");
		try{
			List<Map> list = mybaitsGenericDao.executeQuery(sql.toString());
			for(Map item : list){
				if(item.get("day") != null){
					String day = item.get("day").toString();
					//设置值
					resultMap.put(day,item.get("ct").toString());
				}
			}
			
		}catch(LedpException ex){
			String msg = "查询本周粉丝增长趋势发生异常：" + ex.getMessage();

		}
		return resultMap;
	}
	
	/**
	 * 获取活动个数
	 * @param org
	 * @param dealer
	 * @param beginDate
	 * @param endDate
	 * @return
	 */
	public Map<String,String> getCampaignByDate(Organization org, Dealer dealer, Date beginDate, Date endDate){
		Map<String,String> resultMap = new LinkedHashMap<String, String>();
		StringBuilder sql = new StringBuilder();
		if(beginDate == null || endDate == null){
			return resultMap;
		}
		//网点
		if(dealer!=null){
			sql.append(" select SUBSTRING(DATE_FORMAT( c.begindate, '%Y-%m-%d %h:%i:%s'),1,10) as day,COUNT(c.id) as ct from t_campaign c ");
			sql.append(" where c.dealer="+dealer.getId());
			sql.append(" and c.begindate>='"+DateUtil.format(beginDate,"yyyy-MM-dd 00:00:00")+"' or c.enddate<='"+DateUtil.format(endDate,"yyyy-MM-dd 23:59:59")+"'");
			sql.append(" or ( c.begindate<'"+DateUtil.format(beginDate,"yyyy-MM-dd 00:00:00")+"' and c.enddate>'"+DateUtil.format(endDate,"yyyy-MM-dd 23:59:59")+"')");
		}
		//大区
		else if(org!=null && org.getLevel()==2){
			sql.append(" select SUBSTRING(DATE_FORMAT( c.begindate, '%Y-%m-%d %h:%i:%s'),1,10) as day,COUNT(c.id) as ct from t_campaign c ");
			sql.append(" left join t_dealer d on c.dealer=d.id and d.organization ="+org.getId());
			sql.append(" where c.begindate>='"+DateUtil.format(beginDate,"yyyy-MM-dd 00:00:00")+"' or c.enddate<='"+DateUtil.format(endDate,"yyyy-MM-dd 23:59:59")+"'");
			sql.append(" or ( c.begindate<'"+DateUtil.format(beginDate,"yyyy-MM-dd 00:00:00")+"' and c.enddate>'"+DateUtil.format(endDate,"yyyy-MM-dd 23:59:59")+"')");
			sql.append(" union ");
			sql.append(" select SUBSTRING(DATE_FORMAT( c.begindate, '%Y-%m-%d %h:%i:%s'),1,10) as day,COUNT(c.id) as ct from t_campaign c ");
			sql.append(" left join t_organization org on org.id = c.org and org.id="+org.getId());
			sql.append(" where c.begindate>='"+DateUtil.format(beginDate,"yyyy-MM-dd 00:00:00")+"' or c.enddate<='"+DateUtil.format(endDate,"yyyy-MM-dd 23:59:59")+"'");
			sql.append(" or ( c.begindate<'"+DateUtil.format(beginDate,"yyyy-MM-dd 00:00:00")+"' and c.enddate>'"+DateUtil.format(endDate,"yyyy-MM-dd 23:59:59")+"')");
		}
		//总部
		else{
			sql.append(" select SUBSTRING(DATE_FORMAT( c.begindate, '%Y-%m-%d %h:%i:%s'),1,10) as day,COUNT(c.id) as ct from t_campaign c ");
			sql.append(" where c.begindate>='"+DateUtil.format(beginDate,"yyyy-MM-dd 00:00:00")+"' or c.enddate<='"+DateUtil.format(endDate,"yyyy-MM-dd 23:59:59")+"'");
			sql.append(" or ( c.begindate<'"+DateUtil.format(beginDate,"yyyy-MM-dd 00:00:00")+"' and c.enddate>'"+DateUtil.format(endDate,"yyyy-MM-dd 23:59:59")+"')");
		}
		sql.append(" GROUP BY SUBSTRING(DATE_FORMAT( c.begindate, '%Y-%m-%d %h:%i:%s'),1,10) ");
		try{
			List<Map> list = mybaitsGenericDao.executeQuery(sql.toString());
			for(Map item : list){
				if(item.get("day") != null){
					String day = item.get("day").toString();
					//设置值
					resultMap.put(day,item.get("ct").toString());
				}
			}
			
		}catch(LedpException ex){
			String msg = "查询本周活动排名发生异常：" + ex.getMessage();
            logger.error(msg);
		}
		return resultMap;
	}
	
	/**
	 * 获取插件使用情况
	 * @param org
	 * @param dealer
	 * @param beginDate
	 * @param endDate
	 * @return
	 */
	public List<Map> getPluginRecordByDate(Organization org, Dealer dealer, Date beginDate, Date endDate){
		StringBuilder sql = new StringBuilder();
		if(beginDate == null || endDate == null){
			return null;
		}
		//网点
		if(dealer!=null){
			sql.append(" select t.name as name,count(r.id) as ct from t_plugin_record r ");
			sql.append(" inner join t_plugin p on r.plugin=p.id ");
			sql.append(" inner join t_plugin_type t on t.id = p.type and t.code <> 'base' ");
			sql.append(" where r.dealer="+dealer.getId());
			sql.append(" and r.create_date>='"+DateUtil.format(beginDate,"yyyy-MM-dd 00:00:00")+"'");
			sql.append("  and r.create_date<='"+DateUtil.format(endDate,"yyyy-MM-dd 23:59:59")+"'");
		}
		//大区
		else if(org!=null && org.getLevel()==2){
			sql.append(" select t.name as name,count(r.id) as ct from t_plugin_record r ");
			sql.append(" inner join t_plugin p on r.plugin=p.id ");
			sql.append(" inner join t_plugin_type t on t.id = p.type and t.code <> 'base' ");
			sql.append(" where r.org ="+org.getId());
			sql.append(" and r.create_date>='"+DateUtil.format(beginDate,"yyyy-MM-dd 00:00:00")+"'");
			sql.append(" and r.create_date<='"+DateUtil.format(endDate,"yyyy-MM-dd 23:59:59")+"'");
		}
		//总部
		else{
			sql.append(" select t.name as name,count(r.id) as ct from t_plugin_record r ");
			sql.append(" left join t_plugin p on r.plugin=p.id ");
			sql.append(" left join t_plugin_type t on t.id = p.type and t.code <> 'base' ");
			sql.append(" where r.create_date>='"+DateUtil.format(beginDate,"yyyy-MM-dd 00:00:00")+"'");
			sql.append("  and r.create_date<='"+DateUtil.format(endDate,"yyyy-MM-dd 23:59:59")+"'");
		}
		sql.append(" group by t.code ");
		try{
			return mybaitsGenericDao.executeQuery(sql.toString());
		}catch(LedpException ex){
			String msg = "查询本周插件使用情况发生异常：" + ex.getMessage();
			logger.error(msg,ex);
		}
		return null;
	}

	/**
	 * 获取留资情况
	 * @param org
	 * @param dealer
	 * @param beginDate
	 * @param endDate
	 * @return
	 */
	public List<Map> getLeadsRecordByDate(Organization org, Dealer dealer, Date beginDate, Date endDate){
		StringBuilder sql = new StringBuilder();
		if(beginDate == null || endDate == null){
			return null;
		}
		//网点
		if(dealer!=null){
			sql.append(" select l.leads_type as type ,count(l.id) as ct from t_campaign_leads l ");
			sql.append(" where l.dealer="+dealer.getId());
			sql.append(" and l.create_date>='"+DateUtil.format(beginDate,"yyyy-MM-dd 00:00:00")+"' and l.create_date<='"+DateUtil.format(endDate,"yyyy-MM-dd 23:59:59")+"'");
		}
		//大区
		else if(org!=null && org.getLevel()==2){
			sql.append(" select l.leads_type as type ,count(l.id) as ct from t_campaign_leads l ");
			sql.append(" where l.create_date>='"+DateUtil.format(beginDate,"yyyy-MM-dd 00:00:00")+"'");
			sql.append("  and l.create_date<='"+DateUtil.format(endDate,"yyyy-MM-dd 23:59:59")+"'");
			sql.append(" and l.large_area="+org.getId());
		}
		//总部
		else{
			sql.append(" select l.leads_type as type ,count(l.id) as ct from t_campaign_leads l ");
			sql.append(" where l.create_date>='"+DateUtil.format(beginDate,"yyyy-MM-dd 00:00:00")+"' and l.create_date<='"+DateUtil.format(endDate,"yyyy-MM-dd 23:59:59")+"'");
		}
		sql.append(" group by campaign is null ");
		try{
			return mybaitsGenericDao.executeQuery(sql.toString());
		}catch(LedpException ex){
			String msg = "查询留资情况发生异常：" + ex.getMessage();
			logger.error(msg,ex);
		}
		return null;
	}
	
	
}
