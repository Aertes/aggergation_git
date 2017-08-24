package com.citroen.wechat.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.citroen.wechat.domain.WinInfo;
import com.citroen.wechat.mapper.WinInfoMapper;
import com.citroen.wechat.form.WinInfoForm;
import com.citroen.wechat.service.IWinInfoService;

import javax.annotation.Resource;

@Service
public class WinInfoServiceImpl implements IWinInfoService {
    @Resource
	private WinInfoMapper winInfoMapper;

	public List<WinInfo> getWinInfos(WinInfoForm form) {
		return winInfoMapper.getWinInfos(form);
	}

	public int getWinInfoCount(WinInfoForm form) {
		return winInfoMapper.getWinInfoCount(form);
	}

	public WinInfo get(long id) {
		return winInfoMapper.get(id);
	}

	public void updateStatus(long id) {
		winInfoMapper.updateStatus(id);
	}

}
