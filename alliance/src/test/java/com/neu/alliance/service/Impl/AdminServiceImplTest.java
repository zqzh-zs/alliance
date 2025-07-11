package com.neu.alliance.service.Impl;

import com.neu.alliance.common.config.pojo.Result;
import com.neu.alliance.common.enums.ResultStatusEnum;
import com.neu.alliance.entity.Admin;
import com.neu.alliance.mapper.AdminMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * AdminServiceImpl单元测试
 */
@ExtendWith(MockitoExtension.class)
class AdminServiceImplTest {

    @InjectMocks
    private AdminServiceImpl adminService;

    @Mock
    private AdminMapper adminMapper;

    // 测试 getById
    @Test
    void testGetById_ReturnsAdmin() {
        Admin admin = new Admin();
        admin.setId(100);
        admin.setUsername("adminUser");

        when(adminMapper.selectById(100)).thenReturn(admin);

        Admin result = adminService.getById(100);
        assertNotNull(result);
        assertEquals("adminUser", result.getUsername());

        verify(adminMapper, times(1)).selectById(100);
    }

    // 测试 updateAdminInfo，调用updateAll
    @Test
    void testUpdateAdminInfo_CallsUpdateAll() {
        Admin admin = new Admin();
        admin.setId(100);
        admin.setUsername("adminUser");

        adminService.updateAdminInfo(admin);

        verify(adminMapper, times(1)).updateAll(admin);
    }

    // ---------- changePassword ----------

    // 1. 用户不存在
    @Test
    void testChangePassword_AdminNotFound() throws Exception {
        when(adminMapper.selectById(1)).thenReturn(null);

        Result<?> result = adminService.changePassword(1, "oldPass", "newPass", "newPass");

        assertEquals(ResultStatusEnum.FAILED.getCode(), result.getCode());
        assertEquals("用户不存在", result.getErrMsg());
        verify(adminMapper, times(1)).selectById(1);
    }

    // 2. 原密码错误
    @Test
    void testChangePassword_WrongOldPassword() throws Exception {
        Admin admin = new Admin();
        admin.setId(1);
        admin.setPassword("correctOld");

        when(adminMapper.selectById(1)).thenReturn(admin);

        Result<?> result = adminService.changePassword(1, "wrongOld", "newPass", "newPass");

        assertEquals(ResultStatusEnum.FAILED.getCode(), result.getCode());
        assertEquals("原密码错误", result.getErrMsg());
        verify(adminMapper, times(1)).selectById(1);
    }

    // 3. 新密码和确认密码不一致
    @Test
    void testChangePassword_ConfirmMismatch() throws Exception {
        Admin admin = new Admin();
        admin.setId(1);
        admin.setPassword("oldPass");

        when(adminMapper.selectById(1)).thenReturn(admin);

        Result<?> result = adminService.changePassword(1, "oldPass", "newPass", "mismatch");

        assertEquals(ResultStatusEnum.FAILED.getCode(), result.getCode());
        assertEquals("两次输入的新密码不一致", result.getErrMsg());
        verify(adminMapper, times(1)).selectById(1);
    }

    // 4. 修改成功
    @Test
    void testChangePassword_Success() throws Exception {
        Admin admin = new Admin();
        admin.setId(1);
        admin.setPassword("oldPass");

        when(adminMapper.selectById(1)).thenReturn(admin);

        Result<?> result = adminService.changePassword(1, "oldPass", "newPass", "newPass");

        assertEquals(ResultStatusEnum.SUCCESS.getCode(), result.getCode());
        assertEquals("", result.getErrMsg());
        assertEquals("newPass", admin.getPassword());

        verify(adminMapper, times(1)).selectById(1);
        verify(adminMapper, times(1)).updateAll(admin);
    }

}
