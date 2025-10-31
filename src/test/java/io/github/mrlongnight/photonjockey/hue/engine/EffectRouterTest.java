package io.github.mrlongnight.photonjockey.hue.engine;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EffectRouterTest {

    private EffectRouter router;
    private MockFastEffectController fastController;
    private MockLowEffectController lowController;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        router = new EffectRouter();
        fastController = new MockFastEffectController();
        lowController = new MockLowEffectController();
        router.setFastController(fastController);
        router.setLowController(lowController);
    }

    @Test
    void testConstructor() {
        EffectRouter newRouter = new EffectRouter();
        assertNotNull(newRouter);
        assertEquals(0, newRouter.getLightCount());
    }

    @Test
    void testLoadLightMapFromStream() throws IOException {
        String json = """
                {
                  "bridges": [
                    {"id": "bridge-001", "ip": "192.168.1.100"}
                  ],
                  "lights": [
                    {"id": "light-001", "x": 0.0, "y": 0.0, "bridgeId": "bridge-001", "controlType": "FAST_UDP"},
                    {"id": "light-002", "x": 1.0, "y": 0.0, "bridgeId": "bridge-001", "controlType": "LOW_HTTP"}
                  ]
                }
                """;
        InputStream inputStream = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));

        router.loadLightMapFromStream(inputStream);

        assertEquals(2, router.getLightCount());
        assertEquals(EffectRouter.ControlType.FAST_UDP, router.getControlType("light-001"));
        assertEquals(EffectRouter.ControlType.LOW_HTTP, router.getControlType("light-002"));
    }

    @Test
    void testLoadLightMapFromFile() throws IOException {
        String json = """
                {
                  "bridges": [
                    {"id": "bridge-001", "ip": "192.168.1.100"}
                  ],
                  "lights": [
                    {"id": "light-003", "x": 0.0, "y": 0.0, "bridgeId": "bridge-001", "controlType": "FAST_UDP"}
                  ]
                }
                """;
        Path configPath = tempDir.resolve("lightmap.json");
        Files.writeString(configPath, json);

        router.loadLightMap(configPath);

        assertEquals(1, router.getLightCount());
        assertEquals(EffectRouter.ControlType.FAST_UDP, router.getControlType("light-003"));
    }

    @Test
    void testLoadLightMapFileNotFound() {
        Path nonExistentPath = tempDir.resolve("nonexistent.json");
        
        IOException exception = assertThrows(IOException.class, () -> {
            router.loadLightMap(nonExistentPath);
        });
        
        assertNotNull(exception.getMessage());
    }

    @Test
    void testRouteFrameToFastController() throws IOException {
        String json = """
                {
                  "lights": [
                    {"id": "light-001", "controlType": "FAST_UDP"}
                  ]
                }
                """;
        router.loadLightMapFromStream(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)));

        LightUpdateDTO update = new LightUpdateDTO("light-001", 200, 0.5, 0.8, 0);
        List<LightUpdateDTO> updates = List.of(update);
        EffectFrame frame = new EffectFrame(updates, System.currentTimeMillis());

        router.routeFrame(frame);

        assertEquals(1, fastController.getFrameCount());
        assertEquals(0, lowController.getUpdateCount());
    }

    @Test
    void testRouteFrameToLowController() throws IOException {
        String json = """
                {
                  "lights": [
                    {"id": "light-002", "controlType": "LOW_HTTP"}
                  ]
                }
                """;
        router.loadLightMapFromStream(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)));

        LightUpdateDTO update = new LightUpdateDTO("light-002", 150, 0.3, 0.9, 5);
        List<LightUpdateDTO> updates = List.of(update);
        EffectFrame frame = new EffectFrame(updates, System.currentTimeMillis());

        router.routeFrame(frame);

        assertEquals(0, fastController.getFrameCount());
        assertEquals(1, lowController.getUpdateCount());
    }

    @Test
    void testRouteFrameToBothControllers() throws IOException {
        String json = """
                {
                  "lights": [
                    {"id": "light-001", "controlType": "FAST_UDP"},
                    {"id": "light-002", "controlType": "LOW_HTTP"}
                  ]
                }
                """;
        router.loadLightMapFromStream(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)));

        List<LightUpdateDTO> updates = new ArrayList<>();
        updates.add(new LightUpdateDTO("light-001", 200, 0.5, 0.8, 0));
        updates.add(new LightUpdateDTO("light-002", 150, 0.3, 0.9, 5));
        EffectFrame frame = new EffectFrame(updates, System.currentTimeMillis());

        router.routeFrame(frame);

        assertEquals(1, fastController.getFrameCount());
        assertEquals(1, lowController.getUpdateCount());
    }

    @Test
    void testRouteFrameWithUnknownLight() throws IOException {
        String json = """
                {
                  "lights": [
                    {"id": "light-001", "controlType": "FAST_UDP"}
                  ]
                }
                """;
        router.loadLightMapFromStream(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)));

        LightUpdateDTO update = new LightUpdateDTO("unknown-light", 200, 0.5, 0.8, 0);
        List<LightUpdateDTO> updates = List.of(update);
        EffectFrame frame = new EffectFrame(updates, System.currentTimeMillis());

        router.routeFrame(frame);

        assertEquals(0, fastController.getFrameCount());
        assertEquals(0, lowController.getUpdateCount());
    }

    @Test
    void testRouteNullFrame() {
        router.routeFrame(null);

        assertEquals(0, fastController.getFrameCount());
        assertEquals(0, lowController.getUpdateCount());
    }

    @Test
    void testGetControlTypeForUnknownLight() {
        assertNull(router.getControlType("unknown-light"));
    }

    @Test
    void testLoadLightMapWithInvalidControlType() throws IOException {
        String json = """
                {
                  "lights": [
                    {"id": "light-001", "controlType": "FAST_UDP"},
                    {"id": "light-002", "controlType": "INVALID_TYPE"},
                    {"id": "light-003", "controlType": "LOW_HTTP"}
                  ]
                }
                """;
        router.loadLightMapFromStream(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)));

        // Should load only valid lights, skipping the invalid one
        assertEquals(2, router.getLightCount());
        assertEquals(EffectRouter.ControlType.FAST_UDP, router.getControlType("light-001"));
        assertNull(router.getControlType("light-002"));
        assertEquals(EffectRouter.ControlType.LOW_HTTP, router.getControlType("light-003"));
    }

    @Test
    void testMultipleFrameRouting() throws IOException {
        String json = """
                {
                  "lights": [
                    {"id": "light-001", "controlType": "FAST_UDP"},
                    {"id": "light-002", "controlType": "LOW_HTTP"}
                  ]
                }
                """;
        router.loadLightMapFromStream(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)));

        // Route multiple frames
        for (int i = 0; i < 5; i++) {
            List<LightUpdateDTO> updates = new ArrayList<>();
            updates.add(new LightUpdateDTO("light-001", 200, 0.5, 0.8, 0));
            updates.add(new LightUpdateDTO("light-002", 150, 0.3, 0.9, 5));
            EffectFrame frame = new EffectFrame(updates, System.currentTimeMillis());
            router.routeFrame(frame);
        }

        assertEquals(5, fastController.getFrameCount());
        assertEquals(5, lowController.getUpdateCount());
    }

    /**
     * Mock implementation of IFastEffectController for testing.
     */
    private static class MockFastEffectController implements IFastEffectController {
        private int frameCount = 0;
        private boolean sessionActive = false;

        @Override
        public void startSession() {
            sessionActive = true;
        }

        @Override
        public void sendFrame(EffectFrame frame) {
            frameCount++;
        }

        @Override
        public void stopSession() {
            sessionActive = false;
        }

        public int getFrameCount() {
            return frameCount;
        }

        public boolean isSessionActive() {
            return sessionActive;
        }
    }

    /**
     * Mock implementation of ILowEffectController for testing.
     */
    private static class MockLowEffectController implements ILowEffectController {
        private int updateCount = 0;

        @Override
        public void updateLights(List<LightUpdateDTO> updates) {
            updateCount++;
        }

        public int getUpdateCount() {
            return updateCount;
        }
    }
}
