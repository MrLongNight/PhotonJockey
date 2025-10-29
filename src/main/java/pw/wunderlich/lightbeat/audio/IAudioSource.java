package pw.wunderlich.lightbeat.audio;

/**
 * Interface for audio source implementations that provide audio frames.
 */
public interface IAudioSource {

    /**
     * Polls for the next available audio frame.
     *
     * @return the next audio frame, or null if no frame is available
     * @throws AudioException if an error occurs while polling for audio data
     */
    AudioFrame pollFrame() throws AudioException;

    /**
     * Starts the audio source.
     *
     * @throws AudioException if the audio source cannot be started
     */
    void start() throws AudioException;

    /**
     * Stops the audio source and releases any resources.
     */
    void stop();
}
