package com.neu.alliance.service.Impl;
import cn.hutool.core.util.ObjectUtil;
import com.neu.alliance.common.config.pojo.Result;
import com.neu.alliance.common.utils.JWTUtils;
import com.neu.alliance.dto.CompanyRegisterDTO;
import com.neu.alliance.dto.LoginDTO;
import com.neu.alliance.entity.Admin;
import com.neu.alliance.entity.Code;
import com.neu.alliance.entity.Company;
import com.neu.alliance.entity.User;
import com.neu.alliance.common.utils.SecurityUtils;
import com.neu.alliance.mapper.AdminMapper;
import com.neu.alliance.mapper.CompanyMapper;
import com.neu.alliance.mapper.UserMapper;
import com.neu.alliance.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @Title: AuthServiceImpl
 * @Author 曦
 * @Date 2025/6/17 12:28
 * @description:
 */
@Slf4j
@Service
@Transactional
public class AuthServiceImpl implements AuthService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CompanyMapper companyMapper;

    @Autowired
    private AdminMapper adminMapper;


    @Autowired
    private HttpServletRequest request;


    @Override
    public Result<String> registerCompany(CompanyRegisterDTO dto) {
        log.info("开始注册企业，参数: companyName={}, username={}, companyPhone={}",
                dto.getCompanyName(), dto.getUsername(), dto.getCompanyPhone());

        // 1. 基础参数校验
        if (!StringUtils.hasLength(dto.getCompanyName()) ||
                !StringUtils.hasLength(dto.getUsername()) ||
                !StringUtils.hasLength(dto.getCompanyPhone()) ||
                !StringUtils.hasLength(dto.getConfirmPassword()) ||
                !StringUtils.hasLength(dto.getPassword()) ) {
            log.warn("注册失败，参数不完整");
            return Result.fail("请填写完整信息");
        }

        // 2. 企业名称是否存在
        int countCompany = companyMapper.countByCompanyName(dto.getCompanyName());
        log.info("查询企业名称重复数: {}", countCompany);
        if (countCompany > 0) {
            return Result.fail("企业名称已存在");
        }

        // 3. 用户名是否存在
        User existUser = userMapper.selectByUsername(dto.getUsername());
        log.info("查询用户名是否存在: {}", existUser != null);
        if (existUser != null) {
            return Result.fail("账号已被注册");
        }
        //验证码校验
        String sessionCode = (String) request.getSession().getAttribute(Code.RANDOMCODEKEY);
        if (dto.getCode() == null || !dto.getCode().equalsIgnoreCase(sessionCode)) {
            return Result.fail("验证码错误");
        }

        // 4. 插入企业信息
        Company company = new Company();
        company.setCompanyName(dto.getCompanyName());
        company.setContactPhone(dto.getCompanyPhone());
        company.setContactPerson(dto.getUsername());
        company.setCreateTime(LocalDateTime.now());
        company.setUpdateTime(LocalDateTime.now());

        int companyRow = companyMapper.insert(company);
        log.info("插入企业表结果：rows={}, companyId={}", companyRow, company.getId());

        if (companyRow <= 0 || company.getId() == null) {
            log.error("企业信息插入失败，回滚事务");
            throw new RuntimeException("企业注册失败");  // 抛异常触发事务回滚
        }

        // 5. 创建用户信息
        User user = new User();
        user.setUsername(dto.getUsername());
        try {
            user.setPassword(SecurityUtils.encrypt(dto.getPassword()));
        } catch (Exception e) {
            log.error("密码加密失败", e);
            throw new RuntimeException("密码加密失败");
        }
        user.setRole(2); // 企业用户
        user.setCompanyId(company.getId());
        user.setCompanyName(company.getCompanyName());
        user.setPhone(dto.getCompanyPhone());
        user.setNickname(dto.getUsername());
        user.setStatus(1);
        user.setCreateTime(LocalDateTime.now());

        int userRow = userMapper.insert(user);
        log.info("插入用户表结果：rows={}", userRow);

        if (userRow <= 0) {
            log.error("企业用户创建失败，回滚事务");
            throw new RuntimeException("企业用户创建失败");
        }

        log.info("企业注册成功");
        return Result.ok("注册成功");
    }

    @Override
    public Result<Map<String, Object>> login(LoginDTO dto) {
        if (dto.getUsername() == null || dto.getPassword() == null) {
            return Result.fail("账号或密码不能为空");
        }

        Integer loginRole = "ADMIN".equalsIgnoreCase(dto.getRole()) ? 1 : 2;

        // 管理员登录
        if (loginRole == 1) {
            Admin admin = adminMapper.selectByUsername(dto.getUsername());
            if (admin == null) return Result.fail("管理员账号不存在");

            if (admin.getStatus() == 0) return Result.fail("管理员账户已禁用");

            boolean isValid = SecurityUtils.verify(dto.getPassword(), admin.getPassword());
            if (!isValid) return Result.fail("密码错误");

            Map<String, Object> claims = new HashMap<>();
            claims.put("id", admin.getId());
            claims.put("username", admin.getUsername());
            claims.put("role", admin.getRole());

            String token = JWTUtils.getJWT(claims);

            Map<String, Object> result = new HashMap<>();
            result.put("token", token);
            result.put("role", admin.getRole());
            result.put("username", admin.getUsername());

            return Result.ok(result);
        }

        // 企业用户登录
        User user = userMapper.selectByUsername(dto.getUsername());
        if (user == null) return Result.fail("账号不存在");

        if (user.getStatus() == 0) return Result.fail("账户已被禁用");

        if (user.getRole() != 2) return Result.fail("角色不匹配");

        boolean isValid = SecurityUtils.verify(dto.getPassword(), user.getPassword());
        if (!isValid) return Result.fail("密码错误");

        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("username", user.getUsername());
        claims.put("role", user.getRole());
        claims.put("companyId", user.getCompanyId());
        claims.put("nickname", user.getNickname());

        String token = JWTUtils.getJWT(claims);

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("role", user.getRole());
        result.put("nickname", user.getNickname());
        result.put("username", user.getUsername());

        return Result.ok(result);
    }
}
