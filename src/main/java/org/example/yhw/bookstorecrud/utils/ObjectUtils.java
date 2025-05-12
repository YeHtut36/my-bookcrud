package org.example.yhw.bookstorecrud.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Utility class providing additional object-related operations, extending
 * {@link org.apache.commons.lang3.ObjectUtils}.
 * <p>
 * This class includes methods for handling null objects and JSON conversion using Jackson.
 * </p>
 * <p>
 * This class cannot be instantiated.
 * </p>
 *
 * @author Zin Ko Win
 */
public final class ObjectUtils extends org.apache.commons.lang3.ObjectUtils {
    private static final ObjectMapper objectMapper = configureObjectMapper();

    private ObjectUtils() {
    }

    /**
     * Returns the provided object if it is not null; otherwise, returns the default value.
     *
     * @param <T>          The type of the object and default value.
     * @param object       The object to check for null.
     * @param defaultValue The default value to return if the object is null.
     * @return The object itself if not null; otherwise, the default value.
     */
    public static <T> T defaultIfNull(T object, T defaultValue) {
        return isNotEmpty(object) ? object : defaultValue;
    }

    /**
     * Configures and returns an {@link ObjectMapper} instance with specific settings.
     * <p>
     * The returned {@link ObjectMapper} has case-insensitive property acceptance enabled
     * and does not fail on unknown properties during deserialization.
     * </p>
     *
     * @return The configured {@link ObjectMapper} instance.
     */
    public static ObjectMapper configureObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper;
    }

    /**
     * Converts the given object to its JSON string representation using the configured {@link ObjectMapper}.
     *
     * @param <T>    The type of the object to be converted to JSON.
     * @param object The object to be converted to JSON.
     * @return The JSON string representation of the object, or {@code null} if an error occurs during conversion.
     */
    public static <T> String convertToJsonString(T object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error serializing object to JSON", e);
        }
    }

    public static <T> T parseJson(String json, Class<T> clazz) {
        try {
            return ObjectUtils.configureObjectMapper().readValue(json, clazz);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error deserializing JSON to object", e);
        }
    }
}
