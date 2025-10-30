package pw.wunderlich.lightbeat.audio;

/**
 * Stub implementation of an audio source that reads from a file.
 * TODO: Implement file reading functionality.
 */
public class FileAudioSource implements IAudioSource {

    private final String filePath;
    private boolean running;

    /**
     * Creates a new FileAudioSource for the specified file.
     *
     * @param filePath the path to the audio file
     */
    public FileAudioSource(String filePath) {
        this.filePath = filePath;
        this.running = false;
    }

    @Override
    public AudioFrame pollFrame() throws AudioException {
        // TODO: Implement file reading and frame extraction
        if (!running) {
            throw new AudioException("Audio source not started");
        }
        // Return null for now as a stub
        return null;
    }

    @Override
    public void start() throws AudioException {
        // TODO: Open the audio file and prepare for reading
        if (filePath == null || filePath.isEmpty()) {
            throw new AudioException("Invalid file path");
        }
        running = true;
    }

    @Override
    public void stop() {
        // TODO: Close the audio file and release resources
        running = false;
    }

    /**
     * Gets the file path.
     *
     * @return the path to the audio file
     */
    public String getFilePath() {
        return filePath;
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
