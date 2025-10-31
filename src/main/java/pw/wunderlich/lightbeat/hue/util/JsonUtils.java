package pw.wunderlich.lightbeat.hue.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

/**
 * Utility class for JSON serialization and deserialization.
 */
public final class JsonUtils {
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    private JsonUtils() {
        // Utility class - prevent instantiation
    }

    /**
     * Deserializes JSON from a file to an object of the specified type.
     *
     * @param <T> The type of the desired object
     * @param filePath Path to the JSON file
     * @param classOfT The class of T
     * @return An object of type T from the JSON
     * @throws IOException If file cannot be read
     * @throws JsonSyntaxException If JSON is malformed
     */
    public static <T> T fromJsonFile(String filePath, Class<T> classOfT) throws IOException {
        try (Reader reader = new FileReader(filePath, StandardCharsets.UTF_8)) {
            return GSON.fromJson(reader, classOfT);
        } catch (JsonIOException e) {
            throw new IOException("Failed to read JSON from file: " + filePath, e);
        }
    }

    /**
     * Serializes an object to JSON and writes it to a file.
     *
     * @param object The object to serialize
     * @param filePath Path to the output file
     * @throws IOException If file cannot be written
     */
    public static void toJsonFile(Object object, String filePath) throws IOException {
        try (Writer writer = new FileWriter(filePath, StandardCharsets.UTF_8)) {
            GSON.toJson(object, writer);
        } catch (JsonIOException e) {
            throw new IOException("Failed to write JSON to file: " + filePath, e);
        }
    }

    /**
     * Deserializes JSON string to an object of the specified type.
     *
     * @param <T> The type of the desired object
     * @param json JSON string
     * @param classOfT The class of T
     * @return An object of type T from the JSON
     * @throws JsonSyntaxException If JSON is malformed
     */
    public static <T> T fromJson(String json, Class<T> classOfT) {
        return GSON.fromJson(json, classOfT);
    }

    /**
     * Serializes an object to JSON string.
     *
     * @param object The object to serialize
     * @return JSON string representation of the object
     */
    public static String toJson(Object object) {
        return GSON.toJson(object);
    }
}
