package com.zhu.androidalliance.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import java.lang.reflect.Type;

public class JsonUtil {
    private static final Gson gson = new GsonBuilder().create();

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

    /**
     * 从JSON字符串解析为指定Class的对象
     * @param json JSON字符串
     * @param clazz 目标对象的Class
     * @param <T> 目标对象类型
     * @return 解析后的对象
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return gson.fromJson(json, clazz);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }
}