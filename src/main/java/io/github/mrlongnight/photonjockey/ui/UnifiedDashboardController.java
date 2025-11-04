package io.github.mrlongnight.photonjockey.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Controller for the unified dashboard with tabs.
 */
public class UnifiedDashboardController {

    private static final Logger logger = LoggerFactory.getLogger(UnifiedDashboardController.class);

    @FXML
    private TabPane mainTabPane;

    @FXML
    private Tab audioAnalyzerTab;

    @FXML
    private Tab lightControllerTab;

    private AudioAnalyzerDashboardController audioAnalyzerController;
    private LightControllerDashboardController lightControllerController;

    @FXML
    public void initialize() {
        loadAudioAnalyzerTab();
        loadLightControllerTab();
    }

    private void loadAudioAnalyzerTab() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/AudioAnalyzerDashboard.fxml")
            );
            Parent content = loader.load();
            audioAnalyzerController = loader.getController();
            audioAnalyzerTab.setContent(content);
            logger.info("Audio Analyzer tab loaded successfully");
        } catch (IOException e) {
            logger.error("Failed to load Audio Analyzer tab", e);
        }
    }

    private void loadLightControllerTab() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/LightControllerDashboard.fxml")
            );
            Parent content = loader.load();
            lightControllerController = loader.getController();
            lightControllerTab.setContent(content);
            logger.info("Light Controller tab loaded successfully");
        } catch (IOException e) {
            logger.error("Failed to load Light Controller tab", e);
        }
    }

    public AudioAnalyzerDashboardController getAudioAnalyzerController() {
        return audioAnalyzerController;
    }

    public LightControllerDashboardController getLightControllerController() {
        return lightControllerController;
    }
}