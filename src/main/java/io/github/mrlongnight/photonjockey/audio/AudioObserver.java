package io.github.mrlongnight.photonjockey.audio;

/**
 * Observer interface for receiving audio frames.
 * <p>
 * Implementations of this interface will be notified whenever a new {@link AudioFrame} is available.
 * <p>
 * <b>Threading:</b> The {@code audioReceived} method may be called from an internal audio processing thread.
 * Implementations should avoid blocking or performing long-running operations in this method.
 * <p>
 * <b>Usage:</b> Observers are expected to process, analyze, or forward the received {@link AudioFrame}
 * as appropriate for their use case. The frame should not be modified.
 */
public interface AudioObserver {
    /**
     * Called when a new {@link AudioFrame} is available.
     *
     * @param audioFrame the received audio frame; must not be modified
     */
    void audioReceived(AudioFrame audioFrame);
}
