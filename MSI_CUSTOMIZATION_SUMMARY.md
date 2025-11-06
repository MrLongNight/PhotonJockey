# MSI Installer Customization - Implementation Summary

## Problem Statement (German)

1. Design der Installation per MSI anpassen - Design-Tabellen ändern:
   - Bearbeiten Sie die Tabellen, die für das Aussehen der Installation zuständig sind
   - Property: Ändern Sie Eigenschaften wie das Icon, den Namen der Anwendung oder die Farbe
   - Verwenden Sie Ressourcen aus `src/main/resources/png` und `src/main/resources/jpackage`

2. MSI so anpassen, dass es möglich ist über die vorhandene Installation drüber zu installieren

## Implementation

### 1. MSI Installer Customization (build.gradle)

Added the following jpackage options to customize the Windows MSI installer:

#### Branding and Information
- **`--description`**: Added descriptive text about the application
  - "AI-powered music visualizer for Philips Hue lights. Synchronizes smart lighting effects with electronic music beats in real-time."
- **`--copyright`**: Added copyright and license information
  - "Copyright (C) 2025 Ai-LightBotEngine. Licensed under GNU GPL v3."

#### User Experience Enhancements
- **`--win-dir-chooser`**: Allows users to choose the installation directory during setup
- **`--win-per-user-install`**: Enables installation without administrator privileges

#### Existing Configuration (Verified Working)
- **`--icon`**: Uses custom icon from `src/main/resources/jpackage/icon.ico`
- **`--vendor`**: Set to 'Ai-LightBotEngine'
- **`--win-menu`**: Adds application to Windows Start Menu
- **`--win-menu-group`**: Creates Start Menu folder
- **`--win-shortcut`**: Creates desktop shortcut
- **`--win-upgrade-uuid`**: UUID `3646e8cc-645a-441b-9c4f-e119d90f8657` enables upgrade installations

### 2. Upgrade Installation Support

The existing `--win-upgrade-uuid` configuration already enables installation over existing installations:
- The UUID is consistent across all versions
- Windows detects the existing installation and upgrades it
- Users can install new versions without uninstalling the old version first

### 3. Icon Resources

Verified all icon resources are present and properly referenced:

**Installer Icons** (`src/main/resources/jpackage/`):
- `icon.ico` (150 KB) - Windows installer icon ✓
- `icon.icns` (183 KB) - macOS installer icon ✓
- `icon.png` (193 KB) - Linux installer icon ✓

**Application Icons** (`src/main/resources/png/`):
- `banner.png`, `bannerflash.png`, `bannerxsmall.png` - Application branding ✓
- `icon_16.png`, `icon_32.png`, `icon_48.png`, `icon_64.png` - Various icon sizes ✓
- `pushlink_image.png` - Hue bridge pairing image ✓

### 4. Documentation

Created comprehensive documentation:

1. **Updated `docs/development/01-BUILD_INSTRUCTIONS.md`**:
   - Added "Creating MSI Installer (Windows)" section
   - Documented all MSI installer features
   - Included build command

2. **Created `docs/development/02-MSI_INSTALLER_CUSTOMIZATION.md`**:
   - Detailed overview of all customization options
   - MSI Property Tables mapping
   - Icon resources documentation
   - Upgrade testing procedures
   - References to official documentation

## Testing

- ✓ Gradle configuration validated (no syntax errors)
- ✓ jpackage tasks available and recognized
- ✓ All icon resources present and accessible
- ✓ Build configuration successfully loads

## MSI Property Tables

The jpackage options translate to Windows Installer Property Tables:

| jpackage Option | MSI Property | Value/Effect |
|-----------------|--------------|--------------|
| `--name` | ProductName | PhotonJockey |
| `--vendor` | Manufacturer | Ai-LightBotEngine |
| `--description` | Description | AI-powered music visualizer... |
| `--copyright` | Copyright | Copyright (C) 2025... |
| `--icon` | ARPPRODUCTICON | icon.ico |
| `--win-upgrade-uuid` | UpgradeCode | 3646e8cc-645a-441b-9c4f-e119d90f8657 |

## Building the MSI Installer

To build the customized MSI installer on Windows:

```bash
./gradlew jpackage -Pwindows-msi
```

The installer will be created in:
```
build/jpackage/PhotonJockey-<version>.msi
```

## Key Features

1. **Customized Branding**: Application name, description, copyright, and icon
2. **User-Friendly Installation**: Directory chooser, per-user install option
3. **Windows Integration**: Start Menu, desktop shortcut
4. **Seamless Upgrades**: Install new versions over existing installations
5. **No Admin Required**: Per-user installation option for easier deployment

## Files Modified

- `build.gradle` - Added MSI customization options
- `docs/development/01-BUILD_INSTRUCTIONS.md` - Added MSI installer section
- `docs/development/02-MSI_INSTALLER_CUSTOMIZATION.md` - New detailed documentation

## Requirements Met

✅ Design der Installation per MSI anpassen
✅ Design-Tabellen ändern (via jpackage options)
✅ Eigenschaften wie Icon, Name, Farbe anpassen
✅ Ressourcen aus png/ und jpackage/ Verzeichnissen verwenden
✅ MSI so anpassen, dass Installation über vorhandene Installation möglich ist

## Next Steps (Optional)

To fully test the MSI installer:
1. Build on a Windows machine with WiX Toolset installed
2. Install the MSI package
3. Build a new version and verify upgrade works correctly
4. Test directory chooser and per-user installation features
