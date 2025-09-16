package com.setianjay.base;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.setianjay.constants.NetworkConstant;
import com.setianjay.enums.Method;
import com.setianjay.utils.AnnotationUtil;
import com.setianjay.utils.JsonUtils;
import com.setianjay.utils.LoggerUtils;
import io.restassured.RestAssured;
import io.restassured.config.ConnectionConfig;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public abstract class BaseTest {
    private RestAssuredConfig restAssuredConfig;
    private RequestSpecification requestSpec;
    private Response response;
    private String testName;
    private String tokenAuth;

    protected Logger logger = LoggerUtils.getLogger(this.getClazz());


    @BeforeClass
    public void globalSetup() {

        try {
            // Initialize JsonUtils
            JsonUtils.getInstance();

            // Setup RestAssured configuration
            setupRestAssuredConfig();

            // Custom setup hook
            customSetupBeforeClass();

            // Configure RestAssured
            RestAssured.config = restAssuredConfig;

            logger.info("globalSetup completed successfully");

        } catch (Exception e) {
            logger.error("globalSetup failed", e);
            throw new RuntimeException("globalSetup failed", e);
        }
    }

    @BeforeMethod
    public void setUp(ITestResult result) {
        try {
            Test test = AnnotationUtil.findMethodAnnotation(
                    result.getTestClass().getRealClass(),
                    result.getMethod().getMethodName(),
                    Test.class);

            // Enhanced request specification dengan logging headers
            requestSpec = RestAssured.given()
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .header("User-Agent", "API-Test-Automation/1.0");
            // .header("X-Test-ID", LoggerUtils.getCurrentTestId())
            // .header("X-Test-Timestamp", LoggerUtils.getCurrentTimestamp());

            // Reset last response
            response = null;

            // method name
            testName = test.testName();

            // custom setup hook
            customSetupBeforeMethod();

            logger.info("Test setup completed successfully");

        } catch (Exception e) {
            logger.error("Setup failed", e);
            throw new RuntimeException("Test setup failed", e);
        }
    }

    @AfterMethod
    public void tearDown() {

        try {
            // Clear response object
            if (response != null) {
                response = null;
            }

            // Clear request specification
            if (requestSpec != null) {
                requestSpec = null;
            }

            // Clear request specification
            if (testName != null) {
                testName = null;
            }

            // Custom cleanup hook
            customTearDownAfterMethod();

            LoggerUtils.clearTestContext();
        } catch (Exception e) {
            logger.error("Failed during tearDown: ", e);
        }
    }

    @AfterClass
    public void globalTearDown() {
        try {
            // Reset RestAssured
            RestAssured.reset();

            // Clear static references
            restAssuredConfig = null;

            // Cleanup JsonUtils
            JsonUtils.cleanup();

            // Cleanup Logger
            LoggerUtils.clearContext();

            // Custom cleanup hook
            customTearDownAfterClass();

            // Clear token auth
            tokenAuth = null;

            logger.info("globalTearDown completed successfully");
        } catch (Exception e) {
            logger.error("Failed during globalTearDown", e);
        }
    }

    private void setupRestAssuredConfig() {
        logger.info("Rest assured configuration started");

        restAssuredConfig = RestAssuredConfig.config()
                .connectionConfig(ConnectionConfig.connectionConfig()
                        .closeIdleConnectionsAfterEachResponseAfter(30, TimeUnit.SECONDS))
                .httpClient(HttpClientConfig.httpClientConfig()
                        .setParam("http.connection.timeout", NetworkConstant.CONNECTION_TIMEOUT)
                        .setParam("http.socket.timeout", NetworkConstant.SOCKET_TIMEOUT)
                        .setParam("http.conn-manager.max-total", NetworkConstant.MAX_CONNECTIONS)
                        .setParam("http.conn-manager.max-per-route", 20));

        logger.info("Rest assured configuration completed");
    }

    protected Class<?> getClazz() {
        return this.getClass();
    }

    protected String getClazzName() {
        return getClazz().getSimpleName();
    }

    protected String getTestName() {
        return this.testName;
    }

    // ========== UTILITY METHODS WITH INTEGRATED LOGGING ==========

    protected <T> T deserializeResponse(String jsonResponse, Class<T> clazz)
            throws JsonProcessingException {
        return JsonUtils.fromJson(jsonResponse, clazz);
    }

    protected <T> T deserializeResponse(String jsonResponse, TypeReference<T> typeRef)
            throws JsonProcessingException {
        return JsonUtils.fromJson(jsonResponse, typeRef);
    }

    protected <T> T deserializeResponse(String jsonResponse, JavaType javaType)
            throws JsonProcessingException {
        return JsonUtils.fromJson(jsonResponse, javaType);
    }

    protected <T> List<T> deserializeResponseToList(String jsonResponse, Class<T> elementClass)
            throws JsonProcessingException {
        return JsonUtils.fromJsonToList(jsonResponse, elementClass);
    }

    protected String serializeToJson(Object object) throws JsonProcessingException {
        return JsonUtils.toJson(object);
    }

    protected String serializeToPrettyJson(Object object) throws JsonProcessingException {
        return JsonUtils.toPrettyJson(object);
    }

    protected Response executeRequest(Method method, String basePath, Object requestBody, Map<String, String> customHeader, Map<String, Object> requestParams, Map<String, Object> requestPaths) {
        try {
            String endpoint = getBaseUri() + basePath;
            String requestBodyJson = requestBody != null ? serializeToJson(requestBody) : null;

            if (requestSpec == null) {
                requestSpec = RestAssured.given();
            }

            if (customHeader != null) {
                requestSpec.headers(customHeader);
            }

            RequestSpecification request = getRequestSpec();
            // Set request body if exist
            if (requestBodyJson != null) {
                request.body(requestBodyJson);
            }

            // Set request params if exist
            if (requestParams != null) {
                if (!requestParams.isEmpty()) {
                    request.params(requestParams);
                }
            }

            // Set request path if exist
            if (requestPaths != null) {
                if (!requestPaths.isEmpty()) {
                    request.pathParams(requestPaths);
                }
            }

            long startTime = System.currentTimeMillis();

            switch (method) {
                case GET:
                    response = request.when().get(endpoint);
                    break;
                case POST:
                    response = request.when().post(endpoint);
                    break;
                case PUT:
                    response = request.when().put(endpoint);
                    break;
                case PATCH:
                    response = request.when().patch(endpoint);
                    break;
                case DELETE:
                    response = request.when().delete(endpoint);
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported HTTP method: " + method);
            }

            long duration = System.currentTimeMillis() - startTime;
            LoggerUtils.logApiDetails(logger, method.name(), endpoint, request, response, duration);
            return response;

        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize request body", e);
            throw new RuntimeException("Request execution failed", e);
        }
    }

    // ========== HOOK METHODS ==========

    protected void customSetupBeforeClass() throws JsonProcessingException {

    }

    protected void customTearDownAfterClass() {

    }

    protected void customSetupBeforeMethod() {

    }

    protected void customTearDownAfterMethod() {

    }

    // ========== GETTER METHODS ==========

    protected RequestSpecification getRequestSpec() {
        return this.requestSpec;
    }

    protected Response getResponse() {
        return this.response;
    }

    protected String getBaseUri() {
        return RestAssured.baseURI;
    }

    protected String getTokenAuth() {
        return this.tokenAuth;
    }

    // ========== GETTER METHODS ==========
    protected abstract void setBaseURI();

    protected void setToken(String token) {
        this.tokenAuth = token;
    }
}
