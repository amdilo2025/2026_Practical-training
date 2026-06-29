package com.diabetes.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * RiskPrediction 实体单元测试 —— 目标覆盖率 100%
 */
@DisplayName("RiskPrediction 实体")
class RiskPredictionTest {

    @Test
    @DisplayName("全字段 setter/getter 正常读写")
    void allFields_SetterGetter_ShouldWork() {
        RiskPrediction prediction = new RiskPrediction();

        prediction.setId(1);
        prediction.setUserId(100);
        prediction.setAge(55);
        prediction.setHeight(170.0);
        prediction.setWeight(80.0);
        prediction.setFastingBloodSugar(7.2);
        prediction.setPostprandialBloodSugar(11.5);
        prediction.setBloodPressureSystolic(135);
        prediction.setBloodPressureDiastolic(85);
        prediction.setFamilyHistory(1);
        prediction.setBmi(27.7);
        prediction.setRiskLevel("中风险");
        prediction.setRiskScore(45);
        prediction.setAdvice("建议改善生活习惯");

        LocalDateTime now = LocalDateTime.now();
        prediction.setCreatedTime(now);

        assertEquals(1, prediction.getId());
        assertEquals(100, prediction.getUserId());
        assertEquals(55, prediction.getAge());
        assertEquals(170.0, prediction.getHeight());
        assertEquals(80.0, prediction.getWeight());
        assertEquals(7.2, prediction.getFastingBloodSugar());
        assertEquals(11.5, prediction.getPostprandialBloodSugar());
        assertEquals(135, prediction.getBloodPressureSystolic());
        assertEquals(85, prediction.getBloodPressureDiastolic());
        assertEquals(1, prediction.getFamilyHistory());
        assertEquals(27.7, prediction.getBmi());
        assertEquals("中风险", prediction.getRiskLevel());
        assertEquals(45, prediction.getRiskScore());
        assertEquals("建议改善生活习惯", prediction.getAdvice());
        assertEquals(now, prediction.getCreatedTime());
    }

    @Test
    @DisplayName("默认初始值为 null")
    void defaultValues_ShouldBeNull() {
        RiskPrediction prediction = new RiskPrediction();

        assertNull(prediction.getId());
        assertNull(prediction.getUserId());
        assertNull(prediction.getAge());
        assertNull(prediction.getHeight());
        assertNull(prediction.getWeight());
        assertNull(prediction.getFastingBloodSugar());
        assertNull(prediction.getPostprandialBloodSugar());
        assertNull(prediction.getBloodPressureSystolic());
        assertNull(prediction.getBloodPressureDiastolic());
        assertNull(prediction.getFamilyHistory());
        assertNull(prediction.getBmi());
        assertNull(prediction.getRiskLevel());
        assertNull(prediction.getRiskScore());
        assertNull(prediction.getAdvice());
        assertNull(prediction.getCreatedTime());
    }

    @Test
    @DisplayName("riskLevel = '高风险' → 高风险预测")
    void highRiskPrediction_ShouldWork() {
        RiskPrediction prediction = new RiskPrediction();
        prediction.setRiskLevel("高风险");
        prediction.setRiskScore(85);
        assertEquals("高风险", prediction.getRiskLevel());
        assertEquals(85, prediction.getRiskScore());
    }

    @Test
    @DisplayName("riskLevel = '低风险' → 低风险预测")
    void lowRiskPrediction_ShouldWork() {
        RiskPrediction prediction = new RiskPrediction();
        prediction.setRiskLevel("低风险");
        prediction.setRiskScore(15);
        assertEquals("低风险", prediction.getRiskLevel());
        assertEquals(15, prediction.getRiskScore());
    }

    @Test
    @DisplayName("BMI 精确到小数点后 1 位")
    void bmiWithOneDecimal_ShouldWork() {
        RiskPrediction prediction = new RiskPrediction();
        prediction.setBmi(24.8);
        assertEquals(24.8, prediction.getBmi());
    }
}
