package io.github.mrlongnight.photonjockey.ui;

import io.github.mrlongnight.photonjockey.AppTaskOrchestrator;
import io.github.mrlongnight.photonjockey.audio.PJAudioReader;
import io.github.mrlongnight.photonjockey.config.Config;
import io.github.mrlongnight.photonjockey.config.ConfigNode;
import io.github.mrlongnight.photonjockey.hue.bridge.PJHueManager;
import io.github.mrlongnight.photonjockey.util.WindowsThemeDetector;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.net.URL;

/**
 * Unified dashboard application combining Audio Analyzer and Light Controller.
 */
public class UnifiedDashboard extends Application {

    private static final Logger logger = LoggerFactory.getLogger(UnifiedDashboard.class);

    private static Config staticConfig;
    private static AppTaskOrchestrator staticTaskOrchestrator;
    private static PJAudioReader staticAudioReader;
    private static PJHueManager staticHueManager;

    private UnifiedDashboardController controller;
    private AppTaskOrchestrator taskOrchestrator;
    private Config config;
    private PJAudioReader audioReader;
    private PJHueManager hueManager;

    public static void init(Config config, AppTaskOrchestrator taskOrchestrator,
                           PJAudioReader audioReader, PJHueManager hueManager) {
        staticConfig = config;
        staticTaskOrchestrator = taskOrchestrator;
        staticAudioReader = audioReader;
        staticHueManager = hueManager;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            logger.info("Starting Unified Dashboard application");

            // Initialize from static variables
            config = staticConfig;
            taskOrchestrator = staticTaskOrchestrator;
            audioReader = staticAudioReader;
            hueManager = staticHueManager;

            // Detect Windows theme preference
            String themePreference = WindowsThemeDetector.getThemeDescription();
            logger.info("Windows theme preference: {}", themePreference);
            logger.info("Note: JavaFX does not support Windows dark mode title bars natively");

            // Load main UI
            logger.info("Loading FXML...");
            URL fxmlUrl = getClass().getResource("/fxml/UnifiedDashboard.fxml");
            if (fxmlUrl == null) {
                logger.error("Could not find UnifiedDashboard.fxml");
                return;
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();
            controller = loader.getController();
            controller.initData(config);
            logger.info("FXML loaded and controller initialized");

            // Initialize controllers with dependencies
            logger.info("Initializing controllers...");
            initializeControllers();
            logger.info("Controllers initialized");

            logger.info("Creating scene...");
            Scene scene = new Scene(root, 1100, 750);
            logger.info("Scene created with size: 1100x750");
            
            // Configure primary stage
            primaryStage.setTitle("PhotonJockey - Audio & Light Controller");
            primaryStage.setMinWidth(900);
            primaryStage.setMinHeight(600);
            logger.info("Stage title and minimum size set");
            
            // Set application icon
            try {
                logger.info("Loading application icon...");
                InputStream iconStream = getClass().getResourceAsStream("/png/icon_64.png");
                if (iconStream != null) {
                    primaryStage.getIcons().add(new Image(iconStream));
                    logger.info("Application icon loaded successfully");
                } else {
                    logger.warn("Could not find application icon at /png/icon_64.png");
                }
            } catch (Exception e) {
                logger.warn("Failed to load application icon", e);
            }
            
            logger.info("Setting scene on stage...");
            primaryStage.setScene(scene);
            primaryStage.setOnCloseRequest(e -> {
                e.consume();
                shutdown();
            });
            logger.info("Scene set on stage");

            logger.info("Applying theme...");
            applyTheme(scene);
            logger.info("Theme applied");
            
            logger.info("Configuring stage for display...");
            // Ensure window is not minimized or hidden
            primaryStage.setIconified(false);
            // Request focus when shown
            primaryStage.setAlwaysOnTop(false);
            
            logger.info("Showing stage...");
            primaryStage.show();
            logger.info("Stage.show() called");
            
            // Request focus to ensure window is visible
            primaryStage.toFront();
            primaryStage.requestFocus();
            logger.info("Stage focus requested");

            logger.info("Unified Dashboard started successfully");
        } catch (Exception e) {
            logger.error("FATAL: Failed to start Unified Dashboard", e);
            throw e;
        }
    }

    private void applyTheme(Scene scene) {
        String theme = config.get(ConfigNode.THEME);
        logger.info("Applying theme: {}", theme);

        scene.getRoot().getStyleClass().remove("light-theme");

        if ("Light".equals(theme)) {
            scene.getRoot().getStyleClass().add("light-theme");
        } else if ("Automatic".equals(theme)) {
            if (!WindowsThemeDetector.isDarkModeEnabled()) {
                scene.getRoot().getStyleClass().add("light-theme");
            }
        }
    }

    private void initializeControllers() {
        // Initialize Audio Analyzer controller
        AudioAnalyzerDashboard audioAnalyzerDashboard = new AudioAnalyzerDashboard();
        AudioAnalyzerDashboardController audioController =
            controller.getAudioAnalyzerController();
        if (audioController != null) {
            audioAnalyzerDashboard.initialize(config, taskOrchestrator, audioReader, audioController);
        }

        // Initialize Light Controller controller
        LightControllerDashboardController lightController =
            controller.getLightControllerController();
        if (lightController != null) {
            lightController.initialize(config, taskOrchestrator, audioReader, hueManager);
        }
    }

    private void shutdown() {
        logger.info("Shutting down Unified Dashboard");

        if (audioReader != null && audioReader.isOpen()) {
            audioReader.stop();
        }

        if (taskOrchestrator != null) {
            taskOrchestrator.shutdown();
        }

        Platform.exit();
    }
    
    @Override
    public void stop() throws Exception {
        logger.info("Application stop() called");
        shutdown();
        super.stop();
    }

    // Callback methods for audio analyzer
    private void refreshAudioDevices() {
        // Implementation
    }

    private void onAudioDeviceSelected(String deviceName) {
        // Implementation
    }

    private void updateConfigFromUi() {
        // Implementation
    }

    private void onVisualizationsToggled(Boolean enabled) {
        // Implementation
    }
}
