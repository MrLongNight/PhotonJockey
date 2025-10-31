package io.github.mrlongnight.photonjockey.audio;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AudioProfileManagerTest {

    @TempDir
    Path tempDir;

    private AudioProfileManager manager;
    private String configDir;

    @BeforeEach
    void setUp() {
        configDir = tempDir.toString();
        manager = new AudioProfileManager(configDir);
    }

    @AfterEach
    void tearDown() {
        // Cleanup is handled by @TempDir
    }

    @Test
    void testDefaultProfilesInitialization() {
        String[] profiles = manager.getAvailableProfiles();
        assertNotNull(profiles);
        assertEquals(3, profiles.length, "Should have 3 default profiles");

        assertTrue(manager.hasProfile("techno"));
        assertTrue(manager.hasProfile("house"));
        assertTrue(manager.hasProfile("ambient"));
    }

    @Test
    void testLoadDefaultProfiles() {
        AudioProfile techno = manager.loadProfile("techno");
        assertNotNull(techno);
        assertEquals("techno", techno.getId());
        assertEquals("Techno", techno.getName());
        assertEquals(6, techno.getIntParameter("beatSensitivity", 0));
        assertEquals(150, techno.getIntParameter("minTimeBetweenBeats", 0));

        AudioProfile house = manager.loadProfile("house");
        assertNotNull(house);
        assertEquals("house", house.getId());
        assertEquals("House", house.getName());
        assertEquals(5, house.getIntParameter("beatSensitivity", 0));

        AudioProfile ambient = manager.loadProfile("ambient");
        assertNotNull(ambient);
        assertEquals("ambient", ambient.getId());
        assertEquals("Ambient", ambient.getName());
        assertEquals(3, ambient.getIntParameter("beatSensitivity", 0));
    }

    @Test
    void testLoadNonExistentProfile() {
        AudioProfile profile = manager.loadProfile("nonexistent");
        assertNull(profile);
    }

    @Test
    void testLoadNullProfile() {
        AudioProfile profile = manager.loadProfile(null);
        assertNull(profile);
    }

    @Test
    void testLoadEmptyProfile() {
        AudioProfile profile = manager.loadProfile("");
        assertNull(profile);
    }

    @Test
    void testSaveNewProfile() {
        AudioProfile custom = new AudioProfile("custom", "Custom Profile");
        custom.setParameter("beatSensitivity", 7);
        custom.setParameter("minTimeBetweenBeats", 100);

        boolean saved = manager.saveProfile(custom);
        assertTrue(saved);

        // Verify it can be loaded
        AudioProfile loaded = manager.loadProfile("custom");
        assertNotNull(loaded);
        assertEquals("custom", loaded.getId());
        assertEquals("Custom Profile", loaded.getName());
        assertEquals(7, loaded.getIntParameter("beatSensitivity", 0));
        assertEquals(100, loaded.getIntParameter("minTimeBetweenBeats", 0));
    }

    @Test
    void testSaveNullProfile() {
        boolean saved = manager.saveProfile(null);
        assertFalse(saved);
    }

    @Test
    void testSaveProfileWithNullId() {
        AudioProfile profile = new AudioProfile("", "Empty ID");
        boolean saved = manager.saveProfile(profile);
        assertFalse(saved);
    }

    @Test
    void testUpdateExistingProfile() {
        // Load an existing profile
        AudioProfile techno = manager.loadProfile("techno");
        assertNotNull(techno);

        // Modify it
        AudioProfile modified = new AudioProfile("techno", "Techno Modified");
        modified.setParameter("beatSensitivity", 10);
        modified.setParameter("newParam", "newValue");

        boolean saved = manager.saveProfile(modified);
        assertTrue(saved);

        // Reload and verify changes
        AudioProfile reloaded = manager.loadProfile("techno");
        assertNotNull(reloaded);
        assertEquals("Techno Modified", reloaded.getName());
        assertEquals(10, reloaded.getIntParameter("beatSensitivity", 0));
        assertEquals("newValue", reloaded.getParameter("newParam"));
    }

    @Test
    void testPersistenceAcrossInstances() {
        // Create a custom profile in first manager
        AudioProfile custom = new AudioProfile("persistent", "Persistent Profile");
        custom.setParameter("testParam", 42);
        manager.saveProfile(custom);

        // Create a new manager instance pointing to the same config
        AudioProfileManager newManager = new AudioProfileManager(configDir);

        // Verify the profile persisted
        AudioProfile loaded = newManager.loadProfile("persistent");
        assertNotNull(loaded);
        assertEquals("persistent", loaded.getId());
        assertEquals("Persistent Profile", loaded.getName());
        assertEquals(42, loaded.getIntParameter("testParam", 0));
    }

    @Test
    void testDeleteProfile() {
        // Verify profile exists
        assertTrue(manager.hasProfile("techno"));

        // Delete it
        boolean deleted = manager.deleteProfile("techno");
        assertTrue(deleted);

        // Verify it's gone
        assertFalse(manager.hasProfile("techno"));
        assertNull(manager.loadProfile("techno"));
    }

    @Test
    void testDeleteNonExistentProfile() {
        boolean deleted = manager.deleteProfile("nonexistent");
        assertFalse(deleted);
    }

    @Test
    void testHasProfile() {
        assertTrue(manager.hasProfile("techno"));
        assertTrue(manager.hasProfile("house"));
        assertTrue(manager.hasProfile("ambient"));
        assertFalse(manager.hasProfile("nonexistent"));
    }

    @Test
    void testGetAvailableProfiles() {
        String[] profiles = manager.getAvailableProfiles();
        assertNotNull(profiles);
        assertEquals(3, profiles.length);

        // Add a new profile
        AudioProfile custom = new AudioProfile("custom", "Custom");
        manager.saveProfile(custom);

        profiles = manager.getAvailableProfiles();
        assertEquals(4, profiles.length);
    }

    @Test
    void testReloadProfiles() throws Exception {
        // Access internal profiles map via reflection to modify it
        java.lang.reflect.Field field = AudioProfileManager.class.getDeclaredField("profiles");
        field.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<String, AudioProfile> profiles = (Map<String, AudioProfile>) field.get(manager);
        
        // Modify the profiles in memory
        AudioProfile custom = new AudioProfile("memory", "Memory Only");
        profiles.put("memory", custom);

        // Reload from file (should not include the memory-only profile)
        boolean reloaded = manager.reloadProfiles();
        assertTrue(reloaded);

        // Memory profile should not exist after reload
        assertFalse(manager.hasProfile("memory"));

        // Default profiles should still exist
        assertTrue(manager.hasProfile("techno"));
        assertTrue(manager.hasProfile("house"));
        assertTrue(manager.hasProfile("ambient"));
    }

    @Test
    void testConfigFileCreation() {
        File configFile = manager.getConfigFile();
        assertNotNull(configFile);
        assertTrue(configFile.exists());
        assertTrue(configFile.getName().equals("audio_profiles.json"));
    }

    @Test
    void testProfileWithVariousParameterTypes() {
        AudioProfile profile = new AudioProfile("mixed", "Mixed Types");
        profile.setParameter("intParam", 42);
        profile.setParameter("doubleParam", 3.14);
        profile.setParameter("stringParam", "test");
        profile.setParameter("boolParam", true);

        manager.saveProfile(profile);

        AudioProfile loaded = manager.loadProfile("mixed");
        assertNotNull(loaded);
        assertEquals(42, loaded.getIntParameter("intParam", 0));
        assertEquals(3.14, loaded.getDoubleParameter("doubleParam", 0.0), 0.01);
        assertEquals("test", loaded.getParameter("stringParam"));
        assertEquals(true, loaded.getParameter("boolParam"));
    }

    @Test
    void testEmptyConfigDirectory() {
        // Create a manager with a fresh temp directory
        Path newTempDir = tempDir.resolve("new_config");
        AudioProfileManager newManager = new AudioProfileManager(newTempDir.toString());

        // Should still have default profiles
        assertTrue(newManager.hasProfile("techno"));
        assertTrue(newManager.hasProfile("house"));
        assertTrue(newManager.hasProfile("ambient"));
    }

    @Test
    void testProfileEquality() {
        AudioProfile profile1 = new AudioProfile("test", "Test");
        profile1.setParameter("param1", 100);

        AudioProfile profile2 = new AudioProfile("test", "Test");
        profile2.setParameter("param1", 100);

        assertEquals(profile1, profile2);
        assertEquals(profile1.hashCode(), profile2.hashCode());
    }

    @Test
    void testProfileToString() {
        AudioProfile profile = manager.loadProfile("techno");
        assertNotNull(profile);
        
        String str = profile.toString();
        assertNotNull(str);
        assertTrue(str.contains("techno"));
        assertTrue(str.contains("Techno"));
    }

    @Test
    void testMultipleSaves() {
        // Test that multiple consecutive saves work correctly
        for (int i = 0; i < 5; i++) {
            AudioProfile profile = new AudioProfile("test" + i, "Test " + i);
            profile.setParameter("iteration", i);
            assertTrue(manager.saveProfile(profile));
        }

        // Verify all profiles were saved
        for (int i = 0; i < 5; i++) {
            AudioProfile loaded = manager.loadProfile("test" + i);
            assertNotNull(loaded);
            assertEquals(i, loaded.getIntParameter("iteration", -1));
        }
    }

    @Test
    void testReloadNonExistentFile() throws IOException {
        // Delete the config file
        File configFile = manager.getConfigFile();
        Files.deleteIfExists(configFile.toPath());

        // Reload should fail gracefully
        boolean reloaded = manager.reloadProfiles();
        assertFalse(reloaded);
    }

    // Helper method to access private field for testing
    private Map<String, AudioProfile> getProfilesMap() {
        try {
            java.lang.reflect.Field field = AudioProfileManager.class.getDeclaredField("profiles");
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            Map<String, AudioProfile> profiles = (Map<String, AudioProfile>) field.get(manager);
            return profiles;
        } catch (Exception e) {
            fail("Failed to access profiles field: " + e.getMessage());
            return null;
        }
    }
}
