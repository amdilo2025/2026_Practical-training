package com.diabetes.service;

import com.diabetes.entity.LifePlan;
import java.util.List;

public interface LifePlanService {
    List<LifePlan> listByUser(Integer userId);
    LifePlan getLatestByType(Integer userId, String planType);
    int add(LifePlan plan);
    int delete(Integer id);
}
