package io.github.mrlongnight.photonjockey;

import io.github.mrlongnight.photonjockey.audio.AudioReader;
import io.github.mrlongnight.photonjockey.audio.BeatEventManager;
import io.github.mrlongnight.photonjockey.audio.PJAudioReader;
import io.github.mrlongnight.photonjockey.config.Config;
import io.github.mrlongnight.photonjockey.config.PJConfig;
import io.github.mrlongnight.photonjockey.gui.frame.MainFrame;
import io.github.mrlongnight.photonjockey.hue.bridge.HueManager;
import io.github.mrlongnight.photonjockey.hue.bridge.PJHueManager;
import io.github.mrlongnight.photonjockey.ui.AudioAnalyzerDashboard;
import javafx.application.Application;

public class PhotonJockey {

    public static void main(String[] args) {
        // Initialize core components
        Config config = new PJConfig();
        AppTaskOrchestrator taskOrchestrator = new AppTaskOrchestrator();
        AudioReader audioReader = new PJAudioReader(config, taskOrchestrator);
        BeatEventManager beatEventManager = (BeatEventManager) audioReader;
        HueManager hueManager = new PJHueManager(config, taskOrchestrator);

        // Start the old Swing UI
        new MainFrame(config, taskOrchestrator, audioReader, beatEventManager, hueManager, 100, 100);

        // Initialize and start the new JavaFX UI
        AudioAnalyzerDashboard.init(config, taskOrchestrator, (PJAudioReader) audioReader);
        Application.launch(AudioAnalyzerDashboard.class, args);
    }
}
