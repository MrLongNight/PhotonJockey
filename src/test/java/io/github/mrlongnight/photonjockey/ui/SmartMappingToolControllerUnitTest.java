package io.github.mrlongnight.photonjockey.ui;

import io.github.mrlongnight.photonjockey.hue.dto.BridgeConfig;
import io.github.mrlongnight.photonjockey.hue.dto.LightConfig;
import io.github.mrlongnight.photonjockey.hue.dto.LightMapConfig;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SmartMappingToolController without full TestFX framework.
 * Tests basic functionality and controller initialization.
 */
class SmartMappingToolControllerUnitTest {

    private SmartMappingToolController controller;

    @TempDir
    Path tempDir;

    @BeforeAll
    static void initJavaFX() throws InterruptedException {
        // Initialize JavaFX toolkit
        CountDownLatch latch = new CountDownLatch(1);
        new Thread(() -> {
            new JFXPanel(); // Initializes JavaFX environment
            latch.countDown();
        }).start();
        if (!latch.await(10, TimeUnit.SECONDS)) {
            throw new RuntimeException("JavaFX initialization timed out");
        }
    }

    @BeforeEach
    void setUp() throws Exception {
        URL fxmlUrl = getClass().getResource("/fxml/SmartMappingTool.fxml");
        assertNotNull(fxmlUrl, "FXML file should exist");

        CountDownLatch latch = new CountDownLatch(1);
        javafx.application.Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(fxmlUrl);
                Parent root = loader.load();
                controller = loader.getController();
                assertNotNull(controller, "Controller should be loaded");
                latch.countDown();
            } catch (Exception e) {
                fail("Failed to load FXML: " + e.getMessage());
            }
        });
        if (!latch.await(10, TimeUnit.SECONDS)) {
            throw new RuntimeException("Controller setup timed out");
        }
    }

    @Test
    void testControllerNotNull() {
        assertNotNull(controller, "Controller should be initialized");
    }

    @Test
    void testInitialConfigurationEmpty() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        javafx.application.Platform.runLater(() -> {
            LightMapConfig config = controller.getConfig();
            assertNotNull(config, "Configuration should not be null");
            assertEquals(0, config.getBridges().size(), "Initial bridges should be empty");
            assertEquals(0, config.getLights().size(), "Initial lights should be empty");
            latch.countDown();
        });
        if (!latch.await(5, TimeUnit.SECONDS)) {
            fail("Test timed out");
        }
    }

    @Test
    void testSetConfiguration() throws InterruptedException {
        LightMapConfig testConfig = createTestConfig();

        CountDownLatch latch = new CountDownLatch(1);
        javafx.application.Platform.runLater(() -> {
            controller.setConfig(testConfig);
            LightMapConfig config = controller.getConfig();
            assertEquals(1, config.getBridges().size(), "Should have 1 bridge");
            assertEquals(2, config.getLights().size(), "Should have 2 lights");
            latch.countDown();
        });
        if (!latch.await(5, TimeUnit.SECONDS)) {
            fail("Test timed out");
        }
    }

    @Test
    void testSaveConfiguration() throws Exception {
        LightMapConfig testConfig = createTestConfig();
        File testFile = tempDir.resolve("test-lightmap.json").toFile();

        CountDownLatch saveLatch = new CountDownLatch(1);
        javafx.application.Platform.runLater(() -> {
            controller.setConfig(testConfig);
            controller.saveConfiguration(testFile);
            saveLatch.countDown();
        });
        if (!saveLatch.await(5, TimeUnit.SECONDS)) {
            fail("Save operation timed out");
        }

        // Verify file exists and content is correct
        CountDownLatch verifyLatch = new CountDownLatch(1);
        javafx.application.Platform.runLater(() -> {
            try {
                assertTrue(testFile.exists(), "Configuration file should be created");
                String content = Files.readString(testFile.toPath());
                assertTrue(content.contains("bridge-1"), "File should contain bridge ID");
                assertTrue(content.contains("light-1"), "File should contain light ID");
            } catch (Exception e) {
                fail("Verification failed: " + e.getMessage());
            }
            verifyLatch.countDown();
        });
        if (!verifyLatch.await(5, TimeUnit.SECONDS)) {
            fail("Verification timed out");
        }
    }

    @Test
    void testLoadConfiguration() throws Exception {
        // Create a test configuration file
        LightMapConfig testConfig = createTestConfig();
        File testFile = tempDir.resolve("test-lightmap.json").toFile();

        // First save it
        CountDownLatch saveLatch = new CountDownLatch(1);
        javafx.application.Platform.runLater(() -> {
            controller.setConfig(testConfig);
            controller.saveConfiguration(testFile);
            saveLatch.countDown();
        });
        if (!saveLatch.await(5, TimeUnit.SECONDS)) {
            fail("Save operation timed out");
        }

        // Now load it back
        CountDownLatch loadLatch = new CountDownLatch(1);
        javafx.application.Platform.runLater(() -> {
            controller.setConfig(new LightMapConfig()); // Clear first
            controller.loadConfiguration(testFile);
            loadLatch.countDown();
        });
        if (!loadLatch.await(5, TimeUnit.SECONDS)) {
            fail("Load operation timed out");
        }

        // Verify loaded configuration
        CountDownLatch checkLatch = new CountDownLatch(1);
        javafx.application.Platform.runLater(() -> {
            LightMapConfig config = controller.getConfig();
            assertEquals(1, config.getBridges().size(), "Should load 1 bridge");
            assertEquals(2, config.getLights().size(), "Should load 2 lights");
            assertEquals("bridge-1", config.getBridges().get(0).getId());
            assertEquals("192.168.1.100", config.getBridges().get(0).getIp());
            checkLatch.countDown();
        });
        if (!checkLatch.await(5, TimeUnit.SECONDS)) {
            fail("Verification timed out");
        }
    }

    @Test
    void testSaveAndLoadRoundTrip() throws Exception {
        LightMapConfig testConfig = createTestConfig();
        File testFile = tempDir.resolve("roundtrip-test.json").toFile();

        // Save
        CountDownLatch saveLatch = new CountDownLatch(1);
        javafx.application.Platform.runLater(() -> {
            controller.setConfig(testConfig);
            controller.saveConfiguration(testFile);
            saveLatch.countDown();
        });
        if (!saveLatch.await(5, TimeUnit.SECONDS)) {
            fail("Save operation timed out");
        }

        // Load
        CountDownLatch loadLatch = new CountDownLatch(1);
        javafx.application.Platform.runLater(() -> {
            controller.setConfig(new LightMapConfig());
            controller.loadConfiguration(testFile);
            loadLatch.countDown();
        });
        if (!loadLatch.await(5, TimeUnit.SECONDS)) {
            fail("Load operation timed out");
        }

        // Verify
        CountDownLatch verifyLatch = new CountDownLatch(1);
        javafx.application.Platform.runLater(() -> {
            LightMapConfig loadedConfig = controller.getConfig();
            
            assertEquals(testConfig.getBridges().size(), loadedConfig.getBridges().size());
            assertEquals(testConfig.getLights().size(), loadedConfig.getLights().size());
            
            BridgeConfig originalBridge = testConfig.getBridges().get(0);
            BridgeConfig loadedBridge = loadedConfig.getBridges().get(0);
            assertEquals(originalBridge.getId(), loadedBridge.getId());
            assertEquals(originalBridge.getIp(), loadedBridge.getIp());
            
            LightConfig originalLight = testConfig.getLights().get(0);
            LightConfig loadedLight = loadedConfig.getLights().get(0);
            assertEquals(originalLight.getId(), loadedLight.getId());
            assertEquals(originalLight.getX(), loadedLight.getX(), 0.01);
            assertEquals(originalLight.getY(), loadedLight.getY(), 0.01);
            assertEquals(originalLight.getBridgeId(), loadedLight.getBridgeId());
            assertEquals(originalLight.getControlType(), loadedLight.getControlType());
            
            verifyLatch.countDown();
        });
        if (!verifyLatch.await(5, TimeUnit.SECONDS)) {
            fail("Verification timed out");
        }
    }

    @Test
    void testSetNullConfiguration() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        javafx.application.Platform.runLater(() -> {
            controller.setConfig(null);
            LightMapConfig config = controller.getConfig();
            assertNotNull(config, "Config should not be null even when set to null");
            assertEquals(0, config.getBridges().size());
            assertEquals(0, config.getLights().size());
            latch.countDown();
        });
        if (!latch.await(5, TimeUnit.SECONDS)) {
            fail("Test timed out");
        }
    }

    @Test
    void testConfigurationWithMultipleLights() throws InterruptedException {
        LightMapConfig testConfig = new LightMapConfig();
        
        BridgeConfig bridge = new BridgeConfig("bridge-1", "192.168.1.100", null);
        testConfig.getBridges().add(bridge);
        
        for (int i = 1; i <= 5; i++) {
            LightConfig light = new LightConfig(
                "light-" + i,
                i * 50.0,
                i * 40.0,
                "bridge-1",
                i % 2 == 0 ? "FAST_UDP" : "LOW_HTTP",
                "Light " + i
            );
            testConfig.getLights().add(light);
        }

        CountDownLatch latch = new CountDownLatch(1);
        javafx.application.Platform.runLater(() -> {
            controller.setConfig(testConfig);
            LightMapConfig config = controller.getConfig();
            assertEquals(5, config.getLights().size(), "Should have 5 lights");
            latch.countDown();
        });
        if (!latch.await(5, TimeUnit.SECONDS)) {
            fail("Test timed out");
        }
    }

    /**
     * Creates a test configuration for testing.
     */
    private LightMapConfig createTestConfig() {
        LightMapConfig config = new LightMapConfig();
        
        BridgeConfig bridge = new BridgeConfig("bridge-1", "192.168.1.100", "testuser");
        config.getBridges().add(bridge);
        
        LightConfig light1 = new LightConfig(
            "light-1",
            100.0,
            150.0,
            "bridge-1",
            "FAST_UDP",
            "Living Room"
        );
        config.getLights().add(light1);
        
        LightConfig light2 = new LightConfig(
            "light-2",
            300.0,
            250.0,
            "bridge-1",
            "LOW_HTTP",
            "Bedroom"
        );
        config.getLights().add(light2);
        
        return config;
    }
}
