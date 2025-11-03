package io.github.mrlongnight.photonjockey.audio;

import java.util.ArrayList;
import java.util.List;

/**
 * BPMDetector detects the tempo (beats per minute) of a sequence of beats.
 * <p>
 * Usage:
 * <ul>
 *   <li>Call {@link #recordBeat()} each time a beat occurs (e.g., on user tap or audio event).</li>
 *   <li>The detector maintains up to {@code maxTimestamps} recent beat timestamps; older ones are discarded.</li>
 *   <li>Use {@link #getBpm()} to retrieve the current BPM, calculated from the intervals between recent beats.</li>
 *   <li>Optionally, set a BPM range with {@link #setBpmRange(double, double)} to filter out implausible intervals.</li>
 * </ul>
 * <p>
 * Algorithm:
 * <ul>
 *   <li>Stores up to {@code maxTimestamps} timestamps of recent beats.</li>
 *   <li>Calculates intervals (deltas) between consecutive beats.</li>
 *   <li>Filters intervals whose BPM is outside the specified range.</li>
 *   <li>Averages the remaining intervals and converts to BPM.</li>
 * </ul>
 * <p>
 * Typical usage pattern:
 * <pre>
 *   BPMDetector detector = new BPMDetector(8);
 *   // On each beat:
 *   detector.recordBeat();
 *   double bpm = detector.getBpm();
 * </pre>
 */
public class BPMDetector {

    private final List<Long> beatTimestamps = new ArrayList<>();
    private final int maxTimestamps;
    private double minBpm;
    private double maxBpm;

    public BPMDetector(int maxTimestamps) {
        this.maxTimestamps = maxTimestamps;
        this.minBpm = 60;
        this.maxBpm = 180;
    }

    public void setBpmRange(double minBpm, double maxBpm) {
        if (minBpm > maxBpm) {
            // Swap values if provided in the wrong order
            double temp = minBpm;
            minBpm = maxBpm;
            maxBpm = temp;
        }
        this.minBpm = minBpm;
        this.maxBpm = maxBpm;
    }

    public void recordBeat() {
        beatTimestamps.add(System.currentTimeMillis());
        if (beatTimestamps.size() > maxTimestamps) {
            beatTimestamps.remove(0);
        }
    }

    public double getBpm() {
        if (beatTimestamps.size() < 2) {
            return 0.0;
        }

        List<Long> deltas = new ArrayList<>();
        for (int i = 0; i < beatTimestamps.size() - 1; i++) {
            long delta = beatTimestamps.get(i + 1) - beatTimestamps.get(i);
            double bpm = 60000.0 / delta;
            if (bpm >= minBpm && bpm <= maxBpm) {
                deltas.add(delta);
            }
        }

        if (deltas.isEmpty()) {
            return 0.0;
        }

        double averageDelta = deltas.stream().mapToLong(Long::longValue).average().orElse(0.0);
        if (averageDelta == 0.0) {
            return 0.0;
        }

        return 60000.0 / averageDelta;
    }
}
