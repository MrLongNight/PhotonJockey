package pw.wunderlich.lightbeat.hue.visualizer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for BrightnessData value object.
 */
class BrightnessDataTest {

    @Test
    void constructor_setsAllFields() {
        // Arrange
        double brightnessPercentage = 0.75;
        boolean doBrightnessChange = true;
        int brightnessFade = 100;
        int brightness = 200;

        // Act
        BrightnessData data = new BrightnessData(brightnessPercentage, doBrightnessChange,
                brightnessFade, brightness);

        // Assert
        assertAll("brightnessData",
                () -> assertEquals(brightnessPercentage, data.getBrightnessPercentage()),
                () -> assertEquals(doBrightnessChange, data.isBrightnessChange()),
                () -> assertEquals(brightnessFade, data.getBrightnessFade()),
                () -> assertEquals(brightness, data.getBrightness())
        );
    }

    @Test
    void getBrightnessPercentage_returnsCorrectValue() {
        // Arrange
        BrightnessData data = new BrightnessData(0.5, false, 50, 150);

        // Act & Assert
        assertEquals(0.5, data.getBrightnessPercentage());
    }

    @Test
    void isBrightnessChange_whenTrue_returnsTrue() {
        // Arrange
        BrightnessData data = new BrightnessData(0.5, true, 50, 150);

        // Act & Assert
        assertTrue(data.isBrightnessChange());
    }

    @Test
    void isBrightnessChange_whenFalse_returnsFalse() {
        // Arrange
        BrightnessData data = new BrightnessData(0.5, false, 50, 150);

        // Act & Assert
        assertFalse(data.isBrightnessChange());
    }

    @Test
    void getBrightnessFade_returnsCorrectValue() {
        // Arrange
        BrightnessData data = new BrightnessData(0.5, false, 50, 150);

        // Act & Assert
        assertEquals(50, data.getBrightnessFade());
    }

    @Test
    void getBrightness_returnsCorrectValue() {
        // Arrange
        BrightnessData data = new BrightnessData(0.5, false, 50, 150);

        // Act & Assert
        assertEquals(150, data.getBrightness());
    }

    @Test
    void constructor_withBoundaryValues_works() {
        // Arrange & Act
        BrightnessData minData = new BrightnessData(0.0, false, 0, 0);
        BrightnessData maxData = new BrightnessData(1.0, true, 254, 254);

        // Assert
        assertAll("minData",
                () -> assertEquals(0.0, minData.getBrightnessPercentage()),
                () -> assertFalse(minData.isBrightnessChange()),
                () -> assertEquals(0, minData.getBrightnessFade()),
                () -> assertEquals(0, minData.getBrightness())
        );

        assertAll("maxData",
                () -> assertEquals(1.0, maxData.getBrightnessPercentage()),
                () -> assertTrue(maxData.isBrightnessChange()),
                () -> assertEquals(254, maxData.getBrightnessFade()),
                () -> assertEquals(254, maxData.getBrightness())
        );
    }
}
