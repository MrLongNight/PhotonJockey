# Dokumentationsstruktur / Documentation Structure

Dieses Verzeichnis enthält die gesamte Projektdokumentation, organisiert in thematischen Unterordnern.

## Verzeichnisstruktur / Directory Structure

### 📋 Projekt_Vorgaben/
**Projekt-Spezifikationen und Konventionen** - Enthält alle grundlegenden Projektrichtlinien, Build-Anleitungen und Sicherheitshinweise.

- `BUILD_ANLEITUNG.md` - Detaillierte Anleitung zum Bauen des Projekts und zur Einrichtung der Entwicklungsumgebung
- `CODING_KONVENTIONEN.md` - Kodierungsstandards, Branch- und Commit-Konventionen
- `PROJEKT_PLAN.md` - Vollständiger Projektplan mit TaskGroups und Aufgaben
- `SICHERHEIT.md` - Sicherheitsrichtlinien und Best Practices
- `LIZENZ.md` - Projektlizenz
- `DRITTANBIETER_LIZENZEN.md` - Lizenzen von Drittanbieter-Bibliotheken

### 📊 Reports/
**Analyseberichte und Abschlussberichte** - Enthält alle generierten Reports, Codeanalysen und Task-Abschlussberichte.

- `TG1.1_ABSCHLUSSBERICHT.md` - Abschlussbericht für TaskGroup 1.1 (Code-Stil & Projektkonventionen)
- `TG2.4_ABSCHLUSSBERICHT.md` - Abschlussbericht für TaskGroup 2.4 (Audio-Profile Feature)
- `CODEBASE_UEBERSICHT.md` - Automatisch generierte Übersicht über die Codebasis
- `REFACTORING_PLAN.md` - Plan für Code-Refactoring mit Prioritäten

### 🎯 Feature_Dokumentation/
**Feature-spezifische Dokumentation** - Enthält detaillierte Beschreibungen einzelner Features und deren Verwendung.

- `AUDIO_PROFILE_FEATURE.md` - Dokumentation des Audio-Profile-Features

### 📐 Diagramme/
**Architektur- und Abhängigkeitsdiagramme** - Enthält visuelle Darstellungen der Projektstruktur.

- `dependency.dot` - Graphviz-Diagramm der Paketabhängigkeiten

### 🔧 Skripte/
**Demo- und Hilfsskripte** - Enthält ausführbare Skripte für Demos und Hilfsfunktionen.

- `demo_audio_profiles.sh` - Demo-Skript für Audio-Profile

## Navigation

- **Neue Entwickler:** Beginnen Sie mit `Projekt_Vorgaben/BUILD_ANLEITUNG.md`
- **Kodierungsstandards:** Siehe `Projekt_Vorgaben/CODING_KONVENTIONEN.md`
- **Projektübersicht:** Siehe `Reports/CODEBASE_UEBERSICHT.md`
- **Feature-Dokumentation:** Siehe `Feature_Dokumentation/`

## Hinweise / Notes

- Alle Dokumente verwenden deutschsprachige Dateinamen für bessere Klarheit
- Automatisch generierte Berichte befinden sich in `Reports/`
- Build-Skripte und Tools befinden sich im Hauptverzeichnis unter `tools/`
