package com.citroen.wechat.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.citroen.ledp.domain.Dealer;
import com.citroen.ledp.domain.Organization;
import com.citroen.ledp.service.DealerService;
import com.citroen.ledp.service.PermissionService;
import com.citroen.ledp.util.DateUtil;
import com.citroen.wechat.domain.PublicNo;
import com.citroen.wechat.service.WechatHomeService;
import com.citroen.wechat.util.ConstantUtil;

/**
 * 首页
 * @author 何海粟
 * @date2015年6月4日
 */
@Controller("wechatHomeController")
@RequestMapping("/wechat/home")
public class HomeController {
	
	@Autowired
	private PermissionService permissionService;
	@Autowired
	private WechatHomeService wechatHomeService;
	@Autowired
	private DealerService dealerService;
	
	private Map<String,Object> params;
	
	@RequestMapping(value={"","index"})
	public String index(Model model,HttpServletRequest request,HttpServletResponse response,HttpSession session) throws Exception {
		// 公众号获取
		PublicNo publicNo = (PublicNo) request.getSession().getAttribute(ConstantUtil.SESSION_CURRENT_PUBLICNO);
		if(publicNo==null){
			return "redirect:/wechat/publicno/message";
		}
/*		if("-1".equals(publicNo.getVerify_type_info())){
			return "redirect:/wechat/publicno/unauth/"+publicNo.getVerify_type_info();
		}*/
		Map<String,Object> data = model.asMap();
		Map<String,Object> params = new HashMap<String,Object>();
		long publicNoId = 0l;
		
		Date now = new Date();
		//本周
		Date beginDate = DateUtil.getMondayOfThisWeek();
		Date endDate = DateUtil.getSundayOfThisWeek();
		Organization curOrg = (Organization) session.getAttribute("loginOrg");
		Dealer curDealer = (Dealer)session.getAttribute("loginDealer");
		
		publicNoId = publicNo.getId();
		
		if(curOrg != null){
			data.put("curOrg", curOrg.getId());
		}
		
		//当天的新消息
		long messages = wechatHomeService.getCurrDayMessages(curOrg, curDealer,publicNoId);
		data.put("currDayMsg", messages);
		
		//当天新增粉丝，不包括取消关注的
		Long fans = wechatHomeService.getFansByDate(curOrg, curDealer,publicNoId,now,now);
		data.put("currDayNewFans", fans);
		
		//总粉丝数
		Long totalFans = wechatHomeService.getFansByDate(curOrg, curDealer,publicNoId,null,null);
		data.put("totalFans", totalFans);
		
		//本周粉丝增长趋势图
		Map<String,String> fansGrow = wechatHomeService.getFansGrowByDate(curOrg, curDealer,publicNoId,beginDate,endDate);
		data.put("fansGrow", convertLineReportFormat(fansGrow));
		
		//本周活动开展统计
		Map<String, String> campaign = wechatHomeService.getCampaignByDate(curOrg, curDealer, beginDate, endDate);
		data.put("totalCampaign", convertLineReportFormat(campaign));
		
		//本周营销插件使用情况
		List<Map> pluginRecords = wechatHomeService.getPluginRecordByDate(curOrg, curDealer, beginDate, endDate);
		String pluginPie=convertPieReportFormat(pluginRecords);
		data.put("pluginPie", pluginPie);
		if(!pluginPie.equals("[]")){
			data.put("ispluginPie","pluginPie");
		}
		//本周线索数量
		List<Map> leadsRecords = wechatHomeService.getLeadsRecordByDate(curOrg, curDealer, beginDate, endDate);
		String leadsPie=convertLeadsPieReportFormat(leadsRecords);
		data.put("leadsPie", leadsPie);
		if(!leadsPie.equals("[]")){
			data.put("isleadsPie","isleadsPie");
		}
		// 返回到界面
		return "wechat/home/index";
	}
	
	
	/**
	 * 转换成线性报表前台显示
	 * @param data
	 */
	private String convertLineReportFormat(Map<String, String> data) {
		StringBuffer reportLine = new StringBuffer();	
		reportLine.append("[");
		List<Date> weeks = DateUtil.dateToWeek1(new Date());
		for(int i=0; i<weeks.size(); i++){
			String count = data.get(DateUtil.format(weeks.get(i), "yyyy-MM-dd"));
			if(StringUtils.isNotBlank(count)){
				reportLine.append(count);
			}else{
				reportLine.append("0");
			}
			reportLine.append(",");
		}
		reportLine.substring(0, reportLine.length()-1);
		reportLine.append("]");
		return reportLine.toString();
	}
	
	/**
	 * 
	 * 转换成饼图前台显示
	 * @param data
	 */
	private String convertPieReportFormat(List<Map> list) {
		if(CollectionUtils.isEmpty(list)){
			return "[]";
		}
		String[] colors = {"#7cb5ec","#8085e9","#d12d32","#7cb5ec"};
		StringBuffer reportPie= new StringBuffer();	
		reportPie.append("[");
		int i = 0;
		for(Map map : list){
			reportPie.append("{");
			if(i<4){
				reportPie.append("color:'"+colors[i++]+"',");
			}
			reportPie.append("name:'"+map.get("name")+"',y:"+map.get("ct"));
		    reportPie.append("},");
		}
		reportPie.substring(0, reportPie.length()-2);
		reportPie.append("]");
		return reportPie.toString();
	}
	
	/**
	 * 
	 * 转换成饼图前台显示
	 * @param data
	 */
	private String convertLeadsPieReportFormat(List<Map> list) {
		String[] colors = {"#7cb5ec","#8085e9","#d12d32","#7cb5ec"};
		if(CollectionUtils.isEmpty(list)){
			return "[]";
		}
		//活动留资数
		long campaignCount = 0l;
		//常规留资数
		long normalCount = 0l;
		StringBuffer reportPie= new StringBuffer();	
		reportPie.append("[");
		
		try {
			campaignCount = (Long) list.get(0).get("ct");
			normalCount = (Long) list.get(1).get("ct");
		} catch (Exception e) {
			
		}
		return "[{name:'活动留资',y:"+campaignCount+",color:'#7cb5eb'},{name:'常规留资',y:"+normalCount+",color:'#d12d32'}]";
	}
}
