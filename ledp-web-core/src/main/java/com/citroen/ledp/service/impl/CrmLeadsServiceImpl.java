package com.citroen.ledp.service.impl;

import com.citroen.ledp.dao.mybaits.MybaitsGenericDao;
import com.citroen.ledp.domain.*;
import com.citroen.ledp.exception.LedpException;
import com.citroen.ledp.service.ConstantService;
import com.citroen.ledp.service.CrmLeadsService;
import com.citroen.ledp.util.ExcelUtil;
import com.citroen.ledp.util.MapUtil;
import com.citroen.wechat.api.util.DateUtil;
import com.citroen.wechat.form.LeadsReportForm;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 客户服务类
 * @author      miaoshuai
 * @email       miaoshuai@tocersfot.com
 * @company		www.tocersoft.com
 * @create-time 2015年3月13日 下午1:58:34
 * @version     1.0
 */
@Service
public class CrmLeadsServiceImpl implements CrmLeadsService{
	private final static String dateFormat = "yyyy-MM-dd HH:mm";
	@Autowired
	private MybaitsGenericDao<Long> mybaitsGenericDao;
	@Autowired
	private ConstantService constantService;
	
	
	public List<CrmLeads> executeQuery(Map params) throws LedpException{
		Map<String,Object> condition = getCondition(params);
		Map namedParams = (Map)condition.get("namedParams");
		
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT `id`,`D`,`E`,`J`,`S`,`batch`,`Y`");
//		sql.append(" `address`,`intent`,`province`,");
//		sql.append(" `city`,`district`,`user_create`,");
//		sql.append(" `user_update`,`date_create`,`date_update`");
		sql.append(" FROM t_crm_leads");
		sql.append(" WHERE 1=1");
		sql.append(condition.get("namedSql"));
		if (params.containsKey("sortName")) {
			sql.append(" order by " + params.get("sortName"));
		}
		if (params.containsKey("sortOrder")) {
			sql.append( " " + params.get("sortOrder"));
		}
		Map paginateParams = new HashMap();
		try {
			Integer pageSize = Integer.valueOf(params.get("pageSize").toString());
			Integer pageNumber = Integer.valueOf(params.get("pageNumber").toString());
			int offset=0;
			if(pageNumber!=0){
				offset = (pageNumber - 1) * pageSize;
			}
			paginateParams.put("offset", offset);
			paginateParams.put("max", pageSize);
		} catch (Exception e1) {
			e1.printStackTrace();
			paginateParams.put("offset", 10);
			paginateParams.put("max", 0);
		}
		return mybaitsGenericDao.executeQuery(CrmLeads.class,sql.toString(),namedParams,paginateParams);
	}
	
	public int getTotalRow(Map params) throws LedpException{
		Map<String,Object> condition = getCondition(params);
		Map namedParams = (Map)condition.get("namedParams");
		
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT COUNT(1) as count");
		sql.append(" FROM t_crm_leads");
		sql.append(" WHERE 1=1");
		sql.append(condition.get("namedSql"));
		List<Map> list = mybaitsGenericDao.executeQuery(sql.toString(),namedParams);
		if(list.isEmpty()){
			return 0;
		}
		Map map = list.get(0);
		return map.isEmpty()?0:Integer.parseInt(map.get("count").toString());
	}
	
	public Map<String,Object> getCondition(Map<String,Object> params){
		MapUtil<String,Object> mapUtil = new MapUtil<String,Object>(params);
		Map<String,Object> namedParams = new HashMap<String,Object>();
		
		StringBuilder namedSql = new StringBuilder();
		
		// 客户名称
		if(!mapUtil.isBlank("name")){
			String name = mapUtil.get("name").toString();
			namedParams.put("name","%"+name+"%");
			namedSql.append(" and name like :name");
		}
		if(!mapUtil.isBlank("D")){
			String name = mapUtil.get("D").toString();
			namedParams.put("name","%"+name+"%");
			namedSql.append(" and D like :name");
		}
		// 手机号码
		if(!mapUtil.isBlank("phone")){
			String phone = params.get("phone").toString();
			namedParams.put("phone","%"+phone+"%");
			namedSql.append(" and phone like :phone");
		}
		if(!mapUtil.isBlank("E")){
			String phone = params.get("E").toString();
			namedParams.put("phone","%"+phone+"%");
			namedSql.append(" and E like :phone");
		}
		// 意向类别
		if(!mapUtil.isBlank("intent")){
			Long intent = Long.parseLong(params.get("intent").toString());
			namedParams.put("intent",intent);
			namedSql.append(" and intent =:intent");
		}
		if(!mapUtil.isBlank("S")){
			String yxjb = params.get("S").toString();
			namedParams.put("s","%"+yxjb+"%");
			namedSql.append(" and S like:s");
		}
		// 电子邮箱
		if(!mapUtil.isBlank("email")){
			String email = params.get("email").toString();
			namedParams.put("email","%"+email+"%");
			namedSql.append(" and email like :email");
		}
		HttpSession session = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getSession();
		User user = (User) session.getAttribute("loginUser");
		Organization org =  user.getOrg();
		//大区名称
		if(org!=null){
			if(!mapUtil.isBlank("J")){
				Long wdmc = Long.parseLong(params.get("J").toString());
				namedParams.put("j",wdmc);
				namedSql.append(" and dealer =:j");
			}else if(!mapUtil.isBlank("H")){
				Long wdmc = Long.parseLong(params.get("H").toString());
				namedParams.put("h",wdmc);
				namedSql.append(" and organization =:h");
			}else{
				if(org.getLevel()==2){
					namedParams.put("h",org.getId());
					namedSql.append(" and organization =:h");
				}
			}
		}else{
			namedParams.put("j",user.getDealer().getId());
			namedSql.append(" and dealer =:j");
		}
		//时间
		if(!mapUtil.isBlank("startTime")){
			String kssj = params.get("startTime").toString();
			namedParams.put("start",kssj);
			namedSql.append(" and batch>=:start");
		}
		if(!mapUtil.isBlank("endTime")){
			String endTime = params.get("endTime").toString();
			namedParams.put("end",endTime);
			namedSql.append(" and batch<= :end");
		}
		Map<String,Object> rs = new HashMap<String,Object>();
		rs.put("namedSql",namedSql);
		rs.put("namedParams",namedParams);
		return rs;
	}
	
	public CrmLeads get(long id) throws LedpException{
		return mybaitsGenericDao.get(CrmLeads.class, id);
	}
	
	public Customer getByPhone(String phone) throws LedpException{
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT `id`,`name`,`phone`,`email`,");
		sql.append(" `address`,`intent`,`province`,");
		sql.append(" `city`,`district`,`user_create`,");
		sql.append(" `user_update`,`date_create`,`date_update`");
		sql.append(" FROM t_crm_leads");
		sql.append(" WHERE phone = "+phone);
		return mybaitsGenericDao.find(Customer.class, sql.toString());
	}
	
	/**
	 * 导出查询
	 * @param params
	 * @return
	 * @throws LedpException
	 */
	public List<Customer> exportQuery(Map params) throws LedpException{
		Map<String,Object> condition = getCondition(params);
		Map namedParams = (Map)condition.get("namedParams");
		
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT `id`,`name`,`phone`,`email`,");
		sql.append(" `address`,`intent`,`province`,");
		sql.append(" `city`,`district`,`user_create`,");
		sql.append(" `user_update`,`date_create`,`date_update`");
		sql.append(" FROM t_crm_leads");
		sql.append(" WHERE 1=1");
		sql.append(condition.get("namedSql"));
		
		return mybaitsGenericDao.executeQuery(Customer.class,sql.toString(),namedParams);
	}
	
	/**
	 * 创建导出Excel文件
	 * @return
	 */
	public HSSFWorkbook createWorkbook(List<Customer> customerList){
		HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet("客户信息导出记录");
        String[] titles = new String[]{
        		"客户名称","手机号码","意向级别","手机号码","电子邮箱"
        }; 
        Integer[] titleWidths = new Integer[]{12,12,12,20,30};
        // 列头
        HSSFRow row = ExcelUtil.createHSSFRow(titles, titleWidths, wb, sheet, 0);
        for(int i = 0; i < customerList.size(); i++){
        	row = sheet.createRow(i+1);
        	Customer item = customerList.get(i);
        	// 客户名称
        	row.createCell(0).setCellValue(item.getName());
        	// 手机号码
        	row.createCell(1).setCellValue(item.getPhone());
        	// 意向级别
        	String intentName = "";
        	if(null != item.getIntent()){
        		intentName = item.getIntent().getName();
        	}
        	row.createCell(2).setCellValue(intentName);
        	// 手机号码
        	row.createCell(3).setCellValue(item.getPhone());
        	// 电子邮箱
        	row.createCell(4).setCellValue(item.getEmail());
        }
		return wb;
	}
	
	public void update(CrmLeads lead) throws LedpException {
		mybaitsGenericDao.update(lead);
	}
	
	public void saves(CrmLeads lead) throws LedpException {
		mybaitsGenericDao.save(lead);
	}
	/**
	 * 数据合并
	 */
    @Transactional
    public void merges(Date date){
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time=sdf.format(date);
		String sql="select * from t_crm_leads where batch='"+time+"' and S is not null order by G asc";
		try {
			//根据批次时间查询出当前批次的数据
			List<CrmLeads> rows=mybaitsGenericDao.executeQuery(CrmLeads.class,sql);
			for(CrmLeads lead:rows){
				Dealer dealer = mybaitsGenericDao.find(Dealer.class,"select id from t_dealer where code='"+lead.getI()+"'");
				if(dealer!=null){
					Long wdbm=dealer.getId();//网点编码，根据网点编码和手机号进行更新
					String phone=lead.getE();//电话
					StringBuffer update = new StringBuffer();
					StringBuffer update1 = new StringBuffer();
					if(StringUtils.isNotBlank(lead.getS())){
						if("失控".equals(lead.getS())){
							//跟进状态设置为战败，意向级别设置为A级
							update.append("update t_leads set ledp_follow=4030,ledp_intent=2010 where ledp_dealer="+wdbm+" and phone='"+phone+"'");
							update1.append("update t_leads_merged set ledp_follow=4030,ledp_intent=2010 where ledp_dealer="+wdbm+" and phone='"+phone+"'");
							
						}else if("战败".equals(lead.getS())){
							update.append("update t_leads set ledp_follow=4050,ledp_intent=2010 where ledp_dealer="+wdbm+" and phone='"+phone+"'");
							update1.append("update t_leads_merged set ledp_follow=4050,ledp_intent=2010 where ledp_dealer="+wdbm+" and phone='"+phone+"'");
						}else if("成交".equals(lead.getS())){
							update.append("update t_leads set ledp_follow=4040,ledp_intent=2050 where ledp_dealer="+wdbm+" and phone='"+phone+"'");
							update1.append("update t_leads_merged set ledp_follow=4040,ledp_intent=2050 where ledp_dealer="+wdbm+" and phone='"+phone+"'");
						}else{
							//跟进中
							update.append("update t_leads set ledp_follow=4020");
							update1.append("update t_leads_merged set ledp_follow=4020");
							String jbsql="select * from t_constant where name='"+lead.getS()+"'";
							List<Constant> con=mybaitsGenericDao.executeQuery(Constant.class,jbsql);
							if(null!=con &&con.size()>0){
								Constant c=con.get(0);
								update.append(",ledp_intent="+c.getId()+"");
								update1.append(",ledp_intent="+c.getId()+"");
							}
							update.append("  where ledp_dealer="+wdbm+"  and phone='"+phone+"'");
							update1.append("  where ledp_dealer="+wdbm+" and phone='"+phone+"'");
						}
						//执行更新操作
						mybaitsGenericDao.execute(update.toString());
						mybaitsGenericDao.execute(update1.toString());
						try{
							long intent=2010;
							long follow=4020;
							if("失控".equals(lead.getS())){
								intent=2010;
								follow = 4030;
							}else
							if("战败".equals(lead.getS())){
								intent=2010;
								follow = 4050;
							}else
							if("成交".equals(lead.getS())){
									intent=2050;
									follow = 4040;
							}else{
								Constant constant=getConstantByCode(lead.getS());
								if(constant!=null){
									intent=constant.getId();
									follow=4020;
								}
							}
							String sqlcustomer="update t_customer set follow="+follow+",intent="+intent+" where phone='"+phone+"'";
							mybaitsGenericDao.execute(sqlcustomer);
						}catch(Exception e){
						}
						
					}
				}
			}
		
		} catch (LedpException e) {
			e.printStackTrace();
		}
	}
	public List<Dealer> queryByCondition(String code) {

		List<Dealer> result = null;
		StringBuffer sql = new StringBuffer();
		sql.append("select * from t_dealer where code='"+code+"'");
		try {
			result = mybaitsGenericDao.executeQuery(Dealer.class, sql.toString());
		} catch (LedpException e) {
			e.printStackTrace();
		}
		return result;
	}
	public Constant getConstantByCode(String code) {
		try {
			return mybaitsGenericDao.find(Constant.class,"select * from t_constant where code='"+code+"'");
		} catch (LedpException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @Title: getApiLogTotal
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param params
	 * @return
	 * @return int
	 * @throws LedpException 
	 */
	public int getApiLogTotal(Map<String, Object> params) throws LedpException {
		String query = "select count(id) count from t_api_log where 1=1 "+getApiLogCondition(params);
		Map row = mybaitsGenericDao.find(query);
		return Integer.parseInt(row.get("count").toString());
	}
	/**
	 * @Title: executeApiLogQuery
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param params
	 * @return
	 * @return List<ApiLog>
	 * @throws LedpException 
	 */
	public List<ApiLog> executeApiLogQuery(Map<String, Object> params) throws LedpException {
		String query = "select * from t_api_log where 1=1 "+getApiLogCondition(params);
		if (params.containsKey("sortName")) {
			query +=" order by "+params.get("sortName");
		}
		if (params.containsKey("sortOrder")) {
			query +=" "+params.get("sortOrder");
		}
		Integer offset = 0;
		Integer max = 10;
		try {
			Integer pageSize = Integer.valueOf(params.get("pageSize").toString());
			Integer pageNumber = Integer.valueOf(params.get("pageNumber").toString());
			if(pageNumber>0){
				offset = (pageNumber - 1) * pageSize;
			}
		} catch (Exception e1) {}
		
		query +=" limit "+offset+","+max;
		return mybaitsGenericDao.executeQuery(ApiLog.class, query);
	}
	public String getApiLogCondition(Map<String,Object> params){
		StringBuilder condition = new StringBuilder();
		
		Object date1 = params.get("date1");
		Object date2 = params.get("date2");
		Object serial = params.get("serial");
		Object state = params.get("state");
		
		if(date1!=null){
			String d = date1.toString();
			if(StringUtils.isNotBlank(d)){
				condition.append(" and date>='"+d+"'");
			}
		}
		if(date2!=null){
			String d = date2.toString();
			if(StringUtils.isNotBlank(d)){
				condition.append(" and date<='"+d+"'");
			}
		}
		if(serial!=null){
			String s = serial.toString();
			if(StringUtils.isNotBlank(s)){
				condition.append(" and serial like '%"+s+"%'");
			}
		}
		if(state!=null){
			String s = state.toString();
			if(StringUtils.isNotBlank(s)){
				condition.append(" and state like '%"+s+"%'");
			}
		}
		return condition.toString();
	}
	
	public List<CrmLeads> executeQuery(LeadsReportForm form) throws LedpException{
		Map<String,Object> condition = getCondition(form);
		Map namedParams = (Map)condition.get("namedParams");
		
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT *");
//		sql.append(" `address`,`intent`,`province`,");
//		sql.append(" `city`,`district`,`user_create`,");
//		sql.append(" `user_update`,`date_create`,`date_update`");
		sql.append(" FROM t_crm_leads");
		sql.append(" WHERE 1=1");
		sql.append(condition.get("namedSql"));
		sql.append(" order by D asc");
		Map paginateParams = new HashMap();
		Integer pageSize = form.getPageSize();
		Integer pageNumber = form.getCurrentPage();
		int offset=0;
		if(pageNumber!=0){
			offset = (pageNumber - 1) * pageSize;
		}
		paginateParams.put("offset", offset);
		paginateParams.put("max", pageSize);
		
		return mybaitsGenericDao.executeQuery(CrmLeads.class,sql.toString(),namedParams,paginateParams);
	}
	
	public int getTotalRow(LeadsReportForm form) throws LedpException{
		Map<String,Object> condition = getCondition(form);
		Map namedParams = (Map)condition.get("namedParams");
		
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT COUNT(1) as count");
		sql.append(" FROM t_crm_leads");
		sql.append(" WHERE 1=1");
		sql.append(condition.get("namedSql"));
		List<Map> list = mybaitsGenericDao.executeQuery(sql.toString(),namedParams);
		if(list.isEmpty()){
			return 0;
		}
		Map map = list.get(0);
		return map.isEmpty()?0:Integer.parseInt(map.get("count").toString());
	}
	
	public Map<String,Object> getCondition(LeadsReportForm form){
		Map<String,Object> namedParams = new HashMap<String,Object>();
		
		StringBuilder namedSql = new StringBuilder();
		
		// 客户名称
		if(StringUtils.isNotBlank(form.getName())){
			namedParams.put("name","%"+form.getName()+"%");
			namedSql.append(" and d like :name");
		}
		// 手机号码
		if(StringUtils.isNotBlank(form.getPhone())){
			namedParams.put("phone","%"+form.getPhone()+"%");
			namedSql.append(" and e like :phone");
		}
		// 意向类别
		if(form.getLedpIntentId() != null){
			Constant intent;
			try {
				intent = constantService.get(form.getLedpIntentId());
				namedParams.put("intent",intent.getName());
				namedSql.append(" and IFNULL(s,r) =:intent");
			} catch (LedpException e) {
			}
		}
		//大区
		if(form.getOrganizationSelectedId() != null){
			namedParams.put("organization",form.getOrganizationSelectedId());
			namedSql.append(" and organization =:organization");
		}
		//网点
		if(form.getLedpDealer() != null){
			namedParams.put("dealer",form.getLedpDealer());
			namedSql.append(" and dealer =:dealer");
		}
		//时间
		if(form.getBeginDate() != null){
			namedParams.put("start",DateUtil.convert(form.getBeginDate(), dateFormat));
			namedSql.append(" and batch>=:start");
		}
		if(form.getEndDate() != null){
			namedParams.put("end",DateUtil.convert(form.getEndDate(), dateFormat));
			namedSql.append(" and batch<= :end");
		}
		Map<String,Object> rs = new HashMap<String,Object>();
		rs.put("namedSql",namedSql);
		rs.put("namedParams",namedParams);
		return rs;
	}
	
}