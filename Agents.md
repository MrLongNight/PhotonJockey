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
