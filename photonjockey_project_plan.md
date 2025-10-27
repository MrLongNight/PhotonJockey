# PhotonJockey – powered by LightBot Engine
Ein KI-gestütztes Licht-Performance-System für DJs, VJs und Streamer

---

## 1) Analyse der aktuellen Projektplanung (Version 2.5)

### Was bereits sehr gut ist
- **Modulare TaskGroups** (Codeanalyse → Audio → Hue → UI → CI) — sinnvolle Reihenfolge.
- **Klare Trennung** zwischen FastEffectControl (V2 UDP) und LowEffectControl (HTTP).
- **Smart Mapping Tool** als zentrale Bedienkomponente — richtige Priorität.
- **Testphilosophie:** „eine abgeschlossene Unit pro Commit“ ist genau richtig für Copilot-Arbeit.
- **Multi-Bridge / Fallback / Sync** berücksichtigt — wichtig für professionelle Setups.

### Schwachstellen / Lücken (wo Nacharbeit nötig ist)
1. Fehlende Granularität auf Methoden-/Datei-Ebene.
2. Unpräzise Test-Definitionen.
3. Kein Codestyle / Schnittstellenvertrag (API contracts).
4. Kein Branching/Release-Workflow definiert.
5. Fehlende Observability und Logging-Spezifikationen.
6. Fehlendes Format für `lightmap.json` und fehlende API-Events.
7. Keine Replayable Tests / Regressionstests.
8. Fehlende Review Gates und Abnahmebedingungen.

### Risiken & Mitigation
- **Risk:** Copilot schreibt zu große Änderungen → *Mitigation:* max. 1 Klasse / 1 Feature per Commit.
- **Risk:** UDP Latenz / Packet Loss → *Mitigation:* Paketnummern, Retry Logic, Monitoring.
- **Risk:** Regression bei Entertainment V2 → *Mitigation:* Feature Flags + Staging.

---

## 2) Optimierter Copilot-ready Projektplan

### Strukturkonventionen
- **Branches:** `feature/<TG#>-<short>` (z. B. `feature/3-hue-engine`)
- **Commits:** Template mit TG, Test-ID und Reviewer.
- **PR Template:** Build/Test/Doku-Checklist.
- **Codestyle:** Google Java Style; enforced via `.editorconfig` & `checkstyle.xml`.
- **Copilot-Regel:** max. 1 Klasse oder 1 Modul pro PR.

---

## TaskGroup 1 – Codeanalyse & Refactoring

### TG1.1 Fork & Initial Build
- **Action:** Fork → Branch `feature/1-fork-setup`.
- **Deliverable:** `docs/setup.md` mit JDK, Gradle, Env.
- **Acceptance:** `./gradlew build` = 0 Exit Code.

### TG1.2 Automated Static Analysis
```yaml
@copilot:task
Create script `tools/analyze_codebase.py` that scans `src/` and outputs `docs/codebase_overview.md`.
Must include package list, main classes, threads created.
Also produce UML dependency graph `docs/diagrams/dependency.dot`.
```

### TG1.3 Code Metrics
- **Action:** Add Sonar/SpotBugs config.
- **Output:** `reports/metrics.json` mit LOC und Complexity.

### TG1.4 Refactor Plan Generation
- **Action:** Automatisch aus metrics Hotspots in `docs/refactor_plan.md`.

### TG1.5 Small Refactors
```yaml
@copilot:task
Refactor `AudioAnalyzer` → use `ScheduledExecutorService` statt Threads.
Add test `AudioAnalyzerTest#startStop_noException`.
```

---

## TaskGroup 2 – Audio Engine Upgrade

### TG2.1 Interfaces
```java
@copilot:task
interface IAudioSource { AudioFrame pollFrame(); void start(); void stop(); }
interface IAudioAnalyzer { AnalysisResult analyze(AudioFrame f); }
```

### TG2.2 FFT & Beat Detection
```yaml
@copilot:task
Implement FFTProcessor (fftSize, smoothing) & BeatDetector (BPM detection).
Add deterministic unit tests with sine wave input.
```

### TG2.3 Audio Analyzer Dashboard (Visualizer)
- FXML UI mit Waveform, Spectrum, Beat-Indicator, Sliders.
- Subscribes auf `IAudioAnalyzer` events.

### TG2.4 Profiles & Presets
```json
{ "profiles": [{ "id": "techno", "fftSize":2048, "gain":1.2 }] }
```

### TG2.5 Test Audio
- Add `beat_120bpm.wav` & Integration test für BPM ≈ 120 ± 0.5.

---

## TaskGroup 3 – Hue Engine NextGen (Multi-Bridge + Mapping)

### TG3.1 Core Interfaces
```java
@copilot:task
Interface IFastEffectController { startSession(); sendFrame(); stopSession(); }
Interface ILowEffectController { ... }
Class EffectRouter routes frames by light id → Fast or Low.
```

### TG3.2 DTOs & Mapping Config
```json
{ "lights": [{"id":"1","x":120.5,"y":300.0,"bridge":"b1"}] }
```
Schema unter `schemas/lightmap.schema.json`.

### TG3.3 FastEffectController (UDP)
```yaml
@copilot:task
Implement UDP-based FastEffectController.
Frame: sequenceNumber, timestamp, per-light data.
Unit Test: MockUdpServer verifies sequence increment.
```

### TG3.4 LowEffectController (HTTP)
- Batch Updates, Rate Limit.
- MockHttpServer Test: handle 429 gracefully.

### TG3.5 EffectRouter Tests
- 10 Fast + 2 HTTP Lights.
- Assert routing per group.

### TG3.6 Multi-Bridge Sync
- 2 Mock Bridges, latency compensation test.

### TG3.7 Smart Mapping Tool
- Canvas UI (drag/drop lights, assign bridge).
- Live preview test frames.
- Save/Load verified via JSON.

---

## TaskGroup 4 – UI/UX Redesign
- **MainController.java** mit Sidebar (Audio, Licht, Settings, Status).
- **Hotkeys via ShortcutManager**.
- **User prefs:** `user_prefs.json`.

---

## TaskGroup 5 – Performance & Stability
- **Threads:** ExecutorService (4 threads).
- **Queue:** Bounded queue, drop oldest.
- **Perf-Test:** 1h run mit MockBridge, Memory Growth < 5%.

---

## TaskGroup 6 – CI/CD & Tests
```yaml
@copilot:task
Create `.github/workflows/ci.yml`
Steps: checkout, JDK, build, test, lint, upload docs.
PR rule: max 10 files, 1 class changed.
```

---

## TaskGroup 7 – Docs, Release, Monitoring
- **Docs:** overview, refactor plan, hue_integration, lightmap_guide.
- **Metrics endpoint:** `localhost:9000/metrics` JSON.
- **Alert:** Discord webhook bei Test-Fail.

---

## Review Gates
- **Gate A:** Codeanalyse abgeschlossen, reports vorhanden.
- **Gate B:** Audio MVP getestet.
- **Gate C:** Hue MVP (UDP/HTTP Routing funktioniert).
- **Gate D:** E2E Stability (1h Loadtest).

---

## Beispiel Copilot-Issue
```yaml
@copilot:task
TG3-2 Implement FastEffectController (UDP)
- Create class pw.wunderlich.lightbeat.hue.FastEffectController
- Methods: startSession(), sendFrame(), stopSession()
- MockUdpServer test verifies incrementing sequence
- Add metrics points: fast.framesSent, fast.lastAckMs
```

---

## JSON Schema Beispiel
```json
{ "$schema":"http://json-schema.org/draft-07/schema#", "type":"object", "required":["bridges","lights"] }
```

---

## Progress Tracking
- `docs/task_status.md` auto-updated by CI.
- Format: `- [ ] TG3-2 FastEffectController – PR #123 – Status: open/merged`

---

## Review Checklist
1. Build green ✅
2. Tests green ✅
3. Docs aktualisiert ✅
4. Change Size ok ✅
5. Manual Review ok ✅

---

**Nächste Schritte:**
1. Repo-Fork vorbereiten → Datei in `/docs/PROJECT_PLAN_VK.md` committen.
2. Copilot Task `TG1.2` starten → Codebase Overview generieren.
3. Nach Review Gate A die ersten Refactors freigeben.

