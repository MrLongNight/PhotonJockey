package io.github.mrlongnight.photonjockey.hue.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Root configuration object for light mapping.
 * Contains all bridges and lights configuration.
 */
public class LightMapConfig {
    private List<BridgeConfig> bridges;
    private List<LightConfig> lights;

    /**
     * Default constructor for JSON deserialization.
     */
    public LightMapConfig() {
        this.bridges = new ArrayList<>();
        this.lights = new ArrayList<>();
    }

    /**
     * Creates a new light map configuration.
     *
     * @param bridges List of bridge configurations
     * @param lights List of light configurations
     */
    public LightMapConfig(List<BridgeConfig> bridges, List<LightConfig> lights) {
        this.bridges = bridges != null ? new ArrayList<>(bridges) : new ArrayList<>();
        this.lights = lights != null ? new ArrayList<>(lights) : new ArrayList<>();
    }

    public List<BridgeConfig> getBridges() {
        return bridges;
    }

    public void setBridges(List<BridgeConfig> bridges) {
        this.bridges = bridges;
    }

    public List<LightConfig> getLights() {
        return lights;
    }

    public void setLights(List<LightConfig> lights) {
        this.lights = lights;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LightMapConfig that = (LightMapConfig) o;
        return Objects.equals(bridges, that.bridges) && Objects.equals(lights, that.lights);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bridges, lights);
    }

    @Override
    public String toString() {
        return "LightMapConfig{bridges=" + bridges + ", lights=" + lights + "}";
    }
}
