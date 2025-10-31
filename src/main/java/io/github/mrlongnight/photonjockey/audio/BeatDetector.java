package io.github.mrlongnight.photonjockey.audio;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Detects beats in audio by analyzing energy levels from AnalysisResult objects.
 * Uses a threshold-based approach with energy history to identify significant peaks.
 * Also tracks beat timestamps to estimate beats per minute (BPM).
 */
public class BeatDetector {

    private static final int ENERGY_HISTORY_SIZE = 43;
    private static final double BEAT_THRESHOLD_MULTIPLIER = 1.3;
    private static final int MIN_BEAT_INTERVAL_MS = 200;
    private static final int BPM_HISTORY_SIZE = 8;
    private static final long BPM_TIMEOUT_MS = 3000;

    private final Queue<Double> energyHistory;
    private final Queue<Long> beatTimestamps;
    private long lastBeatTime;
    private double currentBpm;

    /**
     * Creates a new BeatDetector with default configuration.
     */
    public BeatDetector() {
        this.energyHistory = new LinkedList<>();
        this.beatTimestamps = new LinkedList<>();
        this.lastBeatTime = 0;
        this.currentBpm = 0.0;
    }

    /**
     * Analyzes the given AnalysisResult to determine if it represents a beat.
     * A beat is detected when the energy level significantly exceeds the average
     * of recent energy levels, and sufficient time has passed since the last beat.
     *
     * @param result the analysis result containing energy information
     * @return true if a beat is detected, false otherwise
     */
    public boolean isBeat(AnalysisResult result) {
        if (result == null) {
            return false;
        }

        double energy = result.getEnergy();
        long currentTime = System.currentTimeMillis();

        // Ensure minimum time between beats
        if (currentTime - lastBeatTime < MIN_BEAT_INTERVAL_MS) {
            updateEnergyHistory(energy);
            return false;
        }

        // Calculate average energy from history
        double averageEnergy = calculateAverageEnergy();

        // Add current energy to history
        updateEnergyHistory(energy);

        // Detect beat if energy exceeds threshold
        double threshold = averageEnergy * BEAT_THRESHOLD_MULTIPLIER;
        if (energy > threshold && averageEnergy > 0.0) {
            lastBeatTime = currentTime;
            updateBeatTimestamps(currentTime);
            updateBpm();
            return true;
        }

        return false;
    }

    /**
     * Gets the current estimated beats per minute (BPM).
     * Returns 0 if insufficient beats have been detected or if too much time
     * has passed since the last beat.
     *
     * @return the estimated BPM, or 0 if unavailable
     */
    public double getBPM() {
        long currentTime = System.currentTimeMillis();

        // Reset BPM if too much time has passed since last beat
        if (currentTime - lastBeatTime > BPM_TIMEOUT_MS) {
            currentBpm = 0.0;
            beatTimestamps.clear();
        }

        return currentBpm;
    }

    /**
     * Updates the energy history queue with the new energy value.
     */
    private void updateEnergyHistory(double energy) {
        energyHistory.offer(energy);
        if (energyHistory.size() > ENERGY_HISTORY_SIZE) {
            energyHistory.poll();
        }
    }

    /**
     * Calculates the average energy from the history.
     */
    private double calculateAverageEnergy() {
        if (energyHistory.isEmpty()) {
            return 0.0;
        }

        double sum = 0.0;
        for (double energy : energyHistory) {
            sum += energy;
        }
        return sum / energyHistory.size();
    }

    /**
     * Updates the beat timestamps queue with the new timestamp.
     */
    private void updateBeatTimestamps(long timestamp) {
        beatTimestamps.offer(timestamp);
        if (beatTimestamps.size() > BPM_HISTORY_SIZE) {
            beatTimestamps.poll();
        }
    }

    /**
     * Updates the BPM estimate based on recent beat intervals.
     */
    private void updateBpm() {
        if (beatTimestamps.size() < 2) {
            currentBpm = 0.0;
            return;
        }

        // Calculate average interval between beats
        Long firstTimestamp = beatTimestamps.peek();
        Long lastTimestamp = null;
        for (Long ts : beatTimestamps) {
            lastTimestamp = ts;
        }

        if (firstTimestamp == null || lastTimestamp == null
                || firstTimestamp.equals(lastTimestamp)) {
            return;
        }

        long totalInterval = lastTimestamp - firstTimestamp;
        int intervalCount = beatTimestamps.size() - 1;
        double averageInterval = (double) totalInterval / intervalCount;

        // Convert to BPM (beats per minute)
        if (averageInterval > 0) {
            currentBpm = 60000.0 / averageInterval;
        }
    }

    /**
     * Resets the detector's internal state, clearing all history.
     */
    public void reset() {
        energyHistory.clear();
        beatTimestamps.clear();
        lastBeatTime = 0;
        currentBpm = 0.0;
    }
}
