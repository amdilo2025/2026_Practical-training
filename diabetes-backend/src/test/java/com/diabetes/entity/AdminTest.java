package com.diabetes.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Admin 实体单元测试 —— 目标覆盖率 100%
 */
@DisplayName("Admin 实体")
class AdminTest {

    @Test
    @DisplayName("全字段 setter/getter 正常读写")
    void allFields_SetterGetter_ShouldWork() {
        Admin admin = new Admin();

        admin.setId(1);
        admin.setUsername("admin123");
        admin.setPassword("password_hash");
        admin.setName("王管理员");
        admin.setPhone("13900139000");
        admin.setRole("admin");
        admin.setStatus("正常");

        LocalDateTime now = LocalDateTime.now();
        admin.setCreatedTime(now);

        assertEquals(1, admin.getId());
        assertEquals("admin123", admin.getUsername());
        assertEquals("password_hash", admin.getPassword());
        assertEquals("王管理员", admin.getName());
        assertEquals("13900139000", admin.getPhone());
        assertEquals("admin", admin.getRole());
        assertEquals("正常", admin.getStatus());
        assertEquals(now, admin.getCreatedTime());
    }

    @Test
    @DisplayName("默认初始值为 null")
    void defaultValues_ShouldBeNull() {
        Admin admin = new Admin();

        assertNull(admin.getId());
        assertNull(admin.getUsername());
        assertNull(admin.getPassword());
        assertNull(admin.getName());
        assertNull(admin.getPhone());
        assertNull(admin.getRole());
        assertNull(admin.getStatus());
        assertNull(admin.getCreatedTime());
    }

    @Test
    @DisplayName("role = 'super_admin' → 超级管理员场景")
    void superAdminRole_ShouldWork() {
        Admin admin = new Admin();
        admin.setRole("super_admin");
        assertEquals("super_admin", admin.getRole());
    }
}
