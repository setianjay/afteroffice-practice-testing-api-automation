package com.setianjay.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonUtils {

    private static final Logger logger = LoggerUtils.getLogger(JsonUtils.class);
    private static volatile JsonUtils instance;
    private static volatile ObjectMapper objectMapper;

    // Private constructor untuk Singleton pattern
    private JsonUtils() {
        if (objectMapper == null) {
            synchronized (JsonUtils.class) {
                if (objectMapper == null) {
                    initializeObjectMapper();
                }
            }
        }
    }

    /**
     * Get singleton instance of JsonUtils
     */
    public static JsonUtils getInstance() {
        if (instance == null) {
            synchronized (JsonUtils.class) {
                if (instance == null) {
                    instance = new JsonUtils();
                }
            }
        }
        return instance;
    }

    /**
     * Get ObjectMapper instance
     */
    public static ObjectMapper getObjectMapper() {
        getInstance(); // Ensure initialization
        return objectMapper;
    }

    /**
     * Initialize ObjectMapper dengan konfigurasi optimal
     */
    private static void initializeObjectMapper() {
        logger.info("Initializing ObjectMapper...");

        objectMapper = new ObjectMapper();

        // Deserialization features
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true);
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        objectMapper.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);

        // Serialization features
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        // Property naming strategy (uncomment jika diperlukan)
        // objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

        logger.info("ObjectMapper initialized successfully");
    }

    /**
     * Configure ObjectMapper untuk specific naming strategy
     */
    public static void configureNamingStrategy(PropertyNamingStrategies.NamingBase strategy) {
        getObjectMapper().setPropertyNamingStrategy(strategy);
        logger.info("ObjectMapper naming strategy configured: {}", strategy.getClass().getSimpleName());
    }

    // ========== DESERIALIZATION METHODS ==========

    /**
     * Deserialize JSON string ke object menggunakan Class
     */
    public static <T> T fromJson(String json, Class<T> clazz) throws JsonProcessingException {
        if (json == null || json.trim().isEmpty()) {
            throw new IllegalArgumentException("JSON string cannot be null or empty");
        }

        try {
            return getObjectMapper().readValue(json, clazz);
        } catch (JsonProcessingException e) {
            logger.error("Failed to deserialize JSON to {}: {}", clazz.getSimpleName(), e.getMessage());
            throw e;
        }
    }

    /**
     * Deserialize JSON string ke object menggunakan TypeReference
     */
    public static <T> T fromJson(String json, TypeReference<T> typeRef) throws JsonProcessingException {
        if (json == null || json.trim().isEmpty()) {
            throw new IllegalArgumentException("JSON string cannot be null or empty");
        }

        try {
            return getObjectMapper().readValue(json, typeRef);
        } catch (JsonProcessingException e) {
            logger.error("Failed to deserialize JSON with TypeReference: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Deserialize JSON string ke object menggunakan JavaType
     */
    public static <T> T fromJson(String json, JavaType javaType) throws JsonProcessingException {
        if (json == null || json.trim().isEmpty()) {
            throw new IllegalArgumentException("JSON string cannot be null or empty");
        }

        try {
            return getObjectMapper().readValue(json, javaType);
        } catch (JsonProcessingException e) {
            logger.error("Failed to deserialize JSON with JavaType: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Deserialize JSON ke List<T>
     */
    public static <T> List<T> fromJsonToList(String json, Class<T> elementClass) throws JsonProcessingException {
        JavaType listType = createCollectionType(elementClass);
        return fromJson(json, listType);
    }

    /**
     * Deserialize JSON ke Map
     */
    public static Map<String, Object> fromJsonToMap(String json) throws JsonProcessingException {
        TypeReference<Map<String, Object>> typeRef = new TypeReference<Map<String, Object>>() {};
        return fromJson(json, typeRef);
    }

    // ========== SERIALIZATION METHODS ==========

    /**
     * Serialize object ke JSON string
     */
    public static String toJson(Object object) throws JsonProcessingException {
        if (object == null) {
            return "null";
        }

        try {
            return getObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize object to JSON: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Serialize object ke pretty printed JSON string
     */
    public static String toPrettyJson(Object object) throws JsonProcessingException {
        if (object == null) {
            return "null";
        }

        try {
            return getObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize object to pretty JSON: {}", e.getMessage());
            throw e;
        }
    }

    // ========== JSON NODE METHODS ==========

    /**
     * Parse JSON string ke JsonNode untuk dynamic handling
     */
    public static JsonNode parseToJsonNode(String json) throws JsonProcessingException {
        return getObjectMapper().readTree(json);
    }

    /**
     * Convert object ke JsonNode
     */
    public static JsonNode objectToJsonNode(Object object) {
        return getObjectMapper().valueToTree(object);
    }

    /**
     * Convert JsonNode ke specific type
     */
    public static <T> T jsonNodeToObject(JsonNode jsonNode, Class<T> clazz) throws JsonProcessingException {
        return getObjectMapper().treeToValue(jsonNode, clazz);
    }

    /**
     * Convert JsonNode menggunakan TypeReference
     */
    public static <T> T jsonNodeToObject(JsonNode jsonNode, TypeReference<T> typeRef) throws JsonProcessingException {
        return getObjectMapper().convertValue(jsonNode, typeRef);
    }

    // ========== JAVATYPE HELPER METHODS ==========

    /**
     * Create JavaType untuk parametric types (e.g., BaseResponse<List<Phone>>)
     */
    public static JavaType createParametricType(Class<?> parametrized, Class<?>... parameterClasses) {
        return getObjectMapper().getTypeFactory().constructParametricType(parametrized, parameterClasses);
    }

    /**
     * Create JavaType untuk parametric types dengan JavaType parameters
     */
    public static JavaType createParametricType(Class<?> parametrized, JavaType... parameterTypes) {
        return getObjectMapper().getTypeFactory().constructParametricType(parametrized, parameterTypes);
    }

    /**
     * Create JavaType untuk Collection types
     */
    public static JavaType createCollectionType(Class<?> elementClass) {
        return getObjectMapper().getTypeFactory().constructCollectionType(ArrayList.class, elementClass);
    }

    /**
     * Create JavaType untuk Map types
     */
    public static JavaType createMapType( Class<?> keyClass, Class<?> valueClass) {
        return getObjectMapper().getTypeFactory().constructMapType(HashMap.class, keyClass, valueClass);
    }

    // ========== VALIDATION METHODS ==========

    /**
     * Validate apakah string adalah valid JSON
     */
    public static boolean isValidJson(String json) {
        if (json == null || json.trim().isEmpty()) {
            return false;
        }

        try {
            getObjectMapper().readTree(json);
            return true;
        } catch (JsonProcessingException e) {
            return false;
        }
    }

    /**
     * Validate dan parse JSON, return null jika invalid
     */
    public static JsonNode parseJsonSafely(String json) {
        try {
            return parseToJsonNode(json);
        } catch (JsonProcessingException e) {
            logger.warn("Invalid JSON provided: {}", e.getMessage());
            return null;
        }
    }

    // ========== CLEANUP METHODS ==========

    /**
     * Cleanup resources - dipanggil saat aplikasi shutdown
     */
    public static void cleanup() {
        logger.info("Cleaning up JsonUtils resources...");
        objectMapper = null;
        instance = null;
        logger.info("JsonUtils cleanup completed");
    }

}
