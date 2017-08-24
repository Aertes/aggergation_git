package com.citroen.ledp.service.impl;

import com.citroen.ledp.dao.mybaits.MybaitsGenericDao;
import com.citroen.ledp.domain.Media;
import com.citroen.ledp.domain.News;
import com.citroen.ledp.domain.NewsMedia;
import com.citroen.ledp.domain.Organization;
import com.citroen.ledp.exception.LedpException;
import com.citroen.ledp.exception.LogicException;
import com.citroen.ledp.service.ConstantService;
import com.citroen.ledp.service.NewsService;
import com.citroen.ledp.service.PermissionService;
import com.citroen.ledp.util.DateUtil;
import com.citroen.ledp.util.MapUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Title: NewsService.java
 * @Package com.citroen.ledp.service
 * @Description: TODO(用一句话描述该文件做什么)
 * @author 廖启洪
 * @date 2015年3月18日 下午11:21:18
 * @version V1.0
 */
@Service
public class NewsServiceImpl implements NewsService{

	@Autowired
	private MybaitsGenericDao<Long> mybaitsGenericDao;
	@Autowired
	private ConstantService constantService;
	@Autowired
	private PermissionService permissionService;

	public List<Map<String,String>> executeMapQuery(Map params) throws LedpException{
		List<News> listNews = executeQuery(params);
		List<Map<String,String>> listMap = new ArrayList<Map<String,String>>();
		for(News news:listNews){
			Map<String,String> map = new HashMap<String,String>();

			map.put("id",String.valueOf(news.getId()));
			map.put("dealer.name",news.getDealer().getName());
			map.put("type.name",news.getType().getName());
			map.put("title",news.getTitle());
			map.put("state.name","");
			map.put("statusCode","");
			if(news.getState()!=null){
				map.put("state.name",news.getState().getName());
				map.put("statusCode",news.getState().getCode());
			}
			map.put("dateCreate",DateUtil.format(news.getDateCreate(),"yyyy-MM-dd"));
			map.put("scanCount",news.getScanCount()+"");
			StringBuilder result = new StringBuilder();

			String stateCode = news.getState().getCode();
			if("publish_whole".equals(stateCode)||"publish_part".equals(stateCode)
					||"publish_none".equals(stateCode)||"destroy_whole".equals(stateCode)
					||"destroy_part".equals(stateCode)||"destroy_none".equals(stateCode)
					||"update_whole".equals(stateCode)||"update_part".equals(stateCode)||"update_none".equals(stateCode)){
				List<NewsMedia> listSuccess = mybaitsGenericDao.executeQuery(NewsMedia.class,"select * from t_news_media where state in(6050,6060,6130,6140,6090) and news="+news.getId());
				List<NewsMedia> listfailure = mybaitsGenericDao.executeQuery(NewsMedia.class,"select * from t_news_media where state not in(6050,6060,6130,6140,6090) and news="+news.getId());
				if(!listSuccess.isEmpty()){
					result.append("成功（");
					for(int i=0;i<listSuccess.size();i++){
						NewsMedia newsMedia = listSuccess.get(i);
						if(i>0){ result.append("，"); }
						result.append(newsMedia.getMedia().getName());
					}
					result.append("）");
				}
				if(!listfailure.isEmpty()){
					if(!listSuccess.isEmpty()){
						result.append("；");
					}
					result.append("失败（");
					for(int i=0;i<listfailure.size();i++){
						NewsMedia newsMedia = listfailure.get(i);
						if(i>0){ result.append("，"); }
						result.append(newsMedia.getMedia().getName());
					}
					result.append("）");
				}
			}
			map.put("result",result.toString());
			listMap.add(map);
		}
		return listMap;
	}

	public List<News> executeQuery(Map params) throws LedpException{
		//根据当前用户确定筛选条件
		HttpSession session = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getSession();
		//获取登陆用户的权限列表
		List<String> permissions = (List<String>) session.getAttribute("permissions");
		params.put("permissions", permissions);
		Map<String,Object> condition = getCondition(params);
		Map namedParams = (Map)condition.get("namedParams");
		String sql = "select * from t_news where 1=1 "+condition.get("namedSql");
		if(params.containsKey("sortName")){
			sql+=" order by  "+params.get("sortName");
		}else{
			sql+=" order by  date_create desc";
		}
		if(params.containsKey("sortOrder")){
			sql+=" "+params.get("sortOrder");
		}

		Map paginateParams = new HashMap();
		if(!params.containsKey("max")){
			int pageSize =(Integer) params.get("pageSize");
			int pageNumber =(Integer) params.get("pageNumber");
			paginateParams.put("max",pageSize);
			paginateParams.put("offset",(pageNumber-1)*pageSize);
		}
		return mybaitsGenericDao.executeQuery(News.class,sql,namedParams,paginateParams);
	}

	public int getTotalRow(Map params) throws LedpException{
		Map<String,Object> condition = getCondition(params);
		Map namedParams = (Map)condition.get("namedParams");
		String sql ="select count(id) count from t_news where 1=1 "+condition.get("namedSql");
		List<Map> list = mybaitsGenericDao.executeQuery(sql,namedParams);
		if(list.isEmpty()){
			return 0;
		}
		Map map = list.get(0);
		return map.isEmpty()?0:Integer.parseInt(map.get("count").toString());
	}

	public Map<String,Object> getCondition(Map<String,Object> params){
		MapUtil<String,Object> mapUtil = new MapUtil<String,Object>(params);
		Map<String,Object> namedParams = new HashMap<String,Object>();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		StringBuilder namedSql = new StringBuilder();

		if(!mapUtil.isBlank("title")){
			String title = mapUtil.get("title");
			namedParams.put("title","%"+title+"%");
			namedSql.append(" and title like :title");
		}
		if(!mapUtil.isBlank("date1")){
			String date1 = format.format(DateUtil.parse((String)mapUtil.get("date1"),"yyyy-MM-dd"));
			namedParams.put("date1",date1);
			namedSql.append(" and date_create >=:date1");
		}
		if(!mapUtil.isBlank("date2")){
			String date2 = format.format(DateUtil.parse((String)mapUtil.get("date2"),"yyyy-MM-dd"));
			namedParams.put("date2",date2);
			namedSql.append(" and date_create <=:date2");
		}
		if(!mapUtil.isBlank("type")){
			String type = mapUtil.get("type");
			namedParams.put("type",type);
			namedSql.append(" and type =:type");
		}
		if(!mapUtil.isBlank("stateId")){
			Long stateId = Long.parseLong(params.get("stateId").toString());
			namedParams.put("stateId",stateId);
			namedSql.append(" and state =:stateId");
		}
		Organization org = (Organization) mapUtil.get("org");
		if(null!= org && 1 == org.getLevel()){ //总部
			if("2".equals(mapUtil.get("level"))){
				if(null!=params.get("organation")){
					Long organation = Long.parseLong(params.get("organation").toString());
					namedParams.put("organation",organation);
					namedSql.append(" and org =:organation");
				}
			}else if("3".equals(mapUtil.get("level"))){
				if(null!=params.get("organation")){
					Long organation = Long.parseLong(params.get("organation").toString());
					namedParams.put("organation",organation);
					namedSql.append(" and dealer =:organation");
				}
			}
		}else if(null!= org && 2 == org.getLevel()){ //大区
			if("2".equals(mapUtil.get("level"))){
				if(null!=params.get("organation")){
					Long organation = Long.parseLong(params.get("organation").toString());
					namedParams.put("organation",organation);
					namedSql.append(" and dealer =:organation");
				}
			}else{
				if(null!=params.get("organation")){
					Long organation = Long.parseLong(params.get("organation").toString());
					namedParams.put("organation",organation);
					namedSql.append(" and org =:organation");
				}
			}
		}else{//网点
			if(null!=params.get("dealer")){
				Long dealer = Long.parseLong(params.get("dealer").toString());
				namedParams.put("dealer",dealer);
				namedSql.append(" and dealer =:dealer");
			}
		}
		Map<String,Object> rs = new HashMap<String,Object>();
		rs.put("namedSql",namedSql);
		rs.put("namedParams",namedParams);
		return rs;
	}

	public News get(long id) throws LedpException{
		return mybaitsGenericDao.get(News.class, id);
	}

	@Transactional
	public Long save(News entity,String medias) throws LedpException{
		Long newsId = mybaitsGenericDao.save(entity);
		entity.setId(newsId);
		List<Media> listMedia = mybaitsGenericDao.executeQuery(Media.class,"select * from t_media where id in("+medias+")");
		//添加发送媒体
		for(Media media:listMedia){
			String type = ","+entity.getType().getId()+",";
			String newsType = ","+media.getNewsType()+",";
			if(newsType.contains(type)){
				NewsMedia newsMedia = new NewsMedia();
				newsMedia.setNews(entity);
				newsMedia.setMedia(media);
				newsMedia.setState(entity.getState());
				mybaitsGenericDao.save(newsMedia);
			}
		}
		return newsId;
	}

	@Transactional
	public void update(News entity) throws LedpException{
		mybaitsGenericDao.update(entity);
		long newsId = entity.getId();
		long state = entity.getState().getId();

		mybaitsGenericDao.execute("update t_news_media set comment='信息已发布，此次为信息修改操作',state="+state+" where uniqueKey is not null and news ="+newsId);
		mybaitsGenericDao.execute("update t_news_media set comment='',state="+state+" where  uniqueKey is null and news ="+newsId);
	}

	@Transactional
	public void update(News entity,String medias) throws LedpException{
		mybaitsGenericDao.update(entity);
		List<String> listMediaId = Arrays.asList(medias.split(","));
		List<NewsMedia> listNewsMedia = mybaitsGenericDao.executeQuery(NewsMedia.class,"select * from t_news_media where news="+entity.getId());
		//修正旧记录状态
		for(NewsMedia newsMedia:listNewsMedia){
			String code = newsMedia.getState().getCode();
			if("6050".equals(code)||"6090".equals(code)){
				newsMedia.setState(entity.getState());
				newsMedia.setDatetime(null);
				newsMedia.setComment("修改已发布信息");
				mybaitsGenericDao.update(newsMedia);
				continue;
			}
			String newsMediaId = newsMedia.getMedia().getId().toString();
			if(listMediaId.contains(newsMediaId)){
				newsMedia.setState(entity.getState());
				newsMedia.setDatetime(null);
				newsMedia.setComment("");
				mybaitsGenericDao.update(newsMedia);
			}else{
				mybaitsGenericDao.delete(NewsMedia.class,newsMedia.getId());
			}
		}

		List<Media> listMedia = mybaitsGenericDao.executeQuery(Media.class,"select * from t_media where id in("+medias+")");
		//添加发送媒体
		for(Media media:listMedia){
			NewsMedia newsMedia = mybaitsGenericDao.find(NewsMedia.class,"select * from t_news_media where media="+media.getId()+" and news="+entity.getId());
			if(newsMedia==null ||newsMedia.getId()==null){
				String type = ","+entity.getType().getId()+",";
				String newsType = ","+media.getNewsType()+",";
				if(newsType.contains(type)){
					newsMedia = new NewsMedia();
					newsMedia.setNews(entity);
					newsMedia.setMedia(media);
					newsMedia.setState(entity.getState());
					mybaitsGenericDao.save(newsMedia);
				}
			}
		}
	}

	public List<Media> getDealerMediaList(Long dealer) throws LedpException{
		String sql = "select * from t_media where status=1010 and id in(select media from t_dealer_media where dealer="+dealer+") order by id asc";
		List<Media> mediaList = mybaitsGenericDao .executeQuery(Media.class, sql);
		return mediaList;
	}
	public List<Long> getNewsMediaList(Long news) throws LedpException{
		String sql = "select media from t_news_media where news="+news+"";
		List<Map> mediaList = mybaitsGenericDao .executeQuery(sql);
		List<Long> list = new ArrayList<Long>();
		for(Map media:mediaList){
			if(media.get("media")!=null){
				list.add(Long.parseLong(media.get("media").toString()));
			}
		}
		return list;
	}

	public List<NewsMedia> getNewsMedia(Long newsId) throws LedpException{
		return mybaitsGenericDao .executeQuery(NewsMedia.class,"select * from t_news_media where news="+newsId+" order by news");
	}

	public void delete(Long id) throws LedpException{
		if(id==null){
			throw new LogicException("操作对象不存在！");
		}
		mybaitsGenericDao.execute("delete from t_news_media where news ="+id);
		mybaitsGenericDao.execute("delete from t_news where id ="+id);
	}
	@Transactional
	public void destroy(Long id) throws LedpException{
		if(id==null){
			throw new LogicException("操作对象不存在！");
		}
		mybaitsGenericDao.execute("update t_news_media set state=6080 where news ="+id);
		mybaitsGenericDao.execute("update t_news set state=6080,scan_count=0 where id ="+id);
	}

	public News find(Long dealer,String name) throws LedpException{
		String query = "select * from t_news where dealer="+dealer+" and title='"+name+"'";
		return mybaitsGenericDao.find(News.class,query);
	}

	public PermissionService getPermissionService() {
		return permissionService;
	}

	public void setPermissionService(PermissionService permissionService) {
		this.permissionService = permissionService;
	}

	public List<News> executeQuery1(Map params) throws LedpException{
		if(params.get("dealer")==null){
			return new ArrayList<News>();
		}
		Map<String,Object> condition = getCondition(params);
		Map namedParams = (Map)condition.get("namedParams");
		String sql = "";
		sql += "select * from t_news where  dealer="+params.get("dealer")+" and state in(6020,6040)";

		if(params.containsKey("sortName")){
			sql+=" order by "+params.get("sortName");
		}
		if(params.containsKey("sortOrder")){
			sql+=" "+params.get("sortOrder");
		}

		Map paginateParams = new HashMap();
		if(!params.containsKey("max")){
			int pageSize =(Integer) params.get("pageSize");
			int pageNumber =(Integer) params.get("pageNumber");
			paginateParams.put("max",pageSize);
			paginateParams.put("offset",(pageNumber-1)*pageSize);
		}
		return mybaitsGenericDao.executeQuery(News.class,sql,namedParams,paginateParams);
	}

	public int getTotalRow1(Map params) throws LedpException{
		if(params.get("dealer")==null){
			return 0;
		}
		Map<String,Object> condition = getCondition(params);
		Map namedParams = (Map)condition.get("namedParams");
		String sql = "";
		sql +="select count(id) count from t_news where dealer="+params.get("dealer")+" and state in(6020,6040)";

		List<Map> list = mybaitsGenericDao.executeQuery(sql,namedParams);
		if(list.isEmpty()){
			return 0;
		}
		Map map = list.get(0);
		return map.isEmpty()?0:Integer.parseInt(map.get("count").toString());
	}


	public Map<String,String> uploadCover(HttpServletRequest request,String name){
		Map<String,String> response = new HashMap<String,String>();
		MultipartHttpServletRequest multipartRequest  =  (MultipartHttpServletRequest) request;
		MultipartFile file = multipartRequest.getFile("file");

		String saveFileName = getSaveFileName(file.getOriginalFilename(),name);
		String savePath = request.getSession().getServletContext().getRealPath("/") + "/cover/"+saveFileName;

		if(file.isEmpty()){
			response.put("code","201");
			response.put("message","封面图片为空");
			return response;
		}
		long fileSize = file.getSize();
		if(fileSize > 5000000){
			response.put("code","201");
			response.put("message","封面图片大小不能超过5M");
			return response;
		}
		File uploadDir = new File(savePath);
		if(!uploadDir.isDirectory()){
			uploadDir.mkdir();
		}
		//检查目录写入权限
		if(!uploadDir.canWrite()){
			response.put("code","201");
			response.put("message","封面图片上传目录没有写入权限");
			return response;
		}

		//获取服务器全路径
		String serverURL = request.getRequestURL().toString();
		serverURL = serverURL.substring(0,serverURL.indexOf(request.getContextPath()));
		//文件保存目录URL
		String saveUrl = serverURL + request.getContextPath() + "/cover/"+saveFileName;
		try{
			File uploadedFile = new File(savePath, saveFileName);
			file.transferTo(uploadedFile);
		}catch(Exception e){
			response.put("code","201");
			response.put("message","封面图片上传失败");
			response.put("error",e.getMessage());
			return response;
		}
		response.put("code","200");
		response.put("message","封面图片上传成功");
		response.put("url",saveUrl);
		return response;
	}

	public String getSaveFileName(String originalFilename,String saveFileName){
		if(StringUtils.isBlank(originalFilename) || !originalFilename.contains(".")){
			return "";
		}
		String suffix = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
		return saveFileName+suffix;
	}
}
