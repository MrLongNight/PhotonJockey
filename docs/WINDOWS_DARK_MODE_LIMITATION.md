# Windows Dark Mode Title Bar Limitation

## Issue
The PhotonJockey application window title bar does not respect the Windows dark mode setting.

## Root Cause
JavaFX uses native OS window decorations by default. While the application content can be styled with CSS, **JavaFX does not provide API access to control the Windows title bar appearance** (light/dark mode).

## Technical Details

### What We've Implemented
1. **Windows Theme Detection** (`WindowsThemeDetector.java`):
   - Detects Windows dark/light mode preference from registry
   - Logs the detected theme preference on startup
   - Can be used by application logic to adjust UI colors

2. **Application Icon**:
   - Window icon is now properly set from `/png/icon_64.png`

### Why Title Bar Dark Mode Doesn't Work

JavaFX title bars are rendered by the OS using native window decorations. The dark/light appearance is controlled by:
- Windows 10/11: DWM (Desktop Window Manager) APIs
- Requires setting window attributes like `DWMWA_USE_IMMERSIVE_DARK_MODE`

**JavaFX does not expose these APIs.** The only ways to achieve dark title bars are:

#### Option 1: Custom Window Decorations (Undecorated Window)
```java
primaryStage.initStyle(StageStyle.UNDECORATED);
// Then implement custom title bar with minimize/maximize/close buttons
```
**Drawbacks:**
- Must implement all window controls (drag, resize, minimize, maximize, close)
- Significant UI development effort
- May break window snap features
- Loses native OS integration (Alt+F4, taskbar previews, etc.)

#### Option 2: JNI Native Code
- Write native Windows code (C/C++) to call DWM APIs
- Load via JNI in Java
- **Drawbacks:**
  - Platform-specific code
  - Complex build process
  - Security concerns with native code
  - Maintenance burden

#### Option 3: Third-Party Libraries
Some experimental JavaFX libraries attempt to provide dark title bars:
- Not officially supported
- May have compatibility issues
- Often require JDK internal API access

## Current Behavior
The application now:
1. ✅ Detects Windows theme preference and logs it
2. ✅ Uses dark theme for all application content
3. ✅ Shows the PhotonJockey icon in the title bar
4. ❌ Cannot change the title bar color (OS limitation)

## Recommendation
Accept this as a JavaFX limitation. The title bar will use the default Windows appearance:
- Light mode: white title bar
- Dark mode: dark title bar (if Windows has dark mode enabled at app launch)

Users who want consistent dark appearance should:
1. Enable Windows dark mode system-wide
2. The title bar will then appear dark automatically on next app launch

## References
- [JavaFX StageStyle Documentation](https://openjfx.io/javadoc/21/javafx.graphics/javafx/stage/StageStyle.html)
- [Windows DWM Dark Mode](https://learn.microsoft.com/en-us/windows/apps/desktop/modernize/apply-windows-themes)
- [JDK-8238533: Dark title bars on Windows](https://bugs.openjdk.org/browse/JDK-8238533) - Open JDK issue since 2020
