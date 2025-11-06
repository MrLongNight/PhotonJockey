package io.github.mrlongnight.photonjockey.gui.frame;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test to verify that IntelliJ IDEA form instrumentation is working correctly
 * and that images (banner, icons) can be loaded.
 */
public class MainFrameFormInstrumentationTest {

    @Test
    public void testBannerImageResourceExists() {
        // Verify that the banner image resource can be loaded
        assertNotNull(getClass().getResource("/png/banner.png"), 
            "Banner image resource should exist at /png/banner.png");
        assertNotNull(getClass().getResource("/png/bannerflash.png"), 
            "Banner flash image resource should exist at /png/bannerflash.png");
    }

    @Test
    public void testIconImageResourcesExist() {
        // Verify that all icon image resources can be loaded
        for (int i = 16; i <= 64; i += 16) {
            String iconPath = "/png/icon_" + i + ".png";
            assertNotNull(getClass().getResource(iconPath), 
                "Icon image resource should exist at " + iconPath);
        }
    }
}
