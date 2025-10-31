package io.github.mrlongnight.photonjockey.audio;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FFTProcessorTest {

    private static final double DELTA = 0.01;
    private static final int SAMPLE_RATE = 44100;

    @Test
    void testConstructor() {
        FFTProcessor processor = new FFTProcessor(512, WindowFunction.HAMMING, 0.5);

        assertEquals(512, processor.getFftSize());
        assertEquals(WindowFunction.HAMMING, processor.getWindowFunction());
        assertEquals(0.5, processor.getSmoothing(), DELTA);
    }

    @Test
    void testConstructorWithInvalidFftSize() {
        assertThrows(IllegalArgumentException.class,
                () -> new FFTProcessor(-1, WindowFunction.HAMMING, 0.5));
        assertThrows(IllegalArgumentException.class,
                () -> new FFTProcessor(0, WindowFunction.HAMMING, 0.5));
    }

    @Test
    void testConstructorWithInvalidSmoothing() {
        assertThrows(IllegalArgumentException.class,
                () -> new FFTProcessor(512, WindowFunction.HAMMING, -0.1));
        assertThrows(IllegalArgumentException.class,
                () -> new FFTProcessor(512, WindowFunction.HAMMING, 1.1));
    }

    @Test
    void testConstructorWithNullWindowFunction() {
        assertThrows(IllegalArgumentException.class,
                () -> new FFTProcessor(512, null, 0.5));
    }

    @Test
    void testComputeSpectrumWithNullSamples() {
        FFTProcessor processor = new FFTProcessor(512, WindowFunction.NONE, 0.0);
        assertThrows(IllegalArgumentException.class, () -> processor.computeSpectrum(null));
    }

    @Test
    void testComputeSpectrumWithSineWave() {
        int fftSize = 512;
        FFTProcessor processor = new FFTProcessor(fftSize, WindowFunction.NONE, 0.0);

        // Generate a 440 Hz sine wave (A4 note)
        double frequency = 440.0;
        double[] samples = generateSineWave(frequency, SAMPLE_RATE, fftSize);

        double[] spectrum = processor.computeSpectrum(samples);

        // Spectrum should have N/2 + 1 bins
        assertEquals(fftSize / 2 + 1, spectrum.length);

        // Find the peak frequency bin
        int peakBin = findPeakBin(spectrum);
        double peakFrequency = binToFrequency(peakBin, SAMPLE_RATE, fftSize);

        // Peak should be close to 440 Hz (within one bin width)
        double binWidth = (double) SAMPLE_RATE / fftSize;
        assertTrue(Math.abs(peakFrequency - frequency) < binWidth,
                "Peak frequency " + peakFrequency + " should be close to " + frequency);
    }

    @Test
    void testComputeSpectrumWithMultipleFrequencies() {
        int fftSize = 1024;
        FFTProcessor processor = new FFTProcessor(fftSize, WindowFunction.HANN, 0.0);

        // Generate a signal with two frequencies
        double freq1 = 200.0;
        double freq2 = 800.0;
        double[] samples1 = generateSineWave(freq1, SAMPLE_RATE, fftSize);
        double[] samples2 = generateSineWave(freq2, SAMPLE_RATE, fftSize);

        // Mix the signals
        double[] mixedSamples = new double[fftSize];
        for (int i = 0; i < fftSize; i++) {
            mixedSamples[i] = 0.5 * samples1[i] + 0.5 * samples2[i];
        }

        double[] spectrum = processor.computeSpectrum(mixedSamples);

        // Find peaks
        int peak1Bin = findPeakInRange(spectrum, 0, spectrum.length / 2);
        int peak2Bin = findPeakInRange(spectrum, peak1Bin + 5, spectrum.length);

        double peak1Frequency = binToFrequency(peak1Bin, SAMPLE_RATE, fftSize);
        double peak2Frequency = binToFrequency(peak2Bin, SAMPLE_RATE, fftSize);

        // Both frequencies should be detected
        double binWidth = (double) SAMPLE_RATE / fftSize;
        assertTrue(Math.abs(peak1Frequency - freq1) < binWidth * 2);
        assertTrue(Math.abs(peak2Frequency - freq2) < binWidth * 2);
    }

    @Test
    void testComputeSpectrumWithSmoothing() {
        int fftSize = 512;
        double smoothing = 0.8;
        FFTProcessor processor = new FFTProcessor(fftSize, WindowFunction.NONE, smoothing);

        double frequency = 440.0;
        double[] samples = generateSineWave(frequency, SAMPLE_RATE, fftSize);

        // First computation
        double[] spectrum1 = processor.computeSpectrum(samples);

        // Second computation with same samples
        double[] spectrum2 = processor.computeSpectrum(samples);

        // With high smoothing, second spectrum should be similar but not identical to first
        assertNotEquals(spectrum1[findPeakBin(spectrum1)], spectrum2[findPeakBin(spectrum2)]);
    }

    @Test
    void testComputeSpectrumWithZeroSamples() {
        int fftSize = 256;
        FFTProcessor processor = new FFTProcessor(fftSize, WindowFunction.NONE, 0.0);

        double[] samples = new double[fftSize];
        double[] spectrum = processor.computeSpectrum(samples);

        // Spectrum should be all zeros (or very close to zero)
        for (double magnitude : spectrum) {
            assertTrue(magnitude < 0.001, "Magnitude should be near zero for zero input");
        }
    }

    @Test
    void testComputeSpectrumWithFewerSamplesThanFftSize() {
        int fftSize = 512;
        FFTProcessor processor = new FFTProcessor(fftSize, WindowFunction.NONE, 0.0);

        // Generate samples with fewer elements than FFT size
        double frequency = 440.0;
        double[] samples = generateSineWave(frequency, SAMPLE_RATE, 256);

        double[] spectrum = processor.computeSpectrum(samples);

        // Should still produce valid spectrum (zero-padded internally)
        assertEquals(fftSize / 2 + 1, spectrum.length);
    }

    @Test
    void testComputeSpectrumWithMoreSamplesThanFftSize() {
        int fftSize = 256;
        FFTProcessor processor = new FFTProcessor(fftSize, WindowFunction.NONE, 0.0);

        // Generate more samples than FFT size
        double frequency = 440.0;
        double[] samples = generateSineWave(frequency, SAMPLE_RATE, 512);

        double[] spectrum = processor.computeSpectrum(samples);

        // Should truncate and produce valid spectrum
        assertEquals(fftSize / 2 + 1, spectrum.length);
    }

    @Test
    void testWindowFunctionApplication() {
        int fftSize = 512;
        double frequency = 440.0;
        double[] samples = generateSineWave(frequency, SAMPLE_RATE, fftSize);

        // Test with different window functions
        FFTProcessor noWindow = new FFTProcessor(fftSize, WindowFunction.NONE, 0.0);
        FFTProcessor hammingWindow = new FFTProcessor(fftSize, WindowFunction.HAMMING, 0.0);
        FFTProcessor hannWindow = new FFTProcessor(fftSize, WindowFunction.HANN, 0.0);
        FFTProcessor blackmanWindow = new FFTProcessor(fftSize, WindowFunction.BLACKMAN, 0.0);

        double[] spectrumNone = noWindow.computeSpectrum(samples);
        double[] spectrumHamming = hammingWindow.computeSpectrum(samples);
        double[] spectrumHann = hannWindow.computeSpectrum(samples);
        double[] spectrumBlackman = blackmanWindow.computeSpectrum(samples);

        // All should detect the same peak frequency
        int peakBinNone = findPeakBin(spectrumNone);
        int peakBinHamming = findPeakBin(spectrumHamming);
        int peakBinHann = findPeakBin(spectrumHann);
        int peakBinBlackman = findPeakBin(spectrumBlackman);

        // Peak bins should be close (within 1-2 bins)
        assertTrue(Math.abs(peakBinNone - peakBinHamming) <= 2);
        assertTrue(Math.abs(peakBinNone - peakBinHann) <= 2);
        assertTrue(Math.abs(peakBinNone - peakBinBlackman) <= 2);
    }

    @Test
    void testReset() {
        int fftSize = 512;
        FFTProcessor processor = new FFTProcessor(fftSize, WindowFunction.NONE, 0.8);

        double frequency = 440.0;
        double[] samples = generateSineWave(frequency, SAMPLE_RATE, fftSize);

        // First computation
        processor.computeSpectrum(samples);

        // Reset
        processor.reset();

        // After reset, next computation should be like the first one (no smoothing history)
        double[] spectrum = processor.computeSpectrum(samples);
        assertNotNull(spectrum);
        assertEquals(fftSize / 2 + 1, spectrum.length);
    }

    // Helper methods

    /**
     * Generates a sine wave with the specified parameters.
     */
    private double[] generateSineWave(double frequency, int sampleRate, int numSamples) {
        double[] samples = new double[numSamples];
        double angularFrequency = 2.0 * Math.PI * frequency / sampleRate;

        for (int i = 0; i < numSamples; i++) {
            samples[i] = Math.sin(angularFrequency * i);
        }

        return samples;
    }

    /**
     * Finds the bin with the maximum magnitude in the spectrum.
     */
    private int findPeakBin(double[] spectrum) {
        int peakBin = 0;
        double peakMagnitude = spectrum[0];

        for (int i = 1; i < spectrum.length; i++) {
            if (spectrum[i] > peakMagnitude) {
                peakMagnitude = spectrum[i];
                peakBin = i;
            }
        }

        return peakBin;
    }

    /**
     * Finds the bin with the maximum magnitude in a specific range.
     */
    private int findPeakInRange(double[] spectrum, int startBin, int endBin) {
        int peakBin = startBin;
        double peakMagnitude = spectrum[startBin];

        for (int i = startBin + 1; i < Math.min(endBin, spectrum.length); i++) {
            if (spectrum[i] > peakMagnitude) {
                peakMagnitude = spectrum[i];
                peakBin = i;
            }
        }

        return peakBin;
    }

    /**
     * Converts a bin index to frequency in Hz.
     */
    private double binToFrequency(int bin, int sampleRate, int fftSize) {
        return (double) bin * sampleRate / fftSize;
    }
}
