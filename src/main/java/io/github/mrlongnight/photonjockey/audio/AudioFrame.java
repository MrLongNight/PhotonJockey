package io.github.mrlongnight.photonjockey.audio;

/**
 * Represents a frame of audio data with associated metadata.
 */
public class AudioFrame {

    private final byte[] data;
    private final int sampleRate;
    private final int channels;
    private final long timestamp;
    private final double levelDB;
    private final double[] lowFreqData;
    private final double[] midFreqData;
    private final double[] highFreqData;

    /**
     * Creates a new AudioFrame.
     *
     * @param data         the audio data
     * @param sampleRate   the sample rate in Hz
     * @param channels     the number of audio channels
     * @param timestamp    the timestamp of this frame in milliseconds
     * @param levelDB      the audio level in decibels
     * @param lowFreqData  the low frequency audio data
     * @param midFreqData  the mid frequency audio data
     * @param highFreqData the high frequency audio data
     */
    public AudioFrame(byte[] data, int sampleRate, int channels, long timestamp, double levelDB, double[] lowFreqData, double[] midFreqData, double[] highFreqData) {
        this.data = data.clone();
        this.sampleRate = sampleRate;
        this.channels = channels;
        this.timestamp = timestamp;
        this.levelDB = levelDB;
        this.lowFreqData = lowFreqData;
        this.midFreqData = midFreqData;
        this.highFreqData = highFreqData;
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
     * Gets the audio level in decibels.
     *
     * @return the audio level in dB
     */
    public double getLevelDB() {
        return levelDB;
    }

    public double[] getLowFreqData() {
        return lowFreqData;
    }

    public double[] getMidFreqData() {
        return midFreqData;
    }

    public double[] getHighFreqData() {
        return highFreqData;
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
