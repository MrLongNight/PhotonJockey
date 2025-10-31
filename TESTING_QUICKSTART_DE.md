# Audio Visualizer - Schnellstart zum Testen

## TL;DR - In 3 Schritten testen

### 1. Demo starten
```bash
cd /pfad/zu/PhotonJockey
./gradlew run -PmainClass=io.github.mrlongnight.photonjockey.ui.AudioAnalyzerDashboardDemo
```

### 2. Fenster erscheint mit
- ✅ Wellenform (oben) - grüne animierte Linie
- ✅ Frequenzspektrum (Mitte) - blaue animierte Balken
- ✅ Beat-Indikator (unten) - Kreis wird alle ~1.5s grün
- ✅ Zwei Regler für Gain und Beat Sensitivity

### 3. Interaktiv testen
- **Gain-Regler** bewegen → Visualisierung wird verstärkt/gedämpft
- **Beat Sensitivity** (hat in Demo keinen Effekt, nur mit echten Audio-Daten)

---

## Detaillierte Anleitung
Siehe: [docs/TESTING_GUIDE_DE.md](docs/TESTING_GUIDE_DE.md)

## Was wird visualisiert?

| Komponente | Beschreibung | Update-Rate |
|------------|--------------|-------------|
| Wellenform | Audio-Samples als Liniengraph | ~20 FPS |
| Spektrum | 64 Frequenz-Balken | ~20 FPS |
| Beat-Indikator | Grün bei Beat, sonst grau | Event-basiert |
| BPM-Anzeige | Beats pro Minute | Event-basiert |

## Systemanforderungen
- Java 21 (OpenJDK 21 oder höher)
- JavaFX 21.0.1 (wird automatisch von Gradle geladen)
- ~50MB freier RAM
- Grafikkarte mit OpenGL/DirectX Support

## Problem? 
Siehe Troubleshooting in [docs/TESTING_GUIDE_DE.md](docs/TESTING_GUIDE_DE.md)
