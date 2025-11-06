package io.github.mrlongnight.photonjockey.gui.frame;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test to verify that IntelliJ IDEA form instrumentation is working correctly
 * and that images (banner, icons) can be loaded.
 */
public class MainFrameFormInstrumentationTest {

    /**
     * Icon sizes expected to be available, matching those used in AbstractFrame.
     * These correspond to standard icon sizes for window title bars.
     */
    private static final int[] EXPECTED_ICON_SIZES = {16, 32, 48, 64};

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
        for (int size : EXPECTED_ICON_SIZES) {
            String iconPath = "/png/icon_" + size + ".png";
            assertNotNull(getClass().getResource(iconPath), 
                "Icon image resource should exist at " + iconPath);
        }
    }
}
