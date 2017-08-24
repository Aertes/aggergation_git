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
import org.springframework.web.servlet.ModelAndView;

import com.citroen.ledp.domain.Dealer;
import com.citroen.ledp.domain.Kpi;
import com.citroen.ledp.domain.Media;
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
@RequestMapping("/report/dealer")
public class ReportDealerController {
	
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
	@Permission(code="report/dealer/index")
	public ModelAndView index(Model model,HttpServletRequest request,HttpServletResponse response,HttpSession session) throws Exception {
		ModelAndView view = new ModelAndView();
		view.setViewName("report/dealer/index");
		Map<String,Object> data = view.getModel();
		
		//获取当前经销商
		Dealer curDealer = (Dealer) session.getAttribute("loginDealer");
		Long dealerId = null;
		if(null != curDealer){
			dealerId = curDealer.getId();
			data.put("dealer1", curDealer);
		}else{
			//当前非经销商登录
			//获取选择的经销商
			String searchDealer = request.getParameter("dealer1");
			if(StringUtils.isNotBlank(searchDealer)){
				try{
					Long dealer1 = Long.parseLong(searchDealer);
					curDealer = dealerService.getDealerInstance(dealer1);
					dealerId = curDealer.getId();
					data.put("dealer1", curDealer);
				}catch(Exception e){}
			}
		}
		
		//1.查询线索量 - 近一月趋势总览
		Date now = new Date();
		Calendar c = Calendar.getInstance(Locale.CHINA);
		c.setTime(now);
		int totalDay = c.getActualMaximum(Calendar.DAY_OF_MONTH);
		int m = c.get(Calendar.MONTH) + 1;
		String month = c.get(Calendar.YEAR) + "-" + (m < 10 ? ("0" + m) : m);
		Map<Integer,String> leadsMonthMap = reportService.listLeadsByMonth(dealerId, null, month,totalDay);
		
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
		
		Map<String,String> newsWeekMap = reportService.listNewsByWeek(dealerId, null, beginDate, endDate);
		this.convertLineReportFormat(data, newsWeekMap, "reportNewsWeekLineX", "reportNewsWeekLineY");
		//信息总条数
		int totalNews = 0;
		for(String value:newsWeekMap.values()){
			try{totalNews +=Integer.parseInt(value);}catch(Exception e){}
		}
		data.put("totalNews", totalNews);
		
		//400来电 - 本周来电量趋势图
		Map<String,String> phoneWeekMap = reportService.listPhoneByWeek(dealerId, null, beginDate, endDate);
		this.convertLineReportFormat(data, phoneWeekMap, "reportPhoneWeekLineX", "reportPhoneWeekLineY");
		//400来电总条数
		int total400 = 0;
		for(String value:phoneWeekMap.values()){
			try{total400 +=Integer.parseInt(value);}catch(Exception e){}
		}
		data.put("total400", total400);
		
		//3.查询本周完成率 = ((本月截止到当天的发布总数/指标数) * 100%)
		Kpi mediaKpi = kpiService.get(2L);
		int mediaThreshold = mediaKpi.getThreshold();	//阀值
		
		//查找本周发布数量
		//c.set(Calendar.DAY_OF_MONTH, 1);	//1号
		//beginDate = c.getTime();
		
		int mediaFinishCount = reportService.getPublishMediaCountByDate(dealerId,null,beginDate,endDate);
		double mediaFinishRate = 0.0;
		double mediaUnfinishRate = 100.0;
		
		mediaFinishRate = mediaFinishCount * 1.0 / mediaThreshold * 100;
		mediaUnfinishRate = 100 - mediaFinishRate;
		
		data.put("mediaFinishRate", mediaFinishRate);
		data.put("mediaUnfinishRate", mediaUnfinishRate);
		
		//4.查询本周400电话接起率 = (本周接听电话数量/本周来电数量 * 100%)
		double phoneCount = reportService.getPhoneCountByDate(dealerId,null,beginDate,endDate);
		double phoneSuccessCount = reportService.getPhoneSuccessCountByDate(dealerId,null,beginDate,endDate);
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
	@Permission(code="report/dealer/media")
	public ModelAndView media(Model model) throws Exception {
		ModelAndView view = new ModelAndView();
		view.setViewName("report/dealer/media");
		Map<String,Object> data = view.getModel();
		//查询所有媒体列表
		List<Media> mediaList = mediaService.listAll();
		data.put("mediaList", mediaList);
		
		// 返回到界面
		return view;
	}
	
	@RequestMapping(value="doMedia")
	public ModelAndView doMedia(Model model,HttpServletRequest request,HttpServletResponse response,HttpSession session) throws Exception {
		ModelAndView view = new ModelAndView();
		view.setViewName("report/dealer/media");
		Map<String,Object> data = view.getModel();
		
		//获取当前经销商
		Dealer curDealer = (Dealer) session.getAttribute("loginDealer");
		Long dealerId = null;
		if(null != curDealer){
			dealerId = curDealer.getId();
		}else{
			//当前非经销商登录
			//获取选择的经销商
			String searchDealer = request.getParameter("searchDealer");
			if(StringUtils.isNotBlank(searchDealer)){
				curDealer = dealerService.getDealerInstance(Long.parseLong(searchDealer));
				dealerId = curDealer.getId();
				
				data.put("searchDealer", curDealer);
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
			Map<String,Integer> mediaResultMap = reportService.listMediaReportByDate(dealerId,null,reportType,mediaId,beginDate,endDate);
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
		//获取当前经销商
		Dealer curDealer = (Dealer) session.getAttribute("loginDealer");
		Long dealerId = null;
		if(null != curDealer){
			dealerId = curDealer.getId();
		}else{
			//当前非经销商登录
			//获取选择的经销商
			String searchDealer = request.getParameter("searchDealer");
			if(StringUtils.isNotBlank(searchDealer)){
				curDealer = dealerService.getDealerInstance(Long.parseLong(searchDealer));
				dealerId = curDealer.getId();
				
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
			Map<String,Integer> mediaResultMap = reportService.listMediaReportByDate(dealerId,null,reportType,mediaId,beginDate,endDate);
			
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
}
