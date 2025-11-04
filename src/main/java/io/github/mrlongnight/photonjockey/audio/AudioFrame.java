package io.github.mrlongnight.photonjockey.audio;

/**
 * Represents a frame of audio data with associated metadata.
 */
public class AudioFrame {

    private final byte[] data;
    private final int sampleRate;
    private final int channels;
    private final long timestamp;

    /**
     * Creates a new AudioFrame.
     *
     * @param data       the audio data
     * @param sampleRate the sample rate in Hz
     * @param channels   the number of audio channels
     * @param timestamp  the timestamp of this frame in milliseconds
     */
    public AudioFrame(byte[] data, int sampleRate, int channels, long timestamp) {
        this.data = data.clone();
        this.sampleRate = sampleRate;
        this.channels = channels;
        this.timestamp = timestamp;
    }

    /**
     * Gets the audio data.
     *
     * @return the audio data as a byte array
     */
    public byte[] getData() {
        return data.clone();
    }

    /**
     * Gets the sample rate.
     *
     * @return the sample rate in Hz
     */
    public int getSampleRate() {
        return sampleRate;
    }

    /**
     * Gets the number of channels.
     *
     * @return the number of audio channels
     */
    public int getChannels() {
        return channels;
    }

    /**
     * Gets the timestamp.
     *
     * @return the timestamp in milliseconds
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Converts the raw byte data of this frame into a normalized double array.
     * Assumes 16-bit little-endian signed PCM audio format.
     *
     * @return An array of audio samples, normalized to a range of [-1.0, 1.0].
     */
    public double[] toNormalizedSamples() {
        int sampleCount = data.length / 2;
        double[] samples = new double[sampleCount];
        for (int i = 0; i < sampleCount; i++) {
            // Combine two bytes to a 16-bit sample (little-endian)
            short sample = (short) ((data[i * 2 + 1] << 8) | (data[i * 2] & 0xFF));
            // Normalize to [-1.0, 1.0]
            samples[i] = sample / 32768.0;
        }
        return samples;
    }
}
