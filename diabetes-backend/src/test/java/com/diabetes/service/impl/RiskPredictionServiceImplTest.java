package com.diabetes.service.impl;

import com.diabetes.entity.RiskPrediction;
import com.diabetes.entity.User;
import com.diabetes.mapper.RiskPredictionMapper;
import com.diabetes.mapper.UserMapper;
import com.diabetes.utils.DifyClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * RiskPredictionServiceImpl 单元测试 —— 目标覆盖率 100%
 * 核心风险预测服务，必须全部覆盖
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("RiskPredictionServiceImpl 风险预测服务")
class RiskPredictionServiceImplTest {

    @Mock
    private RiskPredictionMapper riskPredictionMapper;

    @Mock
    private DifyClient difyClient;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private RiskPredictionServiceImpl riskPredictionService;

    private RiskPrediction testPrediction;
    private User testUser;

    @BeforeEach
    void setUp() {
        testPrediction = new RiskPrediction();
        testPrediction.setUserId(100);
        testPrediction.setAge(55);
        testPrediction.setHeight(170.0);
        testPrediction.setWeight(80.0);
        testPrediction.setFastingBloodSugar(7.2);
        testPrediction.setPostprandialBloodSugar(11.5);
        testPrediction.setBloodPressureSystolic(135);
        testPrediction.setBloodPressureDiastolic(85);
        testPrediction.setFamilyHistory(1);

        testUser = new User();
        testUser.setId(100);
        testUser.setGender("男");
        testUser.setAge(55);
        testUser.setHeight(170.0);
        testUser.setWeight(80.0);
        testUser.setFamilyHistory(1);
        testUser.setDiabetesType("2型糖尿病");
    }

    // ==================== getById ====================

    @Test
    @DisplayName("getById → 返回预测记录")
    void getById_ShouldReturnPrediction() {
        when(riskPredictionMapper.selectById(1)).thenReturn(testPrediction);
        RiskPrediction result = riskPredictionService.getById(1);
        assertNotNull(result);
        assertEquals(55, result.getAge());
    }

    // ==================== listByUser ====================

    @Test
    @DisplayName("listByUser → 返回用户预测历史")
    void listByUser_ShouldReturnPredictions() {
        when(riskPredictionMapper.findByUserId(100)).thenReturn(List.of(testPrediction));
        List<RiskPrediction> result = riskPredictionService.listByUser(100);
        assertEquals(1, result.size());
    }

    // ==================== predict ====================

    @Nested
    @DisplayName("predict 风险预测")
    class PredictTests {

        @Test
        @DisplayName("predict workflow 成功 → 解析 AI 返回的风险等级")
        void predict_WorkflowSuccess_ShouldParseResult() {
            when(userMapper.selectById(100)).thenReturn(testUser);
            when(riskPredictionMapper.insert(any(RiskPrediction.class))).thenReturn(1);

            Map<String, Object> outputs = new HashMap<>();
            outputs.put("result", "【高风险】|评分:85|建议:及时就医检查");
            when(difyClient.runRiskWorkflow(anyMap(), eq("100"))).thenReturn(outputs);

            RiskPrediction result = riskPredictionService.predict(testPrediction);

            assertNotNull(result);
            assertEquals("高风险", result.getRiskLevel());
            assertEquals(85, result.getRiskScore());
            assertEquals("及时就医检查", result.getAdvice());
            verify(riskPredictionMapper).insert(any(RiskPrediction.class));
        }

        @Test
        @DisplayName("predict workflow 失败 → 使用本地算法兜底（高风险）")
        void predict_WorkflowException_ShouldFallbackToLocalAlgorithm() {
            when(userMapper.selectById(100)).thenReturn(testUser);
            when(riskPredictionMapper.insert(any(RiskPrediction.class))).thenReturn(1);
            when(difyClient.runRiskWorkflow(anyMap(), eq("100")))
                    .thenThrow(new RuntimeException("Dify 不可用"));

            RiskPrediction result = riskPredictionService.predict(testPrediction);

            assertNotNull(result);
            assertNotNull(result.getRiskLevel());
            assertNotNull(result.getRiskScore());
            assertNotNull(result.getAdvice());
            verify(riskPredictionMapper).insert(any(RiskPrediction.class));
        }

        @Test
        @DisplayName("predict 计算 BMI → 体重/身高²")
        void predict_ShouldCalculateBmi() {
            when(userMapper.selectById(100)).thenReturn(testUser);

            Map<String, Object> outputs = new HashMap<>();
            outputs.put("result", "【中风险】|评分:45|建议:改善生活习惯");
            when(difyClient.runRiskWorkflow(anyMap(), eq("100"))).thenReturn(outputs);
            when(riskPredictionMapper.insert(any(RiskPrediction.class))).thenReturn(1);

            RiskPrediction result = riskPredictionService.predict(testPrediction);

            // BMI = 80 / (1.7*1.7) = 27.68 ≈ 27.7
            assertEquals(27.7, result.getBmi());
        }

        @Test
        @DisplayName("predict height=0 → 不计算 BMI")
        void predict_ZeroHeight_ShouldNotCalculateBmi() {
            testPrediction.setHeight(0.0);
            when(userMapper.selectById(100)).thenReturn(testUser);

            Map<String, Object> outputs = new HashMap<>();
            outputs.put("result", "【低风险】|评分:15|建议:保持良好习惯");
            when(difyClient.runRiskWorkflow(anyMap(), eq("100"))).thenReturn(outputs);
            when(riskPredictionMapper.insert(any(RiskPrediction.class))).thenReturn(1);

            RiskPrediction result = riskPredictionService.predict(testPrediction);

            assertNull(result.getBmi()); // height<=0 不计算 BMI
        }

        @Test
        @DisplayName("predict 低风险场景 → 本地兜底 score < 30")
        void predict_LowRisk_LocalFallback() {
            testPrediction.setAge(25);
            testPrediction.setFastingBloodSugar(5.0);
            testPrediction.setFamilyHistory(0);
            testPrediction.setBmi(22.0);

            when(userMapper.selectById(100)).thenReturn(testUser);
            when(riskPredictionMapper.insert(any(RiskPrediction.class))).thenReturn(1);
            when(difyClient.runRiskWorkflow(anyMap(), eq("100")))
                    .thenThrow(new RuntimeException("fail"));

            RiskPrediction result = riskPredictionService.predict(testPrediction);

            assertEquals("低风险", result.getRiskLevel());
            assertTrue(result.getRiskScore() < 30);
            assertTrue(result.getAdvice().contains("风险较低"));
        }

        @Test
        @DisplayName("predict 高风险场景 → 本地兜底 score >= 60 (BMI>28 + 血糖>7.0 + 家族史)")
        void predict_HighRisk_LocalFallback() {
            // 使用已设置的高风险参数：Age=55(>50 → 15)
            // BMI 由计算: 80/(1.7*1.7)=27.68 (24-28 → 15)
            // 血糖 7.2 (>7.0 → 30)
            // 家族史=1 → 15
            // 合计 ≥ 60
            when(userMapper.selectById(100)).thenReturn(testUser);
            when(riskPredictionMapper.insert(any(RiskPrediction.class))).thenReturn(1);
            when(difyClient.runRiskWorkflow(anyMap(), eq("100")))
                    .thenThrow(new RuntimeException("fail"));

            RiskPrediction result = riskPredictionService.predict(testPrediction);

            // 本地算法得分: 血糖>7.0→30, BMI>24→15, 家族史→15, 年龄>50→15 = 75
            assertEquals("高风险", result.getRiskLevel());
            assertTrue(result.getRiskScore() >= 60);
        }
    }

    // ==================== riskTrend ====================

    @Test
    @DisplayName("riskTrend → 返回风险趋势数据")
    void riskTrend_ShouldReturnTrend() {
        List<Map<String, Object>> trend = List.of(
                Map.of("date", "2026-06-01", "avg_score", 45.0)
        );
        when(riskPredictionMapper.selectRiskTrend(100)).thenReturn(trend);

        List<Map<String, Object>> result = riskPredictionService.riskTrend(100);
        assertEquals(1, result.size());
        assertEquals(45.0, result.get(0).get("avg_score"));
    }

    // ==================== parseRiskResult 边界 ====================

    @Test
    @DisplayName("predict workflow 输出含 <think> 标签 → 自动剔除")
    void predict_ThinkTagInOutput_ShouldStrip() {
        when(userMapper.selectById(100)).thenReturn(testUser);
        when(riskPredictionMapper.insert(any(RiskPrediction.class))).thenReturn(1);

        Map<String, Object> outputs = new HashMap<>();
        outputs.put("result", "<think>需要分析</think>【中风险】|评分:45|建议:改善生活习惯");
        when(difyClient.runRiskWorkflow(anyMap(), eq("100"))).thenReturn(outputs);

        RiskPrediction result = riskPredictionService.predict(testPrediction);

        assertEquals("中风险", result.getRiskLevel());
        assertEquals(45, result.getRiskScore());
    }

    @Test
    @DisplayName("predict workflow 输出格式异常 → 兜底本地算法")
    void predict_MalformedOutput_ShouldFallback() {
        when(userMapper.selectById(100)).thenReturn(testUser);
        when(riskPredictionMapper.insert(any(RiskPrediction.class))).thenReturn(1);

        Map<String, Object> outputs = new HashMap<>();
        outputs.put("result", "格式不正确的输出");
        when(difyClient.runRiskWorkflow(anyMap(), eq("100"))).thenReturn(outputs);

        RiskPrediction result = riskPredictionService.predict(testPrediction);

        assertNotNull(result.getRiskLevel());
        assertNotNull(result.getRiskScore());
    }
}
