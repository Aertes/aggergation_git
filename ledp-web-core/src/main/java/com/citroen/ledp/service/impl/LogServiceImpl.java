package com.citroen.ledp.service.impl;

import com.citroen.ledp.dao.mybaits.MybaitsGenericDao;
import com.citroen.ledp.domain.Log;
import com.citroen.ledp.domain.User;
import com.citroen.ledp.exception.LedpException;
import com.citroen.ledp.service.LogService;
import org.apache.commons.lang.StringUtils;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LogServiceImpl implements LogService{
	@Autowired
	private MybaitsGenericDao<Long>  mybaitsGenericDao;
	

	private static Logger logger = Logger.getLogger(Log.class);
	
	/**
	 * 
	 * queryLog:(根据条件进行分页查询列表). <br/>
	 * 
	 * @param params
	 * @param pageSize
	 * @param pageNumber
	 * @return
	 */
	public List<Log> queryLog(Log log, int pageSize, int pageNumber,String name,String date1,String date2,String sortName,String sortOrder) {

		List<Log> result = null;
		Map<String, Object> paginate = new HashMap<String, Object>();

		StringBuffer sql = new StringBuffer();
		sql.append("select case type when 'info' then '信息' when 'warn' then '警告' when 'error' then '错误' else '其他' end as type, operator,resource, case operation when 'create' then '增加' when 'update' then '修改' when 'delete' then '删除' else '其他' end as operation,datetime,case result when 'success' then '成功' when 'failure' then '失败' else '其他' end as result,comment from t_log where 1=1 ");
		
		paginate.put("max", pageSize);
		paginate.put("offset",pageNumber);
		
		try {
			Map<String, Object> condition = getCondition(log, sql,name,date1,date2);
			Map namedParams = (Map)condition.get("namedParams");
			
			String sql1=condition.get("namedSql").toString();
			if(!sortName.equals("")){
				if(sortName.equals("operator.name")){
					sql1+=" order by operator ";
				}else{
					sql1+=" order by "+sortName;
				}
				if(!sortOrder.equals("")){
					sql1+=" "+sortOrder;
				}
			}else{
				sql1+=" order by datetime desc";
			}
			result = mybaitsGenericDao.executeQuery(Log.class, sql1.toString(), namedParams, paginate);
			
		} catch (LedpException e) {
			logger.error("根据分页查询日志列表出错" + e.getMessage());
		}
		return result;
	}
	
	
	@SuppressWarnings("unchecked")   //  有疑问
	public Integer getTotal(Log log,String name,String date1,String date2) {

		StringBuffer sql = new StringBuffer();
		
		sql.append("select count(id) from t_log where 1=1 ");
		
		Map<String, Object> rs = null;
		try {
			Map<String, Object> condition = getCondition(log, sql,name,date1,date2);
			Map namedParams = (Map)condition.get("namedParams");
			rs = mybaitsGenericDao.executeQuery(condition.get("namedSql").toString(), namedParams).get(0);

		} catch (LedpException e) {
			logger.error("获取日志总量出错" + e.getMessage());
		}
		return rs.isEmpty() ? 0 : Integer.valueOf(rs.get("count(id)").toString());
	}
	
	
	
	/**
	 * 
	 * getCondition:(查询条件). <br/>
	 * 
	 * @param params
	 * @return
	 */
	public Map<String, Object> getCondition(Log log, StringBuffer sql,String name,String date1,String date2) throws LedpException {
		Map<String, Object> condition = new HashMap<String, Object>();

		if (log != null) {
			//操作对象(名称)
			if (StringUtils.isNotBlank(log.getResource())) {
				condition.put("resource", "%" + log.getResource() + "%");
				sql.append(" and resource like :resource");
			}
			//日志类型
			if (StringUtils.isNotBlank(log.getType())) {
				condition.put("type", "%" + log.getType() + "%");
				sql.append(" and type like :type");
			}
			//操作类型
			if (StringUtils.isNotBlank(log.getOperation())) {
				condition.put("operation", "%" + log.getOperation() + "%");
				sql.append(" and operation like :operation");
			}
//			// 操作时间
			if (log.getDatetime()!=null) {
				condition.put("datetime",log.getDatetime() );
				sql.append(" and datetime like :datetime");
			}
			if(StringUtils.isNotBlank(date1) && StringUtils.isNotBlank(date2)){
				condition.put("datetime1", date1);
				condition.put("datetime2", date2);
				sql.append(" and datetime between :datetime1 and :datetime2 ");
			}else if(StringUtils.isNotBlank(date1)){
				condition.put("datetime1", date1);
				sql.append(" and datetime >=:datetime1 ");
			}else if(StringUtils.isNotBlank(date2)){
				condition.put("datetime2", date2);
				sql.append(" and datetime <=:datetime2 ");
			}
			
			//操作结果
			if (StringUtils.isNotBlank(log.getResult())) {
				condition.put("result", "%" + log.getResult() + "%");
				sql.append(" and result like :result");
			}
			
			//操作用户
			if (StringUtils.isNotBlank(name)) {
				String query = "select * from t_user where name='"+ name +"'";
				User user = mybaitsGenericDao.find(User.class, query);
				if(user != null){
					condition.put("operator",user.getId());
					sql.append(" and operator=:operator");
				}
			}
		
		}
		Map<String,Object> rs = new HashMap<String,Object>();
		rs.put("namedSql",sql);
		rs.put("namedParams",condition);
		return rs;
	}


}
