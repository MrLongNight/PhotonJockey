package pw.wunderlich.lightbeat.audio;

/**
 * Interface for analyzing audio frames and extracting audio features.
 */
public interface IAudioAnalyzer {

    /**
     * Analyzes the given audio frame and returns the analysis result.
     *
     * @param frame the audio frame to analyze
     * @return the analysis result containing extracted features
     */
    AnalysisResult analyze(AudioFrame frame);
}
