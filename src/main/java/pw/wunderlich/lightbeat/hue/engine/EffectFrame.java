package pw.wunderlich.lightbeat.hue.engine;

import java.util.List;
import java.util.Objects;

/**
 * Data transfer object representing a frame of light effects.
 * Contains light state updates to be sent to the controller.
 */
public class EffectFrame {

    private final List<LightUpdateDTO> updates;
    private final long timestamp;

    /**
     * Create a new effect frame.
     *
     * @param updates list of light updates in this frame
     * @param timestamp timestamp when this frame was created
     */
    public EffectFrame(List<LightUpdateDTO> updates, long timestamp) {
        this.updates = Objects.requireNonNull(updates, "Updates cannot be null");
        this.timestamp = timestamp;
    }

    /**
     * Get the list of light updates in this frame.
     *
     * @return list of light updates
     */
    public List<LightUpdateDTO> getUpdates() {
        return updates;
    }

    /**
     * Get the timestamp of this frame.
     *
     * @return timestamp in milliseconds
     */
    public long getTimestamp() {
        return timestamp;
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
        return timestamp == that.timestamp && Objects.equals(updates, that.updates);
    }

    @Override
    public int hashCode() {
        return Objects.hash(updates, timestamp);
    }

    @Override
    public String toString() {
        return "EffectFrame{updates=" + updates.size() + " items, timestamp=" + timestamp + '}';
    }
}
