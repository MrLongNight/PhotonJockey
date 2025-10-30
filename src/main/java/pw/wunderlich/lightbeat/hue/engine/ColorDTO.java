package pw.wunderlich.lightbeat.hue.engine;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * Data transfer object representing a color in CIE XY color space.
 * Contains X, Y coordinates and brightness value for precise color control.
 */
public class ColorDTO {

    private final double x;
    private final double y;
    private final int brightness;

    /**
     * Create a new color DTO.
     *
     * @param x X coordinate in CIE color space (0.0-1.0)
     * @param y Y coordinate in CIE color space (0.0-1.0)
     * @param brightness brightness value (0-254)
     */
    @JsonCreator
    public ColorDTO(
            @JsonProperty("x") double x,
            @JsonProperty("y") double y,
            @JsonProperty("brightness") int brightness) {
        if (x < 0.0 || x > 1.0) {
            throw new IllegalArgumentException("X coordinate must be between 0.0 and 1.0");
        }
        if (y < 0.0 || y > 1.0) {
            throw new IllegalArgumentException("Y coordinate must be between 0.0 and 1.0");
        }
        if (brightness < 0 || brightness > 254) {
            throw new IllegalArgumentException("Brightness must be between 0 and 254");
        }
        this.x = x;
        this.y = y;
        this.brightness = brightness;
    }

    @JsonProperty("x")
    public double getX() {
        return x;
    }

    @JsonProperty("y")
    public double getY() {
        return y;
    }

    @JsonProperty("brightness")
    public int getBrightness() {
        return brightness;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ColorDTO colorDTO = (ColorDTO) o;
        return Double.compare(colorDTO.x, x) == 0
                && Double.compare(colorDTO.y, y) == 0
                && brightness == colorDTO.brightness;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, brightness);
    }

    @Override
    public String toString() {
        return "ColorDTO{"
                + "x=" + x
                + ", y=" + y
                + ", brightness=" + brightness
                + '}';
    }
}
