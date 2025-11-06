package io.github.mrlongnight.photonjockey.ui;

import io.github.mrlongnight.photonjockey.audio.AudioFrame;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import io.github.mrlongnight.photonjockey.config.Config;

import java.io.IOException;
import java.util.function.Consumer;

public class AudioAnalyzerDashboardController {

    @FXML private VBox topVBox; // Parent container for canvases
    @FXML private Canvas waveformCanvas;
    @FXML private Canvas spectrumCanvas;
    @FXML private Canvas lowFreqCanvas;
    @FXML private Canvas midFreqCanvas;
    @FXML private Canvas highFreqCanvas;
    @FXML private StackPane beatIndicatorPane;
    @FXML private Label bpmLabel;
    @FXML private Slider gainSlider;
    @FXML private Label gainValueLabel;
    @FXML private Slider beatSensitivitySlider;
    @FXML private Label beatSensitivityValueLabel;
    @FXML private Slider minBpmSlider;
    @FXML private Label minBpmValueLabel;
    @FXML private Slider maxBpmSlider;
    @FXML private Label maxBpmValueLabel;
    @FXML private ProgressBar levelProgressBar;
    @FXML private Slider minBeatIntervalSlider;
    @FXML private Label minBeatIntervalValueLabel;
    @FXML private CheckBox bassOnlyModeCheckbox;
    @FXML private ComboBox<String> audioDeviceComboBox;
    @FXML private Button refreshDevicesButton;
    @FXML private CheckBox visualizationsCheckbox;
    @FXML private Button settingsButton;
    @FXML private Label statusLabel;
    @FXML private Label infoLabel;

    @FXML private Slider lowFreqSlider;
    @FXML private Label lowFreqValueLabel;
    @FXML private Slider midFreqSlider;
    @FXML private Label midFreqValueLabel;
    @FXML private Slider highFreqSlider;
    @FXML private Label highFreqValueLabel;

    private double[] waveformData;
    private double[] spectrumData;
    private double[] lowFreqData;
    private double[] midFreqData;
    private double[] highFreqData;
    private double currentBpm;
    private boolean beatActive;

    private Runnable onRefreshDevicesCallback;
    private Consumer<String> onAudioDeviceSelectedCallback;
    private Runnable onConfigChangedCallback;
    private Consumer<Boolean> onVisualizationsToggledCallback;
    private Config config;

    public void setConfig(Config config) {
        this.config = config;
    }

    @FXML
    public void initialize() {
        setupSliders();
        initializeCanvases();
        beatIndicatorPane.getStyleClass().add("beat-indicator-off");
        currentBpm = 0.0;
        beatActive = false;

        settingsButton.setOnAction(event -> openSettingsWindow());

        // Bind canvas width to parent width for responsiveness
        waveformCanvas.widthProperty().bind(topVBox.widthProperty().subtract(20)); // Subtract padding
        spectrumCanvas.widthProperty().bind(topVBox.widthProperty().subtract(20));

        // Bind slider widths to parent width for responsiveness
        lowFreqSlider.prefWidthProperty().bind(topVBox.widthProperty().subtract(200));
        midFreqSlider.prefWidthProperty().bind(topVBox.widthProperty().subtract(200));
        highFreqSlider.prefWidthProperty().bind(topVBox.widthProperty().subtract(200));
        gainSlider.prefWidthProperty().bind(topVBox.widthProperty().subtract(200));
        beatSensitivitySlider.prefWidthProperty().bind(topVBox.widthProperty().subtract(200));
        minBpmSlider.prefWidthProperty().bind(topVBox.widthProperty().subtract(200));
        maxBpmSlider.prefWidthProperty().bind(topVBox.widthProperty().subtract(200));
        minBeatIntervalSlider.prefWidthProperty().bind(topVBox.widthProperty().subtract(200));
        levelProgressBar.prefWidthProperty().bind(topVBox.widthProperty().subtract(800));


        // Redraw canvas on size change
        waveformCanvas.widthProperty().addListener((obs, oldVal, newVal) -> drawWaveform());
        spectrumCanvas.widthProperty().addListener((obs, oldVal, newVal) -> drawSpectrum());
        lowFreqCanvas.widthProperty().addListener((obs, oldVal, newVal) -> drawLowFreqSpectrum());
        midFreqCanvas.widthProperty().addListener((obs, oldVal, newVal) -> drawMidFreqSpectrum());
        highFreqCanvas.widthProperty().addListener((obs, oldVal, newVal) -> drawHighFreqSpectrum());
    }

    private void drawLowFreqSpectrum() {
        if (lowFreqData == null) return;
        GraphicsContext gc = lowFreqCanvas.getGraphicsContext2D();
        drawSpectrumData(gc, lowFreqCanvas, lowFreqData);
    }

    private void drawMidFreqSpectrum() {
        if (midFreqData == null) return;
        GraphicsContext gc = midFreqCanvas.getGraphicsContext2D();
        drawSpectrumData(gc, midFreqCanvas, midFreqData);
    }

    private void drawHighFreqSpectrum() {
        if (highFreqData == null) return;
        GraphicsContext gc = highFreqCanvas.getGraphicsContext2D();
        drawSpectrumData(gc, highFreqCanvas, highFreqData);
    }

    private void drawSpectrumData(GraphicsContext gc, Canvas canvas, double[] data) {
        clearCanvas(canvas);
        double width = canvas.getWidth();
        double height = canvas.getHeight();
        int barCount = Math.min(data.length, 128);
        if(barCount == 0) return;
        double barWidth = width / barCount;
        gc.setFill(Color.web("#8A2BE2"));
        for (int i = 0; i < barCount; i++) {
            double value = 0;
            int binStart = (i * data.length) / barCount;
            int binEnd = ((i + 1) * data.length) / barCount;
            for (int j = binStart; j < binEnd && j < data.length; j++) {
                value += data[j];
            }
            value /= (binEnd - binStart);
            value = Math.min(value, 1.0);
            double barHeight = value * height;
            double x = i * barWidth;
            double y = height - barHeight;
            gc.fillRect(x, y, barWidth > 1 ? barWidth - 1 : barWidth, barHeight);
        }
    }

    private void openSettingsWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Settings.fxml"));
            Parent root = loader.load();

            SettingsController settingsController = loader.getController();
            Stage settingsStage = new Stage();
            settingsController.initialize(config, settingsStage);

            settingsStage.setTitle("Settings");
            settingsStage.setScene(new Scene(root));
            settingsStage.initModality(Modality.APPLICATION_MODAL);
            settingsStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupSliders() {
        gainSlider.valueProperty().addListener((obs, oldVal, newVal) -> gainValueLabel.setText(String.format("%.2f", newVal.doubleValue())));
        audioDeviceComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && onAudioDeviceSelectedCallback != null) onAudioDeviceSelectedCallback.accept(newVal);
        });
        minBpmSlider.valueProperty().addListener((obs, oldVal, newVal) -> minBpmValueLabel.setText(String.format("%.0f", newVal.doubleValue())));
        maxBpmSlider.valueProperty().addListener((obs, oldVal, newVal) -> maxBpmValueLabel.setText(String.format("%.0f", newVal.doubleValue())));
        minBeatIntervalSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            minBeatIntervalValueLabel.setText(String.format("%.0f", newVal.doubleValue()));
            if (onConfigChangedCallback != null) onConfigChangedCallback.run();
        });
        beatSensitivitySlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            beatSensitivityValueLabel.setText(String.format("%.2f", newVal.doubleValue()));
            if (onConfigChangedCallback != null) onConfigChangedCallback.run();
        });
        bassOnlyModeCheckbox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (onConfigChangedCallback != null) onConfigChangedCallback.run();
        });
        visualizationsCheckbox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (onVisualizationsToggledCallback != null) onVisualizationsToggledCallback.accept(newVal);
        });

        gainValueLabel.setText(String.format("%.2f", gainSlider.getValue()));
        beatSensitivityValueLabel.setText(String.format("%.2f", beatSensitivitySlider.getValue()));
        minBpmValueLabel.setText(String.format("%.0f", minBpmSlider.getValue()));
        maxBpmValueLabel.setText(String.format("%.0f", maxBpmSlider.getValue()));
        minBeatIntervalValueLabel.setText(String.format("%.0f", minBeatIntervalSlider.getValue()));

        lowFreqSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            lowFreqValueLabel.setText(String.format("%.0f Hz", newVal.doubleValue()));
            if (onConfigChangedCallback != null) onConfigChangedCallback.run();
        });
        midFreqSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            midFreqValueLabel.setText(String.format("%.0f Hz", newVal.doubleValue()));
            if (onConfigChangedCallback != null) onConfigChangedCallback.run();
        });
        highFreqSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            highFreqValueLabel.setText(String.format("%.0f Hz", newVal.doubleValue()));
            if (onConfigChangedCallback != null) onConfigChangedCallback.run();
        });

        lowFreqValueLabel.setText(String.format("%.0f Hz", lowFreqSlider.getValue()));
        midFreqValueLabel.setText(String.format("%.0f Hz", midFreqSlider.getValue()));
        highFreqValueLabel.setText(String.format("%.0f Hz", highFreqSlider.getValue()));
    }

    private void initializeCanvases() {
        clearCanvas(waveformCanvas);
        clearCanvas(spectrumCanvas);
    }

    private void clearCanvas(Canvas canvas) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.web("#2b2b2b"));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    public void updateWaveform(AudioFrame frame) {
        if (frame == null) return;
        byte[] data = frame.getData();
        int sampleCount = data.length / 2;
        waveformData = new double[sampleCount];
        for (int i = 0; i < sampleCount && i * 2 < data.length; i++) {
            short sample = (short) ((data[i * 2 + 1] << 8) | (data[i * 2] & 0xFF));
            waveformData[i] = sample / 32768.0 * gainSlider.getValue();
        }
        Platform.runLater(this::drawWaveform);
    }

    public void updateSpectrum(double[] spectrum) {
        if (spectrum == null) return;
        spectrumData = new double[spectrum.length];
        double gain = gainSlider.getValue();
        for (int i = 0; i < spectrum.length; i++) {
            spectrumData[i] = spectrum[i] * gain;
        }
        Platform.runLater(this::drawSpectrum);
    }

    public void updateLowFreqSpectrum(double[] lowFreqData) {
        if (lowFreqData == null) return;
        this.lowFreqData = new double[lowFreqData.length];
        double gain = gainSlider.getValue();
        for (int i = 0; i < lowFreqData.length; i++) {
            this.lowFreqData[i] = lowFreqData[i] * gain;
        }
        Platform.runLater(this::drawLowFreqSpectrum);
    }

    public void updateMidFreqSpectrum(double[] midFreqData) {
        if (midFreqData == null) return;
        this.midFreqData = new double[midFreqData.length];
        double gain = gainSlider.getValue();
        for (int i = 0; i < midFreqData.length; i++) {
            this.midFreqData[i] = midFreqData[i] * gain;
        }
        Platform.runLater(this::drawMidFreqSpectrum);
    }

    public void updateHighFreqSpectrum(double[] highFreqData) {
        if (highFreqData == null) return;
        this.highFreqData = new double[highFreqData.length];
        double gain = gainSlider.getValue();
        for (int i = 0; i < highFreqData.length; i++) {
            this.highFreqData[i] = highFreqData[i] * gain;
        }
        Platform.runLater(this::drawHighFreqSpectrum);
    }

    public void updateBeatIndicator(boolean isBeat, double bpm) {
        this.beatActive = isBeat;
        this.currentBpm = bpm;
        Platform.runLater(() -> {
            beatIndicatorPane.getStyleClass().remove("beat-indicator-on");
            beatIndicatorPane.getStyleClass().remove("beat-indicator-off");
            beatIndicatorPane.getStyleClass().add(beatActive ? "beat-indicator-on" : "beat-indicator-off");
            bpmLabel.setText(String.format("%.1f", currentBpm));
        });
    }

    private void drawWaveform() {
        if (waveformData == null) return;
        GraphicsContext gc = waveformCanvas.getGraphicsContext2D();
        clearCanvas(waveformCanvas);
        double width = waveformCanvas.getWidth();
        double height = waveformCanvas.getHeight();
        double centerY = height / 2;
        gc.setStroke(Color.web("#00ff00"));
        gc.setLineWidth(1.5);
        gc.beginPath();
        for (int i = 0; i < waveformData.length; i++) {
            double x = (i / (double) waveformData.length) * width;
            double y = centerY - (waveformData[i] * centerY);
            if (i == 0) gc.moveTo(x, y);
            else gc.lineTo(x, y);
        }
        gc.stroke();
        gc.setStroke(Color.web("#555555"));
        gc.setLineWidth(1);
        gc.strokeLine(0, centerY, width, centerY);
    }

    private void drawSpectrum() {
        if (spectrumData == null) return;
        GraphicsContext gc = spectrumCanvas.getGraphicsContext2D();
        clearCanvas(spectrumCanvas);
        double width = spectrumCanvas.getWidth();
        double height = spectrumCanvas.getHeight();
        int barCount = Math.min(spectrumData.length, 128);
        if(barCount == 0) return;
        double barWidth = width / barCount;
        gc.setFill(Color.web("#8A2BE2"));
        for (int i = 0; i < barCount; i++) {
            double value = 0;
            int binStart = (i * spectrumData.length) / barCount;
            int binEnd = ((i + 1) * spectrumData.length) / barCount;
            for (int j = binStart; j < binEnd && j < spectrumData.length; j++) {
                value += spectrumData[j];
            }
            value /= (binEnd - binStart);
            value = Math.min(value, 1.0);
            double barHeight = value * height;
            double x = i * barWidth;
            double y = height - barHeight;
            gc.fillRect(x, y, barWidth > 1 ? barWidth - 1 : barWidth, barHeight);
        }
    }

    public double getGain() { return gainSlider.getValue(); }
    public void setGain(double gain) { gainSlider.setValue(gain); }
    public double getBeatSensitivity() { return beatSensitivitySlider.getValue(); }
    public double getMinBpm() { return minBpmSlider.getValue(); }
    public double getMaxBpm() { return maxBpmSlider.getValue(); }
    public double getMinBeatInterval() { return minBeatIntervalSlider.getValue(); }
    public double getLowFreq() { return lowFreqSlider.getValue(); }
    public double getMidFreq() { return midFreqSlider.getValue(); }
    public double getHighFreq() { return highFreqSlider.getValue(); }
    public boolean isBassOnlyModeEnabled() { return bassOnlyModeCheckbox.isSelected(); }
    public void setBeatSensitivity(double sensitivity) { beatSensitivitySlider.setValue(sensitivity); }
    public void setMinBeatInterval(double interval) { minBeatIntervalSlider.setValue(interval); }
    public void setBassOnlyMode(boolean enabled) { bassOnlyModeCheckbox.setSelected(enabled); }
    public void setLowFreq(double freq) { lowFreqSlider.setValue(freq); }
    public void setMidFreq(double freq) { midFreqSlider.setValue(freq); }
    public void setHighFreq(double freq) { highFreqSlider.setValue(freq); }

    public void clear() {
        Platform.runLater(() -> {
            initializeCanvases();
            beatIndicatorPane.getStyleClass().remove("beat-indicator-on");
            beatIndicatorPane.getStyleClass().add("beat-indicator-off");
            bpmLabel.setText("0.0");
        });
    }

    public void setCallbacks(Runnable onRefreshDevices, Consumer<String> onAudioDeviceSelected, Runnable onConfigChanged, Consumer<Boolean> onVisualizationsToggled) {
        this.onRefreshDevicesCallback = onRefreshDevices;
        this.onAudioDeviceSelectedCallback = onAudioDeviceSelected;
        this.onConfigChangedCallback = onConfigChanged;
        this.onVisualizationsToggledCallback = onVisualizationsToggled;
    }

    @FXML private void onRefreshDevices() {
        if (onRefreshDevicesCallback != null) onRefreshDevicesCallback.run();
    }

    public void updateAudioDevices(java.util.List<String> deviceNames, String selectedDevice) {
        Platform.runLater(() -> {
            audioDeviceComboBox.getItems().clear();
            audioDeviceComboBox.getItems().addAll(deviceNames);
            if (selectedDevice != null && deviceNames.contains(selectedDevice)) {
                audioDeviceComboBox.setValue(selectedDevice);
            } else if (!deviceNames.isEmpty()) {
                audioDeviceComboBox.setValue(deviceNames.get(0));
            }
        });
    }

    public String getSelectedAudioDevice() { return audioDeviceComboBox.getValue(); }
    public void updateStatus(String status) { Platform.runLater(() -> statusLabel.setText("Status: " + status)); }
    public boolean isVisualizationsEnabled() { return visualizationsCheckbox.isSelected(); }
    public void updateInfo(String info) { Platform.runLater(() -> infoLabel.setText(info)); }
    public void updateLevel(double level) { Platform.runLater(() -> levelProgressBar.setProgress(level)); }
}
