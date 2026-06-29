package com.diabetes.service.impl;

import cn.hutool.crypto.digest.DigestUtil;
import com.diabetes.entity.Admin;
import com.diabetes.mapper.AdminMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * AdminServiceImpl 单元测试 —— 目标覆盖率 100%
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AdminServiceImpl 管理员服务")
class AdminServiceImplTest {

    @Mock
    private AdminMapper adminMapper;

    @InjectMocks
    private AdminServiceImpl adminService;

    private Admin testAdmin;

    @BeforeEach
    void setUp() {
        testAdmin = new Admin();
        testAdmin.setId(1);
        testAdmin.setUsername("admin");
        testAdmin.setPassword(DigestUtil.md5Hex("admin123"));
        testAdmin.setName("王管理员");
        testAdmin.setRole("admin");
    }

    @Test
    @DisplayName("getById 存在 → 返回管理员")
    void getById_Exists_ShouldReturnAdmin() {
        when(adminMapper.selectById(1)).thenReturn(testAdmin);
        Admin result = adminService.getById(1);
        assertNotNull(result);
        assertEquals("admin", result.getUsername());
    }

    @Test
    @DisplayName("getById 不存在 → 返回 null")
    void getById_NotFound_ShouldReturnNull() {
        when(adminMapper.selectById(999)).thenReturn(null);
        assertNull(adminService.getById(999));
    }

    @Test
    @DisplayName("login 正确凭证 → 返回管理员对象")
    void login_CorrectCredentials_ShouldReturnAdmin() {
        when(adminMapper.findByUsername("admin")).thenReturn(testAdmin);
        Admin result = adminService.login("admin", "admin123");
        assertNotNull(result);
        assertEquals("admin", result.getUsername());
    }

    @Test
    @DisplayName("login 错误密码 → 返回 null")
    void login_WrongPassword_ShouldReturnNull() {
        when(adminMapper.findByUsername("admin")).thenReturn(testAdmin);
        Admin result = adminService.login("admin", "wrongpassword");
        assertNull(result);
    }

    @Test
    @DisplayName("login 用户名不存在 → 返回 null")
    void login_UserNotFound_ShouldReturnNull() {
        when(adminMapper.findByUsername("unknown")).thenReturn(null);
        Admin result = adminService.login("unknown", "any");
        assertNull(result);
    }

    @Test
    @DisplayName("findByUsername → 委托给 mapper")
    void findByUsername_ShouldDelegateToMapper() {
        when(adminMapper.findByUsername("admin")).thenReturn(testAdmin);
        Admin result = adminService.findByUsername("admin");
        assertNotNull(result);
        verify(adminMapper).findByUsername("admin");
    }

    @Test
    @DisplayName("update → 调用 updateById")
    void update_ShouldCallUpdateById() {
        when(adminMapper.updateById(testAdmin)).thenReturn(1);
        int result = adminService.update(testAdmin);
        assertEquals(1, result);
        verify(adminMapper).updateById(testAdmin);
    }
}
