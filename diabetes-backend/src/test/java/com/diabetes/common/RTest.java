package com.diabetes.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * R 通用响应包装类单元测试 —— 目标覆盖率 100%
 */
@DisplayName("R 通用响应类")
class RTest {

    // ==================== 构造函数测试 ====================

    @Test
    @DisplayName("无参构造 → 所有字段为 null")
    void noArgsConstructor_ShouldHaveNullFields() {
        R<String> r = new R<>();
        assertNull(r.getCode());
        assertNull(r.getMessage());
        assertNull(r.getData());
    }

    @Test
    @DisplayName("全参构造 → 字段值正确赋值")
    void allArgsConstructor_ShouldSetAllFields() {
        R<Integer> r = new R<>(200, "成功", 42);
        assertEquals(200, r.getCode());
        assertEquals("成功", r.getMessage());
        assertEquals(42, r.getData());
    }

    // ==================== success 静态工厂 ====================

    @Nested
    @DisplayName("success 静态工厂方法")
    class SuccessFactory {

        @Test
        @DisplayName("success(T data) → code=200, message=成功, data 正确")
        void successWithData_ShouldReturn200() {
            String data = "test-data";
            R<String> r = R.success(data);

            assertEquals(200, r.getCode());
            assertEquals("成功", r.getMessage());
            assertEquals("test-data", r.getData());
        }

        @Test
        @DisplayName("success(T data) 传入 null → data 为 null")
        void successWithNullData_ShouldReturn200WithNullData() {
            R<Object> r = R.success(null);

            assertEquals(200, r.getCode());
            assertEquals("成功", r.getMessage());
            assertNull(r.getData());
        }

        @Test
        @DisplayName("success(T data) 传入复杂对象 → 正确包装")
        void successWithComplexObject() {
            java.util.Map<String, Object> map = new java.util.HashMap<>();
            map.put("key", "value");
            R<java.util.Map<String, Object>> r = R.success(map);

            assertEquals(200, r.getCode());
            assertEquals("成功", r.getMessage());
            assertSame(map, r.getData());
            assertEquals("value", r.getData().get("key"));
        }

        @Test
        @DisplayName("success(String message, T data) → 自定义 message")
        void successWithCustomMessage_ShouldReturn200() {
            R<String> r = R.success("操作完成", "payload");

            assertEquals(200, r.getCode());
            assertEquals("操作完成", r.getMessage());
            assertEquals("payload", r.getData());
        }

        @Test
        @DisplayName("success(String message, T data) message 为空字符串 → 合法")
        void successWithEmptyMessage() {
            R<Integer> r = R.success("", 100);

            assertEquals(200, r.getCode());
            assertEquals("", r.getMessage());
            assertEquals(100, r.getData());
        }
    }

    // ==================== error 静态工厂 ====================

    @Nested
    @DisplayName("error 静态工厂方法")
    class ErrorFactory {

        @Test
        @DisplayName("error(Integer code, String message) → 自定义 code 和 message")
        void errorWithCodeAndMessage() {
            R<Object> r = R.error(404, "资源未找到");

            assertEquals(404, r.getCode());
            assertEquals("资源未找到", r.getMessage());
            assertNull(r.getData());
        }

        @Test
        @DisplayName("error(Integer code, String message) code=401 → 未授权")
        void errorWith401Code() {
            R<Object> r = R.error(401, "未授权访问");

            assertEquals(401, r.getCode());
            assertEquals("未授权访问", r.getMessage());
            assertNull(r.getData());
        }

        @Test
        @DisplayName("error(String message) → 默认 code=500")
        void errorWithDefaultCode_ShouldBe500() {
            R<Object> r = R.error("服务器内部错误");

            assertEquals(500, r.getCode());
            assertEquals("服务器内部错误", r.getMessage());
            assertNull(r.getData());
        }

        @Test
        @DisplayName("error(String message) message 为空字符串 → 合法")
        void errorWithEmptyMessage_ShouldDefaultCode500() {
            R<Object> r = R.error("");

            assertEquals(500, r.getCode());
            assertEquals("", r.getMessage());
            assertNull(r.getData());
        }
    }

    // ==================== getter / setter 测试 ====================

    @Nested
    @DisplayName("getter/setter 方法")
    class GetterSetter {

        @Test
        @DisplayName("setCode / getCode → 正确读写")
        void codeSetterGetter() {
            R<String> r = new R<>();
            r.setCode(201);
            assertEquals(201, r.getCode());
        }

        @Test
        @DisplayName("setMessage / getMessage → 正确读写")
        void messageSetterGetter() {
            R<String> r = new R<>();
            r.setMessage("test message");
            assertEquals("test message", r.getMessage());
        }

        @Test
        @DisplayName("setData / getData → 正确读写")
        void dataSetterGetter() {
            R<String> r = new R<>();
            r.setData("hello");
            assertEquals("hello", r.getData());
        }

        @Test
        @DisplayName("链式设置所有字段 → getter 全部正确")
        void chainedSetters_AllGettersReturnCorrectValues() {
            R<java.util.List<String>> r = new R<>();
            r.setCode(200);
            r.setMessage("OK");
            r.setData(java.util.List.of("a", "b"));

            assertEquals(200, r.getCode());
            assertEquals("OK", r.getMessage());
            assertEquals(2, r.getData().size());
        }
    }

    // ==================== 泛型边界场景 ====================

    @Test
    @DisplayName("泛型 R<List<Map>> → 正常包装复杂泛型")
    void genericWithNestedCollection_ShouldWork() {
        var list = java.util.List.of(
                java.util.Map.of("name", "张三"),
                java.util.Map.of("name", "李四")
        );
        R<java.util.List<java.util.Map<String, String>>> r = R.success(list);

        assertEquals(200, r.getCode());
        assertEquals("成功", r.getMessage());
        assertEquals(2, r.getData().size());
        assertEquals("李四", r.getData().get(1).get("name"));
    }
}
