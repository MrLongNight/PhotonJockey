package io.github.mrlongnight.photonjockey.hue.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EntertainmentLightInfoTest {

    @Test
    void testConstructorAndGetters() {
        double[] position = {0.5, 0.3, 0.0};
        EntertainmentLightInfo light = new EntertainmentLightInfo(
                "1",
                "Living Room Light",
                "EXTENDED_COLOR_LIGHT",
                position
        );

        assertEquals("1", light.getId());
        assertEquals("Living Room Light", light.getName());
        assertEquals("EXTENDED_COLOR_LIGHT", light.getType());
        assertArrayEquals(position, light.getPosition());
    }

    @Test
    void testConstructorWithNullIdThrowsException() {
        assertThrows(NullPointerException.class, () -> {
            new EntertainmentLightInfo(null, "Light", "TYPE", new double[]{0, 0, 0});
        });
    }

    @Test
    void testConstructorWithNullNameThrowsException() {
        assertThrows(NullPointerException.class, () -> {
            new EntertainmentLightInfo("1", null, "TYPE", new double[]{0, 0, 0});
        });
    }

    @Test
    void testConstructorWithNullTypeThrowsException() {
        assertThrows(NullPointerException.class, () -> {
            new EntertainmentLightInfo("1", "Light", null, new double[]{0, 0, 0});
        });
    }

    @Test
    void testConstructorWithNullPositionUsesDefault() {
        EntertainmentLightInfo light = new EntertainmentLightInfo(
                "1",
                "Light",
                "TYPE",
                null
        );

        assertArrayEquals(new double[]{0.0, 0.0, 0.0}, light.getPosition());
    }

    @Test
    void testPositionArrayIsCloned() {
        double[] originalPosition = {1.0, 2.0, 3.0};
        EntertainmentLightInfo light = new EntertainmentLightInfo(
                "1",
                "Light",
                "TYPE",
                originalPosition
        );

        // Modify original array
        originalPosition[0] = 999.0;

        // Verify light's position is unchanged
        assertArrayEquals(new double[]{1.0, 2.0, 3.0}, light.getPosition());
    }

    @Test
    void testGetPositionReturnsClone() {
        EntertainmentLightInfo light = new EntertainmentLightInfo(
                "1",
                "Light",
                "TYPE",
                new double[]{1.0, 2.0, 3.0}
        );

        double[] position1 = light.getPosition();
        double[] position2 = light.getPosition();

        // Modify first returned array
        position1[0] = 999.0;

        // Verify second returned array is unchanged
        assertArrayEquals(new double[]{1.0, 2.0, 3.0}, position2);
    }

    @Test
    void testEqualsAndHashCode() {
        EntertainmentLightInfo light1 = new EntertainmentLightInfo(
                "1", "Light", "TYPE", new double[]{1.0, 2.0, 3.0}
        );
        EntertainmentLightInfo light2 = new EntertainmentLightInfo(
                "1", "Light", "TYPE", new double[]{1.0, 2.0, 3.0}
        );
        EntertainmentLightInfo light3 = new EntertainmentLightInfo(
                "2", "Light", "TYPE", new double[]{1.0, 2.0, 3.0}
        );

        assertEquals(light1, light2);
        assertNotEquals(light1, light3);
        assertEquals(light1.hashCode(), light2.hashCode());
    }

    @Test
    void testToString() {
        EntertainmentLightInfo light = new EntertainmentLightInfo(
                "1",
                "Living Room Light",
                "COLOR",
                new double[]{0.5, 0.3, 0.0}
        );

        String result = light.toString();
        assertTrue(result.contains("1"));
        assertTrue(result.contains("Living Room Light"));
        assertTrue(result.contains("COLOR"));
    }
}
