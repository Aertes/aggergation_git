package com.citroen.ledp.controller;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.citroen.ledp.domain.Dealer;
import com.citroen.ledp.domain.Kpi;
import com.citroen.ledp.domain.Organization;
import com.citroen.ledp.domain.User;
import com.citroen.ledp.service.DealerService;
import com.citroen.ledp.service.KpiService;
import com.citroen.ledp.service.LeadsService;
import com.citroen.ledp.service.PermissionService;
import com.citroen.ledp.service.ReportService;
import com.citroen.ledp.util.DateUtil;

/**
 * @Title: AuthController.java
 * @Package com.citroen.ledp.controller
 * @Description: TODO(登录退出类)
 * @author 廖启洪
 * @date 2015年1月29日 下午3:24:24
 * @version V1.0
 */
@Controller
@RequestMapping("/home")
public class HomeController {
	
	@Autowired
	private ReportService reportService;
	@Autowired
	private DealerService dealerService;
	@Autowired
	private KpiService kpiService;
	@Autowired
	private LeadsService leadsService;
	@Autowired
	private PermissionService permissionService;
	
	@RequestMapping(value={"","index"})
	public ModelAndView index(Model model,HttpServletRequest request,HttpServletResponse response,HttpSession session) throws Exception {
		ModelAndView view = new ModelAndView();
		view.setViewName("home/index");
		
		if(permissionService.hasAuth("news/auth")){
			model.addAttribute("permission", "permission");
		}
		String home="";
		try{
			home=request.getParameter("home");
			if(home!=null){
				model.addAttribute("home", home);
			}
		}catch(Exception e){
			
		}
		Map<String,Object> data = view.getModel();
		//本周
		Date beginDate = DateUtil.getMondayOfThisWeek();
		Date endDate = DateUtil.getSundayOfThisWeek();
		Organization curOrg = (Organization) session.getAttribute("loginOrg");
		Dealer curDealer = (Dealer)session.getAttribute("loginDealer");
		//1.本周信息发布数量
		int mediaCount = 0;
		if(null != curOrg){
			if(curOrg.getLevel() == 1){
				//总部
				mediaCount = reportService.getPublishMediaCountByDate(null,null, beginDate, endDate);
			}else{
				//大区
				mediaCount = reportService.getPublishMediaCountByDate(null, curOrg.getId(), beginDate, endDate);
			}
		}else if(null != curDealer){
			//经销商
			mediaCount = reportService.getPublishMediaCountByDate(curDealer.getId(),null, beginDate, endDate);
		}
		data.put("mediaCount", mediaCount);
		
		//2.本周线索数量
		int leadsCount = 0;
		int totalDealerCount = 1;
		
		if(null != curOrg){
			if(curOrg.getLevel() == 1){
				//总部
				leadsCount = leadsService.getLeadsCountByDate(null,null,beginDate,endDate);
				totalDealerCount = dealerService.getTotalLeadesCount(null);
			}else{
				//大区
				leadsCount = leadsService.getLeadsCountByDate(null,curOrg.getId(),beginDate,endDate);
				totalDealerCount = dealerService.getTotalLeadesCount(null);
			}
		}else if(null != curDealer){
			//经销商
			leadsCount = leadsService.getLeadsCountByDate(curDealer.getId(),null,beginDate,endDate);
		}
		
		Kpi leadsKpi = kpiService.get(1L);
		int leadsThreshold = leadsKpi.getThreshold();	//阀值
		int totalLeadsCount = totalDealerCount * leadsThreshold / 4;
		data.put("leadsCount", leadsCount);
		data.put("totalLeadsCount", totalLeadsCount);
		
		//3.本周400来电量
		Map<String,String> phoneCountResultMap = null;
		if(null != curOrg){
			if(curOrg.getLevel() == 1){
				//总部
				phoneCountResultMap = reportService.listPhoneByWeek(null, null, beginDate, endDate);
			}else{
				//大区
				phoneCountResultMap = reportService.listPhoneByWeek(null, curOrg.getId(), beginDate, endDate);
			}
		}else if(null != curDealer){
			//经销商
			phoneCountResultMap = reportService.listPhoneByWeek(curDealer.getId(), null, beginDate, endDate);
		}
		this.convertLineReportFormat(data,phoneCountResultMap, "reportPhoneWeekLineX", "reportPhoneWeekLineY");
		
		//4.本周400接起率
		Map<String,String> phoneSuccessResultMap = null;
		if(null != curOrg){
			if(curOrg.getLevel() == 1){
				//总部
				phoneSuccessResultMap = reportService.listPhoneSuccessByWeek(null, null, beginDate, endDate);
			}else{
				//大区
				phoneSuccessResultMap = reportService.listPhoneSuccessByWeek(null, curOrg.getId(), beginDate, endDate);
			}
		}else if(null != curDealer){
			//经销商
			phoneSuccessResultMap = reportService.listPhoneSuccessByWeek(curDealer.getId(), null, beginDate, endDate);
		}
		Iterator<String> phoneSuccessMapIter = phoneSuccessResultMap.keySet().iterator();
		while(phoneSuccessMapIter.hasNext()){
			String key = phoneSuccessMapIter.next();
			String phoneSuccessCount = phoneSuccessResultMap.get(key);
			String phoneCount = phoneCountResultMap.get(key);

			phoneSuccessResultMap.put(key, "0");
			if(!StringUtils.isBlank(phoneSuccessCount) && !StringUtils.isBlank(phoneCount)){
				double num1 = 0;
				double num2 = 0;
				try{num1 = Double.parseDouble(phoneSuccessCount);}catch(Exception e){}
				try{num2 = Double.parseDouble(phoneCount);}catch(Exception e){}
				if(num2>0){
					java.math.BigDecimal bd = new java.math.BigDecimal((num1/num2*100)+"");  
					bd = bd.setScale(2,BigDecimal.ROUND_HALF_UP);
					phoneSuccessResultMap.put(key,String.valueOf(bd.doubleValue()));
				}else{
					phoneSuccessResultMap.put(key, "0");
				}
			}
		}
		this.convertLineReportFormat(data,phoneSuccessResultMap, "reportPhoneSuccessWeekLineX", "reportPhoneSuccessWeekLineY");
		// 返回到界面
		return view;
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
			String count = resultMap.get(day);
			
			reportLineXBuf.append(day.toString()).append(",");
			reportLineYBuf.append(count).append(",");
		}
		
		String reportLineX = reportLineXBuf.substring(0,reportLineXBuf.length() - 1) + "]";
		String reportLineY = reportLineYBuf.substring(0,reportLineYBuf.length() - 1) + "]";
		data.put(xKey, reportLineX);
		data.put(yKey, reportLineY);
		
	}
}
