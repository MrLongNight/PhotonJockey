package io.github.mrlongnight.photonjockey.ui;

import io.github.mrlongnight.photonjockey.AppTaskOrchestrator;
import io.github.mrlongnight.photonjockey.audio.AudioFrame;
import io.github.mrlongnight.photonjockey.audio.AudioReader;
import io.github.mrlongnight.photonjockey.audio.BeatEvent;
import io.github.mrlongnight.photonjockey.audio.BeatObserver;
import io.github.mrlongnight.photonjockey.config.Config;
import io.github.mrlongnight.photonjockey.config.ConfigNode;
import io.github.mrlongnight.photonjockey.hue.bridge.AccessPoint;
import io.github.mrlongnight.photonjockey.hue.bridge.BridgeConnection;
import io.github.mrlongnight.photonjockey.hue.bridge.HueStateObserver;
import io.github.mrlongnight.photonjockey.hue.bridge.PJHueManager;
import io.github.mrlongnight.photonjockey.hue.bridge.color.ColorSet;
import io.github.mrlongnight.photonjockey.hue.bridge.color.CustomColorSet;
import io.github.mrlongnight.photonjockey.hue.bridge.color.RandomColorSet;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LightControllerDashboardController implements BeatObserver, HueStateObserver {

    private static final Logger logger = LoggerFactory.getLogger(LightControllerDashboardController.class);

    @FXML private VBox topVBox;
    @FXML private FlowPane colorSetPanel;
    @FXML private Button addCustomColorsButton;
    @FXML private Button deleteCustomColorsButton;
    @FXML private Canvas colorPreviewCanvas;
    @FXML private FlowPane lightSelectPanel;
    @FXML private Button restoreLightsButton;
    @FXML private Slider minBrightnessSlider;
    @FXML private Label minBrightnessLabel;
    @FXML private Slider maxBrightnessSlider;
    @FXML private Label maxBrightnessLabel;
    @FXML private Button restoreBrightnessButton;
    @FXML private TitledPane advancedPane;
    @FXML private VBox advancedPanel;
    @FXML private CheckBox strobeCheckBox;
    @FXML private CheckBox colorStrobeCheckbox;
    @FXML private CheckBox glowCheckBox;
    @FXML private CheckBox bassOnlyModeCheckBox;
    @FXML private Slider beatSensitivitySlider;
    @FXML private Label beatSensitivityLabel;
    @FXML private Slider beatDelaySlider;
    @FXML private Label beatDelayLabel;
    @FXML private Slider lightsPerBeatSlider;
    @FXML private Label lightsPerBeatLabel;
    @FXML private Slider maxFadeTimeSlider;
    @FXML private Label maxFadeTimeLabel;
    @FXML private Button readdColorSetPresetsButton;
    @FXML private Button restoreAdvancedButton;
    @FXML private Button startButton;
    @FXML private CheckBox showAdvancedCheckbox;
    @FXML private CheckBox autoStartCheckBox;
    @FXML private CheckBox lightThemeCheckbox;
    @FXML private Label versionLabel;
    @FXML private Label statusLabel;
    @FXML private Label infoLabel;
    @FXML private VBox bridgeManagementPanel;
    @FXML private VBox bridgeDisconnectedPane;
    @FXML private Button findBridgesButton;
    @FXML private ProgressIndicator bridgeSearchIndicator;
    @FXML private ListView<AccessPoint> bridgeListView;
    @FXML private Button connectBridgeButton;
    @FXML private Label pushlinkInstructionLabel;
    @FXML private VBox bridgeConnectedPane;
    @FXML private Label bridgeStatusLabel;
    @FXML private ComboBox<String> entertainmentGroupComboBox;
    @FXML private Button disconnectBridgeButton;

    private Config config;
    private AppTaskOrchestrator taskOrchestrator;
    private AudioReader audioReader;
    private PJHueManager hueManager;
    private ToggleGroup colorSetToggleGroup;
    private List<CheckBox> lightCheckBoxes;

    @FXML
    public void initialize() {
        colorSetToggleGroup = new ToggleGroup();
        lightCheckBoxes = new ArrayList<>();
        setupListeners();
        colorPreviewCanvas.widthProperty().bind(topVBox.widthProperty().subtract(20));
        colorPreviewCanvas.widthProperty().addListener((obs, old, val) -> updateColorPreview());
    }

    public void initialize(Config config, AppTaskOrchestrator taskOrchestrator, AudioReader audioReader, PJHueManager hueManager) {
        this.config = config;
        this.taskOrchestrator = taskOrchestrator;
        this.audioReader = audioReader;
        this.hueManager = hueManager;
        this.hueManager.setStateObserver(this);

        loadConfiguration();
        refreshColorSets();
        updateColorPreview();
        updateBridgeConnectionState(!hueManager.getBridges().isEmpty());
    }

    private void setupListeners() {
        minBrightnessSlider.valueProperty().addListener((obs, o, n) -> {
            minBrightnessLabel.setText(String.format("%d%%", (int) (n.doubleValue() / 254 * 100)));
            if (config != null) config.putInt(ConfigNode.BRIGHTNESS_MIN, n.intValue());
        });
        maxBrightnessSlider.valueProperty().addListener((obs, o, n) -> {
            maxBrightnessLabel.setText(String.format("%d%%", (int) (n.doubleValue() / 254 * 100)));
            if (config != null) config.putInt(ConfigNode.BRIGHTNESS_MAX, n.intValue());
        });
        showAdvancedCheckbox.selectedProperty().addListener((obs, o, n) -> {
            advancedPane.setExpanded(n);
            if (config != null) config.putBoolean(ConfigNode.SHOW_ADVANCED_SETTINGS, n);
        });
        beatSensitivitySlider.valueProperty().addListener((obs, o, n) -> {
            beatSensitivityLabel.setText(String.format("%d%%", n.intValue() * 10));
            if (config != null) config.putInt(ConfigNode.BEAT_SENSITIVITY, n.intValue());
        });
        beatDelaySlider.valueProperty().addListener((obs, o, n) -> {
            beatDelayLabel.setText(String.format("%d", n.intValue()));
            if (config != null) config.putInt(ConfigNode.BEAT_DELAY, n.intValue());
        });
        lightsPerBeatSlider.valueProperty().addListener((obs, o, n) -> {
            lightsPerBeatLabel.setText(String.format("%d", n.intValue()));
            if (config != null) config.putInt(ConfigNode.LIGHTS_PER_BEAT, n.intValue());
        });
        maxFadeTimeSlider.valueProperty().addListener((obs, o, n) -> {
            maxFadeTimeLabel.setText(String.format("%d", n.intValue()));
            if (config != null) config.putInt(ConfigNode.HUE_MAX_FADE_TIME, n.intValue());
        });
        startButton.setOnAction(e -> onStartStopClicked());
        addCustomColorsButton.setOnAction(e -> onAddCustomColors());
        deleteCustomColorsButton.setOnAction(e -> onDeleteCustomColors());
        restoreLightsButton.setOnAction(e -> onRestoreLights());
        restoreBrightnessButton.setOnAction(e -> onRestoreBrightness());
        restoreAdvancedButton.setOnAction(e -> onRestoreAdvanced());
        findBridgesButton.setOnAction(e -> onFindBridges());
        connectBridgeButton.setOnAction(e -> onConnectBridge());
        disconnectBridgeButton.setOnAction(e -> onDisconnectBridge());
        bridgeListView.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> connectBridgeButton.setDisable(n == null));
        lightThemeCheckbox.selectedProperty().addListener((obs, o, n) -> {
            if (n) startButton.getScene().getRoot().getStyleClass().add("light-theme");
            else startButton.getScene().getRoot().getStyleClass().remove("light-theme");
            if (config != null) config.putBoolean(ConfigNode.LIGHT_THEME_ENABLED, n);
        });
    }

    private void onRestoreAdvanced() {
        beatSensitivitySlider.setValue(config.getDefaultInt(ConfigNode.BEAT_SENSITIVITY));
        beatDelaySlider.setValue(config.getDefaultInt(ConfigNode.BEAT_DELAY));
        lightsPerBeatSlider.setValue(config.getDefaultInt(ConfigNode.LIGHTS_PER_BEAT));
        maxFadeTimeSlider.setValue(config.getDefaultInt(ConfigNode.HUE_MAX_FADE_TIME));
        strobeCheckBox.setSelected(config.getDefaultBoolean(ConfigNode.EFFECT_STROBE));
        colorStrobeCheckbox.setSelected(config.getDefaultBoolean(ConfigNode.EFFECT_COLOR_STROBE));
        glowCheckBox.setSelected(config.getDefaultBoolean(ConfigNode.EFFECT_ALERT));
        bassOnlyModeCheckBox.setSelected(config.getDefaultBoolean(ConfigNode.BEAT_BASS_ONLY_MODE));
    }

    private void loadConfiguration() {
        minBrightnessSlider.setValue(config.getInt(ConfigNode.BRIGHTNESS_MIN));
        maxBrightnessSlider.setValue(config.getInt(ConfigNode.BRIGHTNESS_MAX));
        showAdvancedCheckbox.setSelected(config.getBoolean(ConfigNode.SHOW_ADVANCED_SETTINGS));
        advancedPane.setExpanded(config.getBoolean(ConfigNode.SHOW_ADVANCED_SETTINGS));
        autoStartCheckBox.setSelected(config.getBoolean(ConfigNode.AUTOSTART));
        bassOnlyModeCheckBox.setSelected(config.getBoolean(ConfigNode.BEAT_BASS_ONLY_MODE));
        beatSensitivitySlider.setValue(config.getInt(ConfigNode.BEAT_SENSITIVITY));
        beatDelaySlider.setValue(config.getInt(ConfigNode.BEAT_DELAY));
        lightsPerBeatSlider.setValue(config.getInt(ConfigNode.LIGHTS_PER_BEAT));
        maxFadeTimeSlider.setValue(config.getInt(ConfigNode.HUE_MAX_FADE_TIME));
        strobeCheckBox.setSelected(config.getBoolean(ConfigNode.EFFECT_STROBE));
        colorStrobeCheckbox.setSelected(config.getBoolean(ConfigNode.EFFECT_COLOR_STROBE));
        glowCheckBox.setSelected(config.getBoolean(ConfigNode.EFFECT_ALERT));
        lightThemeCheckbox.setSelected(config.getBoolean(ConfigNode.LIGHT_THEME_ENABLED));
    }

    private void updateBridgeConnectionState(boolean isConnected) {
        Platform.runLater(() -> {
            bridgeConnectedPane.setVisible(isConnected);
            bridgeConnectedPane.setManaged(isConnected);
            bridgeDisconnectedPane.setVisible(!isConnected);
            bridgeDisconnectedPane.setManaged(!isConnected);
            pushlinkInstructionLabel.setVisible(false);
            if (isConnected) {
                bridgeStatusLabel.setText("Status: Connected to " + hueManager.getBridges().get(0).getAccessPoint().ip());
                // refreshEntertainmentGroups(); // Temporarily disabled
                refreshLights();
            } else {
                bridgeStatusLabel.setText("Status: Disconnected");
                lightSelectPanel.getChildren().clear();
                entertainmentGroupComboBox.getItems().clear();
            }
        });
    }

    private void onFindBridges() {
        hueManager.doBridgesScan();
    }

    private void onConnectBridge() {
        AccessPoint selectedBridge = bridgeListView.getSelectionModel().getSelectedItem();
        if (selectedBridge != null) {
            hueManager.setAttemptConnection(selectedBridge);
        }
    }

    private void onDisconnectBridge() {
        taskOrchestrator.dispatch(hueManager::disconnectAll);
    }

    // private void refreshEntertainmentGroups() { ... } // Temporarily disabled

    // HueStateObserver implementation
    @Override public void displayFoundBridges(List<AccessPoint> accessPoints) {
        Platform.runLater(() -> {
            bridgeListView.setItems(FXCollections.observableArrayList(accessPoints));
            bridgeSearchIndicator.setVisible(false);
            findBridgesButton.setDisable(false);
            updateStatus(accessPoints.isEmpty() ? "No Hue Bridges found." : "Found " + accessPoints.size() + " Hue Bridge(s).");
        });
    }
    @Override public void isScanningForBridges() {
        Platform.runLater(() -> {
            bridgeSearchIndicator.setVisible(true);
            findBridgesButton.setDisable(true);
        });
    }
    @Override public void isAttemptingConnection() {
        Platform.runLater(() -> connectBridgeButton.setDisable(true));
    }
    @Override public void hasConnected() {
        updateBridgeConnectionState(true);
        updateStatus("Successfully connected to Hue Bridge!");
    }
    @Override public void requestPushlink() {
        Platform.runLater(() -> pushlinkInstructionLabel.setVisible(true));
    }
    @Override public void pushlinkHasFailed() {
        Platform.runLater(() -> {
            pushlinkInstructionLabel.setVisible(false);
            connectBridgeButton.setDisable(false);
            new Alert(Alert.AlertType.ERROR, "Pushlink button was not pressed in time.", ButtonType.OK).showAndWait();
        });
    }
    @Override public void connectionWasLost(AccessPoint ap, BridgeConnection.ConnectionListener.Error error) {
        updateBridgeConnectionState(false);
        updateStatus("Connection to Hue Bridge lost.");
    }
    @Override public void disconnected() {
        updateBridgeConnectionState(false);
        updateStatus("Disconnected from Hue Bridge.");
    }

    private void refreshColorSets() {
        colorSetPanel.getChildren().clear();
        addColorSetRadioButton("Random");
        config.getStringList(ConfigNode.COLOR_SET_LIST).forEach(this::addColorSetRadioButton);
        String selected = config.get(ConfigNode.COLOR_SET_SELECTED);
        if (selected == null) selected = "Random";
        config.put(ConfigNode.COLOR_SET_SELECTED, selected);
        final String finalSelected = selected;
        colorSetToggleGroup.getToggles().stream()
            .filter(t -> ((RadioButton)t).getText().equals(finalSelected))
            .findFirst().ifPresent(t -> t.setSelected(true));
    }

    private void addColorSetRadioButton(String name) {
        RadioButton rb = new RadioButton(name);
        rb.setToggleGroup(colorSetToggleGroup);
        rb.setOnAction(e -> onColorSetSelected(name));
        colorSetPanel.getChildren().add(rb);
    }

    private void refreshLights() {
        lightSelectPanel.getChildren().clear();
        lightCheckBoxes.clear();
        if (hueManager.getBridges().isEmpty()) return;
        List<String> disabledLights = config.getStringList(ConfigNode.LIGHTS_DISABLED);
        hueManager.getLights(false).forEach(light -> {
            CheckBox cb = new CheckBox(light.getName());
            cb.setSelected(!disabledLights.contains(light.getId()));
            cb.setOnAction(e -> onLightSelectionChanged(light.getId(), cb.isSelected()));
            lightSelectPanel.getChildren().add(cb);
            lightCheckBoxes.add(cb);
        });
    }

    private void updateColorPreview() {
        String name = config.get(ConfigNode.COLOR_SET_SELECTED);
        if (name == null) name = "Random";
        ColorSet cs = "Random".equals(name) ? new RandomColorSet() : new CustomColorSet(config, name);
        drawColorPreview(cs);
    }

    private void drawColorPreview(ColorSet cs) {
        GraphicsContext gc = colorPreviewCanvas.getGraphicsContext2D();
        double w = colorPreviewCanvas.getWidth(), h = colorPreviewCanvas.getHeight();
        gc.setFill(Color.web("#2b2b2b"));
        gc.fillRect(0, 0, w, h);
        List<io.github.mrlongnight.photonjockey.hue.bridge.color.Color> colors = cs.getColors();
        if (colors == null || colors.isEmpty()) return;
        int count = Math.min(colors.size(), 20);
        double barW = w / count;
        for (int i = 0; i < count; i++) {
            int rgb = colors.get(i).getRGB();
            gc.setFill(Color.rgb((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF));
            gc.fillRect(i * barW, 0, barW, h);
        }
    }

    private void onStartStopClicked() {
        if (audioReader.isOpen()) audioReader.stop();
    }

    private void onColorSetSelected(String name) {
        config.put(ConfigNode.COLOR_SET_SELECTED, name);
        updateColorPreview();
    }

    private void onLightSelectionChanged(String id, boolean selected) {
        List<String> disabled = new ArrayList<>(config.getStringList(ConfigNode.LIGHTS_DISABLED));
        if (selected) disabled.remove(id);
        else if (!disabled.contains(id)) disabled.add(id);
        config.putList(ConfigNode.LIGHTS_DISABLED, disabled);
    }

    private void onAddCustomColors() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AddCustomColorSetDialog.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("New Color Set");
            stage.initModality(Modality.APPLICATION_MODAL);
            Scene scene = new Scene(root);
            ((AddCustomColorSetDialogController) loader.getController()).initialize(config, stage);
            stage.setScene(scene);
            stage.showAndWait();
            refreshColorSets();
            updateColorPreview();
        } catch (IOException e) {
            logger.error("Failed to open dialog.", e);
        }
    }

    private void onDeleteCustomColors() {
        RadioButton selected = (RadioButton) colorSetToggleGroup.getSelectedToggle();
        if (selected == null) return;
        String name = selected.getText();
        if ("Random".equals(name)) return;
        config.remove(ConfigNode.getCustomNode(ConfigNode.CUSTOM_COLOR_SET_PREFIX.getKey() + name));
        List<String> sets = new ArrayList<>(config.getStringList(ConfigNode.COLOR_SET_LIST));
        sets.remove(name);
        config.putList(ConfigNode.COLOR_SET_LIST, sets);
        config.put(ConfigNode.COLOR_SET_SELECTED, "Random");
        refreshColorSets();
        updateColorPreview();
    }

    private void onRestoreLights() {
        config.remove(ConfigNode.LIGHTS_DISABLED);
        refreshLights();
    }

    private void onRestoreBrightness() {
        minBrightnessSlider.setValue(config.getDefaultInt(ConfigNode.BRIGHTNESS_MIN));
        maxBrightnessSlider.setValue(config.getDefaultInt(ConfigNode.BRIGHTNESS_MAX));
    }

    private void updateStatus(String msg) {
        Platform.runLater(() -> statusLabel.setText("Status: " + msg));
    }

    @Override public void beatReceived(BeatEvent e) { Platform.runLater(this::updateColorPreview); }
    @Override public void noBeatReceived() {}
    @Override public void silenceDetected() {}
    @Override public void audioReceived(AudioFrame f) {}

    @Override
    public void audioReaderStopped(StopStatus status) {
        Platform.runLater(() -> {
            startButton.setText("Start");
            updateStatus("Stopped");
        });
    }
}
