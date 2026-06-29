package com.diabetes.entity.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * RiskPredictDTO 单元测试 —— 目标覆盖率 100%
 */
@DisplayName("RiskPredictDTO")
class RiskPredictDTOTest {

    @Test
    @DisplayName("全字段 setter/getter 正常读写")
    void allFields_SetterGetter_ShouldWork() {
        RiskPredictDTO dto = new RiskPredictDTO();

        dto.setUserId(1);
        dto.setAge(55);
        dto.setHeight(170.0);
        dto.setWeight(80.0);
        dto.setFastingBloodSugar(7.2);
        dto.setPostprandialBloodSugar(11.5);
        dto.setBloodPressureSystolic(135);
        dto.setBloodPressureDiastolic(85);
        dto.setFamilyHistory(1);

        assertEquals(1, dto.getUserId());
        assertEquals(55, dto.getAge());
        assertEquals(170.0, dto.getHeight());
        assertEquals(80.0, dto.getWeight());
        assertEquals(7.2, dto.getFastingBloodSugar());
        assertEquals(11.5, dto.getPostprandialBloodSugar());
        assertEquals(135, dto.getBloodPressureSystolic());
        assertEquals(85, dto.getBloodPressureDiastolic());
        assertEquals(1, dto.getFamilyHistory());
    }

    @Test
    @DisplayName("默认初始值为 null")
    void defaultValues_ShouldBeNull() {
        RiskPredictDTO dto = new RiskPredictDTO();

        assertNull(dto.getUserId());
        assertNull(dto.getAge());
        assertNull(dto.getHeight());
        assertNull(dto.getWeight());
        assertNull(dto.getFastingBloodSugar());
        assertNull(dto.getPostprandialBloodSugar());
        assertNull(dto.getBloodPressureSystolic());
        assertNull(dto.getBloodPressureDiastolic());
        assertNull(dto.getFamilyHistory());
    }

    @Test
    @DisplayName("无家族史 → familyHistory=0")
    void noFamilyHistory_ShouldWork() {
        RiskPredictDTO dto = new RiskPredictDTO();
        dto.setFamilyHistory(0);
        assertEquals(0, dto.getFamilyHistory());
    }

    @Test
    @DisplayName("BMI > 28 高风险数据 → 全字段正确")
    void highRiskObesityData_ShouldWork() {
        RiskPredictDTO dto = new RiskPredictDTO();
        dto.setUserId(10);
        dto.setAge(60);
        dto.setHeight(165.0);
        dto.setWeight(90.0);
        dto.setFastingBloodSugar(9.0);
        dto.setPostprandialBloodSugar(14.0);
        dto.setBloodPressureSystolic(150);
        dto.setBloodPressureDiastolic(95);
        dto.setFamilyHistory(1);

        assertEquals(90.0, dto.getWeight());
        assertEquals(9.0, dto.getFastingBloodSugar());
        assertEquals(150, dto.getBloodPressureSystolic());
    }
}
