package com.citroen.wechat.service;

import java.util.List;

import com.citroen.wechat.domain.WinInfo;
import com.citroen.wechat.form.WinInfoForm;

/**
 * 前端接口服务
 * @author hhs
 * @date 2015年11月9日 下午7:23:56
 */
public interface IWinInfoService {
	public List<WinInfo> getWinInfos(WinInfoForm form);
	public int getWinInfoCount(WinInfoForm form);
	public WinInfo get(long id);
	public void updateStatus(long id);
}
