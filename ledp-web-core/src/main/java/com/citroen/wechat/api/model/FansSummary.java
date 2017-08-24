package com.citroen.wechat.api.model;

/**
 * Created by YuanSongMing on 2015/11/1114:04.
 */
public class FansSummary {
    private String ref_date;     //数据的日期
    private String user_source;  //用户的渠道
    private String new_user;     //新增的用户数量
    private String cancel_user;  //取消关注的用户数量

    public String getRef_date() {
        return ref_date;
    }

    public void setRef_date(String ref_date) {
        this.ref_date = ref_date;
    }

    public String getUser_source() {
        return user_source;
    }

    public void setUser_source(String user_source) {
        this.user_source = user_source;
    }

    public String getNew_user() {
        return new_user;
    }

    public void setNew_user(String new_user) {
        this.new_user = new_user;
    }

    public String getCancel_user() {
        return cancel_user;
    }

    public void setCancel_user(String cancel_user) {
        this.cancel_user = cancel_user;
    }

    @Override
    public String toString() {
        return "FansSummary{" +
                "ref_date='" + ref_date + '\'' +
                ", user_source='" + user_source + '\'' +
                ", new_user='" + new_user + '\'' +
                ", cancel_user='" + cancel_user + '\'' +
                '}';
    }
}
