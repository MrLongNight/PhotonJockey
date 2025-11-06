# MSI Installer Customization

This document describes the Windows MSI installer customization options for PhotonJockey.

## Overview

PhotonJockey uses jpackage (from the JDK) to create native Windows MSI installers. The installer is customized with branding, user-friendly features, and upgrade support.

## Customization Options

The MSI installer is configured in `build.gradle` with the following options:

### Branding and Information

- **Vendor**: `Ai-LightBotEngine`
- **Description**: `AI-powered music visualizer for Philips Hue lights. Synchronizes smart lighting effects with electronic music beats in real-time.`
- **Copyright**: `Copyright (C) 2025 Ai-LightBotEngine. Licensed under GNU GPL v3.`
- **Application Icon**: `src/main/resources/jpackage/icon.ico`

### User Features

- **`--win-menu`**: Adds application to Windows Start Menu
- **`--win-menu-group`**: Creates a Start Menu folder named "PhotonJockey"
- **`--win-shortcut`**: Creates a desktop shortcut
- **`--win-dir-chooser`**: Allows users to choose the installation directory during setup
- **`--win-per-user-install`**: Enables installation without administrator privileges

### Upgrade Support

- **`--win-upgrade-uuid`**: `3646e8cc-645a-441b-9c4f-e119d90f8657`
  - This UUID ensures that new versions can be installed over existing installations
  - The same UUID is used across all versions to enable seamless upgrades
  - When a user installs a new version, Windows will detect the existing installation and upgrade it

## Icon Resources

The application uses icons from two directories:

- **`src/main/resources/jpackage/`**: Platform-specific installer icons
  - `icon.ico` - Windows installer icon (150 KB)
  - `icon.icns` - macOS installer icon (183 KB)
  - `icon.png` - Linux installer icon (193 KB)

- **`src/main/resources/png/`**: Additional application icons and branding
  - `banner.png`, `bannerflash.png`, `bannerxsmall.png` - Application branding
  - `icon_16.png`, `icon_32.png`, `icon_48.png`, `icon_64.png` - Various icon sizes
  - `pushlink_image.png` - Hue bridge pairing image

## Building the MSI Installer

To build the MSI installer on Windows:

```bash
# Build MSI installer
./gradlew jpackage -Pwindows-msi
```

The installer will be created in:
```
build/jpackage/PhotonJockey-<version>.msi
```

## Debug Console Launcher

The installer includes a debug console launcher configured in:
```
src/main/resources/jpackage/debug_build.properties
```

This creates a secondary launcher with console output enabled for troubleshooting.

## MSI Property Tables

The jpackage tool uses the WiX Toolset to create MSI packages. The command-line options translate to Windows Installer Property Tables:

| jpackage Option | MSI Property/Feature | Description |
|-----------------|---------------------|-------------|
| `--name` | ProductName | Application name |
| `--vendor` | Manufacturer | Vendor/company name |
| `--description` | Description | Application description |
| `--copyright` | Copyright | Copyright information |
| `--app-version` | ProductVersion | Version number |
| `--icon` | ARPPRODUCTICON | Application icon |
| `--win-upgrade-uuid` | UpgradeCode | Unique identifier for upgrades |

## Testing Upgrades

To test the upgrade functionality:

1. Install version X using the MSI installer
2. Build version Y (newer version) with the same `--win-upgrade-uuid`
3. Run the version Y installer
4. Windows should detect the existing installation and upgrade it
5. Verify the application is upgraded, not duplicated

## References

- [jpackage Command Documentation](https://docs.oracle.com/en/java/javase/21/docs/specs/man/jpackage.html)
- [WiX Toolset Documentation](https://wixtoolset.org/documentation/)
- [Windows Installer Property Reference](https://docs.microsoft.com/en-us/windows/win32/msi/property-reference)
