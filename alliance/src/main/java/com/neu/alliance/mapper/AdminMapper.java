package com.neu.alliance.mapper;

import com.neu.alliance.entity.Admin;
import com.neu.alliance.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

/**
 * @Title: AdminMapper
 * @Author 曦
 * @Date 2025/6/18 9:43
 * @description:
 */
@Mapper
public interface AdminMapper {
    // 根据用户名查找用户（用于登录验证）
    Admin selectByUsername(String username);

    // 根据ID查找用户（用于获取用户详情）
    Admin selectById(Integer id);

    // 更新用户资料（基本信息修改）
    int updateAll(Admin admin);

    // 修改密码（旧密码校验）
    int updatePassword(Map<String, Object> param);

}