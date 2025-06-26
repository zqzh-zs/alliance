package com.zhu.androidalliance.callback;

public interface JsonCallback {
    void onSuccess(String json);
    void onError(String errorMsg);
}