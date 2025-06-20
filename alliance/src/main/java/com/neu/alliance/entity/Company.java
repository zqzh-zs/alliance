package com.neu.alliance.entity;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @Title: Company
 * @Author 曦
 * @Date 2025/6/16 22:41
 * @description:
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Company {
    private Integer id;              // 企业主键ID
    private String companyName;      // 企业名称
    private String contactPerson;    // 联系人姓名
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String contactPhone;     // 联系人电话
    private String contactEmail;     // 联系人邮箱
    private LocalDateTime createTime; // 创建时间
    private LocalDateTime updateTime; // 更新时间
}

