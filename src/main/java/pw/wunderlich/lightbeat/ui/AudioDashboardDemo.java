package pw.wunderlich.lightbeat.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pw.wunderlich.lightbeat.audio.AnalysisResult;

import java.util.Random;

/**
 * Demo application for the Audio Dashboard UI.
 * This class demonstrates how to use the AudioDashboardController
 * and can be used for manual testing and development.
 */
public class AudioDashboardDemo extends Application {

    private AudioDashboardController controller;
    private Thread simulationThread;
    private volatile boolean running;

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load FXML
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/ui/audio_dashboard.fxml")
        );
        Parent root = loader.load();
        controller = loader.getController();

        // Setup scene
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Audio Dashboard Demo");
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(event -> stopSimulation());
        primaryStage.show();

        // Start simulating audio analysis data
        startSimulation();
    }

    /**
     * Starts the audio analysis simulation thread.
     */
    private void startSimulation() {
        running = true;
        simulationThread = new Thread(() -> {
            Random random = new Random();
            double phase = 0.0;
            int beatCounter = 0;

            while (running) {
                try {
                    // Simulate audio analysis with varying parameters
                    phase += 0.05;
                    beatCounter++;

                    // Generate simulated frequency (oscillating between 100-800 Hz)
                    double frequency = 400.0 + (Math.sin(phase) * 300.0);

                    // Generate simulated amplitude (oscillating)
                    double amplitude = 0.5 + (Math.sin(phase * 2) * 0.3);

                    // Generate simulated energy with occasional spikes (beats)
                    double energy;
                    if (beatCounter % 20 == 0) {
                        // Simulate a beat
                        energy = 0.8 + (random.nextDouble() * 0.5);
                    } else {
                        // Normal energy level
                        energy = 0.2 + (random.nextDouble() * 0.2);
                    }

                    // Create and send analysis result
                    AnalysisResult result = new AnalysisResult(frequency, amplitude, energy);
                    controller.onAnalysis(result);

                    // Sleep to simulate frame rate (~60 FPS)
                    Thread.sleep(16);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        simulationThread.setDaemon(true);
        simulationThread.start();
    }

    /**
     * Stops the audio analysis simulation.
     */
    private void stopSimulation() {
        running = false;
        if (simulationThread != null) {
            simulationThread.interrupt();
        }
    }

    @Override
    public void stop() {
        stopSimulation();
    }

    /**
     * Main entry point for the demo application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
