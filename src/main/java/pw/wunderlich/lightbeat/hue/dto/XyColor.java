package pw.wunderlich.lightbeat.hue.dto;

import java.util.Objects;

/**
 * Represents a color in XY color space with brightness.
 * Used for Philips Hue light control.
 */
public class XyColor {
    private final double x;
    private final double y;
    private final int brightness;

    /**
     * Creates a new XY color.
     *
     * @param x X coordinate in CIE color space (0.0 to 1.0)
     * @param y Y coordinate in CIE color space (0.0 to 1.0)
     * @param brightness Brightness value (0 to 254)
     */
    public XyColor(double x, double y, int brightness) {
        if (x < 0.0 || x > 1.0) {
            throw new IllegalArgumentException("X must be between 0.0 and 1.0");
        }
        if (y < 0.0 || y > 1.0) {
            throw new IllegalArgumentException("Y must be between 0.0 and 1.0");
        }
        if (brightness < 0 || brightness > 254) {
            throw new IllegalArgumentException("Brightness must be between 0 and 254");
        }
        this.x = x;
        this.y = y;
        this.brightness = brightness;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

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
        XyColor xyColor = (XyColor) o;
        return Double.compare(xyColor.x, x) == 0
                && Double.compare(xyColor.y, y) == 0
                && brightness == xyColor.brightness;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, brightness);
    }

    @Override
    public String toString() {
        return "XyColor{x=" + x + ", y=" + y + ", brightness=" + brightness + "}";
    }
}
