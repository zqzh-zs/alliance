package com.neu.alliance.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result {
    private String code;
    private String msg;
    private Object data;

    // 成功返回
    public static Result success(Object data) {
        return new Result("200", "操作成功", data);
    }

    // 成功返回（无数据）
    public static Result success() {
        return new Result("201", "操作成功", null);
    }

    // 失败返回
    public static Result error(String msg) {
        return new Result("500", msg, null);
    }
}
