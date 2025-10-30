# Audio Visualizer Dashboard

## Overview

The Audio Visualizer Dashboard provides real-time visualization of audio analysis data including waveforms, frequency spectrum, and beat detection. It's designed to work with the PhotonJockey audio analysis pipeline.

## Components

### UI Elements

#### Waveform View
- **Type**: Canvas (780x150 pixels)
- **Purpose**: Displays the time-domain audio waveform
- **Update Rate**: Real-time as audio frames are received
- **Visualization**: Line graph showing audio sample amplitudes

#### Frequency Spectrum
- **Type**: Canvas (780x150 pixels)
- **Purpose**: Displays the frequency-domain spectrum analysis
- **Visualization**: 64 vertical bars representing frequency bins
- **Features**: Bass frequencies emphasized on the left, higher frequencies on the right

#### Beat Indicator
- **Type**: Circle (40px radius)
- **States**: 
  - Gray (#444444): No beat detected
  - Green (#00ff00): Beat detected
- **BPM Display**: Shows current estimated beats per minute

#### Controls

##### Gain Slider
- **Range**: 0.0 to 2.0
- **Default**: 1.0
- **Purpose**: Controls the amplitude/scaling of visualizations
- **Effect**: Amplifies or reduces the displayed waveform and spectrum intensity

##### Beat Sensitivity Slider
- **Range**: 0.5 to 2.0
- **Default**: 1.3
- **Purpose**: Adjusts the threshold for beat detection
- **Effect**: 
  - Lower values: More sensitive (more beats detected)
  - Higher values: Less sensitive (fewer beats detected)

## Usage

### Integration in Code

```java
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pw.wunderlich.lightbeat.ui.AudioAnalyzerDashboardController;
import pw.wunderlich.lightbeat.audio.AudioFrame;

// Load the FXML
FXMLLoader loader = new FXMLLoader(
    getClass().getResource("/fxml/AudioAnalyzerDashboard.fxml")
);
Parent root = loader.load();
AudioAnalyzerDashboardController controller = loader.getController();

// Create scene and show
Scene scene = new Scene(root, 800, 600);
stage.setScene(scene);
stage.show();

// Update with audio data
AudioFrame frame = ...; // Your audio frame
controller.updateWaveform(frame);

double[] spectrum = ...; // Your spectrum data
controller.updateSpectrum(spectrum);

boolean isBeat = ...; // Beat detection result
double bpm = ...; // Current BPM
controller.updateBeatIndicator(isBeat, bpm);
```

### Running the Demo

A demo application is provided to see the visualizer in action with simulated data:

```bash
./gradlew run -PmainClass=pw.wunderlich.lightbeat.ui.AudioAnalyzerDashboardDemo
```

Or with Java directly:

```bash
java -cp build/libs/PhotonJockey-shadow.jar \
     pw.wunderlich.lightbeat.ui.AudioAnalyzerDashboardDemo
```

## API Reference

### AudioAnalyzerDashboardController

#### Methods

##### updateWaveform(AudioFrame frame)
Updates the waveform visualization with new audio data.
- **Parameters**: `frame` - Audio frame containing sample data
- **Thread-Safe**: Yes (uses Platform.runLater)

##### updateSpectrum(double[] spectrum)
Updates the frequency spectrum visualization.
- **Parameters**: `spectrum` - Array of frequency bin magnitudes
- **Thread-Safe**: Yes (uses Platform.runLater)

##### updateBeatIndicator(boolean isBeat, double bpm)
Updates the beat indicator and BPM display.
- **Parameters**: 
  - `isBeat` - True if beat detected
  - `bpm` - Current beats per minute
- **Thread-Safe**: Yes (uses Platform.runLater)

##### getGain() / setGain(double gain)
Get or set the gain value.
- **Range**: 0.0 to 2.0

##### getBeatSensitivity() / setBeatSensitivity(double sensitivity)
Get or set the beat sensitivity threshold.
- **Range**: 0.5 to 2.0

##### clear()
Clears all visualizations and resets displays.
- **Thread-Safe**: Yes (uses Platform.runLater)

## Architecture

### Thread Safety

All visualization update methods use `javafx.application.Platform.runLater()` to ensure thread-safe updates from non-JavaFX threads. This allows audio processing threads to update the UI safely.

### Data Flow

```
Audio Source → AudioFrame → updateWaveform()
                ↓
         FFT Processor → Spectrum → updateSpectrum()
                ↓
         Beat Detector → (isBeat, BPM) → updateBeatIndicator()
```

### Performance Considerations

- Canvas-based rendering for efficient graphics
- Double buffering via GraphicsContext
- Update rate limited by calling frequency (recommended: 20-60 FPS)
- Waveform downsampling to canvas width for efficiency

## Testing

### Unit Tests

Run the unit tests:

```bash
./gradlew test --tests "pw.wunderlich.lightbeat.ui.AudioAnalyzerDashboardControllerUnitTest"
```

### Manual Testing

Use the demo application for manual verification:

```bash
./gradlew run -PmainClass=pw.wunderlich.lightbeat.ui.AudioAnalyzerDashboardDemo
```

The demo simulates audio data with:
- Synthetic waveforms (sine waves with noise)
- Random frequency spectrum with bass emphasis
- Periodic beat detection every 30 frames

## Requirements

- Java 21 or higher
- JavaFX 21.0.1
- Gradle 9.0+

## Dependencies

```gradle
javafx {
    version = "21.0.1"
    modules = ['javafx.controls', 'javafx.fxml', 'javafx.swing']
}
```

## Files

- **FXML**: `src/main/resources/fxml/AudioAnalyzerDashboard.fxml`
- **Controller**: `src/main/java/pw/wunderlich/lightbeat/ui/AudioAnalyzerDashboardController.java`
- **Demo**: `src/main/java/pw/wunderlich/lightbeat/ui/AudioAnalyzerDashboardDemo.java`
- **Tests**: `src/test/java/pw/wunderlich/lightbeat/ui/AudioAnalyzerDashboardControllerUnitTest.java`

## Future Enhancements

Potential improvements:
- Configurable color themes
- Export visualization as image/video
- Multiple visualization modes (spectrogram, 3D spectrum)
- Audio recording integration
- FFT size configuration
- Frequency range selection
- Peak frequency labeling
- Beat history visualization
