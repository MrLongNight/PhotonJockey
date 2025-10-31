package io.github.mrlongnight.photonjockey.audio;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AudioProfileTest {

    @Test
    void testConstructorWithIdAndName() {
        AudioProfile profile = new AudioProfile("test-id", "Test Profile");
        assertEquals("test-id", profile.getId());
        assertEquals("Test Profile", profile.getName());
        assertNotNull(profile.getParameters());
        assertTrue(profile.getParameters().isEmpty());
    }

    @Test
    void testConstructorWithParameters() {
        Map<String, Object> params = new HashMap<>();
        params.put("param1", 100);
        params.put("param2", "value");

        AudioProfile profile = new AudioProfile("test-id", "Test Profile", params);
        assertEquals("test-id", profile.getId());
        assertEquals("Test Profile", profile.getName());
        assertEquals(2, profile.getParameters().size());
        assertEquals(100, profile.getParameter("param1"));
        assertEquals("value", profile.getParameter("param2"));
    }

    @Test
    void testConstructorWithNullId() {
        assertThrows(NullPointerException.class, () -> {
            new AudioProfile(null, "Test Profile");
        });
    }

    @Test
    void testConstructorWithNullName() {
        assertThrows(NullPointerException.class, () -> {
            new AudioProfile("test-id", null);
        });
    }

    @Test
    void testSetAndGetParameter() {
        AudioProfile profile = new AudioProfile("test-id", "Test Profile");
        profile.setParameter("testKey", "testValue");
        assertEquals("testValue", profile.getParameter("testKey"));
    }

    @Test
    void testGetNonExistentParameter() {
        AudioProfile profile = new AudioProfile("test-id", "Test Profile");
        assertNull(profile.getParameter("nonexistent"));
    }

    @Test
    void testGetIntParameter() {
        AudioProfile profile = new AudioProfile("test-id", "Test Profile");
        profile.setParameter("intValue", 42);
        profile.setParameter("doubleValue", 42.5);
        profile.setParameter("stringValue", "not a number");

        assertEquals(42, profile.getIntParameter("intValue", 0));
        assertEquals(42, profile.getIntParameter("doubleValue", 0));
        assertEquals(99, profile.getIntParameter("stringValue", 99));
        assertEquals(99, profile.getIntParameter("nonexistent", 99));
    }

    @Test
    void testGetDoubleParameter() {
        AudioProfile profile = new AudioProfile("test-id", "Test Profile");
        profile.setParameter("doubleValue", 3.14);
        profile.setParameter("intValue", 42);
        profile.setParameter("stringValue", "not a number");

        assertEquals(3.14, profile.getDoubleParameter("doubleValue", 0.0), 0.001);
        assertEquals(42.0, profile.getDoubleParameter("intValue", 0.0), 0.001);
        assertEquals(9.99, profile.getDoubleParameter("stringValue", 9.99), 0.001);
        assertEquals(9.99, profile.getDoubleParameter("nonexistent", 9.99), 0.001);
    }

    @Test
    void testGetParametersReturnsCopy() {
        AudioProfile profile = new AudioProfile("test-id", "Test Profile");
        profile.setParameter("key1", "value1");

        Map<String, Object> params = profile.getParameters();
        params.put("key2", "value2");

        // Original profile should not be affected
        assertNull(profile.getParameter("key2"));
        assertEquals(1, profile.getParameters().size());
    }

    @Test
    void testEqualsAndHashCode() {
        Map<String, Object> params1 = new HashMap<>();
        params1.put("param1", 100);

        Map<String, Object> params2 = new HashMap<>();
        params2.put("param1", 100);

        AudioProfile profile1 = new AudioProfile("test-id", "Test Profile", params1);
        AudioProfile profile2 = new AudioProfile("test-id", "Test Profile", params2);
        AudioProfile profile3 = new AudioProfile("different-id", "Test Profile", params1);

        assertEquals(profile1, profile2);
        assertEquals(profile1.hashCode(), profile2.hashCode());
        assertNotEquals(profile1, profile3);
    }

    @Test
    void testEqualsSameInstance() {
        AudioProfile profile = new AudioProfile("test-id", "Test Profile");
        assertEquals(profile, profile);
    }

    @Test
    void testEqualsNull() {
        AudioProfile profile = new AudioProfile("test-id", "Test Profile");
        assertNotEquals(null, profile);
    }

    @Test
    void testEqualsDifferentClass() {
        AudioProfile profile = new AudioProfile("test-id", "Test Profile");
        assertNotEquals("string", profile);
    }

    @Test
    void testToString() {
        AudioProfile profile = new AudioProfile("test-id", "Test Profile");
        profile.setParameter("param1", 100);

        String str = profile.toString();
        assertNotNull(str);
        assertTrue(str.contains("test-id"));
        assertTrue(str.contains("Test Profile"));
        assertTrue(str.contains("param1"));
    }

    @Test
    void testMultipleParameters() {
        AudioProfile profile = new AudioProfile("test-id", "Test Profile");
        profile.setParameter("intParam", 42);
        profile.setParameter("doubleParam", 3.14);
        profile.setParameter("stringParam", "test");
        profile.setParameter("boolParam", true);

        assertEquals(4, profile.getParameters().size());
        assertEquals(42, profile.getParameter("intParam"));
        assertEquals(3.14, profile.getParameter("doubleParam"));
        assertEquals("test", profile.getParameter("stringParam"));
        assertEquals(true, profile.getParameter("boolParam"));
    }

    @Test
    void testOverwriteParameter() {
        AudioProfile profile = new AudioProfile("test-id", "Test Profile");
        profile.setParameter("key", "value1");
        assertEquals("value1", profile.getParameter("key"));

        profile.setParameter("key", "value2");
        assertEquals("value2", profile.getParameter("key"));
        assertEquals(1, profile.getParameters().size());
    }

    @Test
    void testParameterTypes() {
        AudioProfile profile = new AudioProfile("test-id", "Test Profile");
        
        // Test Integer
        profile.setParameter("integer", Integer.valueOf(42));
        assertEquals(42, profile.getIntParameter("integer", 0));

        // Test Long
        profile.setParameter("long", Long.valueOf(1000L));
        assertEquals(1000, profile.getIntParameter("long", 0));

        // Test Float
        profile.setParameter("float", Float.valueOf(3.14f));
        assertEquals(3.14, profile.getDoubleParameter("float", 0.0), 0.01);

        // Test Double
        profile.setParameter("double", Double.valueOf(2.718));
        assertEquals(2.718, profile.getDoubleParameter("double", 0.0), 0.001);
    }

    @Test
    void testEmptyProfile() {
        AudioProfile profile = new AudioProfile("empty", "Empty Profile");
        assertTrue(profile.getParameters().isEmpty());
        assertNull(profile.getParameter("anything"));
        assertEquals(0, profile.getIntParameter("anything", 0));
        assertEquals(0.0, profile.getDoubleParameter("anything", 0.0));
    }
}
