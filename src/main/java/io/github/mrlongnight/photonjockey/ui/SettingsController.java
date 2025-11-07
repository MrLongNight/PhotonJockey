package io.github.mrlongnight.photonjockey.ui;

import io.github.mrlongnight.photonjockey.config.Config;
import io.github.mrlongnight.photonjockey.config.ConfigNode;
import io.github.mrlongnight.photonjockey.ui.controls.ControlStyle;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Objects;

public class SettingsController {

    private static final Logger logger = LoggerFactory.getLogger(SettingsController.class);

    @FXML
    private ComboBox<String> themeComboBox;
    @FXML
    private ComboBox<String> controlStyleComboBox;
    @FXML
    private TextField logPathTextField;
    @FXML
    private Button browseButton;
    @FXML
    private ComboBox<String> consoleLogLevelComboBox;
    @FXML
    private ComboBox<String> fileLogLevelComboBox;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;

    private Stage dialogStage;
    private Config config;

    @FXML
    public void initialize() {
        // Populate combo boxes
        themeComboBox.getItems().addAll("Automatic", "Dark", "Light");
        controlStyleComboBox.getItems().addAll("Slider", "Knob", "Display");
        consoleLogLevelComboBox.getItems().addAll("INFO", "ERROR", "DEBUG");
        fileLogLevelComboBox.getItems().addAll("INFO", "ERROR", "DEBUG");

        // Add listeners
        browseButton.setOnAction(event -> handleBrowse());
    }

    public void initData(Config config) {
        this.config = config;
        loadSettings();
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    private void loadSettings() {
        themeComboBox.setValue(config.get(ConfigNode.THEME));
        
        // Load control style
        String controlStyle = config.get(ConfigNode.UI_CONTROL_STYLE);
        if (controlStyle == null || controlStyle.isEmpty()) {
            controlStyle = "Slider";
        }
        controlStyleComboBox.setValue(controlStyle);
        
        logPathTextField.setText(config.get(ConfigNode.LOG_PATH));
        consoleLogLevelComboBox.setValue(config.get(ConfigNode.CONSOLE_LOG_LEVEL));
        fileLogLevelComboBox.setValue(config.get(ConfigNode.FILE_LOG_LEVEL));
    }

    @FXML
    private void handleSave() {
        // Save current values
        String oldConsoleLevel = config.get(ConfigNode.CONSOLE_LOG_LEVEL);
        String oldFileLevel = config.get(ConfigNode.FILE_LOG_LEVEL);
        String oldLogPath = config.get(ConfigNode.LOG_PATH);
        
        config.put(ConfigNode.THEME, themeComboBox.getValue());
        config.put(ConfigNode.UI_CONTROL_STYLE, controlStyleComboBox.getValue());
        config.put(ConfigNode.LOG_PATH, logPathTextField.getText());
        config.put(ConfigNode.CONSOLE_LOG_LEVEL, consoleLogLevelComboBox.getValue());
        config.put(ConfigNode.FILE_LOG_LEVEL, fileLogLevelComboBox.getValue());

        // Check if logging settings changed using null-safe comparisons
        String newConsoleLevel = consoleLogLevelComboBox.getValue();
        String newFileLevel = fileLogLevelComboBox.getValue();
        String newLogPath = logPathTextField.getText();
        
        boolean loggingChanged = !Objects.equals(newConsoleLevel, oldConsoleLevel) ||
                                 !Objects.equals(newFileLevel, oldFileLevel) ||
                                 !Objects.equals(newLogPath, oldLogPath);
        
        if (loggingChanged) {
            logger.info("Logging settings changed. Application restart required for changes to take effect.");
            showRestartWarning();
        }

        logger.info("Settings saved.");
        dialogStage.close();
    }
    
    private void showRestartWarning() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Restart Required");
        alert.setHeaderText("Log settings changed");
        alert.setContentText("Please restart PhotonJockey for the new log settings to take effect.");
        alert.showAndWait();
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    private void handleBrowse() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Log File Path");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        File file = fileChooser.showSaveDialog(dialogStage);
        if (file != null) {
            logPathTextField.setText(file.getAbsolutePath());
        }
    }
}
