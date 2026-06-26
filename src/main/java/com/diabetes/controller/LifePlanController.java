package com.diabetes.controller;

import com.diabetes.common.R;
import com.diabetes.entity.LifePlan;
import com.diabetes.service.LifePlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plan")
public class LifePlanController {

    @Autowired
    private LifePlanService lifePlanService;

    @GetMapping("/list/{userId}")
    public R<List<LifePlan>> listByUser(@PathVariable Integer userId) {
        return R.success(lifePlanService.listByUser(userId));
    }

    @GetMapping("/latest/{userId}/{planType}")
    public R<LifePlan> getLatest(@PathVariable Integer userId, @PathVariable String planType) {
        LifePlan plan = lifePlanService.getLatestByType(userId, planType);
        return plan != null ? R.success(plan) : R.error("暂无方案");
    }

    @PostMapping("/add")
    public R<String> add(@RequestBody LifePlan plan) {
        lifePlanService.add(plan);
        return R.success("方案已生成");
    }

    @DeleteMapping("/delete/{id}")
    public R<String> delete(@PathVariable Integer id) {
        lifePlanService.delete(id);
        return R.success("删除成功");
    }
}
