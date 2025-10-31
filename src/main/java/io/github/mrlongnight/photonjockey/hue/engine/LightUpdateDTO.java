package io.github.mrlongnight.photonjockey.hue.engine;

import java.util.Objects;

/**
 * Data transfer object representing a light update.
 * Contains the state information for updating a single light.
 */
public class LightUpdateDTO {

    private final String lightId;
    private final Integer brightness;
    private final Double hue;
    private final Double saturation;
    private final Integer transitionTime;

    /**
     * Create a new light update DTO.
     *
     * @param lightId ID of the light to update
     * @param brightness brightness value (0-254), or null if not changing
     * @param hue hue value (0.0-1.0), or null if not changing
     * @param saturation saturation value (0.0-1.0), or null if not changing
     * @param transitionTime transition time in 100ms steps, or null for instant
     */
    public LightUpdateDTO(String lightId, Integer brightness, Double hue,
                          Double saturation, Integer transitionTime) {
        this.lightId = Objects.requireNonNull(lightId, "Light ID cannot be null");
        this.brightness = brightness;
        this.hue = hue;
        this.saturation = saturation;
        this.transitionTime = transitionTime;
    }

    public String getLightId() {
        return lightId;
    }

    public Integer getBrightness() {
        return brightness;
    }

    public Double getHue() {
        return hue;
    }

    public Double getSaturation() {
        return saturation;
    }

    public Integer getTransitionTime() {
        return transitionTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LightUpdateDTO that = (LightUpdateDTO) o;
        return Objects.equals(lightId, that.lightId)
                && Objects.equals(brightness, that.brightness)
                && Objects.equals(hue, that.hue)
                && Objects.equals(saturation, that.saturation)
                && Objects.equals(transitionTime, that.transitionTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lightId, brightness, hue, saturation, transitionTime);
    }

    @Override
    public String toString() {
        return "LightUpdateDTO{"
                + "lightId='" + lightId + '\''
                + ", brightness=" + brightness
                + ", hue=" + hue
                + ", saturation=" + saturation
                + ", transitionTime=" + transitionTime
                + '}';
    }
}
