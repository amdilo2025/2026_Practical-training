package com.diabetes.service.impl;

import com.diabetes.entity.LifePlan;
import com.diabetes.mapper.LifePlanMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * LifePlanServiceImpl 单元测试 —— 目标覆盖率 100%
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("LifePlanServiceImpl 生活方案服务")
class LifePlanServiceImplTest {

    @Mock
    private LifePlanMapper lifePlanMapper;

    @InjectMocks
    private LifePlanServiceImpl lifePlanService;

    private LifePlan testPlan;

    @BeforeEach
    void setUp() {
        testPlan = new LifePlan();
        testPlan.setId(1);
        testPlan.setUserId(100);
        testPlan.setPlanType("饮食");
        testPlan.setTitle("糖尿病饮食计划");
        testPlan.setContent("少食多餐，控制碳水化合物摄入");
        testPlan.setStatus("有效");
    }

    @Test
    @DisplayName("listByUser → 返回用户有效方案")
    void listByUser_ShouldReturnValidPlans() {
        when(lifePlanMapper.findValidByUser(100)).thenReturn(List.of(testPlan));
        List<LifePlan> result = lifePlanService.listByUser(100);
        assertEquals(1, result.size());
        assertEquals("饮食", result.get(0).getPlanType());
        verify(lifePlanMapper).findValidByUser(100);
    }

    @Test
    @DisplayName("listByUser 无方案 → 返回空列表")
    void listByUser_Empty_ShouldReturnEmptyList() {
        when(lifePlanMapper.findValidByUser(999)).thenReturn(List.of());
        List<LifePlan> result = lifePlanService.listByUser(999);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("getLatestByType → 返回最新方案")
    void getLatestByType_ShouldReturnLatest() {
        when(lifePlanMapper.findLatestByType(100, "饮食")).thenReturn(testPlan);
        LifePlan result = lifePlanService.getLatestByType(100, "饮食");
        assertNotNull(result);
        assertEquals("糖尿病饮食计划", result.getTitle());
    }

    @Test
    @DisplayName("getLatestByType 无方案 → 返回 null")
    void getLatestByType_None_ShouldReturnNull() {
        when(lifePlanMapper.findLatestByType(100, "运动")).thenReturn(null);
        LifePlan result = lifePlanService.getLatestByType(100, "运动");
        assertNull(result);
    }

    @Test
    @DisplayName("add → 自动设置 status='有效' 后插入")
    void add_ShouldSetStatusAndInsert() {
        LifePlan newPlan = new LifePlan();
        newPlan.setUserId(100);
        newPlan.setPlanType("运动");
        newPlan.setTitle("运动计划");

        when(lifePlanMapper.insert(any(LifePlan.class))).thenReturn(1);

        int result = lifePlanService.add(newPlan);

        assertEquals(1, result);
        assertEquals("有效", newPlan.getStatus(), "add 应自动设置 status 为 '有效'");
        verify(lifePlanMapper).insert(newPlan);
    }

    @Test
    @DisplayName("delete → 调用 deleteById")
    void delete_ShouldCallDeleteById() {
        when(lifePlanMapper.deleteById(1)).thenReturn(1);
        int result = lifePlanService.delete(1);
        assertEquals(1, result);
        verify(lifePlanMapper).deleteById(1);
    }
}
