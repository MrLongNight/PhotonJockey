# Refactor Plan for PhotonJockey

**Generated:** 2025-10-28
**Based on:** reports/metrics.json

This document outlines the top 10 classes/methods that should be refactored to improve code quality, maintainability, and testability.

## Methodology

Classes were analyzed based on:
- **Lines of Code (LOC)**: Large classes are harder to understand and maintain
- **Method Count**: High method count indicates potential for splitting
- **Concurrency Patterns**: Manual thread management and synchronization complexity
- **Complexity Score**: Weighted combination of above factors

## Refactoring Strategy

- **One class per PR**: Each refactoring will be done in a separate pull request
- **Small, incremental changes**: Minimize risk by making focused improvements
- **Test-first approach**: Add tests before refactoring when possible
- **Preserve behavior**: Ensure functionality remains unchanged

---

## 1. pw.wunderlich.lightbeat.gui.frame.MainFrame

### Metrics

- **Complexity Score:** 227
- **Lines of Code:** 516
- **Method Count:** 28
- **Thread Usage:** 0
- **Executor Services:** 0
- **Synchronized Blocks:** 0

### Summary

Large GUI class with many responsibilities

### Proposed Refactor Actions

**Branch:** `feature/TG1-refactor-mainframe`

1. Extract event handlers into separate handler classes
1. Extract UI component initialization to builder classes
1. Separate business logic from presentation logic
1. Consider Model-View-Presenter (MVP) pattern

### Risk Assessment

- ⚠️ GUI state management is complex
- ⚠️ Event listeners may have hidden dependencies
- ⚠️ UI layout changes may affect other frames

### Suggested Unit Tests

- `MainFrameInitializationTest - verify frame initializes correctly`
- `MainFrameStateTest - test button enable/disable logic`
- `MainFrameEventHandlingTest - verify event handlers work correctly`
- `MainFrameConfigurationTest - test config binding`

### Refactoring Priority

🔴 **HIGH** - Should be addressed soon

---

## 2. pw.wunderlich.lightbeat.audio.device.provider.WASAPIDeviceProvider

### Metrics

- **Complexity Score:** 96
- **Lines of Code:** 216
- **Method Count:** 17
- **Thread Usage:** 0
- **Executor Services:** 0
- **Synchronized Blocks:** 0

### Summary

Windows audio device provider with platform-specific code

### Proposed Refactor Actions

**Branch:** `feature/TG1-refactor-wasapideviceprovider`

1. Extract native interface to separate interface
1. Create factory for device creation
1. Separate device enumeration from device management
1. Add abstraction layer for OS-specific implementations

### Risk Assessment

- ⚠️ Platform-specific code may break if not tested on Windows
- ⚠️ Native code interaction is fragile
- ⚠️ Audio buffer handling is timing-sensitive

### Suggested Unit Tests

- `WASAPIDeviceProviderTest - mock native calls`
- `WASAPIDeviceEnumerationTest - test device discovery`
- `WASAPIBufferHandlingTest - verify audio buffer management`

### Refactoring Priority

🟡 **MEDIUM** - Address after high priority items

---

## 3. pw.wunderlich.lightbeat.gui.frame.ConnectFrame

### Metrics

- **Complexity Score:** 93
- **Lines of Code:** 210
- **Method Count:** 16
- **Thread Usage:** 0
- **Executor Services:** 0
- **Synchronized Blocks:** 0

### Summary

Connection UI with multiple concerns

### Proposed Refactor Actions

**Branch:** `feature/TG1-refactor-connectframe`

1. Extract bridge discovery logic to separate service
1. Create ConnectionManager for connection lifecycle
1. Separate UI updates from connection logic
1. Extract validation logic

### Risk Assessment

- ⚠️ Network operations may have timeouts
- ⚠️ UI updates must be on EDT
- ⚠️ Connection state transitions need careful handling

### Suggested Unit Tests

- `ConnectFrameUITest - verify UI components`
- `ConnectionManagerTest - test connection lifecycle`
- `BridgeDiscoveryServiceTest - mock network discovery`

### Refactoring Priority

🟡 **MEDIUM** - Address after high priority items

---

## 4. pw.wunderlich.lightbeat.gui.frame.ColorSelectionFrame

### Metrics

- **Complexity Score:** 90
- **Lines of Code:** 187
- **Method Count:** 15
- **Thread Usage:** 0
- **Executor Services:** 0
- **Synchronized Blocks:** 0

### Summary

Color selection UI with custom color management

### Proposed Refactor Actions

**Branch:** `feature/TG1-refactor-colorselectionframe`

1. Extract color preview generation to ColorPreviewRenderer
1. Create ColorSetManager for color set operations
1. Separate custom color persistence logic
1. Extract color validation logic

### Risk Assessment

- ⚠️ Color calculations may need color space conversions
- ⚠️ Custom color storage format may need migration
- ⚠️ UI repainting performance

### Suggested Unit Tests

- `ColorPreviewRendererTest - verify color rendering`
- `ColorSetManagerTest - test color set operations`
- `CustomColorPersistenceTest - test save/load`

### Refactoring Priority

🟡 **MEDIUM** - Address after high priority items

---

## 5. org.jitsi.impl.neomedia.device.PulseAudioSystem

### Metrics

- **Complexity Score:** 86
- **Lines of Code:** 490
- **Method Count:** 0
- **Thread Usage:** 0
- **Executor Services:** 0
- **Synchronized Blocks:** 0

### Summary

Linux audio system with JNI calls

### Proposed Refactor Actions

**Branch:** `feature/TG1-refactor-pulseaudiosystem`

1. Document JNI interface requirements
1. Add error handling for missing native libraries
1. Create fallback mechanism for unsupported platforms
1. Consider isolating in separate module

### Risk Assessment

- ⚠️ Native library dependencies
- ⚠️ Platform-specific behavior
- ⚠️ Memory management across JNI boundary

### Suggested Unit Tests

- `PulseAudioSystemTest - test with mocked native layer`
- `PulseAudioFallbackTest - verify graceful degradation`

### Refactoring Priority

🟡 **MEDIUM** - Address after high priority items

---

## 6. pw.wunderlich.lightbeat.audio.LBAudioReader

### Metrics

- **Complexity Score:** 82
- **Lines of Code:** 200
- **Method Count:** 9
- **Thread Usage:** 0
- **Executor Services:** 0
- **Synchronized Blocks:** 0

### Summary

Audio processing with multiple responsibilities

### Proposed Refactor Actions

**Branch:** `feature/TG1-refactor-lbaudioreader`

1. Extract FFT processing to separate FFTProcessor
1. Create AudioBufferManager for buffer handling
1. Separate beat detection from audio reading
1. Add AudioProcessingPipeline abstraction

### Risk Assessment

- ⚠️ Real-time audio processing is timing-critical
- ⚠️ Buffer size and sampling rate dependencies
- ⚠️ Thread synchronization for audio callbacks

### Suggested Unit Tests

- `FFTProcessorTest - test FFT with known signals`
- `AudioBufferManagerTest - verify buffer management`
- `LBAudioReaderIntegrationTest - end-to-end test with test audio`

### Refactoring Priority

🟡 **MEDIUM** - Address after high priority items

---

## 7. pw.wunderlich.lightbeat.hue.visualizer.BrightnessCalibrator

### Metrics

- **Complexity Score:** 73
- **Lines of Code:** 96
- **Method Count:** 10
- **Thread Usage:** 0
- **Executor Services:** 0
- **Synchronized Blocks:** 0

### Summary

Brightness calibration with stateful operations

### Proposed Refactor Actions

**Branch:** `feature/TG1-refactor-brightnesscalibrator`

1. Extract calibration algorithm to separate strategy
1. Create CalibrationDataCollector for data gathering
1. Separate UI feedback from calibration logic
1. Add CalibrationResult value object

### Risk Assessment

- ⚠️ Calibration depends on light hardware response time
- ⚠️ User interaction timing is variable
- ⚠️ Light state must be properly restored after calibration

### Suggested Unit Tests

- `BrightnessCalibrationAlgorithmTest - test with synthetic data`
- `CalibrationDataCollectorTest - verify data collection`
- `BrightnessCalibratorIntegrationTest - mock light responses`

### Refactoring Priority

🟢 **LOW** - Can be deferred

---

## 8. pw.wunderlich.lightbeat.gui.FrameManager

### Metrics

- **Complexity Score:** 70
- **Lines of Code:** 120
- **Method Count:** 11
- **Thread Usage:** 0
- **Executor Services:** 0
- **Synchronized Blocks:** 3

### Summary

GUI lifecycle management with synchronization

### Proposed Refactor Actions

**Branch:** `feature/TG1-refactor-framemanager`

1. Remove synchronized blocks by using SwingUtilities.invokeLater
1. Create FrameRegistry for frame tracking
1. Extract frame creation to factory
1. Add FrameLifecycleListener interface

### Risk Assessment

- ⚠️ EDT violations can cause deadlocks
- ⚠️ Frame disposal order matters
- ⚠️ Memory leaks from undisposed frames

### Suggested Unit Tests

- `FrameManagerTest - verify frame lifecycle`
- `FrameRegistryTest - test frame tracking`
- `FrameManagerThreadSafetyTest - verify EDT usage`

### Refactoring Priority

🟢 **LOW** - Can be deferred

---

## 9. pw.wunderlich.lightbeat.hue.bridge.LBHueManager

### Metrics

- **Complexity Score:** 68
- **Lines of Code:** 152
- **Method Count:** 12
- **Thread Usage:** 0
- **Executor Services:** 0
- **Synchronized Blocks:** 0

### Summary

Hue bridge management with multiple responsibilities

### Proposed Refactor Actions

**Branch:** `feature/TG1-refactor-lbhuemanager`

1. Extract light group management to LightGroupManager
1. Create BridgeConnectionPool for connection handling
1. Separate light state synchronization logic
1. Add HueEventBus for decoupled event handling

### Risk Assessment

- ⚠️ Network latency affects responsiveness
- ⚠️ API rate limiting from Hue bridge
- ⚠️ Light state consistency across operations

### Suggested Unit Tests

- `LightGroupManagerTest - test group operations`
- `BridgeConnectionPoolTest - verify connection pooling`
- `LBHueManagerTest - mock bridge API calls`

### Refactoring Priority

🟢 **LOW** - Can be deferred

---

## 10. pw.wunderlich.lightbeat.audio.device.provider.JavaAudioDeviceProvider

### Metrics

- **Complexity Score:** 66
- **Lines of Code:** 125
- **Method Count:** 10
- **Thread Usage:** 0
- **Executor Services:** 0
- **Synchronized Blocks:** 0

### Summary

Java Sound API integration

### Proposed Refactor Actions

**Branch:** `feature/TG1-refactor-javaaudiodeviceprovider`

1. Extract device capability detection
1. Create AudioFormat validator
1. Separate device enumeration from initialization
1. Add DeviceCapabilities value object

### Risk Assessment

- ⚠️ Java Sound API quirks vary by platform
- ⚠️ Audio format support differs by device
- ⚠️ Device availability changes at runtime

### Suggested Unit Tests

- `AudioFormatValidatorTest - test format validation`
- `DeviceCapabilityDetectorTest - mock device queries`
- `JavaAudioDeviceProviderTest - test device enumeration`

### Refactoring Priority

🟢 **LOW** - Can be deferred

---

## Summary

### Priority Order

#### High Priority (Complexity > 150)
- pw.wunderlich.lightbeat.gui.frame.MainFrame

#### Medium Priority (Complexity 80-150)
- pw.wunderlich.lightbeat.audio.device.provider.WASAPIDeviceProvider
- pw.wunderlich.lightbeat.gui.frame.ConnectFrame
- pw.wunderlich.lightbeat.gui.frame.ColorSelectionFrame
- org.jitsi.impl.neomedia.device.PulseAudioSystem
- pw.wunderlich.lightbeat.audio.LBAudioReader

#### Low Priority (Complexity < 80)
- pw.wunderlich.lightbeat.hue.visualizer.BrightnessCalibrator
- pw.wunderlich.lightbeat.gui.FrameManager
- pw.wunderlich.lightbeat.hue.bridge.LBHueManager
- pw.wunderlich.lightbeat.audio.device.provider.JavaAudioDeviceProvider

### Next Steps

1. Review this refactor plan with the team
2. Prioritize which classes to refactor first
3. Create feature branches for each refactoring (one per PR)
4. Add tests before refactoring when possible
5. Perform refactoring incrementally
6. Review and merge each PR independently

### Notes

- Each refactoring should be done in a separate PR to minimize risk
- Tests should be added before or during refactoring
- Behavior should remain unchanged after refactoring
- Code reviews are essential for each refactoring
