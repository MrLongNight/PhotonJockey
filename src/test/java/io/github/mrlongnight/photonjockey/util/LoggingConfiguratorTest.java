package io.github.mrlongnight.photonjockey.util;

import io.github.mrlongnight.photonjockey.config.Config;
import io.github.mrlongnight.photonjockey.config.ConfigNode;
import io.github.mrlongnight.photonjockey.config.PJConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for LoggingConfigurator.
 */
class LoggingConfiguratorTest {

    @Test
    void testConfigureWithDefaults() {
        Config config = new PJConfig();
        
        // Configure logging (this sets system properties)
        LoggingConfigurator.configure(config);
        
        // Verify defaults were set
        String consoleLevel = config.get(ConfigNode.CONSOLE_LOG_LEVEL);
        String fileLevel = config.get(ConfigNode.FILE_LOG_LEVEL);
        String logPath = config.get(ConfigNode.LOG_PATH);
        
        assertNotNull(consoleLevel, "Console log level should be set");
        assertNotNull(fileLevel, "File log level should be set");
        assertNotNull(logPath, "Log path should be set");
        
        // Verify system properties were set
        String defaultLevel = System.getProperty("org.slf4j.simpleLogger.defaultLogLevel");
        assertNotNull(defaultLevel, "SLF4J default log level should be set");
        
        String dateFormat = System.getProperty("org.slf4j.simpleLogger.dateTimeFormat");
        assertEquals("yyyy-MM-dd HH:mm:ss", dateFormat, "Date format should be set correctly");
    }
    
    @Test
    void testConfigureWithCustomSettings(@TempDir Path tempDir) {
        Config config = new PJConfig();
        
        // Set custom log settings
        Path logFile = tempDir.resolve("custom-test.log");
        config.put(ConfigNode.CONSOLE_LOG_LEVEL, "DEBUG");
        config.put(ConfigNode.FILE_LOG_LEVEL, "ERROR");
        config.put(ConfigNode.LOG_PATH, logFile.toString());
        
        // Configure logging
        LoggingConfigurator.configure(config);
        
        // Verify custom settings were applied
        assertEquals("DEBUG", config.get(ConfigNode.CONSOLE_LOG_LEVEL));
        assertEquals("ERROR", config.get(ConfigNode.FILE_LOG_LEVEL));
        assertEquals(logFile.toString(), config.get(ConfigNode.LOG_PATH));
        
        // Verify system properties
        String defaultLevel = System.getProperty("org.slf4j.simpleLogger.defaultLogLevel");
        assertEquals("debug", defaultLevel, "Log level should be normalized to lowercase");
    }
    
    @Test
    void testLogFileCreation(@TempDir Path tempDir) throws Exception {
        Config config = new PJConfig();
        
        // Set log path to temp directory
        Path logFile = tempDir.resolve("test.log");
        config.put(ConfigNode.LOG_PATH, logFile.toString());
        config.put(ConfigNode.CONSOLE_LOG_LEVEL, "INFO");
        config.put(ConfigNode.FILE_LOG_LEVEL, "INFO");
        
        // Configure logging
        LoggingConfigurator.configure(config);
        
        // Verify log file was created - it should be created immediately
        // File creation is synchronous in the configure method
        assertTrue(Files.exists(logFile), "Log file should be created at configured path");
        
        // Verify the file is writable by checking it has content (header)
        long fileSize = Files.size(logFile);
        assertTrue(fileSize > 0, "Log file should have header content");
        
        // Clean up
        LoggingConfigurator.closeFileLogging();
    }
    
    @Test
    void testInvalidLogLevelDefaultsToInfo() {
        Config config = new PJConfig();
        
        // Set invalid log levels
        config.put(ConfigNode.CONSOLE_LOG_LEVEL, "INVALID");
        config.put(ConfigNode.FILE_LOG_LEVEL, "WRONG");
        
        // Configure logging
        LoggingConfigurator.configure(config);
        
        // System property should default to info for invalid levels
        String defaultLevel = System.getProperty("org.slf4j.simpleLogger.defaultLogLevel");
        assertEquals("info", defaultLevel, "Invalid log level should default to info");
    }
}
