package io.github.mrlongnight.photonjockey.ui;

import io.github.mrlongnight.photonjockey.hue.dto.BridgeConfig;
import io.github.mrlongnight.photonjockey.hue.dto.LightConfig;
import io.github.mrlongnight.photonjockey.hue.dto.LightMapConfig;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URL;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for drag-and-drop functionality in SmartMappingToolController.
 */
class SmartMappingToolDragDropTest {

    private SmartMappingToolController controller;
    private Canvas canvas;

    @BeforeAll
    static void initJavaFX() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        new Thread(() -> {
            new JFXPanel();
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
                canvas = (Canvas) root.lookup("#mapCanvas");
                assertNotNull(controller, "Controller should be loaded");
                assertNotNull(canvas, "Canvas should be found");
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
    void testLightDragUpdatesPosition() throws InterruptedException {
        // Setup: Create a configuration with a light
        LightMapConfig config = new LightMapConfig();
        BridgeConfig bridge = new BridgeConfig("bridge-1", "192.168.1.100", null);
        config.getBridges().add(bridge);
        
        LightConfig light = new LightConfig(
            "light-1",
            100.0,
            100.0,
            "bridge-1",
            "FAST_UDP",
            "Test Light"
        );
        config.getLights().add(light);

        CountDownLatch setupLatch = new CountDownLatch(1);
        javafx.application.Platform.runLater(() -> {
            controller.setConfig(config);
            setupLatch.countDown();
        });
        if (!setupLatch.await(5, TimeUnit.SECONDS)) {
            fail("Setup timed out");
        }

        // Simulate drag from initial position to new position
        double initialX = 100.0;
        double initialY = 100.0;
        double targetX = 200.0;
        double targetY = 150.0;

        CountDownLatch dragLatch = new CountDownLatch(1);
        javafx.application.Platform.runLater(() -> {
            // Simulate mouse press
            MouseEvent pressEvent = new MouseEvent(
                MouseEvent.MOUSE_PRESSED,
                initialX, initialY,
                initialX, initialY,
                MouseButton.PRIMARY,
                1,
                false, false, false, false,
                true, false, false, false, false, false,
                null
            );
            canvas.fireEvent(pressEvent);

            // Simulate mouse drag
            MouseEvent dragEvent = new MouseEvent(
                MouseEvent.MOUSE_DRAGGED,
                targetX, targetY,
                targetX, targetY,
                MouseButton.PRIMARY,
                1,
                false, false, false, false,
                true, false, false, false, false, false,
                null
            );
            canvas.fireEvent(dragEvent);

            // Simulate mouse release
            MouseEvent releaseEvent = new MouseEvent(
                MouseEvent.MOUSE_RELEASED,
                targetX, targetY,
                targetX, targetY,
                MouseButton.PRIMARY,
                1,
                false, false, false, false,
                false, false, false, false, false, false,
                null
            );
            canvas.fireEvent(releaseEvent);

            dragLatch.countDown();
        });
        if (!dragLatch.await(5, TimeUnit.SECONDS)) {
            fail("Drag operation timed out");
        }

        // Verify the light position was updated
        CountDownLatch verifyLatch = new CountDownLatch(1);
        javafx.application.Platform.runLater(() -> {
            LightConfig movedLight = controller.getConfig().getLights().get(0);
            assertEquals(targetX, movedLight.getX(), 1.0, "X position should be updated");
            assertEquals(targetY, movedLight.getY(), 1.0, "Y position should be updated");
            verifyLatch.countDown();
        });
        if (!verifyLatch.await(5, TimeUnit.SECONDS)) {
            fail("Verification timed out");
        }
    }

    @Test
    void testDragMultipleLightsSequentially() throws InterruptedException {
        // Setup: Create configuration with multiple lights
        LightMapConfig config = new LightMapConfig();
        BridgeConfig bridge = new BridgeConfig("bridge-1", "192.168.1.100", null);
        config.getBridges().add(bridge);
        
        LightConfig light1 = new LightConfig(
            "light-1", 100.0, 100.0, "bridge-1", "FAST_UDP", "Light 1"
        );
        LightConfig light2 = new LightConfig(
            "light-2", 200.0, 200.0, "bridge-1", "LOW_HTTP", "Light 2"
        );
        config.getLights().add(light1);
        config.getLights().add(light2);

        CountDownLatch setupLatch = new CountDownLatch(1);
        javafx.application.Platform.runLater(() -> {
            controller.setConfig(config);
            setupLatch.countDown();
        });
        if (!setupLatch.await(5, TimeUnit.SECONDS)) {
            fail("Setup timed out");
        }

        // Drag first light
        CountDownLatch drag1Latch = new CountDownLatch(1);
        javafx.application.Platform.runLater(() -> {
            simulateDrag(canvas, 100.0, 100.0, 150.0, 150.0);
            drag1Latch.countDown();
        });
        if (!drag1Latch.await(5, TimeUnit.SECONDS)) {
            fail("First drag operation timed out");
        }

        // Drag second light
        CountDownLatch drag2Latch = new CountDownLatch(1);
        javafx.application.Platform.runLater(() -> {
            simulateDrag(canvas, 200.0, 200.0, 250.0, 250.0);
            drag2Latch.countDown();
        });
        if (!drag2Latch.await(5, TimeUnit.SECONDS)) {
            fail("Second drag operation timed out");
        }

        // Verify both lights were moved
        CountDownLatch verifyLatch = new CountDownLatch(1);
        javafx.application.Platform.runLater(() -> {
            LightConfig movedLight1 = controller.getConfig().getLights().get(0);
            LightConfig movedLight2 = controller.getConfig().getLights().get(1);
            
            assertEquals(150.0, movedLight1.getX(), 1.0);
            assertEquals(150.0, movedLight1.getY(), 1.0);
            assertEquals(250.0, movedLight2.getX(), 1.0);
            assertEquals(250.0, movedLight2.getY(), 1.0);
            
            verifyLatch.countDown();
        });
        if (!verifyLatch.await(5, TimeUnit.SECONDS)) {
            fail("Verification timed out");
        }
    }

    @Test
    void testDragLightToCanvasBounds() throws InterruptedException {
        // Setup
        LightMapConfig config = new LightMapConfig();
        BridgeConfig bridge = new BridgeConfig("bridge-1", "192.168.1.100", null);
        config.getBridges().add(bridge);
        
        LightConfig light = new LightConfig(
            "light-1", 400.0, 250.0, "bridge-1", "FAST_UDP", "Test Light"
        );
        config.getLights().add(light);

        CountDownLatch setupLatch = new CountDownLatch(1);
        javafx.application.Platform.runLater(() -> {
            controller.setConfig(config);
            setupLatch.countDown();
        });
        if (!setupLatch.await(5, TimeUnit.SECONDS)) {
            fail("Setup timed out");
        }

        // Try to drag beyond canvas bounds
        double canvasWidth = canvas.getWidth();
        double canvasHeight = canvas.getHeight();

        CountDownLatch dragLatch = new CountDownLatch(1);
        javafx.application.Platform.runLater(() -> {
            // Try to drag way beyond bounds
            simulateDrag(canvas, 400.0, 250.0, canvasWidth + 100, canvasHeight + 100);
            dragLatch.countDown();
        });
        if (!dragLatch.await(5, TimeUnit.SECONDS)) {
            fail("Drag operation timed out");
        }

        // Verify light is clamped to canvas bounds
        CountDownLatch verifyLatch = new CountDownLatch(1);
        javafx.application.Platform.runLater(() -> {
            LightConfig movedLight = controller.getConfig().getLights().get(0);
            assertTrue(movedLight.getX() <= canvasWidth, "X should be within canvas width");
            assertTrue(movedLight.getY() <= canvasHeight, "Y should be within canvas height");
            assertTrue(movedLight.getX() >= 0, "X should be non-negative");
            assertTrue(movedLight.getY() >= 0, "Y should be non-negative");
            verifyLatch.countDown();
        });
        if (!verifyLatch.await(5, TimeUnit.SECONDS)) {
            fail("Verification timed out");
        }
    }

    @Test
    void testClickOutsideLightDoesNotDrag() throws InterruptedException {
        // Setup
        LightMapConfig config = new LightMapConfig();
        BridgeConfig bridge = new BridgeConfig("bridge-1", "192.168.1.100", null);
        config.getBridges().add(bridge);
        
        LightConfig light = new LightConfig(
            "light-1", 100.0, 100.0, "bridge-1", "FAST_UDP", "Test Light"
        );
        config.getLights().add(light);

        CountDownLatch setupLatch = new CountDownLatch(1);
        javafx.application.Platform.runLater(() -> {
            controller.setConfig(config);
            setupLatch.countDown();
        });
        if (!setupLatch.await(5, TimeUnit.SECONDS)) {
            fail("Setup timed out");
        }

        double originalX = light.getX();
        double originalY = light.getY();

        // Click and drag far from the light
        CountDownLatch dragLatch = new CountDownLatch(1);
        javafx.application.Platform.runLater(() -> {
            simulateDrag(canvas, 500.0, 500.0, 600.0, 600.0);
            dragLatch.countDown();
        });
        if (!dragLatch.await(5, TimeUnit.SECONDS)) {
            fail("Drag operation timed out");
        }

        // Verify light position unchanged
        CountDownLatch verifyLatch = new CountDownLatch(1);
        javafx.application.Platform.runLater(() -> {
            LightConfig unchangedLight = controller.getConfig().getLights().get(0);
            assertEquals(originalX, unchangedLight.getX(), 0.01, "X should be unchanged");
            assertEquals(originalY, unchangedLight.getY(), 0.01, "Y should be unchanged");
            verifyLatch.countDown();
        });
        if (!verifyLatch.await(5, TimeUnit.SECONDS)) {
            fail("Verification timed out");
        }
    }

    /**
     * Helper method to simulate a drag operation.
     */
    private void simulateDrag(Canvas canvas, double fromX, double fromY, double toX, double toY) {
        MouseEvent pressEvent = new MouseEvent(
            MouseEvent.MOUSE_PRESSED,
            fromX, fromY, fromX, fromY,
            MouseButton.PRIMARY, 1,
            false, false, false, false,
            true, false, false, false, false, false,
            null
        );
        canvas.fireEvent(pressEvent);

        MouseEvent dragEvent = new MouseEvent(
            MouseEvent.MOUSE_DRAGGED,
            toX, toY, toX, toY,
            MouseButton.PRIMARY, 1,
            false, false, false, false,
            true, false, false, false, false, false,
            null
        );
        canvas.fireEvent(dragEvent);

        MouseEvent releaseEvent = new MouseEvent(
            MouseEvent.MOUSE_RELEASED,
            toX, toY, toX, toY,
            MouseButton.PRIMARY, 1,
            false, false, false, false,
            false, false, false, false, false, false,
            null
        );
        canvas.fireEvent(releaseEvent);
    }
}
