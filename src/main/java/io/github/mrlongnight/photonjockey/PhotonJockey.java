package io.github.mrlongnight.photonjockey;

import io.github.mrlongnight.photonjockey.audio.AudioReader;
import io.github.mrlongnight.photonjockey.audio.BeatEventManager;
import io.github.mrlongnight.photonjockey.audio.PJAudioReader;
import io.github.mrlongnight.photonjockey.audio.BeatEventManager;
import io.github.mrlongnight.photonjockey.audio.PJAudioReader;
import io.github.mrlongnight.photonjockey.config.Config;
import io.github.mrlongnight.photonjockey.config.PJConfig;
import io.github.mrlongnight.photonjockey.hue.bridge.HueManager;
import io.github.mrlongnight.photonjockey.hue.bridge.PJHueManager;
import io.github.mrlongnight.photonjockey.ui.UnifiedDashboard;
import javafx.application.Application;

public class PhotonJockey {

    public static void main(String[] args) {
        // Initialize core components
        Config config = new PJConfig();
        AppTaskOrchestrator taskOrchestrator = new AppTaskOrchestrator();
        PJAudioReader audioReader = new PJAudioReader(config, taskOrchestrator);
        BeatEventManager beatEventManager = audioReader;
        PJHueManager hueManager = new PJHueManager(config, taskOrchestrator);

        // REMOVED: Old Swing UI
        // new MainFrame(config, taskOrchestrator, audioReader, beatEventManager, hueManager, 100, 100);

        // Initialize and start the unified JavaFX UI
        UnifiedDashboard.init(config, taskOrchestrator, audioReader, hueManager);
        Application.launch(UnifiedDashboard.class, args);
    }
}
