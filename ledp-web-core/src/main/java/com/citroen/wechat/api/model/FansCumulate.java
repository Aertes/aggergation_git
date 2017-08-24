package com.citroen.wechat.api.model;

/**
 * Created by YuanSongMing on 2015/11/1114:04.
 */
public class FansCumulate {
    private String ref_date;     //数据的日期
    private String user_source;  //用户的渠道
    private String cumulate_user;//总用户量

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

    public String getCumulate_user() {
        return cumulate_user;
    }

    public void setCumulate_user(String cumulate_user) {
        this.cumulate_user = cumulate_user;
    }

    @Override
    public String toString() {
        return "FansCumulate{" +
                "ref_date='" + ref_date + '\'' +
                ", user_source='" + user_source + '\'' +
                ", cumulate_user='" + cumulate_user + '\'' +
                '}';
    }
}
