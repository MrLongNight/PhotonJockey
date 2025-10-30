package pw.wunderlich.lightbeat.hue.engine;

import java.util.List;

/**
 * Interface for low-frequency effect controller using HTTP API.
 * Handles slower light updates with more detailed control.
 */
public interface ILowEffectController {

    /**
     * Update lights with the specified light updates.
     *
     * @param updates list of light updates to apply
     */
    void updateLights(List<LightUpdateDTO> updates);
}
