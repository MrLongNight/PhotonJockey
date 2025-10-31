package io.github.mrlongnight.photonjockey.ui;

import io.github.mrlongnight.photonjockey.hue.dto.BridgeConfig;
import io.github.mrlongnight.photonjockey.hue.dto.LightConfig;
import io.github.mrlongnight.photonjockey.hue.dto.LightMapConfig;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

/**
 * Demo application for Smart Mapping Tool with sample data pre-loaded.
 * Provides a standalone application for testing the UI with realistic data.
 */
public class SmartMappingToolDemoWithData extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        URL fxmlUrl = getClass().getResource("/fxml/SmartMappingTool.fxml");
        FXMLLoader loader = new FXMLLoader(fxmlUrl);
        Parent root = loader.load();
        
        SmartMappingToolController controller = loader.getController();
        
        // Load sample data after a short delay to ensure UI is ready
        Platform.runLater(() -> {
            LightMapConfig sampleConfig = createSampleConfiguration();
            controller.setConfig(sampleConfig);
        });

        Scene scene = new Scene(root, 1000, 700);
        primaryStage.setTitle("PhotonJockey - Smart Mapping Tool (Demo with Data)");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Creates a sample configuration with bridges and lights for demonstration.
     */
    private LightMapConfig createSampleConfiguration() {
        LightMapConfig config = new LightMapConfig();
        
        // Add sample bridges
        config.getBridges().add(new BridgeConfig("bridge-1", "192.168.1.100", "user123"));
        config.getBridges().add(new BridgeConfig("bridge-2", "192.168.1.101", "user456"));
        
        // Add sample lights in different positions
        config.getLights().add(new LightConfig(
            "light-1", 150.0, 150.0, "bridge-1", "FAST_UDP", "Living Room Left"
        ));
        config.getLights().add(new LightConfig(
            "light-2", 350.0, 150.0, "bridge-1", "FAST_UDP", "Living Room Right"
        ));
        config.getLights().add(new LightConfig(
            "light-3", 150.0, 350.0, "bridge-1", "LOW_HTTP", "Bedroom"
        ));
        config.getLights().add(new LightConfig(
            "light-4", 350.0, 350.0, "bridge-2", "FAST_UDP", "Kitchen"
        ));
        config.getLights().add(new LightConfig(
            "light-5", 550.0, 250.0, "bridge-2", "LOW_HTTP", "Hallway"
        ));
        config.getLights().add(new LightConfig(
            "light-6", 750.0, 200.0, "bridge-2", "FAST_UDP", "Bathroom"
        ));
        
        return config;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
