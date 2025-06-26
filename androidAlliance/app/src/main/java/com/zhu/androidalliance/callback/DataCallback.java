package com.zhu.androidalliance.callback;

import com.zhu.androidalliance.pojo.dataObject.Meeting;

import java.util.List;

public interface DataCallback<T> {
    void onSuccess(List<T> list, int total);
    void onFailure(String error);
}
