/**
 * Copyright © 2013-2015 上海扬邑企业管理咨询有限公司.
 * Project Name:ledp-web-core
 * File Name:KpiService.java
 * Package Name:com.citroen.ledp.service
 * Date:2015-3-17下午3:43:26
 * Description: //模块目的、功能描述      
 * History: //修改记录
 */

package com.citroen.ledp.service.impl;

import com.citroen.ledp.dao.mybaits.MybaitsGenericDao;
import com.citroen.ledp.domain.Kpi;
import com.citroen.ledp.exception.LedpException;
import com.citroen.ledp.service.KpiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ClassName:KpiService <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2015-3-17 下午3:43:26 <br/>
 * 
 * @author 刘学斌
 * @version V1.0
 */
@Service
public class KpiServiceImpl implements KpiService {

	@Autowired
	private MybaitsGenericDao<Long> genericDao;

	public List<Kpi> queryKpiList() throws LedpException {
		return genericDao.executeQuery(Kpi.class, " select * from t_kpi ");
	}

	public Kpi get(long id) {
		try {
			return genericDao.get(Kpi.class, id);
		} catch (LedpException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void update(Kpi k) throws LedpException {
		genericDao.update(k);
	}

}
