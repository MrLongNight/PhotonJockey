# Changelog

Alle wichtigen Änderungen an diesem Projekt werden in dieser Datei dokumentiert.

Das Format basiert auf [Keep a Changelog](https://keepachangelog.com/de/1.0.0/),
und dieses Projekt folgt [Semantic Versioning](https://semver.org/lang/de/).

## [Unreleased]

### In Planung
- Weitere Lichteffekte und Visualisierungsmodi
- Erweitertes Audio-Profil-System
- Performance-Optimierungen für Entertainment API V2

## [0.0.2] - 2025-11-06

### Hinzugefügt
- **Smart Mapping Tool**: Visuelles Tool zur Konfiguration von Philips Hue Light Mappings
  - Interaktive Canvas-basierte Light-Positionierung (960x500 Pixel)
  - Drag & Drop für präzise Lichtplatzierung
  - Bridge-Management (ID und IP-Konfiguration)
  - FAST_UDP und LOW_HTTP Control-Type-Unterstützung
  - JSON-basierte Konfigurationspersistierung
  - Live-Test-Funktion für Effect-Routing

- **Audio Visualizer Dashboard**: Echtzeit-Audio-Analyse-Visualisierung
  - Waveform-Ansicht (Zeitbereich)
  - Frequenzspektrum-Visualisierung (64 Frequenzbänder)
  - Beat-Indikator mit BPM-Anzeige
  - Konfigurierbarer Gain-Slider (0.0-2.0)
  - Beat-Sensitivity-Slider (0.5-2.0)
  - JavaFX-basierte UI mit FXML

- **Audio-Profile-System**: Genre-spezifische Audio-Analyse-Parameter
  - `AudioProfile`: Typ-sichere Parameter-Verwaltung
  - `AudioProfileManager`: JSON-basierte Persistierung in `/config/audio_profiles.json`
  - Standard-Profile: Techno, House, Ambient
  - CRUD-Operationen für benutzerdefinierte Profile
  - Automatische Initialisierung und Directory-Erstellung
  - 46 Unit-Tests für vollständige Abdeckung

- **Audio Engine Verbesserungen**:
  - `IAudioSource` Interface mit `SystemAudioSource` und `FileAudioSource` Implementierungen
  - `IAudioAnalyzer` Interface für modulare Audio-Analyse
  - `FFTProcessor` mit Window Functions (Hann, Hamming, Blackman, etc.)
  - `BeatDetector` mit Energy-Threshold-Algorithmus
  - `SimpleAudioAnalyzer` Audio-Pipeline
  - Beat-Event-System (`BeatEvent`, `BeatEventManager`, `BeatInterpreter`)
  - Test-Audio-Dateien für Integration-Tests (120 BPM, 440 Hz Sinus)

- **Projekt-Infrastruktur**:
  - `.editorconfig` (Google Java Styleguide)
  - `checkstyle.xml` (Vollständige Checkstyle-Konfiguration)
  - Pull Request Template mit Checkliste
  - Coding Conventions Dokumentation
  - Automatisierte Code-Analyse-Tools (`analyze_codebase.py`, `generate_metrics.py`)

- **CI/CD Workflows**:
  - `unit-tests.yml`: Automatische Test-Ausführung bei PRs und Commits
  - `build-and-release.yml`: Automatisierte Release-Erstellung mit JAR und MSI Installer
  - Java 21 Support in allen Workflows

- **Dokumentation**:
  - Umfangreiche Feature-Dokumentation (Audio Profiles, Audio Visualizer, Smart Mapping)
  - Build-Anleitung (`docs/development/01-BUILD_INSTRUCTIONS.md`)
  - Testing Guides (Deutsch)
  - Troubleshooting Guide
  - UI-Übersicht und Integration-Dokumentation
  - Projekt-Status-Tracking (`docs/project/02-IMPLEMENTATION_STATUS.md`)
  - Implementierte Tasks-Übersicht (`docs/project/03-IMPLEMENTED_TASKS.md`)

### Geändert
- Upgrade auf Java 21 (von Java 17)
- Konsolidierung von 7 CI-Workflows auf 2 wesentliche Workflows
- Refactoring der Audio-Thread-Verwaltung
- Verbesserte Code-Metriken und Complexity-Analyse

### Entfernt
- Redundante Release-Workflows (`b&r_windows.yml`, `release-build.yml`, `release.yml`)
- Nicht konfigurierte Analyse-Tools (`sonarcloud.yml`, `codeql.yml`)
- Dokumentations-Workflow (`static_analysis.yml`)
- `gradle-publish.yml` (GitHub Packages nicht genutzt)

### Behoben
- UI Component Initialization Issues
- Thread-Management in Audio-Analyzer
- Workflow-Redundanzen und Inkonsistenzen

## [0.0.1] - 2025-10-20 (Initiales Setup)

### Hinzugefügt
- Initiales Projekt-Setup mit Gradle
- Basis-Projektstruktur (src/main/java, src/test/java)
- Philips Hue Integration (Bridge-Kommunikation)
- Grundlegende Audio-Analyse-Funktionalität
- Erste Lichteffekte (ColorFlip, ColorStrobe, ColorFade)
- Hue Manager und Bridge-Konnektivität
- Light Controllers (Brightness, Color, Strobe)
- Effect Router und Effect Controllers (Fast UDP, Low HTTP)
- Basis-Tests für Core-Funktionalität

### Technischer Stack
- Java 21 + Gradle Build-System
- JavaFX für GUI-Komponenten
- Philips Hue Bridge API (Entertainment API V2)
- JUnit 5 für Tests
- Checkstyle für Code-Style-Enforcement

---

## Release-Strategie

### Versioning-Schema (Semantic Versioning)
- **MAJOR** (1.x.x): Breaking Changes, grundlegende Architektur-Änderungen
- **MINOR** (x.1.x): Neue Features, abwärtskompatibel
- **PATCH** (x.x.1): Bugfixes, kleine Verbesserungen

### Geplante Releases
- **v1.0.0**: Erstes produktives Release (geplant)
  - Vollständige Feature-Stabilität
  - Produktionsreife Dokumentation
  - Windows Installer (MSI)
  - Umfassende Test-Abdeckung

### Release-Prozess
1. Version in `build.gradle` aktualisieren
2. CHANGELOG.md mit allen Änderungen aktualisieren
3. Git Tag erstellen: `git tag v1.0.0`
4. Tag pushen: `git push origin v1.0.0`
5. GitHub Actions erstellt automatisch Release-Artefakte
6. Draft-Release auf GitHub überprüfen und veröffentlichen

[Unreleased]: https://github.com/MrLongNight/PhotonJockey/compare/v0.0.2...HEAD
[0.0.2]: https://github.com/MrLongNight/PhotonJockey/compare/v0.0.1...v0.0.2
[0.0.1]: https://github.com/MrLongNight/PhotonJockey/releases/tag/v0.0.1
