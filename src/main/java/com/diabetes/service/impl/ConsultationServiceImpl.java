package com.diabetes.service.impl;

import com.diabetes.entity.Consultation;
import com.diabetes.mapper.ConsultationMapper;
import com.diabetes.service.ConsultationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class ConsultationServiceImpl implements ConsultationService {

    @Autowired
    private ConsultationMapper consultationMapper;

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
        consultation.setStatus("待回复");
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
