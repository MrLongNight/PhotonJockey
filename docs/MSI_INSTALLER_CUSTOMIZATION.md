# MSI Installer Customization

## Overview
This document explains how PhotonJockey customizes the Windows MSI installer appearance using WiX UI customization files.

## WiX UI Customization Files

The MSI installer uses custom branding images during the installation process:

### Banner Image
- **File**: `src/main/resources/jpackage/WixUIBannerBmp.bmp`
- **Dimensions**: 493 x 58 pixels
- **Format**: 24-bit BMP
- **Purpose**: Displays at the top of installer dialogs
- **Content**: PhotonJockey logo and branding

### Dialog Image
- **File**: `src/main/resources/jpackage/WixUIDialogBmp.bmp`
- **Dimensions**: 493 x 312 pixels
- **Format**: 24-bit BMP
- **Purpose**: Displays on the left side of installer dialogs
- **Content**: App icon and visual branding

## Configuration

The customization is configured in `build.gradle` through the jpackage configuration:

```gradle
installerOptions = [
    '--resource-dir', 'src/main/resources/jpackage',
    // ... other options
]
```

The `--resource-dir` option tells jpackage where to find WiX customization files. When jpackage finds files with these specific names in the resource directory, it automatically includes them in the MSI installer:

- `WixUIBannerBmp.bmp` - Top banner
- `WixUIDialogBmp.bmp` - Side panel image
- `icon.ico` - Application icon

## How It Works

1. During the build process, jpackage creates the Windows installer using WiX Toolset
2. The `--resource-dir` option points jpackage to `src/main/resources/jpackage/`
3. jpackage automatically detects and uses the WiX customization BMP files
4. The custom images are embedded in the generated MSI file
5. During installation, Windows Installer displays these custom images

## File Requirements

For WiX UI customization to work correctly:

### Banner (WixUIBannerBmp.bmp)
- Must be exactly 493 x 58 pixels
- Must be 24-bit or 32-bit BMP format
- Displayed at the top of most installer screens

### Dialog (WixUIDialogBmp.bmp)
- Must be exactly 493 x 312 pixels
- Must be 24-bit or 32-bit BMP format
- Displayed on the left side of most installer screens

### Naming Convention
- File names are case-sensitive and must match exactly
- Files must be in the directory specified by `--resource-dir`

## Testing

To verify the customization works:

1. Build the Windows installer on a Windows machine:
   ```bash
   gradlew.bat clean jpackage -Pwindows-msi
   ```

2. Run the generated MSI installer:
   ```
   build/jpackage/PhotonJockey-<version>.msi
   ```

3. Verify during installation:
   - Top banner shows PhotonJockey branding
   - Left panel shows app icon and branding
   - Installation dialogs display custom images

## Troubleshooting

### Images Not Showing
- Verify BMP files are in `src/main/resources/jpackage/`
- Check file names match exactly (case-sensitive)
- Verify image dimensions are correct
- Ensure `--resource-dir` option is in `installerOptions`

### Build Errors
- Ensure WiX Toolset is installed on Windows build machine
- Check BMP file format (must be valid BMP, not PNG renamed to .bmp)
- Verify Gradle configuration syntax is correct

## References

- [jpackage Documentation](https://docs.oracle.com/en/java/javase/21/docs/specs/man/jpackage.html)
- [WiX Toolset Documentation](https://wixtoolset.org/documentation/)
- [WiX UI Customization](https://wixtoolset.org/documentation/manual/v3/wixui/wixui_customizations.html)

## History

- **2025-11-10**: Added `--resource-dir` option to enable WiX UI customization
- **2025-11-06**: Created initial WixUIBannerBmp.bmp and WixUIDialogBmp.bmp files
