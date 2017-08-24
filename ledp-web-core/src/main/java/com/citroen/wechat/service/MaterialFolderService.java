package com.citroen.wechat.service;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.citroen.ledp.dao.mybaits.MybaitsGenericDao;
import com.citroen.ledp.domain.Menu;
import com.citroen.ledp.exception.LedpException;
import com.citroen.wechat.domain.Campaign;
import com.citroen.wechat.domain.Material;
import com.citroen.wechat.domain.MaterialFolder;
import com.citroen.wechat.domain.Template;

/**
 * 文件组
 * @author 何海粟
 * @date2015年6月16日
 */
@Service
public class MaterialFolderService {
	@Autowired
	private MybaitsGenericDao<Long> mybaitsGenericDao;
	
	public MaterialFolder getFolderTree() throws Exception{
		MaterialFolder root = null;
		List<MaterialFolder> list = mybaitsGenericDao.executeQuery(MaterialFolder.class,"select * from t_material_folder m where  m.parent is null");
		if(CollectionUtils.isNotEmpty(list)){
			root = list.get(0);
			root.setChildren(getChildren(root));
		}
		
		return root;
	}
	
	private List<MaterialFolder> getChildren(MaterialFolder parent) throws Exception{
		List<MaterialFolder> children = mybaitsGenericDao.executeQuery(MaterialFolder.class,"select * from t_material_folder m where  m.parent="+parent.getId());
		for(MaterialFolder child : children){
			child.setChildren(getChildren(child));
		}
		return children;
	}
	
	public Long save(MaterialFolder mf) throws LedpException {
		return mybaitsGenericDao.save(mf);
	}
	public MaterialFolder materialFolderInstance(Long mfId) throws LedpException {
		return mybaitsGenericDao.get(MaterialFolder.class, mfId);
	}
	public void update(MaterialFolder entity) throws LedpException {
		mybaitsGenericDao.update(entity);
	}
	public void deleteMaterialFolderInstance(Long mfId) throws LedpException {
		 mybaitsGenericDao.delete(MaterialFolder.class, mfId);
	}
	public List<MaterialFolder> getMaterialFolder(Long parent) throws LedpException{
		String sql="select * from t_material_folder where parent="+parent+" order by sort desc limit 0,1";
		return mybaitsGenericDao.executeQuery(MaterialFolder.class,sql);
	}
	public List<MaterialFolder> getMaterialFolders(Long parent) throws LedpException{
		String sql="select * from t_material_folder where parent="+parent+" order by sort desc";
		return mybaitsGenericDao.executeQuery(MaterialFolder.class,sql);
	}
	public List<Material> getMaterials(Long mfId) throws LedpException{
		String sql="select * from t_material where folder="+mfId;
		return mybaitsGenericDao.executeQuery(Material.class,sql);
	}
}
