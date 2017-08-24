package com.citroen.wechat.form;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

public class LeadsReportForm {
	/**
	 * 1:上周
	 * 2：本周
	 * 3：本月
	 * 4：上月
	 * 5：自定义
	 */
	private int effect;
	private Long organizationSelectedId;
	private Integer organizationLevel;
	private Long ledpDealer;
	private int pageSize;
	private int currentPage;
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
	private Date beginDate;
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
	private Date endDate;
	private String name;
	private String phone;
	private Long ledpMediaId;
	/**
	 * 留资来源
	 * 0活动
	 * 1微站
	 */
	private int source;
	/**
	 * 留资类型
	 */
	private Long typeId;
	/**
	 * 意向级别
	 */
	private Long ledpIntentId;
	/**
	 * 跟进状态
	 */
	private Long ledpFollowId;
	private String sortName;
	private String sortOrder;
	public int getEffect() {
		return effect;
	}
	public void setEffect(int effect) {
		this.effect = effect;
	}
	public Long getOrganizationSelectedId() {
		return organizationSelectedId;
	}
	public void setOrganizationSelectedId(Long organizationSelectedId) {
		this.organizationSelectedId = organizationSelectedId;
	}
	public Integer getOrganizationLevel() {
		return organizationLevel;
	}
	public void setOrganizationLevel(Integer organizationLevel) {
		this.organizationLevel = organizationLevel;
	}
	public Long getLedpDealer() {
		return ledpDealer;
	}
	public void setLedpDealer(Long ledpDealer) {
		this.ledpDealer = ledpDealer;
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
	public Date getBeginDate() {
		return beginDate;
	}
	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public Long getLedpMediaId() {
		return ledpMediaId;
	}
	public void setLedpMediaId(Long ledpMediaId) {
		this.ledpMediaId = ledpMediaId;
	}
	public int getSource() {
		return source;
	}
	public void setSource(int source) {
		this.source = source;
	}
	public Long getTypeId() {
		return typeId;
	}
	public void setTypeId(Long typeId) {
		this.typeId = typeId;
	}
	public Long getLedpFollowId() {
		return ledpFollowId;
	}
	public void setLedpFollowId(Long ledpFollowId) {
		this.ledpFollowId = ledpFollowId;
	}
	public String getSortName() {
		return sortName;
	}
	public void setSortName(String sortName) {
		this.sortName = sortName;
	}
	public String getSortOrder() {
		return sortOrder;
	}
	public void setSortOrder(String sortOrder) {
		this.sortOrder = sortOrder;
	}
	public Long getLedpIntentId() {
		return ledpIntentId;
	}
	public void setLedpIntentId(Long ledpIntentId) {
		this.ledpIntentId = ledpIntentId;
	}
}