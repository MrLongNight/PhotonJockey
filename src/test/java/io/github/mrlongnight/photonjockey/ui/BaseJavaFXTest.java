package io.github.mrlongnight.photonjockey.ui;

import javafx.embed.swing.JFXPanel;
import org.junit.jupiter.api.BeforeAll;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Base class for JavaFX tests that provides shared JavaFX initialization.
 * This ensures JavaFX is only initialized once across all tests and handles
 * potential initialization issues gracefully.
 */
public abstract class BaseJavaFXTest {

    private static final AtomicBoolean initialized = new AtomicBoolean(false);
    private static final AtomicBoolean initFailed = new AtomicBoolean(false);

    @BeforeAll
    public static void initJavaFX() throws Exception {
        if (initialized.get()) {
            return; // Already initialized
        }

        if (initFailed.get()) {
            throw new RuntimeException("JavaFX initialization previously failed");
        }

        // Ensure headless mode properties are set before JavaFX initialization
        // These are also set in build.gradle test configuration, but we ensure they're
        // present here as well for robustness. We only set if not already present to
        // respect any pre-configured values from build.gradle or environment.
        if (System.getProperty("testfx.robot") == null) {
            System.setProperty("testfx.robot", "glass");
        }
        if (System.getProperty("testfx.headless") == null) {
            System.setProperty("testfx.headless", "true");
        }
        if (System.getProperty("prism.order") == null) {
            System.setProperty("prism.order", "sw");
        }
        if (System.getProperty("prism.text") == null) {
            System.setProperty("prism.text", "t2k");
        }
        if (System.getProperty("java.awt.headless") == null) {
            System.setProperty("java.awt.headless", "true");
        }
        // Critical: Use monocle headless platform to avoid GTK/X11 display requirements
        if (System.getProperty("glass.platform") == null) {
            System.setProperty("glass.platform", "Monocle");
        }
        if (System.getProperty("monocle.platform") == null) {
            System.setProperty("monocle.platform", "Headless");
        }

        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean success = new AtomicBoolean(false);

        Thread initThread = new Thread(() -> {
            try {
                // Create JFXPanel to initialize JavaFX runtime in headless mode
                new JFXPanel();
                success.set(true);
            } catch (Throwable t) {
                System.err.println("JavaFX initialization error: " + t.getClass().getName() + ": " + t.getMessage());
                t.printStackTrace();
                initFailed.set(true);
            } finally {
                latch.countDown();
            }
        }, "JavaFX-Init-Thread");
        
        initThread.setDaemon(true);
        initThread.start();

        // Wait for initialization with reasonable timeout
        if (!latch.await(15, TimeUnit.SECONDS)) {
            initThread.interrupt();
            initFailed.set(true);
            throw new RuntimeException("JavaFX initialization timed out after 15 seconds. "
                    + "This may indicate missing headless display configuration.");
        }

        if (!success.get()) {
            throw new RuntimeException("JavaFX initialization failed");
        }

        initialized.set(true);
    }
}
