package pw.wunderlich.lightbeat.gui.util;

import java.awt.Desktop;
import java.net.URI;

/**
 * Utility class for launching URLs in the system's default browser.
 * Extracted from MainFrame to improve testability and reusability.
 */
public final class BrowserLauncher {

    private BrowserLauncher() {
        // Utility class, prevent instantiation
    }

    /**
     * Opens the specified URL in the system's default browser.
     * If the desktop is not supported or browsing is not supported, the method fails silently.
     *
     * @param url the URL to open
     */
    public static void openUrl(String url) {
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(new URI(url));
            } catch (Exception ignored) {
                // Fail silently as per original behavior
            }
        }
    }
}
