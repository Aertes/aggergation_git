package com.citroen.ledp.service.impl;

import com.citroen.ledp.dao.mybaits.MybaitsGenericDao;
import com.citroen.ledp.domain.*;
import com.citroen.ledp.exception.LedpException;
import com.citroen.ledp.service.ConstantService;
import com.citroen.ledp.service.DealerService;
import com.citroen.ledp.service.LeadsMergedService;
import com.citroen.ledp.util.ExcelUtil;
import com.citroen.ledp.util.MapUtil;
import com.citroen.ledp.util.SysConstant;
import com.citroen.wechat.api.util.DateUtil;
import com.citroen.wechat.form.LeadsReportForm;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 合并后留资服务类
 * @author      miaoshuai
 * @email       miaoshuai@tocersfot.com
 * @company		www.tocersoft.com
 * @create-time 2015年3月18日 上午10:43:31
 * @version     1.0
 */
@Service
public class LeadsMergedServiceImpl implements LeadsMergedService{
    private static Logger logger = Logger.getLogger(LeadsMergedServiceImpl.class);
	private final static String dateFormat = "yyyy-MM-dd HH:mm";
	@Autowired
	private MybaitsGenericDao<Long> mybaitsGenericDao;
	@Autowired
	private ConstantService constantService;
	@Autowired
	private DealerService dealerService;
	
	
	public List<LeadsMerged> executeQuery(Map params) throws LedpException{
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT o1.id,o1.name,o1.phone,o1.ledp_media,");
		sql.append("o1.ledp_dealer,o1.createTime,");
		sql.append("o1.ledp_intent,o1.ledp_follow");
		sql.append(" FROM t_leads_merged o1");
		sql.append(" LEFT JOIN t_media o2 ON o2.id = o1.ledp_media");		// 媒体渠道
		sql.append(" LEFT JOIN t_constant o3 ON o3.id = o1.ledp_type");		// 线索类型
		sql.append(" LEFT JOIN t_constant o4 ON o4.id = o1.ledp_follow");	// 跟进状态
		sql.append(" LEFT JOIN t_dealer o5 ON o5.id = o1.ledp_dealer");	// 网点
		sql.append(" LEFT JOIN t_vehicle_series o6 ON o6.id = o1.ledp_series");	// 车系
		sql.append(" LEFT JOIN t_vehicle o7 ON o7.id = o1.ledp_vehicle");	// 车型
		sql.append(" LEFT JOIN t_constant o8 ON o8.id = o1.ledp_intent");	// 意向类别
		sql.append(" WHERE 1=1");
		// 获取查询条件
		Map<String,Object> condition = getCondition(params);
		Map namedParams = (Map)condition.get("namedParams");
		sql.append(condition.get("namedSql"));
		if (params.containsKey("sortName")) {
			String sortName="";
			if(params.get("sortName").equals("ledpMedia.name")){
				sortName="ledp_media";
			}else if(params.get("sortName").equals("ledpDealer.name")){
				sortName="ledp_dealer";
			}else if(params.get("sortName").equals("ledpIntent.name")){
				sortName="ledp_intent";
			}else if(params.get("sortName").equals("ledpFollow.name")){
				sortName="ledp_follow";
			}else{
				sortName=params.get("sortName").toString();
			}
			sql.append (" order by o1." + sortName);
		}
		if (params.containsKey("sortOrder")) {
			sql.append (" " + params.get("sortOrder"));
		}
		Map paginateParams = new HashMap();
		try {
			Integer pageSize = Integer.valueOf(params.get("pageSize").toString());
			Integer pageNumber = Integer.valueOf(params.get("pageNumber").toString());
			int offset = (pageNumber - 1) * pageSize;
			paginateParams.put("offset", offset);
			paginateParams.put("max", pageSize);
		} catch (Exception e1) {
			paginateParams.put("offset", 10);
			paginateParams.put("max", 0);
		}
		return mybaitsGenericDao.executeQuery(LeadsMerged.class,sql.toString(),namedParams,paginateParams);
	}
	
	public int getTotalRow(Map params) throws LedpException{
		Map<String,Object> condition = getCondition(params);
		Map namedParams = (Map)condition.get("namedParams");
		
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT COUNT(1) as count");
		sql.append(" FROM t_leads_merged o1");
		sql.append(" LEFT JOIN t_media o2 ON o2.id = o1.ledp_media");		// 媒体渠道
		sql.append(" LEFT JOIN t_constant o3 ON o3.id = o1.ledp_type");		// 线索类型
		sql.append(" LEFT JOIN t_constant o4 ON o4.id = o1.ledp_follow");	// 跟进状态
		sql.append(" LEFT JOIN t_dealer o5 ON o5.id = o1.ledp_dealer");	// 网点
		sql.append(" LEFT JOIN t_vehicle_series o6 ON o6.id = o1.ledp_series");	// 车系
		sql.append(" LEFT JOIN t_vehicle o7 ON o7.id = o1.ledp_vehicle");	// 车型
		sql.append(" LEFT JOIN t_constant o8 ON o8.id = o1.ledp_intent");	// 意向类别
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
		
		// 媒体渠道
		if(!mapUtil.isBlank("ledpMediaId")){
			Long media = Long.parseLong(params.get("ledpMediaId").toString());
			namedParams.put("ledpMediaId",media);
			namedSql.append(" and o1.ledp_media =:ledpMediaId");
		}
		// 意向类别
		if(!mapUtil.isBlank("ledpIntentId")){
			Long ledpIntentId = Long.parseLong(params.get("ledpIntentId").toString());
			namedParams.put("ledpIntentId",ledpIntentId);
			namedSql.append(" and o1.ledp_intent =:ledpIntentId");
		}
		// 跟进状态
		if(!mapUtil.isBlank("ledpFollowId")){
			Long ledpFollowId = Long.parseLong(params.get("ledpFollowId").toString());
			namedParams.put("ledpFollowId",ledpFollowId);
			namedSql.append(" and o1.ledp_follow =:ledpFollowId");
		}
		// 留资开始时间
		if(!mapUtil.isBlank("beginDate")){
			String beginDate = params.get("beginDate").toString();
			namedParams.put("beginDate",beginDate);
			namedSql.append(" and o1.createTime >=:beginDate");
		}
		// 留资结束时间
		if(!mapUtil.isBlank("endDate")){
			String endDate = params.get("endDate").toString();
			namedParams.put("endDate",endDate);
			namedSql.append(" and o1.CreateTime <=:endDate");
		}
		// 手机号码
		if(!mapUtil.isBlank("phone")){
			String phone = params.get("phone").toString();
			namedParams.put("phone",phone);
			namedSql.append(" and o1.phone =:phone");
		}
		// 组织ID
		HttpSession session = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getSession();
		User user = (User) session.getAttribute("loginUser");
		Organization org = user.getOrg();
		if(org!=null){
			if(!mapUtil.isBlank("organizationSelectedId") && !mapUtil.isBlank("organizationLevel")){
				String organization = params.get("organizationSelectedId").toString();
				String level = params.get("organizationLevel").toString();
				
				// 用户如果选择总部
				if(StringUtils.equals(level, "1")){
					/*if(null!= org){
						namedParams.put("organization",organization);
						namedSql.append(" and o5.organization =:organization");
					}else if(null == org){
						namedParams.put("organization",organization);
						namedSql.append(" and o1.ledp_dealer =:organization");
					}*/
					if(!organization.equals("1")){
						if(organization.equals("2")){
							namedParams.put("organization",organization);
							namedSql.append(" and o5.organization =:organization");
						}else{
							namedParams.put("organization",organization);
							namedSql.append(" and o1.ledp_dealer =:organization");
						}
					}
				}else if(StringUtils.equals(level,"2")){
					if(2==org.getLevel()){
						namedParams.put("organization",organization);
						namedSql.append(" and o1.ledp_dealer =:organization");
					}else{
						namedParams.put("organization",organization);
						namedSql.append(" and o5.organization =:organization");
					}
				}else if(StringUtils.equals(level, "3")){
					Dealer dealer = dealerService.gets(Long.valueOf(organization));
					namedParams.put("organization",organization);
					namedSql.append(" and o1.ledp_dealer =:organization");
				}
				
				/*if(StringUtils.equals(level,"2")){
					// 大区
					namedParams.put("organization",organization);
					namedSql.append(" and o2.organization =:organization");
				}else if(StringUtils.equals(level, "3")){
					// 网点
					namedParams.put("organization",organization);
					namedSql.append(" and o1.ledp_dealer =:organization");
				}*/
			}
		}else{
			namedParams.put("organization",user.getDealer().getId());
			namedSql.append(" and o1.ledp_dealer =:organization");
		}
		// 客户名称
		if(!mapUtil.isBlank("name")){
			String name = params.get("name").toString();
			namedParams.put("name",name);
			namedSql.append(" and o1.name =:name");
		}
		
		Map<String,Object> rs = new HashMap<String,Object>();
		rs.put("namedSql",namedSql);
		rs.put("namedParams",namedParams);
		return rs;
	}
	
	public void update(LeadsMerged leadsMerged) throws LedpException{
		mybaitsGenericDao.update(leadsMerged);
	}
	
	public LeadsMerged get(long id) throws LedpException{
		return mybaitsGenericDao.get(LeadsMerged.class, id);
	}
	
	/**
	 * 根据电话号码和网点编码查询
	 * @param phone
	 * @param dealerCode
	 * @return
	 * @throws LedpException 
	 */
	public LeadsMerged getByPhoneAndDealerCode(String phone,String dealerCode) throws LedpException{
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT * FROM t_leads_merged o1");
		sql.append(" LEFT JOIN t_dealer o2");
		sql.append(" ON o1.ledp_dealer = o2.id");
		sql.append(" WHERE o1.phone = '"+phone+"'");
		sql.append(" AND o2.code = '"+dealerCode+"'");
		return mybaitsGenericDao.find(LeadsMerged.class, sql.toString());
	}

    @Transactional
	public void importLeadsMerged(String filePath) {
		File file = new File(filePath);
		InputStream is = null;
		try {
			is = new FileInputStream(file);
			// 根据上述创建的输入流 创建工作簿对象 
			HSSFWorkbook wb = new HSSFWorkbook(is);
			// 得到第一页 sheet,Sheet是从0开始索引的 
			HSSFSheet sheet = wb.getSheetAt(0);
			for (int i = 1; i <= sheet.getLastRowNum(); i++) {
				HSSFRow row = sheet.getRow(i);
				if(null == row){
					continue;
				}
				// 1.获取单元格的值
				String phone = StringUtils.defaultString(ExcelUtil.getCellValue(row.getCell(0)),"");
				if(StringUtils.isBlank(phone)){
					continue;
				}
				// 网点编码
				String dealerCode = StringUtils.defaultString(ExcelUtil.getCellValue(row.getCell(1)),"");
				// 意向级别
				String intent = StringUtils.defaultString(ExcelUtil.getCellValue(row.getCell(2)),"");
				// 跟进状态
				String follow = StringUtils.defaultString(ExcelUtil.getCellValue(row.getCell(3)),"");
				
				// 根据电话和网点编号查询合并后留资
				LeadsMerged leadsMerged = getByPhoneAndDealerCode(phone,dealerCode);
				if(null == leadsMerged){
					continue;
				}
				Constant intentConstant = constantService.getByNameAndCategory(intent,SysConstant.CONSTANT_CATEGORY_LEADS_INTENT);
				if(null != intentConstant){
					leadsMerged.setLedpIntent(intentConstant);
				}
				Constant followConstant = constantService.getByNameAndCategory(follow,SysConstant.CONSTANT_CATEGORY_LESDS_STATE);
				if(null != intentConstant){
					leadsMerged.setLedpFollow(followConstant);
				}
				update(leadsMerged);
			}
		} catch (Exception e) {
			String message = "导入合并后留资时发生异常:"+e.getMessage();
			logger.error(message);
			throw new RuntimeException(message);
		}finally{
			IOUtils.closeQuietly(is);
			FileUtils.deleteQuietly(file);	//删除临时文件
		}
	}
	
	public List<Map> executeQuery(LeadsReportForm form) throws LedpException{
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT o1.id,o1.name,o1.phone,o1.ledp_media,o2.name mediaName,");
		sql.append("o1.ledp_dealer,o5.name dealerName,o1.createTime,");
		sql.append("o1.ledp_intent,o1.ledp_follow,o9.name orgName,o3.name ledpType,o4.name ledpFollow,o8.name ledpIntent");
		sql.append(" FROM t_leads_merged o1");
		sql.append(" LEFT JOIN t_media o2 ON o2.id = o1.ledp_media");		// 媒体渠道
		sql.append(" LEFT JOIN t_constant o3 ON o3.id = o1.ledp_type");		// 线索类型
		sql.append(" LEFT JOIN t_constant o4 ON o4.id = o1.ledp_follow");	// 跟进状态
		sql.append(" LEFT JOIN t_dealer o5 ON o5.id = o1.ledp_dealer");	// 网点
		sql.append(" LEFT JOIN t_vehicle_series o6 ON o6.id = o1.ledp_series");	// 车系
		sql.append(" LEFT JOIN t_vehicle o7 ON o7.id = o1.ledp_vehicle");	// 车型
		sql.append(" LEFT JOIN t_constant o8 ON o8.id = o1.ledp_intent");	// 意向类别
		sql.append(" LEFT JOIN t_organization o9 ON o9.id = o5.organization");	// 大区
		sql.append(" WHERE 1=1");
		// 获取查询条件
		Map<String,Object> condition = getCondition(form);
		Map namedParams = (Map)condition.get("namedParams");
		sql.append(condition.get("namedSql"));
		if (StringUtils.isNotBlank(form.getSortName())) {
			String sortName="";
			if("ledpMedia.name".equals(form.getSortName())){
				sortName="ledp_media";
			}else if("ledpDealer.name".equals(form.getSortName())){
				sortName="ledp_dealer";
			}else if("ledpIntent.name".equals(form.getSortName())){
				sortName="ledp_intent";
			}else if("ledpFollow.name".equals(form.getSortName())){
				sortName="ledp_follow";
			}else{
				sortName=form.getSortName();
			}
			sql.append (" order by o1." + sortName);
		}
		if (StringUtils.isNotBlank(form.getSortOrder())) {
			sql.append (" " + form.getSortOrder());
		}
		Map paginateParams = new HashMap();
		Integer pageSize = form.getPageSize();
		Integer pageNumber = form.getCurrentPage();
		int offset = (pageNumber - 1) * pageSize;
		paginateParams.put("offset", offset);
		paginateParams.put("max", pageSize);
		return mybaitsGenericDao.executeQuery(sql.toString(),namedParams,paginateParams);
	}
	
	public int getTotalRow(LeadsReportForm form) throws LedpException{
		Map<String,Object> condition = getCondition(form);
		Map namedParams = (Map)condition.get("namedParams");
		
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT COUNT(1) as count");
		sql.append(" FROM t_leads_merged o1");
		sql.append(" LEFT JOIN t_media o2 ON o2.id = o1.ledp_media");		// 媒体渠道
		sql.append(" LEFT JOIN t_constant o3 ON o3.id = o1.ledp_type");		// 线索类型
		sql.append(" LEFT JOIN t_constant o4 ON o4.id = o1.ledp_follow");	// 跟进状态
		sql.append(" LEFT JOIN t_dealer o5 ON o5.id = o1.ledp_dealer");	// 网点
		sql.append(" LEFT JOIN t_vehicle_series o6 ON o6.id = o1.ledp_series");	// 车系
		sql.append(" LEFT JOIN t_vehicle o7 ON o7.id = o1.ledp_vehicle");	// 车型
		sql.append(" LEFT JOIN t_constant o8 ON o8.id = o1.ledp_intent");	// 意向类别
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
		
		// 媒体渠道
		if(form.getLedpMediaId() != null){
			namedParams.put("ledpMediaId",form.getLedpMediaId());
			namedSql.append(" and o1.ledp_media =:ledpMediaId");
		}
		// 意向类别
		if(form.getLedpIntentId() != null){
			namedParams.put("ledpIntentId",form.getLedpIntentId());
			namedSql.append(" and o1.ledp_intent =:ledpIntentId");
		}
		// 跟进状态
		if(form.getLedpFollowId() != null){
			namedParams.put("ledpFollowId",form.getLedpFollowId());
			namedSql.append(" and o1.ledp_follow =:ledpFollowId");
		}
		// 留资开始时间
		if(form.getBeginDate() != null){
			namedParams.put("beginDate",DateUtil.convert(form.getBeginDate(), dateFormat));
			namedSql.append(" and o1.createTime >=:beginDate");
		}
		// 留资结束时间
		if(form.getEndDate() != null){
			namedParams.put("endDate",DateUtil.convert(form.getEndDate(), dateFormat));
			namedSql.append(" and o1.CreateTime <=:endDate");
		}
		// 手机号码
		if(StringUtils.isNotBlank(form.getPhone())){
			namedParams.put("phone",form.getPhone());
			namedSql.append(" and o1.phone =:phone");
		}
		// 组织ID
		if(form.getOrganizationSelectedId() != null && form.getOrganizationSelectedId() != 1){
			namedParams.put("organization",form.getOrganizationSelectedId());
			namedSql.append(" and o5.id =:organization");
		}
		if(form.getLedpDealer() != null){
			namedParams.put("dealer",form.getLedpDealer());
			namedSql.append(" and o1.ledp_dealer =:dealer");
		}
		// 客户名称
		if(StringUtils.isNotBlank(form.getName())){
			namedParams.put("name",form.getName());
			namedSql.append(" and o1.name =:name");
		}
		
		Map<String,Object> rs = new HashMap<String,Object>();
		rs.put("namedSql",namedSql);
		rs.put("namedParams",namedParams);
		return rs;
	}
}
