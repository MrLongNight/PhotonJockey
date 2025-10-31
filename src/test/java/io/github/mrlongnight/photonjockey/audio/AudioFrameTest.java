package io.github.mrlongnight.photonjockey.audio;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AudioFrameTest {

    private static final int SAMPLE_RATE = 44100;
    private static final int CHANNELS = 2;
    private static final long TIMESTAMP = 1000L;

    @Test
    void testConstructorAndGetters() {
        byte[] data = {1, 2, 3, 4, 5};
        AudioFrame frame = new AudioFrame(data, SAMPLE_RATE, CHANNELS, TIMESTAMP);

        assertArrayEquals(data, frame.getData());
        assertEquals(SAMPLE_RATE, frame.getSampleRate());
        assertEquals(CHANNELS, frame.getChannels());
        assertEquals(TIMESTAMP, frame.getTimestamp());
    }

    @Test
    void testEmptyData() {
        byte[] data = {};
        AudioFrame frame = new AudioFrame(data, SAMPLE_RATE, CHANNELS, TIMESTAMP);

        assertArrayEquals(data, frame.getData());
        assertEquals(0, frame.getData().length);
    }
}
