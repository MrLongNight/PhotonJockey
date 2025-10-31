package io.github.mrlongnight.photonjockey.audio;

/**
 * Enum representing different window functions for FFT processing.
 * Window functions are used to reduce spectral leakage by smoothing the signal at the edges.
 */
public enum WindowFunction {
    /**
     * No window function applied (rectangular window).
     */
    NONE,

    /**
     * Hamming window: w(n) = 0.54 - 0.46 * cos(2*pi*n / (N-1)).
     */
    HAMMING,

    /**
     * Hann window: w(n) = 0.5 * (1 - cos(2*pi*n / (N-1))).
     */
    HANN,

    /**
     * Blackman window: w(n) = 0.42 - 0.5*cos(2*pi*n/(N-1)) + 0.08*cos(4*pi*n/(N-1)).
     */
    BLACKMAN;

    /**
     * Applies the window function to the given samples.
     *
     * @param samples the input samples to be windowed
     */
    public void apply(double[] samples) {
        int n = samples.length;
        if (n == 0) {
            return;
        }

        switch (this) {
            case NONE:
                // No modification needed
                break;
            case HAMMING:
                for (int i = 0; i < n; i++) {
                    double window = 0.54 - 0.46 * Math.cos(2.0 * Math.PI * i / (n - 1));
                    samples[i] *= window;
                }
                break;
            case HANN:
                for (int i = 0; i < n; i++) {
                    double window = 0.5 * (1.0 - Math.cos(2.0 * Math.PI * i / (n - 1)));
                    samples[i] *= window;
                }
                break;
            case BLACKMAN:
                for (int i = 0; i < n; i++) {
                    double window = 0.42
                            - 0.5 * Math.cos(2.0 * Math.PI * i / (n - 1))
                            + 0.08 * Math.cos(4.0 * Math.PI * i / (n - 1));
                    samples[i] *= window;
                }
                break;
            default:
                throw new IllegalStateException("Unexpected window function: " + this);
        }
    }
}
