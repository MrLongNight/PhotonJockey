package pw.wunderlich.lightbeat.hue.engine;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ColorDTOTest {

    @Test
    void testConstructorValid() {
        ColorDTO color = new ColorDTO(0.5, 0.5, 127);
        assertEquals(0.5, color.getX());
        assertEquals(0.5, color.getY());
        assertEquals(127, color.getBrightness());
    }

    @Test
    void testConstructorMinimumValues() {
        ColorDTO color = new ColorDTO(0.0, 0.0, 0);
        assertEquals(0.0, color.getX());
        assertEquals(0.0, color.getY());
        assertEquals(0, color.getBrightness());
    }

    @Test
    void testConstructorMaximumValues() {
        ColorDTO color = new ColorDTO(1.0, 1.0, 254);
        assertEquals(1.0, color.getX());
        assertEquals(1.0, color.getY());
        assertEquals(254, color.getBrightness());
    }

    @Test
    void testConstructorInvalidXNegative() {
        assertThrows(IllegalArgumentException.class, () -> new ColorDTO(-0.1, 0.5, 127));
    }

    @Test
    void testConstructorInvalidXTooHigh() {
        assertThrows(IllegalArgumentException.class, () -> new ColorDTO(1.1, 0.5, 127));
    }

    @Test
    void testConstructorInvalidYNegative() {
        assertThrows(IllegalArgumentException.class, () -> new ColorDTO(0.5, -0.1, 127));
    }

    @Test
    void testConstructorInvalidYTooHigh() {
        assertThrows(IllegalArgumentException.class, () -> new ColorDTO(0.5, 1.1, 127));
    }

    @Test
    void testConstructorInvalidBrightnessNegative() {
        assertThrows(IllegalArgumentException.class, () -> new ColorDTO(0.5, 0.5, -1));
    }

    @Test
    void testConstructorInvalidBrightnessTooHigh() {
        assertThrows(IllegalArgumentException.class, () -> new ColorDTO(0.5, 0.5, 255));
    }

    @Test
    void testEquals() {
        ColorDTO color1 = new ColorDTO(0.5, 0.5, 127);
        ColorDTO color2 = new ColorDTO(0.5, 0.5, 127);
        ColorDTO color3 = new ColorDTO(0.6, 0.5, 127);

        assertEquals(color1, color2);
        assertNotEquals(color1, color3);
        assertNotEquals(color1, null);
        assertNotEquals(color1, "not a color");
    }

    @Test
    void testHashCode() {
        ColorDTO color1 = new ColorDTO(0.5, 0.5, 127);
        ColorDTO color2 = new ColorDTO(0.5, 0.5, 127);
        ColorDTO color3 = new ColorDTO(0.6, 0.5, 127);

        assertEquals(color1.hashCode(), color2.hashCode());
        assertNotEquals(color1.hashCode(), color3.hashCode());
    }

    @Test
    void testToString() {
        ColorDTO color = new ColorDTO(0.5, 0.5, 127);
        String str = color.toString();

        assertEquals("ColorDTO{x=0.5, y=0.5, brightness=127}", str);
    }
}
