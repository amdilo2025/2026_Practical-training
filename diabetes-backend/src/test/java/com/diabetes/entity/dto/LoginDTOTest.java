package com.diabetes.entity.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * LoginDTO 单元测试 —— 目标覆盖率 100%
 */
@DisplayName("LoginDTO")
class LoginDTOTest {

    @Test
    @DisplayName("全字段 setter/getter 正常读写")
    void allFields_SetterGetter_ShouldWork() {
        LoginDTO dto = new LoginDTO();

        dto.setUsername("testuser");
        dto.setPassword("123456");
        dto.setRole("patient");

        assertEquals("testuser", dto.getUsername());
        assertEquals("123456", dto.getPassword());
        assertEquals("patient", dto.getRole());
    }

    @Test
    @DisplayName("role = 'admin' → 管理员登录场景")
    void roleAdmin_ShouldWork() {
        LoginDTO dto = new LoginDTO();
        dto.setUsername("admin");
        dto.setPassword("admin123");
        dto.setRole("admin");

        assertEquals("admin", dto.getRole());
        assertEquals("admin", dto.getUsername());
        assertEquals("admin123", dto.getPassword());
    }

    @Test
    @DisplayName("字段初始值为 null")
    void defaultValues_ShouldBeNull() {
        LoginDTO dto = new LoginDTO();

        assertNull(dto.getUsername());
        assertNull(dto.getPassword());
        assertNull(dto.getRole());
    }

    @Test
    @DisplayName("set null → get 返回 null")
    void setNullValues_ShouldReturnNull() {
        LoginDTO dto = new LoginDTO();
        dto.setUsername("user");
        dto.setPassword("pass");
        dto.setRole("patient");

        dto.setUsername(null);
        dto.setPassword(null);
        dto.setRole(null);

        assertNull(dto.getUsername());
        assertNull(dto.getPassword());
        assertNull(dto.getRole());
    }
}
