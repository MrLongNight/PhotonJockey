package io.github.mrlongnight.photonjockey.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for PlatformDetector utility class.
 * These tests verify platform detection works on the current platform.
 * Note: Only Windows platform is supported by this application.
 */
class PlatformDetectorTest {

    @Test
    void isWindows_returnsConsistentValue() {
        // Act
        boolean result1 = PlatformDetector.isWindows();
        boolean result2 = PlatformDetector.isWindows();

        // Assert - should be consistent across calls
        assertEquals(result1, result2);
    }

    @Test
    void getOSName_returnsNonNullValue() {
        // Act
        String osName = PlatformDetector.getOSName();

        // Assert
        assertNotNull(osName, "OS name should not be null");
        assertFalse(osName.isEmpty(), "OS name should not be empty");
    }

    @Test
    void isWindows_matchesOSName() {
        // Arrange
        String osName = PlatformDetector.getOSName();

        // Act
        boolean isWindows = PlatformDetector.isWindows();

        // Assert
        assertEquals(osName.startsWith("Windows"), isWindows,
                "isWindows() result should match os.name starting with 'Windows'");
    }
}
