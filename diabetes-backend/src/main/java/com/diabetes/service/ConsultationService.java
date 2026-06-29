package com.diabetes.service;

import com.diabetes.entity.Consultation;
import java.util.List;
import java.util.Map;

public interface ConsultationService {
    List<Map<String, Object>> listByUser(Integer userId);
    List<Map<String, Object>> listAll();
    Consultation getById(Integer id);
    int add(Consultation consultation);
    int reply(Integer id, String reply);
    Integer countByStatus(String status);
}
