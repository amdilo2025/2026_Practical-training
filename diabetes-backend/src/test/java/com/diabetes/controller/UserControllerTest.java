package com.diabetes.controller;

import cn.hutool.crypto.digest.DigestUtil;
import com.diabetes.entity.Admin;
import com.diabetes.entity.User;
import com.diabetes.entity.dto.LoginDTO;
import com.diabetes.service.AdminService;
import com.diabetes.service.UserService;
import com.diabetes.utils.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * UserController 单元测试 —— 目标覆盖率 100%
 */
@WebMvcTest(UserController.class)
@DisplayName("UserController 用户控制器")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private AdminService adminService;

    @MockBean
    private JwtUtil jwtUtil;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1);
        testUser.setUsername("zhangsan");
        testUser.setPassword(DigestUtil.md5Hex("123456"));
        testUser.setName("张三");
        testUser.setStatus("正常");
    }

    // ==================== 登录 ====================

    @Nested
    @DisplayName("POST /api/login")
    class LoginTests {

        @Test
        @DisplayName("patient 登录成功 → 返回 token + user + role")
        void patientLoginSuccess() throws Exception {
            LoginDTO loginDTO = new LoginDTO();
            loginDTO.setUsername("zhangsan");
            loginDTO.setPassword("123456");
            loginDTO.setRole("patient");

            when(userService.login("zhangsan", "123456")).thenReturn(testUser);
            when(jwtUtil.generateToken(1, "zhangsan", "patient")).thenReturn("jwt-token-xyz");

            mockMvc.perform(post("/api/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginDTO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.token").value("jwt-token-xyz"))
                    .andExpect(jsonPath("$.data.role").value("patient"));
        }

        @Test
        @DisplayName("admin 登录成功 → 返回 admin 角色")
        void adminLoginSuccess() throws Exception {
            LoginDTO loginDTO = new LoginDTO();
            loginDTO.setUsername("admin");
            loginDTO.setPassword("admin123");
            loginDTO.setRole("admin");

            Admin admin = new Admin();
            admin.setId(1);
            admin.setUsername("admin");

            when(adminService.login("admin", "admin123")).thenReturn(admin);
            when(jwtUtil.generateToken(1, "admin", "admin")).thenReturn("jwt-admin-token");

            mockMvc.perform(post("/api/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginDTO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.role").value("admin"));
        }

        @Test
        @DisplayName("密码错误 → 返回错误")
        void loginWrongPassword() throws Exception {
            LoginDTO loginDTO = new LoginDTO();
            loginDTO.setUsername("zhangsan");
            loginDTO.setPassword("wrong");
            loginDTO.setRole("patient");

            when(userService.login("zhangsan", "wrong")).thenReturn(null);

            mockMvc.perform(post("/api/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginDTO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(500))
                    .andExpect(jsonPath("$.message").value("用户名或密码错误"));
        }

        @Test
        @DisplayName("用户被禁用 → 返回账号已被禁用")
        void loginDisabledUser() throws Exception {
            testUser.setStatus("禁用");
            LoginDTO loginDTO = new LoginDTO();
            loginDTO.setUsername("zhangsan");
            loginDTO.setPassword("123456");
            loginDTO.setRole("patient");

            when(userService.login("zhangsan", "123456")).thenReturn(testUser);

            mockMvc.perform(post("/api/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginDTO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("账号已被禁用"));
        }
    }

    // ==================== 注册 ====================

    @Test
    @DisplayName("register 新用户 → 注册成功")
    void registerSuccess() throws Exception {
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setPassword("password123");

        when(userService.findByUsername("newuser")).thenReturn(null);
        when(userService.add(any(User.class))).thenReturn(1);

        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("register 用户名已存在 → 返回错误")
    void registerDuplicateUsername() throws Exception {
        User newUser = new User();
        newUser.setUsername("zhangsan");
        newUser.setPassword("123456");

        when(userService.findByUsername("zhangsan")).thenReturn(testUser);

        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("用户名已存在"));
    }

    // ==================== CRUD ====================

    @Test
    @DisplayName("GET /api/user/get/{id} → 返回用户")
    void getUserById() throws Exception {
        when(userService.getById(1)).thenReturn(testUser);

        mockMvc.perform(get("/api/user/get/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.username").value("zhangsan"));
    }

    @Test
    @DisplayName("GET /api/user/get/{id} 不存在 → 返回错误")
    void getUserByIdNotFound() throws Exception {
        when(userService.getById(999)).thenReturn(null);

        mockMvc.perform(get("/api/user/get/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("用户不存在"));
    }

    @Test
    @DisplayName("GET /api/user/list → 返回用户列表")
    void listUsers() throws Exception {
        when(userService.listAll()).thenReturn(List.of(testUser));

        mockMvc.perform(get("/api/user/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].username").value("zhangsan"));
    }

    @Test
    @DisplayName("PUT /api/user/update → 更新成功")
    void updateUser() throws Exception {
        when(userService.update(any(User.class))).thenReturn(1);

        mockMvc.perform(put("/api/user/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("DELETE /api/user/delete/{id} → 删除成功")
    void deleteUser() throws Exception {
        when(userService.delete(1)).thenReturn(1);

        mockMvc.perform(delete("/api/user/delete/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    // ==================== 个人中心 ====================

    @Test
    @DisplayName("GET /api/user/profile/{id} → 返回个人信息")
    void getProfile() throws Exception {
        when(userService.getById(1)).thenReturn(testUser);

        mockMvc.perform(get("/api/user/profile/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username").value("zhangsan"));
    }

    @Test
    @DisplayName("PUT /api/user/profile/update → 更新个人信息成功")
    void updateProfile() throws Exception {
        when(userService.update(any(User.class))).thenReturn(1);

        mockMvc.perform(put("/api/user/profile/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    // ==================== 修改密码 ====================

    @Test
    @DisplayName("PUT /api/user/password 正确旧密码 → 修改成功")
    void changePasswordCorrectOldPassword() throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", 1);
        params.put("oldPassword", "123456");
        params.put("newPassword", "newpass");

        when(userService.getById(1)).thenReturn(testUser);
        when(userService.update(any(User.class))).thenReturn(1);

        mockMvc.perform(put("/api/user/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("PUT /api/user/password 错误旧密码 → 修改失败")
    void changePasswordWrongOldPassword() throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", 1);
        params.put("oldPassword", "wrongold");
        params.put("newPassword", "newpass");

        when(userService.getById(1)).thenReturn(testUser);

        mockMvc.perform(put("/api/user/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("原密码错误"));
    }
}
