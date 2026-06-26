package com.diabetes.service;

import com.diabetes.entity.RiskPrediction;
import java.util.List;
import java.util.Map;

public interface RiskPredictionService {
    RiskPrediction getById(Integer id);
    List<RiskPrediction> listByUser(Integer userId);
    RiskPrediction predict(RiskPrediction prediction);
    List<Map<String, Object>> riskTrend(Integer userId);
}
