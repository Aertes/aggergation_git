package com.citroen.ledp.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.citroen.ledp.dao.mybaits.MybaitsGenericDao;
import com.citroen.ledp.domain.Customer;
import com.citroen.ledp.exception.LedpException;
import com.citroen.ledp.util.ExcelUtil;
import com.citroen.ledp.util.MapUtil;

/**
 * 客户服务类
 * @author      miaoshuai
 * @email       miaoshuai@tocersfot.com
 * @company		www.tocersoft.com
 * @create-time 2015年3月13日 下午1:58:34
 * @version     1.0
 */

public interface CustomerService {
	List<Customer> executeQuery(Map params) throws LedpException;
	
	int getTotalRow(Map params) throws LedpException;
	
	Map<String,Object> getCondition(Map<String,Object> params);
	
	Customer get(long id) throws LedpException;
	
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
	SXSSFWorkbook createWorkbook(List<Customer> customerList);
	
	void update(Customer customer) throws LedpException;
}
