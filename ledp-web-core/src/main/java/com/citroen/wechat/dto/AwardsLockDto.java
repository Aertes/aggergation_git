package com.citroen.wechat.dto;

import java.util.Date;

/**
 * 大转盘奖品锁dto
 * @author Administrator
 *
 */
public class AwardsLockDto {
	private String userCode;
	private Date createDate;
	private long awardsId;
	public String getUserCode() {
		return userCode;
	}
	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date date) {
		this.createDate = date;
	}
	public long getAwardsId() {
		return awardsId;
	}
	public void setAwardsId(long awardsId) {
		this.awardsId = awardsId;
	}
}
