package com.neu.alliance.common.config.pojo;

import com.neu.alliance.common.enums.ResultStatusEnum;
import lombok.Data;


@Data
public class Result <T>{
    int code;
    String errMsg;
    T data;

    public static <T> Result<T> ok(T data){
        Result<T> result = new Result<>();
        result.setCode(ResultStatusEnum.SUCCESS.getCode());
        result.setErrMsg("");
        result.setData(data);
        return result;
    }

    public static <T> Result<T> fail(T data,String errorMsg){
        Result<T> result = new Result<>();
        result.setCode(ResultStatusEnum.FAILED.getCode());
        result.setErrMsg(errorMsg);
        result.setData(data);
        return result;
    }
    public static <T> Result<T> fail(String errorMsg){
        Result<T> result = new Result<>();
        result.setCode(ResultStatusEnum.FAILED.getCode());
        result.setErrMsg(errorMsg);
        return result;
    }

    public static <T> Result<T> fail(T data){
        Result<T> result = new Result<>();
        result.setCode(ResultStatusEnum.FAILED.getCode());
        result.setErrMsg("");
        result.setData(data);
        return result;
    }

}
