package pw.wunderlich.lightbeat.ui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.paint.Color;
import pw.wunderlich.lightbeat.audio.AnalysisResult;
import pw.wunderlich.lightbeat.audio.BeatDetector;
import pw.wunderlich.lightbeat.audio.FFTProcessor;
import pw.wunderlich.lightbeat.audio.WindowFunction;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Controller for the Audio Dashboard UI.
 * Subscribes to audio analysis events and updates visualization components.
 */
public class AudioDashboardController {

    @FXML
    private Canvas waveformCanvas;

    @FXML
    private Canvas spectrumCanvas;

    @FXML
    private Label beatIndicator;

    @FXML
    private Label bpmLabel;

    @FXML
    private Slider fftSizeSlider;

    @FXML
    private Label fftSizeLabel;

    @FXML
    private Slider smoothingSlider;

    @FXML
    private Label smoothingLabel;

    @FXML
    private Slider gainSlider;

    @FXML
    private Label gainLabel;

    @FXML
    private ComboBox<String> presetComboBox;

    private final Queue<Double> waveformHistory;
    private final int maxWaveformSamples = 500;
    private double[] currentSpectrum;
    private boolean currentBeat;
    private double currentBpm;
    private BeatDetector beatDetector;
    private FFTProcessor fftProcessor;
    private long lastBeatTime;
    private static final long BEAT_DISPLAY_DURATION_MS = 100;

    /**
     * Creates a new AudioDashboardController.
     */
    public AudioDashboardController() {
        this.waveformHistory = new LinkedList<>();
        this.currentSpectrum = new double[0];
        this.currentBeat = false;
        this.currentBpm = 0.0;
        this.beatDetector = new BeatDetector();
        this.lastBeatTime = 0;
    }

    /**
     * Initializes the controller after FXML loading.
     */
    @FXML
    public void initialize() {
        // Initialize FFT processor with default values
        int fftSize = (int) Math.pow(2, fftSizeSlider.getValue());
        fftProcessor = new FFTProcessor(fftSize, WindowFunction.HAMMING,
                smoothingSlider.getValue());

        // Setup slider listeners
        setupSliderListeners();

        // Setup preset combo box
        setupPresetComboBox();

        // Initialize canvases
        clearWaveform();
        clearSpectrum();
    }

    /**
     * Sets up listeners for the control sliders.
     */
    private void setupSliderListeners() {
        // FFT Size slider
        fftSizeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            int fftSize = (int) Math.pow(2, newVal.intValue());
            fftSizeLabel.setText(String.valueOf(fftSize));
            // Recreate FFT processor with new size
            fftProcessor = new FFTProcessor(fftSize, WindowFunction.HAMMING,
                    smoothingSlider.getValue());
        });

        // Smoothing slider
        smoothingSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            smoothingLabel.setText(String.format("%.2f", newVal.doubleValue()));
            // Recreate FFT processor with new smoothing
            int fftSize = (int) Math.pow(2, fftSizeSlider.getValue());
            fftProcessor = new FFTProcessor(fftSize, WindowFunction.HAMMING,
                    newVal.doubleValue());
        });

        // Gain slider
        gainSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            gainLabel.setText(String.format("%.2f", newVal.doubleValue()));
        });
    }

    /**
     * Sets up the preset combo box with predefined configurations.
     */
    private void setupPresetComboBox() {
        presetComboBox.setOnAction(event -> {
            String preset = presetComboBox.getValue();
            applyPreset(preset);
        });
    }

    /**
     * Applies a preset configuration.
     *
     * @param preset the preset name
     */
    private void applyPreset(String preset) {
        switch (preset) {
            case "High Sensitivity":
                fftSizeSlider.setValue(12); // 4096
                smoothingSlider.setValue(0.3);
                gainSlider.setValue(2.0);
                break;
            case "Low Sensitivity":
                fftSizeSlider.setValue(10); // 1024
                smoothingSlider.setValue(0.7);
                gainSlider.setValue(0.5);
                break;
            case "Bass Focus":
                fftSizeSlider.setValue(13); // 8192
                smoothingSlider.setValue(0.6);
                gainSlider.setValue(1.5);
                break;
            case "Treble Focus":
                fftSizeSlider.setValue(10); // 1024
                smoothingSlider.setValue(0.4);
                gainSlider.setValue(1.5);
                break;
            default: // "Default"
                fftSizeSlider.setValue(11); // 2048
                smoothingSlider.setValue(0.5);
                gainSlider.setValue(1.0);
                break;
        }
    }

    /**
     * Called when new audio analysis data is available.
     * This method should be called by the audio analysis pipeline.
     *
     * @param result the analysis result containing audio features
     */
    public void onAnalysis(AnalysisResult result) {
        if (result == null) {
            return;
        }

        // Update beat detection
        boolean isBeat = beatDetector.isBeat(result);
        if (isBeat) {
            currentBeat = true;
            lastBeatTime = System.currentTimeMillis();
        } else {
            // Clear beat indicator after display duration
            if (System.currentTimeMillis() - lastBeatTime > BEAT_DISPLAY_DURATION_MS) {
                currentBeat = false;
            }
        }
        currentBpm = beatDetector.getBPM();

        // Update waveform history
        updateWaveformHistory(result.getAmplitude());

        // Update spectrum (simulate FFT output from energy/frequency data)
        updateSpectrum(result);

        // Update UI on JavaFX thread
        Platform.runLater(this::updateUI);
    }

    /**
     * Updates the waveform history with new amplitude data.
     *
     * @param amplitude the current amplitude
     */
    private void updateWaveformHistory(double amplitude) {
        waveformHistory.offer(amplitude * gainSlider.getValue());
        while (waveformHistory.size() > maxWaveformSamples) {
            waveformHistory.poll();
        }
    }

    /**
     * Updates the spectrum data from analysis result.
     *
     * @param result the analysis result
     */
    private void updateSpectrum(AnalysisResult result) {
        // Create a simple spectrum representation
        // In a real implementation, this would come from actual FFT processing
        int numBins = 64;
        currentSpectrum = new double[numBins];
        
        // Simulate spectrum based on frequency and energy
        double frequency = result.getFrequency();
        double energy = result.getEnergy();
        double gain = gainSlider.getValue();
        
        // Create a peak around the dominant frequency
        for (int i = 0; i < numBins; i++) {
            double binFreq = (i + 1) * 50.0; // Simulate frequency bins
            double distance = Math.abs(frequency - binFreq);
            double magnitude = energy * Math.exp(-distance / 500.0) * gain;
            currentSpectrum[i] = Math.min(magnitude, 1.0);
        }
    }

    /**
     * Updates all UI components.
     */
    private void updateUI() {
        updateBeatIndicator();
        updateBpmLabel();
        drawWaveform();
        drawSpectrum();
    }

    /**
     * Updates the beat indicator label.
     */
    private void updateBeatIndicator() {
        if (currentBeat) {
            beatIndicator.setText("YES");
            beatIndicator.setStyle(
                    "-fx-background-color: #00ff00; -fx-padding: 5 15; -fx-font-weight: bold;");
        } else {
            beatIndicator.setText("NO");
            beatIndicator.setStyle(
                    "-fx-background-color: #cccccc; -fx-padding: 5 15; -fx-font-weight: bold;");
        }
    }

    /**
     * Updates the BPM label.
     */
    private void updateBpmLabel() {
        bpmLabel.setText(String.format("%.1f", currentBpm));
    }

    /**
     * Draws the waveform on the canvas.
     */
    private void drawWaveform() {
        GraphicsContext gc = waveformCanvas.getGraphicsContext2D();
        double width = waveformCanvas.getWidth();
        double height = waveformCanvas.getHeight();
        double centerY = height / 2.0;

        // Clear canvas
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, width, height);

        // Draw center line
        gc.setStroke(Color.GRAY);
        gc.strokeLine(0, centerY, width, centerY);

        // Draw waveform
        gc.setStroke(Color.LIME);
        gc.setLineWidth(2.0);

        if (waveformHistory.size() > 1) {
            Double[] samples = waveformHistory.toArray(new Double[0]);
            double xStep = width / (double) maxWaveformSamples;
            
            for (int i = 1; i < samples.length; i++) {
                double x1 = (i - 1) * xStep;
                double x2 = i * xStep;
                double y1 = centerY - (samples[i - 1] * centerY);
                double y2 = centerY - (samples[i] * centerY);
                
                gc.strokeLine(x1, y1, x2, y2);
            }
        }
    }

    /**
     * Draws the spectrum bars on the canvas.
     */
    private void drawSpectrum() {
        GraphicsContext gc = spectrumCanvas.getGraphicsContext2D();
        double width = spectrumCanvas.getWidth();
        double height = spectrumCanvas.getHeight();

        // Clear canvas
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, width, height);

        // Draw spectrum bars
        if (currentSpectrum.length > 0) {
            double barWidth = width / currentSpectrum.length;
            
            for (int i = 0; i < currentSpectrum.length; i++) {
                double barHeight = currentSpectrum[i] * height;
                double x = i * barWidth;
                double y = height - barHeight;
                
                // Color gradient based on frequency (low = red, high = blue)
                double hue = (i / (double) currentSpectrum.length) * 240.0;
                Color barColor = Color.hsb(hue, 0.8, 0.9);
                
                gc.setFill(barColor);
                gc.fillRect(x, y, barWidth - 1, barHeight);
            }
        }
    }

    /**
     * Clears the waveform canvas.
     */
    private void clearWaveform() {
        GraphicsContext gc = waveformCanvas.getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, waveformCanvas.getWidth(), waveformCanvas.getHeight());
        
        // Draw center line
        double centerY = waveformCanvas.getHeight() / 2.0;
        gc.setStroke(Color.GRAY);
        gc.strokeLine(0, centerY, waveformCanvas.getWidth(), centerY);
    }

    /**
     * Clears the spectrum canvas.
     */
    private void clearSpectrum() {
        GraphicsContext gc = spectrumCanvas.getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, spectrumCanvas.getWidth(), spectrumCanvas.getHeight());
    }

    /**
     * Gets the current FFT size setting.
     *
     * @return the FFT size
     */
    public int getFftSize() {
        return (int) Math.pow(2, fftSizeSlider.getValue());
    }

    /**
     * Gets the current smoothing setting.
     *
     * @return the smoothing factor
     */
    public double getSmoothing() {
        return smoothingSlider.getValue();
    }

    /**
     * Gets the current gain setting.
     *
     * @return the gain value
     */
    public double getGain() {
        return gainSlider.getValue();
    }

    /**
     * Gets the current preset selection.
     *
     * @return the preset name
     */
    public String getPreset() {
        return presetComboBox.getValue();
    }

    /**
     * Resets the controller state.
     */
    public void reset() {
        waveformHistory.clear();
        currentSpectrum = new double[0];
        currentBeat = false;
        currentBpm = 0.0;
        beatDetector.reset();
        if (fftProcessor != null) {
            fftProcessor.reset();
        }
        Platform.runLater(() -> {
            clearWaveform();
            clearSpectrum();
            updateUI();
        });
    }
}
