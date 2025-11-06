# Image Display Issue - Fixed

## Problem Description
Images in `src/main/resources/png/` and `src/main/resources/jpackage/` were not displaying in the application after replacing them with new PhotonJockey logos.

## Root Cause
The issue had **two main causes**:

### 1. Outdated Package References in Form Files (PRIMARY ISSUE)
The IntelliJ IDEA form files (`.form`) still referenced the old package name `pw.wunderlich.lightbeat` instead of the new package `io.github.mrlongnight.photonjockey`. This caused UI components to fail to initialize properly, preventing images from loading.

**Affected files:**
- `MainFrame.form`
- `ColorSelectionFrame.form`
- `ConnectFrame.form`

### 2. Hardcoded Dimension Mismatch (SECONDARY ISSUE)
In `MainFrame.java`, the banner dimensions were hardcoded to 482x100 pixels, but the actual banner images are 800x101 pixels. This caused the images to be scaled incorrectly.

## Changes Made

### Fixed Package References
Updated all `.form` files to use the correct package name:
```
OLD: pw.wunderlich.lightbeat
NEW: io.github.mrlongnight.photonjockey
```

### Updated Banner Dimensions
In `MainFrame.java`, line 227:
```java
// Before:
bannerLabel = new JIconLabel("/png/banner.png", "/png/bannerflash.png", 482, 100);

// After:
bannerLabel = new JIconLabel("/png/banner.png", "/png/bannerflash.png", 800, 101);
```

## Image Dimensions Reference

### Banner Images
- `banner.png`: 800 x 101 pixels ✓
- `bannerflash.png`: 800 x 101 pixels ✓
- `bannerxsmall.png`: 634 x 80 pixels (not currently used)

### Icon Images
**Note:** Icon filenames suggest specific dimensions, but actual sizes differ. This is not critical as Java's `setIconImages()` handles any size and the OS selects the appropriate one.

- `icon_16.png`: Actually 28 x 28 pixels
- `icon_32.png`: Actually 44 x 44 pixels
- `icon_48.png`: Actually 60 x 60 pixels
- `icon_64.png`: Actually 76 x 76 pixels

### Other Images
- `pushlink_image.png`: 260 x 239 pixels (not currently referenced in code)

## Building the Application

### Clean Build (Recommended after Image Changes)
To ensure no caching issues:

```bash
# Clean all build artifacts
./gradlew clean

# Build the application
./gradlew build

# Or build without tests
./gradlew build -x test
```

### Building Distribution Packages

**Note:** Building requires the custom fork of yetanotherhueapi to be installed in Maven Local first.

```bash
# Install dependency (required once)
git clone https://github.com/Kakifrucht/yetanotherhueapi.git
cd yetanotherhueapi
mvn install -DskipTests -Dmaven.javadoc.skip=true
cd ..

# Build Windows MSI
./gradlew clean jpackage -Pwindows-msi

# Build macOS DMG
./gradlew clean jpackage -Pmacos-dmg

# Build Linux DEB
./gradlew clean jpackage -Plinux-deb

# Build portable FAT JAR
./gradlew clean shadowJar
```

### Clearing Gradle Cache
If you encounter persistent caching issues:

```bash
# Remove Gradle cache
rm -rf ~/.gradle/caches/

# Clean project build directory
./gradlew clean

# Rebuild
./gradlew build
```

## Verification
After building, verify that:
1. The banner image displays correctly in the main application window
2. The application icon appears in the window title bar and taskbar
3. Images scale properly to fit the UI layout

## Additional Notes
- The `.form` files are IntelliJ IDEA GUI Designer files that are processed during build
- Package name mismatches in `.form` files can cause silent failures in UI initialization
- Always run a clean build after modifying resources to ensure they are properly packaged
- The `shadowJar` task excludes `jpackage/*` resources as they are only for installers

## Related Files Changed
- `src/main/java/io/github/mrlongnight/photonjockey/gui/frame/MainFrame.java`
- `src/main/java/io/github/mrlongnight/photonjockey/gui/frame/MainFrame.form`
- `src/main/java/io/github/mrlongnight/photonjockey/gui/frame/ColorSelectionFrame.form`
- `src/main/java/io/github/mrlongnight/photonjockey/gui/frame/ConnectFrame.form`
