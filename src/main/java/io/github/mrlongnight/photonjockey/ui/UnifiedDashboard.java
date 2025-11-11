package io.github.mrlongnight.photonjockey.ui;

import com.pixelduke.window.ThemeWindowManager;
import com.pixelduke.window.ThemeWindowManagerFactory;
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
    private ThemeWindowManager themeWindowManager;

    public static void init(Config config, AppTaskOrchestrator taskOrchestrator,
                           PJAudioReader audioReader, PJHueManager hueManager) {
        staticConfig = config;
        staticTaskOrchestrator = taskOrchestrator;
        staticAudioReader = audioReader;
        staticHueManager = hueManager;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Set up uncaught exception handler for JavaFX thread
        Thread.currentThread().setUncaughtExceptionHandler((thread, throwable) -> {
            logger.error("UNCAUGHT EXCEPTION on JavaFX Application Thread", throwable);
        });
        
        try {
            logger.info("Starting Unified Dashboard application");

            // Initialize from static variables
            config = staticConfig;
            taskOrchestrator = staticTaskOrchestrator;
            audioReader = staticAudioReader;
            hueManager = staticHueManager;
            
            // Initialize FXThemes window manager for dark mode title bar support
            themeWindowManager = ThemeWindowManagerFactory.create();
            logger.info("ThemeWindowManager initialized");

            // Detect Windows theme preference
            String themePreference = WindowsThemeDetector.getThemeDescription();
            logger.info("Windows theme preference: {}", themePreference);

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

            logger.info("Creating scene...");
            Scene scene = new Scene(root, 1100, 750);
            logger.info("Scene created with size: 1100x750");
            
            // Configure primary stage
            primaryStage.setTitle("PhotonJockey - Audio & Light Controller");
            logger.info("Stage title set");
            
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
            
            logger.info("Configuring stage for display...");
            // Configure primary stage for proper display
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(900);
            primaryStage.setMinHeight(600);
            
            // Ensure window is visible, not minimized, and has normal z-order
            primaryStage.setIconified(false);
            primaryStage.setAlwaysOnTop(false);
            
            // Set up close request handler
            primaryStage.setOnCloseRequest(e -> {
                e.consume();
                shutdown();
            });
            logger.info("Stage configured");

            logger.info("Applying scene theme...");
            // Ensure config is properly initialized before accessing theme
            if (config == null) {
                logger.error("Config is null, cannot apply theme");
                throw new IllegalStateException("Configuration not initialized");
            }
            String theme = config.get(ConfigNode.THEME);
            // If theme is still null despite default, fall back to "Automatic"
            if (theme == null) {
                logger.warn("Theme config returned null, defaulting to 'Automatic'");
                theme = "Automatic";
            }
            boolean isDarkMode = shouldUseDarkMode(theme);
            applySceneTheme(scene, isDarkMode);
            logger.info("Scene theme applied");
            
            logger.info("Showing stage...");
            primaryStage.show();
            logger.info("Stage.show() completed");
            
            // Apply window frame theme AFTER stage.show() to avoid NullPointerException
            // The native window handle is only available after the stage is shown
            logger.info("Applying window frame theme...");
            applyWindowFrameTheme(primaryStage, isDarkMode);
            logger.info("Window frame theme applied");
            
            // Center window on screen to ensure it's visible
            primaryStage.centerOnScreen();
            
            // Ensure window is visible and has focus
            primaryStage.toFront();
            primaryStage.requestFocus();
            
            // Log window state for debugging
            logger.info("Window state - showing: {}, iconified: {}, focused: {}", primaryStage.isShowing(), primaryStage.isIconified(), primaryStage.isFocused());
            logger.info("Window size: {}x{}", primaryStage.getWidth(), primaryStage.getHeight());
            logger.info("Window position: ({}, {})", primaryStage.getX(), primaryStage.getY());

            logger.info("Unified Dashboard window displayed successfully");
            
            // Initialize controllers AFTER the window is shown to avoid blocking the UI thread
            // This prevents heavy initialization (audio devices, network I/O) from blocking the window display
            // CRITICAL: Use taskOrchestrator.dispatch() instead of Platform.runLater() because
            // Platform.runLater() runs on the JavaFX Application Thread which will freeze the UI
            // during heavy operations like audio device enumeration and network I/O
            logger.info("Initializing controllers in background...");
            taskOrchestrator.dispatch(() -> {
                try {
                    logger.info("Starting controller initialization...");
                    initializeControllers();
                    logger.info("Controllers initialized successfully");
                    
                    // Attempt auto-connect to Hue bridge after controllers are initialized
                    // This runs in a background thread to avoid blocking the UI
                    logger.info("Attempting Hue bridge auto-connect in background...");
                    if (hueManager != null) {
                        hueManager.attemptAutoConnect();
                    }
                } catch (Exception e) {
                    logger.error("Failed to initialize controllers", e);
                }
            });

            logger.info("Unified Dashboard started successfully");
        } catch (Exception e) {
            logger.error("FATAL: Failed to start Unified Dashboard", e);
            throw e;
        }
    }

    private void applyTheme(Scene scene, Stage stage) {
        String theme = config.get(ConfigNode.THEME);
        logger.info("Applying theme: {}", theme);

        // Determine if dark mode should be enabled
        boolean isDarkMode = shouldUseDarkMode(theme);
        
        // Apply theme to scene
        applySceneTheme(scene, isDarkMode);
        
        // Apply theme to window frame using FXThemes
        applyWindowFrameTheme(stage, isDarkMode);
    }
    
    private boolean shouldUseDarkMode(String theme) {
        // Handle null theme
        if (theme == null) {
            logger.warn("Theme is null in shouldUseDarkMode, defaulting to dark mode");
            return true;
        }
        
        if ("Light".equals(theme)) {
            return false;
        } else if ("Dark".equals(theme)) {
            return true;
        } else if ("Automatic".equals(theme)) {
            return WindowsThemeDetector.isDarkModeEnabled();
        }
        // Default to dark mode for unknown themes
        logger.warn("Unknown theme '{}', defaulting to dark mode", theme);
        return true;
    }
    
    private void applySceneTheme(Scene scene, boolean isDarkMode) {
        scene.getRoot().getStyleClass().remove("light-theme");
        if (!isDarkMode) {
            scene.getRoot().getStyleClass().add("light-theme");
        }
    }
    
    private void applyWindowFrameTheme(Stage stage, boolean isDarkMode) {
        if (themeWindowManager != null) {
            try {
                themeWindowManager.setDarkModeForWindowFrame(stage, isDarkMode);
                logger.info("Window frame theme set to: {}", isDarkMode ? "Dark" : "Light");
            } catch (Exception e) {
                logger.warn("Failed to set window frame theme", e);
            }
        } else {
            logger.warn("ThemeWindowManager is not available - window frame theme cannot be set");
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
