#!/bin/bash
# Demo script for AudioProfileManager functionality

set -e

echo "=== AudioProfileManager Demo ==="
echo ""

# Setup environment
export JAVA_HOME=/usr/lib/jvm/temurin-21-jdk-amd64
export PATH=$JAVA_HOME/bin:$PATH

# Find dependencies
SLF4J_JAR=$(find ~/.gradle -name "slf4j-api-*.jar" | head -1)
if [ -z "$SLF4J_JAR" ]; then
    echo "ERROR: SLF4J not found. Please run: ./gradlew dependencies"
    exit 1
fi

# Create demo program
cat > /tmp/AudioProfileDemo.java << 'JAVA_EOF'
import pw.wunderlich.lightbeat.audio.*;

public class AudioProfileDemo {
    public static void main(String[] args) {
        System.out.println("=== AudioProfileManager Demo ===\n");
        
        // Create manager with config directory
        AudioProfileManager manager = new AudioProfileManager("config");
        
        // Display default profiles
        System.out.println("Default Profiles:");
        System.out.println("-----------------");
        for (String profileId : new String[]{"techno", "house", "ambient"}) {
            AudioProfile profile = manager.loadProfile(profileId);
            if (profile != null) {
                System.out.println("\n" + profile.getName() + " (" + profileId + "):");
                System.out.println("  Beat Sensitivity: " + profile.getIntParameter("beatSensitivity", 0));
                System.out.println("  Min Time Between Beats: " + profile.getIntParameter("minTimeBetweenBeats", 0) + "ms");
                System.out.println("  Beat Threshold Multiplier: " + profile.getDoubleParameter("beatThresholdMultiplier", 0.0));
                System.out.println("  Description: " + profile.getParameter("description"));
            }
        }
        
        // Create and save a custom profile
        System.out.println("\n\nCreating Custom Profile:");
        System.out.println("------------------------");
        AudioProfile custom = new AudioProfile("dubstep", "Dubstep");
        custom.setParameter("beatSensitivity", 8);
        custom.setParameter("minTimeBetweenBeats", 120);
        custom.setParameter("beatThresholdMultiplier", 1.5);
        custom.setParameter("description", "Heavy bass drops and wobbles");
        
        if (manager.saveProfile(custom)) {
            System.out.println("Successfully saved 'dubstep' profile");
        }
        
        // List all available profiles
        System.out.println("\n\nAll Available Profiles:");
        System.out.println("-----------------------");
        String[] allProfiles = manager.getAvailableProfiles();
        for (String id : allProfiles) {
            AudioProfile p = manager.loadProfile(id);
            System.out.println("  - " + id + ": " + p.getName());
        }
        
        System.out.println("\n\n=== Demo Complete ===");
        System.out.println("Configuration file: config/audio_profiles.json");
    }
}
JAVA_EOF

# Compile
echo "Compiling classes..."
mkdir -p /tmp/demo-compile
javac -cp "$SLF4J_JAR" -d /tmp/demo-compile \
    src/main/java/pw/wunderlich/lightbeat/audio/AudioProfile.java \
    src/main/java/pw/wunderlich/lightbeat/audio/SimpleJsonUtil.java \
    src/main/java/pw/wunderlich/lightbeat/audio/AudioProfileManager.java

javac -cp "/tmp/demo-compile:$SLF4J_JAR" -d /tmp/demo-compile /tmp/AudioProfileDemo.java

# Run
echo "Running demo..."
echo ""
java -cp "/tmp/demo-compile:$SLF4J_JAR" AudioProfileDemo 2>&1 | grep -v "SLF4J"

echo ""
echo "Demo completed. Check config/audio_profiles.json for the saved profiles."
