# PhotonJockey — Project Plan (Copilot-driven, Human-supervised)

Kurzbeschreibung
----------------
PhotonJockey — powered by LightBot Engine

Ein KI-gestütztes Licht-Performance-System für DJs, VJs und Streamer.

Governance
----------
- Project Lead: @MrLongNight — entscheidet über Sicherheits- und Release-Gates.
- Copilot / Copilot Agents: ausführende Entwickler — erzeugen Code, Tests, Docs, PRs.
- Humans: führen sicherheitsrelevante Aufgaben, Secrets & Releases manuell aus.

Wichtige Rollen & Verantwortungen
--------------------------------
- Projektmanager: @MrLongNight — weist Aufgaben als @copilot:task zu, merget PRs nach grünem CI (keine manuelle Code-Reviewtätigkeit erwartet).
- Entwickler / Copilot Agent: Du (Copilot) — verantwortlich für Code, Tests, CI, Docs. Du erstellst Branches, implementierst Tasks, schreibst Tests und öffnest PRs.
- Hinweis: Da der Project Lead keinen Code reviewt, sind automatisierte Tests und CI unsere einzige Absicherung.

Konventionen / Regeln (immer befolgen)
-------------------------------------
- Repo-Name: PhotonJockey
- Stack: Java 17, Gradle
- Codestyle: Google Java Styleguide (Checkstyle)
- Branch-Naming: feature/TG<group>-<short> (z. B. feature/TG3-FastEffect)
- Commit-Message Template:
  TG<group>-T<task>: <short description>

  Details:
  - test: <test-id>
  - reviewer: @MrLongNight

- PR Template (Checklist, in .github/pull_request_template.md):
  - [ ] Build is green
  - [ ] Unit tests are green
  - [ ] Integration tests are green
  - [ ] Docs updated
  - [ ] Manual UI review required? (yes/no)

- Copilot constraint: Maximal eine Klasse / Datei pro PR (CI kann dies erzwingen).
- Alle Änderungen müssen atomar sein (1 Feature / 1 Klasse pro PR).
- Tasks ohne Tests sind ungültig.

Dokumentstruktur & Format der Tasks
----------------------------------
Jede Task enthält:
1. Title
2. Responsibility: copilot / human / shared
3. Developer Directive (präzise @copilot:task Anweisung)
4. Project Lead Note (was der Mensch tun muss)
5. Acceptance Criteria / Tests

Überblick: TaskGroups (TG)
--------------------------
- TG1: Codeanalyse & Refactoring
- TG2: Audio Engine Upgrade
- TG3: Hue Engine NextGen (Fast + Low) & Mapping
- TG4: UI/UX Redesign
- TG5: Performance & Stability
- TG6: CI/CD & Tests
- TG7: Docs, Release & Monitoring

Detaillierter Plan (zusammengeführt)
-----------------------------------

TG1 — Codeanalyse & Projekt-Struktur
------------------------------------
TG1.1 Setup & Coding Conventions
Responsibility: shared (human: repo / fork, copilot: files)
Developer Directive:
@copilot:task
- Erstelle Branch: feature/TG1-setup
- Lege an:
  - .editorconfig (Google Java Style sensible defaults)
  - checkstyle.xml (Google Java Style)
  - docs/CODING_CONVENTIONS.md (Branch-Namen, Commit-Template, PR-Verhalten)
  - docs/setup.md (JDK 17, Gradle wrapper usage, Build- & Run-Schritte, CI-Badge-Placeholder)
  - .github/pull_request_template.md (Checklist)
- Integriere Checkstyle ins build.gradle, sodass `./gradlew check` Stil prüft.
Project Lead Note:
- Human: Fork / initiales Repo-Setup, lokal ./gradlew build ausführen.
Acceptance:
- Style-Files + docs existieren; PR erstellt.

TG1.2 Automated Static Analysis / Codebase Report
Responsibility: copilot
Developer Directive:
@copilot:task
- Erstelle Branch: feature/TG1-analysis
- Implementiere ein Tool (z. B. tools/analyze_codebase.py) oder eine Gradle-Task `generateCodeReport`:
  - Scan von src/main/java
  - Ausgabe: docs/codebase_overview.md mit Paketliste, Hauptklassen, vermuteten EntryPoints, Stellen mit `new Thread()` usw.
  - Optional: docs/diagrams/dependency.dot (Graphviz)
- Commit & PR öffnen.
Acceptance:
- docs/codebase_overview.md + dependency.dot vorhanden.

TG1.3 Code Metrics
Responsibility: copilot
Developer Directive:
@copilot:task
- Branch: feature/TG1-metrics
- Implementiere Report-Generator (SpotBugs + Metriken):
  - LOC pro Package
  - Cyclomatic Complexity Top-20
  - Threads gestartet pro Klasse
  - Externe Libs
- Ergebnis: reports/metrics.json, PR erstellen.
Acceptance:
- reports/metrics.json vorhanden.

TG1.4 Refactor Plan & kleine Refactors
Responsibility: copilot (implementierung) / shared (merge)
Developer Directive:
@copilot:task
- Branch(es): feature/TG1-REF-<n>
- Aus reports/metrics.json ein docs/refactor_plan.md erzeugen (Top-10 Hotspots).
- Pro TG1-REF-<n> eine atomare Änderung: 1 Klasse refactor + Unit-Test.
Beispiel (aus ORIGINAL): Ersetze `new Thread()` in AudioAnalyzer-Klasse durch `ScheduledExecutorService`.
Acceptance:
- docs/refactor_plan.md; einzelne PRs je Refactor.

TG2 — Audio Engine Upgrade
--------------------------
TG2.1 Audio Interfaces
Responsibility: copilot
Developer Directive:
@copilot:task
- Branch: feature/TG2-audio-interfaces
- Füge Interfaces hinzu (package: pw.wunderlich.lightbeat.audio):
  - IAudioSource { AudioFrame pollFrame(); void start(); void stop(); }
  - IAudioAnalyzer { AnalysisResult analyze(AudioFrame frame); }
- DTOs: AudioFrame, AnalysisResult (fields: data, timestamp, currentBPM, fftBands, isBeat)
- Stubs: FileAudioSource/SystemAudioSource für Tests.
Acceptance:
- Interfaces + Unit-Tests vorhanden.

TG2.2 FFTProcessor & BeatDetector
Responsibility: copilot
Developer Directive:
@copilot:task
- Branch: feature/TG2-audio-processing
- Implementiere FFTProcessor(fftSize, smoothing...) mit computeSpectrum().
- Implementiere BeatDetector, der BPM aus AudioFrames erkennt.
- Tests:
  - FFTProcessorTest (synthetischer Sinus => Peak in erwarteter Band)
  - BeatDetector integration test with beat_120bpm.wav (BPM within 119.5–120.5)
Acceptance:
- Tests grün.

TG2.3 Deterministic Test-Audio
Responsibility: copilot
Developer Directive:
@copilot:task
- Branch: feature/TG2-audio-testfiles
- Füge deterministische Test-Audios hinzu: src/test/resources/audio/beat_120bpm.wav, sine_440hz.wav
- Integrationstest: AudioEngineIntegrationTest laden & prüfen BPM.
Acceptance:
- BeatDetectionIT grün.

TG2.4 Audio Profiles
Responsibility: copilot
Developer Directive:
@copilot:task
- Branch: feature/TG2-audio-profiles
- Implementiere ProfileManager, lädt /config/audio_profiles.json (Beispiele: techno, ambient).
Acceptance:
- JSON + Tests vorhanden.

TG2.5 Audio Visualizer (UI)
Responsibility: copilot (dev) / shared (UI review)
Developer Directive:
@copilot:task
- Branch: feature/TG2-audio-visualizer
- JavaFX View: AudioAnalyzerDashboard.fxml + AudioAnalyzerDashboardController.java
  - Wellenform-Ansicht
  - Frequenz-Balken (Spectrum)
  - Beat-Indikator (Circle)
  - Slider für gain & beatSensitivity
- Test: TestFX (headless wo möglich)
Acceptance:
- TestFX-Tests + manuelle UI-Checks.

TG3 — Hue Engine NextGen (Multi-Bridge + Mapping)
-----------------------------------------------
TG3.1 Core Interfaces & EffectRouter
Responsibility: copilot
Developer Directive:
@copilot:task
- Branch: feature/TG3-hue-interfaces
- Schnittstellen:
  - IFastEffectController { startSession(); sendFrame(...); stopSession(); }
  - ILowEffectController { updateLights(List<LightUpdate> updates); }
- Implementiere EffectRouter, lädt lightmap.json und routet Frames entsprechend.
Acceptance:
- Unit-Tests validieren Routing-Logik.

TG3.2 DTOs & Mapping-Schema
Responsibility: copilot
Developer Directive:
@copilot:task
- Branch: feature/TG3-hue-config
- DTOs: LightUpdate, EffectFrame, Color (XY+bri)
- Schema: schemas/lightmap.schema.json (bridges + lights) — siehe Beispiel in Projektplan
- Utilities: JSON (de)serializer
Acceptance:
- Schema + load/save Tests vorhanden.

TG3.3 FastEffectController (UDP)
Responsibility: copilot
Developer Directive:
@copilot:task
- Branch: feature/TG3-hue-fast-udp
- Implementiere FastEffectController (DatagramSocket) — Hue Entertainment V2 like
  - UDP-Paket enthält sequenceNumber (int) und per-light payload
- Tests:
  - MockUdpServer + MockUdpServerTest prüft Paketankunft & sequenceNumber-Inkrement
- Metriken: fast.framesSent, fast.packetLossPct
Acceptance:
- Unit-Tests grün.

TG3.4 LowEffectController (HTTP)
Responsibility: copilot
Developer Directive:
@copilot:task
- Branch: feature/TG3-hue-low-http
- Implementiere LowEffectController mit:
  - Batching (z. B. 100ms)
  - Rate-Limiter (configurable, z. B. 10 req/s)
  - Backoff bei HTTP 429
- Tests:
  - MockHttpServer Tests (inkl. 429 Handling)
Acceptance:
- Tests grün.

TG3.5 Smart Mapping Tool (UI)
Responsibility: copilot (dev) / shared (UI review)
Developer Directive:
@copilot:task
- Branch: feature/TG3-hue-mapping-tool
- JavaFX View: SmartMappingTool.fxml + SmartMappingToolController.java
  - Canvas mit Drag&Drop, assign bridgeId & controlType, Save/Load config/lightmap.json
  - Live-Preview (test effect through EffectRouter)
- Test: TestFX save/load + drag/drop
Acceptance:
- TestFX-Tests + manuelle UI-Checks.

TG3.6 Multi-Bridge Support
Responsibility: copilot
Developer Directive:
@copilot:task
- Branch: feature/TG3-multibridge
- HueBridgeManager: support for multiple bridges, sequencing & timestamp compensation between bridges (stubs + tests)
Acceptance:
- Multi-bridge Tests grün.

TG4 — UI/UX Redesign
--------------------
TG4.1 Hauptnavigation & UserPrefs
Responsibility: copilot (dev) / shared (UX review)
Developer Directive:
@copilot:task
- Branch: feature/TG4-ui-redesign
- Refactor Main UI: permanent sidebar (Audio Dashboard, Light Mapping, Settings)
- Implement user_prefs.json persistence (window size, last volume, last opened view)
- ShortcutManager (Hotkeys z. B. Ctrl+1)
- Test: TestFX
Acceptance:
- Tests grün; manuelle UX-Signoff dokumentiert.

TG5 — Performance & Stabilität
------------------------------
TG5.1 Thread-Management & Queues
Responsibility: copilot
Developer Directive:
@copilot:task
- Branch: feature/TG5-stability
- Effekt-Pipeline via zentralem ExecutorService (max 4 Threads)
- BoundedBlockingQueue (ArrayBlockingQueue size=10) zwischen Analyzer und EffectRouter
- Verhalten: wenn Queue voll, ältesten Frame verwerfen (poll()) bevor neues offer()
- Tests: Unit-Tests zur Queue-Policy
Acceptance:
- Unit-Tests grün.

TG5.2 Langzeit-Stresstest
Responsibility: copilot
Developer Directive:
@copilot:task
- Branch: feature/TG5-perf-test
- Implementiere StressTest (z. B. StressTest.java) mit MockAudioSource (60 FPS), MockBridges
- Laufzeit: 30–60 Minuten (für CI ggf. 30min oder kürzer)
- Log: JVM Heap alle 5 Minuten, Fail bei >10% Heap-Wachstum nach 10min Warmup
Acceptance:
- Perf-Logs unter Threshold.

TG6 — CI/CD (Automatische Tests)
-------------------------------
TG6.1 GitHub Actions Workflow
Responsibility: copilot
Developer Directive:
@copilot:task
- Branch: feature/TG6-ci-cd
- Erstelle .github/workflows/ci.yml:
  - Trigger: push auf main, pull_request
  - Steps:
    - actions/checkout@v4
    - actions/setup-java@v4 (JDK 17)
    - ./gradlew clean build
    - ./gradlew test
    - ./gradlew checkstyleMain
    - Optional: run tools/analyze_codebase.py and upload artifacts
- CI muss Unit Tests, Integrationstests und (soweit möglich) headless TestFX ausführen.
Acceptance:
- CI Workflow vorhanden; PRs laufen im CI.

TG7 — Monitoring & Release
--------------------------
TG7.1 Metrik-Endpunkt
Responsibility: copilot
Developer Directive:
@copilot:task
- Branch: feature/TG7-monitoring
- Implementiere leichter HTTP-Server (Javalin / NanoHTTPD) auf localhost:9000
- Endpoint: /metrics liefert JSON:
  {
    "appStatus":"RUNNING",
    "currentBPM":120.5,
    "audioQueueSize":3,
    "fastFramesSent":10500,
    "lowRequestsSent":50
  }
Acceptance:
- /metrics liefert validen JSON.

TG7.2 CI-Failure Alerts (Discord)
Responsibility: copilot (workflow change) / human (secret)
Developer Directive:
@copilot:task
- Branch: feature/TG7-ci-alert
- Modifiziere .github/workflows/ci.yml:
  - Füge Step hinzu mit `if: failure()` am Ende des Test-Jobs, der Discord Webhook Action nutzt.
  - Webhook-URL als GitHub Secret: DISCORD_WEBHOOK_URL (human eintragen)
Project Lead Note:
- Human erzeugt Discord Webhook und trägt Secret in Settings -> Secrets ein.
Acceptance:
- Workflow step existiert; Secret muss manuell hinzugefügt.

Konkrete Templates (Issues / Tasks)
-----------------------------------
- Verwende die standardisierten Issue-Templates (siehe Dokumentteil "Issue / Copilot Task Templates" im Original).
- Beispiel (TG1.2) siehe Vorlagen im Projektplan: exaktes Format verwenden, damit @copilot:task richtig erkannt wird.

Gates & Acceptance Policy
-------------------------
- Gate A (Codeanalyse Complete): docs/codebase_overview.md + reports/metrics.json vorhanden und vom Project Lead genehmigt.
- Gate B (Audio MVP): FFTProcessorTest & BeatDetectionIT grün.
- Gate C (Hue MVP): FastEffectControllerTest & LowEffectControllerTest grün; EffectRouter validiert.
- Gate D (Integration & Stability): Langzeit-Perf-Test innerhalb Threshold; docs aktualisiert; Release-Tag erstellt.

Manual Steps for Project Lead (Kurz)
-----------------------------------
1. Fork LightBeat als PhotonJockey oder push initiales Repo (human).
2. Prüfe lokal `./gradlew clean build`.
3. Erstelle Issues für initiale Aufgaben mit den bereitgestellten Templates.
4. Review & Merge PRs nur wenn CI grün ist; führe UI-Reviews manuell durch.
5. Setze Secrets (z. B. DISCORD_WEBHOOK_URL) und Branch Protection Rules (require PR + status checks).

Zu klären / offene Punkte
-------------------------
- Dateinamen-Case: die Repo-Umgebung ist case-sensitive. Stelle sicher, dass die Ziel-Dateinamen exakt sind (z. B. Project-Planer_new.Md vs Project-Planer_New).
- Test-Audio-Files: Lizenz/Distribution prüfen bevor Sample-Audio in Repo aufgenommen wird.
- Mock-Server-Abhängigkeiten für Tests (NanoHTTPD / Testcontainers / Jetty) werden in build.gradle als Test-Dependencies ergänzt.
- CI Headless TestFX: ggf. zusätzliche Runner/Headless-Flags / xvfb auf Runner nötig.
- Real-Bridge Tests: die manuellen Hue-Tests benötigen echte Hardware und werden als human Tasks markiert.

Appendix: Konkrete technische Hinweise & Beispiele
-------------------------------------------------
- JSON-Schema Beispiel für lightmap.schema.json (bridges + lights) ist Bestandteil des TG3.2 Developer Directive und muss so in schemas/ abgelegt werden.
- UDP Frame Format: sequenceNumber (int), timestamp (long), per-light payload: id-length + id-bytes + float X + float Y + int bri — Genaues Serialisat ist in TG3.3 spezifiziert.
- Rate-Limiter / Backoff: LowEffectController muss 429 korrekt behandeln (exponentielles Backoff, höchstens 3 Retries pro Batch).

Ende
----
Dieses Dokument wurde aus den beiden ursprünglichen Dateien zusammengeführt, Duplikate entfernt und sprachlich konsolidiert. Nutze die Task-Vorlagen, um Issues zu erstellen und anschließend @copilot:task Anweisungen zu geben, damit ich (Copilot) die implementierbaren Tasks ausführen kann.
