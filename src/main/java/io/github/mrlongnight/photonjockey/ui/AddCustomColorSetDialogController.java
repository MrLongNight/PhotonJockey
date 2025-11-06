package io.github.mrlongnight.photonjockey.ui;

import io.github.mrlongnight.photonjockey.config.Config;
import io.github.mrlongnight.photonjockey.config.ConfigNode;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class AddCustomColorSetDialogController {

    @FXML private Canvas colorPickerCanvas;
    @FXML private Rectangle selectedColorView;
    @FXML private Button randomizeColorButton;
    @FXML private FlowPane chosenColorsPane;
    @FXML private TextField colorSetNameField;
    @FXML private Button addSetButton;
    @FXML private Button cancelButton;

    private Config config;
    private Stage dialogStage;
    private Color selectedColor = Color.CYAN;
    private List<Color> chosenColors = new ArrayList<>();
    private final Random random = new Random();

    public void initialize(Config config, Stage dialogStage) {
        this.config = config;
        this.dialogStage = dialogStage;
        drawColorPicker();
        updateSelectedColorView();
        setupListeners();
    }

    private void setupListeners() {
        colorPickerCanvas.setOnMouseClicked(this::handleColorPickerClick);
        selectedColorView.setOnMouseClicked(e -> addChosenColor());
        randomizeColorButton.setOnAction(e -> randomizeColor());
        addSetButton.setOnAction(e -> addColorSet());
        cancelButton.setOnAction(e -> dialogStage.close());
        colorSetNameField.textProperty().addListener((obs, oldVal, newVal) -> validateInput());
    }

    private void drawColorPicker() {
        GraphicsContext gc = colorPickerCanvas.getGraphicsContext2D();
        double width = colorPickerCanvas.getWidth();
        double height = colorPickerCanvas.getHeight();

        for (int x = 0; x < width; x++) {
            Color color = Color.hsb(x / width * 360, 1.0, 1.0);
            gc.setStroke(color);
            gc.strokeLine(x, 0, x, height);
        }
    }

    private void handleColorPickerClick(MouseEvent event) {
        double hue = (event.getX() / colorPickerCanvas.getWidth()) * 360;
        selectedColor = Color.hsb(hue, 1.0, 1.0);
        updateSelectedColorView();
    }

    private void updateSelectedColorView() {
        selectedColorView.setFill(selectedColor);
    }

    private void randomizeColor() {
        selectedColor = Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));
        updateSelectedColorView();
    }

    private void addChosenColor() {
        if (chosenColors.size() >= 10) return; // Limit number of colors
        chosenColors.add(selectedColor);
        updateChosenColorsPane();
    }

    private void updateChosenColorsPane() {
        chosenColorsPane.getChildren().clear();
        for (Color color : chosenColors) {
            Rectangle colorRect = new Rectangle(30, 30, color);
            colorRect.setOnMouseClicked(e -> {
                chosenColors.remove(color);
                updateChosenColorsPane();
            });
            chosenColorsPane.getChildren().add(colorRect);
        }
        validateInput();
    }

    private void validateInput() {
        boolean isNameValid = colorSetNameField.getText() != null && !colorSetNameField.getText().trim().isEmpty();
        boolean areColorsValid = chosenColors.size() >= 3;
        addSetButton.setDisable(!isNameValid || !areColorsValid);
    }

    private void addColorSet() {
        String setName = colorSetNameField.getText().trim();
        List<String> existingSets = config.getStringList(ConfigNode.COLOR_SET_LIST);
        if (existingSets.contains(setName) || "Random".equalsIgnoreCase(setName)) {
            // Show an error, name already exists
            // (Implementation for alert dialog omitted for brevity)
            System.err.println("Color set name already exists.");
            return;
        }

        List<String> colorsAsHex = chosenColors.stream()
            .map(this::toHexString)
            .collect(Collectors.toList());

        // Save to config
        config.putList(ConfigNode.getCustomNode(ConfigNode.CUSTOM_COLOR_SET_PREFIX.getKey() + setName), colorsAsHex);
        existingSets.add(setName);
        config.putList(ConfigNode.COLOR_SET_LIST, existingSets);

        dialogStage.close();
    }

    private String toHexString(Color color) {
        return String.format("#%02X%02X%02X",
            (int) (color.getRed() * 255),
            (int) (color.getGreen() * 255),
            (int) (color.getBlue() * 255));
    }
}
