# PhotonJockey Fehlerbehebung

## Wenn PhotonJockey nicht startet

Falls PhotonJockey nicht startet oder sofort wieder schließt, gibt es jetzt ein umfassendes Logging-System, das hilft, das Problem zu identifizieren.

## Log-Dateien

PhotonJockey erstellt automatisch zwei Log-Dateien im Installationsverzeichnis bzw. im Verzeichnis, aus dem die Anwendung gestartet wurde:

### 1. photonjockey.log
Dies ist die Haupt-Log-Datei mit detaillierten Informationen über:
- Startvorgang der Anwendung
- System-Informationen (Java-Version, Betriebssystem, Architektur)
- JavaFX-Verfügbarkeit
- Alle Fehler und Warnungen während der Laufzeit

**Beispiel-Speicherorte:**
- **Windows (Installer):** `C:\Program Files\PhotonJockey\photonjockey.log`
- **Windows (ZIP):** Im gleichen Ordner wie `launch_photonjockey.bat`
- **Linux/macOS:** Im gleichen Verzeichnis wie das Startskript

### 2. photonjockey_error.log
Diese Datei wird nur erstellt, wenn ein schwerwiegender Fehler beim Start auftritt. Sie enthält:
- Zeitstempel des Fehlers
- Vollständige Fehlermeldung
- Stack-Trace für detaillierte Fehleranalyse

## Häufige Probleme und Lösungen

### Problem: Anwendung startet nicht, kein Fenster erscheint

**Lösung:**
1. Öffnen Sie `photonjockey_error.log` im Installationsverzeichnis
2. Suchen Sie nach der Fehlermeldung
3. Siehe spezifische Lösungen unten

### Problem: "JavaFX runtime components are missing"

**Ursache:** Die Java-Installation enthält keine JavaFX-Komponenten.

**Lösung:**
- Verwenden Sie den PhotonJockey Windows-Installer (.msi), der eine vollständige Java-Laufzeitumgebung mit JavaFX enthält
- ODER: Installieren Sie ein JDK mit JavaFX (z.B. [Azul Zulu FX](https://www.azul.com/downloads/?package=jdk-fx) oder [Bellsoft Liberica Full JDK](https://bell-sw.com/pages/downloads/))
- Mindestens Java 21 erforderlich

### Problem: "Unable to open DISPLAY" (Linux)

**Ursache:** Keine grafische Benutzeroberfläche verfügbar oder Display nicht konfiguriert.

**Lösung (Linux):**
```bash
export DISPLAY=:0
./launch_photonjockey.sh
```

Oder wenn Sie über SSH verbunden sind:
```bash
ssh -X benutzer@server
./launch_photonjockey.sh
```

### Problem: Anwendung stürzt mit unbekanntem Fehler ab

**Diagnose:**
1. Öffnen Sie `photonjockey.log`
2. Scrollen Sie zum Ende der Datei
3. Suchen Sie nach Zeilen mit `ERROR` oder `FATAL`
4. Der Stack-Trace zeigt, wo der Fehler aufgetreten ist

**Hilfe anfordern:**
Wenn Sie das Problem nicht selbst lösen können:
1. Öffnen Sie ein Issue auf GitHub: https://github.com/MrLongNight/PhotonJockey/issues
2. Fügen Sie folgende Informationen bei:
   - Betriebssystem und Version
   - Java-Version (führen Sie `java -version` aus)
   - Inhalt von `photonjockey_error.log` (falls vorhanden)
   - Relevante Teile von `photonjockey.log` (insbesondere Fehlerzeilen)

## Erweiterte Diagnose

### System-Informationen anzeigen

Die ersten Zeilen in `photonjockey.log` zeigen wichtige System-Informationen:
```
PhotonJockey Launcher starting
Java Version: 21.0.x
Java Vendor: ...
OS: Windows 11 / Linux / macOS
OS Architecture: amd64 / aarch64
JavaFX Available: true / false
```

Diese Informationen helfen bei der Fehlerdiagnose.

### Debug-Modus

Für zusätzliche Audio-Geräte-Informationen können Sie PhotonJockey im Debug-Modus starten:

**Windows:**
```batch
launch_photonjockey.bat --dump-all-devices
```

**Linux/macOS:**
```bash
./launch_photonjockey.sh --dump-all-devices
```

Dies listet alle verfügbaren Audio-Geräte und deren Anbieter im Log auf.

## Log-Dateien löschen

Sie können die Log-Dateien jederzeit gefahrlos löschen. Sie werden beim nächsten Start neu erstellt.

**Windows:**
```batch
del photonjockey.log
del photonjockey_error.log
```

**Linux/macOS:**
```bash
rm photonjockey.log photonjockey_error.log
```

## Weitere Hilfe

- **GitHub Issues:** https://github.com/MrLongNight/PhotonJockey/issues
- **Dokumentation:** Siehe `docs/` Verzeichnis im Repository
- **README:** Siehe `README.md` für allgemeine Informationen

Bei Problemen mit der Installation oder Konfiguration konsultieren Sie bitte auch:
- `docs/development/BUILD_INSTRUCTIONS.md` - Build-Anleitung für Entwickler
- `docs/TESTING_GUIDE_DE.md` - Test-Anleitung
