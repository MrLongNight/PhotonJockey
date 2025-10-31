package io.github.mrlongnight.photonjockey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JOptionPane;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Robust launcher for PhotonJockey application that ensures all startup errors
 * are logged to a file, especially important for Windows where console output
 * is not visible.
 *
 * This launcher:
 * - Catches and logs all exceptions during startup
 * - Writes detailed error information to photonjockey_error.log
 * - Shows error dialog on Windows when startup fails
 * - Provides diagnostic information for troubleshooting
 *
 * @author PhotonJockey Team
 */
public class PhotonJockeyLauncher {

    private static final Logger logger = LoggerFactory.getLogger(PhotonJockeyLauncher.class);
    private static final String ERROR_LOG_FILE = "photonjockey_error.log";
    private static final String SEPARATOR = "================================================================================";

    public static void main(String[] args) {
        try {
            // Log startup information
            logger.info(SEPARATOR);
            logger.info("PhotonJockey Launcher starting");
            logger.info("Java Version: {}", System.getProperty("java.version"));
            logger.info("Java Vendor: {}", System.getProperty("java.vendor"));
            logger.info("OS: {} {}", System.getProperty("os.name"), System.getProperty("os.version"));
            logger.info("OS Architecture: {}", System.getProperty("os.arch"));
            logger.info("User Directory: {}", System.getProperty("user.dir"));
            logger.info("User Home: {}", System.getProperty("user.home"));
            logger.info("JavaFX Available: {}", isJavaFXAvailable());
            logger.info(SEPARATOR);

            // Check JavaFX availability
            if (!isJavaFXAvailable()) {
                String errorMsg = "JavaFX runtime components are missing. PhotonJockey requires JavaFX to run.";
                logger.error(errorMsg);
                writeErrorLog(errorMsg, null);
                showErrorDialog(
                    "JavaFX Missing",
                    errorMsg + "\n\nPlease ensure you are using a JDK with JavaFX included, " +
                    "or that JavaFX is properly installed.\n\n" +
                    "Error details have been written to: " + getErrorLogPath()
                );
                System.exit(1);
            }

            // Launch the actual application
            logger.info("Starting AudioAnalyzerDashboardDemo...");
            io.github.mrlongnight.photonjockey.ui.AudioAnalyzerDashboardDemo.main(args);
            logger.info("Application launched successfully");

        } catch (Throwable t) {
            // Catch ALL exceptions including Error subclasses
            handleFatalError(t);
        }
    }

    /**
     * Check if JavaFX is available in the classpath.
     */
    private static boolean isJavaFXAvailable() {
        try {
            Class.forName("javafx.application.Application");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    /**
     * Handle fatal errors during startup.
     */
    private static void handleFatalError(Throwable t) {
        // Log to SLF4J
        logger.error("FATAL: Application failed to start", t);

        // Write detailed error log
        String errorMessage = String.format(
            "PhotonJockey failed to start at %s\n\nError: %s\n\nStack trace:\n%s",
            LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            t.getMessage() != null ? t.getMessage() : t.getClass().getName(),
            getStackTraceAsString(t)
        );
        writeErrorLog(errorMessage, t);

        // Show error dialog
        showErrorDialog(
            "PhotonJockey Startup Failed",
            "PhotonJockey could not start due to an error:\n\n" +
            t.getClass().getSimpleName() + ": " + t.getMessage() + "\n\n" +
            "Detailed error information has been written to:\n" + getErrorLogPath() + "\n\n" +
            "Please check this log file for more information."
        );

        System.exit(1);
    }

    /**
     * Write error details to a log file in the current directory.
     */
    private static void writeErrorLog(String message, Throwable t) {
        try {
            Path logPath = Paths.get(ERROR_LOG_FILE);
            String logEntry = String.format(
                "\n%s\n%s\n%s\n%s\n",
                SEPARATOR,
                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                message,
                t != null ? getStackTraceAsString(t) : ""
            );
            
            Files.write(
                logPath,
                logEntry.getBytes(),
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
            );
            
            System.err.println("Error log written to: " + logPath.toAbsolutePath());
        } catch (Exception e) {
            System.err.println("Failed to write error log: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Get the full path to the error log file.
     */
    private static String getErrorLogPath() {
        return new File(ERROR_LOG_FILE).getAbsolutePath();
    }

    /**
     * Convert stack trace to string.
     */
    private static String getStackTraceAsString(Throwable t) {
        StringWriter sw = new StringWriter();
        try (PrintWriter pw = new PrintWriter(sw)) {
            t.printStackTrace(pw);
        }
        return sw.toString();
    }

    /**
     * Show error dialog to user (works on Windows without console).
     */
    private static void showErrorDialog(String title, String message) {
        try {
            // This will work even without JavaFX as it uses Swing
            JOptionPane.showMessageDialog(
                null,
                message,
                title,
                JOptionPane.ERROR_MESSAGE
            );
        } catch (Exception e) {
            // If even Swing dialog fails, just print to stderr
            System.err.println("ERROR: " + title);
            System.err.println(message);
        }
    }
}
