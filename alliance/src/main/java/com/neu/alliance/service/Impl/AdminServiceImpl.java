package com.neu.alliance.service.Impl;

import com.neu.alliance.common.config.pojo.Result;
import com.neu.alliance.entity.Admin;
import com.neu.alliance.mapper.AdminMapper;
import com.neu.alliance.service.AdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Title: AdminServiceImpl
 * @Author 曦
 * @Date 2025/6/20 16:22
 * @description:
 */
@Service
@Slf4j
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminMapper adminMapper;

    @Override
    public Admin getById(Integer id) {
        return adminMapper.selectById(id);
    }

    @Override
    public void updateAdminInfo(Admin admin) {
        adminMapper.updateAll(admin);
    }

    @Override
    public Result<?> changePassword(Integer userId, String oldPwd, String newPwd, String confirmPwd) throws Exception {
        Admin admin = adminMapper.selectById(userId);
        if (admin == null) {
            return Result.fail("用户不存在");
        }

        if (!admin.getPassword().equals(oldPwd)) {
            return Result.fail("原密码错误");
        }

        if (!newPwd.equals(confirmPwd)) {
            return Result.fail("两次输入的新密码不一致");
        }

        admin.setPassword(newPwd);
        adminMapper.updateAll(admin);
        return Result.ok(null);
    }

}
