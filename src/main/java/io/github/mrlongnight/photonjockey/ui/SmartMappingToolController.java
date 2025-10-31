package io.github.mrlongnight.photonjockey.ui;

import io.github.mrlongnight.photonjockey.hue.dto.BridgeConfig;
import io.github.mrlongnight.photonjockey.hue.dto.LightConfig;
import io.github.mrlongnight.photonjockey.hue.dto.LightMapConfig;
import io.github.mrlongnight.photonjockey.hue.engine.EffectFrame;
import io.github.mrlongnight.photonjockey.hue.engine.EffectRouter;
import io.github.mrlongnight.photonjockey.hue.engine.LightUpdateDTO;
import io.github.mrlongnight.photonjockey.hue.util.JsonUtils;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Controller for the Smart Mapping Tool UI.
 * Provides interactive canvas for positioning lights and managing bridge/light configurations.
 */
public class SmartMappingToolController {

    @FXML
    private Canvas mapCanvas;

    @FXML
    private ListView<String> lightsListView;

    @FXML
    private TextField lightIdField;

    @FXML
    private TextField lightNameField;

    @FXML
    private ComboBox<String> bridgeIdCombo;

    @FXML
    private ComboBox<String> controlTypeCombo;

    @FXML
    private TextField bridgeIdField;

    @FXML
    private TextField bridgeIpField;

    @FXML
    private Label statusLabel;

    @FXML
    private Button loadButton;

    @FXML
    private Button saveButton;

    @FXML
    private Button testEffectButton;

    @FXML
    private Button addLightButton;

    @FXML
    private Button removeLightButton;

    @FXML
    private Button updateLightButton;

    @FXML
    private Button addBridgeButton;

    private LightMapConfig config;
    private ObservableList<String> lightsList;
    private LightConfig selectedLight;
    private LightConfig draggedLight;
    private double dragOffsetX;
    private double dragOffsetY;
    private File currentFile;

    /**
     * Initializes the controller.
     * Called automatically by JavaFX after FXML loading.
     */
    @FXML
    public void initialize() {
        config = new LightMapConfig();
        lightsList = FXCollections.observableArrayList();
        lightsListView.setItems(lightsList);

        // Initialize control type combo
        controlTypeCombo.setItems(FXCollections.observableArrayList("FAST_UDP", "LOW_HTTP"));
        controlTypeCombo.setValue("FAST_UDP");

        // Setup canvas mouse handlers
        setupCanvasHandlers();

        // Setup list view selection handler
        lightsListView.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldVal, newVal) -> handleLightSelection(newVal)
        );

        // Initial canvas draw
        drawCanvas();
        updateStatus("Ready");
    }

    /**
     * Sets up mouse event handlers for the canvas.
     */
    private void setupCanvasHandlers() {
        mapCanvas.setOnMousePressed(this::handleCanvasMousePressed);
        mapCanvas.setOnMouseDragged(this::handleCanvasMouseDragged);
        mapCanvas.setOnMouseReleased(this::handleCanvasMouseReleased);
    }

    /**
     * Handles mouse press on canvas.
     */
    private void handleCanvasMousePressed(MouseEvent event) {
        double x = event.getX();
        double y = event.getY();

        // Check if we clicked on a light
        draggedLight = findLightAt(x, y);
        if (draggedLight != null) {
            dragOffsetX = x - draggedLight.getX();
            dragOffsetY = y - draggedLight.getY();
        }
    }

    /**
     * Handles mouse drag on canvas.
     */
    private void handleCanvasMouseDragged(MouseEvent event) {
        if (draggedLight != null) {
            double x = event.getX() - dragOffsetX;
            double y = event.getY() - dragOffsetY;

            // Clamp to canvas bounds
            x = Math.max(0, Math.min(x, mapCanvas.getWidth()));
            y = Math.max(0, Math.min(y, mapCanvas.getHeight()));

            draggedLight.setX(x);
            draggedLight.setY(y);
            drawCanvas();
        }
    }

    /**
     * Handles mouse release on canvas.
     */
    private void handleCanvasMouseReleased(MouseEvent event) {
        if (draggedLight != null) {
            updateStatus("Light '" + draggedLight.getId() + "' moved to ("
                + String.format("%.1f", draggedLight.getX()) + ", "
                + String.format("%.1f", draggedLight.getY()) + ")");
            draggedLight = null;
        }
    }

    /**
     * Finds a light at the given coordinates.
     */
    private LightConfig findLightAt(double x, double y) {
        final double hitRadius = 15.0;
        for (LightConfig light : config.getLights()) {
            double dx = light.getX() - x;
            double dy = light.getY() - y;
            if (Math.sqrt(dx * dx + dy * dy) <= hitRadius) {
                return light;
            }
        }
        return null;
    }

    /**
     * Handles light selection from the list.
     */
    private void handleLightSelection(String lightId) {
        if (lightId == null) {
            selectedLight = null;
            clearLightFields();
            return;
        }

        selectedLight = findLightById(lightId);
        if (selectedLight != null) {
            lightIdField.setText(selectedLight.getId());
            lightNameField.setText(selectedLight.getName() != null ? selectedLight.getName() : "");
            bridgeIdCombo.setValue(selectedLight.getBridgeId());
            controlTypeCombo.setValue(selectedLight.getControlType());
        }
    }

    /**
     * Finds a light by ID.
     */
    private LightConfig findLightById(String id) {
        return config.getLights().stream()
            .filter(light -> light.getId().equals(id))
            .findFirst()
            .orElse(null);
    }

    /**
     * Clears the light property fields.
     */
    private void clearLightFields() {
        lightIdField.clear();
        lightNameField.clear();
        bridgeIdCombo.setValue(null);
        controlTypeCombo.setValue("FAST_UDP");
    }

    /**
     * Draws the canvas with all lights and bridges.
     */
    private void drawCanvas() {
        GraphicsContext gc = mapCanvas.getGraphicsContext2D();

        // Clear canvas with dark background
        gc.setFill(Color.web("#1a1a1a"));
        gc.fillRect(0, 0, mapCanvas.getWidth(), mapCanvas.getHeight());

        // Draw grid
        gc.setStroke(Color.web("#333333"));
        gc.setLineWidth(1);
        for (int i = 0; i < mapCanvas.getWidth(); i += 50) {
            gc.strokeLine(i, 0, i, mapCanvas.getHeight());
        }
        for (int i = 0; i < mapCanvas.getHeight(); i += 50) {
            gc.strokeLine(0, i, mapCanvas.getWidth(), i);
        }

        // Draw lights
        for (LightConfig light : config.getLights()) {
            drawLight(gc, light);
        }
    }

    /**
     * Draws a single light on the canvas.
     */
    private void drawLight(GraphicsContext gc, LightConfig light) {
        double x = light.getX();
        double y = light.getY();
        boolean isSelected = light.equals(selectedLight);

        // Choose color based on control type
        Color fillColor = "FAST_UDP".equals(light.getControlType())
            ? Color.web("#00aaff")
            : Color.web("#ffaa00");

        // Draw circle
        gc.setFill(fillColor);
        gc.fillOval(x - 10, y - 10, 20, 20);

        // Draw selection indicator
        if (isSelected) {
            gc.setStroke(Color.web("#00ff00"));
            gc.setLineWidth(3);
            gc.strokeOval(x - 15, y - 15, 30, 30);
        } else {
            gc.setStroke(Color.web("#ffffff"));
            gc.setLineWidth(1);
            gc.strokeOval(x - 10, y - 10, 20, 20);
        }

        // Draw label
        gc.setFill(Color.web("#ffffff"));
        String label = light.getName() != null && !light.getName().isEmpty()
            ? light.getName()
            : light.getId();
        gc.fillText(label, x + 15, y + 5);
    }

    /**
     * Handles load configuration button.
     */
    @FXML
    private void handleLoad() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load Light Map Configuration");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("JSON Files", "*.json")
        );

        if (currentFile != null) {
            fileChooser.setInitialDirectory(currentFile.getParentFile());
        }

        File file = fileChooser.showOpenDialog(mapCanvas.getScene().getWindow());
        if (file != null) {
            loadConfiguration(file);
        }
    }

    /**
     * Loads configuration from a file.
     */
    public void loadConfiguration(File file) {
        try {
            config = JsonUtils.fromJsonFile(file.getAbsolutePath(), LightMapConfig.class);
            currentFile = file;
            refreshUI();
            updateStatus("Loaded configuration from " + file.getName());
        } catch (IOException e) {
            updateStatus("Error loading configuration: " + e.getMessage());
        }
    }

    /**
     * Handles save configuration button.
     */
    @FXML
    private void handleSave() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Light Map Configuration");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("JSON Files", "*.json")
        );

        if (currentFile != null) {
            fileChooser.setInitialDirectory(currentFile.getParentFile());
            fileChooser.setInitialFileName(currentFile.getName());
        } else {
            fileChooser.setInitialFileName("lightmap.json");
        }

        File file = fileChooser.showSaveDialog(mapCanvas.getScene().getWindow());
        if (file != null) {
            saveConfiguration(file);
        }
    }

    /**
     * Saves configuration to a file.
     */
    public void saveConfiguration(File file) {
        try {
            JsonUtils.toJsonFile(config, file.getAbsolutePath());
            currentFile = file;
            updateStatus("Saved configuration to " + file.getName());
        } catch (IOException e) {
            updateStatus("Error saving configuration: " + e.getMessage());
        }
    }

    /**
     * Handles test effect button.
     */
    @FXML
    private void handleTestEffect() {
        if (config.getLights().isEmpty()) {
            updateStatus("No lights to test");
            return;
        }

        // Create a simple test effect
        List<LightUpdateDTO> updates = new ArrayList<>();
        for (LightConfig light : config.getLights()) {
            updates.add(new LightUpdateDTO(
                light.getId(),
                200,
                0.0,
                1.0,
                5
            ));
        }

        EffectFrame frame = new EffectFrame(updates, System.currentTimeMillis());

        // Simulate routing (without actual network calls)
        updateStatus("Test effect created with " + updates.size() + " light updates");
    }

    /**
     * Handles add light button.
     */
    @FXML
    private void handleAddLight() {
        String id = "light-" + (config.getLights().size() + 1);
        String bridgeId = bridgeIdCombo.getValue();

        if (bridgeId == null || bridgeId.isEmpty()) {
            if (!config.getBridges().isEmpty()) {
                bridgeId = config.getBridges().get(0).getId();
            } else {
                updateStatus("Please add a bridge first");
                return;
            }
        }

        LightConfig newLight = new LightConfig(
            id,
            mapCanvas.getWidth() / 2,
            mapCanvas.getHeight() / 2,
            bridgeId,
            "FAST_UDP",
            "New Light"
        );

        config.getLights().add(newLight);
        refreshUI();
        lightsListView.getSelectionModel().select(id);
        updateStatus("Added light: " + id);
    }

    /**
     * Handles remove light button.
     */
    @FXML
    private void handleRemoveLight() {
        String selectedId = lightsListView.getSelectionModel().getSelectedItem();
        if (selectedId == null) {
            updateStatus("No light selected");
            return;
        }

        config.getLights().removeIf(light -> light.getId().equals(selectedId));
        selectedLight = null;
        refreshUI();
        updateStatus("Removed light: " + selectedId);
    }

    /**
     * Handles update light button.
     */
    @FXML
    private void handleUpdateLight() {
        if (selectedLight == null) {
            updateStatus("No light selected");
            return;
        }

        String newId = lightIdField.getText();
        if (newId == null || newId.trim().isEmpty()) {
            updateStatus("Light ID cannot be empty");
            return;
        }

        // Check for duplicate ID (except for the current light)
        boolean duplicateId = config.getLights().stream()
            .anyMatch(light -> !light.equals(selectedLight) && light.getId().equals(newId));

        if (duplicateId) {
            updateStatus("Light ID already exists: " + newId);
            return;
        }

        selectedLight.setId(newId);
        selectedLight.setName(lightNameField.getText());
        selectedLight.setBridgeId(bridgeIdCombo.getValue());
        selectedLight.setControlType(controlTypeCombo.getValue());

        refreshUI();
        lightsListView.getSelectionModel().select(newId);
        updateStatus("Updated light: " + newId);
    }

    /**
     * Handles add bridge button.
     */
    @FXML
    private void handleAddBridge() {
        String id = bridgeIdField.getText();
        String ip = bridgeIpField.getText();

        if (id == null || id.trim().isEmpty()) {
            updateStatus("Bridge ID cannot be empty");
            return;
        }

        if (ip == null || ip.trim().isEmpty()) {
            updateStatus("Bridge IP cannot be empty");
            return;
        }

        // Check for duplicate bridge ID
        boolean duplicateId = config.getBridges().stream()
            .anyMatch(bridge -> bridge.getId().equals(id));

        if (duplicateId) {
            updateStatus("Bridge ID already exists: " + id);
            return;
        }

        BridgeConfig newBridge = new BridgeConfig(id, ip, null);
        config.getBridges().add(newBridge);
        refreshBridgeCombo();
        bridgeIdField.clear();
        bridgeIpField.clear();
        updateStatus("Added bridge: " + id + " (" + ip + ")");
    }

    /**
     * Refreshes the UI to reflect the current configuration.
     */
    private void refreshUI() {
        refreshLightsList();
        refreshBridgeCombo();
        drawCanvas();
    }

    /**
     * Refreshes the lights list view.
     */
    private void refreshLightsList() {
        lightsList.clear();
        for (LightConfig light : config.getLights()) {
            lightsList.add(light.getId());
        }
    }

    /**
     * Refreshes the bridge combo box.
     */
    private void refreshBridgeCombo() {
        ObservableList<String> bridgeIds = FXCollections.observableArrayList();
        for (BridgeConfig bridge : config.getBridges()) {
            bridgeIds.add(bridge.getId());
        }
        bridgeIdCombo.setItems(bridgeIds);
    }

    /**
     * Updates the status label.
     */
    private void updateStatus(String message) {
        Platform.runLater(() -> statusLabel.setText("Status: " + message));
    }

    /**
     * Gets the current configuration.
     */
    public LightMapConfig getConfig() {
        return config;
    }

    /**
     * Sets the configuration.
     */
    public void setConfig(LightMapConfig config) {
        this.config = config != null ? config : new LightMapConfig();
        refreshUI();
    }
}
