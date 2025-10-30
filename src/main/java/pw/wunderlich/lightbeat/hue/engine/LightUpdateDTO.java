package pw.wunderlich.lightbeat.hue.engine;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

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
    @JsonCreator
    public LightUpdateDTO(
            @JsonProperty("lightId") String lightId,
            @JsonProperty("brightness") Integer brightness,
            @JsonProperty("hue") Double hue,
            @JsonProperty("saturation") Double saturation,
            @JsonProperty("transitionTime") Integer transitionTime) {
        this.lightId = Objects.requireNonNull(lightId, "Light ID cannot be null");
        this.brightness = brightness;
        this.hue = hue;
        this.saturation = saturation;
        this.transitionTime = transitionTime;
    }

    @JsonProperty("lightId")
    public String getLightId() {
        return lightId;
    }

    @JsonProperty("brightness")
    public Integer getBrightness() {
        return brightness;
    }

    @JsonProperty("hue")
    public Double getHue() {
        return hue;
    }

    @JsonProperty("saturation")
    public Double getSaturation() {
        return saturation;
    }

    @JsonProperty("transitionTime")
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
