package com.citroen.ledp.service.impl;

import com.citroen.ledp.dao.GenericDao;
import com.citroen.ledp.domain.Dealer;
import com.citroen.ledp.domain.DealerMapping;
import com.citroen.ledp.exception.LedpException;
import com.citroen.ledp.service.DealerMappingService;
import com.citroen.ledp.vo.DealerMappingVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by maskx on 2017/5/24.
 */
@Service
public class DealerMappingServiceImpl implements DealerMappingService{

    @Autowired
    private GenericDao genericDao;

    public DealerMapping find(String dealer) throws LedpException {
        return (DealerMapping) genericDao.find(DealerMapping.class, "select * from t_dealer_mapping where source_dealer = '"+dealer+"'");
    }

    public DealerMapping find(long id) throws LedpException {
        return (DealerMapping) genericDao.find(DealerMapping.class, "select * from t_dealer_mapping where id = " + id);
    }

    public Dealer findD(String dealer) throws LedpException {
        return (Dealer) genericDao.find(Dealer.class, "select * from t_dealer where code = '" + dealer + "'");
    }

    public void save(DealerMapping dealerMapping) throws LedpException {
        genericDao.save(dealerMapping);
    }

    public List<DealerMappingVO> query(DealerMappingVO dealerMapping, int pageSize, int pageNumber) throws LedpException {
        Map<String,Object> condition = getCondition(dealerMapping);
        Map namedParams = (Map)condition.get("namedParams");
        String sql = "select * from (select dm.*, (select d.name from t_dealer d where d.code = dm.source_dealer limit 1) source_dealer_name, (select d.name from t_dealer d where d.code = dm.target_dealer limit 1) target_dealer_name from t_dealer_mapping dm ) d where 1 = 1 " + condition.get("namedSql");

        Map paginateParams = new HashMap();
        int offset = (pageNumber - 1) * pageSize;
        paginateParams.put("max",pageSize);
        paginateParams.put("offset",offset);

        return genericDao.executeQuery(DealerMappingVO.class, sql, namedParams, paginateParams);
    }

    public int totalRow(DealerMappingVO dealerMapping) throws LedpException {
        Map<String,Object> condition = getCondition(dealerMapping);
        Map namedParams = (Map)condition.get("namedParams");
        String sql = "select count(1) count from (select dm.*, (select d.name from t_dealer d where d.code = dm.source_dealer limit 1) source_dealer_name, (select d.name from t_dealer d where d.code = dm.target_dealer limit 1) target_dealer_name from t_dealer_mapping dm ) d where 1 = 1 " + condition.get("namedSql");
        List<Map> list = genericDao.executeQuery(sql, namedParams);
        return Integer.parseInt(list.get(0).get("count").toString());
    }

    public Map<String,Object> getCondition(DealerMappingVO dealerMapping){
        Map<String,Object> namedParams = new HashMap<String,Object>();

        StringBuilder namedSql = new StringBuilder();

        if(!StringUtils.isBlank(dealerMapping.getSourceDealer())){
            String sourceDealer = dealerMapping.getSourceDealer();
            namedParams.put("sourceDealer",sourceDealer);
            namedSql.append(" and source_dealer = :sourceDealer");
        }
        if(!StringUtils.isBlank(dealerMapping.getSourceDealerName())){
            String sourceDealerName = dealerMapping.getSourceDealerName();
            namedParams.put("sourceDealerName", "%" + sourceDealerName + "%");
            namedSql.append(" and source_dealer_name like :sourceDealerName");
        }

        if(!StringUtils.isBlank(dealerMapping.getTargetDealer())){
            String targetDealer = dealerMapping.getTargetDealer();
            namedParams.put("targetDealer",targetDealer);
            namedSql.append(" and target_dealer = :targetDealer");
        }
        if(!StringUtils.isBlank(dealerMapping.getTargetDealerName())){
            String targetDealerName = dealerMapping.getTargetDealerName();
            namedParams.put("targetDealerName", "%" + targetDealerName + "%");
            namedSql.append(" and target_dealer_name like :targetDealerName");
        }

        Map<String,Object> rs = new HashMap<String,Object>();
        rs.put("namedSql",namedSql);
        rs.put("namedParams",namedParams);
        return rs;
    }

    public DealerMappingVO get(long id) throws LedpException {
        return (DealerMappingVO) genericDao.find(
                DealerMappingVO.class,
                "select * from (select dm.*, (select d.name from t_dealer d where d.code = dm.source_dealer limit 1) source_dealer_name, (select d.name from t_dealer d where d.code = dm.target_dealer limit 1) target_dealer_name from t_dealer_mapping dm where id = "+ id +" ) d"
        );
    }

    public void delete(long id) throws LedpException {
        genericDao.delete(DealerMapping.class, id);
    }

    public void update(DealerMapping dealerMapping) throws LedpException {
        genericDao.update(dealerMapping);
    }
}
