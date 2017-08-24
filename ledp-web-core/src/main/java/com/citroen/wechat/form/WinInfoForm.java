package com.citroen.wechat.form;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

public class WinInfoForm {
    private String name;
    private String phone;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date beginDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date endDate;
    private Integer status;
    private Integer sourceType;
    private int currentPage;
    private int pageSize;
    private Long org;
    private Long dealer;
    private String userCode;

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
    public int getCurrentPage() {
        return currentPage;
    }
    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }
    public int getPageSize() {
        return pageSize;
    }
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
    public Integer getStatus() {
        return status;
    }
    public void setStatus(Integer status) {
        this.status = status;
    }
    public Long getOrg() {
        return org;
    }
    public void setOrg(Long org) {
        this.org = org;
    }
    public Long getDealer() {
        return dealer;
    }
    public void setDealer(Long dealer) {
        this.dealer = dealer;
    }
    public Integer getSourceType() {
        return sourceType;
    }
    public void setSourceType(Integer sourceType) {
        this.sourceType = sourceType;
    }
    public String getUserCode() {
        return userCode;
    }
    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }
}