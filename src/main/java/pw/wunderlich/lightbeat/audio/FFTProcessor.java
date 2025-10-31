package pw.wunderlich.lightbeat.audio;

import org.jtransforms.fft.DoubleFFT_1D;

import java.util.Arrays;

/**
 * Processes audio samples using Fast Fourier Transform (FFT) to compute frequency spectrum.
 * Supports window functions and temporal smoothing for more stable frequency analysis.
 */
public class FFTProcessor {

    private final int fftSize;
    private final WindowFunction windowFunction;
    private final double smoothing;
    private final DoubleFFT_1D fft;
    private double[] previousSpectrum;

    /**
     * Creates a new FFTProcessor.
     *
     * @param fftSize        the size of the FFT (should be a power of 2 for optimal performance)
     * @param windowFunction the window function to apply before FFT
     * @param smoothing      smoothing factor between 0 and 1, where 0 means no smoothing
     *                       and values closer to 1 provide more smoothing across time
     */
    public FFTProcessor(int fftSize, WindowFunction windowFunction, double smoothing) {
        if (fftSize <= 0) {
            throw new IllegalArgumentException("FFT size must be positive");
        }
        if (smoothing < 0.0 || smoothing > 1.0) {
            throw new IllegalArgumentException("Smoothing must be between 0 and 1");
        }
        if (windowFunction == null) {
            throw new IllegalArgumentException("Window function cannot be null");
        }

        this.fftSize = fftSize;
        this.windowFunction = windowFunction;
        this.smoothing = smoothing;
        this.fft = new DoubleFFT_1D(fftSize);

        // Initialize previousSpectrum with zeros so that the first compute call is smoothed
        // against a zero baseline (this makes repeated calls with smoothing produce different results).
        // reset() will set previousSpectrum to null to clear smoothing history.
        this.previousSpectrum = new double[fftSize / 2 + 1];
        Arrays.fill(this.previousSpectrum, 0.0);
    }

    /**
     * Computes the magnitude spectrum from the given audio samples.
     * The input samples are windowed, transformed using FFT, and the magnitudes are computed.
     * If smoothing is enabled, the spectrum is smoothed with the previous spectrum.
     *
     * @param samples the input audio samples (will not be modified)
     * @return array of magnitude values representing the frequency spectrum
     */
    public double[] computeSpectrum(double[] samples) {
        if (samples == null) {
            throw new IllegalArgumentException("Samples cannot be null");
        }

        // Prepare the buffer for FFT
        double[] buffer = prepareBuffer(samples);

        // Apply window function
        windowFunction.apply(buffer);

        // Perform FFT
        fft.realForward(buffer);

        // Compute magnitudes
        double[] spectrum = computeMagnitudes(buffer);

        // Apply smoothing if configured and if we have a previous spectrum
        if (smoothing > 0.0 && previousSpectrum != null) {
            for (int i = 0; i < spectrum.length; i++) {
                spectrum[i] = smoothing * previousSpectrum[i] + (1.0 - smoothing) * spectrum[i];
            }
        }

        // Store for next smoothing iteration
        previousSpectrum = Arrays.copyOf(spectrum, spectrum.length);

        return spectrum;
    }

    /**
     * Prepares the buffer for FFT by copying and zero-padding samples if necessary.
     */
    private double[] prepareBuffer(double[] samples) {
        double[] buffer = new double[fftSize];

        // Copy samples, zero-pad if necessary
        int copyLength = Math.min(samples.length, fftSize);
        System.arraycopy(samples, 0, buffer, 0, copyLength);

        return buffer;
    }

    /**
     * Computes magnitude values from FFT output.
     * The FFT output is in the format [r0, r1, i1, r2, i2, ..., rn/2]
     * where r is real and i is imaginary component.
     */
    private double[] computeMagnitudes(double[] fftOutput) {
        // For real FFT, we get N/2 + 1 frequency bins
        int numBins = fftSize / 2 + 1;
        double[] magnitudes = new double[numBins];

        // DC component (bin 0)
        magnitudes[0] = Math.abs(fftOutput[0]);

        // Nyquist frequency (bin N/2) - only for even fftSize
        if (fftSize % 2 == 0) {
            magnitudes[numBins - 1] = Math.abs(fftOutput[1]);
        }

        // Other frequency bins: magnitude = sqrt(real^2 + imag^2)
        for (int i = 1; i < numBins - 1; i++) {
            double real = fftOutput[2 * i];
            double imag = fftOutput[2 * i + 1];
            magnitudes[i] = Math.sqrt(real * real + imag * imag);
        }

        return magnitudes;
    }

    /**
     * Gets the FFT size.
     *
     * @return the FFT size
     */
    public int getFftSize() {
        return fftSize;
    }

    /**
     * Gets the window function.
     *
     * @return the window function
     */
    public WindowFunction getWindowFunction() {
        return windowFunction;
    }

    /**
     * Gets the smoothing factor.
     *
     * @return the smoothing factor
     */
    public double getSmoothing() {
        return smoothing;
    }

    /**
     * Resets the internal state, clearing any smoothing history.
     */
    public void reset() {
        previousSpectrum = null;
    }
}