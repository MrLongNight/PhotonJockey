package pw.wunderlich.lightbeat.hue.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import pw.wunderlich.lightbeat.hue.dto.LightMapConfig;

/**
 * Integration tests for loading LightMapConfig from JSON files.
 * Tests schema validation and load/save functionality.
 */
class LightMapConfigLoaderTest {

    @Test
    void testLoadValidLightMapConfig() {
        String json = loadResourceAsString("/test-lightmap-valid.json");
        assertNotNull(json);

        LightMapConfig config = JsonUtils.fromJson(json, LightMapConfig.class);

        assertNotNull(config);
        assertEquals(1, config.getBridges().size());
        assertEquals(2, config.getLights().size());

        // Verify bridge details
        assertEquals("bridge-1", config.getBridges().get(0).getId());
        assertEquals("192.168.1.100", config.getBridges().get(0).getIp());
        assertEquals("testuser123", config.getBridges().get(0).getUsername());

        // Verify first light
        assertEquals("light-1", config.getLights().get(0).getId());
        assertEquals(10.5, config.getLights().get(0).getX(), 0.001);
        assertEquals(20.3, config.getLights().get(0).getY(), 0.001);
        assertEquals("bridge-1", config.getLights().get(0).getBridgeId());
        assertEquals("FAST_UDP", config.getLights().get(0).getControlType());
        assertEquals("Living Room", config.getLights().get(0).getName());

        // Verify second light
        assertEquals("light-2", config.getLights().get(1).getId());
        assertEquals(30.0, config.getLights().get(1).getX(), 0.001);
        assertEquals(40.0, config.getLights().get(1).getY(), 0.001);
        assertEquals("bridge-1", config.getLights().get(1).getBridgeId());
        assertEquals("LOW_HTTP", config.getLights().get(1).getControlType());
        assertEquals("Bedroom", config.getLights().get(1).getName());
    }

    @Test
    void testLoadMinimalLightMapConfig() {
        String json = loadResourceAsString("/test-lightmap-minimal.json");
        assertNotNull(json);

        LightMapConfig config = JsonUtils.fromJson(json, LightMapConfig.class);

        assertNotNull(config);
        assertEquals(1, config.getBridges().size());
        assertEquals(1, config.getLights().size());

        // Verify bridge details (without optional username)
        assertEquals("bridge-1", config.getBridges().get(0).getId());
        assertEquals("192.168.1.100", config.getBridges().get(0).getIp());

        // Verify light (without optional name)
        assertEquals("light-1", config.getLights().get(0).getId());
        assertEquals(0.0, config.getLights().get(0).getX(), 0.001);
        assertEquals(0.0, config.getLights().get(0).getY(), 0.001);
        assertEquals("bridge-1", config.getLights().get(0).getBridgeId());
        assertEquals("FAST_UDP", config.getLights().get(0).getControlType());
    }

    @Test
    void testRoundTripSerialization() {
        String originalJson = loadResourceAsString("/test-lightmap-valid.json");
        LightMapConfig config = JsonUtils.fromJson(originalJson, LightMapConfig.class);

        // Serialize back to JSON
        String serializedJson = JsonUtils.toJson(config);
        assertNotNull(serializedJson);

        // Deserialize again and verify
        LightMapConfig reloadedConfig = JsonUtils.fromJson(serializedJson, LightMapConfig.class);
        assertEquals(config, reloadedConfig);
    }

    private String loadResourceAsString(String resourcePath) {
        try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new IllegalArgumentException("Resource not found: " + resourcePath);
            }
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load resource: " + resourcePath, e);
        }
    }
}
