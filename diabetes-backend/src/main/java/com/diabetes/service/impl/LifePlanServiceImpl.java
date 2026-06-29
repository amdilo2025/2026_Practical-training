package com.diabetes.service.impl;

import com.diabetes.entity.LifePlan;
import com.diabetes.mapper.LifePlanMapper;
import com.diabetes.service.LifePlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LifePlanServiceImpl implements LifePlanService {

    @Autowired
    private LifePlanMapper lifePlanMapper;

    @Override
    public List<LifePlan> listByUser(Integer userId) {
        return lifePlanMapper.findValidByUser(userId);
    }

    @Override
    public LifePlan getLatestByType(Integer userId, String planType) {
        return lifePlanMapper.findLatestByType(userId, planType);
    }

    @Override
    public int add(LifePlan plan) {
        plan.setStatus("有效");
        return lifePlanMapper.insert(plan);
    }

    @Override
    public int delete(Integer id) {
        return lifePlanMapper.deleteById(id);
    }
}
