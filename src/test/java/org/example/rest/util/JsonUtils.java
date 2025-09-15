package org.example.rest.util;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


public class JsonUtils {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return mapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Błąd podczas deserializacji JSON do obiektu " + clazz.getSimpleName(), e);
        }
    }

    /**
     * Konwertuje obiekt na JSON string.
     */
    public static String toJson(Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Błąd podczas serializacji obiektu do JSON", e);
        }
    }
}