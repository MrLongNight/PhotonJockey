package io.github.mrlongnight.photonjockey.audio;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WindowFunctionTest {

    private static final double DELTA = 0.0001;

    @Test
    void testNoneWindow() {
        double[] samples = {1.0, 2.0, 3.0, 4.0, 5.0};
        double[] original = samples.clone();

        WindowFunction.NONE.apply(samples);

        assertArrayEquals(original, samples, DELTA);
    }

    @Test
    void testHammingWindow() {
        double[] samples = {1.0, 1.0, 1.0, 1.0, 1.0};

        WindowFunction.HAMMING.apply(samples);

        // Hamming window should reduce values at edges
        assertTrue(samples[0] < 1.0, "First sample should be reduced");
        assertTrue(samples[4] < 1.0, "Last sample should be reduced");
        assertTrue(samples[2] > samples[0], "Middle sample should be larger than edge");
    }

    @Test
    void testHannWindow() {
        double[] samples = {1.0, 1.0, 1.0, 1.0, 1.0};

        WindowFunction.HANN.apply(samples);

        // Hann window should reduce values at edges more aggressively
        assertTrue(samples[0] < 0.1, "First sample should be close to 0");
        assertTrue(samples[4] < 0.1, "Last sample should be close to 0");
        assertTrue(samples[2] > samples[0], "Middle sample should be larger than edge");
    }

    @Test
    void testBlackmanWindow() {
        double[] samples = {1.0, 1.0, 1.0, 1.0, 1.0};

        WindowFunction.BLACKMAN.apply(samples);

        // Blackman window should reduce values at edges
        assertTrue(samples[0] < 0.1, "First sample should be close to 0");
        assertTrue(samples[4] < 0.1, "Last sample should be close to 0");
        assertTrue(samples[2] > samples[0], "Middle sample should be larger than edge");
    }

    @Test
    void testEmptySamples() {
        double[] samples = {};

        // Should not throw exception
        assertDoesNotThrow(() -> WindowFunction.NONE.apply(samples));
        assertDoesNotThrow(() -> WindowFunction.HAMMING.apply(samples));
        assertDoesNotThrow(() -> WindowFunction.HANN.apply(samples));
        assertDoesNotThrow(() -> WindowFunction.BLACKMAN.apply(samples));
    }

    @Test
    void testSingleSample() {
        double[] samples = {1.0};

        WindowFunction.HAMMING.apply(samples);

        // With single sample, window value should be 1.0 (or close to it)
        // Due to division by (n-1) = 0, the formula evaluates differently
        assertTrue(samples[0] > 0.0, "Single sample should have some value");
    }

    @Test
    void testHammingSymmetry() {
        int n = 256;
        double[] samples = new double[n];
        for (int i = 0; i < n; i++) {
            samples[i] = 1.0;
        }

        WindowFunction.HAMMING.apply(samples);

        // Window should be symmetric
        for (int i = 0; i < n / 2; i++) {
            assertEquals(samples[i], samples[n - 1 - i], DELTA,
                    "Window should be symmetric at position " + i);
        }
    }

    @Test
    void testHannSymmetry() {
        int n = 256;
        double[] samples = new double[n];
        for (int i = 0; i < n; i++) {
            samples[i] = 1.0;
        }

        WindowFunction.HANN.apply(samples);

        // Window should be symmetric
        for (int i = 0; i < n / 2; i++) {
            assertEquals(samples[i], samples[n - 1 - i], DELTA,
                    "Window should be symmetric at position " + i);
        }
    }

    @Test
    void testBlackmanSymmetry() {
        int n = 256;
        double[] samples = new double[n];
        for (int i = 0; i < n; i++) {
            samples[i] = 1.0;
        }

        WindowFunction.BLACKMAN.apply(samples);

        // Window should be symmetric
        for (int i = 0; i < n / 2; i++) {
            assertEquals(samples[i], samples[n - 1 - i], DELTA,
                    "Window should be symmetric at position " + i);
        }
    }

    @Test
    void testWindowsPreserveZeros() {
        double[] samples = {0.0, 0.0, 0.0, 0.0, 0.0};

        WindowFunction.HAMMING.apply(samples);
        assertArrayEquals(new double[]{0.0, 0.0, 0.0, 0.0, 0.0}, samples, DELTA);

        samples = new double[]{0.0, 0.0, 0.0, 0.0, 0.0};
        WindowFunction.HANN.apply(samples);
        assertArrayEquals(new double[]{0.0, 0.0, 0.0, 0.0, 0.0}, samples, DELTA);

        samples = new double[]{0.0, 0.0, 0.0, 0.0, 0.0};
        WindowFunction.BLACKMAN.apply(samples);
        assertArrayEquals(new double[]{0.0, 0.0, 0.0, 0.0, 0.0}, samples, DELTA);
    }

    @Test
    void testHammingWindowValues() {
        double[] samples = {1.0, 1.0, 1.0};

        WindowFunction.HAMMING.apply(samples);

        // For n=3: w(0) = 0.54 - 0.46*cos(0) = 0.08
        //          w(1) = 0.54 - 0.46*cos(pi) = 1.0
        //          w(2) = 0.54 - 0.46*cos(2*pi) = 0.08
        assertEquals(0.08, samples[0], 0.01);
        assertEquals(1.0, samples[1], 0.01);
        assertEquals(0.08, samples[2], 0.01);
    }

    @Test
    void testHannWindowValues() {
        double[] samples = {1.0, 1.0, 1.0};

        WindowFunction.HANN.apply(samples);

        // For n=3: w(0) = 0.5 * (1 - cos(0)) = 0.0
        //          w(1) = 0.5 * (1 - cos(pi)) = 1.0
        //          w(2) = 0.5 * (1 - cos(2*pi)) = 0.0
        assertEquals(0.0, samples[0], 0.01);
        assertEquals(1.0, samples[1], 0.01);
        assertEquals(0.0, samples[2], 0.01);
    }

    @Test
    void testDifferentWindowsProduceDifferentResults() {
        double[] samplesHamming = {1.0, 1.0, 1.0, 1.0, 1.0};
        double[] samplesHann = samplesHamming.clone();
        double[] samplesBlackman = samplesHamming.clone();

        WindowFunction.HAMMING.apply(samplesHamming);
        WindowFunction.HANN.apply(samplesHann);
        WindowFunction.BLACKMAN.apply(samplesBlackman);

        // Different window functions should produce different results
        assertNotEquals(samplesHamming[0], samplesHann[0], DELTA);
        assertNotEquals(samplesHamming[0], samplesBlackman[0], DELTA);
        assertNotEquals(samplesHann[0], samplesBlackman[0], DELTA);
    }
}
