package com.diabetes.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * HealthArticle 实体单元测试 —— 目标覆盖率 100%
 */
@DisplayName("HealthArticle 实体")
class HealthArticleTest {

    @Test
    @DisplayName("全字段 setter/getter 正常读写")
    void allFields_SetterGetter_ShouldWork() {
        HealthArticle article = new HealthArticle();

        article.setId(1);
        article.setTitle("糖尿病饮食指南");
        article.setSummary("本文介绍糖尿病饮食基本原则");
        article.setContent("糖尿病是一种慢性代谢性疾病...");
        article.setCover("/covers/diet.jpg");
        article.setCategory("饮食");
        article.setAuthor("赵晓峰");
        article.setViews(150);
        article.setStatus("已发布");

        LocalDateTime now = LocalDateTime.now();
        article.setCreatedTime(now);
        article.setUpdatedTime(now);

        assertEquals(1, article.getId());
        assertEquals("糖尿病饮食指南", article.getTitle());
        assertEquals("本文介绍糖尿病饮食基本原则", article.getSummary());
        assertEquals("糖尿病是一种慢性代谢性疾病...", article.getContent());
        assertEquals("/covers/diet.jpg", article.getCover());
        assertEquals("饮食", article.getCategory());
        assertEquals("赵晓峰", article.getAuthor());
        assertEquals(150, article.getViews());
        assertEquals("已发布", article.getStatus());
        assertEquals(now, article.getCreatedTime());
        assertEquals(now, article.getUpdatedTime());
    }

    @Test
    @DisplayName("默认初始值为 null")
    void defaultValues_ShouldBeNull() {
        HealthArticle article = new HealthArticle();

        assertNull(article.getId());
        assertNull(article.getTitle());
        assertNull(article.getSummary());
        assertNull(article.getContent());
        assertNull(article.getCover());
        assertNull(article.getCategory());
        assertNull(article.getAuthor());
        assertNull(article.getViews());
        assertNull(article.getStatus());
        assertNull(article.getCreatedTime());
        assertNull(article.getUpdatedTime());
    }

    @Test
    @DisplayName("status = '草稿' → 未发布文章")
    void draftArticle_ShouldWork() {
        HealthArticle article = new HealthArticle();
        article.setStatus("草稿");
        article.setViews(0);
        assertEquals("草稿", article.getStatus());
        assertEquals(0, article.getViews());
    }

    @Test
    @DisplayName("views = 0 → 新文章初始浏览量为 0")
    void zeroViews_ShouldWork() {
        HealthArticle article = new HealthArticle();
        article.setViews(0);
        assertEquals(0, article.getViews());
    }

    @Test
    @DisplayName("category = '运动' → 运动分类文章")
    void exerciseCategory_ShouldWork() {
        HealthArticle article = new HealthArticle();
        article.setCategory("运动");
        article.setTitle("适合糖尿病患者的运动方式");
        assertEquals("运动", article.getCategory());
    }
}
