package com.neu.alliance.controller;

import com.github.pagehelper.PageInfo;
import com.neu.alliance.common.config.pojo.Result;
import com.neu.alliance.dto.PasswordDTO;
import com.neu.alliance.entity.User;
import com.neu.alliance.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.neu.alliance.common.utils.JWTUtils;

import java.util.Map;

/**
 * @Title: UserController
 * @Author 曦
 * @Date 2025/6/19 17:39
 * @description:
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    // 获取当前用户信息
    @GetMapping("/info")
    public Result<User> getUserInfo(HttpServletRequest request) {
        Integer userId = JWTUtils.getUserId(request);
        if (userId == null) return Result.fail("请先登录");
        User user = userService.getById(userId);
        log.info("查询到用户信息: {}", user);
        return Result.ok(user);
    }

    // 更新用户基本资料
    @PutMapping("/update")
    public Result<?> updateUserInfo(@RequestBody User user, HttpServletRequest request) {
        Integer userId = JWTUtils.getUserId(request);
        if (userId == null) return Result.fail("请先登录");
        user.setId(userId); // 确保是本人操作

        log.info("收到用户更新请求: {}", user);
        userService.updateUserInfo(user);
        return Result.ok("更新成功");
    }
    // 修改密码
    @PutMapping("/password")
    public Result<?> updatePassword(@RequestBody PasswordDTO dto, HttpServletRequest request) throws Exception {
        Integer userId = JWTUtils.getUserId(request);
        if (userId == null) return Result.fail("请先登录");


        return userService.changePassword(userId, dto.getOldPassword(), dto.getNewPassword(), dto.getConfirmPassword());
    }


    //查看用户列表
    @GetMapping("/selectAll")
    public Result<?> list(@RequestParam Map<String, Object> params) {
        PageInfo<User> pageInfo = userService.queryUserList(params);
        return Result.ok(pageInfo);
    }

    //新增
    @PostMapping("/add")
    public Result<?> add(@RequestBody User user) throws Exception {
        return userService.createUser(user);
    }

    //修改
    @PutMapping("/update/{id}")
    public Result<?> update(@PathVariable Integer id, @RequestBody User user) {
        user.setId(id);
        return userService.updateUser(user);
    }

    // 删除用户接口
    @DeleteMapping("/delete/{id}")
    public Result<?> deleteUser(@PathVariable Integer id) {
        boolean success = userService.deleteUserById(id);
        if (success) {
            return Result.ok("删除成功");
        } else {
            return Result.fail("删除失败，用户不存在或已被删除");
        }
    }
}