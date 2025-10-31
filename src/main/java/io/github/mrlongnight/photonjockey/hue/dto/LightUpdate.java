package io.github.mrlongnight.photonjockey.hue.dto;

import java.util.Objects;

/**
 * Represents an update command for a single light.
 * Used for HTTP-based light control.
 */
public class LightUpdate {
    private final String lightId;
    private final XyColor color;
    private final Integer transitionTime;

    /**
     * Creates a new light update.
     *
     * @param lightId Unique identifier of the light
     * @param color Color to set
     * @param transitionTime Transition time in milliseconds (optional)
     */
    public LightUpdate(String lightId, XyColor color, Integer transitionTime) {
        if (lightId == null || lightId.isEmpty()) {
            throw new IllegalArgumentException("Light ID cannot be null or empty");
        }
        if (color == null) {
            throw new IllegalArgumentException("Color cannot be null");
        }
        if (transitionTime != null && transitionTime < 0) {
            throw new IllegalArgumentException("Transition time cannot be negative");
        }
        this.lightId = lightId;
        this.color = color;
        this.transitionTime = transitionTime;
    }

    public String getLightId() {
        return lightId;
    }

    public XyColor getColor() {
        return color;
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
        LightUpdate that = (LightUpdate) o;
        return Objects.equals(lightId, that.lightId)
                && Objects.equals(color, that.color)
                && Objects.equals(transitionTime, that.transitionTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lightId, color, transitionTime);
    }

    @Override
    public String toString() {
        return "LightUpdate{lightId='" + lightId + "', color=" + color
                + ", transitionTime=" + transitionTime + "}";
    }
}
