package io.github.mrlongnight.photonjockey.gui.frame;

import io.github.mrlongnight.photonjockey.AppTaskOrchestrator;
import io.github.mrlongnight.photonjockey.audio.BeatEventManager;
import io.github.mrlongnight.photonjockey.audio.PJAudioReader;
import io.github.mrlongnight.photonjockey.config.Config;
import io.github.mrlongnight.photonjockey.config.PJConfig;
import io.github.mrlongnight.photonjockey.hue.bridge.PJHueManager;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Field;

/**
 * Visual test program to verify that MainFrame displays correctly with banner and icons.
 * Run this with: DISPLAY=:99 java -cp build/libs/PhotonJockey-0.0.2-all.jar VisualMainFrameTest
 */
public class VisualMainFrameTest {
    public static void main(String[] args) throws Exception {
        System.out.println("Starting Visual MainFrame Test...");
        
        // Initialize dependencies
        Config config = new PJConfig();
        AppTaskOrchestrator taskOrchestrator = new AppTaskOrchestrator();
        PJAudioReader audioReader = new PJAudioReader(config, taskOrchestrator);
        BeatEventManager beatEventManager = audioReader;
        PJHueManager hueManager = new PJHueManager(config, taskOrchestrator);

        System.out.println("Creating MainFrame...");
        MainFrame mainFrame = new MainFrame(config, taskOrchestrator, audioReader, beatEventManager, hueManager, 100, 100);

        // Wait for the frame to be fully rendered
        SwingUtilities.invokeAndWait(() -> {
            try {
                Thread.sleep(2000); // Give it time to render
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        System.out.println("Verifying components...");
        
        // Verify banner label
        Field bannerLabelField = MainFrame.class.getDeclaredField("bannerLabel");
        bannerLabelField.setAccessible(true);
        JLabel bannerLabel = (JLabel) bannerLabelField.get(mainFrame);
        
        if (bannerLabel == null) {
            System.err.println("ERROR: Banner label is null!");
            System.exit(1);
        }
        
        if (bannerLabel.getIcon() == null) {
            System.err.println("ERROR: Banner icon is null!");
            System.exit(1);
        }
        
        System.out.println("✓ Banner label exists and has icon");
        System.out.println("  Icon dimensions: " + bannerLabel.getIcon().getIconWidth() + "x" + bannerLabel.getIcon().getIconHeight());
        
        // Verify window icons
        JFrame frame = mainFrame.getJFrame();
        java.util.List<Image> icons = frame.getIconImages();
        
        if (icons.isEmpty()) {
            System.err.println("ERROR: No window icons set!");
            System.exit(1);
        }
        
        System.out.println("✓ Window icons set: " + icons.size() + " icons");
        for (int i = 0; i < icons.size(); i++) {
            Image icon = icons.get(i);
            System.out.println("  Icon " + (i+1) + " dimensions: " + icon.getWidth(null) + "x" + icon.getHeight(null));
        }
        
        // Take a screenshot
        System.out.println("Taking screenshot...");
        SwingUtilities.invokeAndWait(() -> {
            try {
                Robot robot = new Robot();
                Rectangle frameBounds = frame.getBounds();
                BufferedImage screenshot = robot.createScreenCapture(frameBounds);
                File outputFile = new File("/tmp/mainframe_screenshot.png");
                ImageIO.write(screenshot, "png", outputFile);
                System.out.println("✓ Screenshot saved to: " + outputFile.getAbsolutePath());
            } catch (Exception e) {
                System.err.println("ERROR taking screenshot: " + e.getMessage());
                e.printStackTrace();
            }
        });
        
        System.out.println("\n=== Visual MainFrame Test PASSED ===");
        System.out.println("All components verified successfully!");
        
        // Cleanup
        taskOrchestrator.shutdown();
        System.exit(0);
    }
}
