package pw.wunderlich.lightbeat.ui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import pw.wunderlich.lightbeat.audio.AnalysisResult;
import pw.wunderlich.lightbeat.audio.AudioFrame;

/**
 * Controller for the Audio Analyzer Dashboard UI.
 * Provides real-time visualization of audio waveform, frequency spectrum, and beat detection.
 */
public class AudioAnalyzerDashboardController {

    @FXML
    private Canvas waveformCanvas;

    @FXML
    private Canvas spectrumCanvas;

    @FXML
    private Circle beatIndicator;

    @FXML
    private Label bpmLabel;

    @FXML
    private Slider gainSlider;

    @FXML
    private Label gainValueLabel;

    @FXML
    private Slider beatSensitivitySlider;

    @FXML
    private Label beatSensitivityValueLabel;

    private double[] waveformData;
    private double[] spectrumData;
    private double currentBpm;
    private boolean beatActive;

    /**
     * Initializes the controller.
     * Called automatically by JavaFX after FXML loading.
     */
    @FXML
    public void initialize() {
        setupSliders();
        initializeCanvases();
        beatIndicator.setFill(Color.web("#444444"));
        currentBpm = 0.0;
        beatActive = false;
    }

    /**
     * Sets up slider listeners and initial values.
     */
    private void setupSliders() {
        // Gain slider
        gainSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            gainValueLabel.setText(String.format("%.2f", newVal.doubleValue()));
        });

        // Beat sensitivity slider
        beatSensitivitySlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            beatSensitivityValueLabel.setText(String.format("%.2f", newVal.doubleValue()));
        });

        // Set initial label values
        gainValueLabel.setText(String.format("%.2f", gainSlider.getValue()));
        beatSensitivityValueLabel.setText(String.format("%.2f", beatSensitivitySlider.getValue()));
    }

    /**
     * Initializes canvas backgrounds.
     */
    private void initializeCanvases() {
        clearCanvas(waveformCanvas);
        clearCanvas(spectrumCanvas);
    }

    /**
     * Clears a canvas with a background color.
     */
    private void clearCanvas(Canvas canvas) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.web("#2b2b2b"));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    /**
     * Updates the waveform visualization with audio frame data.
     *
     * @param frame the audio frame containing sample data
     */
    public void updateWaveform(AudioFrame frame) {
        if (frame == null) {
            return;
        }

        // Convert byte data to normalized samples
        byte[] data = frame.getData();
        int sampleCount = Math.min(data.length / 2, (int) waveformCanvas.getWidth());
        waveformData = new double[sampleCount];

        for (int i = 0; i < sampleCount && i * 2 < data.length; i++) {
            // Read 16-bit samples (little-endian)
            short sample = (short) ((data[i * 2 + 1] << 8) | (data[i * 2] & 0xFF));
            waveformData[i] = sample / 32768.0 * gainSlider.getValue();
        }

        Platform.runLater(this::drawWaveform);
    }

    /**
     * Updates the frequency spectrum visualization.
     *
     * @param spectrum the frequency spectrum data
     */
    public void updateSpectrum(double[] spectrum) {
        if (spectrum == null) {
            return;
        }

        spectrumData = new double[spectrum.length];
        double gain = gainSlider.getValue();
        for (int i = 0; i < spectrum.length; i++) {
            spectrumData[i] = spectrum[i] * gain;
        }

        Platform.runLater(this::drawSpectrum);
    }

    /**
     * Updates the beat indicator and BPM display.
     *
     * @param isBeat true if a beat is detected
     * @param bpm    the current beats per minute
     */
    public void updateBeatIndicator(boolean isBeat, double bpm) {
        this.beatActive = isBeat;
        this.currentBpm = bpm;

        Platform.runLater(() -> {
            if (beatActive) {
                beatIndicator.setFill(Color.web("#00ff00"));
            } else {
                beatIndicator.setFill(Color.web("#444444"));
            }
            bpmLabel.setText(String.format("BPM: %.1f", currentBpm));
        });
    }

    /**
     * Draws the waveform on the canvas.
     */
    private void drawWaveform() {
        if (waveformData == null) {
            return;
        }

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
            
            if (i == 0) {
                gc.moveTo(x, y);
            } else {
                gc.lineTo(x, y);
            }
        }
        gc.stroke();

        // Draw center line
        gc.setStroke(Color.web("#555555"));
        gc.setLineWidth(1);
        gc.strokeLine(0, centerY, width, centerY);
    }

    /**
     * Draws the frequency spectrum as bars on the canvas.
     */
    private void drawSpectrum() {
        if (spectrumData == null) {
            return;
        }

        GraphicsContext gc = spectrumCanvas.getGraphicsContext2D();
        clearCanvas(spectrumCanvas);

        double width = spectrumCanvas.getWidth();
        double height = spectrumCanvas.getHeight();
        int barCount = Math.min(spectrumData.length, 64);
        double barWidth = width / barCount;

        gc.setFill(Color.web("#0088ff"));

        for (int i = 0; i < barCount; i++) {
            double value = 0;
            // Average multiple spectrum bins per bar
            int binStart = (i * spectrumData.length) / barCount;
            int binEnd = ((i + 1) * spectrumData.length) / barCount;
            for (int j = binStart; j < binEnd && j < spectrumData.length; j++) {
                value += spectrumData[j];
            }
            value /= (binEnd - binStart);

            // Clamp and scale value
            value = Math.min(value, 1.0);
            double barHeight = value * height;

            double x = i * barWidth;
            double y = height - barHeight;

            gc.fillRect(x, y, barWidth - 1, barHeight);
        }
    }

    /**
     * Gets the current gain value.
     *
     * @return the gain value
     */
    public double getGain() {
        return gainSlider.getValue();
    }

    /**
     * Sets the gain value.
     *
     * @param gain the new gain value
     */
    public void setGain(double gain) {
        gainSlider.setValue(gain);
    }

    /**
     * Gets the current beat sensitivity value.
     *
     * @return the beat sensitivity value
     */
    public double getBeatSensitivity() {
        return beatSensitivitySlider.getValue();
    }

    /**
     * Sets the beat sensitivity value.
     *
     * @param sensitivity the new beat sensitivity value
     */
    public void setBeatSensitivity(double sensitivity) {
        beatSensitivitySlider.setValue(sensitivity);
    }

    /**
     * Clears all visualizations.
     */
    public void clear() {
        Platform.runLater(() -> {
            initializeCanvases();
            beatIndicator.setFill(Color.web("#444444"));
            bpmLabel.setText("BPM: 0.0");
        });
    }
}
