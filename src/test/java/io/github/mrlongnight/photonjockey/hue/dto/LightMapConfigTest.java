package io.github.mrlongnight.photonjockey.hue.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for LightMapConfig and related configuration classes.
 */
class LightMapConfigTest {

    @Test
    void testEmptyConfig() {
        LightMapConfig config = new LightMapConfig();
        
        assertNotNull(config.getBridges());
        assertNotNull(config.getLights());
        assertEquals(0, config.getBridges().size());
        assertEquals(0, config.getLights().size());
    }

    @Test
    void testConfigWithData() {
        List<BridgeConfig> bridges = new ArrayList<>();
        bridges.add(new BridgeConfig("bridge-1", "192.168.1.100", "user123"));

        List<LightConfig> lights = new ArrayList<>();
        lights.add(new LightConfig("light-1", 10.0, 20.0, "bridge-1", "FAST_UDP", "Living Room"));

        LightMapConfig config = new LightMapConfig(bridges, lights);

        assertEquals(1, config.getBridges().size());
        assertEquals(1, config.getLights().size());
        assertEquals("bridge-1", config.getBridges().get(0).getId());
        assertEquals("light-1", config.getLights().get(0).getId());
    }

    @Test
    void testBridgeConfig() {
        BridgeConfig bridge = new BridgeConfig("bridge-1", "192.168.1.100", "user123");
        
        assertEquals("bridge-1", bridge.getId());
        assertEquals("192.168.1.100", bridge.getIp());
        assertEquals("user123", bridge.getUsername());
    }

    @Test
    void testBridgeConfigSetters() {
        BridgeConfig bridge = new BridgeConfig();
        bridge.setId("bridge-2");
        bridge.setIp("192.168.1.101");
        bridge.setUsername("user456");
        
        assertEquals("bridge-2", bridge.getId());
        assertEquals("192.168.1.101", bridge.getIp());
        assertEquals("user456", bridge.getUsername());
    }

    @Test
    void testLightConfig() {
        LightConfig light = new LightConfig("light-1", 10.0, 20.0, "bridge-1", "FAST_UDP",
                "Living Room");
        
        assertEquals("light-1", light.getId());
        assertEquals(10.0, light.getX(), 0.001);
        assertEquals(20.0, light.getY(), 0.001);
        assertEquals("bridge-1", light.getBridgeId());
        assertEquals("FAST_UDP", light.getControlType());
        assertEquals("Living Room", light.getName());
    }

    @Test
    void testLightConfigSetters() {
        LightConfig light = new LightConfig();
        light.setId("light-2");
        light.setX(15.0);
        light.setY(25.0);
        light.setBridgeId("bridge-2");
        light.setControlType("LOW_HTTP");
        light.setName("Bedroom");
        
        assertEquals("light-2", light.getId());
        assertEquals(15.0, light.getX(), 0.001);
        assertEquals(25.0, light.getY(), 0.001);
        assertEquals("bridge-2", light.getBridgeId());
        assertEquals("LOW_HTTP", light.getControlType());
        assertEquals("Bedroom", light.getName());
    }

    @Test
    void testConfigEquals() {
        List<BridgeConfig> bridges = new ArrayList<>();
        bridges.add(new BridgeConfig("bridge-1", "192.168.1.100", "user123"));

        List<LightConfig> lights = new ArrayList<>();
        lights.add(new LightConfig("light-1", 10.0, 20.0, "bridge-1", "FAST_UDP", "Living Room"));

        LightMapConfig config1 = new LightMapConfig(bridges, lights);
        LightMapConfig config2 = new LightMapConfig(bridges, lights);

        assertEquals(config1, config2);
    }

    @Test
    void testBridgeEquals() {
        BridgeConfig bridge1 = new BridgeConfig("bridge-1", "192.168.1.100", "user123");
        BridgeConfig bridge2 = new BridgeConfig("bridge-1", "192.168.1.100", "user123");

        assertEquals(bridge1, bridge2);
        assertEquals(bridge1.hashCode(), bridge2.hashCode());
    }

    @Test
    void testLightEquals() {
        LightConfig light1 = new LightConfig("light-1", 10.0, 20.0, "bridge-1", "FAST_UDP",
                "Living Room");
        LightConfig light2 = new LightConfig("light-1", 10.0, 20.0, "bridge-1", "FAST_UDP",
                "Living Room");

        assertEquals(light1, light2);
        assertEquals(light1.hashCode(), light2.hashCode());
    }
}
