# PhotonJockey
powered by AiLightBot Engine

Eine KI-gestützte Windows App für DJs, VJs und Streamer zur Visualisierung von smarten Lichteffekten synchron zum Beat elektronischer Musik. 

![logo](https://photonjockey.io/images/banner.png)

Der Musik-Visualizer unterstützt Philips Hue-kompatible Lichter, Multibridge Support und beherrscht sowohl die klassische Kommunikation mit der Bridge per Web requests, als auch die deutlich performantere DTLS/UDP Kommunikation mit der Entertainment API V2 Schnittstelle 
Dieses Projekt synchronisiert Beleuchtung mit Musik/Audioquellen und bietet Effekte, Mapping-Optionen und Anpassungsmöglichkeiten für Live- und Offline-Nutzung.

## Highlights / Features
- Synchronisation von Philips Hue-kompatiblen Lichtern mit Musik in Echtzeit
- Mehrere Visualisierungsmodi (z. B. Spektrumanalyse, Beat-Trigger, Farbflächen)
- Unterstützung für lokale und netzwerkbasierte Light-Controller (Hue-Bridge, kompatible Bridges)
- Konfigurierbare Mapping-Profile für individuelle Licht-Layouts
- Erweiterbar: Plugin-/Modul-Architektur für neue Effekte
- CLI- und GUI-Komponenten (je nach Distribution)
- Umfangreiche Unit- und Integrationstests (siehe src/test)

## Quick Start (Developer)
1. Repository klonen:
   git clone https://github.com/MrLongNight/PhotonJockey.git
2. Detaillierte Build-Anleitung:
   [docs/development/01-BUILD_INSTRUCTIONS.md](docs/development/01-BUILD_INSTRUCTIONS.md)
3. Mit Gradle bauen (Java 17 empfohlen):
   ./gradlew clean build

Hinweis: Projekt-Stack: Java 17 + Gradle. Codestil: Google Java Styleguide (Checkstyle).

## Projekt-Status & Implementierungsfortschritt
📊 **Implementierte Tasks:** Siehe [docs/project/03-IMPLEMENTED_TASKS.md](docs/project/03-IMPLEMENTED_TASKS.md) für eine Übersicht aller umgesetzten TG-Tasks.
📋 **Detaillierter Status:** [docs/project/02-IMPLEMENTATION_STATUS.md](docs/project/02-IMPLEMENTATION_STATUS.md) zeigt den vollständigen Fortschritt aller geplanten TaskGroups.
📖 **Alle Dokumentation:** Siehe [docs/README.md](docs/README.md) für einen kompletten Überblick.

## Visuelle Projektstruktur (ASCII-Baum, Top-Level, Stand: 02.11.25)
Nachfolgend eine leicht lesbare Baum-Ansicht der Top-Level-Struktur (Stand: master). Unterverzeichnisse sind soweit sinnvoll bis zu 2–3 Ebenen eingerückt. Nebem jedem Eintrag steht eine kurze Zweckbeschreibung.
## Visuelle Projektstruktur (Top-Level, Stand: aktueller Repo-Stand)

/
├── .editorconfig
├── .github/                    (Workflows, Issue/PR-Templates, ggf. CI Packaging)
├── .gitignore
├── README.md
├── build.gradle                (Gradle Buildskript — Dependencies, Packaging Tasks)
├── checkstyle.xml
├── docs/                       (User- und Entwicklerdokumentation)
│   ├── development/
│   ├── features/
│   ├── guides/
│   └── project/
├── gradle/                     (Gradle Wrapper / Helper)
├── gradlew
├── gradlew.bat
├── reports/                    (Build- / Test-Reports)
├── settings.gradle
├── src/
│   ├── main/
│   │   ├── java/               (Java-Quellcode: Audio-Analyse, Effects, Hue-Adapter...)
│   │   └── resources/          (Konfigurationen, Presets, Icons/Assets — erster Ort für App-Icon)
│   └── test/
│       └── java/               (Unit- und Integrationstests)
├── tools/                      (Hilfsskripte für Build, Packaging, Installer-Templates)
└── packaging/ (optional)       (falls vorhanden: WiX/NSIS/Inno-Vorlagen, icons/, installer-assets)

Kurze Erläuterungen:
- src/main/java: Hier befinden sich die Kernklassen — Audio-Input, Analyzer, Effect-Engines, Bridge-Adapter (Hue), Konfigurations-Loader und CLI-/GUI-Entrypoints.
- src/main/resources: Standardkonfigurationen, Effekt-Presets, ggf. Icons/Assets.
- src/test/java: Unit-Tests für einzelne Module und Integrationstests, die z. B. das Zusammenspiel von Analyzer und Light-Adapter überprüfen.
- docs/development/BUILD_INSTRUCTIONS.md: Schritt-für-Schritt zur lokalen Einrichtung (SDK-Versionen, native Abhängigkeiten, ggf. Bridge-Pairing).
- .github/: Enthält CI-Pipelines (GitHub Actions), die Build, Tests und Checkstyle ausführen.

## Build, Tests & CI
- Build: ./gradlew clean build
- Tests: ./gradlew test
- Checkstyle / Linting: In CI aktiviert; Checkstyle-Konfiguration in checkstyle.xml.
- CI: GitHub Actions (in .github/) baut und testet Pull Requests.

## Konfiguration & Laufzeit
- Laufzeitkonfigurationen (Bridge-IP, Token, Mappings) liegen typischerweise in resources oder in externen config-Dateien. Details in [docs/development/01-BUILD_INSTRUCTIONS.md](docs/development/01-BUILD_INSTRUCTIONS.md).
- Für lokale Hue-Integration: Bridge-IP & Auth-Token; Pairing-Schritte sind dokumentiert.

## Fehlerbehebung
Falls PhotonJockey nicht startet oder Probleme auftreten, siehe [docs/guides/troubleshooting/01-TROUBLESHOOTING_DE.md](docs/guides/troubleshooting/01-TROUBLESHOOTING_DE.md) für:
- Informationen zu Log-Dateien (photonjockey.log, photonjockey_error.log)
- Lösungen für häufige Probleme
- Anleitung zur Fehlerdiagnose

## Mitwirken / Contribution Guidelines (Kurz)
- Arbeitsablauf: Feature-Branch für jede Task (z. B. feature/3-hue-fast-udp), atomare Commits.
- PRs gegen main; CI muss grün sein bevor Mergen.
- Tests sind Pflicht — ohne Tests keine Akzeptanz.
- Codestil: Google Java Styleguide (Checkstyle erzwingt Regeln).
- Siehe [docs/development/02-CODING_CONVENTIONS.md](docs/development/02-CODING_CONVENTIONS.md) für Details.

## Lizenz
Siehe [docs/legal/01-LICENSE.md](docs/legal/01-LICENSE.md) für rechtliche Hinweise und Nutzungsbedingungen.
## Kontakt
Projekt-Maintainer: Mr. LongNight  
Hilfe & Diskussion: Discord (Link oben)
