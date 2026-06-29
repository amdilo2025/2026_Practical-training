package com.diabetes.controller;

import com.diabetes.common.R;
import com.diabetes.entity.RiskPrediction;
import com.diabetes.service.RiskPredictionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/risk")
public class RiskPredictionController {

    @Autowired
    private RiskPredictionService riskPredictionService;

    @PostMapping("/predict")
    public R<RiskPrediction> predict(@RequestBody RiskPrediction prediction) {
        RiskPrediction result = riskPredictionService.predict(prediction);
        return R.success(result);
    }

    @GetMapping("/list/{userId}")
    public R<List<RiskPrediction>> listByUser(@PathVariable Integer userId) {
        return R.success(riskPredictionService.listByUser(userId));
    }

    @GetMapping("/trend/{userId}")
    public R<List<Map<String, Object>>> trend(@PathVariable Integer userId) {
        return R.success(riskPredictionService.riskTrend(userId));
    }

    @GetMapping("/get/{id}")
    public R<RiskPrediction> getById(@PathVariable Integer id) {
        return R.success(riskPredictionService.getById(id));
    }
}
