package io.github.mrlongnight.photonjockey.audio;

/**
 * Represents the result of analyzing an audio frame.
 */
public class AnalysisResult {

    private final double frequency;
    private final double amplitude;
    private final double energy;

    /**
     * Creates a new AnalysisResult.
     *
     * @param frequency the dominant frequency detected
     * @param amplitude the amplitude level
     * @param energy    the energy level
     */
    public AnalysisResult(double frequency, double amplitude, double energy) {
        this.frequency = frequency;
        this.amplitude = amplitude;
        this.energy = energy;
    }

    /**
     * Gets the dominant frequency.
     *
     * @return the frequency in Hz
     */
    public double getFrequency() {
        return frequency;
    }

    /**
     * Gets the amplitude.
     *
     * @return the amplitude level
     */
    public double getAmplitude() {
        return amplitude;
    }

    /**
     * Gets the energy.
     *
     * @return the energy level
     */
    public double getEnergy() {
        return energy;
    }
}
