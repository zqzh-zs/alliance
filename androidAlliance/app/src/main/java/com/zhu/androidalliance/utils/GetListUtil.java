package com.zhu.androidalliance.utils;

import android.text.TextUtils;
import com.google.gson.reflect.TypeToken;
import com.zhu.androidalliance.callback.DataCallback;
import com.zhu.androidalliance.callback.JsonCallback;
import com.zhu.androidalliance.common.Commons;
import com.zhu.androidalliance.enums.MeetingType;
import com.zhu.androidalliance.pojo.dataObject.NewsInfo;
import com.zhu.androidalliance.pojo.response.Response;
import com.zhu.androidalliance.pojo.response.MeetingListData;
import com.zhu.androidalliance.pojo.response.NewsListData;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class GetListUtil {
    private static final String MEETINGS_API = "/api/meetings";
    private static final String NEWS_API = "/api/latestNews"; // 修正新闻API路径

    public static void getMeetingsByType(int page, int size, MeetingType type, DataCallback callback) {
        String apiUrl = Commons.BASE_HOST + MEETINGS_API +
                "?page=" + page +
                "&pageSize=" + size +
                "&type=" + type.name();
        sendRequestForMeeting(apiUrl, callback);
    }

    private static void sendRequestForMeeting(String url, DataCallback callback) {
        OkHttpUtil.doGet(url, new JsonCallback() {
            @Override
            public void onSuccess(String json) {
                try {
                    // 使用TypeToken处理泛型
                    Type responseType = new TypeToken<Response<MeetingListData>>(){}.getType();
                    Response<MeetingListData> response = JsonUtil.fromJson(json, responseType);

                    if (response != null && response.isSuccess() && response.getData() != null) {
                        MeetingListData data = response.getData();
                        callback.onSuccess(data.getList(), data.getTotal());
                    } else {
                        String errorMsg = (response != null) ? response.getMsg() : "响应为空";
                        callback.onFailure("请求错误: " + errorMsg);
                    }
                } catch (Exception e) {
                    callback.onFailure("数据解析错误: " + e.getMessage());
                }
            }

            @Override
            public void onError(String errorMsg) {
                callback.onFailure("网络请求失败: " + errorMsg);
            }
        });
    }

    public static void getAllMeetings(DataCallback callback) {
        String url = Commons.BASE_HOST + MEETINGS_API;
        sendRequestForMeeting(url, callback);
    }

    public static void getLatestNews(DataCallback callback) {
        String apiUrl = Commons.BASE_HOST + NEWS_API;
        sendRequestForNews(apiUrl, callback);
    }

    private static void sendRequestForNews(String apiUrl, DataCallback callback) {
        OkHttpUtil.doGet(apiUrl, new JsonCallback() {
            @Override
            public void onSuccess(String json) {
                try {
                    // 使用TypeToken处理泛型
                    Type responseType = new TypeToken<Response<NewsListData>>(){}.getType();
                    Response<NewsListData> response = JsonUtil.fromJson(json, responseType);

                    if (response != null && response.isSuccess() && response.getData() != null) {
                        NewsListData data = response.getData();
                        callback.onSuccess(data.getList(), data.getTotal());
                    } else {
                        String errorMsg = (response != null) ? response.getMsg() : "响应为空";
                        callback.onFailure("请求错误: " + errorMsg);
                    }
                } catch (Exception e) {
                    callback.onFailure("数据解析错误: " + e.getMessage());
                }
            }

            @Override
            public void onError(String errorMsg) {
                callback.onFailure("网络请求失败: " + errorMsg);
            }
        });
    }

    public static void getNewsList(int page, int size, String keyword, DataCallback callback) {
        String encodedKeyword = "";
        if (!TextUtils.isEmpty(keyword)) {
            try {
                encodedKeyword = URLEncoder.encode(keyword, StandardCharsets.UTF_8.name());
            } catch (UnsupportedEncodingException e) {
                callback.onFailure("关键词编码失败: " + e.getMessage());
                return;
            }
        }

        String apiUrl = Commons.BASE_HOST + NEWS_API +
                "?page=" + page +
                "&pageSize=" + size +
                "&keyword=" + encodedKeyword;

        sendRequestForNews(apiUrl, callback);
    }
}