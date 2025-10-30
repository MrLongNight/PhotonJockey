package pw.wunderlich.lightbeat.audio;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for beat detection using FileAudioSource with deterministic test audio files.
 * Tests that the BeatDetector correctly identifies beats per minute (BPM) from known audio patterns.
 */
class BeatDetectionIT {

    private static final double BPM_TOLERANCE = 10.0; // Allow +/- 10 BPM tolerance
    private static final int FFT_SIZE = 2048;

    private FileAudioSource audioSource;
    private SimpleAudioAnalyzer analyzer;
    private BeatDetector detector;

    @BeforeEach
    void setUp() {
        detector = new BeatDetector();
    }

    @AfterEach
    void tearDown() {
        if (audioSource != null && audioSource.isRunning()) {
            audioSource.stop();
        }
    }

    @Test
    void testBeatDetection120BPM() throws AudioException {
        // Load the 120 BPM test file
        String testFilePath = getTestResourcePath("test_audio/beat_120bpm.wav");
        audioSource = new FileAudioSource(testFilePath);
        audioSource.start();

        // Create analyzer with the audio format from the file
        int sampleRate = (int) audioSource.getAudioFormat().getSampleRate();
        analyzer = new SimpleAudioAnalyzer(sampleRate, FFT_SIZE);

        // Process audio frames and detect beats
        int framesProcessed = 0;
        int beatsDetected = 0;
        AudioFrame frame;
        double maxEnergy = 0;
        double avgEnergy = 0;
        
        // Calculate frame duration in milliseconds
        int frameDurationMs = (1024 * 1000) / sampleRate;  // 1024 samples per frame

        while ((frame = audioSource.pollFrame()) != null && framesProcessed < 200) {
            AnalysisResult result = analyzer.analyze(frame);
            if (result != null) {
                double energy = result.getEnergy();
                maxEnergy = Math.max(maxEnergy, energy);
                avgEnergy += energy;
                if (detector.isBeat(result)) {
                    beatsDetected++;
                    System.out.printf("Beat detected at frame %d: energy=%.4f%n", framesProcessed, energy);
                }
            }
            framesProcessed++;
            
            // Sleep to simulate real-time audio processing
            try {
                Thread.sleep(frameDurationMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        avgEnergy = avgEnergy / framesProcessed;

        // Get the detected BPM
        double detectedBPM = detector.getBPM();

        System.out.printf("Test beat_120bpm.wav: BPM=%.2f, Beats=%d, Frames=%d, MaxEnergy=%.4f, AvgEnergy=%.4f%n",
                detectedBPM, beatsDetected, framesProcessed, maxEnergy, avgEnergy);

        // Assert that we detected some beats
        assertTrue(beatsDetected > 0, "Should detect at least one beat in 120 BPM audio");

        // Assert that the detected BPM is close to 120
        assertTrue(detectedBPM > 0, "BPM should be greater than 0");
        assertTrue(Math.abs(detectedBPM - 120.0) < BPM_TOLERANCE,
                String.format("Expected BPM around 120, but got %.2f (detected %d beats in %d frames)",
                        detectedBPM, beatsDetected, framesProcessed));

        System.out.printf("Test beat_120bpm.wav: Detected BPM=%.2f, Beats=%d, Frames=%d%n",
                detectedBPM, beatsDetected, framesProcessed);
    }

    @Test
    void testSineWaveNoBeat() throws AudioException {
        // Load the 440 Hz sine wave test file
        String testFilePath = getTestResourcePath("test_audio/sine_440hz.wav");
        audioSource = new FileAudioSource(testFilePath);
        audioSource.start();

        // Create analyzer with the audio format from the file
        int sampleRate = (int) audioSource.getAudioFormat().getSampleRate();
        analyzer = new SimpleAudioAnalyzer(sampleRate, FFT_SIZE);

        // Process audio frames
        int framesProcessed = 0;
        int beatsDetected = 0;
        AudioFrame frame;

        while ((frame = audioSource.pollFrame()) != null && framesProcessed < 100) {
            AnalysisResult result = analyzer.analyze(frame);
            if (result != null && detector.isBeat(result)) {
                beatsDetected++;
            }
            framesProcessed++;
        }

        // A pure sine wave should not produce many beats (might get 1-2 false positives)
        assertTrue(beatsDetected <= 2,
                String.format("Pure sine wave should not produce many beats, but got %d in %d frames",
                        beatsDetected, framesProcessed));

        System.out.printf("Test sine_440hz.wav: Beats=%d, Frames=%d (expected: few or no beats)%n",
                beatsDetected, framesProcessed);
    }

    @Test
    void testLongMixProcessing() throws AudioException {
        // Load the long mix test file
        String testFilePath = getTestResourcePath("test_audio/long_mix.wav");
        audioSource = new FileAudioSource(testFilePath);
        audioSource.start();

        // Create analyzer with the audio format from the file
        int sampleRate = (int) audioSource.getAudioFormat().getSampleRate();
        analyzer = new SimpleAudioAnalyzer(sampleRate, FFT_SIZE);

        // Process audio frames
        int framesProcessed = 0;
        int beatsDetected = 0;
        AudioFrame frame;

        while ((frame = audioSource.pollFrame()) != null && framesProcessed < 300) {
            AnalysisResult result = analyzer.analyze(frame);
            if (result != null && detector.isBeat(result)) {
                beatsDetected++;
            }
            framesProcessed++;
        }

        // The long mix should produce some beats (it has two beat sections)
        assertTrue(beatsDetected > 0, "Long mix should produce at least some beats");
        assertTrue(framesProcessed > 100, "Should process a reasonable number of frames");

        double detectedBPM = detector.getBPM();
        System.out.printf("Test long_mix.wav: BPM=%.2f, Beats=%d, Frames=%d%n",
                detectedBPM, beatsDetected, framesProcessed);
    }

    @Test
    void testFileAudioSourceBasics() throws AudioException {
        String testFilePath = getTestResourcePath("test_audio/sine_440hz.wav");
        audioSource = new FileAudioSource(testFilePath);

        assertFalse(audioSource.isRunning(), "Audio source should not be running before start");
        assertEquals(testFilePath, audioSource.getFilePath());

        audioSource.start();
        assertTrue(audioSource.isRunning(), "Audio source should be running after start");
        assertNotNull(audioSource.getAudioFormat(), "Audio format should be available after start");

        // Should be able to read at least one frame
        AudioFrame frame = audioSource.pollFrame();
        assertNotNull(frame, "Should be able to read at least one frame");
        assertTrue(frame.getData().length > 0, "Frame should contain data");

        audioSource.stop();
        assertFalse(audioSource.isRunning(), "Audio source should not be running after stop");
    }

    @Test
    void testFileNotFound() {
        audioSource = new FileAudioSource("/nonexistent/file.wav");
        assertThrows(AudioException.class, () -> audioSource.start(),
                "Should throw AudioException for nonexistent file");
    }

    @Test
    void testPollFrameBeforeStart() {
        String testFilePath = getTestResourcePath("test_audio/sine_440hz.wav");
        audioSource = new FileAudioSource(testFilePath);

        assertThrows(AudioException.class, () -> audioSource.pollFrame(),
                "Should throw AudioException when polling before start");
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
