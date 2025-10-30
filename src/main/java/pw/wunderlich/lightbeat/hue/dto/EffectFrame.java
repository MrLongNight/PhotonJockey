package pw.wunderlich.lightbeat.hue.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents a frame of lighting effects for multiple lights.
 * Used for UDP-based fast entertainment mode.
 */
public class EffectFrame {
    private final List<LightUpdate> updates;
    private final long timestamp;
    private final int sequenceNumber;

    /**
     * Creates a new effect frame.
     *
     * @param updates List of light updates in this frame
     * @param timestamp Timestamp of the frame in milliseconds
     * @param sequenceNumber Sequential frame number for UDP transmission
     */
    public EffectFrame(List<LightUpdate> updates, long timestamp, int sequenceNumber) {
        if (updates == null) {
            throw new IllegalArgumentException("Updates cannot be null");
        }
        if (sequenceNumber < 0) {
            throw new IllegalArgumentException("Sequence number cannot be negative");
        }
        this.updates = Collections.unmodifiableList(new ArrayList<>(updates));
        this.timestamp = timestamp;
        this.sequenceNumber = sequenceNumber;
    }

    public List<LightUpdate> getUpdates() {
        return updates;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EffectFrame that = (EffectFrame) o;
        return timestamp == that.timestamp
                && sequenceNumber == that.sequenceNumber
                && Objects.equals(updates, that.updates);
    }

    @Override
    public int hashCode() {
        return Objects.hash(updates, timestamp, sequenceNumber);
    }

    @Override
    public String toString() {
        return "EffectFrame{updates=" + updates + ", timestamp=" + timestamp
                + ", sequenceNumber=" + sequenceNumber + "}";
    }
}
