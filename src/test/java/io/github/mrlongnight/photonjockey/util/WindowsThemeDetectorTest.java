package io.github.mrlongnight.photonjockey.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import static org.junit.jupiter.api.Assertions.*;

class WindowsThemeDetectorTest {

    @Test
    @EnabledOnOs(OS.WINDOWS)
    void testIsDarkModeEnabledOnWindows() {
        // Verify the method executes without throwing an exception
        // The actual value depends on the system's current theme setting
        assertDoesNotThrow(() -> WindowsThemeDetector.isDarkModeEnabled());
    }

    @Test
    void testGetThemeDescription() {
        String theme = WindowsThemeDetector.getThemeDescription();
        assertNotNull(theme);
        assertTrue(theme.equals("Dark") || theme.equals("Light"),
                "Theme description should be either 'Dark' or 'Light', but was: " + theme);
    }

    @Test
    @EnabledOnOs({OS.LINUX, OS.MAC})
    void testIsDarkModeEnabledOnNonWindows() {
        // On non-Windows platforms, should return false
        assertFalse(WindowsThemeDetector.isDarkModeEnabled());
    }

    @Test
    @EnabledOnOs({OS.LINUX, OS.MAC})
    void testGetThemeDescriptionOnNonWindows() {
        // On non-Windows platforms, should return "Light"
        assertEquals("Light", WindowsThemeDetector.getThemeDescription());
    }
}
