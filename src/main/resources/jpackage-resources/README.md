# jPackage Resources for MSI Customization

This directory contains custom resources used by jPackage when building the Windows MSI installer.

## Files

### Banner Images
- **WixUIBannerBmp.bmp** (493x58 pixels): Top banner displayed at the top of installer dialogs
- **WixUIDialogBmp.bmp** (493x312 pixels): Welcome and completion dialog background image

These images are automatically used by the WiX Toolset when creating the MSI installer. They are created from the main PhotonJockey banner image (`src/main/resources/png/banner.png`) and resized to meet WiX requirements.

### WiX Include File
- **overrides.wxi**: WiX XML include file that defines custom properties for the MSI installer
  - Product name and manufacturer information
  - Application description and keywords
  - Comments about upgrade behavior

## How It Works

When building the MSI installer with `./gradlew jpackage -Pwindows-msi`, jPackage uses the WiX Toolset to create the installer. The `--resource-dir` option in `build.gradle` tells jPackage to look in this directory for custom resources.

jPackage will:
1. Use custom banner images if present (WixUIBannerBmp.bmp, WixUIDialogBmp.bmp)
2. Include WiX override files (overrides.wxi) in the build
3. Apply properties defined in build.gradle (vendor, version, upgrade UUID, etc.)

## MSI Installer Features

The configured MSI installer includes:
- **Custom branding**: PhotonJockey logo and banners
- **Upgrade capability**: Can install over existing versions (via --win-upgrade-uuid)
- **Directory chooser**: Users can select installation directory (via --win-dir-chooser)
- **Start menu integration**: Creates Start Menu shortcuts (via --win-menu)
- **Desktop shortcut**: Creates desktop shortcut (via --win-shortcut)
- **Detailed metadata**: Version, copyright, description

## Regenerating Banner Images

If you need to regenerate the banner images from the source, you can use Python with PIL/Pillow:

```python
from PIL import Image

# Load source banner
img = Image.open('src/main/resources/png/banner.png')

# Create top banner (493x58)
banner_top = img.resize((493, 58), Image.Resampling.LANCZOS)
if banner_top.mode == 'RGBA':
    bg = Image.new('RGB', banner_top.size, (255, 255, 255))
    bg.paste(banner_top, mask=banner_top.split()[3])
    banner_top = bg
banner_top.save('src/main/resources/jpackage-resources/WixUIBannerBmp.bmp', 'BMP')

# Create dialog banner (493x312)
dialog_img = Image.new('RGB', (493, 312), (240, 240, 240))
banner_resized = img.resize((400, 50), Image.Resampling.LANCZOS)
if banner_resized.mode == 'RGBA':
    bg = Image.new('RGB', banner_resized.size, (240, 240, 240))
    bg.paste(banner_resized, mask=banner_resized.split()[3])
    banner_resized = bg
dialog_img.paste(banner_resized, ((493-400)//2, 130))
dialog_img.save('src/main/resources/jpackage-resources/WixUIDialogBmp.bmp', 'BMP')
```

## References
- [jPackage Documentation](https://docs.oracle.com/en/java/javase/21/docs/specs/man/jpackage.html)
- [WiX Toolset Documentation](https://wixtoolset.org/documentation/)
- [WiX UI Customization](https://wixtoolset.org/documentation/manual/v3/wixui/wixui_customizations.html)
