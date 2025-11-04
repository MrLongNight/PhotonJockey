package io.github.mrlongnight.photonjockey.ui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import io.github.mrlongnight.photonjockey.AppTaskOrchestrator;
import io.github.mrlongnight.photonjockey.audio.AudioReader;
import io.github.mrlongnight.photonjockey.audio.BeatEvent;
import io.github.mrlongnight.photonjockey.audio.BeatObserver;
import io.github.mrlongnight.photonjockey.config.Config;
import io.github.mrlongnight.photonjockey.config.ConfigNode;
import io.github.mrlongnight.photonjockey.hue.bridge.HueManager;
import io.github.mrlongnight.photonjockey.hue.bridge.color.ColorSet;
import io.github.mrlongnight.photonjockey.hue.bridge.color.CustomColorSet;
import io.github.mrlongnight.photonjockey.audio.BeatObserver.StopStatus;
import io.github.mrlongnight.photonjockey.hue.bridge.color.RandomColorSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller for the Light Controller Dashboard (formerly MainFrame).
 * Manages Philips Hue light control and synchronization with audio.
 */
public class LightControllerDashboardController implements BeatObserver {

    private static final Logger logger = LoggerFactory.getLogger(LightControllerDashboardController.class);

    // Audio Source Panel
    @FXML private ComboBox<String> audioDeviceComboBox;
    @FXML private Button refreshDevicesButton;
    @FXML private Button deviceHelpButton;

    // Color Selection Panel
    @FXML private FlowPane colorSetPanel;
    @FXML private Button addCustomColorsButton;
    @FXML private Button deleteCustomColorsButton;
    @FXML private Canvas colorPreviewCanvas;

    // Lights Selection Panel
    @FXML private FlowPane lightSelectPanel;
    @FXML private Button restoreLightsButton;

    // Brightness Panel
    @FXML private Slider minBrightnessSlider;
    @FXML private Label minBrightnessLabel;
    @FXML private Slider maxBrightnessSlider;
    @FXML private Label maxBrightnessLabel;
    @FXML private Button restoreBrightnessButton;

    // Advanced Settings Panel
    @FXML private VBox advancedPanel;
    @FXML private CheckBox strobeCheckBox;
    @FXML private CheckBox colorStrobeCheckbox;
    @FXML private CheckBox glowCheckBox;
    @FXML private CheckBox bassOnlyModeCheckBox;
    @FXML private Slider beatSensitivitySlider;
    @FXML private Label beatSensitivityLabel;
    @FXML private Button readdColorSetPresetsButton;
    @FXML private Button restoreAdvancedButton;
    @FXML private Button disconnectBridgeButton;

    // Action Panel
    @FXML private Button startButton;
    @FXML private CheckBox showAdvancedCheckbox;
    @FXML private CheckBox autoStartCheckBox;
    @FXML private CheckBox lightThemeCheckbox;

    // Status Bar
    @FXML private Label versionLabel;
    @FXML private Label statusLabel;
    @FXML private Label infoLabel;

    // Dependencies
    private Config config;
    private AppTaskOrchestrator taskOrchestrator;
    private AudioReader audioReader;
    private HueManager hueManager;

    private ToggleGroup colorSetToggleGroup;
    private List<CheckBox> lightCheckBoxes;

    @FXML
    public void initialize() {
        colorSetToggleGroup = new ToggleGroup();
        lightCheckBoxes = new ArrayList<>();
        setupListeners();
    }

    public void initialize(Config config, AppTaskOrchestrator taskOrchestrator,
                          AudioReader audioReader, HueManager hueManager) {
        this.config = config;
        this.taskOrchestrator = taskOrchestrator;
        this.audioReader = audioReader;
        this.hueManager = hueManager;

        loadConfiguration();
        refreshAudioDevices();
        refreshColorSets();
        refreshLights();
        updateColorPreview();
    }

    private void setupListeners() {
        // Brightness sliders
        minBrightnessSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            minBrightnessLabel.setText(String.format("%d%%", (int)(newVal.doubleValue() / 254 * 100)));
            if (config != null) {
                config.putInt(ConfigNode.BRIGHTNESS_MIN, newVal.intValue());
            }
        });

        maxBrightnessSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            maxBrightnessLabel.setText(String.format("%d%%", (int)(newVal.doubleValue() / 254 * 100)));
            if (config != null) {
                config.putInt(ConfigNode.BRIGHTNESS_MAX, newVal.intValue());
            }
        });

        // Advanced panel toggle
        showAdvancedCheckbox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            advancedPanel.setVisible(newVal);
            advancedPanel.setManaged(newVal);
            if (config != null) {
                config.putBoolean(ConfigNode.SHOW_ADVANCED_SETTINGS, newVal);
            }
        });

        // Beat sensitivity
        beatSensitivitySlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            beatSensitivityLabel.setText(String.format("%d%%", newVal.intValue() * 10));
            if (config != null) {
                config.putInt(ConfigNode.BEAT_SENSITIVITY, newVal.intValue());
            }
        });

        // Start button
        startButton.setOnAction(e -> onStartStopClicked());

        // Other buttons
        refreshDevicesButton.setOnAction(e -> refreshAudioDevices());
        addCustomColorsButton.setOnAction(e -> onAddCustomColors());
        deleteCustomColorsButton.setOnAction(e -> onDeleteCustomColors());
        restoreLightsButton.setOnAction(e -> onRestoreLights());
        restoreBrightnessButton.setOnAction(e -> onRestoreBrightness());
        disconnectBridgeButton.setOnAction(e -> onDisconnectBridge());
    }

    private void loadConfiguration() {
        minBrightnessSlider.setValue(config.getInt(ConfigNode.BRIGHTNESS_MIN));
        maxBrightnessSlider.setValue(config.getInt(ConfigNode.BRIGHTNESS_MAX));
        showAdvancedCheckbox.setSelected(config.getBoolean(ConfigNode.SHOW_ADVANCED_SETTINGS));
        autoStartCheckBox.setSelected(config.getBoolean(ConfigNode.AUTOSTART));
        bassOnlyModeCheckBox.setSelected(config.getBoolean(ConfigNode.BEAT_BASS_ONLY_MODE));
        beatSensitivitySlider.setValue(config.getInt(ConfigNode.BEAT_SENSITIVITY));
        strobeCheckBox.setSelected(config.getBoolean(ConfigNode.EFFECT_STROBE));
        colorStrobeCheckbox.setSelected(config.getBoolean(ConfigNode.EFFECT_COLOR_STROBE));
        glowCheckBox.setSelected(config.getBoolean(ConfigNode.EFFECT_ALERT));
    }

    private void refreshAudioDevices() {
        // Implementation: Load audio devices from audioReader
        audioDeviceComboBox.getItems().clear();
        audioReader.getSupportedDevices().forEach(device ->
            audioDeviceComboBox.getItems().add(device.getName())
        );

        String lastSource = config.get(ConfigNode.LAST_AUDIO_SOURCE);
        if (lastSource != null && audioDeviceComboBox.getItems().contains(lastSource)) {
            audioDeviceComboBox.setValue(lastSource);
        }
    }

    private void refreshColorSets() {
        colorSetPanel.getChildren().clear();

        // Add "Random" color set
        addColorSetRadioButton("Random");

        // Add custom color sets
        List<String> colorSets = config.getStringList(ConfigNode.COLOR_SET_LIST);
        colorSets.forEach(this::addColorSetRadioButton);

        // Select current color set
        String selected = config.get(ConfigNode.COLOR_SET_SELECTED);
        colorSetToggleGroup.getToggles().stream()
            .filter(toggle -> ((RadioButton)toggle).getText().equals(selected))
            .findFirst()
            .ifPresent(toggle -> toggle.setSelected(true));
    }

    private void addColorSetRadioButton(String name) {
        RadioButton radioButton = new RadioButton(name);
        radioButton.setToggleGroup(colorSetToggleGroup);
        radioButton.setOnAction(e -> onColorSetSelected(name));
        colorSetPanel.getChildren().add(radioButton);
    }

    private void refreshLights() {
        lightSelectPanel.getChildren().clear();
        lightCheckBoxes.clear();

        List<String> disabledLights = config.getStringList(ConfigNode.LIGHTS_DISABLED);

        hueManager.getLights(false).forEach(light -> {
            CheckBox checkBox = new CheckBox(light.getName());
            checkBox.setSelected(!disabledLights.contains(light.getId()));
            checkBox.setOnAction(e -> onLightSelectionChanged(light.getId(), checkBox.isSelected()));

            lightSelectPanel.getChildren().add(checkBox);
            lightCheckBoxes.add(checkBox);
        });
    }

    private void updateColorPreview() {
        String colorSetName = config.get(ConfigNode.COLOR_SET_SELECTED);
        ColorSet colorSet = colorSetName.equals("Random") ?
            new RandomColorSet() :
            new CustomColorSet(config, colorSetName);

        drawColorPreview(colorSet);
    }

    private void drawColorPreview(ColorSet colorSet) {
        GraphicsContext gc = colorPreviewCanvas.getGraphicsContext2D();
        double width = colorPreviewCanvas.getWidth();
        double height = colorPreviewCanvas.getHeight();

        // Clear canvas
        gc.setFill(Color.web("#2b2b2b"));
        gc.fillRect(0, 0, width, height);

        List<io.github.mrlongnight.photonjockey.hue.bridge.color.Color> colors = colorSet.getColors();
        if (colors == null || colors.isEmpty()) {
            return;
        }

        // Draw color bars
        int colorCount = Math.min(colors.size(), 20);
        double barWidth = width / colorCount;

        for (int i = 0; i < colorCount; i++) {
            int rgb = colors.get(i).getRGB();
            int red = (rgb >> 16) & 0xFF;
            int green = (rgb >> 8) & 0xFF;
            int blue = rgb & 0xFF;
            gc.setFill(Color.rgb(red, green, blue));
            gc.fillRect(i * barWidth, 0, barWidth, height);
        }
    }

    // Event handlers
    private void onStartStopClicked() {
        if (audioReader.isOpen()) {
            audioReader.stop();
            startButton.setText("Start");
            updateStatus("Stopped");
        } else {
            String deviceName = audioDeviceComboBox.getValue();
            if (deviceName != null) {
                var device = audioReader.getDeviceByName(deviceName);
                if (device != null && audioReader.start(device)) {
                    startButton.setText("Stop");
                    updateStatus("Running");
                }
            }
        }
    }

    private void onColorSetSelected(String name) {
        config.put(ConfigNode.COLOR_SET_SELECTED, name);
        updateColorPreview();
    }

    private void onLightSelectionChanged(String lightId, boolean selected) {
        List<String> disabledLights = new ArrayList<>(config.getStringList(ConfigNode.LIGHTS_DISABLED));
        if (selected) {
            disabledLights.remove(lightId);
        } else {
            if (!disabledLights.contains(lightId)) {
                disabledLights.add(lightId);
            }
        }
        config.putList(ConfigNode.LIGHTS_DISABLED, disabledLights);
    }

    private void onAddCustomColors() {
        // Open color selection dialog
        logger.info("Add custom colors clicked");
    }

    private void onDeleteCustomColors() {
        // Delete selected color set
        logger.info("Delete custom colors clicked");
    }

    private void onRestoreLights() {
        config.remove(ConfigNode.LIGHTS_DISABLED);
        refreshLights();
    }

    private void onRestoreBrightness() {
        minBrightnessSlider.setValue(config.getDefaultInt(ConfigNode.BRIGHTNESS_MIN));
        maxBrightnessSlider.setValue(config.getDefaultInt(ConfigNode.BRIGHTNESS_MAX));
    }

    private void onDisconnectBridge() {
        hueManager.disconnectAll();
        refreshLights();
        updateStatus("Bridge disconnected");
    }

    private void updateStatus(String message) {
        Platform.runLater(() -> statusLabel.setText("Status: " + message));
    }

    // BeatObserver implementation
    @Override
    public void beatReceived(BeatEvent event) {
        Platform.runLater(() -> {
            // Visual feedback on beat
            updateColorPreview();
        });
    }

    @Override
    public void noBeatReceived() {}

    @Override
    public void silenceDetected() {}

    @Override
    public void audioReceived(io.github.mrlongnight.photonjockey.audio.AudioFrame audioFrame) {}

    @Override
    public void audioReaderStopped(StopStatus status) {
        Platform.runLater(() -> {
            startButton.setText("Start");
            updateStatus("Stopped");
        });
    }
}