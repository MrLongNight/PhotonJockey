package io.github.mrlongnight.photonjockey.hue.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import io.github.mrlongnight.photonjockey.hue.dto.BridgeConfig;
import io.github.mrlongnight.photonjockey.hue.dto.LightConfig;
import io.github.mrlongnight.photonjockey.hue.dto.LightMapConfig;

/**
 * Unit tests for JsonUtils.
 */
class JsonUtilsTest {
    private Path tempDir;

    @BeforeEach
    void setUp() throws IOException {
        tempDir = Files.createTempDirectory("json-test");
    }

    @AfterEach
    void tearDown() throws IOException {
        // Clean up temp files
        Files.walk(tempDir)
                .map(Path::toFile)
                .forEach(File::delete);
    }

    @Test
    void testSerializeAndDeserializeLightMapConfig() throws IOException {
        // Create test config
        List<BridgeConfig> bridges = new ArrayList<>();
        bridges.add(new BridgeConfig("bridge-1", "192.168.1.100", "user123"));
        bridges.add(new BridgeConfig("bridge-2", "192.168.1.101", "user456"));

        List<LightConfig> lights = new ArrayList<>();
        lights.add(new LightConfig("light-1", 10.0, 20.0, "bridge-1", "FAST_UDP", "Living Room"));
        lights.add(new LightConfig("light-2", 30.0, 40.0, "bridge-2", "LOW_HTTP", "Bedroom"));

        LightMapConfig originalConfig = new LightMapConfig(bridges, lights);

        // Save to file
        String filePath = tempDir.resolve("lightmap.json").toString();
        JsonUtils.toJsonFile(originalConfig, filePath);

        // Verify file exists
        assertTrue(new File(filePath).exists());

        // Load from file
        LightMapConfig loadedConfig = JsonUtils.fromJsonFile(filePath, LightMapConfig.class);

        // Verify content
        assertNotNull(loadedConfig);
        assertEquals(2, loadedConfig.getBridges().size());
        assertEquals(2, loadedConfig.getLights().size());

        assertEquals("bridge-1", loadedConfig.getBridges().get(0).getId());
        assertEquals("192.168.1.100", loadedConfig.getBridges().get(0).getIp());
        assertEquals("user123", loadedConfig.getBridges().get(0).getUsername());

        assertEquals("light-1", loadedConfig.getLights().get(0).getId());
        assertEquals(10.0, loadedConfig.getLights().get(0).getX(), 0.001);
        assertEquals(20.0, loadedConfig.getLights().get(0).getY(), 0.001);
        assertEquals("bridge-1", loadedConfig.getLights().get(0).getBridgeId());
        assertEquals("FAST_UDP", loadedConfig.getLights().get(0).getControlType());
        assertEquals("Living Room", loadedConfig.getLights().get(0).getName());

        assertEquals(originalConfig, loadedConfig);
    }

    @Test
    void testToJsonString() {
        BridgeConfig bridge = new BridgeConfig("bridge-1", "192.168.1.100", "user123");
        String json = JsonUtils.toJson(bridge);

        assertNotNull(json);
        assertTrue(json.contains("bridge-1"));
        assertTrue(json.contains("192.168.1.100"));
        assertTrue(json.contains("user123"));
    }

    @Test
    void testFromJsonString() {
        String json = "{\"id\":\"bridge-1\",\"ip\":\"192.168.1.100\",\"username\":\"user123\"}";
        BridgeConfig bridge = JsonUtils.fromJson(json, BridgeConfig.class);

        assertNotNull(bridge);
        assertEquals("bridge-1", bridge.getId());
        assertEquals("192.168.1.100", bridge.getIp());
        assertEquals("user123", bridge.getUsername());
    }

    @Test
    void testLoadFromNonExistentFile() {
        String filePath = tempDir.resolve("nonexistent.json").toString();
        assertThrows(IOException.class, () -> JsonUtils.fromJsonFile(filePath, 
                LightMapConfig.class));
    }

    @Test
    void testSaveEmptyConfig() throws IOException {
        LightMapConfig emptyConfig = new LightMapConfig();
        String filePath = tempDir.resolve("empty.json").toString();

        JsonUtils.toJsonFile(emptyConfig, filePath);

        LightMapConfig loadedConfig = JsonUtils.fromJsonFile(filePath, LightMapConfig.class);
        assertNotNull(loadedConfig);
        assertNotNull(loadedConfig.getBridges());
        assertNotNull(loadedConfig.getLights());
        assertEquals(0, loadedConfig.getBridges().size());
        assertEquals(0, loadedConfig.getLights().size());
    }
}
