package io.github.mrlongnight.photonjockey.audio.device;

import org.jetbrains.annotations.NotNull;

import javax.media.format.AudioFormat;

/**
 * Wrapper around different audio format implementations.
 */
public record PJAudioFormat(double sampleRate, boolean littleEndian, int channels, int bytesPerSample) {

    public PJAudioFormat(AudioFormat format) {
        this(
                format.getSampleRate(),
                format.getEndian() == AudioFormat.LITTLE_ENDIAN,
                format.getChannels(),
                format.getSampleSizeInBits() / 8
        );
    }

    public PJAudioFormat(javax.sound.sampled.AudioFormat format) {
        this(
                format.getSampleRate(),
                !format.isBigEndian(),
                format.getChannels(),
                format.getSampleSizeInBits() / 8
        );
    }

    /**
     * @return amount of bytes one frame consist (audio sample across channels)
     */
    public int getBytesPerFrame() {
        return channels * bytesPerSample;
    }

    @NotNull
    @Override
    public String toString() {
        return "PJAudioFormat{" +
                "sampleRate=" + sampleRate +
                ", littleEndian=" + littleEndian +
                ", channels=" + channels +
                ", bytesPerSample=" + bytesPerSample +
                '}';
    }
}
