package com.citroen.wechat.form;

import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * Created by cyberoller on 2015/11/10.
 */
public class ReportFansQuery {
    private Integer searchDateType;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date searchDateBegin;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date searchDateEnd;
    private Long publicNoId;
    private Long orgId;
    private Long dealerId;

    public Integer getSearchDateType() {
        return searchDateType;
    }

    public void setSearchDateType(Integer searchDateType) {
        this.searchDateType = searchDateType;
    }

    public Date getSearchDateBegin() {
        return searchDateBegin;
    }

    public void setSearchDateBegin(Date searchDateBegin) {
        this.searchDateBegin = searchDateBegin;
    }

    public Date getSearchDateEnd() {
        return searchDateEnd;
    }

    public void setSearchDateEnd(Date searchDateEnd) {
        this.searchDateEnd = searchDateEnd;
    }

    public Long getPublicNoId() {
        return publicNoId;
    }

    public void setPublicNoId(Long publicNoId) {
        this.publicNoId = publicNoId;
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
}
