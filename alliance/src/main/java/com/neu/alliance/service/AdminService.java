package com.neu.alliance.service;

import com.neu.alliance.common.config.pojo.Result;
import com.neu.alliance.entity.Admin;
import com.neu.alliance.entity.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @Title: AdminService
 * @Author 曦
 * @Date 2025/6/20 16:21
 * @description:
 */
@Service
public interface AdminService {
    Admin getById(Integer id);

    void updateAdminInfo(Admin admin);

    Result<?> changePassword(Integer userId, String oldPwd, String newPwd, String confirmPwd) throws Exception;


}
