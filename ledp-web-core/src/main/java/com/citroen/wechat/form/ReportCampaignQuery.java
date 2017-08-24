package com.citroen.wechat.form;

import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * Created by cyberoller on 2015/11/3.
 */
public class ReportCampaignQuery {
    private Long cmpaignId;         // 活动ID
    private Long dealerId;          // 网点ID
    private Long orgId;             // 大区ID
    private Long pluginTypeId;      // 插件类型ID
    private Long pluginId;          // 插件ID
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date searchDateBegin;   // 开始时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date searchDateEnd;     // 结束时间
    private Integer searchDateType;    // 时间类型
    private int pageSize;			//每页条数
    private int currentPage;        //当前页
    private String name;			//名称
    private String phone;			//手机
    public Long getCmpaignId() {
        return cmpaignId;
    }

    public void setCmpaignId(Long cmpaignId) {
        this.cmpaignId = cmpaignId;
    }

    public Long getDealerId() {
        return dealerId;
    }

    public void setDealerId(Long dealerId) {
        this.dealerId = dealerId;
    }

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    public Long getPluginTypeId() {
        return pluginTypeId;
    }

    public void setPluginTypeId(Long pluginTypeId) {
        this.pluginTypeId = pluginTypeId;
    }

    public Long getPluginId() {
        return pluginId;
    }

    public void setPluginId(Long pluginId) {
        this.pluginId = pluginId;
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

    public Integer getSearchDateType() {
        return searchDateType;
    }

    public void setSearchDateType(Integer searchDateType) {
        this.searchDateType = searchDateType;
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
}
