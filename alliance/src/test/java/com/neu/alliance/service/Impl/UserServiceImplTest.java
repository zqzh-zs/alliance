package com.neu.alliance.service.Impl;

import com.github.pagehelper.PageInfo;
import com.neu.alliance.common.config.pojo.Result;
import com.neu.alliance.common.enums.ResultStatusEnum;
import com.neu.alliance.entity.User;
import com.neu.alliance.mapper.UserMapper;
import com.neu.alliance.common.utils.SecurityUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserMapper userMapper;

    private MockedStatic<SecurityUtils> securityUtilsMock;

    @BeforeEach
    void setUp() {
        securityUtilsMock = mockStatic(SecurityUtils.class);
    }

    @AfterEach
    void tearDown() {
        securityUtilsMock.close();
    }

    // getById 测试
    @Test
    void testGetById_ReturnsUser() {
        User user = new User();
        user.setId(1);
        user.setUsername("testUser");

        when(userMapper.selectById(1)).thenReturn(user);

        User result = userService.getById(1);
        assertNotNull(result);
        assertEquals("testUser", result.getUsername());
        verify(userMapper).selectById(1);
    }

    // updateUserInfo 测试
    @Test
    void testUpdateUserInfo_CallsMapper() {
        User user = new User();
        userService.updateUserInfo(user);
        verify(userMapper).updateAll(user);
    }

    // changePassword 成功
    @Test
    void testChangePassword_Success() throws Exception {
        Integer userId = 1;
        String oldPwd = "old";
        String newPwd = "new";
        String confirmPwd = "new";

        User user = new User();
        user.setId(userId);
        user.setPassword("oldHashed");

        when(userMapper.selectById(userId)).thenReturn(user);
        securityUtilsMock.when(() -> SecurityUtils.verify(oldPwd, "oldHashed")).thenReturn(true);
        securityUtilsMock.when(() -> SecurityUtils.encrypt(newPwd)).thenReturn("newHashed");
        when(userMapper.updatePassword(anyMap())).thenReturn(1);

        Result<?> result = userService.changePassword(userId, oldPwd, newPwd, confirmPwd);
        assertEquals(ResultStatusEnum.SUCCESS.getCode(), result.getCode());
        assertEquals("", result.getErrMsg());
    }

    // changePassword 异常情况：旧密码错误
    @Test
    void testChangePassword_OldPasswordError() throws Exception {
        Integer userId = 1;
        String oldPwd = "wrongOld";
        String newPwd = "new";
        String confirmPwd = "new";

        User user = new User();
        user.setId(userId);
        user.setPassword("oldHashed");

        when(userMapper.selectById(userId)).thenReturn(user);
        securityUtilsMock.when(() -> SecurityUtils.verify(oldPwd, "oldHashed")).thenReturn(false);

        Result<?> result = userService.changePassword(userId, oldPwd, newPwd, confirmPwd);
        assertEquals(ResultStatusEnum.FAILED.getCode(), result.getCode());
        assertEquals("旧密码错误", result.getErrMsg());
    }

    // changePassword 异常情况：用户不存在
    @Test
    void testChangePassword_UserNotExist() throws Exception {
        when(userMapper.selectById(99)).thenReturn(null);
        Result<?> result = userService.changePassword(99, "old", "new", "new");
        assertEquals(ResultStatusEnum.FAILED.getCode(), result.getCode());
        assertEquals("用户不存在", result.getErrMsg());
    }

    // changePassword 异常情况：参数校验失败
    @Test
    void testChangePassword_InvalidParams() throws Exception {
        Result<?> res1 = userService.changePassword(1, "", "new", "new");
        assertEquals(ResultStatusEnum.FAILED.getCode(), res1.getCode());
        assertEquals("请填写正确的密码信息", res1.getErrMsg());

        Result<?> res2 = userService.changePassword(1, "old", "", "new");
        assertEquals(ResultStatusEnum.FAILED.getCode(), res2.getCode());
        assertEquals("请填写正确的密码信息", res2.getErrMsg());

        Result<?> res3 = userService.changePassword(1, "old", "new", "different");
        assertEquals(ResultStatusEnum.FAILED.getCode(), res3.getCode());
        assertEquals("请填写正确的密码信息", res3.getErrMsg());
    }

    // createUser 成功
    @Test
    void testCreateUser_Success() throws Exception {
        User user = new User();
        user.setUsername("newUser");
        user.setPassword(null);

        when(userMapper.countByUsername("newUser")).thenReturn(0);
        securityUtilsMock.when(() -> SecurityUtils.encrypt(anyString())).thenReturn("hashedPwd");

        doAnswer(invocation -> {
            User arg = invocation.getArgument(0);
            assertEquals("newUser", arg.getUsername());
            assertEquals("hashedPwd", arg.getPassword());
            assertEquals("newUser", arg.getNickname());
            assertEquals(2, arg.getRole());
            assertEquals(1, arg.getStatus());
            assertNotNull(arg.getCreateTime());
            return null;
        }).when(userMapper).insert(any(User.class));

        Result<?> result = userService.createUser(user);
        assertEquals(ResultStatusEnum.SUCCESS.getCode(), result.getCode());
        assertEquals("", result.getErrMsg());
    }

    // createUser 失败：用户名空
    @Test
    void testCreateUser_FailWhenUsernameEmpty() throws Exception {
        User user = new User();
        user.setUsername("");
        Result<?> result = userService.createUser(user);
        assertEquals(ResultStatusEnum.FAILED.getCode(), result.getCode());
        assertEquals("用户名不能为空", result.getErrMsg());
    }

    // createUser 失败：用户名已存在
    @Test
    void testCreateUser_FailWhenUsernameExists() throws Exception {
        User user = new User();
        user.setUsername("existUser");
        when(userMapper.countByUsername("existUser")).thenReturn(1);

        Result<?> result = userService.createUser(user);
        assertEquals(ResultStatusEnum.FAILED.getCode(), result.getCode());
        assertEquals("用户名已存在", result.getErrMsg());
        verify(userMapper, never()).insert(any());
    }

    // updateUser 成功
    @Test
    void testUpdateUser_Success() {
        User user = new User();
        when(userMapper.updateAll(user)).thenReturn(1);

        Result<?> result = userService.updateUser(user);
        assertEquals(ResultStatusEnum.SUCCESS.getCode(), result.getCode());
        assertEquals("", result.getErrMsg());
    }

    // updateUser 失败
    @Test
    void testUpdateUser_Fail() {
        User user = new User();
        when(userMapper.updateAll(user)).thenReturn(0);

        Result<?> result = userService.updateUser(user);
        assertEquals(ResultStatusEnum.FAILED.getCode(), result.getCode());
        assertEquals("更新失败", result.getErrMsg());

    }

    // queryUserList 正常调用，测试分页逻辑
    @Test
    void testQueryUserList() {
        Map<String, Object> params = new HashMap<>();
        params.put("page", "1");
        params.put("pageSize", "5");

        List<User> users = Arrays.asList(new User(), new User());
        when(userMapper.queryUserList(params)).thenReturn(users);

        PageInfo<User> pageInfo = userService.queryUserList(params);
        assertNotNull(pageInfo);
        assertEquals(users.size(), pageInfo.getList().size());
    }

    // queryUserListWithPage 测试
    @Test
    void testQueryUserListWithPage() {
        Map<String, Object> params = new HashMap<>();
        params.put("page", "1");
        params.put("pageSize", "5");

        List<User> users = Collections.singletonList(new User());
        when(userMapper.queryUserList(params)).thenReturn(users);

        Result<?> result = userService.queryUserListWithPage(params);
        assertEquals(ResultStatusEnum.SUCCESS.getCode(), result.getCode());
        assertEquals("", result.getErrMsg());
        assertTrue(result.getData() instanceof PageInfo);
        PageInfo<User> pageInfo = (PageInfo<User>) result.getData();
        assertEquals(users.size(), pageInfo.getList().size());
    }

    // deleteUserById 成功
    @Test
    void testDeleteUserById_Success() {
        when(userMapper.deleteById(1)).thenReturn(1);
        assertTrue(userService.deleteUserById(1));
    }

    // deleteUserById 失败
    @Test
    void testDeleteUserById_Fail() {
        when(userMapper.deleteById(1)).thenReturn(0);
        assertFalse(userService.deleteUserById(1));
    }
}
