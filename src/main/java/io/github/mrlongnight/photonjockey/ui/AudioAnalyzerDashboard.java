package io.github.mrlongnight.photonjockey.ui;

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
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Controller for the AudioAnalyzerDashboard.
 * Integrates real audio analysis with Philips Hue light control.
 * Provides comprehensive visualization and control of audio-to-light synchronization.
 */
public class AudioAnalyzerDashboard implements BeatObserver {

    private static final Logger logger = LoggerFactory.getLogger(AudioAnalyzerDashboard.class);
    private static final int FFT_SIZE = 2048;
    private static final int BPM_HISTORY_SIZE = 20;
    private static final long UI_UPDATE_THROTTLE_MS = 33; // ~30 FPS to avoid overwhelming UI

    private AudioAnalyzerDashboardController controller;
    private AppTaskOrchestrator taskOrchestrator;
    private Config config;
    private PJAudioReader audioReader;
    private FFTProcessor fftProcessor;
    private BPMDetector bpmDetector;
    private final AtomicBoolean visualizationsEnabled = new AtomicBoolean(true);
    private volatile long lastUiUpdateTime = 0;

    public void initialize(Config config, AppTaskOrchestrator taskOrchestrator, PJAudioReader audioReader, AudioAnalyzerDashboardController controller) {
        logger.info("Initializing AudioAnalyzerDashboard");

        this.config = config;
        this.taskOrchestrator = taskOrchestrator;
        this.audioReader = audioReader;
        this.controller = controller;
        this.controller.setConfig(config);

        this.audioReader.registerBeatObserver(this);

        // Initialize FFT processor for spectrum analysis
        this.fftProcessor = new FFTProcessor(FFT_SIZE, WindowFunction.HANN, 0.5);

        // Initialize BPM detector
        this.bpmDetector = new BPMDetector(BPM_HISTORY_SIZE);

        // Set up callbacks for UI actions
        this.controller.setCallbacks(
            this::refreshAudioDevices,
            this::onAudioDeviceSelected,
            this::updateConfigFromUi,
            visualizationsEnabled::set
        );

        // Load config and set UI elements
        loadConfigToUi();

        // Auto-start audio monitoring
        // Note: This will also populate the device list in the UI when devices are scanned
        startAudioMonitoring();

        logger.info("AudioAnalyzerDashboard initialized successfully");
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
                
                // Update UI with available devices
                List<String> deviceNames = devices.stream()
                    .map(AudioDevice::getName)
                    .collect(java.util.stream.Collectors.toList());
                controller.updateAudioDevices(deviceNames, null);
                controller.updateInfo("Found " + deviceNames.size() + " audio device(s)");
                
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
        if (controller == null) {
            return;
        }
        
        // Throttle UI updates to avoid overwhelming the JavaFX thread
        long currentTime = System.currentTimeMillis();
        long timeSinceLastUpdate = currentTime - lastUiUpdateTime;
        
        if (timeSinceLastUpdate < UI_UPDATE_THROTTLE_MS) {
            // Skip this update if too soon since last update
            return;
        }
        
        lastUiUpdateTime = currentTime;
        
        // Update level indicator (lightweight, always update)
        double normalizedLevel = (audioFrame.getLevelDB() + 80.0) / 80.0;
        controller.updateLevel(Math.max(0.0, normalizedLevel));

        // Only perform heavy visualizations if enabled
        if (visualizationsEnabled.get()) {
            // Process FFT on background thread to avoid blocking
            taskOrchestrator.dispatch(() -> {
                try {
                    // Update waveform
                    controller.updateWaveform(audioFrame);

                    // Perform FFT for spectrum analysis (CPU intensive)
                    double[] samples = audioFrame.toNormalizedSamples();
                    double[] spectrum = fftProcessor.computeSpectrum(samples);
                    
                    // Update all spectrum visualizations
                    controller.updateSpectrum(spectrum);
                    controller.updateLowFreqSpectrum(audioFrame.getLowFreqData());
                    controller.updateMidFreqSpectrum(audioFrame.getMidFreqData());
                    controller.updateHighFreqSpectrum(audioFrame.getHighFreqData());
                } catch (Exception e) {
                    logger.error("Error processing audio visualization", e);
                }
            });
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

    private void loadConfigToUi() {
        if (controller == null) {
            return;
        }

        controller.setBeatSensitivity((double) config.getInt(ConfigNode.BEAT_SENSITIVITY));
        controller.setMinBeatInterval((double) config.getInt(ConfigNode.BEAT_MIN_TIME_BETWEEN));
        controller.setBassOnlyMode(config.getBoolean(ConfigNode.BEAT_BASS_ONLY_MODE));
        controller.setLowFreq(config.getInt(ConfigNode.LOW_FREQ));
        controller.setMidFreq(config.getInt(ConfigNode.MID_FREQ));
        controller.setHighFreq(config.getInt(ConfigNode.HIGH_FREQ));
    }

    private void updateConfigFromUi() {
        if (controller == null || config == null) {
            return;
        }

        config.putInt(ConfigNode.BEAT_SENSITIVITY, (int) controller.getBeatSensitivity());
        config.putInt(ConfigNode.BEAT_MIN_TIME_BETWEEN, (int) controller.getMinBeatInterval());
        config.putBoolean(ConfigNode.BEAT_BASS_ONLY_MODE, controller.isBassOnlyModeEnabled());
        config.putInt(ConfigNode.LOW_FREQ, (int) controller.getLowFreq());
        config.putInt(ConfigNode.MID_FREQ, (int) controller.getMidFreq());
        config.putInt(ConfigNode.HIGH_FREQ, (int) controller.getHighFreq());
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