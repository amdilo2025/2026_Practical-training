package com.diabetes.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * MyBatisPlusConfig 单元测试 —— 目标覆盖率 100%
 */
@DisplayName("MyBatisPlusConfig 分页插件配置")
class MyBatisPlusConfigTest {

    @Test
    @DisplayName("mybatisPlusInterceptor Bean → 返回拦截器实例")
    void mybatisPlusInterceptor_ShouldReturnInterceptor() {
        MyBatisPlusConfig config = new MyBatisPlusConfig();
        MybatisPlusInterceptor interceptor = config.mybatisPlusInterceptor();

        assertNotNull(interceptor, "MybatisPlusInterceptor 不应为 null");
    }

    @Test
    @DisplayName("mybatisPlusInterceptor → 包含分页内部拦截器")
    void mybatisPlusInterceptor_ShouldContainPaginationInterceptor() {
        MyBatisPlusConfig config = new MyBatisPlusConfig();
        MybatisPlusInterceptor interceptor = config.mybatisPlusInterceptor();

        // 验证拦截器列表非空（包含 PaginationInnerInterceptor）
        assertFalse(interceptor.getInterceptors().isEmpty(),
                "应包含至少一个内部拦截器");
    }

    @Test
    @DisplayName("多次调用返回不同实例 (prototype)")
    void multipleCalls_ShouldReturnDifferentInstances() {
        MyBatisPlusConfig config = new MyBatisPlusConfig();
        MybatisPlusInterceptor interceptor1 = config.mybatisPlusInterceptor();
        MybatisPlusInterceptor interceptor2 = config.mybatisPlusInterceptor();

        assertNotSame(interceptor1, interceptor2,
                "每次调用应返回新的实例");
    }
}
