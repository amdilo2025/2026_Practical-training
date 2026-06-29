package com.diabetes.utils;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JwtUtil 单元测试 —— 目标覆盖率 100%
 * 核心认证工具，必须全部覆盖
 */
@DisplayName("JwtUtil JWT 工具类")
class JwtUtilTest {

    private JwtUtil jwtUtil;

    private static final String TEST_SECRET = "test-secret-key-for-junit-testing-minimum-256bits-long";
    private static final long TEST_EXPIRATION = 3600000L; // 1小时

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        // 使用反射注入 @Value 字段
        ReflectionTestUtils.setField(jwtUtil, "secret", TEST_SECRET);
        ReflectionTestUtils.setField(jwtUtil, "expiration", TEST_EXPIRATION);
    }

    // ==================== generateToken 测试 ====================

    @Test
    @DisplayName("generateToken → 返回非空 token 字符串")
    void generateToken_ShouldReturnNonNullToken() {
        String token = jwtUtil.generateToken(1, "zhangsan", "patient");
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    @DisplayName("generateToken → token 格式正确(JWT三段式)")
    void generateToken_ShouldHaveJwtFormat() {
        String token = jwtUtil.generateToken(1, "zhangsan", "patient");
        String[] parts = token.split("\\.");
        assertEquals(3, parts.length, "JWT token 应为三段式 (header.payload.signature)");
    }

    @Test
    @DisplayName("generateToken 管理员角色 → token 含 role=admin")
    void generateToken_AdminRole_ShouldContainAdminRole() {
        String token = jwtUtil.generateToken(2, "admin", "admin");
        Claims claims = jwtUtil.parseToken(token);
        assertEquals("admin", claims.get("role", String.class));
    }

    @Test
    @DisplayName("generateToken → token 含 username claim")
    void generateToken_ShouldContainUsernameClaim() {
        String token = jwtUtil.generateToken(3, "lisi", "patient");
        Claims claims = jwtUtil.parseToken(token);
        assertEquals("lisi", claims.get("username", String.class));
    }

    @Test
    @DisplayName("generateToken → subject 为用户 ID")
    void generateToken_SubjectShouldBeUserId() {
        String token = jwtUtil.generateToken(42, "testuser", "patient");
        assertEquals(42, jwtUtil.getUserId(token));
    }

    // ==================== parseToken 测试 ====================

    @Test
    @DisplayName("parseToken 正常 token → 返回 Claims 对象")
    void parseToken_ValidToken_ShouldReturnClaims() {
        String token = jwtUtil.generateToken(1, "zhangsan", "patient");
        Claims claims = jwtUtil.parseToken(token);
        assertNotNull(claims);
        assertEquals("1", claims.getSubject());
    }

    @Test
    @DisplayName("parseToken 无效 token → 抛出异常")
    void parseToken_InvalidToken_ShouldThrowException() {
        assertThrows(Exception.class, () -> jwtUtil.parseToken("invalid.token.here"));
    }

    @Test
    @DisplayName("parseToken 空字符串 → 抛出异常")
    void parseToken_EmptyString_ShouldThrowException() {
        assertThrows(Exception.class, () -> jwtUtil.parseToken(""));
    }

    @Test
    @DisplayName("parseToken 篡改过的 token → 抛出异常")
    void parseToken_TamperedToken_ShouldThrowException() {
        String token = jwtUtil.generateToken(1, "zhangsan", "patient");
        String tamperedToken = token.substring(0, token.length() - 4) + "xxxx";
        assertThrows(Exception.class, () -> jwtUtil.parseToken(tamperedToken));
    }

    // ==================== validateToken 测试 ====================

    @Test
    @DisplayName("validateToken 正常 token → 返回 true")
    void validateToken_ValidToken_ShouldReturnTrue() {
        String token = jwtUtil.generateToken(1, "zhangsan", "patient");
        assertTrue(jwtUtil.validateToken(token));
    }

    @Test
    @DisplayName("validateToken 无效 token → 返回 false")
    void validateToken_InvalidToken_ShouldReturnFalse() {
        assertFalse(jwtUtil.validateToken("invalid.token.string"));
    }

    @Test
    @DisplayName("validateToken null → 返回 false")
    void validateToken_NullToken_ShouldReturnFalse() {
        assertFalse(jwtUtil.validateToken(null));
    }

    @Test
    @DisplayName("validateToken 空字符串 → 返回 false")
    void validateToken_EmptyToken_ShouldReturnFalse() {
        assertFalse(jwtUtil.validateToken(""));
    }

    @Test
    @DisplayName("validateToken 过期 token → 返回 false")
    void validateToken_ExpiredToken_ShouldReturnFalse() {
        // 设置极短过期时间
        ReflectionTestUtils.setField(jwtUtil, "expiration", 1L); // 1毫秒
        String token = jwtUtil.generateToken(1, "zhangsan", "patient");

        // 等待 token 过期
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        assertFalse(jwtUtil.validateToken(token));
    }

    // ==================== getUserId 测试 ====================

    @Test
    @DisplayName("getUserId → 返回正确的用户 ID")
    void getUserId_ShouldReturnCorrectId() {
        String token = jwtUtil.generateToken(99, "testuser", "patient");
        Integer userId = jwtUtil.getUserId(token);
        assertEquals(99, userId);
    }

    @Test
    @DisplayName("getUserId 大 ID 值 → 正确解析")
    void getUserId_LargeValue_ShouldWork() {
        String token = jwtUtil.generateToken(Integer.MAX_VALUE, "testuser", "patient");
        assertEquals(Integer.MAX_VALUE, jwtUtil.getUserId(token));
    }

    // ==================== getRole 测试 ====================

    @Test
    @DisplayName("getRole → 返回 patient 角色")
    void getRole_Patient_ShouldReturnPatient() {
        String token = jwtUtil.generateToken(1, "zhangsan", "patient");
        assertEquals("patient", jwtUtil.getRole(token));
    }

    @Test
    @DisplayName("getRole → 返回 admin 角色")
    void getRole_Admin_ShouldReturnAdmin() {
        String token = jwtUtil.generateToken(2, "admin", "admin");
        assertEquals("admin", jwtUtil.getRole(token));
    }

    // ==================== 边界场景 ====================

    @Test
    @DisplayName("generateToken 带特殊字符 username → 正常工作")
    void generateToken_SpecialCharactersInUsername_ShouldWork() {
        String token = jwtUtil.generateToken(1, "user@domain.com", "patient");
        assertTrue(jwtUtil.validateToken(token));
        assertEquals("user@domain.com", jwtUtil.parseToken(token).get("username", String.class));
    }

    @Test
    @DisplayName("多次生成 token 结果不同 (issuedAt 不同)")
    void multipleGenerateToken_ShouldProduceDifferentTokens() throws InterruptedException {
        String token1 = jwtUtil.generateToken(1, "zhangsan", "patient");
        // JWT iat 精度为秒级，需等待超过 1 秒
        Thread.sleep(1100);
        String token2 = jwtUtil.generateToken(1, "zhangsan", "patient");
        assertNotEquals(token1, token2, "不同时间生成的 token 应不同");
    }
}
