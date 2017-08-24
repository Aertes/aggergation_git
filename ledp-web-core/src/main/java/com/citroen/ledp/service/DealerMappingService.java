package com.citroen.ledp.service;

import com.citroen.ledp.dao.GenericDao;
import com.citroen.ledp.domain.Dealer;
import com.citroen.ledp.domain.DealerMapping;
import com.citroen.ledp.exception.LedpException;
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

public interface DealerMappingService {

    DealerMapping find(String dealer) throws LedpException ;

    DealerMapping find(long id) throws LedpException ;

    Dealer findD(String dealer) throws LedpException;

    void save(DealerMapping dealerMapping) throws LedpException;

    List<DealerMappingVO> query(DealerMappingVO dealerMapping, int pageSize, int pageNumber) throws LedpException ;

    int totalRow(DealerMappingVO dealerMapping) throws LedpException ;

    Map<String,Object> getCondition(DealerMappingVO dealerMapping);
    DealerMappingVO get(long id) throws LedpException ;

    void delete(long id) throws LedpException ;

    void update(DealerMapping dealerMapping) throws LedpException ;
}
