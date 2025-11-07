# UI Enhancements - Dynamic & Responsive Design

This document describes the UI enhancements implemented in PhotonJockey to provide a more flexible and customizable user interface.

## 1. Control Style Options

Users can now choose from three different visual styles for value controls (sliders):

### Available Styles

1. **Slider** (Default)
   - Traditional horizontal slider control
   - Shows current value and allows precise adjustment via dragging

2. **Knob**
   - Rotary knob control inspired by physical mixing boards
   - Visual arc shows the current value
   - Drag up/down to adjust value
   - Compact design, ideal for panel-heavy layouts

3. **Display**
   - Read-only value display with virtual LED display appearance
   - Useful for monitoring values without accidental changes
   - Retro digital display aesthetic

### How to Change Control Style

1. Open **Settings** from the File menu
2. Select your preferred **Control Style** from the dropdown
3. Click **Save**
4. The new style will be applied to all compatible controls

### Implementation Details

- **ControlStyle enum**: Defines the three available styles
- **RotaryKnob**: Custom JavaFX control for knob-style interaction
- **ValueDisplay**: Display-only control with LED-style appearance
- **ConfigurableValueControl**: Wrapper that can switch between styles

Configuration is stored in `UI_CONTROL_STYLE` config node.

## 2. Drag & Drop Layout Customization

PhotonJockey now supports custom layout arrangements where users can reorganize UI panels to suit their workflow.

### Features

- **Drag and Drop**: Move panels to different positions
- **Collapse/Expand**: Minimize panels you don't need
- **Hide/Show**: Completely hide panels from view
- **Persistent Layout**: Your layout is saved and restored between sessions

### How to Use

1. **Enable Layout Customization**
   - Go to **View → Enable Layout Customization**
   - Panels will show drag handles in their headers

2. **Reorganize Panels**
   - Click and drag the drag handle (⋮⋮) to move a panel
   - Drop it in the desired position

3. **Collapse a Panel**
   - Click the **−** button in the panel header
   - Click **+** to expand again

4. **Hide a Panel**
   - Click the **✕** button in the panel header
   - To show hidden panels, reset the layout (see below)

5. **Disable Layout Customization**
   - Go to **View → Disable Layout Customization**
   - Drag handles will be hidden, layout is locked

6. **Reset to Default**
   - Go to **View → Reset Layout to Default**
   - All panels return to original positions and visibility

### Implementation Details

- **DraggablePanel**: Individual panel component with drag handle and controls
- **DraggableLayoutContainer**: Container managing panel positions and persistence
- Layout configuration stored in `UI_LAYOUT_CUSTOMIZATION` and `UI_PANEL_VISIBILITY` config nodes
- Uses JSON serialization for saving panel order and visibility states

## 3. Responsive Design Enhancements

The UI now better adapts to different window sizes and screen resolutions.

### Features

- **Minimum Window Size**: Dashboard has minimum dimensions (900x600) to ensure usability
- **Flexible Layouts**: Panels automatically adjust to available space
- **FlowPane Wrapping**: Control panels wrap to next line when window is too narrow
- **Scrollable Sections**: Long content areas become scrollable rather than clipped

### Technical Implementation

- Added `minWidth` and `minHeight` properties to FXML layouts
- Enhanced CSS with responsive visual states
- TitledPane components can expand/collapse to manage space
- ScrollPanes with `fitToWidth` for dynamic content sizing

## CSS Styling

New CSS classes have been added to support the new features:

### Control Styles
- `.value-display-name`: Label for value display controls
- `.value-display-box`: Container for LED-style display
- `.value-display-value`: The numeric value text

### Draggable Panels
- `.draggable-panel`: Applied to draggable panel containers
- `.panel-header`: Panel header with drag handle and controls
- `.panel-control-button`: Collapse/hide buttons
- `.dragging`: Applied during drag operation
- `.drag-target`: Visual feedback for drop targets

## Configuration

All UI customizations are stored in the application configuration and persist between sessions:

### Config Nodes

- `UI_CONTROL_STYLE`: Selected control style ("Slider", "Knob", or "Display")
- `UI_LAYOUT_CUSTOMIZATION`: JSON array of panel order
- `UI_PANEL_VISIBILITY`: JSON map of panel visibility states

## Future Enhancements

Potential improvements for future releases:

1. **Per-Control Style Override**: Allow different styles for different controls
2. **Multiple Layout Presets**: Save and switch between different layout configurations
3. **Panel Resizing**: Allow users to adjust individual panel sizes
4. **Floating Panels**: Detach panels into separate windows
5. **Grid Snapping**: Snap panels to grid for cleaner layouts
6. **Theme-Aware Controls**: Knobs that change appearance based on current theme

## Compatibility

- Requires JavaFX 21 or higher
- All features are optional and can be disabled
- Default behavior unchanged if features not used
- Layout customizations are stored in existing config system

## Troubleshooting

### Control style not applying
- Check Settings → Control Style selection
- Restart application if setting doesn't take effect
- Verify UI_CONTROL_STYLE in config file

### Panels not dragging
- Ensure "Enable Layout Customization" is checked in View menu
- Check that you're dragging from the drag handle (⋮⋮)

### Layout not persisting
- Check that config file is writable
- Verify UI_LAYOUT_CUSTOMIZATION in config file
- Try "Reset Layout to Default" and reconfigure

### Panel disappeared
- Go to View → Reset Layout to Default
- This will restore all hidden panels

## Developer Notes

### Adding New Draggable Panels

To make a new panel draggable:

```java
DraggableLayoutContainer container = new DraggableLayoutContainer(config);
container.addPanel("panelId", "Panel Title", panelContent);
```

### Creating Custom Control Styles

To add a new control style:

1. Add new enum value to `ControlStyle`
2. Implement the control as a JavaFX `Region`
3. Update `ConfigurableValueControl.rebuildControl()` to handle new style
4. Add corresponding CSS styles

### Layout Serialization

Layout data is serialized to JSON using Gson:

```java
// Panel order
List<String> panelOrder = ["panel1", "panel2", "panel3"];
String json = gson.toJson(panelOrder);

// Panel visibility
Map<String, Boolean> visibility = {"panel1": true, "panel2": false};
String json = gson.toJson(visibility);
```
