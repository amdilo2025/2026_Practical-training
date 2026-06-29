package com.diabetes.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CorsConfig 单元测试 —— 目标覆盖率 100%
 */
@DisplayName("CorsConfig 跨域配置")
class CorsConfigTest {

    @Test
    @DisplayName("corsFilter Bean → 返回 CorsFilter 实例")
    void corsFilter_ShouldReturnCorsFilter() {
        CorsConfig corsConfig = new CorsConfig();
        CorsFilter filter = corsConfig.corsFilter();

        assertNotNull(filter, "CorsFilter 不应为 null");
    }

    @Test
    @DisplayName("corsFilter 配置 → 通过反射验证 CORS 配置")
    void corsFilter_ShouldHaveCorrectConfiguration() throws Exception {
        CorsConfig corsConfig = new CorsConfig();
        CorsFilter filter = corsConfig.corsFilter();

        // 通过反射获取内部的 configSource 字段
        UrlBasedCorsConfigurationSource source = getConfigSource(filter);
        assertNotNull(source, "configSource 不应为 null");

        CorsConfiguration config = source.getCorsConfigurations().get("/**");
        assertNotNull(config, "/** 路径的 CORS 配置不应为 null");

        // 验证核心配置
        assertTrue(config.getAllowedOriginPatterns().contains("*"),
                "应允许所有 Origin Pattern");
        assertTrue(config.getAllowedHeaders().contains("*"),
                "应允许所有 Header");
        assertTrue(config.getAllowedMethods().contains("*"),
                "应允许所有 HTTP Method");
        assertTrue(config.getAllowCredentials(),
                "应允许携带凭证");
    }

    @Test
    @DisplayName("corsFilter 覆盖路径为 /**")
    void corsFilter_ShouldCoverAllPaths() throws Exception {
        CorsConfig corsConfig = new CorsConfig();
        CorsFilter filter = corsConfig.corsFilter();

        UrlBasedCorsConfigurationSource source = getConfigSource(filter);
        assertNotNull(source.getCorsConfigurations().get("/**"),
                "所有路径 /** 应有跨域配置");
    }

    @Test
    @DisplayName("多次调用返回不同 Filter 实例")
    void multipleCalls_ShouldReturnDifferentInstances() {
        CorsConfig corsConfig = new CorsConfig();
        CorsFilter filter1 = corsConfig.corsFilter();
        CorsFilter filter2 = corsConfig.corsFilter();

        assertNotSame(filter1, filter2, "每次调用应返回新的实例");
    }

    /**
     * 通过反射获取 CorsFilter 内部的 configSource
     */
    private UrlBasedCorsConfigurationSource getConfigSource(CorsFilter filter) throws Exception {
        Field field = CorsFilter.class.getDeclaredField("configSource");
        field.setAccessible(true);
        return (UrlBasedCorsConfigurationSource) field.get(filter);
    }
}
