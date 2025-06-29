package com.zhu.androidalliance.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.zhu.androidalliance.adapter.DateAdapter;

import java.lang.reflect.Type;
import java.util.Date;

public class JsonUtil {
    private static final Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss") // 注册自定义适配器
            .create();

    /**
     * 从JSON字符串解析为指定类型的对象
     * @param json JSON字符串
     * @param type 目标对象的Type
     * @param <T> 目标对象类型
     * @return 解析后的对象
     */
    public static <T> T fromJson(String json, Type type) {
        try {
            return gson.fromJson(json, type);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

}