# UI Enhancement Features - PhotonJockey

This document describes the new UI enhancement features implemented in PhotonJockey.

## Overview

Three major UI enhancements have been implemented to improve user experience and workflow:

1. **Collapsible UI Sections** - Show/hide UI elements as needed
2. **Drag-and-Drop Tab Reordering** - Customize tab layout
3. **2D Mapping Tool Integration** - Unified interface with integrated mapping tool

## 1. Collapsible UI Sections

### Audio Analyzer Dashboard

All visualization and settings sections can now be collapsed/expanded individually:

#### Visualization Sections (Center Area)
- **Waveform** - Real-time audio waveform display (expanded by default)
- **Frequency Spectrum** - Full spectrum analyzer (expanded by default)
- **Low Frequency Spectrum** - Bass frequencies visualization (collapsed by default)
- **Mid Frequency Spectrum** - Mid-range frequencies (collapsed by default)
- **High Frequency Spectrum** - Treble frequencies (collapsed by default)
- **Analytics** - BPM and beat indicator (expanded by default)

#### Settings Sections (Bottom Area)
- **Frequency Bands** - Configure low/mid/high frequency ranges (collapsed by default)
- **Audio Settings** - Gain, beat sensitivity, BPM range, beat interval (expanded by default)

**Benefits:**
- Focus on the visualizations you need
- Hide advanced settings until needed
- Maximize screen space for important information
- Reduce visual clutter

### Light Controller Dashboard

All configuration sections organized in collapsible panels:

#### Top Sections
- **Color Sets** - Color palette selection, add/delete custom colors, preview
- **Lights** - Select which lights to control, restore all lights
- **Hue Bridge** - Bridge discovery, connection, entertainment group selection

#### Center Sections
- **Brightness** - Min/max brightness controls
- **Advanced Settings** - Linked to "Show Advanced Settings" checkbox
  - Effects: Strobe, Color Strobe, Glow, Bass Only Mode
  - Beat Sensitivity, Beat Delay, Lights per Beat, Max Fade Time
  - Re-add presets, restore defaults

**Benefits:**
- Quick access to frequently used settings
- Advanced settings hidden by default for beginners
- Better organization of related controls
- Consistent with existing "Show Advanced Settings" behavior

## 2. Drag-and-Drop Tab Reordering

### Usage

1. Click and hold on any tab header
2. Drag the tab left or right
3. Release over the target position
4. Tab will be reordered to the new position

### Visual Feedback

- **Cursor**: Changes to hand pointer when hovering over tabs
- **During Drag**: Dragged tab becomes semi-transparent (50% opacity)
- **Drop Target**: Target tab highlights with purple accent color
- **After Drop**: Tab order is immediately updated

### Tabs Available

1. **Audio Analyzer** - Audio analysis and beat detection
2. **Light Controller** - Light control and configuration
3. **2D Mapping Tool** - Spatial light mapping and positioning

**Benefits:**
- Customize workflow to your preference
- Put most-used tab first for quick access
- Flexible UI layout
- Personal workspace organization

## 3. 2D Mapping Tool Integration

The SmartMappingTool is now fully integrated as a third tab in the main dashboard.

### Features

- **Interactive Canvas** - Visual representation of light positions
- **Drag-and-Drop Lights** - Position lights by dragging on canvas
- **Light Management** - Add, remove, and configure lights
- **Bridge Configuration** - Add and manage Hue bridges
- **Save/Load Configurations** - Export and import light maps
- **Test Effects** - Preview effects on configured lights

### Canvas Controls

- Click and drag any light to reposition it
- Grid overlay for alignment
- Color-coded lights by control type:
  - Blue: FAST_UDP (Entertainment API)
  - Orange: LOW_HTTP (Classic API)
- Selection indicator around selected light
- Light labels show ID or name

### Light Properties

- **ID**: Unique identifier for the light
- **Name**: Human-readable display name
- **Bridge ID**: Which bridge controls this light
- **Control Type**: FAST_UDP or LOW_HTTP
- **Position**: X,Y coordinates on canvas (0-960, 0-500)

**Benefits:**
- No separate window needed
- Seamless workflow integration
- All tools in one place
- Easy access while monitoring audio/lights

## Implementation Details

### Technologies Used

- **JavaFX Accordion/TitledPane**: Native collapsible components
- **JavaFX Drag-and-Drop API**: Tab reordering functionality
- **CSS Styling**: Consistent theme and visual feedback
- **TabDragHelper Utility**: Reusable drag-and-drop implementation

### Code Structure

```
src/main/java/io/github/mrlongnight/photonjockey/ui/
├── UnifiedDashboardController.java         # Main dashboard with tabs
├── AudioAnalyzerDashboardController.java   # Audio analysis tab
├── LightControllerDashboardController.java # Light control tab
├── SmartMappingToolController.java         # 2D mapping tab
└── util/
    └── TabDragHelper.java                  # Drag-and-drop utility

src/main/resources/
├── fxml/
│   ├── UnifiedDashboard.fxml              # Main layout with tabs
│   ├── AudioAnalyzerDashboard.fxml        # Audio UI with Accordion
│   ├── LightControllerDashboard.fxml      # Light UI with Accordion
│   └── SmartMappingTool.fxml              # Mapping tool UI
└── css/
    └── dashboard.css                       # Enhanced styling
```

### CSS Classes

- `.accordion` - Accordion container styling
- `.titled-pane` - Collapsible pane styling
- `.titled-pane:expanded` - Expanded state with purple accent
- `.tab` - Tab styling with hand cursor
- `.tab.drag-over` - Drag-over visual feedback

### Configuration

All UI state is preserved:
- **Accordion expansion state**: Managed by JavaFX internally
- **Tab order**: Maintained in TabPane observable list
- **Advanced settings visibility**: Linked to existing ConfigNode.SHOW_ADVANCED_SETTINGS

## User Guide

### Getting Started

1. **Launch PhotonJockey** - UI opens with all tabs available
2. **Organize Tabs** - Drag tabs to preferred order
3. **Collapse Unused Sections** - Click section headers to collapse/expand
4. **Configure Lights** - Use 2D Mapping Tool tab for spatial setup

### Best Practices

1. **Audio Analyzer**:
   - Keep main Waveform and Spectrum expanded during use
   - Collapse detailed frequency sections unless debugging
   - Expand Audio Settings when tuning beat detection

2. **Light Controller**:
   - Expand Color Sets and Lights for quick configuration
   - Collapse Bridge section after connection established
   - Use Advanced Settings only when needed

3. **2D Mapping Tool**:
   - Position lights accurately on canvas
   - Save configuration after making changes
   - Test effects before live use

### Tips

- **Double-click** TitledPane headers to quickly expand/collapse
- **Keyboard navigation** works with Tab key through sections
- **Hover** over section headers to see collapse/expand indicator
- **Tab dragging** preserves your current selection

## Accessibility

- All collapsible sections are keyboard accessible
- Screen readers can announce section expand/collapse state
- Tab order maintained for keyboard navigation
- Visual feedback provided for all interactions

## Performance

- Minimal impact on application performance
- Accordion only renders expanded sections
- Drag-and-drop operations are lightweight
- No additional background threads required

## Backward Compatibility

- All existing functionality preserved
- Configuration files remain compatible
- No changes to core application logic
- UI preferences can be reset to defaults

## Future Enhancements

Potential future improvements:

1. Save tab order in user preferences
2. Remember accordion expansion state between sessions
3. Add "Collapse All" / "Expand All" buttons
4. Implement section floating/docking
5. Add tab closing/reopening capability
6. Theme-specific accordion animations

## Support

For issues or questions:
- Check existing GitHub issues
- Review application logs
- Report bugs with screenshot if UI-related
- Include steps to reproduce

## Version

These features were implemented in PhotonJockey v0.0.2

---

*Last Updated: November 6, 2025*
