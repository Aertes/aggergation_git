package com.citroen.ledp.controller.report;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.citroen.ledp.domain.Kpi;
import com.citroen.ledp.domain.Media;
import com.citroen.ledp.domain.Organization;
import com.citroen.ledp.interceptor.Permission;
import com.citroen.ledp.service.DealerService;
import com.citroen.ledp.service.KpiService;
import com.citroen.ledp.service.MediaService;
import com.citroen.ledp.service.OrganizationService;
import com.citroen.ledp.service.ReportService;
import com.citroen.ledp.util.DateUtil;
import com.citroen.ledp.util.ExcelUtil;

/**
 * @Title: AuthController.java
 * @Package com.citroen.ledp.controller
 * @Description: TODO(报表管理类)
 * @author 廖启洪
 * @date 2015年1月29日 下午3:24:24
 * @version V1.0
 */
@Controller
@RequestMapping("/report/whole")
public class ReportWholeController {
	
	@Autowired
	private ReportService reportService;
	@Autowired
	private OrganizationService organizationService;
	@Autowired
	private MediaService mediaService;
	@Autowired
	private KpiService kpiService;
	@Autowired
	private DealerService dealerService;
	
	@RequestMapping(value="index")
	@Permission(code="report/whole/media")
	public ModelAndView index(Model model) throws Exception {
		ModelAndView view = new ModelAndView();
		view.setViewName("report/whole/index");
		Map<String,Object> data = view.getModel();
		
		//1.查询线索量 - 近一月趋势总览
		Date now = new Date();
		Calendar c = Calendar.getInstance(Locale.CHINA);
		c.setTime(now);
		int totalDay = c.getActualMaximum(Calendar.DAY_OF_MONTH);
		int m = c.get(Calendar.MONTH) + 1;
		String month = c.get(Calendar.YEAR) + "-" + (m < 10 ? ("0" + m) : m);
		Map<Integer,String> leadsMonthMap = reportService.listLeadsByMonth(null, null, month,totalDay);
		
		//转换成前台报表能显示的格式
		this.convertLineReportFormat(data,leadsMonthMap,"reportLeadsMonthLineX","reportLeadsMonthLineY");
		//线索总条数
		int totalLeads = 0;
		for(String value:leadsMonthMap.values()){
			try{totalLeads +=Integer.parseInt(value);}catch(Exception e){}
		}
		data.put("totalLeads", totalLeads);
		
		//2.查询本周信息发布数量
		Date beginDate = DateUtil.getMondayOfThisWeek();
		Date endDate = DateUtil.getSundayOfThisWeek();
		
		Map<String,String> newsWeekMap = reportService.listNewsByWeek(null, null, beginDate, endDate);
		this.convertLineReportFormat(data, newsWeekMap, "reportNewsWeekLineX", "reportNewsWeekLineY");
		//信息总条数
		int totalNews = 0;
		for(String value:newsWeekMap.values()){
			try{totalNews +=Integer.parseInt(value);}catch(Exception e){}
		}
		data.put("totalNews", totalNews);
		
		//400来电 - 本周来电量趋势图
		Map<String,String> phoneWeekMap = reportService.listPhoneByWeek(null, null, beginDate, endDate);
		this.convertLineReportFormat(data, phoneWeekMap, "reportPhoneWeekLineX", "reportPhoneWeekLineY");
		//400来电总条数
		int total400 = 0;
		for(String value:phoneWeekMap.values()){
			try{total400 +=Integer.parseInt(value);}catch(Exception e){}
		}
		data.put("total400", total400);
		
		//3.查询本周完成率 = ((本月截止到当天的发布总数/开通媒体经销商数 * 指标数) * 100%)
		Kpi mediaKpi = kpiService.get(2L);
		int mediaThreshold = mediaKpi.getThreshold();	//阀值
		
		//查找本周发布数量
		//c.set(Calendar.DAY_OF_MONTH, 1);	//1号
		//beginDate = c.getTime();
		
		int totalDealerMediaCount = dealerService.getTotalDealerMediaCount(null);
		int mediaFinishCount = reportService.getPublishMediaCountByDate(null,null,beginDate,endDate);
		double mediaFinishRate = 0;
		double mediaUnfinishRate = 100;
		
		if(totalDealerMediaCount > 0){
			mediaFinishRate = mediaFinishCount * 1.0 / (totalDealerMediaCount * mediaThreshold) * 100;
			java.math.BigDecimal bd = new java.math.BigDecimal(mediaFinishRate+"");  
			bd = bd.setScale(2,BigDecimal.ROUND_HALF_UP);
			mediaFinishRate = bd.doubleValue();
			mediaUnfinishRate = 100 - mediaFinishRate;
		}
		
		data.put("mediaFinishRate", mediaFinishRate);
		data.put("mediaUnfinishRate", mediaUnfinishRate);
		
		//4.查询本周400电话接起率 = (本周接听电话数量/本周来电数量 * 100%)
		double phoneCount = reportService.getPhoneCountByDate(null,null,beginDate,endDate);
		double phoneSuccessCount = reportService.getPhoneSuccessCountByDate(null,null,beginDate,endDate);
		String phoneSuccessRate = "0";
		if(phoneCount>0){
			java.math.BigDecimal bd2 = new java.math.BigDecimal((phoneSuccessCount/phoneCount*100)+"");  
			bd2 = bd2.setScale(2,BigDecimal.ROUND_HALF_UP);
			phoneSuccessRate = String.valueOf(bd2.doubleValue());
		}
		data.put("phoneSuccessRate", phoneSuccessRate);
		//5.查询本周数据列表
		//线索量
		//信息发布量
		//400来电
		
		
		
		// 返回到界面
		return view;
	}

	@RequestMapping(value="media")
	@Permission(code="report/whole/media")
	public ModelAndView media(Model model) throws Exception {
		ModelAndView view = new ModelAndView();
		view.setViewName("report/whole/media");
		Map<String,Object> data = view.getModel();
		//查询所有媒体列表
		List<Media> mediaList = mediaService.listAll();
		data.put("mediaList", mediaList);
		
		// 返回到界面
		return view;
	}
	
	@RequestMapping(value="doMedia")
	public ModelAndView doMedia(Model model,HttpServletRequest request) throws Exception {
		ModelAndView view = new ModelAndView();
		view.setViewName("report/whole/media");
		Map<String,Object> data = view.getModel();
		//查询所有媒体列表
		List<Media> mediaList = mediaService.listAll();
		data.put("mediaList", mediaList);
		
		//1.获取报表内容
		String reportTypeStr = request.getParameter("reportType");
		
		//类型为空则直接返回
		if(StringUtils.isBlank(reportTypeStr)){
			return view;
		}
		
		//2.获取合作媒体
		String[] mediaIdStrs = request.getParameterValues("mediaIds");
		Long[] mediaIds = new Long[mediaIdStrs.length];
		for(int i = 0;i<mediaIdStrs.length;i++){
			mediaIds[i] = Long.parseLong(mediaIdStrs[i]);
		}
		//3.获取报表日期
		String searchDateType = request.getParameter("searchDateType");
		String searchDateBegin = request.getParameter("searchDateBegin");
		String searchDateEnd = request.getParameter("searchDateEnd");
		
		//判断日期
		Date beginDate = null;
		Date endDate = null;
		Date now = new Date();
		Calendar c = Calendar.getInstance(Locale.CHINA);
		c.setTime(now);
		Integer dateType = Integer.parseInt(searchDateType);
		
		if (dateType != 4) {
			searchDateBegin = "";
			searchDateEnd = "";
		}
		switch(dateType){
		case 1:
			//本周
			beginDate = DateUtil.getMondayOfThisWeek();
			endDate = DateUtil.getSundayOfThisWeek();
			c.setTime(endDate);
			c.add(Calendar.DAY_OF_YEAR, 1);	//加一天
			endDate = c.getTime();
			break;
		case 2:
			//本月
			c.set(Calendar.DAY_OF_MONTH, 1);
			beginDate = c.getTime();
			c.add(Calendar.MONTH, 1);
			c.set(Calendar.DAY_OF_MONTH, 1);
			endDate = c.getTime();
			break;
		case 3:
			//上个月
			c.set(Calendar.DAY_OF_MONTH, 1);
			endDate = c.getTime();
			
			c.add(Calendar.MONTH, -1);
			c.set(Calendar.DAY_OF_MONTH, 1);
			beginDate = c.getTime();
			break;
		case 4:
			//自定义
			c.setTime(DateUtil.parse(searchDateBegin, "yyyy-MM-dd"));
			beginDate = c.getTime();
			c.setTime(DateUtil.parse(searchDateEnd,"yyyy-MM-dd"));
			c.add(Calendar.DAY_OF_YEAR, 1);	//加一天
			endDate = c.getTime();
			
			Calendar d1 = Calendar.getInstance(Locale.CHINA);
			d1.setTime(beginDate);
			Calendar d2 = Calendar.getInstance(Locale.CHINA);
			d2.setTime(endDate);
			break;
		}
		
		int reportType = Integer.parseInt(reportTypeStr);
		
		JSONArray seriesArray = new JSONArray();
		JSONArray mediaXArray = new JSONArray();
		
		Map<String,Map<String,List<Integer>>> tableMap = new LinkedHashMap<String, Map<String,List<Integer>>>();
		
		List medias1=new ArrayList();
		int cout=0;
		//循环遍历媒体查询
		for(Long mediaId : mediaIds){
			Media media = mediaService.getMedia(mediaId);
			Map<String,Integer> mediaResultMap = reportService.listMediaReportByDate(null,null,reportType,mediaId,beginDate,endDate);
			Map mediaCount=new HashMap();
			mediaCount.put("name",media.getName());
			int medias1_count=0;
			
			JSONObject mediaJson = new JSONObject();
			mediaJson.put("name", media.getName());
			JSONArray mediaData = new JSONArray();
			
			Iterator<String> iter = mediaResultMap.keySet().iterator();
			while(iter.hasNext()){
				String day = iter.next();
				Integer count = mediaResultMap.get(day);
				
				if(mediaXArray.size() < mediaResultMap.size()){
					mediaXArray.add(day.substring(8) + "日");
				}
				medias1_count=medias1_count+count;
				mediaData.add(count);
				
				Map<String,List<Integer>> tableDataMap = tableMap.get(day);
				if(null == tableDataMap){
					tableDataMap = new LinkedHashMap<String, List<Integer>>();
//					tableDataMap.put(media.getName(), new ArrayList<Integer>());
					tableMap.put(day, tableDataMap);
				}
				List<Integer> countList = tableDataMap.get(media.getName());
				if(null == countList){
					countList = new ArrayList<Integer>();
					tableDataMap.put(media.getName(), countList);
				}
				countList.add(count);
			}
			mediaCount.put("mediasCount",medias1_count);
			medias1.add(mediaCount);
			cout=cout+medias1_count;
			mediaJson.put("data", mediaData);
			seriesArray.add(mediaJson);
		}
		data.put("mediasMaps", medias1);
		data.put("cout", cout);
		String mediaXStr = mediaXArray.toString();
		String seriesStr = seriesArray.toString();
		data.put("seriesArray", seriesStr);
		data.put("mediaX", mediaXStr);
		data.put("mediaIds", StringUtils.join(mediaIds, ","));
		data.put("mediaIdArray", mediaIds);
		data.put("reportType", reportType);
		data.put("searchDateType", searchDateType);
		data.put("searchDateBegin", searchDateBegin);
		data.put("searchDateEnd", searchDateEnd);
		data.put("tableMap", tableMap);
		
		// 返回到界面
		return view;
	}
	
	/**
	 * 导出报表
	 * @param model
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="doExportMedia")
	public void doExportMedia(Model model,HttpServletRequest request,HttpServletResponse response) throws Exception {
		//1.获取报表内容
		String reportTypeStr = request.getParameter("reportType");
		//2.获取合作媒体
		String[] mediaIdStrs = request.getParameterValues("mediaIds");
		Long[] mediaIds = new Long[mediaIdStrs.length];
		for(int i = 0;i<mediaIdStrs.length;i++){
			mediaIds[i] = Long.parseLong(mediaIdStrs[i]);
		}
		//3.获取报表日期
		String searchDateType = request.getParameter("searchDateType");
		String searchDateBegin = request.getParameter("searchDateBegin");
		String searchDateEnd = request.getParameter("searchDateEnd");
		
		//判断日期
		Date beginDate = null;
		Date endDate = null;
		Date now = new Date();
		Calendar c = Calendar.getInstance(Locale.CHINA);
		c.setTime(now);
		Integer dateType = Integer.parseInt(searchDateType);
		//如果不为自定义，则清除前台的时间
		if (dateType != 4) {
			searchDateBegin = "";
			searchDateEnd = "";
		}
		switch(dateType){
		case 1:
			//本周
			beginDate = DateUtil.getMondayOfThisWeek();
			endDate = DateUtil.getSundayOfThisWeek();
			break;
		case 2:
			//本月
			c.set(Calendar.DAY_OF_MONTH, 1);
			beginDate = c.getTime();
			c.add(Calendar.MONTH, 1);
			c.set(Calendar.DAY_OF_MONTH, 1);
			endDate = c.getTime();
			break;
		case 3:
			//上个月
			c.set(Calendar.DAY_OF_MONTH, 1);
			endDate = c.getTime();
			
			c.add(Calendar.MONTH, -1);
			c.set(Calendar.DAY_OF_MONTH, 1);
			beginDate = c.getTime();
			break;
		case 4:
			//自定义
			c.setTime(DateUtil.parse(searchDateBegin, "yyyy-MM-dd"));
			beginDate = c.getTime();
			c.setTime(DateUtil.parse(searchDateEnd,"yyyy-MM-dd"));
			c.add(Calendar.DAY_OF_YEAR, 1);	//加一天
			endDate = c.getTime();
			
			Calendar d1 = Calendar.getInstance(Locale.CHINA);
			d1.setTime(beginDate);
			Calendar d2 = Calendar.getInstance(Locale.CHINA);
			d2.setTime(endDate);
			break;
		}
		
		int reportType = Integer.parseInt(reportTypeStr);
		
		Map<String,Map<String,List<Integer>>> tableMap = new LinkedHashMap<String, Map<String,List<Integer>>>();
		
		//循环遍历媒体查询
		for(Long mediaId : mediaIds){
			Media media = mediaService.getMedia(mediaId);
			Map<String,Integer> mediaResultMap = reportService.listMediaReportByDate(null,null,reportType,mediaId,beginDate,endDate);
			
			JSONObject mediaJson = new JSONObject();
			mediaJson.put("name", media.getName());
			JSONArray mediaData = new JSONArray();
			
			Iterator<String> iter = mediaResultMap.keySet().iterator();
			while(iter.hasNext()){
				String day = iter.next();
				Integer count = mediaResultMap.get(day);
				
				mediaData.add(count);
				
				Map<String,List<Integer>> tableDataMap = tableMap.get(day);
				if(null == tableDataMap){
					tableDataMap = new LinkedHashMap<String, List<Integer>>();
					tableMap.put(day, tableDataMap);
				}
				List<Integer> countList = tableDataMap.get(media.getName());
				if(null == countList){
					countList = new ArrayList<Integer>();
					tableDataMap.put(media.getName(), countList);
				}
				countList.add(count);
			}
			mediaJson.put("data", mediaData);
		}
		
		//导出数据
		String name="";
		if(reportType==1){
			name="线索获取量";
		}
		if(reportType==2){
			name="线索处理量";
		}
		if(reportType==3){
			name="400来电量";
		}
		if(reportType==4){
			name="信息发布量";
		}
		SXSSFWorkbook wb = reportService.createMediaWorkbook(tableMap,name);
		ExcelUtil.exportExcelData("媒体分析记录",response,request, wb);
	}
	
	@RequestMapping(value="comparison")
	@Permission(code="report/whole/comparison")
	public ModelAndView comparison(Model model) throws Exception {
		ModelAndView view = new ModelAndView();
		view.setViewName("report/whole/comparison");
		Map<String,Object> data = view.getModel();
		
		//查询所有媒体列表
		List<Media> mediaList = mediaService.listAll();
		
		data.put("mediaList", mediaList);
		
		// 返回到界面
		return view;
	}
	
	/**
	 * 大区对比
	 * @param model
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value="doComparison")
	public ModelAndView doComparison(Model model,HttpServletRequest request) throws Exception{
		ModelAndView view = new ModelAndView();
		view.setViewName("report/whole/comparison");
		Map<String,Object> data = view.getModel();
		
		//1.获取大区
		String regions = request.getParameter("searchRegion");
		//2.获取报表内容
		String reportTypeStr = request.getParameter("reportType");
		
		//类型为空则跳到首页
		if(StringUtils.isBlank(reportTypeStr)){
			return view;
		}
		
		//3.获取合作媒体
		String[] mediaIdStrs = request.getParameterValues("mediaIds");
		Long[] mediaIds = null;
		if(null != mediaIdStrs){
			mediaIds = new Long[mediaIdStrs.length];
			for(int i = 0;i<mediaIdStrs.length;i++){
				mediaIds[i] = Long.parseLong(mediaIdStrs[i]);
			}
		}
		
		//4.获取报表日期
		String searchDateType = request.getParameter("searchDateType");
		String searchDateBegin = request.getParameter("searchDateBegin");
		String searchDateEnd = request.getParameter("searchDateEnd");
		
		//判断日期
		Date beginDate = null;
		Date endDate = null;
		Date now = new Date();
		Calendar c = Calendar.getInstance(Locale.CHINA);
		c.setTime(now);
		Integer dateType = Integer.parseInt(searchDateType);
		//如果不为自定义，则清除前台的时间
		if (dateType != 4) {
			searchDateBegin = "";
			searchDateEnd = "";
		}
		
		switch(dateType){
		case 1:
			//本周
			beginDate = DateUtil.getMondayOfThisWeek();
			endDate = DateUtil.getSundayOfThisWeek();
			c.setTime(endDate);
			c.add(Calendar.DAY_OF_YEAR, 1);	//加一天
			endDate = c.getTime();
			break;
		case 2:
			//本月
			c.set(Calendar.DAY_OF_MONTH, 1);
			beginDate = c.getTime();
			c.add(Calendar.MONTH, 1);
			c.set(Calendar.DAY_OF_MONTH, 1);
			endDate = c.getTime();
			break;
		case 3:
			//上个月
			c.set(Calendar.DAY_OF_MONTH, 1);
			endDate = c.getTime();
			
			c.add(Calendar.MONTH, -1);
			c.set(Calendar.DAY_OF_MONTH, 1);
			beginDate = c.getTime();
			break;
		case 4:
			//自定义
			c.setTime(DateUtil.parse(searchDateBegin, "yyyy-MM-dd"));
			beginDate = c.getTime();
			c.setTime(DateUtil.parse(searchDateEnd,"yyyy-MM-dd"));
			c.add(Calendar.DAY_OF_YEAR, 1);	//加一天
			endDate = c.getTime();
			
			Calendar d1 = Calendar.getInstance(Locale.CHINA);
			d1.setTime(beginDate);
			Calendar d2 = Calendar.getInstance(Locale.CHINA);
			d2.setTime(endDate);
			break;
		}
		
		int reportType = Integer.parseInt(reportTypeStr);
		
		//查找媒体
		String mediaNames = "";
		if(null != mediaIds){
			mediaNames = mediaService.getMediaNamesByMediaIds(mediaIds);
		}
				
		
		//汇总数据
		List<Map> tableList = new ArrayList<Map>();
		
		//计算总部每天均值
		Map<String,String> allResultMap = reportService.listAvgComparisionByDate(null,reportType,mediaIds,beginDate,endDate,0);
		this.convertColumnReportFormat(data, allResultMap, "regionX", "region3Y");
		//查找大区数据
		String[] regionStrs = regions.split(",");
		String regionNames = "";
		
		//第一个大区
		Long firstRegionId = Long.parseLong(regionStrs[0]);
		Organization firstOrg = organizationService.get(firstRegionId);
		if(null != firstOrg){
			regionNames += firstOrg.getName() + ",";
		}
		Map<String,Integer> firstRegionResultMap = reportService.listComparisionByDate(null,firstRegionId,reportType,mediaIds,beginDate,endDate);
		this.convertColumnReportFormat(data, firstRegionResultMap, "regionX", "region1Y");
		//第二个大区
		Long secondRegionId = Long.parseLong(regionStrs[1]);
		Organization secondOrg = organizationService.get(secondRegionId);
		if(null != secondOrg){
			regionNames += secondOrg.getName() + ",";
		}
		Map<String,Integer> secondRegionResultMap = reportService.listComparisionByDate(null,secondRegionId,reportType,mediaIds,beginDate,endDate);
		this.convertColumnReportFormat(data, secondRegionResultMap, "regionX", "region2Y");
		Iterator<String> iter = firstRegionResultMap.keySet().iterator();
		while(iter.hasNext()){
			String day = iter.next();
			Integer firstCount = firstRegionResultMap.get(day);
			Integer secondCount = secondRegionResultMap.get(day);
			String allCount = allResultMap.get(day);
			
			Map firstMap = new HashMap();
			firstMap.put("day", day);
			firstMap.put("regionName", firstOrg.getName());
			firstMap.put("mediaName", mediaNames);
			firstMap.put("regionCount", firstCount);
			firstMap.put("allCount", allCount);
			
			Map secondMap = new HashMap();
			secondMap.put("day", day);
			secondMap.put("regionName", secondOrg.getName());
			secondMap.put("mediaName", mediaNames);
			secondMap.put("regionCount", secondCount);
			secondMap.put("allCount", allCount);
			
			tableList.add(firstMap);
			tableList.add(secondMap);
		}
		
		if(StringUtils.isNotBlank(regionNames)){
			regionNames = regionNames.substring(0,regionNames.length() - 1);
		}
		List medias1 = reportService.getListMediaIds(null,regions,reportType,mediaIds,beginDate,endDate);
		data.put("mediasMaps", medias1);
		data.put("regionNames", regionNames);
		data.put("searchRegion", regions);
		data.put("mediaIds", StringUtils.join(mediaIds, ","));
		data.put("mediaIdArray", mediaIds);
		data.put("reportType", reportType);
		data.put("searchDateType", searchDateType);
		data.put("searchDateBegin", searchDateBegin);
		data.put("searchDateEnd", searchDateEnd);
		data.put("tableList", tableList);
		
		//查询所有媒体列表
		List<Media> mediaList = mediaService.listAll();
		data.put("mediaList", mediaList);
		
		// 返回到界面
		return view;
	}
	
	/**
	 * 导出大区对比
	 * @param model
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value="doExportComparison")
	public void doExportComparison(Model model,HttpServletRequest request,HttpServletResponse response) throws Exception{
		//1.获取大区
		String regions = request.getParameter("searchRegion");
		//2.获取报表内容
		String reportTypeStr = request.getParameter("reportType");
		//3.获取合作媒体
		String[] mediaIdStrs = request.getParameterValues("mediaIds");
		Long[] mediaIds = null;
		if(null != mediaIdStrs){
			mediaIds = new Long[mediaIdStrs.length];
			for(int i = 0;i<mediaIdStrs.length;i++){
				mediaIds[i] = Long.parseLong(mediaIdStrs[i]);
			}
		}
		
		//4.获取报表日期
		String searchDateType = request.getParameter("searchDateType");
		String searchDateBegin = request.getParameter("searchDateBegin");
		String searchDateEnd = request.getParameter("searchDateEnd");
		
		//判断日期
		Date beginDate = null;
		Date endDate = null;
		Date now = new Date();
		Calendar c = Calendar.getInstance(Locale.CHINA);
		c.setTime(now);
		Integer dateType = Integer.parseInt(searchDateType);
		
		if (dateType != 4) {
			searchDateBegin = "";
			searchDateEnd = "";
		}

		switch(dateType){
		case 1:
			//本周
			//2.查询本周信息发布数量
			beginDate = DateUtil.getMondayOfThisWeek();
			endDate = DateUtil.getSundayOfThisWeek();
			c.setTime(endDate);
			c.add(Calendar.DAY_OF_YEAR, 1);	//加一天
			endDate = c.getTime();
			break;
		case 2:
			//本月
			c.set(Calendar.DAY_OF_MONTH, 1);
			beginDate = c.getTime();
			c.add(Calendar.MONTH, 1);
			c.set(Calendar.DAY_OF_MONTH, 1);
			endDate = c.getTime();
			break;
		case 3:
			//上个月
			c.set(Calendar.DAY_OF_MONTH, 1);
			endDate = c.getTime();
			
			c.add(Calendar.MONTH, -1);
			c.set(Calendar.DAY_OF_MONTH, 1);
			beginDate = c.getTime();
			break;
		case 4:
			//自定义
			c.setTime(DateUtil.parse(searchDateBegin, "yyyy-MM-dd"));
			beginDate = c.getTime();
			c.setTime(DateUtil.parse(searchDateEnd,"yyyy-MM-dd"));
			c.add(Calendar.DAY_OF_YEAR, 1);	//加一天
			endDate = c.getTime();
			
			Calendar d1 = Calendar.getInstance(Locale.CHINA);
			d1.setTime(beginDate);
			Calendar d2 = Calendar.getInstance(Locale.CHINA);
			d2.setTime(endDate);
			break;
		}
		
		int reportType = Integer.parseInt(reportTypeStr);
		
		//查找媒体
		String mediaNames = "";
		if(null != mediaIds){
			mediaNames = mediaService.getMediaNamesByMediaIds(mediaIds);
		}
				
		
		//汇总数据
		List<Map> tableList = new ArrayList<Map>();
		
		//计算总部每天均值
		Map<String,String> allResultMap = reportService.listAvgComparisionByDate(null,reportType,mediaIds,beginDate,endDate,0);
		
		//查找大区数据
		String[] regionStrs = regions.split(",");
		
		//第一个大区
		Long firstRegionId = Long.parseLong(regionStrs[0]);
		Organization firstOrg = organizationService.get(firstRegionId);
		Map<String,Integer> firstRegionResultMap = reportService.listComparisionByDate(null,firstRegionId,reportType,mediaIds,beginDate,endDate);
		
		//第二个大区
		Long secondRegionId = Long.parseLong(regionStrs[1]);
		Organization secondOrg = organizationService.get(secondRegionId);
		Map<String,Integer> secondRegionResultMap = reportService.listComparisionByDate(null,secondRegionId,reportType,mediaIds,beginDate,endDate);
		
		Iterator<String> iter = firstRegionResultMap.keySet().iterator();
		while(iter.hasNext()){
			String day = iter.next();
			Integer firstCount = firstRegionResultMap.get(day);
			Integer secondCount = secondRegionResultMap.get(day);
			String allCount = allResultMap.get(day);
			
			Map firstMap = new HashMap();
			firstMap.put("day", day);
			firstMap.put("regionName", firstOrg.getName());
			firstMap.put("mediaName", mediaNames);
			firstMap.put("regionCount", firstCount);
			firstMap.put("allCount", allCount);
			
			Map secondMap = new HashMap();
			secondMap.put("day", day);
			secondMap.put("regionName", secondOrg.getName());
			secondMap.put("mediaName", mediaNames);
			secondMap.put("regionCount", secondCount);
			secondMap.put("allCount", allCount);
			
			tableList.add(firstMap);
			tableList.add(secondMap);
		}
		
		//导出数据
		String name="";
		String name1="";
		if(reportType==1){
			name="大区线索获取量";
			name1="全国大区线索获取量均值";
		}
		if(reportType==2){
			name="大区线索处理量";
			name1="全国大区线索处理量均值";
		}
		if(reportType==3){
			name="大区400来电量";
			name1="全国大区400来电量均值";
		}
		if(reportType==4){
			name="大区信息发布量";
			name1="全国大区信息发布量均值";
		}
		SXSSFWorkbook wb = reportService.createWholeComparisonWorkbook(tableList,name,name1);
		ExcelUtil.exportExcelData("大区对比记录",response,request, wb);
		
	}
	
	/**
	 * 分页查询报表结果
	 * @param model
	 * @return
	 * @throws Exception
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value="/listComparisonByPage")
	@ResponseBody
	public Map listComparisonByPage(Model model) throws Exception {
		//获取所有大区列表
		List lists = new ArrayList();
		
		
		
		for(int i=0;i<10;i++){
			Map map =new HashMap();
			map.put("time", "2015-03-18"+i);
			map.put("mediacome", "上海大区"+i);
			map.put("mediacome", "中国媒体"+i);
			map.put("dqxsl", "10"+i);
			map.put("zbxsl", "20"+i);
			lists.add(map);
		}
		Map map =new HashMap();
		map.put("rows", lists);
		map.put("sortName","time");
		map.put("sortOrder", "desc");
		map.put("pageNumber", 2);
		map.put("currentPage", 1);
		// 返回到界面
		return map;
	}
	 */
	
	/**
	 * 转换成线性报表前台显示
	 * @param data
	 * @param resultMap
	 * @param xKey
	 * @param yKey
	 */
	private void convertLineReportFormat(Map<String, Object> data,Map<?, String> resultMap, String xKey, String yKey) {
		StringBuffer reportLineXBuf = new StringBuffer();	//X轴
		StringBuffer reportLineYBuf = new StringBuffer();	//Y轴
		reportLineXBuf.append("[");
		reportLineYBuf.append("[");
		
		Iterator<?> iter = resultMap.keySet().iterator();
		while(iter.hasNext()){
			Object day = iter.next();
			reportLineXBuf.append(day.toString()).append(",");
			reportLineYBuf.append(resultMap.get(day)).append(",");
		}
		
		String reportLineX = reportLineXBuf.substring(0,reportLineXBuf.length() - 1) + "]";
		String reportLineY = reportLineYBuf.substring(0,reportLineYBuf.length() - 1) + "]";
		data.put(xKey, reportLineX);
		data.put(yKey, reportLineY);
		
	}
	
	/**
	 * 转换成柱状报表前台显示
	 * @param data
	 * @param resultMap
	 * @param xKey
	 * @param yKey
	 */
	private void convertColumnReportFormat(Map<String, Object> data,Map<?, ?> resultMap, String xKey, String yKey) {
		StringBuffer reportLineXBuf = new StringBuffer();	//X轴
		StringBuffer reportLineYBuf = new StringBuffer();	//Y轴
		reportLineXBuf.append("[");
		reportLineYBuf.append("[");
		
		Iterator<?> iter = resultMap.keySet().iterator();
		while(iter.hasNext()){
			String day = iter.next().toString();
			Object count = resultMap.get(day);
			
			String dayInMonth =day.substring(8);
			
			reportLineXBuf.append(dayInMonth).append(",");
			reportLineYBuf.append(count).append(",");
		}
		
		String reportLineX = reportLineXBuf.substring(0,reportLineXBuf.length() - 1) + "]";
		String reportLineY = reportLineYBuf.substring(0,reportLineYBuf.length() - 1) + "]";
		data.put(xKey, reportLineX);
		data.put(yKey, reportLineY);
		
	}

}
