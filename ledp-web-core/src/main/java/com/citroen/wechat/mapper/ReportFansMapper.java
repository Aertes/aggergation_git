package com.citroen.wechat.mapper;

import com.citroen.wechat.domain.FansReport;
import com.citroen.wechat.domain.PublicNo;
import com.citroen.wechat.form.ReportFansQuery;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by cyberoller on 2015/11/6.
 */
@Repository
public interface ReportFansMapper {
    int insert(FansReport fansReport);

    int selectByPrimaryKey(Long id);

    List<FansReport> selectListByByQuery(ReportFansQuery query);

    List<FansReport> selectCharByByQuery(ReportFansQuery query);

    int selectTotalRowsByQuery(ReportFansQuery query);

    int selectTotalFansByPublicNo(@Param("id") Long id);

    int selectTotalFansByQuery(ReportFansQuery query);

    List<String> selectNewUserListByQuery(ReportFansQuery query);

    List<String> selectCancelNewUserListByQuery(ReportFansQuery query);

    List<String> selectCancelOldUserListByQuery(ReportFansQuery query);

    Integer selectCumulateUserByQuery(ReportFansQuery query);



    List<PublicNo> selectPublicNoList(ReportFansQuery query);
}
