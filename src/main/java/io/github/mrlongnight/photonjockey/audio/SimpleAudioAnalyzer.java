package io.github.mrlongnight.photonjockey.audio;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Simple audio analyzer that converts audio frames into analysis results.
 * Computes basic audio features like energy and RMS amplitude.
 */
public class SimpleAudioAnalyzer implements IAudioAnalyzer {

    private final FFTProcessor fftProcessor;
    private final int sampleRate;

    /**
     * Creates a new SimpleAudioAnalyzer.
     *
     * @param sampleRate the sample rate of the audio
     * @param fftSize    the size of the FFT
     */
    public SimpleAudioAnalyzer(int sampleRate, int fftSize) {
        this.sampleRate = sampleRate;
        this.fftProcessor = new FFTProcessor(fftSize, WindowFunction.HANN, 0.5);
    }

    @Override
    public AnalysisResult analyze(AudioFrame frame) {
        if (frame == null) {
            return null;
        }

        // Convert byte data to double samples
        double[] samples = bytesToSamples(frame.getData(), frame.getChannels());

        // Compute FFT spectrum
        double[] spectrum = fftProcessor.computeSpectrum(samples);

        // Find dominant frequency
        double dominantFrequency = findDominantFrequency(spectrum);

        // Calculate energy (RMS of samples)
        double energy = calculateEnergy(samples);

        // Calculate amplitude (max absolute value)
        double amplitude = calculateAmplitude(samples);

        return new AnalysisResult(dominantFrequency, amplitude, energy);
    }

    /**
     * Converts byte array to normalized double samples.
     */
    private double[] bytesToSamples(byte[] data, int channels) {
        // Assume 16-bit signed little-endian samples
        ByteBuffer buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
        int sampleCount = data.length / 2 / channels;
        double[] samples = new double[sampleCount];

        for (int i = 0; i < sampleCount; i++) {
            // For stereo, average the channels; for mono, just use the value
            double sum = 0;
            for (int c = 0; c < channels; c++) {
                if (buffer.remaining() >= 2) {
                    short value = buffer.getShort();
                    sum += value / 32768.0; // Normalize to [-1, 1]
                }
            }
            samples[i] = sum / channels;
        }

        return samples;
    }

    /**
     * Finds the frequency with the highest magnitude in the spectrum.
     */
    private double findDominantFrequency(double[] spectrum) {
        int maxIndex = 0;
        double maxValue = spectrum[0];

        for (int i = 1; i < spectrum.length; i++) {
            if (spectrum[i] > maxValue) {
                maxValue = spectrum[i];
                maxIndex = i;
            }
        }

        // Convert bin index to frequency
        double frequencyPerBin = (double) sampleRate / (2 * spectrum.length);
        return maxIndex * frequencyPerBin;
    }

    /**
     * Calculates the energy (RMS) of the samples.
     */
    private double calculateEnergy(double[] samples) {
        double sum = 0;
        for (double sample : samples) {
            sum += sample * sample;
        }
        return Math.sqrt(sum / samples.length);
    }

    /**
     * Calculates the amplitude (max absolute value) of the samples.
     */
    private double calculateAmplitude(double[] samples) {
        double max = 0;
        for (double sample : samples) {
            double abs = Math.abs(sample);
            if (abs > max) {
                max = abs;
            }
        }
        return max;
    }

    /**
     * Resets the analyzer state.
     */
    public void reset() {
        fftProcessor.reset();
    }
}
