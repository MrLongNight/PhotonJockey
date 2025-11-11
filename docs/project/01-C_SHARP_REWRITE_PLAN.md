# Projektplan: PhotonJockey C# Neuentwicklung

**Dokument-ID:** `01-C_SHARP_REWRITE_PLAN.md`  
**Datum:** 2025-11-10  
**Status:** Überarbeitet nach Review  
**Review-Dokument:** ⚠️ [Analyse und Optimierungsvorschläge](C_SHARP_REWRITE_PLAN_REVIEW.md)

> **Hinweis:** Es existiert ein detailliertes Review-Dokument mit kritischen Problemen, Risiken und Optimierungsvorschlägen. Bitte vor Beginn der Implementierung durchlesen!
**Dokument-ID:** `01-C_SHARP_REWRITE_PLAN.md`
**Datum:** 2025-11-10
**Status:** Finalisiert nach Experten-Review

---

## 1. Übersicht und Ziele

Dieses Dokument beschreibt den detaillierten Plan für die Neuentwicklung der PhotonJockey-Anwendung in C#. Die Umsetzung soll in einem **neuen, leeren GitHub Repository** erfolgen und ausschließlich durch KI-Coding-Tools gesteuert werden. Der Plan wurde nach einem detaillierten Review überarbeitet, um Risiken zu minimieren und bewährte Praktiken aus der bestehenden Java-Anwendung zu übernehmen.

**Leitprinzipien:**
- **Robuste Architektur:** Eine modulare, testbare und erweiterbare Codebasis von Anfang an. Multi-Bridge-Fähigkeit wird architektonisch berücksichtigt.
- **Performance:** Die Anwendung muss Echtzeit-Audio-Analyse und Licht-Synchronisation mit geringer Latenz (< 50ms) gewährleisten.
- **Qualität:** Testbarkeit, Logging, Fehlerbehandlung und Code-Metriken sind integraler Bestandteil jeder Phase.
- **KI-gesteuerte Entwicklung:** Der Prozess wird durch präzise, risiko-minimierte Prompts für KI-Agenten gesteuert.

---

## 2. Technologie-Stack (Finalisiert)

- **Programmiersprache:** C#
- **UI-Framework:** **WPF (Windows Presentation Foundation)** - Ausgewählt aufgrund von Reife, Performance und exzellenter Eignung für Echtzeit-Visualisierungen.
- **Audio-Analyse-Bibliothek:** **Wird in Task 1.0 evaluiert (NAudio als primärer Kandidat)**
- **Hue-Kommunikations-Bibliothek:** HueApi.Net
- **DI-Container:** Microsoft.Extensions.DependencyInjection (mit Option auf Autofac/DryIoc)
- **CI/CD:** GitHub Actions

---

## 3. Übergreifende Qualitätsanforderungen

- **Security:** App-Keys werden über den Windows Credential Manager gespeichert. Die Kommunikation zur Bridge wird durch Certificate Pinning abgesichert.
- **Code-Qualität:** `.editorconfig`, Roslyn-Analyzer und StyleCop werden zur Sicherstellung eines einheitlichen Code-Stils verwendet.
- **Dokumentation:** Öffentliche APIs werden mit XML-Kommentaren dokumentiert. Wichtige Entscheidungen werden in Architecture Decision Records (ADRs) festgehalten.

---

## 4. Entwicklungsphasen

### Phase 1: MVP - Stabiles Fundament

**Ziel:** Ein testbarer Prototyp mit einer robusten Architektur, der die Kernfunktionalität (Audio -> Licht) zuverlässig abbildet.

---

**Task 1.0: Technologie-Evaluierung & Proof-of-Concept (PoC)**
- **Beschreibung:** Praktische Validierung der Kerntechnologien (UI und Audio), um technische Risiken frühzeitig auszuschließen.
- **Agent-Prompt:** "Erstelle ein Dokument `docs/project/02-TECHNOLOGY_EVALUATION.md` und implementiere Proof-of-Concepts:
  1.  **UI-Framework (WPF):** Erstelle ein PoC-Projekt. Implementiere ein Fenster mit einem Canvas-Element, das 64 animierte Balken (Spektrum-Simulation) bei stabilen 60 FPS rendert. Messe die CPU-Last und Framerate, um die Performance zu validieren.
  2.  **Audio-Bibliothek (NAudio vs. CSCore):** Erstelle für jede Bibliothek ein PoC (NAudio ist der Favorit). Implementiere Loopback-Aufnahme, eine FFT (4096 Samples, Hann Window) und extrahiere 64 Frequenzbänder. Messe Latenz und CPU-Last.
  3.  **Dokumentiere** die Ergebnisse und gib eine finale, begründete Empfehlung für die Audio-Bibliothek ab."

**Task 1.1: Projekt-Grundgerüst & CI/CD**
- **Beschreibung:** Aufsetzen des finalen WPF-Projekts, Konfiguration von DI, Logging, Code-Qualitäts-Tools und einer robusten CI-Pipeline.
- **Agent-Prompt:** "Erstelle ein neues WPF-Projekt für `.net8.0-windows`. Richte Ordner für 'Core', 'Services', 'UI', 'ViewModels' ein. Konfiguriere `Microsoft.Extensions.DependencyInjection`, Serilog und `.editorconfig`. Richte einen GitHub Actions CI-Workflow mit `runs-on: ${{ matrix.os }}` und einer Matrix für `windows-2019` und `windows-2022` ein."

**Task 1.2: Hue Bridge Kommunikation & Sicherheit**
- **Beschreibung:** Integration von `HueApi.Net` inklusive sicherem App-Key-Handling und Certificate Pinning. Die Architektur muss von Anfang an für Multi-Bridge ausgelegt sein.
- **Agent-Prompt:** "Integriere 'HueApi.Net' und 'CredentialManagement'. Erstelle einen 'HueService', der intern mit einem `Dictionary<string, BridgeClient>` arbeitet, um Multi-Bridge zu ermöglichen. Implementiere:
  1. Bridge-Suche.
  2. App-Registrierung mit Speicherung des App-Keys im Windows Credential Manager.
  3. Abruf von Entertainment-Gruppen.
  4. Certificate Pinning: Speichere den Zertifikats-Fingerprint bei der ersten Verbindung und validiere ihn bei nachfolgenden."

**Task 1.3: Audio-Verarbeitung mit Reactive Extensions**
- **Beschreibung:** Integration der in Task 1.0 gewählten Audio-Bibliothek mittels `System.Reactive` für einen robusten und testbaren Audio-Stream.
- **Agent-Prompt:** "Integriere die gewählte Audio-Bibliothek. Erstelle einen 'AudioService'. Implementiere die Auflistung von Audio-Geräten. Erstelle einen `IObservable<AudioFrame>`-Stream, der die Audio-Daten vom Loopback-Gerät als reaktiven Stream zur Verfügung stellt."

**Task 1.4: Audio-Analyse-Engine (inkl. Profile)**
- **Beschreibung:** Implementierung der vollständigen Audio-Analyse-Pipeline, inklusive eines erweiterbaren Profil-Systems.
- **Agent-Prompt:** "Erstelle eine 'AudioAnalysisEngine', die den reaktiven Audio-Stream abonniert.
  - **1.4a (FFT & Pooling):** Implementiere eine FFT. Nutze `System.Buffers.ArrayPool` für die FFT-Buffer, um die GC-Last zu minimieren.
  - **1.4b (Spektralanalyse):** Extrahiere Bass-, Mid- und High-Frequenzbänder.
  - **1.4c (Beat-Detektor):** Implementiere einen Beat-Detektor mit konfigurierbaren Parametern.
  - **1.4d (Audio-Profile):** Implementiere ein vollständiges Audio-Profil-System (wie in Java), das Konfigurationen aus JSON-Dateien laden und verwalten kann."

**Task 1.5: Entertainment API V2 Integration (Aufgeteilt)**
- **Beschreibung:** Implementierung der komplexen Entertainment API V2 Kommunikation in mehreren Schritten.
- **Agent-Prompt:** "Implementiere die Entertainment API V2-Kommunikation in einer 'EntertainmentStreamer'-Klasse:
  - **1.5a (Grundlagen):** Implementiere das DTLS-Setup und das Connection-Handling.
  - **1.5b (Message Encoding):** Implementiere die binäre Serialisierung der Licht-Kommandos.
  - **1.5c (Light Streaming):** Sende die serialisierten Kommandos als UDP-Stream mit FPS-Kontrolle.
  - **1.5d (Fehlerbehandlung):** Implementiere eine robuste Reconnection-Logik.
  - **1.5e (Performance-Monitoring):** Integriere Latenz- und FPS-Messungen."

**Task 1.6: Konfigurations-Management**
- **Beschreibung:** Speichern und Laden von Benutzerkonfigurationen mit einer robusten Migrationsstrategie.
- **Agent-Prompt:** "Erstelle einen 'ConfigurationService' mit `Microsoft.Extensions.Configuration`. Lade `appsettings.json`. Implementiere eine Konfigurationsklasse mit einer `ConfigVersion`-Eigenschaft, um zukünftige Migrationen zu ermöglichen."

**Task 1.7: Light Mapping Grundlagen**
- **Beschreibung:** Implementierung eines einfachen Systems zur Zuordnung von Lichtern zu logischen Positionen.
- **Agent-Prompt:** "Erstelle eine 'LightMappingService'-Klasse. Implementiere das Laden und Speichern einer JSON-Datei, die Hue-Light-IDs auf 2D-Koordinaten (X/Y) mappt. Diese grundlegende Zuordnung ist für gezielte Effekte in späteren Phasen erforderlich."

**Task 1.8: UI des Prototyps verbinden**
- **Beschreibung:** Verknüpfung aller Komponenten mit einer einfachen, funktionalen WPF-Benutzeroberfläche.
- **Agent-Prompt:** "Erstelle die `MainView.xaml` und das zugehörige `MainViewModel`. Binde alle notwendigen Funktionen (Geräteauswahl, Gruppenauswahl, Start/Stop, Performance-Metriken) an das ViewModel."

**Task 1.9: Tests (Unit & Integration)**
- **Beschreibung:** Erstellung von Tests für die Kernlogik und die gesamte Verarbeitungskette.
- **Agent-Prompt:** "Erstelle ein Test-Projekt mit xUnit und Moq.
  - **1.9a (Unit-Tests):** Teste `HueService` und `AudioService` isoliert.
  - **1.9b (Integration-Tests):** Erstelle einen Test, der die gesamte Kette von einem Mock-Audiosignal bis zum (gemockten) Licht-Kommando validiert und die End-to-End-Latenz misst."

**Task 1.10: Performance-Benchmarks**
- **Beschreibung:** Erstellung von Performance-Benchmarks für die kritischen Pfade.
- **Agent-Prompt:** "Erstelle ein Benchmark-Projekt mit `BenchmarkDotNet`. Implementiere Benchmarks für die FFT-Berechnung und die Light-Update-Serialisierung, um Performance-Ziele zu validieren."

**Task 1.11: Release-Pipeline**
- **Beschreibung:** Automatisierung der Erstellung von Releases.
- **Agent-Prompt:** "Erstelle eine `.github/workflows/release.yml`-Datei. Konfiguriere sie so, dass bei einem Git-Tag automatisch ein Build erstellt, mit dem WiX Toolset ein MSI-Installer paketiert und ein GitHub Release erzeugt wird."

---

### Phase 2: Erweiterte Features & UI/UX

- **Task 2.1:** **Visualizer Dashboard:** UI für Waveform und Frequenz-Spektrum.
- **Task 2.2:** **Anpassbare UI:** Drag & Drop-Layout-System.
- **Task 2.3:** **Kalibrierung:** UI für Helligkeits- und Übergangszeit-Kalibrierung.
- **Task 2.4:** **UI-Tests:** Erstellung von UI-Tests mit FlaUI für kritische Workflows.
- **Task 2.5:** **Auto-Update & Crash-Reporting:** Integration von Squirrel.Windows und Sentry.io.
- **Task 2.6:** **Migrations-Guide:** Erstellung einer Anleitung zum Umstieg von der Java-Version.

---

### Phase 3: Skalierung und Optimierung

- **Task 3.1:** **Plugin-Architektur:** Design und Implementierung einer API für benutzerdefinierte Effekte.
- **Task 3.2:** **Accessibility:** Sicherstellung der Barrierefreiheit (Screen-Reader, Keyboard-Navigation).
- **Task 3.3:** **Performance-Optimierung:** Tiefgehende Analyse und Optimierung der gesamten Pipeline.
