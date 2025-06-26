package com.zhu.androidalliance.pojo.response;

public class Response<T> {
    private int code;
    private String msg;
    private T data;

    public boolean isSuccess() {
        return code == 200;
    }

    public int getCode() { return code; }
    public String getMsg() { return msg; }
    public T getData() { return data; }
}

