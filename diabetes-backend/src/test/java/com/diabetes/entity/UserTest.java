package com.diabetes.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * User 实体单元测试 —— 目标覆盖率 100%
 */
@DisplayName("User 实体")
class UserTest {

    @Test
    @DisplayName("全字段 setter/getter 正常读写")
    void allFields_SetterGetter_ShouldWork() {
        User user = new User();

        user.setId(1);
        user.setUsername("zhangsan");
        user.setPassword("e10adc3949ba59abbe56e057f20f883e");
        user.setName("张三");
        user.setPhone("13800138000");
        user.setAvatar("/avatar/zhangsan.jpg");
        user.setAge(45);
        user.setGender("男");
        user.setHeight(170.5);
        user.setWeight(75.0);
        user.setDiabetesType("2型糖尿病");
        user.setFamilyHistory(1);
        user.setStatus("正常");

        LocalDateTime now = LocalDateTime.now();
        user.setCreatedTime(now);
        user.setUpdatedTime(now);

        assertEquals(1, user.getId());
        assertEquals("zhangsan", user.getUsername());
        assertEquals("e10adc3949ba59abbe56e057f20f883e", user.getPassword());
        assertEquals("张三", user.getName());
        assertEquals("13800138000", user.getPhone());
        assertEquals("/avatar/zhangsan.jpg", user.getAvatar());
        assertEquals(45, user.getAge());
        assertEquals("男", user.getGender());
        assertEquals(170.5, user.getHeight());
        assertEquals(75.0, user.getWeight());
        assertEquals("2型糖尿病", user.getDiabetesType());
        assertEquals(1, user.getFamilyHistory());
        assertEquals("正常", user.getStatus());
        assertEquals(now, user.getCreatedTime());
        assertEquals(now, user.getUpdatedTime());
    }

    @Test
    @DisplayName("默认初始值为 null")
    void defaultValues_ShouldBeNull() {
        User user = new User();

        assertNull(user.getId());
        assertNull(user.getUsername());
        assertNull(user.getPassword());
        assertNull(user.getName());
        assertNull(user.getPhone());
        assertNull(user.getAvatar());
        assertNull(user.getAge());
        assertNull(user.getGender());
        assertNull(user.getHeight());
        assertNull(user.getWeight());
        assertNull(user.getDiabetesType());
        assertNull(user.getFamilyHistory());
        assertNull(user.getStatus());
        assertNull(user.getCreatedTime());
        assertNull(user.getUpdatedTime());
    }

    @Test
    @DisplayName("女性用户场景 → gender = '女'")
    void femaleUser_ShouldWork() {
        User user = new User();
        user.setGender("女");
        user.setHeight(160.0);
        user.setWeight(55.0);
        assertEquals("女", user.getGender());
        assertEquals(160.0, user.getHeight());
        assertEquals(55.0, user.getWeight());
    }

    @Test
    @DisplayName("状态为 '禁用' → 禁用用户场景")
    void disabledUser_ShouldWork() {
        User user = new User();
        user.setStatus("禁用");
        assertEquals("禁用", user.getStatus());
    }

    @Test
    @DisplayName("无糖尿病 → diabetesType 可为 null 或空")
    void noDiabetes_ShouldWork() {
        User user = new User();
        user.setDiabetesType(null);
        assertNull(user.getDiabetesType());
    }

    @Test
    @DisplayName("无家族史 → familyHistory = 0")
    void noFamilyHistory_ShouldWork() {
        User user = new User();
        user.setFamilyHistory(0);
        assertEquals(0, user.getFamilyHistory());
    }
}
