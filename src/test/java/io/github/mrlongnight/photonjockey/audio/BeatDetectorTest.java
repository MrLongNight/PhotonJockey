package io.github.mrlongnight.photonjockey.audio;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BeatDetectorTest {

    private static final double DELTA = 0.01;
    private static final double LOW_ENERGY = 0.1;
    private static final double HIGH_ENERGY = 1.0;

    private BeatDetector detector;

    @BeforeEach
    void setUp() {
        detector = new BeatDetector();
    }

    @Test
    void testConstructor() {
        BeatDetector newDetector = new BeatDetector();
        assertNotNull(newDetector);
        assertEquals(0.0, newDetector.getBPM(), DELTA);
    }

    @Test
    void testIsBeatWithNullResult() {
        assertFalse(detector.isBeat(null));
    }

    @Test
    void testNoBeatOnLowEnergy() {
        // Feed low energy values - should not detect beat
        for (int i = 0; i < 50; i++) {
            AnalysisResult result = new AnalysisResult(440.0, 0.5, LOW_ENERGY);
            boolean isBeat = detector.isBeat(result);
            assertFalse(isBeat, "Low energy should not trigger beat at iteration " + i);
        }
    }

    @Test
    void testBeatOnEnergySpike() throws InterruptedException {
        // Build energy history with low values
        for (int i = 0; i < 43; i++) {
            AnalysisResult result = new AnalysisResult(440.0, 0.5, LOW_ENERGY);
            detector.isBeat(result);
        }

        // Wait to ensure minimum beat interval
        Thread.sleep(250);

        // Send high energy spike - should detect beat
        AnalysisResult spike = new AnalysisResult(440.0, 0.5, HIGH_ENERGY);
        boolean isBeat = detector.isBeat(spike);
        assertTrue(isBeat, "High energy spike should trigger beat");
    }

    @Test
    void testMinimumBeatInterval() throws InterruptedException {
        // Build energy history
        for (int i = 0; i < 43; i++) {
            AnalysisResult result = new AnalysisResult(440.0, 0.5, LOW_ENERGY);
            detector.isBeat(result);
        }

        Thread.sleep(250);

        // First spike - should detect
        AnalysisResult spike1 = new AnalysisResult(440.0, 0.5, HIGH_ENERGY);
        assertTrue(detector.isBeat(spike1));

        // Immediate second spike - should NOT detect (too soon)
        AnalysisResult spike2 = new AnalysisResult(440.0, 0.5, HIGH_ENERGY);
        assertFalse(detector.isBeat(spike2));

        // Wait and try again - should detect
        Thread.sleep(250);
        AnalysisResult spike3 = new AnalysisResult(440.0, 0.5, HIGH_ENERGY);
        assertTrue(detector.isBeat(spike3));
    }

    @Test
    void testBpmCalculation() throws InterruptedException {
        // Build energy history
        for (int i = 0; i < 43; i++) {
            AnalysisResult result = new AnalysisResult(440.0, 0.5, LOW_ENERGY);
            detector.isBeat(result);
        }

        // Initial BPM should be 0
        assertEquals(0.0, detector.getBPM(), DELTA);

        // Generate beats at approximately 120 BPM (500ms interval)
        int numBeats = 8;
        for (int i = 0; i < numBeats; i++) {
            Thread.sleep(500); // 500ms = 120 BPM

            // Add low energy values between beats
            for (int j = 0; j < 5; j++) {
                detector.isBeat(new AnalysisResult(440.0, 0.5, LOW_ENERGY));
            }

            // Add high energy spike
            detector.isBeat(new AnalysisResult(440.0, 0.5, HIGH_ENERGY));
        }

        // BPM should be approximately 120 (with some tolerance)
        double bpm = detector.getBPM();
        assertTrue(bpm > 100 && bpm < 140,
                "BPM should be approximately 120, got " + bpm);
    }

    @Test
    void testBpmResetAfterTimeout() throws InterruptedException {
        // Build energy history
        for (int i = 0; i < 43; i++) {
            AnalysisResult result = new AnalysisResult(440.0, 0.5, LOW_ENERGY);
            detector.isBeat(result);
        }

        // Generate a few beats
        for (int i = 0; i < 3; i++) {
            Thread.sleep(500);
            detector.isBeat(new AnalysisResult(440.0, 0.5, HIGH_ENERGY));
        }

        // BPM should be non-zero
        assertTrue(detector.getBPM() > 0);

        // Wait for timeout (3+ seconds)
        Thread.sleep(3100);

        // BPM should reset to 0
        assertEquals(0.0, detector.getBPM(), DELTA);
    }

    @Test
    void testReset() throws InterruptedException {
        // Build energy history and generate beats
        for (int i = 0; i < 43; i++) {
            AnalysisResult result = new AnalysisResult(440.0, 0.5, LOW_ENERGY);
            detector.isBeat(result);
        }

        Thread.sleep(250);
        detector.isBeat(new AnalysisResult(440.0, 0.5, HIGH_ENERGY));
        Thread.sleep(500);
        detector.isBeat(new AnalysisResult(440.0, 0.5, HIGH_ENERGY));

        // BPM should be non-zero
        assertTrue(detector.getBPM() > 0);

        // Reset detector
        detector.reset();

        // BPM should be 0 after reset
        assertEquals(0.0, detector.getBPM(), DELTA);

        // Should not immediately detect beat after reset (needs to rebuild history)
        AnalysisResult spike = new AnalysisResult(440.0, 0.5, HIGH_ENERGY);
        assertFalse(detector.isBeat(spike));
    }

    @Test
    void testGradualEnergyIncrease() throws InterruptedException {
        // Gradually increase energy - should eventually trigger beat
        boolean beatDetected = false;

        for (int i = 0; i < 100; i++) {
            double energy = 0.1 + (i * 0.02); // Gradually increase from 0.1 to 3.0
            AnalysisResult result = new AnalysisResult(440.0, 0.5, energy);

            if (detector.isBeat(result)) {
                beatDetected = true;
                break;
            }

            // Small delay to respect minimum beat interval
            Thread.sleep(10);
        }

        assertTrue(beatDetected, "Beat should be detected with gradual energy increase");
    }

    @Test
    void testConsistentLowEnergyNoBeat() {
        // Consistently low energy should never trigger beat
        boolean beatDetected = false;

        for (int i = 0; i < 100; i++) {
            AnalysisResult result = new AnalysisResult(440.0, 0.5, LOW_ENERGY);
            if (detector.isBeat(result)) {
                beatDetected = true;
                break;
            }
        }

        assertFalse(beatDetected, "Consistent low energy should not trigger beat");
    }

    @Test
    void testEnergyThresholdDetection() throws InterruptedException {
        // Build baseline with low energy
        for (int i = 0; i < 50; i++) {
            detector.isBeat(new AnalysisResult(440.0, 0.5, 0.5));
        }

        Thread.sleep(250);

        // Energy just below threshold (1.3x average) - should NOT detect
        AnalysisResult belowThreshold = new AnalysisResult(440.0, 0.5, 0.6);
        assertFalse(detector.isBeat(belowThreshold));

        Thread.sleep(250);

        // Energy above threshold (1.3x average) - should detect
        AnalysisResult aboveThreshold = new AnalysisResult(440.0, 0.5, 0.8);
        assertTrue(detector.isBeat(aboveThreshold));
    }

    @Test
    void testMultipleFrequenciesWithSameEnergy() throws InterruptedException {
        // Build energy history
        for (int i = 0; i < 43; i++) {
            detector.isBeat(new AnalysisResult(100.0, 0.5, LOW_ENERGY));
        }

        Thread.sleep(250);

        // Different frequencies but same energy spike - should still detect
        AnalysisResult spike1 = new AnalysisResult(200.0, 0.5, HIGH_ENERGY);
        assertTrue(detector.isBeat(spike1));

        Thread.sleep(250);

        AnalysisResult spike2 = new AnalysisResult(800.0, 0.5, HIGH_ENERGY);
        assertTrue(detector.isBeat(spike2));
    }

    @Test
    void testZeroEnergyHandling() {
        // Zero energy should not cause issues
        for (int i = 0; i < 50; i++) {
            AnalysisResult result = new AnalysisResult(440.0, 0.5, 0.0);
            assertFalse(detector.isBeat(result));
        }
    }

    @Test
    void testVeryHighEnergyHandling() throws InterruptedException {
        // Build baseline
        for (int i = 0; i < 43; i++) {
            detector.isBeat(new AnalysisResult(440.0, 0.5, LOW_ENERGY));
        }

        Thread.sleep(250);

        // Very high energy should still be detected properly
        AnalysisResult veryHighEnergy = new AnalysisResult(440.0, 0.5, 100.0);
        assertTrue(detector.isBeat(veryHighEnergy));
    }

    @Test
    void testBpmWithFastBeats() throws InterruptedException {
        // Build energy history
        for (int i = 0; i < 43; i++) {
            detector.isBeat(new AnalysisResult(440.0, 0.5, LOW_ENERGY));
        }

        // Generate fast beats at approximately 150 BPM (400ms interval)
        for (int i = 0; i < 8; i++) {
            Thread.sleep(400);
            detector.isBeat(new AnalysisResult(440.0, 0.5, HIGH_ENERGY));
        }

        double bpm = detector.getBPM();
        assertTrue(bpm > 130 && bpm < 170,
                "BPM should be approximately 150 for 400ms intervals, got " + bpm);
    }
}
