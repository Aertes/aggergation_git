package com.citroen.wechat.form;
        ;

import com.citroen.ledp.domain.Dealer;
import com.citroen.ledp.domain.Leads;
import com.citroen.wechat.domain.Campaign;
import com.citroen.wechat.domain.CampaignLeads;
import com.citroen.wechat.domain.Site;

/**
 * 提交留资form
 * @author hhs
 * @date 2015-11-09 18:41:20
 *
 */
public class SubmitLeadsForm {
    private String name;
    private String uuid;//用户唯一标识
    private String sex;
    private String phone;
    private String unionCode;//优惠券编码
    private long awardId;//奖品id
    private long pageId;//页面id
    private long pluginId;//插件id
    private long vehicleCode;//车型id
    private String state;//测试标识
    private Leads leads;//留资
    private CampaignLeads entity;
    private Dealer dealer;
    private Campaign campaign;
    private Site site;
    private long yixiangchexing;//团购意向车型

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getSex() {
        return sex;
    }
    public void setSex(String sex) {
        this.sex = sex;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getUnionCode() {
        return unionCode;
    }
    public void setUnionCode(String unionCode) {
        this.unionCode = unionCode;
    }
    public long getVehicleCode() {
        return vehicleCode;
    }
    public void setVehicleCode(long vehicleCode) {
        this.vehicleCode = vehicleCode;
    }
    public long getAwardId() {
        return awardId;
    }
    public void setAwardId(long awardId) {
        this.awardId = awardId;
    }
    public long getPageId() {
        return pageId;
    }
    public void setPageId(long pageId) {
        this.pageId = pageId;
    }
    public long getPluginId() {
        return pluginId;
    }
    public void setPluginId(long pluginId) {
        this.pluginId = pluginId;
    }
    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }
    public String getUuid() {
        return uuid;
    }
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
    public long getYixiangchexing() {
        return yixiangchexing;
    }
    public void setYixiangchexing(long yixiangchexing) {
        this.yixiangchexing = yixiangchexing;
    }
}