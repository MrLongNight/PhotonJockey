package io.github.mrlongnight.photonjockey.hue.dto;

import java.util.List;
import java.util.Objects;

/**
 * Data transfer object containing information about an entertainment group.
 * Includes the group details and the lights that belong to it.
 */
public class EntertainmentGroupInfo {

    private final String id;
    private final String name;
    private final List<EntertainmentLightInfo> lights;
    private final String bridgeIp;

    /**
     * Create a new entertainment group info object.
     *
     * @param id unique identifier of the entertainment group
     * @param name human-readable name of the entertainment group
     * @param lights list of lights in this entertainment group
     * @param bridgeIp IP address of the bridge this group belongs to
     */
    public EntertainmentGroupInfo(String id, String name, List<EntertainmentLightInfo> lights, String bridgeIp) {
        this.id = Objects.requireNonNull(id, "ID cannot be null");
        this.name = Objects.requireNonNull(name, "Name cannot be null");
        this.lights = Objects.requireNonNull(lights, "Lights list cannot be null");
        this.bridgeIp = Objects.requireNonNull(bridgeIp, "Bridge IP cannot be null");
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<EntertainmentLightInfo> getLights() {
        return lights;
    }

    public String getBridgeIp() {
        return bridgeIp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EntertainmentGroupInfo that = (EntertainmentGroupInfo) o;
        return Objects.equals(id, that.id)
                && Objects.equals(name, that.name)
                && Objects.equals(lights, that.lights)
                && Objects.equals(bridgeIp, that.bridgeIp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, lights, bridgeIp);
    }

    @Override
    public String toString() {
        return name + " (" + lights.size() + " lights)";
    }
}
