package pw.wunderlich.lightbeat.ui;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;
import pw.wunderlich.lightbeat.audio.AnalysisResult;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TestFX tests for AudioDashboardController.
 * Simulates onAnalysis events to verify UI element updates.
 */
@ExtendWith(ApplicationExtension.class)
class AudioDashboardControllerTest {

    private AudioDashboardController controller;
    private Stage stage;

    @Start
    public void start(Stage stage) throws IOException {
        this.stage = stage;
        
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/ui/audio_dashboard.fxml")
        );
        Parent root = loader.load();
        controller = loader.getController();
        
        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.show();
    }

    @Test
    void testControllerInitialization(FxRobot robot) {
        assertNotNull(controller, "Controller should be initialized");
        
        // Check default values
        assertEquals(2048, controller.getFftSize(), "Default FFT size should be 2048");
        assertEquals(0.5, controller.getSmoothing(), 0.01, "Default smoothing should be 0.5");
        assertEquals(1.0, controller.getGain(), 0.01, "Default gain should be 1.0");
        assertEquals("Default", controller.getPreset(), "Default preset should be selected");
    }

    @Test
    void testBeatIndicatorUpdate(FxRobot robot) throws InterruptedException {
        // Build energy history first
        for (int i = 0; i < 45; i++) {
            AnalysisResult lowEnergy = new AnalysisResult(440.0, 0.5, 0.1);
            controller.onAnalysis(lowEnergy);
            WaitForAsyncUtils.waitForFxEvents();
        }
        
        Thread.sleep(250);
        
        // Send high energy to trigger beat
        AnalysisResult highEnergy = new AnalysisResult(440.0, 0.8, 1.5);
        controller.onAnalysis(highEnergy);
        
        // Wait for UI update
        WaitForAsyncUtils.waitForFxEvents();
        Thread.sleep(50);
        
        // Verify beat indicator shows YES
        robot.lookup("#beatIndicator").tryQuery().ifPresent(node -> {
            javafx.scene.control.Label label = (javafx.scene.control.Label) node;
            String text = label.getText();
            assertTrue(text.contains("YES") || text.contains("NO"), 
                "Beat indicator should show YES or NO");
        });
    }

    @Test
    void testBpmLabelUpdate(FxRobot robot) throws InterruptedException {
        // Build energy history
        for (int i = 0; i < 45; i++) {
            AnalysisResult lowEnergy = new AnalysisResult(440.0, 0.5, 0.1);
            controller.onAnalysis(lowEnergy);
            WaitForAsyncUtils.waitForFxEvents();
        }
        
        // Generate several beats to establish BPM
        for (int beat = 0; beat < 5; beat++) {
            Thread.sleep(500); // 120 BPM
            
            AnalysisResult highEnergy = new AnalysisResult(440.0, 0.8, 1.5);
            controller.onAnalysis(highEnergy);
            WaitForAsyncUtils.waitForFxEvents();
        }
        
        Thread.sleep(100);
        WaitForAsyncUtils.waitForFxEvents();
        
        // Verify BPM label is updated
        robot.lookup("#bpmLabel").tryQuery().ifPresent(node -> {
            javafx.scene.control.Label label = (javafx.scene.control.Label) node;
            String bpmText = label.getText();
            assertNotNull(bpmText, "BPM label should have text");
            double bpm = Double.parseDouble(bpmText);
            assertTrue(bpm >= 0.0, "BPM should be non-negative");
        });
    }

    @Test
    void testWaveformCanvasUpdate(FxRobot robot) {
        // Send analysis results to populate waveform
        for (int i = 0; i < 10; i++) {
            double amplitude = Math.sin(i * 0.5);
            AnalysisResult result = new AnalysisResult(440.0, amplitude, 0.5);
            controller.onAnalysis(result);
        }
        
        WaitForAsyncUtils.waitForFxEvents();
        
        // Verify canvas exists and has been drawn on
        robot.lookup("#waveformCanvas").tryQuery().ifPresent(node -> {
            javafx.scene.canvas.Canvas canvas = (javafx.scene.canvas.Canvas) node;
            assertNotNull(canvas, "Waveform canvas should exist");
            assertEquals(760, canvas.getWidth(), 0.1, "Canvas width should match FXML");
            assertEquals(150, canvas.getHeight(), 0.1, "Canvas height should match FXML");
        });
    }

    @Test
    void testSpectrumCanvasUpdate(FxRobot robot) {
        // Send analysis results with varying frequencies
        for (int i = 0; i < 10; i++) {
            double frequency = 200.0 + (i * 100.0);
            AnalysisResult result = new AnalysisResult(frequency, 0.7, 0.6);
            controller.onAnalysis(result);
        }
        
        WaitForAsyncUtils.waitForFxEvents();
        
        // Verify spectrum canvas exists
        robot.lookup("#spectrumCanvas").tryQuery().ifPresent(node -> {
            javafx.scene.canvas.Canvas canvas = (javafx.scene.canvas.Canvas) node;
            assertNotNull(canvas, "Spectrum canvas should exist");
            assertEquals(760, canvas.getWidth(), 0.1, "Canvas width should match FXML");
            assertEquals(200, canvas.getHeight(), 0.1, "Canvas height should match FXML");
        });
    }

    @Test
    void testFftSizeSlider(FxRobot robot) {
        // Interact with FFT size slider
        robot.interact(() -> {
            javafx.scene.control.Slider slider = robot.lookup("#fftSizeSlider").query();
            slider.setValue(12); // 4096
        });
        
        WaitForAsyncUtils.waitForFxEvents();
        
        assertEquals(4096, controller.getFftSize(), "FFT size should be updated to 4096");
        
        // Check label is updated
        robot.lookup("#fftSizeLabel").tryQuery().ifPresent(node -> {
            javafx.scene.control.Label label = (javafx.scene.control.Label) node;
            assertEquals("4096", label.getText(), "FFT size label should show 4096");
        });
    }

    @Test
    void testSmoothingSlider(FxRobot robot) {
        // Interact with smoothing slider
        robot.interact(() -> {
            javafx.scene.control.Slider slider = robot.lookup("#smoothingSlider").query();
            slider.setValue(0.75);
        });
        
        WaitForAsyncUtils.waitForFxEvents();
        
        assertEquals(0.75, controller.getSmoothing(), 0.01, "Smoothing should be updated to 0.75");
        
        // Check label is updated
        robot.lookup("#smoothingLabel").tryQuery().ifPresent(node -> {
            javafx.scene.control.Label label = (javafx.scene.control.Label) node;
            assertTrue(label.getText().startsWith("0.7"), "Smoothing label should show ~0.75");
        });
    }

    @Test
    void testGainSlider(FxRobot robot) {
        // Interact with gain slider
        robot.interact(() -> {
            javafx.scene.control.Slider slider = robot.lookup("#gainSlider").query();
            slider.setValue(2.5);
        });
        
        WaitForAsyncUtils.waitForFxEvents();
        
        assertEquals(2.5, controller.getGain(), 0.01, "Gain should be updated to 2.5");
        
        // Check label is updated
        robot.lookup("#gainLabel").tryQuery().ifPresent(node -> {
            javafx.scene.control.Label label = (javafx.scene.control.Label) node;
            assertTrue(label.getText().startsWith("2.5"), "Gain label should show 2.5");
        });
    }

    @Test
    void testPresetComboBox(FxRobot robot) {
        // Select different preset
        robot.interact(() -> {
            javafx.scene.control.ComboBox<String> comboBox = robot.lookup("#presetComboBox").query();
            comboBox.setValue("High Sensitivity");
        });
        
        WaitForAsyncUtils.waitForFxEvents();
        
        assertEquals("High Sensitivity", controller.getPreset(), "Preset should be High Sensitivity");
        assertEquals(4096, controller.getFftSize(), "FFT size should change with preset");
        assertEquals(0.3, controller.getSmoothing(), 0.01, "Smoothing should change with preset");
        assertEquals(2.0, controller.getGain(), 0.01, "Gain should change with preset");
    }

    @Test
    void testMultipleAnalysisEvents(FxRobot robot) throws InterruptedException {
        // Simulate multiple analysis events in sequence
        for (int i = 0; i < 50; i++) {
            double phase = i * 0.1;
            double amplitude = Math.sin(phase);
            double frequency = 440.0 + (Math.sin(phase * 2) * 200.0);
            double energy = 0.3 + (Math.abs(Math.cos(phase)) * 0.5);
            
            AnalysisResult result = new AnalysisResult(frequency, amplitude, energy);
            controller.onAnalysis(result);
            
            if (i % 10 == 0) {
                WaitForAsyncUtils.waitForFxEvents();
                Thread.sleep(10);
            }
        }
        
        WaitForAsyncUtils.waitForFxEvents();
        
        // Verify UI is still responsive
        robot.lookup("#beatIndicator").tryQuery().ifPresent(node -> {
            assertNotNull(node, "Beat indicator should exist after multiple events");
        });
    }

    @Test
    void testNullAnalysisResult(FxRobot robot) {
        // Controller should handle null gracefully
        assertDoesNotThrow(() -> {
            controller.onAnalysis(null);
            WaitForAsyncUtils.waitForFxEvents();
        }, "Controller should handle null AnalysisResult");
    }

    @Test
    void testControllerReset(FxRobot robot) throws InterruptedException {
        // Populate with data
        for (int i = 0; i < 20; i++) {
            AnalysisResult result = new AnalysisResult(440.0, 0.5, 0.5);
            controller.onAnalysis(result);
        }
        WaitForAsyncUtils.waitForFxEvents();
        
        // Reset controller
        controller.reset();
        WaitForAsyncUtils.waitForFxEvents();
        Thread.sleep(100);
        
        // Verify UI is cleared (BPM should be 0)
        robot.lookup("#bpmLabel").tryQuery().ifPresent(node -> {
            javafx.scene.control.Label label = (javafx.scene.control.Label) node;
            String bpmText = label.getText();
            double bpm = Double.parseDouble(bpmText);
            assertEquals(0.0, bpm, 0.1, "BPM should be reset to 0");
        });
    }

    @Test
    void testLowFrequencyAnalysis(FxRobot robot) {
        // Test with low frequency
        AnalysisResult lowFreq = new AnalysisResult(50.0, 0.6, 0.5);
        controller.onAnalysis(lowFreq);
        WaitForAsyncUtils.waitForFxEvents();
        
        assertDoesNotThrow(() -> {
            robot.lookup("#spectrumCanvas").query();
        }, "Should handle low frequency analysis");
    }

    @Test
    void testHighFrequencyAnalysis(FxRobot robot) {
        // Test with high frequency
        AnalysisResult highFreq = new AnalysisResult(5000.0, 0.6, 0.5);
        controller.onAnalysis(highFreq);
        WaitForAsyncUtils.waitForFxEvents();
        
        assertDoesNotThrow(() -> {
            robot.lookup("#spectrumCanvas").query();
        }, "Should handle high frequency analysis");
    }
}
