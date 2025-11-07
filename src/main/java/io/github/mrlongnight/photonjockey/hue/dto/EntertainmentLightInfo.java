package io.github.mrlongnight.photonjockey.hue.dto;

import java.util.Objects;

/**
 * Data transfer object containing information about a light in an entertainment group.
 */
public class EntertainmentLightInfo {

    private final String id;
    private final String name;
    private final String type;
    private final double[] position; // x, y, z coordinates

    /**
     * Create a new entertainment light info object.
     *
     * @param id unique identifier of the light
     * @param name human-readable name of the light
     * @param type type/model of the light
     * @param position 3D position coordinates (x, y, z) in the entertainment area
     */
    public EntertainmentLightInfo(String id, String name, String type, double[] position) {
        this.id = Objects.requireNonNull(id, "ID cannot be null");
        this.name = Objects.requireNonNull(name, "Name cannot be null");
        this.type = Objects.requireNonNull(type, "Type cannot be null");
        this.position = position != null ? position.clone() : new double[]{0.0, 0.0, 0.0};
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public double[] getPosition() {
        return position.clone();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EntertainmentLightInfo that = (EntertainmentLightInfo) o;
        return Objects.equals(id, that.id)
                && Objects.equals(name, that.name)
                && Objects.equals(type, that.type)
                && java.util.Arrays.equals(position, that.position);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, name, type);
        result = 31 * result + java.util.Arrays.hashCode(position);
        return result;
    }

    @Override
    public String toString() {
        return "EntertainmentLightInfo{"
                + "id='" + id + '\''
                + ", name='" + name + '\''
                + ", type='" + type + '\''
                + '}';
    }
}
