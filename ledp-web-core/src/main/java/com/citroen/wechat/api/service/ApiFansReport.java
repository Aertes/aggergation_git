package com.citroen.wechat.api.service;

import com.citroen.wechat.api.model.FansCumulate;
import com.citroen.wechat.api.model.FansSummary;
import com.citroen.wechat.api.model.Final;
import com.citroen.wechat.api.util.HttpUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * Created by YuanSongMing on 2015/11/1113:09.
 */
public class ApiFansReport {
    private static Logger logger = Logger.getLogger(ApiFansReport.class);

    /**
     * 获取用户增减数据
     *
     * @return
     */
    public static List<FansSummary> getUserSummary(String access_token, String beginDate, String endDate) throws ApiException {
        if (StringUtils.isBlank(beginDate) || StringUtils.isBlank(endDate)) {
            throw new ApiException("开始时间和结束时间都不能为空");
        }
        // 拼装参数
        String params = "{\"begin_date\":\"" + beginDate + "\",\"end_date\":\"" + endDate + "\"}";
        String uri = Final.USER_SUMMARY + access_token;
        JSONObject json;
        try {
            json = HttpUtil.doPost(uri, params);
            logger.debug(beginDate + " 新增取消粉丝情况：" + json.toString());
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApiException(e.getCause());
        }
        // 检查是否有错误信息
        if (json.containsKey("errcode")) {
            throw new ApiException(json.getInt("errcode"), json.getString("errmsg"));
        }
        JSONArray jsonObject = json.getJSONArray("list");
        return (List<FansSummary>) JSONArray.toCollection(jsonObject, FansSummary.class);
    }

    /**
     * 获取累计用户数据
     *
     * @return
     */
    public static List<FansCumulate> getUserCumulate(String access_token, String beginDate, String endDate) throws ApiException {
        if (StringUtils.isBlank(beginDate) || StringUtils.isBlank(endDate)) {
            throw new ApiException("开始时间和结束时间都不能为空");
        }
        // 拼装参数
        String params = "{\"begin_date\":\"" + beginDate + "\",\"end_date\":\"" + endDate + "\"}";
        String uri = Final.USER_CUMULATE + access_token;
        JSONObject json;
        try {
            json = HttpUtil.doPost(uri, params);
            logger.debug(beginDate + " 总粉丝情况：" + json.toString());
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApiException(e.getCause());
        }
        // 检查是否有错误信息
        if (json.containsKey("errcode")) {
            throw new ApiException(json.getInt("errcode"), json.getString("errmsg"));
        }
        JSONArray jsonObject = json.getJSONArray("list");
        return (List<FansCumulate>) JSONArray.toCollection(jsonObject, FansCumulate.class);
    }
}
