package pw.wunderlich.lightbeat.hue.dto;

import java.util.Objects;

/**
 * Represents a light configuration with spatial and control information.
 */
public class LightConfig {
    private String id;
    private double x;
    private double y;
    private String bridgeId;
    private String controlType;
    private String name;

    /**
     * Default constructor for JSON deserialization.
     */
    public LightConfig() {
    }

    /**
     * Creates a new light configuration.
     *
     * @param id Unique identifier for the light
     * @param x X coordinate in 2D space
     * @param y Y coordinate in 2D space
     * @param bridgeId Reference to the bridge ID
     * @param controlType Control type (FAST_UDP or LOW_HTTP)
     * @param name Human-readable name (optional)
     */
    public LightConfig(String id, double x, double y, String bridgeId, String controlType,
                      String name) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.bridgeId = bridgeId;
        this.controlType = controlType;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public String getBridgeId() {
        return bridgeId;
    }

    public void setBridgeId(String bridgeId) {
        this.bridgeId = bridgeId;
    }

    public String getControlType() {
        return controlType;
    }

    public void setControlType(String controlType) {
        this.controlType = controlType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LightConfig that = (LightConfig) o;
        return Double.compare(that.x, x) == 0
                && Double.compare(that.y, y) == 0
                && Objects.equals(id, that.id)
                && Objects.equals(bridgeId, that.bridgeId)
                && Objects.equals(controlType, that.controlType)
                && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, x, y, bridgeId, controlType, name);
    }

    @Override
    public String toString() {
        return "LightConfig{id='" + id + "', x=" + x + ", y=" + y
                + ", bridgeId='" + bridgeId + "', controlType='" + controlType
                + "', name='" + name + "'}";
    }
}
