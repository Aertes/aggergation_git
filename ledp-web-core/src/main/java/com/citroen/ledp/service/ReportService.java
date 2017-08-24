package com.citroen.ledp.service;

import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 报表service
 * @creator     zhangqiang
 * @create-time Mar 16, 2015   11:42:27 AM
 * @version 1.0
 */
@Service
public interface ReportService {

	Map<Integer,String> listLeadsByMonth(Long dealerId,Long regionId,String month,Integer totalDay);



	Map<String,String> listNewsByWeek(Long dealerId,Long regionId,Date beginDate,Date endDate);

	/**
	 * 查询400来电 - 本周来电量趋势图
	 * @param dealerId
	 * @param regionId
	 * @param beginDate
	 * @param endDate
	 * @return
	 */

	Map<String,String> listPhoneByWeek(Long dealerId,Long regionId,Date beginDate,Date endDate);


	Map<String, Integer> listComparisionByDate(Long dealerId,Long regionId, Integer reportType, Long[] mediaIds,Date beginDate,Date endDate);
	List getListMediaIds(String dealerIds,String regionIds, Integer reportType, Long[] mediaIds,Date beginDate,Date endDate);


	Map<String, String> listAvgComparisionByDate(Long regionId,int reportType, Long[] mediaIds, Date beginDate, Date endDate,int style);


	Map<String, Integer> listMediaReportByDate(Long dealerId,Long regionId,int reportType,Long mediaId, Date beginDate,Date endDate);


	int getPublishMediaCountByDate(Long dealerId, Long regionId,Date beginDate, Date endDate);


	Map<String, String> listPhoneSuccessByWeek(Long dealerId, Long regionId,Date beginDate, Date endDate);

	int getPhoneSuccessCountByDate(Long dealerId,Long regionId,Date beginDate, Date endDate) ;

	int getPhoneCountByDate(Long dealerId,Long regionId,Date beginDate, Date endDate);

	SXSSFWorkbook createWholeComparisonWorkbook(List<Map> tableList,String name,String name1);

	SXSSFWorkbook createAreaComparisonWorkbook(List<Map> tableList,String name,String name1);
	SXSSFWorkbook createMediaWorkbook(Map<String, Map<String, List<Integer>>> tableMap,String name);
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
	double getPhoneCountByDate(Object object, Object object2,Date beginDate, Date endDate);


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
	int getThisMonthTotal(Long type, Long dealerId, Long regionId);
}
