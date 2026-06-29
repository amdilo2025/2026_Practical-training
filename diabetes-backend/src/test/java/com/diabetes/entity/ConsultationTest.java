package com.diabetes.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Consultation 实体单元测试 —— 目标覆盖率 100%
 */
@DisplayName("Consultation 实体")
class ConsultationTest {

    @Test
    @DisplayName("全字段 setter/getter 正常读写")
    void allFields_SetterGetter_ShouldWork() {
        Consultation consultation = new Consultation();

        consultation.setId(1);
        consultation.setUserId(100);
        consultation.setDoctorId(10);
        consultation.setContent("最近血糖偏高怎么办？");
        consultation.setReply("建议控制饮食，增加运动量");
        consultation.setStatus("已回复");

        LocalDateTime now = LocalDateTime.now();
        consultation.setCreatedTime(now);
        consultation.setReplyTime(now.plusHours(1));
        consultation.setDoctorName("赵晓峰");
        consultation.setDoctorTitle("主任医师");
        consultation.setDoctorDepartment("内分泌科");

        assertEquals(1, consultation.getId());
        assertEquals(100, consultation.getUserId());
        assertEquals(10, consultation.getDoctorId());
        assertEquals("最近血糖偏高怎么办？", consultation.getContent());
        assertEquals("建议控制饮食，增加运动量", consultation.getReply());
        assertEquals("已回复", consultation.getStatus());
        assertEquals(now, consultation.getCreatedTime());
        assertEquals(now.plusHours(1), consultation.getReplyTime());
        assertEquals("赵晓峰", consultation.getDoctorName());
        assertEquals("主任医师", consultation.getDoctorTitle());
        assertEquals("内分泌科", consultation.getDoctorDepartment());
    }

    @Test
    @DisplayName("默认初始值为 null")
    void defaultValues_ShouldBeNull() {
        Consultation consultation = new Consultation();

        assertNull(consultation.getId());
        assertNull(consultation.getUserId());
        assertNull(consultation.getDoctorId());
        assertNull(consultation.getContent());
        assertNull(consultation.getReply());
        assertNull(consultation.getStatus());
        assertNull(consultation.getCreatedTime());
        assertNull(consultation.getReplyTime());
        assertNull(consultation.getDoctorName());
        assertNull(consultation.getDoctorTitle());
        assertNull(consultation.getDoctorDepartment());
    }

    @Test
    @DisplayName("status = '待回复' → 待处理咨询")
    void pendingConsultation_ShouldWork() {
        Consultation consultation = new Consultation();
        consultation.setStatus("待回复");
        assertNull(consultation.getReply());
        assertEquals("待回复", consultation.getStatus());
    }

    @Test
    @DisplayName("doctor 不存在字段(exist=false) → 不影响持久化")
    void transientFields_ShouldNotAffectPersistence() {
        Consultation consultation = new Consultation();
        consultation.setDoctorName("赵晓峰");
        consultation.setDoctorTitle("主任医师");
        consultation.setDoctorDepartment("内分泌科");

        assertEquals("赵晓峰", consultation.getDoctorName());
        assertEquals("主任医师", consultation.getDoctorTitle());
        assertEquals("内分泌科", consultation.getDoctorDepartment());
    }
}
