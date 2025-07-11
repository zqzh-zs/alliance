package com.neu.alliance.service.Impl;

import cn.hutool.core.util.ObjectUtil;
import com.neu.alliance.common.config.pojo.Result;
import com.neu.alliance.dto.CompanyRegisterDTO;
import com.neu.alliance.dto.LoginDTO;
import com.neu.alliance.entity.Admin;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
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

    private static final String CAPTCHA_KEY = "ValidateCode"; // 验证码key
    private static final String VALID_CAPTCHA = "ABCD";

    @BeforeEach
    void setup() {
        when(request.getSession()).thenReturn(session);
    }

    private CompanyRegisterDTO createCompanyRegisterDTO() {
        CompanyRegisterDTO dto = new CompanyRegisterDTO();
        dto.setCompanyName("TestCompany");
        dto.setUsername("testuser");
        dto.setCompanyPhone("12345678901");
        dto.setPassword("123456");
        dto.setConfirmPassword("123456");
        dto.setCode(VALID_CAPTCHA);
        return dto;
    }

    @Test
    void testRegisterCompany_success() {
        CompanyRegisterDTO dto = createCompanyRegisterDTO();

        when(session.getAttribute("ValidateCode")).thenReturn(VALID_CAPTCHA);
        when(companyMapper.countByCompanyName(dto.getCompanyName())).thenReturn(0);
        when(userMapper.selectByUsername(dto.getUsername())).thenReturn(null);

        doAnswer(invocation -> {
            Company company = invocation.getArgument(0);
            company.setId(1);
            return 1;
        }).when(companyMapper).insert(any(Company.class));

        when(userMapper.insert(any(User.class))).thenReturn(1);

        Result<String> result = authService.registerCompany(dto);

        assertEquals(200, result.getCode());
        assertEquals("注册成功", result.getData());
    }

    @Test
    void testRegisterCompany_missingFields() {
        CompanyRegisterDTO dto = createCompanyRegisterDTO();
        dto.setCompanyName("");

        when(session.getAttribute("ValidateCode")).thenReturn(VALID_CAPTCHA);

        Result<String> result = authService.registerCompany(dto);
        assertEquals(-1, result.getCode());
        assertEquals("请填写完整信息", result.getErrMsg());
    }

    @Test
    void testRegisterCompany_duplicateCompanyName() {
        CompanyRegisterDTO dto = createCompanyRegisterDTO();
        dto.setCompanyName("ExistingCompany");

        when(session.getAttribute("ValidateCode")).thenReturn(VALID_CAPTCHA);
        when(companyMapper.countByCompanyName(dto.getCompanyName())).thenReturn(1);

        Result<String> result = authService.registerCompany(dto);
        assertEquals(-1, result.getCode());
        assertEquals("企业名称已存在", result.getErrMsg());
    }

    @Test
    void testRegisterCompany_usernameAlreadyExists() {
        CompanyRegisterDTO dto = createCompanyRegisterDTO();
        dto.setUsername("existingUser");

        when(session.getAttribute("ValidateCode")).thenReturn(VALID_CAPTCHA);
        when(companyMapper.countByCompanyName(dto.getCompanyName())).thenReturn(0);
        when(userMapper.selectByUsername(dto.getUsername())).thenReturn(new User());

        Result<String> result = authService.registerCompany(dto);
        assertEquals(-1, result.getCode());
        assertEquals("账号已被注册", result.getErrMsg());
    }

    @Test
    void testRegisterCompany_wrongCaptcha() {
        CompanyRegisterDTO dto = createCompanyRegisterDTO();
        dto.setCode("WRONG");

        when(session.getAttribute("ValidateCode")).thenReturn(VALID_CAPTCHA);

        Result<String> result = authService.registerCompany(dto);
        assertEquals(-1, result.getCode());
        assertEquals("验证码错误", result.getErrMsg());
    }

    // ------------ 新增异常测试 ------------

    @Test
    void testRegisterCompany_companyInsertFailed() {
        CompanyRegisterDTO dto = createCompanyRegisterDTO();

        when(session.getAttribute("ValidateCode")).thenReturn(VALID_CAPTCHA);
        when(companyMapper.countByCompanyName(dto.getCompanyName())).thenReturn(0);
        when(userMapper.selectByUsername(dto.getUsername())).thenReturn(null);
        when(companyMapper.insert(any(Company.class))).thenReturn(0); // 插入失败

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            authService.registerCompany(dto);
        });
        assertEquals("企业注册失败", ex.getMessage());
    }

    @Test
    void testRegisterCompany_userInsertFailed() {
        CompanyRegisterDTO dto = createCompanyRegisterDTO();

        when(session.getAttribute("ValidateCode")).thenReturn(VALID_CAPTCHA);
        when(companyMapper.countByCompanyName(dto.getCompanyName())).thenReturn(0);
        when(userMapper.selectByUsername(dto.getUsername())).thenReturn(null);

        doAnswer(invocation -> {
            Company company = invocation.getArgument(0);
            company.setId(1);
            return 1;
        }).when(companyMapper).insert(any(Company.class));

        when(userMapper.insert(any(User.class))).thenReturn(0); // 插入失败

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            authService.registerCompany(dto);
        });
        assertEquals("企业用户创建失败", ex.getMessage());
    }

    @Test
    void testRegisterCompany_encryptPasswordFailed() {
        CompanyRegisterDTO dto = createCompanyRegisterDTO();

        when(session.getAttribute("ValidateCode")).thenReturn(VALID_CAPTCHA);
        when(companyMapper.countByCompanyName(dto.getCompanyName())).thenReturn(0);
        when(userMapper.selectByUsername(dto.getUsername())).thenReturn(null);

        doAnswer(invocation -> {
            Company company = invocation.getArgument(0);
            company.setId(1);
            return 1;
        }).when(companyMapper).insert(any(Company.class));

        // 静态mock SecurityUtils.encrypt 抛异常
        try (MockedStatic<com.neu.alliance.common.utils.SecurityUtils> utilities = mockStatic(com.neu.alliance.common.utils.SecurityUtils.class)) {
            utilities.when(() -> com.neu.alliance.common.utils.SecurityUtils.encrypt(anyString()))
                    .thenThrow(new RuntimeException("加密失败"));

            RuntimeException ex = assertThrows(RuntimeException.class, () -> {
                authService.registerCompany(dto);
            });
            assertEquals("密码加密失败", ex.getMessage());
        }
    }

    // ------------ login 相关测试 ------------

    @Test
    void testLoginSuccess_user() {
        LoginDTO dto = new LoginDTO();
        dto.setUsername("testuser");
        dto.setPassword("123456");
        dto.setRole("USER");

        String salt = "12345678901234567890123456789012";
        String encrypted = DigestUtils.md5DigestAsHex(("123456" + salt).getBytes(StandardCharsets.UTF_8));
        String dbPassword = salt + encrypted;

        User user = new User();
        user.setUsername("testuser");
        user.setPassword(dbPassword);
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

    @Test
    void testLoginFail_wrongPassword() {
        LoginDTO dto = new LoginDTO();
        dto.setUsername("testuser");
        dto.setPassword("wrongpassword");
        dto.setRole("USER");

        String salt = "12345678901234567890123456789012";
        String encrypted = DigestUtils.md5DigestAsHex(("123456" + salt).getBytes(StandardCharsets.UTF_8));
        String dbPassword = salt + encrypted;

        User user = new User();
        user.setUsername("testuser");
        user.setPassword(dbPassword);
        user.setRole(2);
        user.setStatus(1);

        when(userMapper.selectByUsername("testuser")).thenReturn(user);

        Result<Map<String, Object>> result = authService.login(dto);
        assertEquals(-1, result.getCode());
        assertEquals("密码错误", result.getErrMsg());
    }

    @Test
    void testLoginFail_userNotExist() {
        LoginDTO dto = new LoginDTO();
        dto.setUsername("unknown");
        dto.setPassword("123456");
        dto.setRole("USER");

        when(userMapper.selectByUsername("unknown")).thenReturn(null);

        Result<Map<String, Object>> result = authService.login(dto);
        assertEquals(-1, result.getCode());
        assertEquals("账号不存在", result.getErrMsg());
    }

    @Test
    void testLoginFail_emptyUsernameOrPassword() {
        LoginDTO dto = new LoginDTO();
        dto.setUsername(null);
        dto.setPassword(null);

        Result<Map<String, Object>> result = authService.login(dto);
        assertEquals(-1, result.getCode());
        assertTrue(result.getErrMsg().contains("不能为空"));
    }

    @Test
    void testLoginSuccess_admin() {
        LoginDTO dto = new LoginDTO();
        dto.setUsername("adminuser");
        dto.setPassword("adminpass");
        dto.setRole("ADMIN");

        String salt = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
        String encrypted = DigestUtils.md5DigestAsHex(("adminpass" + salt).getBytes(StandardCharsets.UTF_8));
        String dbPassword = salt + encrypted;

        Admin admin = new Admin();
        admin.setUsername("adminuser");
        admin.setPassword(dbPassword);
        admin.setRole(1);
        admin.setStatus(1);
        admin.setId(99);

        when(adminMapper.selectByUsername("adminuser")).thenReturn(admin);

        Result<Map<String, Object>> result = authService.login(dto);
        assertEquals(200, result.getCode());
        assertNotNull(result.getData().get("token"));
        assertEquals("adminuser", result.getData().get("username"));
    }

    @Test
    void testLoginFail_adminNotExist() {
        LoginDTO dto = new LoginDTO();
        dto.setUsername("adminX");
        dto.setPassword("123");
        dto.setRole("ADMIN");

        when(adminMapper.selectByUsername("adminX")).thenReturn(null);
        Result<Map<String, Object>> result = authService.login(dto);
        assertEquals(-1, result.getCode());
        assertEquals("管理员账号不存在", result.getErrMsg());
    }

    @Test
    void testLoginFail_adminDisabled() {
        LoginDTO dto = new LoginDTO();
        dto.setUsername("admin");
        dto.setPassword("123");
        dto.setRole("ADMIN");

        Admin admin = new Admin();
        admin.setStatus(0);
        when(adminMapper.selectByUsername("admin")).thenReturn(admin);

        Result<Map<String, Object>> result = authService.login(dto);
        assertEquals(-1, result.getCode());
        assertEquals("管理员账户已禁用", result.getErrMsg());
    }

    @Test
    void testLoginFail_adminWrongPassword() {
        LoginDTO dto = new LoginDTO();
        dto.setUsername("admin");
        dto.setPassword("wrong");
        dto.setRole("ADMIN");

        Admin admin = new Admin();
        admin.setStatus(1);
        admin.setPassword("correctEncryptedPassword");
        when(adminMapper.selectByUsername("admin")).thenReturn(admin);

        try (MockedStatic<com.neu.alliance.common.utils.SecurityUtils> utilities = mockStatic(com.neu.alliance.common.utils.SecurityUtils.class)) {
            utilities.when(() -> com.neu.alliance.common.utils.SecurityUtils.verify("wrong", "correctEncryptedPassword"))
                    .thenReturn(false);

            Result<Map<String, Object>> result = authService.login(dto);
            assertEquals(-1, result.getCode());
            assertEquals("密码错误", result.getErrMsg());
        }
    }
}
