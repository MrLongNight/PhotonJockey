package pw.wunderlich.lightbeat.util;

/**
 * Utility class for detecting the operating system platform.
 * Extracted from WASAPIDeviceProvider to improve testability and reusability.
 * Note: Only Windows platform is supported by this application.
 */
public final class PlatformDetector {

    private PlatformDetector() {
        // Utility class, prevent instantiation
    }

    /**
     * Checks if the current operating system is Windows.
     *
     * @return true if running on Windows, false otherwise
     */
    public static boolean isWindows() {
        return System.getProperty("os.name").startsWith("Windows");
    }

    /**
     * Gets the name of the current operating system.
     *
     * @return the OS name from system properties
     */
    public static String getOSName() {
        return System.getProperty("os.name");
    }
}
