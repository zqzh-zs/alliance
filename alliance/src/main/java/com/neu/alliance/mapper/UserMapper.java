package com.neu.alliance.mapper;

import com.neu.alliance.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Title: UserMapper
 * @Author 曦
 * @Date 2025/6/16 23:10
 * @description:
 */
@Mapper
public interface UserMapper {
    /**
     * 根据用户名查询用户
     */
    User selectByUsername(String username);

    /**
     * 检查账号是否存在
     */
    int countByUsername(String username);

    /**
     * 插入新用户
     */
    int insert(User user);

    // 根据ID查找用户（用于获取用户详情）
    User selectById(Integer id);

    // 更新用户资料（基本信息修改）
    int update(User user);

    // 修改密码（旧密码校验）
    int updatePassword(Map<String, Object> param);

    // 新增：模糊查询用户列表（用户名、手机号、状态）
    List<User> queryUserList(Map<String, Object> map);

    // 用于所有用户信息的更新（管理员或本人）
    int updateAll(User user);

    int deleteById(@Param("id") Integer id);

}
