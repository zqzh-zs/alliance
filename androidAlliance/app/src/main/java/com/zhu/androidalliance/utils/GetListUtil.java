package com.zhu.androidalliance.utils;

import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.zhu.androidalliance.adapter.DateAdapter;
import com.zhu.androidalliance.callback.DataCallback;
import com.zhu.androidalliance.callback.JsonCallback;
import com.zhu.androidalliance.common.Commons;
import com.zhu.androidalliance.enums.MeetingType;

import com.zhu.androidalliance.pojo.response.MeetingListData;
import com.zhu.androidalliance.pojo.response.NewsListData;
import com.zhu.androidalliance.pojo.response.Result;


import java.lang.reflect.Type;
import java.util.Date;


public class GetListUtil {
    private static final String MEETINGS_API = Commons.BASE_HOST+ "/api/meeting";
    private static final String NEWS_API = Commons.BASE_HOST+"/news/getNews";

    public static void getMeetingsByType(int page, int size, MeetingType type, DataCallback callback) {
        String apiUrl = MEETINGS_API +
                "/mobile/search?page=" + page +
                "&pageSize=" + size +
                "&type=" + type.name();
        sendRequestForMeeting(apiUrl, callback);
    }
    private static void sendRequestForMeeting(String url, DataCallback callback) {
        OkHttpUtil.doGet(url, new JsonCallback() {
            @Override
            public void onSuccess(String json) {
                // 在后台线程解析JSON
                new Thread(() -> {
                    try {
                        Type responseType = new TypeToken<Result<MeetingListData>>(){}.getType();
                        Result<MeetingListData> res = JsonUtil.fromJson(json, responseType);

                        if (res != null && res.getCode() == 200 && res.getData() != null) {
                            MeetingListData data = res.getData();
                            // 切换到主线程回调
                            new Handler(Looper.getMainLooper()).post(() ->
                                    callback.onSuccess(data.getList(), data.getTotal())
                            );
                        } else if(res != null) {
                            new Handler(Looper.getMainLooper()).post(() ->
                                    callback.onFailure("响应失败: "+res.getErrMsg())
                            );
                        }else{
                            new Handler(Looper.getMainLooper()).post(() ->
                                    callback.onFailure("响应为null")
                            );
                        }
                    } catch (Exception e) {
                        new Handler(Looper.getMainLooper()).post(() ->
                                callback.onFailure("数据解析错误: " + e.getMessage())
                        );
                    }
                }).start();
            }

            @Override
            public void onError(String errorMsg) {
                // 已经在主线程
                callback.onFailure("网络请求失败: " + errorMsg);
            }
        });
    }


    public static void getAllMeetings( DataCallback callback) {
        String url =  MEETINGS_API+"/searchAll";
        sendRequestForMeeting(url, callback);
    }

    public static void getLatestNews(DataCallback callback) {
        getNewsList(1,10,callback);
    }

    private static void sendRequestForNews(String apiUrl, DataCallback callback) {
        OkHttpUtil.doGet(apiUrl, new JsonCallback() {
            @Override
            public void onSuccess(String json) {
                try {
                    // 使用TypeToken处理泛型
                    Gson gson = new GsonBuilder()
                            .registerTypeAdapter(Date.class, new DateAdapter()) // 注册自定义适配器
                            .create();
                    Type responseType = new TypeToken<Result<NewsListData>>(){}.getType();
                    Result<NewsListData> res = gson.fromJson(json,responseType);
                    if (res != null && res.getCode() == 200 && res.getData() != null) {
                        NewsListData data = res.getData();
                        new Handler(Looper.getMainLooper()).post(() ->
                                callback.onSuccess(data.getList(), data.getTotal())
                        );
                    } else if(res != null) {
                        new Handler(Looper.getMainLooper()).post(() ->
                                callback.onFailure("响应失败: "+res.getErrMsg())
                        );
                    }else{
                        new Handler(Looper.getMainLooper()).post(() ->
                                callback.onFailure("响应为null")
                        );
                    }
                } catch (Exception e) {
                    new Handler(Looper.getMainLooper()).post(() ->
                            callback.onFailure("数据解析错误: " + e.getMessage())
                    );
                }
            }

            @Override
            public void onError(String errorMsg) {
                callback.onFailure("网络请求失败: " + errorMsg);
            }
        });
    }

    public static void getNewsList(int page, int size, DataCallback callback) {
        String apiUrl = NEWS_API +
                "?page=" + page +
                "&pageSize=" + size;
        sendRequestForNews(apiUrl, callback);
    }
}