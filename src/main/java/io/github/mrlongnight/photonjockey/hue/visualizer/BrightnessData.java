package io.github.mrlongnight.photonjockey.hue.visualizer;

/**
 * Value object containing brightness information for light updates.
 * Encapsulates both the brightness percentage and the calculated brightness values
 * for fading and direct brightness control.
 */
public class BrightnessData {

    private final double brightnessPercentage;
    private final boolean doBrightnessChange;
    private final int brightnessFade;
    private final int brightness;

    /**
     * Creates a new BrightnessData instance.
     *
     * @param brightnessPercentage     the brightness as a percentage (0.0 to 1.0)
     * @param doBrightnessChange       whether a brightness change should be applied
     * @param brightnessFade           the calculated fade brightness value
     * @param brightness               the calculated direct brightness value
     */
    public BrightnessData(double brightnessPercentage, boolean doBrightnessChange,
                          int brightnessFade, int brightness) {
        this.brightnessPercentage = brightnessPercentage;
        this.doBrightnessChange = doBrightnessChange;
        this.brightnessFade = brightnessFade;
        this.brightness = brightness;
    }

    /**
     * Gets the brightness percentage.
     *
     * @return brightness as a percentage (0.0 to 1.0)
     */
    public double getBrightnessPercentage() {
        return brightnessPercentage;
    }

    /**
     * Checks if a brightness change should be applied.
     *
     * @return true if brightness should change, false otherwise
     */
    public boolean isBrightnessChange() {
        return doBrightnessChange;
    }

    /**
     * Gets the fade brightness value.
     *
     * @return the brightness value for fade effects
     */
    public int getBrightnessFade() {
        return brightnessFade;
    }

    /**
     * Gets the direct brightness value.
     *
     * @return the brightness value for direct control
     */
    public int getBrightness() {
        return brightness;
    }
}
