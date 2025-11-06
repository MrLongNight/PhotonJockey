package io.github.mrlongnight.photonjockey.ui;

import io.github.mrlongnight.photonjockey.ui.util.TabDragHelper;
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

    @FXML
    private Tab smartMappingTab;

    private AudioAnalyzerDashboardController audioAnalyzerController;
    private LightControllerDashboardController lightControllerController;
    private SmartMappingToolController smartMappingController;

    @FXML
    public void initialize() {
        loadAudioAnalyzerTab();
        loadLightControllerTab();
        loadSmartMappingTab();
        
        // Enable drag-and-drop for tab reordering
        TabDragHelper.enableTabDragAndDrop(mainTabPane);
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

    private void loadSmartMappingTab() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/SmartMappingTool.fxml")
            );
            Parent content = loader.load();
            smartMappingController = loader.getController();
            smartMappingTab.setContent(content);
            logger.info("Smart Mapping Tool tab loaded successfully");
        } catch (IOException e) {
            logger.error("Failed to load Smart Mapping Tool tab", e);
        }
    }

    public SmartMappingToolController getSmartMappingController() {
        return smartMappingController;
    }
}