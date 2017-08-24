/**
 * Copyright © 2013-2015 上海扬邑企业管理咨询有限公司.
 * Project Name:ledp-web-core
 * File Name:KpiService.java
 * Package Name:com.citroen.ledp.service
 * Date:2015-3-17下午3:43:26
 * Description: //模块目的、功能描述      
 * History: //修改记录
 */

package com.citroen.ledp.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.citroen.ledp.dao.mybaits.MybaitsGenericDao;
import com.citroen.ledp.domain.Kpi;
import com.citroen.ledp.exception.LedpException;

/**
 * ClassName:KpiService <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2015-3-17 下午3:43:26 <br/>
 * 
 * @author 刘学斌
 * @version V1.0
 */

public interface KpiService {


	List<Kpi> queryKpiList() throws LedpException;

	Kpi get(long id);

	void update(Kpi k) throws LedpException;

}
