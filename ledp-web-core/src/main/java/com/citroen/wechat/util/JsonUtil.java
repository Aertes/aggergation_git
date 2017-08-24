package com.citroen.wechat.util;

import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.SimpleDateFormatSerializer;

public class JsonUtil {
    private static SerializeConfig mapping = new SerializeConfig();

    /**
     * 默认的处理时间
     * 
     * @param jsonText
     * @return
     */
    public static String toJSON(Object jsonText) {
        return JSON.toJSONString(jsonText,
                SerializerFeature.WriteDateUseDateFormat);
    }

    /**
     * 自定义时间格式
     * 
     * @param jsonText
     * @return
     */
    public static String toJSON(String dateFormat, Object jsonText) {
        mapping.put(Date.class, new SimpleDateFormatSerializer(dateFormat));
        return JSON.toJSONString(jsonText, mapping);
    }
    
    /**
     * string 解析成list
     * @param json
     * @param clazz
     * @return
     */
    public static List toList(String json, Class clazz) {

    	return JSONArray.parseArray(json, clazz);
    }
    
    public static void main(String[] args) {
		String str = "{'data':[{'date','2015-11-11','ct':1},{'date','2015-11-11','ct':2}]}";
		JSONArray arr = JSONArray.parseArray(str);
		System.out.println(arr.toJSONString());
	}
}