Hallo Copilot Agent,
​wir starten ein neues Projekt namens PhotonJockey (basierend auf der LightBeat-Codebasis). Das Ziel ist die Entwicklung eines KI-gestützten Licht-Performance-Systems für DJs, VJs und Streamer.
​Ich bin "Mr. LongNight" und agiere als Projektmanager.
​Bitte lesen Sie diese Anweisungen sorgfältig durch, da sie für unsere gesamte Zusammenarbeit fundamental sind.
​1. Rollenverteilung (Sehr wichtig!)
​Ihre Rolle (Copilot Agent): Sie sind der alleinige Entwickler, Tester und technischer Redakteur dieses Projekts. Sie sind zu 100% für den gesamten Code, alle Konfigurationen, alle Tests und die gesamte technische Dokumentation verantwortlich.
​Meine Rolle (Mr. LongNight): Ich bin Ihr Projektmanager. Ich bin kein Entwickler und werde keinen Java-Code schreiben, lesen oder reviewen. Meine Aufgaben sind:
​Ihnen klare Aufgaben aus unserem Projektplan zuzuweisen.
​Ihre Pull Requests zu mergen, nachdem alle automatisierten Tests (CI) bestanden sind.
​Manuelle, visuelle Tests der Anwendung durchzuführen (z. B. "Startet die App?", "Reagiert die Lampe?", "Ist die UI sichtbar?").
​2. Grundsätzlicher Projektablauf (Unser Workflow)
​Wir werden den von mir bereitgestellten Projektplan Task für Task (TG) abarbeiten. Jeder Task folgt diesem Zyklus:
​Zuweisung: Ich gebe Ihnen eine klar definierte @copilot:task-Anweisung (z.B. TG1.1).
​Branch: Sie erstellen für jede einzelne Anweisung einen neuen, korrekt benannten Feature-Branch (z.B. feature/1-setup oder feature/3-hue-fast-udp).
​Implementierung: Sie schreiben den gesamten Code, der zur Erfüllung des Tasks notwendig ist.
​Tests: Sie schreiben die im Task definierten Unit-Tests, Integrationstests oder Mock-Server. Tasks ohne Tests werden nicht akzeptiert.
​Dokumentation: Falls der Task die App ändert, aktualisieren Sie die relevanten docs/-Dateien.
​Pull Request (PR): Wenn der Task abgeschlossen und getestet ist, erstellen Sie einen Pull Request gegen den main-Branch. Die PR-Beschreibung muss die Task-Nummer (z.B. "Implementiert TG3.3") enthalten.
​CI-Check: Ich warte, bis die von Ihnen (in TG6) erstellte GitHub Action (CI) alle Tests automatisch ausführt und ein grünes Häkchen anzeigt.
​Merge: Ich (Mr. LongNight) klicke auf den "Merge"-Button.
​Nächster Task: Ich gebe Ihnen die nächste @copilot:task.
​3. Technische Grundregeln
​Projekt-Stack: Java 17, Gradle.
​Codestil: Wir verwenden den "Google Java Styleguide". Sie werden in TG1.1 Checkstyle dafür einrichten.
​Task-Größe: Die Aufgaben sind bewusst klein gehalten (z.B. 1 Klasse oder 1 Feature pro PR). Halten Sie sich strikt an diese atomaren Änderungen.
​Verantwortung: Da ich den Code nicht reviewen kann, liegt die Verantwortung für Stabilität, Sicherheit und Fehlerfreiheit zu 100% bei Ihnen. Die Tests sind unser einziges Sicherheitsnetz.

TaskGroup 1: Code-Analyse & Projekt-Struktur
Ziel: Das Projekt standardisieren und eine erste Analyse durchführen.
1.1: Code-Stil & Projektkonventionen
@copilot:task
Erstelle einen neuen Branch: `feature/1-setup`.
In diesem Branch:
1.  Erstelle eine `.editorconfig`-Datei und eine `checkstyle.xml`-Datei. Konfiguriere beide für den "Google Java Styleguide".
2.  Integriere Checkstyle in das `build.gradle`, sodass `./gradlew check` den Stil prüft.
3.  Erstelle `docs/Projekt_Vorgaben/CODING_KONVENTIONEN.md`. Inhalt:
    * Branch-Namen: `feature/<TG#>-<shortname>`
    * Commits: Müssen die Task-Nummer enthalten (z.B. "TG1.1: Add checkstyle").
4.  Erstelle eine Pull-Request-Vorlage unter `.github/pull_request_template.md` mit einer Checkliste:
    * - [ ] Build ist grün
    * - [ ] Tests sind grün
    * - [ ] Doku aktualisiert

1.2: 👤 Review & Merge (Mr. LongNight)
 * Copilot hat einen Pull Request (PR) auf GitHub erstellt.
 * Ihre Aufgabe: Gehen Sie zu GitHub, öffnen Sie den "Pull Requests"-Tab, klicken Sie auf den PR und klicken Sie auf den grünen "Merge pull request"-Knopf.
1.3: Automatisierte Code-Analyse
@copilot:task
Erstelle einen neuen Branch: `feature/1-analysis`.
In diesem Branch:
1.  Füge eine neue Gradle-Task `generateCodeReport` zum `build.gradle` hinzu.
2.  Diese Task soll das Verzeichnis `src/main/java` scannen und eine Datei `docs/Reports/CODEBASE_UEBERSICHT.md` erstellen.
3.  Der Report muss enthalten:
    * Eine Liste aller Pakete (z.B. `pw.wunderlich.lightbeat.audio`).
    * Eine Liste der wichtigsten Klassen (Main-Klassen, Controller).
    * Eine Liste aller Stellen, an denen `new Thread()` manuell aufgerufen wird (als Basis für TG1.4).

1.4: Refactoring (Audio-Threads)
@copilot:task
Erstelle einen neuen Branch: `feature/1-refactor-audio`.
Basierend auf dem Report aus TG1.3:
1.  Finde die Klasse `AudioAnalyzer` (oder ähnlich), die manuell Threads startet.
2.  Refaktoriere diese Klasse: Ersetze die `new Thread()`-Aufrufe durch einen `ScheduledExecutorService`.
3.  Füge einen neuen Unit-Test (z.B. `AudioAnalyzerTest.java`) hinzu.
4.  Der Test `startStop_noException` soll prüfen, ob der Service sauber startet und (via `shutdown()`) wieder stoppt, ohne eine Exception zu werfen.

1.5: 👤 Review & Merge (Mr. LongNight)
 * Mergen Sie die beiden Pull Requests für 1-analysis und 1-refactor-audio auf GitHub.
TaskGroup 2: Audio Engine Upgrade
Ziel: Die Audio-Analyse modernisieren und testbar machen.
2.1: Schnittstellen (Interfaces)
@copilot:task
Erstelle einen neuen Branch: `feature/2-audio-interfaces`.
In diesem Branch:
1.  Definiere zwei neue Interfaces in Java:
    * `interface IAudioSource { AudioFrame pollFrame(); void start(); void stop(); }`
    * `interface IAudioAnalyzer { AnalysisResult analyze(AudioFrame f); }`
2.  Definiere die DTO-Klassen (reine Datencontainer):
    * `class AudioFrame { byte[] data; long timestamp; }`
    * `class AnalysisResult { double currentBPM; double[] fftBands; boolean isBeat; }`

2.2: FFT & Beat Detection Implementierung
@copilot:task
Erstelle einen neuen Branch: `feature/2-audio-processing`.
In diesem Branch:
1.  Implementiere eine Klasse `FFTProcessor`, die `fftSize` und `smoothing` als Parameter im Konstruktor akzeptiert.
2.  Implementiere eine Klasse `BeatDetector`, die eine `AnalysisResult` (speziell BPM) aus den `AudioFrame`-Daten berechnet.
3.  Füge Unit-Tests für beide Klassen hinzu.
4.  WICHTIG: Erstelle einen Test `FFTProcessorTest` mit einem generierten Sinus-Wellen-Input und prüfe, ob der Output-Peak in der erwarteten FFT-Band liegt.

2.3: Test-Audio & Integrationstest
@copilot:task
Erstelle einen neuen Branch: `feature/2-audio-testfiles`.
In diesem Branch:
1.  Erstelle eine Test-Audiodatei `src/test/resources/audio/beat_120bpm.wav`. Dies kann ein einfacher, synthetischer Klick-Track sein (z.B. 2 Klicks im Abstand von 500ms).
2.  Erstelle einen Integrationstest `AudioEngineIntegrationTest`.
3.  Dieser Test muss die `beat_120bpm.wav` laden, sie durch den `BeatDetector` laufen lassen und prüfen (assert), ob das erkannte `currentBPM` im Bereich 119.5 bis 120.5 liegt.

2.4: Audio-Profile
@copilot:task
Erstelle einen neuen Branch: `feature/2-audio-profiles`.
In diesem Branch:
1.  Implementiere einen `ProfileManager`, der Audio-Einstellungen aus einer JSON-Datei lädt: `config/audio_profiles.json`.
2.  Definiere das JSON-Format:
    ```json
    {
      "profiles": [
        { "id": "techno", "fftSize": 2048, "gain": 1.2, "beatSensitivity": 0.8 },
        { "id": "ambient", "fftSize": 4096, "gain": 1.0, "beatSensitivity": 0.6 }
      ]
    }
    ```

2.5: Audio-Visualizer (UI)
@copilot:task
Erstelle einen neuen Branch: `feature/2-audio-visualizer`.
In diesem Branch:
1.  Erstelle eine neue JavaFX UI-Ansicht (View): `AudioAnalyzerDashboard.fxml`.
2.  Diese Ansicht muss enthalten:
    * Eine Komponente zur Anzeige der Wellenform (Waveform).
    * Ein Balkendiagramm (Bar chart) für das Frequenzspektrum (Spectrum).
    * Einen Kreis (Circle) als "Beat-Indikator", der bei `isBeat = true` aufleuchtet.
    * Slider (Schieberegler) zur Einstellung von `gain` und `beatSensitivity`.
3.  Erstelle die Controller-Klasse `AudioAnalyzerDashboardController.java`.
4.  Der Controller muss die `AnalysisResult`-Events abonnieren und die UI-Komponenten (Spectrum, Beat-Indikator) live aktualisieren.
5.  Integriere diese neue Ansicht in die Hauptanwendung.

2.6: 👤 Manueller Test: Audio-Visualizer (Mr. LongNight)
 * Mergen Sie alle 5 Pull Requests aus TaskGroup 2.
 * Starten Sie die LightBeat-Anwendung auf Ihrem Computer (Copilot kann Ihnen den Gradle-Befehl dafür geben, z.B. ./gradlew run).
 * Öffnen Sie das neue "Audio Dashboard" in der App.
 * Spielen Sie Musik auf Ihrem Computer ab (z.B. von YouTube oder Spotify).
 * Ihre Aufgabe (Visuelle Prüfung):
   * Bewegt sich die Wellenform?
   * Reagieren die Frequenz-Balken auf die Musik?
   * Blinkt der "Beat-Indikator" sichtbar im Takt der Musik?
 * Wenn alles gut aussieht, können Sie mit der nächsten TaskGroup weitermachen.
TaskGroup 3: Hue Engine NextGen (Multi-Bridge + Mapping)
Ziel: Eine robuste Steuerung für Philips Hue implementieren, die schnelle (UDP) und langsame (HTTP) Befehle trennt.
3.1: Core Interfaces & Effekt-Router
@copilot:task
Erstelle einen neuen Branch: `feature/3-hue-interfaces`.
In diesem Branch:
1.  Definiere die Interfaces:
    * `interface IFastEffectController { void startSession(); void sendFrame(EffectFrame frame); void stopSession(); }` (für UDP Entertainment API)
    * `interface ILowEffectController { void updateLights(List<LightUpdate> updates); }` (für HTTP API)
2.  Implementiere die Klasse `EffectRouter`.
3.  Der `EffectRouter` muss eine `lightmap.json` laden und basierend auf der Konfiguration einer Lampe entscheiden, ob Effekte an den `IFastEffectController` oder `ILowEffectController` gesendet werden.

3.2: DTOs & Mapping-Konfiguration
@copilot:task
Erstelle einen neuen Branch: `feature/3-hue-config`.
In diesem Branch:
1.  Definiere die DTOs (Datenklassen) für `LightUpdate` und `EffectFrame`.
2.  Definiere das Schema für `config/lightmap.json` und speichere es als `schemas/lightmap.schema.json`.
3.  WICHTIGE Anforderung an das Schema (Lösung eines Problems aus dem Originalplan): Das Schema muss Bridges *und* Lichter definieren.
    ```json
    {
      "$schema": "[http://json-schema.org/draft-07/schema#](http://json-schema.org/draft-07/schema#)",
      "type": "object",
      "required": ["bridges", "lights"],
      "properties": {
        "bridges": {
          "type": "array",
          "items": { "type": "object", "properties": { "id": {"type": "string"}, "ip": {"type": "string"} } }
        },
        "lights": {
          "type": "array",
          "items": {
            "type": "object",
            "properties": {
              "id": {"type": "string"},
              "x": {"type": "number"},
              "y": {"type": "number"},
              "bridgeId": {"type": "string"},
              "controlType": {"type": "string", "enum": ["FAST_UDP", "LOW_HTTP"]}
            }
          }
        }
      }
    }
    ```

3.3: FastEffectController (UDP)
@copilot:task
Erstelle einen neuen Branch: `feature/3-hue-fast-udp`.
In diesem Branch:
1.  Implementiere die Klasse `FastEffectController` (via `DatagramSocket` für UDP).
2.  Das UDP-Paket (Frame) muss dem Hue Entertainment V2-Format entsprechen und eine `sequenceNumber` (Paketnummer) enthalten.
3.  Implementiere einen `MockUdpServer` für einen Unit-Test.
4.  Der Test `MockUdpServerTest` muss prüfen, ob die gesendeten Pakete ankommen und ob die `sequenceNumber` bei jedem Frame korrekt hochzählt.

3.4: LowEffectController (HTTP)
@copilot:task
Erstelle einen neuen Branch: `feature/3-hue-low-http`.
In diesem Branch:
1.  Implementiere die Klasse `LowEffectController` (via Java 11+ `HttpClient`).
2.  Die Implementierung muss Updates bündeln (Batch-Updates) und einen Rate-Limiter (z.B. max. 2 Anfragen pro Sekunde) verwenden.
3.  Implementiere einen `MockHttpServer` für einen Unit-Test (z.B. mit NanoHTTPD).
4.  Der Test muss prüfen, dass der Controller eine `HTTP 429 (Too Many Requests)`-Antwort vom Mock-Server korrekt verarbeitet (z.B. gracefully backoff), ohne abzustürzen.

3.5: Smart Mapping Tool (UI)
@copilot:task
Erstelle einen neuen Branch: `feature/3-hue-mapping-tool`.
In diesem Branch:
1.  Erstelle eine neue JavaFX UI-Ansicht: `SmartMappingTool.fxml`.
2.  Diese Ansicht muss folgende Funktionen haben:
    * Eine 2D-Leinwand (Canvas).
    * Eine Liste von "entdeckten" Hue-Lampen (zunächst als Mock).
    * Drag-and-Drop-Funktion, um Lampen aus der Liste auf die Canvas zu ziehen und ihre X/Y-Position zu setzen.
    * Möglichkeit, jeder Lampe einen `bridgeId` und `controlType` (FAST/LOW) zuzuweisen.
    * Einen "Speichern"-Knopf (schreibt `config/lightmap.json`).
    * Einen "Laden"-Knopf (liest `config/lightmap.json`).
    * Einen "Live Preview"-Knopf, der einen Test-Effekt (z.B. einen Farbübergang) an den `EffectRouter` sendet.
3.  Erstelle die `SmartMappingToolController.java` für die UI-Logik.

3.6: 👤 Manueller Test: Smart Mapping Tool (Mr. LongNight)
 * Mergen Sie alle Pull Requests aus TaskGroup 3.
 * WICHTIG: Für diesen Test benötigen Sie eine echte Philips Hue Bridge und mindestens eine Lampe, die für "Entertainment" konfiguriert ist.
 * Tragen Sie die IP-Adresse Ihrer Bridge in die config/lightmap.json ein (oder nutzen Sie die Lade-Funktion, falls Copilot eine Bridge-Discovery implementiert hat).
 * Starten Sie die LightBeat-Anwendung.
 * Öffnen Sie das "Smart Mapping Tool".
 * Ihre Aufgabe (Visuelle Prüfung):
   * Können Sie Ihre Lampen sehen und auf der Canvas positionieren?
   * Können Sie die Konfiguration speichern und wieder laden?
   * Der wichtigste Test: Wenn Sie auf "Live Preview" klicken, reagiert Ihre echte Lampe sichtbar auf den Test-Effekt?
 * Wenn die Lampe reagiert, war die Implementierung der Hue Engine erfolgreich.
TaskGroup 4: UI/UX Redesign
Ziel: Die Benutzeroberfläche aufräumen und Einstellungen speichern.
4.1: Hauptnavigation & Benutzereinstellungen
@copilot:task
Erstelle einen neuen Branch: `feature/4-ui-redesign`.
In diesem Branch:
1.  Refaktoriere die Haupt-UI (`MainController.java` o.ä.).
2.  Füge eine permanente Sidebar (Seitenleiste) für die Hauptnavigation hinzu.
3.  Links in der Sidebar: "Audio Dashboard", "Light Mapping", "Einstellungen".
4.  Implementiere das Speichern von Benutzereinstellungen (z.B. Fenstergröße, letzte Lautstärke) in einer Datei `user_prefs.json`. Die App soll diese beim Start laden.
5.  Implementiere einen `ShortcutManager` (Hotkey-Verwaltung), z.B. `Ctrl+1` für "Audio Dashboard".

4.2: 👤 Manueller Test: UI-Verhalten (Mr. LongNight)
 * Mergen Sie den PR für 4-ui-redesign.
 * Starten Sie die App.
 * Ihre Aufgabe (Visuelle Prüfung):
   * Sehen Sie die neue Sidebar? Funktionieren die Links?
   * Ändern Sie die Fenstergröße und schließen Sie die App.
   * Starten Sie die App erneut. Hat sie sich die Fenstergröße gemerkt?
   * Testen Sie die neuen Hotkeys.
TaskGroup 5: Performance & Stabilität
Ziel: Sicherstellen, dass die App im Dauerbetrieb (z.B. bei einem DJ-Set) nicht abstürzt.
5.1: Thread-Management & Queues
@copilot:task
Erstelle einen neuen Branch: `feature/5-stability`.
In diesem Branch:
1.  Stelle sicher, dass die gesamte Effekt-Pipeline (Audio-Analyse -> Effekt-Router) über einen zentralen `ExecutorService` (mit max. 4 Threads) läuft.
2.  Implementiere eine `BoundedBlockingQueue` (z.B. `ArrayBlockingQueue` mit Größe 10) zwischen dem `IAudioAnalyzer` und dem `EffectRouter`.
3.  Stabilitäts-Feature: Wenn die Queue voll ist (Effekte hinken hinterher), muss der *älteste* Frame verworfen werden (`poll()`), bevor der neueste hinzugefügt wird (`offer()`). Dies verhindert Latenz.

5.2: 1-Stunden-Stabilitätstest (Loadtest)
@copilot:task
Erstelle einen neuen Branch: `feature/5-perf-test`.
In diesem Branch:
1.  Erstelle einen neuen Test (z.B. `StressTest.java`).
2.  Dieser Test darf *keine* echten Lampen oder Audio-Inputs benötigen.
3.  Er muss:
    * Die App mit einem `MockAudioSource` (der 60 FPS Audio-Frames generiert) und `MockBridge`-Controllern (die Daten empfangen) starten.
    * Den Test für 1 Stunde (oder 30 Minuten, falls 1h zu lang für CI ist) laufen lassen.
    * Den JVM Heap-Speicher alle 5 Minuten protokollieren.
    * Der Test schlägt fehl, wenn der Heap-Speicher nach den ersten 10 Minuten (Warmup) um mehr als 10% anwächst (Anzeichen für ein Memory Leak).

5.3: 👤 Review & Merge (Mr. LongNight)
 * Mergen Sie die PRs 5-stability und 5-perf-test.
 * Der StressTest wird automatisch von GitHub ausgeführt (siehe TG6). Sie müssen hier nichts manuell testen.
TaskGroup 6: CI/CD (Automatische Tests)
Ziel: GitHub so konfigurieren, dass jeder PR automatisch gebaut und getestet wird.
6.1: GitHub Actions Workflow
@copilot:task
Erstelle einen neuen Branch: `feature/6-ci-cd`.
In diesem Branch:
1.  Erstelle die Workflow-Datei `.github/workflows/ci.yml`.
2.  Der Workflow muss bei jedem `push` auf `main` und bei jedem `pull_request` starten.
3.  Die "Jobs" im Workflow müssen folgende Schritte (Steps) enthalten:
    * `actions/checkout@v4` (Code holen)
    * `actions/setup-java@v4` (mit Java JDK 17)
    * `./gradlew build` (Kompilieren)
    * `./gradlew test` (Alle Tests ausführen, inkl. dem StressTest aus TG5.2)
    * `./gradlew checkstyleMain` (Code-Stil prüfen)

TaskGroup 7: Monitoring & Release
Ziel: Die App "fertigstellen" durch Hinzufügen von Monitoring und automatischen Benachrichtigungen.
7.1: Metrik-Endpunkt
@copilot:task
Erstelle einen neuen Branch: `feature/7-monitoring`.
In diesem Branch:
1.  Integriere einen leichtgewichtigen HTTP-Server (z.B. Javalin oder NanoHTTPD) in die App, der auf `localhost:9000` läuft.
2.  Erstelle einen Endpunkt `/metrics`.
3.  Wenn dieser Endpunkt (JSON) aufgerufen wird, soll er Live-Daten anzeigen:
    ```json
    {
      "appStatus": "RUNNING",
      "currentBPM": 120.5,
      "audioQueueSize": 3,
      "fastFramesSent": 10500,
      "lowRequestsSent": 50
    }
    ```

7.2: Discord-Benachrichtigung bei Fehlern
@copilot:task
Erstelle einen neuen Branch: `feature/7-ci-alert`.
In diesem Branch:
1.  Modifiziere die `.github/workflows/ci.yml` aus TG6.1.
2.  Füge einen neuen "Step" am Ende des Test-Jobs hinzu.
3.  Dieser Step darf *nur* ausgeführt werden, wenn ein vorheriger Step (z.B. `./gradlew test`) fehlgeschlagen ist (`if: failure()`).
4.  Der Step muss eine "Discord Webhook" GitHub Action verwenden, um eine Fehlermeldung an einen Discord-Kanal zu senden.
5.  Die Webhook-URL muss sicher als GitHub Secret namens `DISCORD_WEBHOOK_URL` geladen werden.

7.3: 👤 Finale GitHub-Konfiguration (Mr. LongNight)
Dies ist der letzte manuelle Schritt, um Ihr Projekt abzusichern. Copilot kann dies nicht tun.
 * Discord-Webhook einrichten:
   * Erstellen Sie einen privaten Discord-Server für sich selbst.
   * Gehen Sie zu: Server-Einstellungen -> Integrationen -> Webhooks -> Neuer Webhook.
   * Kopieren Sie die "Webhook-URL".
 * GitHub-Secret eintragen:
   * Gehen Sie zu Ihrem GitHub-Repository (Ihrem Fork).
   * Gehen Sie zu Settings -> Secrets and variables -> Actions.
   * Klicken Sie auf New repository secret.
   * Name: DISCORD_WEBHOOK_URL
   * Value: (Fügen Sie hier die kopierte URL aus Discord ein).
 * Branch-Schutz (WICHTIG):
   * Gehen Sie zu Settings -> Branches.
   * Fügen Sie eine Schutzregel (protection rule) für Ihren main-Branch hinzu.
   * Aktivieren Sie (Häkchen setzen):
     * Require a pull request before merging (Damit Sie nie direkt auf main arbeiten).
     * Require status checks to pass before merging (Damit nur Code gemergt wird, der alle Tests besteht).
     * Wählen Sie in der erscheinenden Liste den test-Job (oder wie auch immer der Job in ci.yml heißt) als erforderlichen Check aus.
7.4: 👤 Review & Merge (Mr. LongNight)
 * Mergen Sie die letzten Pull Requests (7-monitoring, 7-ci-alert).

