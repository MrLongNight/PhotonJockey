# Release-Prozess und Dokumentation

Diese Dokumentation beschreibt den standardisierten Prozess für Releases und die Dokumentation von Änderungen in PhotonJockey.

---

## 📋 Übersicht

PhotonJockey folgt [Semantic Versioning](https://semver.org/lang/de/) und dokumentiert alle Änderungen nach dem [Keep a Changelog](https://keepachangelog.com/de/1.0.0/) Standard.

---

## 🔢 Versioning-Schema (Semantic Versioning)

### Version Format: MAJOR.MINOR.PATCH

**MAJOR (1.x.x)** - Breaking Changes
- Inkompatible API-Änderungen
- Grundlegende Architektur-Überarbeitungen
- Änderungen, die Migration erfordern

**MINOR (x.1.x)** - Neue Features
- Neue Funktionalität (abwärtskompatibel)
- Neue APIs oder Interfaces
- Größere Performance-Verbesserungen
- Neue Dokumentation

**PATCH (x.x.1)** - Bugfixes und kleine Verbesserungen
- Bugfixes
- Kleine Performance-Optimierungen
- Dokumentations-Korrekturen
- Dependency-Updates (Sicherheit)

### Beispiele
- `0.0.1` → `0.0.2`: Patch-Release (Bugfixes)
- `0.0.2` → `0.1.0`: Minor-Release (neue Features)
- `0.1.0` → `1.0.0`: Major-Release (erstes produktives Release)
- `1.0.0` → `2.0.0`: Major-Release (Breaking Changes)

---

## 📝 CHANGELOG.md Pflege

### Struktur

Das CHANGELOG.md folgt dem "Keep a Changelog" Format:

```markdown
# Changelog

## [Unreleased]
### Hinzugefügt
### Geändert
### Veraltet
### Entfernt
### Behoben
### Sicherheit

## [1.0.0] - 2025-XX-XX
### Hinzugefügt
- Feature A
- Feature B
...
```

### Kategorien

**Hinzugefügt** (Added)
- Neue Features
- Neue Funktionalität
- Neue Dateien/Module

**Geändert** (Changed)
- Änderungen an bestehender Funktionalität
- API-Änderungen
- Dependency-Updates (non-security)

**Veraltet** (Deprecated)
- Features, die in zukünftigen Versionen entfernt werden
- Migrations-Hinweise

**Entfernt** (Removed)
- Entfernte Features
- Entfernte APIs
- Gelöschte Dateien

**Behoben** (Fixed)
- Bugfixes
- Fehlerkorekturen
- Performance-Probleme

**Sicherheit** (Security)
- Sicherheits-Fixes
- Vulnerability-Patches
- Security-relevante Dependency-Updates

### Best Practices

1. **Kontinuierliche Updates**: CHANGELOG bei jedem PR aktualisieren
2. **Unreleased-Section**: Änderungen sammeln in `[Unreleased]`
3. **Benutzer-Perspektive**: Beschreibe Änderungen aus Nutzersicht
4. **Verlinken**: Issue- und PR-Nummern verlinken
5. **Gruppieren**: Ähnliche Änderungen zusammenfassen
6. **Präzise**: Kurze, klare Beschreibungen

### Beispiel-Eintrag

```markdown
## [1.0.0] - 2025-12-15

### Hinzugefügt
- Smart Mapping Tool mit Drag & Drop Interface (#42)
- Audio-Profile-System mit Techno, House, Ambient Presets (#38)
- Windows MSI Installer Support (#45)

### Geändert
- Upgrade auf Java 21 von Java 17 (#40)
- Verbesserte Beat-Detection-Algorithmus für höhere Genauigkeit (#41)

### Behoben
- UI Initialization Error beim Start (#39)
- Memory Leak in Audio-Analyzer (#43)
- Bridge-Connection Timeout-Probleme (#44)
```

---

## 🚀 Release-Prozess

### Vorbereitung (Development Phase)

1. **Feature-Development**
   - Feature-Branch erstellen: `feature/feature-name`
   - Code implementieren mit Tests
   - CHANGELOG.md in `[Unreleased]` aktualisieren
   - Pull Request öffnen

2. **Code Review**
   - CI muss grün sein (Tests + Checkstyle)
   - Code Review durch Maintainer
   - Nach Approval: Merge in `main` Branch

3. **Unreleased-Section pflegen**
   - Bei jedem Merge: CHANGELOG.md aktualisieren
   - Änderungen in korrekter Kategorie dokumentieren
   - Issue/PR-Nummern hinzufügen

### Release-Erstellung

#### Schritt 1: Version festlegen

Entscheide Versions-Nummer basierend auf Änderungen:
- Breaking Changes? → MAJOR bump
- Neue Features? → MINOR bump
- Nur Bugfixes? → PATCH bump

#### Schritt 2: Version aktualisieren

```bash
# build.gradle
version = '1.0.0'  # Alte Version: '0.0.2'
```

#### Schritt 3: CHANGELOG.md finalisieren

1. `[Unreleased]` in versionierte Section umbenennen:
```markdown
## [1.0.0] - 2025-12-15
```

2. Neue `[Unreleased]` Section erstellen:
```markdown
## [Unreleased]

### In Planung
- Feature X
- Feature Y
```

3. Version-Links am Ende aktualisieren:
```markdown
[Unreleased]: https://github.com/MrLongNight/PhotonJockey/compare/v1.0.0...HEAD
[1.0.0]: https://github.com/MrLongNight/PhotonJockey/compare/v0.0.2...v1.0.0
```

#### Schritt 4: Commit und Tag

```bash
# Alle Änderungen committen
git add build.gradle CHANGELOG.md
git commit -m "Release v1.0.0"

# Git Tag erstellen
git tag -a v1.0.0 -m "Release v1.0.0"

# Pushen
git push origin main
git push origin v1.0.0
```

#### Schritt 5: Automatischer Build

GitHub Actions (`build-and-release.yml`) wird automatisch gestartet:
- Baut Fat JAR auf Ubuntu
- Baut Windows MSI Installer
- Erstellt GitHub Release (als Draft)
- Lädt Artefakte hoch

#### Schritt 6: Release veröffentlichen

1. Gehe zu [GitHub Releases](https://github.com/MrLongNight/PhotonJockey/releases)
2. Finde den Draft-Release für `v1.0.0`
3. Überprüfe hochgeladene Artefakte:
   - `photonjockey-1.0.0-all.jar`
   - `photonjockey-1.0.0.msi`
4. Release Notes aus CHANGELOG.md kopieren
5. Klicke "Publish Release"

### Post-Release

1. **Announcement**
   - GitHub Release published
   - Optional: Discord/Social Media

2. **Next Development Cycle**
   - Neue Features in `[Unreleased]` dokumentieren
   - Feature-Branches für nächste Version erstellen

---

## 🔄 Kontinuierliche Dokumentation

### Bei jedem Pull Request

**Pflicht-Checks:**
- [ ] CHANGELOG.md aktualisiert (in `[Unreleased]`)
- [ ] Änderung in korrekter Kategorie
- [ ] Benutzerfreundliche Beschreibung
- [ ] Issue/PR-Nummer referenziert

**Optional:**
- Feature-spezifische Dokumentation in `/docs/features/`
- README.md Update (bei Major Features)

### Wöchentlich/Monatlich

- CHANGELOG.md Review: Duplikate entfernen, gruppieren
- Überprüfe `[Unreleased]` auf Vollständigkeit
- Dokumentation-Links validieren

### Vor jedem Release

- Vollständiges CHANGELOG.md Review
- Alle Änderungen kategorisiert
- Version-Links korrekt
- Release Notes Preview erstellen

---

## 📋 Checkliste für Releases

### Pre-Release Checklist

- [ ] Alle geplanten Features gemerged
- [ ] Alle Tests grün (lokal und CI)
- [ ] CHANGELOG.md vollständig und aktuell
- [ ] Versionsnummer in `build.gradle` aktualisiert
- [ ] Dokumentation aktualisiert (README, Features, etc.)
- [ ] Breaking Changes dokumentiert (falls MAJOR)
- [ ] Migration Guide erstellt (falls nötig)

### Release Checklist

- [ ] Commit erstellt: `"Release v1.0.0"`
- [ ] Git Tag erstellt: `v1.0.0`
- [ ] Tag und Commits gepushed
- [ ] CI/CD Build erfolgreich
- [ ] Artefakte verfügbar (JAR + MSI)
- [ ] Draft Release überprüft
- [ ] Release Notes aus CHANGELOG kopiert
- [ ] Release published

### Post-Release Checklist

- [ ] Release funktioniert (Installation getestet)
- [ ] Announcement veröffentlicht
- [ ] `[Unreleased]` Section erstellt
- [ ] Version-Links aktualisiert
- [ ] Milestone auf GitHub geschlossen (optional)

---

## 🛠️ Tools und Automation

### GitHub Actions

**unit-tests.yml**
- Trigger: Push/PR auf main
- Führt Tests aus
- Lädt Test-Reports hoch

**build-and-release.yml**
- Trigger: Version Tag (`v*.*.*`)
- Baut JAR und MSI
- Erstellt GitHub Release
- Lädt Artefakte hoch

### Lokale Tools

**Build testen:**
```bash
./gradlew clean build
./gradlew shadowJar
```

**Tests ausführen:**
```bash
./gradlew test
```

**Release lokal simulieren:**
```bash
./gradlew clean build shadowJar
# Überprüfe build/libs/photonjockey-*-all.jar
```

---

## 📚 Weiterführende Ressourcen

- [Semantic Versioning](https://semver.org/lang/de/)
- [Keep a Changelog](https://keepachangelog.com/de/1.0.0/)
- [GitHub Releases Guide](https://docs.github.com/en/repositories/releasing-projects-on-github)
- [Conventional Commits](https://www.conventionalcommits.org/) (optional)

---

## 💡 Tipps und Best Practices

### CHANGELOG.md

✅ **GUT:**
```markdown
- Smart Mapping Tool mit Drag & Drop Interface für Light-Positionierung (#42)
- Audio-Profile-System mit 3 Standard-Presets (Techno, House, Ambient) (#38)
- Behoben: Memory Leak in Audio-Analyzer bei langen Sessions (#43)
```

❌ **SCHLECHT:**
```markdown
- Added new feature
- Fixed bug in analyzer
- Updated dependencies
```

### Commit Messages

✅ **GUT:**
```
Release v1.0.0

- Updated version in build.gradle
- Finalized CHANGELOG.md for v1.0.0 release
- Added release notes
```

❌ **SCHLECHT:**
```
version bump
```

### Release Timing

- **Regelmäßig**: Besser kleine, häufige Releases als seltene große
- **Feature-Complete**: Nur Features releasen, die fertig und getestet sind
- **Dokumentiert**: Immer mit vollständigem CHANGELOG
- **Kommuniziert**: Nutzer über Breaking Changes informieren

---

**Letzte Aktualisierung:** 2025-11-06  
**Verantwortlich:** Mr. LongNight  
**Version:** 1.0
