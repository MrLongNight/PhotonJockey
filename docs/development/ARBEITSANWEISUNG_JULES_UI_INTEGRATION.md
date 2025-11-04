# Arbeitsanweisung für Jules: UI Integration und Modernisierung

## Projektübersicht
**Repository**: MrLongNight/PhotonJockey
**Aufgabe**: Integration der alten LightBeat UI (Swing) mit dem modernen AudioAnalyzerDashboard (JavaFX) in einer einheitlichen Tab-basierten Oberfläche.

## Zielsetzung
Erstelle eine moderne, einheitliche JavaFX-Anwendung mit Tab-Navigation zwischen:
1. **Audio Analyzer Dashboard** - Visualisierung und Audio-Analyse
2. **Light Controller Dashboard** - Steuerung der Philips Hue Lichter (früher "LightBeat UI")

## Technische Anforderungen

### 1. Technologie-Stack
- **UI Framework**: JavaFX 21.0.1
- **Styling**: CSS-basiert, dunkles Theme
- **Build-System**: Gradle
- **Java Version**: 21

### 2. Design-System (von AudioAnalyzerDashboard übernehmen)

#### Farbpalette
```css
-fx-background-primary: #1e1e1e;    /* Haupthintergrund */
-fx-background-secondary: #2b2b2b;  /* Panels und Karten */
-fx-text-primary: #ffffff;          /* Haupttext */
-fx-text-secondary: #aaaaaa;        /* Sekundärtext */
-fx-text-tertiary: #888888;         /* Tertiärtext */
-fx-accent-green: #00ff00;          /* Aktive Zustände, Beat-Indikator */
-fx-accent-blue: #0088ff;           /* Visualisierungen, Spektrum */
-fx-border: #444444;                /* Rahmen */
-fx-separator: #555555;             /* Trennlinien */
```

#### Komponenten-Styling
- **Canvas**: Border #444444, 1px
- **Sliders**: Tick marks, kein Label-Text
- **Buttons**: Abgerundete Ecken, Hover-Effekte
- **Labels**: Konsistente Font-Größen (12px Standard, 14px Überschriften)

### 3. Architektur

#### Neue Dateistruktur
```
src/main/java/io/github/mrlongnight/photonjockey/ui/
├── UnifiedDashboard.java                    (Haupt-Application mit TabPane)
├── UnifiedDashboardController.java          (Controller für Tab-Management)
├── AudioAnalyzerDashboardController.java    (Bestehend, evtl. anpassen)
├── LightControllerDashboardController.java  (NEU - Migration von MainFrame)
└── shared/
    └── DashboardTab.java                    (Optional: Abstract base für Tabs)

src/main/resources/
├── fxml/
│   ├── UnifiedDashboard.fxml               (NEU - Hauptlayout mit TabPane)
│   ├── AudioAnalyzerDashboard.fxml         (Bestehend, als Tab einbinden)
│   └── LightControllerDashboard.fxml       (NEU - JavaFX Version von MainFrame)
└── css/
    └── dashboard.css                        (NEU - Gemeinsames Styling)
```

## Detaillierte Implementierungsschritte

### Phase 1: Vorbereitung und CSS Framework

#### Schritt 1.1: CSS-Stylesheet erstellen
**Datei**: `src/main/resources/css/dashboard.css`

```css
/* Globale Farbvariablen */
.root {
    -fx-background-primary: #1e1e1e;
    -fx-background-secondary: #2b2b2b;
    -fx-text-primary: #ffffff;
    -fx-text-secondary: #aaaaaa;
    -fx-text-tertiary: #888888;
    -fx-accent-green: #00ff00;
    -fx-accent-blue: #0088ff;
    -fx-border: #444444;
    -fx-separator: #555555;
}

/* Hauptcontainer */
.dashboard-root {
    -fx-background-color: -fx-background-primary;
}

/* Tab-Pane Styling */
.tab-pane {
    -fx-background-color: -fx-background-primary;
    -fx-tab-min-width: 150px;
}

.tab-pane .tab-header-area {
    -fx-background-color: -fx-background-secondary;
}

.tab-pane .tab {
    -fx-background-color: -fx-background-secondary;
    -fx-text-fill: -fx-text-secondary;
    -fx-font-size: 14px;
    -fx-padding: 10px 20px;
}

.tab-pane .tab:selected {
    -fx-background-color: -fx-background-primary;
    -fx-text-fill: -fx-text-primary;
    -fx-border-color: -fx-accent-green;
    -fx-border-width: 0 0 2px 0;
}

.tab-pane .tab:hover {
    -fx-background-color: derive(-fx-background-secondary, 10%);
}

/* Panel Styling */
.panel {
    -fx-background-color: -fx-background-secondary;
    -fx-padding: 10px;
}

/* Canvas Styling */
.canvas {
    -fx-border-color: -fx-border;
    -fx-border-width: 1;
}

/* Slider Styling */
.slider {
    -fx-pref-width: 400px;
}

.slider .track {
    -fx-background-color: -fx-border;
}

.slider .thumb {
    -fx-background-color: -fx-accent-blue;
}

/* Button Styling */
.button {
    -fx-background-color: -fx-background-secondary;
    -fx-text-fill: -fx-text-primary;
    -fx-border-color: -fx-border;
    -fx-border-width: 1;
    -fx-background-radius: 4;
    -fx-border-radius: 4;
    -fx-padding: 8px 16px;
    -fx-font-size: 12px;
}

.button:hover {
    -fx-background-color: derive(-fx-background-secondary, 15%);
}

.button:pressed {
    -fx-background-color: derive(-fx-background-secondary, -10%);
}

.button.primary {
    -fx-background-color: -fx-accent-blue;
    -fx-text-fill: white;
    -fx-font-size: 16px;
    -fx-font-weight: bold;
}

.button.primary:hover {
    -fx-background-color: derive(-fx-accent-blue, 15%);
}

/* Label Styling */
.label {
    -fx-text-fill: -fx-text-primary;
    -fx-font-size: 12px;
}

.label.section-title {
    -fx-font-size: 14px;
    -fx-font-weight: bold;
}

.label.status {
    -fx-text-fill: -fx-text-secondary;
    -fx-font-size: 11px;
}

.label.info {
    -fx-text-fill: -fx-text-tertiary;
    -fx-font-size: 10px;
}

/* ComboBox Styling */
.combo-box {
    -fx-background-color: -fx-background-secondary;
    -fx-text-fill: -fx-text-primary;
}

.combo-box .list-cell {
    -fx-background-color: -fx-background-secondary;
    -fx-text-fill: -fx-text-primary;
}

/* CheckBox Styling */
.check-box {
    -fx-text-fill: -fx-text-primary;
}

.check-box .box {
    -fx-background-color: -fx-background-secondary;
    -fx-border-color: -fx-border;
}

.check-box:selected .mark {
    -fx-background-color: -fx-accent-green;
}

/* Separator Styling */
.separator {
    -fx-background-color: -fx-separator;
}

/* Progress Bar Styling */
.progress-bar {
    -fx-background-color: -fx-background-secondary;
}

.progress-bar .track {
    -fx-background-color: -fx-border;
}

.progress-bar .bar {
    -fx-background-color: -fx-accent-green;
}

/* Color Panel */
.color-panel {
    -fx-border-color: -fx-border;
    -fx-border-width: 1;
    -fx-background-radius: 4;
    -fx-border-radius: 4;
}
```

### Phase 2: Hauptanwendung mit TabPane erstellen

#### Schritt 2.1: UnifiedDashboard.fxml erstellen
**Datei**: `src/main/resources/fxml/UnifiedDashboard.fxml`

```xml
<?xml version="1.0" encoding="UTF-8"?>

<?import javafx.scene.control.TabPane?>
<?import javafx.scene.control.Tab?>
<?import javafx.scene.layout.BorderPane?>
<?import javafx.geometry.Insets?>

<BorderPane xmlns="http://javafx.com/javafx/21" xmlns:fx="http://javafx.com/fxml/1"
            fx:controller="io.github.mrlongnight.photonjockey.ui.UnifiedDashboardController"
            prefHeight="750.0" prefWidth="1100.0" styleClass="dashboard-root"
            stylesheets="@../css/dashboard.css">
    
    <center>
        <TabPane fx:id="mainTabPane" tabClosingPolicy="UNAVAILABLE">
            <Tab text="Audio Analyzer" fx:id="audioAnalyzerTab">
                <!-- Content wird dynamisch geladen -->
            </Tab>
            <Tab text="Light Controller" fx:id="lightControllerTab">
                <!-- Content wird dynamisch geladen -->
            </Tab>
        </TabPane>
    </center>
    
</BorderPane>
```

#### Schritt 2.2: UnifiedDashboardController.java erstellen
**Datei**: `src/main/java/io/github/mrlongnight/photonjockey/ui/UnifiedDashboardController.java`

```java
package io.github.mrlongnight.photonjockey.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Controller for the unified dashboard with tabs.
 */
public class UnifiedDashboardController {
    
    private static final Logger logger = LoggerFactory.getLogger(UnifiedDashboardController.class);
    
    @FXML
    private TabPane mainTabPane;
    
    @FXML
    private Tab audioAnalyzerTab;
    
    @FXML
    private Tab lightControllerTab;
    
    private AudioAnalyzerDashboardController audioAnalyzerController;
    private LightControllerDashboardController lightControllerController;
    
    @FXML
    public void initialize() {
        loadAudioAnalyzerTab();
        loadLightControllerTab();
    }
    
    private void loadAudioAnalyzerTab() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/AudioAnalyzerDashboard.fxml")
            );
            Parent content = loader.load();
            audioAnalyzerController = loader.getController();
            audioAnalyzerTab.setContent(content);
            logger.info("Audio Analyzer tab loaded successfully");
        } catch (IOException e) {
            logger.error("Failed to load Audio Analyzer tab", e);
        }
    }
    
    private void loadLightControllerTab() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/LightControllerDashboard.fxml")
            );
            Parent content = loader.load();
            lightControllerController = loader.getController();
            lightControllerTab.setContent(content);
            logger.info("Light Controller tab loaded successfully");
        } catch (IOException e) {
            logger.error("Failed to load Light Controller tab", e);
        }
    }
    
    public AudioAnalyzerDashboardController getAudioAnalyzerController() {
        return audioAnalyzerController;
    }
    
    public LightControllerDashboardController getLightControllerController() {
        return lightControllerController;
    }
}
```

#### Schritt 2.3: UnifiedDashboard.java (Application-Klasse) erstellen
**Datei**: `src/main/java/io/github/mrlongnight/photonjockey/ui/UnifiedDashboard.java`

```java
package io.github.mrlongnight.photonjockey.ui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import io.github.mrlongnight.photonjockey.AppTaskOrchestrator;
import io.github.mrlongnight.photonjockey.config.Config;
import io.github.mrlongnight.photonjockey.audio.PJAudioReader;
import io.github.mrlongnight.photonjockey.hue.bridge.HueManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;

/**
 * Unified dashboard application combining Audio Analyzer and Light Controller.
 */
public class UnifiedDashboard extends Application {
    
    private static final Logger logger = LoggerFactory.getLogger(UnifiedDashboard.class);
    
    private static Config staticConfig;
    private static AppTaskOrchestrator staticTaskOrchestrator;
    private static PJAudioReader staticAudioReader;
    private static HueManager staticHueManager;
    
    private UnifiedDashboardController controller;
    private AppTaskOrchestrator taskOrchestrator;
    private Config config;
    private PJAudioReader audioReader;
    private HueManager hueManager;
    
    public static void init(Config config, AppTaskOrchestrator taskOrchestrator,
                           PJAudioReader audioReader, HueManager hueManager) {
        staticConfig = config;
        staticTaskOrchestrator = taskOrchestrator;
        staticAudioReader = audioReader;
        staticHueManager = hueManager;
    }
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        logger.info("Starting Unified Dashboard application");
        
        // Initialize from static variables
        config = staticConfig;
        taskOrchestrator = staticTaskOrchestrator;
        audioReader = staticAudioReader;
        hueManager = staticHueManager;
        
        // Load main UI
        URL fxmlUrl = getClass().getResource("/fxml/UnifiedDashboard.fxml");
        if (fxmlUrl == null) {
            logger.error("Could not find UnifiedDashboard.fxml");
            return;
        }
        
        FXMLLoader loader = new FXMLLoader(fxmlUrl);
        Parent root = loader.load();
        controller = loader.getController();
        
        // Initialize controllers with dependencies
        initializeControllers();
        
        Scene scene = new Scene(root, 1100, 750);
        primaryStage.setTitle("PhotonJockey - Audio & Light Controller");
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(e -> {
            e.consume();
            shutdown();
        });
        primaryStage.show();
        
        logger.info("Unified Dashboard started successfully");
    }
    
    private void initializeControllers() {
        // Initialize Audio Analyzer controller
        AudioAnalyzerDashboardController audioController = 
            controller.getAudioAnalyzerController();
        if (audioController != null) {
            audioController.setCallbacks(
                this::refreshAudioDevices,
                this::onAudioDeviceSelected,
                this::updateConfigFromUi,
                this::onVisualizationsToggled
            );
            audioReader.registerBeatObserver(/* create observer */);
        }
        
        // Initialize Light Controller controller
        LightControllerDashboardController lightController = 
            controller.getLightControllerController();
        if (lightController != null) {
            lightController.initialize(config, taskOrchestrator, audioReader, hueManager);
        }
    }
    
    private void shutdown() {
        logger.info("Shutting down Unified Dashboard");
        
        if (audioReader != null && audioReader.isOpen()) {
            audioReader.stop();
        }
        
        if (taskOrchestrator != null) {
            taskOrchestrator.shutdown();
        }
        
        Platform.exit();
    }
    
    // Callback methods for audio analyzer
    private void refreshAudioDevices() {
        // Implementation
    }
    
    private void onAudioDeviceSelected(String deviceName) {
        // Implementation
    }
    
    private void updateConfigFromUi() {
        // Implementation
    }
    
    private void onVisualizationsToggled(Boolean enabled) {
        // Implementation
    }
}
```

### Phase 3: Light Controller Dashboard (JavaFX Migration)

#### Schritt 3.1: LightControllerDashboard.fxml erstellen
**Datei**: `src/main/resources/fxml/LightControllerDashboard.fxml`

Dieser Schritt erfordert die vollständige Migration aller UI-Elemente von MainFrame.form nach JavaFX FXML.

**Hauptbereiche:**

1. **Audio Source Panel** (oben)
   - ComboBox für Audio-Device-Auswahl
   - Refresh-Button
   - Help-Button

2. **Control Panel** (Mitte)
   - Color Set Selection (Radio Buttons in FlowPane)
   - Color Preview Panel
   - Add/Delete Custom Colors Buttons
   - Lights Selection (CheckBoxes in FlowPane)
   - Brightness Sliders (Min/Max)

3. **Advanced Settings Panel** (unten, toggle-bar)
   - Effects CheckBoxes (Strobe, Color Strobe, Glow, Bass Only)
   - Sensitivity Sliders
   - Color Randomization
   - Fade Settings

4. **Action Panel** (unten)
   - Start/Stop Button (groß, primary style)
   - Settings CheckBoxes (Show Advanced, Auto Start, Light Theme)

5. **Status Bar** (ganz unten)
   - Version Label
   - Status Label
   - Info Label

**Beispiel-Struktur** (gekürzt):
```xml
<?xml version="1.0" encoding="UTF-8"?>

<?import javafx.geometry.Insets?>
<?import javafx.scene.control.*?>
<?import javafx.scene.layout.*?>

<BorderPane xmlns="http://javafx.com/javafx/21" xmlns:fx="http://javafx.com/fxml/1"
            fx:controller="io.github.mrlongnight.photonjockey.ui.LightControllerDashboardController"
            prefHeight="700.0" prefWidth="1000.0" styleClass="dashboard-root">
    
    <top>
        <VBox spacing="10">
            <padding><Insets top="10" right="10" bottom="10" left="10"/></padding>
            
            <!-- Audio Source Panel -->
            <HBox spacing="10" alignment="CENTER_LEFT" styleClass="panel">
                <Label text="Audio Device:" styleClass="section-title"/>
                <ComboBox fx:id="audioDeviceComboBox" prefWidth="300"/>
                <Button fx:id="refreshDevicesButton" text="Refresh"/>
                <Button fx:id="deviceHelpButton" text="?"/>
            </HBox>
            
            <Separator/>
            
            <!-- Color Selection Panel -->
            <VBox spacing="10">
                <Label text="Color Sets" styleClass="section-title"/>
                <FlowPane fx:id="colorSetPanel" hgap="10" vgap="10"/>
                <HBox spacing="10">
                    <Button fx:id="addCustomColorsButton" text="Add Custom Colors"/>
                    <Button fx:id="deleteCustomColorsButton" text="Delete Selected"/>
                </HBox>
                <!-- Color Preview Canvas -->
                <Canvas fx:id="colorPreviewCanvas" width="980" height="40" styleClass="canvas"/>
            </VBox>
            
            <Separator/>
            
            <!-- Lights Selection Panel -->
            <VBox spacing="10">
                <Label text="Lights" styleClass="section-title"/>
                <FlowPane fx:id="lightSelectPanel" hgap="10" vgap="10"/>
                <Button fx:id="restoreLightsButton" text="Restore All Lights"/>
            </VBox>
        </VBox>
    </top>
    
    <center>
        <VBox spacing="15" styleClass="panel">
            <padding><Insets top="10" right="10" bottom="10" left="10"/></padding>
            
            <!-- Brightness Panel -->
            <VBox spacing="10">
                <Label text="Brightness" styleClass="section-title"/>
                <HBox spacing="10" alignment="CENTER_LEFT">
                    <Label text="Minimum:" minWidth="100"/>
                    <Slider fx:id="minBrightnessSlider" min="0" max="254" value="80"/>
                    <Label fx:id="minBrightnessLabel" text="31%" minWidth="50"/>
                </HBox>
                <HBox spacing="10" alignment="CENTER_LEFT">
                    <Label text="Maximum:" minWidth="100"/>
                    <Slider fx:id="maxBrightnessSlider" min="0" max="254" value="254"/>
                    <Label fx:id="maxBrightnessLabel" text="100%" minWidth="50"/>
                </HBox>
                <Button fx:id="restoreBrightnessButton" text="Restore Defaults"/>
            </VBox>
            
            <Separator/>
            
            <!-- Advanced Settings (initially hidden) -->
            <VBox fx:id="advancedPanel" spacing="10" visible="false" managed="false">
                <Label text="Advanced Settings" styleClass="section-title"/>
                
                <!-- Effects -->
                <HBox spacing="15">
                    <CheckBox fx:id="strobeCheckBox" text="Strobe Effect"/>
                    <CheckBox fx:id="colorStrobeCheckbox" text="Color Strobe"/>
                    <CheckBox fx:id="glowCheckBox" text="Glow Effect"/>
                    <CheckBox fx:id="bassOnlyModeCheckBox" text="Bass Only Mode"/>
                </HBox>
                
                <!-- Sensitivity -->
                <HBox spacing="10" alignment="CENTER_LEFT">
                    <Label text="Beat Sensitivity:" minWidth="120"/>
                    <Slider fx:id="beatSensitivitySlider" min="5" max="20" value="13"/>
                    <Label fx:id="beatSensitivityLabel" minWidth="50"/>
                </HBox>
                
                <!-- More sliders... -->
                
                <HBox spacing="10">
                    <Button fx:id="readdColorSetPresetsButton" text="Re-add Presets"/>
                    <Button fx:id="restoreAdvancedButton" text="Restore Defaults"/>
                    <Button fx:id="disconnectBridgeButton" text="Disconnect Bridge"/>
                </HBox>
            </VBox>
        </VBox>
    </center>
    
    <bottom>
        <VBox spacing="10">
            <padding><Insets top="10" right="10" bottom="10" left="10"/></padding>
            
            <!-- Start Button -->
            <Button fx:id="startButton" text="Start" styleClass="primary"
                    prefHeight="50" maxWidth="Infinity"/>
            
            <!-- Settings -->
            <HBox spacing="20" alignment="CENTER">
                <CheckBox fx:id="showAdvancedCheckbox" text="Show Advanced Settings"/>
                <CheckBox fx:id="autoStartCheckBox" text="Auto Start On Launch"/>
                <CheckBox fx:id="lightThemeCheckbox" text="Light Theme"/>
            </HBox>
            
            <!-- Status Bar -->
            <HBox spacing="10" alignment="CENTER_LEFT" styleClass="panel">
                <Label fx:id="versionLabel" text="v0.0.2" styleClass="info"/>
                <Region HBox.hgrow="ALWAYS"/>
                <Label fx:id="statusLabel" text="Status: Idle" styleClass="status"/>
                <Label fx:id="infoLabel" text="" styleClass="info"/>
            </HBox>
        </VBox>
    </bottom>
    
</BorderPane>
```

#### Schritt 3.2: LightControllerDashboardController.java erstellen
**Datei**: `src/main/java/io/github/mrlongnight/photonjockey/ui/LightControllerDashboardController.java`

```java
package io.github.mrlongnight.photonjockey.ui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import io.github.mrlongnight.photonjockey.AppTaskOrchestrator;
import io.github.mrlongnight.photonjockey.audio.AudioReader;
import io.github.mrlongnight.photonjockey.audio.BeatEvent;
import io.github.mrlongnight.photonjockey.audio.BeatObserver;
import io.github.mrlongnight.photonjockey.config.Config;
import io.github.mrlongnight.photonjockey.config.ConfigNode;
import io.github.mrlongnight.photonjockey.hue.bridge.HueManager;
import io.github.mrlongnight.photonjockey.hue.bridge.color.ColorSet;
import io.github.mrlongnight.photonjockey.hue.bridge.color.CustomColorSet;
import io.github.mrlongnight.photonjockey.hue.bridge.color.RandomColorSet;
import io.github.mrlongnight.photonjockey.hue.bridge.light.Light;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller for the Light Controller Dashboard (formerly MainFrame).
 * Manages Philips Hue light control and synchronization with audio.
 */
public class LightControllerDashboardController implements BeatObserver {
    
    private static final Logger logger = LoggerFactory.getLogger(LightControllerDashboardController.class);
    
    // Audio Source Panel
    @FXML private ComboBox<String> audioDeviceComboBox;
    @FXML private Button refreshDevicesButton;
    @FXML private Button deviceHelpButton;
    
    // Color Selection Panel
    @FXML private FlowPane colorSetPanel;
    @FXML private Button addCustomColorsButton;
    @FXML private Button deleteCustomColorsButton;
    @FXML private Canvas colorPreviewCanvas;
    
    // Lights Selection Panel
    @FXML private FlowPane lightSelectPanel;
    @FXML private Button restoreLightsButton;
    
    // Brightness Panel
    @FXML private Slider minBrightnessSlider;
    @FXML private Label minBrightnessLabel;
    @FXML private Slider maxBrightnessSlider;
    @FXML private Label maxBrightnessLabel;
    @FXML private Button restoreBrightnessButton;
    
    // Advanced Settings Panel
    @FXML private VBox advancedPanel;
    @FXML private CheckBox strobeCheckBox;
    @FXML private CheckBox colorStrobeCheckbox;
    @FXML private CheckBox glowCheckBox;
    @FXML private CheckBox bassOnlyModeCheckBox;
    @FXML private Slider beatSensitivitySlider;
    @FXML private Label beatSensitivityLabel;
    @FXML private Button readdColorSetPresetsButton;
    @FXML private Button restoreAdvancedButton;
    @FXML private Button disconnectBridgeButton;
    
    // Action Panel
    @FXML private Button startButton;
    @FXML private CheckBox showAdvancedCheckbox;
    @FXML private CheckBox autoStartCheckBox;
    @FXML private CheckBox lightThemeCheckbox;
    
    // Status Bar
    @FXML private Label versionLabel;
    @FXML private Label statusLabel;
    @FXML private Label infoLabel;
    
    // Dependencies
    private Config config;
    private AppTaskOrchestrator taskOrchestrator;
    private AudioReader audioReader;
    private HueManager hueManager;
    
    private ToggleGroup colorSetToggleGroup;
    private List<CheckBox> lightCheckBoxes;
    
    @FXML
    public void initialize() {
        colorSetToggleGroup = new ToggleGroup();
        lightCheckBoxes = new ArrayList<>();
        setupListeners();
    }
    
    public void initialize(Config config, AppTaskOrchestrator taskOrchestrator,
                          AudioReader audioReader, HueManager hueManager) {
        this.config = config;
        this.taskOrchestrator = taskOrchestrator;
        this.audioReader = audioReader;
        this.hueManager = hueManager;
        
        loadConfiguration();
        refreshAudioDevices();
        refreshColorSets();
        refreshLights();
        updateColorPreview();
    }
    
    private void setupListeners() {
        // Brightness sliders
        minBrightnessSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            minBrightnessLabel.setText(String.format("%d%%", (int)(newVal.doubleValue() / 254 * 100)));
            if (config != null) {
                config.putInt(ConfigNode.BRIGHTNESS_MIN, newVal.intValue());
            }
        });
        
        maxBrightnessSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            maxBrightnessLabel.setText(String.format("%d%%", (int)(newVal.doubleValue() / 254 * 100)));
            if (config != null) {
                config.putInt(ConfigNode.BRIGHTNESS_MAX, newVal.intValue());
            }
        });
        
        // Advanced panel toggle
        showAdvancedCheckbox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            advancedPanel.setVisible(newVal);
            advancedPanel.setManaged(newVal);
            if (config != null) {
                config.putBoolean(ConfigNode.SHOW_ADVANCED_SETTINGS, newVal);
            }
        });
        
        // Beat sensitivity
        beatSensitivitySlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            beatSensitivityLabel.setText(String.format("%d%%", newVal.intValue() * 10));
            if (config != null) {
                config.putInt(ConfigNode.BEAT_SENSITIVITY, newVal.intValue());
            }
        });
        
        // Start button
        startButton.setOnAction(e -> onStartStopClicked());
        
        // Other buttons
        refreshDevicesButton.setOnAction(e -> refreshAudioDevices());
        addCustomColorsButton.setOnAction(e -> onAddCustomColors());
        deleteCustomColorsButton.setOnAction(e -> onDeleteCustomColors());
        restoreLightsButton.setOnAction(e -> onRestoreLights());
        restoreBrightnessButton.setOnAction(e -> onRestoreBrightness());
        disconnectBridgeButton.setOnAction(e -> onDisconnectBridge());
    }
    
    private void loadConfiguration() {
        minBrightnessSlider.setValue(config.getInt(ConfigNode.BRIGHTNESS_MIN));
        maxBrightnessSlider.setValue(config.getInt(ConfigNode.BRIGHTNESS_MAX));
        showAdvancedCheckbox.setSelected(config.getBoolean(ConfigNode.SHOW_ADVANCED_SETTINGS));
        autoStartCheckBox.setSelected(config.getBoolean(ConfigNode.AUTOSTART));
        bassOnlyModeCheckBox.setSelected(config.getBoolean(ConfigNode.BEAT_BASS_ONLY_MODE));
        beatSensitivitySlider.setValue(config.getInt(ConfigNode.BEAT_SENSITIVITY));
        strobeCheckBox.setSelected(config.getBoolean(ConfigNode.EFFECT_STROBE));
        colorStrobeCheckbox.setSelected(config.getBoolean(ConfigNode.EFFECT_COLOR_STROBE));
        glowCheckBox.setSelected(config.getBoolean(ConfigNode.EFFECT_ALERT));
    }
    
    private void refreshAudioDevices() {
        // Implementation: Load audio devices from audioReader
        audioDeviceComboBox.getItems().clear();
        audioReader.getSupportedDevices().forEach(device -> 
            audioDeviceComboBox.getItems().add(device.getName())
        );
        
        String lastSource = config.get(ConfigNode.LAST_AUDIO_SOURCE);
        if (lastSource != null && audioDeviceComboBox.getItems().contains(lastSource)) {
            audioDeviceComboBox.setValue(lastSource);
        }
    }
    
    private void refreshColorSets() {
        colorSetPanel.getChildren().clear();
        
        // Add "Random" color set
        addColorSetRadioButton("Random");
        
        // Add custom color sets
        List<String> colorSets = config.getStringList(ConfigNode.COLOR_SET_LIST);
        colorSets.forEach(this::addColorSetRadioButton);
        
        // Select current color set
        String selected = config.get(ConfigNode.COLOR_SET_SELECTED);
        colorSetToggleGroup.getToggles().stream()
            .filter(toggle -> ((RadioButton)toggle).getText().equals(selected))
            .findFirst()
            .ifPresent(toggle -> toggle.setSelected(true));
    }
    
    private void addColorSetRadioButton(String name) {
        RadioButton radioButton = new RadioButton(name);
        radioButton.setToggleGroup(colorSetToggleGroup);
        radioButton.setOnAction(e -> onColorSetSelected(name));
        colorSetPanel.getChildren().add(radioButton);
    }
    
    private void refreshLights() {
        lightSelectPanel.getChildren().clear();
        lightCheckBoxes.clear();
        
        List<String> disabledLights = config.getStringList(ConfigNode.LIGHTS_DISABLED);
        
        hueManager.getLights(false).forEach(light -> {
            CheckBox checkBox = new CheckBox(light.getName());
            checkBox.setSelected(!disabledLights.contains(light.getId()));
            checkBox.setOnAction(e -> onLightSelectionChanged(light.getId(), checkBox.isSelected()));
            
            lightSelectPanel.getChildren().add(checkBox);
            lightCheckBoxes.add(checkBox);
        });
    }
    
    private void updateColorPreview() {
        String colorSetName = config.get(ConfigNode.COLOR_SET_SELECTED);
        ColorSet colorSet = colorSetName.equals("Random") ? 
            new RandomColorSet() : 
            new CustomColorSet(config, colorSetName);
        
        drawColorPreview(colorSet);
    }
    
    private void drawColorPreview(ColorSet colorSet) {
        GraphicsContext gc = colorPreviewCanvas.getGraphicsContext2D();
        double width = colorPreviewCanvas.getWidth();
        double height = colorPreviewCanvas.getHeight();
        
        // Clear canvas
        gc.setFill(Color.web("#2b2b2b"));
        gc.fillRect(0, 0, width, height);
        
        // Draw color bars
        int colorCount = Math.min(colorSet.size(), 20);
        double barWidth = width / colorCount;
        
        for (int i = 0; i < colorCount; i++) {
            io.github.mrlongnight.photonjockey.hue.bridge.color.Color color = 
                colorSet.getColor(i);
            gc.setFill(Color.rgb(color.getRed(), color.getGreen(), color.getBlue()));
            gc.fillRect(i * barWidth, 0, barWidth, height);
        }
    }
    
    // Event handlers
    private void onStartStopClicked() {
        if (audioReader.isOpen()) {
            audioReader.stop();
            startButton.setText("Start");
            updateStatus("Stopped");
        } else {
            String deviceName = audioDeviceComboBox.getValue();
            if (deviceName != null) {
                var device = audioReader.getDeviceByName(deviceName);
                if (device != null && audioReader.start(device)) {
                    startButton.setText("Stop");
                    updateStatus("Running");
                }
            }
        }
    }
    
    private void onColorSetSelected(String name) {
        config.put(ConfigNode.COLOR_SET_SELECTED, name);
        updateColorPreview();
    }
    
    private void onLightSelectionChanged(String lightId, boolean selected) {
        List<String> disabledLights = new ArrayList<>(config.getStringList(ConfigNode.LIGHTS_DISABLED));
        if (selected) {
            disabledLights.remove(lightId);
        } else {
            disabledLights.add(lightId);
        }
        config.putList(ConfigNode.LIGHTS_DISABLED, disabledLights);
    }
    
    private void onAddCustomColors() {
        // Open color selection dialog
        logger.info("Add custom colors clicked");
    }
    
    private void onDeleteCustomColors() {
        // Delete selected color set
        logger.info("Delete custom colors clicked");
    }
    
    private void onRestoreLights() {
        config.remove(ConfigNode.LIGHTS_DISABLED);
        refreshLights();
    }
    
    private void onRestoreBrightness() {
        minBrightnessSlider.setValue(config.getDefaultInt(ConfigNode.BRIGHTNESS_MIN));
        maxBrightnessSlider.setValue(config.getDefaultInt(ConfigNode.BRIGHTNESS_MAX));
    }
    
    private void onDisconnectBridge() {
        hueManager.disconnectAll();
        refreshLights();
        updateStatus("Bridge disconnected");
    }
    
    private void updateStatus(String message) {
        Platform.runLater(() -> statusLabel.setText("Status: " + message));
    }
    
    // BeatObserver implementation
    @Override
    public void beatReceived(BeatEvent event) {
        Platform.runLater(() -> {
            // Visual feedback on beat
            updateColorPreview();
        });
    }
    
    @Override
    public void noBeatReceived() {}
    
    @Override
    public void silenceDetected() {}
    
    @Override
    public void audioReceived(io.github.mrlongnight.photonjockey.audio.AudioFrame audioFrame) {}
    
    @Override
    public void audioReaderStopped(StopStatus status) {
        Platform.runLater(() -> {
            startButton.setText("Start");
            updateStatus("Stopped");
        });
    }
}
```

### Phase 4: Integration in PhotonJockey.main()

#### Schritt 4.1: PhotonJockey.java anpassen
**Datei**: `src/main/java/io/github/mrlongnight/photonjockey/PhotonJockey.java`

```java
package io.github.mrlongnight.photonjockey;

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
        HueManager hueManager = new PJHueManager(config, taskOrchestrator);

        // REMOVED: Old Swing UI
        // new MainFrame(config, taskOrchestrator, audioReader, beatEventManager, hueManager, 100, 100);

        // Initialize and start the unified JavaFX UI
        UnifiedDashboard.init(config, taskOrchestrator, audioReader, hueManager);
        Application.launch(UnifiedDashboard.class, args);
    }
}
```

### Phase 5: Cleanup und Deprecation

#### Schritt 5.1: MainFrame.java als deprecated markieren
Füge `@Deprecated` Annotation hinzu und Javadoc-Kommentar:

```java
/**
 * @deprecated This class is deprecated and will be removed in a future version.
 * Use {@link io.github.mrlongnight.photonjockey.ui.UnifiedDashboard} instead.
 */
@Deprecated
public class MainFrame extends AbstractFrame implements BeatObserver {
    // ...
}
```

#### Schritt 5.2: AudioAnalyzerDashboard.java anpassen
Da es jetzt Teil eines größeren Dashboards ist, muss die standalone Application-Logik entfernt werden. Die Klasse sollte nur noch Controller-Funktionalität haben.

### Phase 6: Build und Tests

#### Schritt 6.1: Build.gradle prüfen
Stelle sicher, dass JavaFX-Module korrekt konfiguriert sind:

```gradle
javafx {
    version = "21.0.1"
    modules = ['javafx.controls', 'javafx.fxml', 'javafx.swing']
    configuration = 'implementation'
}
```

#### Schritt 6.2: Build ausführen
```bash
./gradlew clean build --no-daemon
```

#### Schritt 6.3: Tests erstellen
Erstelle Tests für die neuen Controller in `src/test/java/`:

```java
package io.github.mrlongnight.photonjockey.ui;

import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ApplicationExtension.class)
class UnifiedDashboardTest {
    
    @Start
    public void start(Stage stage) throws Exception {
        // Initialize test stage
    }
    
    @Test
    void testTabSwitching(FxRobot robot) {
        // Test that tabs can be switched
    }
    
    @Test
    void testAudioAnalyzerTabLoaded(FxRobot robot) {
        // Verify Audio Analyzer tab loads correctly
    }
    
    @Test
    void testLightControllerTabLoaded(FxRobot robot) {
        // Verify Light Controller tab loads correctly
    }
}
```

### Phase 7: Verifikation und Dokumentation

#### Schritt 7.1: Manuelle Verifikation
1. Starte die Anwendung
2. Überprüfe beide Tabs
3. Teste Tab-Wechsel
4. Teste alle Funktionen in beiden Tabs
5. Mache Screenshots für Dokumentation

#### Schritt 7.2: Dokumentation aktualisieren
Update `README.md` und relevante Dokumentation:

```markdown
## UI Architecture

PhotonJockey features a modern JavaFX-based user interface with two main dashboards:

### Audio Analyzer Dashboard
- Real-time waveform visualization
- Frequency spectrum analysis
- Beat detection with BPM display
- Audio device selection and configuration

### Light Controller Dashboard
- Philips Hue light selection and control
- Color set management
- Brightness controls
- Advanced effect settings
- Audio-to-light synchronization

Both dashboards are integrated in a tabbed interface for easy navigation.
```

## Checkliste für Jules

### Must-Have (Kernfunktionalität)
- [ ] CSS-Stylesheet (`dashboard.css`) erstellt mit allen Styles
- [ ] `UnifiedDashboard.fxml` erstellt mit TabPane
- [ ] `UnifiedDashboard.java` Application-Klasse implementiert
- [ ] `UnifiedDashboardController.java` implementiert
- [ ] `LightControllerDashboard.fxml` vollständig erstellt
- [ ] `LightControllerDashboardController.java` vollständig implementiert
- [ ] `PhotonJockey.java` angepasst (UnifiedDashboard statt MainFrame)
- [ ] Alle UI-Elemente von MainFrame nach JavaFX migriert
- [ ] Tab-Wechsel funktioniert
- [ ] Beide Tabs laden korrekt

### Should-Have (Wichtige Features)
- [ ] Color Preview Canvas funktioniert
- [ ] Audio Device Selection funktioniert in beiden Tabs
- [ ] Start/Stop Button funktioniert
- [ ] Brightness Sliders funktionieren
- [ ] Advanced Settings Panel toggle funktioniert
- [ ] CheckBoxes für Light-Selection funktionieren
- [ ] RadioButtons für Color Set Selection funktionieren

### Nice-to-Have (Zusätzliche Features)
- [ ] Animations beim Tab-Wechsel
- [ ] Tooltips für alle Steuerelemente
- [ ] Keyboard-Shortcuts (Strg+1, Strg+2 für Tabs)
- [ ] Window-Größe und Position werden gespeichert
- [ ] Theme-Switch funktioniert (Light/Dark)

### Testing & Documentation
- [ ] Unit-Tests für UnifiedDashboardController
- [ ] Unit-Tests für LightControllerDashboardController
- [ ] Build erfolgreich (`./gradlew clean build`)
- [ ] Application startet ohne Fehler
- [ ] README.md aktualisiert
- [ ] Screenshots der neuen UI erstellt

## Wichtige Hinweise für Jules

1. **Codestyle**: Beachte Google Java Style Guide (Checkstyle wird automatisch ausgeführt)

2. **Logging**: Verwende SLF4J Logger für alle Log-Ausgaben

3. **Threading**: Alle UI-Updates müssen über `Platform.runLater()` erfolgen

4. **Config-Management**: Alle Einstellungen über `Config` und `ConfigNode` verwalten

5. **Error-Handling**: Alle Exceptions loggen und User-freundliche Fehler anzeigen

6. **Dependency**: Falls yetanotherhueapi-Fehler auftreten, siehe `.github/workflows/unit-tests.yml` für Build-Anleitung

7. **Minimal Changes**: Ändere bestehenden Code nur minimal. Fokus auf neue Dateien.

8. **Testing**: Teste regelmäßig während der Entwicklung, nicht erst am Ende

## Priorität der Implementierung

1. **Höchste Priorität**: CSS und UnifiedDashboard.fxml + Controller
2. **Hohe Priorität**: LightControllerDashboard.fxml Grundstruktur
3. **Mittlere Priorität**: LightControllerDashboardController.java Logik
4. **Niedrige Priorität**: Advanced Features und Animationen

## Fragen bei Unklarheiten

Falls während der Implementierung Unklarheiten auftreten:
1. Prüfe bestehende Implementierung in AudioAnalyzerDashboard
2. Prüfe MainFrame.java für Business-Logik
3. Dokumentiere offene Fragen für Review

## Geschätzter Zeitaufwand

- Phase 1 (CSS): 2 Stunden
- Phase 2 (UnifiedDashboard): 3 Stunden
- Phase 3 (LightControllerDashboard): 8 Stunden
- Phase 4 (Integration): 1 Stunde
- Phase 5 (Cleanup): 1 Stunde
- Phase 6 (Tests): 3 Stunden
- Phase 7 (Dokumentation): 2 Stunden

**Gesamt**: ~20 Stunden

## Erfolgsmetrik

Die Implementierung gilt als erfolgreich, wenn:
1. Build ohne Fehler durchläuft
2. Application startet und beide Tabs anzeigt
3. Tab-Wechsel funktioniert flüssig
4. Alle Hauptfunktionen beider UIs funktionieren
5. UI folgt konsistentem Design-System
6. Code-Coverage >= 60% für neue Controller
