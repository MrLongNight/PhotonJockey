# LightJockey - Kompletter Entwicklungsplan

**Dokument-ID:** `LIGHTJOCKEY_DEVELOPMENT_PLAN.md`  
**Projekt:** LightJockey (C# Neuentwicklung von PhotonJockey)  
**Datum:** 2025-11-11  
**Status:** Finalisiert - Integration aller Review-Empfehlungen  
**Version:** 2.0

---

## Dokumentübersicht

Dieses Dokument konsolidiert den ursprünglichen C# Rewrite-Plan mit allen Erkenntnissen und Empfehlungen aus der technischen Review. Es enthält:
- Vollständige Technologie-Stack-Entscheidungen
- Detaillierte Phase-1-Tasks mit erweiterten Agent-Prompts
- Alle identifizierten Lücken aus der PhotonJockey Java-Version
- Best Practices für Architektur, Testing und Security

---

## 1. Übersicht und Ziele

### 1.1 Projektvision

LightJockey ist die Neuentwicklung der PhotonJockey-Anwendung in C#. Die Umsetzung erfolgt in einem **neuen, separaten GitHub Repository** und wird ausschließlich durch KI-Coding-Tools gesteuert.

### 1.2 Leitprinzipien

- **Modulare Architektur:** Klare Trennung von UI, Geschäftslogik (Audio-Analyse, Effekt-Engine) und Services (Hue-Kommunikation)
- **Testbarkeit:** Von Anfang an auf Unit- und Integrationstests auslegen mit Dependency Injection
- **Moderne UI/UX:** Ansprechende und intuitive Benutzeroberfläche mit flüssigen Audio-Visualisierungen
- **Performance-First:** Audio-Latenz < 50ms, Light-Update-Rate > 20 FPS
- **Security:** Verschlüsselte Speicherung sensibler Daten, HTTPS-Validierung
- **KI-gesteuerte Entwicklung:** Der gesamte Prozess wird durch detaillierte Prompts für KI-Agenten gesteuert

### 1.3 Abgrenzung zur Java-Version

LightJockey soll **alle Features** der PhotonJockey Java-Version übernehmen und verbessern:
- Multi-Bridge-Support (bereits in Phase 1)
- Audio-Visualisierungen (Waveform + Frequency Spectrum)
- Audio-Profil-System (Genre-spezifisch: Techno, House, Ambient)
- Light Mapping Configuration
- Custom Color Sets
- Strobe Effects
- Brightness & Transition Time Calibration

---

## 2. Technologie-Stack

### 2.1 Kernkomponenten

| Komponente | Technologie | Begründung |
|------------|-------------|------------|
| **Programmiersprache** | C# 12.0 | Modern, typsicher, exzellentes Tooling |
| **Target Framework** | .NET 8.0 | LTS-Version mit bester Performance |
| **UI-Framework** | **WPF** (Empfehlung) | Ausgereift, exzellente Canvas-Performance, große Community. Alternative: WinUI 3 (modern) oder MAUI (Cross-Platform) |
| **Audio-Bibliothek** | **NAudio** (Empfohlen) | Aktiv gewartet, bessere FFT-Unterstützung als CSCore, .NET 8 kompatibel |
| **Hue-Kommunikation** | HueApi.Net | Native .NET Bibliothek, Entertainment API V2 Support |
| **Dependency Injection** | Microsoft.Extensions.DependencyInjection | Standard .NET DI-Container |
| **Logging** | Serilog | Strukturierte Logs, flexibles Routing |
| **Testing** | xUnit + Moq | De-facto Standard für .NET |
| **JSON-Serialisierung** | System.Text.Json | Performant, built-in |
| **Build-System** | .NET CLI / MSBuild | Standard .NET Toolchain |
| **CI/CD** | GitHub Actions | Integriert, Windows-Runner verfügbar |

### 2.2 Technologie-Evaluierung (Task 0)

**Vor Start der Entwicklung muss eine Evaluierung durchgeführt werden:**

**UI-Framework-Entscheidung:**
- **WPF:** Beste Performance für Desktop, XAML-Expertise vorhanden
- **WinUI 3:** Modern, Fluent Design, aber jüngeres Ökosystem
- **MAUI:** Cross-Platform, aber Performance-Einschränkungen bei Echtzeit-Rendering

**Entscheidungskriterien:**
1. Canvas-Rendering-Performance für Audio-Visualisierungen
2. Drag & Drop Layout-Unterstützung
3. Community-Support und verfügbare Ressourcen
4. Langfristige Wartbarkeit

**Audio-Bibliothek-Entscheidung:**
- **NAudio:** Aktiv, große Community, gute FFT-Unterstützung
- **CSCore:** Nicht mehr gewartet (letztes Update 2018)

**Empfehlung: NAudio**

---

## 3. Architektur-Überblick

### 3.1 Projektstruktur

```
LightJockey/
├── LightJockey.Core/              # Business Logic
│   ├── Audio/
│   │   ├── FFTProcessor.cs
│   │   ├── BeatDetector.cs
│   │   ├── SpectralAnalyzer.cs
│   │   └── AudioProfileManager.cs
│   ├── Effects/
│   │   ├── EffectEngine.cs
│   │   ├── IEffect.cs
│   │   └── Effects/
│   └── Models/
├── LightJockey.Services/          # External Services
│   ├── HueService.cs
│   ├── AudioService.cs
│   ├── ConfigurationService.cs
│   └── Interfaces/
├── LightJockey.UI/                # WPF/MAUI Application
│   ├── Views/
│   ├── ViewModels/
│   ├── Controls/
│   └── Resources/
├── LightJockey.Tests/             # Unit & Integration Tests
│   ├── Core/
│   ├── Services/
│   └── Integration/
└── LightJockey.Common/            # Shared Utilities
    ├── Extensions/
    └── Helpers/
```

### 3.2 Dependency Injection

Alle Services werden über DI registriert:
- **Singleton:** HueService, AudioService, EffectEngine, ConfigurationService, Logging
- **Transient:** ViewModels
- **Scoped:** Nicht verwendet (keine Web-Anwendung)

---

## 4. Phase 1: MVP (Minimum Viable Product)

**Ziel:** Funktionierende Kernanwendung mit Live-Audio-Analyse und Hue Entertainment API Synchronisation.

---

### Task 0: Technologie-Evaluierung

**Beschreibung:**  
Entscheidung über UI-Framework und Audio-Bibliothek basierend auf Prototyping und Performance-Tests.

**Deliverables:**
- Entscheidungsdokument (Architecture Decision Record)
- Proof-of-Concept für Canvas-Rendering
- Proof-of-Concept für FFT-Performance

**Geschätzte Dauer:** 2-3 Tage

---

### Task 1.1: Projekt-Grundgerüst + Dependency Injection

**Beschreibung:**  
Erstellen eines neuen .NET Projekts (WPF/MAUI basierend auf Task 0). Einrichten der Ordnerstruktur, DI-Container und CI-Pipeline.

**Agent-Prompt:**
```
Erstelle in diesem leeren Repository eine .NET Solution namens 'LightJockey' mit folgenden Projekten:
1. LightJockey.Core (Class Library, net8.0)
2. LightJockey.Services (Class Library, net8.0)
3. LightJockey.UI (WPF Application, net8.0-windows)
4. LightJockey.Tests (xUnit Test Project, net8.0)
5. LightJockey.Common (Class Library, net8.0)

Konfiguriere Microsoft.Extensions.DependencyInjection in LightJockey.UI. 
Erstelle eine 'ServiceConfiguration.cs'-Klasse, die den DI-Container konfiguriert.

Erstelle eine Directory.Build.props-Datei mit:
- C# Language Version: 12.0
- Nullable: enable
- TreatWarningsAsErrors: true
- ImplicitUsings: enable

Füge eine .editorconfig mit C# Coding-Standards hinzu (Microsoft-Konventionen).

Erstelle einen GitHub Actions Workflow (.github/workflows/build.yml):
- runs-on: windows-latest
- Checkout, .NET 8 SDK Setup
- dotnet restore, dotnet build, dotnet test
- Artifact-Upload für Build-Outputs

Erstelle eine README.md mit Projekt-Übersicht und Quick-Start-Anleitung.
```

**Deliverables:**
- Solution-Struktur mit 5 Projekten
- DI-Container konfiguriert
- CI-Pipeline funktionsfähig
- Code-Quality-Regeln etabliert

---

### Task 1.2: Hue Bridge Kommunikation

**Beschreibung:**  
Integration von HueApi.Net. Implementierung von Bridge-Discovery, Pairing und Entertainment-Group-Abruf.

**Agent-Prompt:**
```
Integriere das NuGet-Paket 'HueApi.Net' (neueste stabile Version) in LightJockey.Services.

Erstelle ein Interface 'IHueService' in Services/Interfaces/:
- Task<IEnumerable<LocatedBridge>> FindBridgesAsync()
- Task<string> RegisterAppAsync(string bridgeIp, string appName, string deviceName)
- Task<IEnumerable<EntertainmentConfiguration>> GetEntertainmentGroupsAsync(string bridgeIp, string appKey)
- Task<bool> StartEntertainmentSessionAsync(string bridgeIp, string appKey, string groupId)
- Task StopEntertainmentSessionAsync()

Implementiere 'HueService' in Services/:
1. FindBridgesAsync: Nutze BridgeLocator für mDNS und N-UPnP Discovery
2. RegisterAppAsync: Implementiere den Link-Button-Press-Flow mit Retry-Logic (max 30 Sekunden)
3. GetEntertainmentGroupsAsync: Rufe Entertainment-Konfigurationen ab
4. Entertainment-Session-Management mit DTLS-Verschlüsselung
5. Implementiere Fehlerbehandlung mit aussagekräftigen Exceptions
6. Logge alle Operationen mit ILogger<HueService>

Registriere IHueService als Singleton im DI-Container.

Erstelle eine einfache WPF-View 'HueBridgeSetupView.xaml' mit:
- Button "Bridges suchen" → zeigt gefundene Bridges in ListView
- Button "Pairing starten" → startet Registrierung
- ListView für Entertainment-Gruppen
Erstelle ein zugehöriges ViewModel mit ICommand-Properties.
```

**Zusätzliche Anforderungen:**
- HTTPS-Zertifikats-Validierung für Bridge-Kommunikation
- Retry-Mechanismus bei Connection-Timeouts
- Detailliertes Logging aller Bridge-Operationen

**Deliverables:**
- IHueService Interface
- HueService Implementierung
- UI für Bridge-Setup
- Unit-Tests mit Mock-Responses

---

### Task 1.3: Audio-Verarbeitung

**Beschreibung:**  
Integration der Audio-Bibliothek (NAudio empfohlen). Implementierung von Audio-Capture mit Loopback.

**Agent-Prompt:**
```
Integriere das NuGet-Paket 'NAudio' (neueste Version) in LightJockey.Services.

Erstelle ein Interface 'IAudioService' in Services/Interfaces/:
- IEnumerable<AudioDevice> GetAvailableDevices()
- void StartCapture(string deviceId, Action<float[]> onSamplesReady)
- void StopCapture()
- AudioDevice CurrentDevice { get; }
- bool IsCapturing { get; }

Erstelle eine Model-Klasse 'AudioDevice' in Common/Models/:
- string Id
- string Name
- int SampleRate
- int Channels

Implementiere 'AudioService' in Services/:
1. GetAvailableDevices(): Nutze WasapiLoopbackCapture für System-Audio-Erfassung
2. StartCapture(): 
   - Initialisiere WasapiLoopbackCapture mit dem gewählten Gerät
   - Konvertiere Audio-Samples zu float[] (normalized -1.0 bis 1.0)
   - Rufe Callback mit Buffer auf (typisch 2048 Samples)
3. StopCapture(): Stoppe Capture sauber
4. Implementiere IDisposable für korrektes Resource-Management
5. Logge alle Operationen

Registriere IAudioService als Singleton.

Erweitere die Main-View um:
- ComboBox für Audio-Geräte-Auswahl
- Label für aktuellen Status (Capturing / Stopped)
- Button "Start/Stop Audio"
Erstelle zugehöriges ViewModel.
```

**Evaluierungs-Note:**  
Falls NAudio Probleme zeigt, evaluiere CSCore als Alternative.

**Deliverables:**
- IAudioService Interface
- AudioService Implementierung
- UI für Audio-Geräte-Auswahl
- Unit-Tests mit Mock-Audio-Daten

---

### Task 1.4a: FFT-Implementierung

**Beschreibung:**  
Implementierung des FFT-Prozessors für Frequenz-Analyse.

**Agent-Prompt:**
```
Erstelle eine 'FFTProcessor'-Klasse in LightJockey.Core/Audio/.

Die Klasse soll:
1. Einen Konstruktor mit konfigurierbarer FFT-Größe (default: 2048)
2. Eine Process(float[] samples)-Methode, die:
   - Window-Function anwendet (Hann, Hamming, Blackman, Rectangular - konfigurierbar)
   - FFT durchführt (nutze NAudio's FastFourierTransform wenn vorhanden, sonst eigene Implementierung)
   - Magnitude-Spektrum zurückgibt (float[] mit dB-Werten)
3. Smoothing-Mechanismus (konfigurierbarer Glättungsfaktor 0.0-1.0)
4. Properties:
   - WindowType (Enum)
   - SmoothingFactor (float)
   - FFTSize (int, read-only)

Erstelle ein Enum 'WindowType' mit: Hann, Hamming, Blackman, Rectangular

Implementiere performante Berechnungen (nutze Span<T> wo möglich).
Logge Performance-Metriken (Processing-Zeit) bei Bedarf.

Erstelle Unit-Tests:
- Test mit bekanntem Sinus-Signal
- Test für verschiedene Window-Functions
- Performance-Test (soll < 10ms sein für 2048 Samples)
```

**Deliverables:**
- FFTProcessor-Klasse
- WindowType-Enum
- Unit-Tests mit bekannten Signalen
- Performance-Benchmarks

---

### Task 1.4b: Beat-Detektor

**Beschreibung:**  
Implementierung eines Beat-Detektors mit adaptivem Threshold.

**Agent-Prompt:**
```
Erstelle eine 'BeatDetector'-Klasse in LightJockey.Core/Audio/.

Die Klasse soll:
1. Einen Konstruktor mit konfigurierbaren Parametern:
   - Sensitivity (float, 0.5 - 2.0, default: 1.0)
   - MinTimeBetweenBeats (TimeSpan, default: 200ms)
   - ThresholdMultiplier (float, default: 1.3)

2. Eine ProcessFrame(float[] frequencyMagnitudes)-Methode:
   - Berechne Energie der Bass-Frequenzen (z.B. 60-250 Hz)
   - Adaptiver Threshold basierend auf Historie (Rolling Average der letzten N Frames)
   - Beat erkannt wenn: currentEnergy > threshold * ThresholdMultiplier
   - Debouncing mit MinTimeBetweenBeats
   - Gebe 'true' zurück bei erkanntem Beat

3. BPM-Schätzung:
   - Messe Zeit zwischen Beats
   - Gleitender Durchschnitt der letzten 10 Beats
   - Property: EstimatedBPM (int)

4. Event: 'OnBeatDetected' (EventHandler)

5. Properties:
   - Sensitivity, MinTimeBetweenBeats, ThresholdMultiplier (get/set)
   - EstimatedBPM (get)
   - LastBeatTime (DateTime)

Implementiere Thread-Safety (Locks wo nötig).
Logge Beat-Events (optional, nur im Debug-Modus).

Erstelle Unit-Tests:
- Test mit simulierten Beats in festem Intervall
- Test für Debouncing
- Test für BPM-Schätzung (bekanntes Tempo)
```

**Deliverables:**
- BeatDetector-Klasse
- OnBeatDetected-Event
- Unit-Tests
- BPM-Schätzung

---

### Task 1.4c: Spektralanalyse

**Beschreibung:**  
Extraktion von Bass-, Mid- und High-Frequenzbändern.

**Agent-Prompt:**
```
Erstelle eine 'SpectralAnalyzer'-Klasse in LightJockey.Core/Audio/.

Die Klasse soll:
1. Einen Konstruktor mit Konfiguration:
   - SampleRate (int, z.B. 44100)
   - FFTSize (int)

2. Eine Analyze(float[] fftMagnitudes)-Methode, die:
   - Bass-Energie berechnet (60-250 Hz)
   - Mid-Energie berechnet (250-2000 Hz)
   - High-Energie berechnet (2000-8000 Hz)
   - Normalisiert die Werte auf 0.0-1.0
   - Gibt ein 'SpectrumData'-Objekt zurück

3. Erstelle eine 'SpectrumData'-Klasse in Core/Models/:
   - float Bass
   - float Mid
   - float High
   - float[] FrequencyBands (64 Bänder für Visualisierung)

4. Methode GetFrequencyBands(float[] fftMagnitudes, int numBands):
   - Teile Spektrum in numBands logarithmische Bänder
   - Jedes Band: Durchschnitt der Magnitudes in diesem Bereich
   - Normalisierung auf 0.0-1.0

Optimiere für Performance (Span<T>, cached Berechnungen).

Erstelle Unit-Tests:
- Test mit bekannten Frequenzen
- Test für korrekte Band-Aufteilung
```

**Deliverables:**
- SpectralAnalyzer-Klasse
- SpectrumData-Model
- Unit-Tests

---

### Task 1.4d: Audio-Profil-System

**Beschreibung:**  
Implementierung des Genre-spezifischen Audio-Profil-Systems mit JSON-Persistierung.

**Agent-Prompt:**
```
Erstelle eine 'AudioProfile'-Klasse in LightJockey.Core/Models/:
- string Name
- float BeatSensitivity
- int MinTimeBetweenBeatsMs
- float ThresholdMultiplier
- string Description

Erstelle eine 'AudioProfileManager'-Klasse in LightJockey.Core/Audio/:
1. Laden und Speichern von Profilen aus/in JSON:
   - Speicherort: AppData/Local/LightJockey/audio_profiles.json
   - Nutze System.Text.Json
2. Methoden:
   - Task<List<AudioProfile>> LoadProfilesAsync()
   - Task SaveProfilesAsync(List<AudioProfile> profiles)
   - Task<AudioProfile> GetProfileAsync(string name)
   - Task CreateProfileAsync(AudioProfile profile)
   - Task DeleteProfileAsync(string name)
3. Default-Profile (beim ersten Start erstellen):
   - **Techno**: Sensitivity=6, MinTime=150ms, Multiplier=1.4
   - **House**: Sensitivity=5, MinTime=200ms, Multiplier=1.3
   - **Ambient**: Sensitivity=3, MinTime=300ms, Multiplier=1.2
   - **Generic**: Sensitivity=4, MinTime=250ms, Multiplier=1.25

Registriere AudioProfileManager als Singleton.

Erstelle UI-View 'AudioProfileView.xaml':
- ListView mit allen Profilen
- Buttons: Create, Edit, Delete, Apply
- Eingabefelder für Profil-Parameter
Erstelle zugehöriges ViewModel.

Erstelle Unit-Tests:
- Test für JSON-Serialisierung/Deserialisierung
- Test für CRUD-Operationen
- Test für Default-Profile-Erstellung
```

**Deliverables:**
- AudioProfile-Model
- AudioProfileManager mit JSON-Persistierung
- UI für Profil-Verwaltung
- Unit-Tests

---

### Task 1.4e: Entertainment API Integration

**Beschreibung:**  
Verbindung der Audio-Analyse mit der Hue Entertainment API.

**Agent-Prompt:**
```
Erstelle eine 'EffectEngine'-Klasse in LightJockey.Core/Effects/.

Die Klasse soll:
1. Dependencies via Constructor Injection:
   - IHueService
   - FFTProcessor
   - BeatDetector
   - SpectralAnalyzer
   - ILogger<EffectEngine>

2. Methoden:
   - void Start(string bridgeIp, string appKey, string groupId, AudioProfile profile)
   - void Stop()
   - void ProcessAudioFrame(float[] samples)

3. Workflow in ProcessAudioFrame:
   - FFT auf samples anwenden
   - Spektralanalyse durchführen
   - Beat-Detection durchführen
   - Bei Beat: Licht-Update an Hue senden

4. Licht-Update-Logik (einfach für MVP):
   - Bei Beat: Alle Lichter in der Gruppe kurz hell aufleuchten lassen
   - Farbe basierend auf Spektrum: Bass=Rot, Mid=Grün, High=Blau
   - Helligkeit basierend auf Beat-Energie

5. Entertainment API V2 Setup:
   - Erstelle DTLS-verschlüsselte Verbindung
   - Implementiere Retry-Mechanismus bei Timeouts
   - Logge alle Fehler detailliert
   - Implementiere Fallback auf HTTP REST API bei Verbindungsproblemen

6. Performance-Monitoring:
   - Messe Latenz zwischen Audio-Sample und Light-Command (Ziel: < 50ms)
   - Messe Light-Update-Rate (Ziel: > 20 FPS)
   - Logge Warnungen bei Performance-Problemen
   - Property: CurrentLatencyMs, CurrentFPS

7. Properties:
   - bool IsRunning
   - float CurrentLatencyMs
   - int CurrentFPS

Implementiere Thread-Safety (Audio-Callback läuft auf anderem Thread).

Erstelle Integration-Tests:
- Test mit Mock-HueService
- Test für Performance-Metriken
- Test für Fehlerbehandlung (Connection-Loss)
```

**Zusätzliche Anforderungen:**
- DTLS-Verschlüsselung korrekt implementiert
- Retry-Mechanismus bei Timeouts
- Detailliertes Error-Logging
- Performance-Counter in UI anzeigen

**Deliverables:**
- EffectEngine-Klasse
- Entertainment API V2 Integration
- Performance-Monitoring
- Integration-Tests

---

### Task 1.5: UI-Verbindung + Performance-Monitoring

**Beschreibung:**  
Zusammenführung aller Komponenten in einer funktionsfähigen UI mit Performance-Anzeige.

**Agent-Prompt:**
```
Erstelle ein 'MainViewModel' in LightJockey.UI/ViewModels/.

Dependencies via Constructor Injection:
- IHueService
- IAudioService
- EffectEngine
- AudioProfileManager
- ILogger<MainViewModel>

Properties:
- ObservableCollection<AudioDevice> AudioDevices
- ObservableCollection<EntertainmentConfiguration> EntertainmentGroups
- ObservableCollection<AudioProfile> AudioProfiles
- AudioDevice SelectedAudioDevice
- EntertainmentConfiguration SelectedGroup
- AudioProfile SelectedProfile
- bool IsRunning
- string StatusText
- float CurrentLatencyMs
- int CurrentFPS

Commands (ICommand):
- RefreshAudioDevicesCommand
- FindBridgesCommand
- StartPairingCommand
- RefreshGroupsCommand
- StartCommand
- StopCommand

Implementiere MVVM-Pattern korrekt:
- Nutze INotifyPropertyChanged
- Nutze RelayCommand (oder CommunityToolkit.Mvvm)
- Fehlerbehandlung mit User-Feedback (MessageBox/Dialog)

Erstelle/Erweitere 'MainWindow.xaml':
Layout mit Bereichen:
1. **Audio-Sektion:**
   - ComboBox: Audio-Geräte
   - Button: Refresh
   - Label: Status
2. **Hue-Sektion:**
   - Button: Bridges suchen
   - Button: Pairing
   - ListView: Entertainment-Gruppen
3. **Profile-Sektion:**
   - ComboBox: Audio-Profile
   - Button: Profile verwalten
4. **Control-Sektion:**
   - Button: Start/Stop (grün/rot)
   - Label: Status-Text
5. **Performance-Monitoring:**
   - Label: "Latenz: {CurrentLatencyMs} ms"
   - Label: "FPS: {CurrentFPS}"
   - Warnungs-Label (rot) wenn Latenz > 50ms oder FPS < 20

Nutze Data-Binding für alle UI-Elemente.

Implementiere Update-Mechanismus für Performance-Metriken (z.B. Timer, alle 500ms).

Erstelle UI-Tests (falls Tooling verfügbar) oder manuelle Test-Checkliste.
```

**Deliverables:**
- MainViewModel mit allen Commands
- MainWindow.xaml mit vollständiger UI
- Performance-Monitoring in UI
- MVVM-Pattern korrekt umgesetzt

---

### Task 1.6: HTTPS Web Request Tester

**Beschreibung:**  
Debug-Tool zum manuellen Senden von HTTPS-Befehlen an die Bridge.

**Agent-Prompt:**
```
Erstelle eine neue View 'HttpsTesterView.xaml' und ViewModel.

UI-Elemente:
- TextBox: Bridge-IP
- TextBox: App-Key
- ComboBox: HTTP-Methode (GET, POST, PUT, DELETE)
- TextBox: API-Endpunkt (z.B. '/clip/v2/resource/light')
- TextBox (Multiline): JSON-Body
- Button: Senden
- TextBox (Multiline, ReadOnly): Response

ViewModel mit:
- Properties für alle Eingabefelder
- SendRequestCommand (ICommand)
- Response (string)

Implementiere SendRequestCommand:
1. Erstelle HttpClient mit BaseAddress = https://{bridgeIp}
2. Füge Header hinzu: "hue-application-key: {appKey}"
3. Sende Request basierend auf Methode
4. Zeige Response (formatiert) in Response-TextBox
5. Fehlerbehandlung mit Try-Catch, zeige Exception-Details

Füge MenuItem in MainWindow hinzu: "Tools" > "HTTPS Tester"

Speichere letzte Eingaben (Bridge-IP, App-Key) in ConfigurationService.
```

**Deliverables:**
- HttpsTesterView + ViewModel
- Integration in MainWindow
- Persistierung der Eingaben

---

### Task 1.7: Konfigurationspersistierung

**Beschreibung:**  
Speichern und Laden von App-Konfigurationen.

**Agent-Prompt:**
```
Erstelle eine 'AppConfiguration'-Klasse in LightJockey.Common/Models/:
- string LastBridgeIp
- string LastAppKey (verschlüsselt)
- string LastAudioDeviceId
- string LastEntertainmentGroupId
- string LastAudioProfileName

Erstelle ein Interface 'IConfigurationService' in Services/Interfaces/:
- Task<AppConfiguration> LoadAsync()
- Task SaveAsync(AppConfiguration config)

Implementiere 'ConfigurationService' in Services/:
1. Speicherort: AppData/Local/LightJockey/config.json
2. Nutze System.Text.Json für Serialisierung
3. **Security:** Verschlüssle LastAppKey mit Data Protection API (DPAPI)
   - Nutze System.Security.Cryptography.ProtectedData
   - ProtectedData.Protect() beim Speichern
   - ProtectedData.Unprotect() beim Laden
4. Auto-Create Verzeichnis falls nicht vorhanden
5. Fehlerbehandlung (Corrupt-File → Default-Config zurückgeben)
6. Logge Operationen

Registriere IConfigurationService als Singleton.

Erweitere MainViewModel:
- Lade Config beim Start (Constructor oder OnLoaded)
- Auto-Save bei Änderungen der Auswahl (SelectedAudioDevice, etc.)
- Setze UI-Elemente basierend auf letzter Config

Erstelle Unit-Tests:
- Test für Serialisierung/Deserialisierung
- Test für DPAPI-Verschlüsselung
- Test für Corrupt-File-Handling
```

**Security-Note:**  
App-Keys müssen verschlüsselt gespeichert werden. DPAPI nutzen (Windows-spezifisch ist OK).

**Deliverables:**
- AppConfiguration-Model
- IConfigurationService + Implementierung
- DPAPI-Verschlüsselung
- Auto-Load/Save in UI
- Unit-Tests

---

### Task 1.8: Logging und Error-Handling

**Beschreibung:**  
Integration von Serilog und globalem Exception-Handling.

**Agent-Prompt:**
```
Integriere NuGet-Pakete in LightJockey.UI:
- Serilog
- Serilog.Sinks.File
- Serilog.Sinks.Console
- Serilog.Extensions.Logging
- Microsoft.Extensions.Logging

Konfiguriere Serilog in App.xaml.cs (OnStartup):
1. Log-Datei: AppData/Local/LightJockey/logs/lightjockey-.log
2. Rolling-Interval: Daily
3. Output-Template: Timestamp, Level, Message, Exception
4. Console-Sink für Debug-Builds (#if DEBUG)
5. Minimum-Level: Information (Debug-Build: Debug)

Konfiguriere globalen Exception-Handler in App.xaml.cs:
1. AppDomain.CurrentDomain.UnhandledException
2. Application.DispatcherUnhandledException
3. TaskScheduler.UnobservedTaskException
4. Logge alle unbehandelten Exceptions
5. Zeige User einen Error-Dialog mit Fehlermeldung und Log-Pfad
6. Erstelle einen "ErrorDialogService" für User-Feedback

Registriere ILoggerFactory im DI-Container.
Injiziere ILogger<T> in alle Services/ViewModels.

Füge Logging zu allen Services hinzu:
- Information: Operation-Start/End
- Warning: Performance-Probleme
- Error: Exceptions, API-Fehler
- Debug: Detaillierte Ablauf-Informationen

Erstelle eine 'ErrorDialogService'-Klasse:
- Methode: ShowError(string title, string message, Exception ex)
- Zeigt MessageBox mit Details
- Bietet "Log-Datei öffnen"-Button

Erstelle Unit-Tests:
- Test für Log-File-Creation
- Mock-Tests für Logger-Aufrufe
```

**Deliverables:**
- Serilog-Integration
- Globaler Exception-Handler
- ErrorDialogService
- Logging in allen Services
- Unit-Tests

---

### Task 1.9: Unit-Tests für HueService

**Agent-Prompt:**
```
Erstelle Test-Klasse 'HueServiceTests' in LightJockey.Tests/Services/.

Setup:
- Nutze xUnit
- Nutze Moq für Mocking

Tests:
1. **FindBridgesAsync_ShouldReturnBridges**
   - Mock Network-Response mit bekannter Bridge
   - Verifiziere, dass Bridge gefunden wird

2. **RegisterAppAsync_ShouldReturnAppKey**
   - Mock Button-Press-Simulation
   - Verifiziere, dass App-Key zurückgegeben wird

3. **GetEntertainmentGroupsAsync_ShouldReturnGroups**
   - Mock JSON-Response mit Entertainment-Gruppen
   - Verifiziere Deserialisierung

4. **StartEntertainmentSessionAsync_ShouldConnectViaDTLS**
   - Mock DTLS-Setup
   - Verifiziere Success-Status

5. **StartEntertainmentSessionAsync_ShouldRetryOnTimeout**
   - Mock Timeout-Exception
   - Verifiziere Retry-Logic

Nutze Arrange-Act-Assert-Pattern.
Alle Tests müssen grün sein.
```

**Deliverables:**
- HueServiceTests mit 5+ Tests
- Alle Tests grün

---

### Task 1.10: Unit-Tests für AudioService

**Agent-Prompt:**
```
Erstelle Test-Klasse 'AudioServiceTests' in LightJockey.Tests/Services/.

Tests:
1. **GetAvailableDevices_ShouldReturnDevices**
   - Mock Audio-System
   - Verifiziere Device-Liste

2. **StartCapture_ShouldInvokeCallback**
   - Mock Audio-Capture
   - Verifiziere, dass Callback mit Samples aufgerufen wird

3. **StopCapture_ShouldStopCapture**
   - Verifiziere, dass IsCapturing = false nach Stop

4. **StartCapture_WithInvalidDevice_ShouldThrow**
   - Verifiziere Exception bei ungültigem Gerät

Nutze Mock-Audio-Daten (bekannte Sample-Arrays).
```

**Deliverables:**
- AudioServiceTests mit 4+ Tests
- Alle Tests grün

---

### Task 1.11: Integration-Tests für EffectEngine

**Agent-Prompt:**
```
Erstelle Test-Klasse 'EffectEngineIntegrationTests' in LightJockey.Tests/Integration/.

Setup:
- Mock-HueService (simuliert Bridge-Responses)
- Echte FFTProcessor, BeatDetector, SpectralAnalyzer

Tests:
1. **ProcessAudioFrame_WithBeat_ShouldSendLightUpdate**
   - Erstelle Audio-Frame mit simuliertem Beat
   - Verifiziere, dass HueService.SendUpdate() aufgerufen wurde

2. **Start_ShouldConnectToBridge**
   - Verifiziere Entertainment-Session-Aufbau

3. **ProcessAudioFrame_ShouldMeetPerformanceTarget**
   - Messe Latenz von ProcessAudioFrame
   - Assert: Latenz < 50ms

4. **ProcessAudioFrame_WithMultipleFrames_ShouldMaintainFPS**
   - Simuliere 100 Frames
   - Verifiziere FPS > 20

Nutze Stopwatch für Performance-Messungen.
```

**Deliverables:**
- Integration-Tests mit 4+ Tests
- Performance-Validierung
- Alle Tests grün

---

## 5. Phase 2: Erweiterte Features und UI/UX

**Ziel:** Verbesserung der Benutzererfahrung und Implementierung der fehlenden Features aus PhotonJockey.

---

### Task 2.1: Audio-Visualisierungen (Waveform + Spectrum)

**Beschreibung:**  
Implementierung eines Visualizer-Dashboards ähnlich der Java-Version.

**Features:**
- Echtzeit-Waveform-Anzeige (Zeitbereich)
- Frequenzspektrum-Darstellung (64 Bänder als Balkendiagramm)
- Beat-Indikator (visuelles Feedback)
- Live-BPM-Anzeige

**Canvas-Rendering:**
- WPF: WritableBitmap oder DrawingVisual
- Performance: > 30 FPS für flüssige Darstellung

**UI-Controls:**
- Gain-Slider (Amplitude)
- Beat-Sensitivity-Slider

---

### Task 2.2: Light Mapping Configuration

**Beschreibung:**  
Zuordnung von Lichtern zu räumlichen Positionen für erweiterte Effekte.

**Features:**
- Drag & Drop Interface für Licht-Positionierung
- JSON-basierte Speicherung der Konfiguration
- Visualisierung des Raum-Layouts
- Import/Export von Mapping-Profilen

**Datenstruktur:**
```json
{
  "mappings": [
    {
      "lightId": "light-1",
      "name": "Deckenlampe Links",
      "position": { "x": 0.2, "y": 0.8 },
      "zone": "bass"
    }
  ]
}
```

---

### Task 2.3: Custom Color Sets

**Beschreibung:**  
User-definierte Farbpaletten für Effekte.

**Features:**
- Color-Picker für Farbauswahl
- Speichern/Laden von Color-Sets
- Vordefinierte Sets (Warm, Cool, Rainbow, etc.)
- Anwendung auf Effekte

---

### Task 2.4: Multi-Bridge-Support

**Beschreibung:**  
Gleichzeitige Kontrolle mehrerer Hue Bridges.

**Features:**
- Verwaltung mehrerer Bridge-Konfigurationen
- Parallele Entertainment-Sessions
- UI für Bridge-Auswahl
- Synchronisation der Effekte über Bridges

**Architektur:**
- HueService erweitern für Multi-Bridge
- Separate Entertainment-Sessions pro Bridge
- Thread-Safety bei parallelen Updates

---

### Task 2.5: Erweiterte Effekte

**Beschreibung:**  
Implementierung zusätzlicher Effekt-Typen.

**Effekte aus PhotonJockey:**
1. **Strobe-Effect:** Schnelles Blinken synchron zum Beat
2. **Color-Wave:** Farbwelle läuft durch Lichter
3. **Spectrum-Mapping:** Frequenzbänder auf verschiedene Lichter
4. **Ambient-Mode:** Sanfte Farbübergänge basierend auf Audio-Energie

**Plugin-Architektur:**
- Interface: IEffect
- Dynamisches Laden von Effect-Assemblies
- UI für Effekt-Auswahl und -Parameter

---

### Task 2.6: Brightness & Transition Calibration

**Beschreibung:**  
Automatische Kalibrierung für optimale Licht-Performance.

**Features:**
- **Brightness-Calibration:** Automatische Anpassung der Helligkeit
- **Transition-Time-Calibration:** Optimierung der Übergangszeiten basierend auf Netzwerk-Latenz

---

### Task 2.7: Farb-Themes und Dark-Mode

**Beschreibung:**  
Anpassung der UI an Windows-Themes.

**Features:**
- Automatische Erkennung von Windows Dark-Mode
- Light/Dark Theme für App
- Benutzerdefinierte Accent-Colors (Windows 11 Integration)

---

### Task 2.8: Drag & Drop Layout-System

**Beschreibung:**  
Flexibles UI-Layout für Benutzeranpassung.

**Features:**
- Verschiebbare/Vergrößerbare UI-Panels
- Speichern von Layout-Konfigurationen
- Reset auf Standard-Layout

---

## 6. Phase 3: Fortgeschrittene Features

**Ziel:** Optimierungen, Plugin-System und Packaging.

---

### Task 3.1: Performance-Optimierungen

- Profiling und Bottleneck-Analyse
- Multi-Threading-Optimierungen
- Memory-Pool für Audio-Buffers
- GPU-beschleunigte FFT (falls verfügbar)

---

### Task 3.2: Plugin-API

**Beschreibung:**  
Öffentliche API für benutzerdefinierte Effekte.

**Deliverables:**
- NuGet-Package: LightJockey.PluginSDK
- Dokumentation für Plugin-Entwicklung
- Beispiel-Plugins

**API:**
```csharp
public interface ILightJockeyEffect
{
    string Name { get; }
    string Description { get; }
    void Initialize(IEffectContext context);
    void ProcessFrame(AudioData audioData, LightUpdate[] lights);
    void Dispose();
}
```

---

### Task 3.3: Installer und Packaging

**Features:**
- MSI-Installer mit WiX Toolset
- Auto-Update-Mechanismus
- Digitale Signatur
- Windows Store Distribution (optional)

---

### Task 3.4: Telemetrie und Crash-Reporting (Optional)

**Features:**
- Anonyme Nutzungsstatistiken (Opt-In)
- Crash-Reports an Backend
- Performance-Metriken

**Privacy:**
- GDPR-konform
- Opt-In mit klarer Erklärung
- Keine PII (Personally Identifiable Information)

---

## 7. Code-Qualität und Best Practices

### 7.1 Code-Standards

- **.editorconfig:** Microsoft C# Konventionen
- **StyleCop:** Enforced in CI
- **Roslynator:** Code-Analyse
- **Nullable Reference Types:** Enabled
- **XML-Dokumentation:** Für alle öffentlichen APIs

### 7.2 Testing-Strategie

- **Unit-Tests:** Alle Services und Core-Logik (> 80% Coverage)
- **Integration-Tests:** End-to-End-Flows
- **Performance-Tests:** Benchmarks für kritische Pfade
- **UI-Tests:** Manuelle Test-Checkliste (oder automatisiert falls Tooling verfügbar)

### 7.3 Security

- **DPAPI:** Verschlüsselte Speicherung von App-Keys
- **HTTPS-Validierung:** Zertifikats-Prüfung für Bridge-Kommunikation
- **Input-Validation:** Alle User-Eingaben validieren
- **Dependency-Scanning:** Regelmäßige Überprüfung auf bekannte Vulnerabilities (z.B. Dependabot)

### 7.4 Dokumentation

- **README.md:** Quick-Start, Build-Anleitung
- **ARCHITECTURE.md:** Architektur-Übersicht
- **ADRs (Architecture Decision Records):** Wichtige Entscheidungen dokumentieren
- **User-Manual:** Parallel zur Entwicklung
- **API-Dokumentation:** XML-Kommentare → DocFX

### 7.5 CI/CD-Pipeline

**GitHub Actions Workflows:**
1. **Build & Test:** Bei jedem Push/PR
   - Checkout, .NET SDK Setup
   - Restore, Build, Test
   - Code-Coverage-Report
   - Upload Artifacts
2. **Code-Quality:** StyleCop, Roslynator
3. **Release:** Bei Git-Tag
   - Build Release-Version
   - Erstelle Installer
   - Publish zu GitHub Releases
4. **Dependency-Check:** Wöchentlich (Dependabot)

---

## 8. Projektmanagement

### 8.1 Entwicklungs-Workflow

1. **Repository erstellen:** Neues leeres Repo "LightJockey"
2. **Task 0:** Technologie-Evaluierung (ADR erstellen)
3. **Phase 1:** Task für Task abarbeiten
4. **Code-Review:** Nach jedem Task (automatisiert + manuell)
5. **Testing:** Kontinuierlich mit TDD-Ansatz
6. **Dokumentation:** Parallel zur Entwicklung
7. **Release:** Nach Phase 1 → Alpha-Release

### 8.2 Milestones

- **M1 - MVP (Phase 1):** Funktionierende Kern-App (Ziel: 4-6 Wochen)
- **M2 - Feature-Complete (Phase 2):** Alle PhotonJockey-Features (Ziel: +4 Wochen)
- **M3 - Optimized (Phase 3):** Performance, Plugin-API, Installer (Ziel: +2 Wochen)
- **M4 - Release 1.0:** Produkt ionsreif

### 8.3 Erfolgskriterien

**Phase 1 (MVP):**
- [x] Alle Tasks 1.1 - 1.11 abgeschlossen
- [x] Alle Unit- und Integration-Tests grün
- [x] CI-Pipeline grün
- [x] Audio-Latenz < 50ms
- [x] Light-Update-Rate > 20 FPS
- [x] Code-Coverage > 70%

**Phase 2:**
- [x] Alle PhotonJockey-Features implementiert
- [x] Code-Coverage > 80%
- [x] User-Manual vorhanden

**Phase 3:**
- [x] Installer funktionsfähig
- [x] Plugin-SDK veröffentlicht
- [x] Performance-Ziele erreicht

---

## 9. Risiken und Mitigationen

| Risiko | Wahrscheinlichkeit | Impact | Mitigation |
|--------|-------------------|--------|------------|
| **Entertainment API V2 Probleme** | Mittel | Hoch | HTTP REST API als Fallback implementieren |
| **Audio-Latenz zu hoch** | Niedrig | Hoch | Frühzeitig Performance-Tests, ggf. Threading-Optimierung |
| **UI-Framework-Performance** | Mittel | Mittel | Task 0: Evaluierung mit Prototyp |
| **NAudio-Inkompatibilität** | Niedrig | Mittel | CSCore als Backup evaluieren |
| **DPAPI-Plattform-Einschränkung** | Niedrig | Niedrig | Akzeptiert (Windows-only App) |
| **Scope-Creep (Phase 2/3)** | Hoch | Mittel | Strikte Priorisierung, MVP zuerst |

---

## 10. Offene Fragen (zu klären vor Start)

1. **Repository-Name:** "LightJockey" bestätigt?
2. **UI-Framework:** Finale Entscheidung WPF vs. MAUI vs. WinUI 3 (Task 0)
3. **Audio-Bibliothek:** Finale Entscheidung NAudio vs. CSCore (Task 0)
4. **Lizenz:** MIT? Apache 2.0? GPL?
5. **Branding:** Logo, Farben, Design-System?

---

## 11. Anhang

### 11.1 Referenzen

- **PhotonJockey (Java):** `/docs/FEATURES_OVERVIEW.md`
- **HueApi.Net:** https://github.com/michielpost/Q42.HueApi
- **NAudio:** https://github.com/naudio/NAudio
- **Serilog:** https://serilog.net/
- **WPF Best Practices:** Microsoft Docs

### 11.2 Glossar

- **FFT:** Fast Fourier Transform
- **DTLS:** Datagram Transport Layer Security
- **DPAPI:** Data Protection API (Windows)
- **MVVM:** Model-View-ViewModel
- **DI:** Dependency Injection
- **ADR:** Architecture Decision Record
- **MVP:** Minimum Viable Product

---

## 12. Änderungshistorie

| Version | Datum | Änderungen | Autor |
|---------|-------|------------|-------|
| 1.0 | 2025-11-10 | Initiale Version | KI-Agent |
| 2.0 | 2025-11-11 | Integration aller Review-Empfehlungen, Konsolidierung zu LightJockey | Copilot |

---

**Ende des Dokuments**

Nächster Schritt: **Task 0 - Technologie-Evaluierung** starten nach Bestätigung der offenen Fragen.
