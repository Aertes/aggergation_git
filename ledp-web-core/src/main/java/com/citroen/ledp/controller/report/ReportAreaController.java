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
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.citroen.ledp.domain.Dealer;
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
@RequestMapping("/report/area")
public class ReportAreaController {
	
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
	@Permission(code="report/area/index")
	public ModelAndView index(Model model,HttpServletRequest request,HttpServletResponse response,HttpSession session) throws Exception {
		ModelAndView view = new ModelAndView();
		view.setViewName("report/area/index");
		Map<String,Object> data = view.getModel();
		
		//获取当前大区
		Organization userOrg = (Organization) session.getAttribute("loginOrg");
		
		Long regionId = null;
		if(null != userOrg){
			if(userOrg.getLevel() == 1){
				//总部人员
				String searchRegion = request.getParameter("searchRegion");
				if(StringUtils.isNotBlank(searchRegion)){
					Organization region = organizationService.get(Long.parseLong(searchRegion));
					regionId = region.getId();
					data.put("searchRegion", searchRegion);
				}
				
			}else{
				//当前大区
				regionId = userOrg.getId();
			}
			
		}
		
		//1.查询线索量 - 近一月趋势总览
		Date now = new Date();
		Calendar c = Calendar.getInstance(Locale.CHINA);
		c.setTime(now);
		int totalDay = c.getActualMaximum(Calendar.DAY_OF_MONTH);
		int m = c.get(Calendar.MONTH) + 1;
		String month = c.get(Calendar.YEAR) + "-" + (m < 10 ? ("0" + m) : m);
		Map<Integer,String> leadsMonthMap = reportService.listLeadsByMonth(null, regionId, month,totalDay);
		
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
		
		Map<String,String> newsWeekMap = reportService.listNewsByWeek(null,  regionId, beginDate, endDate);
		this.convertLineReportFormat(data, newsWeekMap, "reportNewsWeekLineX", "reportNewsWeekLineY");
		//信息总条数
		int totalNews = 0;
		for(String value:newsWeekMap.values()){
			try{totalNews +=Integer.parseInt(value);}catch(Exception e){}
		}
		data.put("totalNews", totalNews);
		//400来电 - 本周来电量趋势图
		Map<String,String> phoneWeekMap = reportService.listPhoneByWeek(null, regionId, beginDate, endDate);
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
		
		double totalDealerMediaCount = dealerService.getTotalDealerMediaCount(regionId);
		double mediaFinishCount = reportService.getPublishMediaCountByDate(null,regionId,beginDate,endDate);
		double mediaFinishRate = 0;
		double mediaUnfinishRate = 100;
		if(totalDealerMediaCount > 0){
			mediaFinishRate = mediaFinishCount / (totalDealerMediaCount * mediaThreshold) * 100;
			mediaUnfinishRate = 100 - mediaFinishRate;
		}
		
		data.put("mediaFinishRate", mediaFinishRate);
		data.put("mediaUnfinishRate", mediaUnfinishRate);
		//4.查询本周400电话接起率 = (本周接听电话数量/本周来电数量 * 100%)
		double phoneCount = reportService.getPhoneCountByDate(null,regionId,beginDate,endDate);
		double phoneSuccessCount = reportService.getPhoneSuccessCountByDate(null,regionId,beginDate,endDate);
		String phoneSuccessRate = "0";
		if(phoneCount>0){
			java.math.BigDecimal bd2 = new java.math.BigDecimal((phoneSuccessCount/phoneCount*100)+"");  
			bd2 = bd2.setScale(2,BigDecimal.ROUND_HALF_UP);
			phoneSuccessRate = String.valueOf(bd2.doubleValue());
		}
		data.put("phoneSuccessRate", phoneSuccessRate);
		
		// 返回到界面
		return view;
	}
	
	@RequestMapping(value="media")
	@Permission(code="report/area/media")
	public ModelAndView media(Model model) throws Exception {
		ModelAndView view = new ModelAndView();
		view.setViewName("report/area/media");
		Map<String,Object> data = view.getModel();
		//查询所有媒体列表
		List<Media> mediaList = mediaService.listAll();
		data.put("mediaList", mediaList);
		
		// 返回到界面
		return view;
	}
	
	
	@RequestMapping(value="/autoValue.json")
	@ResponseBody
	public List autoValue(){
		List list =new ArrayList();
		
		Map map1 =new HashMap();
		map1.put("title", "ajdoijife");
		map1.put("id", 1);
		list.add(map1);
		
		Map map2 =new HashMap();
		map2.put("title", "ajbpeld");
		map2.put("id", 2);
		list.add(map2);
		
		Map map3 =new HashMap();
		map3.put("title", "aiengkl");
		map3.put("id", 3);
		list.add(map3);
		return list;
	}

	@RequestMapping(value="doMedia")
	public ModelAndView doMedia(Model model,HttpServletRequest request,HttpServletResponse response,HttpSession session) throws Exception {
		ModelAndView view = new ModelAndView();
		view.setViewName("report/area/media");
		Map<String,Object> data = view.getModel();
		//获取当前大区
		Organization userOrg = (Organization) session.getAttribute("loginOrg");
		Long regionId = null;
		if(null != userOrg){
			if(userOrg.getLevel() == 2){
				regionId = userOrg.getId();
			}else{
				//总部
				String searchRegion = request.getParameter("searchRegion");
				if(StringUtils.isNotBlank(searchRegion)){
					userOrg = organizationService.get(Long.parseLong(searchRegion));
					regionId = userOrg.getId();
					
					data.put("searchRegion", searchRegion);
				}
			}
			
		}
		
		//查询所有媒体列表
		List<Media> mediaList = mediaService.listAll();
		data.put("mediaList", mediaList);
		
		//1.获取报表内容
		String reportTypeStr = request.getParameter("reportType");
		//报表类型为空则直接返回
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
			Map<String,Integer> mediaResultMap = reportService.listMediaReportByDate(null,regionId,reportType,mediaId,beginDate,endDate);
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
	 * 导出媒体分析
	 * @param model
	 * @param request
	 * @param response
	 * @param session
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="doExportMedia")
	public void doExportMedia(Model model,HttpServletRequest request,HttpServletResponse response,HttpSession session) throws Exception {
		//获取当前大区
		Organization userOrg = (Organization) session.getAttribute("loginOrg");
		Long regionId = null;
		if(null != userOrg){
			if(userOrg.getLevel() == 2){
				regionId = userOrg.getId();
			}else{
				//总部
				String searchRegion = request.getParameter("searchRegion");
				if(StringUtils.isNotBlank(searchRegion)){
					userOrg = organizationService.get(Long.parseLong(searchRegion));
					regionId = userOrg.getId();
				}
			}
			
		}
		
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
		//如果不为自定义，则清除前台的日期
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
		
		Map<String,Map<String,List<Integer>>> tableMap = new LinkedHashMap<String, Map<String,List<Integer>>>();
		
		
		//循环遍历媒体查询
		for(Long mediaId : mediaIds){
			Media media = mediaService.getMedia(mediaId);
			Map<String,Integer> mediaResultMap = reportService.listMediaReportByDate(null,regionId,reportType,mediaId,beginDate,endDate);
			
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
	@Permission(code="report/area/comparison")
	public ModelAndView comparison(Model model,HttpServletRequest request) throws Exception {
		ModelAndView view = new ModelAndView();
		view.setViewName("report/area/comparison");
		Map<String,Object> data = view.getModel();
		
		//查询所有媒体列表
		data.put("mediaList", mediaService.listAll());
		// 返回到界面
		return view;
	}
	
	/**
	 * 经销商对比
	 * @param model
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value="doComparison")
	public ModelAndView doComparison(Model model,HttpServletRequest request,HttpServletResponse response,HttpSession session) throws Exception{
		ModelAndView view = new ModelAndView();
		view.setViewName("report/area/comparison");
		Map<String,Object> data = view.getModel();
		
		//获取当前大区
		Organization userOrg = (Organization) session.getAttribute("loginOrg");
		Long userId=userOrg.getId();
		data.put("name_avg", "大区均值");
		if(userOrg!=null){
			if(userOrg.getLevel()==1){
				userId=null;
				data.put("name_avg", "全国均值");
			}
		}
		//1.获取大区
		//String dealers = request.getParameter("searchDealer");
		//2.获取报表内容
		String reportTypeStr = request.getParameter("reportType");
		//报表类型为空则直接返回
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
		String dealer1 = request.getParameter("dealer1");
		String dealer2 = request.getParameter("dealer2");
		String dealers = dealer1+","+dealer2;
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
		//查找媒体
		String mediaNames = "";
		if(null != mediaIds){
			mediaNames = mediaService.getMediaNamesByMediaIds(mediaIds);
		}
		
		//汇总数据
		List<Map> tableList = new ArrayList<Map>();
		
		//计算大区每天均值
		Map<String,String> allResultMap = reportService.listAvgComparisionByDate(userId,reportType,mediaIds,beginDate,endDate,1);
		this.convertColumnReportFormat(data, allResultMap, "regionX", "dealer3Y");
		//查找大区数据
		String dealerNames = "";
		
		//第一个经销商
		Long firstDealerId = 0l;
		try{firstDealerId = Long.parseLong(dealer1);}catch(Exception e){}
		Dealer firstDealer = dealerService.getDealerInstance(firstDealerId);
		if(null != firstDealer){
			dealerNames += firstDealer.getName() + ",";
		}
		Map<String,Integer> firstDealerResultMap = reportService.listComparisionByDate(firstDealerId,userOrg.getId(),reportType,mediaIds,beginDate,endDate);
		this.convertColumnReportFormat(data, firstDealerResultMap, "dealerX", "dealer1Y");
		
		//第二个经销商
		Long secondDealerId = 0l;
		try{secondDealerId = Long.parseLong(dealer2);}catch(Exception e){}
		Dealer secondDealer = dealerService.getDealerInstance(secondDealerId);
		if(null != secondDealer){
			dealerNames += secondDealer.getName() + ",";
		}
		Map<String,Integer> secondDealerResultMap = reportService.listComparisionByDate(secondDealerId,userOrg.getId(),reportType,mediaIds,beginDate,endDate);
		this.convertColumnReportFormat(data, secondDealerResultMap, "dealerX", "dealer2Y");
		
		Iterator<String> iter = firstDealerResultMap.keySet().iterator();
		while(iter.hasNext()){
			String day = iter.next();
			Integer firstCount = firstDealerResultMap.get(day);
			Integer secondCount = secondDealerResultMap.get(day);
			String allCount = allResultMap.get(day);
			
			Map firstMap = new HashMap();
			firstMap.put("day", day);
			if(null != firstDealer){
				data.put("dealer1", firstDealer);
				firstMap.put("dealerName", firstDealer.getName());
			}else{
				firstMap.put("dealerName", "");
			}
			
			firstMap.put("mediaName", mediaNames);
			firstMap.put("dealerCount", firstCount);
			firstMap.put("allCount", allCount);
			
			Map secondMap = new HashMap();
			secondMap.put("day", day);
			if(null != secondDealer){
				data.put("dealer2", secondDealer);
				secondMap.put("dealerName", secondDealer.getName());
			}else{
				firstMap.put("dealerName", "");
			}
			secondMap.put("mediaName", mediaNames);
			secondMap.put("dealerCount", secondCount);
			secondMap.put("allCount", allCount);
			
			tableList.add(firstMap);
			tableList.add(secondMap);
		}
		
		if(StringUtils.isNotBlank(dealerNames)){
			dealerNames = dealerNames.substring(0,dealerNames.length() - 1);
		}
		List medias1 = reportService.getListMediaIds(dealers,userOrg.getId().toString(),reportType,mediaIds,beginDate,endDate);
		data.put("mediasMaps", medias1);
		data.put("dealerNames", dealerNames);
		data.put("searchDealer", dealers);
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
	 * 经销商对比
	 * @param model
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value="doExportComparison")
	public void doExportComparison(Model model,HttpServletRequest request,HttpServletResponse response,HttpSession session) throws Exception{
		//获取当前大区
		Organization userOrg = (Organization) session.getAttribute("loginOrg");
		
		//1.获取大区
		//String dealers = request.getParameter("searchDealer");
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
		String dealer1 = request.getParameter("dealer1");
		String dealer2 = request.getParameter("dealer2");
		String dealers = dealer1+","+dealer2;
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
		
		//查找媒体
		String mediaNames = "";
		if(null != mediaIds){
			mediaNames = mediaService.getMediaNamesByMediaIds(mediaIds);
		}
		
		//汇总数据
		List<Map> tableList = new ArrayList<Map>();
		
		//计算大区每天均值
		Map<String,String> allResultMap = reportService.listAvgComparisionByDate(userOrg.getId(),reportType,mediaIds,beginDate,endDate,1);
		//查找大区数据
		String[] dealerStrs = dealers.split(",");
		String dealerNames = "";
		
		//第一个经销商
		Long firstDealerId = Long.parseLong(dealerStrs[0]);
		Dealer firstDealer = dealerService.getDealerInstance(firstDealerId);
		if(null != firstDealer){
			dealerNames += firstDealer.getName() + ",";
		}
		Map<String,Integer> firstDealerResultMap = reportService.listComparisionByDate(firstDealerId,userOrg.getId(),reportType,mediaIds,beginDate,endDate);
		
		//第二个经销商
		Long secondDealerId = Long.parseLong(dealerStrs[1]);
		Dealer secondDealer = dealerService.getDealerInstance(secondDealerId);
		if(null != secondDealer){
			dealerNames += secondDealer.getName() + ",";
		}
		Map<String,Integer> secondDealerResultMap = reportService.listComparisionByDate(secondDealerId,userOrg.getId(),reportType,mediaIds,beginDate,endDate);
		
		Iterator<String> iter = firstDealerResultMap.keySet().iterator();
		while(iter.hasNext()){
			String day = iter.next();
			Integer firstCount = firstDealerResultMap.get(day);
			Integer secondCount = secondDealerResultMap.get(day);
			String allCount = allResultMap.get(day);
			
			Map firstMap = new HashMap();
			firstMap.put("day", day);
			if(null != firstDealer){
				firstMap.put("dealerName", firstDealer.getName());
			}else{
				firstMap.put("dealerName", "");
			}
			
			firstMap.put("mediaName", mediaNames);
			firstMap.put("dealerCount", firstCount);
			firstMap.put("allCount", allCount);
			
			Map secondMap = new HashMap();
			secondMap.put("day", day);
			if(null != secondDealer){
				secondMap.put("dealerName", secondDealer.getName());
			}else{
				firstMap.put("dealerName", "");
			}
			secondMap.put("mediaName", mediaNames);
			secondMap.put("dealerCount", secondCount);
			secondMap.put("allCount", allCount);
			
			tableList.add(firstMap);
			tableList.add(secondMap);
		}
		
		if(StringUtils.isNotBlank(dealerNames)){
			dealerNames = dealerNames.substring(0,dealerNames.length() - 1);
		}
		
		//导出数据
		Long userId=userOrg.getId();
		String name_avg="大区";
		if(userOrg!=null){
			if(userOrg.getLevel()==1){
				name_avg="全国";
			}
		}
		String name="";
		String name1="";
		if(reportType==1){
			name="网点线索获取量";
			name1=name_avg+"线索获取量均值";
		}
		if(reportType==2){
			name="网点线索处理量";
			name1=name_avg+"线索处理量均值";
		}
		if(reportType==3){
			name="网点400来电量";
			name1=name_avg+"400来电量均值";
		}
		if(reportType==4){
			name="网点信息发布量";
			name1=name_avg+"信息发布量均值";
		}
		SXSSFWorkbook wb = reportService.createAreaComparisonWorkbook(tableList,name,name1);
		ExcelUtil.exportExcelData("网点对比记录",response,request, wb);
	}
	
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
			Object objectCount = resultMap.get(day);
			String count = objectCount.toString();
			String dayInMonth =day.substring(8);
			
			reportLineXBuf.append(dayInMonth).append(",");
			reportLineYBuf.append(count).append(",");
		}
		
		String reportLineX = reportLineXBuf.substring(0,reportLineXBuf.length() - 1) + "]";
		String reportLineY = reportLineYBuf.substring(0,reportLineYBuf.length() - 1) + "]";
		data.put(xKey, reportLineX);
		data.put(yKey, reportLineY);
		
	}

	@RequestMapping(value = "/autocomplete")
	@ResponseBody
	public JSON autocomplete(HttpServletRequest request){
		Organization loginOrg = (Organization)request.getSession().getAttribute("loginOrg");
		Long orgId = null;
		try{
			orgId=Long.valueOf(request.getParameter("orgId"));
		}catch (Exception e){
			if(loginOrg!=null && loginOrg.getLevel()>1){
				orgId = loginOrg.getId();
			}
		}
		List<Map> list = dealerService.queryListByOrganization(orgId);
		return (JSON)JSON.toJSON(list);
	}
}
