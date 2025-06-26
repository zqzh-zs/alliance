package com.neu.alliance.service.Impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.neu.alliance.common.config.pojo.Result;
import com.neu.alliance.entity.User;
import com.neu.alliance.common.utils.SecurityUtils;
import com.neu.alliance.mapper.UserMapper;
import com.neu.alliance.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @Title: UserService
 * @Author 曦
 * @Date 2025/6/16 23:06
 * @description:
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User getById(Integer id) {
        return userMapper.selectById(id);
    }

    @Override
    public void updateUserInfo(User user) {
        userMapper.updateAll(user);
    }

    @Override
    public Result<?> changePassword(Integer userId, String oldPwd, String newPwd, String confirmPwd) throws Exception {
        if (!StringUtils.hasText(oldPwd) || !StringUtils.hasText(newPwd) || !newPwd.equals(confirmPwd)) {
            return Result.fail("请填写正确的密码信息");
        }
        // 获取当前用户信息
        User user = userMapper.selectById(userId);
        if (user == null) {
            return Result.fail("用户不存在");
        }
        // 验证旧密码是否正确
        if (!SecurityUtils.verify(oldPwd, user.getPassword())) {
            return Result.fail("旧密码错误");
        }
        // 加密新密码
        String newHashed = SecurityUtils.encrypt(newPwd);
        // 更新密码（注意：updatePassword 方法要只传 userId 和 newPassword）
        int rows = userMapper.updatePassword(Map.of(
                "userId", userId,
                "newPassword", newHashed
        ));
        return rows > 0 ? Result.ok("密码修改成功") : Result.fail("密码修改失败");
    }

    @Override
    public Result<?> createUser(User user) throws Exception {
        if (!StringUtils.hasText(user.getUsername())) {
            return Result.fail("用户名不能为空");
        }
        // 用户名是否存在
        if (userMapper.countByUsername(user.getUsername()) > 0) {
            return Result.fail("用户名已存在");
        }
        // 设置默认密码：123456（加密）
        if (!StringUtils.hasText(user.getPassword())) {
            user.setPassword("123456");
        }
        // 加密密码
        user.setPassword(SecurityUtils.encrypt(user.getPassword()));
        // 设置默认昵称为用户名
        if (!StringUtils.hasText(user.getNickname())) {
            user.setNickname(user.getUsername());
        }
        // 设置默认角色（2 = 企业用户）
        if (user.getRole() == null) {
            user.setRole(2);
        }
        // 设置默认状态（1 = 启用）
        if (user.getStatus() == null) {
            user.setStatus(1);
        }
        // 设置创建时间
        user.setCreateTime(LocalDateTime.now());
        // 插入用户
        userMapper.insert(user);
        return Result.ok("创建成功");
    }


    @Override
    public Result<?> updateUser(User user) {
        int rows = userMapper.updateAll(user);
        return rows > 0 ? Result.ok("更新成功") : Result.fail("更新失败");
    }

    @Override
    public PageInfo<User> queryUserList(Map<String, Object> params) {
        int page = Integer.parseInt(params.get("page").toString());
        int pageSize = Integer.parseInt(params.get("pageSize").toString());

        PageHelper.startPage(page, pageSize); // 启动分页

        List<User> list = userMapper.queryUserList(params); // 查询列表
        return new PageInfo<>(list); // 用 PageInfo 包装
    }


    @Override
    public Result<?> queryUserListWithPage(Map<String, Object> params) {
        int page = Integer.parseInt(params.getOrDefault("page", "1").toString());
        int pageSize = Integer.parseInt(params.getOrDefault("pageSize", "5").toString());
        PageHelper.startPage(page, pageSize);
        List<User> list = userMapper.queryUserList(params);
        return Result.ok(new PageInfo<>(list));
    }

    @Override
    public boolean deleteUserById(Integer id) {
        int rows = userMapper.deleteById(id);
        return rows > 0;
    }
}
