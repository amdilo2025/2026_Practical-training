package com.diabetes.service.impl;

import com.diabetes.entity.HealthArticle;
import com.diabetes.mapper.HealthArticleMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * HealthArticleServiceImpl 单元测试 —— 目标覆盖率 100%
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("HealthArticleServiceImpl 健康资讯服务")
class HealthArticleServiceImplTest {

    @Mock
    private HealthArticleMapper healthArticleMapper;

    @InjectMocks
    private HealthArticleServiceImpl healthArticleService;

    private HealthArticle testArticle;

    @BeforeEach
    void setUp() {
        testArticle = new HealthArticle();
        testArticle.setId(1);
        testArticle.setTitle("糖尿病饮食指南");
        testArticle.setCategory("饮食");
        testArticle.setStatus("已发布");
        testArticle.setViews(100);
        testArticle.setAuthor("赵晓峰");
    }

    // ==================== listAll ====================

    @Nested
    @DisplayName("listAll")
    class ListAllTests {

        @Test
        @DisplayName("listAll status=null → 不筛选状态，按创建时间降序")
        void listAll_NullStatus_ShouldNotFilter() {
            when(healthArticleMapper.selectList(any())).thenReturn(List.of(testArticle));

            List<HealthArticle> result = healthArticleService.listAll(null);

            assertEquals(1, result.size());
            verify(healthArticleMapper).selectList(any());
        }

        @Test
        @DisplayName("listAll status='已发布' → 只返回已发布")
        void listAll_Published_ShouldFilterByStatus() {
            when(healthArticleMapper.selectList(any())).thenReturn(List.of(testArticle));

            List<HealthArticle> result = healthArticleService.listAll("已发布");

            assertEquals(1, result.size());
            assertEquals("已发布", result.get(0).getStatus());
        }

        @Test
        @DisplayName("listAll status 为空字符串 → 不筛选")
        void listAll_EmptyStatus_ShouldNotFilter() {
            when(healthArticleMapper.selectList(any())).thenReturn(List.of(testArticle));

            List<HealthArticle> result = healthArticleService.listAll("");

            assertEquals(1, result.size());
        }
    }

    // ==================== getById ====================

    @Test
    @DisplayName("getById → 返回文章")
    void getById_ShouldReturnArticle() {
        when(healthArticleMapper.selectById(1)).thenReturn(testArticle);
        HealthArticle result = healthArticleService.getById(1);
        assertNotNull(result);
        assertEquals("糖尿病饮食指南", result.getTitle());
    }

    // ==================== add ====================

    @Test
    @DisplayName("add views=null → 自动设置为 0")
    void add_NullViews_ShouldDefaultToZero() {
        HealthArticle newArticle = new HealthArticle();
        newArticle.setTitle("新文章");
        assertNull(newArticle.getViews());

        when(healthArticleMapper.insert(any(HealthArticle.class))).thenReturn(1);
        healthArticleService.add(newArticle);

        assertEquals(0, newArticle.getViews(), "views 为 null 时应自动设为 0");
    }

    @Test
    @DisplayName("add views 已有值 → 保留原值")
    void add_WithViews_ShouldKeepOriginal() {
        testArticle.setViews(50);
        when(healthArticleMapper.insert(testArticle)).thenReturn(1);
        healthArticleService.add(testArticle);
        assertEquals(50, testArticle.getViews(), "views 已有值时不应修改");
    }

    // ==================== update ====================

    @Test
    @DisplayName("update → 调用 updateById")
    void update_ShouldCallUpdateById() {
        when(healthArticleMapper.updateById(testArticle)).thenReturn(1);
        int result = healthArticleService.update(testArticle);
        assertEquals(1, result);
    }

    // ==================== delete ====================

    @Test
    @DisplayName("delete → 调用 deleteById")
    void delete_ShouldCallDeleteById() {
        when(healthArticleMapper.deleteById(1)).thenReturn(1);
        int result = healthArticleService.delete(1);
        assertEquals(1, result);
    }

    // ==================== incrementViews ====================

    @Nested
    @DisplayName("incrementViews")
    class IncrementViewsTests {

        @Test
        @DisplayName("incrementViews 文章存在 → views +1")
        void incrementViews_Exists_ShouldIncrement() {
            when(healthArticleMapper.selectById(1)).thenReturn(testArticle);
            when(healthArticleMapper.updateById((HealthArticle) any())).thenReturn(1);

            int result = healthArticleService.incrementViews(1);

            assertEquals(1, result);
            assertEquals(101, testArticle.getViews());
        }

        @Test
        @DisplayName("incrementViews 文章不存在 → 返回 0")
        void incrementViews_NotFound_ShouldReturnZero() {
            when(healthArticleMapper.selectById(999)).thenReturn(null);

            int result = healthArticleService.incrementViews(999);

            assertEquals(0, result);
            verify(healthArticleMapper, never()).updateById(any(HealthArticle.class));
        }

        @Test
        @DisplayName("incrementViews views=null → 设为 1")
        void incrementViews_NullViews_ShouldSetToOne() {
            testArticle.setViews(null);
            when(healthArticleMapper.selectById(1)).thenReturn(testArticle);
            when(healthArticleMapper.updateById((HealthArticle) any())).thenReturn(1);

            healthArticleService.incrementViews(1);

            assertEquals(1, testArticle.getViews());
        }
    }

    // ==================== findByCategory ====================

    @Test
    @DisplayName("findByCategory → 返回分类文章")
    void findByCategory_ShouldReturnArticles() {
        when(healthArticleMapper.findByCategory("饮食")).thenReturn(List.of(testArticle));
        List<HealthArticle> result = healthArticleService.findByCategory("饮食");
        assertEquals(1, result.size());
        assertEquals("饮食", result.get(0).getCategory());
    }

    // ==================== countByCategory ====================

    @Test
    @DisplayName("countByCategory → 返回分类统计")
    void countByCategory_ShouldReturnStats() {
        List<Map<String, Object>> stats = List.of(
                Map.of("category", "饮食", "count", 5L)
        );
        when(healthArticleMapper.countByCategory()).thenReturn(stats);
        List<Map<String, Object>> result = healthArticleService.countByCategory();
        assertEquals(1, result.size());
        assertEquals("饮食", result.get(0).get("category"));
    }
}
