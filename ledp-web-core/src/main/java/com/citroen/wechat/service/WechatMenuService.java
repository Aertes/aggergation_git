package com.citroen.wechat.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.citroen.ledp.dao.mybaits.MybaitsGenericDao;
import com.citroen.ledp.exception.LedpException;
import com.citroen.ledp.service.ConstantService;
import com.citroen.wechat.domain.WechatMenu;
import com.citroen.wechat.exception.WechatException;

/**
 * 微信菜单服务
 * @author 何海粟
 * @date2015年6月17日
 */
@Service
public class WechatMenuService {
    private static Logger logger = Logger.getLogger(WechatMenuService.class);
	@Autowired
	private MybaitsGenericDao<Long> mybaitsGenericDao;
	@Autowired
	private ConstantService constantService;
	
	public WechatMenu get(long id) throws Exception{
		return mybaitsGenericDao.get(WechatMenu.class, id);
	}
	
	public List<WechatMenu> getMenus(WechatMenu parent, long publicNoId) throws Exception{
		List<WechatMenu> menus = new ArrayList<WechatMenu>();
		if(parent == null){
			menus = mybaitsGenericDao.executeQuery(WechatMenu.class,"select * from t_wechat_menu m where m.parent is null and publicno="+publicNoId+" order by sort desc,col ");
		}else{
			menus = mybaitsGenericDao.executeQuery(WechatMenu.class,"select * from t_wechat_menu m where m.parent="+parent.getId()+" and publicno="+publicNoId+" order by sort,col");
		}
		
		if(CollectionUtils.isNotEmpty(menus)){
			for(WechatMenu menu : menus){
				menu.setChildren(mybaitsGenericDao.executeQuery(WechatMenu.class,"select * from t_wechat_menu m where m.parent="+menu.getId()+" and publicno="+publicNoId+" order by sort desc"));
			}
			
		}
		return menus;
	}
	
	/**
	 * 获取待发布的菜单
	 * @param parent
	 * @return
	 * @throws Exception
	 */
	public List<WechatMenu> getPublishMenus(WechatMenu parent, long publicNoId) throws Exception{
		List<WechatMenu> menus = new ArrayList<WechatMenu>();
		if(parent == null){
			menus = mybaitsGenericDao.executeQuery(WechatMenu.class,"select * from t_wechat_menu m where m.parent is null and publicno="+publicNoId+"  order by sort,col desc");
		}else{
			menus = mybaitsGenericDao.executeQuery(WechatMenu.class,"select * from t_wechat_menu m where m.parent="+parent.getId()+" and publicno="+publicNoId+" order by sort desc,col");
		}
		
		if(CollectionUtils.isNotEmpty(menus)){
			for(WechatMenu menu : menus){
				menu.setChildren(mybaitsGenericDao.executeQuery(WechatMenu.class,"select * from t_wechat_menu m where m.parent="+menu.getId()+" and publicno="+publicNoId+" order by sort desc"));
			}
			
		}
		return menus;
	}
	
	/**
	 * 获取列数
	 * @param parent
	 * @return
	 * @throws Exception
	 */
	public int countParentByCol(int col, long publicNoId) throws Exception{
		Map result = new HashMap();
		int count = 0;
		result = mybaitsGenericDao.find("select count(m.id) as ct from t_wechat_menu m where m.parent is null and publicno="+publicNoId+"  and col="+col);
		if(result != null){
			try {
				count = Integer.valueOf(result.get("ct").toString());
			} catch (Exception e) {
				logger.error("异常信息：" + e.getMessage());
			}
		}
		return count;
	}
	/**
	 * 获取每列有几个菜单
	 * @param parent
	 * @return
	 * @throws Exception
	 */
	public int countSubMenuByCol(int col, long publicNoId) throws Exception{
		Map result = new HashMap();
		int count = 0;
		result = mybaitsGenericDao.find("select count(m.id) as ct from t_wechat_menu m where m.parent is not null and publicno="+publicNoId+"  and col="+col);
		if(result != null){
			try {
				count = Integer.valueOf(result.get("ct").toString());
			} catch (Exception e) {}
		}
		return count;
	}
	
	public void delete(long id) throws Exception {
		try{
			WechatMenu menu = mybaitsGenericDao.get(WechatMenu.class, id);
			mybaitsGenericDao.delete(WechatMenu.class, id);
			List<WechatMenu> children = getChildren(menu);
			if(CollectionUtils.isNotEmpty(children)){
				for(WechatMenu child : children){
					mybaitsGenericDao.delete(WechatMenu.class, child.getId());
				}
			}
		}catch(Exception e){
            logger.error("删除失败，异常信息：" + e.getMessage());
			throw new WechatException("删除失败");
		}
	}
	
	private List<WechatMenu> getChildren(WechatMenu parent) throws LedpException{
		List<WechatMenu> children = mybaitsGenericDao.executeQuery(WechatMenu.class,"select * from t_wechat_menu m where  m.parent="+parent.getId()+" order by sort");
		for(WechatMenu child : children){
			child.setChildren(getChildren(child));
		}
		return children;
	}

	public long saveOrUpdate(WechatMenu menu) throws Exception {
		long id = 0L;
		//更新
		if(menu != null && menu.getId() != null){
			try{
				WechatMenu _menu = mybaitsGenericDao.get(WechatMenu.class, menu.getId());
				if(_menu == null){
					throw new WechatException("未查到该菜单,请刷新");
				}
				if(StringUtils.isNotBlank(menu.getName())){
					_menu.setName(menu.getName());
				}
				if(StringUtils.isNotBlank(menu.getEvent())){
					_menu.setEvent(menu.getEvent());
				}
				if(StringUtils.isNotBlank(menu.getUrl())){
					_menu.setUrl(menu.getUrl());
				}
				if(menu.getMaterial() != null && menu.getMaterial().getId() != null){
					_menu.setMaterial(menu.getMaterial());
				}
				if(menu.getLevel() > 0){
					_menu.setLevel(menu.getLevel());
				}
				if(menu.getSort() > 0){
					_menu.setSort(menu.getSort());
				}
				if(menu.getPublicNo() != null){
					_menu.setPublicNo(menu.getPublicNo());
				}
				mybaitsGenericDao.update(_menu);
			}catch(Exception e){
                logger.error("异常信息：" + e.getMessage());
				throw new WechatException("保存出错,原因:"+e.getMessage());
			}
		}//保存
		else{
			try {
				if(StringUtils.isBlank(menu.getName())){
					throw new WechatException("保存出错");
				}
				menu.setSn(String.valueOf(System.currentTimeMillis()));
				menu.setStatus(true);
				id = mybaitsGenericDao.save(menu);
				if(CollectionUtils.isNotEmpty(menu.getChildren())){
					for(WechatMenu _menu : menu.getChildren()){
						_menu.setParent(menu);
						mybaitsGenericDao.save(_menu);
					}
				}
			} catch (Exception e) {
                logger.error("异常信息：" + e.getMessage());
				throw new WechatException("保存出错");
			}
			
		}
		
		return id;
		
		
	}
	
	/**
	 * 重设菜单
	 * @throws Exception 
	 */
	public void reset(WechatMenu menu) throws Exception{
		if(menu == null){
			throw new WechatException("id不能为空");
		}
		if(menu != null && menu.getId() != null){
			try{
				WechatMenu _menu = mybaitsGenericDao.get(WechatMenu.class, menu.getId());
				if(_menu == null){
					throw new WechatException("保存出错");
				}
				_menu.setEvent(null);
				_menu.setMaterial(null);
				_menu.setUrl(null);
				if(StringUtils.isNotBlank(menu.getName())){
					_menu.setName(menu.getName());
				}
				if(menu.getLevel() > 0){
					_menu.setLevel(menu.getLevel());
				}
				if(menu.getSort() > 0){
					_menu.setSort(menu.getSort());
				}
				if(menu.getPublicNo() != null){
					_menu.setPublicNo(menu.getPublicNo());
				}
				mybaitsGenericDao.update(_menu);
			}catch(Exception e){
                logger.error("异常信息：" + e.getMessage());
				throw new WechatException("保存出错");
			}
		}
	}
	
	public int getMaxSort(int col,Long publicnoId) throws LedpException{
		String query = "select max(sort) sort from t_wechat_menu where col="+col+" and publicno="+publicnoId;
		Map map = mybaitsGenericDao.find(query);
		if(map == null){
			return 1;
		}
		return (Integer)map.get("sort");
	}
	
	
	public Long countByMaterialId(long materialId) throws Exception{
		Long count = 0l;
		StringBuilder sql = new StringBuilder();
		sql.append("select count(id) as ct from t_wechat_menu where material="+materialId);
		Map map = mybaitsGenericDao.find(sql.toString());
		if(map != null){
			try {
				count = (Long)map.get("ct");
			} catch (Exception e) {
                logger.error("异常信息：" + e.getMessage());
			}
			return count;
		}
		return count;
	}
	
	
	
	
	
}
