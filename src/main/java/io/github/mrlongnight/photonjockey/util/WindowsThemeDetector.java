package io.github.mrlongnight.photonjockey.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Utility class for detecting Windows theme preferences (Light/Dark mode).
 * Note: JavaFX does not natively support Windows dark mode title bars.
 * This class can detect the system preference but cannot apply it to JavaFX window decorations.
 */
public final class WindowsThemeDetector {

    private static final Logger logger = LoggerFactory.getLogger(WindowsThemeDetector.class);

    private WindowsThemeDetector() {
        // Utility class, prevent instantiation
    }

    /**
     * Detects if Windows is using dark mode.
     * Checks the Windows registry for the AppsUseLightTheme setting.
     *
     * @return true if dark mode is enabled, false if light mode or unable to detect
     */
    public static boolean isDarkModeEnabled() {
        if (!PlatformDetector.isWindows()) {
            logger.debug("Not running on Windows, dark mode detection not applicable");
            return false;
        }

        try {
            // Query Windows registry for theme setting
            // HKEY_CURRENT_USER\Software\Microsoft\Windows\CurrentVersion\Themes\Personalize
            // Value: AppsUseLightTheme (0 = Dark, 1 = Light)
            ProcessBuilder processBuilder = new ProcessBuilder(
                "reg", "query",
                "HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Themes\\Personalize",
                "/v", "AppsUseLightTheme"
            );
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("AppsUseLightTheme")) {
                    // Extract the value (0x0 = dark, 0x1 = light)
                    if (line.contains("0x0")) {
                        logger.info("Windows dark mode detected");
                        return true;
                    } else if (line.contains("0x1")) {
                        logger.info("Windows light mode detected");
                        return false;
                    }
                }
            }
            
            process.waitFor();
        } catch (Exception e) {
            logger.warn("Failed to detect Windows theme preference", e);
        }

        // Default to light mode if unable to detect
        return false;
    }

    /**
     * Gets a descriptive string of the current Windows theme.
     *
     * @return "Dark" or "Light" based on Windows settings
     */
    public static String getThemeDescription() {
        return isDarkModeEnabled() ? "Dark" : "Light";
    }
}
