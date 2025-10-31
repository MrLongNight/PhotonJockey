package io.github.mrlongnight.photonjockey.ui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import io.github.mrlongnight.photonjockey.AppTaskOrchestrator;
import io.github.mrlongnight.photonjockey.audio.AudioFrame;
import io.github.mrlongnight.photonjockey.audio.BeatEvent;
import io.github.mrlongnight.photonjockey.audio.BeatObserver;
import io.github.mrlongnight.photonjockey.audio.FFTProcessor;
import io.github.mrlongnight.photonjockey.audio.PJAudioReader;
import io.github.mrlongnight.photonjockey.audio.WindowFunction;
import io.github.mrlongnight.photonjockey.audio.device.AudioDevice;
import io.github.mrlongnight.photonjockey.config.Config;
import io.github.mrlongnight.photonjockey.config.PJConfig;
import io.github.mrlongnight.photonjockey.hue.bridge.AccessPoint;
import io.github.mrlongnight.photonjockey.hue.bridge.BridgeConnection;
import io.github.mrlongnight.photonjockey.hue.bridge.HueManager;
import io.github.mrlongnight.photonjockey.hue.bridge.HueStateObserver;
import io.github.mrlongnight.photonjockey.hue.bridge.PJHueManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.List;

/**
 * Main application for AudioAnalyzerDashboard.
 * Integrates real audio analysis with Philips Hue light control.
 * Provides comprehensive visualization and control of audio-to-light synchronization.
 */
public class AudioAnalyzerDashboard extends Application implements BeatObserver, HueStateObserver {

    private static final Logger logger = LoggerFactory.getLogger(AudioAnalyzerDashboard.class);

    private AudioAnalyzerDashboardController controller;
    private AppTaskOrchestrator taskOrchestrator;
    private Config config;
    private PJAudioReader audioReader;
    private HueManager hueManager;
    private boolean running = true;
    private FFTProcessor fftProcessor;

    @Override
    public void start(Stage primaryStage) throws Exception {
        logger.info("Starting AudioAnalyzerDashboard application");

        // Initialize configuration
        config = new PJConfig();
        
        // Initialize task orchestrator
        taskOrchestrator = new AppTaskOrchestrator();

        // Initialize audio reader
        audioReader = new PJAudioReader(config, taskOrchestrator);
        audioReader.registerBeatObserver(this);

        // Initialize FFT processor for spectrum analysis
        fftProcessor = new FFTProcessor(2048, WindowFunction.HANN, 0.5);

        // Initialize Hue manager
        hueManager = new PJHueManager(config, taskOrchestrator);
        hueManager.setStateObserver(this);

        // Load UI
        URL fxmlUrl = getClass().getResource("/fxml/AudioAnalyzerDashboard.fxml");
        if (fxmlUrl == null) {
            logger.error("Could not find FXML file");
            showError("Resource Error", "Could not load UI definition file");
            return;
        }

        FXMLLoader loader = new FXMLLoader(fxmlUrl);
        Parent root = loader.load();
        controller = loader.getController();

        Scene scene = new Scene(root, 1000, 700);
        primaryStage.setTitle("PhotonJockey - Audio Analyzer Dashboard");
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(e -> {
            e.consume();
            shutdown();
        });
        primaryStage.show();

        // Auto-start audio monitoring
        startAudioMonitoring();

        logger.info("AudioAnalyzerDashboard started successfully");
    }

    /**
     * Starts audio monitoring from the default or configured device.
     */
    private void startAudioMonitoring() {
        taskOrchestrator.dispatch(() -> {
            try {
                List<AudioDevice> devices = audioReader.getSupportedDevices();
                if (devices.isEmpty()) {
                    logger.warn("No audio devices found");
                    Platform.runLater(() -> 
                        showWarning("No Audio Devices", "No audio capture devices found on this system")
                    );
                    return;
                }

                // Use the first available device
                AudioDevice device = devices.get(0);
                logger.info("Starting audio monitoring on device: {}", device.getName());
                
                boolean started = audioReader.start(device);
                if (!started) {
                    logger.error("Failed to start audio device");
                    Platform.runLater(() -> 
                        showError("Audio Error", "Failed to start audio capture device")
                    );
                }
            } catch (Exception e) {
                logger.error("Error starting audio monitoring", e);
                Platform.runLater(() -> 
                    showError("Audio Error", "Error starting audio monitoring: " + e.getMessage())
                );
            }
        });
    }

    /**
     * Processes audio frame data and updates the UI.
     * Note: Currently audio frames are processed internally by PJAudioReader.
     * This method would be used if we need additional processing.
     */
    private void processAudioFrame(AudioFrame frame) {
        if (frame == null || controller == null) {
            return;
        }

        // Update waveform visualization
        controller.updateWaveform(frame);

        // Analyze spectrum using FFT
        try {
            // Convert byte data to double samples
            byte[] data = frame.getData();
            int sampleCount = Math.min(data.length / 2, 2048);
            double[] samples = new double[sampleCount];
            
            for (int i = 0; i < sampleCount && i * 2 < data.length; i++) {
                short sample = (short) ((data[i * 2 + 1] << 8) | (data[i * 2] & 0xFF));
                samples[i] = sample / 32768.0;
            }
            
            double[] spectrum = fftProcessor.computeSpectrum(samples);
            controller.updateSpectrum(spectrum);
        } catch (Exception e) {
            logger.debug("Error analyzing spectrum: {}", e.getMessage());
        }
    }

    // BeatObserver implementation

    @Override
    public void beatReceived(BeatEvent beatEvent) {
        if (controller != null) {
            // BeatEvent doesn't have BPM directly, use amplitude to estimate activity
            double amplitude = beatEvent.triggeringAmplitude();
            // Estimate BPM from beat events (this is a simplified approach)
            double estimatedBpm = 120.0; // Default/placeholder
            controller.updateBeatIndicator(true, estimatedBpm);

            // Reset beat indicator after a short delay
            taskOrchestrator.schedule(() -> {
                controller.updateBeatIndicator(false, estimatedBpm);
            }, 100, java.util.concurrent.TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public void noBeatReceived() {
        // Called when no beat is detected in a frame
        // The beat indicator will be reset by the beatReceived method
    }

    @Override
    public void silenceDetected() {
        if (controller != null) {
            controller.updateBeatIndicator(false, 0.0);
        }
    }

    @Override
    public void audioReaderStopped(StopStatus status) {
        logger.info("Audio reader stopped: {}", status);
        Platform.runLater(() -> {
            if (controller != null) {
                controller.clear();
            }
            if (status == StopStatus.ERROR) {
                showWarning("Audio Stopped", "Audio monitoring stopped due to an error");
            }
        });
    }

    // HueStateObserver implementation

    @Override
    public void isScanningForBridges() {
        logger.info("Scanning for Hue bridges...");
    }

    @Override
    public void displayFoundBridges(List<AccessPoint> accessPoints) {
        logger.info("Found {} Hue bridge(s)", accessPoints.size());
        for (AccessPoint ap : accessPoints) {
            logger.info("  Bridge at {}: {}", ap.ip(), ap.name());
        }
    }

    @Override
    public void isAttemptingConnection() {
        logger.info("Attempting to connect to Hue bridge...");
    }

    @Override
    public void hasConnected() {
        logger.info("Successfully connected to Hue bridge");
        Platform.runLater(() -> 
            showInfo("Hue Connected", "Successfully connected to Philips Hue bridge")
        );
    }

    @Override
    public void requestPushlink() {
        logger.info("Please press the link button on your Hue bridge");
        Platform.runLater(() -> 
            showInfo("Pushlink Required", "Please press the link button on your Hue bridge to authenticate")
        );
    }

    @Override
    public void pushlinkHasFailed() {
        logger.warn("Pushlink authentication failed");
        Platform.runLater(() -> 
            showWarning("Authentication Failed", "Failed to authenticate with Hue bridge. Please try again.")
        );
    }

    @Override
    public void connectionWasLost(AccessPoint accessPoint, BridgeConnection.ConnectionListener.Error error) {
        logger.warn("Connection to Hue bridge lost: {}", error);
        Platform.runLater(() -> 
            showWarning("Connection Lost", "Lost connection to Hue bridge: " + error)
        );
    }

    @Override
    public void disconnected() {
        logger.info("Disconnected from Hue bridge");
    }

    /**
     * Shuts down the application gracefully.
     */
    private void shutdown() {
        logger.info("Shutting down AudioAnalyzerDashboard");

        running = false;

        // Stop audio reader
        if (audioReader != null && audioReader.isOpen()) {
            audioReader.stop();
        }

        // Disconnect from Hue
        if (hueManager != null) {
            hueManager.disconnect();
        }

        // Shutdown task orchestrator
        if (taskOrchestrator != null) {
            taskOrchestrator.shutdown();
        }

        // Close application
        Platform.exit();
        System.exit(0);
    }

    /**
     * Shows an error dialog.
     */
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Shows a warning dialog.
     */
    private void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Shows an information dialog.
     */
    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
