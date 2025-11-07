package io.github.mrlongnight.photonjockey.util;

import io.github.mrlongnight.photonjockey.config.Config;
import io.github.mrlongnight.photonjockey.config.ConfigNode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Configures logging for PhotonJockey application.
 * Handles both console and file logging with configurable log levels.
 * 
 * This class must be called BEFORE any SLF4J Logger is instantiated,
 * as SLF4J SimpleLogger reads system properties only once during initialization.
 */
public class LoggingConfigurator {
    
    private static final String DEFAULT_LOG_LEVEL = "INFO";
    private static final String DEFAULT_LOG_DIR = System.getProperty("user.home") + File.separator + "PhotonJockey";
    private static final String DEFAULT_LOG_FILENAME = "photonjockey.log";
    
    private static PrintStream fileLogStream = null;
    private static PrintStream originalErr = null;
    
    /**
     * Configures logging based on user configuration.
     * Must be called before any Logger instances are created.
     * 
     * @param config Configuration object containing log settings
     */
    public static void configure(Config config) {
        // Get log levels from config with defaults
        String consoleLevel = config.get(ConfigNode.CONSOLE_LOG_LEVEL);
        String fileLevel = config.get(ConfigNode.FILE_LOG_LEVEL);
        String logPath = config.get(ConfigNode.LOG_PATH);
        
        // Set defaults if not configured
        if (consoleLevel == null || consoleLevel.isEmpty()) {
            consoleLevel = DEFAULT_LOG_LEVEL;
            config.put(ConfigNode.CONSOLE_LOG_LEVEL, consoleLevel);
        }
        if (fileLevel == null || fileLevel.isEmpty()) {
            fileLevel = DEFAULT_LOG_LEVEL;
            config.put(ConfigNode.FILE_LOG_LEVEL, fileLevel);
        }
        if (logPath == null || logPath.isEmpty()) {
            logPath = DEFAULT_LOG_DIR + File.separator + DEFAULT_LOG_FILENAME;
            config.put(ConfigNode.LOG_PATH, logPath);
        }
        
        // Convert log level strings to lowercase for SLF4J
        consoleLevel = normalizeLogLevel(consoleLevel);
        fileLevel = normalizeLogLevel(fileLevel);
        
        // Set SLF4J Simple Logger system properties
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", consoleLevel);
        System.setProperty("org.slf4j.simpleLogger.showDateTime", "true");
        System.setProperty("org.slf4j.simpleLogger.dateTimeFormat", "yyyy-MM-dd HH:mm:ss");
        System.setProperty("org.slf4j.simpleLogger.showThreadName", "false");
        System.setProperty("org.slf4j.simpleLogger.showLogName", "true");
        System.setProperty("org.slf4j.simpleLogger.showShortLogName", "true");
        
        // Configure file logging
        configureFileLogging(logPath);
        
        System.err.println("Logging configured - Console: " + consoleLevel + ", File: " + fileLevel + ", Path: " + logPath);
    }
    
    /**
     * Normalizes log level string to lowercase for SLF4J.
     */
    private static String normalizeLogLevel(String level) {
        if (level == null || level.isEmpty()) {
            return "info";
        }
        String normalized = level.toLowerCase().trim();
        // Validate and default to info if invalid
        if (!normalized.equals("trace") && !normalized.equals("debug") && 
            !normalized.equals("info") && !normalized.equals("warn") && 
            !normalized.equals("error")) {
            return "info";
        }
        return normalized;
    }
    
    /**
     * Configures file logging by redirecting System.err to a file.
     * SLF4J Simple Logger writes to System.err by default.
     */
    private static void configureFileLogging(String logPath) {
        try {
            // Create log directory if it doesn't exist
            Path logFilePath = Paths.get(logPath);
            Path logDir = logFilePath.getParent();
            if (logDir != null && !Files.exists(logDir)) {
                Files.createDirectories(logDir);
            }
            
            // Create or append to log file
            File logFile = logFilePath.toFile();
            FileOutputStream fos = new FileOutputStream(logFile, true);
            
            // Save original System.err for potential restoration
            if (originalErr == null) {
                originalErr = System.err;
            }
            
            // Create a tee stream that writes to both file and console
            fileLogStream = new PrintStream(fos, true);
            TeeOutputStream tee = new TeeOutputStream(originalErr, fileLogStream);
            System.setErr(new PrintStream(tee, true));
            
            // Write log file header
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            fileLogStream.println("\n========================================");
            fileLogStream.println("PhotonJockey Log Session Started: " + timestamp);
            fileLogStream.println("Log File: " + logFile.getAbsolutePath());
            fileLogStream.println("========================================\n");
            fileLogStream.flush();
            
        } catch (IOException e) {
            System.err.println("Failed to configure file logging: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Closes file logging stream if open.
     */
    public static void closeFileLogging() {
        if (fileLogStream != null) {
            try {
                fileLogStream.flush();
                fileLogStream.close();
                fileLogStream = null;
                
                // Restore original System.err
                if (originalErr != null) {
                    System.setErr(originalErr);
                }
            } catch (Exception e) {
                System.err.println("Error closing file log stream: " + e.getMessage());
            }
        }
    }
    
    /**
     * TeeOutputStream writes to multiple output streams simultaneously.
     */
    private static class TeeOutputStream extends java.io.OutputStream {
        private final java.io.OutputStream out1;
        private final java.io.OutputStream out2;
        
        public TeeOutputStream(java.io.OutputStream out1, java.io.OutputStream out2) {
            this.out1 = out1;
            this.out2 = out2;
        }
        
        @Override
        public void write(int b) throws IOException {
            out1.write(b);
            out2.write(b);
        }
        
        @Override
        public void write(byte[] b) throws IOException {
            out1.write(b);
            out2.write(b);
        }
        
        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            out1.write(b, off, len);
            out2.write(b, off, len);
        }
        
        @Override
        public void flush() throws IOException {
            out1.flush();
            out2.flush();
        }
        
        @Override
        public void close() throws IOException {
            try {
                out1.close();
            } finally {
                out2.close();
            }
        }
    }
}
