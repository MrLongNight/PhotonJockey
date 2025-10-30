package pw.wunderlich.lightbeat.hue.engine;

/**
 * Interface for fast effect controller using UDP Entertainment API.
 * Handles high-frequency light updates for responsive effects.
 */
public interface IFastEffectController {

    /**
     * Start a new effect session. Must be called before sending frames.
     */
    void startSession();

    /**
     * Send a frame to the lights.
     *
     * @param frame the effect frame to send containing light state information
     */
    void sendFrame(EffectFrame frame);

    /**
     * Stop the current effect session and release resources.
     */
    void stopSession();
}
