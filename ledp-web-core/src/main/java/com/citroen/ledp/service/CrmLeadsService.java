package com.citroen.ledp.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.citroen.ledp.dao.mybaits.MybaitsGenericDao;
import com.citroen.ledp.domain.ApiLog;
import com.citroen.ledp.domain.Constant;
import com.citroen.ledp.domain.CrmLeads;
import com.citroen.ledp.domain.Customer;
import com.citroen.ledp.domain.Dealer;
import com.citroen.ledp.domain.Organization;
import com.citroen.ledp.domain.User;
import com.citroen.ledp.exception.LedpException;
import com.citroen.ledp.util.ExcelUtil;
import com.citroen.ledp.util.MapUtil;
import com.citroen.wechat.api.util.DateUtil;
import com.citroen.wechat.form.LeadsReportForm;

/**
 * 客户服务类
 * @author      miaoshuai
 * @email       miaoshuai@tocersfot.com
 * @company		www.tocersoft.com
 * @create-time 2015年3月13日 下午1:58:34
 * @version     1.0
 */

public interface CrmLeadsService {

	
	List<CrmLeads> executeQuery(Map params) throws LedpException;
	
	int getTotalRow(Map params) throws LedpException;
	
	Map<String,Object> getCondition(Map<String,Object> params);
	
	CrmLeads get(long id) throws LedpException;
	Customer getByPhone(String phone) throws LedpException;
	
	/**
	 * 导出查询
	 * @param params
	 * @return
	 * @throws LedpException
	 */
	List<Customer> exportQuery(Map params) throws LedpException;
	
	/**
	 * 创建导出Excel文件
	 * @return
	 */
	HSSFWorkbook createWorkbook(List<Customer> customerList);
	
	void update(CrmLeads lead) throws LedpException;
	
	public void saves(CrmLeads lead) throws LedpException;
	/**
	 * 数据合并
	 */
    @Transactional
    void merges(Date date);
	List<Dealer> queryByCondition(String code);
	Constant getConstantByCode(String code) ;
	/**
	 * @Title: getApiLogTotal
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param params
	 * @return
	 * @return int
	 * @throws LedpException 
	 */
	int getApiLogTotal(Map<String, Object> params) throws LedpException;
	/**
	 * @Title: executeApiLogQuery
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param params
	 * @return
	 * @return List<ApiLog>
	 * @throws LedpException 
	 */
	List<ApiLog> executeApiLogQuery(Map<String, Object> params) throws LedpException;
	String getApiLogCondition(Map<String,Object> params);
	
	List<CrmLeads> executeQuery(LeadsReportForm form) throws LedpException;
	
	int getTotalRow(LeadsReportForm form) throws LedpException;

	Map<String,Object> getCondition(LeadsReportForm form);
	
}