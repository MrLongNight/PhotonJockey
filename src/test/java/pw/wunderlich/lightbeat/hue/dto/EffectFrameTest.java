package pw.wunderlich.lightbeat.hue.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for EffectFrame.
 */
class EffectFrameTest {

    @Test
    void testValidEffectFrame() {
        List<LightUpdate> updates = createTestUpdates();
        EffectFrame frame = new EffectFrame(updates, 1000L, 42);

        assertEquals(2, frame.getUpdates().size());
        assertEquals(1000L, frame.getTimestamp());
        assertEquals(42, frame.getSequenceNumber());
    }

    @Test
    void testEmptyUpdatesList() {
        List<LightUpdate> updates = new ArrayList<>();
        EffectFrame frame = new EffectFrame(updates, 1000L, 0);

        assertEquals(0, frame.getUpdates().size());
        assertEquals(1000L, frame.getTimestamp());
        assertEquals(0, frame.getSequenceNumber());
    }

    @Test
    void testNullUpdates() {
        assertThrows(IllegalArgumentException.class, () -> new EffectFrame(null, 1000L, 0));
    }

    @Test
    void testInvalidSequenceNumber() {
        List<LightUpdate> updates = createTestUpdates();
        assertThrows(IllegalArgumentException.class, () -> new EffectFrame(updates, 1000L, -1));
    }

    @Test
    void testImmutableUpdatesList() {
        List<LightUpdate> updates = createTestUpdates();
        EffectFrame frame = new EffectFrame(updates, 1000L, 0);

        // Verify that the returned list is unmodifiable
        assertThrows(UnsupportedOperationException.class, () -> frame.getUpdates().clear());
    }

    @Test
    void testEquals() {
        List<LightUpdate> updates = createTestUpdates();
        EffectFrame frame1 = new EffectFrame(updates, 1000L, 42);
        EffectFrame frame2 = new EffectFrame(updates, 1000L, 42);
        EffectFrame frame3 = new EffectFrame(updates, 2000L, 42);

        assertEquals(frame1, frame2);
        assertNotEquals(frame1, frame3);
        assertNotEquals(frame1, null);
        assertNotEquals(frame1, new Object());
    }

    @Test
    void testHashCode() {
        List<LightUpdate> updates = createTestUpdates();
        EffectFrame frame1 = new EffectFrame(updates, 1000L, 42);
        EffectFrame frame2 = new EffectFrame(updates, 1000L, 42);

        assertEquals(frame1.hashCode(), frame2.hashCode());
    }

    @Test
    void testToString() {
        List<LightUpdate> updates = createTestUpdates();
        EffectFrame frame = new EffectFrame(updates, 1000L, 42);
        String str = frame.toString();

        assertTrue(str.contains("EffectFrame"));
        assertTrue(str.contains("1000"));
        assertTrue(str.contains("42"));
    }

    private List<LightUpdate> createTestUpdates() {
        List<LightUpdate> updates = new ArrayList<>();
        updates.add(new LightUpdate("light-1", new XyColor(0.5, 0.5, 127), 400));
        updates.add(new LightUpdate("light-2", new XyColor(0.3, 0.7, 200), 400));
        return updates;
    }
}
