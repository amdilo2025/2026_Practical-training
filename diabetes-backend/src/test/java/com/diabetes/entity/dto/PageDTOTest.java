package com.diabetes.entity.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * PageDTO 单元测试 —— 目标覆盖率 100%
 */
@DisplayName("PageDTO")
class PageDTOTest {

    @Test
    @DisplayName("默认值: page=1, pageSize=10, keyword/category=null")
    void defaultValues_ShouldBeCorrect() {
        PageDTO dto = new PageDTO();

        assertEquals(1, dto.getPage());
        assertEquals(10, dto.getPageSize());
        assertNull(dto.getKeyword());
        assertNull(dto.getCategory());
    }

    @Test
    @DisplayName("全字段 setter/getter 正常读写")
    void allFields_SetterGetter_ShouldWork() {
        PageDTO dto = new PageDTO();

        dto.setPage(3);
        dto.setPageSize(20);
        dto.setKeyword("糖尿病");
        dto.setCategory("饮食");

        assertEquals(3, dto.getPage());
        assertEquals(20, dto.getPageSize());
        assertEquals("糖尿病", dto.getKeyword());
        assertEquals("饮食", dto.getCategory());
    }

    @Test
    @DisplayName("大页码 → 合法")
    void largePageNumber_ShouldWork() {
        PageDTO dto = new PageDTO();
        dto.setPage(999999);
        assertEquals(999999, dto.getPage());
    }

    @Test
    @DisplayName("pageSize 设为 1 → 最小合法值")
    void minimumPageSize_ShouldWork() {
        PageDTO dto = new PageDTO();
        dto.setPageSize(1);
        assertEquals(1, dto.getPageSize());
    }

    @Test
    @DisplayName("pageSize 设为 100 → 大页容量")
    void largePageSize_ShouldWork() {
        PageDTO dto = new PageDTO();
        dto.setPageSize(100);
        assertEquals(100, dto.getPageSize());
    }
}
