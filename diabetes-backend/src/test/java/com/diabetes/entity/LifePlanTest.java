package com.diabetes.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * LifePlan 实体单元测试 —— 目标覆盖率 100%
 */
@DisplayName("LifePlan 实体")
class LifePlanTest {

    @Test
    @DisplayName("全字段 setter/getter 正常读写")
    void allFields_SetterGetter_ShouldWork() {
        LifePlan plan = new LifePlan();

        plan.setId(1);
        plan.setUserId(100);
        plan.setPlanType("饮食");
        plan.setTitle("糖尿病饮食计划");
        plan.setContent("早餐：燕麦粥+鸡蛋；午餐：杂粮饭+清蒸鱼...");
        plan.setStatus("有效");

        LocalDateTime now = LocalDateTime.now();
        plan.setCreatedTime(now);

        assertEquals(1, plan.getId());
        assertEquals(100, plan.getUserId());
        assertEquals("饮食", plan.getPlanType());
        assertEquals("糖尿病饮食计划", plan.getTitle());
        assertEquals("早餐：燕麦粥+鸡蛋；午餐：杂粮饭+清蒸鱼...", plan.getContent());
        assertEquals("有效", plan.getStatus());
        assertEquals(now, plan.getCreatedTime());
    }

    @Test
    @DisplayName("默认初始值为 null")
    void defaultValues_ShouldBeNull() {
        LifePlan plan = new LifePlan();

        assertNull(plan.getId());
        assertNull(plan.getUserId());
        assertNull(plan.getPlanType());
        assertNull(plan.getTitle());
        assertNull(plan.getContent());
        assertNull(plan.getStatus());
        assertNull(plan.getCreatedTime());
    }

    @Test
    @DisplayName("planType = '运动' → 运动计划")
    void exercisePlan_ShouldWork() {
        LifePlan plan = new LifePlan();
        plan.setPlanType("运动");
        plan.setTitle("每周运动计划");
        assertEquals("运动", plan.getPlanType());
        assertEquals("每周运动计划", plan.getTitle());
    }

    @Test
    @DisplayName("planType = '用药' → 用药计划")
    void medicationPlan_ShouldWork() {
        LifePlan plan = new LifePlan();
        plan.setPlanType("用药");
        plan.setTitle("二甲双胍用药计划");
        assertEquals("用药", plan.getPlanType());
    }

    @Test
    @DisplayName("status = '失效' → 已失效方案")
    void invalidPlan_ShouldWork() {
        LifePlan plan = new LifePlan();
        plan.setStatus("失效");
        assertEquals("失效", plan.getStatus());
    }
}
