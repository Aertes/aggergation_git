/**
 * 
 */
package com.citroen.wechat.service;


import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.citroen.ledp.dao.mybaits.MybaitsGenericDao;
import com.citroen.ledp.exception.LedpException;
import com.citroen.wechat.domain.AutoReply;

/**
 * Created by vpc on 2015/6/19.
 */
@Service
@Transactional
public class AutoReplyService {
	private static Logger log = Logger.getLogger(AutoReplyService.class);
	@Autowired
	private MybaitsGenericDao<Long> mybaitsGenericDao;
	
	public AutoReply get(Long id) {
		try {
			return mybaitsGenericDao.get(AutoReply.class, id);
		} catch (LedpException e) {
			log.error(e.getMessage());
		}
		return null;
	}

	public boolean delete(Long id) {
		try {
			mybaitsGenericDao.delete(AutoReply.class, id);
			return true;
		} catch (LedpException e) {
			log.error(e.getMessage());
		}
		
		return false;
	}
	
	public Long countByMaterialId(long materialId) throws Exception{
		Long count = 0l;
		StringBuilder sql = new StringBuilder();
		sql.append("select count(id) as ct from t_autoreply where material_id="+materialId);
		Map map = mybaitsGenericDao.find(sql.toString());
		if(map != null){
			try {
				count = (Long)map.get("ct");
			} catch (Exception e) {}
			return count;
		}
		return count;
	}
	
	public List<AutoReply> list(Map<String, Object> params) {
		return null;
	}

    /**
     * 根据公众号ID获取自动回复信息
     * @param l 公众号ID
     * @return
     * @throws LedpException 
     */
	public List<AutoReply> getAutoReplyByPublicNo(String type,long l) throws LedpException {
		
		if("subscribe".equals(type)){
	       List<AutoReply> autoReplies = mybaitsGenericDao.executeQuery(AutoReply.class,"select * from t_autoreply where MsgType in(0,1) and publicno=" + l+" order by MsgType asc limit 0,2");
	        if(autoReplies.isEmpty()) {
	        	AutoReply reply0 = new AutoReply();
	        	reply0.setMsgType("news");
	        	reply0.setStatus(1);
	        	reply0.setContent("");//借用第一个content字段作为是否开启自动回复,checked/""
	        	autoReplies.add(reply0);
	        	AutoReply reply1 = new AutoReply();
	        	reply1.setMsgType("1");
	        	reply1.setStatus(0);
	        	autoReplies.add(reply1);
	        }
	        return autoReplies;
		}
        List<AutoReply> autoReplies = mybaitsGenericDao.executeQuery(AutoReply.class,"select * from t_autoreply where MsgType in(2,3) and publicno=" + l+" order by MsgType asc limit 0,2");
        if(autoReplies.isEmpty()) {
        	AutoReply reply2 = new AutoReply();
        	reply2.setMsgType("2");
        	reply2.setStatus(1);
        	autoReplies.add(reply2);
        	AutoReply reply3 = new AutoReply();
        	reply3.setMsgType("3");
        	reply3.setStatus(0);
        	autoReplies.add(reply3);
        }
        return autoReplies;
    }
	
	
	public AutoReply find(String type,long publicno) throws LedpException{
		return mybaitsGenericDao.find(AutoReply.class,"select * from t_autoreply where type='"+type+"' and publicno=" + publicno);
	}
	
	public void saveOrUpdate(AutoReply autoReply, long publicNo) throws LedpException {
		AutoReply oldAutoReply = find(autoReply.getType(),publicNo);
		if(oldAutoReply!=null){
			oldAutoReply.setMaterialId(autoReply.getMaterialId());
			oldAutoReply.setContent(autoReply.getContent());
			oldAutoReply.setMsgType(autoReply.getMsgType());
			oldAutoReply.setStatus(autoReply.getStatus());
			oldAutoReply.setUserUpdate(autoReply.getUserUpdate());
			oldAutoReply.setDateUpdate(new Date());
            mybaitsGenericDao.update(oldAutoReply);
		}else {
    		autoReply.setPublicno(publicNo);
            long id = mybaitsGenericDao.save(autoReply);
            autoReply.setId(id);
        }
	}
	public void saveOrUpdate(List<AutoReply> autoReplies, long publicNo) throws LedpException {
		for(AutoReply reply:autoReplies){
			reply.setPublicno(publicNo);
            if(null != reply.getId() ) {
                mybaitsGenericDao.update(reply);
            }else {
                long id = mybaitsGenericDao.save(reply);
                reply.setId(id);
            }
		}
	}
}
