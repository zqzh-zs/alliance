package com.zhu.androidalliance.utils;

import android.os.Handler;
import android.os.Looper;

import com.zhu.androidalliance.callback.JsonCallback;

import okhttp3.*;
import okhttp3.logging.HttpLoggingInterceptor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class OkHttpUtil {
    private static final HttpLoggingInterceptor httpLoggingInterceptor;
    private static final OkHttpClient client;
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    static {
        httpLoggingInterceptor = new HttpLoggingInterceptor();

        // 手动设置日志级别（生产环境应改为Level.NONE）
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        client = new OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    public static void doGet(String url, final JsonCallback callback) {
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                postToMainThread(() -> callback.onError("网络请求失败: " + e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful()) {
                        String errorMsg = "请求失败，状态码: " + response.code();
                        postToMainThread(() -> callback.onError(errorMsg));
                        return;
                    }

                    if (responseBody == null) {
                        postToMainThread(() -> callback.onError("响应体为空"));
                        return;
                    }

                    String json = responseBody.string();
                    postToMainThread(() -> callback.onSuccess(json));
                } catch (Exception e) {
                    postToMainThread(() -> callback.onError("解析响应失败: " + e.getMessage()));
                }
            }
        });
    }

    private static void postToMainThread(Runnable runnable) {
        new Handler(Looper.getMainLooper()).post(runnable);
    }


    // 新增 JSON 提交方法
    public static void doPostJson(String url, final JsonCallback callback, String jsonData) {
        RequestBody requestBody = RequestBody.create(jsonData, JSON);

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                postToMainThread(() -> callback.onError("网络请求失败: " + e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful()) {
                        String errorMsg = "请求失败，状态码: " + response.code();
                        postToMainThread(() -> callback.onError(errorMsg));
                        return;
                    }

                    if (responseBody == null) {
                        postToMainThread(() -> callback.onError("响应体为空"));
                        return;
                    }

                    String json = responseBody.string();
                    postToMainThread(() -> callback.onSuccess(json));
                } catch (Exception e) {
                    postToMainThread(() -> callback.onError("解析响应失败: " + e.getMessage()));
                }
            }
        });
    }

    public static void doPost(String url, final JsonCallback callback, HashMap<String, Object> params) {
        FormBody.Builder formBuilder = new FormBody.Builder();

        if (params != null) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                formBuilder.add(entry.getKey(), String.valueOf(entry.getValue()));
            }
        }

        RequestBody requestBody = formBuilder.build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                postToMainThread(() -> callback.onError("网络请求失败: " + e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful()) {
                        postToMainThread(() -> callback.onError("请求失败，状态码: " + response.code()));
                        return;
                    }

                    if (responseBody == null) {
                        postToMainThread(() -> callback.onError("响应体为空"));
                        return;
                    }

                    String json = responseBody.string();
                    postToMainThread(() -> callback.onSuccess(json));
                } catch (Exception e) {
                    postToMainThread(() -> callback.onError("解析响应失败: " + e.getMessage()));
                }
            }
        });
    }


}