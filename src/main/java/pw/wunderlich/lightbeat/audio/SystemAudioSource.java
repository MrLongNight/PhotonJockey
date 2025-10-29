package pw.wunderlich.lightbeat.audio;

/**
 * Stub implementation of an audio source that captures system audio.
 * TODO: Implement system audio capture functionality.
 */
public class SystemAudioSource implements IAudioSource {

    private boolean running;
    private String deviceName;

    /**
     * Creates a new SystemAudioSource with the default device.
     */
    public SystemAudioSource() {
        this(null);
    }

    /**
     * Creates a new SystemAudioSource for the specified device.
     *
     * @param deviceName the name of the audio device, or null for default
     */
    public SystemAudioSource(String deviceName) {
        this.deviceName = deviceName;
        this.running = false;
    }

    @Override
    public AudioFrame pollFrame() throws AudioException {
        // TODO: Implement system audio capture and frame extraction
        if (!running) {
            throw new AudioException("Audio source not started");
        }
        // Return null for now as a stub
        return null;
    }

    @Override
    public void start() throws AudioException {
        // TODO: Initialize system audio capture device
        running = true;
    }

    @Override
    public void stop() {
        // TODO: Stop audio capture and release device
        running = false;
    }

    /**
     * Gets the device name.
     *
     * @return the name of the audio device
     */
    public String getDeviceName() {
        return deviceName;
    }

    /**
     * Sets the device name.
     *
     * @param deviceName the name of the audio device
     */
    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    /**
     * Checks if the audio source is running.
     *
     * @return true if the source is running
     */
    public boolean isRunning() {
        return running;
    }
}
