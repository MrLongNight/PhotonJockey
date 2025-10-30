package pw.wunderlich.lightbeat.audio;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for FileAudioSource.
 * Note: These tests use real WAV files from test/resources/test_audio/
 */
class FileAudioSourceTest {

    private FileAudioSource audioSource;

    @AfterEach
    void tearDown() {
        if (audioSource != null && audioSource.isRunning()) {
            audioSource.stop();
        }
    }

    @Test
    void testConstructor() {
        String testPath = "/path/to/test/audio.wav";
        audioSource = new FileAudioSource(testPath);
        assertEquals(testPath, audioSource.getFilePath());
        assertFalse(audioSource.isRunning());
    }

    @Test
    void testStartAndStopWithRealFile() throws AudioException {
        String testFilePath = getTestResourcePath("test_audio/sine_440hz.wav");
        audioSource = new FileAudioSource(testFilePath);
        
        assertFalse(audioSource.isRunning());

        audioSource.start();
        assertTrue(audioSource.isRunning());
        assertNotNull(audioSource.getAudioFormat());

        audioSource.stop();
        assertFalse(audioSource.isRunning());
    }

    @Test
    void testStartWithEmptyPath() {
        audioSource = new FileAudioSource("");
        assertThrows(AudioException.class, () -> audioSource.start());
    }

    @Test
    void testStartWithNullPath() {
        audioSource = new FileAudioSource(null);
        assertThrows(AudioException.class, () -> audioSource.start());
    }

    @Test
    void testStartWithNonexistentFile() {
        audioSource = new FileAudioSource("/nonexistent/file.wav");
        assertThrows(AudioException.class, () -> audioSource.start());
    }

    @Test
    void testPollFrameBeforeStart() {
        audioSource = new FileAudioSource("/dummy/path.wav");
        assertThrows(AudioException.class, () -> audioSource.pollFrame());
    }

    @Test
    void testPollFrameAfterStart() throws AudioException {
        String testFilePath = getTestResourcePath("test_audio/sine_440hz.wav");
        audioSource = new FileAudioSource(testFilePath);
        audioSource.start();
        
        // Should be able to read at least one frame
        AudioFrame frame = audioSource.pollFrame();
        assertNotNull(frame, "Should read at least one frame from the audio file");
        assertTrue(frame.getData().length > 0, "Frame should contain data");
    }

    @Test
    void testPollFrameAfterStop() throws AudioException {
        String testFilePath = getTestResourcePath("test_audio/sine_440hz.wav");
        audioSource = new FileAudioSource(testFilePath);
        audioSource.start();
        audioSource.stop();
        
        assertThrows(AudioException.class, () -> audioSource.pollFrame());
    }

    @Test
    void testGetFrameCount() throws AudioException {
        String testFilePath = getTestResourcePath("test_audio/sine_440hz.wav");
        audioSource = new FileAudioSource(testFilePath);
        
        assertEquals(0, audioSource.getFrameCount());
        
        audioSource.start();
        audioSource.pollFrame();
        
        assertTrue(audioSource.getFrameCount() > 0);
    }

    /**
     * Helper method to get the absolute path of a test resource file.
     */
    private String getTestResourcePath(String resourcePath) {
        URL resourceUrl = getClass().getClassLoader().getResource(resourcePath);
        assertNotNull(resourceUrl, "Test resource not found: " + resourcePath);
        return new File(resourceUrl.getFile()).getAbsolutePath();
    }
}
