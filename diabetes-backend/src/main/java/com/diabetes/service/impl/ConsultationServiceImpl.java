package com.diabetes.service.impl;

import com.diabetes.entity.Consultation;
import com.diabetes.entity.Doctor;
import com.diabetes.entity.User;
import com.diabetes.mapper.ConsultationMapper;
import com.diabetes.mapper.DoctorMapper;
import com.diabetes.mapper.UserMapper;
import com.diabetes.service.ConsultationService;
import com.diabetes.utils.DifyClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ConsultationServiceImpl implements ConsultationService {

    @Autowired
    private ConsultationMapper consultationMapper;

    @Autowired
    private DoctorMapper doctorMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DifyClient difyClient;

    @Value("${dify.doctor-api-key:}")
    private String doctorApiKey;

    @Override
    public List<Map<String, Object>> listByUser(Integer userId) {
        return consultationMapper.selectConsultationsWithDoctor(userId);
    }

    @Override
    public List<Map<String, Object>> listAll() {
        return consultationMapper.selectAllWithUser();
    }

    @Override
    public Consultation getById(Integer id) {
        return consultationMapper.selectById(id);
    }

    @Override
    public int add(Consultation consultation) {
        // 识别是否为 AI 医生（赵晓峰），若是则即时调用 Dify 生成回复
        Doctor doctor = doctorMapper.selectById(consultation.getDoctorId());
        if (doctor != null && doctor.getIsAi() != null && doctor.getIsAi() == 1) {
            User user = userMapper.selectById(consultation.getUserId());
            Map<String, Object> inputs = new HashMap<>();
            if (user != null) {
                inputs.put("userId", String.valueOf(user.getId()));
                inputs.put("sex", user.getGender() == null ? "男" : user.getGender());
                inputs.put("age", user.getAge() == null ? 0 : user.getAge());
                inputs.put("height", user.getHeight() == null ? 0 : user.getHeight());
                inputs.put("weight", user.getWeight() == null ? 0 : user.getWeight());
                inputs.put("familyHistory", (user.getFamilyHistory() != null && user.getFamilyHistory() == 1) ? "有" : "无");
                inputs.put("disease", (user.getDiabetesType() != null && !user.getDiabetesType().isEmpty()) ? "是" : "否");
            }
            Map<String, String> ai = difyClient.chatWith(
                    consultation.getContent(),
                    String.valueOf(consultation.getUserId()),
                    "new", inputs, doctorApiKey);
            consultation.setReply(ai.getOrDefault("answer", "抱歉，暂时无法回复，请稍后再试。"));
            consultation.setStatus("已回复");
            consultation.setReplyTime(LocalDateTime.now());
        } else {
            consultation.setStatus("待回复");
        }
        return consultationMapper.insert(consultation);
    }

    @Override
    public int reply(Integer id, String reply) {
        Consultation consultation = consultationMapper.selectById(id);
        if (consultation != null) {
            consultation.setReply(reply);
            consultation.setStatus("已回复");
            consultation.setReplyTime(LocalDateTime.now());
            return consultationMapper.updateById(consultation);
        }
        return 0;
    }

    @Override
    public Integer countByStatus(String status) {
        return consultationMapper.countByStatus(status);
    }
}
