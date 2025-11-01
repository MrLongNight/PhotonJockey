# Audio Visualizer Dashboard - UI Übersicht

## Fensterlayout (800x600 Pixel)

```
┌────────────────────────────────────────────────────────────────────────┐
│  Audio Analyzer Dashboard Demo                                    [_][□][X]│
├────────────────────────────────────────────────────────────────────────┤
│                                                                        │
│  Waveform                                                              │
│  ┌──────────────────────────────────────────────────────────────────┐ │
│  │░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░│ │
│  │░░░░░░░░░░░░░░░░░░/\░░░░░░░░░░/\░░░░░░░░░░░/\░░░░░░░░░░░░░░░░░░│ │
│  │░░░░░░░░░░░░░░░░/░░\░░░░░░░░/░░\░░░░░░░░░/░░\░░░░░░░░░░░░░░░░░│ │
│  │──────────────/────\────────/────\────────/────\───────────────│ │
│  │░░░░░░░░░░░░░░░░░░░░\░░░░/░░░░░░░\░░░░/░░░░░░░░\░░░░░░░░░░░░░░│ │
│  │░░░░░░░░░░░░░░░░░░░░░\/░░░░░░░░░░░\/░░░░░░░░░░░░░░░░░░░░░░░░░│ │
│  │░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░│ │
│  └──────────────────────────────────────────────────────────────────┘ │
│                                                                        │
│  Frequency Spectrum                                                    │
│  ┌──────────────────────────────────────────────────────────────────┐ │
│  │▓▓▓▓▓▓▓▓░░░░░░░░                                                  │ │
│  │▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓░░░░░░░░                                          │ │
│  │▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓░░░░░░░░                                  │ │
│  │▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓░░░░░░░░                          │ │
│  │▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓░░░░░░░░                  │ │
│  │▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓░░░░░░░░░░░░░░░░│ │
│  └──────────────────────────────────────────────────────────────────┘ │
│      ↑ Bass                                                  Höhen ↑  │
│                                                                        │
│                        Beat Indicator                                 │
│                            ┌────┐                                      │
│                            │ ●  │  ← Grün bei Beat                    │
│                            └────┘     Grau sonst                       │
│                         BPM: 124.5                                     │
│                                                                        │
├────────────────────────────────────────────────────────────────────────┤
│  Gain:            ├────●───────────┤  1.00                            │
│                   0.0              2.0                                 │
│                                                                        │
│  Beat Sensitivity:├─────────●──────┤  1.30                            │
│                   0.5              2.0                                 │
└────────────────────────────────────────────────────────────────────────┘
```

## Komponenten-Details

### 1. Waveform Canvas (oben)
- **Größe**: 780x150 Pixel
- **Hintergrund**: Dunkelgrau (#2b2b2b)
- **Wellenform**: Grüne Linie (#00ff00)
- **Mittellinie**: Graue Hilfslinie (#555555)
- **Aktualisierung**: ~20x pro Sekunde

**Was Sie sehen:**
- Sinuswellen mit Rauschen
- Bewegung von links nach rechts
- Amplitude hängt von Gain-Wert ab

### 2. Frequency Spectrum Canvas (Mitte)
- **Größe**: 780x150 Pixel
- **Hintergrund**: Dunkelgrau (#2b2b2b)
- **Balken**: Blau (#0088ff)
- **Anzahl**: 64 Balken
- **Aktualisierung**: ~20x pro Sekunde

**Was Sie sehen:**
- Bass-Frequenzen (links) sind höher
- Mittlere Frequenzen (Mitte) mittel
- Hohe Frequenzen (rechts) niedriger
- Alle Balken animieren sich

### 3. Beat Indicator (unten Mitte)
- **Form**: Kreis, 40px Radius
- **Farben**:
  - Grau (#444444): Kein Beat
  - Grün (#00ff00): Beat erkannt
- **Rand**: Grau (#888888), 2px Breite

**In der Demo:**
- Wechselt alle ~1.5 Sekunden zu Grün
- Bleibt für eine Frame grün
- Kehrt zu Grau zurück

### 4. BPM Label
- **Position**: Unter dem Beat-Indikator
- **Format**: "BPM: 120.5"
- **Bereich**: In Demo 120-140 BPM

### 5. Gain Slider
- **Bereich**: 0.0 bis 2.0
- **Default**: 1.0
- **Schritte**: 0.1
- **Effekt**: 
  - 0.0 = Keine Visualisierung
  - 1.0 = Normale Ansicht
  - 2.0 = Doppelte Verstärkung

**Echtzeit-Änderung:**
```
Gain = 0.5  →  Wellenform: halbe Höhe
Gain = 1.0  →  Wellenform: normale Höhe
Gain = 1.5  →  Wellenform: 1.5x Höhe
Gain = 2.0  →  Wellenform: doppelte Höhe
```

### 6. Beat Sensitivity Slider
- **Bereich**: 0.5 bis 2.0
- **Default**: 1.3
- **Schritte**: 0.1
- **Effekt** (nur mit echten Audio-Daten):
  - 0.5 = Sehr empfindlich (viele Beats)
  - 1.3 = Normal
  - 2.0 = Wenig empfindlich (wenige Beats)

## Farbschema

| Element | Farbe | Hex-Code |
|---------|-------|----------|
| Hintergrund Canvas | Dunkelgrau | #2b2b2b |
| Wellenform | Grün | #00ff00 |
| Spektrum-Balken | Blau | #0088ff |
| Beat-Indikator (aktiv) | Grün | #00ff00 |
| Beat-Indikator (inaktiv) | Grau | #444444 |
| Mittellinie | Grau | #555555 |
| Rand | Grau | #888888 |

## Animations-Timeline

```
Zeit →  0s    0.5s    1.0s    1.5s    2.0s    2.5s    3.0s
        │      │       │       │       │       │       │
Wellenform: ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
            (kontinuierliche Animation)

Spektrum:   ▓▓░░░░  ▓▓▓░░░  ▓▓▓▓░░  ▓▓▓░░░  ▓▓▓▓▓░  ▓▓░░░░
            (kontinuierliche Animation)

Beat:       ○      ○      ●      ○      ○      ●      ○
            grau   grau  GRÜN   grau   grau  GRÜN   grau

BPM:        122.1  123.4  128.7  121.5  125.3  132.1  119.8
```

## Performance-Metriken

- **Frame-Rate**: ~20 FPS
- **CPU-Last**: <2%
- **Memory**: ~2MB für UI
- **Thread-Count**: 2 (UI + Simulation)
- **Latenz**: <5ms pro Update

## Interaktions-Beispiele

### Beispiel 1: Gain erhöhen
```
1. Gain-Slider ist bei 1.0
2. Wellenform: normale Amplitude (±50 Pixel vom Zentrum)
3. Gain-Slider auf 2.0 schieben
4. Wellenform: doppelte Amplitude (±100 Pixel vom Zentrum)
5. Spektrum-Balken: auch doppelt so hoch
```

### Beispiel 2: Demo laufen lassen
```
Zeit     Wellenform    Spektrum       Beat    BPM
0.0s     ~~~~~         ▓▓▓▓░░░       ●       124.2
0.5s     ~~~~~         ▓▓░░░░        ○       124.2
1.0s     ~~~~~         ▓▓▓░░░        ○       124.2
1.5s     ~~~~~         ▓▓▓▓▓░░       ●       128.5
2.0s     ~~~~~         ▓▓▓░░░        ○       128.5
```

## Tastatur-Shortcuts (nicht implementiert)

Aktuell keine Tastatur-Shortcuts. Alle Interaktion erfolgt über Maus/Regler.

## Fenster-Größe

- **Minimum**: 800x600 Pixel
- **Empfohlen**: 800x600 Pixel (fest in Demo)
- **Maximum**: Skaliert mit Fenster (FXML unterstützt Resize)

## Bekannte Einschränkungen

1. **Demo-Daten sind simuliert**: Keine echte Audio-Analyse
2. **Beat Sensitivity**: Hat in Demo keinen Effekt (simulierte Beats)
3. **Keine Aufnahme-Funktion**: Nur Visualisierung, keine Audio-Speicherung
4. **Feste Update-Rate**: 20 FPS in Demo, anpassbar bei Integration
