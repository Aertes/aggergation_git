package com.citroen.wechat.form;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

public class PluginReportForm {
	/**
	 * 1:上周
	 * 2：本周
	 * 3：本月
	 * 4：上月
	 * 5：自定义
	 */
	private Integer effect;
	private Long pluginId;
	private Long orgId;
	private Long dealerId;
	private int pageSize;
	private int currentPage;
	private Integer pageId;
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
	private Date startTime;
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
	private Date endTime;
	private String customName;
	private String phone;
	public Integer getEffect() {
		return effect;
	}
	public void setEffect(Integer effect) {
		this.effect = effect;
	}
	public Long getPluginId() {
		return pluginId;
	}
	public void setPluginId(Long pluginId) {
		this.pluginId = pluginId;
	}
	public Long getOrgId() {
		return orgId;
	}
	public void setOrgId(Long orgId) {
		this.orgId = orgId;
	}
	public Long getDealerId() {
		return dealerId;
	}
	public void setDealerId(Long dealerId) {
		this.dealerId = dealerId;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public int getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	public Integer getPageId() {
		return pageId;
	}
	public void setPageId(Integer pageId) {
		this.pageId = pageId;
	}
	public String getCustomName() {
		return customName;
	}
	public void setCustomName(String customName) {
		this.customName = customName;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
}