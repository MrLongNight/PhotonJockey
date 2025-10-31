package io.github.mrlongnight.photonjockey.ui;

import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import io.github.mrlongnight.photonjockey.audio.AudioFrame;

import java.net.URL;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for AudioAnalyzerDashboardController without full TestFX framework.
 * Tests basic functionality and controller initialization.
 */
class AudioAnalyzerDashboardControllerUnitTest {

    private AudioAnalyzerDashboardController controller;

    @BeforeAll
    static void initJavaFX() throws InterruptedException {
        // Initialize JavaFX toolkit
        CountDownLatch latch = new CountDownLatch(1);
        new Thread(() -> {
            new JFXPanel(); // Initializes JavaFX environment
            latch.countDown();
        }).start();
        latch.await();
    }

    @BeforeEach
    void setUp() throws Exception {
        URL fxmlUrl = getClass().getResource("/fxml/AudioAnalyzerDashboard.fxml");
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
        latch.await();
    }

    @Test
    void testControllerNotNull() {
        assertNotNull(controller, "Controller should be initialized");
    }

    @Test
    void testGetGainDefaultValue() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        javafx.application.Platform.runLater(() -> {
            assertEquals(1.0, controller.getGain(), 0.01, "Default gain should be 1.0");
            latch.countDown();
        });
        latch.await();
    }

    @Test
    void testGetBeatSensitivityDefaultValue() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        javafx.application.Platform.runLater(() -> {
            assertEquals(1.3, controller.getBeatSensitivity(), 0.01, 
                         "Default beat sensitivity should be 1.3");
            latch.countDown();
        });
        latch.await();
    }

    @Test
    void testSetGain() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        javafx.application.Platform.runLater(() -> {
            controller.setGain(1.5);
            assertEquals(1.5, controller.getGain(), 0.01, "Gain should be set to 1.5");
            latch.countDown();
        });
        latch.await();
    }

    @Test
    void testSetBeatSensitivity() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        javafx.application.Platform.runLater(() -> {
            controller.setBeatSensitivity(1.8);
            assertEquals(1.8, controller.getBeatSensitivity(), 0.01, 
                         "Beat sensitivity should be set to 1.8");
            latch.countDown();
        });
        latch.await();
    }

    @Test
    void testUpdateWaveformWithNull() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        javafx.application.Platform.runLater(() -> {
            assertDoesNotThrow(() -> controller.updateWaveform(null));
            latch.countDown();
        });
        latch.await();
    }

    @Test
    void testUpdateWaveformWithValidData() throws InterruptedException {
        byte[] data = new byte[256];
        for (int i = 0; i < data.length; i += 2) {
            short sample = (short) (Math.sin(i / 50.0) * 16384);
            data[i] = (byte) (sample & 0xFF);
            data[i + 1] = (byte) ((sample >> 8) & 0xFF);
        }
        AudioFrame frame = new AudioFrame(data, 44100, 1, System.currentTimeMillis());

        CountDownLatch latch = new CountDownLatch(1);
        javafx.application.Platform.runLater(() -> {
            assertDoesNotThrow(() -> controller.updateWaveform(frame));
            latch.countDown();
        });
        latch.await();
    }

    @Test
    void testUpdateSpectrumWithNull() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        javafx.application.Platform.runLater(() -> {
            assertDoesNotThrow(() -> controller.updateSpectrum(null));
            latch.countDown();
        });
        latch.await();
    }

    @Test
    void testUpdateSpectrumWithValidData() throws InterruptedException {
        double[] spectrum = new double[64];
        for (int i = 0; i < spectrum.length; i++) {
            spectrum[i] = Math.random();
        }

        CountDownLatch latch = new CountDownLatch(1);
        javafx.application.Platform.runLater(() -> {
            assertDoesNotThrow(() -> controller.updateSpectrum(spectrum));
            latch.countDown();
        });
        latch.await();
    }

    @Test
    void testUpdateBeatIndicator() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        javafx.application.Platform.runLater(() -> {
            assertDoesNotThrow(() -> controller.updateBeatIndicator(false, 0.0));
            assertDoesNotThrow(() -> controller.updateBeatIndicator(true, 120.0));
            latch.countDown();
        });
        latch.await();
    }

    @Test
    void testClear() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        javafx.application.Platform.runLater(() -> {
            assertDoesNotThrow(() -> controller.clear());
            latch.countDown();
        });
        latch.await();
    }

    @Test
    void testGainBounds() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        javafx.application.Platform.runLater(() -> {
            // Test minimum
            controller.setGain(0.0);
            assertEquals(0.0, controller.getGain(), 0.01);

            // Test maximum
            controller.setGain(2.0);
            assertEquals(2.0, controller.getGain(), 0.01);

            latch.countDown();
        });
        latch.await();
    }

    @Test
    void testBeatSensitivityBounds() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        javafx.application.Platform.runLater(() -> {
            // Test minimum
            controller.setBeatSensitivity(0.5);
            assertEquals(0.5, controller.getBeatSensitivity(), 0.01);

            // Test maximum
            controller.setBeatSensitivity(2.0);
            assertEquals(2.0, controller.getBeatSensitivity(), 0.01);

            latch.countDown();
        });
        latch.await();
    }
}
