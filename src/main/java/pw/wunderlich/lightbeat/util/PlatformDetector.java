package pw.wunderlich.lightbeat.util;

/**
 * Utility class for detecting the operating system platform.
 * Extracted from WASAPIDeviceProvider to improve testability and reusability.
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
     * Checks if the current operating system is macOS.
     *
     * @return true if running on macOS, false otherwise
     */
    public static boolean isMacOS() {
        String os = System.getProperty("os.name").toLowerCase();
        return os.contains("mac") || os.contains("darwin");
    }

    /**
     * Checks if the current operating system is Linux.
     *
     * @return true if running on Linux, false otherwise
     */
    public static boolean isLinux() {
        String os = System.getProperty("os.name").toLowerCase();
        return os.contains("nix") || os.contains("nux") || os.contains("aix");
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
