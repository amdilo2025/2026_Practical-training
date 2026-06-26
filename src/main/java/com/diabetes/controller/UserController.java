package com.diabetes.controller;

import cn.hutool.crypto.digest.DigestUtil;
import com.diabetes.common.R;
import com.diabetes.entity.Admin;
import com.diabetes.entity.User;
import com.diabetes.entity.dto.LoginDTO;
import com.diabetes.service.AdminService;
import com.diabetes.service.UserService;
import com.diabetes.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private JwtUtil jwtUtil;

    // ==================== 登录 ====================
    @PostMapping("/login")
    public R<Map<String, Object>> login(@RequestBody LoginDTO loginDTO) {
        if ("patient".equals(loginDTO.getRole())) {
            User user = userService.login(loginDTO.getUsername(), loginDTO.getPassword());
            if (user != null) {
                if ("禁用".equals(user.getStatus())) {
                    return R.error("账号已被禁用");
                }
                String token = jwtUtil.generateToken(user.getId(), user.getUsername(), "patient");
                Map<String, Object> data = new HashMap<>();
                data.put("token", token);
                data.put("user", user);
                data.put("role", "patient");
                return R.success(data);
            }
        } else if ("admin".equals(loginDTO.getRole())) {
            Admin admin = adminService.login(loginDTO.getUsername(), loginDTO.getPassword());
            if (admin != null) {
                String token = jwtUtil.generateToken(admin.getId(), admin.getUsername(), "admin");
                Map<String, Object> data = new HashMap<>();
                data.put("token", token);
                data.put("user", admin);
                data.put("role", "admin");
                return R.success(data);
            }
        }
        return R.error("用户名或密码错误");
    }

    // ==================== 注册 ====================
    @PostMapping("/register")
    public R<User> register(@RequestBody User user) {
        User existing = userService.findByUsername(user.getUsername());
        if (existing != null) {
            return R.error("用户名已存在");
        }
        user.setPassword(DigestUtil.md5Hex(user.getPassword()));
        user.setStatus("正常");
        userService.add(user);
        return R.success("注册成功", user);
    }

    // ==================== 用户CRUD ====================
    @GetMapping("/user/get/{id}")
    public R<User> getUserById(@PathVariable Integer id) {
        User user = userService.getById(id);
        return user != null ? R.success(user) : R.error("用户不存在");
    }

    @GetMapping("/user/list")
    public R<java.util.List<User>> listUsers() {
        return R.success(userService.listAll());
    }

    @PutMapping("/user/update")
    public R<String> updateUser(@RequestBody User user) {
        userService.update(user);
        return R.success("更新成功");
    }

    @DeleteMapping("/user/delete/{id}")
    public R<String> deleteUser(@PathVariable Integer id) {
        userService.delete(id);
        return R.success("删除成功");
    }

    // ==================== 个人中心 ====================
    @GetMapping("/user/profile/{id}")
    public R<User> getProfile(@PathVariable Integer id) {
        User user = userService.getById(id);
        return user != null ? R.success(user) : R.error("用户不存在");
    }

    @PutMapping("/user/profile/update")
    public R<String> updateProfile(@RequestBody User user) {
        userService.update(user);
        return R.success("个人信息更新成功");
    }

    @PutMapping("/user/password")
    public R<String> changePassword(@RequestBody Map<String, Object> params) {
        Integer userId = Integer.parseInt(params.get("userId").toString());
        String oldPwd = params.get("oldPassword").toString();
        String newPwd = params.get("newPassword").toString();
        User user = userService.getById(userId);
        if (user == null || !user.getPassword().equals(DigestUtil.md5Hex(oldPwd))) {
            return R.error("原密码错误");
        }
        user.setPassword(DigestUtil.md5Hex(newPwd));
        userService.update(user);
        return R.success("密码修改成功");
    }
}
