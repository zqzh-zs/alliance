package com.neu.alliance.dto;

import lombok.Data;

/**
 * @Title: PasswordDTO
 * @Author 曦
 * @Date 2025/6/19 17:39
 * @description:修改密码 DTO
 */

@Data
public class PasswordDTO {
    private String oldPassword;
    private String newPassword;
    private String confirmPassword;
}
