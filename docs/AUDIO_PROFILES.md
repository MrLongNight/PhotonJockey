# Audio Profiles Feature

## Overview

The Audio Profiles feature allows users to customize audio analysis parameters for different music genres. Profiles are stored in `/config/audio_profiles.json` and can be loaded and saved dynamically.

## Components

### AudioProfile

Represents an audio profile with customizable parameters.

**Key Features:**
- Unique identifier (id) and display name
- Key-value parameter storage
- Type-safe parameter accessors (int, double, generic)

**Example:**
```java
AudioProfile profile = new AudioProfile("techno", "Techno");
profile.setParameter("beatSensitivity", 6);
profile.setParameter("minTimeBetweenBeats", 150);
int sensitivity = profile.getIntParameter("beatSensitivity", 5);
```

### AudioProfileManager

Manages audio profiles with JSON persistence.

**Key Features:**
- Automatic initialization with default profiles (techno, house, ambient)
- JSON-based storage in `/config/audio_profiles.json`
- CRUD operations for profiles
- Automatic directory creation
- Profile persistence across application restarts

**API:**
```java
AudioProfileManager manager = new AudioProfileManager();

// Load a profile
AudioProfile profile = manager.loadProfile("techno");

// Save a profile
AudioProfile custom = new AudioProfile("dubstep", "Dubstep");
custom.setParameter("beatSensitivity", 8);
manager.saveProfile(custom);

// List available profiles
String[] profiles = manager.getAvailableProfiles();

// Delete a profile
manager.deleteProfile("techno");

// Reload from disk
manager.reloadProfiles();
```

### SimpleJsonUtil

Internal utility for JSON serialization/deserialization without external dependencies.

## Default Profiles

### Techno
- **Beat Sensitivity:** 6 (higher = more sensitive)
- **Min Time Between Beats:** 150ms
- **Beat Threshold Multiplier:** 1.4
- **Description:** High-energy techno with fast, consistent beats

### House
- **Beat Sensitivity:** 5
- **Min Time Between Beats:** 200ms
- **Beat Threshold Multiplier:** 1.3
- **Description:** Steady house rhythm with balanced sensitivity

### Ambient
- **Beat Sensitivity:** 3 (lower = less sensitive)
- **Min Time Between Beats:** 300ms
- **Beat Threshold Multiplier:** 1.2
- **Description:** Gentle ambient music with subtle beat detection

## Configuration File

Profiles are stored in `config/audio_profiles.json`:

```json
{
  "profiles": [
    {
      "id": "techno",
      "name": "Techno",
      "parameters": {
        "beatSensitivity": 6,
        "minTimeBetweenBeats": 150,
        "beatThresholdMultiplier": 1.4,
        "description": "High-energy techno with fast, consistent beats"
      }
    }
  ]
}
```

## Usage Examples

### Creating a Custom Profile

```java
AudioProfileManager manager = new AudioProfileManager();

// Create a new profile for drum and bass
AudioProfile dnb = new AudioProfile("dnb", "Drum and Bass");
dnb.setParameter("beatSensitivity", 9);
dnb.setParameter("minTimeBetweenBeats", 100);
dnb.setParameter("beatThresholdMultiplier", 1.6);
dnb.setParameter("description", "Fast-paced drum and bass");

// Save it
manager.saveProfile(dnb);
```

### Loading and Using a Profile

```java
AudioProfileManager manager = new AudioProfileManager();

// Load a profile
AudioProfile profile = manager.loadProfile("house");

if (profile != null) {
    int sensitivity = profile.getIntParameter("beatSensitivity", 5);
    int minTime = profile.getIntParameter("minTimeBetweenBeats", 200);
    
    // Apply to beat detector
    // beatDetector.setSensitivity(sensitivity);
    // beatDetector.setMinTimeBetween(minTime);
}
```

### Listing All Profiles

```java
AudioProfileManager manager = new AudioProfileManager();

String[] profileIds = manager.getAvailableProfiles();
for (String id : profileIds) {
    AudioProfile profile = manager.loadProfile(id);
    System.out.println(profile.getName() + ": " + 
        profile.getParameter("description"));
}
```

## Demo Script

A demo script is provided to showcase the functionality:

```bash
./demo_audio_profiles.sh
```

This script demonstrates:
1. Loading default profiles
2. Creating a custom profile
3. Saving profiles to disk
4. Listing all available profiles

## Testing

Unit tests are provided for both `AudioProfile` and `AudioProfileManager`:

- `AudioProfileTest`: Tests profile creation, parameter storage, and equality
- `AudioProfileManagerTest`: Tests profile management, persistence, and CRUD operations

To run tests manually (when dependencies are available):
```bash
./gradlew test --tests "*AudioProfile*"
```

## Integration

To integrate audio profiles with existing beat detection:

1. Load a profile when the user selects a genre
2. Extract parameters from the profile
3. Apply parameters to the beat detector

```java
// Example integration
AudioProfileManager profileManager = new AudioProfileManager();
AudioProfile profile = profileManager.loadProfile(selectedGenre);

if (profile != null) {
    beatDetector.setSensitivity(
        profile.getIntParameter("beatSensitivity", 5)
    );
    beatDetector.setMinTimeBetweenBeats(
        profile.getIntParameter("minTimeBetweenBeats", 200)
    );
}
```

## Future Enhancements

Possible future improvements:
- UI for profile management
- Import/export profiles
- Profile validation
- More genre presets
- Machine learning-based profile optimization
- Profile sharing between users
