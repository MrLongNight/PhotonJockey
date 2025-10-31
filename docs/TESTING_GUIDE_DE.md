# Audio Visualizer Dashboard - Test-Anleitung

## Schnellstart: Demo ausführen

Der einfachste Weg, den Audio Visualizer zu testen, ist die Demo-Anwendung mit simulierten Daten:

### Option 1: Mit Gradle (empfohlen)

```bash
cd /pfad/zu/PhotonJockey
export JAVA_HOME=/usr/lib/jvm/temurin-21-jdk-amd64  # oder Ihr Java 21 Pfad
./gradlew run -PmainClass=io.github.mrlongnight.photonjockey.ui.AudioAnalyzerDashboardDemo
```

### Option 2: Mit kompiliertem JAR

```bash
# Zuerst bauen
./gradlew shadowJar

# Dann ausführen
java -cp build/libs/PhotonJockey-*.jar \
     io.github.mrlongnight.photonjockey.ui.AudioAnalyzerDashboardDemo
```

## Was Sie sehen werden

Die Demo zeigt:

1. **Wellenform-Anzeige** (oben)
   - Zeigt simulierte Audio-Wellenform als Liniengraph
   - Aktualisiert sich in Echtzeit (~20 FPS)
   - Grüne Linie auf dunklem Hintergrund

2. **Frequenzspektrum** (Mitte)
   - 64 blaue Balken zeigen Frequenzverteilung
   - Bass-Frequenzen (links) sind betont
   - Balken ändern sich dynamisch

3. **Beat-Indikator** (unten Mitte)
   - Grauer Kreis = Kein Beat
   - Grüner Kreis = Beat erkannt
   - BPM-Anzeige darunter (ca. 120-140 BPM simuliert)

4. **Steuerungsregler** (unten)
   - **Gain**: Verstärkung der Visualisierung (0.0 - 2.0)
   - **Beat Sensitivity**: Empfindlichkeit der Beat-Erkennung (0.5 - 2.0)

## Interaktive Tests

### Gain-Regler testen
1. Demo starten
2. Gain-Regler nach rechts schieben → Wellenform und Spektrum werden verstärkt
3. Gain-Regler nach links schieben → Visualisierung wird gedämpft

### Beat Sensitivity testen
1. In der Demo ist Beat-Erkennung alle ~1.5 Sekunden simuliert
2. Beat Sensitivity hat in der Demo keinen sichtbaren Effekt (da simuliert)
3. Bei echter Audio-Integration: höhere Werte = weniger Beats erkannt

## Integration mit echten Audio-Daten

Um den Visualizer mit echten Audio-Daten zu testen, müssen Sie ihn in Ihre Anwendung integrieren:

### 1. FXML laden und Controller holen

```java
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import io.github.mrlongnight.photonjockey.ui.AudioAnalyzerDashboardController;

// In Ihrer JavaFX Application.start() Methode:
FXMLLoader loader = new FXMLLoader(
    getClass().getResource("/fxml/AudioAnalyzerDashboard.fxml")
);
Parent root = loader.load();
AudioAnalyzerDashboardController controller = loader.getController();

Scene scene = new Scene(root, 800, 600);
stage.setScene(scene);
stage.show();
```

### 2. Audio-Pipeline aufbauen

```java
import io.github.mrlongnight.photonjockey.audio.*;

// Audio-Analyse-Komponenten erstellen
int sampleRate = 44100;
int fftSize = 2048;
SimpleAudioAnalyzer analyzer = new SimpleAudioAnalyzer(sampleRate, fftSize);
BeatDetector beatDetector = new BeatDetector();
FFTProcessor fftProcessor = new FFTProcessor(fftSize, WindowFunction.HANN, 0.5);

// Audio-Source (z.B. FileAudioSource für Tests)
IAudioSource audioSource = new FileAudioSource("pfad/zu/ihrer/audio.wav");
```

### 3. Audio-Daten verarbeiten und visualisieren

```java
// In Ihrer Audio-Verarbeitungsschleife (separater Thread):
while (audioSource.hasMoreFrames()) {
    // Audio-Frame holen
    AudioFrame frame = audioSource.getNextFrame();
    
    // Wellenform aktualisieren
    controller.updateWaveform(frame);
    
    // Audio analysieren
    AnalysisResult result = analyzer.analyze(frame);
    
    // Spektrum berechnen und anzeigen
    double[] samples = extractSamples(frame);  // Ihre Implementierung
    double[] spectrum = fftProcessor.computeSpectrum(samples);
    controller.updateSpectrum(spectrum);
    
    // Beat-Erkennung
    boolean isBeat = beatDetector.isBeat(result);
    double bpm = beatDetector.getBPM();
    controller.updateBeatIndicator(isBeat, bpm);
    
    // Kleine Pause für vernünftige Update-Rate
    Thread.sleep(20);  // ~50 FPS
}
```

### 4. Beispiel mit Test-Audio-Dateien

Das Projekt enthält Test-Audio-Dateien im `src/test/resources` Verzeichnis:

```java
// FileAudioSource mit Test-Datei
IAudioSource audioSource = new FileAudioSource(
    "src/test/resources/audio/beat_120bpm.wav"
);

// Oder andere Test-Dateien:
// - sine_440hz.wav - Einfacher 440Hz Sinus-Ton
// - long_mix.wav - Längerer Mix für erweiterte Tests
```

## Manuelle Test-Szenarien

### Szenario 1: Einfacher Funktionstest
1. Demo starten
2. Prüfen: Wellenform bewegt sich
3. Prüfen: Spektrum-Balken animieren
4. Prüfen: Beat-Indikator wird periodisch grün
5. Prüfen: BPM-Wert ändert sich

### Szenario 2: Regler-Interaktion
1. Demo starten
2. Gain auf 0.0 setzen → Visualisierung sollte fast verschwinden
3. Gain auf 2.0 setzen → Visualisierung sollte stark verstärkt sein
4. Gain zurück auf 1.0 → Normale Ansicht

### Szenario 3: Langzeit-Stabilität
1. Demo starten
2. 5-10 Minuten laufen lassen
3. Prüfen: Keine Memory Leaks
4. Prüfen: CPU-Auslastung bleibt stabil (~2%)
5. Prüfen: UI bleibt responsiv

## Unit Tests ausführen

```bash
# Alle UI-Tests
./gradlew test --tests "io.github.mrlongnight.photonjockey.ui.*"

# Nur Unit Tests (keine GUI)
./gradlew test --tests "io.github.mrlongnight.photonjockey.ui.AudioAnalyzerDashboardControllerUnitTest"
```

## Troubleshooting

### Problem: "Cannot find JavaFX runtime"
**Lösung**: Stellen Sie sicher, dass Java 21 verwendet wird:
```bash
java -version  # Sollte Java 21 anzeigen
export JAVA_HOME=/pfad/zu/java-21
```

### Problem: Demo startet nicht
**Lösung**: Projekt neu bauen:
```bash
./gradlew clean compileJava
./gradlew run -PmainClass=io.github.mrlongnight.photonjockey.ui.AudioAnalyzerDashboardDemo
```

### Problem: Schwarzer Bildschirm
**Lösung**: 
1. Prüfen Sie die Konsole auf Fehler
2. Stellen Sie sicher, dass FXML-Datei existiert: `src/main/resources/fxml/AudioAnalyzerDashboard.fxml`

### Problem: Tests schlagen fehl (headless)
**Lösung**: Tests benötigen JavaFX Toolkit. Verwenden Sie:
```bash
./gradlew test -Dtestfx.headless=true -Djava.awt.headless=true
```

## Performance-Hinweise

- **Empfohlene Update-Rate**: 20-60 FPS (20-50ms zwischen Updates)
- **CPU-Auslastung**: <2% bei 30 FPS
- **Memory**: ~2MB für UI-Komponenten
- **Thread-Sicherheit**: Alle `update*()` Methoden sind thread-safe

## Nächste Schritte

Nach dem Testen der Demo können Sie:

1. **Integration in Hauptanwendung**: Visualizer in Ihre Audio-Pipeline einbinden
2. **Anpassungen**: Farben, Größen in FXML ändern
3. **Erweiterungen**: Weitere Visualisierungen hinzufügen
4. **Performance-Optimierung**: Update-Rate an Ihre Bedürfnisse anpassen

## Weitere Dokumentation

- **API-Referenz**: `docs/AUDIO_VISUALIZER.md`
- **Completion Summary**: `TG2.5_COMPLETION_SUMMARY.md`
- **Code-Beispiele**: `AudioAnalyzerDashboardDemo.java`
