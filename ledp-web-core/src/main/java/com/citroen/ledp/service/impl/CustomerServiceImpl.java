package com.citroen.ledp.service.impl;

import com.citroen.ledp.dao.mybaits.MybaitsGenericDao;
import com.citroen.ledp.domain.Customer;
import com.citroen.ledp.exception.LedpException;
import com.citroen.ledp.service.ConstantService;
import com.citroen.ledp.service.CustomerService;
import com.citroen.ledp.util.ExcelUtil;
import com.citroen.ledp.util.MapUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
public class CustomerServiceImpl implements CustomerService{
	
	@Autowired
	private MybaitsGenericDao<Long> mybaitsGenericDao;
	@Autowired
	private ConstantService constantService;
	
	
	public List<Customer> executeQuery(Map params) throws LedpException{
		Map<String,Object> condition = getCondition(params);
		Map namedParams = (Map)condition.get("namedParams");
		
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT * FROM t_customer WHERE 1=1");
		sql.append(condition.get("namedSql"));
		if (params.containsKey("sortName")) {
			if("intent.name".equals(params.get("sortName").toString())){
				sql.append(" order by intent" );
			}else if("follow.name".equals(params.get("sortName").toString())){
				sql.append(" order by follow" );
			}else{
				sql.append(" order by " + params.get("sortName"));
			}
		}
		if (params.containsKey("sortOrder")) {
			sql.append( " " + params.get("sortOrder"));
		}
		Map paginateParams = new HashMap();
		try {
			Integer pageSize = Integer.valueOf(params.get("pageSize").toString());
			Integer pageNumber = Integer.valueOf(params.get("pageNumber").toString());
			int offset = (pageNumber - 1) * pageSize;
			paginateParams.put("offset", offset);
			paginateParams.put("max", pageSize);
		} catch (Exception e1) {
			e1.printStackTrace();
			paginateParams.put("offset", 10);
			paginateParams.put("max", 0);
		}
		return mybaitsGenericDao.executeQuery(Customer.class,sql.toString(),namedParams,paginateParams);
	}
	
	public int getTotalRow(Map params) throws LedpException{
		Map<String,Object> condition = getCondition(params);
		Map namedParams = (Map)condition.get("namedParams");
		
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT COUNT(1) as count");
		sql.append(" FROM t_customer");
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
		// 手机号码
		if(!mapUtil.isBlank("phone")){
			String phone = params.get("phone").toString();
			namedParams.put("phone","%"+phone+"%");
			namedSql.append(" and phone like :phone");
		}
		// 意向类别
		if(!mapUtil.isBlank("intent")){
			Long intent = Long.parseLong(params.get("intent").toString());
			namedParams.put("intent",intent);
			namedSql.append(" and intent =:intent");
		}
		if(!mapUtil.isBlank("follow")){
			Long follow = Long.parseLong(params.get("follow").toString());
			namedParams.put("follow",follow);
			namedSql.append(" and follow =:follow");
		}
		// 电子邮箱
		if(!mapUtil.isBlank("email")){
			String email = params.get("email").toString();
			namedParams.put("email","%"+email+"%");
			namedSql.append(" and email like :email");
		}
		
		// 电子邮箱
		if(!mapUtil.isBlank("ledp_intent")){
			String ledp_intent = params.get("ledp_intent").toString();
			namedParams.put("ledp_intent1","%"+ledp_intent+"%");
			namedSql.append(" and ledp_intent like :ledp_intent1");
		}
		Map<String,Object> rs = new HashMap<String,Object>();
		rs.put("namedSql",namedSql);
		rs.put("namedParams",namedParams);
		return rs;
	}
	
	public Customer get(long id) throws LedpException{
		return mybaitsGenericDao.get(Customer.class, id);
	}
	
	public Customer getByPhone(String phone) throws LedpException{
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT `id`,`name`,`phone`,`email`,");
		sql.append(" `address`,`intent`,`province`,");
		sql.append(" `city`,`district`,`user_create`,");
		sql.append(" `user_update`,`date_create`,`date_update`");
		sql.append(" FROM t_customer");
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
		sql.append(" FROM t_customer");
		sql.append(" WHERE 1=1");
		sql.append(condition.get("namedSql"));
		
		return mybaitsGenericDao.executeQuery(Customer.class,sql.toString(),namedParams);
	}
	
	/**
	 * 创建导出Excel文件
	 * @return
	 */
	public SXSSFWorkbook createWorkbook(List<Customer> customerList){
		SXSSFWorkbook wb = new SXSSFWorkbook();
		Sheet sheet = wb.createSheet("客户信息导出记录");
        String[] titles = new String[]{
        		"客户名称","手机号码","意向级别","手机号码","电子邮箱"
        }; 
        Integer[] titleWidths = new Integer[]{12,12,12,20,30};
        // 列头
        Row row = ExcelUtil.createRow(titles, titleWidths, wb, sheet, 0);
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
	
	public void update(Customer customer) throws LedpException {
		mybaitsGenericDao.update(customer);
	}
}
