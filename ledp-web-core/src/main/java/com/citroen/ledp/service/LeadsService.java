package com.citroen.ledp.service;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.citroen.ledp.dao.mybaits.MybaitsGenericDao;
import com.citroen.ledp.domain.Dealer;
import com.citroen.ledp.domain.Leads;
import com.citroen.ledp.domain.Organization;
import com.citroen.ledp.domain.User;
import com.citroen.ledp.exception.LedpException;
import com.citroen.ledp.util.DateUtil;
import com.citroen.ledp.util.ExcelUtil;
import com.citroen.ledp.util.MapUtil;

/**
 * 留资服务类
 *
 * @author miaoshuai
 * @version 1.0
 * @email miaoshuai@tocersfot.com
 * @company www.tocersoft.com
 * @create-time 2015年3月13日 下午1:58:34
 */

public interface LeadsService {

    List<Leads> executeQuery(Map params) throws LedpException;

    int getTotalRow(Map params) throws LedpException;

    Map<String, Object> getCondition(Map<String, Object> params);
    Leads get(long id) throws LedpException ;

    /**
     * 导出查询
     *
     * @param params
     * @return
     * @throws LedpException
     */
    List<Map> exportQuery(Map params) throws LedpException;

    /**
     * 创建导出Excel文件
     *
     * @return
     */
    SXSSFWorkbook createWorkbook(Map params) throws LedpException;
    /**
     * 创建导出Excel文件
     *
     * @return
     */
    SXSSFWorkbook createWechatWorkbook(List<Map> leadsList) ;

    int getLeadsCountByDate(Long dealerId, Long regionId, Date beginDate, Date endDate) throws LedpException ;
}
