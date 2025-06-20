package com.neu.alliance.controller;
import com.neu.alliance.common.config.pojo.Result;
import com.neu.alliance.dto.CompanyRegisterDTO;
import com.neu.alliance.dto.LoginDTO;
import com.neu.alliance.common.enums.ResultStatusEnum;
import com.neu.alliance.common.utils.JWTUtils;
import com.neu.alliance.entity.Code;
import com.neu.alliance.service.Impl.AuthServiceImpl;
import com.neu.alliance.service.Impl.UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private AuthServiceImpl authService;

    /**
     * 企业注册接口
     */
    @PostMapping("/company/register")
    public Result<Map<String, Object>> companyRegister(@RequestBody @Valid CompanyRegisterDTO dto) {
        try {
            // 1. 参数校验
            if (dto.getUsername() == null || dto.getPassword() == null || dto.getCompanyName() == null) {
                return Result.fail("企业名称、账号和密码不能为空");
            }

            // 2. 调用注册服务
            Result<?> registerResult = authService.registerCompany(dto);
            if (registerResult.getCode() != ResultStatusEnum.SUCCESS.getCode()) {
                return Result.fail(registerResult.getErrMsg());
            }

            // 3. 注册成功后生成 JWT 令牌
            Map<String, Object> claims = new HashMap<>();
            claims.put("username", dto.getUsername());
            claims.put("role", 2);  // 企业用户
            claims.put("companyName", dto.getCompanyName());

            String jwtToken = JWTUtils.getJWT(claims);

            // 4. 构建响应数据
            Map<String, Object> resultData = new HashMap<>();
            resultData.put("token", jwtToken);
            resultData.put("message", "注册成功");

            return Result.ok(resultData);

        } catch (Exception e) {
            log.error("企业注册失败", e);

            return Result.fail("注册失败，请稍后再试");
        }
    }

    @GetMapping("/checkCode")
    public void checkcode(HttpServletRequest request, HttpServletResponse response){
        response.setContentType("image/jpeg");
        response.setDateHeader("Expires",0);
        response.setHeader("Pragma","no-cache");
        response.setHeader("Cache-Control","no-cache");
        //引用验证码工具类，生成验证码
        Code code=new Code();
        code.getValidateCode(request,response);
    }

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody @Valid LoginDTO dto) {
        return authService.login(dto);
    }
}
