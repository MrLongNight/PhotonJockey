package io.github.mrlongnight.photonjockey.ui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import io.github.mrlongnight.photonjockey.AppTaskOrchestrator;
import io.github.mrlongnight.photonjockey.audio.AudioFrame;
import io.github.mrlongnight.photonjockey.audio.BeatEvent;
import io.github.mrlongnight.photonjockey.audio.BPMDetector;
import io.github.mrlongnight.photonjockey.audio.BeatObserver;
import io.github.mrlongnight.photonjockey.audio.FFTProcessor;
import io.github.mrlongnight.photonjockey.audio.PJAudioReader;
import io.github.mrlongnight.photonjockey.audio.WindowFunction;
import io.github.mrlongnight.photonjockey.audio.device.AudioDevice;
import io.github.mrlongnight.photonjockey.config.Config;
import io.github.mrlongnight.photonjockey.config.ConfigNode;
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
public class AudioAnalyzerDashboard extends Application implements BeatObserver {

    private static final Logger logger = LoggerFactory.getLogger(AudioAnalyzerDashboard.class);
    private static final int FFT_SIZE = 2048;
    private static final int BPM_HISTORY_SIZE = 20;

    private static AppTaskOrchestrator staticTaskOrchestrator;
    private static Config staticConfig;
    private static PJAudioReader staticAudioReader;

    private AudioAnalyzerDashboardController controller;
    private AppTaskOrchestrator taskOrchestrator;
    private Config config;
    private PJAudioReader audioReader;
    private FFTProcessor fftProcessor;
    private BPMDetector bpmDetector;
    private final java.util.concurrent.atomic.AtomicBoolean visualizationsEnabled = new java.util.concurrent.atomic.AtomicBoolean(true);

    public static void init(Config config, AppTaskOrchestrator taskOrchestrator, PJAudioReader audioReader) {
        staticConfig = config;
        staticTaskOrchestrator = taskOrchestrator;
        staticAudioReader = audioReader;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        logger.info("Starting AudioAnalyzerDashboard application");

        // Initialize configuration
        config = staticConfig;
        
        // Initialize task orchestrator
        taskOrchestrator = staticTaskOrchestrator;

        // Initialize audio reader
        audioReader = staticAudioReader;
        audioReader.registerBeatObserver(this);

        // Initialize FFT processor for spectrum analysis
        fftProcessor = new FFTProcessor(FFT_SIZE, WindowFunction.HANN, 0.5);

        // Initialize BPM detector
        bpmDetector = new BPMDetector(BPM_HISTORY_SIZE);

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

        // Set up callbacks for UI actions
        controller.setCallbacks(
            this::refreshAudioDevices,
            this::onAudioDeviceSelected,
            this::updateConfigFromUi,
            visualizationsEnabled::set
        );

        Scene scene = new Scene(root, 1000, 700);
        primaryStage.setTitle("PhotonJockey - Audio Analyzer Dashboard");
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(e -> {
            e.consume();
            shutdown();
        });
        primaryStage.show();

        // Initialize UI with available devices
        refreshAudioDevices();

        // Load config and set UI elements
        loadConfigToUi();

        // Auto-start audio monitoring
        startAudioMonitoring();

        logger.info("AudioAnalyzerDashboard started successfully");
    }

    /**
     * Refreshes the list of available audio devices.
     */
    private void refreshAudioDevices() {
        taskOrchestrator.dispatch(() -> {
            try {
                List<AudioDevice> devices = audioReader.getSupportedDevices();
                List<String> deviceNames = devices.stream()
                    .map(AudioDevice::getName)
                    .collect(java.util.stream.Collectors.toList());
                
                String currentDevice = audioReader.isOpen() ? 
                    devices.stream()
                        .filter(d -> d.isOpen())
                        .map(AudioDevice::getName)
                        .findFirst().orElse(null) : null;
                
                controller.updateAudioDevices(deviceNames, currentDevice);
                controller.updateInfo("Found " + deviceNames.size() + " audio device(s)");
                
                if (deviceNames.isEmpty()) {
                    logger.warn("No audio devices found");
                    Platform.runLater(() -> 
                        showWarning("No Audio Devices", "No audio capture devices found on this system")
                    );
                }
            } catch (Exception e) {
                logger.error("Error refreshing audio devices", e);
                Platform.runLater(() -> 
                    showError("Device Error", "Error refreshing audio devices: " + e.getMessage())
                );
            }
        });
    }

    private void onAudioDeviceSelected(String deviceName) {
        if (audioReader.isOpen()) {
            audioReader.stop();
        }
        taskOrchestrator.dispatch(() -> {
            try {
                logger.info("Audio device selected: {}", deviceName);

                AudioDevice selectedDevice = audioReader.getSupportedDevices().stream()
                    .filter(d -> d.getName().equals(deviceName))
                    .findFirst()
                    .orElse(null);

                if (selectedDevice != null) {
                    startAudioMonitoring(selectedDevice);
                } else {
                    logger.error("Selected audio device not found: {}", deviceName);
                    Platform.runLater(() ->
                        showError("Audio Error", "Selected audio device not found.")
                    );
                }
            } catch (Exception e) {
                logger.error("Error switching audio device", e);
                Platform.runLater(() ->
                    showError("Audio Error", "Error switching audio device: " + e.getMessage())
                );
            }
        });
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
                startAudioMonitoring(devices.get(0));
            } catch (Exception e) {
                logger.error("Error starting audio monitoring", e);
                Platform.runLater(() -> 
                    showError("Audio Error", "Error starting audio monitoring: " + e.getMessage())
                );
            }
        });
    }

    private void startAudioMonitoring(AudioDevice device) {
        if (device == null) {
            logger.error("Cannot start monitoring on a null device.");
            return;
        }

        taskOrchestrator.dispatch(() -> {
            try {
                logger.info("Starting audio monitoring on device: {}", device.getName());
                
                boolean started = audioReader.start(device);
                if (started) {
                    controller.updateStatus("Monitoring: " + device.getName());
                    controller.updateInfo("Audio capture active");
                } else {
                    logger.error("Failed to start audio device: {}", device.getName());
                    Platform.runLater(() ->
                        showError("Audio Error", "Failed to start audio capture on " + device.getName())
                    );
                }
            } catch (Exception e) {
                logger.error("Error starting audio monitoring on device: {}", device.getName(), e);
                Platform.runLater(() -> 
                    showError("Audio Error", "Error starting audio monitoring: " + e.getMessage())
                );
            }
        });
    }

    // BeatObserver implementation

    @Override
    public void audioReceived(AudioFrame audioFrame) {
        if (controller != null) {
            double[] samples = audioFrame.toNormalizedSamples();
            double rms = 0.0;
            for (double sample : samples) {
                rms += sample * sample;
            }
            rms = Math.sqrt(rms / samples.length);
            controller.updateLevel(rms);

            if (visualizationsEnabled.get()) {
                controller.updateWaveform(audioFrame);

                // Perform FFT for spectrum analysis
                double[] spectrum = fftProcessor.computeSpectrum(samples);
                controller.updateSpectrum(spectrum);
            }
        }
    }

    @Override
    public void beatReceived(BeatEvent beatEvent) {
        if (controller != null) {
            bpmDetector.setBpmRange(controller.getMinBpm(), controller.getMaxBpm());
            bpmDetector.recordBeat();
            double bpm = bpmDetector.getBpm();

            controller.updateBeatIndicator(true, bpm);

            // Reset beat indicator after a short delay
            taskOrchestrator.schedule(() -> {
                controller.updateBeatIndicator(false, bpm);
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

    /**
     * Shuts down the application gracefully.
     */
    private void shutdown() {
        logger.info("Shutting down AudioAnalyzerDashboard");

        // Stop audio reader
        if (audioReader != null && audioReader.isOpen()) {
            audioReader.stop();
        }

        // Shutdown task orchestrator
        if (taskOrchestrator != null) {
            taskOrchestrator.shutdown();
        }

        bpmDetector = null;
        fftProcessor = null;

        // Close application
        Platform.exit();
    }

    private void loadConfigToUi() {
        if (controller == null) {
            return;
        }

        controller.setBeatSensitivity((double) config.getInt(ConfigNode.BEAT_SENSITIVITY));
        controller.setMinBeatInterval((double) config.getInt(ConfigNode.BEAT_MIN_TIME_BETWEEN));
        controller.setBassOnlyMode(config.getBoolean(ConfigNode.BEAT_BASS_ONLY_MODE));
    }

    private void updateConfigFromUi() {
        if (controller == null || config == null) {
            return;
        }

        config.putInt(ConfigNode.BEAT_SENSITIVITY, (int) controller.getBeatSensitivity());
        config.putInt(ConfigNode.BEAT_MIN_TIME_BETWEEN, (int) controller.getMinBeatInterval());
        config.putBoolean(ConfigNode.BEAT_BASS_ONLY_MODE, controller.isBassOnlyModeEnabled());
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
}
