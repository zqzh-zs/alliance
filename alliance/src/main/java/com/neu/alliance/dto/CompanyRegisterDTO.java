package com.neu.alliance.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 企业注册请求 DTO
 * @Author 曦
 * @Date 2025/6/17
 */
@Data
public class CompanyRegisterDTO {
    /**
     * 企业名称
     */
    private String companyName;

    /**
     * 企业联系方式（电话或邮箱）
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String companyPhone;

    /**
     * 企业登录账号（用户名）
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 确认密码（用于前后端校验一致性）
     */
    private String confirmPassword;

    /**
     * 验证码
     */
    private String code;

}
