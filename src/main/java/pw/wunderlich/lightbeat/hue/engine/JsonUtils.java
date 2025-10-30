package pw.wunderlich.lightbeat.hue.engine;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Utility class for JSON serialization and deserialization.
 * Provides convenient methods for working with JSON data.
 */
public final class JsonUtils {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    private JsonUtils() {
        // Utility class should not be instantiated
    }

    /**
     * Get the shared ObjectMapper instance.
     *
     * @return the ObjectMapper
     */
    public static ObjectMapper getMapper() {
        return MAPPER;
    }

    /**
     * Serialize an object to a JSON string.
     *
     * @param object the object to serialize
     * @return JSON string representation
     * @throws JsonProcessingException if serialization fails
     */
    public static String toJson(Object object) throws JsonProcessingException {
        return MAPPER.writeValueAsString(object);
    }

    /**
     * Deserialize a JSON string to an object.
     *
     * @param json the JSON string
     * @param clazz the target class
     * @param <T> the type of the target class
     * @return the deserialized object
     * @throws JsonProcessingException if deserialization fails
     */
    public static <T> T fromJson(String json, Class<T> clazz) throws JsonProcessingException {
        return MAPPER.readValue(json, clazz);
    }

    /**
     * Deserialize JSON from an input stream to an object.
     *
     * @param inputStream the input stream containing JSON data
     * @param clazz the target class
     * @param <T> the type of the target class
     * @return the deserialized object
     * @throws IOException if deserialization fails
     */
    public static <T> T fromJson(InputStream inputStream, Class<T> clazz) throws IOException {
        return MAPPER.readValue(inputStream, clazz);
    }

    /**
     * Write an object as JSON to a file.
     *
     * @param object the object to serialize
     * @param path the target file path
     * @throws IOException if writing fails
     */
    public static void writeToFile(Object object, Path path) throws IOException {
        try (OutputStream outputStream = Files.newOutputStream(path)) {
            MAPPER.writeValue(outputStream, object);
        }
    }

    /**
     * Read an object from a JSON file.
     *
     * @param path the source file path
     * @param clazz the target class
     * @param <T> the type of the target class
     * @return the deserialized object
     * @throws IOException if reading fails
     */
    public static <T> T readFromFile(Path path, Class<T> clazz) throws IOException {
        try (InputStream inputStream = Files.newInputStream(path)) {
            return fromJson(inputStream, clazz);
        }
    }
}
