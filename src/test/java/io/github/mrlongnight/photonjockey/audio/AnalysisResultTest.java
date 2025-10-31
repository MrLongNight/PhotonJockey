package io.github.mrlongnight.photonjockey.audio;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AnalysisResultTest {

    private static final double FREQUENCY = 440.0;
    private static final double AMPLITUDE = 0.5;
    private static final double ENERGY = 0.8;
    private static final double DELTA = 0.001;

    @Test
    void testConstructorAndGetters() {
        AnalysisResult result = new AnalysisResult(FREQUENCY, AMPLITUDE, ENERGY);

        assertEquals(FREQUENCY, result.getFrequency(), DELTA);
        assertEquals(AMPLITUDE, result.getAmplitude(), DELTA);
        assertEquals(ENERGY, result.getEnergy(), DELTA);
    }

    @Test
    void testZeroValues() {
        AnalysisResult result = new AnalysisResult(0.0, 0.0, 0.0);

        assertEquals(0.0, result.getFrequency(), DELTA);
        assertEquals(0.0, result.getAmplitude(), DELTA);
        assertEquals(0.0, result.getEnergy(), DELTA);
    }
}
