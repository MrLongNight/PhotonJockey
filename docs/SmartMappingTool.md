# Smart Mapping Tool

## Overview

The Smart Mapping Tool is a JavaFX-based UI application for configuring and managing Philips Hue light mappings in PhotonJockey. It provides an interactive visual interface for positioning lights on a 2D canvas and assigning bridge and control type configurations.

## Features

### Visual Light Mapping
- **Interactive Canvas**: 960x500 pixel canvas with grid background for precise light positioning
- **Drag & Drop**: Drag lights on the canvas to set their spatial position
- **Visual Feedback**: 
  - Blue circles for FAST_UDP lights
  - Orange circles for LOW_HTTP lights
  - Green selection indicator for selected light
  - Light labels showing name or ID

### Configuration Management
- **Load Configuration**: Import existing `lightmap.json` files
- **Save Configuration**: Export configuration to JSON format
- **Bridge Management**: Add Philips Hue bridges with ID and IP address
- **Light Management**: Add, remove, and update light configurations

### Light Properties
- **Light ID**: Unique identifier for each light
- **Light Name**: Human-readable label
- **Bridge ID**: Reference to parent Hue bridge
- **Control Type**: 
  - `FAST_UDP`: For low-latency entertainment API control
  - `LOW_HTTP`: For standard HTTP API control
- **Position**: X/Y coordinates on the canvas (set via drag & drop)

### Live Testing
- **Test Effect**: Create and simulate effect routing through the configured lights
- **Preview**: Verify light configurations before deployment

## Usage

### Running the Application

#### Standard Demo (Empty Configuration)
```bash
./gradlew run -PmainClass=io.github.mrlongnight.photonjockey.ui.SmartMappingToolDemo
```

#### Demo with Sample Data
```bash
./gradlew run -PmainClass=io.github.mrlongnight.photonjockey.ui.SmartMappingToolDemoWithData
```

### Basic Workflow

1. **Add a Bridge**
   - Enter Bridge ID (e.g., "bridge-1")
   - Enter Bridge IP (e.g., "192.168.1.100")
   - Click "Add Bridge"

2. **Add Lights**
   - Select a bridge from the dropdown
   - Click "Add Light" to create a new light at canvas center
   - Drag the light to desired position

3. **Configure Light Properties**
   - Select a light from the list or canvas
   - Update ID, Name, Bridge ID, and Control Type
   - Click "Update Light" to save changes

4. **Save Configuration**
   - Click "Save Configuration"
   - Choose file location (default: `lightmap.json`)
   - Configuration is saved in JSON format

5. **Load Configuration**
   - Click "Load Configuration"
   - Select an existing `lightmap.json` file
   - Configuration and visual layout are restored

## Configuration Format

The tool saves configurations in JSON format compatible with the PhotonJockey lightmap schema:

```json
{
  "bridges": [
    {
      "id": "bridge-1",
      "ip": "192.168.1.100",
      "username": "user123"
    }
  ],
  "lights": [
    {
      "id": "light-1",
      "x": 150.0,
      "y": 150.0,
      "bridgeId": "bridge-1",
      "controlType": "FAST_UDP",
      "name": "Living Room Left"
    }
  ]
}
```

## Architecture

### Components

- **SmartMappingTool.fxml**: JavaFX UI layout definition
- **SmartMappingToolController.java**: Main controller with business logic
- **SmartMappingToolDemo.java**: Standalone demo application
- **SmartMappingToolDemoWithData.java**: Demo with pre-loaded sample data

### Integration Points

- **LightMapConfig**: DTO for configuration structure
- **BridgeConfig**: DTO for Hue bridge configuration
- **LightConfig**: DTO for individual light configuration
- **EffectRouter**: Routes effect frames to appropriate controllers
- **JsonUtils**: JSON serialization/deserialization utilities

## Testing

### Unit Tests

The tool includes comprehensive TestFX unit tests:

- **SmartMappingToolControllerUnitTest.java**: 
  - Configuration management
  - Save/Load operations
  - Round-trip serialization
  - Multi-light configurations

- **SmartMappingToolDragDropTest.java**:
  - Drag and drop positioning
  - Canvas boundary clamping
  - Multiple light drag operations
  - Click detection accuracy

### Running Tests

```bash
./gradlew test --tests "*SmartMappingTool*"
```

**Note**: UI tests may timeout in headless CI environments. Manual testing is recommended for full validation.

## Keyboard Shortcuts

- **Click**: Select a light
- **Drag**: Reposition a light
- **Ctrl+S**: Save configuration (if implemented)
- **Ctrl+O**: Open configuration (if implemented)

## Technical Details

### Canvas Rendering
- Grid: 50-pixel spacing
- Light radius: 10 pixels
- Selection indicator: 15 pixels
- Dark theme: #1a1a1a background

### Control Types
- **FAST_UDP**: Entertainment API with ~20ms latency
- **LOW_HTTP**: Standard REST API with ~100ms latency

### Threading
- All UI updates run on JavaFX Application Thread
- Status updates use `Platform.runLater()`
- Canvas redraws are synchronized

## Future Enhancements

Potential features for future versions:
- Undo/redo functionality
- Multi-select and bulk operations
- Canvas zoom and pan
- Import from Hue bridge discovery
- Real-time bridge connectivity testing
- Animation preview
- Grid snapping toggle
- Keyboard shortcuts for common operations
- Configuration templates

## Troubleshooting

### UI Not Loading
- Ensure JavaFX modules are available
- Check Java version (requires Java 21)
- Verify FXML file is in resources

### Drag & Drop Not Working
- Check mouse event handlers are registered
- Ensure light is within hit radius (15 pixels)
- Verify canvas bounds are correct

### Save/Load Failures
- Check file permissions
- Validate JSON format
- Ensure parent directories exist
- Check for special characters in paths

## License

Part of the PhotonJockey project. See main project LICENSE for details.
