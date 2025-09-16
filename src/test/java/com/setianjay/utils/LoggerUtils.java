package com.setianjay.utils;

import com.setianjay.enums.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;


public class LoggerUtils {

    private static final String TEST_ID_KEY = "testId";
    private static final String TEST_CLASS_KEY = "testClass";
    private static final String TEST_METHOD_KEY = "testMethod";
    private static final String THREAD_ID_KEY = "threadId";

    // Private constructor untuk utility class
    private LoggerUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Get logger instance untuk specific class
     */
    public static Logger getLogger(Class<?> clazz) {
        return LoggerFactory.getLogger(clazz);
    }

    /**
     * Get logger instance dengan custom name
     */
    public static Logger getLogger(String name) {
        return LoggerFactory.getLogger(name);
    }

    // ========== TEST CONTEXT METHODS ==========

    /**
     * Set test context untuk MDC (Mapped Diagnostic Context)
     * Berguna untuk tracing logs dalam test execution
     */
    public static void setTestContext(String testClass, String testMethod) {
        String testId = generateTestId();
        String threadId = Thread.currentThread().getName();

        MDC.put(TEST_ID_KEY, testId);
        MDC.put(TEST_CLASS_KEY, testClass);
        MDC.put(TEST_METHOD_KEY, testMethod);
        MDC.put(THREAD_ID_KEY, threadId);
    }

    /**
     * Clear all MDC context
     */
    public static void clearContext() {
        MDC.clear();
    }

    /**
     * Clear test context dari MDC
     */
    public static void clearTestContext() {
        MDC.remove(TEST_ID_KEY);
        MDC.remove(TEST_CLASS_KEY);
        MDC.remove(TEST_METHOD_KEY);
        MDC.remove(THREAD_ID_KEY);
    }

    /**
     * Get current test ID dari MDC
     */
    public static String getCurrentTestId() {
        return MDC.get(TEST_ID_KEY);
    }

    // ========== CONVENIENCE LOGGING METHODS ==========

    /**
     * Log test start dengan context
     */
    public static void logTestStart(Logger logger, String testClass, String testMethod) {
        setTestContext(testClass, testMethod);
        logger.info("🚀 Starting test: {}.{} [TestID: {}]", testClass, testMethod, getCurrentTestId());
    }

    /**
     * Log test completion dengan context
     */
    public static void logTestEnd(Logger logger, String testClass, String testMethod) {
        logger.info("✅ Completed test: {}.{} [TestID: {}]",
                testClass, testMethod, getCurrentTestId());
        clearTestContext();
    }

    /**
     * Log API details
     */
    public static void logApiDetails(Logger logger, String method, String endpoint, RequestSpecification request, Response response, Long duration) {
        logger.info("📤 API Request: {} {} with duration {} seconds", method, endpoint, duration);
        Method requestMethod = Method.valueOf(method);

        switch (requestMethod) {
            case GET:
                request.filter((reqSpec, resSpec, ctx) -> {
                    String requestParams = reqSpec.getQueryParams().toString();
                    String requestPaths = reqSpec.getURI();

                    if (requestParams != null) {
                        logger.debug("📤 Request Params: {}", requestParams);
                    }

                    if (requestPaths != null) {
                        logger.debug("📤 Request Path: {}", requestPaths);
                    }

                    return ctx.next(reqSpec, resSpec);
            });

                break;
            case POST:
            case PUT:
            case PATCH:
                String requestBody = request.log().body().toString();

                if (requestBody != null) {
                    logger.debug("📤 Request Body: {}", requestBody);
                }
                break;
            default:
        }

        String responseBody = response.body().asString();
        if (responseBody != null && !responseBody.trim().isEmpty()) {
            // Truncate long responses untuk readability
            String truncatedBody = responseBody.length() > 1000
                    ? responseBody.substring(0, 1000) + "... (truncated)"
                    : responseBody;
            logger.debug("📥 Response Body: {}", truncatedBody);
        }
    }

    /**
     * Log performance metrics
     */
    public static void logPerformanceMetric(Logger logger, String operation, long durationMs) {
        logger.info("⏱️ Performance: {} took {}ms [TestID: {}]",
                operation, durationMs, getCurrentTestId());
    }

    // ========== UTILITY METHODS ==========

    /**
     * Generate unique test ID
     */
    private static String generateTestId() {
        return "TEST-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    /**
     * Format exception untuk logging
     */
    public static String formatException(Throwable throwable) {
        if (throwable == null) return "null";

        StringBuilder sb = new StringBuilder();
        sb.append(throwable.getClass().getSimpleName())
                .append(": ")
                .append(throwable.getMessage());

        if (throwable.getCause() != null) {
            sb.append(" (Caused by: ")
                    .append(throwable.getCause().getClass().getSimpleName())
                    .append(": ")
                    .append(throwable.getCause().getMessage())
                    .append(")");
        }

        return sb.toString();
    }

    /**
     * Mask sensitive data dalam logs
     */
    public static String maskSensitiveData(String data, String... sensitiveFields) {
        if (data == null || data.trim().isEmpty()) return data;

        String maskedData = data;
        for (String field : sensitiveFields) {
            // Simple regex untuk mask common sensitive fields
            String pattern = "(" + field + "\"\\s*:\\s*\")([^\"]+)(\".*?)";
            maskedData = maskedData.replaceAll(pattern, "$1***MASKED***$3");
        }

        return maskedData;
    }

    /**
     * Get current timestamp for logging
     */
    public static String getCurrentTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

}
