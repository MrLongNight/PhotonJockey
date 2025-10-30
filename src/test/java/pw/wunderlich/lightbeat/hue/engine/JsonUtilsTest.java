package pw.wunderlich.lightbeat.hue.engine;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JsonUtilsTest {

    @TempDir
    Path tempDir;

    @Test
    void testGetMapper() {
        assertNotNull(JsonUtils.getMapper());
    }

    @Test
    void testToJsonColorDTO() throws JsonProcessingException {
        ColorDTO color = new ColorDTO(0.5, 0.5, 127);
        String json = JsonUtils.toJson(color);

        assertNotNull(json);
        assertTrue(json.contains("\"x\""));
        assertTrue(json.contains("0.5"));
        assertTrue(json.contains("\"y\""));
        assertTrue(json.contains("\"brightness\""));
        assertTrue(json.contains("127"));
    }

    @Test
    void testFromJsonColorDTO() throws JsonProcessingException {
        String json = "{\"x\":0.5,\"y\":0.5,\"brightness\":127}";
        ColorDTO color = JsonUtils.fromJson(json, ColorDTO.class);

        assertNotNull(color);
        assertEquals(0.5, color.getX());
        assertEquals(0.5, color.getY());
        assertEquals(127, color.getBrightness());
    }

    @Test
    void testToJsonLightUpdateDTO() throws JsonProcessingException {
        LightUpdateDTO update = new LightUpdateDTO("light-001", 200, 0.5, 0.8, 0);
        String json = JsonUtils.toJson(update);

        assertNotNull(json);
        assertTrue(json.contains("\"lightId\""));
        assertTrue(json.contains("light-001"));
        assertTrue(json.contains("\"brightness\""));
        assertTrue(json.contains("200"));
    }

    @Test
    void testFromJsonLightUpdateDTO() throws JsonProcessingException {
        String json = "{\"lightId\":\"light-001\",\"brightness\":200,\"hue\":0.5,"
                + "\"saturation\":0.8,\"transitionTime\":0}";
        LightUpdateDTO update = JsonUtils.fromJson(json, LightUpdateDTO.class);

        assertNotNull(update);
        assertEquals("light-001", update.getLightId());
        assertEquals(200, update.getBrightness());
        assertEquals(0.5, update.getHue());
        assertEquals(0.8, update.getSaturation());
        assertEquals(0, update.getTransitionTime());
    }

    @Test
    void testToJsonEffectFrame() throws JsonProcessingException {
        LightUpdateDTO update = new LightUpdateDTO("light-001", 200, 0.5, 0.8, 0);
        EffectFrame frame = new EffectFrame(List.of(update), 1234567890L);
        String json = JsonUtils.toJson(frame);

        assertNotNull(json);
        assertTrue(json.contains("\"updates\""));
        assertTrue(json.contains("\"timestamp\""));
        assertTrue(json.contains("1234567890"));
    }

    @Test
    void testFromJsonEffectFrame() throws JsonProcessingException {
        String json = "{\"updates\":[{\"lightId\":\"light-001\",\"brightness\":200,"
                + "\"hue\":0.5,\"saturation\":0.8,\"transitionTime\":0}],\"timestamp\":1234567890}";
        EffectFrame frame = JsonUtils.fromJson(json, EffectFrame.class);

        assertNotNull(frame);
        assertEquals(1234567890L, frame.getTimestamp());
        assertEquals(1, frame.getUpdates().size());
        assertEquals("light-001", frame.getUpdates().get(0).getLightId());
    }

    @Test
    void testFromJsonInputStream() throws IOException {
        String json = "{\"x\":0.5,\"y\":0.5,\"brightness\":127}";
        InputStream inputStream = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
        ColorDTO color = JsonUtils.fromJson(inputStream, ColorDTO.class);

        assertNotNull(color);
        assertEquals(0.5, color.getX());
        assertEquals(0.5, color.getY());
        assertEquals(127, color.getBrightness());
    }

    @Test
    void testWriteToFile() throws IOException {
        ColorDTO color = new ColorDTO(0.5, 0.5, 127);
        Path testFile = tempDir.resolve("test.json");

        JsonUtils.writeToFile(color, testFile);

        assertTrue(testFile.toFile().exists());
        assertTrue(testFile.toFile().length() > 0);
    }

    @Test
    void testReadFromFile() throws IOException {
        ColorDTO color = new ColorDTO(0.5, 0.5, 127);
        Path testFile = tempDir.resolve("test.json");

        JsonUtils.writeToFile(color, testFile);
        ColorDTO readColor = JsonUtils.readFromFile(testFile, ColorDTO.class);

        assertNotNull(readColor);
        assertEquals(color, readColor);
    }

    @Test
    void testRoundTripColorDTO() throws IOException {
        ColorDTO original = new ColorDTO(0.3, 0.7, 200);
        Path testFile = tempDir.resolve("color.json");

        JsonUtils.writeToFile(original, testFile);
        ColorDTO restored = JsonUtils.readFromFile(testFile, ColorDTO.class);

        assertEquals(original, restored);
    }

    @Test
    void testRoundTripLightUpdateDTO() throws IOException {
        LightUpdateDTO original = new LightUpdateDTO("light-123", 150, 0.6, 0.9, 5);
        Path testFile = tempDir.resolve("update.json");

        JsonUtils.writeToFile(original, testFile);
        LightUpdateDTO restored = JsonUtils.readFromFile(testFile, LightUpdateDTO.class);

        assertEquals(original, restored);
    }

    @Test
    void testRoundTripEffectFrame() throws IOException {
        LightUpdateDTO update1 = new LightUpdateDTO("light-001", 200, 0.5, 0.8, 0);
        LightUpdateDTO update2 = new LightUpdateDTO("light-002", 150, 0.3, 0.9, 5);
        EffectFrame original = new EffectFrame(List.of(update1, update2), 1234567890L);
        Path testFile = tempDir.resolve("frame.json");

        JsonUtils.writeToFile(original, testFile);
        EffectFrame restored = JsonUtils.readFromFile(testFile, EffectFrame.class);

        assertEquals(original, restored);
    }

    @Test
    void testFromJsonInvalidJson() {
        String invalidJson = "{invalid json}";
        assertThrows(JsonProcessingException.class, () -> 
            JsonUtils.fromJson(invalidJson, ColorDTO.class)
        );
    }

    @Test
    void testReadFromFileNonExistent() {
        Path nonExistentFile = tempDir.resolve("nonexistent.json");
        assertThrows(IOException.class, () -> 
            JsonUtils.readFromFile(nonExistentFile, ColorDTO.class)
        );
    }
}
