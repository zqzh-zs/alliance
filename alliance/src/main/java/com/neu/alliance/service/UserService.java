package com.neu.alliance.service;

import com.github.pagehelper.PageInfo;
import com.neu.alliance.common.config.pojo.Result;
import com.neu.alliance.entity.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @Title: UserService
 * @Author 曦
 * @Date 2025/6/16 23:08
 * @description:
 */
@Service
public interface UserService {
    User getById(Integer id);

    void updateUserInfo(User user);

    Result<?> changePassword(Integer userId, String oldPwd, String newPwd, String confirmPwd) throws Exception;

    Result<?> createUser(User user) throws Exception;

    Result<?> updateUser(User user);

    PageInfo<User> queryUserList(Map<String, Object> params);

    Result<?> queryUserListWithPage(Map<String, Object> params);

    boolean deleteUserById(Integer id);

}

