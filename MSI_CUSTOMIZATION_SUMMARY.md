# MSI Installer Design Customization - Summary

## Changes Made

This document summarizes the changes made to customize the Windows MSI installer for PhotonJockey.

### Problem Statement (German)
The issue requested:
1. Design der Installation per MSI anpassen - Customize MSI installation design
2. Design-Tabellen ändern (Property, Icon, Name, Farbe) - Modify design tables (properties, icon, name, color)
3. MSI so anpassen dass es möglich ist über die vorhandene Installation drüber zu installieren - Enable installation over existing installation

### Solution Overview

#### 1. Created Custom MSI Resources Directory
Location: `src/main/resources/jpackage-resources/`

Files created:
- **WixUIBannerBmp.bmp** (493x58 pixels) - Top banner for installer dialogs
- **WixUIDialogBmp.bmp** (493x312 pixels) - Welcome and completion dialog background
- **overrides.wxi** - WiX XML include file with custom properties
- **README.md** - Documentation for the resource directory

The banner images were generated from the existing PhotonJockey banner (`src/main/resources/png/banner.png`) and resized to meet WiX Toolset requirements.

#### 2. Enhanced build.gradle Configuration

Added to the Windows MSI configuration:
```gradle
imageOptions = [
    '--icon', 'src/main/resources/jpackage/icon.ico',
    '--add-launcher', "${project.name}-console=src/main/resources/jpackage/debug_build.properties",
    '--resource-dir', 'src/main/resources/jpackage-resources',  // NEW
]
installerOptions = [
    '--vendor', 'Ai-LightBotEngine',
    '--app-version', project.version,                            // NEW
    '--copyright', 'Copyright © 2024 Ai-LightBotEngine',        // NEW
    '--description', 'AI-powered music visualization...',        // NEW
    '--win-menu',
    '--win-menu-group', project.name,
    '--win-shortcut',
    '--win-dir-chooser',                                        // NEW
    '--win-upgrade-uuid', '3646e8cc-645a-441b-9c4f-e119d90f8657',
]
```

**Key enhancements:**
- `--resource-dir`: Points to custom WiX resources for branding
- `--app-version`: Displays version in installer
- `--copyright`: Shows copyright notice
- `--description`: Provides detailed application description
- `--win-dir-chooser`: Allows users to select installation directory
- `--win-upgrade-uuid`: Enables installation over existing versions (was already present)

### Features Implemented

#### ✅ Custom Branding
- Custom banner images in installer dialogs
- PhotonJockey logo and branding
- Consistent visual identity throughout installation

#### ✅ Enhanced Properties
- Application name: PhotonJockey
- Vendor: Ai-LightBotEngine
- Version information
- Copyright notice
- Detailed description

#### ✅ Upgrade Capability
The `--win-upgrade-uuid` option (already present) enables:
- Installation over existing versions
- Automatic upgrade detection
- Removal of previous version during upgrade
- Preservation of user settings (if configured)

#### ✅ User-Friendly Installation
- Directory chooser dialog for custom installation path
- Start Menu integration
- Desktop shortcut creation
- Detailed progress information

### How to Build the MSI Installer

On a Windows machine with WiX Toolset installed:

```bash
# Install WiX Toolset (if not already installed)
choco install wixtoolset

# Build the MSI installer
.\gradlew.bat clean jpackage -Pwindows-msi
```

The MSI file will be generated in: `build/jpackage/PhotonJockey-<version>.msi`

### Testing the Changes

To test the MSI installer customization:

1. **Build the installer** following the steps above
2. **Run the installer** and verify:
   - Custom banners appear in installer dialogs
   - Application information is displayed correctly
   - Directory chooser allows selecting installation path
   - Installation completes successfully
3. **Test upgrade capability**:
   - Install version X
   - Build version Y (with higher version number)
   - Run installer for version Y
   - Verify it detects and upgrades the existing installation

### Technical Details

**jPackage Integration:**
- jPackage uses WiX Toolset to build MSI installers
- Custom resources are automatically detected in the resource directory
- Banner images must be in BMP format with specific dimensions
- WiX include files (.wxi) can define additional properties

**WiX Banner Requirements:**
- Top banner (WixUIBannerBmp.bmp): 493x58 pixels, BMP format
- Dialog banner (WixUIDialogBmp.bmp): 493x312 pixels, BMP format
- Must be 24-bit RGB (no alpha channel)

**Upgrade Behavior:**
- Same UpgradeCode ensures Windows recognizes upgrades
- ProductCode changes with each version (automatically handled by jPackage)
- Major upgrades remove previous version before installing new version

### Files Modified

1. `build.gradle` - Added MSI customization options
2. `src/main/resources/jpackage-resources/` - New directory with custom resources
   - WixUIBannerBmp.bmp
   - WixUIDialogBmp.bmp
   - overrides.wxi
   - README.md

### References

- [jPackage Documentation](https://docs.oracle.com/en/java/javase/21/docs/specs/man/jpackage.html)
- [WiX Toolset](https://wixtoolset.org/)
- [Windows Installer Upgrade Scenarios](https://learn.microsoft.com/en-us/windows/win32/msi/upgrade-scenarios)

### Verification Checklist

Before merging:
- [ ] Build succeeds on Windows with WiX Toolset
- [ ] MSI installer is generated successfully
- [ ] Custom banners appear in installer UI
- [ ] Application properties are displayed correctly
- [ ] Directory chooser works as expected
- [ ] Desktop shortcut is created
- [ ] Start Menu entry is created
- [ ] Upgrade from previous version works correctly
- [ ] Uninstallation works properly

### Notes

- The actual MSI build requires a Windows environment with WiX Toolset
- This cannot be fully tested in the Linux CI environment
- The changes are designed to be compatible with the existing CI/CD workflow
- Banner images are generated programmatically from existing assets
