package io.github.mrlongnight.photonjockey.audio;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

/**
 * Implementation of an audio source that reads from a WAV file.
 * Reads audio data in frames and provides them for analysis.
 */
public class FileAudioSource implements IAudioSource {

    private static final int FRAME_SIZE = 1024; // Number of samples per frame

    private final String filePath;
    private boolean running;
    private AudioInputStream audioInputStream;
    private AudioFormat audioFormat;
    private byte[] buffer;
    private long frameCount;

    /**
     * Creates a new FileAudioSource for the specified file.
     *
     * @param filePath the path to the audio file
     */
    public FileAudioSource(String filePath) {
        this.filePath = filePath;
        this.running = false;
        this.frameCount = 0;
    }

    @Override
    public AudioFrame pollFrame() throws AudioException {
        if (!running) {
            throw new AudioException("Audio source not started");
        }

        if (audioInputStream == null) {
            return null;
        }

        try {
            int bytesPerSample = audioFormat.getSampleSizeInBits() / 8;
            int channels = audioFormat.getChannels();
            int bytesToRead = FRAME_SIZE * bytesPerSample * channels;

            if (buffer == null || buffer.length != bytesToRead) {
                buffer = new byte[bytesToRead];
            }

            int bytesRead = audioInputStream.read(buffer);

            if (bytesRead == -1) {
                // End of file reached
                return null;
            }

            // Create frame with actual bytes read
            byte[] frameData;
            if (bytesRead < bytesToRead) {
                frameData = new byte[bytesRead];
                System.arraycopy(buffer, 0, frameData, 0, bytesRead);
            } else {
                frameData = buffer.clone();
            }

            AudioFrame frame = new AudioFrame(
                    frameData,
                    (int) audioFormat.getSampleRate(),
                    channels,
                    System.currentTimeMillis()
            );

            frameCount++;
            return frame;

        } catch (IOException e) {
            throw new AudioException("Error reading audio file: " + e.getMessage(), e);
        }
    }

    @Override
    public void start() throws AudioException {
        if (filePath == null || filePath.isEmpty()) {
            throw new AudioException("Invalid file path");
        }

        File audioFile = new File(filePath);
        if (!audioFile.exists()) {
            throw new AudioException("Audio file not found: " + filePath);
        }

        try {
            audioInputStream = AudioSystem.getAudioInputStream(audioFile);
            audioFormat = audioInputStream.getFormat();
            running = true;
            frameCount = 0;
        } catch (UnsupportedAudioFileException e) {
            throw new AudioException("Unsupported audio file format: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new AudioException("Error opening audio file: " + e.getMessage(), e);
        }
    }

    @Override
    public void stop() {
        running = false;
        if (audioInputStream != null) {
            try {
                audioInputStream.close();
            } catch (IOException e) {
                // Ignore close errors
            }
            audioInputStream = null;
        }
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

    /**
     * Gets the audio format of the file.
     *
     * @return the audio format, or null if not started
     */
    public AudioFormat getAudioFormat() {
        return audioFormat;
    }

    /**
     * Gets the number of frames read so far.
     *
     * @return the frame count
     */
    public long getFrameCount() {
        return frameCount;
    }
}
