package com.citroen.wechat.service;

import java.util.Map;

import com.citroen.wechat.domain.CampaignRecord;
import com.citroen.wechat.domain.RotaryTableConfig;
import com.citroen.wechat.domain.WinInfo;
import com.citroen.wechat.form.SubmitLeadsForm;

/**
 * 前端接口服务
 * @author hhs
 * @date 2015年11月9日 下午7:23:56
 */
public interface IWebService {
	
	public WinInfo submitLeads(SubmitLeadsForm form) throws Exception ;

	public void isWin(RotaryTableConfig config, boolean flag, String uuid,
			Map<String, Object> result, CampaignRecord entity,
			Map<String, Object> params, Map<String, Object> json) throws Exception;
}
