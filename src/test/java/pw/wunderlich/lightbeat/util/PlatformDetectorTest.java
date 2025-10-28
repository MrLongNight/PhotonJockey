package pw.wunderlich.lightbeat.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for PlatformDetector utility class.
 * These tests verify platform detection works on the current platform.
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
    void isMacOS_returnsConsistentValue() {
        // Act
        boolean result1 = PlatformDetector.isMacOS();
        boolean result2 = PlatformDetector.isMacOS();

        // Assert - should be consistent across calls
        assertEquals(result1, result2);
    }

    @Test
    void isLinux_returnsConsistentValue() {
        // Act
        boolean result1 = PlatformDetector.isLinux();
        boolean result2 = PlatformDetector.isLinux();

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
    void platformDetection_onlyOnePlatformIsTrue() {
        // Act
        boolean windows = PlatformDetector.isWindows();
        boolean macOS = PlatformDetector.isMacOS();
        boolean linux = PlatformDetector.isLinux();

        // Assert - at most one should be true
        // (Some systems might not match any, but multiple shouldn't be true)
        int trueCount = (windows ? 1 : 0) + (macOS ? 1 : 0) + (linux ? 1 : 0);
        assertTrue(trueCount <= 1, "At most one platform should be detected as true");
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

    @Test
    void isMacOS_matchesOSName() {
        // Arrange
        String osName = PlatformDetector.getOSName().toLowerCase();

        // Act
        boolean isMacOS = PlatformDetector.isMacOS();

        // Assert
        assertEquals(osName.contains("mac") || osName.contains("darwin"), isMacOS,
                "isMacOS() result should match os.name containing 'mac' or 'darwin'");
    }

    @Test
    void isLinux_matchesOSName() {
        // Arrange
        String osName = PlatformDetector.getOSName().toLowerCase();

        // Act
        boolean isLinux = PlatformDetector.isLinux();

        // Assert
        assertEquals(osName.contains("nix") || osName.contains("nux") || osName.contains("aix"), isLinux,
                "isLinux() result should match os.name containing 'nix', 'nux', or 'aix'");
    }
}
