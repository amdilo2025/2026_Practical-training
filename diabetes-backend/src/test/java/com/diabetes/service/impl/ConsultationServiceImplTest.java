package com.diabetes.service.impl;

import com.diabetes.entity.Consultation;
import com.diabetes.entity.Doctor;
import com.diabetes.entity.User;
import com.diabetes.mapper.ConsultationMapper;
import com.diabetes.mapper.DoctorMapper;
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
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * ConsultationServiceImpl 单元测试 —— 目标覆盖率 100%
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ConsultationServiceImpl 咨询管理服务")
class ConsultationServiceImplTest {

    @Mock
    private ConsultationMapper consultationMapper;

    @Mock
    private DoctorMapper doctorMapper;

    @Mock
    private UserMapper userMapper;

    @Mock
    private DifyClient difyClient;

    @InjectMocks
    private ConsultationServiceImpl consultationService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(consultationService, "doctorApiKey", "app-test-doctor-key");
    }

    @Test
    @DisplayName("listByUser → 返回用户咨询列表（含医生信息）")
    void listByUser_ShouldReturnConsultationsWithDoctor() {
        List<Map<String, Object>> list = List.of(
                Map.of("content", "血糖偏高怎么办", "doctor_name", "赵晓峰")
        );
        when(consultationMapper.selectConsultationsWithDoctor(100)).thenReturn(list);

        List<Map<String, Object>> result = consultationService.listByUser(100);
        assertEquals(1, result.size());
        assertEquals("赵晓峰", result.get(0).get("doctor_name"));
    }

    @Test
    @DisplayName("listAll → 返回所有咨询（含用户信息）")
    void listAll_ShouldReturnAllConsultations() {
        List<Map<String, Object>> list = List.of(
                Map.of("content", "咨询内容", "user_name", "张三")
        );
        when(consultationMapper.selectAllWithUser()).thenReturn(list);

        List<Map<String, Object>> result = consultationService.listAll();
        assertEquals(1, result.size());
        assertEquals("张三", result.get(0).get("user_name"));
    }

    @Test
    @DisplayName("getById → 返回咨询")
    void getById_ShouldReturnConsultation() {
        Consultation consultation = new Consultation();
        consultation.setId(1);
        consultation.setContent("咨询内容");

        when(consultationMapper.selectById(1)).thenReturn(consultation);
        Consultation result = consultationService.getById(1);
        assertNotNull(result);
        assertEquals("咨询内容", result.getContent());
    }

    // ==================== add (含 AI 医生自动回复) ====================

    @Nested
    @DisplayName("add 提交咨询")
    class AddTests {

        @Test
        @DisplayName("add AI 医生 → 自动调用 Dify 生成回复，status='已回复'")
        void add_AiDoctor_ShouldAutoReply() {
            // 准备 AI 医生
            Doctor aiDoctor = new Doctor();
            aiDoctor.setId(10);
            aiDoctor.setIsAi(1);

            // 准备用户
            User user = createTestUser();

            // 准备咨询
            Consultation consultation = new Consultation();
            consultation.setUserId(100);
            consultation.setDoctorId(10);
            consultation.setContent("血糖怎么控制？");

            Map<String, String> aiResponse = new HashMap<>();
            aiResponse.put("answer", "建议控制饮食，增加运动...");

            when(doctorMapper.selectById(10)).thenReturn(aiDoctor);
            when(userMapper.selectById(100)).thenReturn(user);
            when(difyClient.chatWith(anyString(), anyString(), eq("new"), anyMap(), anyString()))
                    .thenReturn(aiResponse);
            when(consultationMapper.insert(any(Consultation.class))).thenReturn(1);

            consultationService.add(consultation);

            assertEquals("已回复", consultation.getStatus());
            assertNotNull(consultation.getReply());
            assertNotNull(consultation.getReplyTime());
            verify(difyClient).chatWith(anyString(), anyString(), eq("new"), anyMap(), anyString());
        }

        @Test
        @DisplayName("add 普通医生 → status='待回复'，不调用 Dify")
        void add_RegularDoctor_ShouldPending() {
            Doctor regularDoctor = new Doctor();
            regularDoctor.setId(20);
            regularDoctor.setIsAi(0);

            Consultation consultation = new Consultation();
            consultation.setUserId(100);
            consultation.setDoctorId(20);
            consultation.setContent("咨询内容");

            when(doctorMapper.selectById(20)).thenReturn(regularDoctor);
            when(consultationMapper.insert(any(Consultation.class))).thenReturn(1);

            consultationService.add(consultation);

            assertEquals("待回复", consultation.getStatus());
            verify(difyClient, never()).chatWith(anyString(), anyString(), anyString(), anyMap(), anyString());
        }

        @Test
        @DisplayName("add AI 医生但医生不存在 → status='待回复'")
        void add_AiDoctorNotFound_ShouldPending() {
            when(doctorMapper.selectById(10)).thenReturn(null);

            Consultation consultation = new Consultation();
            consultation.setUserId(100);
            consultation.setDoctorId(10);
            consultation.setContent("咨询内容");

            when(consultationMapper.insert(any(Consultation.class))).thenReturn(1);

            consultationService.add(consultation);

            assertEquals("待回复", consultation.getStatus());
        }

        @Test
        @DisplayName("add AI 医 Dify 返回空 answer → 使用兜底文案")
        void add_AiDoctorEmptyAnswer_ShouldUseFallback() {
            Doctor aiDoctor = new Doctor();
            aiDoctor.setId(10);
            aiDoctor.setIsAi(1);

            User user = createTestUser();

            Map<String, String> aiResponse = new HashMap<>();
            aiResponse.put("answer", ""); // 空回答

            when(doctorMapper.selectById(10)).thenReturn(aiDoctor);
            when(userMapper.selectById(100)).thenReturn(user);
            when(difyClient.chatWith(anyString(), anyString(), eq("new"), anyMap(), anyString()))
                    .thenReturn(aiResponse);
            when(consultationMapper.insert(any(Consultation.class))).thenReturn(1);

            Consultation consultation = new Consultation();
            consultation.setUserId(100);
            consultation.setDoctorId(10);
            consultation.setContent("测试");

            consultationService.add(consultation);

            assertEquals("已回复", consultation.getStatus());
            assertNotNull(consultation.getReply());
        }
    }

    // ==================== reply ====================

    @Nested
    @DisplayName("reply 回复咨询")
    class ReplyTests {

        @Test
        @DisplayName("reply 咨询存在 → 更新状态和回复时间")
        void reply_ConsultationExists_ShouldUpdate() {
            Consultation consultation = new Consultation();
            consultation.setId(1);
            consultation.setStatus("待回复");

            when(consultationMapper.selectById(1)).thenReturn(consultation);
            when(consultationMapper.updateById((Consultation) any())).thenReturn(1);

            int result = consultationService.reply(1, "这是医生的回复");

            assertEquals(1, result);
            assertEquals("已回复", consultation.getStatus());
            assertEquals("这是医生的回复", consultation.getReply());
            assertNotNull(consultation.getReplyTime());
        }

        @Test
        @DisplayName("reply 咨询不存在 → 返回 0")
        void reply_ConsultationNotFound_ShouldReturnZero() {
            when(consultationMapper.selectById(999)).thenReturn(null);

            int result = consultationService.reply(999, "回复内容");
            assertEquals(0, result);
            verify(consultationMapper, never()).updateById(any(Consultation.class));
        }
    }

    // ==================== countByStatus ====================

    @Test
    @DisplayName("countByStatus → 返回指定状态数量")
    void countByStatus_ShouldReturnCount() {
        when(consultationMapper.countByStatus("待回复")).thenReturn(5);
        assertEquals(5, consultationService.countByStatus("待回复"));
    }

    private User createTestUser() {
        User user = new User();
        user.setId(100);
        user.setGender("男");
        user.setAge(45);
        user.setHeight(170.0);
        user.setWeight(75.0);
        user.setFamilyHistory(1);
        user.setDiabetesType("2型糖尿病");
        return user;
    }
}
