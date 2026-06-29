package com.diabetes.service.impl;

import cn.hutool.crypto.digest.DigestUtil;
import com.diabetes.entity.User;
import com.diabetes.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * UserServiceImpl 单元测试 —— 目标覆盖率 100%
 * 用户核心服务，必须全部覆盖
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserServiceImpl 用户服务")
class UserServiceImplTest {

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1);
        testUser.setUsername("zhangsan");
        testUser.setPassword(DigestUtil.md5Hex("123456"));
        testUser.setName("张三");
        testUser.setPhone("13800138000");
        testUser.setStatus("正常");
    }

    // ==================== getById ====================

    @Test
    @DisplayName("getById 用户存在 → 返回用户")
    void getById_Exists_ShouldReturnUser() {
        when(userMapper.selectById(1)).thenReturn(testUser);
        User result = userService.getById(1);
        assertNotNull(result);
        assertEquals("zhangsan", result.getUsername());
    }

    @Test
    @DisplayName("getById 用户不存在 → 返回 null")
    void getById_NotFound_ShouldReturnNull() {
        when(userMapper.selectById(999)).thenReturn(null);
        User result = userService.getById(999);
        assertNull(result);
    }

    // ==================== listAll ====================

    @Test
    @DisplayName("listAll → 返回全部用户列表")
    void listAll_ShouldReturnAllUsers() {
        List<User> users = List.of(testUser);
        when(userMapper.selectList(null)).thenReturn(users);
        List<User> result = userService.listAll();
        assertEquals(1, result.size());
        assertEquals("zhangsan", result.get(0).getUsername());
    }

    @Test
    @DisplayName("listAll 空表 → 返回空列表")
    void listAll_Empty_ShouldReturnEmptyList() {
        when(userMapper.selectList(null)).thenReturn(Collections.emptyList());
        List<User> result = userService.listAll();
        assertTrue(result.isEmpty());
    }

    // ==================== add ====================

    @Test
    @DisplayName("add → 密码会被 MD5 加密后插入")
    void add_ShouldEncryptPassword() {
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setPassword("plaintext");

        when(userMapper.insert(any(User.class))).thenReturn(1);

        userService.add(newUser);
        assertEquals(DigestUtil.md5Hex("plaintext"), newUser.getPassword());
        verify(userMapper).insert(newUser);
    }

    // ==================== update ====================

    @Test
    @DisplayName("update → 调用 updateById")
    void update_ShouldCallUpdateById() {
        when(userMapper.updateById(testUser)).thenReturn(1);
        int result = userService.update(testUser);
        assertEquals(1, result);
        verify(userMapper).updateById(testUser);
    }

    // ==================== delete ====================

    @Test
    @DisplayName("delete → 调用 deleteById")
    void delete_ShouldCallDeleteById() {
        when(userMapper.deleteById(1)).thenReturn(1);
        int result = userService.delete(1);
        assertEquals(1, result);
    }

    // ==================== login ====================

    @Nested
    @DisplayName("login 登录")
    class LoginTests {

        @Test
        @DisplayName("login 正确用户名密码 → 返回用户对象")
        void login_CorrectCredentials_ShouldReturnUser() {
            when(userMapper.findByUsername("zhangsan")).thenReturn(testUser);
            User result = userService.login("zhangsan", "123456");
            assertNotNull(result);
            assertEquals("zhangsan", result.getUsername());
        }

        @Test
        @DisplayName("login 错误密码 → 返回 null")
        void login_WrongPassword_ShouldReturnNull() {
            when(userMapper.findByUsername("zhangsan")).thenReturn(testUser);
            User result = userService.login("zhangsan", "wrongpassword");
            assertNull(result);
        }

        @Test
        @DisplayName("login 用户名不存在 → 返回 null")
        void login_UserNotFound_ShouldReturnNull() {
            when(userMapper.findByUsername("unknown")).thenReturn(null);
            User result = userService.login("unknown", "123456");
            assertNull(result);
        }
    }

    // ==================== register ====================

    @Nested
    @DisplayName("register 注册")
    class RegisterTests {

        @Test
        @DisplayName("register 新用户名 → 注册成功，密码加密，状态设为正常")
        void register_NewUser_ShouldSucceed() {
            User newUser = new User();
            newUser.setUsername("newuser");
            newUser.setPassword("password123");

            when(userMapper.findByUsername("newuser")).thenReturn(null);
            when(userMapper.insert(any(User.class))).thenReturn(1);

            User result = userService.register(newUser);

            assertNotNull(result);
            assertEquals(DigestUtil.md5Hex("password123"), result.getPassword());
            assertEquals("正常", result.getStatus());
        }

        @Test
        @DisplayName("register 用户名已存在 → 返回 null")
        void register_ExistingUsername_ShouldReturnNull() {
            User newUser = new User();
            newUser.setUsername("zhangsan");

            when(userMapper.findByUsername("zhangsan")).thenReturn(testUser);

            User result = userService.register(newUser);
            assertNull(result);
            verify(userMapper, never()).insert(any(User.class));
        }
    }

    // ==================== findByUsername ====================

    @Test
    @DisplayName("findByUsername → 委托给 mapper")
    void findByUsername_ShouldDelegateToMapper() {
        when(userMapper.findByUsername("zhangsan")).thenReturn(testUser);
        User result = userService.findByUsername("zhangsan");
        assertNotNull(result);
        verify(userMapper).findByUsername("zhangsan");
    }

    // ==================== countActiveUsers ====================

    @Test
    @DisplayName("countActiveUsers → 返回活跃用户数")
    void countActiveUsers_ShouldReturnCount() {
        when(userMapper.countActiveUsers()).thenReturn(100);
        assertEquals(100, userService.countActiveUsers());
    }

    // ==================== userTrend ====================

    @Test
    @DisplayName("userTrend → 返回用户趋势数据")
    void userTrend_ShouldReturnTrendData() {
        List<Map<String, Object>> trendData = List.of(
                Map.of("date", "2026-06-01", "count", 5L)
        );
        when(userMapper.countUserTrend("2026-06-01")).thenReturn(trendData);
        List<Map<String, Object>> result = userService.userTrend("2026-06-01");
        assertEquals(1, result.size());
        assertEquals(5L, result.get(0).get("count"));
    }
}
