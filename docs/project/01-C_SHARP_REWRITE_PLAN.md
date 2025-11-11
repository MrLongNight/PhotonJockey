# Projektplan: PhotonJockey C# Neuentwicklung

**Dokument-ID:** `01-C_SHARP_REWRITE_PLAN.md`
**Datum:** 2025-11-10
**Status:** Finalisiert

---

## 1. Übersicht und Ziele

Dieses Dokument beschreibt den detaillierten Plan für die Neuentwicklung der PhotonJockey-Anwendung in C#. Die Umsetzung soll in einem **neuen, leeren GitHub Repository** erfolgen und ausschließlich durch KI-Coding-Tools gesteuert werden.

**Leitprinzipien:**
- **Modulare Architektur:** Klare Trennung von UI, Geschäftslogik (Audio-Analyse, Effekt-Engine) und Services (Hue-Kommunikation).
- **Testbarkeit:** Von Anfang an auf Unit- und Integrationstests auslegen.
- **Moderne UI/UX:** Eine ansprechende und intuitive Benutzeroberfläche basierend auf .NET MAUI.
- **KI-gesteuerte Entwicklung:** Der gesamte Prozess wird durch detaillierte Prompts für KI-Agenten gesteuert.

---

## 2. Technologie-Stack

- **Programmiersprache:** C#
- **UI-Framework:** .NET MAUI (ausschließlich für Windows)
- **Audio-Analyse-Bibliothek:** CSCore
- **Hue-Kommunikations-Bibliothek:** HueApi.Net
- **Build-System:** .NET CLI / MSBuild
- **CI/CD:** GitHub Actions

---

## 3. Entwicklungsphasen

### Phase 1: Prototyp (Minimum Viable Product)

**Ziel:** Eine funktionierende Kernanwendung, die Live-Audio analysiert und Lichter über die Hue Entertainment API synchronisiert.

---

#### **Task 1.1: Projekt-Grundgerüst erstellen**

**Beschreibung:**
Erstellen eines neuen .NET MAUI-Projekts für Windows. Einrichten der grundlegenden Ordnerstruktur und Konfiguration einer CI-Pipeline für automatische Builds.

**Wichtiger Hinweis für den Agenten:** Die Entwicklung von .NET MAUI für Windows erfordert eine Windows-Umgebung. Der CI/CD-Workflow muss daher auf einem Windows-Runner ausgeführt werden.

**Agent-Prompt:**
"Erstelle in diesem leeren Repository ein neues .NET MAUI Projekt namens 'PhotonJockey.New'. Konfiguriere die Projektdatei (`.csproj`) so, dass sie ausschließlich für das Ziel-Framework `net8.0-windows10.0.19041.0` kompiliert. Richte eine grundlegende Ordnerstruktur mit den Verzeichnissen 'Core', 'Services', 'UI' und 'ViewModels' ein. Erstelle eine `Directory.Build.props`-Datei, um globale Projekteinstellungen wie die C# Sprachversion (12.0) festzulegen. Füge eine grundlegende GitHub Actions Workflow-Datei unter `.github/workflows/build.yml` hinzu. Diese soll `runs-on: windows-latest` verwenden und das Projekt bei jedem Push auf den `main`-Branch mit dem .NET CLI (`dotnet build`) bauen."

---

#### **Task 1.2: Hue Bridge Kommunikation (Basis)**

**Beschreibung:**
Integration der `HueApi.Net` Bibliothek. Implementierung der Funktionalität zur Suche, Kopplung und zum Abruf von Entertainment-Gruppen von einer Hue Bridge.

**Agent-Prompt:**
"Integriere das NuGet-Paket 'HueApi.Net' in das 'PhotonJockey.New' Projekt. Erstelle eine neue Klasse 'HueService' im 'Services'-Verzeichnis. Implementiere in diesem Service die folgenden Methoden:
1. `async Task<IEnumerable<Bridge>> FindBridgesAsync()`: Sucht über die in der Bibliothek bereitgestellten Mechanismen (z.B. `BridgeLocator`) nach Hue Bridges im Netzwerk.
2. `async Task<string> RegisterAppAsync(string bridgeIp, string appName, string deviceName)`: Führt den Kopplungsprozess mit der Bridge durch und gibt den generierten App-Key zurück.
3. `async Task<IEnumerable<Group>> GetEntertainmentGroupsAsync(string bridgeIp, string appKey)`: Ruft alle verfügbaren Entertainment-Gruppen (`type: 'entertainment'`) von einer gekoppelten Bridge ab.
Erstelle eine einfache UI in `MainPage.xaml` mit Buttons, um diese Methoden zu testen und die Ergebnisse in einem ListView anzuzeigen."

---

#### **Task 1.3: Audio-Verarbeitung (Basis)**

**Beschreibung:**
Integration der `CSCore` Bibliothek zur Erfassung von Live-Audio von einem ausgewählten System-Audio-Gerät.

**Agent-Prompt:**
"Integriere das NuGet-Paket 'CSCore' in das 'PhotonJockey.New' Projekt. Erstelle eine neue Klasse 'AudioService' im 'Services'-Verzeichnis. Implementiere in diesem Service:
1. `IEnumerable<MMDevice> GetAudioDevices()`: Gibt eine Liste aller verfügbaren Audio-Loopback-Geräte mittels `WasapiLoopbackCapture` zurück.
2. `void StartCapture(MMDevice device, Action<byte[]> onDataAvailable)`: Startet die Audio-Aufnahme vom ausgewählten Gerät und ruft den `onDataAvailable`-Callback mit den rohen Audio-Daten (PCM-Samples) auf.
3. `void StopCapture()`: Stoppt die Audio-Aufnahme.
Aktualisiere die `MainPage.xaml` UI, um eine ComboBox zur Auswahl des Audio-Geräts anzuzeigen."

---

#### **Task 1.4: Effekt-Engine und Entertainment API**

**Beschreibung:**
Verbindung der Audio-Daten mit der Hue Entertainment API. Implementierung einer einfachen Beat-Erkennung und Senden eines Licht-Updates bei jedem Beat.

**Agent-Prompt:**
"Erstelle eine 'EffectEngine'-Klasse im 'Core'-Verzeichnis. Diese Klasse soll die Audio-Daten vom 'AudioService' entgegennehmen. Implementiere einen einfachen FFT-basierten Algorithmus zur Beat-Erkennung. Nutze die `HueApi.Net` Bibliothek, um eine Entertainment-Session (`StreamingSetup.V2`) zu einer ausgewählten Gruppe aufzubauen. Bei jedem erkannten Beat, sende einen Befehl an die Gruppe, der alle Lichter darin kurz hell aufleuchten lässt (z.B. in einer zufälligen Farbe)."

---

#### **Task 1.5: UI des Prototyps verbinden**

**Beschreibung:**
Verknüpfung aller Services und der Effekt-Engine mit der Benutzeroberfläche, um den Prototyp funktionsfähig zu machen.

**Agent-Prompt:**
"Erstelle ein 'MainViewModel' im 'ViewModels'-Verzeichnis. Dieses ViewModel soll Instanzen von 'HueService', 'AudioService' und 'EffectEngine' enthalten. Binde die UI-Elemente in `MainPage.xaml` an das ViewModel:
- Eine ComboBox für Audio-Geräte.
- Eine ListView zur Anzeige und Auswahl von Entertainment-Gruppen.
- Ein 'Start/Stop'-Button, der die Audio-Aufnahme und die Effekt-Engine aktiviert/deaktiviert.
Stelle sicher, dass der Datenfluss via Data-Binding korrekt ist."

---

#### **Task 1.6: HTTPS Web Request Tester**

**Beschreibung:**
Erstellen eines einfachen UI-Tools zum manuellen Senden von HTTPS-Befehlen an die Hue Bridge.

**Agent-Prompt:**
"Erstelle eine neue Seite 'HttpsTestPage.xaml'. Diese Seite soll Eingabefelder für die Bridge-IP, den App-Key, die HTTP-Methode (GET, POST, PUT), den API-Endpunkt (z.B. '/clip/v2/resource/light') und einen JSON-Body enthalten. Füge einen 'Senden'-Button hinzu. Bei Klick soll der 'HueService' eine entsprechende HTTPS-Anfrage an die Bridge senden und die zurückgegebene JSON-Antwort in einem Textfeld anzeigen."

---

### Phase 2: Erweiterungen und UI/UX

**Ziel:** Verbesserung der Benutzererfahrung und Erweiterung der Effekt-Möglichkeiten.

- **Task 2.1:** Implementierung eines anpassbaren Drag & Drop Layout-Systems.
- **Task 2.2:** Entwicklung komplexerer visueller Effekte (z.B. Frequenz-Spektrum-Visualisierung).
- **Task 2.3:** Implementierung von Farb-Themes und Anpassung an den Windows Dark-Mode.
- **Task 2.4:** Speichern und Laden von Benutzerkonfigurationen (Profile).

*(Detaillierte Prompts für Phase 2 werden nach Abschluss von Phase 1 erstellt.)*

---

### Phase 3: Weitere Features

**Ziel:** Hinzufügen von fortgeschrittenen Funktionen und Optimierungen.

- **Task 3.1:** Multi-Bridge-Unterstützung.
- **Task 3.2:** Plugin-Architektur für benutzerdefinierte Effekte.
- **Task 3.3:** Leistungsoptimierung der Audio-Analyse und des Netzwerk-Traffics.

*(Detaillierte Prompts für Phase 3 werden nach Abschluss von Phase 2 erstellt.)*
