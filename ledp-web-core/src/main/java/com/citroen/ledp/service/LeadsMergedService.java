package com.citroen.ledp.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

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

import com.citroen.ledp.dao.mybaits.MybaitsGenericDao;
import com.citroen.ledp.domain.Constant;
import com.citroen.ledp.domain.Dealer;
import com.citroen.ledp.domain.LeadsMerged;
import com.citroen.ledp.domain.Organization;
import com.citroen.ledp.domain.User;
import com.citroen.ledp.exception.LedpException;
import com.citroen.ledp.util.ExcelUtil;
import com.citroen.ledp.util.MapUtil;
import com.citroen.ledp.util.SysConstant;
import com.citroen.wechat.api.util.DateUtil;
import com.citroen.wechat.form.LeadsReportForm;

/**
 * 合并后留资服务类
 * @author      miaoshuai
 * @email       miaoshuai@tocersfot.com
 * @company		www.tocersoft.com
 * @create-time 2015年3月18日 上午10:43:31
 * @version     1.0
 */

public interface LeadsMergedService {

	List<LeadsMerged> executeQuery(Map params) throws LedpException;
	int getTotalRow(Map params) throws LedpException;
	
	Map<String,Object> getCondition(Map<String,Object> params);
	
	void update(LeadsMerged leadsMerged) throws LedpException;
	
	LeadsMerged get(long id) throws LedpException;
	
	/**
	 * 根据电话号码和网点编码查询
	 * @param phone
	 * @param dealerCode
	 * @return
	 * @throws LedpException 
	 */
	LeadsMerged getByPhoneAndDealerCode(String phone,String dealerCode) throws LedpException;

    @Transactional
	void importLeadsMerged(String filePath) ;
	
	List<Map> executeQuery(LeadsReportForm form) throws LedpException;
	
	int getTotalRow(LeadsReportForm form) throws LedpException;
	
	Map<String,Object> getCondition(LeadsReportForm form);
}
