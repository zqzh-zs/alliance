package com.neu.alliance.service;

import com.neu.alliance.common.config.pojo.Result;
import com.neu.alliance.dto.CompanyRegisterDTO;
import com.neu.alliance.dto.LoginDTO;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * @Title: AuthService
 * @Author 曦
 * @Date 2025/6/17 12:27
 * @description:
 */
@Service
public interface AuthService {
    /**
     * 企业注册
     */
    Result<String> registerCompany(CompanyRegisterDTO dto);


    /**
     * 用户登录
     */
    Result<Map<String, Object>> login(LoginDTO dto);

}
