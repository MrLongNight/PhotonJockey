package io.github.mrlongnight.photonjockey.audio;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SystemAudioSourceTest {

    private static final String TEST_DEVICE_NAME = "Test Audio Device";
    private SystemAudioSource audioSource;

    @BeforeEach
    void setUp() {
        audioSource = new SystemAudioSource(TEST_DEVICE_NAME);
    }

    @Test
    void testConstructorWithDeviceName() {
        assertEquals(TEST_DEVICE_NAME, audioSource.getDeviceName());
        assertFalse(audioSource.isRunning());
    }

    @Test
    void testConstructorWithoutDeviceName() {
        SystemAudioSource defaultSource = new SystemAudioSource();
        assertNull(defaultSource.getDeviceName());
        assertFalse(defaultSource.isRunning());
    }

    @Test
    void testStartAndStop() throws AudioException {
        assertFalse(audioSource.isRunning());

        audioSource.start();
        assertTrue(audioSource.isRunning());

        audioSource.stop();
        assertFalse(audioSource.isRunning());
    }

    @Test
    void testSetDeviceName() {
        String newDeviceName = "New Device";
        audioSource.setDeviceName(newDeviceName);
        assertEquals(newDeviceName, audioSource.getDeviceName());
    }

    @Test
    void testPollFrameBeforeStart() {
        assertThrows(AudioException.class, () -> audioSource.pollFrame());
    }

    @Test
    void testPollFrameAfterStart() throws AudioException {
        audioSource.start();
        // Stub implementation returns null
        assertNull(audioSource.pollFrame());
    }

    @Test
    void testPollFrameAfterStop() throws AudioException {
        audioSource.start();
        audioSource.stop();
        assertThrows(AudioException.class, () -> audioSource.pollFrame());
    }
}
