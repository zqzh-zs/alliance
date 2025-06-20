package com.neu.alliance.controller;

import com.neu.alliance.common.config.pojo.Result;
import com.neu.alliance.common.utils.JWTUtils;
import com.neu.alliance.dto.PasswordDTO;
import com.neu.alliance.entity.Admin;
import com.neu.alliance.service.AdminService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Title: AdminController
 * @Author 曦
 * @Date 2025/6/20 16:35
 * @description:
 */
@RestController
@RequestMapping("/admin")
@Slf4j
public class AdminController {
    @Autowired
    private AdminService adminService;

    // 获取当前用户信息
    @GetMapping("/info")
    public Result<Admin> getAdminInfo(HttpServletRequest request) {
        Integer adminId = JWTUtils.getUserId(request);
        if (adminId == null) return Result.fail("请先登录");
        Admin admin = adminService.getById(adminId);
        log.info("查询到用户信息: {}", admin);
        return Result.ok(admin);
    }

    // 更新用户基本资料
    @PutMapping("/update")
    public Result<?> updateAdminInfo(@RequestBody Admin admin, HttpServletRequest request) {
        Integer adminId = JWTUtils.getUserId(request);
        if (adminId == null) return Result.fail("请先登录");
        admin.setId(adminId); // 确保是本人操作

        log.info("收到用户更新请求: {}", admin);
        adminService.updateAdminInfo(admin);
        return Result.ok("更新成功");
    }
    // 修改密码
    @PutMapping("/password")
    public Result<?> updatePassword(@RequestBody PasswordDTO dto, HttpServletRequest request) throws Exception {
        Integer adminId = JWTUtils.getUserId(request);
        if (adminId == null) return Result.fail("请先登录");
        return adminService.changePassword(adminId, dto.getOldPassword(), dto.getNewPassword(), dto.getConfirmPassword());
    }
}
