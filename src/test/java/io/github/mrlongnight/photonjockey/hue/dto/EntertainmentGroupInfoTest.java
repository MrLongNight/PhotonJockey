package io.github.mrlongnight.photonjockey.hue.dto;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EntertainmentGroupInfoTest {

    @Test
    void testConstructorAndGetters() {
        EntertainmentLightInfo light1 = new EntertainmentLightInfo("1", "Light 1", "COLOR", new double[]{0.5, 0.5, 0.0});
        EntertainmentLightInfo light2 = new EntertainmentLightInfo("2", "Light 2", "COLOR", new double[]{-0.5, 0.5, 0.0});
        List<EntertainmentLightInfo> lights = Arrays.asList(light1, light2);

        EntertainmentGroupInfo group = new EntertainmentGroupInfo(
                "group1",
                "Living Room",
                lights,
                "192.168.1.100"
        );

        assertEquals("group1", group.getId());
        assertEquals("Living Room", group.getName());
        assertEquals(2, group.getLights().size());
        assertEquals("192.168.1.100", group.getBridgeIp());
    }

    @Test
    void testConstructorWithNullIdThrowsException() {
        List<EntertainmentLightInfo> lights = Collections.emptyList();
        
        assertThrows(NullPointerException.class, () -> {
            new EntertainmentGroupInfo(null, "Living Room", lights, "192.168.1.100");
        });
    }

    @Test
    void testConstructorWithNullNameThrowsException() {
        List<EntertainmentLightInfo> lights = Collections.emptyList();
        
        assertThrows(NullPointerException.class, () -> {
            new EntertainmentGroupInfo("group1", null, lights, "192.168.1.100");
        });
    }

    @Test
    void testConstructorWithNullLightsThrowsException() {
        assertThrows(NullPointerException.class, () -> {
            new EntertainmentGroupInfo("group1", "Living Room", null, "192.168.1.100");
        });
    }

    @Test
    void testConstructorWithNullBridgeIpThrowsException() {
        List<EntertainmentLightInfo> lights = Collections.emptyList();
        
        assertThrows(NullPointerException.class, () -> {
            new EntertainmentGroupInfo("group1", "Living Room", lights, null);
        });
    }

    @Test
    void testToString() {
        List<EntertainmentLightInfo> lights = Collections.singletonList(
                new EntertainmentLightInfo("1", "Light 1", "COLOR", new double[]{0.5, 0.5, 0.0})
        );
        EntertainmentGroupInfo group = new EntertainmentGroupInfo(
                "group1",
                "Living Room",
                lights,
                "192.168.1.100"
        );

        String result = group.toString();
        assertTrue(result.contains("Living Room"));
        assertTrue(result.contains("1 light"));
    }

    @Test
    void testEqualsAndHashCode() {
        List<EntertainmentLightInfo> lights = Collections.singletonList(
                new EntertainmentLightInfo("1", "Light 1", "COLOR", new double[]{0.5, 0.5, 0.0})
        );

        EntertainmentGroupInfo group1 = new EntertainmentGroupInfo(
                "group1", "Living Room", lights, "192.168.1.100"
        );
        EntertainmentGroupInfo group2 = new EntertainmentGroupInfo(
                "group1", "Living Room", lights, "192.168.1.100"
        );
        EntertainmentGroupInfo group3 = new EntertainmentGroupInfo(
                "group2", "Bedroom", lights, "192.168.1.100"
        );

        assertEquals(group1, group2);
        assertNotEquals(group1, group3);
        assertEquals(group1.hashCode(), group2.hashCode());
    }

    @Test
    void testEmptyLightsList() {
        EntertainmentGroupInfo group = new EntertainmentGroupInfo(
                "group1",
                "Empty Room",
                Collections.emptyList(),
                "192.168.1.100"
        );

        assertEquals(0, group.getLights().size());
        assertTrue(group.toString().contains("0 lights"));
    }
}
