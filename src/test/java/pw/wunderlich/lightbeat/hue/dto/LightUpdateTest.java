package pw.wunderlich.lightbeat.hue.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for LightUpdate.
 */
class LightUpdateTest {

    @Test
    void testValidLightUpdate() {
        XyColor color = new XyColor(0.5, 0.5, 127);
        LightUpdate update = new LightUpdate("light-1", color, 400);

        assertEquals("light-1", update.getLightId());
        assertEquals(color, update.getColor());
        assertEquals(400, update.getTransitionTime());
    }

    @Test
    void testLightUpdateWithoutTransitionTime() {
        XyColor color = new XyColor(0.5, 0.5, 127);
        LightUpdate update = new LightUpdate("light-1", color, null);

        assertEquals("light-1", update.getLightId());
        assertEquals(color, update.getColor());
        assertNull(update.getTransitionTime());
    }

    @Test
    void testInvalidLightId() {
        XyColor color = new XyColor(0.5, 0.5, 127);
        assertThrows(IllegalArgumentException.class, () -> new LightUpdate(null, color, 400));
        assertThrows(IllegalArgumentException.class, () -> new LightUpdate("", color, 400));
    }

    @Test
    void testNullColor() {
        assertThrows(IllegalArgumentException.class, () -> new LightUpdate("light-1", null, 400));
    }

    @Test
    void testInvalidTransitionTime() {
        XyColor color = new XyColor(0.5, 0.5, 127);
        assertThrows(IllegalArgumentException.class, () -> new LightUpdate("light-1", color, -1));
    }

    @Test
    void testEquals() {
        XyColor color = new XyColor(0.5, 0.5, 127);
        LightUpdate update1 = new LightUpdate("light-1", color, 400);
        LightUpdate update2 = new LightUpdate("light-1", color, 400);
        LightUpdate update3 = new LightUpdate("light-2", color, 400);

        assertEquals(update1, update2);
        assertNotEquals(update1, update3);
        assertNotEquals(update1, null);
        assertNotEquals(update1, new Object());
    }

    @Test
    void testHashCode() {
        XyColor color = new XyColor(0.5, 0.5, 127);
        LightUpdate update1 = new LightUpdate("light-1", color, 400);
        LightUpdate update2 = new LightUpdate("light-1", color, 400);

        assertEquals(update1.hashCode(), update2.hashCode());
    }

    @Test
    void testToString() {
        XyColor color = new XyColor(0.5, 0.5, 127);
        LightUpdate update = new LightUpdate("light-1", color, 400);
        String str = update.toString();

        assertTrue(str.contains("LightUpdate"));
        assertTrue(str.contains("light-1"));
        assertTrue(str.contains("400"));
    }
}
