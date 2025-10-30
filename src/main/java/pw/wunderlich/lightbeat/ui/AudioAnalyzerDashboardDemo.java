package pw.wunderlich.lightbeat.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pw.wunderlich.lightbeat.audio.AudioFrame;

import java.net.URL;
import java.util.Random;

/**
 * Demo application for AudioAnalyzerDashboard.
 * Simulates audio data visualization for testing purposes.
 */
public class AudioAnalyzerDashboardDemo extends Application {

    private AudioAnalyzerDashboardController controller;
    private boolean running = true;
    private Random random = new Random();

    @Override
    public void start(Stage primaryStage) throws Exception {
        URL fxmlUrl = getClass().getResource("/fxml/AudioAnalyzerDashboard.fxml");
        FXMLLoader loader = new FXMLLoader(fxmlUrl);
        Parent root = loader.load();
        controller = loader.getController();

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Audio Analyzer Dashboard Demo");
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(e -> running = false);
        primaryStage.show();

        // Start simulation thread
        startSimulation();
    }

    private void startSimulation() {
        Thread simulationThread = new Thread(() -> {
            int frameCount = 0;
            while (running) {
                try {
                    // Generate simulated waveform data
                    byte[] waveformData = generateWaveform(frameCount);
                    AudioFrame frame = new AudioFrame(waveformData, 44100, 1, System.currentTimeMillis());
                    controller.updateWaveform(frame);

                    // Generate simulated spectrum data
                    double[] spectrum = generateSpectrum(frameCount);
                    controller.updateSpectrum(spectrum);

                    // Simulate beat detection
                    boolean isBeat = (frameCount % 30 == 0);
                    double bpm = 120.0 + random.nextInt(20);
                    controller.updateBeatIndicator(isBeat, bpm);

                    frameCount++;
                    Thread.sleep(50); // Update at ~20 FPS
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        simulationThread.setDaemon(true);
        simulationThread.start();
    }

    private byte[] generateWaveform(int frameCount) {
        byte[] data = new byte[1024];
        for (int i = 0; i < data.length; i += 2) {
            // Generate sine wave with some noise
            double t = (frameCount * 1024 + i) / 100.0;
            double value = Math.sin(t) * 0.7 + Math.sin(t * 2.5) * 0.3;
            value += (random.nextDouble() - 0.5) * 0.1;
            
            short sample = (short) (value * 16384);
            data[i] = (byte) (sample & 0xFF);
            data[i + 1] = (byte) ((sample >> 8) & 0xFF);
        }
        return data;
    }

    private double[] generateSpectrum(int frameCount) {
        double[] spectrum = new double[64];
        for (int i = 0; i < spectrum.length; i++) {
            // Simulate frequency spectrum with bass emphasis
            double bassBoost = Math.max(0, 1.0 - i / 32.0);
            double energy = random.nextDouble() * 0.5 + 0.2;
            spectrum[i] = energy * (bassBoost * 0.5 + 0.5);
            
            // Add some variation based on frame count
            spectrum[i] *= (1.0 + Math.sin(frameCount / 10.0 + i / 10.0) * 0.3);
        }
        return spectrum;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
