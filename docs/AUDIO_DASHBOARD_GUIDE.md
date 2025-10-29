# Audio Dashboard Quick Start Guide

## Overview

The Audio Dashboard is a JavaFX-based real-time audio visualization component that displays:
- Waveform (time-domain)
- Frequency spectrum (frequency-domain)
- Beat detection indicator
- BPM (beats per minute) tracking

## File Structure

```
PhotonJockey/
├── src/main/
│   ├── java/pw/wunderlich/lightbeat/ui/
│   │   ├── AudioDashboardController.java    (Main controller)
│   │   ├── AudioDashboardDemo.java          (Demo application)
│   │   └── README.md                         (Package documentation)
│   └── resources/ui/
│       └── audio_dashboard.fxml             (UI layout)
├── src/test/java/pw/wunderlich/lightbeat/ui/
│   └── AudioDashboardControllerTest.java    (15 test cases)
└── AUDIO_DASHBOARD_IMPLEMENTATION.md        (Requirement mapping)
```

## Quick Start

### Running the Demo

```bash
# Compile and run the demo application
./gradlew run -PmainClass=pw.wunderlich.lightbeat.ui.AudioDashboardDemo
```

The demo will display the dashboard with simulated audio data including periodic beats.

### Integration Example

```java
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pw.wunderlich.lightbeat.ui.AudioDashboardController;
import pw.wunderlich.lightbeat.audio.AnalysisResult;

public class MyApp extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        // Load the FXML
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/ui/audio_dashboard.fxml")
        );
        Parent root = loader.load();
        AudioDashboardController controller = loader.getController();
        
        // Display the UI
        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.show();
        
        // In your audio processing loop:
        // controller.onAnalysis(analysisResult);
    }
}
```

## UI Components

### Visualization Areas

1. **Waveform Canvas (Top)**
   - Displays last 500 samples of amplitude data
   - Green line on black background
   - Updates in real-time

2. **Spectrum Canvas (Middle)**
   - Shows 64 frequency bins
   - Color gradient from red (low freq) to blue (high freq)
   - Bar height represents magnitude

3. **Beat Indicator**
   - Shows "YES" (green) when beat is detected
   - Shows "NO" (gray) otherwise
   - Based on energy threshold detection

4. **BPM Label**
   - Displays current beats per minute
   - Calculated from recent beat intervals
   - Resets if no beats for 3 seconds

### Control Sliders

1. **FFT Size**: 128 to 8192
   - Controls frequency resolution
   - Higher = better frequency resolution
   - Lower = better time resolution

2. **Smoothing**: 0.0 to 1.0
   - Controls temporal smoothing
   - 0.0 = no smoothing (fast response)
   - 1.0 = maximum smoothing (stable display)

3. **Gain**: 0.1 to 5.0
   - Amplifies visualization
   - Use higher values for quiet signals
   - Use lower values for loud signals

### Presets

Quick configurations for common use cases:

- **Default**: Balanced settings (FFT=2048, Smoothing=0.5, Gain=1.0)
- **High Sensitivity**: Fast response (FFT=4096, Smoothing=0.3, Gain=2.0)
- **Low Sensitivity**: Stable display (FFT=1024, Smoothing=0.7, Gain=0.5)
- **Bass Focus**: Low frequencies (FFT=8192, Smoothing=0.6, Gain=1.5)
- **Treble Focus**: High frequencies (FFT=1024, Smoothing=0.4, Gain=1.5)

## API Reference

### AudioDashboardController

#### Methods

```java
// Called for each audio analysis result
public void onAnalysis(AnalysisResult result)

// Get current settings
public int getFftSize()
public double getSmoothing()
public double getGain()
public String getPreset()

// Reset to initial state
public void reset()
```

#### Thread Safety

The `onAnalysis()` method can be called from any thread. UI updates are automatically
performed on the JavaFX application thread using `Platform.runLater()`.

## Testing

Run all tests:
```bash
./gradlew test --tests "pw.wunderlich.lightbeat.ui.*"
```

Test coverage includes:
- UI initialization
- Real-time updates
- Slider interactions
- Preset selection
- Edge cases (null, extreme values)
- Beat detection
- BPM calculation

## Performance

- Optimized for 60 FPS update rate
- Efficient canvas rendering
- Bounded history buffers (no memory growth)
- No blocking operations in UI thread

## Troubleshooting

### Issue: Beat indicator never shows "YES"
**Solution:** Check that energy values in AnalysisResult are sufficiently high and varied.
The beat detector looks for energy spikes above 1.3x the average.

### Issue: Waveform appears flat
**Solution:** Increase the gain slider or check that amplitude values are non-zero.

### Issue: Spectrum appears empty
**Solution:** Ensure frequency and energy values are positive and non-zero.

### Issue: UI freezes
**Solution:** Verify you're not calling UI methods directly from non-UI threads.
Use the provided `onAnalysis()` method which handles thread safety.

## Architecture

```
Audio Source
    ↓
IAudioAnalyzer.analyze(AudioFrame)
    ↓
AnalysisResult (frequency, amplitude, energy)
    ↓
AudioDashboardController.onAnalysis(result)
    ↓
    ├─→ Update waveform history
    ├─→ Update spectrum data
    ├─→ Process beat detection (BeatDetector)
    ├─→ Calculate BPM
    └─→ Trigger UI refresh (Platform.runLater)
        ↓
        ├─→ Draw waveform (Canvas)
        ├─→ Draw spectrum (Canvas)
        ├─→ Update beat indicator (Label)
        └─→ Update BPM display (Label)
```

## Further Reading

- `src/main/java/pw/wunderlich/lightbeat/ui/README.md` - Detailed package documentation
- `AUDIO_DASHBOARD_IMPLEMENTATION.md` - Complete requirement mapping
- `AudioDashboardDemo.java` - Working example implementation
- `AudioDashboardControllerTest.java` - Test examples

## Support

For issues or questions about the Audio Dashboard implementation, refer to:
1. The test cases for usage examples
2. The demo application for integration patterns
3. The package README for API details
