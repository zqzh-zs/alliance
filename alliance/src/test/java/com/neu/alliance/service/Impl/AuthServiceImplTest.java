package com.neu.alliance.service.Impl;

import com.neu.alliance.common.config.pojo.Result;
import com.neu.alliance.common.utils.SecurityUtils;
import com.neu.alliance.dto.CompanyRegisterDTO;
import com.neu.alliance.dto.LoginDTO;
import com.neu.alliance.entity.Company;
import com.neu.alliance.entity.User;
import com.neu.alliance.mapper.AdminMapper;
import com.neu.alliance.mapper.CompanyMapper;
import com.neu.alliance.mapper.UserMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @InjectMocks
    private AuthServiceImpl authService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private CompanyMapper companyMapper;

    @Mock
    private AdminMapper adminMapper;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpSession session;

    @BeforeEach
    void setup() {
        // 模拟 request.getSession() 返回我们 mock 的 session
        when(request.getSession()).thenReturn(session);
    }

    // ✅ 注册成功（验证码正确）
    @Test
    void testRegisterCompany_success_withValidCaptcha() throws Exception {
        CompanyRegisterDTO dto = new CompanyRegisterDTO();
        dto.setCompanyName("TestCompany");
        dto.setUsername("testuser");
        dto.setCompanyPhone("12345678901");
        dto.setPassword("123456");
        dto.setConfirmPassword("123456");
        dto.setCode("ABCD");

        when(session.getAttribute("captcha")).thenReturn("ABCD");
        when(companyMapper.countByCompanyName("TestCompany")).thenReturn(0);
        when(userMapper.selectByUsername("testuser")).thenReturn(null);
        when(companyMapper.insert(any())).thenAnswer(invocation -> {
            Company company = invocation.getArgument(0);
            company.setId(1); // 设置主键id，模拟插入成功
            return 1;
        });
        when(userMapper.insert(any())).thenReturn(1);

        Result<String> result = authService.registerCompany(dto);

        assertEquals(200, result.getCode());
        assertEquals("注册成功", result.getData());
    }

    // ❌ 验证码错误
    @Test
    void testRegisterCompany_wrongCaptcha() {
        CompanyRegisterDTO dto = new CompanyRegisterDTO();
        dto.setCompanyName("TestCompany");
        dto.setUsername("testuser");
        dto.setCompanyPhone("12345678901");
        dto.setPassword("123456");
        dto.setConfirmPassword("123456");
        dto.setCode("WRONG");

        when(session.getAttribute("captcha")).thenReturn("RIGHT");

        Result<String> result = authService.registerCompany(dto);

        assertEquals(500, result.getCode());
        assertEquals("验证码错误", result.getErrMsg());
    }

    // ❌ 公司名重复
    @Test
    void testRegisterCompany_duplicateCompanyName() {
        CompanyRegisterDTO dto = new CompanyRegisterDTO();
        dto.setCompanyName("ExistingCompany");
        dto.setUsername("newuser");
        dto.setCompanyPhone("12345678901");
        dto.setPassword("123456");
        dto.setConfirmPassword("123456");
        dto.setCode("ABCD");

        when(session.getAttribute("captcha")).thenReturn("ABCD");
        when(companyMapper.countByCompanyName("ExistingCompany")).thenReturn(1);

        Result<String> result = authService.registerCompany(dto);

        assertEquals(500, result.getCode());
        assertEquals("企业名称已存在", result.getErrMsg());
    }

    // ❌ 用户名已注册
    @Test
    void testRegisterCompany_usernameAlreadyExists() {
        CompanyRegisterDTO dto = new CompanyRegisterDTO();
        dto.setCompanyName("NewCompany");
        dto.setUsername("existingUser");
        dto.setCompanyPhone("12345678901");
        dto.setPassword("123456");
        dto.setConfirmPassword("123456");
        dto.setCode("ABCD");

        when(session.getAttribute("captcha")).thenReturn("ABCD");
        when(companyMapper.countByCompanyName("NewCompany")).thenReturn(0);
        when(userMapper.selectByUsername("existingUser")).thenReturn(new User());

        Result<String> result = authService.registerCompany(dto);

        assertEquals(500, result.getCode());
        assertEquals("账号已被注册", result.getErrMsg());
    }

    // ❌ 注册参数为空
    @Test
    void testRegisterCompany_missingFields() {
        CompanyRegisterDTO dto = new CompanyRegisterDTO();
        dto.setCompanyName(""); // 为空
        dto.setUsername("testuser");
        dto.setCompanyPhone("12345678901");
        dto.setPassword("123456");
        dto.setConfirmPassword("123456");
        dto.setCode("ABCD");

        when(session.getAttribute("captcha")).thenReturn("ABCD");

        Result<String> result = authService.registerCompany(dto);

        assertEquals(500, result.getCode());
        assertEquals("请填写完整信息", result.getErrMsg());
    }


    //登录成功
    @Test
    void testLoginSuccess_user() throws Exception {
        LoginDTO dto = new LoginDTO();
        dto.setUsername("testuser");
        dto.setPassword("123456");
        dto.setRole("USER");

        User user = new User();
        user.setUsername("testuser");
        user.setPassword(SecurityUtils.encrypt("123456"));
        user.setRole(2);
        user.setStatus(1);
        user.setId(1);
        user.setNickname("测试用户");

        when(userMapper.selectByUsername("testuser")).thenReturn(user);

        Result<Map<String, Object>> result = authService.login(dto);

        assertEquals(200, result.getCode());
        assertNotNull(result.getData().get("token"));
        assertEquals("testuser", result.getData().get("username"));
    }

    //登录失败，密码错误
    @Test
    void testLoginFail_wrongPassword() throws Exception {
        LoginDTO dto = new LoginDTO();
        dto.setUsername("testuser");
        dto.setPassword("wrongpassword");
        dto.setRole("USER");

        User user = new User();
        user.setUsername("testuser");
        user.setPassword(SecurityUtils.encrypt("123456"));
        user.setRole(2);
        user.setStatus(1);

        when(userMapper.selectByUsername("testuser")).thenReturn(user);

        Result<Map<String, Object>> result = authService.login(dto);
        assertEquals(500, result.getCode());
        assertTrue(result.getErrMsg().contains("密码错误"));
    }

    //登录失败，用户不存在
    @Test
    void testLoginFail_userNotExist() {
        LoginDTO dto = new LoginDTO();
        dto.setUsername("unknown");
        dto.setPassword("123456");
        dto.setRole("USER");

        when(userMapper.selectByUsername("unknown")).thenReturn(null);

        Result<Map<String, Object>> result = authService.login(dto);
        assertEquals(500, result.getCode());
        assertTrue(result.getErrMsg().contains("账号不存在"));
    }

    //登录失败，用户名或密码为空
    @Test
    void testLoginFail_emptyUsernameOrPassword() {
        LoginDTO dto = new LoginDTO();
        dto.setUsername(null);
        dto.setPassword(null);

        Result<Map<String, Object>> result = authService.login(dto);
        assertEquals(500, result.getCode());
        assertTrue(result.getErrMsg().contains("不能为空"));
    }



}
