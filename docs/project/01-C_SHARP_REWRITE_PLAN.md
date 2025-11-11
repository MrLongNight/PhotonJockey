# Projektplan: PhotonJockey C# Neuentwicklung

**Dokument-ID:** `01-C_SHARP_REWRITE_PLAN.md`
**Datum:** 2025-11-10
**Status:** Überarbeitet nach Review

---

## 1. Übersicht und Ziele

Dieses Dokument beschreibt den detaillierten Plan für die Neuentwicklung der PhotonJockey-Anwendung in C#. Die Umsetzung soll in einem **neuen, leeren GitHub Repository** erfolgen und ausschließlich durch KI-Coding-Tools gesteuert werden.

**Leitprinzipien:**
- **Modulare Architektur:** Klare Trennung von UI, Geschäftslogik und Services.
- **Testbarkeit & Qualität:** Von Anfang an auf Unit-, Integrations- und Systemtests auslegen.
- **Moderne UI/UX:** Eine performante, ansprechende und intuitive Benutzeroberfläche.
- **KI-gesteuerte Entwicklung:** Der gesamte Prozess wird durch detaillierte Prompts für KI-Agenten gesteuert.

---

## 2. Technologie-Stack

- **Programmiersprache:** C#
- **UI-Framework:** **Wird in Task 1.0 evaluiert (WPF, WinUI 3, .NET MAUI)**
- **Audio-Analyse-Bibliothek:** **Wird in Task 1.3 evaluiert (NAudio, CSCore)**
- **Hue-Kommunikations-Bibliothek:** HueApi.Net
- **Build-System:** .NET CLI / MSBuild
- **CI/CD:** GitHub Actions

---

## 3. Übergreifende Qualitätsanforderungen

Diese Anforderungen sind in allen Phasen und Tasks zu berücksichtigen.

### 3.1. Security
- **App-Keys/Secrets:** Müssen sicher gespeichert werden (z.B. mittels .NET Data Protection API).
- **Netzwerkkommunikation:** HTTPS-Zertifikate der Bridge müssen validiert werden.

### 3.2. Code-Qualität
- **Coding Standards:** Ein `.editorconfig` zur Durchsetzung von C#-Coding-Standards wird in Task 1.1 eingerichtet.
- **Code-Analyse:** Statische Code-Analyse-Tools (z.B. Roslynator, StyleCop) werden integriert.

### 3.3. Dokumentation
- **API-Dokumentation:** Alle öffentlichen Methoden und Klassen werden mit XML-Kommentaren dokumentiert.
- **Architektur-Entscheidungen:** Wichtige Entscheidungen (z.B. die Wahl des UI-Frameworks) werden als Architecture Decision Records (ADRs) im `docs/` Verzeichnis festgehalten.

---

## 4. Entwicklungsphasen

### Phase 1 (MVP): Fundament und Kernfunktionalität

**Ziel:** Ein stabiler, testbarer Prototyp mit robuster Audio-Analyse und Hue-Entertainment-Synchronisation.

---

**Task 1.0: Technologie-Evaluierung (UI & Audio)**
- **Beschreibung:** Evaluierung der UI- und Audio-Bibliotheken, um eine fundierte technologische Entscheidung zu treffen.
- **Agent-Prompt:** "Erstelle ein Dokument `docs/project/02-TECHNOLOGY_EVALUATION.md`. Analysiere und vergleiche darin die folgenden Technologien:
  1.  **UI-Frameworks:** WPF, WinUI 3 und .NET MAUI. Bewerte sie nach: Rendering-Performance für Echtzeit-Visualisierungen, Reifegrad, Community-Support und Eignung für Drag & Drop-Layouts. Gib eine klare Empfehlung ab.
  2.  **Audio-Bibliotheken:** NAudio und CSCore. Bewerte sie nach: FFT-Funktionalität, Wartungsstatus, Performance und .NET 8 Kompatibilität. Gib eine klare Empfehlung ab."

**Task 1.1: Projekt-Grundgerüst & Dependency Injection**
- **Beschreibung:** Erstellen des Projekts basierend auf Task 1.0. Einrichten von Dependency Injection (DI) und Code-Qualitäts-Tools.
- **Agent-Prompt:** "Erstelle ein neues Projekt basierend auf der in Task 1.0 empfohlenen UI-Technologie. Konfiguriere das Projekt für `net8.0-windows10.0.19041.0`. Richte Ordner für 'Core', 'Services', 'UI', 'ViewModels' ein. Konfiguriere `Microsoft.Extensions.DependencyInjection` und registriere Platzhalter-Services als Singletons. Integriere Serilog. Erstelle eine `.editorconfig`-Datei mit C#-Coding-Standards. Richte einen CI-Workflow auf GitHub Actions mit `runs-on: windows-latest` ein."

**Task 1.2: Hue Bridge Kommunikation**
- **Beschreibung:** Integration von `HueApi.Net` zur Bridge-Suche, Kopplung und zum Abruf von Gruppen.
- **Agent-Prompt:** "Integriere das NuGet-Paket 'HueApi.Net'. Erstelle einen 'HueService'. Implementiere Methoden für Bridge-Suche (`FindBridgesAsync`), App-Registrierung (`RegisterAppAsync`) und das Abrufen von Entertainment-Gruppen (`GetEntertainmentGroupsAsync`)."

**Task 1.3: Audio-Verarbeitung (Basis)**
- **Beschreibung:** Integration der in Task 1.0 gewählten Audio-Bibliothek zur Erfassung von Live-Audio.
- **Agent-Prompt:** "Integriere die in Task 1.0 gewählte Audio-Bibliothek (NAudio/CSCore). Erstelle einen 'AudioService'. Implementiere die Auflistung von Audio-Geräten und das Starten/Stoppen der Aufnahme von einem Loopback-Gerät."

**Task 1.4: Audio-Analyse-Engine**
- **Beschreibung:** Implementierung der detaillierten Audio-Analyse-Pipeline.
- **Agent-Prompt:** "Erweitere den 'AudioService' und die 'EffectEngine':
  - **1.4a (FFT):** Implementiere eine FFT-Transformation der rohen Audiodaten.
  - **1.4b (Spektralanalyse):** Extrahiere die Energie für Bass-, Mid- und High-Frequenzbänder.
  - **1.4c (Beat-Detektor):** Implementiere einen Beat-Detektor mit konfigurierbaren Parametern (Sensitivität, Threshold).
  - **1.4d (Audio-Profile):** Erstelle eine Struktur für Audio-Profile (z.B. 'Techno', 'House'), die die Beat-Detektor-Parameter kapseln und aus JSON geladen werden können."

**Task 1.5: Entertainment API Integration & Performance-Monitoring**
- **Beschreibung:** Verbindung der Analyse-Engine mit der Hue Entertainment API, inklusive Fehlerbehandlung und Performance-Metriken.
- **Agent-Prompt:** "Implementiere die Hue Entertainment API V2-Kommunikation in der 'EffectEngine':
  - Stelle sicher, dass die Verbindung korrekt mit DTLS verschlüsselt wird.
  - Implementiere einen Wiederverbindungs-Mechanismus bei Timeouts.
  - Logge alle Fehler beim Session-Aufbau detailliert.
  - Füge Performance-Counter hinzu: Messe die Latenz (Audio-Eingang bis Licht-Befehl) und die FPS der Licht-Updates. Zeige diese Werte in der UI an."

**Task 1.6: UI des Prototyps verbinden**
- **Beschreibung:** Verknüpfung aller Komponenten mit einer einfachen Benutzeroberfläche.
- **Agent-Prompt:** "Erstelle ein 'MainViewModel' und eine einfache `MainPage`-UI. Binde die Auswahl für Audio-Geräte und Entertainment-Gruppen, einen Start/Stopp-Button sowie die Anzeige für Latenz und FPS an das ViewModel."

**Task 1.7: Konfigurationspersistierung**
- **Beschreibung:** Speichern und Laden der Benutzerkonfiguration.
- **Agent-Prompt:** "Erstelle einen 'ConfigurationService'. Speichere und lade mittels `System.Text.Json` eine `appsettings.json`-Datei im lokalen AppData-Verzeichnis. Die Konfiguration soll Bridge-IP, App-Key, gewähltes Audio-Gerät und Entertainment-Gruppe enthalten. Lade die Konfiguration beim Start und speichere sie bei Änderungen."

**Task 1.8: Logging und Fehlerbehandlung**
- **Beschreibung:** Einrichtung eines robusten Logging- und Fehlerbehandlungssystems.
- **Agent-Prompt:** "Konfiguriere Serilog für File-Logging (`logs/photonjockey-.log`) und Console-Logging. Implementiere einen globalen Exception-Handler, der unbehandelte Ausnahmen fängt, loggt und dem Benutzer einen Fehlerdialog anzeigt."

**Task 1.9: Unit-Tests**
- **Beschreibung:** Erstellung von Unit-Tests für die Kern-Services.
- **Agent-Prompt:** "Erstelle ein Test-Projekt mit xUnit und Moq. Schreibe Unit-Tests für den 'HueService' (mit Mock-HTTP-Responses) und den 'AudioService' (mit Mock-Audiodaten)."

---

### Phase 2: Erweiterte Features & UI/UX

**Ziel:** Implementierung der vollen visuellen Funktionalität und Verbesserung der Benutzererfahrung.

- **Task 2.1:** **Visualizer Dashboard:** UI-Elemente zur Anzeige der Waveform und des Frequenz-Spektrums.
- **Task 2.2:** **Anpassbare UI:** Implementierung des per Drag & Drop anpassbaren Layouts.
- **Task 2.3:** **Design & Theming:** Umsetzung von Farb-Themes und Anpassung an den Windows Dark-Mode.
- **Task 2.4:** **Light Mapping:** UI zur Konfiguration und Zuordnung von Lichtern zu Positionen.
- **Task 2.5:** **Erweiterte Effekte:** Implementierung von Strobe-Effekten und benutzerdefinierten Farbpaletten.

---

### Phase 3: Skalierung und Optimierung

**Ziel:** Hinzufügen von fortgeschrittenen Funktionen und Vorbereitung für eine breite Nutzung.

- **Task 3.1:** **Multi-Bridge-Support:** Gleichzeitige Steuerung mehrerer Hue Bridges.
- **Task 3.2:** **Plugin-Architektur:** Design und Implementierung einer API für benutzerdefinierte Effekte.
- **Task 3.3:** **Kalibrierung:** Funktionen zur Helligkeits- und Übergangszeit-Kalibrierung.
- **Task 3.4:** **Performance-Optimierung:** Tiefgehende Analyse und Optimierung der gesamten Pipeline.
