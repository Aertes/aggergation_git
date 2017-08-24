package com.citroen.wechat.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.citroen.ledp.dao.mybaits.MybaitsGenericDao;
import com.citroen.ledp.domain.Leads;
import com.citroen.ledp.domain.Vehicle;
import com.citroen.ledp.domain.VehicleSeries;
import com.citroen.ledp.exception.LedpException;
import com.citroen.wechat.domain.AnswerConfig;
import com.citroen.wechat.domain.AnswerRecord;
import com.citroen.wechat.domain.Awards;
import com.citroen.wechat.domain.Campaign;
import com.citroen.wechat.domain.CampaignLeads;
import com.citroen.wechat.domain.CampaignRecord;
import com.citroen.wechat.domain.CheckPoint;
import com.citroen.wechat.domain.Coupon;
import com.citroen.wechat.domain.CouponConfig;
import com.citroen.wechat.domain.Material;
import com.citroen.wechat.domain.Plugin;
import com.citroen.wechat.domain.PluginBaseConfig;
import com.citroen.wechat.domain.PluginRecord;
import com.citroen.wechat.domain.PluginType;
import com.citroen.wechat.domain.RotaryTableConfig;
import com.citroen.wechat.domain.ShareConfig;
import com.citroen.wechat.domain.SitePage;
import com.citroen.wechat.domain.Topic;
import com.citroen.wechat.domain.TopicOption;
import com.citroen.wechat.domain.WinInfo;
import com.citroen.wechat.exception.WechatException;

/**
 * 插件服务
 * @author 何海粟
 * @date2015年6月23日
 */
@Service
public class PluginService {
	@Autowired
	private MybaitsGenericDao<Long> mybaitsGenericDao;
	@Autowired
	private SitePageService sitePageService;
	
	public List<Plugin> executeQuery(Map params, Map<String,Integer> paginateParams) throws Exception{
		String query = "";
		
		if(params.isEmpty() || StringUtils.isEmpty(params.get("type"))){
			query = "select * from t_plugin p where p.type <> 1 order by sort";
		}else{
			query = "select * from t_plugin p where p.type in ("+params.get("type")+") order by sort";
		}
		List<Plugin> plugins = mybaitsGenericDao.executeQuery(Plugin.class, query,paginateParams);
		
		return plugins;
	}
	
	public int getTotalRow(Map params) throws Exception{
		String query = "";
		if(params.isEmpty() || StringUtils.isEmpty(params.get("type"))){
			query = "select * from t_plugin p where p.type <> 1 order by sort";
		}else{
			query = "select * from t_plugin p where p.type in ("+params.get("type")+") order by sort";
		}
		
		List<Material> list = mybaitsGenericDao.executeQuery(Material.class,query.toString());
		if(list.isEmpty()){
			return 0;
		}
		
		return list.size();
	}
	
	public List<PluginType> getPlugin(String type) throws Exception{
		String query = "";
		if(StringUtils.isEmpty(type)){
			query = "select * from t_plugin_type p order by sort";
		}else{
			query = "select * from t_plugin_type p where p.id="+type+" order by sort";
		}
		List<PluginType> pluginTypes = mybaitsGenericDao.executeQuery(PluginType.class, query);
		if(CollectionUtils.isNotEmpty(pluginTypes)){
			for(PluginType pluginType : pluginTypes){
				String sql = "select * from t_plugin p where p.type="+pluginType.getId();
				pluginType.setPlugins(mybaitsGenericDao.executeQuery(Plugin.class, sql));
			}
		}
		return pluginTypes;
	}
	
	/**
	 * 获取插件
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public Plugin get(long id) throws Exception{
		return mybaitsGenericDao.get(Plugin.class, id);
	}
	
	/**
	 * 获取插件
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public Plugin getByCode(String code) throws Exception{
		if(org.apache.commons.lang.StringUtils.isNotBlank(code)){
			return mybaitsGenericDao.find(Plugin.class, "select * from t_plugin t where t.code='"+code+"'");
		}
		return null;
		
	}
	
	/**
	 * 保存插件使用记录
	 * @param entity
	 * @return
	 * @throws Exception
	 */
	public Long savePluginRecord(PluginRecord entity) throws Exception{
		return mybaitsGenericDao.save(entity);
	}
	
	/**
	 * 删除插件使用记录
	 * @param entity
	 * @return
	 * @throws Exception
	 */
	public void deletePluginRecord(Long sitePageId) throws Exception{
		if(sitePageId == null){
			throw new WechatException("页面id不能为空");
		}
		String sql = "delete from t_plugin_record where page="+sitePageId;
		mybaitsGenericDao.execute(sql);
	}
	
	
	
	/*****************************************查询***********************************************/

	public List<PluginBaseConfig> getBaseConfig(Map<String, Object> params) throws Exception {
		StringBuffer sql = new StringBuffer();
		sql.append("select * from t_plugin_basecfg t where 1=1 ");
		if(!params.isEmpty()){
			if(params.get("pluginId") != null || params.get("pageId") != null || params.get("pluginUnion") != null){
				if(params.get("pluginId") != null){
					sql.append("and pluginid=:pluginId ");
				}
				if(params.get("pageId") != null){
				sql.append("and pageid=:pageId ");
				}
				if(params.get("pluginUnion") != null){
					sql.append("and plugin_union=:pluginUnion ");
				}
				if(params.get("name") != null){
					sql.append("and name=:name ");
				}
			}else if(params.get("code") != null){
				sql.append("and code=:code ");
			}else{
				return null;
			}
			List<PluginBaseConfig> configs = mybaitsGenericDao.executeQuery(PluginBaseConfig.class, sql.toString(), params);
			if(CollectionUtils.isNotEmpty(configs)){
				for(PluginBaseConfig config : configs){
					List<PluginBaseConfig> children = mybaitsGenericDao.executeQuery(PluginBaseConfig.class, "select * from t_plugin_basecfg t where t.parent="+config.getId());
					config.setParams(children);
				}
			}
			return configs;
		}
		return null;
	}
	
	/**
	 * 
	 * @param code
	 * @return
	 * @throws LedpException 
	 */
	public List<Map> getBaseConfig(String code) throws LedpException{
		if(StringUtils.isEmpty(code)){
			return null;
		}
		String sql = "select id,name,type,val as value from t_plugin_basecfg t where code='"+code+"'";
		return mybaitsGenericDao.executeQuery(sql);
	}
	
	/**
	 * 通过参数获取配置值
	 * @param params
	 * @return
	 * @throws Exception
	 */
	/*public String getVal(Map<String, Object> params) throws Exception{
		StringBuffer sql = new StringBuffer();
		sql.append("select c.val as val from t_plugin_basecfg p");
		sql.append(" left join t_plugin_basecfg c on c.parent = p.id and c.name='"+params.get("name")+"'");
		sql.append(" where 1=1 ");
		if(!params.isEmpty()){
			if(params.get("pluginId") != null || params.get("pageId") != null || params.get("pluginUnion") != null){
				if(params.get("pluginId") != null){
					sql.append(" and p.pluginid="+params.get("pluginId"));
				}
				if(params.get("pageId") != null){
					sql.append(" and p.pageid="+params.get("pageId"));
				}
				if(params.get("pluginUnion") != null){
					sql.append(" and p.plugin_union='"+params.get("pluginUnion")+"'");
				}
			}else{
				return "";
			}
			Map map = mybaitsGenericDao.find(sql.toString());
			if(map != null && map.size() > 0){
				try {
					return map.get("val").toString();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return "";
	}*/
	/**
	 * 通过参数获取配置值
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public String getVal(Map<String, Object> params) throws Exception{
		StringBuffer sql = new StringBuffer();
		sql.append("select p.val as val from t_plugin_basecfg p");
		sql.append(" where p.name='"+params.get("name")+"'");
		if(!params.isEmpty()){
			if(params.get("pluginId") != null || params.get("pageId") != null || params.get("pluginUnion") != null){
				if(params.get("pluginId") != null){
					sql.append(" and p.pluginid="+params.get("pluginId"));
				}
				if(params.get("pageId") != null){
					sql.append(" and p.pageid="+params.get("pageId"));
				}
				if(params.get("pluginUnion") != null){
					sql.append(" and p.plugin_union='"+params.get("pluginUnion")+"'");
				}
			}else{
				return "";
			}
			Map map = mybaitsGenericDao.find(sql.toString());
			if(map != null && map.size() > 0){
				try {
					return map.get("val").toString();
				} catch (Exception e) {	}
			}
		}
		return "";
	}
	
	//获取分享有礼渠道
	public List<String> getShareMedia(Long pageId) throws Exception {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("pluginId", 10);
		params.put("pageId", pageId);
		params.put("name", "sharePintai");
		String channels = this.getVal(params);
		//如果有多个就取第一个
		if(channels != null && !"".equals(channels)){
			channels = channels.substring(2, channels.length()-2);
			String[] arr = channels.split("\",\"");
			return Arrays.asList(arr);
		}
		return null;
	}
	
	
	
	/**
	 * 获取大转盘配置参数
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public RotaryTableConfig getRotaryTableConfig(
			Map<String, Object> params) throws Exception {
		StringBuffer sql = new StringBuffer();
		sql.append("select * from t_plugin_rotarytable t where 1=1 ");
		if(!params.isEmpty()){
			if(params.get("pluginId") != null || params.get("pageId") != null || params.get("pluginUnion") != null){
				if(params.get("pluginId") != null){
					sql.append("and pluginid=:pluginId ");
				}
				if(params.get("pageId") != null){
				sql.append("and pageid=:pageId ");
				}
				if(params.get("pluginUnion") != null){
					sql.append("and plugin_union=:pluginUnion ");
				}
			}else if(params.get("code") != null){
				sql.append("and code=:code ");
			}else{
				return null;
			}
			List<RotaryTableConfig> configs = mybaitsGenericDao.executeQuery(RotaryTableConfig.class, sql.toString(), params);
			if(CollectionUtils.isNotEmpty(configs)){
				RotaryTableConfig config = configs.get(0);
				String query = "select * from t_plugin_awards t where t.pluginid="+params.get("pluginId")+" and t.configid="+config.getId()+" order by level";
				List<Awards> items = mybaitsGenericDao.executeQuery(Awards.class, query);
				config.setItems(items);
				return config;
			}
		}
		return null;
	}
	
	/**
	 * 获取分享有礼配置参数
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public ShareConfig getShareConfig(
			Map<String, Object> params) throws Exception {
		StringBuffer sql = new StringBuffer();
		sql.append("select * from t_plugin_share t where 1=1 ");
		if(!params.isEmpty()){
			if(params.get("pluginId") != null || params.get("pageId") != null || params.get("pluginUnion") != null){
				if(params.get("pluginId") != null){
					sql.append("and pluginid=:pluginId ");
				}
				if(params.get("pageId") != null){
				sql.append("and pageid=:pageId ");
				}
				if(params.get("pluginUnion") != null){
					sql.append("and plugin_union=:pluginUnion ");
				}
			}else if(params.get("code") != null){
				sql.append("and code=:code ");
			}else{
				return null;
			}
			List<ShareConfig> configs = mybaitsGenericDao.executeQuery(ShareConfig.class, sql.toString(), params);
			if(CollectionUtils.isNotEmpty(configs)){
				ShareConfig config = configs.get(0);
				String query = "select * from t_plugin_awards t where t.pluginid="+params.get("pluginId")+" and t.configid="+config.getId();
				List<Awards> items = mybaitsGenericDao.executeQuery(Awards.class, query);
				config.setItems(items);
				return config;
			}
		}
		return null;
	}
	
	/**
	 * 获取答题闯关配置参数
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public AnswerConfig getAnswerConfig(
			Map<String, Object> params) throws Exception {
		StringBuffer sql = new StringBuffer();
		sql.append("select * from t_plugin_answer t where 1=1 ");
		if(!params.isEmpty()){
			if(params.get("pluginId") != null || params.get("pageId") != null || params.get("pluginUnion") != null){
				if(params.get("pluginId") != null){
					sql.append("and pluginid=:pluginId ");
				}
				if(params.get("pageId") != null){
				sql.append("and pageid=:pageId ");
				}
				if(params.get("pluginUnion") != null){
					sql.append("and plugin_union=:pluginUnion ");
				}
			}else if(params.get("code") != null){
				sql.append("and code=:code ");
			}else{
				return null;
			}
			List<AnswerConfig> configs = mybaitsGenericDao.executeQuery(AnswerConfig.class, sql.toString(), params);
			if(CollectionUtils.isNotEmpty(configs)){
				AnswerConfig config = configs.get(0);
				String query = "select * from t_plugin_checkpoint t where t.answer="+config.getId();
				List<CheckPoint> checkPoints = mybaitsGenericDao.executeQuery(CheckPoint.class, query);
				if(CollectionUtils.isNotEmpty(checkPoints)){
					for(CheckPoint chickPoint : checkPoints){
						String queryTopic = "select * from t_plugin_topic t where t.checkpoint="+chickPoint.getId();
						List<Topic> topics = mybaitsGenericDao.executeQuery(Topic.class, queryTopic);
						if(CollectionUtils.isNotEmpty(topics)){
							for(Topic topic : topics){
								//删除答案
								topic.setRightOption("");
								String queryOption = "select * from t_plugin_topicoption t where t.topic="+topic.getId();
								List<TopicOption> options = mybaitsGenericDao.executeQuery(TopicOption.class, queryOption);
								topic.setOption(options);
							}
							chickPoint.setTopics(topics);
						}
					}
					config.setCheckPoint(checkPoints);
				}
				String queryAwards = "select * from t_plugin_awards t where t.pluginid="+config.getPluginId()+" and t.configid="+config.getId();
				List<Awards> prizeOne = mybaitsGenericDao.executeQuery(Awards.class, queryAwards);
				config.setPrizeOne(prizeOne);
				return config;
				
			}
		}
		return null;
	}
	
	/**
	 * 获取优惠券配置
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public CouponConfig getCouponConfig(Map<String, Object> params) throws Exception {
		StringBuffer sql = new StringBuffer();
		sql.append("select * from t_plugin_coupon t where 1=1 ");
		if(!params.isEmpty()){
			if(params.get("pluginId") != null || params.get("pageId") != null || params.get("pluginUnion") != null){
				if(params.get("pluginId") != null){
					sql.append("and pluginid=:pluginId ");
				}
				if(params.get("pageId") != null){
				sql.append("and pageid=:pageId ");
				}
				if(params.get("pluginUnion") != null){
					sql.append("and plugin_union=:pluginUnion ");
				}
			}else if(params.get("code") != null){
				sql.append("and code=:code ");
			}else{
				return null;
			}
			List<CouponConfig> configs = mybaitsGenericDao.executeQuery(CouponConfig.class, sql.toString(), params);
			if(CollectionUtils.isNotEmpty(configs)){
				CouponConfig config = configs.get(0);
				List<Coupon> coupons = mybaitsGenericDao.executeQuery(Coupon.class, "select * from t_coupon t where t.parent="+config.getId());
				config.setItems(coupons);
				return config;
			}
		}
		return null;
	}
	
	public Coupon getCoupon(Map<String, Object> params) throws LedpException{
		StringBuffer sql = new StringBuffer();
		sql.append("select * from t_coupon t where 1=1 ");
		if(!params.isEmpty()){
			if(params.get("id") != null){
				sql.append(" and id=:id ");
			}
			if(params.get("sn") != null){
				sql.append(" and sn=:sn ");
			}
		}
		List<Coupon> list = mybaitsGenericDao.executeQuery(Coupon.class, sql.toString(),params);
		if(CollectionUtils.isNotEmpty(list)){
			return list.get(0);
		}else{
			return null;
		}
	}
	
	/**
	 * 是否答对
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public Boolean isAnswerSuccess(
			Map<String, Object> params) throws Exception {
		if(!params.isEmpty()){
			if(params.get("questionId") != null){
				long id = (Long) params.get("questionId");
				Topic topic = mybaitsGenericDao.get(Topic.class, id);
				if(topic != null && (topic.getRightOption()).equalsIgnoreCase((String) params.get("optionKey"))){
					return true;
				}
			}
				
		}
		return false;
	}
	
	/**
	 * 获取关卡
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public CheckPoint getCheckPoint(Long id) throws Exception {
		CheckPoint checkpoint = mybaitsGenericDao.get(CheckPoint.class, id);
		String sql = "select * from t_plugin_topic t where t.checkpoint="+id;
		List<Topic> list = mybaitsGenericDao.executeQuery(Topic.class, sql);
		checkpoint.setTopics(list);
		return checkpoint;
	}
	
	/**
	 * 根据奖品获取关卡
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public CheckPoint getCheckPointByAwards(Long awardsId) throws Exception {
		String sql = "select * from t_plugin_awards pa where pa.id ="+awardsId;
		Awards awards = mybaitsGenericDao.find(Awards.class, sql);
		if(awards != null){
			return awards.getCheckPoint();
		}
		return null;
	}
	
	public int getRightAnswer(Long levelId) throws LedpException{
		String sql = "select * from t_campaign_record cr where cr.level="+levelId;
		List<CampaignRecord> list = mybaitsGenericDao.executeQuery(CampaignRecord.class, sql);
		if(CollectionUtils.isEmpty(list)){
			return 0;
		}
		return list.size();
	}
	
	
	/*********************************************保存**********************************************/
	
	/**
	 * 保存基本配置参数
	 * @param entity
	 * @throws Exception
	 */
	@Transactional
	public void saveBaseConfig(PluginBaseConfig entity) throws Exception{
		Map<String, Object> params = new HashMap<String,Object>();
		params.put("pluginId", entity.getPluginId());
		params.put("pageId", entity.getPageId());
		params.put("pluginUnion", entity.getPluginUnion());
		long count = getTotalCount(params);
		if(count>0){
			throw new WechatException("已有人员参与,不能修改");
		}
		deleteBaseConfig(params);
		long id = mybaitsGenericDao.save(entity);
		entity.setId(id);
		if(CollectionUtils.isNotEmpty(entity.getParams())){
			for(PluginBaseConfig config : entity.getParams()){
				config.setParent(entity);
				config.setPageId(entity.getPageId());
				config.setPluginId(entity.getPluginId());
				config.setPluginUnion(entity.getPluginUnion());
				mybaitsGenericDao.save(config);
			}
		}
	}
	
	/**
	 * 保存大转盘配置参数
	 * @param config
	 * @throws Exception
	 */
	@Transactional
	public void saveRotaryTable(RotaryTableConfig config) throws Exception {
		Map<String, Object> params = new HashMap<String,Object>();
		params.put("pluginId", config.getPluginId());
		params.put("pageId", config.getPageId());
		params.put("pluginUnion", config.getPluginUnion());
		Campaign campaign = null;
		SitePage page = sitePageService.siteInstance(config.getPageId());
		if(page != null && page.getSite() != null && page.getSite().getCampaign() != null){
			campaign = page.getSite().getCampaign();
		}
		long count = getTotalCount(params);
		if(count>0){
			throw new WechatException("已有人员参与,不能修改");
		}
		//先删除
		deleteRotaryTable(params);
		//再保存
		long id = mybaitsGenericDao.save(config);
		if(CollectionUtils.isNotEmpty(config.getItems())){
			for(Awards awards : config.getItems()){
				awards.setPluginId(config.getPluginId());
				awards.setConfigId(id);
				awards.setCampaign(campaign);
				mybaitsGenericDao.save(awards);
			}
		}
	}
	
	/**
	 * 保存分享有礼配置参数
	 * @param share
	 * @throws Exception 
	 */
	@Transactional
	public void saveShare(ShareConfig share) throws Exception {
		//首先查询是否已存在
		Map<String, Object> params = new HashMap<String,Object>();
		params.put("pluginId", share.getPluginId());
		params.put("pageId", share.getPageId());
		params.put("pluginUnion", share.getPluginUnion());
		
		Campaign campaign = null;
		SitePage page = sitePageService.siteInstance(share.getPageId());
		if(page != null && page.getSite() != null && page.getSite().getCampaign() != null){
			campaign = page.getSite().getCampaign();
		}
		long count = getTotalCount(params);
		if(count>0){
			throw new WechatException("已有人员参与,不能修改");
		}
		//先删除
		deleteShare(params);
		//保存
		long id = mybaitsGenericDao.save(share);
		if(CollectionUtils.isNotEmpty(share.getItems())){
			for(Awards awards : share.getItems()){
				awards.setPluginId(share.getPluginId());
				awards.setConfigId(id);
				awards.setCampaign(campaign);
				mybaitsGenericDao.save(awards);
			}
		}
		
	}
	
	/**
	 * 保存答题闯关配置
	 * @param answer
	 */
	@Transactional
	public void saveAnswer(AnswerConfig answer) throws Exception{
		//查询如果存在则更新
		Map<String, Object> params = new HashMap<String,Object>();
		params.put("pluginId", answer.getPluginId());
		params.put("pageId", answer.getPageId());
		params.put("pluginUnion", answer.getPluginUnion());
		
		Campaign campaign = null;
		Awards prize = answer.getPrize();
		SitePage page = sitePageService.siteInstance(answer.getPageId());
		if(page != null && page.getSite() != null && page.getSite().getCampaign() != null){
			campaign = page.getSite().getCampaign();
		}
		long count = getTotalCount(params);
		if(count>0){
			throw new WechatException("已有人员参与,不能修改");
		}
		//先删除
		deleteAnswer(params);
		
		//保存通关奖品
		if(prize != null){
			long prizeId = mybaitsGenericDao.save(prize);
			prize.setId(prizeId);
			answer.setPrize(prize);
		}
		//再保存
		long id = mybaitsGenericDao.save(answer);
		answer.setId(id);
		//保存关卡
		if(CollectionUtils.isNotEmpty(answer.getCheckPoint())){
			for(CheckPoint checkPoint : answer.getCheckPoint()){
				checkPoint.setAnswer(answer);
				long checkId = mybaitsGenericDao.save(checkPoint);
				checkPoint.setId(checkId);
				if(CollectionUtils.isNotEmpty(answer.getPrizeOne())){
					//保存奖项
					for(Awards awards : answer.getPrizeOne()){
						if(awards.getLevel() == checkPoint.getLevel() && awards.getLevel() != 0){
							awards.setCheckPoint(checkPoint);
							awards.setPluginId(answer.getPluginId());
							awards.setConfigId(id);
							awards.setCampaign(campaign);
							mybaitsGenericDao.save(awards);
						}
					}
				}
				
				//保存题目
				if(CollectionUtils.isNotEmpty(checkPoint.getTopics())){
					for(Topic topic : checkPoint.getTopics()){
						topic.setCheckPoint(checkPoint);
						long topicId = mybaitsGenericDao.save(topic);
						topic.setId(topicId);
						//保存选项
						if(CollectionUtils.isNotEmpty(topic.getOption())){
							for(TopicOption option : topic.getOption()){
								option.setTopic(topic);
								mybaitsGenericDao.save(option);
							}
						}
					}
				}
				
			}
		}
		
	}
	
	/**
	 * 保存优惠券配置参数
	 * @param config
	 * @throws Exception
	 */
	@Transactional
	public void saveCoupon(CouponConfig config) throws Exception {
		Map<String, Object> params = new HashMap<String,Object>();
		params.put("pluginId", config.getPluginId());
		params.put("pageId", config.getPageId());
		params.put("pluginUnion", config.getPluginUnion());
		long count = getTotalCount(params);
		if(count>0){
			throw new WechatException("已有人员参与,不能修改");
		}
		//先删除
		deleteCoupon(params);
		//再保存
		long id = mybaitsGenericDao.save(config);
		config.setId(id);
		if(CollectionUtils.isNotEmpty(config.getItems())){
			for(Coupon item : config.getItems()){
				item.setParent(config);
				item.setSn(UUID.randomUUID().toString());
				mybaitsGenericDao.save(item);
			}
		}
	}
	
	/*********************************************删除**********************************************/
	/**
	 * 删除基本配置参数
	 * @param entity
	 * @throws Exception
	 */
	public void deleteBaseConfig(Map<String, Object> params) throws Exception{
		List<PluginBaseConfig> configs = getBaseConfig(params);
		if(CollectionUtils.isNotEmpty(configs)){
			for(PluginBaseConfig config : configs){
				mybaitsGenericDao.delete(PluginBaseConfig.class, config.getId());
				if(CollectionUtils.isNotEmpty(config.getParams())){
					for(PluginBaseConfig child : config.getParams()){
						mybaitsGenericDao.delete(PluginBaseConfig.class, child.getId());
					}
				}
			}
		}
	}
	
	/**
	 * 删除大转盘相关配置参数
	 * @param config
	 * @throws Exception 
	 */
	public void deleteRotaryTable(Map<String, Object> params) throws Exception {
		RotaryTableConfig entity = this.getRotaryTableConfig(params);
		if(entity != null){
			mybaitsGenericDao.delete(RotaryTableConfig.class, entity.getId());
			if(CollectionUtils.isNotEmpty(entity.getItems())){
				for(Awards awards : entity.getItems()){
					mybaitsGenericDao.delete(Awards.class, awards.getId());
				}
			}
		}
	}
	
	/**
	 * 删除分享有礼相关配置参数
	 * @param config
	 * @throws Exception 
	 */
	public void deleteShare(Map<String, Object> params) throws Exception {
		ShareConfig entity = this.getShareConfig(params);
		if(entity != null){
			mybaitsGenericDao.delete(ShareConfig.class, entity.getId());
			if(CollectionUtils.isNotEmpty(entity.getItems())){
				for(Awards awards : entity.getItems()){
					mybaitsGenericDao.delete(Awards.class, awards.getId());
				}
			}
		}
	}
	
	/**
	 * 删除答题闯关相关配置参数
	 * @param config
	 * @throws Exception 
	 */
	public void deleteAnswer(Map<String, Object> params) throws Exception {
		AnswerConfig entity = this.getAnswerConfig(params);
		if(entity != null){
			if(CollectionUtils.isNotEmpty(entity.getCheckPoint())){
				for(CheckPoint checkPoint : entity.getCheckPoint()){
					if(CollectionUtils.isNotEmpty(checkPoint.getTopics())){
						for(Topic topic : checkPoint.getTopics()){
							if(CollectionUtils.isNotEmpty(topic.getOption())){
								for(TopicOption option : topic.getOption()){
									mybaitsGenericDao.delete(TopicOption.class, option.getId());
								}
							}
							mybaitsGenericDao.delete(Topic.class, topic.getId());
						}
					}
					mybaitsGenericDao.delete(CheckPoint.class, checkPoint.getId());
				}
			}
			if(CollectionUtils.isNotEmpty(entity.getPrizeOne())){
				for(Awards awards : entity.getPrizeOne()){
					mybaitsGenericDao.delete(Awards.class, awards.getId());
				}
			}
			if(entity.getPrize() != null){
				mybaitsGenericDao.delete(Awards.class, entity.getPrize().getId());
			}
			mybaitsGenericDao.delete(AnswerConfig.class, entity.getId());
		}
	}
	
	/**
	 * 删除优惠券配置参数
	 * @param config
	 * @throws Exception 
	 */
	public void deleteCoupon(Map<String, Object> params) throws Exception {
		CouponConfig entity = this.getCouponConfig(params);
		if(entity != null){
			if(CollectionUtils.isNotEmpty(entity.getItems())){
				for(Coupon item : entity.getItems()){
					mybaitsGenericDao.delete(Coupon.class, item.getId());
				}
			}
			mybaitsGenericDao.delete(CouponConfig.class, entity.getId());
		}
	}
	
	
	
	/****************************************private****************************************************/
	
	private List<PluginBaseConfig> getBaseConfigChildren(PluginBaseConfig entity) throws Exception{
		String query = "select * from t_plugin_basecfg t where t.parent="+entity.getId();
		return mybaitsGenericDao.executeQuery(PluginBaseConfig.class, query);
	}
	
	/****************************************活动记录处理***********************************************/
	/****************************************活动记录处理***********************************************/
	/****************************************活动记录处理***********************************************/
	/****************************************活动记录处理***********************************************/
	
	/****************************************惠团购记录*************************************************/
	/**
	 * 新增答题记录
	 * @param entity
	 * @throws Exception 
	 */
	public void saveAnswerRecord(AnswerRecord entity) throws Exception{
		mybaitsGenericDao.save(entity);
	}
	/**
	 * 新增活动留资记录
	 * @param config
	 * @throws Exception 
	 */
	public void saveCampaignLeads(CampaignLeads entity) throws Exception {
		mybaitsGenericDao.save(entity);
	}
	
	/**
	 * 更新活动留资记录
	 * @param config
	 * @throws Exception 
	 */
	public void updateCampaignLeads(CampaignLeads entity) throws Exception {
		mybaitsGenericDao.update(entity);
	}
	
	/**
	 * 查询活动留资记录
	 * @param config
	 * @throws Exception 
	 */
	public List<CampaignLeads> getCampaignLeads(String userCode, int type) throws Exception {
		return mybaitsGenericDao.executeQuery(CampaignLeads.class,"select * from t_campaign_leads l where l.usercode='"+userCode+"' and leads_type="+type+" order by  l.create_date desc");
	}
	
	/**
	 * 查询奖品及优惠券记录
	 * @param config
	 * @throws Exception 
	 */
	public List<CampaignLeads> getPrizes(String userCode) throws Exception {
		return mybaitsGenericDao.executeQuery(CampaignLeads.class,"select * from t_campaign_leads l where l.usercode='"+userCode+"' and (awards is not null or coupon is not null) order by  l.create_date desc");
	}
	
	/**
	 * 查询留资人信息
	 * @param config
	 * @throws Exception 
	 */
	public Map getLeadsInfo(String userCode) throws Exception {
		return mybaitsGenericDao.find("select name,phone from t_campaign_leads l where l.usercode='"+userCode+"' order by l.create_date desc");
	}
	
	/**
	 * 新增活动记录
	 * @param config
	 * @throws Exception 
	 */
	public void saveCampaignRecord(CampaignRecord entity) throws Exception {
		mybaitsGenericDao.save(entity);
	}
	
	/**
	 * 查询答题记录
	 * @param config
	 * @throws Exception 
	 */
	public List<AnswerRecord> getAnswerRecord(Map<String, Object> params) throws Exception {
		StringBuffer sql = new StringBuffer();
		sql.append("select * from t_answer_record t where 1=1 ");
		if(!params.isEmpty()){
			if(params.get("usercode") != null){
				sql.append("and usercode=:usercode ");
			}
			if(params.get("levelId") != null){
				sql.append("and levelid=:levelId ");
			}
			if(params.get("question") != null){
				sql.append("and question=:question ");
			}
			if(params.get("isright") != null){
				sql.append("and isright=:isright ");
			}
			if(params.get("sn") != null){
				sql.append("and sn=:sn ");
			}
		}
				
		return mybaitsGenericDao.executeQuery(AnswerRecord.class, sql.toString(), params);
	}
	
	/**
	 * 查询活动记录
	 * @param config
	 * @throws Exception 
	 */
	public List<CampaignRecord> getCampaignRecord(Map<String, Object> params) throws Exception {
		StringBuffer sql = new StringBuffer();
		sql.append("select * from t_campaign_record t where 1=1 ");
		if(!params.isEmpty()){
			if(params.get("pluginId") != null){
				sql.append("and pluginid=:pluginId ");
			}
			if(params.get("pageId") != null){
				sql.append("and pageid=:pageId ");
			}
			if(params.get("pluginUnion") != null){
				sql.append("and plugin_union=:pluginUnion ");
			}
			if(params.get("campaign") != null){
				sql.append("and campaign=:campaign ");
			}
			if(params.get("campaignCode") != null){
				sql.append("and campaign_code=:campaignCode ");
			}
			if(params.get("userCode") != null){
				sql.append("and usercode=:userCode ");
			}
			if(params.get("awards") != null){
				sql.append("and awards=:awards ");
			}
			if(params.get("sn") != null){
				sql.append("and coupon_code=:sn ");
			}
			sql.append(" order by create_date desc");
			
		}
				
		return mybaitsGenericDao.executeQuery(CampaignRecord.class, sql.toString(), params);
	}
	
	public Long getTotalCampaignRecord(Map<String, Object> params) throws Exception {
		StringBuffer sql = new StringBuffer();
		sql.append("select count(t.id) as ct from t_campaign_record t where 1=1 ");
		if(!params.isEmpty()){
			if(params.get("pluginId") != null){
				sql.append(" and pluginid="+params.get("pluginId"));
			}
			if(params.get("pageId") != null){
				sql.append(" and pageid="+params.get("pageId"));
			}
			if(params.get("pluginUnion") != null){
				sql.append(" and plugin_union='"+params.get("pluginUnion")+"'");
			}
			if(params.get("campaign") != null){
				sql.append(" and campaign="+params.get("campaign"));
			}
			if(params.get("campaignCode") != null){
				sql.append(" and campaign_code='"+params.get("campaignCode")+"'");
			}
			if(params.get("userCode") != null){
				sql.append(" and usercode='"+params.get("userCode")+"'");
			}
			if(params.get("awards") != null){
				sql.append(" and awards="+params.get("awards"));
			}
			if(params.get("sn") != null){
				sql.append(" and coupon_code='"+params.get("sn")+"'");
			}
			
		}
		
		Map map = mybaitsGenericDao.find(sql.toString());
		if(map != null){
			try{
				return Long.valueOf(map.get("ct").toString());
			}catch(Exception e){}
		}
		return 0l;
	}
	
	public Long getTotalCampaignLeadsRecord(Map<String, Object> params) throws Exception {
		StringBuffer sql = new StringBuffer();
		sql.append("select count(t.id) as ct from t_campaign_leads t where 1=1 ");
		if(!params.isEmpty()){
			if(params.get("pluginId") != null){
				sql.append(" and pluginid="+params.get("pluginId"));
			}
			if(params.get("pageId") != null){
				sql.append(" and pageid="+params.get("pageId"));
			}
			if(params.get("campaign") != null){
				sql.append(" and campaign="+params.get("campaign"));
			}
			if(params.get("userCode") != null){
				sql.append(" and usercode='"+params.get("userCode")+"'");
			}
			if(params.get("awards") != null){
				sql.append(" and awards="+params.get("awards"));
			}
			if(params.get("coupon") != null){
				sql.append(" and coupon="+params.get("coupon"));
			}
		}
		
		Map map = mybaitsGenericDao.find(sql.toString());
		if(map != null){
			try{
				return Long.valueOf(map.get("ct").toString());
			}catch(Exception e){}
		}
		return 0l;
	}
	
	/**
	 * 保存留资
	 * @param entity
	 * @return
	 * @throws Exception
	 */
	public long saveLeads(Leads entity) throws Exception{
		return mybaitsGenericDao.save(entity);
	}
	
	/****************************************其它查询*************************************************/
	
	/**
	 * 查询所有车系
	 * @return
	 * @throws LedpException 
	 */
	public List<VehicleSeries> getVehicleSeries() throws LedpException{
		String sql = "select * from t_vehicle_series where status=1010 order by name";
		return mybaitsGenericDao.executeQuery(VehicleSeries.class, sql);
	}
	
	/**
	 * 查询所有车型
	 * @return
	 * @throws LedpException 
	 */
	public List<Vehicle> getVehicles(long seriesId) throws LedpException{
		String sql = "select * from t_vehicle where status=1010 ";
		if(seriesId > 0){
			sql = "series="+seriesId+"";
		}
		sql += "order by name";
		
		return mybaitsGenericDao.executeQuery(Vehicle.class, sql);
	}
	
	/**
	 * 查询所有车系(用于新车展厅)
	 * @return
	 * @throws LedpException 
	 */
	public List<Map> getSeriesMaps() throws LedpException{
		StringBuilder query = new StringBuilder();
		query.append(" SELECT se.id as series,v.id as vehicle,se.name AS NAME,max(v.price) as maxmoney, ");
		query.append(" min(v.price) as minmoney,count(v.id) AS ct ");
		query.append(" FROM t_vehicle_series se");
		query.append(" left join t_vehicle v ON v.series = se.id and v.status=1010 ");
		query.append(" where se.status=1010 GROUP BY se.id ");
		return mybaitsGenericDao.executeQuery(query.toString());
	}
	
	/**
	 * 查询所有车系(用于新车展厅)
	 * @return
	 * @throws LedpException 
	 */
	public List<Map> getVehiclesMaps() throws LedpException{
		StringBuilder query = new StringBuilder();
		query.append(" SELECT v.id as vehicle,v.name AS name,v.price as guidePrice ");
		query.append(" FROM t_vehicle v");
		query.append(" where v.status=1010");
		return mybaitsGenericDao.executeQuery(query.toString());
	}
	/**
	 * 查询所有车系(用于新车展厅)
	 * @return
	 * @throws LedpException 
	 */
	public List<Map> getVehicles() throws LedpException{
		StringBuilder query = new StringBuilder();
		query.append(" SELECT v.id as id,v.name AS name ");
		query.append(" FROM t_vehicle v");
		query.append(" where v.status=1010");
		return mybaitsGenericDao.executeQuery(query.toString());
	}
	public String getVehicleImageByVehicleId(long id) throws LedpException{
		Map map = mybaitsGenericDao.find("select vi.webpath as url from t_vehicle_image vi where vi.status=1 and vi.vehicle_id="+id);
		if(!map.isEmpty()){
			return map.get("url").toString();
		}
		return "";
	}
	
	public List<Map> getDealers(String cityName) throws LedpException{
		List<Map> result = new ArrayList<Map>();
		String sql = "select * from t_dealer d where 1=1 ";
		if(cityName != null && "".equals(cityName)){
			sql += " and d.city="+Long.valueOf(cityName);
		}
		sql += " order by code";
		return mybaitsGenericDao.executeQuery(sql);
			
	}
	
	public long isIntentionVehicleSeries(Map<String, Object> params) throws LedpException {
		StringBuffer sql = new StringBuffer();
		sql.append("select count(c.val) as ct from t_plugin_basecfg p");
		sql.append(" left join t_plugin_basecfg c on c.parent = p.id and c.name='purchaseYixiang' and c.val='yes'");
		sql.append(" where 1=1 ");
		if(!params.isEmpty()){
			if(params.get("pluginId") != null && params.get("pageId") != null && params.get("pluginUnion") != null){
				sql.append(" and p.pluginid="+params.get("pluginId"));
				sql.append(" and p.pageid="+params.get("pageId"));
				sql.append(" and p.plugin_union='"+params.get("pluginUnion")+"'");
			}else{
				return 0l;
			}
			Map map = mybaitsGenericDao.find(sql.toString());
			if(map != null && map.size() > 0){
				try {
					return (Long)map.get("ct");
				} catch (Exception e) {}
			}
		}
		return 0;
	}
	
	//保存中奖信息
	public long saveWinInfo(WinInfo entity) throws Exception{
		return mybaitsGenericDao.save(entity);
	}
	
	public long getCountCouponByCouponId(long couponId) throws Exception{
		Map map = mybaitsGenericDao.find("select count(id) as ct from t_wininfo where coupon = "+couponId);
		long count = 0l;
		if(map != null){
			try {
				count = Long.valueOf(map.get("ct").toString());
			} catch (Exception e) {}
		}
		return count;
	}
	
	public long getCountCouponByCouponIdAndUserCode(long couponId,String userCode) throws Exception{
		Map map = mybaitsGenericDao.find("select count(id) as ct from t_wininfo where coupon = "+couponId+" and uuid = "+userCode);
		long count = 0l;
		if(map != null){
			try {
				count = Long.valueOf(map.get("ct").toString());
			} catch (Exception e) {}
		}
		return count;
	}
	
	private long getTotalCount(long id) throws Exception{
		Map map = mybaitsGenericDao.find("select count(id) as ct from t_campaign_leads where awards = "+id);
		long count = 0l;
		if(map != null){
			try {
				count = Long.valueOf(map.get("ct").toString());
			} catch (Exception e) {}
		}
		return count;
	}
	
	private long getTotalCount(Map<String,Object> params) throws Exception{
		String sql = "select count(id) as ct from t_campaign_leads where 1=1 ";
		if(params!=null){
			if(params.get("pluginId")!=null){
				sql+=" and pluginid="+params.get("pluginId");
			}
			if(params.get("pageId")!=null){
				sql+=" and pageid="+params.get("pageId");
			}
		}
		Map map = mybaitsGenericDao.find(sql);
		long count = 0l;
		if(map != null){
			try {
				count = Long.valueOf(map.get("ct").toString());
			} catch (Exception e) {}
		}
		return count;
	}

}
