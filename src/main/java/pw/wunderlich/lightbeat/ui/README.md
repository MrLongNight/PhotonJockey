# Audio Dashboard UI

This package contains JavaFX UI components for visualizing audio analysis in real-time.

## Components

### AudioDashboardController

The main controller for the audio dashboard UI. It provides:

- **Waveform Visualization**: Displays the last N milliseconds of audio amplitude data
- **Spectrum Visualization**: Shows frequency spectrum bars derived from FFT analysis
- **Beat Detection**: Visual indicator showing when beats are detected
- **BPM Display**: Shows the current estimated beats per minute
- **Control Sliders**:
  - FFT Size: Adjusts the FFT window size (128-8192 samples)
  - Smoothing: Controls temporal smoothing of spectrum (0.0-1.0)
  - Gain: Adjusts visualization gain/amplification (0.1-5.0)
- **Presets**: Quick-select configurations for different use cases

### Usage

To integrate the Audio Dashboard into your audio processing pipeline:

```java
// Load the FXML
FXMLLoader loader = new FXMLLoader(
    getClass().getResource("/ui/audio_dashboard.fxml")
);
Parent root = loader.load();
AudioDashboardController controller = loader.getController();

// In your audio processing loop, call controller.onAnalysis() with each result:
while (processing) {
    AudioFrame frame = getNextAudioFrame();
    AnalysisResult result = audioAnalyzer.analyze(frame);
    controller.onAnalysis(result);
}
```

### Demo Application

Run `AudioDashboardDemo` to see the dashboard in action with simulated audio data:

```bash
./gradlew run -PmainClass=pw.wunderlich.lightbeat.ui.AudioDashboardDemo
```

## Testing

The package includes comprehensive TestFX tests that verify:

- UI initialization and default values
- Real-time updates from analysis events
- User interactions with controls
- Beat detection visualization
- BPM calculation display
- Preset application

Run tests with:

```bash
./gradlew test --tests "pw.wunderlich.lightbeat.ui.*"
```

## Architecture

The controller follows the observer pattern:

1. **onAnalysis(AnalysisResult)** method receives audio analysis data
2. Internal state is updated (waveform history, spectrum data, beat detection)
3. UI updates are performed on the JavaFX application thread via Platform.runLater()
4. Canvas elements are redrawn with the latest visualization data

## Customization

### Presets

Presets can be customized in the `applyPreset()` method. Current presets:

- **Default**: Balanced settings (FFT=2048, Smoothing=0.5, Gain=1.0)
- **High Sensitivity**: More responsive (FFT=4096, Smoothing=0.3, Gain=2.0)
- **Low Sensitivity**: More stable (FFT=1024, Smoothing=0.7, Gain=0.5)
- **Bass Focus**: Emphasizes low frequencies (FFT=8192, Smoothing=0.6, Gain=1.5)
- **Treble Focus**: Emphasizes high frequencies (FFT=1024, Smoothing=0.4, Gain=1.5)

### Visual Styling

The FXML file can be modified to change colors, sizes, and layout. The controller's drawing
methods can be customized to change visualization styles.

## Dependencies

- JavaFX 21.0.1 (controls, fxml)
- TestFX 4.0.18 (testing only)

## Integration with IAudioAnalyzer

The controller is designed to work with the `IAudioAnalyzer` interface. To create a complete
integration:

1. Implement or use an existing `IAudioAnalyzer`
2. Call `controller.onAnalysis(result)` whenever new analysis data is available
3. Typically this would be done at the same frame rate as your audio processing (e.g., 60 Hz)

Example:

```java
public class MyAudioProcessor implements IAudioAnalyzer {
    private AudioDashboardController dashboardController;
    
    public void setDashboardController(AudioDashboardController controller) {
        this.dashboardController = controller;
    }
    
    @Override
    public AnalysisResult analyze(AudioFrame frame) {
        AnalysisResult result = // ... perform analysis
        
        if (dashboardController != null) {
            dashboardController.onAnalysis(result);
        }
        
        return result;
    }
}
```
