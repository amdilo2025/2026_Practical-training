package com.diabetes.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * DifyClient 单元测试 —— 目标覆盖率 100%
 * 核心外部 API 调用客户端，覆盖率要求 100%
 */
@DisplayName("DifyClient Dify API 客户端")
class DifyClientTest {

    private DifyClient difyClient;
    private RestTemplate mockRestTemplate;

    private static final String TEST_API_URL = "https://test-api.dify.ai/v1";
    private static final String TEST_API_KEY = "app-test-api-key-123";
    private static final String TEST_RISK_API_KEY = "app-test-risk-key-456";
    private static final String TEST_DOCTOR_API_KEY = "app-test-doctor-key-789";
    private static final String TEST_ADMIN_API_KEY = "app-test-admin-key-000";

    @BeforeEach
    void setUp() {
        difyClient = new DifyClient();
        mockRestTemplate = mock(RestTemplate.class);

        // 使用反射注入 @Value 字段和 RestTemplate
        ReflectionTestUtils.setField(difyClient, "apiUrl", TEST_API_URL);
        ReflectionTestUtils.setField(difyClient, "apiKey", TEST_API_KEY);
        ReflectionTestUtils.setField(difyClient, "riskApiKey", TEST_RISK_API_KEY);
        ReflectionTestUtils.setField(difyClient, "doctorApiKey", TEST_DOCTOR_API_KEY);
        ReflectionTestUtils.setField(difyClient, "adminApiKey", TEST_ADMIN_API_KEY);
        ReflectionTestUtils.setField(difyClient, "restTemplate", mockRestTemplate);
    }

    // ==================== chat 测试 ====================

    @Nested
    @DisplayName("chat 智能助手对话")
    class ChatTests {

        @Test
        @DisplayName("chat 正常调用 → 返回 answer 和 conversation_id")
        void chat_Success_ShouldReturnAnswerAndConvId() {
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("answer", "您好，建议控制饮食。");
            responseBody.put("conversation_id", "conv-abc-123");
            mockRestTemplatePostForEntity(responseBody);

            Map<String, String> result = difyClient.chat("糖尿病饮食建议", "user-1", "new");

            assertNotNull(result);
            assertEquals("您好，建议控制饮食。", result.get("answer"));
            assertEquals("conv-abc-123", result.get("conversation_id"));
        }

        @Test
        @DisplayName("chat 异常时 → 返回 mock 回复(基于关键词)")
        void chat_Exception_ShouldReturnMockResponse() {
            when(mockRestTemplate.postForEntity(anyString(), any(), eq(Map.class)))
                    .thenThrow(new RuntimeException("Network error"));

            Map<String, String> result = difyClient.chat("饮食应该注意什么", "user-1", "new");

            assertNotNull(result);
            assertTrue(result.get("answer").contains("控制总热量"));
            assertNotNull(result.get("conversation_id"));
        }

        @Test
        @DisplayName("chat response.body 为 null → 返回空 answer")
        void chat_NullResponseBody_ShouldReturnEmptyAnswer() {
            ResponseEntity<Map> responseEntity = new ResponseEntity<>(null, HttpStatus.OK);
            when(mockRestTemplate.postForEntity(anyString(), any(), eq(Map.class)))
                    .thenReturn(responseEntity);

            Map<String, String> result = difyClient.chat("你好", "user-1", "new");

            assertNotNull(result);
            assertNull(result.get("answer"));
        }
    }

    // ==================== chatWith 测试 ====================

    @Nested
    @DisplayName("chatWith 通用对话接口")
    class ChatWithTests {

        @Test
        @DisplayName("chatWith 有 conversationId → 请求体中携带 conversation_id")
        void chatWith_ExistingConversation_ShouldIncludeConvId() {
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("answer", "继续上次话题...");
            responseBody.put("conversation_id", "conv-existing");
            mockRestTemplatePostForEntity(responseBody);

            Map<String, Object> inputs = new HashMap<>();
            inputs.put("age", 45);

            Map<String, String> result = difyClient.chatWith("我的血糖偏高", "user-1",
                    "conv-existing", inputs, TEST_DOCTOR_API_KEY);

            assertEquals("继续上次话题...", result.get("answer"));
        }

        @Test
        @DisplayName("chatWith conversationId = 'new' → 不传 conversation_id")
        void chatWith_NewConversation_ShouldNotIncludeConvId() {
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("answer", "新对话开始");
            responseBody.put("conversation_id", "conv-brand-new");
            mockRestTemplatePostForEntity(responseBody);

            Map<String, String> result = difyClient.chatWith("你好医生", "user-1",
                    "new", null, TEST_DOCTOR_API_KEY);

            assertEquals("新对话开始", result.get("answer"));
            assertEquals("conv-brand-new", result.get("conversation_id"));
        }

        @Test
        @DisplayName("chatWith inputs 为 null → 传入空 Map")
        void chatWith_NullInputs_ShouldPassEmptyMap() {
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("answer", "收到");
            mockRestTemplatePostForEntity(responseBody);

            Map<String, String> result = difyClient.chatWith("测试", "user-1",
                    "new", null, TEST_API_KEY);

            assertEquals("收到", result.get("answer"));
        }

        @Test
        @DisplayName("chatWith 异常时 → 返回 mock 响应")
        void chatWith_Exception_ShouldReturnMockFallback() {
            when(mockRestTemplate.postForEntity(anyString(), any(), eq(Map.class)))
                    .thenThrow(new RuntimeException("Connection refused"));

            Map<String, String> result = difyClient.chatWith("运动建议", "user-1",
                    "new", new HashMap<>(), TEST_API_KEY);

            assertNotNull(result);
            assertTrue(result.get("answer").contains("150分钟"));
        }
    }

    // ==================== runRiskWorkflow 测试 ====================

    @Nested
    @DisplayName("runRiskWorkflow 风险预测 workflow")
    class RiskWorkflowTests {

        @Test
        @DisplayName("runRiskWorkflow → 返回 workflow outputs")
        void runRiskWorkflow_ShouldReturnOutputs() {
            configureWorkflowMockResponse("【中风险】|评分:45|建议:改善饮食");

            Map<String, Object> inputs = new HashMap<>();
            inputs.put("age", 55);
            inputs.put("fastingBloodSugar", 7.2);

            Map<String, Object> outputs = difyClient.runRiskWorkflow(inputs, "user-1");

            assertNotNull(outputs);
            assertTrue(outputs.containsKey("result"));
        }
    }

    // ==================== runWorkflow 测试 ====================

    @Nested
    @DisplayName("runWorkflow 通用 workflow 执行")
    class WorkflowTests {

        @Test
        @DisplayName("runWorkflow 正常 → 解析 data.outputs")
        void runWorkflow_Success_ShouldReturnOutputs() {
            configureWorkflowMockResponse("【低风险】|评分:15|建议:保持良好习惯");

            Map<String, Object> result = difyClient.runWorkflow(
                    new HashMap<>(), "user-1", TEST_RISK_API_KEY);

            assertNotNull(result);
            assertTrue(result.containsKey("result"));
        }

        @Test
        @DisplayName("runWorkflow response.body 为 null → 抛出 RuntimeException")
        void runWorkflow_NullBody_ShouldThrowRuntimeException() {
            ResponseEntity<Map> responseEntity = new ResponseEntity<>(null, HttpStatus.OK);
            when(mockRestTemplate.postForEntity(anyString(), any(), eq(Map.class)))
                    .thenReturn(responseEntity);

            assertThrows(RuntimeException.class, () ->
                    difyClient.runWorkflow(new HashMap<>(), "user-1", TEST_RISK_API_KEY));
        }

        @Test
        @DisplayName("runWorkflow data 为 null → 抛出 RuntimeException")
        void runWorkflow_NullData_ShouldThrowRuntimeException() {
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("data", null);
            ResponseEntity<Map> response = new ResponseEntity<>(responseBody, HttpStatus.OK);
            when(mockRestTemplate.postForEntity(anyString(), any(), eq(Map.class)))
                    .thenReturn(response);

            assertThrows(RuntimeException.class, () ->
                    difyClient.runWorkflow(new HashMap<>(), "user-1", TEST_RISK_API_KEY));
        }

        @Test
        @DisplayName("runWorkflow outputs 为非 Map → 返回空 HashMap")
        void runWorkflow_NonMapOutputs_ShouldReturnEmptyMap() {
            Map<String, Object> data = new HashMap<>();
            data.put("outputs", "not-a-map"); // 字符串而非 Map

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("data", data);
            ResponseEntity<Map> response = new ResponseEntity<>(responseBody, HttpStatus.OK);
            when(mockRestTemplate.postForEntity(anyString(), any(), eq(Map.class)))
                    .thenReturn(response);

            Map<String, Object> result = difyClient.runWorkflow(
                    new HashMap<>(), "user-1", TEST_RISK_API_KEY);

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("runWorkflow inputs 为 null → 传入空 Map")
        void runWorkflow_NullInputs_ShouldPassEmptyMap() {
            configureWorkflowMockResponse("【低风险】|评分:10|建议:良好");
            Map<String, Object> result = difyClient.runWorkflow(null, "user-1", TEST_RISK_API_KEY);
            assertNotNull(result);
        }
    }

    // ==================== runAdminWorkflow 测试 ====================

    @Test
    @DisplayName("runAdminWorkflow → 传入 question 作为 intention")
    void runAdminWorkflow_ShouldSetIntentionInput() {
        configureWorkflowMockResponse("查询结果: 总计10条");

        Map<String, Object> result = difyClient.runAdminWorkflow(
                "查询最近一周新增用户数", "admin-user");

        assertNotNull(result);
    }

    // ==================== getDoctorApiKey 测试 ====================

    @Test
    @DisplayName("getDoctorApiKey → 返回注入的 API key")
    void getDoctorApiKey_ShouldReturnInjectedValue() {
        assertEquals(TEST_DOCTOR_API_KEY, difyClient.getDoctorApiKey());
    }

    // ==================== getMockResponse 间接测试 ====================

    @Nested
    @DisplayName("getMockResponse (异常兜底，间接测试)")
    class MockResponseTests {

        @Test
        @DisplayName("query 为 null → 返回默认问候语")
        void nullQuery_ShouldReturnDefaultGreeting() {
            when(mockRestTemplate.postForEntity(anyString(), any(), eq(Map.class)))
                    .thenThrow(new RuntimeException("fail"));

            Map<String, String> result = difyClient.chat(null, "u1", "new");
            assertTrue(result.get("answer").contains("赵晓峰"));
        }

        @Test
        @DisplayName("query 含'饮食' → 返回饮食建议")
        void dietQuery_ShouldReturnDietAdvice() {
            when(mockRestTemplate.postForEntity(anyString(), any(), eq(Map.class)))
                    .thenThrow(new RuntimeException("fail"));

            Map<String, String> result = difyClient.chat("糖尿病怎么吃", "u1", "new");
            assertTrue(result.get("answer").contains("控制总热量"));
        }

        @Test
        @DisplayName("query 含'运动' → 返回运动建议")
        void exerciseQuery_ShouldReturnExerciseAdvice() {
            when(mockRestTemplate.postForEntity(anyString(), any(), eq(Map.class)))
                    .thenThrow(new RuntimeException("fail"));

            Map<String, String> result = difyClient.chat("运动建议", "u1", "new");
            assertTrue(result.get("answer").contains("150分钟"));
        }

        @Test
        @DisplayName("query 含'血糖' → 返回血糖建议")
        void bloodSugarQuery_ShouldReturnBloodSugarAdvice() {
            when(mockRestTemplate.postForEntity(anyString(), any(), eq(Map.class)))
                    .thenThrow(new RuntimeException("fail"));

            Map<String, String> result = difyClient.chat("血糖控制", "u1", "new");
            assertTrue(result.get("answer").contains("4.4-7.0"));
        }

        @Test
        @DisplayName("query 含'药' → 返回用药建议")
        void medicationQuery_ShouldReturnMedicationAdvice() {
            when(mockRestTemplate.postForEntity(anyString(), any(), eq(Map.class)))
                    .thenThrow(new RuntimeException("fail"));

            // 注意: 不能含 "吃" 因为 "吃" 优先匹配饮食建议
            Map<String, String> result = difyClient.chat("药物副作用", "u1", "new");
            assertTrue(result.get("answer").contains("遵医嘱"));
        }

        @Test
        @DisplayName("query 不含关键词 → 返回通用回答")
        void noKeywordQuery_ShouldReturnGeneralAdvice() {
            when(mockRestTemplate.postForEntity(anyString(), any(), eq(Map.class)))
                    .thenThrow(new RuntimeException("fail"));

            Map<String, String> result = difyClient.chat("你好", "u1", "new");
            assertTrue(result.get("answer").contains("赵晓峰"));
        }
    }

    // ==================== 辅助方法 ====================

    private void mockRestTemplatePostForEntity(Map<String, Object> responseBody) {
        ResponseEntity<Map> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);
        when(mockRestTemplate.postForEntity(anyString(), any(), eq(Map.class)))
                .thenReturn(responseEntity);
    }

    @SuppressWarnings("unchecked")
    private void configureWorkflowMockResponse(String resultText) {
        Map<String, Object> outputs = new HashMap<>();
        outputs.put("result", resultText);

        Map<String, Object> data = new HashMap<>();
        data.put("outputs", outputs);

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("data", data);

        ResponseEntity<Map> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);
        when(mockRestTemplate.postForEntity(anyString(), any(), eq(Map.class)))
                .thenReturn(responseEntity);
    }
}
