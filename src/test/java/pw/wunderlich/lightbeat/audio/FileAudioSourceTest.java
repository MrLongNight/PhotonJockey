package pw.wunderlich.lightbeat.audio;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileAudioSourceTest {

    private static final String TEST_FILE_PATH = "/path/to/test/audio.wav";
    private FileAudioSource audioSource;

    @BeforeEach
    void setUp() {
        audioSource = new FileAudioSource(TEST_FILE_PATH);
    }

    @Test
    void testConstructor() {
        assertEquals(TEST_FILE_PATH, audioSource.getFilePath());
        assertFalse(audioSource.isRunning());
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
    void testStartWithEmptyPath() {
        FileAudioSource emptySource = new FileAudioSource("");
        assertThrows(AudioException.class, emptySource::start);
    }

    @Test
    void testStartWithNullPath() {
        FileAudioSource nullSource = new FileAudioSource(null);
        assertThrows(AudioException.class, nullSource::start);
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
