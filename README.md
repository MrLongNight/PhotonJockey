# PhotonJockey
powered by AiLightBot Engine

Eine KI-gestützte Windows App für DJs, VJs und Streamer zur Visualisierung von smarten Lichteffekten synchron zum Beat elektronischer Musik. 

![logo](https://lightbeat.wunderlich.pw/images/banner.png)

[![Discord](https://discordapp.com/api/guilds/355919094026993665/widget.png)](https://discord.gg/mD3Ef6v)

PhotonJockey (LightBeat Fork) — Musik-Visualizer für Philips Hue-kompatible Lichter.  
Dieses Projekt synchronisiert Beleuchtung mit Musik/Audioquellen und bietet umfangreiche Effekte, Mapping-Optionen und Anpassungsmöglichkeiten für Live- und Offline-Nutzung.

Kurze Projekt-Website und Downloads: https://lightbeat.wunderlich.pw

## Highlights / Features
- Synchronisation von Philips Hue-kompatiblen Lichtern mit Musik in Echtzeit
- Mehrere Visualisierungsmodi (z.B. Spektrumanalyse, Beat-Trigger, Farbflächen)
- Unterstützung für lokale und netzwerkbasierte Light-Controller (Hue-Bridge, kompatible Bridges)
- Konfigurierbare Mapping-Profile für individuelle Licht-Layouts
- CLI- und GUI-Komponenten (je nach Build/Distribution)
- Erweiterbar: Plugins / Module für neue Effekte
- Tests: Unit- und Integrationstests vorhanden (siehe src/test)

## Quick Start (Developer)
1. Repository klonen:
   git clone https://github.com/MrLongNight/PhotonJockey.git
2. Siehe detaillierte Build-Anleitung:
   docs/development/BUILD_INSTRUCTIONS.md
3. Mit Gradle bauen (Java 17 erforderlich):
   ./gradlew build

Hinweis: Projekt nutzt Java 17 und Gradle. Für Entwicklungsdetails und CI-Informationen siehe die docs und .github.

## Projektstruktur (Top-Level)
Die folgende Liste zeigt die vorhandenen Top-Level-Dateien und -Ordner im Projekt (Stand: commit c1d6928fc1de8bc25d8474d1333116b8b2f986bb) mit kurzer Beschreibung:

- .editorconfig — Editor / Formatierungs-Regeln (zentrale Editor-Konfiguration)
- .github/ — GitHub-spezifische Konfiguration (Workflows, Issue- / PR-Templates, Actions)
- .gitignore — Dateien/Ordner, die nicht versioniert werden sollen
- README.md — Dieses Dokument
- build.gradle — Gradle Build-Skript (Build-Konfiguration, Dependencies, Tasks)
- checkstyle.xml — Checkstyle-Regeln (Code Style, Google Java Styleguide)
- docs/ — Projekt- und Entwicklungsdokumentation
  - Enthält weitere Unterverzeichnisse wie docs/development mit BUILD_INSTRUCTIONS.md
- gradle/ — Gradle Wrapper Hilfs-Skripte / Konfiguration
- gradlew — Gradle Wrapper (Unix)
- gradlew.bat — Gradle Wrapper (Windows)
- reports/ — Build- / Test-Reports (Ergebnisberichte, z. B. Test-Reports, Lint)
- settings.gradle — Gradle Settings (Projektdef.)
- src/ — Quellcode und Tests
  - src/main/java — Haupt-Source (Java-Klassen für die Anwendung)
  - src/test/java — Tests (Unit- und Integrationstests)
  - src/main/resources — ggf. Ressourcen (Konfigurationsvorlagen, Assets)
- tools/ — Hilfs-Skripte / Tools (Build/Debug/Packaging Hilfs-Skripte)

Hinweise zur Struktur:
- src/ enthält den Java-Quellcode (Projekt ist primär Java — ~90% laut Repo-Komposition). Die Tests sind zwingend erforderlich und sollen bei jeder Änderung mitgeführt werden.
- .github/ enthält CI-Workflows (GitHub Actions) — hier werden Build- und Test-Pipelines konfiguriert.
- docs/ ist die zentrale Stelle für Entwickleranleitungen, Architektur-Dokumentation und Benutzer-Guides.
- checkstyle.xml stellt sicher, dass der Code dem Google Java Styleguide entspricht. (Im Projektkontext wird Checkstyle für die CI verwendet.)

## Typische Dateien und Zweck (erläuternd)
- build.gradle / settings.gradle: Konfiguration des Builds, Abhängigkeiten, Plugin-Konfigurationen.
- checkstyle.xml: Automatische Stilprüfung; passt zur Regel "Codestil: Google Java Styleguide".
- docs/development/BUILD_INSTRUCTIONS.md: Detaillierte Schritte zum Einrichten der Entwicklungsumgebung, benötigte Abhängigkeiten und Platform-spezifische Hinweise.
- src/main/java/...: Implementierung der Core-Logik (Audio-Analyse, Light-Controller, Mapping, Effekte).
- src/test/java/...: Unit-Tests für einzelne Komponenten sowie Integrationstests für End-to-End-Workflows.
- .github/workflows/*: CI-Pipelines zum Bauen, Testen und ggf. Veröffentlichen.

## Build, Tests & CI
- Build: Gradle (Wrapper im Repo)
  - ./gradlew clean build
- Tests: ./gradlew test
- Checkstyle / Linting: Automatisch in CI. Projekt folgt striktem Style-Check.
- CI-Konfiguration: GitHub Actions (in .github), sorgt dafür, dass Pull Requests nur bei grünem CI gemerged werden sollten.

## Konfiguration & Laufzeit
- Konfigurationen (IP/Bridge-Settings, Mapping-Profile, Effekt-Parameter) liegen üblicherweise in src/main/resources oder externen config-Dateien, die beim Start geladen werden.
- Für lokale Hue-Integration: IP-Adresse der Bridge, Benutzer-Token bzw. Pairing-Schritte sind in der Build/Run-Doku beschrieben.

## Mitwirken / Contribution Guidelines
- Arbeitsablauf (Team-intern): Feature-Branches pro Task (z.B. feature/3-hue-fast-udp) — atomare Commits — Tests sind Pflicht.
- Pull Requests: PRs gegen main, CI muss grün sein bevor Mergen.
- Codestil: Google Java Styleguide (Checkstyle enforced).
- Tests: Jede Änderung sollte passende Unit- und (wenn nötig) Integrationstests enthalten.

## Lizenz
Angaben zur Lizenz sollten in der LICENSE-Datei stehen (falls vorhanden). Bitte prüfen Sie das Repo für die genaue Lizenzangabe.

## Kontakt
Projekt-Maintainer / PM: Mr. LongNight  
Diskussion & Hilfe: Discord-Server (Link oben)

---

Wenn Sie weitere Details zur Struktur (z. B. rekursive Auflistung einzelner Dateien in subdirectories) wünschen, kann ich eine detailliertere Übersicht erzeugen — ansonsten sind die oben aufgeführten Ordner und Dateien die relevanten Einstiegspunkte für Entwickler und Mitwirkende.
