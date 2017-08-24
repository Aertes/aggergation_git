package com.citroen.wechat.mapper;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.citroen.wechat.domain.WinInfo;
import com.citroen.wechat.form.WinInfoForm;

/**
 * 奖品信息dao
 *
 * @author hhs
 * @date 2015-11-09 14:36:12
 */
@Repository
public interface WinInfoMapper {
    List<WinInfo> getWinInfos(WinInfoForm form);

    int getWinInfoCount(WinInfoForm form);

    WinInfo get(long id);

    void updateStatus(long id);
}
