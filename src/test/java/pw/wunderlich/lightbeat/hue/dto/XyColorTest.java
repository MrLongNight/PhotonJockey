package pw.wunderlich.lightbeat.hue.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for XyColor.
 */
class XyColorTest {

    @Test
    void testValidColor() {
        XyColor color = new XyColor(0.5, 0.5, 127);
        assertEquals(0.5, color.getX(), 0.001);
        assertEquals(0.5, color.getY(), 0.001);
        assertEquals(127, color.getBrightness());
    }

    @Test
    void testBoundaryValues() {
        // Test minimum values
        XyColor minColor = new XyColor(0.0, 0.0, 0);
        assertEquals(0.0, minColor.getX(), 0.001);
        assertEquals(0.0, minColor.getY(), 0.001);
        assertEquals(0, minColor.getBrightness());

        // Test maximum values
        XyColor maxColor = new XyColor(1.0, 1.0, 254);
        assertEquals(1.0, maxColor.getX(), 0.001);
        assertEquals(1.0, maxColor.getY(), 0.001);
        assertEquals(254, maxColor.getBrightness());
    }

    @Test
    void testInvalidXValues() {
        assertThrows(IllegalArgumentException.class, () -> new XyColor(-0.1, 0.5, 127));
        assertThrows(IllegalArgumentException.class, () -> new XyColor(1.1, 0.5, 127));
    }

    @Test
    void testInvalidYValues() {
        assertThrows(IllegalArgumentException.class, () -> new XyColor(0.5, -0.1, 127));
        assertThrows(IllegalArgumentException.class, () -> new XyColor(0.5, 1.1, 127));
    }

    @Test
    void testInvalidBrightness() {
        assertThrows(IllegalArgumentException.class, () -> new XyColor(0.5, 0.5, -1));
        assertThrows(IllegalArgumentException.class, () -> new XyColor(0.5, 0.5, 255));
    }

    @Test
    void testEquals() {
        XyColor color1 = new XyColor(0.5, 0.5, 127);
        XyColor color2 = new XyColor(0.5, 0.5, 127);
        XyColor color3 = new XyColor(0.6, 0.5, 127);

        assertEquals(color1, color2);
        assertNotEquals(color1, color3);
        assertNotEquals(color1, null);
        assertNotEquals(color1, new Object());
    }

    @Test
    void testHashCode() {
        XyColor color1 = new XyColor(0.5, 0.5, 127);
        XyColor color2 = new XyColor(0.5, 0.5, 127);

        assertEquals(color1.hashCode(), color2.hashCode());
    }

    @Test
    void testToString() {
        XyColor color = new XyColor(0.5, 0.5, 127);
        String str = color.toString();
        
        assertTrue(str.contains("XyColor"));
        assertTrue(str.contains("0.5"));
        assertTrue(str.contains("127"));
    }
}
