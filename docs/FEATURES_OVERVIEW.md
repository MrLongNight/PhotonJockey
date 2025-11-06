# PhotonJockey - Features Übersicht

Vollständige Übersicht aller implementierten Features in PhotonJockey.

**Stand:** November 2025  
**Version:** 0.0.2  
**Nächstes Release:** v1.0.0 (geplant)

---

## 🎵 Audio-Analyse und -Verarbeitung

### Audio Input System
- **Multiple Audio-Quellen**: Unterstützung für verschiedene Audio-Eingänge
  - `SystemAudioSource`: Systemweite Audio-Erfassung
  - `FileAudioSource`: Wiedergabe von Audio-Dateien für Offline-Nutzung
  - Modulares `IAudioSource` Interface für zukünftige Erweiterungen

### FFT-basierte Frequenzanalyse
- **FFT Processor**: Fast Fourier Transform für Echtzeit-Spektralanalyse
  - Unterstützung für verschiedene Window Functions:
    - Hann Window
    - Hamming Window
    - Blackman Window
    - Rectangular Window
  - Konfigurierbares Smoothing für stabilere Visualisierung
  - Optimiert für niedrige Latenz

### Beat Detection
- **BeatDetector**: Intelligente Beat-Erkennung
  - Energy-Threshold-Algorithmus
  - Adaptiver Threshold basierend auf Audio-Historie
  - Konfigurierbare Sensitivität
  - BPM-Schätzung (Beats per Minute)
  - Debouncing für vermeidung von False-Positives
  - Beat-Event-System mit Listenern

### Audio-Profile-System
- **Genre-spezifische Konfigurationen**: Optimale Einstellungen für verschiedene Musikstile
  - **Techno-Profil**: 
    - Beat Sensitivity: 6 (hoch)
    - Min Time Between Beats: 150ms
    - Threshold Multiplier: 1.4
  - **House-Profil**: 
    - Beat Sensitivity: 5 (mittel)
    - Min Time Between Beats: 200ms
    - Threshold Multiplier: 1.3
  - **Ambient-Profil**: 
    - Beat Sensitivity: 3 (niedrig)
    - Min Time Between Beats: 300ms
    - Threshold Multiplier: 1.2
  
- **Profile Management**:
  - Persistierung in JSON (`/config/audio_profiles.json`)
  - Dynamisches Laden und Speichern zur Laufzeit
  - CRUD-Operationen (Create, Read, Update, Delete)
  - Benutzerdefinierte Profile möglich

- **API-Dokumentation**: [docs/features/01-AUDIO_PROFILES.md](features/01-AUDIO_PROFILES.md)

---

## 🎨 Visualisierung

### Audio Visualizer Dashboard
- **Echtzeit-Waveform-Anzeige**: 
  - Zeitbereich-Visualisierung der Audio-Samples
  - 780x150 Pixel Canvas
  - Flüssige Updates bei eingehenden Audio-Frames

- **Frequenzspektrum-Darstellung**:
  - 64 Frequenzbänder visualisiert als Balkendiagramm
  - Bass-Frequenzen links, hohe Frequenzen rechts
  - Logarithmische Skalierung für bessere Übersicht

- **Beat-Indikator**:
  - Visuelles Feedback bei erkannten Beats
  - Grün bei Beat, Grau ohne Beat
  - Live-BPM-Anzeige

- **Interaktive Controls**:
  - **Gain-Slider**: Amplitude-Kontrolle (0.0 - 2.0)
  - **Beat Sensitivity Slider**: Threshold-Anpassung (0.5 - 2.0)

- **Technologie**: JavaFX mit FXML-Layout
- **Dokumentation**: [docs/features/02-AUDIO_VISUALIZER.md](features/02-AUDIO_VISUALIZER.md)

---

## 💡 Philips Hue Integration

### Bridge-Kommunikation
- **Multi-Bridge-Support**: Mehrere Hue Bridges gleichzeitig
- **Dual-Protokoll-Unterstützung**:
  - **FAST_UDP**: Entertainment API V2 (DTLS/UDP) für niedrige Latenz
  - **LOW_HTTP**: Klassische HTTP REST API für Kompatibilität
- **Automatisches Bridge-Discovery**
- **Authentifizierung und Pairing**

### Light Controller
- **BrightnessController**: Helligkeitssteuerung basierend auf Audio-Energie
- **ColorController**: Farbwechsel synchron zur Musik
- **StrobeController**: Stroboskop-Effekte auf Beats

### Light Effects
- **ColorFlipEffect**: Abrupte Farbwechsel bei Beats
- **ColorStrobeEffect**: Rhythmische Strobe-Effekte
- **ColorFadeEffect**: Sanfte Farbübergänge
- **DefaultEffect**: Standard-Visualisierung
- **Erweiterbar**: Abstract Effect-Klassen für eigene Effekte

### Effect Routing
- **EffectRouter**: Intelligentes Routing von Effects zu Lights
- **FastEffectController**: Optimiert für Entertainment API V2
- **LowEffectController**: HTTP-basierte Steuerung
- **UpdateQueue**: Effiziente Update-Verwaltung

---

## 🗺️ Smart Mapping Tool

### Visuelle Light-Konfiguration
- **Interaktives Canvas**: 960x500 Pixel große Arbeitsfläche
  - Grid-Hintergrund für präzise Positionierung
  - Drag & Drop für Light-Platzierung
  - Visuelle Unterscheidung nach Control-Type:
    - Blau: FAST_UDP Lights
    - Orange: LOW_HTTP Lights
  - Grüner Auswahl-Indikator

### Bridge-Management
- **Bridge-Verwaltung**:
  - Hinzufügen von Bridges mit ID und IP
  - Mehrere Bridges gleichzeitig
  - Bridge-Selektor für Light-Zuordnung

### Light-Konfiguration
- **Light-Properties**:
  - Eindeutige Light ID
  - Anzeigename (Label)
  - Bridge-Zuordnung
  - Control Type (FAST_UDP / LOW_HTTP)
  - X/Y-Position (via Drag & Drop)

### Persistierung
- **JSON-Export/Import**:
  - Speichern in `lightmap.json`
  - Laden vorhandener Konfigurationen
  - Kompatibel mit PhotonJockey Runtime

### Testing
- **Live-Test-Funktion**: Effect-Routing-Preview
- **Demo-Modi**:
  - Leeres Template
  - Sample-Daten-Template

- **Dokumentation**: [docs/features/03-SMART_MAPPING_TOOL.md](features/03-SMART_MAPPING_TOOL.md)

---

## 🛠️ Entwickler-Features

### Code-Qualität
- **Checkstyle**: Google Java Styleguide enforcement
- **EditorConfig**: Konsistente Code-Formatierung
- **SpotBugs**: Statische Code-Analyse
- **Automatisierte Metriken**:
  - Lines of Code (LOC)
  - Cyclomatic Complexity
  - Method Count
  - Complexity Score

### Testing
- **Unit-Tests**: 
  - 46+ Tests für Audio-Profile
  - FFT Processor Tests
  - Beat Detector Tests
  - JSON-Utility Tests
- **Integration-Tests**:
  - Beat Detection IT
  - Audio Source Tests
  - End-to-End Audio-Pipeline Tests
- **Test-Audio-Dateien**:
  - 120 BPM Test-Track
  - 440 Hz Sinus-Welle
  - Längerer Mix für Stress-Tests

### Build-System
- **Gradle**: Modernes Build-Management
  - Shadow JAR für Fat JAR Builds
  - Runtime Plugin für Windows-Packaging
  - JavaFX Plugin Integration
- **Multi-Platform**:
  - JAR für alle Plattformen
  - Windows MSI Installer
  - Zukünftig: Linux und macOS Packages

### CI/CD
- **Automatisierte Tests**: Unit-Tests bei jedem PR
- **Release-Automation**: Automatische Erstellung von JAR und MSI
- **Artifact-Upload**: Test-Reports und Build-Artefakte
- **GitHub Actions**: Moderne CI/CD-Pipeline

---

## 📚 Dokumentation

### User Documentation (Deutsch)
- **README.md**: Projekt-Übersicht und Quick Start
- **Build Instructions**: Detaillierte Aufbau-Anleitung
- **Testing Guides**: Test-Ausführung und Best Practices
- **Troubleshooting**: Fehlerbehebung und Log-Analyse
- **UI-Übersicht**: Bedienung der grafischen Oberflächen

### Developer Documentation
- **Coding Conventions**: Branch-Strategie, Commit-Guidelines
- **Architecture Docs**: Code-Struktur und Design-Patterns
- **API Documentation**: Interface-Beschreibungen
- **Feature Specs**: Detaillierte Feature-Dokumentation

### Project Management
- **Project Plan**: Vollständiger Projekt-Roadmap
- **Implementation Status**: Echtzeit-Status aller TaskGroups
- **Implemented Tasks**: Abgeschlossene Tasks-Übersicht
- **Completion Summaries**: Detaillierte Abschluss-Berichte

---

## 🚀 Laufzeit-Features

### Konfiguration
- **JSON-basierte Configs**: Einfache Bearbeitung
- **Config-Directory**: `/config/` für alle Laufzeit-Konfigurationen
  - `audio_profiles.json`
  - `lightmap.json`
  - `bridge_config.json` (geplant)

### Performance
- **Niedrige Latenz**: Optimiert für Echtzeit-Synchronisation
- **Effizientes Threading**: Dedizierte Threads für Audio, Analyse und Light-Updates
- **Update-Queue**: Verhindert API-Flooding

### Erweiterbarkeit
- **Plugin-Architecture**: Modulares Design für neue Features
- **Interface-basiert**: Einfaches Hinzufügen neuer Implementierungen
- **Effect-System**: Abstract Classes für Custom Effects

---

## 🎯 Geplante Features (Roadmap zu v1.0.0)

### In Entwicklung
- Erweiterte Audio-Profile mit mehr Parametern
- Weitere Visualisierungsmodi
- Performance-Optimierungen

### Geplant für v1.0.0
- Vollständige UI-Integration aller Features
- Installer für Windows, Linux, macOS
- Umfassende Benutzer-Dokumentation (EN/DE)
- Performance-Benchmarks
- Erweiterte Effect-Bibliothek

### Zukünftige Releases
- Plugin-System für Community-Effekte
- Web-basierte Fernsteuerung
- Mobile App Integration
- DMX-Licht-Support (über Hue Bridge hinaus)
- MIDI-Controller-Integration

---

## 📊 Technologie-Stack

### Core
- **Sprache**: Java 21
- **Build**: Gradle 8.x
- **UI**: JavaFX 21

### Libraries
- **Hue API**: YetAnotherHueApi (Entertainment API V2)
- **Testing**: JUnit 5, Mockito
- **Code Quality**: Checkstyle, SpotBugs

### Plattformen
- **Windows**: Primäre Plattform mit MSI Installer
- **Linux**: JAR läuft auf allen Distributionen
- **macOS**: JAR-basierte Ausführung

---

## 📞 Support und Community

### Ressourcen
- **GitHub Repository**: [MrLongNight/PhotonJockey](https://github.com/MrLongNight/PhotonJockey)
- **Issue Tracker**: GitHub Issues
- **Dokumentation**: `/docs` Verzeichnis im Repository

### Contribution
- **Pull Requests**: Willkommen mit Tests und Dokumentation
- **Feature Requests**: Via GitHub Issues
- **Bug Reports**: Mit Reproduktionsschritten und Logs

---

**Hinweis**: Diese Features-Übersicht wird kontinuierlich aktualisiert. Für die aktuellste Version siehe GitHub Repository.
