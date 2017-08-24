package com.citroen.ledp.service.impl;

import com.citroen.ledp.dao.mybaits.MybaitsGenericDao;
import com.citroen.ledp.domain.Media;
import com.citroen.ledp.exception.LedpException;
import com.citroen.ledp.service.DealerService;
import com.citroen.ledp.service.MediaService;
import com.citroen.ledp.service.OrganizationService;
import com.citroen.ledp.service.ReportService;
import com.citroen.ledp.util.DateUtil;
import com.citroen.ledp.util.ExcelUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * 报表service
 * @creator     zhangqiang
 * @create-time Mar 16, 2015   11:42:27 AM
 * @version 1.0
 */
@Service
public class ReportServiceImpl implements ReportService{

	private Log logger = LogFactory.getLog(ReportServiceImpl.class);

	@Autowired
	private MybaitsGenericDao<Long> mybaitsGenericDao;

	@Autowired
	private OrganizationService organizationService;
	@Autowired
	private DealerService dealerService;
	@Autowired
	private MediaService mediaService;
	/**
	 * 查询近一个月内的每天线索量
	 * @param dealerId 网点ID
	 * @param regionId 大区ID
	 * @param month 查询月份 格式：2015-03
	 * @param totalDay 该月总计多少天
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Map<Integer,String> listLeadsByMonth(Long dealerId,Long regionId,String month,Integer totalDay){
		StringBuffer query = new StringBuffer();

		//初始化数据
		Map<Integer,String> resultMap = new LinkedHashMap<Integer,String>();
		for(int i=1;i<=totalDay;i++){
			resultMap.put(i, "0");
		}

		if(null != dealerId){
			//查询网点数据
			query.append("SELECT CONVERT(SUBSTRING(o.createTime,9,2), DECIMAL) as day, COUNT(o.id) as ct ");
			query.append("FROM t_leads o ");
			query.append("WHERE SUBSTRING(o.createTime,1,7) = '" + month + "' ");
			query.append("AND o.ledp_dealer = ").append(dealerId);
			query.append(" GROUP BY SUBSTRING(o.createTime,9,2) ");
		}else if(null != regionId && regionId>1){
			//查询大区数据
			query.append("SELECT CONVERT(SUBSTRING(o.createTime,9,2), DECIMAL) as day, COUNT(o.id) as ct ");
			query.append("FROM t_leads o ");
			query.append("INNER JOIN t_dealer o1 ON o.ledp_dealer = o1.id ");
			query.append("WHERE  SUBSTRING(o.createTime,1,7) = '" + month + "' ");
			query.append("AND o1.organization = ").append(regionId);
			query.append(" GROUP BY SUBSTRING(o.createTime,9,2) ");
		}else{
			//查询总部数据
			query.append("SELECT CONVERT(SUBSTRING(o.createTime,9,2), DECIMAL) as day, COUNT(o.id) as ct ");
			query.append("FROM t_leads o ");
			query.append("WHERE SUBSTRING(o.createTime,1,7) = '" + month + "' ");
			query.append(" GROUP BY SUBSTRING(o.createTime,9,2) ");
		}



		try{
			List<Map> list = mybaitsGenericDao.executeQuery(query.toString());
			for(Map item : list){
				int day = Integer.parseInt(item.get("day").toString());
				String count = item.get("ct").toString();

				//设置值
				resultMap.put(day, count);
			}

		}catch(LedpException ex){
			String msg = "查询近一个月内的每天线索量发生异常：" + ex.getMessage();
			logger.error(msg,ex);
		}



		return resultMap;
	}


	@SuppressWarnings("rawtypes")
	public Map<String,String> listNewsByWeek(Long dealerId,Long regionId,Date beginDate,Date endDate){
		StringBuffer query = new StringBuffer();

		//计算区间天数
		Map<String,String> resultMap = new LinkedHashMap<String, String>();

		Calendar c = Calendar.getInstance(Locale.CHINA);
		c.setTime(beginDate);

		Date curDate = beginDate;
		while(curDate.before(endDate)){
			resultMap.put(DateUtil.format(curDate, "yyyy-MM-dd"),"0");
			c.add(Calendar.DAY_OF_YEAR, 1);
			curDate = c.getTime();
		}
		resultMap.put(DateUtil.format(curDate, "yyyy-MM-dd"),"0");

		if(null != dealerId){
			//查询网点数据
			query.append("SELECT DATE_FORMAT(o0.date_create,'%Y-%m-%d') as day, COUNT(o0.id) as ct ");
			query.append("FROM t_news o0 ");
			query.append("INNER JOIN t_news_media o1 ON o0.id = o1.news ");
			query.append("WHERE o1.state in (6050,6060,6130,6140) AND o0.dealer = " + dealerId + " AND o0.date_create >= '"+DateUtil.format(beginDate,"yyyy-MM-dd 00:00:00")+"' AND o0.date_create <= '"+DateUtil.format(endDate,"yyyy-MM-dd 23:59:59")+"' ");
			query.append("GROUP BY DATE_FORMAT(o0.date_create,'%Y-%m-%d') ");
		}else if(null != regionId && regionId>1){
			//查询大区数据
			query.append("SELECT DATE_FORMAT(o0.date_create,'%Y-%m-%d') as day, COUNT(o0.id) as ct ");
			query.append("FROM t_news o0 ");
			query.append("INNER JOIN t_news_media o1 ON o0.id = o1.news ");
			query.append("INNER JOIN t_dealer o2 ON o0.dealer = o2.id ");
			query.append("WHERE o1.state in (6050,6060,6130,6140)  AND o2.organization = " + regionId + " AND o0.date_create >= '"+DateUtil.format(beginDate,"yyyy-MM-dd 00:00:00")+"' AND o0.date_create <= '"+DateUtil.format(endDate,"yyyy-MM-dd 23:59:59")+"' ");
			query.append("GROUP BY DATE_FORMAT(o0.date_create,'%Y-%m-%d') ");
		}else{
			//查询总部数据
			query.append("SELECT DATE_FORMAT(o0.date_create,'%Y-%m-%d') as day, COUNT(o0.id) as ct ");
			query.append("FROM t_news o0 ");
			query.append("INNER JOIN t_news_media o1 ON o0.id = o1.news ");
			query.append("WHERE o1.state in (6050,6060,6130,6140) AND o0.date_create >= '"+DateUtil.format(beginDate,"yyyy-MM-dd 00:00:00")+"' AND o0.date_create <= '"+DateUtil.format(endDate,"yyyy-MM-dd 23:59:59")+"' ");
			query.append("GROUP BY DATE_FORMAT(o0.date_create,'%Y-%m-%d') ");
		}


		try{
			List<Map> list = mybaitsGenericDao.executeQuery(query.toString());
			for(Map item : list){
				String day = item.get("day").toString();
				//设置值
				resultMap.put(day,item.get("ct").toString());
			}

		}catch(LedpException ex){
			String msg = "查询本周内的每天信息发布量发生异常：" + ex.getMessage();
			logger.error(msg,ex);
		}



		return resultMap;
	}

	/**
	 * 查询400来电 - 本周来电量趋势图
	 * @param dealerId
	 * @param regionId
	 * @param beginDate
	 * @param endDate
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Map<String,String> listPhoneByWeek(Long dealerId,Long regionId,Date beginDate,Date endDate){
		StringBuffer query = new StringBuffer();

		//计算区间天数
		Map<String,String> resultMap = new LinkedHashMap<String, String>();

		Calendar c = Calendar.getInstance(Locale.CHINA);
		c.setTime(beginDate);

		Date curDate = beginDate;
		while(curDate.before(endDate)){
			resultMap.put(DateUtil.format(curDate, "yyyy-MM-dd"),"0");
			c.add(Calendar.DAY_OF_YEAR, 1);
			curDate = c.getTime();
		}
		resultMap.put(DateUtil.format(c.getTime(), "yyyy-MM-dd"),"0");

		if(null != dealerId){
			//查询网点数据
			query.append("SELECT SUBSTRING(o0.createTime,1,10) AS day, COUNT(DISTINCT o0.id) AS ct ");
			query.append("FROM t_leads o0 ");
			query.append("WHERE o0.ledp_type = 3030 AND o0.ledp_dealer = " + dealerId + " AND o0.createTime >= '"+DateUtil.format(beginDate,"yyyy-MM-dd 00:00:00")+"' AND o0.createTime <= '"+DateUtil.format(endDate,"yyyy-MM-dd 23:59:59")+"' ");
			query.append("GROUP BY SUBSTRING(o0.createTime,1,10) ");
		}else if(null != regionId && regionId>1){
			//查询大区数据
			query.append("SELECT SUBSTRING(o0.createTime,1,10) AS day, COUNT(DISTINCT o0.id) AS ct ");
			query.append("FROM t_leads o0 ");
			query.append("INNER JOIN t_dealer o1 ON o0.ledp_dealer = o1.id ");
			query.append("WHERE o0.ledp_type = 3030 AND o1.organization = " + regionId + " AND o0.createTime >= '"+DateUtil.format(beginDate,"yyyy-MM-dd 00:00:00")+"' AND o0.createTime <= '"+DateUtil.format(endDate,"yyyy-MM-dd 23:59:59")+"' ");
			query.append("GROUP BY SUBSTRING(o0.createTime,1,10) ");
		}else{
			//查询总部数据
			query.append("SELECT SUBSTRING(o0.createTime,1,10) AS day, COUNT(DISTINCT o0.id) AS ct ");
			query.append("FROM t_leads o0 ");
			query.append("WHERE o0.ledp_type = 3030 AND o0.createTime >= '"+DateUtil.format(beginDate,"yyyy-MM-dd 00:00:00")+"' AND o0.createTime <= '"+DateUtil.format(endDate,"yyyy-MM-dd 23:59:59")+"' ");
			query.append("GROUP BY SUBSTRING(o0.createTime,1,10) ");

		}
		try{
			List<Map> list = mybaitsGenericDao.executeQuery(query.toString());
			for(Map item : list){
				String day = item.get("day").toString();
				//设置值
				resultMap.put(day, item.get("ct").toString());
			}

		}catch(LedpException ex){
			String msg = "查询本周内400来电发生异常：" + ex.getMessage();
			logger.error(msg,ex);
		}



		return resultMap;
	}

	@SuppressWarnings("rawtypes")
	public Map<String, Integer> listComparisionByDate(Long dealerId,Long regionId, Integer reportType, Long[] mediaIds,Date beginDate,Date endDate) {
		//拼接媒体
		String searchMedias = "";
		if(null != mediaIds){
			searchMedias = StringUtils.join(mediaIds, ",");
		}
		StringBuffer query = new StringBuffer();

		Map<String,Integer> resultMap = new LinkedHashMap<String, Integer>();
		Calendar c = Calendar.getInstance(Locale.CHINA);
		c.setTime(beginDate);

		Date curDate = beginDate;
		while(curDate.before(endDate)){
			resultMap.put(DateUtil.format(curDate, "yyyy-MM-dd"), 0);

			c.add(Calendar.DAY_OF_YEAR, 1);
			curDate = c.getTime();
		}

		if(reportType == 1){
			//线索获取情况
			if(null != dealerId){
				//经销商数据
				query.append("SELECT SUBSTRING(o0.createTime,1,10) AS day,count(distinct o0.id) as ct ");
				query.append("FROM t_leads o0 ");
				query.append("WHERE o0.ledp_dealer = "+dealerId+" AND o0.ledp_media in ("+searchMedias+") ");
				query.append("AND o0.createTime >= '"+DateUtil.format(beginDate,"yyyy-MM-dd 00:00:00")+"' AND o0.createTime < '"+DateUtil.format(endDate,"yyyy-MM-dd 00:00:00")+"' ");
				query.append("GROUP BY SUBSTRING(o0.createTime,1,10) ");
			}else{
				//大区数据
				query.append("SELECT SUBSTRING(o0.createTime,1,10) AS day,count(distinct o0.id) as ct ");
				query.append("FROM t_leads o0 ");
				query.append("INNER JOIN t_dealer o1 ON o0.ledp_dealer = o1.id ");
				query.append("WHERE o1.organization = "+regionId+" AND o0.ledp_media in ("+searchMedias+") ");
				query.append("AND o0.createTime >= '"+DateUtil.format(beginDate,"yyyy-MM-dd 00:00:00")+"' AND o0.createTime < '"+DateUtil.format(endDate,"yyyy-MM-dd 00:00:00")+"' ");
				query.append("GROUP BY SUBSTRING(o0.createTime,1,10) ");
			}

		}else if(reportType == 2){
			//线索处理情况
			if(null != dealerId){
				//经销商数据
				query.append("SELECT SUBSTRING(o0.createTime,1,10) AS day,count(distinct o0.id) as ct ");
				query.append("FROM t_leads o0 ");
				query.append("WHERE o0.ledp_dealer = "+dealerId+" AND o0.ledp_follow <> 4010 AND o0.ledp_media in ("+searchMedias+") ");
				query.append("AND o0.createTime >= '"+DateUtil.format(beginDate,"yyyy-MM-dd 00:00:00")+"' AND o0.createTime < '"+DateUtil.format(endDate,"yyyy-MM-dd 00:00:00")+"' ");
				query.append("GROUP BY SUBSTRING(o0.createTime,1,10) ");
			}else{
				//大区数据
				query.append("SELECT SUBSTRING(o0.createTime,1,10) AS day,count(distinct o0.id) as ct ");
				query.append("FROM t_leads o0 ");
				query.append("INNER JOIN t_dealer o1 ON o0.ledp_dealer = o1.id ");
				query.append("WHERE o1.organization = "+regionId+" AND o0.ledp_follow <> 4010 AND o0.ledp_media in ("+searchMedias+") ");
				query.append("AND o0.createTime >= '"+DateUtil.format(beginDate,"yyyy-MM-dd 00:00:00")+"' AND o0.createTime < '"+DateUtil.format(endDate,"yyyy-MM-dd 00:00:00")+"' ");
				query.append("GROUP BY SUBSTRING(o0.createTime,1,10) ");
			}
		}else if(reportType == 3){
			//400来电情况
			if(null != dealerId){
				//经销商数据
				query.append("SELECT SUBSTRING(o0.createTime,1,10) AS day,count(distinct o0.id) as ct ");
				query.append("FROM t_leads o0 ");
				query.append("WHERE o0.ledp_dealer = "+dealerId+" AND o0.ledp_type=3030 AND o0.ledp_media in ("+searchMedias+") ");
				query.append("AND o0.createTime >= '"+DateUtil.format(beginDate,"yyyy-MM-dd 00:00:00")+"' AND o0.createTime < '"+DateUtil.format(endDate,"yyyy-MM-dd 00:00:00")+"' ");
				query.append("GROUP BY SUBSTRING(o0.createTime,1,10) ");
			}else{
				//大区数据
				query.append("SELECT SUBSTRING(o0.createTime,1,10) AS day,count(distinct o0.id) as ct ");
				query.append("FROM t_leads o0 ");
				query.append("INNER JOIN t_dealer o1 ON o0.ledp_dealer = o1.id ");
				query.append("WHERE o1.organization = "+regionId+" AND o0.ledp_type=3030 AND o0.ledp_media in ("+searchMedias+") ");
				query.append("AND o0.createTime >= '"+DateUtil.format(beginDate,"yyyy-MM-dd 00:00:00")+"' AND o0.createTime < '"+DateUtil.format(endDate,"yyyy-MM-dd 00:00:00")+"' ");
				query.append("GROUP BY SUBSTRING(o0.createTime,1,10) ");
			}
		}else if(reportType == 4){
			//信息发布情况
			if(null != dealerId){
				//经销商数据
				query.append("SELECT SUBSTRING(DATE_FORMAT(o0.date_create,'%Y-%m-%d %H:%i:%s'),1,10) AS day,count(o0.id) as ct ");
				query.append("FROM t_news o0 ");
				query.append("INNER JOIN t_news_media o1 ON o0.id = o1.news ");
				query.append("WHERE o0.dealer = "+dealerId+" AND o1.media IN ("+searchMedias+")  AND o1.state = 6050 ");
				query.append("AND o0.date_create >= '"+DateUtil.format(beginDate,"yyyy-MM-dd 00:00:00")+"' AND o0.date_create < '"+DateUtil.format(endDate,"yyyy-MM-dd 00:00:00")+"' ");
				query.append("GROUP BY SUBSTRING(DATE_FORMAT(o0.date_create,'%Y-%m-%d %H:%i:%s'),1,10) ");
			}else{
				//大区数据
				query.append("SELECT SUBSTRING(o0.date_create,1,10) AS day,count(o0.id) as ct ");
				query.append("FROM t_news o0 ");
				query.append("INNER JOIN t_news_media o1 ON o0.id = o1.news ");
				query.append("INNER JOIN t_dealer o2 ON o0.dealer = o2.id ");
				query.append("WHERE o2.organization = "+regionId+" AND o1.media IN ("+searchMedias+")  AND o1.state = 6050 ");
				query.append("AND o0.date_create >= '"+DateUtil.format(beginDate,"yyyy-MM-dd 00:00:00")+"' AND o0.date_create < '"+DateUtil.format(endDate,"yyyy-MM-dd 00:00:00")+"' ");
				query.append("GROUP BY SUBSTRING(o0.date_create,1,10) ");
			}
		}else if(reportType == 5){
			//工作勤勉度情况
			if(null != dealerId){
				//经销商数据
				query.append("SELECT SUBSTRING(DATE_FORMAT(o0.datetime,'%Y-%m-%d %H:%i:%s'),1,10) AS day, COUNT(DISTINCT o0.id) AS ct ");
				query.append("FROM t_log o0 ");
				query.append("INNER JOIN t_user o1 ON o0.operator = o1.id ");
				query.append("INNER JOIN t_dealer o2 on o1.dealer = o2.id ");
				query.append("WHERE o0.operation='login' AND o2.id = "+dealerId+" AND o0.datetime >= '"+DateUtil.format(beginDate,"yyyy-MM-dd 00:00:00")+"' AND o0.datetime < '"+DateUtil.format(endDate,"yyyy-MM-dd 00:00:00")+"' ");
				query.append("GROUP BY SUBSTRING(DATE_FORMAT(o0.datetime,'%Y-%m-%d %H:%i:%s'),1,10) ");
			}else{
				//大区数据
				query.append("SELECT SUBSTRING(DATE_FORMAT(o0.datetime,'%Y-%m-%d %H:%i:%s'),1,10) AS day, COUNT(DISTINCT o0.id) AS ct ");
				query.append("FROM t_log o0 ");
				query.append("INNER JOIN t_user o1 ON o0.operator = o1.id ");
				query.append("inner join t_organization o2 on o1.org = o2.id ");
				query.append("WHERE o0.operation='login' AND o2.id = "+regionId+" AND o0.datetime >= '"+DateUtil.format(beginDate,"yyyy-MM-dd 00:00:00")+"' AND o0.datetime < '"+DateUtil.format(endDate,"yyyy-MM-dd 00:00:00")+"' ");
				query.append("GROUP BY SUBSTRING(DATE_FORMAT(o0.datetime,'%Y-%m-%d %H:%i:%s'),1,10) ");
			}
		}else{
			return null;
		}
		try{
			List<Map> list = mybaitsGenericDao.executeQuery(query.toString());
			for(Map item : list){
				String day = item.get("day").toString();
				int count = Integer.parseInt(item.get("ct").toString());

				//设置值
				resultMap.put(day, count);
			}

		}catch(LedpException ex){
			String msg = "查询对比数据发生异常：" + ex.getMessage();
			logger.error(msg,ex);
		}



		return resultMap;
	}
	public List getListMediaIds(String dealerIds,String regionIds, Integer reportType, Long[] mediaIds,Date beginDate,Date endDate) {
		List medias1=new ArrayList();
		int cout=0;
		for(Long mediaId : mediaIds){
			Media media = mediaService.getMedia(mediaId);
			Map mediaCount=new HashMap();
			mediaCount.put("name",media.getName());

			StringBuffer query = new StringBuffer();
			if(reportType == 1){
				//线索获取情况
				if(null != dealerIds){
					//经销商数据
					query.append("SELECT SUBSTRING(o0.createTime,1,10) AS day,count(distinct o0.id) as ct ");
					query.append("FROM t_leads o0 ");
					query.append("WHERE o0.ledp_dealer in ("+dealerIds+") AND o0.ledp_media in ("+mediaId+") ");
					query.append("AND o0.createTime >= '"+DateUtil.format(beginDate,"yyyy-MM-dd 00:00:00")+"' AND o0.createTime < '"+DateUtil.format(endDate,"yyyy-MM-dd 00:00:00")+"' ");
					query.append("GROUP BY SUBSTRING(o0.createTime,1,10) ");
				}else{
					//大区数据
					query.append("SELECT SUBSTRING(o0.createTime,1,10) AS day,count(distinct o0.id) as ct ");
					query.append("FROM t_leads o0 ");
					query.append("INNER JOIN t_dealer o1 ON o0.ledp_dealer = o1.id ");
					query.append("WHERE o1.organization in ("+regionIds+") AND o0.ledp_media in ("+mediaId+") ");
					query.append("AND o0.createTime >= '"+DateUtil.format(beginDate,"yyyy-MM-dd 00:00:00")+"' AND o0.createTime < '"+DateUtil.format(endDate,"yyyy-MM-dd 00:00:00")+"' ");
					query.append("GROUP BY SUBSTRING(o0.createTime,1,10) ");
				}

			}else if(reportType == 2){
				//线索处理情况
				if(null != dealerIds){
					//经销商数据
					query.append("SELECT SUBSTRING(o0.createTime,1,10) AS day,count(distinct o0.id) as ct ");
					query.append("FROM t_leads o0 ");
					query.append("WHERE o0.ledp_dealer in ("+dealerIds+") AND o0.ledp_follow<>4010 AND o0.ledp_media in ("+mediaId+") ");
					query.append("AND o0.createTime >= '"+DateUtil.format(beginDate,"yyyy-MM-dd 00:00:00")+"' AND o0.createTime < '"+DateUtil.format(endDate,"yyyy-MM-dd 00:00:00")+"' ");
					query.append("GROUP BY SUBSTRING(o0.createTime,1,10) ");
				}else{
					//大区数据
					query.append("SELECT SUBSTRING(o0.createTime,1,10) AS day,count(distinct o0.id) as ct ");
					query.append("FROM t_leads o0 ");
					query.append("INNER JOIN t_dealer o1 ON o0.ledp_dealer = o1.id ");
					query.append("WHERE o1.organization in ("+regionIds+") AND o0.ledp_follow<>4010 AND o0.ledp_media in ("+mediaId+") ");
					query.append("AND o0.createTime >= '"+DateUtil.format(beginDate,"yyyy-MM-dd 00:00:00")+"' AND o0.createTime < '"+DateUtil.format(endDate,"yyyy-MM-dd 00:00:00")+"' ");
					query.append("GROUP BY SUBSTRING(o0.createTime,1,10) ");
				}
			}else if(reportType == 3){
				//400来电情况
				if(null != dealerIds){
					//经销商数据
					query.append("SELECT SUBSTRING(o0.createTime,1,10) AS day,count(distinct o0.id) as ct ");
					query.append("FROM t_leads o0 ");
					query.append("WHERE o0.ledp_dealer in ("+dealerIds+") AND o0.ledp_type=3030 AND o0.ledp_media in ("+mediaId+") ");
					query.append("AND o0.createTime >= '"+DateUtil.format(beginDate,"yyyy-MM-dd 00:00:00")+"' AND o0.createTime < '"+DateUtil.format(endDate,"yyyy-MM-dd 00:00:00")+"' ");
					query.append("GROUP BY SUBSTRING(o0.createTime,1,10) ");
				}else{
					//大区数据
					query.append("SELECT SUBSTRING(o0.createTime,1,10) AS day,count(distinct o0.id) as ct ");
					query.append("FROM t_leads o0 ");
					query.append("INNER JOIN t_dealer o1 ON o0.ledp_dealer = o1.id ");
					query.append("WHERE o1.organization in ("+regionIds+") AND o0.ledp_type=3030 AND o0.ledp_media in ("+mediaId+") ");
					query.append("AND o0.createTime >= '"+DateUtil.format(beginDate,"yyyy-MM-dd 00:00:00")+"' AND o0.createTime < '"+DateUtil.format(endDate,"yyyy-MM-dd 00:00:00")+"' ");
					query.append("GROUP BY SUBSTRING(o0.createTime,1,10) ");
				}
			}else if(reportType == 4){
				//信息发布情况
				if(null != dealerIds){
					//经销商数据
					query.append("SELECT SUBSTRING(DATE_FORMAT(o0.date_create,'%Y-%m-%d %H:%i:%s'),1,10) AS day,count(o0.id) as ct ");
					query.append("FROM t_news o0 ");
					query.append("INNER JOIN t_news_media o1 ON o0.id = o1.news ");
					query.append("WHERE o0.dealer in ("+dealerIds+") AND o1.media IN ("+mediaId+")  AND o1.state = 6050 ");
					query.append("AND o0.date_create >= '"+DateUtil.format(beginDate,"yyyy-MM-dd 00:00:00")+"' AND o0.date_create < '"+DateUtil.format(endDate,"yyyy-MM-dd 00:00:00")+"' ");
					query.append("GROUP BY SUBSTRING(DATE_FORMAT(o0.date_create,'%Y-%m-%d %H:%i:%s'),1,10) ");
				}else{
					//大区数据
					query.append("SELECT SUBSTRING(o0.date_create,1,10) AS day,count(o0.id) as ct ");
					query.append("FROM t_news o0 ");
					query.append("INNER JOIN t_news_media o1 ON o0.id = o1.news ");
					query.append("INNER JOIN t_dealer o2 ON o0.dealer = o2.id ");
					query.append("WHERE o2.organization in ("+regionIds+") AND o1.media IN ("+mediaId+")  AND o1.state = 6050 ");
					query.append("AND o0.date_create >= '"+DateUtil.format(beginDate,"yyyy-MM-dd 00:00:00")+"' AND o0.date_create < '"+DateUtil.format(endDate,"yyyy-MM-dd 00:00:00")+"' ");
					query.append("GROUP BY SUBSTRING(o0.date_create,1,10) ");
				}
			}else if(reportType == 5){
				//工作勤勉度情况
				if(null != dealerIds){
					//经销商数据
					query.append("SELECT SUBSTRING(DATE_FORMAT(o0.datetime,'%Y-%m-%d %H:%i:%s'),1,10) AS day, COUNT(DISTINCT o0.id) AS ct ");
					query.append("FROM t_log o0 ");
					query.append("INNER JOIN t_user o1 ON o0.operator = o1.id ");
					query.append("INNER JOIN t_dealer o2 on o1.dealer = o2.id ");
					query.append("WHERE o0.operation='login' AND o2.id in ("+dealerIds+") AND o0.datetime >= '"+DateUtil.format(beginDate,"yyyy-MM-dd 00:00:00")+"' AND o0.datetime < '"+DateUtil.format(endDate,"yyyy-MM-dd 00:00:00")+"' ");
					query.append("GROUP BY SUBSTRING(DATE_FORMAT(o0.datetime,'%Y-%m-%d %H:%i:%s'),1,10) ");
				}else{
					//大区数据
					query.append("SELECT SUBSTRING(DATE_FORMAT(o0.datetime,'%Y-%m-%d %H:%i:%s'),1,10) AS day, COUNT(DISTINCT o0.id) AS ct ");
					query.append("FROM t_log o0 ");
					query.append("INNER JOIN t_user o1 ON o0.operator = o1.id ");
					query.append("inner join t_organization o2 on o1.org = o2.id ");
					query.append("WHERE o0.operation='login' AND o2.id in ("+regionIds+") AND o0.datetime >= '"+DateUtil.format(beginDate,"yyyy-MM-dd 00:00:00")+"' AND o0.datetime < '"+DateUtil.format(endDate,"yyyy-MM-dd 00:00:00")+"' ");
					query.append("GROUP BY SUBSTRING(DATE_FORMAT(o0.datetime,'%Y-%m-%d %H:%i:%s'),1,10) ");
				}
			}else{
				return null;
			}
			try{
				List<Map> list = mybaitsGenericDao.executeQuery(query.toString());
				int mCount=0;
				for(Map item : list){
					int count = Integer.parseInt(item.get("ct").toString());
					mCount+=count;
					//设置值
				}
				mediaCount.put("mediasCount",mCount);
				medias1.add(mediaCount);
				cout=cout+mCount;
			}catch(LedpException ex){
				String msg = "查询对比数据发生异常：" + ex.getMessage();
				logger.error(msg,ex);
			}
		}
		Map mediaCounts=new HashMap();
		mediaCounts.put("name", "总计");
		mediaCounts.put("mediasCount", cout);
		medias1.add(mediaCounts);
		return medias1;
	}

	@SuppressWarnings("rawtypes")
	public Map<String, String> listAvgComparisionByDate(Long regionId,int reportType, Long[] mediaIds, Date beginDate, Date endDate,int style) {
		String searchMedias = "";
		if(null != mediaIds){
			searchMedias = StringUtils.join(mediaIds, ",");
		}
		StringBuffer query = new StringBuffer();

		Map<String,String> resultMap = new LinkedHashMap<String, String>();
		Calendar c = Calendar.getInstance(Locale.CHINA);
		c.setTime(beginDate);

		Date curDate = beginDate;
		while(curDate.before(endDate)){
			resultMap.put(DateUtil.format(curDate, "yyyy-MM-dd"),"0");

			c.add(Calendar.DAY_OF_YEAR, 1);
			curDate = c.getTime();
		}

		//查询总数量
		int totalCount = 0;
		try{
			if(null == regionId){
				//所有大区
				if(style==0){
					totalCount = organizationService.getChildrenCount(1L);
				}else{
					totalCount = dealerService.getChildrenCount(regionId);
				}
			}else{
				//所有大区下的经销商
				totalCount = dealerService.getChildrenCount1(regionId);

			}

		}catch(Exception ex){
			String msg = "查询平均数据发生异常：" + ex.getMessage();
			logger.error(msg,ex);
		}

		if(reportType == 1){
			//线索获取情况
			if(null != regionId && regionId>1){
				//大区数据
				query.append("SELECT SUBSTRING(o0.createTime,1,10) AS day,count(distinct o0.id) as ct ");
				query.append("FROM t_leads o0 ");
				query.append("INNER JOIN t_dealer o1 ON o0.ledp_dealer = o1.id ");
				query.append("WHERE o1.organization = "+regionId+" AND o0.ledp_media in ("+searchMedias+") ");
				query.append("AND o0.createTime >= '"+DateUtil.format(beginDate,"yyyy-MM-dd 00:00:00")+"' AND o0.createTime < '"+DateUtil.format(endDate,"yyyy-MM-dd 00:00:00")+"' ");
				query.append("GROUP BY SUBSTRING(o0.createTime,1,10) ");
			}else{
				//总部数据
				query.append("SELECT SUBSTRING(o0.createTime,1,10) AS day,count(distinct o0.id) as ct ");
				query.append("FROM t_leads o0 ");
				query.append("WHERE o0.ledp_media in ("+searchMedias+") ");
				query.append("AND o0.createTime >= '"+DateUtil.format(beginDate,"yyyy-MM-dd 00:00:00")+"' AND o0.createTime < '"+DateUtil.format(endDate,"yyyy-MM-dd 00:00:00")+"' ");
				query.append("GROUP BY SUBSTRING(o0.createTime,1,10) ");
			}

		}else if(reportType == 2){
			//线索处理情况
			if(null != regionId && regionId>1){
				//大区数据
				query.append("SELECT SUBSTRING(o0.createTime,1,10) AS day,count(distinct o0.id) as ct ");
				query.append("FROM t_leads o0 ");
				query.append("INNER JOIN t_dealer o1 ON o0.ledp_dealer = o1.id ");
				query.append("WHERE o1.organization = "+regionId+" AND o0.ledp_follow = 4020 AND o0.ledp_media in ("+searchMedias+") ");
				query.append("AND o0.createTime >= '"+DateUtil.format(beginDate,"yyyy-MM-dd 00:00:00")+"' AND o0.createTime < '"+DateUtil.format(endDate,"yyyy-MM-dd 00:00:00")+"' ");
				query.append("GROUP BY SUBSTRING(o0.createTime,1,10) ");
			}else{
				//总部数据
				query.append("SELECT SUBSTRING(o0.createTime,1,10) AS day,count(distinct o0.id) as ct ");
				query.append("FROM t_leads o0 ");
				query.append("WHERE o0.ledp_follow = 4020 AND o0.ledp_media in ("+searchMedias+") ");
				query.append("AND o0.createTime >= '"+DateUtil.format(beginDate,"yyyy-MM-dd 00:00:00")+"' AND o0.createTime < '"+DateUtil.format(endDate,"yyyy-MM-dd 00:00:00")+"' ");
				query.append("GROUP BY SUBSTRING(o0.createTime,1,10) ");
			}
		}else if(reportType == 3){
			//400来电情况
			if(null != regionId && regionId>1){
				//大区数据
				query.append("SELECT SUBSTRING(o0.createTime,1,10) AS day,count(distinct o0.id) as ct ");
				query.append("FROM t_leads o0 ");
				query.append("INNER JOIN t_dealer o1 ON o0.ledp_dealer = o1.id ");
				query.append("WHERE o1.organization = "+regionId+" AND o0.ledp_type=3030 AND o0.ledp_media in ("+searchMedias+") ");
				query.append("AND o0.createTime >= '"+DateUtil.format(beginDate,"yyyy-MM-dd 00:00:00")+"' AND o0.createTime < '"+DateUtil.format(endDate,"yyyy-MM-dd 00:00:00")+"' ");
				query.append("GROUP BY SUBSTRING(o0.createTime,1,10) ");
			}else{
				//总部数据
				query.append("SELECT SUBSTRING(o0.createTime,1,10) AS day,count(distinct o0.id) as ct ");
				query.append("FROM t_leads o0 ");
				query.append("WHERE o0.ledp_type=3030 AND o0.ledp_media in ("+searchMedias+") ");
				query.append("AND o0.createTime >= '"+DateUtil.format(beginDate,"yyyy-MM-dd 00:00:00")+"' AND o0.createTime < '"+DateUtil.format(endDate,"yyyy-MM-dd 00:00:00")+"' ");
				query.append("GROUP BY SUBSTRING(o0.createTime,1,10) ");
			}
		}else if(reportType == 4){
			//信息发布情况
			if(null != regionId && regionId>1){
				//大区数据
				query.append("SELECT SUBSTRING(date_format(o0.date_create,'%Y-%m-%d %H:%i:%s'),1,10) AS day,count(o0.id) as ct ");
				query.append("FROM t_news o0 ");
				query.append("INNER JOIN t_news_media o1 ON o0.id = o1.news ");
				query.append("INNER JOIN t_dealer o2 ON o0.dealer = o2.id ");
				query.append("WHERE o2.organization = "+regionId+" AND o1.media IN ("+searchMedias+")  AND o1.state = 6050 ");
				query.append("AND o0.date_create >= '"+DateUtil.format(beginDate,"yyyy-MM-dd 00:00:00")+"' AND o0.date_create < '"+DateUtil.format(endDate,"yyyy-MM-dd 00:00:00")+"' ");
				query.append("GROUP BY SUBSTRING(date_format(o0.date_create,'%Y-%m-%d %H:%i:%s'),1,10) ");
			}else{
				//总部数据
				query.append("SELECT SUBSTRING(date_format(o0.date_create,'%Y-%m-%d %H:%i:%s'),1,10) AS day,count(o0.id) as ct ");
				query.append("FROM t_news o0 ");
				query.append("INNER JOIN t_news_media o1 ON o0.id = o1.news ");
				query.append("INNER JOIN t_dealer o2 ON o0.dealer = o2.id ");
				query.append("WHERE o1.media IN ("+searchMedias+")  AND o1.state = 6050 ");
				query.append("AND o0.date_create >= '"+DateUtil.format(beginDate,"yyyy-MM-dd 00:00:00")+"' AND o0.date_create < '"+DateUtil.format(endDate,"yyyy-MM-dd 00:00:00")+"' ");
				query.append("GROUP BY SUBSTRING(date_format(o0.date_create,'%Y-%m-%d %H:%i:%s'),1,10) ");
			}
		}else if(reportType == 5){
			//工作勤勉度情况
			if(null != regionId && regionId>1){
				//大区数据
				query.append("SELECT SUBSTRING(DATE_FORMAT(o0.datetime,'%Y-%m-%d %H:%i:%s'),1,10) AS day, COUNT(DISTINCT o0.id) AS ct ");
				query.append("FROM t_log o0 ");
				query.append("INNER JOIN t_user o1 ON o0.operator = o1.id ");
				query.append("inner join t_organization o2 on o1.org = o2.id ");
				query.append("WHERE o0.operation='login' AND o2.id = "+regionId+" AND o0.datetime >= '"+DateUtil.format(beginDate,"yyyy-MM-dd 00:00:00")+"' AND o0.datetime < '"+DateUtil.format(endDate,"yyyy-MM-dd 00:00:00")+"' ");
				query.append("GROUP BY SUBSTRING(DATE_FORMAT(o0.datetime,'%Y-%m-%d %H:%i:%s'),1,10) ");
			}else{
				//总部
				query.append("SELECT SUBSTRING(DATE_FORMAT(o0.datetime,'%Y-%m-%d %H:%i:%s'),1,10) AS day, COUNT(DISTINCT o0.id) AS ct ");
				query.append("FROM t_log o0 ");
				query.append("INNER JOIN t_user o1 ON o0.operator = o1.id ");
				query.append("WHERE o0.operation='login' AND o0.datetime >= '"+DateUtil.format(beginDate,"yyyy-MM-dd 00:00:00")+"' AND o0.datetime < '"+DateUtil.format(endDate,"yyyy-MM-dd 00:00:00")+"' ");
				query.append("GROUP BY SUBSTRING(DATE_FORMAT(o0.datetime,'%Y-%m-%d %H:%i:%s'),1,10) ");
			}
		}else{
			return null;
		}
		try{
			List<Map> list = mybaitsGenericDao.executeQuery(query.toString());
			for(Map item : list){
				String day = item.get("day").toString();
				double count = Double.parseDouble(item.get("ct").toString());
				String agvCount = "0";
				if(totalCount != 0){
					double avg = count / totalCount;
					BigDecimal bd2 = new BigDecimal(avg+"");
					bd2 = bd2.setScale(2,BigDecimal.ROUND_HALF_UP);
					agvCount = bd2+"";
				}
				//设置值
				resultMap.put(day, agvCount);
			}
			
		}catch(LedpException ex){
			String msg = "查询平均数据发生异常：" + ex.getMessage();
			logger.error(msg,ex);
		}
		
		return resultMap;
	}


	public Map<String, Integer> listMediaReportByDate(Long dealerId,Long regionId,int reportType,Long mediaId, Date beginDate,Date endDate) {
		StringBuffer query = new StringBuffer();
		
		Map<String,Integer> resultMap = new LinkedHashMap<String, Integer>();
		Calendar c = Calendar.getInstance(Locale.CHINA);
		c.setTime(beginDate);
		
		Date curDate = beginDate;
		while(curDate.before(endDate)){
			resultMap.put(DateUtil.format(curDate, "yyyy-MM-dd"), 0);
			
			c.add(Calendar.DAY_OF_YEAR, 1);
			curDate = c.getTime();
		}
		
		
		
		if(reportType == 1){
			//线索获取情况
			if(null != dealerId){
				//经销商数据
				query.append("SELECT SUBSTRING(o0.createTime,1,10) AS day,count(distinct o0.id) as ct ");
				query.append("FROM t_leads o0 ");
				query.append("WHERE o0.ledp_dealer = "+dealerId+" AND o0.ledp_media ="+mediaId+" ");
				query.append("AND o0.createTime >= '"+DateUtil.format(beginDate,"yyyy-MM-dd 00:00:00")+"' AND o0.createTime < '"+DateUtil.format(endDate,"yyyy-MM-dd 00:00:00")+"' ");
				query.append("GROUP BY SUBSTRING(o0.createTime,1,10) ");
			}else if(null != regionId && regionId>1){
				//大区数据
				query.append("SELECT SUBSTRING(o0.createTime,1,10) AS day,count(distinct o0.id) as ct ");
				query.append("FROM t_leads o0 ");
				query.append("INNER JOIN t_dealer o1 ON o0.ledp_dealer = o1.id ");
				query.append("WHERE o1.organization = "+regionId+" AND o0.ledp_media ="+mediaId+" ");
				query.append("AND o0.createTime >= '"+DateUtil.format(beginDate,"yyyy-MM-dd 00:00:00")+"' AND o0.createTime < '"+DateUtil.format(endDate,"yyyy-MM-dd 00:00:00")+"' ");
				query.append("GROUP BY SUBSTRING(o0.createTime,1,10) ");
			}else{
				//总部数据
				query.append("SELECT SUBSTRING(o0.createTime,1,10) AS day,count(distinct o0.id) as ct ");
				query.append("FROM t_leads o0 ");
				query.append("WHERE o0.ledp_media ="+mediaId+" ");
				query.append("AND o0.createTime >= '"+DateUtil.format(beginDate,"yyyy-MM-dd 00:00:00")+"' AND o0.createTime < '"+DateUtil.format(endDate,"yyyy-MM-dd 00:00:00")+"' ");
				query.append("GROUP BY SUBSTRING(o0.createTime,1,10) ");
			}
			
		}else if(reportType == 2){
			//线索处理情况
			if(null != dealerId){
				//经销商数据
				query.append("SELECT SUBSTRING(o0.createTime,1,10) AS day,count(distinct o0.id) as ct ");
				query.append("FROM t_leads o0 ");
				query.append("WHERE o0.ledp_dealer = "+dealerId+" AND o0.ledp_media ="+mediaId+" AND o0.ledp_follow not in(4010) ");
				query.append("AND o0.createTime >= '"+DateUtil.format(beginDate,"yyyy-MM-dd 00:00:00")+"' AND o0.createTime < '"+DateUtil.format(endDate,"yyyy-MM-dd 00:00:00")+"' ");
				query.append("GROUP BY SUBSTRING(o0.createTime,1,10) ");
			}else if(null != regionId && regionId>1){
				//大区数据
				query.append("SELECT SUBSTRING(o0.createTime,1,10) AS day,count(distinct o0.id) as ct ");
				query.append("FROM t_leads o0 ");
				query.append("INNER JOIN t_dealer o1 ON o0.ledp_dealer = o1.id ");
				query.append("WHERE o1.organization = "+regionId+" AND  o0.ledp_media ="+mediaId+" AND o0.ledp_follow not in(4010) ");
				query.append("AND o0.createTime >= '"+DateUtil.format(beginDate,"yyyy-MM-dd 00:00:00")+"' AND o0.createTime < '"+DateUtil.format(endDate,"yyyy-MM-dd 00:00:00")+"' ");
				query.append("GROUP BY SUBSTRING(o0.createTime,1,10) ");
			}else{
				//总部数据
				query.append("SELECT SUBSTRING(o0.createTime,1,10) AS day,count(distinct o0.id) as ct ");
				query.append("FROM t_leads o0 ");
				query.append("WHERE o0.ledp_media ="+mediaId+" AND o0.ledp_follow not in(4010) ");
				query.append("AND o0.createTime >= '"+DateUtil.format(beginDate,"yyyy-MM-dd 00:00:00")+"' AND o0.createTime < '"+DateUtil.format(endDate,"yyyy-MM-dd 00:00:00")+"' ");
				query.append("GROUP BY SUBSTRING(o0.createTime,1,10) ");
			}
		}else if(reportType == 3){
			//400来电情况
			if(null != dealerId){
				//经销商数据
				query.append("SELECT SUBSTRING(o0.createTime,1,10) AS day,count(distinct o0.id) as ct ");
				query.append("FROM t_leads o0 ");
				query.append("WHERE o0.ledp_dealer = "+dealerId+" AND o0.ledp_type=3030 AND o0.ledp_media ="+mediaId+" ");
				query.append("AND o0.createTime >= '"+DateUtil.format(beginDate,"yyyy-MM-dd 00:00:00")+"' AND o0.createTime < '"+DateUtil.format(endDate,"yyyy-MM-dd 00:00:00")+"' ");
				query.append("GROUP BY SUBSTRING(o0.createTime,1,10) ");
			}else if(null != regionId && regionId>1){
				//大区数据
				query.append("SELECT SUBSTRING(o0.createTime,1,10) AS day,count(distinct o0.id) as ct ");
				query.append("FROM t_leads o0 ");
				query.append("INNER JOIN t_dealer o1 ON o0.ledp_dealer = o1.id ");
				query.append("WHERE o1.organization = "+regionId+" AND o0.ledp_type=3030 AND o0.ledp_media ="+mediaId+" ");
				query.append("AND o0.createTime >= '"+DateUtil.format(beginDate,"yyyy-MM-dd 00:00:00")+"' AND o0.createTime < '"+DateUtil.format(endDate,"yyyy-MM-dd 00:00:00")+"' ");
				query.append("GROUP BY SUBSTRING(o0.createTime,1,10) ");
			}else{
				//总部数据
				query.append("SELECT SUBSTRING(o0.createTime,1,10) AS day,count(distinct o0.id) as ct ");
				query.append("FROM t_leads o0 ");
				query.append("WHERE o0.ledp_type=3030 AND o0.ledp_media ="+mediaId+" ");
				query.append("AND o0.createTime >= '"+DateUtil.format(beginDate,"yyyy-MM-dd 00:00:00")+"' AND o0.createTime < '"+DateUtil.format(endDate,"yyyy-MM-dd 00:00:00")+"' ");
				query.append("GROUP BY SUBSTRING(o0.createTime,1,10) ");
			}
		}else if(reportType == 4){
			//信息发布情况
			if(null != dealerId){
				//经销商数据
				query.append("SELECT SUBSTRING(DATE_FORMAT(o0.date_create,'%Y-%m-%d %H:%i:%s'),1,10) AS day,count(o0.id) as ct ");
				query.append("FROM t_news o0 ");
				query.append("INNER JOIN t_news_media o1 ON o0.id = o1.news ");
				query.append("WHERE o0.dealer = "+dealerId+" AND o1.media="+mediaId+" AND o1.state = 6050 ");
				query.append("AND o0.date_create >= '"+DateUtil.format(beginDate,"yyyy-MM-dd 00:00:00")+"' AND o0.date_create < '"+DateUtil.format(endDate,"yyyy-MM-dd 00:00:00")+"' ");
				query.append("GROUP BY SUBSTRING(DATE_FORMAT(o0.date_create,'%Y-%m-%d %H:%i:%s'),1,10) ");
			}else if(null != regionId && regionId>1){
				//大区数据
				query.append("SELECT SUBSTRING(DATE_FORMAT(o0.date_create,'%Y-%m-%d %H:%i:%s'),1,10) AS day,count(o0.id) as ct ");
				query.append("FROM t_news o0 ");
				query.append("INNER JOIN t_news_media o1 ON o0.id = o1.news ");
				query.append("INNER JOIN t_dealer o2 ON o0.dealer = o2.id ");
				query.append("WHERE o2.organization = "+regionId+" AND o1.media="+mediaId+" AND o1.state = 6050 ");
				query.append("AND o0.date_create >= '"+DateUtil.format(beginDate,"yyyy-MM-dd 00:00:00")+"' AND o0.date_create < '"+DateUtil.format(endDate,"yyyy-MM-dd 00:00:00")+"' ");
				query.append("GROUP BY SUBSTRING(DATE_FORMAT(o0.date_create,'%Y-%m-%d %H:%i:%s'),1,10) ");
			}else{
				//总部
				query.append("SELECT SUBSTRING(DATE_FORMAT(o0.date_create,'%Y-%m-%d %H:%i:%s'),1,10) AS day,count(o0.id) as ct ");
				query.append("FROM t_news o0 ");
				query.append("INNER JOIN t_news_media o1 ON o0.id = o1.news ");
				query.append("WHERE o1.media="+mediaId+" AND o1.state = 6050 ");
				query.append("AND o0.date_create >= '"+DateUtil.format(beginDate,"yyyy-MM-dd 00:00:00")+"' AND o0.date_create < '"+DateUtil.format(endDate,"yyyy-MM-dd 00:00:00")+"' ");
				query.append("GROUP BY SUBSTRING(DATE_FORMAT(o0.date_create,'%Y-%m-%d %H:%i:%s'),1,10) ");
			}
		}else if(reportType == 5){
			//工作勤勉度情况
			if(null != dealerId){
				//经销商数据
				query.append("SELECT SUBSTRING(DATE_FORMAT(o0.datetime,'%Y-%m-%d %H:%i:%s'),1,10) AS day, COUNT(DISTINCT o0.id) AS ct ");
				query.append("FROM t_log o0 ");
				query.append("INNER JOIN t_user o1 ON o0.operator = o1.id ");
				query.append("INNER JOIN t_dealer o2 on o1.dealer = o2.id ");
				query.append("WHERE o0.operation='login' AND o2.id = "+dealerId+" AND o0.datetime >= '"+DateUtil.format(beginDate,"yyyy-MM-dd 00:00:00")+"' AND o0.datetime < '"+DateUtil.format(endDate,"yyyy-MM-dd 00:00:00")+"' ");
				query.append("GROUP BY SUBSTRING(DATE_FORMAT(o0.datetime,'%Y-%m-%d %H:%i:%s'),1,10) ");
			}else if(null != regionId && regionId>1){
				//大区数据
				query.append("SELECT SUBSTRING(DATE_FORMAT(o0.datetime,'%Y-%m-%d %H:%i:%s'),1,10) AS day, COUNT(DISTINCT o0.id) AS ct ");
				query.append("FROM t_log o0 ");
				query.append("INNER JOIN t_user o1 ON o0.operator = o1.id ");
				query.append("inner join t_organization o2 on o1.org = o2.id ");
				query.append("WHERE o0.operation='login' AND o2.id = "+regionId+" AND o0.datetime >= '"+DateUtil.format(beginDate,"yyyy-MM-dd 00:00:00")+"' AND o0.datetime < '"+DateUtil.format(endDate,"yyyy-MM-dd 00:00:00")+"' ");
				query.append("GROUP BY SUBSTRING(DATE_FORMAT(o0.datetime,'%Y-%m-%d %H:%i:%s'),1,10) ");
			}else{
				//总部
				query.append("SELECT SUBSTRING(DATE_FORMAT(o0.datetime,'%Y-%m-%d %H:%i:%s'),1,10) AS day, COUNT(DISTINCT o0.id) AS ct ");
				query.append("FROM t_log o0 ");
				query.append("INNER JOIN t_user o1 ON o0.operator = o1.id ");
				query.append("WHERE o0.operation='login' AND o0.datetime >= '"+DateUtil.format(beginDate,"yyyy-MM-dd 00:00:00")+"' AND o0.datetime < '"+DateUtil.format(endDate,"yyyy-MM-dd 00:00:00")+"' ");
				query.append("GROUP BY SUBSTRING(DATE_FORMAT(o0.datetime,'%Y-%m-%d %H:%i:%s'),1,10) ");
			}
			
		}else{
			return null;
		}
		try{
			List<Map> list = mybaitsGenericDao.executeQuery(query.toString());
			for(Map item : list){
				String day = item.get("day").toString();
				int count = Integer.parseInt(item.get("ct").toString());
				
				//设置值
				resultMap.put(day, count);
			}
			
		}catch(LedpException ex){
			String msg = "查询媒体数据发生异常：" + ex.getMessage();
			logger.error(msg,ex);
		}
		
		
		
		
		return resultMap;
	}


	public int getPublishMediaCountByDate(Long dealerId, Long regionId,Date beginDate, Date endDate) {
		StringBuffer query = new StringBuffer();
		int count = 0;
		if(null != dealerId){
			//经销商
			query.append("select count(o0.id) as ct from t_news o0 ");
			query.append("inner join t_news_media o1 on o0.id = o1.news ");
			query.append("where o1.state in (6050,6060,6130,6140)  and o0.dealer = "+dealerId+" ");
			query.append("and o0.date_create >='"+DateUtil.format(beginDate,"yyyy-MM-dd 00:00:00")+"' and o0.date_create <= '"+DateUtil.format(endDate,"yyyy-MM-dd 23:59:59")+"' ");
		}else if(null != regionId && regionId>1){
			//大区ID
			query.append("select count(o0.id) as ct from t_news o0 ");
			query.append("inner join t_news_media o1 on o0.id = o1.news ");
			query.append("inner join t_dealer o2 on o0.dealer = o2.id ");
			query.append("where o1.state in (6050,6060,6130,6140) and o2.organization = "+regionId+" ");
			query.append("and o0.date_create >='"+DateUtil.format(beginDate,"yyyy-MM-dd 00:00:00")+"' and o0.date_create <= '"+DateUtil.format(endDate,"yyyy-MM-dd 23:59:59")+"' ");
		}else{
			query.append("select count(o0.id) as ct from t_news o0 ");
			query.append("inner join t_news_media o1 on o0.id = o1.news ");
			query.append("where o1.state  in (6050,6060,6130,6140) ");
			query.append("and o0.date_create >='"+DateUtil.format(beginDate,"yyyy-MM-dd 00:00:00")+"' and o0.date_create <= '"+DateUtil.format(endDate,"yyyy-MM-dd 23:59:59")+"' ");
		}
		try{
			Map row = mybaitsGenericDao.find(query.toString());
			count = Integer.parseInt(row.get("ct").toString());
		}catch(Exception ex){
			String msg = "查询信息发布数量数据发生异常：" + ex.getMessage();
			logger.error(msg,ex);
		}
		return count;
	}


	public Map<String, String> listPhoneSuccessByWeek(Long dealerId, Long regionId,Date beginDate, Date endDate) {
		StringBuffer query = new StringBuffer();
		
		//计算区间天数
		Map<String,String> resultMap = new LinkedHashMap<String, String>();
		
		Calendar c = Calendar.getInstance(Locale.CHINA);
		c.setTime(beginDate);
		
		Date curDate = beginDate;
		while(curDate.before(endDate)){
			resultMap.put(DateUtil.format(curDate, "yyyy-MM-dd"),"0");
			
			c.add(Calendar.DAY_OF_YEAR, 1);
			curDate = c.getTime();
		}
		resultMap.put(DateUtil.format(c.getTime(), "yyyy-MM-dd"),"0");
		if(null != dealerId){
			//查询网点数据
			query.append("SELECT SUBSTRING(o0.createTime,1,10) AS day, COUNT(DISTINCT o0.id) AS ct ");
			query.append("FROM t_leads o0 ");
			query.append("WHERE o0.ledp_type = 3030 AND o0.content='成功' AND o0.ledp_dealer = " + dealerId + " AND o0.createTime >= '"+DateUtil.format(beginDate,"yyyy-MM-dd 00:00:00")+"' AND o0.createTime <= '"+DateUtil.format(endDate,"yyyy-MM-dd 23:59:59")+"' ");
			query.append("GROUP BY SUBSTRING(o0.createTime,1,10) ");
		}else if(null != regionId && regionId>1){
			//查询大区数据
			query.append("SELECT SUBSTRING(o0.createTime,1,10) AS day, COUNT(DISTINCT o0.id) AS ct ");
			query.append("FROM t_leads o0 ");
			query.append("INNER JOIN t_dealer o1 ON o0.ledp_dealer = o1.id ");
			query.append("WHERE o0.ledp_type = 3030 AND o0.content='成功' AND o1.organization = " + regionId + " AND o0.createTime >= '"+DateUtil.format(beginDate,"yyyy-MM-dd 00:00:00")+"' AND o0.createTime <= '"+DateUtil.format(endDate,"yyyy-MM-dd 23:59:59")+"' ");
			query.append("GROUP BY SUBSTRING(o0.createTime,1,10) ");
		}else{
			//查询总部数据
			query.append("SELECT SUBSTRING(o0.createTime,1,10) AS day, COUNT(DISTINCT o0.id) AS ct ");
			query.append("FROM t_leads o0 ");
			query.append("WHERE o0.ledp_type = 3030 AND o0.content='成功' AND o0.createTime >= '"+DateUtil.format(beginDate,"yyyy-MM-dd 00:00:00")+"' AND o0.createTime <= '"+DateUtil.format(endDate,"yyyy-MM-dd 23:59:59")+"' ");
			query.append("GROUP BY SUBSTRING(o0.createTime,1,10) ");
		}
		try{
			List<Map> list = mybaitsGenericDao.executeQuery(query.toString());
			for(Map item : list){
				String day = item.get("day").toString();
				//设置值
				resultMap.put(day, item.get("ct").toString());
			}
			
		}catch(LedpException ex){
			String msg = "查询本周内400接起率发生异常：" + ex.getMessage();
			logger.error(msg,ex);
		}
		
		return resultMap;
	}


	public int getPhoneSuccessCountByDate(Long dealerId,Long regionId,Date beginDate, Date endDate) {
		StringBuffer query = new StringBuffer();
		int count = 0;
		if(null != dealerId){
			//经销商
			query.append("SELECT SUBSTRING(o0.createTime,1,10) as day , COUNT(DISTINCT o0.id) as ct ");
			query.append("FROM t_leads o0 ");
			query.append("WHERE o0.ledp_dealer = "+dealerId+" AND o0.ledp_type = 3030 ");
			query.append("AND o0.content='成功' AND o0.createTime >= '"+DateUtil.format(beginDate,"yyyy-MM-dd 00:00:00")+"' AND o0.createTime <= '"+DateUtil.format(endDate,"yyyy-MM-dd 23:59:59")+"' ");
			query.append("GROUP BY SUBSTRING(o0.createTime,1,10) ");
		}else if(null != regionId && regionId>1){
			//大区ID
			query.append("SELECT SUBSTRING(o0.createTime,1,10) as day , COUNT(DISTINCT o0.id) as ct ");
			query.append("FROM t_leads o0 ");
			query.append("INNER JOIN t_dealer o1 ON o0.ledp_dealer = o1.ID ");
			query.append("WHERE o1.organization = "+regionId+" AND o0.ledp_type = 3030 ");
			query.append("AND o0.content='成功' AND o0.createTime >= '"+DateUtil.format(beginDate,"yyyy-MM-dd 00:00:00")+"' AND o0.createTime <= '"+DateUtil.format(endDate,"yyyy-MM-dd 23:59:59")+"' ");
			query.append("GROUP BY SUBSTRING(o0.createTime,1,10) ");
		}else{
			query.append("SELECT SUBSTRING(o0.createTime,1,10) as day , COUNT(DISTINCT o0.id) as ct ");
			query.append("FROM t_leads o0 ");
			query.append("WHERE o0.ledp_type = 3030 ");
			query.append("AND o0.content='成功' AND o0.createTime >= '"+DateUtil.format(beginDate,"yyyy-MM-dd 00:00:00")+"' AND o0.createTime <= '"+DateUtil.format(endDate,"yyyy-MM-dd 23:59:59")+"' ");
			query.append("GROUP BY SUBSTRING(o0.createTime,1,10) ");
		}
		try{
			List<Map> list = mybaitsGenericDao.executeQuery(query.toString());
			for(Map item : list){
				count += Integer.parseInt(item.get("ct").toString());
			}
			
		}catch(LedpException ex){
			String msg = "查询400来电接起数量发生异常：" + ex.getMessage();
			logger.error(msg,ex);
		}
		
		return count;
	}

	public int getPhoneCountByDate(Long dealerId,Long regionId,Date beginDate, Date endDate) {
		StringBuffer query = new StringBuffer();
		int count = 0;
		if(null != dealerId){
			//经销商
			query.append("SELECT SUBSTRING(o0.createTime,1,10) as day , COUNT(DISTINCT o0.id) as ct ");
			query.append("FROM t_leads o0 ");
			query.append("WHERE o0.ledp_dealer = "+dealerId+" AND o0.ledp_type = 3030 ");
			query.append("AND o0.createTime >= '"+DateUtil.format(beginDate,"yyyy-MM-dd 00:00:00")+"' AND o0.createTime <= '"+DateUtil.format(endDate,"yyyy-MM-dd 23:59:59")+"' ");
			query.append("GROUP BY SUBSTRING(o0.createTime,1,10) ");
		}else if(null != regionId && regionId>1){
			//大区ID
			query.append("SELECT SUBSTRING(o0.createTime,1,10) as day , COUNT(DISTINCT o0.id) as ct ");
			query.append("FROM t_leads o0 ");
			query.append("INNER JOIN t_dealer o1 ON o0.ledp_dealer = o1.ID ");
			query.append("WHERE o1.organization = "+regionId+" AND o0.ledp_type = 3030 ");
			query.append("AND o0.createTime >= '"+DateUtil.format(beginDate,"yyyy-MM-dd 00:00:00")+"' AND o0.createTime <= '"+DateUtil.format(endDate,"yyyy-MM-dd 23:59:59")+"' ");
			query.append("GROUP BY SUBSTRING(o0.createTime,1,10) ");
		}else{
			query.append("SELECT SUBSTRING(o0.createTime,1,10) as day , COUNT(DISTINCT o0.id) as ct ");
			query.append("FROM t_leads o0 ");
			query.append("WHERE o0.ledp_type = 3030 ");
			query.append("AND o0.createTime >= '"+DateUtil.format(beginDate,"yyyy-MM-dd 00:00:00")+"' AND o0.createTime <= '"+DateUtil.format(endDate,"yyyy-MM-dd 23:59:59")+"' ");
			query.append("GROUP BY SUBSTRING(o0.createTime,1,10) ");
		}
		
		try{
			List<Map> list = mybaitsGenericDao.executeQuery(query.toString());
			for(Map item : list){
				count += Integer.parseInt(item.get("ct").toString());
			}
			
		}catch(LedpException ex){
			String msg = "查询来电数量发生异常：" + ex.getMessage();
			logger.error(msg,ex);
		}
		
		return count;
	}

	@SuppressWarnings("rawtypes")
	public SXSSFWorkbook createWholeComparisonWorkbook(List<Map> tableList,String name,String name1) {
		SXSSFWorkbook wb = new SXSSFWorkbook();
        Sheet sheet = wb.createSheet("对比");
        String[] titles = new String[]{"时间","大区","媒体",name,name1}; 
        Integer[] titleWidths = new Integer[]{12,12,12,12,15};
        // 列头
        Row row = ExcelUtil.createRow(titles, titleWidths, wb, sheet, 0);
        for(int i = 0; i < tableList.size(); i++){
        	row = sheet.createRow(i+1);
        	Map item = tableList.get(i);
        	// 时间
        	row.createCell(0).setCellValue(item.get("day").toString());
        	// 大区
        	row.createCell(1).setCellValue(item.get("regionName").toString());
        	// 媒体
        	row.createCell(2).setCellValue(item.get("mediaName").toString());
        	// 大区线索量
        	row.createCell(3).setCellValue(item.get("regionCount").toString());
        	// 总部线索量均值
        	row.createCell(4).setCellValue(item.get("allCount").toString());
        }
		return wb;
	}
	
	@SuppressWarnings("rawtypes")
	public SXSSFWorkbook createAreaComparisonWorkbook(List<Map> tableList,String name,String name1) {
		SXSSFWorkbook wb = new SXSSFWorkbook();
        Sheet sheet = wb.createSheet("对比");
        String[] titles = new String[]{"时间","经销商","媒体",name,name1}; 
        Integer[] titleWidths = new Integer[]{12,12,12,12,15};
        // 列头
        Row row = ExcelUtil.createRow(titles, titleWidths, wb, sheet, 0);
        for(int i = 0; i < tableList.size(); i++){
        	row = sheet.createRow(i+1);
        	Map item = tableList.get(i);
        	// 时间
        	row.createCell(0).setCellValue(item.get("day").toString());
        	// 大区
        	row.createCell(1).setCellValue(item.get("dealerName").toString());
        	// 媒体
        	row.createCell(2).setCellValue(item.get("mediaName").toString());
        	// 大区线索量
        	row.createCell(3).setCellValue(item.get("dealerCount").toString());
        	// 总部线索量均值
        	row.createCell(4).setCellValue(item.get("allCount").toString());
        }
		return wb;
	}


	public SXSSFWorkbook createMediaWorkbook(Map<String, Map<String, List<Integer>>> tableMap,String name) {
		SXSSFWorkbook wb = new SXSSFWorkbook();
        Sheet sheet = wb.createSheet("媒体分析");
        String[] titles = new String[]{"时间","媒体",name}; 
        Integer[] titleWidths = new Integer[]{12,12,12};
        // 列头
        Row row = ExcelUtil.createRow(titles, titleWidths, wb, sheet, 0);
        
        int rowCount = 1;
        Iterator<String> dayIter = tableMap.keySet().iterator();
        while(dayIter.hasNext()){
        	String day = dayIter.next();
        	Map<String,List<Integer>> itemMap = tableMap.get(day);
        	
        	Iterator<String> mediaIter = itemMap.keySet().iterator();
        	while(mediaIter.hasNext()){
        		String mediaName = mediaIter.next();
        		List<Integer> countList = itemMap.get(mediaName);
        		
        		for(int i = 0;i<countList.size();i++){
        			Integer count = countList.get(i);
        			
        			
        			row = sheet.createRow(rowCount);
            		// 时间
                	row.createCell(0).setCellValue(day);
                	// 媒体
                	row.createCell(1).setCellValue(mediaName);
                	// 线索量
                	row.createCell(2).setCellValue(count);
            		
                	rowCount++;
        		}
        		
        	}
        	
        }
      
		return wb;
	}


	/**
	 * @Title: getPhoneCountByDate
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param object
	 * @param object2
	 * @param beginDate
	 * @param endDate
	 * @return
	 * @return double
	 */
	public double getPhoneCountByDate(Object object, Object object2,Date beginDate, Date endDate) {
		// TODO Auto-generated method stub
		return 0;
	}


	/**
	 * @Title: getTotal
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param l
	 * @param object
	 * @param regionId
	 * @param beginDate
	 * @param endDate
	 * @return
	 * @return int
	 */
	public int getThisMonthTotal(Long type, Long dealerId, Long regionId) {
		StringBuffer query = new StringBuffer();
		
		//计算区间天数

		String beginDate = DateUtil.format(new Date(),"yyyy-MM-01 00:00:00");
		String endDate   = DateUtil.format(new Date(),"yyyy-MM-dd 23:59:59");
		if(null != dealerId){
			//查询网点数据
			query.append("SELECT COUNT(DISTINCT o0.id) AS ct ");
			query.append("FROM t_leads o0 ");
			query.append("WHERE o0.ledp_type = "+type+" AND o0.ledp_dealer = " + dealerId + " AND o0.createTime >= '"+beginDate+"' AND o0.createTime <= '"+endDate+"' ");
		}else if(null != regionId && regionId>1){
			//查询大区数据
			query.append("SELECT COUNT(DISTINCT o0.id) AS ct ");
			query.append("FROM t_leads o0 ");
			query.append("INNER JOIN t_dealer o1 ON o0.ledp_dealer = o1.id ");
			query.append("WHERE o0.ledp_type = "+type+" AND o1.organization = " + regionId + " AND o0.createTime >= '"+beginDate+"' AND o0.createTime <= '"+endDate+"' ");
		}else{
			//查询总部数据
			query.append("SELECT COUNT(DISTINCT o0.id) AS ct ");
			query.append("FROM t_leads o0 ");
			query.append("WHERE o0.ledp_type = "+type+" AND o0.createTime >= '"+beginDate+"' AND o0.createTime <= '"+endDate+"' ");
		}
		
		try{
			Map row = mybaitsGenericDao.find(query.toString());
			String count = row.get("ct").toString();
			return Integer.parseInt(count);
		}catch(Exception ex){
			String msg = "查询线索总数发生异常：" + ex.getMessage();
			logger.error(msg,ex);
		}
		
		return 0;
	}
}
