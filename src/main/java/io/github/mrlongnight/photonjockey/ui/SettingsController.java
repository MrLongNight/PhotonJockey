package io.github.mrlongnight.photonjockey.ui;

import io.github.mrlongnight.photonjockey.config.Config;
import io.github.mrlongnight.photonjockey.config.ConfigNode;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;

public class SettingsController {

    @FXML private TextField logPathTextField;
    @FXML private Button browseLogPathButton;
    @FXML private ChoiceBox<String> logLevelChoiceBox;
    @FXML private Label statusLabel;
    @FXML private Label infoLabel;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    private Config config;
    private Stage stage;

    public void initialize(Config config, Stage stage) {
        this.config = config;
        this.stage = stage;

        logLevelChoiceBox.getItems().addAll("INFO", "DEBUG", "ERROR");

        loadSettings();

        browseLogPathButton.setOnAction(event -> browseForLogPath());
        saveButton.setOnAction(event -> saveSettings());
        cancelButton.setOnAction(event -> stage.close());
    }

    private void loadSettings() {
        logPathTextField.setText(config.get(ConfigNode.LOG_PATH));
        logLevelChoiceBox.setValue(config.get(ConfigNode.LOG_LEVEL));
    }

    private void saveSettings() {
        config.put(ConfigNode.LOG_PATH, logPathTextField.getText());
        config.put(ConfigNode.LOG_LEVEL, logLevelChoiceBox.getValue());
        stage.close();
    }

    private void browseForLogPath() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(stage);
        if (selectedDirectory != null) {
            logPathTextField.setText(selectedDirectory.getAbsolutePath());
        }
    }

    public void updateStatus(String status) {
        statusLabel.setText("Status: " + status);
    }

    public void updateInfo(String info) {
        infoLabel.setText(info);
    }
}
