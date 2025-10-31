package io.github.mrlongnight.photonjockey.gui.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for BrowserLauncher utility class.
 * Note: Actual browser launching is not tested as it would open a browser window.
 * This test verifies the method can be called without throwing exceptions.
 */
class BrowserLauncherTest {

    @Test
    void openUrl_withValidUrl_doesNotThrowException() {
        // Arrange
        String validUrl = "https://example.com";

        // Act & Assert - should not throw any exception
        assertDoesNotThrow(() -> BrowserLauncher.openUrl(validUrl));
    }

    @Test
    void openUrl_withInvalidUrl_doesNotThrowException() {
        // Arrange
        String invalidUrl = "not a valid url";

        // Act & Assert - should fail silently per specification
        assertDoesNotThrow(() -> BrowserLauncher.openUrl(invalidUrl));
    }

    @Test
    void openUrl_withNullUrl_doesNotThrowException() {
        // Arrange
        String nullUrl = null;

        // Act & Assert - should fail silently per specification
        assertDoesNotThrow(() -> BrowserLauncher.openUrl(nullUrl));
    }

    @Test
    void openUrl_withEmptyUrl_doesNotThrowException() {
        // Arrange
        String emptyUrl = "";

        // Act & Assert - should fail silently per specification
        assertDoesNotThrow(() -> BrowserLauncher.openUrl(emptyUrl));
    }
}
