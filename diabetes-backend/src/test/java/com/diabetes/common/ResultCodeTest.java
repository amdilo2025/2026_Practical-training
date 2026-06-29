package com.diabetes.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ResultCode 状态码常量类单元测试 —— 目标覆盖率 100%
 */
@DisplayName("ResultCode 状态码常量")
class ResultCodeTest {

    @Test
    @DisplayName("SUCCESS = 200")
    void successCode_ShouldBe200() {
        assertEquals(200, ResultCode.SUCCESS,
                "成功状态码应为 200");
    }

    @Test
    @DisplayName("ERROR = 500")
    void errorCode_ShouldBe500() {
        assertEquals(500, ResultCode.ERROR,
                "服务器内部错误状态码应为 500");
    }

    @Test
    @DisplayName("UNAUTHORIZED = 401")
    void unauthorizedCode_ShouldBe401() {
        assertEquals(401, ResultCode.UNAUTHORIZED,
                "未授权状态码应为 401");
    }

    @Test
    @DisplayName("FORBIDDEN = 403")
    void forbiddenCode_ShouldBe403() {
        assertEquals(403, ResultCode.FORBIDDEN,
                "禁止访问状态码应为 403");
    }

    @Test
    @DisplayName("NOT_FOUND = 404")
    void notFoundCode_ShouldBe404() {
        assertEquals(404, ResultCode.NOT_FOUND,
                "未找到状态码应为 404");
    }

    @Test
    @DisplayName("PARAM_ERROR = 400")
    void paramErrorCode_ShouldBe400() {
        assertEquals(400, ResultCode.PARAM_ERROR,
                "参数错误状态码应为 400");
    }

    @Test
    @DisplayName("所有状态码互不重复")
    void allCodesShouldBeUnique() {
        int[] codes = {
                ResultCode.SUCCESS, ResultCode.ERROR,
                ResultCode.UNAUTHORIZED, ResultCode.FORBIDDEN,
                ResultCode.NOT_FOUND, ResultCode.PARAM_ERROR
        };
        assertEquals(codes.length, java.util.Arrays.stream(codes).distinct().count(),
                "所有状态码应互不重复");
    }

    @Test
    @DisplayName("常量字段均为 public static final int")
    void allConstantsArePublicStaticFinal() throws Exception {
        // 验证类可以通过反射正常访问所有常量
        var fields = ResultCode.class.getDeclaredFields();
        for (var field : fields) {
            assertTrue(java.lang.reflect.Modifier.isPublic(field.getModifiers()),
                    field.getName() + " 应为 public");
            assertTrue(java.lang.reflect.Modifier.isStatic(field.getModifiers()),
                    field.getName() + " 应为 static");
            assertTrue(java.lang.reflect.Modifier.isFinal(field.getModifiers()),
                    field.getName() + " 应为 final");
            assertEquals(int.class, field.getType(),
                    field.getName() + " 类型应为 int");
        }
    }
}
