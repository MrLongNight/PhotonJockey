package pw.wunderlich.lightbeat.audio;

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
}
