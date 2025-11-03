package io.github.mrlongnight.photonjockey.audio;

import java.util.ArrayList;
import java.util.List;

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
