package io.github.mrlongnight.photonjockey.ui;

import io.github.mrlongnight.photonjockey.config.Config;
import io.github.mrlongnight.photonjockey.config.ConfigNode;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class SettingsController {

    private static final Logger logger = LoggerFactory.getLogger(SettingsController.class);

    @FXML
    private ComboBox<String> themeComboBox;
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
        logPathTextField.setText(config.get(ConfigNode.LOG_PATH));
        consoleLogLevelComboBox.setValue(config.get(ConfigNode.CONSOLE_LOG_LEVEL));
        fileLogLevelComboBox.setValue(config.get(ConfigNode.FILE_LOG_LEVEL));
    }

    @FXML
    private void handleSave() {
        config.put(ConfigNode.THEME, themeComboBox.getValue());
        config.put(ConfigNode.LOG_PATH, logPathTextField.getText());
        config.put(ConfigNode.CONSOLE_LOG_LEVEL, consoleLogLevelComboBox.getValue());
        config.put(ConfigNode.FILE_LOG_LEVEL, fileLogLevelComboBox.getValue());

        logger.info("Settings saved.");
        dialogStage.close();
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
