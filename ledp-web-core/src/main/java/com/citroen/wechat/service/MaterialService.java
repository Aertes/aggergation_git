package com.citroen.wechat.service;

import java.util.ArrayList;
import java.util.Date;
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
import com.citroen.wechat.domain.Material;
import com.citroen.wechat.dto.MaterialGroupDto;
import com.citroen.wechat.exception.WechatException;
import org.springframework.transaction.annotation.Transactional;

/**
 * 素材服务类
 * @author 何海粟
 * @date2015年6月8日
 */
@Service
public class MaterialService {
    private static Logger logger = Logger.getLogger(MaterialService.class);
	@Autowired
	private MybaitsGenericDao<Long> mybaitsGenericDao;

    @Transactional
	public Long save(Material entity) throws Exception {
		entity.setCreateDate(new Date());
		Long id = mybaitsGenericDao.save(entity);
		entity.setId(id);
		if(CollectionUtils.isNotEmpty(entity.getChildren())){
			for(Material material : entity.getChildren()){
				material.setParent(entity);
				material.setCreateDate(new Date());
				mybaitsGenericDao.save(material);
			}
		}
		return id;
	}
	
	public List<Material> executeQuery(Map params, Map<String,Integer> paginateParams) throws Exception{
		StringBuffer sql = new StringBuffer();
		sql.append("select * from t_material m where parent is null ");
		sql.append(getCondition(params));
		List<Material> materials = mybaitsGenericDao.executeQuery(Material.class,sql.toString(), params, paginateParams);
		if(CollectionUtils.isNotEmpty(materials)){
			for(Material material : materials){
				String queryChild = "select * from t_material m where m.parent="+material.getId()+" order by sort ";
				material.setChildren(mybaitsGenericDao.executeQuery(Material.class, queryChild));
			}
		}
		return materials;
	}
	
	public List<Material> getImgs(Map params, Map<String,Integer> paginateParams) throws Exception{
		StringBuffer sql = new StringBuffer();
		sql.append("select * from t_material m where parent is null and type in (9002,9007) ");
		sql.append(getCondition(params));
		return mybaitsGenericDao.executeQuery(Material.class,sql.toString(), params, paginateParams);
	}
	
	public Material getByMediaId(String mediaId) throws Exception{
		StringBuffer sql = new StringBuffer();
		if(StringUtils.isBlank(mediaId)){
			return null;
		}
		sql.append("select * from t_material m where media_id='"+mediaId+"'");
		Material material = mybaitsGenericDao.find(Material.class,sql.toString());
		if(material != null){
			String queryChild = "select * from t_material m where m.parent="+material.getId()+" order by sort ";
			material.setChildren(mybaitsGenericDao.executeQuery(Material.class, queryChild));
		}
		return material;
	}
	
	public int getTotalRow(Map params) throws LedpException{
		StringBuffer sql = new StringBuffer();
		sql.append("select m.* from t_material m where parent is null ");
		sql.append(getCondition(params));
		
		List<Material> list = mybaitsGenericDao.executeQuery(Material.class,sql.toString(), params);
		if(list.isEmpty()){
			return 0;
		}
		
		return list.size();
	}
	
	public int getImgTotalRow(Map params) throws LedpException{
		StringBuffer sql = new StringBuffer();
		sql.append("select m.* from t_material m where parent is null and type in (9002,9007) ");
		sql.append(getCondition(params));
		
		List<Material> list = mybaitsGenericDao.executeQuery(Material.class,sql.toString(), params);
		if(list.isEmpty()){
			return 0;
		}
		
		return list.size();
	}


	public void delete(Long[] ids) throws Exception {
		if(ids == null || ids.length <= 0){
			throw new WechatException("删除失败,请刷新后重试");
		}
		for(int i=0; i<ids.length; i++){
			Material entity = mybaitsGenericDao.get(Material.class, ids[i]);
			//删除物理文件
			/*FileUtil.deleteFile(entity.getFilepath());*/
			if(entity == null){
				break;
			}
			List<Material> children = mybaitsGenericDao.executeQuery(Material.class, "select m.* from t_material m where parent="+entity.getId());
			try {
				if(CollectionUtils.isNotEmpty(children)){
					for(Material child : children){
						//删除物理文件
						/*FileUtil.deleteFile(material.getFilepath());*/
						mybaitsGenericDao.delete(Material.class, child.getId());
					}
				}
				mybaitsGenericDao.delete(Material.class, ids[i]);
			} catch (Exception e) {
                logger.error("异常信息：" + e.getMessage());
				throw new WechatException("删除失败,请刷新后重试");
			}
		}
	}

	public Material get(Long id) throws Exception {
		Material material = mybaitsGenericDao.get(Material.class, id);
		if(material == null){
			return null;
		}
		String queryChild = "select * from t_material m where m.parent="+material.getId();
		material.setChildren(mybaitsGenericDao.executeQuery(Material.class, queryChild));
		return material;
	}
	
	public void updateMaterial(Material entity) throws LedpException{
		mybaitsGenericDao.update(entity);
	}

    @Transactional
	public long update(Material entity) throws Exception {
		Material material = this.get(entity.getId());
		if(material == null){
			throw new WechatException("未查到该素材");
		}
		entity.setContentUrl(material.getContentUrl());
		entity.setCreateDate(new Date());
		mybaitsGenericDao.update(entity);
		if(CollectionUtils.isNotEmpty(entity.getChildren())){ 
			List<Material> oldChild = material.getChildren();
			int i = 0;
			for(Material child : entity.getChildren()){
				child.setParent(entity);
				if(CollectionUtils.isNotEmpty(oldChild) && oldChild.get(i) != null){
					child.setContentUrl(oldChild.get(i++).getContentUrl());
				}
				mybaitsGenericDao.update(child);
			}
		}
		return entity.getId();
	}
	
	public List<MaterialGroupDto> searchFileGroup(String type) throws Exception{
		StringBuffer sql = new StringBuffer();
		sql.append("select mf.name as name,count(m.id) as total,mf.id as id from t_material m ");
		sql.append("LEFT JOIN t_material_folder mf on mf.id = m.folder ");
		sql.append("where m.type="+type+" group by m.folder order by mf.sort");
		List<Map> result = mybaitsGenericDao.executeQuery(sql.toString());
		return getGroup(result);
	}
	
	private List<MaterialGroupDto> getGroup(List<Map> result) {
		List<MaterialGroupDto> dtos = new ArrayList<MaterialGroupDto>();
		if(CollectionUtils.isNotEmpty(result)){
			for(int i=0;i<result.size();i++){
				MaterialGroupDto dto = new MaterialGroupDto();
				if(StringUtils.isBlank((String) result.get(i).get("name"))){
					dto.setGroupName("未分组");
					dto.setSort(0);
					dto.setTotal((Long) result.get(i).get("total"));
					dto.setId(0L);
				}else{
					dto.setGroupName((String) result.get(i).get("name"));
					dto.setSort(i+1);
					dto.setTotal((Long) result.get(i).get("total"));
					dto.setId((Long) result.get(i).get("id"));
				}
				dtos.add(dto);
			}
		}
		return dtos;
	}

	private String getCondition(Map params){
		StringBuffer sql = new StringBuffer();
		if(!params.isEmpty()){
			if(params.get("id")!=null){
				sql.append("and m.id=:id ");
			}
			if(params.get("publicnoId")!=null){
				sql.append("and m.publicno=:publicnoId ");
			}
			if(StringUtils.isNotBlank((String) params.get("type"))){
				sql.append("and m.type=:type ");
			}
			if(params.get("folder") != null){
				sql.append("and m.folder=:folder ");
			}
			if(StringUtils.isNotBlank((String) params.get("sharename"))){
				sql.append("and m.title like :sharename ");
			}
			if(StringUtils.isNotBlank((String) params.get("title"))){
				sql.append("and (m.title like :title or m.author like :author or m.digest like :digest )");
			}
			if(StringUtils.isNotBlank((String) params.get("mediaId"))){
				sql.append("and m.media_id = :mediaId ");
			}
		}
		sql.append(" order by m.date_create desc ");
		return sql.toString();
	}
	public void deleteMaterialInstance(Long mId) throws LedpException {
		 mybaitsGenericDao.delete(Material.class, mId);
	}
}
