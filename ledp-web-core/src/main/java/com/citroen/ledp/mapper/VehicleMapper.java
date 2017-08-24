package com.citroen.ledp.mapper;

import com.citroen.ledp.domain.Constant;
import com.citroen.ledp.domain.Vehicle;
import com.citroen.ledp.domain.VehicleSeries;
import com.citroen.ledp.exception.LedpException;
import com.citroen.wechat.domain.Campaign;
import com.citroen.wechat.domain.CampaignLeads;
import com.citroen.wechat.form.ReportCampaignQuery;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

/**
 * Created by cyberoller on 2015/11/3.
 */
@Repository
public interface VehicleMapper extends Mapper<Vehicle>{

}
