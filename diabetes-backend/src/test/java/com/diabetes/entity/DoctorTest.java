package com.diabetes.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Doctor 实体单元测试 —— 目标覆盖率 100%
 */
@DisplayName("Doctor 实体")
class DoctorTest {

    @Test
    @DisplayName("全字段 setter/getter 正常读写")
    void allFields_SetterGetter_ShouldWork() {
        Doctor doctor = new Doctor();

        doctor.setId(1);
        doctor.setName("赵晓峰");
        doctor.setDepartment("内分泌科");
        doctor.setTitle("主任医师");
        doctor.setAvatar("/avatar/zhaoxiaofeng.jpg");
        doctor.setPhone("13700137000");
        doctor.setIntroduction("20年临床经验，擅长糖尿病综合管理");
        doctor.setGoodAt("糖尿病、甲状腺疾病");
        doctor.setStatus("正常");
        doctor.setIsAi(1);

        LocalDateTime now = LocalDateTime.now();
        doctor.setCreatedTime(now);

        assertEquals(1, doctor.getId());
        assertEquals("赵晓峰", doctor.getName());
        assertEquals("内分泌科", doctor.getDepartment());
        assertEquals("主任医师", doctor.getTitle());
        assertEquals("/avatar/zhaoxiaofeng.jpg", doctor.getAvatar());
        assertEquals("13700137000", doctor.getPhone());
        assertEquals("20年临床经验，擅长糖尿病综合管理", doctor.getIntroduction());
        assertEquals("糖尿病、甲状腺疾病", doctor.getGoodAt());
        assertEquals("正常", doctor.getStatus());
        assertEquals(1, doctor.getIsAi());
        assertEquals(now, doctor.getCreatedTime());
    }

    @Test
    @DisplayName("默认初始值为 null")
    void defaultValues_ShouldBeNull() {
        Doctor doctor = new Doctor();

        assertNull(doctor.getId());
        assertNull(doctor.getName());
        assertNull(doctor.getDepartment());
        assertNull(doctor.getTitle());
        assertNull(doctor.getAvatar());
        assertNull(doctor.getPhone());
        assertNull(doctor.getIntroduction());
        assertNull(doctor.getGoodAt());
        assertNull(doctor.getStatus());
        assertNull(doctor.getIsAi());
        assertNull(doctor.getCreatedTime());
    }

    @Test
    @DisplayName("isAi = 1 → AI 医生场景")
    void aiDoctor_ShouldHaveIsAi1() {
        Doctor doctor = new Doctor();
        doctor.setIsAi(1);
        assertEquals(1, doctor.getIsAi());
    }

    @Test
    @DisplayName("isAi = 0 → 普通医生场景")
    void regularDoctor_ShouldHaveIsAi0() {
        Doctor doctor = new Doctor();
        doctor.setIsAi(0);
        assertEquals(0, doctor.getIsAi());
    }

    @Test
    @DisplayName("status = '禁用' → 已禁用医生")
    void disabledDoctor_ShouldWork() {
        Doctor doctor = new Doctor();
        doctor.setStatus("禁用");
        assertEquals("禁用", doctor.getStatus());
    }
}
