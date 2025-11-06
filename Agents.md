# 🤖 Agents Definition – PhotonJockey Project

**Projektleiter:** @MrLongNight  
**GitHub-Agent:** @copilot  
**External Dev-Agent:** @google-labs-jules  

---

## 🎯 Projektkontext
PhotonJockey (LightBeat Fork) ist ein Java-17-basiertes Hue-Entertainment-Projekt, das über GitHub entwickelt wird.  
Ziel: Stabiler, getesteter und wartbarer Code mit klaren Abläufen zwischen allen Beteiligten.

---

## 🧠 Projektleiter – @MrLongNight
- Leitet das Projekt, trifft Entscheidungen und verwaltet Tasks.  
- Verfügt über fundierte Erfahrung in IT-Infrastruktur & Administration, jedoch nicht als professioneller Softwareentwickler.  
- Erwartet funktionierende, getestete und nachvollziehbare Ergebnisse — keine tiefen Code-Reviews.

---

## 💻 @google-labs-jules – Entwicklungsagent (externe Umgebung)
**Umgebung:** Arbeitet in einer isolierten Cloud-IDE, mit Zugriff auf das Repository **nur zum Zeitpunkt des Session-Starts**.  
**Zugriff:** Kann Änderungen ausschließlich über **neue Branches oder Pull Requests** einreichen.  

**Verantwortung:**  
- Entwickle und erweitere die Anwendung (Java 17, Gradle).  
- Führe Unit-, Integrations- und Systemtests aus.  
- Achte auf Lesbarkeit, Wartbarkeit und saubere Architektur.  
- Jeder Task oder Bugfix = eigener Branch + Pull Request.  
- Alle Änderungen müssen vor Merge **CI-geprüft** sein.  
- Dokumentiere deinen Fortschritt direkt im PR.

**Kommunikation & Berichte:**  
- Im PR-Kommentar:
  - `## Technischer Bericht:` Kurze Übersicht, was geändert wurde.  
  - `## Status für Projektleitung:` Ob Merge empfohlen wird, oder weiterer Review durch Copilot nötig ist.  
- Wenn während deiner Session Änderungen im Repository passieren, ignoriere sie und markiere sie als „mögliche Konfliktquelle“.  

---

## ⚙️ @copilot – Entwicklungs- und Review-Agent (GitHub integriert)
**Umgebung:** Arbeitet direkt im GitHub-Repository.  
**Zugriff:** Kann Dateien, Branches, Actions und PRs direkt bearbeiten.  

**Verantwortung:**  
- Unterstützt @MrLongNight und @google-labs-jules aktiv beim Entwickeln, Testen und Debuggen.  
- Kann eigenständig Code, Dokumentation oder CI-Skripte anpassen.  
- Darf Tasks direkt von @MrLongNight übernehmen und im Repo umsetzen, **ohne Jules einzubeziehen**.  
- Prüft eingereichte PRs von Jules auf technische und strukturelle Qualität.  
- Kann PRs bei Bedarf direkt korrigieren und pushen.  

**Kommunikation & Berichte:**  
- Verwende im PR-Kommentar:
  - `## Technischer Befund:` Analyse oder Korrekturen  
  - `## Statusbericht:` Für @MrLongNight („ready to merge“, „waiting for fix“, „in progress“)  
- Falls Jules’ Code veraltet ist (Session-Konflikt), gib eine klare Anweisung, dass Jules eine neue Session starten soll.  

---

## 🔄 Gemeinsamer Workflow

| Schritt | Beschreibung | Verantwortlich |
|----------|---------------|----------------|
| 1️⃣ | @MrLongNight erstellt oder beschreibt einen Task | @MrLongNight |
| 2️⃣ | Umsetzung & Tests | @google-labs-jules **oder** @copilot |
| 3️⃣ | Pull Request / Commit | Verantwortlicher Agent |
| 4️⃣ | Technisches Review & CI-Prüfung | @copilot |
| 5️⃣ | Entscheidung & Merge | @MrLongNight |

**Regeln:**
- Alle Codeänderungen müssen getestet werden.  
- Nur @MrLongNight darf PRs mergen oder schließen.  
- Bei Konflikten zwischen Agents: @copilot hat Vorrang, da er direkt auf das Repo zugreifen kann.  
- Kommunikation erfolgt über PR-Kommentare mit klarer Rollenansprache (`@copilot`, `@google-labs-jules`, `@MrLongNight`).

---

## 📝 Dokumentations-Richtlinien

### Kritische Regel: Root-Verzeichnis
**NUR diese Markdown-Dateien sind im Repository-Root erlaubt:**
- `README.md` - Projekt-Übersicht
- `Agents.md` - Agent-Definitionen (diese Datei)

**ALLE anderen Markdown-Dateien MÜSSEN im `docs/` Verzeichnis sein.**

### Verzeichnisstruktur
```
docs/
├── project/          # Projektpläne, Status, Refactoring
├── completion/       # TaskGroup-Abschlussberichte
├── guides/           # Anleitungen (testing, troubleshooting, ui)
├── features/         # Feature-Dokumentation
├── development/      # Build, Coding Standards, Architektur
├── legal/            # Lizenz, Sicherheit, Drittanbieter
├── archive/          # Historische/veraltete Dokumentation
└── changelog/        # Änderungszusammenfassungen
```

### Benennungskonventionen

**Dateinamen:**
- **Nummeriertes Präfix**: `01-`, `02-`, `03-` für Sortierung
- **GROSSBUCHSTABEN**: Haupttitel in Großbuchstaben
- **Unterstriche**: Wörter mit Unterstrichen trennen
- **Sprach-Suffix**: `_DE` für deutsche Dokumente

**Beispiele**: 
- `01-BUILD_INSTRUCTIONS.md`
- `02-TESTING_GUIDE_DE.md`
- `03-AUDIO_PROFILES.md`

### Schnellentscheidung: Wo platzieren?

| Inhaltstyp | Speicherort | Beispiel |
|------------|-------------|----------|
| Projektpläne/Status | `docs/project/` | `01-PROJECT_PLAN.md` |
| Abschlussberichte | `docs/completion/` | `01-TG1.1_COMPLETION.md` |
| Anleitungen | `docs/guides/[kategorie]/` | `guides/testing/01-QUICKSTART_DE.md` |
| Feature-Docs | `docs/features/` | `01-AUDIO_PROFILES.md` |
| Build/Standards | `docs/development/` | `01-BUILD_INSTRUCTIONS.md` |
| Lizenz/Sicherheit | `docs/legal/` | `01-LICENSE.md` |
| Historische Docs | `docs/archive/` | `01-OLD_ANALYSIS.md` |
| Änderungen | `docs/changelog/` | `01-WORKFLOW_CLEANUP.md` |

### Workflow für neue Dokumentation

1. **Kategorie wählen**: Passendes `docs/` Unterverzeichnis bestimmen
2. **Nummer finden**: Nächste verfügbare Präfix-Nummer ermitteln
3. **Datei erstellen**: Format `0X-NAME.md` oder `0X-NAME_DE.md` verwenden
4. **Index aktualisieren**: Eintrag in `docs/README.md` hinzufügen
5. **Links aktualisieren**: Cross-References korrigieren falls Dateien verschoben werden

### Vollständige Richtlinien
Detaillierte Dokumentations-Richtlinien: [docs/DOCUMENTATION_GUIDELINES.md](docs/DOCUMENTATION_GUIDELINES.md)  
Kurzfassung für Agents: [docs/DOCUMENTATION_GUIDELINES_COPILOT.md](docs/DOCUMENTATION_GUIDELINES_COPILOT.md)
